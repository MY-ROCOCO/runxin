package com.rxinyun.admin.order.modular.invoice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.order.modular.invoice.entity.OrderInvoice;
import com.rxinyun.admin.order.modular.invoice.param.AdminOrderInvoiceIdParam;
import com.rxinyun.admin.order.modular.invoice.param.AdminOrderInvoicePageParam;
import com.rxinyun.admin.order.modular.invoice.param.AdminOrderInvoiceRejectParam;
import com.rxinyun.admin.order.modular.invoice.response.AdminOrderInvoiceResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface OrderInvoiceService extends IService<OrderInvoice> {
   Page<AdminOrderInvoiceResponse> page(AdminOrderInvoicePageParam pageParam);

   AdminOrderInvoiceResponse detail(AdminOrderInvoiceIdParam param);

   void approve(AdminOrderInvoiceIdParam param);

   void reject(AdminOrderInvoiceRejectParam param);

   Map<String, Integer> findOccupiedStatusByOrderNos(Collection<String> orderNos);

   boolean isOrderOccupied(String orderNo);

   void assertOrdersNotOccupied(List<String> orderNos);
}
