package com.rxinyun.admin.production.modular.vhost.version.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.vhost.version.entity.VhostVersion;
import com.rxinyun.admin.production.modular.vhost.version.param.VhostVersionAddParam;
import com.rxinyun.admin.production.modular.vhost.version.param.VhostVersionEditParam;
import com.rxinyun.admin.production.modular.vhost.version.param.VhostVersionIdParam;
import com.rxinyun.admin.production.modular.vhost.version.param.VhostVersionPageParam;
import java.util.List;

public interface VhostVersionService {
   Page<VhostVersion> page(VhostVersionPageParam pageParam);

   void add(VhostVersionAddParam addParam);

   void edit(VhostVersionEditParam editParam);

   VhostVersion detail(VhostVersionIdParam idParam);

   void delete(List<VhostVersionIdParam> idParamList);
}
