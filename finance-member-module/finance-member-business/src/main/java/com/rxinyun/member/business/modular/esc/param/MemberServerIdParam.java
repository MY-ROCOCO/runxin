package com.rxinyun.member.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class MemberServerIdParam implements Serializable {
   @Schema(
      description = "云服务器ID"
   )
   private String id;
   @Schema(
      description = "RkHyper-v重建方式：0=挂原盘，1=全新重建；轻舟等其它受控不传"
   )
   private Integer fullRebuild;
   @Schema(
      description = "监控查询日期类型：1秒、2分、3时、4日、5月、6年"
   )
   private Integer dateType;
   @Schema(
      description = "监控查询最小时间，格式：yyyyMMddHHmmss"
   )
   private String minDateTime;
   @Schema(
      description = "监控查询最大时间，格式：yyyyMMddHHmmss"
   )
   private String maxDateTime;
   @Schema(
      description = "监控查询排序方式：desc降序、asc升序"
   )
   private String sort;

   public String getId() {
      return this.id;
   }

   public Integer getFullRebuild() {
      return this.fullRebuild;
   }

   public Integer getDateType() {
      return this.dateType;
   }

   public String getMinDateTime() {
      return this.minDateTime;
   }

   public String getMaxDateTime() {
      return this.maxDateTime;
   }

   public String getSort() {
      return this.sort;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setFullRebuild(final Integer fullRebuild) {
      this.fullRebuild = fullRebuild;
   }

   public void setDateType(final Integer dateType) {
      this.dateType = dateType;
   }

   public void setMinDateTime(final String minDateTime) {
      this.minDateTime = minDateTime;
   }

   public void setMaxDateTime(final String maxDateTime) {
      this.maxDateTime = maxDateTime;
   }

   public void setSort(final String sort) {
      this.sort = sort;
   }
}
