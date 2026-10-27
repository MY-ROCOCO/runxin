package com.rxinyun.admin.production.modular.esc.cloud.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.cloud.entity.ServerDiscounts;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerDiscountsAddParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerDiscountsEditParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerDiscountsIdParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerDiscountsPageParam;
import java.util.List;

public interface ServerDiscountsService {
   Page<ServerDiscounts> page(ServerDiscountsPageParam pageParam);

   void add(ServerDiscountsAddParam serverDiscountsAddParam);

   void edit(ServerDiscountsEditParam serverDiscountsEditParam);

   ServerDiscounts detail(ServerDiscountsIdParam serverDiscountsIdParam);

   ServerDiscounts queryEntity(String id);

   void delete(List<ServerDiscountsIdParam> serverDiscountsIdParamList);

   List<ServerDiscounts> selectListByLineId(String lineId);
}
