package com.rxinyun.member.order.modular.independent.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.business.api.MemberIndependentApi;
import com.rxinyun.business.core.pojo.BaseMemberIndependentServer;
import com.rxinyun.common.enums.UpgradeItemEnum;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.OrderNoGenerator;
import com.rxinyun.common.util.OsInstancePasswordValidator;
import com.rxinyun.common.util.RenewServerIdSupport;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.order.modular.coupon.OrderCouponSupportService;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import com.rxinyun.member.order.modular.ecs.mapper.MemberOrderMapper;
import com.rxinyun.member.order.modular.ecs.param.MemberOrderServerAddParam;
import com.rxinyun.member.order.modular.ecs.service.MemberOrderServerService;
import com.rxinyun.member.order.modular.independent.helper.MemberIndependentPriceCalculatorHelper;
import com.rxinyun.member.order.modular.independent.helper.MemberIndependentRenewCalculatorHelper;
import com.rxinyun.member.order.modular.independent.helper.MemberIndependentUpgradeCalculatorHelper;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentBandwidthUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentCalculatePriceParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentDefenseUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentIpUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentOrderServerConfigAddParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentRenewOrderParam;
import com.rxinyun.member.order.modular.independent.response.IndependentCalculatePriceResponse;
import com.rxinyun.member.order.modular.independent.service.MemberIndependentOrderServerConfigService;
import com.rxinyun.member.order.modular.independent.service.MemberIndependentOrderService;
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

@Service
public class MemberIndependentOrderServiceImpl extends ServiceImpl<MemberOrderMapper, MemberOrder> implements MemberIndependentOrderService {
   private static final Logger log = LoggerFactory.getLogger(MemberIndependentOrderServiceImpl.class);
   private static final String LOCKED_OPERATION_MESSAGE = "独立服务器已锁定，请先解锁后再操作";
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private AdminMachineApi adminMachineApi;
   @Resource
   private MemberIndependentApi memberIndependentApi;
   @Resource
   private MemberOrderMapper memberOrderMapper;
   @Resource
   private MemberOrderServerService memberOrderServerService;
   @Resource
   private MemberIndependentOrderServerConfigService memberIndependentOrderServerConfigService;
   @Resource
   private MemberIndependentPriceCalculatorHelper memberIndependentPriceCalculatorHelper;
   @Resource
   private MemberIndependentUpgradeCalculatorHelper independentUpgradeCalculatorHelper;
   @Resource
   private MemberIndependentRenewCalculatorHelper independentRenewCalculatorHelper;
   @Resource
   private OrderCouponSupportService orderCouponSupportService;

   private void assertIndependentNotLocked(BaseMemberIndependentServer server) {
      if (server != null && ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
         throw new CommonException("独立服务器已锁定，请先解锁后再操作", new Object[0]);
      }
   }

