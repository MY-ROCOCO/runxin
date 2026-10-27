package com.rxinyun.member.production.modular.ecs.available.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.ecs.available.entity.MemberAvailableRegion;
import com.rxinyun.member.production.modular.ecs.available.param.MemberAvailableRegionIdParam;
import com.rxinyun.member.production.modular.ecs.available.param.MemberAvailableRegionPageParam;
import com.rxinyun.member.production.modular.ecs.available.response.MemberAvailableRegionResponse;
import java.util.List;

public interface MemberAvailableRegionService {
   Page<MemberAvailableRegion> page(MemberAvailableRegionPageParam pageParam);

   MemberAvailableRegion detail(MemberAvailableRegionIdParam serverAvailableRegionIdParam);

   MemberAvailableRegion queryEntity(String id);

   List<MemberAvailableRegionResponse> queryByRegionId(String regionId);
}
