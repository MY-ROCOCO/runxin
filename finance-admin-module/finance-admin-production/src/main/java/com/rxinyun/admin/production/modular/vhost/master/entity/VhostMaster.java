package com.rxinyun.admin.production.modular.vhost.master.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.admin.production.modular.vhost.line.entity.VhostLine;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@TableName(
   value = "VHOST_MASTER",
   autoResultMap = true
)
public class VhostMaster extends CommonEntity {
   @Schema(
      description = "主控ID"
   )
   private String id;
   @Schema(
      description = "主控名称"
   )
   private String name;
   @Schema(
      description = "受控版本：BAOTA 宝塔虚拟主机"
   )
   private String controlledVersion;
   @Schema(
      description = "受控版本名称"
   )
   @TableField(
      exist = false
   )
   private String controlledVersionName;
   @Schema(
      description = "面板地址"
   )
   private String panelUrl;
   @Schema(
      description = "API密钥"
   )
   private String apiKey;
   @Schema(
      description = "分配权重，同一线路多个主控时权重越大分配占比越高"
   )
   private Integer weight;
   @Schema(
      description = "FTP连接地址，购买后展示给用户"
   )
   private String ftpAddress;
   @Schema(
      description = "状态：1启用 0禁用"
   )
   private Integer status;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "绑定线路ID列表"
   )
   @TableField(
      exist = false
   )
   private List<String> lineIds;
   @Schema(
      description = "绑定线路，含线路ID和线路名称"
   )
   @TableField(
      exist = false
   )
   private List<VhostLine.Bound> lines;
   @Schema(
      description = "绑定线路名称，逗号分隔"
   )
   @TableField(
      exist = false
   )
   private String lineNames;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getControlledVersion() {
      return this.controlledVersion;
   }

   public String getControlledVersionName() {
      return this.controlledVersionName;
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

   public List<VhostLine.Bound> getLines() {
      return this.lines;
   }

   public String getLineNames() {
      return this.lineNames;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setControlledVersion(final String controlledVersion) {
      this.controlledVersion = controlledVersion;
   }

   public void setControlledVersionName(final String controlledVersionName) {
      this.controlledVersionName = controlledVersionName;
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

   public void setLines(final List<VhostLine.Bound> lines) {
      this.lines = lines;
   }

   public void setLineNames(final String lineNames) {
      this.lineNames = lineNames;
   }
}
