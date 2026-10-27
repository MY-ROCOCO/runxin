package com.rxinyun.business.api;

import com.rxinyun.business.core.pojo.BaseMemberVhost;
import com.rxinyun.business.core.pojo.BaseMemberVhostCreate;
import com.rxinyun.business.core.pojo.BaseMemberVhostProvision;
import java.util.List;

public interface MemberVhostApi {
   Boolean create(BaseMemberVhostCreate param);

   BaseMemberVhost selectById(String hostId);

   BaseMemberVhost selectByIdAndMemberNo(String hostId, String memberNo);

   List<BaseMemberVhost> listByOrderNo(String orderNo);

   Boolean renew(String hostId, String memberNo, Integer months, String orderNo, String platform);

   Boolean upgradeTraffic(String hostId, String memberNo, Integer upgradeTraffic, String orderNo);

   Boolean provision(BaseMemberVhostProvision param);

   List<BaseMemberVhost> listExpiredToStop();

   void expireStop(String hostId);

   List<BaseMemberVhost> listExpiredToDelete();

   void expireDelete(String hostId);

   void refundDelete(String hostId);
}
