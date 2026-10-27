package com.rxinyun.member.business.modular.esc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "BUSINESS_SERVER_STRATEGY",
   autoResultMap = true
)
public class MemberServerStrategy extends CommonEntity {
   @Schema(
      description = "服务器策略标识"
   )
   private String id;
   @Schema(
      description = "云服务器ID"
   )
   private String serverId;
   @Schema(
      description = "策略：1允许通过，2拒绝通过"
   )
   private Integer action;
   @Schema(
      description = "方向：1流入，2流出"
   )
   private String direction;
   @Schema(
      description = "IP地址"
   )
   private String ip;
   @Schema(
      description = "端口"
   )
   private Integer port;
   @Schema(
      description = "协议类型：tcp udp icmp"
   )
   private String protocol;
   @Schema(
      description = "优先级：1-10000"
   )
   private Integer weight;

   public String getId() {
      return this.id;
   }

   public String getServerId() {
      return this.serverId;
   }

   public Integer getAction() {
      return this.action;
   }

   public String getDirection() {
      return this.direction;
   }

   public String getIp() {
      return this.ip;
   }

   public Integer getPort() {
      return this.port;
   }

   public String getProtocol() {
      return this.protocol;
   }

   public Integer getWeight() {
      return this.weight;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setAction(final Integer action) {
      this.action = action;
   }

   public void setDirection(final String direction) {
      this.direction = direction;
   }

   public void setIp(final String ip) {
      this.ip = ip;
   }

   public void setPort(final Integer port) {
      this.port = port;
   }

   public void setProtocol(final String protocol) {
      this.protocol = protocol;
   }

   public void setWeight(final Integer weight) {
      this.weight = weight;
   }
}
