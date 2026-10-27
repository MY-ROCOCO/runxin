package com.rxinyun.admin.order.modular.invoice.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.order.modular.ecs.entity.Order;
import com.rxinyun.admin.order.modular.ecs.entity.OrderServer;
import com.rxinyun.admin.order.modular.ecs.entity.ServerRefundRecord;
import com.rxinyun.admin.order.modular.ecs.mapper.OrderMapper;
import com.rxinyun.admin.order.modular.ecs.mapper.ServerRefundRecordMapper;
import com.rxinyun.admin.order.modular.ecs.service.OrderServerService;
import com.rxinyun.admin.order.modular.invoice.entity.OrderInvoice;
import com.rxinyun.admin.order.modular.invoice.entity.OrderInvoiceItem;
import com.rxinyun.admin.order.modular.invoice.mapper.OrderInvoiceItemMapper;
import com.rxinyun.admin.order.modular.invoice.mapper.OrderInvoiceMapper;
import com.rxinyun.admin.order.modular.invoice.param.AdminOrderInvoiceIdParam;
import com.rxinyun.admin.order.modular.invoice.param.AdminOrderInvoicePageParam;
import com.rxinyun.admin.order.modular.invoice.param.AdminOrderInvoiceRejectParam;
import com.rxinyun.admin.order.modular.invoice.response.AdminOrderInvoiceItemResponse;
import com.rxinyun.admin.order.modular.invoice.response.AdminOrderInvoiceResponse;
import com.rxinyun.admin.order.modular.invoice.service.OrderInvoiceService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.order.InvoiceStatusEnum;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderInvoiceServiceImpl extends ServiceImpl<OrderInvoiceMapper, OrderInvoice> implements OrderInvoiceService {
   @Resource
   private OrderInvoiceItemMapper orderInvoiceItemMapper;
   @Resource
   private OrderMapper orderMapper;
   @Resource
   private OrderServerService orderServerService;
   @Resource
   private ServerRefundRecordMapper serverRefundRecordMapper;

   @Override
   public Page<AdminOrderInvoiceResponse> page(AdminOrderInvoicePageParam pageParam) {
      QueryWrapper<OrderInvoice> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (StrUtil.isNotBlank(pageParam.getInvoiceNo())) {
         queryWrapper.lambda().eq(OrderInvoice::getInvoiceNo, pageParam.getInvoiceNo());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getMemberNo())) {
         queryWrapper.lambda().eq(OrderInvoice::getMemberNo, pageParam.getMemberNo());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         queryWrapper.lambda().eq(OrderInvoice::getStatus, pageParam.getStatus());
      }

      if (StrUtil.isNotBlank(pageParam.getApplyTimeStart())) {
         queryWrapper.lambda().ge(OrderInvoice::getApplyTime, pageParam.getApplyTimeStart());
      }

      if (StrUtil.isNotBlank(pageParam.getApplyTimeEnd())) {
         queryWrapper.lambda().le(OrderInvoice::getApplyTime, pageParam.getApplyTimeEnd());
      }

      if (StrUtil.isNotBlank(pageParam.getOrderNo())) {
         queryWrapper.apply(
            "EXISTS (SELECT 1 FROM ORDER_INVOICE_ITEM i WHERE i.INVOICE_ID = ORDER_INVOICE.ID AND i.ORDER_NO = {0})", new Object[]{pageParam.getOrderNo()}
         );
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(OrderInvoice::getApplyTime);
      }

      Page<OrderInvoice> entityPage = (Page<OrderInvoice>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      Page<AdminOrderInvoiceResponse> result = new Page(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
      List<AdminOrderInvoiceResponse> records = entityPage.getRecords().stream().map(this::toResponseWithoutItems).collect(Collectors.toList());
      this.fillItems(records);
      result.setRecords(records);
      return result;
   }

   @Override
   public AdminOrderInvoiceResponse detail(AdminOrderInvoiceIdParam param) {
      OrderInvoice invoice = (OrderInvoice)this.getById(param.getId());
      if (invoice == null) {
         throw new CommonException("发票申请不存在", new Object[0]);
      } else {
         AdminOrderInvoiceResponse response = this.toResponseWithoutItems(invoice);
         this.fillItems(Collections.singletonList(response));
         return response;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void approve(AdminOrderInvoiceIdParam param) {
      OrderInvoice invoice = this.requirePending(param.getId());
      List<OrderInvoiceItem> items = this.listItems(invoice.getId());
      if (CollUtil.isEmpty(items)) {
         throw new CommonException("发票明细为空，无法开具", new Object[0]);
      } else {
         for (OrderInvoiceItem item : items) {
            Order order = (Order)this.orderMapper.selectOne((Wrapper<Order>)new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, item.getOrderNo()));
            this.assertOrderStillInvoicable(order, item.getOrderNo());
         }

         Date now = new Date();
         invoice.setStatus(InvoiceStatusEnum.ISSUED.getCode());
         invoice.setIssueTime(now);
         invoice.setIssueUser(StpUtil.getLoginIdAsString());
         invoice.setRejectReason(null);
         this.updateById(invoice);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void reject(AdminOrderInvoiceRejectParam param) {
      OrderInvoice invoice = this.requirePending(param.getId());
      this.orderInvoiceItemMapper.delete((Wrapper<OrderInvoiceItem>)new LambdaQueryWrapper<OrderInvoiceItem>().eq(OrderInvoiceItem::getInvoiceId, invoice.getId()));
      invoice.setStatus(InvoiceStatusEnum.REJECTED.getCode());
      invoice.setRejectReason(param.getRejectReason());
      invoice.setIssueTime(null);
      invoice.setIssueUser(null);
      this.updateById(invoice);
   }

   @Override
   public Map<String, Integer> findOccupiedStatusByOrderNos(Collection<String> orderNos) {
      if (CollUtil.isEmpty(orderNos)) {
         return Collections.emptyMap();
      } else {
         List<OrderInvoiceItem> items = this.orderInvoiceItemMapper.selectList((Wrapper<OrderInvoiceItem>)new LambdaQueryWrapper<OrderInvoiceItem>().in(OrderInvoiceItem::getOrderNo, orderNos));
         if (CollUtil.isEmpty(items)) {
            return Collections.emptyMap();
         } else {
            Set<String> invoiceIds = items.stream().map(OrderInvoiceItem::getInvoiceId).collect(Collectors.toSet());
            List<OrderInvoice> invoices = this.list(
               (Wrapper<OrderInvoice>)((LambdaQueryWrapper<OrderInvoice>)new LambdaQueryWrapper<OrderInvoice>().in(OrderInvoice::getId, invoiceIds))
                  .in(OrderInvoice::getStatus, new Object[]{InvoiceStatusEnum.PENDING.getCode(), InvoiceStatusEnum.ISSUED.getCode()})
            );
            Map<String, Integer> invoiceStatusMap = invoices.stream().collect(Collectors.toMap(OrderInvoice::getId, OrderInvoice::getStatus, (a, b) -> a));
            Map<String, Integer> result = new HashMap<>();

            for (OrderInvoiceItem item : items) {
               Integer status = invoiceStatusMap.get(item.getInvoiceId());
               if (status != null) {
                  result.put(item.getOrderNo(), status);
               }
            }

            return result;
         }
      }
   }

   @Override
   public boolean isOrderOccupied(String orderNo) {
      return StrUtil.isBlank(orderNo) ? false : this.findOccupiedStatusByOrderNos(Collections.singletonList(orderNo)).containsKey(orderNo);
   }

   @Override
   public void assertOrdersNotOccupied(List<String> orderNos) {
      Map<String, Integer> occupied = this.findOccupiedStatusByOrderNos(orderNos);
      if (!occupied.isEmpty()) {
         throw new CommonException("订单已被发票占用，无法退款：{}", new Object[]{String.join("、", occupied.keySet())});
      }
   }

   public void assertOrderStillInvoicable(Order order, String orderNo) {
      if (order == null) {
         throw new CommonException("订单不存在：{}", new Object[]{orderNo});
      } else if (!OrderStatusEnum.PAID.getCode().equals(order.getOrderStatus())) {
         throw new CommonException("订单{}不是已支付状态，无法开具发票", new Object[]{orderNo});
      } else if (this.hasSuccessfulRefund(order)) {
         throw new CommonException("订单{}已发生退款，无法开具发票", new Object[]{orderNo});
      }
   }

   private boolean hasSuccessfulRefund(Order order) {
      Set<String> serverIds = this.collectServerIds(order);
      if (serverIds.isEmpty()) {
         return false;
      } else {
         Long count = this.serverRefundRecordMapper
            .selectCount(
               (Wrapper<ServerRefundRecord>)((LambdaQueryWrapper<ServerRefundRecord>)new LambdaQueryWrapper<ServerRefundRecord>().eq(ServerRefundRecord::getStatus, 1)).in(ServerRefundRecord::getServerId, serverIds)
            );
         return count != null && count > 0L;
      }
   }

   private Set<String> collectServerIds(Order order) {
      Set<String> serverIds = new HashSet<>();
      List<OrderServer> orderServers = this.orderServerService.list((Wrapper<OrderServer>)new LambdaQueryWrapper<OrderServer>().eq(OrderServer::getOrderNo, order.getOrderNo()));
      if (CollUtil.isNotEmpty(orderServers)) {
         for (OrderServer orderServer : orderServers) {
            if (StrUtil.isNotBlank(orderServer.getServerId())) {
               serverIds.add(orderServer.getServerId());
            }
         }
      }

      if (StrUtil.isNotBlank(order.getUpstreamServerIds()) && JSONUtil.isTypeJSONArray(order.getUpstreamServerIds())) {
         for (Object value : JSONUtil.parseArray(order.getUpstreamServerIds())) {
            if (value != null && StrUtil.isNotBlank(String.valueOf(value))) {
               serverIds.add(String.valueOf(value));
            }
         }
      }

      return serverIds;
   }

   private OrderInvoice requirePending(String id) {
      OrderInvoice invoice = (OrderInvoice)this.getById(id);
      if (invoice == null) {
         throw new CommonException("发票申请不存在", new Object[0]);
      } else if (!InvoiceStatusEnum.PENDING.getCode().equals(invoice.getStatus())) {
         throw new CommonException("仅待审核的发票可以审核", new Object[0]);
      } else {
         return invoice;
      }
   }

   private List<OrderInvoiceItem> listItems(String invoiceId) {
      return this.orderInvoiceItemMapper
         .selectList(
            (Wrapper<OrderInvoiceItem>)((LambdaQueryWrapper<OrderInvoiceItem>)new LambdaQueryWrapper<OrderInvoiceItem>().eq(OrderInvoiceItem::getInvoiceId, invoiceId)).orderByAsc(OrderInvoiceItem::getCreateTime)
         );
   }

   private AdminOrderInvoiceResponse toResponseWithoutItems(OrderInvoice invoice) {
      AdminOrderInvoiceResponse response = new AdminOrderInvoiceResponse();
      BeanUtils.copyProperties(invoice, response);
      return response;
   }

   private void fillItems(List<AdminOrderInvoiceResponse> records) {
      if (!CollUtil.isEmpty(records)) {
         List<String> invoiceIds = records.stream().map(AdminOrderInvoiceResponse::getId).collect(Collectors.toList());
         List<OrderInvoiceItem> items = this.orderInvoiceItemMapper
            .selectList((Wrapper<OrderInvoiceItem>)new LambdaQueryWrapper<OrderInvoiceItem>().in(OrderInvoiceItem::getInvoiceId, invoiceIds));
         Map<String, List<OrderInvoiceItem>> itemMap = items.stream().collect(Collectors.groupingBy(OrderInvoiceItem::getInvoiceId));
         Set<String> orderNos = items.stream().map(OrderInvoiceItem::getOrderNo).collect(Collectors.toSet());
         Map<String, Order> orderMap = Collections.emptyMap();
         if (CollUtil.isNotEmpty(orderNos)) {
            List<Order> orders = this.orderMapper.selectList((Wrapper<Order>)new LambdaQueryWrapper<Order>().in(Order::getOrderNo, orderNos));
            orderMap = orders.stream().collect(Collectors.toMap(Order::getOrderNo, Function.identity(), (a, b) -> a));
         }

         for (AdminOrderInvoiceResponse record : records) {
            List<OrderInvoiceItem> invoiceItems = itemMap.getOrDefault(record.getId(), Collections.emptyList());
            List<AdminOrderInvoiceItemResponse> itemResponses = new ArrayList<>();

            for (OrderInvoiceItem item : invoiceItems) {
               AdminOrderInvoiceItemResponse itemResponse = new AdminOrderInvoiceItemResponse();
               itemResponse.setOrderNo(item.getOrderNo());
               itemResponse.setPayPrice(item.getPayPrice());
               Order order = orderMap.get(item.getOrderNo());
               if (order != null) {
                  itemResponse.setProductType(order.getProductType());
                  itemResponse.setOrderType(order.getOrderType());
                  itemResponse.setOrderStatus(order.getOrderStatus());
                  itemResponse.setPayTime(order.getPayTime());
               }

               itemResponses.add(itemResponse);
            }

            record.setItems(itemResponses);
         }
      }
   }
}
