package com.rxinyun.admin.production.modular.esc.ip.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.ip.entity.PrivateNetwork;
import com.rxinyun.admin.production.modular.esc.ip.param.PrivateNetworkAddParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PrivateNetworkEditParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PrivateNetworkIdParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PrivateNetworkPageParam;
import com.rxinyun.common.pojo.CommonResult;
import java.util.List;

public interface PrivateNetworkService {
   Page<PrivateNetwork> page(PrivateNetworkPageParam pageParam);

   CommonResult<String> add(PrivateNetworkAddParam PrivateNetworkAddParam);

   void edit(PrivateNetworkEditParam PrivateNetworkEditParam);

   PrivateNetwork detail(PrivateNetworkIdParam PrivateNetworkIdParam);

   PrivateNetwork queryEntity(String id);

   void delete(List<PrivateNetworkIdParam> serverDiscountsIdParamList);

   Boolean isBindingPrivateNetWork(String id);
}
