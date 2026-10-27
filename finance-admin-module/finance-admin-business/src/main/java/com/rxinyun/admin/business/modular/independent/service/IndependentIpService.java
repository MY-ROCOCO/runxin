package com.rxinyun.admin.business.modular.independent.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.independent.entity.IndependentIp;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpAddParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpDeleteParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpEditParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpIdParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpPageParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpQueryParam;
import java.util.List;

public interface IndependentIpService {
   Page<IndependentIp> page(IndependentIpPageParam pageParam);

   void add(IndependentIpAddParam serverIpAddParam);

   void edit(IndependentIpEditParam serverIpEditParam);

   IndependentIp detail(IndependentIpIdParam serverIpIdParam);

   IndependentIp queryEntity(String id);

   List<IndependentIp> queryByServerId(IndependentIpQueryParam queryParam);

   List<IndependentIp> listAllByServerId(String serverId);

   List<String> listServerIdByIp(String ip);

   void delete(List<IndependentIpDeleteParam> independentIpDeleteParamList);

   void setMainIp(IndependentIpIdParam independentIpIdParam);

   void replaceFromResourcePoolUpstreamSnapshot(String serverId, JSONObject dataSnapshot);
}
