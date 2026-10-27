package com.rxinyun.member.production.modular.domain.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(
   description = "域名可注册查询参数"
)
public class DomainCheckParam {
   @NotBlank(
      message = "域名前缀不能为空"
   )
   @Schema(
      description = "域名前缀",
      example = "abc123",
      requiredMode = RequiredMode.REQUIRED
   )
   private String prefix;
   @NotEmpty(
      message = "域名后缀不能为空"
   )
   @Schema(
      description = "域名后缀数组",
      example = "[ \".com\", \".cn\" ]",
      requiredMode = RequiredMode.REQUIRED
   )
   private List<String> suffix;

   public String getPrefix() {
      return this.prefix;
   }

   public List<String> getSuffix() {
      return this.suffix;
   }

   public void setPrefix(final String prefix) {
      this.prefix = prefix;
   }

   public void setSuffix(final List<String> suffix) {
      this.suffix = suffix;
   }
}
