package com.rxinyun.member.order.modular.invoice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.member.order.modular.invoice.entity.MemberOrderInvoice;
import com.rxinyun.member.order.modular.invoice.param.MemberInvoicableOrderPageParam;
import com.rxinyun.member.order.modular.invoice.param.MemberOrderInvoiceApplyParam;
import com.rxinyun.member.order.modular.invoice.param.MemberOrderInvoiceIdParam;
import com.rxinyun.member.order.modular.invoice.param.MemberOrderInvoicePageParam;
import com.rxinyun.member.order.modular.invoice.response.MemberInvoicableOrderResponse;
import com.rxinyun.member.order.modular.invoice.response.MemberOrderInvoiceResponse;
import com.rxinyun.member.order.modular.invoice.response.MemberOrderInvoiceTitleResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface MemberOrderInvoiceService extends IService<MemberOrderInvoice> {
   Page<MemberInvoicableOrderResponse> availablePage(MemberInvoicableOrderPageParam pageParam);

   MemberOrderInvoiceTitleResponse lastTitle();

   MemberOrderInvoiceResponse apply(MemberOrderInvoiceApplyParam param);

   Page<MemberOrderInvoiceResponse> page(MemberOrderInvoicePageParam pageParam);

   MemberOrderInvoiceResponse detail(MemberOrderInvoiceIdParam param);

   Map<String, Integer> findOccupiedStatusByOrderNos(Collection<String> orderNos);

   void assertOrdersNotOccupied(List<String> orderNos);
}
