package com.rxinyun.common.qzsystem.entity;

import java.io.Serializable;
import java.util.List;

public class MfcCreateResult implements Serializable {
   private String instanceId;
   private String mainip;
   private String gateway;
   private String subnetMask;
   private List<MfcIpItem> ipList;

   public String getInstanceId() {
      return this.instanceId;
   }

   public String getMainip() {
      return this.mainip;
   }

   public String getGateway() {
      return this.gateway;
   }

   public String getSubnetMask() {
      return this.subnetMask;
   }

   public List<MfcIpItem> getIpList() {
      return this.ipList;
   }

   public void setInstanceId(final String instanceId) {
      this.instanceId = instanceId;
   }

   public void setMainip(final String mainip) {
      this.mainip = mainip;
   }

   public void setGateway(final String gateway) {
      this.gateway = gateway;
   }

   public void setSubnetMask(final String subnetMask) {
      this.subnetMask = subnetMask;
   }

   public void setIpList(final List<MfcIpItem> ipList) {
      this.ipList = ipList;
   }

   public MfcCreateResult() {
   }

   public MfcCreateResult(final String instanceId, final String mainip, final String gateway, final String subnetMask, final List<MfcIpItem> ipList) {
      this.instanceId = instanceId;
      this.mainip = mainip;
      this.gateway = gateway;
      this.subnetMask = subnetMask;
      this.ipList = ipList;
   }
}
