package com.rxinyun.admin.marketing.modular.marketing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@TableName(
   value = "DISCOUNT_DETAIL",
   autoResultMap = true
)
public class DiscountDetail extends CommonEntity {
   @Schema(
      description = "折扣详情ID"
   )
   private String id;
   @Schema(
      description = "折扣模板ID"
   )
   private String templateId;
   @Schema(
      description = "会员组ID"
   )
   private String groupId;
   @Schema(
      description = "会员组名称"
   )
   private String groupName;
   @Schema(
      description = "折扣值: 百分比(如0.1表示10%)"
   )
   private BigDecimal discountValue;
   @Schema(
      description = "使用次数限制: 0表示无限制"
   )
   private Integer useLimit;

   public String getId() {
      return this.id;
   }

   public String getTemplateId() {
      return this.templateId;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public String getGroupName() {
      return this.groupName;
   }

   public BigDecimal getDiscountValue() {
      return this.discountValue;
   }

   public Integer getUseLimit() {
      return this.useLimit;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setTemplateId(final String templateId) {
      this.templateId = templateId;
   }

   public void setGroupId(final String groupId) {
      this.groupId = groupId;
   }

   public void setGroupName(final String groupName) {
      this.groupName = groupName;
   }

   public void setDiscountValue(final BigDecimal discountValue) {
      this.discountValue = discountValue;
   }

   public void setUseLimit(final Integer useLimit) {
      this.useLimit = useLimit;
   }
}
