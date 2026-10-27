package com.rxinyun.business.api;

import com.rxinyun.business.core.pojo.BaseMemberAddServer;
import com.rxinyun.business.core.pojo.BaseMemberServer;
import java.math.BigDecimal;
import java.util.List;

public interface MemberServerApi {
   BaseMemberServer selectByServerId(String serverId);

   BaseMemberServer selectByServerIdAndMemberNo(String serverId, Long memberNo);

   String selectMainIp(String serverId, Long memberNo);

   Boolean createServer(BaseMemberAddServer baseMemberAddServer);

   Boolean upgradeHardDisksServer(String orderNo, String serverId, Integer systemDisk, List<Integer> dataDisks, BigDecimal renewPrice);

   Boolean upgradeBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice);

   Boolean upgradeCpuRamServer(String orderNo, String serverId, Integer cpu, Integer ram, BigDecimal renewPrice);

   Boolean renewServer(String orderNo, String serverId, Integer months);

   Boolean upgradeDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice);
}
