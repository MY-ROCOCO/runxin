package com.rxinyun.member.business.modular.esc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.business.modular.esc.entity.MemberServerPort;
import com.rxinyun.member.business.modular.esc.param.MemberServerPortAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerPortIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerPortPageParam;
import java.util.List;

public interface MemberServerPortService {
   Page<MemberServerPort> page(MemberServerPortPageParam pageParam);

   MemberServerPort add(MemberServerPortAddParam memberServerPortAddParam);

   MemberServerPort detail(MemberServerPortIdParam memberServerPortIdParam);

   MemberServerPort queryEntity(String id);

   void delete(List<MemberServerPortIdParam> memberServerPortIdParam);

   void delete(String serverId);
}
