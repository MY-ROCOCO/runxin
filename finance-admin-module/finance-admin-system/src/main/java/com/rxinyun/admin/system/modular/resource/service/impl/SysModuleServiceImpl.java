package com.rxinyun.admin.system.modular.resource.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
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
import com.rxinyun.admin.system.core.enums.SysBuildInEnum;
import com.rxinyun.admin.system.core.enums.SysDataTypeEnum;
import com.rxinyun.admin.system.modular.relation.entity.SysRelation;
import com.rxinyun.admin.system.modular.relation.enums.SysRelationCategoryEnum;
import com.rxinyun.admin.system.modular.relation.service.SysRelationService;
import com.rxinyun.admin.system.modular.resource.entity.SysMenu;
import com.rxinyun.admin.system.modular.resource.entity.SysModule;
import com.rxinyun.admin.system.modular.resource.enums.SysResourceCategoryEnum;
import com.rxinyun.admin.system.modular.resource.mapper.SysModuleMapper;
import com.rxinyun.admin.system.modular.resource.param.module.SysModuleAddParam;
import com.rxinyun.admin.system.modular.resource.param.module.SysModuleEditParam;
import com.rxinyun.admin.system.modular.resource.param.module.SysModuleIdParam;
import com.rxinyun.admin.system.modular.resource.param.module.SysModulePageParam;
import com.rxinyun.admin.system.modular.resource.service.SysMenuService;
import com.rxinyun.admin.system.modular.resource.service.SysModuleService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.listener.CommonDataChangeEventCenter;
import com.rxinyun.common.page.CommonPageRequest;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysModuleServiceImpl extends ServiceImpl<SysModuleMapper, SysModule> implements SysModuleService {
   @Resource
   private SysMenuService sysMenuService;
   @Resource
   private SysRelationService sysRelationService;

   @Override
   public Page<SysModule> page(SysModulePageParam sysModulePageParam) {
      QueryWrapper<SysModule> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(SysModule::getCategory, SysResourceCategoryEnum.MODULE.getValue());
      if (ObjectUtil.isNotEmpty(sysModulePageParam.getSearchKey())) {
         queryWrapper.lambda().like(SysModule::getTitle, sysModulePageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{sysModulePageParam.getSortField(), sysModulePageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(sysModulePageParam.getSortOrder());
         queryWrapper.orderBy(
            true, sysModulePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(sysModulePageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(SysModule::getSortCode);
      }

      return (Page<SysModule>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(SysModuleAddParam sysModuleAddParam) {
      SysModule sysModule = (SysModule)BeanUtil.toBean(sysModuleAddParam, SysModule.class);
      boolean repeatTitle = this.count(
            (Wrapper<SysModule>)((LambdaQueryWrapper<SysModule>)new LambdaQueryWrapper<SysModule>().eq(SysModule::getCategory, SysResourceCategoryEnum.MODULE.getValue()))
               .eq(SysModule::getTitle, sysModule.getTitle())
         )
         > 0L;
      if (repeatTitle) {
         throw new CommonException("存在重复的模块，名称为：{}", new Object[]{sysModule.getTitle()});
      } else {
         sysModule.setCode(RandomUtil.randomString(10));
         sysModule.setCategory(SysResourceCategoryEnum.MODULE.getValue());
         this.save(sysModule);
         CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.RESOURCE.getValue(), JSONUtil.createArray().put(sysModule));
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(SysModuleEditParam sysModuleEditParam) {
      SysModule sysModule = this.queryEntity(sysModuleEditParam.getId());
      BeanUtil.copyProperties(sysModuleEditParam, sysModule, new String[0]);
      boolean repeatTitle = this.count(
            (Wrapper<SysModule>)((LambdaQueryWrapper<SysModule>)((LambdaQueryWrapper<SysModule>)new LambdaQueryWrapper<SysModule>().eq(SysModule::getCategory, SysResourceCategoryEnum.MODULE.getValue()))
                  .eq(SysModule::getTitle, sysModule.getTitle()))
               .ne(SysModule::getId, sysModule.getId())
         )
         > 0L;
      if (repeatTitle) {
         throw new CommonException("存在重复的模块，名称为：{}", new Object[]{sysModule.getTitle()});
      } else {
         this.updateById(sysModule);
         CommonDataChangeEventCenter.doUpdateWithData(SysDataTypeEnum.RESOURCE.getValue(), JSONUtil.createArray().put(sysModule));
      }
   }

   @Override
   public void delete(List<SysModuleIdParam> sysModuleIdParamList) {
      List<String> sysModuleIdList = CollStreamUtil.toList(sysModuleIdParamList, SysModuleIdParam::getId);
      if (ObjectUtil.isNotEmpty(sysModuleIdList)) {
         boolean containsSystemModule = this.listByIds(sysModuleIdList)
            .stream()
            .map(SysModule::getCode)
            .collect(Collectors.toSet())
            .contains(SysBuildInEnum.BUILD_IN_MODULE_CODE.getValue());
         if (containsSystemModule) {
            throw new CommonException("不可删除系统内置模块", new Object[0]);
         }

         List<SysMenu> allMenuList = this.sysMenuService
            .list(
               (Wrapper<SysMenu>)new LambdaQueryWrapper<SysMenu>()
                  .in(
                     SysMenu::getCategory,
                     CollectionUtil.newArrayList(new String[]{SysResourceCategoryEnum.MENU.getValue(), SysResourceCategoryEnum.BUTTON.getValue()})
                  )
            );
         if (ObjectUtil.isNotEmpty(allMenuList)) {
            List<String> toDeleteMenuIdList = CollectionUtil.newArrayList(sysModuleIdList);
            allMenuList.stream()
               .filter(sysMenu -> sysModuleIdList.contains(sysMenu.getModule()))
               .collect(Collectors.toList())
               .forEach(
                  sysMenu -> toDeleteMenuIdList.addAll(
                     this.sysMenuService.getChildListById(allMenuList, sysMenu.getId(), true).stream().map(SysMenu::getId).collect(Collectors.toList())
                  )
               );
            if (ObjectUtil.isNotEmpty(toDeleteMenuIdList)) {
               this.sysRelationService
                  .remove(
                     (Wrapper<SysRelation>)((LambdaUpdateWrapper<SysRelation>)new LambdaUpdateWrapper<SysRelation>().in(SysRelation::getTargetId, toDeleteMenuIdList))
                        .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_ROLE_HAS_RESOURCE.getValue())
                  );
               this.removeByIds(toDeleteMenuIdList);
               CommonDataChangeEventCenter.doDeleteWithDataId(SysDataTypeEnum.RESOURCE.getValue(), toDeleteMenuIdList);
            }
         }
      }
   }

   @Override
   public SysModule detail(SysModuleIdParam sysModuleIdParam) {
      return this.queryEntity(sysModuleIdParam.getId());
   }

   @Override
   public SysModule queryEntity(String id) {
      SysModule sysModule = (SysModule)this.getById(id);
      if (ObjectUtil.isEmpty(sysModule)) {
         throw new CommonException("模块不存在，id值为：{}", new Object[]{id});
      } else {
         return sysModule;
      }
   }
}
