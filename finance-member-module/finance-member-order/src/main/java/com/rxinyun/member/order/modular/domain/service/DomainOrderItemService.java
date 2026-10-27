package com.rxinyun.member.order.modular.domain.service;

import com.rxinyun.member.order.modular.domain.entity.DomainOrderItem;
import java.util.List;

public interface DomainOrderItemService {
   void saveBatch(List<DomainOrderItem> items);

   List<DomainOrderItem> listByOrderNo(String orderNo);
}
