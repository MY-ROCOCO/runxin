package com.rxinyun.admin.production.modular.dns.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@TableName(
   value = "DNS_LINE_GROUP",
   autoResultMap = true
)
public class DnsLineGroup extends CommonEntity {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "线路分组名称"
   )
   private String name;
   @Schema(
      description = "状态：1启用 0禁用"
   )
   private Integer status;
   @Schema(
      description = "备注"
   )
   private String remark;
   @TableField(
      exist = false
   )
   @Schema(
      description = "绑定节点ID列表"
   )
   private List<String> nodeIds;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getRemark() {
      return this.remark;
   }

   public List<String> getNodeIds() {
      return this.nodeIds;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setNodeIds(final List<String> nodeIds) {
      this.nodeIds = nodeIds;
   }
}
