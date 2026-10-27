package com.rxinyun.admin.production.modular.vhost.master.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "VHOST_MASTER_LINE",
   autoResultMap = true
)
public class VhostMasterLine extends CommonEntity {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "主控ID"
   )
   private String masterId;
   @Schema(
      description = "线路ID"
   )
   private String lineId;

   public String getId() {
      return this.id;
   }

   public String getMasterId() {
      return this.masterId;
   }

   public String getLineId() {
      return this.lineId;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setMasterId(final String masterId) {
      this.masterId = masterId;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }
}
