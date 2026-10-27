package com.rxinyun.admin.order.modular.ecs.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.order.modular.dns.entity.AdminDnsOrderConfig;
import com.rxinyun.admin.order.modular.dns.mapper.AdminDnsOrderConfigMapper;
import com.rxinyun.admin.order.modular.ecs.entity.DomainOrderItem;
import com.rxinyun.admin.order.modular.ecs.entity.Order;
import com.rxinyun.admin.order.modular.ecs.entity.OrderServer;
import com.rxinyun.admin.order.modular.ecs.mapper.AdminDomainOrderItemMapper;
import com.rxinyun.admin.order.modular.ecs.mapper.OrderMapper;
import com.rxinyun.admin.order.modular.ecs.param.OrderIdParam;
import com.rxinyun.admin.order.modular.ecs.param.OrderPageParam;
import com.rxinyun.admin.order.modular.ecs.param.OrderServerAddParam;
import com.rxinyun.admin.order.modular.ecs.response.DomainOrderItemResponse;
import com.rxinyun.admin.order.modular.ecs.response.IndependentOrderServerConfigResponse;
import com.rxinyun.admin.order.modular.ecs.response.OrderResponse;
import com.rxinyun.admin.order.modular.ecs.response.OrderServerConfigResponse;
import com.rxinyun.admin.order.modular.ecs.service.IndependentOrderServerConfigService;
import com.rxinyun.admin.order.modular.ecs.service.OrderServerConfigService;
import com.rxinyun.admin.order.modular.ecs.service.OrderServerService;
import com.rxinyun.admin.order.modular.ecs.service.OrderService;
import com.rxinyun.admin.order.modular.invoice.entity.OrderInvoice;
import com.rxinyun.admin.order.modular.invoice.service.OrderInvoiceService;
import com.rxinyun.admin.order.modular.vhost.entity.AdminVhostOrderConfig;
import com.rxinyun.admin.order.modular.vhost.mapper.AdminVhostOrderConfigMapper;
import com.rxinyun.business.api.IndependentApi;
import com.rxinyun.business.api.MemberVhostApi;
import com.rxinyun.business.api.ServerApi;
import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.business.core.pojo.BaseMemberAddServer;
import com.rxinyun.business.core.pojo.BaseMemberIndependentAddServer;
import com.rxinyun.business.core.pojo.BaseMemberVhost;
import com.rxinyun.business.core.pojo.BaseMemberVhostCreate;
import com.rxinyun.business.core.pojo.BaseServer;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.NotificationTypeEnum;
import com.rxinyun.common.enums.UpgradeItemEnum;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.PayTypeEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.enums.order.InvoiceStatusEnum;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.CloudServerPortUtil;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import com.rxinyun.common.util.UniqueHostnameGenerator;
import com.rxinyun.common.util.date.enums.DateResponseKeyEnum;
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.dev.api.ResourcePoolRequestHandlerApi;
import com.rxinyun.dev.api.ToolsNotificationApi;
import com.rxinyun.dev.core.pojo.BaseResourcePoolRequest;
import com.rxinyun.marketing.api.MemberCouponApi;
import com.rxinyun.order.api.OrderApi;
import com.rxinyun.order.core.pojo.BaseOrder;
import com.rxinyun.production.admin.IndependentSystemVersionApi;
import com.rxinyun.production.admin.ServerLocalLineApi;
import com.rxinyun.production.admin.core.pojo.BaseIndependentSystemVersion;
import com.rxinyun.production.admin.core.pojo.BaseLocalLine;
import com.rxinyun.production.api.AdminMachineApi;
import com.rxinyun.production.api.DnsPackageInstanceApi;
import com.rxinyun.production.api.IndependentLineApi;
import com.rxinyun.production.api.IndependentRegionApi;
import com.rxinyun.production.api.MemberSystemVersionApi;
import com.rxinyun.production.api.dto.DnsPackageInstanceVO;
import com.rxinyun.production.core.pojo.BaseAdminMachine;
import com.rxinyun.production.core.pojo.BaseIndependentLine;
import com.rxinyun.production.core.pojo.BaseIndependentRegion;
import com.rxinyun.production.core.pojo.BaseMemberSystemVersion;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService, OrderApi {
   private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
   @Resource
   private OrderMapper orderMapper;
   @Resource
   private IndependentOrderServerConfigService independentOrderServerConfigService;
   @Resource
   private OrderServerConfigService orderServerConfigService;
   @Resource
   private OrderServerService orderServerService;
   @Resource
   private MemberSystemVersionApi memberOsVersionApi;
   @Resource
   private AdminDomainOrderItemMapper adminDomainOrderItemMapper;
   @Resource
   private AdminDnsOrderConfigMapper adminDnsOrderConfigMapper;
   @Resource
   private AdminVhostOrderConfigMapper adminVhostOrderConfigMapper;
   @Resource
   private DnsPackageInstanceApi dnsPackageInstanceApi;
   @Resource
   private MemberVhostApi memberVhostApi;
   @Autowired
   private ToolsNotificationApi toolsNotificationApi;
   @Autowired
   private ServerLocalLineApi serverLocalLineApi;
   @Autowired
   private AdminMachineApi adminMachineApi;
   @Autowired
   private ServerApi serverApi;
   @Autowired
   private IndependentApi independentApi;
   @Autowired
   private IndependentRegionApi independentRegionApi;
   @Autowired
   private IndependentLineApi independentLineApi;
   @Autowired
   private IndependentSystemVersionApi independentSystemVersionApi;
   @Resource
   private ResourcePoolApi resourcePoolApi;
   @Resource(
      name = "orderTaskExecutor"
   )
   private Executor orderTaskExecutor;
   @Resource(
      name = "notificationTaskExecutor"
   )
   private Executor notificationTaskExecutor;
   @Resource
   private OrderInvoiceService orderInvoiceService;
   @Resource
   private MemberCouponApi memberCouponApi;
   @Resource(
      name = "resourcePoolTaskExecutor"
   )
   private Executor resourcePoolTaskExecutor;
   @Resource(
      name = "resourcePoolRequestHandlerApiProvider"
   )
   private ResourcePoolRequestHandlerApi resourcePoolRequestHandlerApi;
   private static final String INVOICABLE_OCCUPIED_NOT_EXISTS_SQL = "NOT EXISTS (SELECT 1 FROM ORDER_INVOICE_ITEM i INNER JOIN ORDER_INVOICE inv ON inv.ID = i.INVOICE_ID WHERE i.ORDER_NO = ORDERS.ORDER_NO AND inv.STATUS IN (0, 1))";
   private static final String INVOICABLE_REFUND_NOT_EXISTS_SQL = "NOT EXISTS (SELECT 1 FROM SERVER_REFUND_RECORD r WHERE r.STATUS = 1 AND (EXISTS (SELECT 1 FROM ORDER_SERVER os WHERE os.ORDER_NO = ORDERS.ORDER_NO AND os.SERVER_ID = r.SERVER_ID) OR (ORDERS.UPSTREAM_SERVER_IDS IS NOT NULL AND ORDERS.UPSTREAM_SERVER_IDS LIKE CONCAT('%', r.SERVER_ID, '%'))))";

   public Long OrderCountByCreateTimeStatus(HashMap<String, LocalDateTime> hashMap) {
      LocalDateTime start = hashMap.get(DateResponseKeyEnum.START_TIME_NAME.getValue());
      LocalDateTime end = hashMap.get(DateResponseKeyEnum.END_TIME_NAME.getValue());
      QueryWrapper<Order> queryWrapper = new QueryWrapper();
      ((QueryWrapper)((QueryWrapper)((QueryWrapper)queryWrapper.eq("order_status", OrderStatusEnum.PAID.getCode())).ge("create_time", start))
            .le("create_time", end))
         .select(new String[]{"DISTINCT member_no"});
      return this.count(queryWrapper);
   }

   public BigDecimal OrderPayByCreateTime(HashMap<String, LocalDateTime> hashMap, Long userId) {
      LocalDateTime start = hashMap.get(DateResponseKeyEnum.START_TIME_NAME.getValue());
      LocalDateTime end = hashMap.get(DateResponseKeyEnum.END_TIME_NAME.getValue());
      BigDecimal totalPrice = new BigDecimal(0);
      LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper();
      ((LambdaQueryWrapper<Order>)((LambdaQueryWrapper<Order>)((LambdaQueryWrapper<Order>)queryWrapper.ge(start != null, Order::getCreateTime, start))
               .le(end != null, Order::getCreateTime, end))
            .eq(userId != null, Order::getMemberNo, userId))
         .eq(Order::getOrderStatus, OrderStatusEnum.PAID.getCode());

      for (Order order : this.list(queryWrapper)) {
         totalPrice = totalPrice.add(order.getPayPrice());
      }

      return totalPrice;
   }

   public Long OrderCountByCreateTime(HashMap<String, LocalDateTime> hashMap) {
      LocalDateTime start = hashMap.get(DateResponseKeyEnum.START_TIME_NAME.getValue());
      LocalDateTime end = hashMap.get(DateResponseKeyEnum.END_TIME_NAME.getValue());
      return ((this.lambdaQuery().ge(start != null, Order::getCreateTime, start))
            .le(end != null, Order::getCreateTime, end))
         .count();
   }

   public BigDecimal OrderPayByCreateTimeAndStatus(HashMap<String, LocalDateTime> hashMap) {
      LocalDateTime start = hashMap.get(DateResponseKeyEnum.START_TIME_NAME.getValue());
      LocalDateTime end = hashMap.get(DateResponseKeyEnum.END_TIME_NAME.getValue());
      LambdaQueryWrapper<Order> lambdaQueryWrapper = new LambdaQueryWrapper();
      ((LambdaQueryWrapper<Order>)((LambdaQueryWrapper<Order>)lambdaQueryWrapper.eq(Order::getOrderStatus, OrderStatusEnum.REFUNDED.getCode()))
            .ge(start != null, Order::getCreateTime, start))
         .le(end != null, Order::getCreateTime, end);
      List<Order> list = this.list(lambdaQueryWrapper);
      BigDecimal totalPrice = new BigDecimal(0);

      for (Order order : list) {
         totalPrice = totalPrice.add(order.getPayPrice());
      }

      return totalPrice;
   }

   public Long CountByPayStatus(HashMap<String, LocalDateTime> hashMap, int type) {
      LocalDateTime start = hashMap.get(DateResponseKeyEnum.START_TIME_NAME.getValue());
      LocalDateTime end = hashMap.get(DateResponseKeyEnum.END_TIME_NAME.getValue());
      return (((this.lambdaQuery().ge(start != null, Order::getCreateTime, start))
               .le(end != null, Order::getCreateTime, end))
            .eq(Order::getOrderStatus, type))
         .count();
   }

   public Long CountByPayStatus(HashMap<String, LocalDateTime> hashMap, int type, Long userId) {
      LocalDateTime start = hashMap.get(DateResponseKeyEnum.START_TIME_NAME.getValue());
      LocalDateTime end = hashMap.get(DateResponseKeyEnum.END_TIME_NAME.getValue());
      return ((((this.lambdaQuery()
                     .ge(start != null, Order::getCreateTime, start))
                  .le(end != null, Order::getCreateTime, end))
               .eq(userId != null, Order::getMemberNo, userId))
            .eq(Order::getOrderStatus, type))
         .count();
   }

   public List<BaseOrder> queryUnpaidOrder() {
      LambdaQueryWrapper<Order> queryWrapper = (LambdaQueryWrapper<Order>)new QueryWrapper<Order>()
         .lambda()
         .eq(Order::getOrderStatus, OrderStatusEnum.UNPAID.getCode());
      List<Order> orderList = this.list(queryWrapper);
      List<BaseOrder> baseOrderList = new ArrayList<>();
      if (CollectionUtils.isNotEmpty(orderList)) {
         for (Order order : orderList) {
            BaseOrder baseOrder = new BaseOrder();
            BeanUtils.copyProperties(order, baseOrder);
            baseOrder.setOrderPayPrice(order.getPayPrice());
            baseOrder.setOrderCreateTime(order.getCreateTime());
            baseOrder.setOrderPayTime(order.getPayTime());
            baseOrder.setOrderPayType(order.getPayType());
            baseOrder.setExpectedAmountUsdt(order.getExpectedUsdtAmount());
            baseOrderList.add(baseOrder);
         }
      }

      return baseOrderList;
   }

   public void updateById(BaseOrder baseOrder) {
      Order order = new Order();
      order.setId(baseOrder.getId());
      order.setOrderStatus(baseOrder.getOrderStatus());
      this.updateById(order);
      if (OrderStatusEnum.CANCELLED.getCode().equals(baseOrder.getOrderStatus())) {
         this.releaseCouponQuietly(baseOrder.getOrderNo());
      }
   }

   private Order findLatestByOrderNo(String orderNo) {
      if (StrUtil.isBlank(orderNo)) {
         return null;
      } else {
         List<Order> list = this.list(
            (Wrapper<Order>)((LambdaQueryWrapper<Order>)((LambdaQueryWrapper<Order>)new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo)).orderByDesc(Order::getCreateTime))
               .last("LIMIT 1")
         );
         return CollectionUtils.isEmpty(list) ? null : list.get(0);
      }
   }

   public BaseOrder getByOrderNo(String orderNo) {
      if (StrUtil.isBlank(orderNo)) {
         return null;
      } else {
         Order order = this.findLatestByOrderNo(orderNo);
         if (order == null) {
            return null;
         } else {
            BaseOrder baseOrder = new BaseOrder();
            BeanUtils.copyProperties(order, baseOrder);
            baseOrder.setOrderPayPrice(order.getPayPrice());
            baseOrder.setOrderCreateTime(order.getCreateTime());
            baseOrder.setOrderPayTime(order.getPayTime());
            baseOrder.setOrderPayType(order.getPayType());
            baseOrder.setExpectedAmountUsdt(order.getExpectedUsdtAmount());
            return baseOrder;
         }
      }
   }

   public void updateExpectedUsdtAmount(String orderNo, BigDecimal amountUsdt) {
      if (!StrUtil.isBlank(orderNo) && amountUsdt != null) {
         Order order = this.findLatestByOrderNo(orderNo);
         if (order != null) {
            Order update = new Order();
            update.setId(order.getId());
            update.setExpectedUsdtAmount(amountUsdt);
            update.setPayType(PayTypeEnum.USDT.getCode());
            this.updateById(update);
         }
      }
   }

   public Long countPendingInvoice() {
      return (this.orderInvoiceService.lambdaQuery().eq(OrderInvoice::getStatus, InvoiceStatusEnum.PENDING.getCode())).count();
   }

   public BigDecimal sumPendingInvoiceAmount() {
      List<OrderInvoice> pendingList = (this.orderInvoiceService
            .lambdaQuery()
            .eq(OrderInvoice::getStatus, InvoiceStatusEnum.PENDING.getCode()))
         .list();
      BigDecimal total = BigDecimal.ZERO;

      for (OrderInvoice invoice : pendingList) {
         if (invoice.getInvoiceAmount() != null) {
            total = total.add(invoice.getInvoiceAmount());
         }
      }

      return total;
   }

   private QueryWrapper<Order> invoicableOrderWrapper(Long memberNo) {
      QueryWrapper<Order> queryWrapper = new QueryWrapper();
      ((LambdaQueryWrapper<Order>)queryWrapper.lambda().eq(Order::getMemberNo, memberNo)).eq(Order::getOrderStatus, OrderStatusEnum.PAID.getCode());
      queryWrapper.apply(
         "NOT EXISTS (SELECT 1 FROM ORDER_INVOICE_ITEM i INNER JOIN ORDER_INVOICE inv ON inv.ID = i.INVOICE_ID WHERE i.ORDER_NO = ORDERS.ORDER_NO AND inv.STATUS IN (0, 1))",
         new Object[0]
      );
      queryWrapper.apply(
         "NOT EXISTS (SELECT 1 FROM SERVER_REFUND_RECORD r WHERE r.STATUS = 1 AND (EXISTS (SELECT 1 FROM ORDER_SERVER os WHERE os.ORDER_NO = ORDERS.ORDER_NO AND os.SERVER_ID = r.SERVER_ID) OR (ORDERS.UPSTREAM_SERVER_IDS IS NOT NULL AND ORDERS.UPSTREAM_SERVER_IDS LIKE CONCAT('%', r.SERVER_ID, '%'))))",
         new Object[0]
      );
      return queryWrapper;
   }

   public Long countInvoicableOrders(Long memberNo) {
      return memberNo == null ? 0L : this.count(this.invoicableOrderWrapper(memberNo));
   }

   public BigDecimal sumInvoicableOrderAmount(Long memberNo) {
      if (memberNo == null) {
         return BigDecimal.ZERO;
      } else {
         QueryWrapper<Order> queryWrapper = this.invoicableOrderWrapper(memberNo);
         queryWrapper.select(new String[]{"IFNULL(SUM(PAY_PRICE), 0) AS payPrice"});
         List<Map<String, Object>> maps = this.listMaps(queryWrapper);
         if (maps != null && !maps.isEmpty() && maps.get(0) != null && !maps.get(0).isEmpty()) {
            Object value = maps.get(0).values().iterator().next();
            return value == null ? BigDecimal.ZERO : new BigDecimal(value.toString());
         } else {
            return BigDecimal.ZERO;
         }
      }
   }

   @Override
   public Page<Order> page(OrderPageParam pageParam) {
      QueryWrapper<Order> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getOrderNo())) {
         queryWrapper.lambda().and(q -> q.eq(Order::getOrderNo, pageParam.getOrderNo()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getOrderType())) {
         queryWrapper.lambda().and(q -> q.eq(Order::getOrderType, pageParam.getOrderType()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getOrderStatus())) {
         queryWrapper.lambda().and(q -> q.eq(Order::getOrderStatus, pageParam.getOrderStatus()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getProductType())) {
         queryWrapper.lambda().and(q -> q.eq(Order::getProductType, pageParam.getProductType()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getPayType())) {
         queryWrapper.lambda().and(q -> q.eq(Order::getPayType, pageParam.getPayType()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getMemberNo())) {
         queryWrapper.lambda().and(q -> q.eq(Order::getMemberNo, pageParam.getMemberNo()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(Order::getCreateTime);
      }

      Page<Order> page = (Page<Order>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      this.fillInvoiceStatus(page.getRecords());
      return page;
   }

   @Override
   public CommonResult<OrderResponse> detail(OrderIdParam orderIdParam) {
      Order order = this.findLatestByOrderNo(orderIdParam.getOrderNo());
      if (ObjectUtil.isNull(order)) {
         return CommonResult.error("订单不存在");
      } else {
         OrderResponse orderResponse = (OrderResponse)JSONUtil.toBean(JSONUtil.toJsonStr(order), OrderResponse.class);
         if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(orderResponse.getProductType())) {
            OrderServerConfigResponse serverConfigResponse = this.orderServerConfigService.queryByOrderNo(order.getOrderNo());
            if (null == serverConfigResponse) {
               return CommonResult.error("云服务器订单详情不存在");
            }

            orderResponse.setOrderServerConfig(serverConfigResponse);
         } else if (ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(orderResponse.getProductType())) {
            IndependentOrderServerConfigResponse independentOrderServerConfigResponse = this.independentOrderServerConfigService
               .queryByOrderNo(order.getOrderNo());
            if (null == independentOrderServerConfigResponse) {
               return CommonResult.error("独立服务器订单详情不存在");
            }

            orderResponse.setIndependentOrderServerConfig(independentOrderServerConfigResponse);
         } else if (ProductTypeEnum.DOMAIN.getCode().equals(orderResponse.getProductType())) {
            List<DomainOrderItem> domainItems = this.adminDomainOrderItemMapper
               .selectList((Wrapper<DomainOrderItem>)new LambdaQueryWrapper<DomainOrderItem>().eq(DomainOrderItem::getOrderNo, order.getOrderNo()));
            List<DomainOrderItemResponse> domainOrderItemResponses = domainItems == null ? Collections.emptyList() : domainItems.stream().map(item -> {
               DomainOrderItemResponse r = new DomainOrderItemResponse();
               BeanUtils.copyProperties(item, r);
               return r;
            }).collect(Collectors.toList());
            orderResponse.setDomainOrderItems(domainOrderItemResponses);
         } else if (ProductTypeEnum.DNS.getCode().equals(orderResponse.getProductType())) {
            AdminDnsOrderConfig dnsOrderConfig = (AdminDnsOrderConfig)this.adminDnsOrderConfigMapper
               .selectOne((Wrapper<AdminDnsOrderConfig>)new LambdaQueryWrapper<AdminDnsOrderConfig>().eq(AdminDnsOrderConfig::getOrderNo, order.getOrderNo()));
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
            AdminVhostOrderConfig vhostOrderConfig = (AdminVhostOrderConfig)this.adminVhostOrderConfigMapper
               .selectOne((Wrapper<AdminVhostOrderConfig>)new LambdaQueryWrapper<AdminVhostOrderConfig>().eq(AdminVhostOrderConfig::getOrderNo, order.getOrderNo()));
            if (vhostOrderConfig == null) {
               return CommonResult.error("虚拟主机订单详情不存在");
            }

            orderResponse.setVhostOrderConfig(vhostOrderConfig);
            this.fillVhostHostMap(vhostOrderConfig, order.getOrderNo(), order.getMemberNo());
         }

         Map<String, Integer> invoiceStatusMap = this.orderInvoiceService.findOccupiedStatusByOrderNos(Collections.singletonList(order.getOrderNo()));
         orderResponse.setInvoiceStatus(invoiceStatusMap.get(order.getOrderNo()));
         return CommonResult.data(orderResponse);
      }
   }

   private void fillVhostHostMap(AdminVhostOrderConfig config, String orderNo, Long memberNo) {
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

   private void fillInvoiceStatus(List<Order> orders) {
      if (orders != null && !orders.isEmpty()) {
         List<String> orderNos = orders.stream().map(Order::getOrderNo).filter(CharSequenceUtil::isNotBlank).collect(Collectors.toList());
         Map<String, Integer> invoiceStatusMap = this.orderInvoiceService.findOccupiedStatusByOrderNos(orderNos);

         for (Order order : orders) {
            order.setInvoiceStatus(invoiceStatusMap.get(order.getOrderNo()));
         }
      }
   }

   @Transactional
   @Override
   public void paySuccess(String orderNo, Date payTime, String alipayPayNo, Integer payType) {
      long startTime = System.currentTimeMillis();

      try {
         log.info("开始处理支付成功逻辑，订单号:{}", orderNo);
         Order order = this.findLatestByOrderNo(orderNo);
         if (order == null) {
            log.error("订单不存在，订单号:{}", orderNo);
            throw new RuntimeException("订单不存在");
         }

         if (OrderStatusEnum.PAID.getCode().equals(order.getOrderStatus())) {
            log.warn("订单已支付，跳过重复处理，订单号:{}", orderNo);
            return;
         }

         ((OrderService)SpringUtil.getBean(OrderService.class)).markOrderPaid(orderNo, payTime, alipayPayNo, payType);
         order.setOrderStatus(OrderStatusEnum.PAID.getCode());
         order.setPayTime(payTime);
         order.setPayOrderNo(alipayPayNo);
         if (payType != null) {
            order.setPayType(payType);
         }

         this.schedulePaidOrderFollowUpsAfterCommit(order, orderNo);
      } catch (Exception var10) {
         long duration = System.currentTimeMillis() - startTime;
         log.error("支付成功后处理订单失败，订单号:{},异常信息:{}, 耗时:{}ms", new Object[]{orderNo, var10.getMessage(), duration, var10});
         throw new RuntimeException("支付成功后处理订单失败：" + var10.getMessage(), var10);
      }

      long duration = System.currentTimeMillis() - startTime;
      log.info("支付状态更新完成，订单号: {}, 耗时: {}ms", orderNo, duration);
   }

   @Transactional
   @Override
   public void paySuccessSync(String orderNo, Date payTime, String alipayPayNo, Integer payType) {
      long startTime = System.currentTimeMillis();

      try {
         log.info("开始同步处理支付成功逻辑，订单号:{}", orderNo);
         Order order = this.findLatestByOrderNo(orderNo);
         if (order == null) {
            log.error("订单不存在，订单号:{}", orderNo);
            throw new RuntimeException("订单不存在");
         }

         if (OrderStatusEnum.PAID.getCode().equals(order.getOrderStatus())) {
            log.warn("订单已支付，跳过重复同步处理，订单号:{}", orderNo);
            return;
         }

         ((OrderService)SpringUtil.getBean(OrderService.class)).markOrderPaid(orderNo, payTime, alipayPayNo, payType);
         order.setOrderStatus(OrderStatusEnum.PAID.getCode());
         order.setPayTime(payTime);
         order.setPayOrderNo(alipayPayNo);
         if (payType != null) {
            order.setPayType(payType);
         }

         this.runAfterCommit(() -> this.sendNotificationAsync(order));
         ((OrderService)SpringUtil.getBean(OrderService.class)).processPaidOrder(orderNo);
      } catch (Exception var10) {
         long duration = System.currentTimeMillis() - startTime;
         log.error("同步处理支付成功失败，订单号:{},异常信息:{}, 耗时:{}ms", new Object[]{orderNo, var10.getMessage(), duration, var10});
         throw new RuntimeException("同步处理支付成功失败：" + var10.getMessage(), var10);
      }

      long duration = System.currentTimeMillis() - startTime;
      log.info("同步支付后处理完成，订单号: {}, 耗时: {}ms", orderNo, duration);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void markOrderPaid(String orderNo, Date payTime, String alipayPayNo, Integer payType) {
      Order order = this.findLatestByOrderNo(orderNo);
      if (order == null) {
         throw new RuntimeException("订单不存在");
      } else if (!OrderStatusEnum.PAID.getCode().equals(order.getOrderStatus())) {
         LambdaUpdateWrapper<Order> updateWrapper = (LambdaUpdateWrapper<Order>)((LambdaUpdateWrapper<Order>)((LambdaUpdateWrapper<Order>)((LambdaUpdateWrapper<Order>)((LambdaUpdateWrapper<Order>)new LambdaUpdateWrapper<Order>()
                        .eq(Order::getId, order.getId()))
                     .set(Order::getOrderStatus, OrderStatusEnum.PAID.getCode()))
                  .set(Order::getPayTime, payTime))
               .set(Order::getPayOrderNo, alipayPayNo))
            .set(payType != null, Order::getPayType, payType);
         this.update(updateWrapper);
         log.info("订单状态已提交为已支付，订单号:{}", orderNo);
      }
   }

   @Override
   public void handlePaidOrderAsync(String orderNo) {
      Order order = this.findLatestByOrderNo(orderNo);
      if (order == null) {
         throw new RuntimeException("订单不存在");
      } else if (!OrderStatusEnum.PAID.getCode().equals(order.getOrderStatus())) {
         throw new RuntimeException("订单未支付，无法触发后续流程");
      } else {
         log.info("订单已支付，开始异步触发后续流程，订单号:{}", orderNo);
         this.sendNotificationAsync(order);
         this.processPaidOrderAsync(orderNo);
      }
   }

   private void schedulePaidOrderFollowUpsAfterCommit(Order order, String orderNo) {
      this.runAfterCommit(() -> {
         this.sendNotificationAsync(order);
         this.processPaidOrderAsync(orderNo);
      });
   }

   private void processPaidOrderAsync(String orderNo) {
      this.orderTaskExecutor.execute(() -> {
         try {
            ((OrderService)SpringUtil.getBean(OrderService.class)).processPaidOrder(orderNo);
         } catch (Exception var2) {
            log.error("后台处理已支付订单失败，订单号:{}, 错误:{}", new Object[]{orderNo, var2.getMessage(), var2});
         }
      });
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void processPaidOrder(String orderNo) {
      long startTime = System.currentTimeMillis();
      Order order = this.findLatestByOrderNo(orderNo);
      if (order == null) {
         throw new RuntimeException("订单不存在");
      } else if (!OrderStatusEnum.PAID.getCode().equals(order.getOrderStatus())) {
         throw new RuntimeException("订单未支付，无法执行后续流程");
      } else {
         if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(order.getProductType())) {
            log.info("后台处理云服务器订单，订单号:{}", orderNo);
            OrderServerConfigResponse orderServerConfig = this.orderServerConfigService.queryByOrderNo(orderNo);
            if (orderServerConfig == null) {
               throw new RuntimeException("订单服务信息不存在");
            }

            if (order.getOrderType().equals(OrderTypeEnum.NEW_PURCHASE.getCode())) {
               if (this.isResourcePoolType(orderServerConfig.getLineId(), null)) {
                  this.createResourcePoolRequest(order, orderServerConfig.getLineId(), null);
               } else {
                  this.createCloudServer(order);
               }
            }

            if (order.getOrderType().equals(OrderTypeEnum.UPGRADE.getCode())) {
               OrderServer orderServer = this.orderServerService.queryByOrderNo(order.getOrderNo());
               if (orderServer == null) {
                  throw new RuntimeException("服务器订单不存在");
               }

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
                     orderNo, orderServer.getServerId(), orderServerConfig.getSystemDisk(), orderServerConfig.getDataDisks(), orderServerConfig.getRenewPrice()
                  );
               }

               if (order.getUpgradeItem().equals(UpgradeItemEnum.UPGRADE_DEFENSE.getCode())) {
                  this.upgradeDefenseServer(orderNo, orderServer.getServerId(), orderServerConfig.getDefense(), orderServerConfig.getRenewPrice());
               }
            }

            if (order.getOrderType().equals(OrderTypeEnum.RENEW.getCode())) {
               List<OrderServer> orderServers = this.orderServerService.queryByList(order.getOrderNo());
               if (orderServers == null || orderServers.isEmpty()) {
                  throw new RuntimeException("服务器订单不存在");
               }

               for (OrderServer orderServerx : orderServers) {
                  if (!Boolean.TRUE.equals(this.serverApi.renewServer(orderNo, orderServerx.getServerId(), orderServerConfig.getDuration()))) {
                     throw new RuntimeException("云服务器续费失败，serverId=" + orderServerx.getServerId());
                  }
               }
            }
         } else if (ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(order.getProductType())) {
            log.info("后台处理独立服务器订单，订单号:{}", orderNo);
            IndependentOrderServerConfigResponse independentOrderServerConfig = this.independentOrderServerConfigService.queryByOrderNo(order.getOrderNo());
            if (null == independentOrderServerConfig) {
               throw new RuntimeException("订单服务信息不存在");
            }

            if (order.getOrderType().equals(OrderTypeEnum.NEW_PURCHASE.getCode())) {
               if (this.isResourcePoolType(null, independentOrderServerConfig.getMachineId())) {
                  this.createResourcePoolRequest(order, null, independentOrderServerConfig.getMachineId());
               } else {
                  this.createIndependentServer(order);
               }
            }

            if (order.getOrderType().equals(OrderTypeEnum.UPGRADE.getCode())) {
               OrderServer orderServerxx = this.orderServerService.queryByOrderNo(order.getOrderNo());
               if (orderServerxx == null) {
                  throw new RuntimeException("服务器订单不存在");
               }

               if (order.getUpgradeItem().equals(UpgradeItemEnum.UPGRADE_IP.getCode())) {
                  this.upgradeIndependentIpServer(
                     orderNo, orderServerxx.getServerId(), independentOrderServerConfig.getIpSize(), independentOrderServerConfig.getRenewPrice()
                  );
               }

               if (order.getUpgradeItem().equals(UpgradeItemEnum.UPGRADE_BANDWIDTH.getCode())) {
                  this.upgradeIndependentBandwidthServer(
                     orderNo, orderServerxx.getServerId(), independentOrderServerConfig.getBandwidth(), independentOrderServerConfig.getRenewPrice()
                  );
               }

               if (order.getUpgradeItem().equals(UpgradeItemEnum.UPGRADE_DEFENSE.getCode())) {
                  this.upgradeIndependentDefenseServer(
                     orderNo, orderServerxx.getServerId(), independentOrderServerConfig.getDefense(), independentOrderServerConfig.getRenewPrice()
                  );
               }
            }

            if (order.getOrderType().equals(OrderTypeEnum.RENEW.getCode())) {
               List<OrderServer> orderServers = this.orderServerService.queryByList(order.getOrderNo());
               if (orderServers == null || orderServers.isEmpty()) {
                  throw new RuntimeException("服务器订单不存在");
               }

               for (OrderServer orderServerxxx : orderServers) {
                  if (!Boolean.TRUE
                     .equals(
                        this.independentApi
                           .renewServer(
                              orderNo, orderServerxxx.getServerId(), independentOrderServerConfig.getDuration(), independentOrderServerConfig.getUnit(), null
                           )
                     )) {
                     throw new RuntimeException("独立服务器续费失败");
                  }
               }
            }
         } else if (ProductTypeEnum.VHOST.getCode().equals(order.getProductType())) {
            this.fulfillAdminVhost(order);
         }

         long duration = System.currentTimeMillis() - startTime;
         log.info("后台处理已支付订单完成，订单号:{}, 耗时:{}ms", orderNo, duration);
      }
   }

   private void fulfillAdminVhost(Order order) {
      AdminVhostOrderConfig config = (AdminVhostOrderConfig)this.adminVhostOrderConfigMapper
         .selectOne((Wrapper<AdminVhostOrderConfig>)new LambdaQueryWrapper<AdminVhostOrderConfig>().eq(AdminVhostOrderConfig::getOrderNo, order.getOrderNo()));
      if (config == null) {
         throw new RuntimeException("虚拟主机订单配置不存在");
      } else if (Integer.valueOf(OrderTypeEnum.RENEW.getCode()).equals(order.getOrderType())) {
         if (order.getMemberNo() != null && !StrUtil.isBlank(config.getHostId()) && config.getMonths() != null) {
            if (!Boolean.TRUE
               .equals(this.memberVhostApi.renew(config.getHostId(), String.valueOf(order.getMemberNo()), config.getMonths(), order.getOrderNo(), "B"))) {
               throw new RuntimeException("虚拟主机续费失败");
            }
         } else {
            throw new RuntimeException("虚拟主机续费参数不完整");
         }
      } else if (Integer.valueOf(OrderTypeEnum.UPGRADE.getCode()).equals(order.getOrderType())) {
         if (order.getMemberNo() != null && !StrUtil.isBlank(config.getHostId()) && config.getUpgradeTraffic() != null) {
            if (!Boolean.TRUE
               .equals(
                  this.memberVhostApi.upgradeTraffic(config.getHostId(), String.valueOf(order.getMemberNo()), config.getUpgradeTraffic(), order.getOrderNo())
               )) {
               throw new RuntimeException("虚拟主机升级流量失败");
            }
         } else {
            throw new RuntimeException("虚拟主机升级流量参数不完整");
         }
      } else {
         this.createAdminVhost(order, config);
      }
   }

   private void createAdminVhost(Order order, AdminVhostOrderConfig config) {
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
         param.setPlatform("B");
         Date start = order.getPayTime() != null ? order.getPayTime() : new Date();
         param.setEndTime(DateUtil.offsetMonth(start, config.getMonths()));
         if (!Boolean.TRUE.equals(this.memberVhostApi.create(param))) {
            throw new RuntimeException("创建虚拟主机实例失败");
         }
      } else {
         throw new RuntimeException("虚拟主机开通参数不完整");
      }
   }

   private void sendNotificationAsync(Order order) {
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
      this.serverApi.upgradeDefenseServer(orderNo, serverId, defense, renewPrice);
   }

   private void upgradeHardDisksServer(String orderNo, String serverId, Integer systemDisk, List<Integer> dataDisks, BigDecimal renewPrice) {
      this.serverApi.upgradeHardDisksServer(orderNo, serverId, systemDisk, dataDisks, renewPrice);
   }

   private void upgradeBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice) {
      this.serverApi.upgradeBandwidthServer(orderNo, serverId, bandwidth, renewPrice);
   }

   private void upgradeCpuRamServer(String orderNo, String serverId, Integer cpu, Integer ram, BigDecimal renewPrice) {
      this.serverApi.upgradeCpuRamServer(orderNo, serverId, cpu, ram, renewPrice);
   }

   public void createIndependentServer(Order order) {
      try {
         log.info("创建独立服务器开始，订单号:{}", JSON.toJSONString(order));
         IndependentOrderServerConfigResponse independentOrderServer = this.independentOrderServerConfigService.queryByOrderNo(order.getOrderNo());

         for (int i = 0; i < independentOrderServer.getNumber(); i++) {
            BaseMemberIndependentAddServer baseMemberAddServer = new BaseMemberIndependentAddServer();
            baseMemberAddServer.setId(null);
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

            BaseIndependentSystemVersion memberOsVersion = this.independentSystemVersionApi.selectByOsVersionId(independentOrderServer.getSystemId());
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
            this.independentApi.createServer(baseMemberAddServer);
            OrderServerAddParam orderServerAddParam = new OrderServerAddParam();
            orderServerAddParam.setOrderNo(independentOrderServer.getOrderNo());
            orderServerAddParam.setServerId(baseMemberAddServer.getId());
            orderServerAddParam.setServerName(baseMemberAddServer.getServerName());
            log.info("创建服务器和订单关系，参数:{}", JSON.toJSONString(orderServerAddParam));
            this.orderServerService.add(orderServerAddParam);

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

   public void createCloudServer(Order order) {
      try {
         log.info("创建云服务器开始，订单号:{}", JSON.toJSONString(order));
         OrderServerConfigResponse orderServer = this.orderServerConfigService.queryByOrderNo(order.getOrderNo());

         for (int i = 0; i < orderServer.getNumber(); i++) {
            BaseLocalLine baseLocalLine = this.serverLocalLineApi.selectByLineId(orderServer.getLineId());
            BaseMemberSystemVersion memberOsVersion = this.memberOsVersionApi.selectByOsVersionId(orderServer.getSystemId());
            BaseMemberAddServer baseMemberAddServer = new BaseMemberAddServer();
            baseMemberAddServer.setId(null);
            baseMemberAddServer.setMemberNo(order.getMemberNo());
            baseMemberAddServer.setHostName("ecs_" + UniqueHostnameGenerator.generateUniqueHostname(10));
            baseMemberAddServer.setBandwidth(orderServer.getBandwidth());
            baseMemberAddServer.setBandwidthIn(baseLocalLine.getBandwidthInValue());
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
            baseMemberAddServer.setAvailableId(baseLocalLine.getAvailableId());
            baseMemberAddServer.setOrderNo(order.getOrderNo());
            baseMemberAddServer.setRenewPrice(orderServer.getRenewPrice());
            baseMemberAddServer.setBuyTime(order.getPayTime());
            baseMemberAddServer.setEndTime(DateUtil.offsetMonth(order.getPayTime(), orderServer.getDuration()));
            this.serverApi.createServer(baseMemberAddServer);
            OrderServerAddParam orderServerAddParam = new OrderServerAddParam();
            orderServerAddParam.setOrderNo(orderServer.getOrderNo());
            orderServerAddParam.setServerId(baseMemberAddServer.getId());
            orderServerAddParam.setServerName(baseMemberAddServer.getHostName());
            this.orderServerService.add(orderServerAddParam);
         }

         log.info("创建云服务器成功，订单号:{}，共创建 {} 台服务器", order.getOrderNo(), orderServer.getNumber());
      } catch (Exception var8) {
         log.error("创建云服务器失败，订单号:{},异常信息:{}", order.getOrderNo(), var8.getMessage());
         throw new RuntimeException("创建云服务器失败：" + var8.getMessage(), var8);
      }
   }

   public void updateOrderStatusByServerId(String serverId) {
      for (Order order : this.orderMapper.selectByServerId(serverId, OrderStatusEnum.UNPAID.getCode())) {
         if (order.getOrderStatus().equals(OrderStatusEnum.UNPAID.getCode())) {
            order.setOrderStatus(OrderStatusEnum.CANCELLED.getCode());
            this.orderMapper.updateById(order);
            this.releaseCouponQuietly(order.getOrderNo());
         }
      }
   }

   private void releaseCouponQuietly(String orderNo) {
      if (!StrUtil.isBlank(orderNo)) {
         try {
            this.memberCouponApi.releaseCoupon(orderNo);
         } catch (Exception var3) {
            log.warn("取消订单释放优惠券失败 orderNo={}", orderNo, var3);
         }
      }
   }

   private void upgradeIndependentDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice) {
      if (!Boolean.TRUE.equals(this.independentApi.upgradeIndependentDefenseServer(orderNo, serverId, defense, renewPrice))) {
         throw new RuntimeException("独立服务器升级防御失败");
      }
   }

   private void upgradeIndependentBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice) {
      if (!Boolean.TRUE.equals(this.independentApi.upgradeIndependentBandwidthServer(orderNo, serverId, bandwidth, renewPrice))) {
         throw new RuntimeException("独立服务器升级带宽失败");
      }
   }

   private void upgradeIndependentIpServer(String orderNo, String serverId, Integer ipSize, BigDecimal renewPrice) {
      if (!Boolean.TRUE.equals(this.independentApi.upgradeIndependentIpServer(orderNo, serverId, ipSize, renewPrice))) {
         throw new RuntimeException("独立服务器升级IP失败");
      }
   }

   private void createResourcePoolRequest(Order order, String lineId, String machineId) {
      try {
         log.info("创建资源池请求，订单号：{}，lineId：{}，machineId：{}", new Object[]{order.getOrderNo(), lineId, machineId});
         String resourcePoolId = null;
         JSONObject requestParams = new JSONObject();
         if (StrUtil.isNotBlank(lineId)) {
            BaseLocalLine baseLocalLine = this.serverLocalLineApi.selectByLineId(lineId);
            if (baseLocalLine != null && StrUtil.isNotBlank(baseLocalLine.getResourcePoolId())) {
               resourcePoolId = StrUtil.trim(baseLocalLine.getResourcePoolId());
            }

            OrderServerConfigResponse orderServerConfig = this.orderServerConfigService.queryByOrderNo(order.getOrderNo());
            if (orderServerConfig == null) {
               throw new RuntimeException("云服务器订单配置不存在");
            }

            BaseMemberSystemVersion memberOsVersion = this.memberOsVersionApi.selectByOsVersionId(orderServerConfig.getSystemId());
            if (memberOsVersion == null) {
               throw new RuntimeException("云服务器操作系统不存在");
            }

            for (int i = 0; i < orderServerConfig.getNumber(); i++) {
               BaseServer server = new BaseServer();
               String serverId = SnowflakeIdGenerator.generateId();
               server.setId(serverId);
               server.setMemberNo(String.valueOf(order.getMemberNo()));
               server.setHostName("ecs_" + UniqueHostnameGenerator.generateUniqueHostname(10));
               server.setBandwidth(orderServerConfig.getBandwidth());
               if (baseLocalLine != null) {
                  server.setBandwidthIn(baseLocalLine.getBandwidthInValue());
                  server.setAvailableId(baseLocalLine.getAvailableId());
               }

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
               server.setRenewPrice(orderServerConfig.getRenewPrice());
               server.setBuyTime(order.getPayTime());
               server.setEndTime(DateUtil.offsetMonth(order.getPayTime(), orderServerConfig.getDuration()));
               server.setType(1);
               server.setResourcePoolId(resourcePoolId);
               server.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
               server.setServerStatus(ServerStatusEnum.CREATING.getCode());
               this.serverApi.save(server);
               OrderServerAddParam relation = new OrderServerAddParam();
               relation.setOrderNo(order.getOrderNo());
               relation.setServerId(serverId);
               relation.setServerName(server.getHostName());
               this.orderServerService.add(relation);
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
            requestParams.put("billingMode", order.getBillingMode());
         } else if (StrUtil.isNotBlank(machineId)) {
            BaseAdminMachine machine = this.adminMachineApi.findMachineById(machineId);
            if (machine != null && StrUtil.isNotBlank(machine.getResourcePoolId())) {
               resourcePoolId = StrUtil.trim(machine.getResourcePoolId());
            }

            IndependentOrderServerConfigResponse orderConfig = this.independentOrderServerConfigService.queryByOrderNo(order.getOrderNo());
            if (orderConfig == null) {
               throw new RuntimeException("独立服务器订单配置不存在");
            }

            BaseIndependentSystemVersion systemVersion = this.independentSystemVersionApi.selectByOsVersionId(orderConfig.getSystemId());
            if (systemVersion == null) {
               throw new RuntimeException("独立服务器操作系统不存在");
            }

            for (int i = 0; i < orderConfig.getNumber(); i++) {
               BaseIndependent independent = new BaseIndependent();
               String serverId = SnowflakeIdGenerator.generateId();
               independent.setId(serverId);
               independent.setMemberNo(order.getMemberNo());
               independent.setMachineId(orderConfig.getMachineId());
               independent.setMachineName(orderConfig.getMachineName());
               independent.setServerName("pm_" + UniqueHostnameGenerator.generateUniqueHostname(10));
               independent.setCpuInfo(orderConfig.getCpuInfo());
               independent.setRamInfo(orderConfig.getRamInfo());
               independent.setDisksInfo(orderConfig.getDisksInfo());
               independent.setBandwidth(orderConfig.getBandwidth());
               independent.setDefense(orderConfig.getDefense());
               independent.setIpSize(orderConfig.getIpSize());
               independent.setSystemPassword(orderConfig.getLoginPassword());
               independent.setRegionId(orderConfig.getRegionId());
               independent.setLineId(orderConfig.getLineId());
               independent.setSystemId(orderConfig.getSystemId());
               independent.setSystemClassId(systemVersion.getOsClassId());
               independent.setSystemClass(systemVersion.getOsClassName());
               independent.setSystemName(systemVersion.getOsName());
               independent.setPort(systemVersion.getDefaultPort());
               independent.setSystemUserName(systemVersion.getDefaultUserName());
               independent.setRenewPrice(orderConfig.getRenewPrice());
               independent.setBuyTime(order.getPayTime());
               independent.setEndTime(DateUtil.offsetMonth(order.getPayTime(), orderConfig.getDuration()));
               independent.setType(1);
               independent.setResourcePoolId(resourcePoolId);
               independent.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
               independent.setServerStatus(ServerStatusEnum.CREATING.getCode());
               if (machine != null) {
                  independent.setAutomation(machine.getAutomation());
               }

               this.independentApi.save(independent);
               OrderServerAddParam relation = new OrderServerAddParam();
               relation.setOrderNo(order.getOrderNo());
               relation.setServerId(serverId);
               relation.setServerName(independent.getServerName());
               this.orderServerService.add(relation);
            }

            requestParams.put("orderNo", order.getOrderNo());
            requestParams.put("machineId", machineId);
            requestParams.put("regionId", orderConfig.getRegionId());
            requestParams.put("lineId", orderConfig.getLineId());
            requestParams.put("systemId", orderConfig.getSystemId());
            requestParams.put("osVersionId", orderConfig.getSystemId());
            if (StrUtil.isNotBlank(systemVersion.getOsClassId())) {
               requestParams.put("systemClassId", systemVersion.getOsClassId());
               requestParams.put("osClassId", systemVersion.getOsClassId());
            }

            requestParams.put("systemPassword", orderConfig.getLoginPassword());
            requestParams.put("memberNo", order.getMemberNo());
            requestParams.put("duration", orderConfig.getDuration());
            requestParams.put("number", orderConfig.getNumber());
            requestParams.put("billingMode", order.getBillingMode());
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
         }

         if (StrUtil.isBlank(resourcePoolId)) {
            throw new RuntimeException("未找到资源池ID");
         } else {
            BaseResourcePoolRequest request = new BaseResourcePoolRequest();
            request.setOrderNo(order.getOrderNo());
            request.setMemberNo(order.getMemberNo());
            request.setResourcePoolId(resourcePoolId);
            request.setRequestParams(JSON.toJSONString(requestParams));
            request.setOperationType("CREATE");
            request.setProductType(order.getProductType());
            this.resourcePoolApi.createRequest(request);
            this.scheduleResourcePoolRequestSendAfterCommit(order.getOrderNo());
         }
      } catch (Exception var13) {
         log.error("创建资源池请求失败，订单号：{}，错误：{}", new Object[]{order.getOrderNo(), var13.getMessage(), var13});
         throw new RuntimeException("创建资源池请求失败：" + var13.getMessage(), var13);
      }
   }

   private int resolveCloudServerPort(BaseMemberSystemVersion memberOsVersion) {
      return memberOsVersion == null
         ? 22
         : CloudServerPortUtil.resolvePort(memberOsVersion.getPortMode(), memberOsVersion.getPortRange(), memberOsVersion.getDefaultPort());
   }

   private void scheduleResourcePoolRequestSendAfterCommit(String requestId) {
      this.runAfterCommit(() -> this.sendResourcePoolRequestAsync(requestId));
   }

   private void runAfterCommit(Runnable task) {
      if (TransactionSynchronizationManager.isSynchronizationActive() && TransactionSynchronizationManager.isActualTransactionActive()) {
         TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            public void afterCommit() {
               task.run();
            }
         });
      } else {
         task.run();
      }
   }

   private void sendResourcePoolRequestAsync(String requestId) {
      this.resourcePoolTaskExecutor.execute(() -> {
         try {
            this.resourcePoolRequestHandlerApi.sendPendingRequestByOrderNo(requestId);
         } catch (Exception var3) {
            log.error("异步发送资源池请求失败，请求ID：{}，错误：{}", new Object[]{requestId, var3.getMessage(), var3});
         }
      });
   }

   private boolean isResourcePoolType(String lineId, String machineId) {
      try {
         if (ObjectUtil.isNotEmpty(lineId)) {
            BaseLocalLine baseLocalLine = this.serverLocalLineApi.selectByLineId(lineId);
            if (baseLocalLine != null) {
               if (ObjectUtil.isNotEmpty(baseLocalLine.getType()) && baseLocalLine.getType() == 1) {
                  return true;
               }

               if (StrUtil.isNotBlank(baseLocalLine.getResourcePoolId())) {
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
}
