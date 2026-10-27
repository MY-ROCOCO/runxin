package com.rxinyun.admin.production.modular.vhost.master.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.vhost.master.entity.VhostMaster;
import com.rxinyun.admin.production.modular.vhost.master.param.VhostControlledVersionResult;
import com.rxinyun.admin.production.modular.vhost.master.param.VhostMasterAddParam;
import com.rxinyun.admin.production.modular.vhost.master.param.VhostMasterEditParam;
import com.rxinyun.admin.production.modular.vhost.master.param.VhostMasterIdParam;
import com.rxinyun.admin.production.modular.vhost.master.param.VhostMasterPageParam;
import com.rxinyun.common.vhost.result.VhostNetWork;
import com.rxinyun.common.vhost.result.VhostSystemTotal;
import java.util.List;

public interface VhostMasterService {
   Page<VhostMaster> page(VhostMasterPageParam pageParam);

   void add(VhostMasterAddParam addParam);

   void edit(VhostMasterEditParam editParam);

   VhostMaster detail(VhostMasterIdParam idParam);

   void delete(List<VhostMasterIdParam> idParamList);

   List<VhostControlledVersionResult> controlledVersions();

   VhostSystemTotal systemTotal(VhostMasterIdParam idParam);

   VhostNetWork netWork(VhostMasterIdParam idParam);
}
