package com.rxinyun.admin.production.modular.esc.ip.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.ip.entity.PublicNetwork;
import com.rxinyun.admin.production.modular.esc.ip.param.PublicNetworkAddParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PublicNetworkEditParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PublicNetworkIdParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PublicNetworkPageParam;
import com.rxinyun.common.pojo.CommonResult;
import java.util.List;

public interface PublicNetworkService {
   Page<PublicNetwork> page(PublicNetworkPageParam pageParam);

   CommonResult<String> add(PublicNetworkAddParam serverIpAddParam);

   void edit(PublicNetworkEditParam serverIpEditParam);

   PublicNetwork detail(PublicNetworkIdParam serverIpIdParam);

   PublicNetwork queryEntity(String id);

   void delete(List<PublicNetworkIdParam> serverDiscountsIdParamList);

   Boolean isBindingPublicNetWork(String id);
}
