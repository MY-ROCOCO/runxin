package com.rxinyun.common.enums.order;

public enum ProductTypeEnum {
   CLOUD_SERVER(1, "CLOUD_SERVER", "云服务器"),
   INDEPENDENT_SERVER(2, "INDEPENDENT_SERVER", "独立服务器"),
   DOMAIN(3, "DOMAIN", "域名"),
   DNS(4, "DNS", "DNS云解析"),
   VHOST(5, "VHOST", "虚拟主机");

   private final Integer code;
   private final String key;
   private final String message;

   private ProductTypeEnum(Integer code, String key, String message) {
      this.code = code;
      this.key = key;
      this.message = message;
   }

   public Integer getCode() {
      return this.code;
   }

   public String getKey() {
      return this.key;
   }

   public String getMessage() {
      return this.message;
   }

   public static String getMessageByCode(Integer code) {
      for (ProductTypeEnum productTypeEnum : values()) {
         if (productTypeEnum.getCode() == code) {
            return productTypeEnum.getMessage();
         }
      }

      return null;
   }
}
