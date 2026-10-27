package com.rxinyun.admin.dev.modular.dict.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.DefaultNodeParser;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.dev.modular.dict.entity.DevDict;
import com.rxinyun.admin.dev.modular.dict.enums.DevDictCategoryEnum;
import com.rxinyun.admin.dev.modular.dict.mapper.DevDictMapper;
import com.rxinyun.admin.dev.modular.dict.param.DevDictAddParam;
import com.rxinyun.admin.dev.modular.dict.param.DevDictEditParam;
import com.rxinyun.admin.dev.modular.dict.param.DevDictIdParam;
import com.rxinyun.admin.dev.modular.dict.param.DevDictListParam;
import com.rxinyun.admin.dev.modular.dict.param.DevDictPageParam;
import com.rxinyun.admin.dev.modular.dict.param.DevDictTreeParam;
import com.rxinyun.admin.dev.modular.dict.service.DevDictService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class DevDictServiceImpl extends ServiceImpl<DevDictMapper, DevDict> implements DevDictService, InitializingBean {
   private static final String ROOT_PARENT_ID = "0";

   @Override
   public Page<DevDict> page(DevDictPageParam devDictPageParam) {
      QueryWrapper<DevDict> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda()
         .select(
            DevDict::getId, DevDict::getParentId, DevDict::getCategory, DevDict::getDictLabel, DevDict::getDictValue, DevDict::getSortCode
         );
      if (ObjectUtil.isNotEmpty(devDictPageParam.getParentId())) {
         queryWrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<DevDict>)((LambdaQueryWrapper<DevDict>)q.eq(DevDict::getParentId, devDictPageParam.getParentId())).or())
                  .eq(DevDict::getId, devDictPageParam.getParentId())
            );
      }

      if (ObjectUtil.isNotEmpty(devDictPageParam.getCategory())) {
         queryWrapper.lambda().eq(DevDict::getCategory, devDictPageParam.getCategory());
      }

      if (ObjectUtil.isNotEmpty(devDictPageParam.getSearchKey())) {
         queryWrapper.lambda().like(DevDict::getDictLabel, devDictPageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{devDictPageParam.getSortField(), devDictPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(devDictPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, devDictPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(devDictPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(DevDict::getSortCode);
      }

      return (Page<DevDict>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public List<DevDict> list(DevDictListParam devDictListParam) {
      LambdaQueryWrapper<DevDict> lambdaQueryWrapper = new LambdaQueryWrapper();
      if (ObjectUtil.isNotEmpty(devDictListParam.getParentId())) {
         lambdaQueryWrapper.eq(DevDict::getParentId, devDictListParam.getParentId());
      }

      if (ObjectUtil.isNotEmpty(devDictListParam.getCategory())) {
         lambdaQueryWrapper.eq(DevDict::getCategory, devDictListParam.getCategory());
      }

      return this.list(lambdaQueryWrapper);
   }

   @Override
   public List<Tree<String>> tree(DevDictTreeParam devDictTreeParam) {
      LambdaQueryWrapper<DevDict> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.select(
         DevDict::getId, DevDict::getParentId, DevDict::getCategory, DevDict::getDictLabel, DevDict::getDictValue, DevDict::getSortCode
      );
      lambdaQueryWrapper.orderByAsc(DevDict::getSortCode);
      if (ObjectUtil.isNotEmpty(devDictTreeParam.getCategory())) {
         lambdaQueryWrapper.eq(DevDict::getCategory, devDictTreeParam.getCategory());
      }

      List<DevDict> devDictList = this.list(lambdaQueryWrapper);
      List<TreeNode<String>> treeNodeList = devDictList.stream()
         .map(
            devDict -> new TreeNode<String>(devDict.getId(), devDict.getParentId(), devDict.getDictLabel(), devDict.getSortCode()).setExtra(JSONUtil.parseObj(devDict))
         )
         .collect(Collectors.toList());
      TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
      treeNodeConfig.setWeightKey("sortCode");
      return TreeUtil.build(treeNodeList, "0", treeNodeConfig, new DefaultNodeParser());
   }

   @Override
   public void add(DevDictAddParam devDictAddParam) {
      this.checkParam(devDictAddParam);
      DevDict devDict = (DevDict)BeanUtil.toBean(devDictAddParam, DevDict.class);
      this.save(devDict);
      this.refreshTransCache();
   }

   private void checkParam(DevDictAddParam devDictAddParam) {
      DevDictCategoryEnum.validate(devDictAddParam.getCategory());
      boolean hasSameDictLabel = this.count(
            (Wrapper<DevDict>)((LambdaQueryWrapper<DevDict>)((LambdaQueryWrapper<DevDict>)new LambdaQueryWrapper<DevDict>().eq(DevDict::getParentId, devDictAddParam.getParentId()))
                  .eq(DevDict::getCategory, devDictAddParam.getCategory()))
               .eq(DevDict::getDictLabel, devDictAddParam.getDictLabel())
         )
         > 0L;
      if (hasSameDictLabel) {
         throw new CommonException("存在重复的字典文字，名称为：{}", new Object[]{devDictAddParam.getDictLabel()});
      } else {
         boolean hasSameDictValue = this.count(
               (Wrapper<DevDict>)((LambdaQueryWrapper<DevDict>)new LambdaQueryWrapper<DevDict>().eq(DevDict::getParentId, devDictAddParam.getParentId()))
                  .eq(DevDict::getDictValue, devDictAddParam.getDictValue())
            )
            > 0L;
         if (hasSameDictValue) {
            throw new CommonException("存在重复的字典值，名称为：{}", new Object[]{devDictAddParam.getDictValue()});
         }
      }
   }

   @Override
   public void edit(DevDictEditParam devDictEditParam) {
      DevDict devDict = this.queryEntity(devDictEditParam.getId());
      this.checkParam(devDictEditParam);
      BeanUtil.copyProperties(devDictEditParam, devDict, new String[0]);
      this.updateById(devDict);
      this.refreshTransCache();
   }

   private void checkParam(DevDictEditParam devDictEditParam) {
      DevDictCategoryEnum.validate(devDictEditParam.getCategory());
      boolean hasSameDictLabel = this.count(
            (Wrapper<DevDict>)((LambdaQueryWrapper<DevDict>)((LambdaQueryWrapper<DevDict>)((LambdaQueryWrapper<DevDict>)new LambdaQueryWrapper<DevDict>()
                        .eq(DevDict::getParentId, devDictEditParam.getParentId()))
                     .eq(DevDict::getCategory, devDictEditParam.getCategory()))
                  .eq(DevDict::getDictLabel, devDictEditParam.getDictLabel()))
               .ne(DevDict::getId, devDictEditParam.getId())
         )
         > 0L;
      if (hasSameDictLabel) {
         throw new CommonException("存在重复的字典文字，名称为：{}", new Object[]{devDictEditParam.getDictLabel()});
      } else {
         boolean hasSameDictValue = this.count(
               (Wrapper<DevDict>)((LambdaQueryWrapper<DevDict>)((LambdaQueryWrapper<DevDict>)new LambdaQueryWrapper<DevDict>().eq(DevDict::getParentId, devDictEditParam.getParentId()))
                     .eq(DevDict::getDictValue, devDictEditParam.getDictValue()))
                  .ne(DevDict::getId, devDictEditParam.getId())
            )
            > 0L;
         if (hasSameDictValue) {
            throw new CommonException("存在重复的字典值，名称为：{}", new Object[]{devDictEditParam.getDictValue()});
         }
      }
   }

   @Override
   public void delete(List<DevDictIdParam> devDictIdParamList) {
      List<String> devDictIdList = CollStreamUtil.toList(devDictIdParamList, DevDictIdParam::getId);
      if (ObjectUtil.isNotEmpty(devDictIdList)) {
         boolean systemDict = this.listByIds(devDictIdList)
            .stream()
            .map(DevDict::getCategory)
            .collect(Collectors.toSet())
            .contains(DevDictCategoryEnum.FRM.getValue());
         if (systemDict) {
            throw new CommonException("不可删除系统内置字典", new Object[0]);
         }

         this.removeByIds(devDictIdList);
      }
   }

   @Override
   public DevDict detail(DevDictIdParam devDictIdParam) {
      return this.queryEntity(devDictIdParam.getId());
   }

   @Override
   public DevDict queryEntity(String id) {
      DevDict devDict = (DevDict)this.getById(id);
      if (ObjectUtil.isEmpty(devDict)) {
         throw new CommonException("字典不存在，id值为：{}", new Object[]{id});
      } else {
         return devDict;
      }
   }

   public void afterPropertiesSet() {
      this.refreshTransCache();
   }

   private void refreshTransCache() {
      CompletableFuture.supplyAsync(
         () -> {
            List<DevDict> devDictList = super.list(new LambdaQueryWrapper());
            Map<String, List<DevDict>> devDictGroupByPIDMap = devDictList.stream()
               .filter(dict -> !"0".equals(dict.getParentId()))
               .collect(Collectors.groupingBy(DevDict::getParentId));
            Map<String, String> parentDictIdValMap = devDictList.stream()
               .filter(dict -> "0".equals(dict.getParentId()))
               .collect(Collectors.toMap(DevDict::getId, DevDict::getDictValue));

            for (String parentId : parentDictIdValMap.keySet()) {
               if (devDictGroupByPIDMap.containsKey(parentId)) {
               }
            }

            return null;
         }
      );
   }
}
