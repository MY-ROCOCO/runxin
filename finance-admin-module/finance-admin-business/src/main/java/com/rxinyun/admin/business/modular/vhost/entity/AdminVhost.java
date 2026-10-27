package com.rxinyun.admin.business.modular.vhost.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.handler.ListToStringTypeHandler;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.type.JdbcType;

@TableName(
   value = "BUSINESS_VHOST",
   autoResultMap = true
)
@Schema(
   description = "虚拟主机实例"
)
public class AdminVhost extends CommonEntity {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "会员编号"
   )
   private String memberNo;
   @Schema(
      description = "来源订单号"
   )
   private String orderNo;
   @Schema(
      description = "最近一次履约订单号"
   )
   private String lastOrderNo;
   @Schema(
      description = "版本ID"
   )
   private String versionId;
   @Schema(
      description = "版本名称"
   )
   private String versionName;
   @Schema(
      description = "地域ID"
   )
   private String regionId;
   @Schema(
      description = "地域名称"
   )
   private String regionName;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "线路名称"
   )
   private String lineName;
   @Schema(
      description = "购买月数"
   )
   private Integer months;
   @Schema(
      description = "购买流量，单位G"
   )
   private Integer trafficSize;
   @Schema(
      description = "赠送流量，单位G"
   )
   private Integer trafficFreeSize;
   @Schema(
      description = "计费流量，单位G"
   )
   private Integer billableTraffic;
   @Schema(
      description = "版本月价格"
   )
   private BigDecimal monthPrice;
   @TableField(
      exist = false
   )
   @Schema(
      description = "当前套餐流量单价，元/G"
   )
   private BigDecimal trafficPrice;
   @Schema(
      description = "实付金额"
   )
   private BigDecimal payPrice;
   @Schema(
      description = "续费月价格"
   )
   private BigDecimal renewPrice;
   @TableField(
      exist = false
   )
   @Schema(
      description = "当前套餐支持的PHP版本"
   )
   private String phpVersions;
   @Schema(
      description = "开通时选择的PHP版本"
   )
   private String phpVersion;
   @TableField(
      exist = false
   )
   @Schema(
      description = "当前套餐支持的MySQL版本"
   )
   private String mysqlVersions;
   @Schema(
      description = "主机名称"
   )
   private String hostName;
   @Schema(
      description = "主机状态：CREATING创建中 RUNNING运行中 STOP已停止"
   )
   private String hostStatus;
   @Schema(
      description = "业务状态：1正常 -1过期 -2删除"
   )
   private Integer businessStatus;
   @Schema(
      description = "到期时间"
   )
   private Date endTime;
   @Schema(
      description = "到期删除时间"
   )
   private Date deleteTime;
   @Schema(
      description = "主控ID"
   )
   private String masterId;
   @Schema(
      description = "主控名称"
   )
   @TableField(
      exist = false
   )
   private String masterName;
   @Schema(
      description = "主控返回的虚拟主机ID"
   )
   private String panelHostId;
   @Schema(
      description = "FTP连接地址"
   )
   private String ftpAddress;
   @Schema(
      description = "数据库名"
   )
   private String dbName;
   @Schema(
      description = "数据库用户名"
   )
   private String dbUser;
   @Schema(
      description = "数据库密码"
   )
   private String dbPassword;
   @Schema(
      description = "FTP用户名"
   )
   private String ftpUser;
   @Schema(
      description = "FTP密码"
   )
   private String ftpPassword;
   @Schema(
      description = "绑定域名"
   )
   @TableField(
      value = "BIND_DOMAINS",
      jdbcType = JdbcType.VARCHAR,
      typeHandler = ListToStringTypeHandler.class
   )
   private List<String> bindDomains;
   @Schema(
      description = "管理员备注"
   )
   private String remark;
   @Schema(
      description = "会员备注"
   )
   private String memberRemark;

   public String getId() {
      return this.id;
   }

   public String getMemberNo() {
      return this.memberNo;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getLastOrderNo() {
      return this.lastOrderNo;
   }

   public String getVersionId() {
      return this.versionId;
   }

   public String getVersionName() {
      return this.versionName;
   }

   public String getRegionId() {
      return this.regionId;
   }

   public String getRegionName() {
      return this.regionName;
   }

   public String getLineId() {
      return this.lineId;
   }

   public String getLineName() {
      return this.lineName;
   }

   public Integer getMonths() {
      return this.months;
   }

   public Integer getTrafficSize() {
      return this.trafficSize;
   }

   public Integer getTrafficFreeSize() {
      return this.trafficFreeSize;
   }

   public Integer getBillableTraffic() {
      return this.billableTraffic;
   }

   public BigDecimal getMonthPrice() {
      return this.monthPrice;
   }

   public BigDecimal getTrafficPrice() {
      return this.trafficPrice;
   }

   public BigDecimal getPayPrice() {
      return this.payPrice;
   }

   public BigDecimal getRenewPrice() {
      return this.renewPrice;
   }

   public String getPhpVersions() {
      return this.phpVersions;
   }

   public String getPhpVersion() {
      return this.phpVersion;
   }

   public String getMysqlVersions() {
      return this.mysqlVersions;
   }

   public String getHostName() {
      return this.hostName;
   }

   public String getHostStatus() {
      return this.hostStatus;
   }

   public Integer getBusinessStatus() {
      return this.businessStatus;
   }

   public Date getEndTime() {
      return this.endTime;
   }

   public Date getDeleteTime() {
      return this.deleteTime;
   }

   public String getMasterId() {
      return this.masterId;
   }

   public String getMasterName() {
      return this.masterName;
   }

   public String getPanelHostId() {
      return this.panelHostId;
   }

   public String getFtpAddress() {
      return this.ftpAddress;
   }

   public String getDbName() {
      return this.dbName;
   }

   public String getDbUser() {
      return this.dbUser;
   }

   public String getDbPassword() {
      return this.dbPassword;
   }

   public String getFtpUser() {
      return this.ftpUser;
   }

   public String getFtpPassword() {
      return this.ftpPassword;
   }

   public List<String> getBindDomains() {
      return this.bindDomains;
   }

   public String getRemark() {
      return this.remark;
   }

   public String getMemberRemark() {
      return this.memberRemark;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setMemberNo(final String memberNo) {
      this.memberNo = memberNo;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setLastOrderNo(final String lastOrderNo) {
      this.lastOrderNo = lastOrderNo;
   }

   public void setVersionId(final String versionId) {
      this.versionId = versionId;
   }

   public void setVersionName(final String versionName) {
      this.versionName = versionName;
   }

   public void setRegionId(final String regionId) {
      this.regionId = regionId;
   }

   public void setRegionName(final String regionName) {
      this.regionName = regionName;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setLineName(final String lineName) {
      this.lineName = lineName;
   }

   public void setMonths(final Integer months) {
      this.months = months;
   }

   public void setTrafficSize(final Integer trafficSize) {
      this.trafficSize = trafficSize;
   }

   public void setTrafficFreeSize(final Integer trafficFreeSize) {
      this.trafficFreeSize = trafficFreeSize;
   }

   public void setBillableTraffic(final Integer billableTraffic) {
      this.billableTraffic = billableTraffic;
   }

   public void setMonthPrice(final BigDecimal monthPrice) {
      this.monthPrice = monthPrice;
   }

   public void setTrafficPrice(final BigDecimal trafficPrice) {
      this.trafficPrice = trafficPrice;
   }

   public void setPayPrice(final BigDecimal payPrice) {
      this.payPrice = payPrice;
   }

   public void setRenewPrice(final BigDecimal renewPrice) {
      this.renewPrice = renewPrice;
   }

   public void setPhpVersions(final String phpVersions) {
      this.phpVersions = phpVersions;
   }

   public void setPhpVersion(final String phpVersion) {
      this.phpVersion = phpVersion;
   }

   public void setMysqlVersions(final String mysqlVersions) {
      this.mysqlVersions = mysqlVersions;
   }

   public void setHostName(final String hostName) {
      this.hostName = hostName;
   }

   public void setHostStatus(final String hostStatus) {
      this.hostStatus = hostStatus;
   }

   public void setBusinessStatus(final Integer businessStatus) {
      this.businessStatus = businessStatus;
   }

   public void setEndTime(final Date endTime) {
      this.endTime = endTime;
   }

   public void setDeleteTime(final Date deleteTime) {
      this.deleteTime = deleteTime;
   }

   public void setMasterId(final String masterId) {
      this.masterId = masterId;
   }

   public void setMasterName(final String masterName) {
      this.masterName = masterName;
   }

   public void setPanelHostId(final String panelHostId) {
      this.panelHostId = panelHostId;
   }

   public void setFtpAddress(final String ftpAddress) {
      this.ftpAddress = ftpAddress;
   }

   public void setDbName(final String dbName) {
      this.dbName = dbName;
   }

   public void setDbUser(final String dbUser) {
      this.dbUser = dbUser;
   }

   public void setDbPassword(final String dbPassword) {
      this.dbPassword = dbPassword;
   }

   public void setFtpUser(final String ftpUser) {
      this.ftpUser = ftpUser;
   }

   public void setFtpPassword(final String ftpPassword) {
      this.ftpPassword = ftpPassword;
   }

   public void setBindDomains(final List<String> bindDomains) {
      this.bindDomains = bindDomains;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setMemberRemark(final String memberRemark) {
      this.memberRemark = memberRemark;
   }
}
