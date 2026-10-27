package com.rxinyun.member.production.modular.ecs.line.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.ecs.line.entity.MemberLocalLine;
import com.rxinyun.member.production.modular.ecs.line.param.MemberLocalLineIdParam;
import com.rxinyun.member.production.modular.ecs.line.param.MemberLocalLinePageParam;
import com.rxinyun.member.production.modular.ecs.line.vo.MemberLocalLineVO;
import java.util.List;
import java.util.Map;

public interface MemberLocalLineService {
   Page<MemberLocalLine> page(MemberLocalLinePageParam pageParam);

   MemberLocalLineVO detail(MemberLocalLineIdParam serverLocalLineIdParam);

   MemberLocalLine queryEntity(String id);

   List<Map<String, Object>> queryByAvailableId(String availableId);
}
