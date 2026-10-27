package com.rxinyun.admin.business.modular.domain.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(
   description = "域名上游注册请求记录分页查询参数"
)
public class DomainRegistrationRequestQueryParam implements Serializable {
   @Schema(
      description = "当前页码",
      example = "1"
   )
   private Integer current = 1;
   @Schema(
      description = "每页条数",
      example = "20"
   )
   private Integer size = 20;
   @Schema(
      description = "订单编号（可选，不传则不过滤）"
   )
   private String orderNo;
   @Schema(
      description = "域名（可选，模糊）"
   )
   private String domainName;
   @Schema(
      description = "状态筛选：0 待提交 1 已成功 2 失败（可选，不传则不过滤）"
   )
   private Integer status;

   public Integer getCurrent() {
      return this.current;
   }

   public Integer getSize() {
      return this.size;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getDomainName() {
      return this.domainName;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setCurrent(final Integer current) {
      this.current = current;
   }

   public void setSize(final Integer size) {
      this.size = size;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setDomainName(final String domainName) {
      this.domainName = domainName;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
