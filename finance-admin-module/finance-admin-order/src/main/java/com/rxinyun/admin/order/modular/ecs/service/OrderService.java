package com.rxinyun.admin.order.modular.ecs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.order.modular.ecs.entity.Order;
import com.rxinyun.admin.order.modular.ecs.param.OrderIdParam;
import com.rxinyun.admin.order.modular.ecs.param.OrderPageParam;
import com.rxinyun.admin.order.modular.ecs.response.OrderResponse;
import com.rxinyun.common.pojo.CommonResult;
import java.util.Date;

public interface OrderService extends IService<Order> {
   Page<Order> page(OrderPageParam pageParam);

   CommonResult<OrderResponse> detail(OrderIdParam orderIdParam);

   void paySuccess(String orderNo, Date payTime, String alipayPayNo, Integer payType);

   void paySuccessSync(String orderNo, Date payTime, String alipayPayNo, Integer payType);

   void markOrderPaid(String orderNo, Date payTime, String alipayPayNo, Integer payType);

   void handlePaidOrderAsync(String orderNo);

   void processPaidOrder(String orderNo);
}
