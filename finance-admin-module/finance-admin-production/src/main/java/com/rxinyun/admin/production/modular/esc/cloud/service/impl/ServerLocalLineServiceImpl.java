package com.rxinyun.admin.production.modular.esc.cloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.esc.cloud.entity.ServerDiscounts;
import com.rxinyun.admin.production.modular.esc.cloud.entity.ServerLineSku;
import com.rxinyun.admin.production.modular.esc.cloud.entity.ServerLocalLine;
import com.rxinyun.admin.production.modular.esc.cloud.mapper.ServerLocalLineMapper;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerDiscountsAddParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerDiscountsEditParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLineSkuAddParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLineSkuEditParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLineAddParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLineEditParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLineEditSortParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLineIdParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLinePageParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLineUpdateDiscountTemplateParam;
import com.rxinyun.admin.production.modular.esc.cloud.response.ServerLocalLineResponse;
import com.rxinyun.admin.production.modular.esc.cloud.service.ServerDiscountsService;
import com.rxinyun.admin.production.modular.esc.cloud.service.ServerLineSkuService;
import com.rxinyun.admin.production.modular.esc.cloud.service.ServerLocalLineService;
import com.rxinyun.admin.production.modular.esc.cloud.vo.ServerLocalLineVO;
import com.rxinyun.admin.production.modular.esc.master.service.ServerMasterService;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.dev.core.pojo.BaseResourcePool;
import com.rxinyun.marketing.api.MemberDiscountTemplateApi;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServerLocalLineServiceImpl extends ServiceImpl<ServerLocalLineMapper, ServerLocalLine> implements ServerLocalLineService {
   private static final Logger log = LoggerFactory.getLogger(ServerLocalLineServiceImpl.class);
   @Resource
   private ServerLocalLineMapper serverLocalLineMapper;
   @Resource
   private ServerLineSkuService serverLineSkuService;
   @Resource
   private ServerDiscountsService serverDiscountsService;
   @Resource
   private JdbcTemplate jdbcTemplate;
   @Resource
   private ServerMasterService serverMasterService;
   @Resource
   private MemberDiscountTemplateApi memberDiscountTemplateApi;
   @Resource
   private ResourcePoolApi resourcePoolApi;
   private static final int MONTH_DAY_COUNT = 31;

   @Override
   public Page<ServerLocalLineResponse> page(ServerLocalLinePageParam pageParam) {
      Page<ServerLocalLine> page = new Page(pageParam.getCurrent().intValue(), pageParam.getSize().intValue());
      Page<ServerLocalLine> pageResult = this.serverLocalLineMapper.selectPage(page, pageParam);
      Page<ServerLocalLineResponse> pageResultResponse = new Page();
      if (CollectionUtils.isNotEmpty(pageResult.getRecords())) {
         List<ServerLocalLineResponse> responseList = pageResult.getRecords().stream().map(serverLocalLine -> {
            ServerLocalLineResponse serverLocalLineResponse = new ServerLocalLineResponse();
            BeanUtil.copyProperties(serverLocalLine, serverLocalLineResponse, new String[0]);
            if (serverLocalLine.getDiscountTemplateId() != null) {
               serverLocalLineResponse.setDiscountTemplateName(this.memberDiscountTemplateApi.selectNameById(serverLocalLine.getDiscountTemplateId()));
            }

            if (ObjectUtil.isNotEmpty(serverLocalLine.getResourcePoolId())) {
               serverLocalLineResponse.setResourcePoolId(serverLocalLine.getResourcePoolId());
               BaseResourcePool resourcePool = this.resourcePoolApi.getById(serverLocalLine.getResourcePoolId());
               if (ObjectUtil.isNotEmpty(resourcePool)) {
                  serverLocalLineResponse.setResourcePoolType(resourcePool.getPoolType());
                  serverLocalLineResponse.setResourcePoolName(resourcePool.getName());
               }
            }

            return serverLocalLineResponse;
         }).collect(Collectors.toList());
         pageResultResponse.setRecords(responseList);
      }

      pageResultResponse.setCurrent(pageResult.getCurrent());
      pageResultResponse.setSize(pageResult.getSize());
      pageResultResponse.setTotal(pageResult.getTotal());
      pageResultResponse.setPages(pageResult.getPages());
      return pageResultResponse;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(ServerLocalLineAddParam serverLocalLineAddParam) {
      if (serverLocalLineAddParam.getBandwidthMinSize() < serverLocalLineAddParam.getBandwidthFreeSize()) {
         throw new CommonException("不能低于赠送带宽", new Object[0]);
      } else if (serverLocalLineAddParam.getDataDiskMinSize() < serverLocalLineAddParam.getDataDiskFreeSize()) {
         throw new CommonException("不能低于赠送硬盘", new Object[0]);
      } else if (serverLocalLineAddParam.getSystemDiskMinSize() < serverLocalLineAddParam.getSystemDiskFreeSize()) {
         throw new CommonException("不能低于赠送系统盘", new Object[0]);
      } else if (serverLocalLineAddParam.getIpMinSize() < serverLocalLineAddParam.getIpFreeSize()) {
         throw new CommonException("不能低于赠送IP数量", new Object[0]);
      } else if (serverLocalLineAddParam.getIpMaxSize() < serverLocalLineAddParam.getIpMinSize()) {
         throw new CommonException("IP数量最大不能小于最小值", new Object[0]);
      } else {
         ServerLocalLine serverLocalLine = (ServerLocalLine)BeanUtil.toBean(serverLocalLineAddParam, ServerLocalLine.class);
         if (serverLocalLine.getDockingStatus() == null) {
            serverLocalLine.setDockingStatus(1);
         }

         serverLocalLine.setSystemDiskDayPrice(serverLocalLine.getSystemDiskMonthPrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
         serverLocalLine.setDataDiskDayPrice(serverLocalLine.getDataDiskMonthPrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
         serverLocalLine.setBandwidthDayPrice(serverLocalLine.getBandwidthPrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
         serverLocalLine.setDefenseDayPrice(serverLocalLine.getDefensePrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
         this.save(serverLocalLine);
         log.info("新增可用区域成功，id值为：{}", serverLocalLine.getId());
         if (CollectionUtils.isNotEmpty(serverLocalLineAddParam.getServerLineSkuList())) {
            for (ServerLineSku serverLineSku : serverLocalLineAddParam.getServerLineSkuList()) {
               ServerLineSkuAddParam serverLineSkuAddParam = new ServerLineSkuAddParam();
               BeanUtil.copyProperties(serverLineSku, serverLineSkuAddParam, new String[0]);
               serverLineSkuAddParam.setLineId(serverLocalLine.getId());
               this.serverLineSkuService.add(serverLineSkuAddParam);
            }
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(ServerLocalLineEditParam serverLocalLineEditParam) {
      if (serverLocalLineEditParam.getBandwidthMinSize() < serverLocalLineEditParam.getBandwidthFreeSize()) {
         throw new CommonException("不能低于赠送带宽", new Object[0]);
      } else if (serverLocalLineEditParam.getDataDiskMinSize() < serverLocalLineEditParam.getDataDiskFreeSize()) {
         throw new CommonException("不能低于赠送硬盘", new Object[0]);
      } else if (serverLocalLineEditParam.getSystemDiskMinSize() < serverLocalLineEditParam.getSystemDiskFreeSize()) {
         throw new CommonException("不能低于赠送系统盘", new Object[0]);
      } else if (serverLocalLineEditParam.getIpMinSize() < serverLocalLineEditParam.getIpFreeSize()) {
         throw new CommonException("不能低于赠送IP数量", new Object[0]);
      } else if (serverLocalLineEditParam.getIpMaxSize() < serverLocalLineEditParam.getIpMinSize()) {
         throw new CommonException("IP数量最大不能小于最小值", new Object[0]);
      } else {
         ServerLocalLine serverLocalLine = this.queryEntity(serverLocalLineEditParam.getId());
         if (serverLocalLine.getType() != null && serverLocalLine.getType() == 1) {
            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getPurchaseDurations())
               && !ObjectUtil.equal(serverLocalLineEditParam.getPurchaseDurations(), serverLocalLine.getPurchaseDurations())) {
               throw new CommonException("资源池类型线路不允许修改可选购买时长", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getMaxOpenNum())
               && !ObjectUtil.equal(serverLocalLineEditParam.getMaxOpenNum(), serverLocalLine.getMaxOpenNum())) {
               throw new CommonException("资源池类型线路不允许修改最大购买台数", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getUpgradeStatus())
               && !ObjectUtil.equal(serverLocalLineEditParam.getUpgradeStatus(), serverLocalLine.getUpgradeStatus())) {
               throw new CommonException("资源池类型线路不允许修改升级状态", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getRenewStatus())
               && !ObjectUtil.equal(serverLocalLineEditParam.getRenewStatus(), serverLocalLine.getRenewStatus())) {
               throw new CommonException("资源池类型线路不允许修改续费状态", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getMasterIndex())
               && !ObjectUtil.equal(serverLocalLineEditParam.getMasterIndex(), serverLocalLine.getMasterIndex())) {
               throw new CommonException("资源池类型线路不允许修改宿主机分配规则", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getIpRand())
               && !ObjectUtil.equal(serverLocalLineEditParam.getIpRand(), serverLocalLine.getIpRand())) {
               throw new CommonException("资源池类型线路不允许修改IP分配规则", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getSnapOpenMax())
               && !ObjectUtil.equal(serverLocalLineEditParam.getSnapOpenMax(), serverLocalLine.getSnapOpenMax())) {
               throw new CommonException("资源池类型线路不允许修改最大快照数量", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getSystemDiskMinSize())
               && !ObjectUtil.equal(serverLocalLineEditParam.getSystemDiskMinSize(), serverLocalLine.getSystemDiskMinSize())) {
               throw new CommonException("资源池类型线路不允许修改系统盘最小", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getSystemDiskMaxSize())
               && !ObjectUtil.equal(serverLocalLineEditParam.getSystemDiskMaxSize(), serverLocalLine.getSystemDiskMaxSize())) {
               throw new CommonException("资源池类型线路不允许修改系统盘最大", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getSystemDiskFreeSize())
               && !ObjectUtil.equal(serverLocalLineEditParam.getSystemDiskFreeSize(), serverLocalLine.getSystemDiskFreeSize())) {
               throw new CommonException("资源池类型线路不允许修改系统盘赠送大小", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getSystemDiskStep())
               && !ObjectUtil.equal(serverLocalLineEditParam.getSystemDiskStep(), serverLocalLine.getSystemDiskStep())) {
               throw new CommonException("资源池类型线路不允许修改系统盘大小必须是G的倍数", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getDataDiskMinSize())
               && !ObjectUtil.equal(serverLocalLineEditParam.getDataDiskMinSize(), serverLocalLine.getDataDiskMinSize())) {
               throw new CommonException("资源池类型线路不允许修改数据盘每块最小", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getDataDiskMaxSize())
               && !ObjectUtil.equal(serverLocalLineEditParam.getDataDiskMaxSize(), serverLocalLine.getDataDiskMaxSize())) {
               throw new CommonException("资源池类型线路不允许修改数据盘每块最大", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getDataDiskFreeSize())
               && !ObjectUtil.equal(serverLocalLineEditParam.getDataDiskFreeSize(), serverLocalLine.getDataDiskFreeSize())) {
               throw new CommonException("资源池类型线路不允许修改数据盘赠送大小", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getDataDiskStep())
               && !ObjectUtil.equal(serverLocalLineEditParam.getDataDiskStep(), serverLocalLine.getDataDiskStep())) {
               throw new CommonException("资源池类型线路不允许修改数据盘大小必须是G的倍数", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getBandwidthMinSize())
               && !ObjectUtil.equal(serverLocalLineEditParam.getBandwidthMinSize(), serverLocalLine.getBandwidthMinSize())) {
               throw new CommonException("资源池类型线路不允许修改带宽最小", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getBandwidthMaxSize())
               && !ObjectUtil.equal(serverLocalLineEditParam.getBandwidthMaxSize(), serverLocalLine.getBandwidthMaxSize())) {
               throw new CommonException("资源池类型线路不允许修改带宽最大", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getBandwidthFreeSize())
               && !ObjectUtil.equal(serverLocalLineEditParam.getBandwidthFreeSize(), serverLocalLine.getBandwidthFreeSize())) {
               throw new CommonException("资源池类型线路不允许修改带宽赠送值", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getBandwidthInValue())
               && !ObjectUtil.equal(serverLocalLineEditParam.getBandwidthInValue(), serverLocalLine.getBandwidthInValue())) {
               throw new CommonException("资源池类型线路不允许修改带宽下行限制值", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getBandwidthInType())
               && !ObjectUtil.equal(serverLocalLineEditParam.getBandwidthInType(), serverLocalLine.getBandwidthInType())) {
               throw new CommonException("资源池类型线路不允许修改带宽下行限制类型", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getIpMinSize())
               && !ObjectUtil.equal(serverLocalLineEditParam.getIpMinSize(), serverLocalLine.getIpMinSize())) {
               throw new CommonException("资源池类型线路不允许修改IP数量最小", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getIpMaxSize())
               && !ObjectUtil.equal(serverLocalLineEditParam.getIpMaxSize(), serverLocalLine.getIpMaxSize())) {
               throw new CommonException("资源池类型线路不允许修改IP数量最大", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getIpFreeSize())
               && !ObjectUtil.equal(serverLocalLineEditParam.getIpFreeSize(), serverLocalLine.getIpFreeSize())) {
               throw new CommonException("资源池类型线路不允许修改赠送IP数量", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getDefenseSize())
               && !ObjectUtil.equal(serverLocalLineEditParam.getDefenseSize(), serverLocalLine.getDefenseSize())) {
               throw new CommonException("资源池类型线路不允许修改防御值选项", new Object[0]);
            }

            if (ObjectUtil.isNotEmpty(serverLocalLineEditParam.getDefenseFreeSize())
               && !ObjectUtil.equal(serverLocalLineEditParam.getDefenseFreeSize(), serverLocalLine.getDefenseFreeSize())) {
               throw new CommonException("资源池类型线路不允许修改赠送防御", new Object[0]);
            }
         }

         if (serverLocalLineEditParam.getDockingStatus() == null) {
            serverLocalLineEditParam.setDockingStatus(serverLocalLine.getDockingStatus());
         }

         BeanUtil.copyProperties(serverLocalLineEditParam, serverLocalLine, new String[0]);
         serverLocalLine.setSystemDiskDayPrice(serverLocalLine.getSystemDiskMonthPrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
         serverLocalLine.setDataDiskDayPrice(serverLocalLine.getDataDiskMonthPrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
         serverLocalLine.setBandwidthDayPrice(serverLocalLine.getBandwidthPrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
         serverLocalLine.setDefenseDayPrice(serverLocalLine.getDefensePrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
         this.handleSkus(serverLocalLineEditParam.getId(), serverLocalLineEditParam.getServerLineSkuList());
         this.handleDiscounts(serverLocalLineEditParam.getId(), serverLocalLineEditParam.getServerDiscountsList());
         this.updateById(serverLocalLine);
      }
   }

   private void handleSkus(String lineId, List<ServerLineSku> skuList) {
      if (!CollectionUtils.isEmpty(skuList)) {
         for (ServerLineSku serverLineSku : skuList) {
            serverLineSku.setLineId(lineId);
            if (serverLineSku.getId() != null) {
               ServerLineSku existingSku = this.serverLineSkuService.queryEntity(serverLineSku.getId());
               if (existingSku.getType() != null && existingSku.getType() == 1) {
                  if (serverLineSku.getCpu() != existingSku.getCpu()) {
                     throw new CommonException("资源池类型SKU不允许修改CPU数量", new Object[0]);
                  }

                  if (serverLineSku.getRam() != existingSku.getRam()) {
                     throw new CommonException("资源池类型SKU不允许修改内存大小", new Object[0]);
                  }
               }

               ServerLineSkuEditParam serverLineSkuEditParam = new ServerLineSkuEditParam();
               BeanUtil.copyProperties(serverLineSku, serverLineSkuEditParam, new String[0]);
               this.serverLineSkuService.edit(serverLineSkuEditParam);
            } else {
               ServerLineSkuAddParam serverLineSkuAddParam = new ServerLineSkuAddParam();
               BeanUtil.copyProperties(serverLineSku, serverLineSkuAddParam, new String[0]);
               this.serverLineSkuService.add(serverLineSkuAddParam);
            }
         }
      }
   }

   private void handleDiscounts(String lineId, List<ServerDiscounts> discountsList) {
      if (!CollectionUtils.isEmpty(discountsList)) {
         for (ServerDiscounts discounts : discountsList) {
            discounts.setLineId(lineId);
            if (discounts.getId() != null) {
               ServerDiscountsEditParam serverDiscountsEditParam = new ServerDiscountsEditParam();
               BeanUtil.copyProperties(discounts, serverDiscountsEditParam, new String[0]);
               this.serverDiscountsService.edit(serverDiscountsEditParam);
            } else {
               ServerDiscountsAddParam serverDiscountsAddParam = new ServerDiscountsAddParam();
               BeanUtil.copyProperties(discounts, serverDiscountsAddParam, new String[0]);
               this.serverDiscountsService.add(serverDiscountsAddParam);
            }
         }
      }
   }

   @Override
   public ServerLocalLineVO detail(ServerLocalLineIdParam serverLocalLineIdParam) {
      ServerLocalLine serverLocalLine = (ServerLocalLine)this.getById(serverLocalLineIdParam.getId());
      if (ObjectUtil.isEmpty(serverLocalLine)) {
         throw new CommonException("线路不存在，id值为：{}", new Object[]{serverLocalLineIdParam.getId()});
      } else {
         ServerLocalLineVO serverLocalLineVO = (ServerLocalLineVO)BeanUtil.toBean(serverLocalLine, ServerLocalLineVO.class);
         if (ObjectUtil.isNotEmpty(serverLocalLine.getResourcePoolId())) {
            serverLocalLineVO.setResourcePoolId(serverLocalLine.getResourcePoolId());
            BaseResourcePool resourcePool = this.resourcePoolApi.getById(serverLocalLine.getResourcePoolId());
            if (ObjectUtil.isNotEmpty(resourcePool)) {
               serverLocalLineVO.setResourcePoolType(resourcePool.getPoolType());
               serverLocalLineVO.setResourcePoolName(resourcePool.getName());
            }
         }

         List<ServerLineSku> serverLineSkuList = this.serverLineSkuService.selectListByLineId(serverLocalLine.getId());
         if (CollectionUtils.isNotEmpty(serverLineSkuList)) {
            serverLocalLineVO.setServerLineSkuList(serverLineSkuList);
         }

         List<ServerDiscounts> serverDiscountsList = this.serverDiscountsService.selectListByLineId(serverLocalLine.getId());
         if (CollectionUtils.isNotEmpty(serverDiscountsList)) {
            serverLocalLineVO.setServerDiscountsList(serverDiscountsList);
         }

         return serverLocalLineVO;
      }
   }

   @Override
   public ServerLocalLine queryEntity(String id) {
      return (ServerLocalLine)this.getById(id);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<ServerLocalLineIdParam> serverLocalLineIdParamList) {
      List<String> serverLocalLineIdList = CollStreamUtil.toList(serverLocalLineIdParamList, ServerLocalLineIdParam::getId);
      log.debug("删除数据，主键：{}", serverLocalLineIdList);
      if (!ObjectUtil.isEmpty(serverLocalLineIdList)) {
         boolean anyBoundToHost = serverLocalLineIdList.stream().anyMatch(lineId -> this.serverMasterService.isBindingMaster(lineId));
         if (anyBoundToHost) {
            throw new CommonException("存在已绑定宿主机的线路，无法删除", new Object[0]);
         } else {
            this.serverLineSkuService.deleteByLineIdList(serverLocalLineIdList);
            String placeholders = String.join(",", Collections.nCopies(serverLocalLineIdList.size(), "?"));
            Object[] idArgs = serverLocalLineIdList.toArray();
            this.jdbcTemplate.update("DELETE FROM SERVER_DISCOUNTS WHERE line_id IN (" + placeholders + ")", idArgs);
            this.jdbcTemplate.update("DELETE FROM SERVER_LOCAL_LINE WHERE id IN (" + placeholders + ")", idArgs);
         }
      }
   }

   @Override
   public Boolean isBindingAvailable(String id) {
      if (ObjectUtil.isEmpty(id)) {
         throw new CommonException("可用区id为空", new Object[0]);
      } else {
         QueryWrapper<ServerLocalLine> queryWrapper = (QueryWrapper<ServerLocalLine>)new QueryWrapper().eq("available_id", id);
         List<ServerLocalLine> lines = this.serverLocalLineMapper.selectList(queryWrapper);
         return !lines.isEmpty();
      }
   }

   @Override
   public Map<String, String> lineList() {
      return this.list(new QueryWrapper<ServerLocalLine>().lambda().select(ServerLocalLine::getId, ServerLocalLine::getLineName))
         .stream()
         .collect(Collectors.toMap(ServerLocalLine::getId, ServerLocalLine::getLineName, (existing, replacement) -> existing));
   }

   @Override
   public void updateSort(ServerLocalLineEditSortParam serverLocalLineEditSortParam) {
      ServerLocalLine serverLocalLine = this.queryEntity(serverLocalLineEditSortParam.getId());
      serverLocalLine.setSortId(serverLocalLineEditSortParam.getSortId());
      this.updateById(serverLocalLine);
   }

   @Override
   public void updateDiscountTemplate(ServerLocalLineUpdateDiscountTemplateParam serverLocalLineUpdateTemplateParam) {
      ServerLocalLine serverLocalLine = this.queryEntity(serverLocalLineUpdateTemplateParam.getLineId());
      serverLocalLine.setDiscountTemplateId(serverLocalLineUpdateTemplateParam.getDiscountTemplateId());
      this.updateById(serverLocalLine);
   }
}
