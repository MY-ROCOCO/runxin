package com.rxinyun.admin.production.modular.esc.cloud.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.cloud.entity.ServerLocalLine;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLineAddParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLineEditParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLineEditSortParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLineIdParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLinePageParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLineUpdateDiscountTemplateParam;
import com.rxinyun.admin.production.modular.esc.cloud.response.ServerLocalLineResponse;
import com.rxinyun.admin.production.modular.esc.cloud.vo.ServerLocalLineVO;
import java.util.List;
import java.util.Map;

public interface ServerLocalLineService {
   Page<ServerLocalLineResponse> page(ServerLocalLinePageParam pageParam);

   void add(ServerLocalLineAddParam serverLocalLineAddParam);

   void edit(ServerLocalLineEditParam serverLocalLineEditParam);

   ServerLocalLineVO detail(ServerLocalLineIdParam serverLocalLineIdParam);

   ServerLocalLine queryEntity(String id);

   void delete(List<ServerLocalLineIdParam> serverLocalLineIdParamList);

   Boolean isBindingAvailable(String id);

   Map<String, String> lineList();

   void updateSort(ServerLocalLineEditSortParam serverLocalLineEditSortParam);

   void updateDiscountTemplate(ServerLocalLineUpdateDiscountTemplateParam serverLocalLineUpdateTemplateParam);
}
