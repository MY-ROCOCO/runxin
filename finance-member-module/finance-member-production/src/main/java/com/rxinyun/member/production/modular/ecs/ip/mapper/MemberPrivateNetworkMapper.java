package com.rxinyun.member.production.modular.ecs.ip.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.member.production.modular.ecs.ip.entity.MemberPrivateNetwork;
import org.apache.ibatis.annotations.Param;

public interface MemberPrivateNetworkMapper extends BaseMapper<MemberPrivateNetwork> {
   MemberPrivateNetwork selectAvailablePrivateNetwork(@Param("masterId") String masterId);
}
