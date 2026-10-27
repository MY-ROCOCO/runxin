package com.rxinyun.admin.notice.modular.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TemplateDTO {
   @Schema(
      description = "模板id"
   )
   private String id;
   @NotBlank(
      message = "模版标识不能为空"
   )
   @Schema(
      description = "模板标识"
   )
   private String mark;
   @NotBlank(
      message = "模版名称不能为空"
   )
   @Schema(
      description = "模板名称"
   )
   private String name;
   @NotNull(
      message = "身份象征，1为会员，2为管理员"
   )
   @Schema(
      description = "身份象征，1为会员，2为管理员"
   )
   private Integer symbol;
   @NotNull(
      message = "是否含有邮件模板，1是0否"
   )
   @Schema(
      description = "是否含有邮件模板，1是0否"
   )
   private Integer havingEmail;
   @NotNull(
      message = "是否含有短信模板，1是0否"
   )
   @Schema(
      description = "是否含有短信模板，1是0否"
   )
   private Integer havingSms;

   public String getId() {
      return this.id;
   }

   public String getMark() {
      return this.mark;
   }

   public String getName() {
      return this.name;
   }

   public Integer getSymbol() {
      return this.symbol;
   }

   public Integer getHavingEmail() {
      return this.havingEmail;
   }

   public Integer getHavingSms() {
      return this.havingSms;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setMark(final String mark) {
      this.mark = mark;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setSymbol(final Integer symbol) {
      this.symbol = symbol;
   }

   public void setHavingEmail(final Integer havingEmail) {
      this.havingEmail = havingEmail;
   }

   public void setHavingSms(final Integer havingSms) {
      this.havingSms = havingSms;
   }
}
