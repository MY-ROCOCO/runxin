package com.rxinyun.admin.system.modular.position.service.impl;

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
import com.rxinyun.admin.system.modular.org.service.SysOrgService;
import com.rxinyun.admin.system.modular.position.entity.SysPosition;
import com.rxinyun.admin.system.modular.position.enums.SysPositionCategoryEnum;
import com.rxinyun.admin.system.modular.position.mapper.SysPositionMapper;
import com.rxinyun.admin.system.modular.position.param.SysPositionAddParam;
import com.rxinyun.admin.system.modular.position.param.SysPositionEditParam;
import com.rxinyun.admin.system.modular.position.param.SysPositionIdParam;
import com.rxinyun.admin.system.modular.position.param.SysPositionPageParam;
import com.rxinyun.admin.system.modular.position.param.SysPositionSelectorPositionParam;
import com.rxinyun.admin.system.modular.position.service.SysPositionService;
import com.rxinyun.admin.system.modular.user.entity.SysUser;
import com.rxinyun.admin.system.modular.user.service.SysUserService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.listener.CommonDataChangeEventCenter;
import com.rxinyun.common.page.CommonPageRequest;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysPositionServiceImpl extends ServiceImpl<SysPositionMapper, SysPosition> implements SysPositionService {
   @Resource
   private SysOrgService sysOrgService;
   @Resource
   private SysUserService sysUserService;

   @Override
   public Page<SysPosition> page(SysPositionPageParam sysPositionPageParam) {
      QueryWrapper<SysPosition> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda()
         .select(SysPosition::getId, SysPosition::getOrgId, SysPosition::getName, SysPosition::getCategory, SysPosition::getSortCode);
      if (ObjectUtil.isNotEmpty(sysPositionPageParam.getOrgId())) {
         queryWrapper.lambda().eq(SysPosition::getOrgId, sysPositionPageParam.getOrgId());
      }

      if (ObjectUtil.isNotEmpty(sysPositionPageParam.getCategory())) {
         queryWrapper.lambda().eq(SysPosition::getCategory, sysPositionPageParam.getCategory());
      }

      if (ObjectUtil.isNotEmpty(sysPositionPageParam.getSearchKey())) {
         queryWrapper.lambda().like(SysPosition::getName, sysPositionPageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{sysPositionPageParam.getSortField(), sysPositionPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(sysPositionPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, sysPositionPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(sysPositionPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(SysPosition::getSortCode);
      }

      return (Page<SysPosition>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(SysPositionAddParam sysPositionAddParam) {
      SysPositionCategoryEnum.validate(sysPositionAddParam.getCategory());
      SysPosition sysPosition = (SysPosition)BeanUtil.toBean(sysPositionAddParam, SysPosition.class);
      boolean repeatName = this.count(
            (Wrapper<SysPosition>)((LambdaQueryWrapper<SysPosition>)new LambdaQueryWrapper<SysPosition>().eq(SysPosition::getOrgId, sysPosition.getOrgId()))
               .eq(SysPosition::getName, sysPosition.getName())
         )
         > 0L;
      if (repeatName) {
         throw new CommonException("同组织下存在重复的职位，名称为：{}", new Object[]{sysPosition.getName()});
      } else {
         sysPosition.setCode(RandomUtil.randomString(10));
         this.save(sysPosition);
         CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.POSITION.getValue(), JSONUtil.createArray().put(sysPosition));
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(SysPositionEditParam sysPositionEditParam) {
      SysPositionCategoryEnum.validate(sysPositionEditParam.getCategory());
      SysPosition sysPosition = this.queryEntity(sysPositionEditParam.getId());
      BeanUtil.copyProperties(sysPositionEditParam, sysPosition, new String[0]);
      boolean repeatName = this.count(
            (Wrapper<SysPosition>)((LambdaQueryWrapper<SysPosition>)((LambdaQueryWrapper<SysPosition>)new LambdaQueryWrapper<SysPosition>().eq(SysPosition::getOrgId, sysPosition.getOrgId()))
                  .eq(SysPosition::getName, sysPosition.getName()))
               .ne(SysPosition::getId, sysPosition.getId())
         )
         > 0L;
      if (repeatName) {
         throw new CommonException("同组织下存在重复的职位，名称为：{}", new Object[]{sysPosition.getName()});
      } else {
         this.updateById(sysPosition);
         CommonDataChangeEventCenter.doUpdateWithData(SysDataTypeEnum.POSITION.getValue(), JSONUtil.createArray().put(sysPosition));
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<SysPositionIdParam> sysPositionIdParamList) {
      List<String> positionIdList = CollStreamUtil.toList(sysPositionIdParamList, SysPositionIdParam::getId);
      if (ObjectUtil.isNotEmpty(positionIdList)) {
         boolean hasOrgUser = this.sysUserService.count((Wrapper<SysUser>)new LambdaQueryWrapper<SysUser>().in(SysUser::getPositionId, positionIdList)) > 0L;
         if (hasOrgUser) {
            throw new CommonException("请先删除职位下的用户", new Object[0]);
         }

         List<String> positionJsonList = this.sysUserService
            .list((Wrapper<SysUser>)new LambdaQueryWrapper<SysUser>().isNotNull(SysUser::getPositionJson))
            .stream()
            .map(SysUser::getPositionJson)
            .collect(Collectors.toList());
         if (ObjectUtil.isNotEmpty(positionJsonList)) {
            List<String> extPositionIdList = CollectionUtil.newArrayList(new String[0]);
            positionJsonList.forEach(
               positionJson -> JSONUtil.toList(JSONUtil.parseArray(positionJson), JSONObject.class)
                  .forEach(jsonObject -> extPositionIdList.add(jsonObject.getStr("positionId")))
            );
            boolean hasPositionUser = CollectionUtil.intersectionDistinct(positionIdList, CollectionUtil.removeNull(extPositionIdList), new Collection[0])
                  .size()
               > 0;
            if (hasPositionUser) {
               throw new CommonException("请先删除职位下的用户", new Object[0]);
            }
         }

         this.removeByIds(positionIdList);
         CommonDataChangeEventCenter.doDeleteWithDataId(SysDataTypeEnum.POSITION.getValue(), positionIdList);
      }
   }

   @Override
   public SysPosition detail(SysPositionIdParam sysPositionIdParam) {
      return this.queryEntity(sysPositionIdParam.getId());
   }

   @Override
   public SysPosition queryEntity(String id) {
      SysPosition sysPosition = (SysPosition)this.getById(id);
      if (ObjectUtil.isEmpty(sysPosition)) {
         throw new CommonException("职位不存在，id值为：{}", new Object[]{id});
      } else {
         return sysPosition;
      }
   }

   @Override
   public SysPosition getById(List<SysPosition> originDataList, String id) {
      int index = CollStreamUtil.toList(originDataList, SysPosition::getId).indexOf(id);
      return index == -1 ? null : originDataList.get(index);
   }

   @Override
   public String getPositionIdByPositionNameWithCreate(String orgId, String positionName) {
      SysPosition sysPosition = (SysPosition)this.getOne(
         (Wrapper<SysPosition>)((LambdaQueryWrapper<SysPosition>)new LambdaQueryWrapper<SysPosition>().eq(SysPosition::getOrgId, orgId)).eq(SysPosition::getName, positionName)
      );
      if (ObjectUtil.isNotEmpty(sysPosition)) {
         return sysPosition.getId();
      } else {
         sysPosition = new SysPosition();
         sysPosition.setOrgId(orgId);
         sysPosition.setName(positionName);
         sysPosition.setCode(RandomUtil.randomString(10));
         sysPosition.setCategory(SysPositionCategoryEnum.LOW.getValue());
         sysPosition.setSortCode(99);
         this.save(sysPosition);
         CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.POSITION.getValue(), JSONUtil.createArray().put(sysPosition));
         return sysPosition.getId();
      }
   }

   @Override
   public List<Tree<String>> orgTreeSelector() {
      List<SysOrg> sysOrgList = this.sysOrgService.getAllOrgList();
      List<TreeNode<String>> treeNodeList = sysOrgList.stream()
         .map(sysOrg -> new TreeNode<String>(sysOrg.getId(), sysOrg.getParentId(), sysOrg.getName(), sysOrg.getSortCode()))
         .collect(Collectors.toList());
      return TreeUtil.build(treeNodeList, "0");
   }

   @Override
   public Page<SysPosition> positionSelector(SysPositionSelectorPositionParam sysPositionSelectorPositionParam) {
      LambdaQueryWrapper<SysPosition> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.select(
         SysPosition::getId, SysPosition::getOrgId, SysPosition::getName, SysPosition::getCategory, SysPosition::getSortCode
      );
      if (ObjectUtil.isNotEmpty(sysPositionSelectorPositionParam.getOrgId())) {
         lambdaQueryWrapper.eq(SysPosition::getOrgId, sysPositionSelectorPositionParam.getOrgId());
      }

      if (ObjectUtil.isNotEmpty(sysPositionSelectorPositionParam.getSearchKey())) {
         lambdaQueryWrapper.like(SysPosition::getName, sysPositionSelectorPositionParam.getSearchKey());
      }

      lambdaQueryWrapper.orderByAsc(SysPosition::getSortCode);
      return (Page<SysPosition>)this.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
   }
}
