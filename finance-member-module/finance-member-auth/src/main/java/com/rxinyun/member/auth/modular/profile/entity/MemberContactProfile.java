package com.rxinyun.member.auth.modular.profile.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("MEMBER_CONTACT_PROFILE")
public class MemberContactProfile extends CommonEntity {
   @Schema(
      description = "主键"
   )
   @TableId(
      type = IdType.AUTO
   )
   private Long id;
   @Schema(
      description = "会员ID"
   )
   private Long memberId;
   @Schema(
      description = "省编码"
   )
   private String provinceCode;
   @Schema(
      description = "省名称"
   )
   private String provinceName;
   @Schema(
      description = "市编码"
   )
   private String cityCode;
   @Schema(
      description = "市名称"
   )
   private String cityName;
   @Schema(
      description = "区县编码"
   )
   private String districtCode;
   @Schema(
      description = "区县名称"
   )
   private String districtName;
   @Schema(
      description = "详细地址"
   )
   private String detailAddress;
   @Schema(
      description = "邮政编码"
   )
   private String postalCode;
   @Schema(
      description = "固定电话"
   )
   private String fixedPhone;
   @Schema(
      description = "传真"
   )
   private String fax;
   @Schema(
      description = "排序"
   )
   private Integer sortCode;
   @Schema(
      description = "扩展信息"
   )
   private String extJson;

   public Long getId() {
      return this.id;
   }

   public Long getMemberId() {
      return this.memberId;
   }

   public String getProvinceCode() {
      return this.provinceCode;
   }

   public String getProvinceName() {
      return this.provinceName;
   }

   public String getCityCode() {
      return this.cityCode;
   }

   public String getCityName() {
      return this.cityName;
   }

   public String getDistrictCode() {
      return this.districtCode;
   }

   public String getDistrictName() {
      return this.districtName;
   }

   public String getDetailAddress() {
      return this.detailAddress;
   }

   public String getPostalCode() {
      return this.postalCode;
   }

   public String getFixedPhone() {
      return this.fixedPhone;
   }

   public String getFax() {
      return this.fax;
   }

   public Integer getSortCode() {
      return this.sortCode;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setId(final Long id) {
      this.id = id;
   }

   public void setMemberId(final Long memberId) {
      this.memberId = memberId;
   }

   public void setProvinceCode(final String provinceCode) {
      this.provinceCode = provinceCode;
   }

   public void setProvinceName(final String provinceName) {
      this.provinceName = provinceName;
   }

   public void setCityCode(final String cityCode) {
      this.cityCode = cityCode;
   }

   public void setCityName(final String cityName) {
      this.cityName = cityName;
   }

   public void setDistrictCode(final String districtCode) {
      this.districtCode = districtCode;
   }

   public void setDistrictName(final String districtName) {
      this.districtName = districtName;
   }

   public void setDetailAddress(final String detailAddress) {
      this.detailAddress = detailAddress;
   }

   public void setPostalCode(final String postalCode) {
      this.postalCode = postalCode;
   }

   public void setFixedPhone(final String fixedPhone) {
      this.fixedPhone = fixedPhone;
   }

   public void setFax(final String fax) {
      this.fax = fax;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
