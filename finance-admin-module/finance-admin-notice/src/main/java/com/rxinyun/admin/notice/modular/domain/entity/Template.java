package com.rxinyun.admin.notice.modular.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "template",
   autoResultMap = true
)
public class Template {
   @Schema(
      description = "模板id"
   )
   private String id;
   @Schema(
      description = "模板标识"
   )
   private String mark;
   @Schema(
      description = "模板名称"
   )
   private String name;
   @Schema(
      description = "身份象征，1为会员，2为管理员"
   )
   private Integer symbol;
   @Schema(
      description = "是否含有邮件模板，1是0否"
   )
   private Integer havingEmail;
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
