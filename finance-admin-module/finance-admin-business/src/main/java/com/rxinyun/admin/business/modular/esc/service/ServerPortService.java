package com.rxinyun.admin.business.modular.esc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.esc.entity.ServerPort;
import com.rxinyun.admin.business.modular.esc.param.ServerPortAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerPortEditParam;
import com.rxinyun.admin.business.modular.esc.param.ServerPortIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerPortPageParam;
import java.util.List;

public interface ServerPortService {
   Page<ServerPort> page(ServerPortPageParam pageParam);

   ServerPort add(ServerPortAddParam ServerPortAddParam);

   void edit(ServerPortEditParam ServerPortEditParam);

   ServerPort detail(ServerPortIdParam ServerPortIdParam);

   ServerPort queryEntity(String id);

   void delete(List<ServerPortIdParam> ServerPortIdParam);

   void deleteVps(String serverId);

   List<ServerPort> queryByServerId(String serverId);
}
