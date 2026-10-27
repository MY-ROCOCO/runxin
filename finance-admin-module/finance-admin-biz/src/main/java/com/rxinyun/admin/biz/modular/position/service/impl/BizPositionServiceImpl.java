package com.rxinyun.admin.biz.modular.position.service.impl;

import cn.dev33.satoken.stp.StpUtil;
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
import com.rxinyun.admin.biz.core.enums.BizDataTypeEnum;
import com.rxinyun.admin.biz.modular.org.entity.BizOrg;
import com.rxinyun.admin.biz.modular.org.service.BizOrgService;
import com.rxinyun.admin.biz.modular.position.entity.BizPosition;
import com.rxinyun.admin.biz.modular.position.enums.BizPositionCategoryEnum;
import com.rxinyun.admin.biz.modular.position.mapper.BizPositionMapper;
import com.rxinyun.admin.biz.modular.position.param.BizPositionAddParam;
import com.rxinyun.admin.biz.modular.position.param.BizPositionEditParam;
import com.rxinyun.admin.biz.modular.position.param.BizPositionIdParam;
import com.rxinyun.admin.biz.modular.position.param.BizPositionPageParam;
import com.rxinyun.admin.biz.modular.position.param.BizPositionSelectorPositionParam;
import com.rxinyun.admin.biz.modular.position.service.BizPositionService;
import com.rxinyun.admin.biz.modular.user.entity.BizUser;
import com.rxinyun.admin.biz.modular.user.service.BizUserService;
import com.rxinyun.auth.core.util.StpLoginUserUtil;
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
public class BizPositionServiceImpl extends ServiceImpl<BizPositionMapper, BizPosition> implements BizPositionService {
   @Resource
   private BizOrgService bizOrgService;
   @Resource
   private BizUserService bizUserService;

