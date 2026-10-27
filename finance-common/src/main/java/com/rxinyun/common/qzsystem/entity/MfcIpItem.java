package com.rxinyun.common.qzsystem.entity;

import java.io.Serializable;

public class MfcIpItem implements Serializable {
   private Integer id;
   private String ipaddress;
   private String gateway;
   private String subnetMask;

   public Integer getId() {
      return this.id;
   }

   public String getIpaddress() {
      return this.ipaddress;
   }

   public String getGateway() {
      return this.gateway;
   }

   public String getSubnetMask() {
      return this.subnetMask;
   }

   public void setId(final Integer id) {
      this.id = id;
   }

   public void setIpaddress(final String ipaddress) {
      this.ipaddress = ipaddress;
   }

   public void setGateway(final String gateway) {
      this.gateway = gateway;
   }

   public void setSubnetMask(final String subnetMask) {
      this.subnetMask = subnetMask;
   }

   public MfcIpItem() {
   }

   public MfcIpItem(final Integer id, final String ipaddress, final String gateway, final String subnetMask) {
      this.id = id;
      this.ipaddress = ipaddress;
      this.gateway = gateway;
      this.subnetMask = subnetMask;
   }
}
