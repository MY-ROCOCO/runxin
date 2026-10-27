package com.rxinyun.member.order.modular.ecs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MemberOrderMapper extends BaseMapper<MemberOrder> {
   List<MemberOrder> selectByServerId(@Param("serverId") String serverId, @Param("orderStatus") Integer orderStatus);

   List<MemberOrder> selectPaidRefundableOrdersByServerIdAndMemberNo(@Param("serverId") String serverId, @Param("memberNo") Long memberNo);

   MemberOrder selectLatestByServerId(String serverId);
}
