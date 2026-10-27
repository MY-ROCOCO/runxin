package com.rxinyun.member.payments.modular.payments.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class OrderParam implements Serializable {
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "CPU 核心数"
   )
   @Min(
      value = 1L,
      message = "购买数量必须大于0"
   )
   private Integer cpu;
   @Schema(
      description = "内存大小（单位：GB）"
   )
   @Min(
      value = 1L,
      message = "购买数量必须大于0"
   )
   private Integer ram;
   @Schema(
      description = "SSD数据盘"
   )
   @NotNull(
      message = "硬盘列表不能为空"
   )
   private List<Integer> harddisks;
   @Schema(
      description = "带宽大小（单位：Mbps）"
   )
   @Min(
      value = 1L,
      message = "购买数量必须大于0"
   )
   private Integer bandwidth;
   @Schema(
      description = "防御等级"
   )
   @Min(
      value = 1L,
      message = "购买数量必须大于0"
   )
   private Integer defense;
   @Schema(
      description = "系统分类ID"
   )
   private String systemClassId;
   @Schema(
      description = "操作系统ID"
   )
   private String systemId;
   @Schema(
      description = "登录密码"
   )
   private String loginPassword;
   @Schema(
      description = "确认密码"
   )
   private String loginPassword2;
   @Schema(
      description = "购买时长"
   )
   @Min(
      value = 1L,
      message = "购买时长必须大于0"
   )
   private Integer months;
   @Schema(
      description = "购买数量"
   )
   @Min(
      value = 1L,
      message = "购买数量必须大于0"
   )
   private Integer number;
   @Schema(
      description = "增值服务"
   )
   private Integer valueAddedGroupId;

   public String getLineId() {
      return this.lineId;
   }

   public Integer getCpu() {
      return this.cpu;
   }

   public Integer getRam() {
      return this.ram;
   }

   public List<Integer> getHarddisks() {
      return this.harddisks;
   }

   public Integer getBandwidth() {
      return this.bandwidth;
   }

   public Integer getDefense() {
      return this.defense;
   }

   public String getSystemClassId() {
      return this.systemClassId;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public String getLoginPassword() {
      return this.loginPassword;
   }

   public String getLoginPassword2() {
      return this.loginPassword2;
   }

   public Integer getMonths() {
      return this.months;
   }

   public Integer getNumber() {
      return this.number;
   }

   public Integer getValueAddedGroupId() {
      return this.valueAddedGroupId;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setCpu(final Integer cpu) {
      this.cpu = cpu;
   }

   public void setRam(final Integer ram) {
      this.ram = ram;
   }

   public void setHarddisks(final List<Integer> harddisks) {
      this.harddisks = harddisks;
   }

   public void setBandwidth(final Integer bandwidth) {
      this.bandwidth = bandwidth;
   }

   public void setDefense(final Integer defense) {
      this.defense = defense;
   }

   public void setSystemClassId(final String systemClassId) {
      this.systemClassId = systemClassId;
   }

   public void setSystemId(final String systemId) {
      this.systemId = systemId;
   }

   public void setLoginPassword(final String loginPassword) {
      this.loginPassword = loginPassword;
   }

   public void setLoginPassword2(final String loginPassword2) {
      this.loginPassword2 = loginPassword2;
   }

   public void setMonths(final Integer months) {
      this.months = months;
   }

   public void setNumber(final Integer number) {
      this.number = number;
   }

   public void setValueAddedGroupId(final Integer valueAddedGroupId) {
      this.valueAddedGroupId = valueAddedGroupId;
   }
}
