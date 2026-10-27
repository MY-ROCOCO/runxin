package com.rxinyun.admin.news.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "news_type",
   autoResultMap = true
)
public class NewsType extends CommonEntity {
   @Schema(
      description = "新闻类型ID"
   )
   private String id;
   @Schema(
      description = "新闻名称"
   )
   private String name;
   @Schema(
      description = "新闻排序字段"
   )
   private Integer sort;
   @Schema(
      description = "新闻发布状态"
   )
   private Integer status;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Integer getSort() {
      return this.sort;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setSort(final Integer sort) {
      this.sort = sort;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
