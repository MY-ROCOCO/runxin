package com.rxinyun.admin.business.modular.esc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("business_server_ports")
public class ServerPort extends CommonEntity {
   private String id;
   @Schema(
      description = "端口名称"
   )
   private String portName;
   @Schema(
      description = "宿主机ID"
   )
   private String masterId;
   @Schema(
      description = "宿主机IP"
   )
   private String masterIp;
   @Schema(
      description = "服务器ID"
   )
   private String serverId;
   @Schema(
      description = "服务器名称"
   )
   private String serverName;
   @Schema(
      description = "公网IP"
   )
   private String publicIp;
   @Schema(
      description = "内网IP"
   )
   private String privateIp;
   @Schema(
      description = "公网端口号"
   )
   private Integer publicPort;
   @Schema(
      description = "内网端口号"
   )
   private Integer privatePort;

   public String getId() {
      return this.id;
   }

   public String getPortName() {
      return this.portName;
   }

   public String getMasterId() {
      return this.masterId;
   }

   public String getMasterIp() {
      return this.masterIp;
   }

   public String getServerId() {
      return this.serverId;
   }

   public String getServerName() {
      return this.serverName;
   }

   public String getPublicIp() {
      return this.publicIp;
   }

   public String getPrivateIp() {
      return this.privateIp;
   }

   public Integer getPublicPort() {
      return this.publicPort;
   }

   public Integer getPrivatePort() {
      return this.privatePort;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setPortName(final String portName) {
      this.portName = portName;
   }

   public void setMasterId(final String masterId) {
      this.masterId = masterId;
   }

   public void setMasterIp(final String masterIp) {
      this.masterIp = masterIp;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setServerName(final String serverName) {
      this.serverName = serverName;
   }

   public void setPublicIp(final String publicIp) {
      this.publicIp = publicIp;
   }

   public void setPrivateIp(final String privateIp) {
      this.privateIp = privateIp;
   }

   public void setPublicPort(final Integer publicPort) {
      this.publicPort = publicPort;
   }

   public void setPrivatePort(final Integer privatePort) {
      this.privatePort = privatePort;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ServerPort other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$publicPort = this.getPublicPort();
         Object other$publicPort = other.getPublicPort();
         if (this$publicPort == null ? other$publicPort == null : this$publicPort.equals(other$publicPort)) {
            Object this$privatePort = this.getPrivatePort();
            Object other$privatePort = other.getPrivatePort();
            if (this$privatePort == null ? other$privatePort == null : this$privatePort.equals(other$privatePort)) {
               Object this$id = this.getId();
               Object other$id = other.getId();
               if (this$id == null ? other$id == null : this$id.equals(other$id)) {
                  Object this$portName = this.getPortName();
                  Object other$portName = other.getPortName();
                  if (this$portName == null ? other$portName == null : this$portName.equals(other$portName)) {
                     Object this$masterId = this.getMasterId();
                     Object other$masterId = other.getMasterId();
                     if (this$masterId == null ? other$masterId == null : this$masterId.equals(other$masterId)) {
                        Object this$masterIp = this.getMasterIp();
                        Object other$masterIp = other.getMasterIp();
                        if (this$masterIp == null ? other$masterIp == null : this$masterIp.equals(other$masterIp)) {
                           Object this$serverId = this.getServerId();
                           Object other$serverId = other.getServerId();
                           if (this$serverId == null ? other$serverId == null : this$serverId.equals(other$serverId)) {
                              Object this$serverName = this.getServerName();
                              Object other$serverName = other.getServerName();
                              if (this$serverName == null ? other$serverName == null : this$serverName.equals(other$serverName)) {
                                 Object this$publicIp = this.getPublicIp();
                                 Object other$publicIp = other.getPublicIp();
                                 if (this$publicIp == null ? other$publicIp == null : this$publicIp.equals(other$publicIp)) {
                                    Object this$privateIp = this.getPrivateIp();
                                    Object other$privateIp = other.getPrivateIp();
                                    return this$privateIp == null ? other$privateIp == null : this$privateIp.equals(other$privateIp);
                                 } else {
                                    return false;
                                 }
                              } else {
                                 return false;
                              }
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof ServerPort;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $publicPort = this.getPublicPort();
      result = result * 59 + ($publicPort == null ? 43 : $publicPort.hashCode());
      Object $privatePort = this.getPrivatePort();
      result = result * 59 + ($privatePort == null ? 43 : $privatePort.hashCode());
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $portName = this.getPortName();
      result = result * 59 + ($portName == null ? 43 : $portName.hashCode());
      Object $masterId = this.getMasterId();
      result = result * 59 + ($masterId == null ? 43 : $masterId.hashCode());
      Object $masterIp = this.getMasterIp();
      result = result * 59 + ($masterIp == null ? 43 : $masterIp.hashCode());
      Object $serverId = this.getServerId();
      result = result * 59 + ($serverId == null ? 43 : $serverId.hashCode());
      Object $serverName = this.getServerName();
      result = result * 59 + ($serverName == null ? 43 : $serverName.hashCode());
      Object $publicIp = this.getPublicIp();
      result = result * 59 + ($publicIp == null ? 43 : $publicIp.hashCode());
      Object $privateIp = this.getPrivateIp();
      return result * 59 + ($privateIp == null ? 43 : $privateIp.hashCode());
   }

   public String toString() {
      return "ServerPort(id="
         + this.getId()
         + ", portName="
         + this.getPortName()
         + ", masterId="
         + this.getMasterId()
         + ", masterIp="
         + this.getMasterIp()
         + ", serverId="
         + this.getServerId()
         + ", serverName="
         + this.getServerName()
         + ", publicIp="
         + this.getPublicIp()
         + ", privateIp="
         + this.getPrivateIp()
         + ", publicPort="
         + this.getPublicPort()
         + ", privatePort="
         + this.getPrivatePort()
         + ")";
   }
}
