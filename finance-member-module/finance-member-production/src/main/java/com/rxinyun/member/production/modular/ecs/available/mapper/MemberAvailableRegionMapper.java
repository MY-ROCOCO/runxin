package com.rxinyun.member.production.modular.ecs.available.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.member.production.modular.ecs.available.entity.MemberAvailableRegion;
import com.rxinyun.member.production.modular.ecs.available.response.MemberAvailableRegionResponse;
import java.util.List;

public interface MemberAvailableRegionMapper extends BaseMapper<MemberAvailableRegion> {
   List<MemberAvailableRegionResponse> selectByRegionId(String regionId);
}
