package com.rxinyun.admin.production.modular.vhost.line.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.vhost.line.entity.VhostLine;
import com.rxinyun.admin.production.modular.vhost.line.param.VhostLineAddParam;
import com.rxinyun.admin.production.modular.vhost.line.param.VhostLineEditParam;
import com.rxinyun.admin.production.modular.vhost.line.param.VhostLineIdParam;
import com.rxinyun.admin.production.modular.vhost.line.param.VhostLinePageParam;
import java.util.List;

public interface VhostLineService {
   Page<VhostLine> page(VhostLinePageParam pageParam);

   void add(VhostLineAddParam addParam);

   void edit(VhostLineEditParam editParam);

   VhostLine detail(VhostLineIdParam idParam);

   void delete(List<VhostLineIdParam> idParamList);

   List<VhostLine> selector(String regionId);
}
