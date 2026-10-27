package com.rxinyun.admin.production.modular.esc.ip.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.ip.entity.NatNetwork;
import com.rxinyun.admin.production.modular.esc.ip.param.NatNetworkAddParam;
import com.rxinyun.admin.production.modular.esc.ip.param.NatNetworkEditParam;
import com.rxinyun.admin.production.modular.esc.ip.param.NatNetworkIdParam;
import com.rxinyun.admin.production.modular.esc.ip.param.NatNetworkPageParam;
import com.rxinyun.common.pojo.CommonResult;
import java.util.List;

public interface NatNetworkService {
   Page<NatNetwork> page(NatNetworkPageParam pageParam);

   CommonResult<String> add(NatNetworkAddParam natNetworkAddParam);

   void edit(NatNetworkEditParam natNetworkEditParam);

   NatNetwork detail(NatNetworkIdParam natNetworkIdParam);

   NatNetwork queryEntity(String id);

   void delete(List<NatNetworkIdParam> natNetworkIdParamList);

   Boolean isBindingNatNetWork(String id);
}
