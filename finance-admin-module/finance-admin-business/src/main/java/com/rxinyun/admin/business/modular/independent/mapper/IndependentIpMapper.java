package com.rxinyun.admin.business.modular.independent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.business.modular.independent.entity.IndependentIp;

public interface IndependentIpMapper extends BaseMapper<IndependentIp> {
   void deleteByServerId(String serverId);
}
