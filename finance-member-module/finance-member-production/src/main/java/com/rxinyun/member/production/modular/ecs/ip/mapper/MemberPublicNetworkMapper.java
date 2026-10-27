package com.rxinyun.member.production.modular.ecs.ip.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.member.production.modular.ecs.ip.entity.MemberPublicNetwork;
import org.apache.ibatis.annotations.Param;

public interface MemberPublicNetworkMapper extends BaseMapper<MemberPublicNetwork> {
   MemberPublicNetwork selectAvailablePublicNetwork(@Param("masterId") String masterId, @Param("ipRand") Integer ipRand);
}
