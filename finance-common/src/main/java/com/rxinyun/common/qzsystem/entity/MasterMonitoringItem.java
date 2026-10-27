package com.rxinyun.common.qzsystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(
   description = "宿主机下云服务器监控项"
)
public class MasterMonitoringItem implements Serializable {
   @Schema(
      description = "实例名称"
   )
   private String instanceName;
   @Schema(
      description = "上行带宽，魔方云原样透传，单位含在字符串内，如 0bps、10.5Mbps"
   )
   private String uploadBandwidth;
   @Schema(
      description = "下行带宽，魔方云原样透传，单位含在字符串内，如 0bps、10.5Mbps"
   )
   private String downloadBandwidth;

   public String getInstanceName() {
      return this.instanceName;
   }

   public String getUploadBandwidth() {
      return this.uploadBandwidth;
   }

   public String getDownloadBandwidth() {
      return this.downloadBandwidth;
   }

   public void setInstanceName(final String instanceName) {
      this.instanceName = instanceName;
   }

   public void setUploadBandwidth(final String uploadBandwidth) {
      this.uploadBandwidth = uploadBandwidth;
   }

   public void setDownloadBandwidth(final String downloadBandwidth) {
      this.downloadBandwidth = downloadBandwidth;
   }

   public MasterMonitoringItem() {
   }

   public MasterMonitoringItem(final String instanceName, final String uploadBandwidth, final String downloadBandwidth) {
      this.instanceName = instanceName;
      this.uploadBandwidth = uploadBandwidth;
      this.downloadBandwidth = downloadBandwidth;
   }
}
