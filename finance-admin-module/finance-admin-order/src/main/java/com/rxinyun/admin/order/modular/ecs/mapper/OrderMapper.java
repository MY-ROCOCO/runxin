package com.rxinyun.admin.order.modular.ecs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.order.modular.ecs.entity.Order;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderMapper extends BaseMapper<Order> {
   Order selectLatestByServerId(String id);

   Order selectLatestPaidOrderForRenew(String serverId);

   List<Order> selectByServerId(@Param("serverId") String serverId, @Param("orderStatus") Integer code);

   List<Order> selectPaidRefundableOrdersByServerIds(@Param("serverIds") List<String> serverIds, @Param("memberNo") Long memberNo);
}
