package com.rxinyun.member.business.modular.esc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.business.modular.esc.entity.MemberServerStrategy;
import com.rxinyun.member.business.modular.esc.param.MemberServerStrategyAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerStrategyEditParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerStrategyIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerStrategyPageParam;
import java.util.List;

public interface MemberServerStrategyService {
   Page<MemberServerStrategy> page(MemberServerStrategyPageParam pageParam);

   CommonResult add(MemberServerStrategyAddParam serverIpAddParam);

   void edit(MemberServerStrategyEditParam serverIpEditParam);

   MemberServerStrategy detail(MemberServerStrategyIdParam serverIpIdParam);

   MemberServerStrategy queryEntity(String id);

   CommonResult delete(MemberServerStrategyIdParam serverStrategyIdParam);

   List<MemberServerStrategy> queryByServerId(String serverId);
}
