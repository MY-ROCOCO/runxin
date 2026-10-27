package com.rxinyun.admin.system.modular.role.service.impl;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.system.core.enums.SysBuildInEnum;
import com.rxinyun.admin.system.core.enums.SysDataTypeEnum;
import com.rxinyun.admin.system.modular.relation.entity.SysRelation;
import com.rxinyun.admin.system.modular.relation.enums.SysRelationCategoryEnum;
import com.rxinyun.admin.system.modular.relation.service.SysRelationService;
import com.rxinyun.admin.system.modular.resource.entity.SysMenu;
import com.rxinyun.admin.system.modular.resource.enums.SysResourceCategoryEnum;
import com.rxinyun.admin.system.modular.resource.enums.SysResourceMenuTypeEnum;
import com.rxinyun.admin.system.modular.resource.service.SysMenuService;
import com.rxinyun.admin.system.modular.role.entity.SysRole;
import com.rxinyun.admin.system.modular.role.enums.SysRoleCategoryEnum;
import com.rxinyun.admin.system.modular.role.mapper.SysRoleMapper;
import com.rxinyun.admin.system.modular.role.param.SysRoleAddParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleEditParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleGrantMobileMenuParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleGrantPermissionParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleGrantResourceParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleGrantUserParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleIdParam;
import com.rxinyun.admin.system.modular.role.param.SysRolePageParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleSelectorRoleParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleSelectorUserParam;
import com.rxinyun.admin.system.modular.role.result.SysRoleGrantResourceTreeResult;
import com.rxinyun.admin.system.modular.role.result.SysRoleOwnMobileMenuResult;
import com.rxinyun.admin.system.modular.role.result.SysRoleOwnPermissionResult;
import com.rxinyun.admin.system.modular.role.result.SysRoleOwnResourceResult;
import com.rxinyun.admin.system.modular.role.service.SysRoleService;
import com.rxinyun.admin.system.modular.user.entity.SysUser;
import com.rxinyun.admin.system.modular.user.service.SysUserService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.listener.CommonDataChangeEventCenter;
import com.rxinyun.common.page.CommonPageRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
   @Resource
   private SysRelationService sysRelationService;
   @Resource
   private SysMenuService sysMenuService;
   @Resource
   private SysUserService sysUserService;

   @Override
   public Page<SysRole> page(SysRolePageParam sysRolePageParam) {
      QueryWrapper<SysRole> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().select(SysRole::getId, SysRole::getOrgId, SysRole::getName, SysRole::getCategory, SysRole::getSortCode);
      queryWrapper.lambda().eq(SysRole::getCategory, SysRoleCategoryEnum.GLOBAL.getValue());
      if (ObjectUtil.isNotEmpty(sysRolePageParam.getSearchKey())) {
         queryWrapper.lambda().like(SysRole::getName, sysRolePageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{sysRolePageParam.getSortField(), sysRolePageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(sysRolePageParam.getSortOrder());
         queryWrapper.orderBy(
            true, sysRolePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(sysRolePageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(SysRole::getSortCode);
      }

      return (Page<SysRole>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(SysRoleAddParam sysRoleAddParam) {
      sysRoleAddParam.setCategory(SysRoleCategoryEnum.GLOBAL.getValue());
      sysRoleAddParam.setOrgId(null);
      SysRole sysRole = (SysRole)BeanUtil.toBean(sysRoleAddParam, SysRole.class);
      boolean repeatName = this.count(
            (Wrapper<SysRole>)((LambdaQueryWrapper<SysRole>)new LambdaQueryWrapper<SysRole>().eq(SysRole::getOrgId, sysRole.getOrgId())).eq(SysRole::getName, sysRole.getName())
         )
         > 0L;
      if (repeatName) {
         if (ObjectUtil.isEmpty(sysRole.getOrgId())) {
            throw new CommonException("存在重复的全局角色，名称为：{}", new Object[]{sysRole.getName()});
         } else {
            throw new CommonException("同组织下存在重复的角色，名称为：{}", new Object[]{sysRole.getName()});
         }
      } else {
         sysRole.setCode(RandomUtil.randomString(10));
         this.save(sysRole);
         CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.ROLE.getValue(), JSONUtil.createArray().put(sysRole));
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(SysRoleEditParam sysRoleEditParam) {
      SysRole sysRole = this.queryEntity(sysRoleEditParam.getId());
      boolean superRole = sysRole.getCode().equals(SysBuildInEnum.BUILD_IN_ROLE_CODE.getValue());
      if (superRole) {
         throw new CommonException("不可编辑超管角色", new Object[0]);
      } else {
         sysRoleEditParam.setCategory(SysRoleCategoryEnum.GLOBAL.getValue());
         sysRoleEditParam.setOrgId(null);
         boolean repeatName = this.count(
               (Wrapper<SysRole>)((LambdaQueryWrapper<SysRole>)((LambdaQueryWrapper<SysRole>)new LambdaQueryWrapper<SysRole>().eq(SysRole::getOrgId, sysRole.getOrgId()))
                     .eq(SysRole::getName, sysRole.getName()))
                  .ne(SysRole::getId, sysRole.getId())
            )
            > 0L;
         if (repeatName) {
            if (ObjectUtil.isEmpty(sysRole.getOrgId())) {
               throw new CommonException("存在重复的全局角色，名称为：{}", new Object[]{sysRole.getName()});
            } else {
               throw new CommonException("同组织下存在重复的角色，名称为：{}", new Object[]{sysRole.getName()});
            }
         } else {
            BeanUtil.copyProperties(sysRoleEditParam, sysRole, new String[0]);
            this.updateById(sysRole);
            CommonDataChangeEventCenter.doUpdateWithData(SysDataTypeEnum.ROLE.getValue(), JSONUtil.createArray().put(sysRole));
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<SysRoleIdParam> sysRoleIdParamList) {
      List<String> sysRoleIdList = CollStreamUtil.toList(sysRoleIdParamList, SysRoleIdParam::getId);
      if (ObjectUtil.isNotEmpty(sysRoleIdList)) {
         boolean containsSuperAdminRole = this.listByIds(sysRoleIdList)
            .stream()
            .map(SysRole::getCode)
            .collect(Collectors.toSet())
            .contains(SysBuildInEnum.BUILD_IN_ROLE_CODE.getValue());
         if (containsSuperAdminRole) {
            throw new CommonException("不可删除系统内置超管角色", new Object[0]);
         }

         this.sysRelationService
            .remove(
               (Wrapper<SysRelation>)((LambdaUpdateWrapper<SysRelation>)new LambdaUpdateWrapper<SysRelation>().in(SysRelation::getTargetId, sysRoleIdList))
                  .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue())
            );
         this.sysRelationService
            .remove(
               (Wrapper<SysRelation>)((LambdaUpdateWrapper<SysRelation>)new LambdaUpdateWrapper<SysRelation>().in(SysRelation::getObjectId, sysRoleIdList))
                  .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_ROLE_HAS_RESOURCE.getValue())
            );
         this.sysRelationService
            .remove(
               (Wrapper<SysRelation>)((LambdaUpdateWrapper<SysRelation>)new LambdaUpdateWrapper<SysRelation>().in(SysRelation::getObjectId, sysRoleIdList))
                  .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_ROLE_HAS_PERMISSION.getValue())
            );
         this.removeByIds(sysRoleIdList);
         CommonDataChangeEventCenter.doDeleteWithDataId(SysDataTypeEnum.ROLE.getValue(), sysRoleIdList);
      }
   }

   @Override
   public SysRole detail(SysRoleIdParam sysRoleIdParam) {
      return this.queryEntity(sysRoleIdParam.getId());
   }

   @Override
   public SysRoleOwnResourceResult ownResource(SysRoleIdParam sysRoleIdParam) {
      SysRoleOwnResourceResult sysRoleOwnResourceResult = new SysRoleOwnResourceResult();
      sysRoleOwnResourceResult.setId(sysRoleIdParam.getId());
      sysRoleOwnResourceResult.setGrantInfoList(
         this.sysRelationService
            .getRelationListByObjectIdAndCategory(sysRoleIdParam.getId(), SysRelationCategoryEnum.SYS_ROLE_HAS_RESOURCE.getValue())
            .stream()
            .map(
               sysRelation -> (SysRoleOwnResourceResult.SysRoleOwnResource)JSONUtil.toBean(
                  sysRelation.getExtJson(), SysRoleOwnResourceResult.SysRoleOwnResource.class
               )
            )
            .collect(Collectors.toList())
      );
      return sysRoleOwnResourceResult;
   }

   @Override
   public void grantResource(SysRoleGrantResourceParam sysRoleGrantResourceParam) {
      String id = sysRoleGrantResourceParam.getId();
      SysRole sysRole = this.queryEntity(id);
      List<String> menuIdList = sysRoleGrantResourceParam.getGrantInfoList()
         .stream()
         .map(SysRoleGrantResourceParam.SysRoleGrantResource::getMenuId)
         .collect(Collectors.toList());
      if (!SysBuildInEnum.BUILD_IN_ROLE_CODE.getValue().equals(sysRole.getCode()) && ObjectUtil.isNotEmpty(menuIdList)) {
         Set extJsonList = this.sysMenuService.listByIds(menuIdList).stream().map(SysMenu::getModule).collect(Collectors.toSet());
      }

      List<String> extJsonList = sysRoleGrantResourceParam.getGrantInfoList().stream().<String>map(JSONUtil::toJsonStr).collect(Collectors.toList());
      this.sysRelationService.saveRelationBatchWithClear(id, menuIdList, SysRelationCategoryEnum.SYS_ROLE_HAS_RESOURCE.getValue(), extJsonList);
   }

   @Override
   public SysRoleOwnMobileMenuResult ownMobileMenu(SysRoleIdParam sysRoleIdParam) {
      SysRoleOwnMobileMenuResult sysRoleOwnMobileMenuResult = new SysRoleOwnMobileMenuResult();
      sysRoleOwnMobileMenuResult.setId(sysRoleIdParam.getId());
      sysRoleOwnMobileMenuResult.setGrantInfoList(
         this.sysRelationService
            .getRelationListByObjectIdAndCategory(sysRoleIdParam.getId(), SysRelationCategoryEnum.SYS_ROLE_HAS_MOBILE_MENU.getValue())
            .stream()
            .map(
               sysRelation -> (SysRoleOwnMobileMenuResult.SysRoleOwnMobileMenu)JSONUtil.toBean(
                  sysRelation.getExtJson(), SysRoleOwnMobileMenuResult.SysRoleOwnMobileMenu.class
               )
            )
            .collect(Collectors.toList())
      );
      return sysRoleOwnMobileMenuResult;
   }

   @Override
   public void grantMobileMenu(SysRoleGrantMobileMenuParam sysRoleGrantMobileMenuParam) {
      String id = sysRoleGrantMobileMenuParam.getId();
      List<String> menuIdList = sysRoleGrantMobileMenuParam.getGrantInfoList()
         .stream()
         .map(SysRoleGrantMobileMenuParam.SysRoleGrantMobileMenu::getMenuId)
         .collect(Collectors.toList());
      List<String> extJsonList = sysRoleGrantMobileMenuParam.getGrantInfoList().stream().<String>map(JSONUtil::toJsonStr).collect(Collectors.toList());
      this.sysRelationService.saveRelationBatchWithClear(id, menuIdList, SysRelationCategoryEnum.SYS_ROLE_HAS_MOBILE_MENU.getValue(), extJsonList);
   }

   @Override
   public SysRoleOwnPermissionResult ownPermission(SysRoleIdParam sysRoleIdParam) {
      SysRoleOwnPermissionResult sysRoleOwnPermissionResult = new SysRoleOwnPermissionResult();
      sysRoleOwnPermissionResult.setId(sysRoleIdParam.getId());
      sysRoleOwnPermissionResult.setGrantInfoList(
         this.sysRelationService
            .getRelationListByObjectIdAndCategory(sysRoleIdParam.getId(), SysRelationCategoryEnum.SYS_ROLE_HAS_PERMISSION.getValue())
            .stream()
            .map(
               sysRelation -> (SysRoleOwnPermissionResult.SysRoleOwnPermission)JSONUtil.toBean(
                  sysRelation.getExtJson(), SysRoleOwnPermissionResult.SysRoleOwnPermission.class
               )
            )
            .collect(Collectors.toList())
      );
      return sysRoleOwnPermissionResult;
   }

   @Override
   public void grantPermission(SysRoleGrantPermissionParam sysRoleGrantPermissionParam) {
      String id = sysRoleGrantPermissionParam.getId();
      List<String> apiUrlList = sysRoleGrantPermissionParam.getGrantInfoList()
         .stream()
         .map(SysRoleGrantPermissionParam.SysRoleGrantPermission::getApiUrl)
         .collect(Collectors.toList());
      List<String> extJsonList = sysRoleGrantPermissionParam.getGrantInfoList().stream().<String>map(JSONUtil::toJsonStr).collect(Collectors.toList());
      this.sysRelationService.saveRelationBatchWithClear(id, apiUrlList, SysRelationCategoryEnum.SYS_ROLE_HAS_PERMISSION.getValue(), extJsonList);
   }

   @Override
   public List<String> ownUser(SysRoleIdParam sysRoleIdParam) {
      return this.sysRelationService.getRelationObjectIdListByTargetIdAndCategory(sysRoleIdParam.getId(), SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
   }

   @Override
   public void grantUser(SysRoleGrantUserParam sysRoleGrantUserParam) {
      String id = sysRoleGrantUserParam.getId();
      List<String> grantInfoList = sysRoleGrantUserParam.getGrantInfoList();
      this.sysRelationService
         .remove(
            (Wrapper<SysRelation>)((LambdaQueryWrapper<SysRelation>)new LambdaQueryWrapper<SysRelation>().eq(SysRelation::getTargetId, id))
               .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue())
         );
      this.sysRelationService.saveBatch(grantInfoList.stream().map(userId -> {
         SysRelation sysRelation = new SysRelation();
         sysRelation.setObjectId(userId);
         sysRelation.setTargetId(id);
         sysRelation.setCategory(SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
         return sysRelation;
      }).collect(Collectors.toList()));
   }

   @Override
   public SysRole queryEntity(String id) {
      SysRole sysRole = (SysRole)this.getById(id);
      if (ObjectUtil.isEmpty(sysRole)) {
         throw new CommonException("角色不存在，id值为：{}", new Object[]{id});
      } else {
         return sysRole;
      }
   }

   @Override
   public List<SysRoleGrantResourceTreeResult> resourceTreeSelector() {
      LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.in(SysMenu::getCategory, new Object[]{SysResourceCategoryEnum.MENU.getValue(), SysResourceCategoryEnum.BUTTON.getValue()});
      List<SysMenu> allMenuAndButtonAndFieldList = this.sysMenuService.list(lambdaQueryWrapper);
      List<SysMenu> sysMenuList = CollectionUtil.newArrayList(new SysMenu[0]);
      List<SysMenu> sysButtonList = CollectionUtil.newArrayList(new SysMenu[0]);
      allMenuAndButtonAndFieldList.forEach(sysMenu -> {
         if (sysMenu.getCategory().equals(SysResourceCategoryEnum.MENU.getValue())) {
            sysMenuList.add(sysMenu);
         }

         if (sysMenu.getCategory().equals(SysResourceCategoryEnum.BUTTON.getValue())) {
            sysButtonList.add(sysMenu);
         }
      });
      List<SysRoleGrantResourceTreeResult.SysRoleGrantResourceMenuResult> leafMenuList = CollectionUtil.newArrayList(
         new SysRoleGrantResourceTreeResult.SysRoleGrantResourceMenuResult[0]
      );
      SysMenu rootSysMenu = new SysMenu();
      rootSysMenu.setId("0");
      rootSysMenu.setParentId("-1");
      rootSysMenu.setSortCode(-1);
      sysMenuList.add(rootSysMenu);
      List<TreeNode<String>> treeNodeList = sysMenuList.stream()
         .map(sysMenu -> new TreeNode<String>(sysMenu.getId(), sysMenu.getParentId(), sysMenu.getTitle(), sysMenu.getSortCode()))
         .collect(Collectors.toList());
      List<Tree<String>> treeList = TreeUtil.build(treeNodeList, "-1");
      sysMenuList.forEach(
         sysMenu -> {
            boolean isLeafMenu = !"0".equals(sysMenu.getId()) && !sysMenu.getMenuType().equals(SysResourceMenuTypeEnum.CATALOG.getValue());
            if (isLeafMenu) {
               SysRoleGrantResourceTreeResult.SysRoleGrantResourceMenuResult sysRoleGrantResourceMenuResult = new SysRoleGrantResourceTreeResult.SysRoleGrantResourceMenuResult();
               BeanUtil.copyProperties(sysMenu, sysRoleGrantResourceMenuResult, new String[0]);
               JSONObject parentJsonObject = this.getParentNode(treeList, sysMenu);
               List<String> parentIdSplitList = StrUtil.split(parentJsonObject.getStr("parentId"), "-");
               List<String> parentNameSplitList = StrUtil.split(parentJsonObject.getStr("parentName"), "-");
               if (parentNameSplitList.size() <= 1) {
                  sysRoleGrantResourceMenuResult.setParentName(parentJsonObject.getStr("parentName"));
               } else {
                  sysRoleGrantResourceMenuResult.setParentId(parentIdSplitList.get(3));
                  sysRoleGrantResourceMenuResult.setParentName(parentNameSplitList.get(0));
                  StringBuilder selfNamePrefix = new StringBuilder();

                  for (int i = 1; i < parentNameSplitList.size(); i++) {
                     selfNamePrefix.append(parentNameSplitList.get(i)).append("-");
                  }

                  sysRoleGrantResourceMenuResult.setTitle(selfNamePrefix + sysRoleGrantResourceMenuResult.getTitle());
               }

               sysRoleGrantResourceMenuResult.setButton(
                  this.getChildListById(sysButtonList, sysMenu.getId(), false)
                     .stream()
                     .map(
                        sysMenuItem -> {
                           SysRoleGrantResourceTreeResult.SysRoleGrantResourceMenuResult.SysRoleGrantResourceButtonResult sysRoleGrantResourceButtonResult = new SysRoleGrantResourceTreeResult.SysRoleGrantResourceMenuResult.SysRoleGrantResourceButtonResult();
                           BeanUtil.copyProperties(sysMenuItem, sysRoleGrantResourceButtonResult, new String[0]);
                           return sysRoleGrantResourceButtonResult;
                        }
                     )
                     .collect(Collectors.toList())
               );
               leafMenuList.add(sysRoleGrantResourceMenuResult);
            }
         }
      );
      SysRoleGrantResourceTreeResult sysRoleGrantResourceTreeResult = new SysRoleGrantResourceTreeResult();
      sysRoleGrantResourceTreeResult.setId("menu-resource-root");
      sysRoleGrantResourceTreeResult.setTitle("菜单权限");
      sysRoleGrantResourceTreeResult.setMenu(leafMenuList);
      return CollectionUtil.newArrayList(new SysRoleGrantResourceTreeResult[]{sysRoleGrantResourceTreeResult});
   }

   @Override
   public List<String> permissionTreeSelector() {
      List<String> permissionResult = CollectionUtil.newArrayList(new String[0]);
      SpringUtil.getApplicationContext()
         .getBeansOfType(RequestMappingHandlerMapping.class)
         .values()
         .forEach(requestMappingHandlerMapping -> requestMappingHandlerMapping.getHandlerMethods().forEach((key, value) -> {
            SaCheckPermission saCheckPermission = value.getMethod().getAnnotation(SaCheckPermission.class);
            if (ObjectUtil.isNotEmpty(saCheckPermission)) {
               PathPatternsRequestCondition pathPatternsCondition = key.getPathPatternsCondition();
               if (pathPatternsCondition != null) {
                  String apiName = "未定义接口名称";
                  Operation apiOperation = value.getMethod().getAnnotation(Operation.class);
                  if (ObjectUtil.isNotEmpty(apiOperation)) {
                     String annotationValue = apiOperation.summary();
                     if (ObjectUtil.isNotEmpty(annotationValue)) {
                        apiName = annotationValue;
                     }
                  }

                  String nm = "[" + apiName + "]";
                  pathPatternsCondition.getPatterns().forEach(pt -> permissionResult.add(pt + nm));
               }
            }
         }));
      return CollectionUtil.sortByPinyin(
         permissionResult.stream()
            .filter(api -> !api.startsWith("/[") && !api.startsWith("/error") && !api.contains("/api-docs") && !api.contains("/swagger-resources"))
            .collect(Collectors.toList())
      );
   }

   @Override
   public Page<SysRole> roleSelector(SysRoleSelectorRoleParam sysRoleSelectorRoleParam) {
      LambdaQueryWrapper<SysRole> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.select(SysRole::getId, SysRole::getOrgId, SysRole::getName, SysRole::getCategory, SysRole::getSortCode);
      lambdaQueryWrapper.eq(SysRole::getCategory, SysRoleCategoryEnum.GLOBAL.getValue());
      if (ObjectUtil.isNotEmpty(sysRoleSelectorRoleParam.getSearchKey())) {
         lambdaQueryWrapper.like(SysRole::getName, sysRoleSelectorRoleParam.getSearchKey());
      }

      if (sysRoleSelectorRoleParam.isExcludeSuperAdmin()) {
         lambdaQueryWrapper.ne(SysRole::getCode, SysBuildInEnum.BUILD_IN_ROLE_CODE.getValue());
      }

      lambdaQueryWrapper.orderByAsc(SysRole::getSortCode);
      return (Page<SysRole>)this.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
   }

   @Override
   public Page<SysUser> userSelector(SysRoleSelectorUserParam sysRoleSelectorUserParam) {
      LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.select(
         
            SysUser::getId,
            SysUser::getAvatar,
            SysUser::getOrgId,
            SysUser::getPositionId,
            SysUser::getAccount,
            SysUser::getName,
            SysUser::getSortCode,
            SysUser::getGender,
            SysUser::getEntryDate
         
      );
      if (ObjectUtil.isEmpty(sysRoleSelectorUserParam.getSearchKey())) {
         return this.sysUserService.getAllUserSelectorList();
      } else {
         lambdaQueryWrapper.like(SysUser::getName, sysRoleSelectorUserParam.getSearchKey());
         lambdaQueryWrapper.orderByAsc(SysUser::getSortCode);
         return (Page<SysUser>)this.sysUserService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
      }
   }

   public JSONObject getParentNode(List<Tree<String>> treeList, SysMenu sysMenu) {
      List<Tree<String>> resultList = CollectionUtil.newArrayList(new Tree[0]);
      this.getNode(treeList, sysMenu.getId(), resultList);
      JSONObject jsonObject = JSONUtil.createObj();
      if (ObjectUtil.isNotEmpty(resultList)) {
         Tree<String> currentNode = resultList.get(0);
         if (!"0".equals(currentNode.getId()) && !"0".equals(currentNode.getParentId())) {
            jsonObject.set("parentId", StrUtil.join("-", CollectionUtil.reverse((List)CollectionUtil.removeNull(this.getParentsId(currentNode, false)))));
            jsonObject.set(
               "parentName", StrUtil.join("-", CollectionUtil.reverse((List)CollectionUtil.removeNull(TreeUtil.getParentsName(currentNode, false))))
            );
         } else {
            jsonObject.set("parentId", sysMenu.getId());
            jsonObject.set("parentName", sysMenu.getTitle());
         }
      } else {
         jsonObject.set("parentId", sysMenu.getId());
         jsonObject.set("parentName", sysMenu.getTitle());
      }

      return jsonObject;
   }

   public List<String> getParentsId(Tree<String> node, boolean includeCurrentNode) {
      List<String> result = new ArrayList<>();
      if (null == node) {
         return result;
      } else {
         if (includeCurrentNode) {
            result.add((String)node.getId());
         }

         for (Tree<String> parent = node.getParent(); null != parent; parent = parent.getParent()) {
            result.add((String)parent.getId());
         }

         return result;
      }
   }

   public void getNode(List<Tree<String>> treeList, String id, List<Tree<String>> resultList) {
      for (Tree<String> tree : treeList) {
         if (((String)tree.getId()).equals(id)) {
            resultList.add(tree);
            break;
         }

         List<Tree<String>> children = tree.getChildren();
         if (ObjectUtil.isNotEmpty(children)) {
            this.getNode(children, id, resultList);
         }
      }
   }

   public List<SysMenu> getChildListById(List<SysMenu> originDataList, String id, boolean includeSelf) {
      List<SysMenu> sysResourceList = CollectionUtil.newArrayList(new SysMenu[0]);
      this.execRecursionFindChild(originDataList, id, sysResourceList);
      if (includeSelf) {
         SysMenu self = this.getById(originDataList, id);
         if (ObjectUtil.isNotEmpty(self)) {
            sysResourceList.add(self);
         }
      }

      return sysResourceList;
   }

   public List<SysMenu> getParentListById(List<SysMenu> originDataList, String id, boolean includeSelf) {
      List<SysMenu> sysResourceList = CollectionUtil.newArrayList(new SysMenu[0]);
      this.execRecursionFindParent(originDataList, id, sysResourceList);
      if (includeSelf) {
         SysMenu self = this.getById(originDataList, id);
         if (ObjectUtil.isNotEmpty(self)) {
            sysResourceList.add(self);
         }
      }

      return sysResourceList;
   }

   public void execRecursionFindChild(List<SysMenu> originDataList, String id, List<SysMenu> resultList) {
      originDataList.forEach(item -> {
         if (item.getParentId().equals(id)) {
            resultList.add(item);
            this.execRecursionFindChild(originDataList, item.getId(), resultList);
         }
      });
   }

   public void execRecursionFindParent(List<SysMenu> originDataList, String id, List<SysMenu> resultList) {
      originDataList.forEach(item -> {
         if (item.getId().equals(id)) {
            SysMenu parent = this.getById(originDataList, item.getParentId());
            if (ObjectUtil.isNotEmpty(parent)) {
               resultList.add(parent);
            }

            this.execRecursionFindParent(originDataList, item.getParentId(), resultList);
         }
      });
   }

   public SysMenu getById(List<SysMenu> originDataList, String id) {
      int index = CollStreamUtil.toList(originDataList, SysMenu::getId).indexOf(id);
      return index == -1 ? null : originDataList.get(index);
   }

   public SysMenu getParentById(List<SysMenu> originDataList, String id) {
      SysMenu self = this.getById(originDataList, id);
      return ObjectUtil.isNotEmpty(self) ? self : this.getById(originDataList, self.getParentId());
   }

   public SysMenu getChildById(List<SysMenu> originDataList, String id) {
      int index = CollStreamUtil.toList(originDataList, SysMenu::getParentId).indexOf(id);
      return index == -1 ? null : originDataList.get(index);
   }
}
