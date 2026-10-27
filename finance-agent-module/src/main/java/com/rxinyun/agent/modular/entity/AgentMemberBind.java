package com.rxinyun.agent.modular.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName(
   value = "agent_member_bind",
   autoResultMap = true
)
public class AgentMemberBind extends CommonEntity {
   @TableId(
      type = IdType.INPUT
   )
   private String id;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "代理主体ID"
   )
   private String agentId;
   @Schema(
      description = "绑定来源"
   )
   private String bindSource;
   @Schema(
      description = "绑定时间"
   )
   private Date bindTime;
   @Schema(
      description = "状态"
   )
   private String status;

   public String getId() {
      return this.id;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getAgentId() {
      return this.agentId;
   }

   public String getBindSource() {
      return this.bindSource;
   }

   public Date getBindTime() {
      return this.bindTime;
   }

   public String getStatus() {
      return this.status;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setAgentId(final String agentId) {
      this.agentId = agentId;
   }

   public void setBindSource(final String bindSource) {
      this.bindSource = bindSource;
   }

   public void setBindTime(final Date bindTime) {
      this.bindTime = bindTime;
   }

   public void setStatus(final String status) {
      this.status = status;
   }
}
