package com.rxinyun.admin.production.modular.esc.cloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.esc.cloud.entity.ServerDiscounts;
import com.rxinyun.admin.production.modular.esc.cloud.mapper.ServerDiscountsMapper;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerDiscountsAddParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerDiscountsEditParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerDiscountsIdParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerDiscountsPageParam;
import com.rxinyun.admin.production.modular.esc.cloud.service.ServerDiscountsService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServerDiscountsServiceImpl extends ServiceImpl<ServerDiscountsMapper, ServerDiscounts> implements ServerDiscountsService {
   @Resource
   private ServerDiscountsMapper serverDiscountsMapper;

   @Override
   public Page<ServerDiscounts> page(ServerDiscountsPageParam pageParam) {
      QueryWrapper<ServerDiscounts> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(ServerDiscounts::getDescription, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<ServerDiscounts>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(ServerDiscountsAddParam serverDiscountsAddParam) {
      ServerDiscounts serverDiscounts = (ServerDiscounts)BeanUtil.toBean(serverDiscountsAddParam, ServerDiscounts.class);
      this.save(serverDiscounts);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(ServerDiscountsEditParam serverDiscountsEditParam) {
      ServerDiscounts serverDiscounts = this.queryEntity(serverDiscountsEditParam.getId());
      BeanUtil.copyProperties(serverDiscountsEditParam, serverDiscounts, new String[0]);
      this.updateById(serverDiscounts);
   }

   @Override
   public ServerDiscounts detail(ServerDiscountsIdParam serverDiscountsIdParam) {
      return this.queryEntity(serverDiscountsIdParam.getId());
   }

   @Override
   public ServerDiscounts queryEntity(String id) {
      ServerDiscounts serverDiscounts = (ServerDiscounts)this.getById(id);
      if (ObjectUtil.isEmpty(serverDiscounts)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return serverDiscounts;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<ServerDiscountsIdParam> serverDiscountsIdParamList) {
      List<String> serverDiscountsIdList = CollStreamUtil.toList(serverDiscountsIdParamList, ServerDiscountsIdParam::getId);
      if (ObjectUtil.isNotEmpty(serverDiscountsIdList)) {
         this.removeByIds(serverDiscountsIdList);
      }
   }

   @Override
   public List<ServerDiscounts> selectListByLineId(String lineId) {
      if (ObjectUtil.isEmpty(lineId)) {
         return new ArrayList<>();
      } else {
         LambdaQueryWrapper<ServerDiscounts> queryWrapper = (LambdaQueryWrapper<ServerDiscounts>)new QueryWrapper<ServerDiscounts>()
            .lambda()
            .eq(ServerDiscounts::getLineId, lineId);
         return this.serverDiscountsMapper.selectList(queryWrapper);
      }
   }
}