   @Transactional
   @Override
   public CommonResult<MemberOrder> create(MemberIndependentOrderParam orderParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      String requireRealStatus = this.devConfigApi.getValueByKey("RUNXIN_SYS_RECHARGE_REQUIRE_REAL_NAME");
      if (!ObjectUtil.isNotEmpty(requireRealStatus)
         || !requireRealStatus.equals("1")
         || !ObjectUtil.isEmpty(clientLoginUser.getRealNameVerified()) && clientLoginUser.getRealNameVerified().equals("1")) {
         if (orderParam.getNumber() == null || orderParam.getNumber() <= 0) {
            return CommonResult.error("购买数量必须大于0");
         } else if (orderParam.getDuration() != null && orderParam.getDuration() > 0) {
            String pwdMsg = OsInstancePasswordValidator.validate(orderParam.getLoginPassword());
            if (pwdMsg != null) {
               return CommonResult.error(pwdMsg);
            } else {
               String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
               MemberIndependentCalculatePriceParam calculatePriceParam = (MemberIndependentCalculatePriceParam)BeanUtil.toBean(
                  orderParam, MemberIndependentCalculatePriceParam.class
               );
               IndependentCalculatePriceResponse calculatePriceResponse = this.memberIndependentPriceCalculatorHelper.calculatePrice(calculatePriceParam);
               if (null == calculatePriceResponse) {
                  return CommonResult.error("订单计价失败~");
               } else {
                  this.orderCouponSupportService
                     .applyCoupon(
                        calculatePriceResponse,
                        clientLoginUser.getId(),
                        ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
                        OrderTypeEnum.NEW_PURCHASE.getCode(),
                        orderParam.getLineId(),
                        orderParam.getCouponCode()
                     );
                  MemberOrder order = new MemberOrder();
                  order.setOrderNo(orderNo);
                  order.setBillingMode(orderParam.getBillingMode());
                  order.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
                  order.setOrderType(OrderTypeEnum.NEW_PURCHASE.getCode());
                  order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
                  order.setMemberNo(clientLoginUser.getId());
                  order.setMemberName(clientLoginUser.getName());
                  order.setMemberPhone(clientLoginUser.getPhone());
                  order.setTotalPrice(calculatePriceResponse.getTotalPrice());
                  order.setPayPrice(calculatePriceResponse.getPayPrice());
                  this.fillCouponFields(
                     order,
                     calculatePriceResponse,
                     orderParam.getCouponCode(),
                     ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
                     OrderTypeEnum.NEW_PURCHASE.getCode(),
                     orderParam.getLineId(),
                     clientLoginUser.getId()
                  );
                  order.setCreateTime(new Date());
                  order.setUpdateTime(new Date());
                  this.save(order);
                  this.fillOrderServerAddParam(orderParam, calculatePriceResponse, orderNo);
                  return CommonResult.data(order);
               }
            }
         } else {
            return CommonResult.error("购买时长必须大于0");
         }
      } else {
         return CommonResult.error("请先实名认证");
      }
   }

   private void fillOrderServerAddParam(MemberIndependentOrderParam orderParam, IndependentCalculatePriceResponse priceResponse, String orderNo) {
      BaseAdminMachine machine = this.adminMachineApi.findMachineById(priceResponse.getMachineId());
      log.info("查询到的机型为: {}", JSONUtil.toJsonStr(machine));
      if (machine == null) {
         throw new CommonException("该机型不存在", new Object[0]);
      } else {
         log.info("获取机型成功, ID: {}, 机型名称: {}", machine.getId(), machine.getName());
         MemberIndependentOrderServerConfigAddParam orderServerConfigAddParam = new MemberIndependentOrderServerConfigAddParam();
         orderServerConfigAddParam.setRegionId(orderParam.getRegionId());
         orderServerConfigAddParam.setLineId(orderParam.getLineId());
         orderServerConfigAddParam.setOrderNo(orderNo);
         orderServerConfigAddParam.setCpuInfo(machine.getCpuInfo());
         orderServerConfigAddParam.setRamInfo(machine.getRamInfo());
         orderServerConfigAddParam.setDisksInfo(machine.getDisksInfo());
         orderServerConfigAddParam.setMachineId(priceResponse.getMachineId());
         orderServerConfigAddParam.setMachineName(priceResponse.getMachineName());
         orderServerConfigAddParam.setMachineMonthPrice(priceResponse.getMachineMonthPrice());
         orderServerConfigAddParam.setBandwidth(priceResponse.getBandwidth());
         orderServerConfigAddParam.setBandwidthFreeSize(priceResponse.getBandwidthFreeSize());
         orderServerConfigAddParam.setBandwidthMonthPrice(priceResponse.getBandwidthMonthPrice());
         orderServerConfigAddParam.setDefense(priceResponse.getDefense());
         orderServerConfigAddParam.setDefenseFreeSize(priceResponse.getDefenseFreeSize());
         orderServerConfigAddParam.setDefenseMonthPrice(priceResponse.getDefenseMonthPrice());
         orderServerConfigAddParam.setIpSize(priceResponse.getIpSize());
         orderServerConfigAddParam.setIpFreeSize(priceResponse.getIpFreeSize());
         orderServerConfigAddParam.setIpMonthPrice(priceResponse.getIpMonthPrice());
         orderServerConfigAddParam.setUnit(priceResponse.getUnit());
         orderServerConfigAddParam.setDuration(priceResponse.getDuration());
         orderServerConfigAddParam.setUnitPrice(priceResponse.getUnitPrice());
         orderServerConfigAddParam.setNumber(priceResponse.getNumber());
         orderServerConfigAddParam.setTotalPrice(priceResponse.getTotalPrice());
         orderServerConfigAddParam.setPayPrice(priceResponse.getPayPrice());
         orderServerConfigAddParam.setLoginPassword(orderParam.getLoginPassword());
         orderServerConfigAddParam.setSystemClassId(orderParam.getSystemClassId());
         orderServerConfigAddParam.setSystemId(orderParam.getSystemId());
         orderServerConfigAddParam.setRenewPrice(priceResponse.getRenewPrice());
         this.memberIndependentOrderServerConfigService.add(orderServerConfigAddParam);
      }
   }

