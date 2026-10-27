package com.rxinyun.admin.business.modular.esc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.esc.entity.ServerStrategy;
import com.rxinyun.admin.business.modular.esc.param.ServerStrategyAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerStrategyEditParam;
import com.rxinyun.admin.business.modular.esc.param.ServerStrategyIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerStrategyPageParam;
import com.rxinyun.common.pojo.CommonResult;
import java.util.List;

public interface ServerStrategyService {
   Page<ServerStrategy> page(ServerStrategyPageParam pageParam);

   CommonResult add(ServerStrategyAddParam serverIpAddParam);

   void edit(ServerStrategyEditParam serverIpEditParam);

   ServerStrategy detail(ServerStrategyIdParam serverIpIdParam);

   ServerStrategy queryEntity(String id);

   CommonResult delete(ServerStrategyIdParam serverStrategyIdParam);

   List<ServerStrategy> queryByServerId(String serverId);
}
