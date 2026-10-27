package com.rxinyun.member.order.modular.ecs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrderServer;
import com.rxinyun.member.order.modular.ecs.param.MemberOrderServerAddParam;
import java.util.List;
import java.util.Map;

public interface MemberOrderServerService extends IService<MemberOrderServer> {
   void add(MemberOrderServerAddParam orderServerAddParam);

   List<MemberOrderServer> queryByList(String orderNo);

   MemberOrderServer queryByOrderNo(String orderNo);

   Map<String, String> queryServerIdByOrderNo(String orderNo);
}
