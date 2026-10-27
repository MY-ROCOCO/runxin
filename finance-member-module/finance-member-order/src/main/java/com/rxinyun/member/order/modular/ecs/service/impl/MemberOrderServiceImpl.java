package com.rxinyun.member.order.modular.ecs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.admin.business.modular.esc.service.ServerService;
import com.rxinyun.admin.dev.modular.api.entity.ResourcePoolRequest;
import com.rxinyun.admin.dev.modular.api.handler.ResourcePoolRequestHandler;
import com.rxinyun.admin.dev.modular.api.service.ResourcePoolRequestService;
import com.rxinyun.admin.production.modular.esc.cloud.entity.ServerLocalLine;
import com.rxinyun.admin.production.modular.esc.cloud.service.ServerLocalLineService;
import com.rxinyun.admin.production.modular.independent.machine.entity.Machine;
import com.rxinyun.admin.production.modular.independent.machine.service.MachineService;
import com.rxinyun.agent.modular.service.AgentCommissionService;
import com.rxinyun.business.api.BusinessDomainApi;
import com.rxinyun.business.api.MemberIndependentApi;
import com.rxinyun.business.api.MemberServerApi;
import com.rxinyun.business.api.MemberVhostApi;
import com.rxinyun.business.core.pojo.BaseMemberAddServer;
import com.rxinyun.business.core.pojo.BaseMemberIndependentAddServer;
import com.rxinyun.business.core.pojo.BaseMemberIndependentServer;
import com.rxinyun.business.core.pojo.BaseMemberServer;
import com.rxinyun.business.core.pojo.BaseMemberVhost;
import com.rxinyun.business.core.pojo.BaseMemberVhostCreate;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.NotificationTypeEnum;
import com.rxinyun.common.enums.ResourcePoolRequestStatusEnum;
import com.rxinyun.common.enums.UpgradeItemEnum;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.CloudServerPortUtil;
import com.rxinyun.common.util.OrderNoGenerator;
import com.rxinyun.common.util.OsInstancePasswordValidator;
import com.rxinyun.common.util.RenewServerIdSupport;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import com.rxinyun.common.util.UniqueHostnameGenerator;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.dev.api.ToolsNotificationApi;
import com.rxinyun.marketing.api.dto.CouponCalculateResponse;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.order.modular.coupon.OrderCouponSupportService;
import com.rxinyun.member.order.modular.dns.entity.DnsOrderConfig;
import com.rxinyun.member.order.modular.dns.service.DnsOrderConfigService;
import com.rxinyun.member.order.modular.domain.entity.DomainOrderItem;
import com.rxinyun.member.order.modular.domain.entity.DomainRegistrationRequest;
import com.rxinyun.member.order.modular.domain.param.DomainOrderCreateParam;
import com.rxinyun.member.order.modular.domain.param.DomainOrderItemParam;
import com.rxinyun.member.order.modular.domain.service.DomainOrderItemService;
import com.rxinyun.member.order.modular.domain.service.DomainRegistrationRequestService;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrderServer;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrderServerConfig;
import com.rxinyun.member.order.modular.ecs.helper.MemberPriceCalculatorHelper;
import com.rxinyun.member.order.modular.ecs.helper.MemberRenewCalculatorHelper;
import com.rxinyun.member.order.modular.ecs.helper.MemberUpgradeCalculatorHelper;
import com.rxinyun.member.order.modular.ecs.mapper.MemberOrderMapper;
import com.rxinyun.member.order.modular.ecs.param.CalculatePriceParam;
import com.rxinyun.member.order.modular.ecs.param.MemberBandwidthUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberCoreUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberDefenseUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberDiskUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberOrderIdParam;
import com.rxinyun.member.order.modular.ecs.param.MemberOrderPageParam;
import com.rxinyun.member.order.modular.ecs.param.MemberOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberOrderServerAddParam;
import com.rxinyun.member.order.modular.ecs.param.MemberOrderServerConfigAddParam;
import com.rxinyun.member.order.modular.ecs.param.MemberPayParam;
import com.rxinyun.member.order.modular.ecs.param.MemberRenewOrderParam;
import com.rxinyun.member.order.modular.ecs.response.MemberCalculatePriceResponse;
import com.rxinyun.member.order.modular.ecs.response.MemberOrderResponse;
import com.rxinyun.member.order.modular.ecs.response.MemberServerConfigResponse;
import com.rxinyun.member.order.modular.ecs.service.MemberOrderServerConfigService;
import com.rxinyun.member.order.modular.ecs.service.MemberOrderServerService;
import com.rxinyun.member.order.modular.ecs.service.MemberOrderService;
import com.rxinyun.member.order.modular.independent.entity.MemberIndependentOrderServerConfig;
import com.rxinyun.member.order.modular.independent.helper.MemberIndependentRenewCalculatorHelper;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentRenewOrderParam;
import com.rxinyun.member.order.modular.independent.response.IndependentCalculatePriceResponse;
import com.rxinyun.member.order.modular.independent.response.IndependentOrderServerConfigResponse;
import com.rxinyun.member.order.modular.independent.service.MemberIndependentOrderServerConfigService;
import com.rxinyun.member.order.modular.invoice.service.MemberOrderInvoiceService;
import com.rxinyun.member.order.modular.vhost.entity.VhostOrderConfig;
import com.rxinyun.member.order.modular.vhost.service.VhostOrderConfigService;
import com.rxinyun.member.production.modular.domain.client.XinwangDomainApiClient.DomainCreateResult;
import com.rxinyun.member.production.modular.domain.entity.DomainTldPrice;
import com.rxinyun.member.production.modular.domain.service.MemberDomainRegisterService;
import com.rxinyun.member.production.modular.domain.service.MemberDomainTldPriceService;
import com.rxinyun.member.production.modular.domain.template.entity.DomainTemplate;
import com.rxinyun.member.production.modular.domain.template.mapper.MemberDomainTemplateMapper;
import com.rxinyun.payments.api.PaymentsApi;
import com.rxinyun.production.api.AdminMachineApi;
import com.rxinyun.production.api.DnsPackageInstanceApi;
import com.rxinyun.production.api.IndependentLineApi;
import com.rxinyun.production.api.IndependentRegionApi;
import com.rxinyun.production.api.MemberIndependentSystemVersionApi;
import com.rxinyun.production.api.MemberLocalLineApi;
import com.rxinyun.production.api.MemberSystemVersionApi;
import com.rxinyun.production.api.dto.DnsPackageInstanceCreateParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceRenewParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceUpgradeQuotaParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceVO;
import com.rxinyun.production.core.pojo.BaseAdminMachine;
import com.rxinyun.production.core.pojo.BaseIndependentLine;
import com.rxinyun.production.core.pojo.BaseIndependentRegion;
import com.rxinyun.production.core.pojo.BaseMemberIndependentSystemVersion;
import com.rxinyun.production.core.pojo.BaseMemberLocalLine;
import com.rxinyun.production.core.pojo.BaseMemberSystemVersion;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class MemberOrderServiceImpl extends ServiceImpl<MemberOrderMapper, MemberOrder> implements MemberOrderService {
   private static final Logger log = LoggerFactory.getLogger(MemberOrderServiceImpl.class);
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private MemberLocalLineApi memberLocalLineApi;
   @Resource
   private MemberOrderMapper memberOrderMapper;
   @Resource
   private MemberOrderServerService memberOrderServerService;
   @Resource
   private MemberOrderServerConfigService orderServerConfigService;
   @Resource
   private MemberIndependentOrderServerConfigService memberIndependentOrderServerConfigService;
   @Resource
   private PaymentsApi paymentsApi;
   @Resource
   private MemberSystemVersionApi memberOsVersionApi;
   @Resource
   private MemberIndependentSystemVersionApi memberIndependentSystemVersionApi;
   @Resource
   private MemberServerApi memberServerApi;
   @Resource
   private MemberIndependentApi memberIndependentApi;
   @Resource
   private MemberPriceCalculatorHelper memberPriceCalculatorHelper;
   @Resource
   private MemberUpgradeCalculatorHelper memberUpgradeCalculatorHelper;
   @Resource
   private MemberRenewCalculatorHelper memberRenewCalculatorHelper;
   @Resource
   private MemberIndependentRenewCalculatorHelper memberIndependentRenewCalculatorHelper;
   @Resource
   private IndependentLineApi independentLineApi;
   @Resource
   private IndependentRegionApi independentRegionApi;
   @Autowired
   private ToolsNotificationApi toolsNotificationApi;
   @Resource
   private ResourcePoolRequestService resourcePoolRequestService;
   @Resource
   private AdminMachineApi adminMachineApi;
   @Resource
   private ServerLocalLineService serverLocalLineService;
   @Resource
   private ServerService serverService;
   @Resource
   private MachineService machineService;
   @Resource
   private ResourcePoolRequestHandler resourcePoolRequestHandler;
   @Resource(
      name = "orderTaskExecutor"
   )
   private Executor orderTaskExecutor;
   @Resource(
      name = "notificationTaskExecutor"
   )
   private Executor notificationTaskExecutor;
   @Resource(
      name = "resourcePoolTaskExecutor"
   )
   private Executor resourcePoolTaskExecutor;
   @Resource
   private DomainOrderItemService domainOrderItemService;
   @Resource
   private DomainRegistrationRequestService domainRegistrationRequestService;
   @Resource
   private MemberDomainRegisterService memberDomainRegisterService;
   @Resource
   private MemberDomainTldPriceService memberDomainTldPriceService;
   @Resource
   private OrderCouponSupportService orderCouponSupportService;
   @Resource
   private MemberDomainTemplateMapper memberDomainTemplateMapper;
   @Resource
   private BusinessDomainApi businessDomainApi;
   @Resource
   private DnsOrderConfigService dnsOrderConfigService;
   @Resource
   private VhostOrderConfigService vhostOrderConfigService;
   @Resource
   private MemberVhostApi memberVhostApi;
   @Resource
   private DnsPackageInstanceApi dnsPackageInstanceApi;
   @Resource
   private AgentCommissionService agentCommissionService;
   @Resource
   private MemberOrderInvoiceService memberOrderInvoiceService;

   @Override
   public Page<MemberOrder> page(MemberOrderPageParam pageParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      QueryWrapper<MemberOrder> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(MemberOrder::getMemberNo, clientLoginUser.getId());
      if (ObjectUtil.isNotEmpty(pageParam.getOrderNo())) {
         queryWrapper.lambda().and(q -> q.eq(MemberOrder::getOrderNo, pageParam.getOrderNo()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getOrderStatus())) {
         queryWrapper.lambda().and(q -> q.eq(MemberOrder::getOrderStatus, pageParam.getOrderStatus()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getPayType())) {
         queryWrapper.lambda().and(q -> q.eq(MemberOrder::getPayType, pageParam.getPayType()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(MemberOrder::getCreateTime);
      }

      Page<MemberOrder> page = (Page<MemberOrder>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      this.fillInvoiceStatus(page.getRecords());
      return page;
   }

   @Override
   public CommonResult<MemberOrderResponse> detail(MemberOrderIdParam orderIdParam) {
      MemberOrder order = (MemberOrder)this.getOne((Wrapper<MemberOrder>)new LambdaQueryWrapper<MemberOrder>().eq(MemberOrder::getOrderNo, orderIdParam.getOrderNo()));
      if (ObjectUtil.isNull(order)) {
         return CommonResult.error("订单不存在");
      } else {
         MemberOrderResponse orderResponse = (MemberOrderResponse)JSONUtil.toBean(JSONUtil.toJsonStr(order), MemberOrderResponse.class);
         if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(orderResponse.getProductType())) {
            MemberServerConfigResponse memberServerConfigResponse = this.orderServerConfigService.queryByOrder(order.getOrderNo());
            if (null == memberServerConfigResponse) {
               return CommonResult.error("云服务器订单详情不存在");
            }

            orderResponse.setMemberServerConfig(memberServerConfigResponse);
         } else if (ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(orderResponse.getProductType())) {
            IndependentOrderServerConfigResponse memberIndependentOrderServerConfig = this.memberIndependentOrderServerConfigService
               .queryByOrder(order.getOrderNo());
            if (null == memberIndependentOrderServerConfig) {
               return CommonResult.error("独立服务器订单详情不存在");
            }

            orderResponse.setMemberIndependentOrderServerConfig(memberIndependentOrderServerConfig);
         } else if (ProductTypeEnum.DOMAIN.getCode().equals(orderResponse.getProductType())) {
            List<DomainOrderItem> domainItems = this.domainOrderItemService.listByOrderNo(order.getOrderNo());
            orderResponse.setDomainOrderItems(domainItems);
         } else if (ProductTypeEnum.DNS.getCode().equals(orderResponse.getProductType())) {
            DnsOrderConfig dnsOrderConfig = this.dnsOrderConfigService.queryByOrderNo(order.getOrderNo());
            if (dnsOrderConfig == null) {
               return CommonResult.error("DNS云解析订单详情不存在");
            }

            DnsPackageInstanceVO packageInstance = this.dnsPackageInstanceApi.getInstanceBySourceOrderNo(order.getOrderNo());
            if (packageInstance == null && StrUtil.isNotBlank(dnsOrderConfig.getInstanceId())) {
               packageInstance = this.dnsPackageInstanceApi.getMemberInstance(dnsOrderConfig.getInstanceId(), String.valueOf(order.getMemberNo()));
            }

            dnsOrderConfig.setPackageInstance(packageInstance);
            orderResponse.setDnsOrderConfig(dnsOrderConfig);
         } else if (ProductTypeEnum.VHOST.getCode().equals(orderResponse.getProductType())) {
            VhostOrderConfig vhostOrderConfig = this.vhostOrderConfigService.queryByOrderNo(order.getOrderNo());
            if (vhostOrderConfig == null) {
               return CommonResult.error("虚拟主机订单详情不存在");
            }

            orderResponse.setVhostOrderConfig(vhostOrderConfig);
            this.fillVhostHostMap(vhostOrderConfig, order.getOrderNo(), order.getMemberNo());
         }

         Map<String, Integer> invoiceStatusMap = this.memberOrderInvoiceService.findOccupiedStatusByOrderNos(Collections.singletonList(order.getOrderNo()));
         orderResponse.setInvoiceStatus(invoiceStatusMap.get(order.getOrderNo()));
         return CommonResult.data(orderResponse);
      }
   }

   private void fillVhostHostMap(VhostOrderConfig config, String orderNo, Long memberNo) {
      Map<String, String> hostMap = new LinkedHashMap<>();
      List<BaseMemberVhost> hosts = this.memberVhostApi.listByOrderNo(orderNo);
      if (hosts != null) {
         for (BaseMemberVhost host : hosts) {
            if (host != null && StrUtil.isNotBlank(host.getId())) {
               hostMap.put(host.getId(), host.getHostName());
            }
         }
      }

      if (hostMap.isEmpty() && StrUtil.isNotBlank(config.getHostId()) && memberNo != null) {
         BaseMemberVhost hostx = this.memberVhostApi.selectByIdAndMemberNo(config.getHostId(), String.valueOf(memberNo));
         if (hostx != null && StrUtil.isNotBlank(hostx.getId())) {
            hostMap.put(hostx.getId(), hostx.getHostName());
         }
      }

      config.setHostMap(hostMap);
   }

   private void fillInvoiceStatus(List<MemberOrder> orders) {
      if (orders != null && !orders.isEmpty()) {
         List<String> orderNos = orders.stream().map(MemberOrder::getOrderNo).filter(CharSequenceUtil::isNotBlank).collect(Collectors.toList());
         Map<String, Integer> invoiceStatusMap = this.memberOrderInvoiceService.findOccupiedStatusByOrderNos(orderNos);

         for (MemberOrder order : orders) {
            order.setInvoiceStatus(invoiceStatusMap.get(order.getOrderNo()));
         }
      }
   }

   @Transactional
   @Override
   public CommonResult<MemberOrder> create(MemberOrderParam orderParam) {
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
               String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.CLOUD_SERVER.getCode());
               CalculatePriceParam calculatePriceParam = (CalculatePriceParam)BeanUtil.toBean(orderParam, CalculatePriceParam.class);
               MemberCalculatePriceResponse MemberCalculatePriceResponse = this.memberPriceCalculatorHelper.calculatePrice(calculatePriceParam);
               if (null == MemberCalculatePriceResponse) {
                  return CommonResult.error("订单计价失败~");
               } else {
                  this.orderCouponSupportService
                     .applyCoupon(
                        MemberCalculatePriceResponse,
                        clientLoginUser.getId(),
                        ProductTypeEnum.CLOUD_SERVER.getCode(),
                        OrderTypeEnum.NEW_PURCHASE.getCode(),
                        orderParam.getLineId(),
                        orderParam.getCouponCode()
                     );
                  MemberOrder order = new MemberOrder();
                  order.setOrderNo(orderNo);
                  order.setBillingMode(orderParam.getBillingMode());
                  order.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
                  order.setOrderType(OrderTypeEnum.NEW_PURCHASE.getCode());
                  order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
                  order.setMemberNo(clientLoginUser.getId());
                  order.setMemberName(clientLoginUser.getName());
                  order.setMemberPhone(clientLoginUser.getPhone());
                  order.setTotalPrice(MemberCalculatePriceResponse.getTotalPrice());
                  order.setPayPrice(MemberCalculatePriceResponse.getPayPrice());
                  this.fillCouponFields(
                     order,
                     MemberCalculatePriceResponse,
                     orderParam.getCouponCode(),
                     ProductTypeEnum.CLOUD_SERVER.getCode(),
                     OrderTypeEnum.NEW_PURCHASE.getCode(),
                     orderParam.getLineId(),
                     clientLoginUser.getId()
                  );
                  order.setCreateTime(new Date());
                  order.setUpdateTime(new Date());
                  order.setPayType(orderParam.getPayType());
                  this.save(order);
                  this.fillOrderServerAddParam(orderParam, MemberCalculatePriceResponse, orderNo);
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

   private void fillOrderServerAddParam(MemberOrderParam orderParam, MemberCalculatePriceResponse priceResponse, String orderNo) {
      MemberOrderServerConfigAddParam orderServerConfigAddParam = new MemberOrderServerConfigAddParam();
      orderServerConfigAddParam.setOrderNo(orderNo);
      orderServerConfigAddParam.setLineId(orderParam.getLineId());
      orderServerConfigAddParam.setCpu(priceResponse.getCpu());
      orderServerConfigAddParam.setRam(priceResponse.getRam());
      orderServerConfigAddParam.setCoreDayPrice(priceResponse.getCoreDayPrice());
      orderServerConfigAddParam.setCoreMonthPrice(priceResponse.getCoreMonthPrice());
      orderServerConfigAddParam.setSystemDisk(priceResponse.getSystemDisk());
      orderServerConfigAddParam.setSystemDiskFreeSize(priceResponse.getSystemDiskFreeSize());
      orderServerConfigAddParam.setSystemDiskDayPrice(priceResponse.getSystemDiskDayPrice());
      orderServerConfigAddParam.setSystemDiskMonthPrice(priceResponse.getSystemDiskMonthPrice());
      orderServerConfigAddParam.setDataDisks(priceResponse.getDataDisks());
      orderServerConfigAddParam.setDataDisksFreeSize(priceResponse.getDataDisksFreeSize());
      orderServerConfigAddParam.setDataDisksDayPrice(priceResponse.getDataDisksDayPrice());
      orderServerConfigAddParam.setDataDisksMonthPrice(priceResponse.getDataDisksMonthPrice());
      orderServerConfigAddParam.setBandwidth(priceResponse.getBandwidth());
      orderServerConfigAddParam.setBandwidthFreeSize(priceResponse.getBandwidthFreeSize());
      orderServerConfigAddParam.setBandwidthDayPrice(priceResponse.getBandwidthDayPrice());
      orderServerConfigAddParam.setBandwidthMonthPrice(priceResponse.getBandwidthMonthPrice());
      orderServerConfigAddParam.setIpNum(priceResponse.getIpNum());
      orderServerConfigAddParam.setIpFreeSize(priceResponse.getIpFreeSize());
      orderServerConfigAddParam.setIpMonthPrice(priceResponse.getIpMonthPrice());
      orderServerConfigAddParam.setDefense(priceResponse.getDefense());
      orderServerConfigAddParam.setDefenseFreeSize(priceResponse.getDefenseFreeSize());
      orderServerConfigAddParam.setDefenseDayPrice(priceResponse.getDefenseDayPrice());
      orderServerConfigAddParam.setDefenseMonthPrice(priceResponse.getDefenseMonthPrice());
      orderServerConfigAddParam.setUnit(priceResponse.getUnit());
      orderServerConfigAddParam.setDuration(priceResponse.getDuration());
      orderServerConfigAddParam.setUnitPrice(priceResponse.getUnitPrice());
      orderServerConfigAddParam.setNumber(priceResponse.getNumber());
      orderServerConfigAddParam.setTotalPrice(priceResponse.getTotalPrice());
      orderServerConfigAddParam.setPayPrice(priceResponse.getPayPrice());
      orderServerConfigAddParam.setSystemClassId(orderParam.getSystemClassId());
      orderServerConfigAddParam.setSystemId(orderParam.getSystemId());
      orderServerConfigAddParam.setLoginPassword(orderParam.getLoginPassword());
      orderServerConfigAddParam.setRenewPrice(priceResponse.getRenewPrice());
      this.orderServerConfigService.add(orderServerConfigAddParam);
   }

   @Transactional
   @Override
   public CommonResult<MemberOrder> upgradeCore(MemberCoreUpgradeOrderParam upgradeOrderParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      String requireRealStatus = this.devConfigApi.getValueByKey("RUNXIN_SYS_RECHARGE_REQUIRE_REAL_NAME");
      if (!ObjectUtil.isNotEmpty(requireRealStatus)
         || !requireRealStatus.equals("1")
         || !ObjectUtil.isEmpty(clientLoginUser.getRealNameVerified()) && clientLoginUser.getRealNameVerified().equals("1")) {
         BaseMemberServer baseMemberServer = this.memberServerApi.selectByServerId(upgradeOrderParam.getServerId());
         if (baseMemberServer == null) {
            return CommonResult.error("服务器不存在");
         } else {
            this.updateOrderStatusByServerId(baseMemberServer.getId());
            CommonResult<MemberOrder> coreValidation = this.validateCloudCoreUpgrade(baseMemberServer, upgradeOrderParam.getCpu(), upgradeOrderParam.getRam());
            if (coreValidation != null) {
               return coreValidation;
            } else {
               MemberCoreUpgradeOrderParam coreUpgradeOrderParam = new MemberCoreUpgradeOrderParam();
               coreUpgradeOrderParam.setCpu(upgradeOrderParam.getCpu());
               coreUpgradeOrderParam.setRam(upgradeOrderParam.getRam());
               coreUpgradeOrderParam.setServerId(upgradeOrderParam.getServerId());
               coreUpgradeOrderParam.setPayType(upgradeOrderParam.getPayType());
               MemberCalculatePriceResponse MemberCalculatePriceResponse = this.memberUpgradeCalculatorHelper.upgradeCoreCalculatePrice(coreUpgradeOrderParam);
               if (null == MemberCalculatePriceResponse) {
                  return CommonResult.error("订单计价失败~");
               } else {
                  this.orderCouponSupportService
                     .applyCoupon(
                        MemberCalculatePriceResponse,
                        clientLoginUser.getId(),
                        ProductTypeEnum.CLOUD_SERVER.getCode(),
                        OrderTypeEnum.UPGRADE.getCode(),
                        baseMemberServer.getLineId(),
                        upgradeOrderParam.getCouponCode()
                     );
                  String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.CLOUD_SERVER.getCode());
                  MemberOrder order = new MemberOrder();
                  order.setOrderNo(orderNo);
                  order.setBillingMode(MemberCalculatePriceResponse.getBillingMode());
                  order.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
                  order.setOrderType(OrderTypeEnum.UPGRADE.getCode());
                  order.setUpgradeItem(UpgradeItemEnum.UPGRADE_CORE.getCode());
                  order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
                  order.setMemberNo(clientLoginUser.getId());
                  order.setMemberName(clientLoginUser.getName());
                  order.setMemberPhone(clientLoginUser.getPhone());
                  order.setTotalPrice(MemberCalculatePriceResponse.getTotalPrice());
                  order.setPayPrice(MemberCalculatePriceResponse.getPayPrice());
                  this.fillCouponFields(
                     order,
                     MemberCalculatePriceResponse,
                     upgradeOrderParam.getCouponCode(),
                     ProductTypeEnum.CLOUD_SERVER.getCode(),
                     OrderTypeEnum.UPGRADE.getCode(),
                     baseMemberServer.getLineId(),
                     clientLoginUser.getId()
                  );
                  order.setCreateTime(new Date());
                  order.setUpdateTime(new Date());
                  order.setOriginalOrderNo(MemberCalculatePriceResponse.getOriginalOrderNo());
                  this.save(order);
                  this.fillOrderServerAddParam(baseMemberServer, MemberCalculatePriceResponse, orderNo);
                  MemberOrderServerAddParam orderServerAddParam = new MemberOrderServerAddParam();
                  orderServerAddParam.setOrderNo(order.getOrderNo());
                  orderServerAddParam.setServerId(baseMemberServer.getId());
                  orderServerAddParam.setServerName(baseMemberServer.getHostName());
                  this.memberOrderServerService.add(orderServerAddParam);
                  return CommonResult.data(order);
               }
            }
         }
      } else {
         return CommonResult.error("请先实名认证");
      }
   }

   private void fillOrderServerAddParam(BaseMemberServer baseMemberServer, MemberCalculatePriceResponse priceResponse, String orderNo) {
      MemberOrderServerConfigAddParam orderServerConfigAddParam = new MemberOrderServerConfigAddParam();
      orderServerConfigAddParam.setOrderNo(orderNo);
      orderServerConfigAddParam.setLineId(baseMemberServer.getLineId());
      orderServerConfigAddParam.setCpu(priceResponse.getCpu());
      orderServerConfigAddParam.setRam(priceResponse.getRam());
      orderServerConfigAddParam.setCoreDayPrice(priceResponse.getCoreDayPrice());
      orderServerConfigAddParam.setCoreMonthPrice(priceResponse.getCoreMonthPrice());
      orderServerConfigAddParam.setSystemDisk(priceResponse.getSystemDisk());
      orderServerConfigAddParam.setSystemDiskFreeSize(priceResponse.getSystemDiskFreeSize());
      orderServerConfigAddParam.setSystemDiskDayPrice(priceResponse.getSystemDiskDayPrice());
      orderServerConfigAddParam.setSystemDiskMonthPrice(priceResponse.getSystemDiskMonthPrice());
      orderServerConfigAddParam.setDataDisks(priceResponse.getDataDisks());
      orderServerConfigAddParam.setDataDisksFreeSize(priceResponse.getDataDisksFreeSize());
      orderServerConfigAddParam.setDataDisksDayPrice(priceResponse.getDataDisksDayPrice());
      orderServerConfigAddParam.setDataDisksMonthPrice(priceResponse.getDataDisksMonthPrice());
      orderServerConfigAddParam.setBandwidth(priceResponse.getBandwidth());
      orderServerConfigAddParam.setBandwidthFreeSize(priceResponse.getBandwidthFreeSize());
      orderServerConfigAddParam.setBandwidthDayPrice(priceResponse.getBandwidthDayPrice());
      orderServerConfigAddParam.setBandwidthMonthPrice(priceResponse.getBandwidthMonthPrice());
      orderServerConfigAddParam.setIpNum(priceResponse.getIpNum());
      orderServerConfigAddParam.setIpFreeSize(priceResponse.getIpFreeSize());
      orderServerConfigAddParam.setIpMonthPrice(priceResponse.getIpMonthPrice());
      orderServerConfigAddParam.setDefense(priceResponse.getDefense());
      orderServerConfigAddParam.setDefenseFreeSize(priceResponse.getDefenseFreeSize());
      orderServerConfigAddParam.setDefenseDayPrice(priceResponse.getDefenseDayPrice());
      orderServerConfigAddParam.setDefenseMonthPrice(priceResponse.getDefenseMonthPrice());
      orderServerConfigAddParam.setUnit(priceResponse.getUnit());
      orderServerConfigAddParam.setDuration(priceResponse.getDuration());
      orderServerConfigAddParam.setUnitPrice(priceResponse.getUnitPrice());
      orderServerConfigAddParam.setNumber(priceResponse.getNumber());
      orderServerConfigAddParam.setTotalPrice(priceResponse.getTotalPrice());
      orderServerConfigAddParam.setPayPrice(priceResponse.getPayPrice());
      orderServerConfigAddParam.setSystemClassId(baseMemberServer.getSystemClassId());
      orderServerConfigAddParam.setSystemId(baseMemberServer.getSystemId());
      orderServerConfigAddParam.setLoginPassword(baseMemberServer.getOsPassword());
      orderServerConfigAddParam.setRenewPrice(priceResponse.getRenewPrice());
      this.orderServerConfigService.add(orderServerConfigAddParam);
   }

   @Transactional
   @Override
   public CommonResult<MemberOrder> upgradeDisk(MemberDiskUpgradeOrderParam upgradeOrderParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      String requireRealStatus = this.devConfigApi.getValueByKey("RUNXIN_SYS_RECHARGE_REQUIRE_REAL_NAME");
      if (!ObjectUtil.isNotEmpty(requireRealStatus)
         || !requireRealStatus.equals("1")
         || !ObjectUtil.isEmpty(clientLoginUser.getRealNameVerified()) && clientLoginUser.getRealNameVerified().equals("1")) {
         BaseMemberServer baseMemberServer = this.memberServerApi.selectByServerId(upgradeOrderParam.getServerId());
         if (baseMemberServer == null) {
            return CommonResult.error("服务器不存在");
         } else {
            this.updateOrderStatusByServerId(baseMemberServer.getId());
            CommonResult<MemberOrder> diskValidation = this.validateCloudDiskUpgrade(
               baseMemberServer, upgradeOrderParam.getSystemDisk(), upgradeOrderParam.getDataDisks()
            );
            if (diskValidation != null) {
               return diskValidation;
            } else {
               MemberCalculatePriceResponse MemberCalculatePriceResponse = this.memberUpgradeCalculatorHelper.upgradeDiskCalculatePrice(upgradeOrderParam);
               if (null == MemberCalculatePriceResponse) {
                  return CommonResult.error("订单计价失败~");
               } else {
                  this.orderCouponSupportService
                     .applyCoupon(
                        MemberCalculatePriceResponse,
                        clientLoginUser.getId(),
                        ProductTypeEnum.CLOUD_SERVER.getCode(),
                        OrderTypeEnum.UPGRADE.getCode(),
                        baseMemberServer.getLineId(),
                        upgradeOrderParam.getCouponCode()
                     );
                  String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.CLOUD_SERVER.getCode());
                  MemberOrder order = new MemberOrder();
                  order.setOrderNo(orderNo);
                  order.setBillingMode(MemberCalculatePriceResponse.getBillingMode());
                  order.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
                  order.setOrderType(OrderTypeEnum.UPGRADE.getCode());
                  order.setUpgradeItem(UpgradeItemEnum.UPGRADE_HARD_DISK.getCode());
                  order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
                  order.setMemberNo(clientLoginUser.getId());
                  order.setMemberName(clientLoginUser.getName());
                  order.setMemberPhone(clientLoginUser.getPhone());
                  order.setTotalPrice(MemberCalculatePriceResponse.getTotalPrice());
                  order.setPayPrice(MemberCalculatePriceResponse.getPayPrice());
                  this.fillCouponFields(
                     order,
                     MemberCalculatePriceResponse,
                     upgradeOrderParam.getCouponCode(),
                     ProductTypeEnum.CLOUD_SERVER.getCode(),
                     OrderTypeEnum.UPGRADE.getCode(),
                     baseMemberServer.getLineId(),
                     clientLoginUser.getId()
                  );
                  order.setCreateTime(new Date());
                  order.setUpdateTime(new Date());
                  this.save(order);
                  this.fillOrderServerAddParam(baseMemberServer, MemberCalculatePriceResponse, orderNo);
                  MemberOrderServerAddParam orderServerAddParam = new MemberOrderServerAddParam();
                  orderServerAddParam.setOrderNo(order.getOrderNo());
                  orderServerAddParam.setServerId(baseMemberServer.getId());
                  orderServerAddParam.setServerName(baseMemberServer.getHostName());
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
   public CommonResult<MemberOrder> upgradeBandwidth(MemberBandwidthUpgradeOrderParam upgradeOrderParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      String requireRealStatus = this.devConfigApi.getValueByKey("RUNXIN_SYS_RECHARGE_REQUIRE_REAL_NAME");
      if (!ObjectUtil.isNotEmpty(requireRealStatus)
         || !requireRealStatus.equals("1")
         || !ObjectUtil.isEmpty(clientLoginUser.getRealNameVerified()) && clientLoginUser.getRealNameVerified().equals("1")) {
         BaseMemberServer baseMemberServer = this.memberServerApi.selectByServerId(upgradeOrderParam.getServerId());
         if (baseMemberServer == null) {
            return CommonResult.error("服务器不存在");
         } else {
            this.updateOrderStatusByServerId(baseMemberServer.getId());
            if (upgradeOrderParam.getBandwidth() <= baseMemberServer.getBandwidth()) {
               return CommonResult.error("升级的带宽不能小于当前服务器");
            } else {
               MemberBandwidthUpgradeOrderParam bandwidthUpgradeOrderParam = new MemberBandwidthUpgradeOrderParam();
               bandwidthUpgradeOrderParam.setBandwidth(upgradeOrderParam.getBandwidth());
               bandwidthUpgradeOrderParam.setServerId(upgradeOrderParam.getServerId());
               bandwidthUpgradeOrderParam.setPayType(upgradeOrderParam.getPayType());
               MemberCalculatePriceResponse MemberCalculatePriceResponse = this.memberUpgradeCalculatorHelper
                  .upgradeBandwidthCalculatePrice(bandwidthUpgradeOrderParam);
               if (null == MemberCalculatePriceResponse) {
                  return CommonResult.error("订单计价失败~");
               } else {
                  this.orderCouponSupportService
                     .applyCoupon(
                        MemberCalculatePriceResponse,
                        clientLoginUser.getId(),
                        ProductTypeEnum.CLOUD_SERVER.getCode(),
                        OrderTypeEnum.UPGRADE.getCode(),
                        baseMemberServer.getLineId(),
                        upgradeOrderParam.getCouponCode()
                     );
                  String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.CLOUD_SERVER.getCode());
                  MemberOrder order = new MemberOrder();
                  order.setOrderNo(orderNo);
                  order.setBillingMode(MemberCalculatePriceResponse.getBillingMode());
                  order.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
                  order.setOrderType(OrderTypeEnum.UPGRADE.getCode());
                  order.setUpgradeItem(UpgradeItemEnum.UPGRADE_BANDWIDTH.getCode());
                  order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
                  order.setMemberNo(clientLoginUser.getId());
                  order.setMemberName(clientLoginUser.getName());
                  order.setMemberPhone(clientLoginUser.getPhone());
                  order.setTotalPrice(MemberCalculatePriceResponse.getTotalPrice());
                  order.setPayPrice(MemberCalculatePriceResponse.getPayPrice());
                  this.fillCouponFields(
                     order,
                     MemberCalculatePriceResponse,
                     upgradeOrderParam.getCouponCode(),
                     ProductTypeEnum.CLOUD_SERVER.getCode(),
                     OrderTypeEnum.UPGRADE.getCode(),
                     baseMemberServer.getLineId(),
                     clientLoginUser.getId()
                  );
                  order.setCreateTime(new Date());
                  order.setUpdateTime(new Date());
                  this.save(order);
                  this.fillOrderServerAddParam(baseMemberServer, MemberCalculatePriceResponse, orderNo);
                  MemberOrderServerAddParam orderServerAddParam = new MemberOrderServerAddParam();
                  orderServerAddParam.setOrderNo(order.getOrderNo());
                  orderServerAddParam.setServerId(baseMemberServer.getId());
                  orderServerAddParam.setServerName(baseMemberServer.getHostName());
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
   public CommonResult<MemberOrder> upgradeDefense(MemberDefenseUpgradeOrderParam upgradeOrderParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      String requireRealStatus = this.devConfigApi.getValueByKey("RUNXIN_SYS_RECHARGE_REQUIRE_REAL_NAME");
      if (!ObjectUtil.isNotEmpty(requireRealStatus)
         || !requireRealStatus.equals("1")
         || !ObjectUtil.isEmpty(clientLoginUser.getRealNameVerified()) && clientLoginUser.getRealNameVerified().equals("1")) {
         BaseMemberServer baseMemberServer = this.memberServerApi.selectByServerId(upgradeOrderParam.getServerId());
         if (baseMemberServer == null) {
            return CommonResult.error("服务器不存在");
         } else {
            this.updateOrderStatusByServerId(baseMemberServer.getId());
            if (upgradeOrderParam.getDefense() <= baseMemberServer.getDefense()) {
               return CommonResult.error("升级的防御不能小于当前服务器");
            } else {
               MemberDefenseUpgradeOrderParam defenseUpgradeOrderParam = new MemberDefenseUpgradeOrderParam();
               defenseUpgradeOrderParam.setDefense(upgradeOrderParam.getDefense());
               defenseUpgradeOrderParam.setServerId(upgradeOrderParam.getServerId());
               defenseUpgradeOrderParam.setPayType(upgradeOrderParam.getPayType());
               MemberCalculatePriceResponse MemberCalculatePriceResponse = this.memberUpgradeCalculatorHelper
                  .upgradeDefenseCalculatePrice(defenseUpgradeOrderParam);
               if (null == MemberCalculatePriceResponse) {
                  return CommonResult.error("订单计价失败~");
               } else {
                  this.orderCouponSupportService
                     .applyCoupon(
                        MemberCalculatePriceResponse,
                        clientLoginUser.getId(),
                        ProductTypeEnum.CLOUD_SERVER.getCode(),
                        OrderTypeEnum.UPGRADE.getCode(),
                        baseMemberServer.getLineId(),
                        upgradeOrderParam.getCouponCode()
                     );
                  String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.CLOUD_SERVER.getCode());
                  MemberOrder order = new MemberOrder();
                  order.setOrderNo(orderNo);
                  order.setBillingMode(MemberCalculatePriceResponse.getBillingMode());
                  order.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
                  order.setOrderType(OrderTypeEnum.UPGRADE.getCode());
                  order.setUpgradeItem(UpgradeItemEnum.UPGRADE_DEFENSE.getCode());
                  order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
                  order.setMemberNo(clientLoginUser.getId());
                  order.setMemberName(clientLoginUser.getName());
                  order.setMemberPhone(clientLoginUser.getPhone());
                  order.setTotalPrice(MemberCalculatePriceResponse.getTotalPrice());
                  order.setPayPrice(MemberCalculatePriceResponse.getPayPrice());
                  this.fillCouponFields(
                     order,
                     MemberCalculatePriceResponse,
                     upgradeOrderParam.getCouponCode(),
                     ProductTypeEnum.CLOUD_SERVER.getCode(),
                     OrderTypeEnum.UPGRADE.getCode(),
                     baseMemberServer.getLineId(),
                     clientLoginUser.getId()
                  );
                  order.setCreateTime(new Date());
                  order.setUpdateTime(new Date());
                  this.save(order);
                  this.fillOrderServerAddParam(baseMemberServer, MemberCalculatePriceResponse, orderNo);
                  MemberOrderServerAddParam orderServerAddParam = new MemberOrderServerAddParam();
                  orderServerAddParam.setOrderNo(order.getOrderNo());
                  orderServerAddParam.setServerId(baseMemberServer.getId());
                  orderServerAddParam.setServerName(baseMemberServer.getHostName());
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
   public CommonResult<MemberOrder> renew(MemberRenewOrderParam renewOrderParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      String requireRealStatus = this.devConfigApi.getValueByKey("RUNXIN_SYS_RECHARGE_REQUIRE_REAL_NAME");
      if (!ObjectUtil.isNotEmpty(requireRealStatus)
         || !requireRealStatus.equals("1")
         || !ObjectUtil.isEmpty(clientLoginUser.getRealNameVerified()) && clientLoginUser.getRealNameVerified().equals("1")) {
         List<String> serverIds = RenewServerIdSupport.resolve(renewOrderParam.getServerId(), renewOrderParam.getServerIds());
         List<BaseMemberServer> servers = new ArrayList<>();

         for (String serverId : serverIds) {
            BaseMemberServer server = this.memberServerApi.selectByServerId(serverId);
            if (server == null) {
               return CommonResult.error("服务器不存在");
            }

            if (server.getBusinessStatus().equals(BusinessStatusEnum.DELETE.getCode())) {
               return CommonResult.error("已删除服务器不支持续费");
            }

            this.updateOrderStatusByServerId(server.getId());
            servers.add(server);
         }

         BaseMemberServer firstServer = servers.get(0);
         if (renewOrderParam.getDuration() != null && renewOrderParam.getDuration() > 0) {
            MemberCalculatePriceResponse MemberCalculatePriceResponse = this.memberRenewCalculatorHelper.renewCalculatePrice(renewOrderParam);
            if (null == MemberCalculatePriceResponse) {
               return CommonResult.error("订单计价失败~");
            } else {
               String couponLineId = this.memberRenewCalculatorHelper.resolveCouponLineId(MemberCalculatePriceResponse, renewOrderParam.getCouponCode());
               this.orderCouponSupportService
                  .applyCoupon(
                     MemberCalculatePriceResponse,
                     clientLoginUser.getId(),
                     ProductTypeEnum.CLOUD_SERVER.getCode(),
                     OrderTypeEnum.RENEW.getCode(),
                     couponLineId,
                     renewOrderParam.getCouponCode()
                  );
               String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.CLOUD_SERVER.getCode());
               MemberOrder order = new MemberOrder();
               order.setOrderNo(orderNo);
               order.setBillingMode(MemberCalculatePriceResponse.getBillingMode());
               order.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
               order.setOrderType(OrderTypeEnum.RENEW.getCode());
               order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
               order.setMemberNo(clientLoginUser.getId());
               order.setMemberName(clientLoginUser.getName());
               order.setMemberPhone(clientLoginUser.getPhone());
               order.setTotalPrice(MemberCalculatePriceResponse.getTotalPrice());
               order.setPayPrice(MemberCalculatePriceResponse.getPayPrice());
               this.fillCouponFields(
                  order,
                  MemberCalculatePriceResponse,
                  renewOrderParam.getCouponCode(),
                  ProductTypeEnum.CLOUD_SERVER.getCode(),
                  OrderTypeEnum.RENEW.getCode(),
                  couponLineId,
                  clientLoginUser.getId()
               );
               order.setCreateTime(new Date());
               order.setUpdateTime(new Date());
               this.save(order);
               this.fillOrderServerAddParam(firstServer, MemberCalculatePriceResponse, orderNo);

               for (BaseMemberServer serverx : servers) {
                  MemberOrderServerAddParam orderServerAddParam = new MemberOrderServerAddParam();
                  orderServerAddParam.setOrderNo(order.getOrderNo());
                  orderServerAddParam.setServerId(serverx.getId());
                  orderServerAddParam.setServerName(serverx.getHostName());
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

   @Transactional
   @Override
   public CommonResult<MemberOrder> createDomainOrder(DomainOrderCreateParam param) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      List<DomainOrderItemParam> itemParams = param.getItems();
      if (itemParams != null && !itemParams.isEmpty()) {
         BigDecimal orderTotalPrice = BigDecimal.ZERO;
         List<DomainOrderItem> items = new ArrayList<>();

         for (DomainOrderItemParam p : itemParams) {
            String tld = StrUtil.nullToEmpty(p.getTld()).trim();
            DomainTldPrice priceConfig = this.memberDomainTldPriceService.getActiveByTld(tld);
            if (priceConfig == null || priceConfig.getRegisterPrice() == null) {
               throw new CommonException("后缀【{}】未配置或已关闭，无法下单", new Object[]{tld});
            }

            int years = p.getYears() == null ? 1 : Math.max(1, p.getYears());
            BigDecimal unitPrice = priceConfig.getRegisterPrice();
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf((long)years)).setScale(2, RoundingMode.HALF_UP);
            orderTotalPrice = orderTotalPrice.add(itemTotal);
            DomainOrderItem item = new DomainOrderItem();
            item.setId(IdWorker.getIdStr());
            item.setOrderNo(null);
            item.setDomainName(p.getDomainName());
            item.setTld(tld);
            item.setTemplateId(param.getTemplateId());
            item.setYears(years);
            item.setUnitPrice(unitPrice);
            item.setTotalPrice(itemTotal);
            item.setCreateTime(new Date());
            items.add(item);
         }

         String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.DOMAIN.getCode());

         for (DomainOrderItem item : items) {
            item.setOrderNo(orderNo);
         }

         MemberOrder order = new MemberOrder();
         order.setOrderNo(orderNo);
         order.setBillingMode(1);
         order.setProductType(ProductTypeEnum.DOMAIN.getCode());
         order.setOrderType(OrderTypeEnum.NEW_PURCHASE.getCode());
         order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
         order.setMemberNo(clientLoginUser.getId());
         order.setMemberName(clientLoginUser.getName());
         order.setMemberPhone(clientLoginUser.getPhone());
         order.setTotalPrice(orderTotalPrice.setScale(2, RoundingMode.HALF_UP));
         order.setPayPrice(order.getTotalPrice());
         order.setDiscountPrice(BigDecimal.ZERO);
         CouponCalculateResponse couponResponse = this.orderCouponSupportService
            .calculateCoupon(
               clientLoginUser.getId(),
               ProductTypeEnum.DOMAIN.getCode(),
               OrderTypeEnum.NEW_PURCHASE.getCode(),
               null,
               param.getCouponCode(),
               order.getPayPrice()
            );
         if (couponResponse != null) {
            order.setCouponCode(couponResponse.getCouponCode());
            order.setMemberCouponId(couponResponse.getMemberCouponId());
            order.setCouponPrice(couponResponse.getCouponPrice());
            order.setCouponUseCount(couponResponse.getUseCount());
            order.setPayPrice(couponResponse.getPayPrice());
         } else {
            order.setCouponPrice(BigDecimal.ZERO);
            order.setCouponUseCount(0);
         }

         order.setCreateTime(new Date());
         order.setUpdateTime(new Date());
         order.setPayType(param.getPayType());
         this.orderCouponSupportService
            .lockCoupon(
               order,
               clientLoginUser.getId(),
               ProductTypeEnum.DOMAIN.getCode(),
               OrderTypeEnum.NEW_PURCHASE.getCode(),
               null,
               param.getCouponCode(),
               this.orderCouponSupportService.resolveOrderAmountBeforeCoupon(order.getPayPrice(), order.getCouponPrice())
            );
         this.save(order);
         this.domainOrderItemService.saveBatch(items);
         return CommonResult.data(order);
      } else {
         return CommonResult.error("请传入要注册的域名列表");
      }
   }

   @Override
   public CommonResult pay(MemberPayParam payParam) {
      LambdaQueryWrapper<MemberOrder> queryWrapper = (LambdaQueryWrapper<MemberOrder>)((LambdaQueryWrapper<MemberOrder>)new QueryWrapper<MemberOrder>()
            .lambda()
            .eq(MemberOrder::getOrderNo, payParam.getOrderNo()))
         .eq(MemberOrder::getOrderStatus, OrderStatusEnum.UNPAID.getCode());
      MemberOrder order = (MemberOrder)this.memberOrderMapper.selectOne(queryWrapper);
      log.info("order 订单查询:  {}", JSON.toJSONString(order));
      if (null == order) {
         return CommonResult.error("订单不存在或已支付");
      } else {
         String productType = ProductTypeEnum.getMessageByCode(order.getProductType());
         String orderType = OrderTypeEnum.getDescriptionByCode(order.getOrderType());
         StringBuilder subjectBuilder = new StringBuilder(productType);
         subjectBuilder.append(orderType);
         if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(order.getProductType())) {
            MemberOrderServerConfig orderServerConfig = this.orderServerConfigService.queryByOrderNo(order.getOrderNo());
            if (orderServerConfig == null) {
               return CommonResult.error("服务器订单详情不存在");
            }

            if (OrderTypeEnum.UPGRADE.getCode() == order.getOrderType()) {
               String upgradeItem = UpgradeItemEnum.getDescriptionByCode(order.getUpgradeItem());
               subjectBuilder.append(upgradeItem);
            }

            if (OrderTypeEnum.RENEW.getCode() == order.getOrderType()) {
               List<MemberOrderServer> orderServers = this.memberOrderServerService.queryByList(order.getOrderNo());
               if (orderServers == null || orderServers.isEmpty()) {
                  return CommonResult.error("服务器订单不存在");
               }

               if (orderServers.size() > 1) {
                  MemberRenewOrderParam repriceParam = new MemberRenewOrderParam();
                  repriceParam.setServerIds(orderServers.stream().map(MemberOrderServer::getServerId).collect(Collectors.toList()));
                  repriceParam.setDuration(orderServerConfig.getDuration());
                  MemberCalculatePriceResponse currentPrice = this.memberRenewCalculatorHelper.renewCalculatePrice(repriceParam);
                  if (currentPrice.getTotalPrice() == null
                     || order.getTotalPrice() == null
                     || currentPrice.getTotalPrice().compareTo(order.getTotalPrice()) != 0) {
                     return CommonResult.error("服务器配置或续费价格已变更，请重新下单");
                  }
               } else {
                  MemberOrderServer orderServer = orderServers.get(0);
                  BaseMemberServer memberServer = this.memberServerApi.selectByServerId(orderServer.getServerId());
                  if (memberServer == null) {
                     return CommonResult.error("服务器不存在");
                  }

                  if (!orderServerConfig.getCpu().equals(memberServer.getCpu())) {
                     return CommonResult.error("服务器核心配置已变更，请重新下单");
                  }

                  if (!orderServerConfig.getRam().equals(memberServer.getRam())) {
                     return CommonResult.error("服务器内存配置已变更，请重新下单");
                  }

                  if (!orderServerConfig.getDataDisks().equals(memberServer.getDataDisks())) {
                     return CommonResult.error("服务器磁盘配置已变更，请重新下单");
                  }

                  if (!orderServerConfig.getBandwidth().equals(memberServer.getBandwidth())) {
                     log.info("服务器带宽配置已变更，请重新下单：{},{}", memberServer.getBandwidth(), orderServerConfig.getBandwidth());
                     return CommonResult.error("服务器带宽配置已变更，请重新下单");
                  }

                  if (!orderServerConfig.getDefense().equals(memberServer.getDefense())) {
                     return CommonResult.error("服务器防御配置已变更，请重新下单");
                  }
               }
            }
         } else if (ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(order.getProductType())) {
            MemberIndependentOrderServerConfig orderServerConfigx = this.memberIndependentOrderServerConfigService.queryByOrderNo(order.getOrderNo());
            if (orderServerConfigx == null) {
               return CommonResult.error("服务器订单详情不存在");
            }

            if (OrderTypeEnum.UPGRADE.getCode() == order.getOrderType()) {
               String upgradeItem = UpgradeItemEnum.getDescriptionByCode(order.getUpgradeItem());
               subjectBuilder.append(upgradeItem);
            }

            if (OrderTypeEnum.RENEW.getCode() == order.getOrderType()) {
               List<MemberOrderServer> orderServersx = this.memberOrderServerService.queryByList(order.getOrderNo());
               if (orderServersx != null && orderServersx.size() > 1) {
                  MemberIndependentRenewOrderParam repriceParam = new MemberIndependentRenewOrderParam();
                  repriceParam.setServerIds(orderServersx.stream().map(MemberOrderServer::getServerId).collect(Collectors.toList()));
                  repriceParam.setDuration(orderServerConfigx.getDuration());
                  IndependentCalculatePriceResponse currentPrice = this.memberIndependentRenewCalculatorHelper.renewCalculatePrice(repriceParam);
                  if (currentPrice.getTotalPrice() == null
                     || order.getTotalPrice() == null
                     || currentPrice.getTotalPrice().compareTo(order.getTotalPrice()) != 0) {
                     return CommonResult.error("服务器配置或续费价格已变更，请重新下单");
                  }
               }
            }
         }

         if (order.getPayPrice() != null && order.getPayPrice().compareTo(BigDecimal.ZERO) > 0) {
            CommonResult<?> result = this.paymentsApi
               .pay(payParam.getPayType(), payParam.getPlatform(), subjectBuilder.toString(), order.getMemberNo(), order.getOrderNo(), order.getPayPrice());
            log.info(
               "订单支付调用结果 orderNo={} payType={} code={} hasData={}",
               new Object[]{order.getOrderNo(), payParam.getPayType(), result.getCode(), result.getData() != null}
            );
            if (Integer.valueOf(5).equals(payParam.getPayType())) {
               if (result.getCode() != null && result.getCode() == 200 && result.getData() == null) {
                  log.error("订单USDT支付返回成功但无数据 orderNo={}", order.getOrderNo());
                  return CommonResult.error("USDT支付信息获取失败，请检查系统配置（收款地址、汇率）或稍后重试");
               }

               if (result.getCode() != null && result.getCode() != 200 && result.getMsg() != null) {
                  return CommonResult.error(result.getMsg());
               }
            }

            return result;
         } else if (order.getPayPrice() != null && order.getPayPrice().compareTo(BigDecimal.ZERO) == 0) {
            ((MemberOrderService)SpringUtil.getBean(MemberOrderService.class)).paySuccess(order.getOrderNo(), new Date(), null, payParam.getPayType());
            return CommonResult.ok();
         } else {
            return CommonResult.error("订单金额异常，无法支付");
         }
      }
   }

   @Transactional
   @Override
   public void paySuccess(String orderNo, Date payTime, String alipayPayNo, Integer payType) {
      long startTime = System.currentTimeMillis();

      try {
         log.info("开始处理支付成功逻辑，订单号:{}", orderNo);
         MemberOrder order = (MemberOrder)this.getOne((Wrapper<MemberOrder>)new LambdaQueryWrapper<MemberOrder>().eq(MemberOrder::getOrderNo, orderNo));
         if (order == null) {
            log.error("订单不存在，订单号:{}", orderNo);
            throw new RuntimeException("订单不存在");
         }

         if (OrderStatusEnum.PAID.getCode().equals(order.getOrderStatus())) {
            log.warn("订单已支付，跳过重复处理，订单号:{}", orderNo);
            return;
         }

         ((MemberOrderService)SpringUtil.getBean(MemberOrderService.class)).markOrderPaid(orderNo, payTime, alipayPayNo, payType);
         order.setOrderStatus(OrderStatusEnum.PAID.getCode());
         order.setPayTime(payTime);
         order.setPayOrderNo(alipayPayNo);
         if (payType != null) {
            order.setPayType(payType);
         }

         this.sendNotificationAsync(order);
         this.createAgentCommissionAsync(orderNo);
         this.processPaidOrderAsync(orderNo);
      } catch (Exception var10) {
         long duration = System.currentTimeMillis() - startTime;
         log.error("支付成功后处理订单失败，订单号:{},异常信息:{}, 耗时:{}ms", new Object[]{orderNo, var10.getMessage(), duration, var10});
         throw new RuntimeException("支付成功后处理订单失败：" + var10.getMessage(), var10);
      }

      long duration = System.currentTimeMillis() - startTime;
      log.info("支付状态更新完成，订单号: {}, 耗时: {}ms", orderNo, duration);
   }

   @Transactional(
      propagation = Propagation.REQUIRES_NEW,
      rollbackFor = {Exception.class}
   )
   @Override
   public void markOrderPaid(String orderNo, Date payTime, String alipayPayNo, Integer payType) {
      MemberOrder order = (MemberOrder)this.getOne((Wrapper<MemberOrder>)new LambdaQueryWrapper<MemberOrder>().eq(MemberOrder::getOrderNo, orderNo));
      if (order == null) {
         throw new RuntimeException("订单不存在");
      } else if (!OrderStatusEnum.PAID.getCode().equals(order.getOrderStatus())) {
         LambdaUpdateWrapper<MemberOrder> updateWrapper = (LambdaUpdateWrapper<MemberOrder>)((LambdaUpdateWrapper<MemberOrder>)((LambdaUpdateWrapper<MemberOrder>)((LambdaUpdateWrapper<MemberOrder>)((LambdaUpdateWrapper<MemberOrder>)new LambdaUpdateWrapper<MemberOrder>()
                        .eq(MemberOrder::getOrderNo, orderNo))
                     .set(MemberOrder::getOrderStatus, OrderStatusEnum.PAID.getCode()))
                  .set(MemberOrder::getPayTime, payTime))
               .set(MemberOrder::getPayOrderNo, alipayPayNo))
            .set(payType != null, MemberOrder::getPayType, payType);
         this.update(updateWrapper);
         this.orderCouponSupportService.useCoupon(orderNo);
         log.info("订单状态已提交为已支付，订单号:{}", orderNo);
      }
   }

   private void processPaidOrderAsync(String orderNo) {
      this.orderTaskExecutor.execute(() -> {
         try {
            ((MemberOrderService)SpringUtil.getBean(MemberOrderService.class)).processPaidOrder(orderNo);
         } catch (Exception var2) {
            log.error("后台处理已支付订单失败，订单号:{}, 错误:{}", new Object[]{orderNo, var2.getMessage(), var2});
         }
      });
   }

   private void createAgentCommissionAsync(String orderNo) {
      this.orderTaskExecutor.execute(() -> {
         try {
            this.agentCommissionService.createCommissionByOrder(orderNo);
         } catch (Exception var3) {
            log.error("生成代理分成流水失败，订单号:{}, 错误:{}", new Object[]{orderNo, var3.getMessage(), var3});
         }
      });
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void processPaidOrder(String orderNo) {
      long startTime = System.currentTimeMillis();
      MemberOrder order = (MemberOrder)this.getOne((Wrapper<MemberOrder>)new LambdaQueryWrapper<MemberOrder>().eq(MemberOrder::getOrderNo, orderNo));
      if (order == null) {
         throw new RuntimeException("订单不存在");
      } else if (!OrderStatusEnum.PAID.getCode().equals(order.getOrderStatus())) {
         throw new RuntimeException("订单未支付，无法执行后续流程");
      } else {
         if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(order.getProductType())) {
            log.info("后台处理云服务器订单，订单号:{}", orderNo);
            MemberOrderServerConfig orderServerConfig = this.orderServerConfigService.queryByOrderNo(orderNo);
            if (orderServerConfig == null) {
               throw new RuntimeException("服务器订单详情不存在");
            }

            if (order.getOrderType().equals(OrderTypeEnum.NEW_PURCHASE.getCode())) {
               if (this.isResourcePoolType(orderServerConfig.getLineId(), null)) {
                  this.createResourcePoolRequest(order, orderServerConfig.getLineId(), null);
               } else {
                  this.createCloudServer(order);
               }
            }

            if (order.getOrderType().equals(OrderTypeEnum.UPGRADE.getCode())) {
               MemberOrderServer orderServer = this.memberOrderServerService.queryByOrderNo(order.getOrderNo());
               if (orderServer == null) {
                  throw new RuntimeException("服务器订单不存在");
               }

               boolean isResourcePool = this.isResourcePoolType(orderServerConfig.getLineId(), null);
               if (isResourcePool) {
                  if (order.getUpgradeItem().equals(UpgradeItemEnum.UPGRADE_CORE.getCode())) {
                     JSONObject additionalParams = new JSONObject();
                     additionalParams.put("cpu", orderServerConfig.getCpu());
                     additionalParams.put("ram", orderServerConfig.getRam());
                     this.createResourcePoolUpgradeRequest(order, orderServer.getServerId(), UpgradeItemEnum.UPGRADE_CORE, additionalParams);
                  }

                  if (order.getUpgradeItem().equals(UpgradeItemEnum.UPGRADE_BANDWIDTH.getCode())) {
                     JSONObject additionalParams = new JSONObject();
                     additionalParams.put("bandwidth", orderServerConfig.getBandwidth());
                     this.createResourcePoolUpgradeRequest(order, orderServer.getServerId(), UpgradeItemEnum.UPGRADE_BANDWIDTH, additionalParams);
                  }

                  if (order.getUpgradeItem().equals(UpgradeItemEnum.UPGRADE_HARD_DISK.getCode())) {
                     JSONObject additionalParams = new JSONObject();
                     additionalParams.put("systemDisk", orderServerConfig.getSystemDisk());
                     additionalParams.put("dataDisks", orderServerConfig.getDataDisks());
                     this.createResourcePoolUpgradeRequest(order, orderServer.getServerId(), UpgradeItemEnum.UPGRADE_HARD_DISK, additionalParams);
                  }

                  if (order.getUpgradeItem().equals(UpgradeItemEnum.UPGRADE_DEFENSE.getCode())) {
                     JSONObject additionalParams = new JSONObject();
                     additionalParams.put("defense", orderServerConfig.getDefense());
                     this.createResourcePoolUpgradeRequest(order, orderServer.getServerId(), UpgradeItemEnum.UPGRADE_DEFENSE, additionalParams);
                  }
               } else {
                  if (order.getUpgradeItem().equals(UpgradeItemEnum.UPGRADE_CORE.getCode())) {
                     this.upgradeCpuRamServer(
                        orderNo, orderServer.getServerId(), orderServerConfig.getCpu(), orderServerConfig.getRam(), orderServerConfig.getRenewPrice()
                     );
                  }

                  if (order.getUpgradeItem().equals(UpgradeItemEnum.UPGRADE_BANDWIDTH.getCode())) {
                     this.upgradeBandwidthServer(orderNo, orderServer.getServerId(), orderServerConfig.getBandwidth(), orderServerConfig.getRenewPrice());
                  }

                  if (order.getUpgradeItem().equals(UpgradeItemEnum.UPGRADE_HARD_DISK.getCode())) {
                     this.upgradeHardDisksServer(
                        orderNo,
                        orderServer.getServerId(),
                        orderServerConfig.getSystemDisk(),
                        orderServerConfig.getDataDisks(),
                        orderServerConfig.getRenewPrice()
                     );
                  }

                  if (order.getUpgradeItem().equals(UpgradeItemEnum.UPGRADE_DEFENSE.getCode())) {
                     this.upgradeDefenseServer(orderNo, orderServer.getServerId(), orderServerConfig.getDefense(), orderServerConfig.getRenewPrice());
                  }
               }
            }

            if (order.getOrderType().equals(OrderTypeEnum.RENEW.getCode())) {
               List<MemberOrderServer> orderServers = this.memberOrderServerService.queryByList(order.getOrderNo());
               if (orderServers == null || orderServers.isEmpty()) {
                  throw new RuntimeException("服务器订单不存在");
               }

               for (MemberOrderServer orderServerx : orderServers) {
                  BaseMemberServer liveServer = this.memberServerApi.selectByServerIdAndMemberNo(orderServerx.getServerId(), order.getMemberNo());
                  String lineId = liveServer == null ? orderServerConfig.getLineId() : liveServer.getLineId();
                  BigDecimal itemPayPrice = this.resolveRenewItemPayPrice(
                     liveServer == null ? null : liveServer.getRenewPrice(), orderServerConfig.getDuration()
                  );
                  if (this.isResourcePoolType(lineId, null)) {
                     this.createResourcePoolRenewRequest(order, orderServerx.getServerId(), orderServerConfig.getDuration(), itemPayPrice);
                  }

                  if (!Boolean.TRUE.equals(this.memberServerApi.renewServer(orderNo, orderServerx.getServerId(), orderServerConfig.getDuration()))) {
                     throw new RuntimeException("云服务器续费失败，serverId=" + orderServerx.getServerId());
                  }
               }
            }
         } else if (ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(order.getProductType())) {
            log.info("后台处理独立服务器订单，订单号:{}", orderNo);
            IndependentOrderServerConfigResponse memberIndependentOrderServerConfig = this.memberIndependentOrderServerConfigService
               .queryByOrder(order.getOrderNo());
            if (null == memberIndependentOrderServerConfig) {
               throw new RuntimeException("订单服务信息不存在");
            }

            if (order.getOrderType().equals(OrderTypeEnum.NEW_PURCHASE.getCode())) {
               if (this.isResourcePoolType(null, memberIndependentOrderServerConfig.getMachineId())) {
                  this.createResourcePoolRequest(order, null, memberIndependentOrderServerConfig.getMachineId());
               } else {
                  this.createIndependentServer(order);
               }
            }

            if (order.getOrderType().equals(OrderTypeEnum.UPGRADE.getCode())) {
               MemberOrderServer orderServerx = this.memberOrderServerService.queryByOrderNo(order.getOrderNo());
               if (orderServerx == null) {
                  throw new RuntimeException("服务器订单不存在");
               }

               if (order.getUpgradeItem().equals(UpgradeItemEnum.UPGRADE_IP.getCode())) {
                  if (this.isIndependentResourcePoolServer(orderServerx.getServerId())) {
                     JSONObject p = new JSONObject();
                     p.put("ipSize", memberIndependentOrderServerConfig.getIpSize());
                     this.createResourcePoolUpgradeIndependentRequest(order, orderServerx.getServerId(), UpgradeItemEnum.UPGRADE_IP, p);
                  } else {
                     this.upgradeIndependentIpServer(
                        orderNo, orderServerx.getServerId(), memberIndependentOrderServerConfig.getIpSize(), memberIndependentOrderServerConfig.getRenewPrice()
                     );
                  }
               }

               if (order.getUpgradeItem().equals(UpgradeItemEnum.UPGRADE_BANDWIDTH.getCode())) {
                  if (this.isIndependentResourcePoolServer(orderServerx.getServerId())) {
                     JSONObject p = new JSONObject();
                     p.put("bandwidth", memberIndependentOrderServerConfig.getBandwidth());
                     this.createResourcePoolUpgradeIndependentRequest(order, orderServerx.getServerId(), UpgradeItemEnum.UPGRADE_BANDWIDTH, p);
                  } else {
                     this.upgradeIndependentBandwidthServer(
                        orderNo,
                        orderServerx.getServerId(),
                        memberIndependentOrderServerConfig.getBandwidth(),
                        memberIndependentOrderServerConfig.getRenewPrice()
                     );
                  }
               }

               if (order.getUpgradeItem().equals(UpgradeItemEnum.UPGRADE_DEFENSE.getCode())) {
                  if (this.isIndependentResourcePoolServer(orderServerx.getServerId())) {
                     JSONObject p = new JSONObject();
                     p.put("defense", memberIndependentOrderServerConfig.getDefense());
                     this.createResourcePoolUpgradeIndependentRequest(order, orderServerx.getServerId(), UpgradeItemEnum.UPGRADE_DEFENSE, p);
                  } else {
                     this.upgradeIndependentDefenseServer(
                        orderNo,
                        orderServerx.getServerId(),
                        memberIndependentOrderServerConfig.getDefense(),
                        memberIndependentOrderServerConfig.getRenewPrice()
                     );
                  }
               }
            }

            if (order.getOrderType().equals(OrderTypeEnum.RENEW.getCode())) {
               List<MemberOrderServer> orderServers = this.memberOrderServerService.queryByList(order.getOrderNo());
               if (orderServers == null || orderServers.isEmpty()) {
                  throw new RuntimeException("服务器订单不存在");
               }

               for (MemberOrderServer orderServerxx : orderServers) {
                  BaseMemberIndependentServer liveServerx = this.memberIndependentApi.selectByServerId(orderServerxx.getServerId());
                  BigDecimal itemPayPricex = this.resolveRenewItemPayPrice(
                     liveServerx == null ? null : liveServerx.getRenewPrice(), memberIndependentOrderServerConfig.getDuration()
                  );
                  if (this.isIndependentResourcePoolServer(orderServerxx.getServerId())) {
                     this.createResourcePoolRenewIndependentRequest(
                        order, orderServerxx.getServerId(), memberIndependentOrderServerConfig.getDuration(), itemPayPricex
                     );
                  }

                  if (!Boolean.TRUE
                     .equals(
                        this.memberIndependentApi
                           .renewServer(
                              orderNo,
                              orderServerxx.getServerId(),
                              memberIndependentOrderServerConfig.getDuration(),
                              memberIndependentOrderServerConfig.getUnit()
                           )
                     )) {
                     throw new RuntimeException("独立服务器续费失败");
                  }
               }
            }
         } else if (ProductTypeEnum.DOMAIN.getCode().equals(order.getProductType())) {
            log.info("后台处理域名订单，订单号:{}", orderNo);

            for (DomainOrderItem item : this.domainOrderItemService.listByOrderNo(orderNo)) {
               DomainRegistrationRequest req = new DomainRegistrationRequest();
               req.setId(IdWorker.getIdStr());
               req.setOrderNo(orderNo);
               req.setDomainName(item.getDomainName());
               req.setTemplateId(item.getTemplateId());
               DomainTemplate domainTemplate = (DomainTemplate)this.memberDomainTemplateMapper.selectById(item.getTemplateId());
               req.setTemplateName(domainTemplate != null ? domainTemplate.getName() : null);
               req.setYears(item.getYears() != null ? item.getYears() : 1);
               req.setCreateTime(new Date());
               req.setUpdateTime(new Date());

               try {
                  DomainCreateResult result = this.memberDomainRegisterService.registerDomain(item.getDomainName(), item.getTemplateId(), item.getYears());
                  req.setUpstreamRequestId(result.getRequestId());
                  req.setUpstreamCode(result.getCode());
                  req.setUpstreamMsg(result.getMsg());
                  req.setStatus("0".equals(result.getCode()) ? 1 : 2);
                  if (1 == req.getStatus()) {
                     int years = item.getYears() != null && item.getYears() >= 1 ? item.getYears() : 1;
                     this.businessDomainApi
                        .addRegisteredDomain(
                           String.valueOf(order.getMemberNo()),
                           item.getDomainName(),
                           item.getTld() != null ? item.getTld() : "",
                           years,
                           item.getTemplateId(),
                           "XINWANG"
                        );
                  }
               } catch (Exception var12) {
                  log.warn("域名注册上游请求失败 orderNo={} domain={} err={}", new Object[]{orderNo, item.getDomainName(), var12.getMessage()});
                  req.setStatus(2);
                  req.setUpstreamMsg(
                     var12.getMessage() != null && var12.getMessage().length() > 500 ? var12.getMessage().substring(0, 500) : var12.getMessage()
                  );
               }

               this.domainRegistrationRequestService.save(req);
            }
         } else if (ProductTypeEnum.DNS.getCode().equals(order.getProductType())) {
            log.info("后台处理DNS云解析订单，订单号:{}", orderNo);
            DnsOrderConfig dnsOrderConfig = this.dnsOrderConfigService.queryByOrderNo(orderNo);
            if (dnsOrderConfig == null) {
               throw new RuntimeException("DNS云解析订单详情不存在");
            }

            if (OrderTypeEnum.NEW_PURCHASE.getCode() == order.getOrderType()) {
               DnsPackageInstanceCreateParam createParam = new DnsPackageInstanceCreateParam();
               createParam.setMemberId(String.valueOf(order.getMemberNo()));
               createParam.setPackageId(dnsOrderConfig.getPackageId());
               createParam.setDomainQuota(dnsOrderConfig.getDomainQuota());
               createParam.setPeriodMonths(dnsOrderConfig.getPeriodMonths());
               createParam.setStartTime(new Date());
               createParam.setSourceOrderNo(orderNo);
               createParam.setRemark("订单开通：" + orderNo);
               this.dnsPackageInstanceApi.createInstanceFromOrder(createParam);
            } else if (OrderTypeEnum.RENEW.getCode() == order.getOrderType()) {
               DnsPackageInstanceRenewParam renewParam = new DnsPackageInstanceRenewParam();
               renewParam.setInstanceId(dnsOrderConfig.getInstanceId());
               renewParam.setMemberId(String.valueOf(order.getMemberNo()));
               renewParam.setPeriodMonths(dnsOrderConfig.getPeriodMonths());
               this.dnsPackageInstanceApi.renewInstanceFromOrder(renewParam);
            } else if (OrderTypeEnum.UPGRADE.getCode() == order.getOrderType()) {
               DnsPackageInstanceUpgradeQuotaParam upgradeParam = new DnsPackageInstanceUpgradeQuotaParam();
               upgradeParam.setInstanceId(dnsOrderConfig.getInstanceId());
               upgradeParam.setMemberId(String.valueOf(order.getMemberNo()));
               upgradeParam.setUpgradeDomainQuota(dnsOrderConfig.getUpgradeDomainQuota());
               this.dnsPackageInstanceApi.upgradeInstanceQuotaFromOrder(upgradeParam);
            }
         } else if (ProductTypeEnum.VHOST.getCode().equals(order.getProductType())) {
            this.fulfillMemberVhost(order);
         }

         long duration = System.currentTimeMillis() - startTime;
         log.info("后台处理已支付订单完成，订单号:{}, 耗时:{}ms", orderNo, duration);
      }
   }

   private void fulfillMemberVhost(MemberOrder order) {
      try {
         VhostOrderConfig config = this.vhostOrderConfigService.queryByOrderNo(order.getOrderNo());
         if (config == null) {
            log.error("虚拟主机订单配置不存在，订单号:{}", order.getOrderNo());
            return;
         }

         if (OrderTypeEnum.RENEW.getCode() == order.getOrderType()) {
            if (order.getMemberNo() != null && !StrUtil.isBlank(config.getHostId()) && config.getMonths() != null) {
               if (!Boolean.TRUE
                  .equals(this.memberVhostApi.renew(config.getHostId(), String.valueOf(order.getMemberNo()), config.getMonths(), order.getOrderNo(), "C"))) {
                  log.error("虚拟主机续费失败，订单号:{}", order.getOrderNo());
               }

               return;
            }

            log.error("虚拟主机续费参数不完整，订单号:{}", order.getOrderNo());
            return;
         }

         if (OrderTypeEnum.UPGRADE.getCode() == order.getOrderType()) {
            if (order.getMemberNo() != null && !StrUtil.isBlank(config.getHostId()) && config.getUpgradeTraffic() != null) {
               if (!Boolean.TRUE
                  .equals(
                     this.memberVhostApi
                        .upgradeTraffic(config.getHostId(), String.valueOf(order.getMemberNo()), config.getUpgradeTraffic(), order.getOrderNo())
                  )) {
                  log.error("虚拟主机升级流量失败，订单号:{}", order.getOrderNo());
               }

               return;
            }

            log.error("虚拟主机升级流量参数不完整，订单号:{}", order.getOrderNo());
            return;
         }

         this.createMemberVhost(order, config);
      } catch (Exception var3) {
         log.error("虚拟主机订单履约失败，订单号:{}", order.getOrderNo(), var3);
      }
   }

   private void createMemberVhost(MemberOrder order, VhostOrderConfig config) {
      if (order.getMemberNo() != null && config.getMonths() != null) {
         BaseMemberVhostCreate param = new BaseMemberVhostCreate();
         param.setMemberNo(String.valueOf(order.getMemberNo()));
         param.setOrderNo(order.getOrderNo());
         param.setVersionId(config.getVersionId());
         param.setVersionName(config.getVersionName());
         param.setRegionId(config.getRegionId());
         param.setRegionName(config.getRegionName());
         param.setLineId(config.getLineId());
         param.setLineName(config.getLineName());
         int number = config.getNumber() != null && config.getNumber() >= 1 ? config.getNumber() : 1;
         param.setNumber(number);
         param.setMonths(config.getMonths());
         param.setTrafficSize(config.getTrafficSize());
         param.setTrafficFreeSize(config.getTrafficFreeSize());
         param.setBillableTraffic(config.getBillableTraffic());
         param.setMonthPrice(config.getMonthPrice());
         if (order.getPayPrice() != null) {
            param.setPayPrice(order.getPayPrice().divide(BigDecimal.valueOf((long)number), 2, RoundingMode.HALF_UP));
         }

         param.setRenewPrice(config.getMonthPrice());
         param.setPhpVersion(config.getPhpVersion());
         param.setDomains(config.getDomains());
         param.setPlatform("C");
         Date start = order.getPayTime() != null ? order.getPayTime() : new Date();
         param.setEndTime(DateUtil.offsetMonth(start, config.getMonths()));
         if (!Boolean.TRUE.equals(this.memberVhostApi.create(param))) {
            log.error("创建虚拟主机实例失败，订单号:{}", order.getOrderNo());
         }
      } else {
         log.error("虚拟主机开通参数不完整，订单号:{}", order.getOrderNo());
      }
   }

   private void sendNotificationAsync(MemberOrder order) {
      this.notificationTaskExecutor
         .execute(
            () -> {
               try {
                  log.info("开始异步发送通知，订单号: {}", order.getOrderNo());
                  Map<String, Object> templateParams = new HashMap<>();
                  templateParams.put("orderNo", order.getOrderNo());
                  templateParams.put("orderAmount", order.getPayPrice());

                  try {
                     this.toolsNotificationApi
                        .sendMemberNotificationBoth(NotificationTypeEnum.MEMBER_ORDER_PAYMENT_SUCCESS.getCode(), order.getMemberNo(), templateParams);
                     log.info("会员通知发送成功，订单号: {}", order.getOrderNo());
                  } catch (Exception var5) {
                     log.error("发送会员通知失败，订单号:{}, 错误信息:{}", order.getOrderNo(), var5.getMessage());
                  }

                  try {
                     this.toolsNotificationApi.sendAdminNotificationBoth(NotificationTypeEnum.ADMIN_ORDER_PAYMENT_SUCCESS.getCode(), templateParams);
                     log.info("管理员通知发送成功，订单号: {}", order.getOrderNo());
                  } catch (Exception var4) {
                     log.error("发送管理员通知失败，订单号:{}, 错误信息:{}", order.getOrderNo(), var4.getMessage());
                  }
               } catch (Exception var6) {
                  log.error("异步发送通知异常，订单号:{}, 错误信息:{}", order.getOrderNo(), var6.getMessage());
               }
            }
         );
   }

   private void upgradeDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice) {
      this.memberServerApi.upgradeDefenseServer(orderNo, serverId, defense, renewPrice);
   }

   private void upgradeHardDisksServer(String orderNo, String serverId, Integer systemDisk, List<Integer> dataDisks, BigDecimal renewPrice) {
      this.memberServerApi.upgradeHardDisksServer(orderNo, serverId, systemDisk, dataDisks, renewPrice);
   }

   private void upgradeBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice) {
      this.memberServerApi.upgradeBandwidthServer(orderNo, serverId, bandwidth, renewPrice);
   }

   private void upgradeCpuRamServer(String orderNo, String serverId, Integer cpu, Integer ram, BigDecimal renewPrice) {
      this.memberServerApi.upgradeCpuRamServer(orderNo, serverId, cpu, ram, renewPrice);
   }

   public void createIndependentServer(MemberOrder order) {
      try {
         log.info("创建独立服务器开始，订单号:{}", JSON.toJSONString(order));
         MemberIndependentOrderServerConfig independentOrderServer = this.memberIndependentOrderServerConfigService.queryByOrderNo(order.getOrderNo());

         for (int i = 0; i < independentOrderServer.getNumber(); i++) {
            BaseMemberIndependentAddServer baseMemberAddServer = new BaseMemberIndependentAddServer();
            baseMemberAddServer.setId(SnowflakeIdGenerator.generateId());
            baseMemberAddServer.setMemberNo(order.getMemberNo());
            baseMemberAddServer.setMachineId(independentOrderServer.getMachineId());
            baseMemberAddServer.setMachineName(independentOrderServer.getMachineName());
            baseMemberAddServer.setServerName("pm_" + UniqueHostnameGenerator.generateUniqueHostname(10));
            baseMemberAddServer.setCpuInfo(independentOrderServer.getCpuInfo());
            baseMemberAddServer.setRamInfo(independentOrderServer.getRamInfo());
            baseMemberAddServer.setDisksInfo(independentOrderServer.getDisksInfo());
            baseMemberAddServer.setBandwidth(independentOrderServer.getBandwidth());
            baseMemberAddServer.setDefense(independentOrderServer.getDefense());
            baseMemberAddServer.setIpSize(independentOrderServer.getIpSize());
            baseMemberAddServer.setSystemPassword(independentOrderServer.getLoginPassword());
            if (null != independentOrderServer.getRegionId()) {
               BaseIndependentRegion independentRegion = this.independentRegionApi.selectByRegionId(independentOrderServer.getRegionId());
               baseMemberAddServer.setRegionId(independentRegion.getId());
               baseMemberAddServer.setRegionName(independentRegion.getName());
            }

            if (null != independentOrderServer.getLineId()) {
               BaseIndependentLine independentLine = this.independentLineApi.selectByLineId(independentOrderServer.getLineId());
               baseMemberAddServer.setLineId(independentLine.getId());
               baseMemberAddServer.setLineName(independentLine.getName());
            }

            BaseMemberIndependentSystemVersion memberOsVersion = this.memberIndependentSystemVersionApi
               .selectByOsVersionId(independentOrderServer.getSystemId());
            log.info("查询系统版本信息，系统id:{},系统版本信息:{}", independentOrderServer.getSystemId(), JSON.toJSONString(memberOsVersion));
            baseMemberAddServer.setSystemId(independentOrderServer.getSystemId());
            baseMemberAddServer.setSystemClassId(memberOsVersion.getOsClassId());
            baseMemberAddServer.setSystemClass(memberOsVersion.getOsClassName());
            baseMemberAddServer.setSystemName(memberOsVersion.getOsName());
            baseMemberAddServer.setPort(memberOsVersion.getDefaultPort());
            baseMemberAddServer.setSystemUserName(memberOsVersion.getDefaultUserName());
            baseMemberAddServer.setRenewPrice(independentOrderServer.getRenewPrice());
            baseMemberAddServer.setBuyTime(order.getPayTime());
            baseMemberAddServer.setEndTime(DateUtil.offsetMonth(order.getPayTime(), independentOrderServer.getDuration()));
            this.memberIndependentApi.createServer(baseMemberAddServer);
            MemberOrderServerAddParam orderServerAddParam = new MemberOrderServerAddParam();
            orderServerAddParam.setOrderNo(independentOrderServer.getOrderNo());
            orderServerAddParam.setServerId(baseMemberAddServer.getId());
            orderServerAddParam.setServerName(baseMemberAddServer.getServerName());
            log.info("创建服务器和订单关系，参数:{}", JSON.toJSONString(orderServerAddParam));
            this.memberOrderServerService.add(orderServerAddParam);

            try {
               Map<String, Object> templateParams = new HashMap<>();
               templateParams.put("serverName", baseMemberAddServer.getServerName());
               templateParams.put("serverId", baseMemberAddServer.getId());
               this.toolsNotificationApi
                  .sendMemberNotificationBoth(NotificationTypeEnum.MEMBER_DEDICATED_SERVER_ACTIVE.getCode(), order.getMemberNo(), templateParams);
               log.info("独立服务器开通成功通知已发送，服务器:{}", baseMemberAddServer.getServerName());
            } catch (Exception var8) {
               log.warn("独立服务器开通成功通知发送失败，服务器:{}，原因:{}", baseMemberAddServer.getServerName(), var8.getMessage());
            }
         }
      } catch (Exception var9) {
         log.error("创建独立服务器失败，订单号:{},异常信息:{}", order.getOrderNo(), var9.getMessage());
      }
   }

   public void createCloudServer(MemberOrder order) {
      try {
         log.info("创建云服务器开始，订单号:{}", JSON.toJSONString(order));
         MemberOrderServerConfig orderServer = this.orderServerConfigService.queryByOrderNo(order.getOrderNo());

         for (int i = 0; i < orderServer.getNumber(); i++) {
            BaseMemberLocalLine memberLocalLine = this.memberLocalLineApi.selectByLineId(orderServer.getLineId());
            BaseMemberSystemVersion memberOsVersion = this.memberOsVersionApi.selectByOsVersionId(orderServer.getSystemId());
            BaseMemberAddServer baseMemberAddServer = new BaseMemberAddServer();
            baseMemberAddServer.setId(SnowflakeIdGenerator.generateId());
            baseMemberAddServer.setMemberNo(order.getMemberNo());
            baseMemberAddServer.setHostName("ecs_" + UniqueHostnameGenerator.generateUniqueHostname(10));
            baseMemberAddServer.setBandwidth(orderServer.getBandwidth());
            baseMemberAddServer.setBandwidthIn(memberLocalLine.getBandwidthInValue());
            baseMemberAddServer.setCpu(orderServer.getCpu());
            baseMemberAddServer.setDefense(orderServer.getDefense());
            baseMemberAddServer.setIpNum(orderServer.getIpNum());
            baseMemberAddServer.setRam(orderServer.getRam());
            baseMemberAddServer.setLineId(orderServer.getLineId());
            baseMemberAddServer.setSystemDisk(orderServer.getSystemDisk());
            baseMemberAddServer.setDataDisks(orderServer.getDataDisks());
            baseMemberAddServer.setSystemPassword(orderServer.getLoginPassword());
            baseMemberAddServer.setImageDirectoryName(memberOsVersion.getImageDirectoryName());
            baseMemberAddServer.setSystemClassId(memberOsVersion.getOsClassId());
            baseMemberAddServer.setSystemClass(memberOsVersion.getOsClassName());
            baseMemberAddServer.setSystemId(memberOsVersion.getId());
            baseMemberAddServer.setSystemName(memberOsVersion.getOsName());
            baseMemberAddServer.setSystemUserName(memberOsVersion.getDefaultUserName());
            baseMemberAddServer.setPort(this.resolveCloudServerPort(memberOsVersion));
            baseMemberAddServer.setAvailableId(memberLocalLine.getAvailableId());
            baseMemberAddServer.setOrderNo(order.getOrderNo());
            baseMemberAddServer.setRenewPrice(orderServer.getRenewPrice());
            baseMemberAddServer.setBuyTime(order.getPayTime());
            baseMemberAddServer.setEndTime(DateUtil.offsetMonth(order.getPayTime(), orderServer.getDuration()));
            this.memberServerApi.createServer(baseMemberAddServer);
            MemberOrderServerAddParam orderServerAddParam = new MemberOrderServerAddParam();
            orderServerAddParam.setOrderNo(orderServer.getOrderNo());
            orderServerAddParam.setServerId(baseMemberAddServer.getId());
            orderServerAddParam.setServerName(baseMemberAddServer.getHostName());
            this.memberOrderServerService.add(orderServerAddParam);
         }
      } catch (Exception var8) {
         log.error("创建云服务器失败，订单号:{},异常信息:{}", order.getOrderNo(), var8.getMessage());
      }
   }

   private int resolveCloudServerPort(BaseMemberSystemVersion memberOsVersion) {
      return memberOsVersion == null
         ? 22
         : CloudServerPortUtil.resolvePort(memberOsVersion.getPortMode(), memberOsVersion.getPortRange(), memberOsVersion.getDefaultPort());
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
      MemberOrder order, MemberCalculatePriceResponse priceResponse, String couponCode, Integer productType, Integer orderType, String lineId, Long memberNo
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

   private void upgradeIndependentDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice) {
      if (!Boolean.TRUE.equals(this.memberIndependentApi.upgradeIndependentDefenseServer(orderNo, serverId, defense, renewPrice))) {
         throw new RuntimeException("独立服务器升级防御失败");
      }
   }

   private void upgradeIndependentBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice) {
      if (!Boolean.TRUE.equals(this.memberIndependentApi.upgradeIndependentBandwidthServer(orderNo, serverId, bandwidth, renewPrice))) {
         throw new RuntimeException("独立服务器升级带宽失败");
      }
   }

   private void upgradeIndependentIpServer(String orderNo, String serverId, Integer ipSize, BigDecimal renewPrice) {
      if (!Boolean.TRUE.equals(this.memberIndependentApi.upgradeIndependentIpServer(orderNo, serverId, ipSize, renewPrice))) {
         throw new RuntimeException("独立服务器升级IP失败");
      }
   }

   private boolean isResourcePoolType(String lineId, String machineId) {
      try {
         if (ObjectUtil.isNotEmpty(lineId)) {
            ServerLocalLine serverLocalLine = this.serverLocalLineService.queryEntity(lineId);
            if (serverLocalLine != null) {
               if (ObjectUtil.isNotEmpty(serverLocalLine.getType()) && serverLocalLine.getType() == 1) {
                  return true;
               }

               if (StrUtil.isNotBlank(serverLocalLine.getResourcePoolId())) {
                  return true;
               }
            }
         }

         if (ObjectUtil.isNotEmpty(machineId)) {
            BaseAdminMachine machine = this.adminMachineApi.findMachineById(machineId);
            if (machine != null) {
               if (ObjectUtil.isNotEmpty(machine.getType()) && machine.getType() == 1) {
                  return true;
               }

               if (StrUtil.isNotBlank(machine.getResourcePoolId())) {
                  return true;
               }
            }
         }
      } catch (Exception var4) {
         log.error("判断资源池类型失败，lineId：{}，machineId：{}，错误：{}", new Object[]{lineId, machineId, var4.getMessage(), var4});
      }

      return false;
   }

   private BigDecimal resolveRenewItemPayPrice(BigDecimal monthlyRenewPrice, Integer months) {
      return monthlyRenewPrice != null && months != null ? monthlyRenewPrice.multiply(BigDecimal.valueOf((long)months.intValue())) : null;
   }

   private void createResourcePoolRenewRequest(MemberOrder order, String serverId, Integer months) {
      this.createResourcePoolRenewRequest(order, serverId, months, null);
   }

   private void createResourcePoolRenewRequest(MemberOrder order, String serverId, Integer months, BigDecimal itemPayPrice) {
      try {
         log.info("创建资源池续费请求，订单号：{}，服务器 ID：{}", order.getOrderNo(), serverId);
         BaseMemberServer baseMemberServer = this.memberServerApi.selectByServerIdAndMemberNo(serverId, order.getMemberNo());
         if (baseMemberServer == null) {
            throw new RuntimeException("服务器不存在");
         } else {
            String resourcePoolId = null;
            if (ObjectUtil.isNotEmpty(baseMemberServer.getLineId())) {
               ServerLocalLine serverLocalLine = this.serverLocalLineService.queryEntity(baseMemberServer.getLineId());
               if (serverLocalLine != null && ObjectUtil.isNotEmpty(serverLocalLine.getResourcePoolId())) {
                  resourcePoolId = serverLocalLine.getResourcePoolId();
               }
            }

            if (ObjectUtil.isEmpty(resourcePoolId)) {
               log.error("未找到资源池 ID，订单号：{}，服务器 ID：{}", order.getOrderNo(), serverId);
               throw new RuntimeException("未找到资源池 ID");
            } else {
               String upstreamPk = StrUtil.trimToNull(baseMemberServer.getUpstreamServerId());
               if (StrUtil.isBlank(upstreamPk)) {
                  throw new RuntimeException("资源池云服务器缺少 UPSTREAM_SERVER_ID，无法请求上游续费");
               } else {
                  JSONObject requestParams = new JSONObject();
                  requestParams.put("downstreamOrderNo", order.getOrderNo());
                  requestParams.put("upstreamServerId", upstreamPk);
                  requestParams.put("months", months);
                  requestParams.put("billingMode", order.getBillingMode());
                  requestParams.put("memberNo", String.valueOf(order.getMemberNo()));
                  requestParams.put("memberName", order.getMemberName());
                  requestParams.put("memberPhone", order.getMemberPhone());
                  BigDecimal payPrice = itemPayPrice != null ? itemPayPrice : order.getPayPrice();
                  if (payPrice == null) {
                     MemberOrderServerConfig orderServerConfig = this.orderServerConfigService.queryByOrderNo(order.getOrderNo());
                     if (orderServerConfig != null) {
                        payPrice = orderServerConfig.getPayPrice();
                     }
                  }

                  if (payPrice != null) {
                     requestParams.put("payPrice", payPrice);
                  }

                  ResourcePoolRequest resourcePoolRequest = new ResourcePoolRequest();
                  resourcePoolRequest.setOrderNo(order.getOrderNo());
                  resourcePoolRequest.setMemberNo(order.getMemberNo());
                  resourcePoolRequest.setResourcePoolId(resourcePoolId);
                  resourcePoolRequest.setRequestParams(JSON.toJSONString(requestParams));
                  resourcePoolRequest.setOperationType("RENEW");
                  resourcePoolRequest.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
                  resourcePoolRequest.setAServerId(upstreamPk);
                  resourcePoolRequest.setMonths(months);
                  resourcePoolRequest.setRequestStatus(ResourcePoolRequestStatusEnum.PENDING.getCode());
                  this.resourcePoolRequestService.create(resourcePoolRequest);
                  log.info("资源池续费请求创建成功，订单号：{}，请求 ID：{}", order.getOrderNo(), resourcePoolRequest.getId());
                  this.scheduleResourcePoolRequestSendAfterCommit(resourcePoolRequest.getId());
               }
            }
         }
      } catch (Exception var11) {
         log.error("创建资源池续费请求失败，订单号：{}，错误：{}", new Object[]{order.getOrderNo(), var11.getMessage(), var11});
         throw new RuntimeException("创建资源池续费请求失败：" + var11.getMessage(), var11);
      }
   }

   private void createResourcePoolUpgradeRequest(MemberOrder order, String serverId, UpgradeItemEnum upgradeItem, JSONObject additionalParams) {
      try {
         log.info("创建资源池升级请求，订单号：{}，服务器 ID：{}，升级类型：{}", new Object[]{order.getOrderNo(), serverId, upgradeItem.getCode()});
         BaseMemberServer baseMemberServer = this.memberServerApi.selectByServerIdAndMemberNo(serverId, order.getMemberNo());
         if (baseMemberServer == null) {
            throw new RuntimeException("服务器不存在");
         } else {
            String resourcePoolId = null;
            if (ObjectUtil.isNotEmpty(baseMemberServer.getLineId())) {
               ServerLocalLine serverLocalLine = this.serverLocalLineService.queryEntity(baseMemberServer.getLineId());
               if (serverLocalLine != null && ObjectUtil.isNotEmpty(serverLocalLine.getResourcePoolId())) {
                  resourcePoolId = serverLocalLine.getResourcePoolId();
               }
            }

            if (ObjectUtil.isEmpty(resourcePoolId)) {
               log.error("未找到资源池 ID，订单号：{}，服务器 ID：{}", order.getOrderNo(), serverId);
               throw new RuntimeException("未找到资源池 ID");
            } else {
               String upstreamPk = StrUtil.trimToNull(baseMemberServer.getUpstreamServerId());
               if (StrUtil.isBlank(upstreamPk)) {
                  throw new RuntimeException("资源池云服务器缺少 UPSTREAM_SERVER_ID，无法请求上游升级");
               } else {
                  JSONObject requestParams = new JSONObject();
                  requestParams.put("downstreamOrderNo", order.getOrderNo());
                  requestParams.put("upstreamServerId", upstreamPk);
                  requestParams.put("upgradeType", upgradeItem.getCode());
                  requestParams.put("billingMode", order.getBillingMode());
                  requestParams.put("memberNo", String.valueOf(order.getMemberNo()));
                  requestParams.put("memberName", order.getMemberName());
                  requestParams.put("memberPhone", order.getMemberPhone());
                  BigDecimal payPrice = order.getPayPrice();
                  BigDecimal renewPrice = null;
                  if (payPrice == null) {
                     MemberOrderServerConfig orderServerConfig = this.orderServerConfigService.queryByOrderNo(order.getOrderNo());
                     if (orderServerConfig != null) {
                        payPrice = orderServerConfig.getPayPrice();
                        renewPrice = orderServerConfig.getRenewPrice();
                     }
                  }

                  if (renewPrice == null) {
                     MemberOrderServerConfig orderServerConfig = this.orderServerConfigService.queryByOrderNo(order.getOrderNo());
                     if (orderServerConfig != null) {
                        renewPrice = orderServerConfig.getRenewPrice();
                     }
                  }

                  if (payPrice != null) {
                     requestParams.put("payPrice", payPrice);
                  }

                  if (renewPrice != null) {
                     requestParams.put("renewPrice", renewPrice);
                  }

                  if (additionalParams != null && !additionalParams.isEmpty()) {
                     for (String key : additionalParams.keySet()) {
                        requestParams.put(key, additionalParams.get(key));
                     }
                  }

                  ResourcePoolRequest resourcePoolRequest = new ResourcePoolRequest();
                  resourcePoolRequest.setOrderNo(order.getOrderNo());
                  resourcePoolRequest.setMemberNo(order.getMemberNo());
                  resourcePoolRequest.setResourcePoolId(resourcePoolId);
                  resourcePoolRequest.setRequestParams(JSON.toJSONString(requestParams));
                  resourcePoolRequest.setOperationType(upgradeItem.getValue());
                  resourcePoolRequest.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
                  resourcePoolRequest.setRequestStatus(ResourcePoolRequestStatusEnum.PENDING.getCode());
                  this.resourcePoolRequestService.create(resourcePoolRequest);
                  log.info("资源池升级请求创建成功，订单号：{}，请求 ID：{}", order.getOrderNo(), resourcePoolRequest.getId());
                  this.scheduleResourcePoolRequestSendAfterCommit(resourcePoolRequest.getId());
               }
            }
         }
      } catch (Exception var13) {
         log.error("创建资源池升级请求失败，订单号：{}，错误：{}", new Object[]{order.getOrderNo(), var13.getMessage(), var13});
         throw new RuntimeException("创建资源池升级请求失败：" + var13.getMessage(), var13);
      }
   }

   private void createResourcePoolRequest(MemberOrder order, String lineId, String machineId) {
      try {
         log.info("创建资源池请求，订单号：{}，lineId：{}，machineId：{}", new Object[]{order.getOrderNo(), lineId, machineId});
         String resourcePoolId = null;
         JSONObject requestParams = new JSONObject();
         if (ObjectUtil.isNotEmpty(lineId)) {
            ServerLocalLine serverLocalLine = this.serverLocalLineService.queryEntity(lineId);
            if (serverLocalLine != null && ObjectUtil.isNotEmpty(serverLocalLine.getResourcePoolId())) {
               resourcePoolId = serverLocalLine.getResourcePoolId();
            }

            MemberOrderServerConfig orderServerConfig = this.orderServerConfigService.queryByOrderNo(order.getOrderNo());

            for (int i = 0; i < orderServerConfig.getNumber(); i++) {
               BaseMemberLocalLine memberLocalLine = this.memberLocalLineApi.selectByLineId(orderServerConfig.getLineId());
               BaseMemberSystemVersion memberOsVersion = this.memberOsVersionApi.selectByOsVersionId(orderServerConfig.getSystemId());
               Server server = new Server();
               String serverId = SnowflakeIdGenerator.generateId();
               server.setId(serverId);
               server.setOrderNo(order.getOrderNo());
               server.setMemberNo(String.valueOf(order.getMemberNo()));
               server.setHostName("ecs_" + UniqueHostnameGenerator.generateUniqueHostname(10));
               server.setBandwidth(orderServerConfig.getBandwidth());
               server.setBandwidthIn(memberLocalLine.getBandwidthInValue());
               server.setCpu(orderServerConfig.getCpu());
               server.setDefense(orderServerConfig.getDefense());
               server.setIpNum(orderServerConfig.getIpNum());
               server.setRam(orderServerConfig.getRam());
               server.setLineId(orderServerConfig.getLineId());
               server.setSystemDisk(orderServerConfig.getSystemDisk());
               server.setDataDisks(orderServerConfig.getDataDisks());
               server.setSystemPassword(orderServerConfig.getLoginPassword());
               server.setImageDirectoryName(memberOsVersion.getImageDirectoryName());
               server.setSystemClassId(memberOsVersion.getOsClassId());
               server.setSystemClass(memberOsVersion.getOsClassName());
               server.setSystemId(memberOsVersion.getId());
               server.setSystemName(memberOsVersion.getOsName());
               server.setSystemUserName(memberOsVersion.getDefaultUserName());
               server.setPort(this.resolveCloudServerPort(memberOsVersion));
               server.setAvailableId(memberLocalLine.getAvailableId());
               server.setRenewPrice(orderServerConfig.getRenewPrice());
               server.setBuyTime(order.getPayTime());
               server.setEndTime(DateUtil.offsetMonth(order.getPayTime(), orderServerConfig.getDuration()));
               server.setType(1);
               server.setResourcePoolId(resourcePoolId);
               server.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
               server.setServerStatus(ServerStatusEnum.CREATING.getCode());
               DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
               String cloudServerRetentionPeriod = devConfigApi.getValueByKey("CLOUD_SERVER_RETENTION_PERIOD");
               if (ObjectUtil.isNotEmpty(cloudServerRetentionPeriod)) {
                  server.setDeleteTime(DateUtil.offsetDay(server.getEndTime(), Integer.valueOf(cloudServerRetentionPeriod)));
               }

               this.serverService.save(server);
               log.info("创建资源池服务器记录成功，服务器ID：{}，订单号：{}，资源池ID：{}", new Object[]{serverId, order.getOrderNo(), resourcePoolId});
               MemberOrderServerAddParam orderServerAddParam = new MemberOrderServerAddParam();
               orderServerAddParam.setOrderNo(orderServerConfig.getOrderNo());
               orderServerAddParam.setServerId(serverId);
               orderServerAddParam.setServerName(server.getHostName());
               this.memberOrderServerService.add(orderServerAddParam);
               log.info("创建服务器和订单关系成功，服务器ID：{}，订单号：{}", serverId, order.getOrderNo());
            }

            requestParams.put("orderNo", order.getOrderNo());
            requestParams.put("lineId", lineId);
            requestParams.put("cpu", orderServerConfig.getCpu());
            requestParams.put("ram", orderServerConfig.getRam());
            requestParams.put("systemDisk", orderServerConfig.getSystemDisk());
            requestParams.put("dataDisks", orderServerConfig.getDataDisks());
            requestParams.put("bandwidth", orderServerConfig.getBandwidth());
            requestParams.put("defense", orderServerConfig.getDefense());
            requestParams.put("ipNum", orderServerConfig.getIpNum());
            requestParams.put("systemId", orderServerConfig.getSystemId());
            requestParams.put("systemPassword", orderServerConfig.getLoginPassword());
            requestParams.put("memberNo", order.getMemberNo());
            requestParams.put("duration", orderServerConfig.getDuration());
            requestParams.put("number", orderServerConfig.getNumber());
         } else if (ObjectUtil.isNotEmpty(machineId)) {
            Machine machine = (Machine)((ServiceImpl)this.machineService).getById(machineId);
            if (machine != null && ObjectUtil.isNotEmpty(machine.getResourcePoolId())) {
               resourcePoolId = machine.getResourcePoolId();
            }

            IndependentOrderServerConfigResponse orderConfig = this.memberIndependentOrderServerConfigService.queryByOrder(order.getOrderNo());
            if (orderConfig == null) {
               throw new RuntimeException("独立服务器订单配置不存在");
            }

            if (StrUtil.isNotBlank(resourcePoolId)) {
               BaseAdminMachine adminMachine = this.adminMachineApi.findMachineById(machineId);

               for (int i = 0; i < orderConfig.getNumber(); i++) {
                  BaseMemberIndependentAddServer baseMemberAddServer = new BaseMemberIndependentAddServer();
                  baseMemberAddServer.setId(SnowflakeIdGenerator.generateId());
                  baseMemberAddServer.setMemberNo(order.getMemberNo());
                  baseMemberAddServer.setMachineId(orderConfig.getMachineId());
                  baseMemberAddServer.setMachineName(orderConfig.getMachineName());
                  baseMemberAddServer.setServerName("pm_" + UniqueHostnameGenerator.generateUniqueHostname(10));
                  baseMemberAddServer.setCpuInfo(orderConfig.getCpuInfo());
                  baseMemberAddServer.setRamInfo(orderConfig.getRamInfo());
                  baseMemberAddServer.setDisksInfo(orderConfig.getDisksInfo());
                  baseMemberAddServer.setBandwidth(orderConfig.getBandwidth());
                  baseMemberAddServer.setDefense(orderConfig.getDefense());
                  baseMemberAddServer.setIpSize(orderConfig.getIpSize());
                  baseMemberAddServer.setSystemPassword(orderConfig.getLoginPassword());
                  baseMemberAddServer.setType(1);
                  baseMemberAddServer.setResourcePoolId(resourcePoolId);
                  if (adminMachine != null) {
                     baseMemberAddServer.setAutomation(adminMachine.getAutomation());
                  }

                  if (null != orderConfig.getRegionId()) {
                     BaseIndependentRegion independentRegion = this.independentRegionApi.selectByRegionId(orderConfig.getRegionId());
                     baseMemberAddServer.setRegionId(independentRegion.getId());
                     baseMemberAddServer.setRegionName(independentRegion.getName());
                  }

                  if (null != orderConfig.getLineId()) {
                     BaseIndependentLine independentLine = this.independentLineApi.selectByLineId(orderConfig.getLineId());
                     baseMemberAddServer.setLineId(independentLine.getId());
                     baseMemberAddServer.setLineName(independentLine.getName());
                  }

                  BaseMemberIndependentSystemVersion memberOsVersion = this.memberIndependentSystemVersionApi.selectByOsVersionId(orderConfig.getSystemId());
                  baseMemberAddServer.setSystemId(orderConfig.getSystemId());
                  baseMemberAddServer.setSystemClassId(memberOsVersion.getOsClassId());
                  baseMemberAddServer.setSystemClass(memberOsVersion.getOsClassName());
                  baseMemberAddServer.setSystemName(memberOsVersion.getOsName());
                  baseMemberAddServer.setPort(memberOsVersion.getDefaultPort());
                  baseMemberAddServer.setSystemUserName(memberOsVersion.getDefaultUserName());
                  baseMemberAddServer.setRenewPrice(orderConfig.getRenewPrice());
                  baseMemberAddServer.setBuyTime(order.getPayTime());
                  baseMemberAddServer.setEndTime(DateUtil.offsetMonth(order.getPayTime(), orderConfig.getDuration()));
                  if (!Boolean.TRUE.equals(this.memberIndependentApi.savePoolPendingServer(baseMemberAddServer))) {
                     throw new RuntimeException("资源池独立服务器占位失败");
                  }

                  MemberOrderServerAddParam orderServerAddParam = new MemberOrderServerAddParam();
                  orderServerAddParam.setOrderNo(order.getOrderNo());
                  orderServerAddParam.setServerId(baseMemberAddServer.getId());
                  orderServerAddParam.setServerName(baseMemberAddServer.getServerName());
                  this.memberOrderServerService.add(orderServerAddParam);
               }
            }

            String systemClassIdForUpstream = orderConfig.getSystemClassId();
            if (StrUtil.isBlank(systemClassIdForUpstream) && ObjectUtil.isNotEmpty(orderConfig.getSystemId())) {
               BaseMemberIndependentSystemVersion ver = this.memberIndependentSystemVersionApi.selectByOsVersionId(orderConfig.getSystemId());
               if (ver != null) {
                  systemClassIdForUpstream = ver.getOsClassId();
               }
            }

            requestParams.put("orderNo", order.getOrderNo());
            requestParams.put("machineId", machineId);
            requestParams.put("regionId", orderConfig.getRegionId());
            requestParams.put("lineId", orderConfig.getLineId());
            requestParams.put("systemId", orderConfig.getSystemId());
            requestParams.put("osVersionId", orderConfig.getSystemId());
            if (StrUtil.isNotBlank(systemClassIdForUpstream)) {
               requestParams.put("systemClassId", systemClassIdForUpstream);
               requestParams.put("osClassId", systemClassIdForUpstream);
            }

            requestParams.put("systemPassword", orderConfig.getLoginPassword());
            requestParams.put("memberNo", order.getMemberNo());
            requestParams.put("duration", orderConfig.getDuration());
            requestParams.put("number", orderConfig.getNumber());
            if (orderConfig.getBandwidth() != null) {
               requestParams.put("bandwidth", orderConfig.getBandwidth());
            }

            if (orderConfig.getDefense() != null) {
               requestParams.put("defense", orderConfig.getDefense());
            }

            if (orderConfig.getIpSize() != null) {
               requestParams.put("ipSize", orderConfig.getIpSize());
            }

            if (StrUtil.isNotBlank(orderConfig.getUnit())) {
               requestParams.put("unit", orderConfig.getUnit());
            }

            if (order.getBillingMode() != null) {
               requestParams.put("billingMode", order.getBillingMode());
            }
         }

         if (ObjectUtil.isEmpty(resourcePoolId)) {
            log.error("未找到资源池ID，订单号：{}", order.getOrderNo());
            throw new RuntimeException("未找到资源池ID");
         } else {
            ResourcePoolRequest resourcePoolRequest = new ResourcePoolRequest();
            resourcePoolRequest.setOrderNo(order.getOrderNo());
            resourcePoolRequest.setMemberNo(order.getMemberNo());
            resourcePoolRequest.setResourcePoolId(resourcePoolId);
            resourcePoolRequest.setRequestParams(JSON.toJSONString(requestParams));
            resourcePoolRequest.setRequestStatus(ResourcePoolRequestStatusEnum.PENDING.getCode());
            resourcePoolRequest.setOperationType("CREATE");
            resourcePoolRequest.setProductType(order.getProductType());
            this.resourcePoolRequestService.create(resourcePoolRequest);
            log.info("资源池请求创建成功，订单号：{}，请求ID：{}", order.getOrderNo(), resourcePoolRequest.getId());
            this.scheduleResourcePoolRequestSendAfterCommit(resourcePoolRequest.getId());
         }
      } catch (Exception var16) {
         log.error("创建资源池请求失败，订单号：{}，错误：{}", new Object[]{order.getOrderNo(), var16.getMessage(), var16});
         throw new RuntimeException("创建资源池请求失败：" + var16.getMessage(), var16);
      }
   }

   private boolean isIndependentResourcePoolServer(String serverId) {
      try {
         BaseMemberIndependentServer s = this.memberIndependentApi.selectByServerId(serverId);
         if (s == null) {
            return false;
         }

         if (ObjectUtil.isNotEmpty(s.getType()) && s.getType() == 1) {
            return true;
         }

         if (StrUtil.isNotBlank(s.getResourcePoolId())) {
            return true;
         }

         if (StrUtil.isNotBlank(s.getMachineId())) {
            BaseAdminMachine m = this.adminMachineApi.findMachineById(s.getMachineId());
            if (m != null) {
               if (ObjectUtil.isNotEmpty(m.getType()) && m.getType() == 1) {
                  return true;
               }

               if (StrUtil.isNotBlank(m.getResourcePoolId())) {
                  return true;
               }
            }
         }
      } catch (Exception var4) {
         log.error("判断独立服务器资源池失败，serverId：{}", serverId, var4);
      }

      return false;
   }

   private String resolveIndependentResourcePoolId(BaseMemberIndependentServer s) {
      if (s == null) {
         return null;
      } else if (StrUtil.isNotBlank(s.getResourcePoolId())) {
         return s.getResourcePoolId();
      } else {
         if (StrUtil.isNotBlank(s.getMachineId())) {
            try {
               BaseAdminMachine m = this.adminMachineApi.findMachineById(s.getMachineId());
               if (m != null && StrUtil.isNotBlank(m.getResourcePoolId())) {
                  return m.getResourcePoolId();
               }
            } catch (Exception var3) {
            }
         }

         return null;
      }
   }

   private void createResourcePoolRenewIndependentRequest(MemberOrder order, String serverId, Integer months) {
      this.createResourcePoolRenewIndependentRequest(order, serverId, months, null);
   }

   private void createResourcePoolRenewIndependentRequest(MemberOrder order, String serverId, Integer months, BigDecimal itemPayPrice) {
      try {
         log.info("创建资源池独立服务器续费请求，订单号：{}，服务器 ID：{}", order.getOrderNo(), serverId);
         BaseMemberIndependentServer memberServer = this.memberIndependentApi.selectByServerId(serverId);
         if (memberServer == null) {
            throw new RuntimeException("服务器不存在");
         } else if (!Objects.equals(order.getMemberNo(), memberServer.getMemberNo())) {
            throw new RuntimeException("服务器与订单会员不一致");
         } else {
            String resourcePoolId = this.resolveIndependentResourcePoolId(memberServer);
            if (ObjectUtil.isEmpty(resourcePoolId)) {
               log.error("未找到资源池 ID，订单号：{}，服务器 ID：{}", order.getOrderNo(), serverId);
               throw new RuntimeException("未找到资源池 ID");
            } else {
               String upstreamPk = StrUtil.trimToNull(memberServer.getUpstreamServerId());
               if (StrUtil.isBlank(upstreamPk)) {
                  throw new RuntimeException("资源池独立服务器缺少 UPSTREAM_SERVER_ID，无法请求上游续费");
               } else {
                  JSONObject requestParams = new JSONObject();
                  requestParams.put("downstreamOrderNo", order.getOrderNo());
                  requestParams.put("upstreamServerId", upstreamPk);
                  requestParams.put("months", months);
                  requestParams.put("billingMode", order.getBillingMode());
                  requestParams.put("memberNo", String.valueOf(order.getMemberNo()));
                  requestParams.put("memberName", order.getMemberName());
                  requestParams.put("memberPhone", order.getMemberPhone());
                  BigDecimal payPrice = itemPayPrice != null ? itemPayPrice : order.getPayPrice();
                  if (payPrice == null) {
                     MemberIndependentOrderServerConfig cfg = this.memberIndependentOrderServerConfigService.queryByOrderNo(order.getOrderNo());
                     if (cfg != null) {
                        payPrice = cfg.getPayPrice();
                     }
                  }

                  if (payPrice != null) {
                     requestParams.put("payPrice", payPrice);
                  }

                  ResourcePoolRequest resourcePoolRequest = new ResourcePoolRequest();
                  resourcePoolRequest.setOrderNo(order.getOrderNo());
                  resourcePoolRequest.setMemberNo(order.getMemberNo());
                  resourcePoolRequest.setResourcePoolId(resourcePoolId);
                  resourcePoolRequest.setRequestParams(JSON.toJSONString(requestParams));
                  resourcePoolRequest.setOperationType("RENEW");
                  resourcePoolRequest.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
                  resourcePoolRequest.setAServerId(upstreamPk);
                  resourcePoolRequest.setMonths(months);
                  resourcePoolRequest.setRequestStatus(ResourcePoolRequestStatusEnum.PENDING.getCode());
                  this.resourcePoolRequestService.create(resourcePoolRequest);
                  log.info("资源池独立服务器续费请求创建成功，订单号：{}，请求 ID：{}", order.getOrderNo(), resourcePoolRequest.getId());
                  this.scheduleResourcePoolRequestSendAfterCommit(resourcePoolRequest.getId());
               }
            }
         }
      } catch (Exception var11) {
         log.error("创建资源池独立服务器续费请求失败，订单号：{}，错误：{}", new Object[]{order.getOrderNo(), var11.getMessage(), var11});
         throw new RuntimeException("创建资源池独立服务器续费请求失败：" + var11.getMessage(), var11);
      }
   }

   private void createResourcePoolUpgradeIndependentRequest(MemberOrder order, String serverId, UpgradeItemEnum upgradeItem, JSONObject additionalParams) {
      try {
         log.info("创建资源池独立服务器升级请求，订单号：{}，服务器 ID：{}，升级类型：{}", new Object[]{order.getOrderNo(), serverId, upgradeItem.getCode()});
         BaseMemberIndependentServer memberServer = this.memberIndependentApi.selectByServerId(serverId);
         if (memberServer == null) {
            throw new RuntimeException("服务器不存在");
         } else if (!Objects.equals(order.getMemberNo(), memberServer.getMemberNo())) {
            throw new RuntimeException("服务器与订单会员不一致");
         } else {
            String resourcePoolId = this.resolveIndependentResourcePoolId(memberServer);
            if (ObjectUtil.isEmpty(resourcePoolId)) {
               log.error("未找到资源池 ID，订单号：{}，服务器 ID：{}", order.getOrderNo(), serverId);
               throw new RuntimeException("未找到资源池 ID");
            } else {
               String upstreamPk = StrUtil.trimToNull(memberServer.getUpstreamServerId());
               if (StrUtil.isBlank(upstreamPk)) {
                  throw new RuntimeException("资源池独立服务器缺少 UPSTREAM_SERVER_ID，无法请求上游升级");
               } else {
                  JSONObject requestParams = new JSONObject();
                  requestParams.put("downstreamOrderNo", order.getOrderNo());
                  requestParams.put("upstreamServerId", upstreamPk);
                  requestParams.put("upgradeType", upgradeItem.getCode());
                  requestParams.put("billingMode", order.getBillingMode());
                  requestParams.put("memberNo", String.valueOf(order.getMemberNo()));
                  requestParams.put("memberName", order.getMemberName());
                  requestParams.put("memberPhone", order.getMemberPhone());
                  BigDecimal payPrice = order.getPayPrice();
                  BigDecimal renewPrice = null;
                  if (payPrice == null) {
                     MemberIndependentOrderServerConfig cfg = this.memberIndependentOrderServerConfigService.queryByOrderNo(order.getOrderNo());
                     if (cfg != null) {
                        payPrice = cfg.getPayPrice();
                        renewPrice = cfg.getRenewPrice();
                     }
                  }

                  if (renewPrice == null) {
                     MemberIndependentOrderServerConfig cfg = this.memberIndependentOrderServerConfigService.queryByOrderNo(order.getOrderNo());
                     if (cfg != null) {
                        renewPrice = cfg.getRenewPrice();
                     }
                  }

                  if (payPrice != null) {
                     requestParams.put("payPrice", payPrice);
                  }

                  if (renewPrice != null) {
                     requestParams.put("renewPrice", renewPrice);
                  }

                  if (additionalParams != null && !additionalParams.isEmpty()) {
                     for (String key : additionalParams.keySet()) {
                        requestParams.put(key, additionalParams.get(key));
                     }
                  }

                  ResourcePoolRequest resourcePoolRequest = new ResourcePoolRequest();
                  resourcePoolRequest.setOrderNo(order.getOrderNo());
                  resourcePoolRequest.setMemberNo(order.getMemberNo());
                  resourcePoolRequest.setResourcePoolId(resourcePoolId);
                  resourcePoolRequest.setRequestParams(JSON.toJSONString(requestParams));
                  resourcePoolRequest.setOperationType(upgradeItem.getValue());
                  resourcePoolRequest.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
                  resourcePoolRequest.setRequestStatus(ResourcePoolRequestStatusEnum.PENDING.getCode());
                  this.resourcePoolRequestService.create(resourcePoolRequest);
                  log.info("资源池独立服务器升级请求创建成功，订单号：{}，请求 ID：{}", order.getOrderNo(), resourcePoolRequest.getId());
                  this.scheduleResourcePoolRequestSendAfterCommit(resourcePoolRequest.getId());
               }
            }
         }
      } catch (Exception var13) {
         log.error("创建资源池独立服务器升级请求失败，订单号：{}，错误：{}", new Object[]{order.getOrderNo(), var13.getMessage(), var13});
         throw new RuntimeException("创建资源池独立服务器升级请求失败：" + var13.getMessage(), var13);
      }
   }

   private void scheduleResourcePoolRequestSendAfterCommit(String requestId) {
      if (TransactionSynchronizationManager.isSynchronizationActive()) {
         TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            public void afterCommit() {
               MemberOrderServiceImpl.this.sendResourcePoolRequestAsync(requestId);
            }
         });
      } else {
         this.sendResourcePoolRequestAsync(requestId);
      }
   }

   private void sendResourcePoolRequestAsync(String requestId) {
      this.resourcePoolTaskExecutor.execute(() -> {
         try {
            ResourcePoolRequest request = this.resourcePoolRequestService.getById(requestId);
            if (request != null) {
               this.resourcePoolRequestHandler.sendRequest(request);
            }
         } catch (Exception var3) {
            log.error("异步发送资源池请求失败，请求ID：{}，错误：{}", new Object[]{requestId, var3.getMessage(), var3});
         }
      });
   }

   private CommonResult<MemberOrder> validateCloudCoreUpgrade(BaseMemberServer server, Integer targetCpu, Integer targetRam) {
      if (targetCpu < server.getCpu()) {
         return CommonResult.error("升级的CPU不能小于当前服务器");
      } else if (targetRam < server.getRam()) {
         return CommonResult.error("升级的内存不能小于当前服务器");
      } else {
         return Objects.equals(targetCpu, server.getCpu()) && Objects.equals(targetRam, server.getRam()) ? CommonResult.error("升级的CPU和内存至少有一项大于当前服务器") : null;
      }
   }

   private CommonResult<MemberOrder> validateCloudDiskUpgrade(BaseMemberServer server, Integer targetSystemDisk, List<Integer> targetDataDisks) {
      if (targetSystemDisk < server.getSystemDisk()) {
         return CommonResult.error("升级的系统盘不能小于当前服务器");
      } else {
         int currentDataDiskTotal = this.sumDisks(server.getDataDisks());
         int targetDataDiskTotal = this.sumDisks(targetDataDisks);
         if (targetDataDiskTotal < currentDataDiskTotal) {
            return CommonResult.error("升级的数据盘总容量不能小于当前服务器");
         } else {
            return Objects.equals(targetSystemDisk, server.getSystemDisk()) && targetDataDiskTotal == currentDataDiskTotal
               ? CommonResult.error("升级的系统盘和数据盘至少有一项大于当前服务器")
               : null;
         }
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
