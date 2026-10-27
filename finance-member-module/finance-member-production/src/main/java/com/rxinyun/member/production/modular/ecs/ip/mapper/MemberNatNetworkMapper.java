package com.rxinyun.member.production.modular.ecs.ip.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.member.production.modular.ecs.ip.entity.MemberNatNetwork;
import org.apache.ibatis.annotations.Param;

public interface MemberNatNetworkMapper extends BaseMapper<MemberNatNetwork> {
   MemberNatNetwork selectAvailableNatNetwork(@Param("masterId") String masterId);
}
