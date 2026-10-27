package com.rxinyun.member.order.modular.invoice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.order.InvoiceStatusEnum;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrderServer;
import com.rxinyun.member.order.modular.ecs.entity.MemberServerRefundRecord;
import com.rxinyun.member.order.modular.ecs.mapper.MemberOrderMapper;
import com.rxinyun.member.order.modular.ecs.mapper.MemberServerRefundRecordMapper;
import com.rxinyun.member.order.modular.ecs.service.MemberOrderServerService;
import com.rxinyun.member.order.modular.invoice.entity.MemberOrderInvoice;
import com.rxinyun.member.order.modular.invoice.entity.MemberOrderInvoiceItem;
import com.rxinyun.member.order.modular.invoice.mapper.MemberOrderInvoiceItemMapper;
import com.rxinyun.member.order.modular.invoice.mapper.MemberOrderInvoiceMapper;
import com.rxinyun.member.order.modular.invoice.param.MemberInvoicableOrderPageParam;
import com.rxinyun.member.order.modular.invoice.param.MemberOrderInvoiceApplyParam;
import com.rxinyun.member.order.modular.invoice.param.MemberOrderInvoiceIdParam;
import com.rxinyun.member.order.modular.invoice.param.MemberOrderInvoicePageParam;
import com.rxinyun.member.order.modular.invoice.response.MemberInvoicableOrderResponse;
import com.rxinyun.member.order.modular.invoice.response.MemberOrderInvoiceItemResponse;
import com.rxinyun.member.order.modular.invoice.response.MemberOrderInvoiceResponse;
import com.rxinyun.member.order.modular.invoice.response.MemberOrderInvoiceTitleResponse;
import com.rxinyun.member.order.modular.invoice.service.MemberOrderInvoiceService;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberOrderInvoiceServiceImpl extends ServiceImpl<MemberOrderInvoiceMapper, MemberOrderInvoice> implements MemberOrderInvoiceService {
   private static final String OCCUPIED_NOT_EXISTS_SQL = "NOT EXISTS (SELECT 1 FROM ORDER_INVOICE_ITEM i INNER JOIN ORDER_INVOICE inv ON inv.ID = i.INVOICE_ID WHERE i.ORDER_NO = ORDERS.ORDER_NO AND inv.STATUS IN (0, 1))";
   private static final String REFUND_NOT_EXISTS_SQL = "NOT EXISTS (SELECT 1 FROM SERVER_REFUND_RECORD r WHERE r.STATUS = 1 AND (EXISTS (SELECT 1 FROM ORDER_SERVER os WHERE os.ORDER_NO = ORDERS.ORDER_NO AND os.SERVER_ID = r.SERVER_ID) OR (ORDERS.UPSTREAM_SERVER_IDS IS NOT NULL AND ORDERS.UPSTREAM_SERVER_IDS LIKE CONCAT('%', r.SERVER_ID, '%'))))";
   @Resource
   private MemberOrderInvoiceItemMapper memberOrderInvoiceItemMapper;
   @Resource
   private MemberOrderMapper memberOrderMapper;
   @Resource
   private MemberOrderServerService memberOrderServerService;
   @Resource
   private MemberServerRefundRecordMapper memberServerRefundRecordMapper;

   @Override
   public Page<MemberInvoicableOrderResponse> availablePage(MemberInvoicableOrderPageParam pageParam) {
      Long memberNo = this.currentMemberNo();
      QueryWrapper<MemberOrder> queryWrapper = new QueryWrapper().checkSqlInjection();
      ((LambdaQueryWrapper<MemberOrder>)queryWrapper.lambda().eq(MemberOrder::getMemberNo, memberNo)).eq(MemberOrder::getOrderStatus, OrderStatusEnum.PAID.getCode());
      if (StrUtil.isNotBlank(pageParam.getOrderNo())) {
         queryWrapper.lambda().eq(MemberOrder::getOrderNo, pageParam.getOrderNo());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getProductType())) {
         queryWrapper.lambda().eq(MemberOrder::getProductType, pageParam.getProductType());
      }

      if (StrUtil.isNotBlank(pageParam.getPayTimeStart())) {
         queryWrapper.lambda().ge(MemberOrder::getPayTime, pageParam.getPayTimeStart());
      }

      if (StrUtil.isNotBlank(pageParam.getPayTimeEnd())) {
         queryWrapper.lambda().le(MemberOrder::getPayTime, pageParam.getPayTimeEnd());
      }

      queryWrapper.apply(
         "NOT EXISTS (SELECT 1 FROM ORDER_INVOICE_ITEM i INNER JOIN ORDER_INVOICE inv ON inv.ID = i.INVOICE_ID WHERE i.ORDER_NO = ORDERS.ORDER_NO AND inv.STATUS IN (0, 1))",
         new Object[0]
      );
      queryWrapper.apply(
         "NOT EXISTS (SELECT 1 FROM SERVER_REFUND_RECORD r WHERE r.STATUS = 1 AND (EXISTS (SELECT 1 FROM ORDER_SERVER os WHERE os.ORDER_NO = ORDERS.ORDER_NO AND os.SERVER_ID = r.SERVER_ID) OR (ORDERS.UPSTREAM_SERVER_IDS IS NOT NULL AND ORDERS.UPSTREAM_SERVER_IDS LIKE CONCAT('%', r.SERVER_ID, '%'))))",
         new Object[0]
      );
      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(MemberOrder::getPayTime);
      }

      Page<MemberOrder> orderPage = (Page<MemberOrder>)this.memberOrderMapper.selectPage(CommonPageRequest.defaultPage(), queryWrapper);
      Page<MemberInvoicableOrderResponse> result = new Page(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
      result.setRecords(orderPage.getRecords().stream().map(order -> {
         MemberInvoicableOrderResponse response = new MemberInvoicableOrderResponse();
         response.setOrderNo(order.getOrderNo());
         response.setProductType(order.getProductType());
         response.setOrderType(order.getOrderType());
         response.setPayPrice(order.getPayPrice());
         response.setPayTime(order.getPayTime());
         response.setCreateTime(order.getCreateTime());
         return response;
      }).collect(Collectors.toList()));
      return result;
   }

   @Override
   public MemberOrderInvoiceTitleResponse lastTitle() {
      Long memberNo = this.currentMemberNo();
      MemberOrderInvoice latest = (MemberOrderInvoice)this.getOne(
         (Wrapper<MemberOrderInvoice>)((LambdaQueryWrapper<MemberOrderInvoice>)((LambdaQueryWrapper<MemberOrderInvoice>)new LambdaQueryWrapper<MemberOrderInvoice>().eq(MemberOrderInvoice::getMemberNo, memberNo))
               .orderByDesc(MemberOrderInvoice::getApplyTime))
            .last("LIMIT 1")
      );
      if (latest == null) {
         return new MemberOrderInvoiceTitleResponse();
      } else {
         MemberOrderInvoiceTitleResponse response = new MemberOrderInvoiceTitleResponse();
         response.setTitle(latest.getTitle());
         response.setTaxNo(latest.getTaxNo());
         response.setEmail(latest.getEmail());
         return response;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public MemberOrderInvoiceResponse apply(MemberOrderInvoiceApplyParam param) {
      Long memberNo = this.currentMemberNo();
      List<String> orderNos = param.getOrderNos()
         .stream()
         .filter(CharSequenceUtil::isNotBlank)
         .map(String::trim)
         .collect(Collectors.toCollection(LinkedHashSet::new))
         .stream()
         .collect(Collectors.toList());
      if (orderNos.isEmpty()) {
         throw new CommonException("请至少选择一笔订单", new Object[0]);
      } else {
         List<MemberOrder> orders = this.memberOrderMapper
            .selectList((Wrapper<MemberOrder>)((LambdaQueryWrapper<MemberOrder>)new LambdaQueryWrapper<MemberOrder>().eq(MemberOrder::getMemberNo, memberNo)).in(MemberOrder::getOrderNo, orderNos));
         Map<String, MemberOrder> orderMap = orders.stream().collect(Collectors.toMap(MemberOrder::getOrderNo, Function.identity(), (a, b) -> a));

         for (String orderNo : orderNos) {
            MemberOrder order = orderMap.get(orderNo);
            if (order == null) {
               throw new CommonException("订单不存在或不属于当前会员：{}", new Object[]{orderNo});
            }

            this.assertOrderInvoicable(order);
         }

         Map<String, Integer> occupied = this.findOccupiedStatusByOrderNos(orderNos);
         if (!occupied.isEmpty()) {
            throw new CommonException("订单已在其他发票中：{}", new Object[]{String.join("、", occupied.keySet())});
         } else {
            BigDecimal amount = orders.stream()
               .map(MemberOrder::getPayPrice)
               .filter(price -> price != null && price.compareTo(BigDecimal.ZERO) > 0)
               .reduce(BigDecimal.ZERO, BigDecimal::add);
            Date now = new Date();
            MemberOrderInvoice invoice = new MemberOrderInvoice();
            invoice.setId(SnowflakeIdGenerator.generateId());
            invoice.setInvoiceNo("INV" + SnowflakeIdGenerator.generateId());
            invoice.setMemberNo(memberNo);
            invoice.setTitle(param.getTitle().trim());
            invoice.setTaxNo(param.getTaxNo().trim());
            invoice.setEmail(param.getEmail().trim());
            invoice.setInvoiceAmount(amount);
            invoice.setOrderCount(orderNos.size());
            invoice.setStatus(InvoiceStatusEnum.PENDING.getCode());
            invoice.setApplyTime(now);
            this.save(invoice);
            List<MemberOrderInvoiceItem> items = new ArrayList<>();

            for (String orderNo : orderNos) {
               MemberOrder order = orderMap.get(orderNo);
               MemberOrderInvoiceItem item = new MemberOrderInvoiceItem();
               item.setId(SnowflakeIdGenerator.generateId());
               item.setInvoiceId(invoice.getId());
               item.setOrderNo(orderNo);
               item.setMemberNo(memberNo);
               item.setPayPrice(order.getPayPrice() == null ? BigDecimal.ZERO : order.getPayPrice());
               item.setCreateTime(now);
               items.add(item);
            }

            try {
               for (MemberOrderInvoiceItem item : items) {
                  this.memberOrderInvoiceItemMapper.insert(item);
               }
            } catch (DuplicateKeyException var15) {
               throw new CommonException("订单已在其他发票中，请刷新后重试", new Object[0]);
            }

            MemberOrderInvoiceResponse response = this.toResponseWithoutItems(invoice);
            this.fillItems(Collections.singletonList(response));
            return response;
         }
      }
   }

   @Override
   public Page<MemberOrderInvoiceResponse> page(MemberOrderInvoicePageParam pageParam) {
      Long memberNo = this.currentMemberNo();
      QueryWrapper<MemberOrderInvoice> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(MemberOrderInvoice::getMemberNo, memberNo);
      if (StrUtil.isNotBlank(pageParam.getInvoiceNo())) {
         queryWrapper.lambda().eq(MemberOrderInvoice::getInvoiceNo, pageParam.getInvoiceNo());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         queryWrapper.lambda().eq(MemberOrderInvoice::getStatus, pageParam.getStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(MemberOrderInvoice::getApplyTime);
      }

      Page<MemberOrderInvoice> entityPage = (Page<MemberOrderInvoice>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      Page<MemberOrderInvoiceResponse> result = new Page(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
      List<MemberOrderInvoiceResponse> records = entityPage.getRecords().stream().map(this::toResponseWithoutItems).collect(Collectors.toList());
      this.fillItems(records);
      result.setRecords(records);
      return result;
   }

   @Override
   public MemberOrderInvoiceResponse detail(MemberOrderInvoiceIdParam param) {
      Long memberNo = this.currentMemberNo();
      MemberOrderInvoice invoice = (MemberOrderInvoice)this.getOne(
         (Wrapper<MemberOrderInvoice>)((LambdaQueryWrapper<MemberOrderInvoice>)new LambdaQueryWrapper<MemberOrderInvoice>().eq(MemberOrderInvoice::getId, param.getId())).eq(MemberOrderInvoice::getMemberNo, memberNo)
      );
      if (invoice == null) {
         throw new CommonException("发票申请不存在", new Object[0]);
      } else {
         MemberOrderInvoiceResponse response = this.toResponseWithoutItems(invoice);
         this.fillItems(Collections.singletonList(response));
         return response;
      }
   }

   @Override
   public Map<String, Integer> findOccupiedStatusByOrderNos(Collection<String> orderNos) {
      if (CollUtil.isEmpty(orderNos)) {
         return Collections.emptyMap();
      } else {
         List<MemberOrderInvoiceItem> items = this.memberOrderInvoiceItemMapper
            .selectList((Wrapper<MemberOrderInvoiceItem>)new LambdaQueryWrapper<MemberOrderInvoiceItem>().in(MemberOrderInvoiceItem::getOrderNo, orderNos));
         if (CollUtil.isEmpty(items)) {
            return Collections.emptyMap();
         } else {
            Set<String> invoiceIds = items.stream().map(MemberOrderInvoiceItem::getInvoiceId).collect(Collectors.toSet());
            List<MemberOrderInvoice> invoices = this.list(
               (Wrapper<MemberOrderInvoice>)((LambdaQueryWrapper<MemberOrderInvoice>)new LambdaQueryWrapper<MemberOrderInvoice>().in(MemberOrderInvoice::getId, invoiceIds))
                  .in(MemberOrderInvoice::getStatus, new Object[]{InvoiceStatusEnum.PENDING.getCode(), InvoiceStatusEnum.ISSUED.getCode()})
            );
            Map<String, Integer> invoiceStatusMap = invoices.stream()
               .collect(Collectors.toMap(MemberOrderInvoice::getId, MemberOrderInvoice::getStatus, (a, b) -> a));
            Map<String, Integer> result = new HashMap<>();

            for (MemberOrderInvoiceItem item : items) {
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
   public void assertOrdersNotOccupied(List<String> orderNos) {
      Map<String, Integer> occupied = this.findOccupiedStatusByOrderNos(orderNos);
      if (!occupied.isEmpty()) {
         throw new CommonException("订单已被发票占用，无法退款：{}", new Object[]{String.join("、", occupied.keySet())});
      }
   }

   private void assertOrderInvoicable(MemberOrder order) {
      if (!OrderStatusEnum.PAID.getCode().equals(order.getOrderStatus())) {
         throw new CommonException("订单{}不是已支付状态，无法开票", new Object[]{order.getOrderNo()});
      } else if (this.hasSuccessfulRefund(order)) {
         throw new CommonException("订单{}已发生退款，无法开票", new Object[]{order.getOrderNo()});
      }
   }

   private boolean hasSuccessfulRefund(MemberOrder order) {
      Set<String> serverIds = this.collectServerIds(order);
      if (serverIds.isEmpty()) {
         return false;
      } else {
         Long count = this.memberServerRefundRecordMapper
            .selectCount(
               (Wrapper<MemberServerRefundRecord>)((LambdaQueryWrapper<MemberServerRefundRecord>)new LambdaQueryWrapper<MemberServerRefundRecord>().eq(MemberServerRefundRecord::getStatus, 1))
                  .in(MemberServerRefundRecord::getServerId, serverIds)
            );
         return count != null && count > 0L;
      }
   }

   private Set<String> collectServerIds(MemberOrder order) {
      Set<String> serverIds = new HashSet<>();
      List<MemberOrderServer> orderServers = this.memberOrderServerService
         .list((Wrapper<MemberOrderServer>)new LambdaQueryWrapper<MemberOrderServer>().eq(MemberOrderServer::getOrderNo, order.getOrderNo()));
      if (CollUtil.isNotEmpty(orderServers)) {
         for (MemberOrderServer orderServer : orderServers) {
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

   private Long currentMemberNo() {
      SaBaseClientLoginUser loginUser = StpClientLoginUserUtil.getClientLoginUser();
      if (loginUser != null && loginUser.getId() != null) {
         return loginUser.getId();
      } else {
         throw new CommonException("请先登录", new Object[0]);
      }
   }

   private MemberOrderInvoiceResponse toResponseWithoutItems(MemberOrderInvoice invoice) {
      MemberOrderInvoiceResponse response = new MemberOrderInvoiceResponse();
      BeanUtils.copyProperties(invoice, response);
      return response;
   }

   private void fillItems(List<MemberOrderInvoiceResponse> records) {
      if (!CollUtil.isEmpty(records)) {
         List<String> invoiceIds = records.stream().map(MemberOrderInvoiceResponse::getId).collect(Collectors.toList());
         List<MemberOrderInvoiceItem> items = this.memberOrderInvoiceItemMapper
            .selectList((Wrapper<MemberOrderInvoiceItem>)new LambdaQueryWrapper<MemberOrderInvoiceItem>().in(MemberOrderInvoiceItem::getInvoiceId, invoiceIds));
         Map<String, List<MemberOrderInvoiceItem>> itemMap = items.stream().collect(Collectors.groupingBy(MemberOrderInvoiceItem::getInvoiceId));
         Set<String> orderNos = items.stream().map(MemberOrderInvoiceItem::getOrderNo).collect(Collectors.toSet());
         Map<String, MemberOrder> orderMap = Collections.emptyMap();
         if (CollUtil.isNotEmpty(orderNos)) {
            List<MemberOrder> orders = this.memberOrderMapper.selectList((Wrapper<MemberOrder>)new LambdaQueryWrapper<MemberOrder>().in(MemberOrder::getOrderNo, orderNos));
            orderMap = orders.stream().collect(Collectors.toMap(MemberOrder::getOrderNo, Function.identity(), (a, b) -> a));
         }

         for (MemberOrderInvoiceResponse record : records) {
            List<MemberOrderInvoiceItem> invoiceItems = itemMap.getOrDefault(record.getId(), Collections.emptyList());
            List<MemberOrderInvoiceItemResponse> itemResponses = new ArrayList<>();

            for (MemberOrderInvoiceItem item : invoiceItems) {
               MemberOrderInvoiceItemResponse itemResponse = new MemberOrderInvoiceItemResponse();
               itemResponse.setOrderNo(item.getOrderNo());
               itemResponse.setPayPrice(item.getPayPrice());
               MemberOrder order = orderMap.get(item.getOrderNo());
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
