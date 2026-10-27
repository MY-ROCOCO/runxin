package com.rxinyun.admin.system.modular.resource.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.system.core.enums.SysDataTypeEnum;
import com.rxinyun.admin.system.modular.relation.entity.SysRelation;
import com.rxinyun.admin.system.modular.relation.enums.SysRelationCategoryEnum;
import com.rxinyun.admin.system.modular.relation.service.SysRelationService;
import com.rxinyun.admin.system.modular.resource.entity.SysMenu;
import com.rxinyun.admin.system.modular.resource.entity.SysModule;
import com.rxinyun.admin.system.modular.resource.enums.SysResourceCategoryEnum;
import com.rxinyun.admin.system.modular.resource.enums.SysResourceMenuTypeEnum;
import com.rxinyun.admin.system.modular.resource.mapper.SysMenuMapper;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuAddParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuChangeModuleParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuEditParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuIdParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuPageParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuSelectorMenuParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuSelectorModuleParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuTreeParam;
import com.rxinyun.admin.system.modular.resource.service.SysMenuService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.listener.CommonDataChangeEventCenter;
import com.rxinyun.common.page.CommonPageRequest;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
   @Resource
   private SysRelationService sysRelationService;

   @Override
   public Page<SysMenu> page(SysMenuPageParam sysMenuPageParam) {
      QueryWrapper<SysMenu> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue());
      if (ObjectUtil.isNotEmpty(sysMenuPageParam.getSearchKey())) {
         queryWrapper.lambda().like(SysMenu::getTitle, sysMenuPageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{sysMenuPageParam.getSortField(), sysMenuPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(sysMenuPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, sysMenuPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(sysMenuPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(SysMenu::getSortCode);
      }

      return (Page<SysMenu>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public List<Tree<String>> tree(SysMenuTreeParam sysMenuTreeParam) {
      LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = new LambdaQueryWrapper();
      ((LambdaQueryWrapper<SysMenu>)lambdaQueryWrapper.eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue())).orderByAsc(SysMenu::getSortCode);
      if (ObjectUtil.isNotEmpty(sysMenuTreeParam.getSearchKey())) {
         lambdaQueryWrapper.like(SysMenu::getTitle, sysMenuTreeParam.getSearchKey());
      }

      List<SysMenu> resourceList = this.list(lambdaQueryWrapper);
      this.fillParentSysMenuInfo(resourceList);
      List<TreeNode<String>> treeNodeList = resourceList.stream()
         .map(sysMenu -> new TreeNode<String>(sysMenu.getId(), sysMenu.getParentId(), sysMenu.getTitle(), sysMenu.getSortCode()).setExtra(JSONUtil.parseObj(sysMenu)))
         .collect(Collectors.toList());
      return TreeUtil.build(treeNodeList, "0");
   }

   private void fillParentSysMenuInfo(List<SysMenu> resourceList) {
      if (CollUtil.isNotEmpty(resourceList)) {
         List<SysMenu> parentRelationSysMenus = resourceList.stream().filter(distinctByKey(SysMenu::getParentId)).collect(Collectors.toList());
         List<String> parentIds = null;
         if (CollUtil.isNotEmpty(parentRelationSysMenus)) {
            parentIds = CollUtil.newArrayList(new String[0]);

            for (SysMenu parentRelationSysMenu : parentRelationSysMenus) {
               if (!StrUtil.equals(parentRelationSysMenu.getParentId(), "0")) {
                  parentIds.add(parentRelationSysMenu.getParentId());
               }
            }
         }

         if (CollUtil.isNotEmpty(parentIds)) {
            LambdaQueryWrapper<SysMenu> parentSysMenuLambdaQueryWrapper = new LambdaQueryWrapper();
            parentSysMenuLambdaQueryWrapper.in(SysMenu::getId, parentIds);
            List<SysMenu> parentSysMenus = this.list(parentSysMenuLambdaQueryWrapper);
            if (CollUtil.isNotEmpty(parentSysMenus)) {
               this.fillParentSysMenuInfo(parentSysMenus);
               resourceList.addAll(parentSysMenus);
            }
         }
      }
   }

   private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
      Set<Object> seen = ConcurrentHashMap.newKeySet();
      return t -> seen.add(keyExtractor.apply(t));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(SysMenuAddParam sysMenuAddParam) {
      this.checkParam(sysMenuAddParam);
      SysMenu sysMenu = (SysMenu)BeanUtil.toBean(sysMenuAddParam, SysMenu.class);
      this.checkRepeatTitle(sysMenu.getParentId(), sysMenu.getTitle(), null);
      this.checkRepeatName(sysMenu.getParentId(), sysMenu.getName(), null);
      List<SysMenu> originDataList = this.list((Wrapper<SysMenu>)new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue()));
      if (!"0".equals(sysMenuAddParam.getParentId())) {
         SysMenu parentMenu = this.getById(originDataList, sysMenuAddParam.getParentId());
         if (ObjectUtil.isEmpty(parentMenu)) {
            throw new CommonException("上级菜单不存在，id值为：{}", new Object[]{sysMenuAddParam.getParentId()});
         }
      }

      sysMenu.setCategory(SysResourceCategoryEnum.MENU.getValue());
      sysMenu.setModule(null);
      this.save(sysMenu);
      CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.RESOURCE.getValue(), JSONUtil.createArray().put(sysMenu));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public String addForGenMenu(String parentId, String busName, String title, String module, String path) {
      if (!"0".equals(parentId)) {
         SysMenu parentMenu = this.queryEntity(parentId);
         if (ObjectUtil.isEmpty(parentMenu)) {
            throw new CommonException("上级菜单不存在，id值为：{}", new Object[]{parentId});
         }
      }

      SysMenu sysOldMenu = (SysMenu)this.getOne(
         (Wrapper<SysMenu>)((LambdaQueryWrapper<SysMenu>)((LambdaQueryWrapper<SysMenu>)((LambdaQueryWrapper<SysMenu>)((LambdaQueryWrapper<SysMenu>)new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getTitle, title))
                     .eq(SysMenu::getParentId, parentId))
                  .eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue()))
               .eq(SysMenu::getMenuType, SysResourceMenuTypeEnum.MENU.getValue()))
            .eq(SysMenu::getPath, path)
      );
      if (ObjectUtil.isNotEmpty(sysOldMenu)) {
         SysMenuIdParam sysMenuIdParam = new SysMenuIdParam();
         sysMenuIdParam.setId(sysOldMenu.getId());
         this.delete(CollectionUtil.newArrayList(new SysMenuIdParam[]{sysMenuIdParam}));
      }

      SysMenu sysMenu = new SysMenu();
      sysMenu.setParentId(parentId);
      sysMenu.setTitle(title);
      sysMenu.setName(busName);
      sysMenu.setCode(RandomUtil.randomString(10));
      sysMenu.setCategory(SysResourceCategoryEnum.MENU.getValue());
      sysMenu.setModule(null);
      sysMenu.setMenuType(SysResourceMenuTypeEnum.MENU.getValue());
      sysMenu.setPath(path);
      sysMenu.setComponent(StrUtil.removePrefix(path, "/") + "/index");
      sysMenu.setIcon("appstore-outlined");
      sysMenu.setSortCode(99);
      this.checkRepeatName(sysMenu.getParentId(), sysMenu.getName(), null);
      this.save(sysMenu);
      return sysMenu.getId();
   }

   private void checkParam(SysMenuAddParam sysMenuAddParam) {
      SysResourceMenuTypeEnum.validate(sysMenuAddParam.getMenuType());
      if (sysMenuAddParam.getTitle().contains("-")) {
         throw new CommonException("title不可包含特殊字符【-】", new Object[0]);
      } else {
         if (SysResourceMenuTypeEnum.MENU.getValue().equals(sysMenuAddParam.getMenuType())) {
            if (ObjectUtil.isEmpty(sysMenuAddParam.getName())) {
               throw new CommonException("name不能为空", new Object[0]);
            }

            if (ObjectUtil.isEmpty(sysMenuAddParam.getComponent())) {
               throw new CommonException("component不能为空", new Object[0]);
            }
         } else if (!SysResourceMenuTypeEnum.IFRAME.getValue().equals(sysMenuAddParam.getMenuType())
            && !SysResourceMenuTypeEnum.LINK.getValue().equals(sysMenuAddParam.getMenuType())) {
            sysMenuAddParam.setName(null);
            sysMenuAddParam.setComponent(null);
         } else {
            sysMenuAddParam.setName(RandomUtil.randomNumbers(10));
            sysMenuAddParam.setComponent(null);
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(SysMenuEditParam sysMenuEditParam) {
      SysMenu sysMenu = this.queryEntity(sysMenuEditParam.getId());
      this.checkParam(sysMenuEditParam);
      BeanUtil.copyProperties(sysMenuEditParam, sysMenu, new String[0]);
      this.checkRepeatTitle(sysMenu.getParentId(), sysMenu.getTitle(), sysMenu.getId());
      this.checkRepeatName(sysMenu.getParentId(), sysMenu.getName(), sysMenu.getId());
      if (sysMenuEditParam.getMenuType().equals(SysResourceMenuTypeEnum.MENU.getValue())) {
         sysMenuEditParam.setComponent(null);
         sysMenuEditParam.setName(null);
      }

      List<SysMenu> originDataList = this.list((Wrapper<SysMenu>)new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue()));
      boolean errorLevel = this.getChildListById(originDataList, sysMenu.getId(), true)
         .stream()
         .map(SysMenu::getId)
         .collect(Collectors.toList())
         .contains(sysMenu.getParentId());
      if (errorLevel) {
         throw new CommonException("不可选择上级菜单：{}", new Object[]{this.getById(originDataList, sysMenu.getParentId()).getName()});
      } else {
         if (!"0".equals(sysMenuEditParam.getParentId())) {
            SysMenu parentMenu = this.getById(originDataList, sysMenuEditParam.getParentId());
            if (ObjectUtil.isEmpty(parentMenu)) {
               throw new CommonException("上级菜单不存在，id值为：{}", new Object[]{sysMenuEditParam.getParentId()});
            }
         }

         sysMenu.setModule(null);
         this.updateById(sysMenu);
         CommonDataChangeEventCenter.doUpdateWithData(SysDataTypeEnum.RESOURCE.getValue(), JSONUtil.createArray().put(sysMenu));
      }
   }

   @Override
   public void changeModule(SysMenuChangeModuleParam sysMenuChangeModuleParam) {
      throw new CommonException("模块能力已下线", new Object[0]);
   }

   private void checkParam(SysMenuEditParam sysMenuEditParam) {
      SysResourceMenuTypeEnum.validate(sysMenuEditParam.getMenuType());
      if (sysMenuEditParam.getTitle().contains("-")) {
         throw new CommonException("title不可包含特殊字符【-】", new Object[0]);
      } else {
         if (SysResourceMenuTypeEnum.MENU.getValue().equals(sysMenuEditParam.getMenuType())) {
            if (ObjectUtil.isEmpty(sysMenuEditParam.getName())) {
               throw new CommonException("name不能为空", new Object[0]);
            }

            if (ObjectUtil.isEmpty(sysMenuEditParam.getComponent())) {
               throw new CommonException("component不能为空", new Object[0]);
            }
         } else if (!SysResourceMenuTypeEnum.IFRAME.getValue().equals(sysMenuEditParam.getMenuType())
            && !SysResourceMenuTypeEnum.LINK.getValue().equals(sysMenuEditParam.getMenuType())) {
            sysMenuEditParam.setName(null);
            sysMenuEditParam.setComponent(null);
         } else {
            if (ObjectUtil.isEmpty(sysMenuEditParam.getName())) {
               sysMenuEditParam.setName(RandomUtil.randomNumbers(10));
            }

            sysMenuEditParam.setComponent(null);
         }
      }
   }

   private void checkRepeatTitle(String parentId, String title, String excludeId) {
      LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = (LambdaQueryWrapper<SysMenu>)((LambdaQueryWrapper<SysMenu>)((LambdaQueryWrapper<SysMenu>)new LambdaQueryWrapper<SysMenu>()
               .eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue()))
            .eq(SysMenu::getParentId, parentId))
         .eq(SysMenu::getTitle, title);
      if (ObjectUtil.isNotEmpty(excludeId)) {
         lambdaQueryWrapper.ne(SysMenu::getId, excludeId);
      }

      if (this.count(lambdaQueryWrapper) > 0L) {
         throw new CommonException("同级下存在重复的菜单，名称为：{}", new Object[]{title});
      }
   }

   private void checkRepeatName(String parentId, String name, String excludeId) {
      if (!ObjectUtil.isEmpty(name)) {
         LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = (LambdaQueryWrapper<SysMenu>)((LambdaQueryWrapper<SysMenu>)((LambdaQueryWrapper<SysMenu>)new LambdaQueryWrapper<SysMenu>()
                  .eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue()))
               .eq(SysMenu::getParentId, parentId))
            .eq(SysMenu::getName, name);
         if (ObjectUtil.isNotEmpty(excludeId)) {
            lambdaQueryWrapper.ne(SysMenu::getId, excludeId);
         }

         if (this.count(lambdaQueryWrapper) > 0L) {
            throw new CommonException("同级下存在重复的菜单路由别名，别名为：{}", new Object[]{name});
         }
      }
   }

   @Override
   public void delete(List<SysMenuIdParam> sysMenuIdParamList) {
      List<String> sysMenuIdList = CollStreamUtil.toList(sysMenuIdParamList, SysMenuIdParam::getId);
      if (ObjectUtil.isNotEmpty(sysMenuIdList)) {
         List<SysMenu> allMenuList = this.list(
            (Wrapper<SysMenu>)new LambdaQueryWrapper<SysMenu>()
               .in(
                  SysMenu::getCategory,
                  CollectionUtil.newArrayList(new String[]{SysResourceCategoryEnum.MENU.getValue(), SysResourceCategoryEnum.BUTTON.getValue()})
               )
         );
         List<String> toDeleteMenuIdList = CollectionUtil.newArrayList(new String[0]);
         sysMenuIdList.forEach(
            menuId -> toDeleteMenuIdList.addAll(this.getChildListById(allMenuList, menuId, true).stream().map(SysMenu::getId).collect(Collectors.toList()))
         );
         if (ObjectUtil.isNotEmpty(toDeleteMenuIdList)) {
            this.sysRelationService
               .remove(
                  (Wrapper<SysRelation>)((LambdaUpdateWrapper<SysRelation>)new LambdaUpdateWrapper<SysRelation>().in(SysRelation::getTargetId, toDeleteMenuIdList))
                     .in(
                        SysRelation::getCategory,
                        new Object[]{SysRelationCategoryEnum.SYS_ROLE_HAS_RESOURCE.getValue(), SysRelationCategoryEnum.SYS_USER_HAS_RESOURCE.getValue()}
                     )
               );
            this.removeByIds(toDeleteMenuIdList);
            CommonDataChangeEventCenter.doDeleteWithDataId(SysDataTypeEnum.RESOURCE.getValue(), toDeleteMenuIdList);
         }
      }
   }

   @Override
   public SysMenu detail(SysMenuIdParam sysMenuIdParam) {
      return this.queryEntity(sysMenuIdParam.getId());
   }

   @Override
   public SysMenu queryEntity(String id) {
      SysMenu sysMenu = (SysMenu)this.getById(id);
      if (ObjectUtil.isEmpty(sysMenu)) {
         throw new CommonException("菜单不存在，id值为：{}", new Object[]{id});
      } else {
         return sysMenu;
      }
   }

   @Override
   public List<SysModule> moduleSelector(SysMenuSelectorModuleParam sysMenuSelectorModuleParam) {
      return CollectionUtil.newArrayList(new SysModule[0]);
   }

   @Override
   public List<Tree<String>> menuTreeSelector(SysMenuSelectorMenuParam sysMenuSelectorMenuParam) {
      LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue());
      List<SysMenu> resourceList = this.list(lambdaQueryWrapper);
      List<TreeNode<String>> treeNodeList = resourceList.stream()
         .map(sysMenu -> new TreeNode<String>(sysMenu.getId(), sysMenu.getParentId(), sysMenu.getTitle(), sysMenu.getSortCode()).setExtra(JSONUtil.parseObj(sysMenu)))
         .collect(Collectors.toList());
      return TreeUtil.build(treeNodeList, "0");
   }

   @Override
   public List<SysMenu> getChildListById(List<SysMenu> originDataList, String id, boolean includeSelf) {
      List<SysMenu> resultList = CollectionUtil.newArrayList(new SysMenu[0]);
      this.execRecursionFindChild(originDataList, id, resultList);
      if (includeSelf) {
         SysMenu self = this.getById(originDataList, id);
         if (ObjectUtil.isNotEmpty(self)) {
            resultList.add(self);
         }
      }

      return resultList;
   }

   @Override
   public List<SysMenu> getParentListById(List<SysMenu> originDataList, String id, boolean includeSelf) {
      List<SysMenu> resultList = CollectionUtil.newArrayList(new SysMenu[0]);
      this.execRecursionFindParent(originDataList, id, resultList);
      if (includeSelf) {
         SysMenu self = this.getById(originDataList, id);
         if (ObjectUtil.isNotEmpty(self)) {
            resultList.add(self);
         }
      }

      return resultList;
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
