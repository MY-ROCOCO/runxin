package com.rxinyun.member.order.modular.ecs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.domain.param.DomainOrderCreateParam;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import com.rxinyun.member.order.modular.ecs.param.MemberBandwidthUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberCoreUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberDefenseUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberDiskUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberOrderIdParam;
import com.rxinyun.member.order.modular.ecs.param.MemberOrderPageParam;
import com.rxinyun.member.order.modular.ecs.param.MemberOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberPayParam;
import com.rxinyun.member.order.modular.ecs.param.MemberRenewOrderParam;
import com.rxinyun.member.order.modular.ecs.response.MemberOrderResponse;
import java.util.Date;

public interface MemberOrderService extends IService<MemberOrder> {
   Page<MemberOrder> page(MemberOrderPageParam pageParam);

   CommonResult<MemberOrderResponse> detail(MemberOrderIdParam orderIdParam);

   CommonResult<MemberOrder> create(MemberOrderParam orderParam);

   CommonResult pay(MemberPayParam orderParam);

   void paySuccess(String orderNo, Date payTime, String alipayPayNo, Integer payType);

   void markOrderPaid(String orderNo, Date payTime, String alipayPayNo, Integer payType);

   void processPaidOrder(String orderNo);

   CommonResult<MemberOrder> upgradeCore(MemberCoreUpgradeOrderParam upgradeOrderParam);

   CommonResult<MemberOrder> upgradeDisk(MemberDiskUpgradeOrderParam upgradeOrderParamm);

   CommonResult<MemberOrder> upgradeBandwidth(MemberBandwidthUpgradeOrderParam upgradeOrderParam);

   CommonResult<MemberOrder> upgradeDefense(MemberDefenseUpgradeOrderParam upgradeOrderParam);

   CommonResult<MemberOrder> renew(MemberRenewOrderParam renewOrderParam);

   CommonResult<MemberOrder> createDomainOrder(DomainOrderCreateParam param);
}
