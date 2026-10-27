package com.rxinyun.admin.notice.modular.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "template_tab",
   autoResultMap = true
)
public class TemplateTab {
   @Schema(
      description = "模板标签标识"
   )
   private String id;
   @Schema(
      description = "标签名称"
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
