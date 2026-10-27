package com.rxinyun.common.context;

public class ApiContext {
   private static final ThreadLocal<Long> MEMBER_NO = new ThreadLocal<>();
   private static final ThreadLocal<Boolean> RESOURCE_POOL_AUTH = new ThreadLocal<>();

   public static void setMemberNo(Long memberNo) {
      MEMBER_NO.set(memberNo);
   }

   public static Long getMemberNo() {
      return MEMBER_NO.get();
   }

   public static void setResourcePoolAuth(boolean resourcePoolAuth) {
      RESOURCE_POOL_AUTH.set(resourcePoolAuth ? Boolean.TRUE : null);
   }

   public static boolean isResourcePoolAuth() {
      return Boolean.TRUE.equals(RESOURCE_POOL_AUTH.get());
   }

   public static void clear() {
      MEMBER_NO.remove();
      RESOURCE_POOL_AUTH.remove();
   }
}
