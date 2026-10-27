package com.rxinyun.member.member.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;

public class BaseMemberGroups {
   @Schema(
      description = "会员组ID"
   )
   private String id;
   @Schema(
      description = "会员组名称 不能重复"
   )
   private String groupName;
   @Schema(
      description = "会员组描述"
   )
   private String groupDescription;
   @TableField(
      exist = false
   )
   @Schema(
      description = "会员组人数"
   )
   private Long groupNumber;
   @Schema(
      description = "会员组等级"
   )
   private Integer power;

   public String getId() {
      return this.id;
   }

   public String getGroupName() {
      return this.groupName;
   }

   public String getGroupDescription() {
      return this.groupDescription;
   }

   public Long getGroupNumber() {
      return this.groupNumber;
   }

   public Integer getPower() {
      return this.power;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setGroupName(final String groupName) {
      this.groupName = groupName;
   }

   public void setGroupDescription(final String groupDescription) {
      this.groupDescription = groupDescription;
   }

   public void setGroupNumber(final Long groupNumber) {
      this.groupNumber = groupNumber;
   }

   public void setPower(final Integer power) {
      this.power = power;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof BaseMemberGroups other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$groupNumber = this.getGroupNumber();
         Object other$groupNumber = other.getGroupNumber();
         if (this$groupNumber == null ? other$groupNumber == null : this$groupNumber.equals(other$groupNumber)) {
            Object this$power = this.getPower();
            Object other$power = other.getPower();
            if (this$power == null ? other$power == null : this$power.equals(other$power)) {
               Object this$id = this.getId();
               Object other$id = other.getId();
               if (this$id == null ? other$id == null : this$id.equals(other$id)) {
                  Object this$groupName = this.getGroupName();
                  Object other$groupName = other.getGroupName();
                  if (this$groupName == null ? other$groupName == null : this$groupName.equals(other$groupName)) {
                     Object this$groupDescription = this.getGroupDescription();
                     Object other$groupDescription = other.getGroupDescription();
                     return this$groupDescription == null ? other$groupDescription == null : this$groupDescription.equals(other$groupDescription);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof BaseMemberGroups;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $groupNumber = this.getGroupNumber();
      result = result * 59 + ($groupNumber == null ? 43 : $groupNumber.hashCode());
      Object $power = this.getPower();
      result = result * 59 + ($power == null ? 43 : $power.hashCode());
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $groupName = this.getGroupName();
      result = result * 59 + ($groupName == null ? 43 : $groupName.hashCode());
      Object $groupDescription = this.getGroupDescription();
      return result * 59 + ($groupDescription == null ? 43 : $groupDescription.hashCode());
   }

   @Override
   public String toString() {
      return "BaseMemberGroups(id="
         + this.getId()
         + ", groupName="
         + this.getGroupName()
         + ", groupDescription="
         + this.getGroupDescription()
         + ", groupNumber="
         + this.getGroupNumber()
         + ", power="
         + this.getPower()
         + ")";
   }
}
