package com.rxinyun.admin.production.modular.vhost.line.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "VHOST_LINE",
   autoResultMap = true
)
public class VhostLine extends CommonEntity {
   @Schema(
      description = "线路ID"
   )
   private String id;
   @Schema(
      description = "地域ID"
   )
   private String regionId;
   @Schema(
      description = "地域名称"
   )
   @TableField(
      exist = false
   )
   private String regionName;
   @Schema(
      description = "线路名称"
   )
   private String name;
   @Schema(
      description = "排序"
   )
   private Integer sortId;
   @Schema(
      description = "状态：1启用 0禁用"
   )
   private Integer status;
   @Schema(
      description = "备注"
   )
   private String remark;

   public String getId() {
      return this.id;
   }

   public String getRegionId() {
      return this.regionId;
   }

   public String getRegionName() {
      return this.regionName;
   }

   public String getName() {
      return this.name;
   }

   public Integer getSortId() {
      return this.sortId;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setRegionId(final String regionId) {
      this.regionId = regionId;
   }

   public void setRegionName(final String regionName) {
      this.regionName = regionName;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setSortId(final Integer sortId) {
      this.sortId = sortId;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public static class Bound {
      @Schema(
         description = "线路ID"
      )
      private String id;
      @Schema(
         description = "线路名称"
      )
      private String name;

      public String getId() {
         return this.id;
      }

      public String getName() {
         return this.name;
      }

      public void setId(final String id) {
         this.id = id;
      }

      public void setName(final String name) {
         this.name = name;
      }
   }
}
