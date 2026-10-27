package com.rxinyun.member.production.modular.ecs.os.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.ecs.os.entity.MemberOsVersion;
import com.rxinyun.member.production.modular.ecs.os.param.OsVersionIdParam;
import com.rxinyun.member.production.modular.ecs.os.param.OsVersionPageParam;

public interface MemberOsVersionService {
   Page<MemberOsVersion> page(OsVersionPageParam pageParam);

   MemberOsVersion detail(OsVersionIdParam osVersionIdParam);

   MemberOsVersion queryEntity(String id);
}
