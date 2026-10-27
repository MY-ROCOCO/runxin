package com.rxinyun.member.production.modular.ecs.os.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.ecs.os.entity.MemberOsClass;
import com.rxinyun.member.production.modular.ecs.os.param.OsClassIdParam;
import com.rxinyun.member.production.modular.ecs.os.param.OsClassPageParam;

public interface MemberOsClassService {
   Page<MemberOsClass> page(OsClassPageParam pageParam);

   MemberOsClass detail(OsClassIdParam osClassIdParam);

   MemberOsClass queryEntity(String id);
}
