package com.rxinyun.admin.production.modular.esc.region.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.region.entity.ServerRegion;
import com.rxinyun.admin.production.modular.esc.region.param.ServerRegionAddParam;
import com.rxinyun.admin.production.modular.esc.region.param.ServerRegionEditParam;
import com.rxinyun.admin.production.modular.esc.region.param.ServerRegionEditSortParam;
import com.rxinyun.admin.production.modular.esc.region.param.ServerRegionIdParam;
import com.rxinyun.admin.production.modular.esc.region.param.ServerRegionPageParam;
import java.util.List;

public interface ServerRegionService {
   Page<ServerRegion> page(ServerRegionPageParam pageParam);

   void add(ServerRegionAddParam serverRegionAddParam);

   void edit(ServerRegionEditParam serverRegionEditParam);

   ServerRegion detail(ServerRegionIdParam serverRegionIdParam);

   ServerRegion queryEntity(String id);

   void delete(List<ServerRegionIdParam> serverRegionIdParamList);

   void updateSort(ServerRegionEditSortParam serverRegionEditSortParam);
}
