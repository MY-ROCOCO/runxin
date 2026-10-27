package com.rxinyun.admin.dev.modular.slideshow.enums;

public enum DevSlideshowEnum {
   TEST("TEST");

   private final String value;

   private DevSlideshowEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
