package com.rxinyun.admin.business.modular.esc.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.esc.entity.ServerIp;
import com.rxinyun.admin.business.modular.esc.param.ServerIpAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpEditParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpPageParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpReplaceParam;
import com.rxinyun.common.pojo.CommonResult;
import java.util.List;
import java.util.Map;

public interface ServerIpService {
   Page<ServerIp> page(ServerIpPageParam pageParam);

   void add(ServerIpAddParam serverIpAddParam);

   void edit(ServerIpEditParam serverIpEditParam);

   ServerIp detail(ServerIpIdParam serverIpIdParam);

   ServerIp queryEntity(String id);

   void deleteByServerId(String serverId);

   void deleteByServerIdWithServerSnapshotFallback(String serverId);

   void delete(List<ServerIpIdParam> serverIpIdParamList);

   List<ServerIp> queryByServerId(String serverId);

   List<String> listServerIdByIp(String ip);

   Map<String, Integer> countPublicIpByServerIds(List<String> serverIds);

   CommonResult addBatch(ServerIpParam serverIdParam);

   CommonResult replacePublicIp(ServerIpReplaceParam serverIpReplaceParam);

   CommonResult resetCurrentIp(String serverId);

   void setMainIp(ServerIpIdParam serverIpIdParam);

   void replaceFromResourcePoolUpstreamSnapshot(String serverId, JSONObject dataSnapshot);
}