   @Transactional
   @Override
   public CommonResult<MemberOrder> upgradeIp(MemberIndependentIpUpgradeOrderParam upgradeOrderParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      String requireRealStatus = this.devConfigApi.getValueByKey("RUNXIN_SYS_RECHARGE_REQUIRE_REAL_NAME");
      if (!ObjectUtil.isNotEmpty(requireRealStatus)
         || !requireRealStatus.equals("1")
         || !ObjectUtil.isEmpty(clientLoginUser.getRealNameVerified()) && clientLoginUser.getRealNameVerified().equals("1")) {
         BaseMemberIndependentServer baseMemberServer = this.memberIndependentApi.selectByServerId(upgradeOrderParam.getServerId());
         if (baseMemberServer == null) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertIndependentNotLocked(baseMemberServer);
            this.updateOrderStatusByServerId(baseMemberServer.getId());
            CommonResult<MemberOrder> ipValidation = this.validateIndependentIpUpgrade(baseMemberServer, upgradeOrderParam.getIpSize());
            if (ipValidation != null) {
               return ipValidation;
            } else {
               IndependentCalculatePriceResponse calculatePriceResponse = this.independentUpgradeCalculatorHelper.upgradeIpCalculatePrice(upgradeOrderParam);
               if (null == calculatePriceResponse) {
                  return CommonResult.error("订单计价失败~");
               } else {
                  this.orderCouponSupportService
                     .applyCoupon(
                        calculatePriceResponse,
                        clientLoginUser.getId(),
                        ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
                        OrderTypeEnum.UPGRADE.getCode(),
                        baseMemberServer.getLineId(),
                        upgradeOrderParam.getCouponCode()
                     );
                  String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
                  MemberOrder order = new MemberOrder();
                  order.setOrderNo(orderNo);
                  order.setBillingMode(calculatePriceResponse.getBillingMode());
                  order.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
                  order.setOrderType(OrderTypeEnum.UPGRADE.getCode());
                  order.setUpgradeItem(UpgradeItemEnum.UPGRADE_IP.getCode());
                  order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
                  order.setMemberNo(clientLoginUser.getId());
                  order.setMemberName(clientLoginUser.getName());
                  order.setMemberPhone(clientLoginUser.getPhone());
                  order.setTotalPrice(calculatePriceResponse.getTotalPrice());
                  order.setPayPrice(calculatePriceResponse.getPayPrice());
                  this.fillCouponFields(
                     order,
                     calculatePriceResponse,
                     upgradeOrderParam.getCouponCode(),
                     ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
                     OrderTypeEnum.UPGRADE.getCode(),
                     baseMemberServer.getLineId(),
                     clientLoginUser.getId()
                  );
                  order.setCreateTime(new Date());
                  order.setUpdateTime(new Date());
                  this.save(order);
                  this.fillOrderServerAddParam(baseMemberServer, calculatePriceResponse, orderNo);
                  MemberOrderServerAddParam orderServerAddParam = new MemberOrderServerAddParam();
                  orderServerAddParam.setOrderNo(order.getOrderNo());
                  orderServerAddParam.setServerId(baseMemberServer.getId());
                  orderServerAddParam.setServerName(baseMemberServer.getServerName());
                  this.memberOrderServerService.add(orderServerAddParam);
                  return CommonResult.data(order);
               }
            }
         }
      } else {
         return CommonResult.error("请先实名认证");
      }
   }

   @Transactional
   @Override
   public CommonResult<MemberOrder> upgradeBandwidth(MemberIndependentBandwidthUpgradeOrderParam upgradeOrderParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      String requireRealStatus = this.devConfigApi.getValueByKey("RUNXIN_SYS_RECHARGE_REQUIRE_REAL_NAME");
      if (!ObjectUtil.isNotEmpty(requireRealStatus)
         || !requireRealStatus.equals("1")
         || !ObjectUtil.isEmpty(clientLoginUser.getRealNameVerified()) && clientLoginUser.getRealNameVerified().equals("1")) {
         BaseMemberIndependentServer baseMemberServer = this.memberIndependentApi.selectByServerId(upgradeOrderParam.getServerId());
         if (baseMemberServer == null) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertIndependentNotLocked(baseMemberServer);
            this.updateOrderStatusByServerId(baseMemberServer.getId());
            CommonResult<MemberOrder> bandwidthValidation = this.validateIndependentBandwidthUpgrade(baseMemberServer, upgradeOrderParam.getBandwidth());
            if (bandwidthValidation != null) {
               return bandwidthValidation;
            } else {
               MemberIndependentBandwidthUpgradeOrderParam bandwidthUpgradeOrderParam = new MemberIndependentBandwidthUpgradeOrderParam();
               bandwidthUpgradeOrderParam.setBandwidth(upgradeOrderParam.getBandwidth());
               bandwidthUpgradeOrderParam.setServerId(upgradeOrderParam.getServerId());
               bandwidthUpgradeOrderParam.setPayType(upgradeOrderParam.getPayType());
               IndependentCalculatePriceResponse calculatePriceResponse = this.independentUpgradeCalculatorHelper
                  .upgradeBandwidthCalculatePrice(bandwidthUpgradeOrderParam);
               if (null == calculatePriceResponse) {
                  return CommonResult.error("订单计价失败~");
               } else {
                  this.orderCouponSupportService
                     .applyCoupon(
                        calculatePriceResponse,
                        clientLoginUser.getId(),
                        ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
                        OrderTypeEnum.UPGRADE.getCode(),
                        baseMemberServer.getLineId(),
                        upgradeOrderParam.getCouponCode()
                     );
                  String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
                  MemberOrder order = new MemberOrder();
                  order.setOrderNo(orderNo);
                  order.setBillingMode(calculatePriceResponse.getBillingMode());
                  order.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
                  order.setOrderType(OrderTypeEnum.UPGRADE.getCode());
                  order.setUpgradeItem(UpgradeItemEnum.UPGRADE_BANDWIDTH.getCode());
                  order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
                  order.setMemberNo(clientLoginUser.getId());
                  order.setMemberName(clientLoginUser.getName());
                  order.setMemberPhone(clientLoginUser.getPhone());
                  order.setTotalPrice(calculatePriceResponse.getTotalPrice());
                  order.setPayPrice(calculatePriceResponse.getPayPrice());
                  this.fillCouponFields(
                     order,
                     calculatePriceResponse,
                     upgradeOrderParam.getCouponCode(),
                     ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
                     OrderTypeEnum.UPGRADE.getCode(),
                     baseMemberServer.getLineId(),
                     clientLoginUser.getId()
                  );
                  order.setCreateTime(new Date());
                  order.setUpdateTime(new Date());
                  this.save(order);
                  this.fillOrderServerAddParam(baseMemberServer, calculatePriceResponse, orderNo);
                  MemberOrderServerAddParam orderServerAddParam = new MemberOrderServerAddParam();
                  orderServerAddParam.setOrderNo(order.getOrderNo());
                  orderServerAddParam.setServerId(baseMemberServer.getId());
                  orderServerAddParam.setServerName(baseMemberServer.getServerName());
                  this.memberOrderServerService.add(orderServerAddParam);
                  return CommonResult.data(order);
               }
            }
         }
      } else {
         return CommonResult.error("请先实名认证");
      }
   }

   @Transactional
   @Override
   public CommonResult<MemberOrder> upgradeDefense(MemberIndependentDefenseUpgradeOrderParam upgradeOrderParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      String requireRealStatus = this.devConfigApi.getValueByKey("RUNXIN_SYS_RECHARGE_REQUIRE_REAL_NAME");
      if (!ObjectUtil.isNotEmpty(requireRealStatus)
         || !requireRealStatus.equals("1")
         || !ObjectUtil.isEmpty(clientLoginUser.getRealNameVerified()) && clientLoginUser.getRealNameVerified().equals("1")) {
         BaseMemberIndependentServer baseMemberServer = this.memberIndependentApi.selectByServerId(upgradeOrderParam.getServerId());
         if (baseMemberServer == null) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertIndependentNotLocked(baseMemberServer);
            this.updateOrderStatusByServerId(baseMemberServer.getId());
            CommonResult<MemberOrder> defenseValidation = this.validateIndependentDefenseUpgrade(baseMemberServer, upgradeOrderParam.getDefense());
            if (defenseValidation != null) {
               return defenseValidation;
            } else {
               MemberIndependentDefenseUpgradeOrderParam defenseUpgradeOrderParam = new MemberIndependentDefenseUpgradeOrderParam();
               defenseUpgradeOrderParam.setDefense(upgradeOrderParam.getDefense());
               defenseUpgradeOrderParam.setServerId(upgradeOrderParam.getServerId());
               defenseUpgradeOrderParam.setPayType(upgradeOrderParam.getPayType());
               IndependentCalculatePriceResponse calculatePriceResponse = this.independentUpgradeCalculatorHelper
                  .upgradeDefenseCalculatePrice(defenseUpgradeOrderParam);
               if (null == calculatePriceResponse) {
                  return CommonResult.error("订单计价失败~");
               } else {
                  this.orderCouponSupportService
                     .applyCoupon(
                        calculatePriceResponse,
                        clientLoginUser.getId(),
                        ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
                        OrderTypeEnum.UPGRADE.getCode(),
                        baseMemberServer.getLineId(),
                        upgradeOrderParam.getCouponCode()
                     );
                  String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
                  MemberOrder order = new MemberOrder();
                  order.setOrderNo(orderNo);
                  order.setBillingMode(calculatePriceResponse.getBillingMode());
                  order.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
                  order.setOrderType(OrderTypeEnum.UPGRADE.getCode());
                  order.setUpgradeItem(UpgradeItemEnum.UPGRADE_DEFENSE.getCode());
                  order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
                  order.setMemberNo(clientLoginUser.getId());
                  order.setMemberName(clientLoginUser.getName());
                  order.setMemberPhone(clientLoginUser.getPhone());
                  order.setTotalPrice(calculatePriceResponse.getTotalPrice());
                  order.setPayPrice(calculatePriceResponse.getPayPrice());
                  this.fillCouponFields(
                     order,
                     calculatePriceResponse,
                     upgradeOrderParam.getCouponCode(),
                     ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
                     OrderTypeEnum.UPGRADE.getCode(),
                     baseMemberServer.getLineId(),
                     clientLoginUser.getId()
                  );
                  order.setCreateTime(new Date());
                  order.setUpdateTime(new Date());
                  this.save(order);
                  this.fillOrderServerAddParam(baseMemberServer, calculatePriceResponse, orderNo);
                  MemberOrderServerAddParam orderServerAddParam = new MemberOrderServerAddParam();
                  orderServerAddParam.setOrderNo(order.getOrderNo());
                  orderServerAddParam.setServerId(baseMemberServer.getId());
                  orderServerAddParam.setServerName(baseMemberServer.getServerName());
                  this.memberOrderServerService.add(orderServerAddParam);
                  return CommonResult.data(order);
               }
            }
         }
      } else {
         return CommonResult.error("请先实名认证");
      }
   }

   @Transactional
   @Override
   public CommonResult<MemberOrder> renew(MemberIndependentRenewOrderParam renewOrderParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      String requireRealStatus = this.devConfigApi.getValueByKey("RUNXIN_SYS_RECHARGE_REQUIRE_REAL_NAME");
      if (!ObjectUtil.isNotEmpty(requireRealStatus)
         || !requireRealStatus.equals("1")
         || !ObjectUtil.isEmpty(clientLoginUser.getRealNameVerified()) && clientLoginUser.getRealNameVerified().equals("1")) {
         List<String> serverIds = RenewServerIdSupport.resolve(renewOrderParam.getServerId(), renewOrderParam.getServerIds());
         List<BaseMemberIndependentServer> servers = new ArrayList<>();

         for (String serverId : serverIds) {
            BaseMemberIndependentServer server = this.memberIndependentApi.selectByServerId(serverId);
            if (server == null) {
               return CommonResult.error("服务器不存在");
            }

            this.assertIndependentNotLocked(server);
            if (server.getBusinessStatus().equals(BusinessStatusEnum.DELETE.getCode())) {
               return CommonResult.error("已删除服务器不支持续费");
            }

            this.updateOrderStatusByServerId(server.getId());
            servers.add(server);
         }

         BaseMemberIndependentServer firstServer = servers.get(0);
         if (renewOrderParam.getDuration() != null && renewOrderParam.getDuration() > 0) {
            IndependentCalculatePriceResponse calculatePriceResponse = this.independentRenewCalculatorHelper.renewCalculatePrice(renewOrderParam);
            if (null == calculatePriceResponse) {
               return CommonResult.error("订单计价失败~");
            } else {
               String couponLineId = this.independentRenewCalculatorHelper.resolveCouponLineId(calculatePriceResponse, renewOrderParam.getCouponCode());
               this.orderCouponSupportService
                  .applyCoupon(
                     calculatePriceResponse,
                     clientLoginUser.getId(),
                     ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
                     OrderTypeEnum.RENEW.getCode(),
                     couponLineId,
                     renewOrderParam.getCouponCode()
                  );
               String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
               MemberOrder order = new MemberOrder();
               order.setOrderNo(orderNo);
               order.setBillingMode(calculatePriceResponse.getBillingMode());
               order.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
               order.setOrderType(OrderTypeEnum.RENEW.getCode());
               order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
               order.setMemberNo(clientLoginUser.getId());
               order.setMemberName(clientLoginUser.getName());
               order.setMemberPhone(clientLoginUser.getPhone());
               order.setTotalPrice(calculatePriceResponse.getTotalPrice());
               order.setPayPrice(calculatePriceResponse.getPayPrice());
               this.fillCouponFields(
                  order,
                  calculatePriceResponse,
                  renewOrderParam.getCouponCode(),
                  ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
                  OrderTypeEnum.RENEW.getCode(),
                  couponLineId,
                  clientLoginUser.getId()
               );
               order.setCreateTime(new Date());
               order.setUpdateTime(new Date());
               this.save(order);
               this.fillOrderServerAddParam(firstServer, calculatePriceResponse, orderNo);

               for (BaseMemberIndependentServer serverx : servers) {
                  MemberOrderServerAddParam orderServerAddParam = new MemberOrderServerAddParam();
                  orderServerAddParam.setOrderNo(order.getOrderNo());
                  orderServerAddParam.setServerId(serverx.getId());
                  orderServerAddParam.setServerName(serverx.getServerName());
                  this.memberOrderServerService.add(orderServerAddParam);
               }

               return CommonResult.data(order);
            }
         } else {
            return CommonResult.error("续费时长必须大于0");
         }
      } else {
         return CommonResult.error("请先实名认证");
      }
   }

   private void fillOrderServerAddParam(BaseMemberIndependentServer baseMemberServer, IndependentCalculatePriceResponse priceResponse, String orderNo) {
      MemberIndependentOrderServerConfigAddParam orderServerConfigAddParam = new MemberIndependentOrderServerConfigAddParam();
      orderServerConfigAddParam.setLineId(baseMemberServer.getLineId());
      orderServerConfigAddParam.setOrderNo(orderNo);
      orderServerConfigAddParam.setCpuInfo(baseMemberServer.getCpuInfo());
      orderServerConfigAddParam.setRamInfo(baseMemberServer.getRamInfo());
      orderServerConfigAddParam.setDisksInfo(baseMemberServer.getDisksInfo());
      orderServerConfigAddParam.setMachineId(priceResponse.getMachineId());
      orderServerConfigAddParam.setMachineName(priceResponse.getMachineName());
      orderServerConfigAddParam.setMachineMonthPrice(priceResponse.getMachineMonthPrice());
      orderServerConfigAddParam.setBandwidth(priceResponse.getBandwidth());
      orderServerConfigAddParam.setBandwidthFreeSize(priceResponse.getBandwidthFreeSize());
      orderServerConfigAddParam.setBandwidthMonthPrice(priceResponse.getBandwidthMonthPrice());
      orderServerConfigAddParam.setDefense(priceResponse.getDefense());
      orderServerConfigAddParam.setDefenseFreeSize(priceResponse.getDefenseFreeSize());
      orderServerConfigAddParam.setDefenseMonthPrice(priceResponse.getDefenseMonthPrice());
      orderServerConfigAddParam.setIpSize(priceResponse.getIpSize());
      orderServerConfigAddParam.setIpFreeSize(priceResponse.getIpFreeSize());
      orderServerConfigAddParam.setIpMonthPrice(priceResponse.getIpMonthPrice());
      orderServerConfigAddParam.setUnit(priceResponse.getUnit());
      orderServerConfigAddParam.setDuration(priceResponse.getDuration());
      orderServerConfigAddParam.setUnitPrice(priceResponse.getUnitPrice());
      orderServerConfigAddParam.setNumber(priceResponse.getNumber());
      orderServerConfigAddParam.setTotalPrice(priceResponse.getTotalPrice());
      orderServerConfigAddParam.setPayPrice(priceResponse.getPayPrice());
      orderServerConfigAddParam.setLoginPassword(baseMemberServer.getSystemPassword());
      orderServerConfigAddParam.setSystemClassId(baseMemberServer.getSystemClassId());
      orderServerConfigAddParam.setSystemId(baseMemberServer.getSystemId());
      orderServerConfigAddParam.setRenewPrice(priceResponse.getRenewPrice());
      this.memberIndependentOrderServerConfigService.add(orderServerConfigAddParam);
   }

   public void updateOrderStatusByServerId(String serverId) {
      for (MemberOrder memberOrder : this.memberOrderMapper.selectByServerId(serverId, OrderStatusEnum.UNPAID.getCode())) {
         if (memberOrder.getOrderStatus().equals(OrderStatusEnum.UNPAID.getCode())) {
            memberOrder.setOrderStatus(OrderStatusEnum.CANCELLED.getCode());
            this.memberOrderMapper.updateById(memberOrder);
            this.orderCouponSupportService.releaseCoupon(memberOrder.getOrderNo());
         }
      }
   }

   private void fillCouponFields(
      MemberOrder order,
      IndependentCalculatePriceResponse priceResponse,
      String couponCode,
      Integer productType,
      Integer orderType,
      String lineId,
      Long memberNo
   ) {
      order.setDiscountPrice((BigDecimal)ObjectUtil.defaultIfNull(priceResponse.getDiscountPrice(), BigDecimal.ZERO));
      order.setCouponPrice((BigDecimal)ObjectUtil.defaultIfNull(priceResponse.getCouponPrice(), BigDecimal.ZERO));
      order.setCouponUseCount(StrUtil.isBlank(couponCode) ? 0 : 1);
      this.orderCouponSupportService
         .lockCoupon(
            order,
            memberNo,
            productType,
            orderType,
            lineId,
            couponCode,
            this.orderCouponSupportService.resolveOrderAmountBeforeCoupon(priceResponse.getPayPrice(), priceResponse.getCouponPrice())
         );
   }

   private CommonResult<MemberOrder> validateIndependentIpUpgrade(BaseMemberIndependentServer server, Integer targetIpSize) {
      if (targetIpSize < server.getIpSize()) {
         return CommonResult.error("升级的IP数量不能小于当前服务器");
      } else {
         return Objects.equals(targetIpSize, server.getIpSize()) ? CommonResult.error("升级的IP数量必须大于当前服务器") : null;
      }
   }

   private CommonResult<MemberOrder> validateIndependentBandwidthUpgrade(BaseMemberIndependentServer server, Integer targetBandwidth) {
      return targetBandwidth <= server.getBandwidth() ? CommonResult.error("升级的带宽不能小于当前服务器") : null;
   }

   private CommonResult<MemberOrder> validateIndependentDefenseUpgrade(BaseMemberIndependentServer server, Integer targetDefense) {
      return targetDefense <= server.getDefense() ? CommonResult.error("升级的防御不能小于当前服务器") : null;
   }
}
