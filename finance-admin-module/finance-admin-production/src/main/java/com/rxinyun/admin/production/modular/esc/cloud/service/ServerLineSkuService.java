package com.rxinyun.admin.production.modular.esc.cloud.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.cloud.entity.ServerLineSku;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLineSkuAddParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLineSkuEditParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLineSkuIdParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLineSkuPageParam;
import java.util.List;

public interface ServerLineSkuService {
   Page<ServerLineSku> page(ServerLineSkuPageParam pageParam);

   void add(ServerLineSkuAddParam serverLineSkuAddParam);

   void edit(ServerLineSkuEditParam serverLineSkuEditParam);

   ServerLineSku detail(ServerLineSkuIdParam serverLineSkuIdParam);

   ServerLineSku queryEntity(String id);

   void delete(List<ServerLineSkuIdParam> serverLineSkuIdParamList);

   List<ServerLineSku> selectListByLineId(String lineId);

   void deleteByLineIdList(List<String> serverLocalLineIdList);
}
