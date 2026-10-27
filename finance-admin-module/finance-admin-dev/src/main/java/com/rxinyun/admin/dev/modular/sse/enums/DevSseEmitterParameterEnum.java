package com.rxinyun.admin.dev.modular.sse.enums;

public enum DevSseEmitterParameterEnum {
   EMITTER("EMITTER"),
   FUTURE("FUTURE"),
   LOGINID("LOGINID");

   private final String value;

   private DevSseEmitterParameterEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
