package com.rxinyun.member.order.modular.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.member.order.modular.domain.entity.DomainOrderItem;
import com.rxinyun.member.order.modular.domain.mapper.DomainOrderItemMapper;
import com.rxinyun.member.order.modular.domain.service.DomainOrderItemService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DomainOrderItemServiceImpl implements DomainOrderItemService {
   @Resource
   private DomainOrderItemMapper domainOrderItemMapper;

   @Override
   public void saveBatch(List<DomainOrderItem> items) {
      if (items != null && !items.isEmpty()) {
         for (DomainOrderItem item : items) {
            this.domainOrderItemMapper.insert(item);
         }
      }
   }

   @Override
   public List<DomainOrderItem> listByOrderNo(String orderNo) {
      return this.domainOrderItemMapper.selectList((Wrapper<DomainOrderItem>)new LambdaQueryWrapper<DomainOrderItem>().eq(DomainOrderItem::getOrderNo, orderNo));
   }
}
