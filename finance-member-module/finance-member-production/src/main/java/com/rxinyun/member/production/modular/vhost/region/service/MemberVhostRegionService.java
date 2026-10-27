package com.rxinyun.member.production.modular.vhost.region.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.vhost.region.entity.MemberVhostRegion;
import com.rxinyun.member.production.modular.vhost.region.param.MemberVhostRegionPageParam;

public interface MemberVhostRegionService {
   Page<MemberVhostRegion> page(MemberVhostRegionPageParam pageParam);
}
