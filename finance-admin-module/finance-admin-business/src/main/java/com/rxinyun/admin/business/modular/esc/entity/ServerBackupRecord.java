package com.rxinyun.admin.business.modular.esc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName(
   value = "SERVER_BACKUP_RECORD",
   autoResultMap = true
)
public class ServerBackupRecord {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "云服务器ID"
   )
   private String serverId;
   @Schema(
      description = "会员ID"
   )
   private String memberNo;
   @Schema(
      description = "备份名称"
   )
   private String backupName;
   @Schema(
      description = "备份存放路径"
   )
   private String backupPath;
   @Schema(
      description = "创建时间"
   )
   private Date createTime;

   public String getId() {
      return this.id;
   }

   public String getServerId() {
      return this.serverId;
   }

   public String getMemberNo() {
      return this.memberNo;
   }

   public String getBackupName() {
      return this.backupName;
   }

   public String getBackupPath() {
      return this.backupPath;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setMemberNo(final String memberNo) {
      this.memberNo = memberNo;
   }

   public void setBackupName(final String backupName) {
      this.backupName = backupName;
   }

   public void setBackupPath(final String backupPath) {
      this.backupPath = backupPath;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }
}
