package com.rxinyun.admin.production.modular.esc.ip.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.rxinyun.admin.production.modular.esc.ip.service.IpMasterRelationService;
import com.rxinyun.admin.production.modular.esc.ip.service.NatNetworkService;
import com.rxinyun.admin.production.modular.esc.ip.service.PrivateNetworkService;
import com.rxinyun.admin.production.modular.esc.ip.service.PublicNetworkService;
import com.rxinyun.common.exception.CommonException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class IpMasterRelationServiceImpl implements IpMasterRelationService {
   @Resource
   private PublicNetworkService publicNetworkService;
   @Resource
   private PrivateNetworkService privateNetworkService;
   @Resource
   private NatNetworkService natNetworkService;

   @Override
   public Boolean isBindingIp(String id) {
      if (ObjectUtil.isEmpty(id)) {
         throw new CommonException("宿主机id为空", new Object[0]);
      } else {
         return this.publicNetworkService.isBindingPublicNetWork(id)
            || this.natNetworkService.isBindingNatNetWork(id)
            || this.privateNetworkService.isBindingPrivateNetWork(id);
      }
   }
}
