package com.rxinyun.member.production.modular.ecs.line.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.member.production.modular.ecs.line.entity.MemberLocalLine;

public interface MemberLocalLineMapper extends BaseMapper<MemberLocalLine> {
   MemberLocalLine getById(String id);
}
