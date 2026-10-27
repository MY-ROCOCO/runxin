package com.rxinyun.member.order.modular.ecs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrderServerConfig;
import com.rxinyun.member.order.modular.ecs.param.MemberOrderServerConfigAddParam;
import com.rxinyun.member.order.modular.ecs.response.MemberServerConfigResponse;
import java.util.List;

public interface MemberOrderServerConfigService extends IService<MemberOrderServerConfig> {
   void add(MemberOrderServerConfigAddParam orderServerAddParam);

   List<MemberOrderServerConfig> queryByList(String orderNo);

   MemberOrderServerConfig queryByOrderNo(String orderNo);

   MemberServerConfigResponse queryByOrder(String orderNo);
}
