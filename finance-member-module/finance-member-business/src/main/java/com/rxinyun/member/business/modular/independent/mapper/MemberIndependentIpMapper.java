package com.rxinyun.member.business.modular.independent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.member.business.modular.independent.entity.MemberIndependentIp;

public interface MemberIndependentIpMapper extends BaseMapper<MemberIndependentIp> {
   void deleteByServerId(String serverId);
}
