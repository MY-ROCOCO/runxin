package com.rxinyun.member.tools.modular.config.enums;

import com.rxinyun.common.exception.CommonException;

public enum MemberConfigCategoryEnum {
   SYS_BASE("SYS_BASE"),
   BIZ_DEFINE("BIZ_DEFINE"),
   THIRD_GITEE("THIRD_GITEE"),
   THIRD_WECHAT("THIRD_WECHAT"),
   FILE_LOCAL("FILE_LOCAL"),
   FILE_TENCENT("FILE_TENCENT"),
   FILE_ALIYUN("FILE_ALIYUN"),
   FILE_MINIO("FILE_MINIO"),
   EMAIL_LOCAL("EMAIL_LOCAL"),
   EMAIL_TENCENT("EMAIL_TENCENT"),
   EMAIL_ALIYUN("EMAIL_ALIYUN"),
   SMS_TENCENT("SMS_TENCENT"),
   SMS_ALIYUN("SMS_ALIYUN"),
   SMS_XIAONUO("SMS_XIAONUO"),
   MONITOR_AI("MONITOR_AI");

   private final String value;

   private MemberConfigCategoryEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = SYS_BASE.getValue().equals(value)
         || BIZ_DEFINE.getValue().equals(value)
         || THIRD_GITEE.getValue().equals(value)
         || THIRD_WECHAT.getValue().equals(value)
         || FILE_LOCAL.getValue().equals(value)
         || FILE_TENCENT.getValue().equals(value)
         || FILE_ALIYUN.getValue().equals(value)
         || FILE_MINIO.getValue().equals(value)
         || EMAIL_TENCENT.getValue().equals(value)
         || EMAIL_ALIYUN.getValue().equals(value)
         || SMS_TENCENT.getValue().equals(value)
         || SMS_ALIYUN.getValue().equals(value)
         || SMS_XIAONUO.getValue().equals(value)
         || MONITOR_AI.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的配置分类：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
