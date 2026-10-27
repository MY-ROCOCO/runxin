package com.rxinyun.admin.production.modular.independent.region.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.independent.region.model.dto.RegionDTO;
import com.rxinyun.admin.production.modular.independent.region.model.dto.RegionPageParam;
import com.rxinyun.admin.production.modular.independent.region.model.entity.Region;
import com.rxinyun.admin.production.modular.independent.region.model.vo.RegionVO;
import com.rxinyun.common.pojo.CommonResult;
import java.util.List;

public interface IRegionService {
   Page<RegionVO> page(RegionPageParam pageParam);

   void add(RegionDTO regionDTO);

   Region searchRegionById(String id);

   void edit(RegionDTO regionDTO);

   void delete(String id);

   CommonResult<List<Region>> subduct();
}
