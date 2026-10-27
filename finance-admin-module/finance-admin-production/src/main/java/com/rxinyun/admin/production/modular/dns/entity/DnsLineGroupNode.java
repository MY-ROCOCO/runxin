package com.rxinyun.admin.production.modular.dns.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "DNS_LINE_GROUP_NODE",
   autoResultMap = true
)
public class DnsLineGroupNode extends CommonEntity {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "线路分组ID"
   )
   private String groupId;
   @Schema(
      description = "节点ID"
   )
   private String nodeId;

   public String getId() {
      return this.id;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public String getNodeId() {
      return this.nodeId;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setGroupId(final String groupId) {
      this.groupId = groupId;
   }

   public void setNodeId(final String nodeId) {
      this.nodeId = nodeId;
   }
}
