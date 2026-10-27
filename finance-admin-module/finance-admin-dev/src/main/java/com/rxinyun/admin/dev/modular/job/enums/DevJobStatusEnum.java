package com.rxinyun.admin.dev.modular.job.enums;

import com.rxinyun.common.exception.CommonException;

public enum DevJobStatusEnum {
   RUNNING("RUNNING"),
   STOPPED("STOPPED");

   private final String value;

   private DevJobStatusEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = RUNNING.getValue().equals(value) || STOPPED.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的定时任务状态：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
