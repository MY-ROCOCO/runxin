package com.rxinyun.admin.production.modular.esc.cloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.esc.cloud.entity.ServerLineSku;
import com.rxinyun.admin.production.modular.esc.cloud.mapper.ServerLineSkuMapper;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLineSkuAddParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLineSkuEditParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLineSkuIdParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLineSkuPageParam;
import com.rxinyun.admin.production.modular.esc.cloud.service.ServerLineSkuService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServerLineSkuServiceImpl extends ServiceImpl<ServerLineSkuMapper, ServerLineSku> implements ServerLineSkuService {
   @Resource
   private ServerLineSkuMapper serverLineSkuMapper;
   @Resource
   private JdbcTemplate jdbcTemplate;
   private static final int MONTH_DAY_COUNT = 31;

   @Override
   public Page<ServerLineSku> page(ServerLineSkuPageParam pageParam) {
      QueryWrapper<ServerLineSku> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(ServerLineSku::getName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         ((LambdaQueryWrapper<ServerLineSku>)queryWrapper.lambda().orderByAsc(ServerLineSku::getSortId)).orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<ServerLineSku>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(ServerLineSkuAddParam serverLineSkuAddParam) {
      ServerLineSku serverLineSku = (ServerLineSku)BeanUtil.toBean(serverLineSkuAddParam, ServerLineSku.class);
      serverLineSku.setCoreDayPrice(serverLineSkuAddParam.getCoreMonthPrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
      this.save(serverLineSku);
   }

   @Override
   public void edit(ServerLineSkuEditParam serverLineSkuEditParam) {
      ServerLineSku serverLineSku = this.queryEntity(serverLineSkuEditParam.getId());
      if (serverLineSku.getType() != null && serverLineSku.getType() == 1) {
         if (serverLineSkuEditParam.getCpu() != serverLineSku.getCpu()) {
            throw new CommonException("资源池类型SKU不允许修改CPU数量", new Object[0]);
         }

         if (serverLineSkuEditParam.getRam() != serverLineSku.getRam()) {
            throw new CommonException("资源池类型SKU不允许修改内存大小", new Object[0]);
         }
      }

      BeanUtil.copyProperties(serverLineSkuEditParam, serverLineSku, new String[0]);
      serverLineSku.setCoreDayPrice(serverLineSkuEditParam.getCoreMonthPrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
      this.updateById(serverLineSku);
   }

   @Override
   public ServerLineSku detail(ServerLineSkuIdParam serverLineSkuIdParam) {
      return this.queryEntity(serverLineSkuIdParam.getId());
   }

   @Override
   public ServerLineSku queryEntity(String id) {
      ServerLineSku serverLineSku = (ServerLineSku)this.getById(id);
      if (ObjectUtil.isEmpty(serverLineSku)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return serverLineSku;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<ServerLineSkuIdParam> serverLineSkuIdParamList) {
      List<String> serverLineSkuIdList = CollStreamUtil.toList(serverLineSkuIdParamList, ServerLineSkuIdParam::getId);
      if (ObjectUtil.isNotEmpty(serverLineSkuIdList)) {
         String placeholders = String.join(",", Collections.nCopies(serverLineSkuIdList.size(), "?"));
         this.jdbcTemplate.update("DELETE FROM SERVER_LINE_SKU WHERE id IN (" + placeholders + ")", serverLineSkuIdList.toArray());
      }
   }

   @Override
   public List<ServerLineSku> selectListByLineId(String lineId) {
      if (ObjectUtil.isEmpty(lineId)) {
         return new ArrayList<>();
      } else {
         LambdaQueryWrapper<ServerLineSku> queryWrapper = (LambdaQueryWrapper<ServerLineSku>)((LambdaQueryWrapper<ServerLineSku>)((LambdaQueryWrapper<ServerLineSku>)new QueryWrapper<ServerLineSku>()
                  .lambda()
                  .eq(ServerLineSku::getLineId, lineId))
               .orderByAsc(ServerLineSku::getSortId))
            .orderByDesc(CommonEntity::getCreateTime);
         return this.serverLineSkuMapper.selectList(queryWrapper);
      }
   }

   @Override
   public void deleteByLineIdList(List<String> serverLocalLineIdList) {
      if (!ObjectUtil.isEmpty(serverLocalLineIdList)) {
         String placeholders = String.join(",", Collections.nCopies(serverLocalLineIdList.size(), "?"));
         this.jdbcTemplate.update("DELETE FROM SERVER_LINE_SKU WHERE line_id IN (" + placeholders + ")", serverLocalLineIdList.toArray());
      }
   }
}
