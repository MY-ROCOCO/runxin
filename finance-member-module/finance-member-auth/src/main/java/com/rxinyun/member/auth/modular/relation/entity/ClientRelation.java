package com.rxinyun.member.auth.modular.relation.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("CLIENT_RELATION")
public class ClientRelation {
   private String id;
   private String objectId;
   private String targetId;
   private String category;
   private String extJson;

   public String getId() {
      return this.id;
   }

   public String getObjectId() {
      return this.objectId;
   }

   public String getTargetId() {
      return this.targetId;
   }

   public String getCategory() {
      return this.category;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setObjectId(final String objectId) {
      this.objectId = objectId;
   }

   public void setTargetId(final String targetId) {
      this.targetId = targetId;
   }

   public void setCategory(final String category) {
      this.category = category;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
