package com.rxinyun.admin.business.modular.esc.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.admin.business.modular.esc.entity.ServerIp;
import com.rxinyun.admin.business.modular.esc.entity.ServerPort;
import java.util.List;

public final class ResourcePoolSnapshotWriter {
   private ResourcePoolSnapshotWriter() {
   }

   public static JSONObject build(Server server, List<ServerIp> ipList, List<ServerPort> portList) {
      String memberNo = server.getMemberNo();
      server.setMemberNo(null);

      JSONObject var6;
      try {
         JSONObject snapshot = (JSONObject)JSONObject.toJSON(server);
         if (ipList != null) {
            snapshot.put("ipList", ipList);
         }

         if (portList != null && !portList.isEmpty()) {
            snapshot.put("portList", portList);
         }

         String upstream = StrUtil.blankToDefault(server.getUpstreamServerId(), server.getId());
         if (StrUtil.isNotBlank(upstream)) {
            snapshot.put("upstreamServerId", upstream);
         }

         var6 = snapshot;
      } finally {
         server.setMemberNo(memberNo);
      }

      return var6;
   }
}
