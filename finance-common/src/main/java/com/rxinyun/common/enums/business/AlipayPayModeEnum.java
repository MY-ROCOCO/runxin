package com.rxinyun.common.enums.business;

public enum AlipayPayModeEnum {
   PC("PC", "电脑网站支付"),
   H5("H5", "手机网站支付"),
   FACE_QR("FACE_QR", "当面付扫码支付");

   private final String code;
   private final String description;

   private AlipayPayModeEnum(String code, String description) {
      this.code = code;
      this.description = description;
   }

   public String getCode() {
      return this.code;
   }

   public String getDescription() {
      return this.description;
   }

   public static AlipayPayModeEnum fromCode(String code) {
      for (AlipayPayModeEnum type : values()) {
         if (type.getCode().equals(code)) {
            return type;
         }
      }

      throw new IllegalArgumentException("Invalid alipay pay mode code: " + code);
   }
}
