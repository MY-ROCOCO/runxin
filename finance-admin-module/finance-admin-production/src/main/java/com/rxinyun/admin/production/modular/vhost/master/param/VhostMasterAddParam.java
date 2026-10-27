package com.rxinyun.admin.production.modular.vhost.master.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class VhostMasterAddParam {
   @Schema(
      description = "主控名称"
   )
   @NotBlank(
      message = "主控名称不能为空"
   )
   private String name;
   @Schema(
      description = "受控版本，当前仅 BAOTA"
   )
   @NotBlank(
      message = "受控版本不能为空"
   )
   private String controlledVersion;
   @Schema(
      description = "面板地址"
   )
   @NotBlank(
      message = "面板地址不能为空"
   )
   private String panelUrl;
   @Schema(
      description = "API密钥"
   )
   @NotBlank(
      message = "API密钥不能为空"
   )
   private String apiKey;
   @Schema(
      description = "分配权重，同一线路多个主控时权重越大分配占比越高，默认1"
   )
   @Min(
      value = 0L,
      message = "分配权重不能小于0"
   )
   private Integer weight;
   @Schema(
      description = "FTP连接地址，购买后展示给用户"
   )
   private String ftpAddress;
   @Schema(
      description = "状态：1启用 0禁用，默认1"
   )
   private Integer status;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "绑定线路ID列表"
   )
   private List<String> lineIds;

   public String getName() {
      return this.name;
   }

   public String getControlledVersion() {
      return this.controlledVersion;
   }

   public String getPanelUrl() {
      return this.panelUrl;
   }

   public String getApiKey() {
      return this.apiKey;
   }

   public Integer getWeight() {
      return this.weight;
   }

   public String getFtpAddress() {
      return this.ftpAddress;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getRemark() {
      return this.remark;
   }

   public List<String> getLineIds() {
      return this.lineIds;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setControlledVersion(final String controlledVersion) {
      this.controlledVersion = controlledVersion;
   }

   public void setPanelUrl(final String panelUrl) {
      this.panelUrl = panelUrl;
   }

   public void setApiKey(final String apiKey) {
      this.apiKey = apiKey;
   }

   public void setWeight(final Integer weight) {
      this.weight = weight;
   }

   public void setFtpAddress(final String ftpAddress) {
      this.ftpAddress = ftpAddress;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setLineIds(final List<String> lineIds) {
      this.lineIds = lineIds;
   }
}
