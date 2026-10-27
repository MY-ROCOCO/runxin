package com.rxinyun.member.order.modular.independent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.member.order.modular.independent.entity.MemberIndependentOrderServerConfig;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentOrderServerConfigAddParam;
import com.rxinyun.member.order.modular.independent.response.IndependentOrderServerConfigResponse;
import java.util.List;

public interface MemberIndependentOrderServerConfigService extends IService<MemberIndependentOrderServerConfig> {
   void add(MemberIndependentOrderServerConfigAddParam orderServerAddParam);

   List<MemberIndependentOrderServerConfig> queryByList(String orderNo);

   MemberIndependentOrderServerConfig queryByOrderNo(String orderNo);

   IndependentOrderServerConfigResponse queryByOrder(String orderNo);
}
