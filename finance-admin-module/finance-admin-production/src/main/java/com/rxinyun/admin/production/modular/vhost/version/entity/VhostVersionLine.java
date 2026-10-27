package com.rxinyun.admin.production.modular.vhost.version.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "VHOST_VERSION_LINE",
   autoResultMap = true
)
public class VhostVersionLine extends CommonEntity {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "版本ID"
   )
   private String versionId;
   @Schema(
      description = "线路ID"
   )
   private String lineId;

   public String getId() {
      return this.id;
   }

   public String getVersionId() {
      return this.versionId;
   }

   public String getLineId() {
      return this.lineId;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setVersionId(final String versionId) {
      this.versionId = versionId;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }
}
