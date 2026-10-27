package com.rxinyun.business.api;

import com.rxinyun.business.core.pojo.BaseMemberIndependentAddServer;
import com.rxinyun.business.core.pojo.BaseMemberIndependentServer;
import java.math.BigDecimal;

public interface MemberIndependentApi {
   BaseMemberIndependentServer selectByServerId(String serverId);

   BaseMemberIndependentServer selectByServerIdAndMemberNo(String serverId, Long memberNo);

   String selectMainIp(String serverId, Long memberNo);

   Boolean createServer(BaseMemberIndependentAddServer baseMemberIndependentAddServer);

   Boolean savePoolPendingServer(BaseMemberIndependentAddServer baseMemberIndependentAddServer);

   Boolean upgradeIndependentBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice);

   Boolean upgradeIndependentIpServer(String orderNo, String serverId, Integer ipSize, BigDecimal renewPrice);

   Boolean renewServer(String orderNo, String serverId, Integer months, String unit);

   Boolean upgradeIndependentDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice);
}
