package com.rxinyun.business.core.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.business.core.pojo.BaseIndependentIp;
import java.util.List;

public final class IndependentResourcePoolSnapshotUtil {
   private IndependentResourcePoolSnapshotUtil() {
   }

   public static JSONObject build(BaseIndependent independent, List<BaseIndependentIp> ipList) {
      if (independent == null) {
         return new JSONObject();
      } else {
         Long memberNo = independent.getMemberNo();
         independent.setMemberNo(null);

         JSONObject var12;
         try {
            JSONObject snapshot = (JSONObject)JSON.toJSON(independent);
            snapshot.remove("systemPassword");
            if (ipList != null && !ipList.isEmpty()) {
               snapshot.put("ipList", ipList);

               for (BaseIndependentIp row : ipList) {
                  if (row != null) {
                     String t = row.getType() != null ? row.getType().trim().toUpperCase() : "";
                     String ipVal = StrUtil.trimToNull(row.getIp());
                     if (ipVal != null) {
                        if (("PUBLIC".equals(t) || "PUBLIC_IP".equals(t) || "WAN".equals(t)) && StrUtil.isBlank(snapshot.getString("publicIp"))) {
                           snapshot.put("publicIp", ipVal);
                        }

                        if (("PRIVATE".equals(t) || "PRIVATE_IP".equals(t) || "LAN".equals(t)) && StrUtil.isBlank(snapshot.getString("privateIp"))) {
                           snapshot.put("privateIp", ipVal);
                        }
                     }
                  }
               }
            }

            String upstream = StrUtil.blankToDefault(independent.getUpstreamServerId(), independent.getId());
            if (StrUtil.isNotBlank(upstream)) {
               snapshot.put("upstreamServerId", upstream);
            }

            var12 = snapshot;
         } finally {
            independent.setMemberNo(memberNo);
         }

         return var12;
      }
   }
}
