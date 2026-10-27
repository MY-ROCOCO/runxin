package com.rxinyun.admin.dev.modular.file.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class DevFileUrlListParam {
   @Schema(
      description = "文件路径集合"
   )
   @NotNull(
      message = "urlList不能为空"
   )
   private List<String> urlList;

   public List<String> getUrlList() {
      return this.urlList;
   }

   public void setUrlList(final List<String> urlList) {
      this.urlList = urlList;
   }
}
