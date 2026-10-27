package com.rxinyun.member.business.modular.independent.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.business.modular.independent.entity.MemberIndependentIp;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentIpAddParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentIpEditParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentIpIdParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentIpPageParam;
import java.util.List;

public interface MemberIndependentIpService {
   Page<MemberIndependentIp> page(MemberIndependentIpPageParam pageParam);

   void add(MemberIndependentIpAddParam serverIpAddParam);

   void edit(MemberIndependentIpEditParam serverIpEditParam);

   MemberIndependentIp detail(MemberIndependentIpIdParam serverIpIdParam);

   MemberIndependentIp queryEntity(String id);

   void deleteByServerId(String serverId);

   List<MemberIndependentIp> queryByServerId(String serverId);

   List<String> listServerIdByIp(String ip);
}
