package com.rxinyun.admin.production.modular.esc.available.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.available.entity.ServerAvailableRegion;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionAddParam;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionEditParam;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionEditSortParam;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionIdParam;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionPageParam;
import java.util.List;

public interface ServerAvailableRegionService {
   Page<ServerAvailableRegion> page(ServerAvailableRegionPageParam pageParam);

   void add(ServerAvailableRegionAddParam serverAvailableRegionAddParam);

   void edit(ServerAvailableRegionEditParam serverAvailableRegionEditParam);

   ServerAvailableRegion detail(ServerAvailableRegionIdParam serverAvailableRegionIdParam);

   ServerAvailableRegion queryEntity(String id);

   void delete(List<ServerAvailableRegionIdParam> serverAvailableRegionIdParamList);

   Boolean isBindingRegion(String id);

   void updateSort(ServerAvailableRegionEditSortParam serverAvailableRegionEditSortParam);
}
