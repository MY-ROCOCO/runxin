package com.rxinyun.member.production.modular.ecs.region.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.ecs.region.entity.MemberRegion;
import com.rxinyun.member.production.modular.ecs.region.param.MemberRegionIdParam;
import com.rxinyun.member.production.modular.ecs.region.param.MemberRegionPageParam;

public interface MemberRegionService {
   Page<MemberRegion> page(MemberRegionPageParam pageParam);

   MemberRegion detail(MemberRegionIdParam serverRegionIdParam);

   MemberRegion queryEntity(String id);
}
