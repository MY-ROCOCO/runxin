package com.rxinyun.admin.production.modular.esc.master.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.master.entity.ServerMaster;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterAddParam;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterEditParam;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterIdParam;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterLimitedEditParam;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterPageParam;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterStatusParam;
import com.rxinyun.admin.production.modular.esc.master.vo.MasterStatusVO;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.qzsystem.entity.MasterMonitoringItem;
import java.util.List;

public interface ServerMasterService {
   Page<ServerMaster> page(ServerMasterPageParam pageParam);

   void add(ServerMasterAddParam serverMasterAddParam);

   void edit(ServerMasterEditParam serverMasterEditParam);

   void edit(ServerMasterLimitedEditParam serverMasterLimitedEditParam);

   ServerMaster detail(ServerMasterIdParam serverMasterIdParam);

   ServerMaster queryEntity(String id);

   void delete(List<ServerMasterIdParam> serverMasterIdParamList);

   CommonResult<MasterStatusVO> selectByStatus(ServerMasterStatusParam serverMasterStatusParam);

   CommonResult selectByCompanyInfo(ServerMasterStatusParam serverMasterStatusParam);

   CommonResult<List<MasterMonitoringItem>> selectByNetworkMonitoring(ServerMasterStatusParam serverMasterStatusParam);

   Boolean isBindingMaster(String id);
}
