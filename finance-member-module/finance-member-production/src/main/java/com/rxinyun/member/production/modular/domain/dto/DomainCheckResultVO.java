package com.rxinyun.member.production.modular.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

@Schema(
   description = "域名可注册查询返回"
)
public class DomainCheckResultVO {
   @Schema(
      description = "执行结果代码：0-成功，非0-失败"
   )
   private String code;
   @Schema(
      description = "执行结果说明"
   )
   private String msg;
   @Schema(
      description = "本次请求唯一ID"
   )
   private String requestId;
   @Schema(
      description = "返回列表，每项为域名及可注册状态、词性、溢价价格、备注"
   )
   private List<DomainCheckItemVO> data = new ArrayList<>();

   public String getCode() {
      return this.code;
   }

   public String getMsg() {
      return this.msg;
   }

   public String getRequestId() {
      return this.requestId;
   }

   public List<DomainCheckItemVO> getData() {
      return this.data;
   }

   public void setCode(final String code) {
      this.code = code;
   }

   public void setMsg(final String msg) {
      this.msg = msg;
   }

   public void setRequestId(final String requestId) {
      this.requestId = requestId;
   }

   public void setData(final List<DomainCheckItemVO> data) {
      this.data = data;
   }
}