   @Override
   public Page<BizPosition> page(BizPositionPageParam bizPositionPageParam) {
      QueryWrapper<BizPosition> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda()
         .select(BizPosition::getId, BizPosition::getOrgId, BizPosition::getName, BizPosition::getCategory, BizPosition::getSortCode);
      if (ObjectUtil.isNotEmpty(bizPositionPageParam.getOrgId())) {
         queryWrapper.lambda().eq(BizPosition::getOrgId, bizPositionPageParam.getOrgId());
      }

      if (ObjectUtil.isNotEmpty(bizPositionPageParam.getCategory())) {
         queryWrapper.lambda().eq(BizPosition::getCategory, bizPositionPageParam.getCategory());
      }

      if (ObjectUtil.isNotEmpty(bizPositionPageParam.getSearchKey())) {
         queryWrapper.lambda().like(BizPosition::getName, bizPositionPageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{bizPositionPageParam.getSortField(), bizPositionPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(bizPositionPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, bizPositionPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(bizPositionPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(BizPosition::getSortCode);
      }

      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         queryWrapper.lambda().in(BizPosition::getOrgId, loginUserDataScope);
         return (Page<BizPosition>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      } else {
         return new Page();
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(BizPositionAddParam bizPositionAddParam) {
      BizPositionCategoryEnum.validate(bizPositionAddParam.getCategory());
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         if (!loginUserDataScope.contains(bizPositionAddParam.getOrgId())) {
            throw new CommonException("您没有权限在该机构下增加岗位，机构id：{}", new Object[]{bizPositionAddParam.getOrgId()});
         } else {
            BizPosition bizPosition = (BizPosition)BeanUtil.toBean(bizPositionAddParam, BizPosition.class);
            boolean repeatName = this.count(
                  (Wrapper<BizPosition>)((LambdaQueryWrapper<BizPosition>)new LambdaQueryWrapper<BizPosition>().eq(BizPosition::getOrgId, bizPosition.getOrgId()))
                     .eq(BizPosition::getName, bizPosition.getName())
               )
               > 0L;
            if (repeatName) {
               throw new CommonException("同机构下存在重复的岗位，名称为：{}", new Object[]{bizPosition.getName()});
            } else {
               bizPosition.setCode(RandomUtil.randomString(10));
               this.save(bizPosition);
               CommonDataChangeEventCenter.doAddWithData(BizDataTypeEnum.POSITION.getValue(), JSONUtil.createArray().put(bizPosition));
            }
         }
      } else {
         throw new CommonException("您没有权限在该机构下增加岗位，机构id：{}", new Object[]{bizPositionAddParam.getOrgId()});
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(BizPositionEditParam bizPositionEditParam) {
      BizPositionCategoryEnum.validate(bizPositionEditParam.getCategory());
      BizPosition bizPosition = this.queryEntity(bizPositionEditParam.getId());
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         if (!loginUserDataScope.contains(bizPositionEditParam.getOrgId())) {
            throw new CommonException("您没有权限编辑该机构下的岗位，机构id：{}", new Object[]{bizPositionEditParam.getOrgId()});
         }
      } else if (!bizPositionEditParam.getId().equals(StpUtil.getLoginIdAsString())) {
         throw new CommonException("您没有权限编辑该机构下的岗位，机构id：{}", new Object[]{bizPositionEditParam.getOrgId()});
      }

      BeanUtil.copyProperties(bizPositionEditParam, bizPosition, new String[0]);
      boolean repeatName = this.count(
            (Wrapper<BizPosition>)((LambdaQueryWrapper<BizPosition>)((LambdaQueryWrapper<BizPosition>)new LambdaQueryWrapper<BizPosition>().eq(BizPosition::getOrgId, bizPosition.getOrgId()))
                  .eq(BizPosition::getName, bizPosition.getName()))
               .ne(BizPosition::getId, bizPosition.getId())
         )
         > 0L;
      if (repeatName) {
         throw new CommonException("同机构下存在重复的岗位，名称为：{}", new Object[]{bizPosition.getName()});
      } else {
         this.updateById(bizPosition);
         CommonDataChangeEventCenter.doUpdateWithData(BizDataTypeEnum.POSITION.getValue(), JSONUtil.createArray().put(bizPosition));
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<BizPositionIdParam> bizPositionIdParamList) {
      List<String> positionIdList = CollStreamUtil.toList(bizPositionIdParamList, BizPositionIdParam::getId);
      if (ObjectUtil.isNotEmpty(positionIdList)) {
         Set<String> positionOrgIdList = this.listByIds(positionIdList).stream().map(BizPosition::getOrgId).collect(Collectors.toSet());
         List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
         if (!ObjectUtil.isNotEmpty(loginUserDataScope)) {
            throw new CommonException("您没有权限删除这些机构下的岗位，机构id：{}", new Object[]{positionOrgIdList});
         }

         if (!loginUserDataScope.containsAll(positionOrgIdList)) {
            throw new CommonException("您没有权限删除这些机构下的岗位，机构id：{}", new Object[]{positionOrgIdList});
         }

         boolean hasOrgUser = this.bizUserService.count((Wrapper<BizUser>)new LambdaQueryWrapper<BizUser>().in(BizUser::getPositionId, positionIdList)) > 0L;
         if (hasOrgUser) {
            throw new CommonException("请先删除岗位下的用户", new Object[0]);
         }

         List<String> positionJsonList = this.bizUserService
            .list((Wrapper<BizUser>)new LambdaQueryWrapper<BizUser>().isNotNull(BizUser::getPositionJson))
            .stream()
            .map(BizUser::getPositionJson)
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
               throw new CommonException("请先删除岗位下的用户", new Object[0]);
            }
         }

         this.removeByIds(positionIdList);
         CommonDataChangeEventCenter.doDeleteWithDataId(BizDataTypeEnum.POSITION.getValue(), positionIdList);
      }
   }

   @Override
   public BizPosition detail(BizPositionIdParam bizPositionIdParam) {
      return this.queryEntity(bizPositionIdParam.getId());
   }

   @Override
   public BizPosition queryEntity(String id) {
      BizPosition bizPosition = (BizPosition)this.getById(id);
      if (ObjectUtil.isEmpty(bizPosition)) {
         throw new CommonException("岗位不存在，id值为：{}", new Object[]{id});
      } else {
         return bizPosition;
      }
   }

   @Override
   public String getPositionIdByPositionNameWithCreate(String orgId, String positionName) {
      return null;
   }

   @Override
   public List<Tree<String>> orgTreeSelector() {
      LambdaQueryWrapper<BizOrg> lambdaQueryWrapper = new LambdaQueryWrapper();
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      Set<BizOrg> bizOrgSet = CollectionUtil.newHashSet(new BizOrg[0]);
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         List<BizOrg> allOrgList = this.bizOrgService.list();
         loginUserDataScope.forEach(orgId -> bizOrgSet.addAll(this.bizOrgService.getParentListById(allOrgList, orgId, true)));
         List<String> loginUserDataScopeFullList = bizOrgSet.stream().map(BizOrg::getId).collect(Collectors.toList());
         lambdaQueryWrapper.in(BizOrg::getId, loginUserDataScopeFullList);
         lambdaQueryWrapper.orderByAsc(BizOrg::getSortCode);
         List<BizOrg> filteredOrgList = this.bizOrgService.list(lambdaQueryWrapper);
         List<TreeNode<String>> treeNodeList = filteredOrgList.stream()
            .map(bizOrg -> new TreeNode<String>(bizOrg.getId(), bizOrg.getParentId(), bizOrg.getName(), bizOrg.getSortCode()))
            .collect(Collectors.toList());
         return TreeUtil.build(treeNodeList, "0");
      } else {
         return CollectionUtil.newArrayList(new Tree[0]);
      }
   }

   @Override
   public Page<BizPosition> positionSelector(BizPositionSelectorPositionParam bizPositionSelectorPositionParam) {
      LambdaQueryWrapper<BizPosition> lambdaQueryWrapper = new LambdaQueryWrapper();
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         lambdaQueryWrapper.in(BizPosition::getOrgId, loginUserDataScope);
         lambdaQueryWrapper.select(
            BizPosition::getId, BizPosition::getOrgId, BizPosition::getName, BizPosition::getCategory, BizPosition::getSortCode
         );
         if (ObjectUtil.isNotEmpty(bizPositionSelectorPositionParam.getOrgId())) {
            lambdaQueryWrapper.eq(BizPosition::getOrgId, bizPositionSelectorPositionParam.getOrgId());
         }

         if (ObjectUtil.isNotEmpty(bizPositionSelectorPositionParam.getSearchKey())) {
            lambdaQueryWrapper.like(BizPosition::getName, bizPositionSelectorPositionParam.getSearchKey());
         }

         lambdaQueryWrapper.orderByAsc(BizPosition::getSortCode);
         return (Page<BizPosition>)this.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
      } else {
         return new Page();
      }
   }
}
