package com.rxinyun.common.enums.business;

public enum DomainTemplateAuditStatusEnum {
   NOT_SUBMITTED(0, "未提交"),
   AUDITING(1, "审核中"),
   APPROVED(2, "审核通过"),
   REJECTED(3, "审核拒绝");

   private final Integer code;
   private final String description;

   private DomainTemplateAuditStatusEnum(Integer code, String description) {
      this.code = code;
      this.description = description;
   }

   public Integer getCode() {
      return this.code;
   }

   public String getDescription() {
      return this.description;
   }

   public static DomainTemplateAuditStatusEnum getByCode(Integer code) {
      if (code == null) {
         return null;
      } else {
         for (DomainTemplateAuditStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
               return e;
            }
         }

         return null;
      }
   }
}
