package com.rxinyun.admin.production.modular.vhost.region.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.vhost.region.entity.VhostRegion;
import com.rxinyun.admin.production.modular.vhost.region.param.VhostRegionAddParam;
import com.rxinyun.admin.production.modular.vhost.region.param.VhostRegionEditParam;
import com.rxinyun.admin.production.modular.vhost.region.param.VhostRegionIdParam;
import com.rxinyun.admin.production.modular.vhost.region.param.VhostRegionPageParam;
import java.util.List;

public interface VhostRegionService {
   Page<VhostRegion> page(VhostRegionPageParam pageParam);

   void add(VhostRegionAddParam addParam);

   void edit(VhostRegionEditParam editParam);

   VhostRegion detail(VhostRegionIdParam idParam);

   void delete(List<VhostRegionIdParam> idParamList);

   List<VhostRegion> selector();
}
