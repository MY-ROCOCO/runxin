package com.rxinyun.member.business.modular.independent.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordIdParam;
import com.rxinyun.member.business.modular.independent.entity.MemberIndependent;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentAddParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentIdListParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentIdParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentPageParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentPasswordEditParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentRenewAutoParam;
import com.rxinyun.member.business.modular.independent.param.MemberRemarkEditParam;
import com.rxinyun.member.business.modular.independent.param.ReinstallEditParam;
import com.rxinyun.member.business.modular.independent.response.MemberIndependentResponse;
import java.math.BigDecimal;
import java.util.List;

public interface MemberIndependentService extends IService<MemberIndependent> {
   Page<MemberIndependentResponse> page(MemberIndependentPageParam pageParam);

   List<MemberIndependent> list(MemberIndependentIdListParam memberIndependentIdListParam);

   void add(MemberIndependentAddParam serverAddParam);

   void savePoolPendingServer(MemberIndependentAddParam serverAddParam);

   MemberIndependentResponse detail(MemberIndependentIdParam serverIdParam);

   MemberIndependent queryEntity(String id);

   void editRemark(MemberRemarkEditParam memberRemarkEditParam);

   void isRenew(MemberIndependentRenewAutoParam param);

   CommonResult updatePasswordServer(MemberIndependentPasswordEditParam passwordEditParam);

   CommonResult upgradeBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice);

   CommonResult upgradeIpServer(String orderNo, String serverId, Integer ipSize, BigDecimal renewPrice);

   CommonResult renewServer(String orderNo, String serverId, Integer months, String unit);

   CommonResult upgradeDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice);

   CommonResult startServer(MemberIndependentIdParam memberServerIdParam);

   CommonResult closeServer(MemberIndependentIdParam memberServerIdParam);

   CommonResult reinstallServer(ReinstallEditParam reinstallEditParam);

   CommonResult powerServer(MemberIndependentIdParam serverIdParam);

   CommonResult plus(MemberServerTransferRecordAddParam memberServerTransferRecordAddParam);

   CommonResult updateStatus(MemberServerTransferRecordIdParam memberServerTransferRecordEditParam);

   CommonResult restartServer(MemberIndependentIdParam serverIdParam);
}
