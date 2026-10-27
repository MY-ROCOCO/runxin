package com.rxinyun.member.business.modular.vhost.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.business.core.pojo.BaseMemberVhost;
import com.rxinyun.business.core.pojo.BaseMemberVhostCreate;
import com.rxinyun.business.core.pojo.BaseMemberVhostProvision;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.vhost.result.VhostPhpVersion;
import com.rxinyun.member.business.modular.vhost.entity.MemberVhost;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostDomainParam;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostIdParam;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostPageParam;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostPasswordParam;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostPhpParam;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostRemarkParam;
import java.util.List;

public interface MemberVhostService extends IService<MemberVhost> {
   Page<MemberVhost> page(MemberVhostPageParam pageParam);

   MemberVhost detail(MemberVhostIdParam idParam);

   void create(BaseMemberVhostCreate param);

   BaseMemberVhost selectById(String hostId);

   BaseMemberVhost selectByIdAndMemberNo(String hostId, String memberNo);

   List<BaseMemberVhost> listByOrderNo(String orderNo);

   void renew(String hostId, String memberNo, Integer months, String orderNo, String platform);

   void upgradeTraffic(String hostId, String memberNo, Integer upgradeTraffic, String orderNo);

   void provision(BaseMemberVhostProvision param);

   CommonResult<String> start(MemberVhostIdParam idParam);

   CommonResult<String> stop(MemberVhostIdParam idParam);

   List<VhostPhpVersion> phpVersions(MemberVhostIdParam idParam);

   CommonResult<String> changePhp(MemberVhostPhpParam param);

   CommonResult<String> addDomain(MemberVhostDomainParam param);

   CommonResult<String> deleteDomain(MemberVhostDomainParam param);

   CommonResult<String> changeFtpPassword(MemberVhostPasswordParam param);

   CommonResult<String> changeDbPassword(MemberVhostPasswordParam param);

   void editRemark(MemberVhostRemarkParam param);

   List<BaseMemberVhost> listExpiredToStop();

   void expireStop(String hostId);

   List<BaseMemberVhost> listExpiredToDelete();

   void expireDelete(String hostId);

   void refundDelete(String hostId);
}
