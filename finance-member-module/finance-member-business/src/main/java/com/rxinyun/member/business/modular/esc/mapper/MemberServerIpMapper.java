package com.rxinyun.member.business.modular.esc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.member.business.modular.esc.entity.MemberServerIp;

public interface MemberServerIpMapper extends BaseMapper<MemberServerIp> {
   void deleteByServerId(String serverId);
}
