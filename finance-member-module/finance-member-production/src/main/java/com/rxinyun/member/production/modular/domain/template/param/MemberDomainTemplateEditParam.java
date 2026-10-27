package com.rxinyun.member.production.modular.domain.template.param;

import com.rxinyun.member.production.modular.domain.template.dto.XinwangTemplateCreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public class MemberDomainTemplateEditParam {
   @Schema(
      description = "模板ID",
      requiredMode = RequiredMode.REQUIRED
   )
   private String id;
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
      description = "新网模板信息"
   )
   private XinwangTemplateCreateDto templateInfo;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Integer getStatus() {
      return this.status;
   }

   public XinwangTemplateCreateDto getTemplateInfo() {
      return this.templateInfo;
   }

   public void setId(final String id) {
      this.id = id;
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
