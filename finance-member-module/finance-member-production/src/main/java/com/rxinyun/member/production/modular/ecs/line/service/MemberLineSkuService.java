package com.rxinyun.member.production.modular.ecs.line.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.ecs.line.entity.MemberLineSku;
import com.rxinyun.member.production.modular.ecs.line.param.MemberLineSkuIdParam;
import com.rxinyun.member.production.modular.ecs.line.param.MemberLineSkuPageParam;
import java.util.List;

public interface MemberLineSkuService {
   Page<MemberLineSku> page(MemberLineSkuPageParam pageParam);

   MemberLineSku detail(MemberLineSkuIdParam serverLineSkuIdParam);

   MemberLineSku queryEntity(String id);

   List<MemberLineSku> selectListByLineId(String lineId);

   MemberLineSku selectByLineSku(String lineId, Integer cpu, Integer ram);

   List<MemberLineSku> queryByCpu(MemberLineSkuIdParam memberLineSkuIdParam);

   List<MemberLineSku> queryByRam(MemberLineSkuIdParam memberLineSkuIdParam);
}
