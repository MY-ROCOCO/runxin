package com.rxinyun.common.util;

import cn.hutool.core.util.StrUtil;
import com.rxinyun.common.exception.CommonException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class RenewServerIdSupport {
   public static final int MAX_BATCH_SIZE = 50;

   private RenewServerIdSupport() {
   }

   public static List<String> resolve(String serverId, List<String> serverIds) {
      LinkedHashSet<String> ids = new LinkedHashSet<>();
      addIfPresent(ids, serverId);
      if (serverIds != null) {
         for (String id : serverIds) {
            addIfPresent(ids, id);
         }
      }

      if (ids.isEmpty()) {
         throw new CommonException("续费的服务器ID不能为空");
      } else if (ids.size() > 50) {
         throw new CommonException("单次最多续费 50 台服务器");
      } else {
         return new ArrayList<>(ids);
      }
   }

   public static String requireUniqueCouponLineId(Set<String> lineIds, String couponCode) {
      if (StrUtil.isBlank(couponCode)) {
         return firstLineId(lineIds);
      } else if (lineIds != null && !lineIds.isEmpty()) {
         if (lineIds.size() > 1) {
            throw new CommonException("批量续费使用优惠券时须为同一线路");
         } else {
            return lineIds.iterator().next();
         }
      } else {
         return null;
      }
   }

   private static String firstLineId(Set<String> lineIds) {
      return lineIds != null && !lineIds.isEmpty() ? lineIds.iterator().next() : null;
   }

   private static void addIfPresent(Set<String> ids, String id) {
      if (StrUtil.isNotBlank(id)) {
         ids.add(id.trim());
      }
   }
}
