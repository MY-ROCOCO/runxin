package com.rxinyun.member.production.modular.domain.template.param;

import com.rxinyun.member.production.modular.domain.template.dto.XinwangTemplateCreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public class MemberDomainTemplateAddParam {
   @Schema(
      description = "模板名称",
      requiredMode = RequiredMode.REQUIRED
   )
   private String name;
   @Schema(
      description = "状态：0 禁用 1 启用"
   )
   private Integer status;
   @Schema(
      description = "新网模板信息（创建模板接口字段）"
   )
   private XinwangTemplateCreateDto templateInfo;

   public String getName() {
      return this.name;
   }

   public Integer getStatus() {
      return this.status;
   }

   public XinwangTemplateCreateDto getTemplateInfo() {
      return this.templateInfo;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setTemplateInfo(final XinwangTemplateCreateDto templateInfo) {
      this.templateInfo = templateInfo;
   }
}
