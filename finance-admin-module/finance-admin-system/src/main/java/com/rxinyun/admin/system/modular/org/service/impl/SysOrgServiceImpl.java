package com.rxinyun.admin.system.modular.org.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.system.core.enums.SysDataTypeEnum;
import com.rxinyun.admin.system.modular.org.entity.SysOrg;
import com.rxinyun.admin.system.modular.org.enums.SysOrgCategoryEnum;
import com.rxinyun.admin.system.modular.org.mapper.SysOrgMapper;
import com.rxinyun.admin.system.modular.org.param.SysOrgAddParam;
import com.rxinyun.admin.system.modular.org.param.SysOrgEditParam;
import com.rxinyun.admin.system.modular.org.param.SysOrgIdParam;
import com.rxinyun.admin.system.modular.org.param.SysOrgPageParam;
import com.rxinyun.admin.system.modular.org.param.SysOrgSelectorOrgListParam;
import com.rxinyun.admin.system.modular.org.param.SysOrgSelectorUserParam;
import com.rxinyun.admin.system.modular.org.service.SysOrgService;
import com.rxinyun.admin.system.modular.position.entity.SysPosition;
import com.rxinyun.admin.system.modular.position.service.SysPositionService;
import com.rxinyun.admin.system.modular.role.entity.SysRole;
import com.rxinyun.admin.system.modular.role.service.SysRoleService;
import com.rxinyun.admin.system.modular.user.entity.SysUser;
import com.rxinyun.admin.system.modular.user.service.SysUserService;
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.listener.CommonDataChangeEventCenter;
import com.rxinyun.common.page.CommonPageRequest;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysOrgServiceImpl extends ServiceImpl<SysOrgMapper, SysOrg> implements SysOrgService {
   public static final String ORG_CACHE_ALL_KEY = "sys-org:all";
   @Resource
   private CommonCacheOperator commonCacheOperator;
   @Resource
   private SysRoleService sysRoleService;
   @Resource
   private SysPositionService sysPositionService;
   @Resource
   private SysUserService sysUserService;

   @Override
   public Page<SysOrg> page(SysOrgPageParam sysOrgPageParam) {
      QueryWrapper<SysOrg> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().select(SysOrg::getId, SysOrg::getParentId, SysOrg::getName, SysOrg::getCategory, SysOrg::getSortCode);
      if (ObjectUtil.isNotEmpty(sysOrgPageParam.getParentId())) {
         queryWrapper.lambda().eq(SysOrg::getParentId, sysOrgPageParam.getParentId());
      }

      if (ObjectUtil.isNotEmpty(sysOrgPageParam.getSearchKey())) {
         queryWrapper.lambda().like(SysOrg::getName, sysOrgPageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{sysOrgPageParam.getSortField(), sysOrgPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(sysOrgPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, sysOrgPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(sysOrgPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(SysOrg::getSortCode);
      }

      return (Page<SysOrg>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public List<Tree<String>> tree() {
      List<SysOrg> sysOrgList = this.getAllOrgList();
      List<TreeNode<String>> treeNodeList = sysOrgList.stream()
         .map(sysOrg -> new TreeNode<String>(sysOrg.getId(), sysOrg.getParentId(), sysOrg.getName(), sysOrg.getSortCode()).setExtra(JSONUtil.parseObj(sysOrg)))
         .collect(Collectors.toList());
      return TreeUtil.build(treeNodeList, "0");
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(SysOrgAddParam sysOrgAddParam) {
      SysOrgCategoryEnum.validate(sysOrgAddParam.getCategory());
      SysOrg sysOrg = (SysOrg)BeanUtil.toBean(sysOrgAddParam, SysOrg.class);
      boolean repeatName = this.count(
            (Wrapper<SysOrg>)((LambdaQueryWrapper<SysOrg>)new LambdaQueryWrapper<SysOrg>().eq(SysOrg::getParentId, sysOrg.getParentId())).eq(SysOrg::getName, sysOrg.getName())
         )
         > 0L;
      if (repeatName) {
         throw new CommonException("存在重复的同级组织，名称为：{}", new Object[]{sysOrg.getName()});
      } else {
         sysOrg.setCode(RandomUtil.randomString(10));
         this.save(sysOrg);
         CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.ORG.getValue(), JSONUtil.createArray().put(sysOrg));
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(SysOrgEditParam sysOrgEditParam) {
      SysOrgCategoryEnum.validate(sysOrgEditParam.getCategory());
      SysOrg sysOrg = this.queryEntity(sysOrgEditParam.getId());
      BeanUtil.copyProperties(sysOrgEditParam, sysOrg, new String[0]);
      boolean repeatName = this.count(
            (Wrapper<SysOrg>)((LambdaQueryWrapper<SysOrg>)((LambdaQueryWrapper<SysOrg>)new LambdaQueryWrapper<SysOrg>().eq(SysOrg::getParentId, sysOrg.getParentId()))
                  .eq(SysOrg::getName, sysOrg.getName()))
               .ne(SysOrg::getId, sysOrg.getId())
         )
         > 0L;
      if (repeatName) {
         throw new CommonException("存在重复的同级组织，名称为：{}", new Object[]{sysOrg.getName()});
      } else {
         List<SysOrg> originDataList = this.getAllOrgList();
         boolean errorLevel = this.getChildListById(originDataList, sysOrg.getId(), true)
            .stream()
            .map(SysOrg::getId)
            .collect(Collectors.toList())
            .contains(sysOrg.getParentId());
         if (errorLevel) {
            throw new CommonException("不可选择上级组织：{}", new Object[]{this.getById(originDataList, sysOrg.getParentId()).getName()});
         } else {
            this.updateById(sysOrg);
            CommonDataChangeEventCenter.doUpdateWithData(SysDataTypeEnum.ORG.getValue(), JSONUtil.createArray().put(sysOrg));
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<SysOrgIdParam> sysOrgIdParamList) {
      List<String> orgIdList = CollStreamUtil.toList(sysOrgIdParamList, SysOrgIdParam::getId);
      if (ObjectUtil.isNotEmpty(orgIdList)) {
         List<SysOrg> allOrgList = this.getAllOrgList();
         List<String> toDeleteOrgIdList = CollectionUtil.newArrayList(new String[0]);
         orgIdList.forEach(
            orgId -> toDeleteOrgIdList.addAll(this.getChildListById(allOrgList, orgId, true).stream().map(SysOrg::getId).collect(Collectors.toList()))
         );
         boolean hasOrgUser = this.sysUserService.count((Wrapper<SysUser>)new LambdaQueryWrapper<SysUser>().in(SysUser::getOrgId, toDeleteOrgIdList)) > 0L;
         if (hasOrgUser) {
            throw new CommonException("请先删除组织下的用户", new Object[0]);
         }

         List<String> positionJsonList = this.sysUserService
            .list((Wrapper<SysUser>)new LambdaQueryWrapper<SysUser>().isNotNull(SysUser::getPositionJson))
            .stream()
            .map(SysUser::getPositionJson)
            .collect(Collectors.toList());
         if (ObjectUtil.isNotEmpty(positionJsonList)) {
            List<String> positionOrgIdList = CollectionUtil.newArrayList(new String[0]);
            positionJsonList.forEach(
               positionJson -> JSONUtil.toList(JSONUtil.parseArray(positionJson), JSONObject.class)
                  .forEach(jsonObject -> positionOrgIdList.add(jsonObject.getStr("orgId")))
            );
            boolean hasPositionUser = CollectionUtil.intersectionDistinct(toDeleteOrgIdList, CollectionUtil.removeNull(positionOrgIdList), new Collection[0])
                  .size()
               > 0;
            if (hasPositionUser) {
               throw new CommonException("请先删除组织下的用户", new Object[0]);
            }
         }

         boolean hasRole = this.sysRoleService.count((Wrapper<SysRole>)new LambdaQueryWrapper<SysRole>().in(SysRole::getOrgId, toDeleteOrgIdList)) > 0L;
         if (hasRole) {
            throw new CommonException("请先删除组织下的角色", new Object[0]);
         }

         boolean hasPosition = this.sysPositionService.count((Wrapper<SysPosition>)new LambdaQueryWrapper<SysPosition>().in(SysPosition::getOrgId, toDeleteOrgIdList)) > 0L;
         if (hasPosition) {
            throw new CommonException("请先删除组织下的职位", new Object[0]);
         }

         this.removeByIds(toDeleteOrgIdList);
         CommonDataChangeEventCenter.doDeleteWithDataId(SysDataTypeEnum.ORG.getValue(), toDeleteOrgIdList);
      }
   }

   @Override
   public SysOrg detail(SysOrgIdParam sysOrgIdParam) {
      return this.queryEntity(sysOrgIdParam.getId());
   }

   @Override
   public SysOrg queryEntity(String id) {
      SysOrg sysOrg = (SysOrg)this.getById(id);
      if (ObjectUtil.isEmpty(sysOrg)) {
         throw new CommonException("组织不存在，id值为：{}", new Object[]{id});
      } else {
         return sysOrg;
      }
   }

   @Override
   public List<SysOrg> getAllOrgList() {
      Object cacheValue = this.commonCacheOperator.get("sys-org:all");
      if (ObjectUtil.isNotEmpty(cacheValue)) {
         return JSONUtil.toList(JSONUtil.parseArray(cacheValue), SysOrg.class);
      } else {
         List<SysOrg> orgList = this.list((Wrapper<SysOrg>)new LambdaQueryWrapper<SysOrg>().orderByAsc(SysOrg::getSortCode));
         if (ObjectUtil.isNotEmpty(orgList)) {
            this.commonCacheOperator.put("sys-org:all", JSONUtil.toJsonStr(orgList));
         }

         return orgList;
      }
   }

   @Override
   public String getOrgIdByOrgFullNameWithCreate(String orgFullName) {
      List<SysOrg> cachedAllOrgList = this.getAllOrgList();
      List<Tree<String>> treeList = TreeUtil.build(
         cachedAllOrgList.stream()
            .map(sysOrg -> new TreeNode<String>(sysOrg.getId(), sysOrg.getParentId(), sysOrg.getName(), sysOrg.getSortCode()))
            .collect(Collectors.toList()),
         "0"
      );
      return this.findOrgIdByOrgName("0", StrUtil.split(orgFullName, "-").iterator(), cachedAllOrgList, treeList);
   }

   public String findOrgIdByOrgName(String parentId, Iterator<String> iterator, List<SysOrg> cachedAllOrgList, List<Tree<String>> treeList) {
      String orgName = iterator.next();
      if (ObjectUtil.isNotEmpty(treeList)) {
         List<Tree<String>> findList = treeList.stream().filter(tree -> tree.getName().equals(orgName)).collect(Collectors.toList());
         if (ObjectUtil.isNotEmpty(findList)) {
            if (iterator.hasNext()) {
               return this.findOrgIdByOrgName((String)findList.get(0).getId(), iterator, cachedAllOrgList, findList.get(0).getChildren());
            }

            return (String)findList.get(0).getId();
         }
      }

      String orgId = this.doCreateOrg(parentId, orgName, cachedAllOrgList);
      return iterator.hasNext() ? this.findOrgIdByOrgName(orgId, iterator, cachedAllOrgList, CollectionUtil.newArrayList(new Tree[0])) : orgId;
   }

   public String doCreateOrg(String parentId, String orgName, List<SysOrg> cachedAllOrgList) {
      SysOrg sysOrg = new SysOrg();
      sysOrg.setName(orgName);
      sysOrg.setCode(RandomUtil.randomString(10));
      sysOrg.setParentId(parentId);
      sysOrg.setCategory("0".equals(parentId) ? SysOrgCategoryEnum.COMPANY.getValue() : SysOrgCategoryEnum.DEPT.getValue());
      sysOrg.setSortCode(99);
      this.save(sysOrg);
      CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.ORG.getValue(), JSONUtil.createArray().put(sysOrg));
      cachedAllOrgList.add(sysOrg);
      this.commonCacheOperator.put("sys-org:all", cachedAllOrgList);
      return sysOrg.getId();
   }

   @Override
   public List<Tree<String>> orgTreeSelector() {
      List<SysOrg> sysOrgList = this.getAllOrgList();
      List<TreeNode<String>> treeNodeList = sysOrgList.stream()
         .map(sysOrg -> new TreeNode<String>(sysOrg.getId(), sysOrg.getParentId(), sysOrg.getName(), sysOrg.getSortCode()))
         .collect(Collectors.toList());
      return TreeUtil.build(treeNodeList, "0");
   }

   @Override
   public Page<SysOrg> orgListSelector(SysOrgSelectorOrgListParam sysOrgSelectorOrgListParam) {
      LambdaQueryWrapper<SysOrg> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.select(SysOrg::getId, SysOrg::getParentId, SysOrg::getName, SysOrg::getCategory, SysOrg::getSortCode);
      if (ObjectUtil.isNotEmpty(sysOrgSelectorOrgListParam.getParentId())) {
         lambdaQueryWrapper.eq(SysOrg::getParentId, sysOrgSelectorOrgListParam.getParentId());
      }

      if (ObjectUtil.isNotEmpty(sysOrgSelectorOrgListParam.getSearchKey())) {
         lambdaQueryWrapper.like(SysOrg::getName, sysOrgSelectorOrgListParam.getSearchKey());
      }

      lambdaQueryWrapper.orderByAsc(SysOrg::getSortCode);
      return (Page<SysOrg>)this.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
   }

   @Override
   public Page<SysUser> userSelector(SysOrgSelectorUserParam sysOrgSelectorUserParam) {
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
      if (ObjectUtil.isAllEmpty(new Object[]{sysOrgSelectorUserParam.getOrgId(), sysOrgSelectorUserParam.getSearchKey()})) {
         return this.sysUserService.getAllUserSelectorList();
      } else {
         if (ObjectUtil.isNotEmpty(sysOrgSelectorUserParam.getOrgId())) {
            List<String> childOrgIdList = CollStreamUtil.toList(
               this.getChildListById(this.getAllOrgList(), sysOrgSelectorUserParam.getOrgId(), true), SysOrg::getId
            );
            if (!ObjectUtil.isNotEmpty(childOrgIdList)) {
               return new Page();
            }

            lambdaQueryWrapper.in(SysUser::getOrgId, childOrgIdList);
         }

         if (ObjectUtil.isNotEmpty(sysOrgSelectorUserParam.getSearchKey())) {
            lambdaQueryWrapper.like(SysUser::getName, sysOrgSelectorUserParam.getSearchKey());
         }

         lambdaQueryWrapper.orderByAsc(SysUser::getSortCode);
         return (Page<SysUser>)this.sysUserService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
      }
   }

   @Override
   public List<SysOrg> getParentAndChildListById(List<SysOrg> originDataList, String id, boolean includeSelf) {
      List<SysOrg> parentListById = this.getParentListById(originDataList, id, false);
      List<SysOrg> childListById = this.getChildListById(originDataList, id, true);
      parentListById.addAll(childListById);
      return parentListById;
   }

   @Override
   public List<SysOrg> getChildListById(List<SysOrg> originDataList, String id, boolean includeSelf) {
      List<SysOrg> resultList = CollectionUtil.newArrayList(new SysOrg[0]);
      this.execRecursionFindChild(originDataList, id, resultList);
      if (includeSelf) {
         SysOrg self = this.getById(originDataList, id);
         if (ObjectUtil.isNotEmpty(self)) {
            resultList.add(self);
         }
      }

      return resultList;
   }

   @Override
   public List<SysOrg> getParentListById(List<SysOrg> originDataList, String id, boolean includeSelf) {
      List<SysOrg> resultList = CollectionUtil.newArrayList(new SysOrg[0]);
      this.execRecursionFindParent(originDataList, id, resultList);
      if (includeSelf) {
         SysOrg self = this.getById(originDataList, id);
         if (ObjectUtil.isNotEmpty(self)) {
            resultList.add(self);
         }
      }

      return resultList;
   }

   public void execRecursionFindChild(List<SysOrg> originDataList, String id, List<SysOrg> resultList) {
      originDataList.forEach(item -> {
         if (item.getParentId().equals(id)) {
            resultList.add(item);
            this.execRecursionFindChild(originDataList, item.getId(), resultList);
         }
      });
   }

   public void execRecursionFindParent(List<SysOrg> originDataList, String id, List<SysOrg> resultList) {
      originDataList.forEach(item -> {
         if (item.getId().equals(id)) {
            SysOrg parent = this.getById(originDataList, item.getParentId());
            if (ObjectUtil.isNotEmpty(parent)) {
               resultList.add(parent);
            }

            this.execRecursionFindParent(originDataList, item.getParentId(), resultList);
         }
      });
   }

   @Override
   public SysOrg getById(List<SysOrg> originDataList, String id) {
      int index = CollStreamUtil.toList(originDataList, SysOrg::getId).indexOf(id);
      return index == -1 ? null : originDataList.get(index);
   }

   @Override
   public SysOrg getParentById(List<SysOrg> originDataList, String id) {
      SysOrg self = this.getById(originDataList, id);
      return ObjectUtil.isNotEmpty(self) ? self : this.getById(originDataList, self.getParentId());
   }

   @Override
   public SysOrg getChildById(List<SysOrg> originDataList, String id) {
      int index = CollStreamUtil.toList(originDataList, SysOrg::getParentId).indexOf(id);
      return index == -1 ? null : originDataList.get(index);
   }
}
