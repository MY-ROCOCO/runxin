package com.rxinyun.agent.modular.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

@TableName(
   value = "agent_product_ratio",
   autoResultMap = true
)
public class AgentProductRatio extends CommonEntity {
   @TableId(
      type = IdType.INPUT
   )
   private String id;
   @Schema(
      description = "代理主体ID"
   )
   private String agentId;
   @Schema(
      description = "产品类型：1云服务器 2独立服务器 3域名 4DNS云解析 5虚拟主机"
   )
   private Integer productType;
   @Schema(
      description = "分成比例，范围0到100"
   )
   private BigDecimal ratio;
   @Schema(
      description = "状态"
   )
   private String status;
   @Schema(
      description = "生效时间"
   )
   private Date effectiveTime;
   @Schema(
      description = "失效时间"
   )
   private Date expireTime;

   public String getId() {
      return this.id;
   }

   public String getAgentId() {
      return this.agentId;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public BigDecimal getRatio() {
      return this.ratio;
   }

   public String getStatus() {
      return this.status;
   }

   public Date getEffectiveTime() {
      return this.effectiveTime;
   }

   public Date getExpireTime() {
      return this.expireTime;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setAgentId(final String agentId) {
      this.agentId = agentId;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
   }

   public void setRatio(final BigDecimal ratio) {
      this.ratio = ratio;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setEffectiveTime(final Date effectiveTime) {
      this.effectiveTime = effectiveTime;
   }

   public void setExpireTime(final Date expireTime) {
      this.expireTime = expireTime;
   }
}
