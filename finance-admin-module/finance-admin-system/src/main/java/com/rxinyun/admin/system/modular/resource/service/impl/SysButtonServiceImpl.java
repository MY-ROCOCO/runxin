package com.rxinyun.admin.system.modular.resource.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
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
import com.rxinyun.admin.system.modular.resource.entity.SysButton;
import com.rxinyun.admin.system.modular.resource.entity.SysMenu;
import com.rxinyun.admin.system.modular.resource.enums.SysResourceCategoryEnum;
import com.rxinyun.admin.system.modular.resource.mapper.SysButtonMapper;
import com.rxinyun.admin.system.modular.resource.param.button.SysButtonAddParam;
import com.rxinyun.admin.system.modular.resource.param.button.SysButtonEditParam;
import com.rxinyun.admin.system.modular.resource.param.button.SysButtonIdParam;
import com.rxinyun.admin.system.modular.resource.param.button.SysButtonPageParam;
import com.rxinyun.admin.system.modular.resource.service.SysButtonService;
import com.rxinyun.admin.system.modular.resource.service.SysMenuService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.listener.CommonDataChangeEventCenter;
import com.rxinyun.common.page.CommonPageRequest;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysButtonServiceImpl extends ServiceImpl<SysButtonMapper, SysButton> implements SysButtonService {
   @Resource
   private SysRelationService sysRelationService;
   @Resource
   private SysMenuService sysMenuService;

   @Override
   public Page<SysButton> page(SysButtonPageParam sysButtonPageParam) {
      QueryWrapper<SysButton> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(SysButton::getCategory, SysResourceCategoryEnum.BUTTON.getValue());
      if (ObjectUtil.isNotEmpty(sysButtonPageParam.getParentId())) {
         queryWrapper.lambda().eq(SysButton::getParentId, sysButtonPageParam.getParentId());
      }

      if (ObjectUtil.isNotEmpty(sysButtonPageParam.getSearchKey())) {
         queryWrapper.lambda().like(SysButton::getTitle, sysButtonPageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{sysButtonPageParam.getSortField(), sysButtonPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(sysButtonPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, sysButtonPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(sysButtonPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(SysButton::getSortCode);
      }

      return (Page<SysButton>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(SysButtonAddParam sysButtonAddParam) {
      SysButton sysButton = (SysButton)BeanUtil.toBean(sysButtonAddParam, SysButton.class);
      boolean repeatCode = this.count(
            (Wrapper<SysButton>)((LambdaQueryWrapper<SysButton>)new LambdaQueryWrapper<SysButton>().eq(SysButton::getCategory, SysResourceCategoryEnum.BUTTON.getValue()))
               .eq(SysButton::getCode, sysButton.getCode())
         )
         > 0L;
      if (repeatCode) {
         throw new CommonException("存在重复的按钮，编码为：{}", new Object[]{sysButton.getCode()});
      } else {
         sysButton.setCategory(SysResourceCategoryEnum.BUTTON.getValue());
         this.save(sysButton);
         CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.RESOURCE.getValue(), JSONUtil.createArray().put(sysButton));
      }
   }

   @Override
   public void addForGenButton(String menuId, String className, String functionName) {
      SysMenu sysMenu = this.sysMenuService.queryEntity(menuId);
      String classNameFirstLower = StrUtil.lowerFirst(className);
      CollectionUtil.newArrayList(
            new JSONObject[]{
               JSONUtil.createObj().set("title", "新增" + functionName).set("code", classNameFirstLower + "Add").set("sortCode", 1),
               JSONUtil.createObj().set("title", "编辑" + functionName).set("code", classNameFirstLower + "Edit").set("sortCode", 2),
               JSONUtil.createObj().set("title", "删除" + functionName).set("code", classNameFirstLower + "Delete").set("sortCode", 3),
               JSONUtil.createObj().set("title", "批量删除").set("code", classNameFirstLower + "BatchDelete").set("sortCode", 4)
            }
         )
         .forEach(jsonObject -> {
            SysButtonAddParam sysButtonAddParam = new SysButtonAddParam();
            BeanUtil.copyProperties(jsonObject, sysButtonAddParam, new String[0]);
            sysButtonAddParam.setParentId(sysMenu.getId());
            this.add(sysButtonAddParam);
         });
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(SysButtonEditParam sysButtonEditParam) {
      SysButton sysButton = this.queryEntity(sysButtonEditParam.getId());
      BeanUtil.copyProperties(sysButtonEditParam, sysButton, new String[0]);
      boolean repeatCode = this.count(
            (Wrapper<SysButton>)((LambdaQueryWrapper<SysButton>)((LambdaQueryWrapper<SysButton>)new LambdaQueryWrapper<SysButton>().eq(SysButton::getCategory, SysResourceCategoryEnum.BUTTON.getValue()))
                  .eq(SysButton::getCode, sysButton.getCode()))
               .ne(SysButton::getId, sysButtonEditParam.getId())
         )
         > 0L;
      if (repeatCode) {
         throw new CommonException("存在重复的按钮，编码为：{}", new Object[]{sysButton.getCode()});
      } else {
         this.updateById(sysButton);
         CommonDataChangeEventCenter.doUpdateWithData(SysDataTypeEnum.RESOURCE.getValue(), JSONUtil.createArray().put(sysButton));
      }
   }

   @Override
   public void delete(List<SysButtonIdParam> sysButtonIdParamList) {
      List<String> buttonIdList = CollStreamUtil.toList(sysButtonIdParamList, SysButtonIdParam::getId);
      if (ObjectUtil.isNotEmpty(buttonIdList)) {
         List<String> parentMenuIdList = this.sysMenuService
            .list(
               (Wrapper<SysMenu>)((LambdaQueryWrapper<SysMenu>)new LambdaQueryWrapper<SysMenu>().in(SysMenu::getId, buttonIdList))
                  .eq(SysMenu::getCategory, SysResourceCategoryEnum.BUTTON.getValue())
            )
            .stream()
            .map(SysMenu::getParentId)
            .collect(Collectors.toList());
         if (ObjectUtil.isNotEmpty(parentMenuIdList)) {
            this.sysRelationService
               .list(
                  (Wrapper<SysRelation>)((LambdaQueryWrapper<SysRelation>)((LambdaQueryWrapper<SysRelation>)new LambdaQueryWrapper<SysRelation>().in(SysRelation::getTargetId, parentMenuIdList))
                        .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_ROLE_HAS_RESOURCE.getValue()))
                     .isNotNull(SysRelation::getExtJson)
               )
               .forEach(
                  sysRelation -> {
                     JSONObject extJsonObject = JSONUtil.parseObj(sysRelation.getExtJson());
                     List<String> buttonInfoList = extJsonObject.getBeanList("buttonInfo", String.class);
                     if (ObjectUtil.isNotEmpty(buttonInfoList)) {
                        Set<String> intersectionDistinct = CollectionUtil.intersectionDistinct(buttonIdList, buttonInfoList, new Collection[0]);
                        if (ObjectUtil.isNotEmpty(intersectionDistinct)) {
                           Collection<String> disjunction = CollectionUtil.disjunction(buttonInfoList, intersectionDistinct);
                           extJsonObject.set("buttonInfo", disjunction);
                        }
                     }

                     this.sysRelationService
                        .update(
                           (Wrapper<SysRelation>)((LambdaUpdateWrapper<SysRelation>)new LambdaUpdateWrapper<SysRelation>().eq(SysRelation::getId, sysRelation.getId()))
                              .set(SysRelation::getExtJson, JSONUtil.toJsonStr(extJsonObject))
                        );
                  }
               );
            this.removeByIds(buttonIdList);
            CommonDataChangeEventCenter.doDeleteWithDataId(SysDataTypeEnum.RESOURCE.getValue(), buttonIdList);
         }
      }
   }

   @Override
   public SysButton detail(SysButtonIdParam sysButtonIdParam) {
      return this.queryEntity(sysButtonIdParam.getId());
   }

   @Override
   public SysButton queryEntity(String id) {
      SysButton sysButton = (SysButton)this.getById(id);
      if (ObjectUtil.isEmpty(sysButton)) {
         throw new CommonException("按钮不存在，id值为：{}", new Object[]{id});
      } else {
         return sysButton;
      }
   }
}
