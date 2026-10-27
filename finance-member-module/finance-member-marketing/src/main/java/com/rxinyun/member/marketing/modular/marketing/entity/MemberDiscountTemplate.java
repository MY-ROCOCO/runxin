package com.rxinyun.member.marketing.modular.marketing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName(
   value = "DISCOUNT_TEMPLATE",
   autoResultMap = true
)
public class MemberDiscountTemplate extends CommonEntity {
   @Schema(
      description = "模板ID"
   )
   private String id;
   @Schema(
      description = "模板名称"
   )
   private String templateName;
   @Schema(
      description = "模板描述"
   )
   private String templateDescription;
   @Schema(
      description = "状态: 0-禁用 1-启用"
   )
   private Integer status;
   @Schema(
      description = "使用次数限制: 0表示无限制"
   )
   private Integer useLimit;
   @Schema(
      description = "开始时间"
   )
   private Date startTime;
   @Schema(
      description = "结束时间"
   )
   private Date endTime;

   public String getId() {
      return this.id;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public String getTemplateDescription() {
      return this.templateDescription;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Integer getUseLimit() {
      return this.useLimit;
   }

   public Date getStartTime() {
      return this.startTime;
   }

   public Date getEndTime() {
      return this.endTime;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setTemplateName(final String templateName) {
      this.templateName = templateName;
   }

   public void setTemplateDescription(final String templateDescription) {
      this.templateDescription = templateDescription;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setUseLimit(final Integer useLimit) {
      this.useLimit = useLimit;
   }

   public void setStartTime(final Date startTime) {
      this.startTime = startTime;
   }

   public void setEndTime(final Date endTime) {
      this.endTime = endTime;
   }
}
