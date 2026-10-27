package com.rxinyun.member.production.modular.independent.region.service;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.independent.region.model.entity.Region;
import java.util.List;

public interface IMemberRegionService {
   Region searchRegionById(String id);

   CommonResult<List<Region>> purchase();
}
