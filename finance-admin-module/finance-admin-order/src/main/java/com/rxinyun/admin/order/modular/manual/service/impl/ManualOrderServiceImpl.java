package com.rxinyun.admin.order.modular.manual.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.rxinyun.admin.order.modular.ecs.entity.IndependentOrderServerConfig;
import com.rxinyun.admin.order.modular.ecs.entity.Order;
import com.rxinyun.admin.order.modular.ecs.mapper.OrderMapper;
import com.rxinyun.admin.order.modular.ecs.param.BandwidthUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.CalculatePriceParam;
import com.rxinyun.admin.order.modular.ecs.param.CoreUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.DefenseUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.DiskUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.OrderServerAddParam;
import com.rxinyun.admin.order.modular.ecs.param.OrderServerConfigAddParam;
import com.rxinyun.admin.order.modular.ecs.param.RenewOrderParam;
import com.rxinyun.admin.order.modular.ecs.response.CalculatePriceResponse;
import com.rxinyun.admin.order.modular.ecs.service.CalculatePriceService;
import com.rxinyun.admin.order.modular.ecs.service.IndependentOrderServerConfigService;
import com.rxinyun.admin.order.modular.ecs.service.OrderServerConfigService;
import com.rxinyun.admin.order.modular.ecs.service.OrderServerService;
import com.rxinyun.admin.order.modular.ecs.service.OrderService;
import com.rxinyun.admin.order.modular.independent.param.IndependentBandwidthUpgradeOrderParam;
import com.rxinyun.admin.order.modular.independent.param.IndependentCalculatePriceParam;
import com.rxinyun.admin.order.modular.independent.param.IndependentDefenseUpgradeOrderParam;
import com.rxinyun.admin.order.modular.independent.param.IndependentIpUpgradeOrderParam;
import com.rxinyun.admin.order.modular.independent.param.IndependentRenewOrderParam;
import com.rxinyun.admin.order.modular.independent.response.IndependentCalculatePriceResponse;
import com.rxinyun.admin.order.modular.independent.service.IndependentCalculatePriceService;
import com.rxinyun.admin.order.modular.manual.param.ManualCloudBandwidthUpgradeOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualCloudCoreUpgradeOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualCloudCreateOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualCloudDefenseUpgradeOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualCloudDiskUpgradeOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualCloudRenewOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualIndependentBandwidthUpgradeOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualIndependentCreateOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualIndependentDefenseUpgradeOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualIndependentIpUpgradeOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualIndependentRenewOrderParam;
import com.rxinyun.admin.order.modular.manual.response.ManualOrderResult;
import com.rxinyun.admin.order.modular.manual.service.ManualOrderService;
import com.rxinyun.business.api.IndependentApi;
import com.rxinyun.business.api.ServerApi;
import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.business.core.pojo.BaseServer;
import com.rxinyun.common.enums.UpgradeItemEnum;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.PayTypeEnum;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.OrderNoGenerator;
import com.rxinyun.common.util.OsInstancePasswordValidator;
import com.rxinyun.common.util.RenewServerIdSupport;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import com.rxinyun.marketing.api.MemberCouponApi;
import com.rxinyun.member.admin.api.BalanceApi;
import com.rxinyun.member.admin.pojo.BaseBalance;
import com.rxinyun.member.member.api.MemberApi;
import com.rxinyun.member.member.pojo.BaseMember;
import com.rxinyun.production.api.AdminMachineApi;
import com.rxinyun.production.core.pojo.BaseAdminMachine;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class ManualOrderServiceImpl implements ManualOrderService {
   private static final Logger log = LoggerFactory.getLogger(ManualOrderServiceImpl.class);
   private static final int DEDUCT_MODE_NONE = 1;
   private static final int DEDUCT_MODE_AUTO = 2;
   private static final int DEDUCT_MODE_MANUAL = 3;
   @Resource
   private MemberApi memberApi;
   @Resource
   private BalanceApi balanceApi;
   @Resource
   private OrderService orderService;
   @Resource
   private MemberCouponApi memberCouponApi;
   @Resource
   private OrderMapper orderMapper;
   @Resource
   private OrderServerConfigService orderServerConfigService;
   @Resource
   private IndependentOrderServerConfigService independentOrderServerConfigService;
   @Resource
   private OrderServerService orderServerService;
   @Resource
   private CalculatePriceService calculatePriceService;
   @Resource
   private IndependentCalculatePriceService independentCalculatePriceService;
   @Resource
   private ServerApi serverApi;
   @Resource
   private IndependentApi independentApi;
   @Resource
   private AdminMachineApi adminMachineApi;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<ManualOrderResult> createCloudOrder(ManualCloudCreateOrderParam param) {
      OsInstancePasswordValidator.validateOrThrow(param.getLoginPassword());
      BaseMember member = this.requireMember(param.getMemberId());
      CalculatePriceParam calculateParam = new CalculatePriceParam();
      calculateParam.setMemberNo(param.getMemberId());
      calculateParam.setBillingMode(param.getBillingMode());
      calculateParam.setLineId(param.getLineId());
      calculateParam.setCpu(param.getCpu());
      calculateParam.setRam(param.getRam());
      calculateParam.setSystemDisk(param.getSystemDisk());
      calculateParam.setDataDisks(param.getDataDisks());
      calculateParam.setBandwidth(param.getBandwidth());
      calculateParam.setDefense(param.getDefense());
      calculateParam.setIpNum(param.getIpNum());
      calculateParam.setUnit(param.getUnit());
      calculateParam.setDuration(param.getDuration());
      calculateParam.setNumber(param.getNumber());
      CalculatePriceResponse priceResponse = this.requireData(this.calculatePriceService.calculatePrice(calculateParam), "云服务器计价失败");
      String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.CLOUD_SERVER.getCode());
      BigDecimal settledAmount = this.resolveSettledAmount(param.getDeductMode(), param.getDeductAmount(), priceResponse.getPayPrice());
      Order order = this.buildBaseOrder(
         orderNo,
         member,
         ProductTypeEnum.CLOUD_SERVER.getCode(),
         OrderTypeEnum.NEW_PURCHASE.getCode(),
         null,
         priceResponse.getBillingMode(),
         priceResponse.getTotalPrice(),
         settledAmount,
         null
      );
      this.orderService.save(order);
      this.saveCloudOrderConfig(
         orderNo, param.getLineId(), priceResponse, settledAmount, param.getSystemClassId(), param.getSystemId(), param.getLoginPassword()
      );
      this.settleAndFulfill(order, param.getMemberId(), settledAmount, "后台手动开通云服务器", param);
      return CommonResult.data(this.buildResult(order, settledAmount));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<ManualOrderResult> upgradeCloudCore(ManualCloudCoreUpgradeOrderParam param) {
      BaseMember member = this.requireMember(param.getMemberId());
      BaseServer server = this.requireCloudServer(param.getServerId(), param.getMemberId());
      this.cancelUnpaidOrders(server.getId());
      this.validateCloudCoreUpgrade(server, param.getCpu(), param.getRam());
      CoreUpgradeOrderParam upgradeParam = new CoreUpgradeOrderParam();
      upgradeParam.setServerId(server.getId());
      upgradeParam.setCpu(param.getCpu());
      upgradeParam.setRam(param.getRam());
      CalculatePriceResponse priceResponse = this.requireData(this.calculatePriceService.upgradeCore(upgradeParam), "云服务器升级CPU/内存计价失败");
      String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.CLOUD_SERVER.getCode());
      BigDecimal settledAmount = this.resolveSettledAmount(param.getDeductMode(), param.getDeductAmount(), priceResponse.getPayPrice());
      Order order = this.buildBaseOrder(
         orderNo,
         member,
         ProductTypeEnum.CLOUD_SERVER.getCode(),
         OrderTypeEnum.UPGRADE.getCode(),
         UpgradeItemEnum.UPGRADE_CORE.getCode(),
         priceResponse.getBillingMode(),
         priceResponse.getTotalPrice(),
         settledAmount,
         priceResponse.getOriginalOrderNo()
      );
      order.setUpstreamServerIds(server.getId());
      this.orderService.save(order);
      this.saveCloudOrderConfig(
         orderNo, server.getLineId(), priceResponse, settledAmount, server.getSystemClassId(), server.getSystemId(), server.getSystemPassword()
      );
      this.saveOrderServerRelation(orderNo, server.getId(), server.getHostName());
      this.settleAndFulfill(order, param.getMemberId(), settledAmount, "后台手动升级云服务器CPU内存", param);
      return CommonResult.data(this.buildResult(order, settledAmount));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<ManualOrderResult> upgradeCloudDisk(ManualCloudDiskUpgradeOrderParam param) {
      BaseMember member = this.requireMember(param.getMemberId());
      BaseServer server = this.requireCloudServer(param.getServerId(), param.getMemberId());
      this.cancelUnpaidOrders(server.getId());
      this.validateCloudDiskUpgrade(server, param.getSystemDisk(), param.getDataDisks());
      DiskUpgradeOrderParam upgradeParam = new DiskUpgradeOrderParam();
      upgradeParam.setServerId(server.getId());
      upgradeParam.setSystemDisk(param.getSystemDisk());
      upgradeParam.setDataDisks(param.getDataDisks());
      CalculatePriceResponse priceResponse = this.requireData(this.calculatePriceService.upgradeDisk(upgradeParam), "云服务器升级磁盘计价失败");
      String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.CLOUD_SERVER.getCode());
      BigDecimal settledAmount = this.resolveSettledAmount(param.getDeductMode(), param.getDeductAmount(), priceResponse.getPayPrice());
      Order order = this.buildBaseOrder(
         orderNo,
         member,
         ProductTypeEnum.CLOUD_SERVER.getCode(),
         OrderTypeEnum.UPGRADE.getCode(),
         UpgradeItemEnum.UPGRADE_HARD_DISK.getCode(),
         priceResponse.getBillingMode(),
         priceResponse.getTotalPrice(),
         settledAmount,
         priceResponse.getOriginalOrderNo()
      );
      order.setUpstreamServerIds(server.getId());
      this.orderService.save(order);
      this.saveCloudOrderConfig(
         orderNo, server.getLineId(), priceResponse, settledAmount, server.getSystemClassId(), server.getSystemId(), server.getSystemPassword()
      );
      this.saveOrderServerRelation(orderNo, server.getId(), server.getHostName());
      this.settleAndFulfill(order, param.getMemberId(), settledAmount, "后台手动升级云服务器磁盘", param);
      return CommonResult.data(this.buildResult(order, settledAmount));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<ManualOrderResult> upgradeCloudBandwidth(ManualCloudBandwidthUpgradeOrderParam param) {
      BaseMember member = this.requireMember(param.getMemberId());
      BaseServer server = this.requireCloudServer(param.getServerId(), param.getMemberId());
      this.cancelUnpaidOrders(server.getId());
      this.validateCloudBandwidthUpgrade(server, param.getBandwidth());
      BandwidthUpgradeOrderParam upgradeParam = new BandwidthUpgradeOrderParam();
      upgradeParam.setServerId(server.getId());
      upgradeParam.setBandwidth(param.getBandwidth());
      CalculatePriceResponse priceResponse = this.requireData(this.calculatePriceService.upgradeBandwidth(upgradeParam), "云服务器升级带宽计价失败");
      String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.CLOUD_SERVER.getCode());
      BigDecimal settledAmount = this.resolveSettledAmount(param.getDeductMode(), param.getDeductAmount(), priceResponse.getPayPrice());
      Order order = this.buildBaseOrder(
         orderNo,
         member,
         ProductTypeEnum.CLOUD_SERVER.getCode(),
         OrderTypeEnum.UPGRADE.getCode(),
         UpgradeItemEnum.UPGRADE_BANDWIDTH.getCode(),
         priceResponse.getBillingMode(),
         priceResponse.getTotalPrice(),
         settledAmount,
         priceResponse.getOriginalOrderNo()
      );
      order.setUpstreamServerIds(server.getId());
      this.orderService.save(order);
      this.saveCloudOrderConfig(
         orderNo, server.getLineId(), priceResponse, settledAmount, server.getSystemClassId(), server.getSystemId(), server.getSystemPassword()
      );
      this.saveOrderServerRelation(orderNo, server.getId(), server.getHostName());
      this.settleAndFulfill(order, param.getMemberId(), settledAmount, "后台手动升级云服务器带宽", param);
      return CommonResult.data(this.buildResult(order, settledAmount));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<ManualOrderResult> upgradeCloudDefense(ManualCloudDefenseUpgradeOrderParam param) {
      BaseMember member = this.requireMember(param.getMemberId());
      BaseServer server = this.requireCloudServer(param.getServerId(), param.getMemberId());
      this.cancelUnpaidOrders(server.getId());
      this.validateCloudDefenseUpgrade(server, param.getDefense());
      DefenseUpgradeOrderParam upgradeParam = new DefenseUpgradeOrderParam();
      upgradeParam.setServerId(server.getId());
      upgradeParam.setDefense(param.getDefense());
      CalculatePriceResponse priceResponse = this.requireData(this.calculatePriceService.upgradeDefense(upgradeParam), "云服务器升级防御计价失败");
      String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.CLOUD_SERVER.getCode());
      BigDecimal settledAmount = this.resolveSettledAmount(param.getDeductMode(), param.getDeductAmount(), priceResponse.getPayPrice());
      Order order = this.buildBaseOrder(
         orderNo,
         member,
         ProductTypeEnum.CLOUD_SERVER.getCode(),
         OrderTypeEnum.UPGRADE.getCode(),
         UpgradeItemEnum.UPGRADE_DEFENSE.getCode(),
         priceResponse.getBillingMode(),
         priceResponse.getTotalPrice(),
         settledAmount,
         priceResponse.getOriginalOrderNo()
      );
      order.setUpstreamServerIds(server.getId());
      this.orderService.save(order);
      this.saveCloudOrderConfig(
         orderNo, server.getLineId(), priceResponse, settledAmount, server.getSystemClassId(), server.getSystemId(), server.getSystemPassword()
      );
      this.saveOrderServerRelation(orderNo, server.getId(), server.getHostName());
      this.settleAndFulfill(order, param.getMemberId(), settledAmount, "后台手动升级云服务器防御", param);
      return CommonResult.data(this.buildResult(order, settledAmount));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<ManualOrderResult> renewCloudOrder(ManualCloudRenewOrderParam param) {
      return this.renewCloudOrder(param, "后台手动续费云服务器");
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<ManualOrderResult> renewCloudOrder(ManualCloudRenewOrderParam param, String settleRemark) {
      BaseMember member = this.requireMember(param.getMemberId());
      List<String> serverIds = RenewServerIdSupport.resolve(param.getServerId(), param.getServerIds());
      List<BaseServer> servers = new ArrayList<>();

      for (String serverId : serverIds) {
         BaseServer server = this.requireCloudServer(serverId, param.getMemberId());
         this.cancelUnpaidOrders(server.getId());
         servers.add(server);
      }

      BaseServer firstServer = servers.get(0);
      this.validateRenewDuration(param.getDuration());
      RenewOrderParam renewParam = new RenewOrderParam();
      renewParam.setServerIds(serverIds);
      renewParam.setDuration(param.getDuration());
      CalculatePriceResponse priceResponse = this.requireData(this.calculatePriceService.renew(renewParam), "云服务器续费计价失败");
      String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.CLOUD_SERVER.getCode());
      BigDecimal settledAmount = this.resolveSettledAmount(param.getDeductMode(), param.getDeductAmount(), priceResponse.getPayPrice());
      Order order = this.buildBaseOrder(
         orderNo,
         member,
         ProductTypeEnum.CLOUD_SERVER.getCode(),
         OrderTypeEnum.RENEW.getCode(),
         null,
         priceResponse.getBillingMode(),
         priceResponse.getTotalPrice(),
         settledAmount,
         null
      );
      order.setUpstreamServerIds(String.join(",", serverIds));
      this.orderService.save(order);
      this.saveCloudOrderConfig(
         orderNo,
         firstServer.getLineId(),
         priceResponse,
         settledAmount,
         firstServer.getSystemClassId(),
         firstServer.getSystemId(),
         firstServer.getSystemPassword()
      );

      for (BaseServer server : servers) {
         this.saveOrderServerRelation(orderNo, server.getId(), server.getHostName());
      }

      this.settleAndFulfill(order, param.getMemberId(), settledAmount, StrUtil.blankToDefault(settleRemark, "后台手动续费云服务器"), param);
      return CommonResult.data(this.buildResult(order, settledAmount));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<ManualOrderResult> createIndependentOrder(ManualIndependentCreateOrderParam param) {
      OsInstancePasswordValidator.validateOrThrow(param.getLoginPassword());
      BaseMember member = this.requireMember(param.getMemberId());
      IndependentCalculatePriceParam calculateParam = new IndependentCalculatePriceParam();
      calculateParam.setMemberNo(param.getMemberId());
      calculateParam.setBillingMode(param.getBillingMode());
      calculateParam.setRegionId(param.getRegionId());
      calculateParam.setLineId(param.getLineId());
      calculateParam.setMachineId(param.getMachineId());
      calculateParam.setBandwidth(param.getBandwidth());
      calculateParam.setDefense(param.getDefense());
      calculateParam.setIpSize(param.getIpSize());
      calculateParam.setUnit(param.getUnit());
      calculateParam.setDuration(param.getDuration());
      calculateParam.setNumber(param.getNumber());
      IndependentCalculatePriceResponse priceResponse = this.requireData(this.independentCalculatePriceService.calculatePrice(calculateParam), "独立服务器计价失败");
      String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
      BigDecimal settledAmount = this.resolveSettledAmount(param.getDeductMode(), param.getDeductAmount(), priceResponse.getPayPrice());
      Order order = this.buildBaseOrder(
         orderNo,
         member,
         ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
         OrderTypeEnum.NEW_PURCHASE.getCode(),
         null,
         priceResponse.getBillingMode(),
         priceResponse.getTotalPrice(),
         settledAmount,
         null
      );
      this.orderService.save(order);
      this.saveIndependentOrderConfig(
         orderNo, priceResponse, settledAmount, param.getRegionId(), param.getLineId(), param.getSystemClassId(), param.getSystemId(), param.getLoginPassword()
      );
      this.settleAndFulfill(order, param.getMemberId(), settledAmount, "后台手动开通独立服务器", param);
      return CommonResult.data(this.buildResult(order, settledAmount));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<ManualOrderResult> upgradeIndependentIp(ManualIndependentIpUpgradeOrderParam param) {
      BaseMember member = this.requireMember(param.getMemberId());
      BaseIndependent server = this.requireIndependentServer(param.getServerId(), param.getMemberId());
      this.cancelUnpaidOrders(server.getId());
      this.validateIndependentIpUpgrade(server, param.getIpSize());
      IndependentIpUpgradeOrderParam upgradeParam = new IndependentIpUpgradeOrderParam();
      upgradeParam.setServerId(server.getId());
      upgradeParam.setIpSize(param.getIpSize());
      IndependentCalculatePriceResponse priceResponse = this.requireData(this.independentCalculatePriceService.upgradeIp(upgradeParam), "独立服务器升级IP计价失败");
      String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
      BigDecimal settledAmount = this.resolveSettledAmount(param.getDeductMode(), param.getDeductAmount(), priceResponse.getPayPrice());
      Order order = this.buildBaseOrder(
         orderNo,
         member,
         ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
         OrderTypeEnum.UPGRADE.getCode(),
         UpgradeItemEnum.UPGRADE_IP.getCode(),
         priceResponse.getBillingMode(),
         priceResponse.getTotalPrice(),
         settledAmount,
         priceResponse.getOriginalOrderNo()
      );
      order.setUpstreamServerIds(server.getId());
      this.orderService.save(order);
      this.saveIndependentOrderConfig(
         orderNo,
         priceResponse,
         settledAmount,
         server.getRegionId(),
         server.getLineId(),
         server.getSystemClassId(),
         server.getSystemId(),
         server.getSystemPassword()
      );
      this.saveOrderServerRelation(orderNo, server.getId(), server.getServerName());
      this.settleAndFulfill(order, param.getMemberId(), settledAmount, "后台手动升级独立服务器IP", param);
      return CommonResult.data(this.buildResult(order, settledAmount));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<ManualOrderResult> upgradeIndependentBandwidth(ManualIndependentBandwidthUpgradeOrderParam param) {
      BaseMember member = this.requireMember(param.getMemberId());
      BaseIndependent server = this.requireIndependentServer(param.getServerId(), param.getMemberId());
      this.cancelUnpaidOrders(server.getId());
      this.validateIndependentBandwidthUpgrade(server, param.getBandwidth());
      IndependentBandwidthUpgradeOrderParam upgradeParam = new IndependentBandwidthUpgradeOrderParam();
      upgradeParam.setServerId(server.getId());
      upgradeParam.setBandwidth(param.getBandwidth());
      IndependentCalculatePriceResponse priceResponse = this.requireData(this.independentCalculatePriceService.upgradeBandwidth(upgradeParam), "独立服务器升级带宽计价失败");
      String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
      BigDecimal settledAmount = this.resolveSettledAmount(param.getDeductMode(), param.getDeductAmount(), priceResponse.getPayPrice());
      Order order = this.buildBaseOrder(
         orderNo,
         member,
         ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
         OrderTypeEnum.UPGRADE.getCode(),
         UpgradeItemEnum.UPGRADE_BANDWIDTH.getCode(),
         priceResponse.getBillingMode(),
         priceResponse.getTotalPrice(),
         settledAmount,
         priceResponse.getOriginalOrderNo()
      );
      order.setUpstreamServerIds(server.getId());
      this.orderService.save(order);
      this.saveIndependentOrderConfig(
         orderNo,
         priceResponse,
         settledAmount,
         server.getRegionId(),
         server.getLineId(),
         server.getSystemClassId(),
         server.getSystemId(),
         server.getSystemPassword()
      );
      this.saveOrderServerRelation(orderNo, server.getId(), server.getServerName());
      this.settleAndFulfill(order, param.getMemberId(), settledAmount, "后台手动升级独立服务器带宽", param);
      return CommonResult.data(this.buildResult(order, settledAmount));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<ManualOrderResult> upgradeIndependentDefense(ManualIndependentDefenseUpgradeOrderParam param) {
      BaseMember member = this.requireMember(param.getMemberId());
      BaseIndependent server = this.requireIndependentServer(param.getServerId(), param.getMemberId());
      this.cancelUnpaidOrders(server.getId());
      this.validateIndependentDefenseUpgrade(server, param.getDefense());
      IndependentDefenseUpgradeOrderParam upgradeParam = new IndependentDefenseUpgradeOrderParam();
      upgradeParam.setServerId(server.getId());
      upgradeParam.setDefense(param.getDefense());
      IndependentCalculatePriceResponse priceResponse = this.requireData(this.independentCalculatePriceService.upgradeDefense(upgradeParam), "独立服务器升级防御计价失败");
      String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
      BigDecimal settledAmount = this.resolveSettledAmount(param.getDeductMode(), param.getDeductAmount(), priceResponse.getPayPrice());
      Order order = this.buildBaseOrder(
         orderNo,
         member,
         ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
         OrderTypeEnum.UPGRADE.getCode(),
         UpgradeItemEnum.UPGRADE_DEFENSE.getCode(),
         priceResponse.getBillingMode(),
         priceResponse.getTotalPrice(),
         settledAmount,
         priceResponse.getOriginalOrderNo()
      );
      order.setUpstreamServerIds(server.getId());
      this.orderService.save(order);
      this.saveIndependentOrderConfig(
         orderNo,
         priceResponse,
         settledAmount,
         server.getRegionId(),
         server.getLineId(),
         server.getSystemClassId(),
         server.getSystemId(),
         server.getSystemPassword()
      );
      this.saveOrderServerRelation(orderNo, server.getId(), server.getServerName());
      this.settleAndFulfill(order, param.getMemberId(), settledAmount, "后台手动升级独立服务器防御", param);
      return CommonResult.data(this.buildResult(order, settledAmount));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<ManualOrderResult> renewIndependentOrder(ManualIndependentRenewOrderParam param) {
      return this.renewIndependentOrder(param, "后台手动续费独立服务器");
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<ManualOrderResult> renewIndependentOrder(ManualIndependentRenewOrderParam param, String settleRemark) {
      BaseMember member = this.requireMember(param.getMemberId());
      List<String> serverIds = RenewServerIdSupport.resolve(param.getServerId(), param.getServerIds());
      List<BaseIndependent> servers = new ArrayList<>();

      for (String serverId : serverIds) {
         BaseIndependent server = this.requireIndependentServer(serverId, param.getMemberId());
         this.cancelUnpaidOrders(server.getId());
         servers.add(server);
      }

      BaseIndependent firstServer = servers.get(0);
      this.validateRenewDuration(param.getDuration());
      IndependentRenewOrderParam renewParam = new IndependentRenewOrderParam();
      renewParam.setServerIds(serverIds);
      renewParam.setDuration(param.getDuration());
      IndependentCalculatePriceResponse priceResponse = this.requireData(this.independentCalculatePriceService.renew(renewParam), "独立服务器续费计价失败");
      String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
      BigDecimal settledAmount = this.resolveSettledAmount(param.getDeductMode(), param.getDeductAmount(), priceResponse.getPayPrice());
      Order order = this.buildBaseOrder(
         orderNo,
         member,
         ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
         OrderTypeEnum.RENEW.getCode(),
         null,
         priceResponse.getBillingMode(),
         priceResponse.getTotalPrice(),
         settledAmount,
         null
      );
      order.setUpstreamServerIds(String.join(",", serverIds));
      this.orderService.save(order);
      this.saveIndependentOrderConfig(
         orderNo,
         priceResponse,
         settledAmount,
         firstServer.getRegionId(),
         firstServer.getLineId(),
         firstServer.getSystemClassId(),
         firstServer.getSystemId(),
         firstServer.getSystemPassword()
      );

      for (BaseIndependent server : servers) {
         this.saveOrderServerRelation(orderNo, server.getId(), server.getServerName());
      }

      this.settleAndFulfill(order, param.getMemberId(), settledAmount, StrUtil.blankToDefault(settleRemark, "后台手动续费独立服务器"), param);
      return CommonResult.data(this.buildResult(order, settledAmount));
   }

   private BaseMember requireMember(Long memberId) {
      BaseMember member = this.memberApi.detail(memberId);
      if (member == null) {
         throw new CommonException("会员不存在", new Object[0]);
      } else {
         return member;
      }
   }

   private BaseServer requireCloudServer(String serverId, Long memberId) {
      BaseServer server = this.serverApi.detail(serverId);
      if (server == null) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else if (server.getMemberNo() != null && memberId != null && !memberId.toString().equals(server.getMemberNo())) {
         throw new CommonException("服务器不属于该会员", new Object[0]);
      } else if (BusinessStatusEnum.DELETE.getCode().equals(server.getBusinessStatus())) {
         throw new CommonException("已删除服务器不支持操作", new Object[0]);
      } else {
         return server;
      }
   }

   private BaseIndependent requireIndependentServer(String serverId, Long memberId) {
      BaseIndependent server = this.independentApi.getById(serverId);
      if (server == null) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else if (server.getMemberNo() != null && memberId != null && !memberId.equals(server.getMemberNo())) {
         throw new CommonException("服务器不属于该会员", new Object[0]);
      } else if (BusinessStatusEnum.DELETE.getCode().equals(server.getBusinessStatus())) {
         throw new CommonException("已删除服务器不支持操作", new Object[0]);
      } else {
         return server;
      }
   }

   private void saveCloudOrderConfig(
      String orderNo, String lineId, CalculatePriceResponse response, BigDecimal payPrice, String systemClassId, String systemId, String loginPassword
   ) {
      OrderServerConfigAddParam configParam = new OrderServerConfigAddParam();
      configParam.setOrderNo(orderNo);
      configParam.setLineId(lineId);
      configParam.setCpu(response.getCpu());
      configParam.setRam(response.getRam());
      configParam.setCoreDayPrice(response.getCoreDayPrice());
      configParam.setCoreMonthPrice(response.getCoreMonthPrice());
      configParam.setSystemDisk(response.getSystemDisk());
      configParam.setSystemDiskFreeSize(response.getSystemDiskFreeSize());
      configParam.setSystemDiskDayPrice(response.getSystemDiskDayPrice());
      configParam.setSystemDiskMonthPrice(response.getSystemDiskMonthPrice());
      configParam.setDataDisks(response.getDataDisks());
      configParam.setDataDisksFreeSize(response.getDataDisksFreeSize());
      configParam.setDataDisksDayPrice(response.getDataDisksDayPrice());
      configParam.setDataDisksMonthPrice(response.getDataDisksMonthPrice());
      configParam.setBandwidth(response.getBandwidth());
      configParam.setBandwidthFreeSize(response.getBandwidthFreeSize());
      configParam.setBandwidthDayPrice(response.getBandwidthDayPrice());
      configParam.setBandwidthMonthPrice(response.getBandwidthMonthPrice());
      configParam.setIpNum(response.getIpNum());
      configParam.setIpFreeSize(response.getIpFreeSize());
      configParam.setIpMonthPrice(response.getIpMonthPrice());
      configParam.setDefense(response.getDefense());
      configParam.setDefenseFreeSize(response.getDefenseFreeSize());
      configParam.setDefenseDayPrice(response.getDefenseDayPrice());
      configParam.setDefenseMonthPrice(response.getDefenseMonthPrice());
      configParam.setUnit(response.getUnit());
      configParam.setDuration(response.getDuration());
      configParam.setUnitPrice(response.getUnitPrice());
      configParam.setNumber(response.getNumber());
      configParam.setTotalPrice(response.getTotalPrice());
      configParam.setPayPrice(payPrice);
      configParam.setRenewPrice(response.getRenewPrice());
      configParam.setSystemClassId(systemClassId);
      configParam.setSystemId(systemId);
      configParam.setLoginPassword(loginPassword);
      this.orderServerConfigService.add(configParam);
   }

   private void saveIndependentOrderConfig(
      String orderNo,
      IndependentCalculatePriceResponse response,
      BigDecimal payPrice,
      String regionId,
      String lineId,
      String systemClassId,
      String systemId,
      String loginPassword
   ) {
      IndependentOrderServerConfig config = new IndependentOrderServerConfig();
      config.setId(SnowflakeIdGenerator.generateId());
      config.setOrderNo(orderNo);
      config.setRegionId(regionId);
      config.setLineId(lineId);
      config.setMachineId(response.getMachineId());
      config.setMachineName(response.getMachineName());
      config.setMachineMonthPrice(response.getMachineMonthPrice());
      config.setBandwidth(response.getBandwidth());
      config.setBandwidthFreeSize(response.getBandwidthFreeSize());
      config.setBandwidthMonthPrice(response.getBandwidthMonthPrice());
      config.setDefense(response.getDefense());
      config.setDefenseFreeSize(response.getDefenseFreeSize());
      config.setDefenseMonthPrice(response.getDefenseMonthPrice());
      config.setIpSize(response.getIpSize());
      config.setIpFreeSize(response.getIpFreeSize());
      config.setIpMonthPrice(response.getIpMonthPrice());
      config.setUnit(response.getUnit());
      config.setDuration(response.getDuration());
      config.setNumber(response.getNumber());
      config.setUnitPrice(response.getUnitPrice());
      config.setTotalPrice(response.getTotalPrice());
      config.setPayPrice(payPrice);
      config.setRemark(response.getTips());
      config.setSystemClassId(systemClassId);
      config.setSystemId(systemId);
      config.setLoginPassword(loginPassword);
      config.setRenewPrice(response.getRenewPrice());
      if (response.getMachineId() != null) {
         BaseAdminMachine machine = this.adminMachineApi.findMachineById(response.getMachineId());
         if (machine != null) {
            config.setMachineName(machine.getName());
            config.setType(machine.getType());
            config.setAutomation(machine.getAutomation());
            config.setCpuInfo(machine.getCpuInfo());
            config.setRamInfo(machine.getRamInfo());
            config.setDisksInfo(machine.getDisksInfo());
            config.setUpgrades(machine.getUpgrades());
         }
      }

      this.independentOrderServerConfigService.saveNew(config);
   }

   private void saveOrderServerRelation(String orderNo, String serverId, String serverName) {
      OrderServerAddParam relation = new OrderServerAddParam();
      relation.setOrderNo(orderNo);
      relation.setServerId(serverId);
      relation.setServerName(serverName);
      this.orderServerService.add(relation);
   }

   private void settleAndFulfill(Order order, Long memberId, BigDecimal settledAmount, String remark, Object requestParam) {
      Date payTime = new Date();
      if (settledAmount.compareTo(BigDecimal.ZERO) > 0) {
         this.deductBalance(memberId, settledAmount, order.getOrderNo(), remark, JSONUtil.toJsonStr(requestParam), payTime);
      }

      order.setOrderStatus(OrderStatusEnum.PAID.getCode());
      order.setPayTime(payTime);
      order.setPayType(PayTypeEnum.BALANCE.getCode());
      order.setUpdateTime(payTime);
      this.orderService.updateById(order);
      this.scheduleHandlePaidOrderAfterCommit(order.getOrderNo());
   }

   private void scheduleHandlePaidOrderAfterCommit(String orderNo) {
      if (TransactionSynchronizationManager.isSynchronizationActive()) {
         TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            public void afterCommit() {
               ((OrderService)SpringUtil.getBean(OrderService.class)).handlePaidOrderAsync(orderNo);
            }
         });
      } else {
         ((OrderService)SpringUtil.getBean(OrderService.class)).handlePaidOrderAsync(orderNo);
      }
   }

   private void deductBalance(Long memberId, BigDecimal amount, String orderNo, String remark, String requestBody, Date payTime) {
      BaseBalance balance = this.balanceApi.selectByMemberNo(memberId);
      if (balance == null) {
         throw new CommonException("账号余额记录不存在", new Object[0]);
      } else if (balance.getBalance().compareTo(amount) < 0) {
         throw new CommonException("账号余额不足，当前余额：" + balance.getBalance() + "，需要金额：" + amount, new Object[0]);
      } else {
         this.balanceApi.deductAndRecord(memberId, amount, orderNo, remark, requestBody, payTime);
      }
   }

   private BigDecimal resolveSettledAmount(Integer deductMode, BigDecimal manualAmount, BigDecimal autoAmount) {
      if (deductMode == null) {
         throw new CommonException("扣费模式不能为空", new Object[0]);
      } else if (1 == deductMode) {
         return BigDecimal.ZERO;
      } else if (2 == deductMode) {
         return this.defaultAmount(autoAmount);
      } else if (3 == deductMode) {
         if (manualAmount != null && manualAmount.compareTo(BigDecimal.ZERO) >= 0) {
            return manualAmount;
         } else {
            throw new CommonException("手动扣费金额不能为空且不能小于0", new Object[0]);
         }
      } else {
         throw new CommonException("不支持的扣费模式", new Object[0]);
      }
   }

   private Order buildBaseOrder(
      String orderNo,
      BaseMember member,
      Integer productType,
      Integer orderType,
      Integer upgradeItem,
      Integer billingMode,
      BigDecimal totalPrice,
      BigDecimal payPrice,
      String originalOrderNo
   ) {
      Order order = new Order();
      order.setId(SnowflakeIdGenerator.generateId());
      order.setOrderNo(orderNo);
      order.setBillingMode(billingMode);
      order.setProductType(productType);
      order.setOrderType(orderType);
      order.setUpgradeItem(upgradeItem);
      order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
      order.setMemberNo(member.getId());
      order.setMemberName(member.getName());
      order.setMemberPhone(member.getPhone());
      order.setTotalPrice(this.defaultAmount(totalPrice));
      order.setPayPrice(this.defaultAmount(payPrice));
      if (order.getTotalPrice().compareTo(order.getPayPrice()) >= 0) {
         order.setDiscountPrice(order.getTotalPrice().subtract(order.getPayPrice()));
      } else {
         order.setDiscountPrice(BigDecimal.ZERO);
      }

      order.setCouponPrice(BigDecimal.ZERO);
      order.setCouponUseCount(0);
      order.setOriginalOrderNo(originalOrderNo);
      order.setCreateTime(new Date());
      order.setUpdateTime(new Date());
      order.setPayType(PayTypeEnum.BALANCE.getCode());
      return order;
   }

   private void cancelUnpaidOrders(String serverId) {
      for (Order order : this.orderMapper.selectByServerId(serverId, OrderStatusEnum.UNPAID.getCode())) {
         order.setOrderStatus(OrderStatusEnum.CANCELLED.getCode());
         this.orderService.updateById(order);
         if (StrUtil.isNotBlank(order.getOrderNo())) {
            this.memberCouponApi.releaseCoupon(order.getOrderNo());
         }
      }
   }

   private <T> T requireData(CommonResult<T> result, String message) {
      if (result != null && result.getCode() == 200 && result.getData() != null) {
         return (T)result.getData();
      } else {
         throw new CommonException(result == null ? message : message + "：" + result.getMsg(), new Object[0]);
      }
   }

   private ManualOrderResult buildResult(Order order, BigDecimal settledAmount) {
      ManualOrderResult result = new ManualOrderResult();
      result.setOrderNo(order.getOrderNo());
      result.setMemberId(order.getMemberNo());
      result.setProductType(order.getProductType());
      result.setOrderType(order.getOrderType());
      result.setOrderStatus(OrderStatusEnum.PAID.getCode());
      result.setTotalPrice(order.getTotalPrice());
      result.setPayPrice(settledAmount);
      return result;
   }

   private BigDecimal defaultAmount(BigDecimal amount) {
      return amount == null ? BigDecimal.ZERO : amount;
   }

   private void validateRenewDuration(Integer duration) {
      if (duration == null || duration <= 0) {
         throw new CommonException("续费时长必须大于0", new Object[0]);
      }
   }

   private void validateCloudCoreUpgrade(BaseServer server, Integer targetCpu, Integer targetRam) {
      if (targetCpu < server.getCpu()) {
         throw new CommonException("升级的CPU不能小于当前服务器", new Object[0]);
      } else if (targetRam < server.getRam()) {
         throw new CommonException("升级的内存不能小于当前服务器", new Object[0]);
      } else if (Objects.equals(targetCpu, server.getCpu()) && Objects.equals(targetRam, server.getRam())) {
         throw new CommonException("升级的CPU和内存至少有一项大于当前服务器", new Object[0]);
      }
   }

   private void validateCloudDiskUpgrade(BaseServer server, Integer targetSystemDisk, List<Integer> targetDataDisks) {
      if (targetSystemDisk < server.getSystemDisk()) {
         throw new CommonException("升级的系统盘不能小于当前服务器", new Object[0]);
      } else {
         int currentDataDiskTotal = this.sumDisks(server.getDataDisks());
         int targetDataDiskTotal = this.sumDisks(targetDataDisks);
         if (targetDataDiskTotal < currentDataDiskTotal) {
            throw new CommonException("升级的数据盘总容量不能小于当前服务器", new Object[0]);
         } else if (Objects.equals(targetSystemDisk, server.getSystemDisk()) && targetDataDiskTotal == currentDataDiskTotal) {
            throw new CommonException("升级的系统盘和数据盘至少有一项大于当前服务器", new Object[0]);
         }
      }
   }

   private void validateCloudBandwidthUpgrade(BaseServer server, Integer targetBandwidth) {
      if (targetBandwidth <= server.getBandwidth()) {
         throw new CommonException("升级的带宽不能小于当前服务器", new Object[0]);
      }
   }

   private void validateCloudDefenseUpgrade(BaseServer server, Integer targetDefense) {
      if (targetDefense <= server.getDefense()) {
         throw new CommonException("升级的防御不能小于当前服务器", new Object[0]);
      }
   }

   private void validateIndependentIpUpgrade(BaseIndependent server, Integer targetIpSize) {
      if (targetIpSize < server.getIpSize()) {
         throw new CommonException("升级的IP数量不能小于当前服务器", new Object[0]);
      } else if (Objects.equals(targetIpSize, server.getIpSize())) {
         throw new CommonException("升级的IP数量必须大于当前服务器", new Object[0]);
      }
   }

   private void validateIndependentBandwidthUpgrade(BaseIndependent server, Integer targetBandwidth) {
      if (targetBandwidth <= server.getBandwidth()) {
         throw new CommonException("升级的带宽不能小于当前服务器", new Object[0]);
      }
   }

   private void validateIndependentDefenseUpgrade(BaseIndependent server, Integer targetDefense) {
      if (targetDefense <= server.getDefense()) {
         throw new CommonException("升级的防御不能小于当前服务器", new Object[0]);
      }
   }

   private int sumDisks(List<Integer> disks) {
      if (disks != null && !disks.isEmpty()) {
         int total = 0;

         for (Integer disk : disks) {
            if (disk != null) {
               total += disk;
            }
         }

         return total;
      } else {
         return 0;
      }
   }
}
