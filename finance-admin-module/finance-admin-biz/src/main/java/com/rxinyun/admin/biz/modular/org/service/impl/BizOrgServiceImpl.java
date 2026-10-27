package com.rxinyun.admin.biz.modular.org.service.impl;

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
import com.rxinyun.admin.biz.modular.org.enums.BizOrgCategoryEnum;
import com.rxinyun.admin.biz.modular.org.mapper.BizOrgMapper;
import com.rxinyun.admin.biz.modular.org.param.BizOrgAddParam;
import com.rxinyun.admin.biz.modular.org.param.BizOrgEditParam;
import com.rxinyun.admin.biz.modular.org.param.BizOrgIdParam;
import com.rxinyun.admin.biz.modular.org.param.BizOrgPageParam;
import com.rxinyun.admin.biz.modular.org.param.BizOrgSelectorOrgListParam;
import com.rxinyun.admin.biz.modular.org.param.BizOrgSelectorUserParam;
import com.rxinyun.admin.biz.modular.org.service.BizOrgService;
import com.rxinyun.admin.biz.modular.position.entity.BizPosition;
import com.rxinyun.admin.biz.modular.position.service.BizPositionService;
import com.rxinyun.admin.biz.modular.user.entity.BizUser;
import com.rxinyun.admin.biz.modular.user.service.BizUserService;
import com.rxinyun.auth.core.util.StpLoginUserUtil;
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.listener.CommonDataChangeEventCenter;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.sys.api.SysRoleApi;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BizOrgServiceImpl extends ServiceImpl<BizOrgMapper, BizOrg> implements BizOrgService {
   public static final String ORG_CACHE_ALL_KEY = "sys-org:all";
   @Resource
   private CommonCacheOperator commonCacheOperator;
   @Resource
   private SysRoleApi sysRoleApi;
   @Resource
   private BizPositionService bizPositionService;
   @Resource
   private BizUserService bizUserService;

   @Override
   public Page<BizOrg> page(BizOrgPageParam bizOrgPageParam) {
      QueryWrapper<BizOrg> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().select(BizOrg::getId, BizOrg::getParentId, BizOrg::getName, BizOrg::getCategory, BizOrg::getSortCode);
      if (ObjectUtil.isNotEmpty(bizOrgPageParam.getParentId())) {
         queryWrapper.lambda().eq(BizOrg::getParentId, bizOrgPageParam.getParentId());
      }

      if (ObjectUtil.isNotEmpty(bizOrgPageParam.getSearchKey())) {
         queryWrapper.lambda().like(BizOrg::getName, bizOrgPageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{bizOrgPageParam.getSortField(), bizOrgPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(bizOrgPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, bizOrgPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(bizOrgPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(BizOrg::getSortCode);
      }

      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         queryWrapper.lambda().in(BizOrg::getId, loginUserDataScope);
         return (Page<BizOrg>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      } else {
         return new Page();
      }
   }

   @Override
   public List<Tree<String>> tree() {
      List<BizOrg> allOrgList = this.list();
      Set<BizOrg> bizOrgSet = CollectionUtil.newHashSet(new BizOrg[0]);
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         loginUserDataScope.forEach(orgId -> bizOrgSet.addAll(this.getParentListById(allOrgList, orgId, true)));
         List<TreeNode<String>> treeNodeList = bizOrgSet.stream()
            .map(bizOrg -> new TreeNode<String>(bizOrg.getId(), bizOrg.getParentId(), bizOrg.getName(), bizOrg.getSortCode()).setExtra(JSONUtil.parseObj(bizOrg)))
            .collect(Collectors.toList());
         return TreeUtil.build(treeNodeList, "0");
      } else {
         return CollectionUtil.newArrayList(new Tree[0]);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(BizOrgAddParam bizOrgAddParam) {
      BizOrgCategoryEnum.validate(bizOrgAddParam.getCategory());
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         if (!loginUserDataScope.contains(bizOrgAddParam.getParentId())) {
            throw new CommonException("您没有权限在该机构下增加机构，机构id：{}", new Object[]{bizOrgAddParam.getParentId()});
         } else {
            BizOrg bizOrg = (BizOrg)BeanUtil.toBean(bizOrgAddParam, BizOrg.class);
            boolean repeatName = this.count(
                  (Wrapper<BizOrg>)((LambdaQueryWrapper<BizOrg>)new LambdaQueryWrapper<BizOrg>().eq(BizOrg::getParentId, bizOrg.getParentId())).eq(BizOrg::getName, bizOrg.getName())
               )
               > 0L;
            if (repeatName) {
               throw new CommonException("存在重复的同级机构，名称为：{}", new Object[]{bizOrg.getName()});
            } else {
               bizOrg.setCode(RandomUtil.randomString(10));
               this.save(bizOrg);
               CommonDataChangeEventCenter.doAddWithData(BizDataTypeEnum.ORG.getValue(), JSONUtil.createArray().put(bizOrg));
            }
         }
      } else {
         throw new CommonException("您没有权限增加机构", new Object[0]);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(BizOrgEditParam bizOrgEditParam) {
      BizOrgCategoryEnum.validate(bizOrgEditParam.getCategory());
      BizOrg bizOrg = this.queryEntity(bizOrgEditParam.getId());
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         if (!loginUserDataScope.contains(bizOrg.getId())) {
            throw new CommonException("您没有权限编辑该机构，机构id：{}", new Object[]{bizOrg.getId()});
         } else if (!loginUserDataScope.contains(bizOrg.getParentId())) {
            throw new CommonException("您没有权限编辑该机构下的机构，机构id：{}", new Object[]{bizOrg.getParentId()});
         } else {
            BeanUtil.copyProperties(bizOrgEditParam, bizOrg, new String[0]);
            boolean repeatName = this.count(
                  (Wrapper<BizOrg>)((LambdaQueryWrapper<BizOrg>)((LambdaQueryWrapper<BizOrg>)new LambdaQueryWrapper<BizOrg>().eq(BizOrg::getParentId, bizOrg.getParentId()))
                        .eq(BizOrg::getName, bizOrg.getName()))
                     .ne(BizOrg::getId, bizOrg.getId())
               )
               > 0L;
            if (repeatName) {
               throw new CommonException("存在重复的同级机构，名称为：{}", new Object[]{bizOrg.getName()});
            } else {
               List<BizOrg> originDataList = this.list();
               boolean errorLevel = this.getChildListById(originDataList, bizOrg.getId(), true)
                  .stream()
                  .map(BizOrg::getId)
                  .collect(Collectors.toList())
                  .contains(bizOrg.getParentId());
               if (errorLevel) {
                  throw new CommonException("不可选择上级机构：{}", new Object[]{this.getById(originDataList, bizOrg.getParentId()).getName()});
               } else {
                  this.updateById(bizOrg);
                  CommonDataChangeEventCenter.doUpdateWithData(BizDataTypeEnum.ORG.getValue(), JSONUtil.createArray().put(bizOrg));
               }
            }
         }
      } else {
         throw new CommonException("您没有权限编辑该机构，机构id：{}", new Object[]{bizOrg.getId()});
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<BizOrgIdParam> bizOrgIdParamList) {
      List<String> orgIdList = CollStreamUtil.toList(bizOrgIdParamList, BizOrgIdParam::getId);
      if (ObjectUtil.isNotEmpty(orgIdList)) {
         List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
         if (!ObjectUtil.isNotEmpty(loginUserDataScope)) {
            throw new CommonException("您没有权限删除这些机构，机构id：{}", new Object[]{orgIdList});
         }

         if (!loginUserDataScope.containsAll(orgIdList)) {
            throw new CommonException("您没有权限删除这些机构，机构id：{}", new Object[]{orgIdList});
         }

         List<BizOrg> allOrgList = this.list();
         List<String> toDeleteOrgIdList = CollectionUtil.newArrayList(new String[0]);
         orgIdList.forEach(
            orgId -> toDeleteOrgIdList.addAll(this.getChildListById(allOrgList, orgId, true).stream().map(BizOrg::getId).collect(Collectors.toList()))
         );
         boolean hasOrgUser = this.bizUserService.count((Wrapper<BizUser>)new LambdaQueryWrapper<BizUser>().in(BizUser::getOrgId, toDeleteOrgIdList)) > 0L;
         if (hasOrgUser) {
            throw new CommonException("请先删除机构下的人员", new Object[0]);
         }

         List<String> positionJsonList = this.bizUserService
            .list((Wrapper<BizUser>)new LambdaQueryWrapper<BizUser>().isNotNull(BizUser::getPositionJson))
            .stream()
            .map(BizUser::getPositionJson)
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
               throw new CommonException("请先删除机构下的人员", new Object[0]);
            }
         }

         boolean hasRole = this.sysRoleApi.orgHasRole(toDeleteOrgIdList);
         if (hasRole) {
            throw new CommonException("请先删除机构下的角色", new Object[0]);
         }

         boolean hasPosition = this.bizPositionService.count((Wrapper<BizPosition>)new LambdaQueryWrapper<BizPosition>().in(BizPosition::getOrgId, toDeleteOrgIdList)) > 0L;
         if (hasPosition) {
            throw new CommonException("请先删除机构下的岗位", new Object[0]);
         }

         this.removeByIds(toDeleteOrgIdList);
         CommonDataChangeEventCenter.doDeleteWithDataId(BizDataTypeEnum.ORG.getValue(), toDeleteOrgIdList);
      }
   }

   @Override
   public BizOrg detail(BizOrgIdParam bizOrgIdParam) {
      return this.queryEntity(bizOrgIdParam.getId());
   }

   @Override
   public BizOrg queryEntity(String id) {
      BizOrg bizOrg = (BizOrg)this.getById(id);
      if (ObjectUtil.isEmpty(bizOrg)) {
         throw new CommonException("机构不存在，id值为：{}", new Object[]{id});
      } else {
         return bizOrg;
      }
   }

   @Override
   public List<BizOrg> getAllOrgList() {
      return this.list((Wrapper<BizOrg>)new LambdaQueryWrapper<BizOrg>().orderByAsc(BizOrg::getSortCode));
   }

   @Override
   public String getOrgIdByOrgFullNameWithCreate(String orgFullName) {
      List<BizOrg> cachedAllOrgList = this.getAllOrgList();
      List<Tree<String>> treeList = TreeUtil.build(
         cachedAllOrgList.stream()
            .map(bizOrg -> new TreeNode<String>(bizOrg.getId(), bizOrg.getParentId(), bizOrg.getName(), bizOrg.getSortCode()))
            .collect(Collectors.toList()),
         "0"
      );
      return this.findOrgIdByOrgName("0", StrUtil.split(orgFullName, "-").iterator(), cachedAllOrgList, treeList);
   }

   public String findOrgIdByOrgName(String parentId, Iterator<String> iterator, List<BizOrg> cachedAllOrgList, List<Tree<String>> treeList) {
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

   public String doCreateOrg(String parentId, String orgName, List<BizOrg> cachedAllOrgList) {
      BizOrg bizOrg = new BizOrg();
      bizOrg.setName(orgName);
      bizOrg.setCode(RandomUtil.randomString(10));
      bizOrg.setParentId(parentId);
      bizOrg.setCategory("0".equals(parentId) ? BizOrgCategoryEnum.COMPANY.getValue() : BizOrgCategoryEnum.DEPT.getValue());
      bizOrg.setSortCode(99);
      this.save(bizOrg);
      CommonDataChangeEventCenter.doAddWithData(BizDataTypeEnum.ORG.getValue(), JSONUtil.createArray().put(bizOrg));
      cachedAllOrgList.add(bizOrg);
      this.commonCacheOperator.put("sys-org:all", cachedAllOrgList);
      return bizOrg.getId();
   }

   @Override
   public List<Tree<String>> orgTreeSelector() {
      LambdaQueryWrapper<BizOrg> lambdaQueryWrapper = new LambdaQueryWrapper();
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      Set<BizOrg> bizOrgSet = CollectionUtil.newHashSet(new BizOrg[0]);
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         List<BizOrg> allOrgList = this.list();
         loginUserDataScope.forEach(orgId -> bizOrgSet.addAll(this.getParentListById(allOrgList, orgId, true)));
         List<String> loginUserDataScopeFullList = bizOrgSet.stream().map(BizOrg::getId).collect(Collectors.toList());
         lambdaQueryWrapper.in(BizOrg::getId, loginUserDataScopeFullList);
         lambdaQueryWrapper.orderByAsc(BizOrg::getSortCode);
         List<BizOrg> filteredOrgList = this.list(lambdaQueryWrapper);
         List<TreeNode<String>> treeNodeList = filteredOrgList.stream()
            .map(bizOrg -> new TreeNode<String>(bizOrg.getId(), bizOrg.getParentId(), bizOrg.getName(), bizOrg.getSortCode()))
            .collect(Collectors.toList());
         return TreeUtil.build(treeNodeList, "0");
      } else {
         return CollectionUtil.newArrayList(new Tree[0]);
      }
   }

   @Override
   public List<BizOrg> orgListSelector(BizOrgSelectorOrgListParam bizOrgSelectorOrgListParam) {
      LambdaQueryWrapper<BizOrg> lambdaQueryWrapper = new LambdaQueryWrapper();
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         lambdaQueryWrapper.in(BizOrg::getId, loginUserDataScope);
         lambdaQueryWrapper.select(BizOrg::getId, BizOrg::getParentId, BizOrg::getName, BizOrg::getCategory, BizOrg::getSortCode);
         if (ObjectUtil.isNotEmpty(bizOrgSelectorOrgListParam.getParentId())) {
            lambdaQueryWrapper.eq(BizOrg::getParentId, bizOrgSelectorOrgListParam.getParentId());
         }

         if (ObjectUtil.isNotEmpty(bizOrgSelectorOrgListParam.getSearchKey())) {
            lambdaQueryWrapper.like(BizOrg::getName, bizOrgSelectorOrgListParam.getSearchKey());
         }

         lambdaQueryWrapper.orderByAsc(BizOrg::getSortCode);
         return this.list(lambdaQueryWrapper);
      } else {
         return CollectionUtil.newArrayList(new BizOrg[0]);
      }
   }

   @Override
   public Page<BizUser> userSelector(BizOrgSelectorUserParam bizOrgSelectorUserParam) {
      LambdaQueryWrapper<BizUser> lambdaQueryWrapper = new LambdaQueryWrapper();
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         lambdaQueryWrapper.in(BizUser::getOrgId, loginUserDataScope);
         lambdaQueryWrapper.select(
            
               BizUser::getId,
               BizUser::getAvatar,
               BizUser::getOrgId,
               BizUser::getPositionId,
               BizUser::getAccount,
               BizUser::getName,
               BizUser::getSortCode,
               BizUser::getGender,
               BizUser::getEntryDate
            
         );
         if (ObjectUtil.isNotEmpty(bizOrgSelectorUserParam.getOrgId())) {
            List<String> childOrgIdList = CollStreamUtil.toList(
               this.getChildListById(this.getAllOrgList(), bizOrgSelectorUserParam.getOrgId(), true), BizOrg::getId
            );
            if (!ObjectUtil.isNotEmpty(childOrgIdList)) {
               return new Page();
            }

            lambdaQueryWrapper.in(BizUser::getOrgId, childOrgIdList);
         }

         if (ObjectUtil.isNotEmpty(bizOrgSelectorUserParam.getSearchKey())) {
            lambdaQueryWrapper.like(BizUser::getName, bizOrgSelectorUserParam.getSearchKey());
         }

         lambdaQueryWrapper.orderByAsc(BizUser::getSortCode);
         return (Page<BizUser>)this.bizUserService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
      } else {
         return new Page();
      }
   }

   @Override
   public List<BizOrg> getParentAndChildListById(List<BizOrg> originDataList, String id, boolean includeSelf) {
      List<BizOrg> parentListById = this.getParentListById(originDataList, id, false);
      List<BizOrg> childListById = this.getChildListById(originDataList, id, true);
      parentListById.addAll(childListById);
      return parentListById;
   }

   @Override
   public List<BizOrg> getChildListById(List<BizOrg> originDataList, String id, boolean includeSelf) {
      List<BizOrg> resultList = CollectionUtil.newArrayList(new BizOrg[0]);
      this.execRecursionFindChild(originDataList, id, resultList);
      if (includeSelf) {
         BizOrg self = this.getById(originDataList, id);
         if (ObjectUtil.isNotEmpty(self)) {
            resultList.add(self);
         }
      }

      return resultList;
   }

   @Override
   public List<BizOrg> getParentListById(List<BizOrg> originDataList, String id, boolean includeSelf) {
      List<BizOrg> resultList = CollectionUtil.newArrayList(new BizOrg[0]);
      this.execRecursionFindParent(originDataList, id, resultList);
      if (includeSelf) {
         BizOrg self = this.getById(originDataList, id);
         if (ObjectUtil.isNotEmpty(self)) {
            resultList.add(self);
         }
      }

      return resultList;
   }

   public void execRecursionFindChild(List<BizOrg> originDataList, String id, List<BizOrg> resultList) {
      originDataList.forEach(item -> {
         if (item.getParentId().equals(id)) {
            resultList.add(item);
            this.execRecursionFindChild(originDataList, item.getId(), resultList);
         }
      });
   }

   public void execRecursionFindParent(List<BizOrg> originDataList, String id, List<BizOrg> resultList) {
      originDataList.forEach(item -> {
         if (item.getId().equals(id)) {
            BizOrg parent = this.getById(originDataList, item.getParentId());
            if (ObjectUtil.isNotEmpty(parent)) {
               resultList.add(parent);
            }

            this.execRecursionFindParent(originDataList, item.getParentId(), resultList);
         }
      });
   }

   @Override
   public BizOrg getById(List<BizOrg> originDataList, String id) {
      int index = CollStreamUtil.toList(originDataList, BizOrg::getId).indexOf(id);
      return index == -1 ? null : originDataList.get(index);
   }

   @Override
   public BizOrg getParentById(List<BizOrg> originDataList, String id) {
      BizOrg self = this.getById(originDataList, id);
      return ObjectUtil.isNotEmpty(self) ? self : this.getById(originDataList, self.getParentId());
   }

   @Override
   public BizOrg getChildById(List<BizOrg> originDataList, String id) {
      int index = CollStreamUtil.toList(originDataList, BizOrg::getParentId).indexOf(id);
      return index == -1 ? null : originDataList.get(index);
   }
}
