package com.rxinyun.member.business.modular.esc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.business.modular.esc.entity.MemberServerIp;
import com.rxinyun.member.business.modular.esc.param.MemberServerIpAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerIpEditParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerIpIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerIpPageParam;
import java.util.List;
import java.util.Map;

public interface MemberServerIpService {
   Page<MemberServerIp> page(MemberServerIpPageParam pageParam);

   void add(MemberServerIpAddParam serverIpAddParam);

   void edit(MemberServerIpEditParam serverIpEditParam);

   MemberServerIp detail(MemberServerIpIdParam serverIpIdParam);

   MemberServerIp queryEntity(String id);

   void deleteByServerId(String serverId);

   List<MemberServerIp> queryByServerId(String serverId);

   List<String> listServerIdByIp(String ip);

   Map<String, Integer> countPublicIpByServerIds(List<String> serverIds);
}
