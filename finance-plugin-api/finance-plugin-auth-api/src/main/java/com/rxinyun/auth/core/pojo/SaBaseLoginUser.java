package com.rxinyun.auth.core.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;

public abstract class SaBaseLoginUser {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "头像"
   )
   private String avatar;
   @Schema(
      description = "签名"
   )
   private String signature;
   @Schema(
      description = "账号"
   )
   private String account;
   @Schema(
      description = "姓名"
   )
   private String name;
   @Schema(
      description = "昵称"
   )
   private String nickname;
   @Schema(
      description = "性别"
   )
   private String gender;
   @Schema(
      description = "年龄"
   )
   private String age;
   @Schema(
      description = "出生日期"
   )
   private String birthday;
   @Schema(
      description = "民族"
   )
   private String nation;
   @Schema(
      description = "籍贯"
   )
   private String nativePlace;
   @Schema(
      description = "家庭住址"
   )
   private String homeAddress;
   @Schema(
      description = "通信地址"
   )
   private String mailingAddress;
   @Schema(
      description = "证件类型"
   )
   private String idCardType;
   @Schema(
      description = "证件号码"
   )
   private String idCardNumber;
   @Schema(
      description = "文化程度"
   )
   private String cultureLevel;
   @Schema(
      description = "政治面貌"
   )
   private String politicalOutlook;
   @Schema(
      description = "毕业院校"
   )
   private String college;
   @Schema(
      description = "学历"
   )
   private String education;
   @Schema(
      description = "学制"
   )
   private String eduLength;
   @Schema(
      description = "学位"
   )
   private String degree;
   @Schema(
      description = "手机"
   )
   private String phone;
   @Schema(
      description = "邮箱"
   )
   private String email;
   @Schema(
      description = "是否已绑定Telegram"
   )
   private Boolean telegramBound;
   @Schema(
      description = "Telegram chat id"
   )
   private String telegramChatId;
   @Schema(
      description = "Telegram用户ID"
   )
   private String telegramUserId;
   @Schema(
      description = "Telegram用户名"
   )
   private String telegramUsername;
   @Schema(
      description = "家庭电话"
   )
   private String homeTel;
   @Schema(
      description = "办公电话"
   )
   private String officeTel;
   @Schema(
      description = "紧急联系人"
   )
   private String emergencyContact;
   @Schema(
      description = "紧急联系人电话"
   )
   private String emergencyPhone;
   @Schema(
      description = "紧急联系人地址"
   )
   private String emergencyAddress;
   @Schema(
      description = "员工编号"
   )
   private String empNo;
   @Schema(
      description = "入职日期"
   )
   private String entryDate;
   @Schema(
      description = "组织名称"
   )
   private String orgName;
   @Schema(
      description = "职位名称"
   )
   private String positionName;
   @Schema(
      description = "职级"
   )
   private String positionLevel;
   @Schema(
      description = "主管id"
   )
   private String directorId;
   @Schema(
      description = "兼任信息"
   )
   private String positionJson;
   @Schema(
      description = "上次登录ip"
   )
   private String lastLoginIp;
   @Schema(
      description = "上次登录地点"
   )
   private String lastLoginAddress;
   @Schema(
      description = "上次登录时间"
   )
   private Date lastLoginTime;
   @Schema(
      description = "上次登录设备"
   )
   private String lastLoginDevice;
   @Schema(
      description = "最新登录ip"
   )
   private String latestLoginIp;
   @Schema(
      description = "最新登录地点"
   )
   private String latestLoginAddress;
   @Schema(
      description = "最新登录时间"
   )
   private Date latestLoginTime;
   @Schema(
      description = "最新登录设备"
   )
   private String latestLoginDevice;
   @Schema(
      description = "用户状态"
   )
   private String userStatus;
   @Schema(
      description = "排序码"
   )
   private Integer sortCode;
   @Schema(
      description = "扩展信息"
   )
   private String extJson;
   @Schema(
      description = "按钮码集合"
   )
   private List<String> buttonCodeList;
   @Schema(
      description = "移动端按钮码集合"
   )
   private List<String> mobileButtonCodeList;
   @Schema(
      description = "权限码集合"
   )
   private List<String> permissionCodeList;
   @Schema(
      description = "角色码集合"
   )
   private List<String> roleCodeList;
   @Schema(
      description = "数据范围集合"
   )
   private List<SaBaseLoginUser.DataScope> dataScopeList;
   @JsonIgnore
   @Schema(
      description = "用户密码hash值"
   )
   private String password;

   public abstract Boolean getEnabled();

   public String getId() {
      return this.id;
   }

   public String getAvatar() {
      return this.avatar;
   }

   public String getSignature() {
      return this.signature;
   }

   public String getAccount() {
      return this.account;
   }

   public String getName() {
      return this.name;
   }

   public String getNickname() {
      return this.nickname;
   }

   public String getGender() {
      return this.gender;
   }

   public String getAge() {
      return this.age;
   }

   public String getBirthday() {
      return this.birthday;
   }

   public String getNation() {
      return this.nation;
   }

   public String getNativePlace() {
      return this.nativePlace;
   }

   public String getHomeAddress() {
      return this.homeAddress;
   }

   public String getMailingAddress() {
      return this.mailingAddress;
   }

   public String getIdCardType() {
      return this.idCardType;
   }

   public String getIdCardNumber() {
      return this.idCardNumber;
   }

   public String getCultureLevel() {
      return this.cultureLevel;
   }

   public String getPoliticalOutlook() {
      return this.politicalOutlook;
   }

   public String getCollege() {
      return this.college;
   }

   public String getEducation() {
      return this.education;
   }

   public String getEduLength() {
      return this.eduLength;
   }

   public String getDegree() {
      return this.degree;
   }

   public String getPhone() {
      return this.phone;
   }

   public String getEmail() {
      return this.email;
   }

   public Boolean getTelegramBound() {
      return this.telegramBound;
   }

   public String getTelegramChatId() {
      return this.telegramChatId;
   }

   public String getTelegramUserId() {
      return this.telegramUserId;
   }

   public String getTelegramUsername() {
      return this.telegramUsername;
   }

   public String getHomeTel() {
      return this.homeTel;
   }

   public String getOfficeTel() {
      return this.officeTel;
   }

   public String getEmergencyContact() {
      return this.emergencyContact;
   }

   public String getEmergencyPhone() {
      return this.emergencyPhone;
   }

   public String getEmergencyAddress() {
      return this.emergencyAddress;
   }

   public String getEmpNo() {
      return this.empNo;
   }

   public String getEntryDate() {
      return this.entryDate;
   }

   public String getOrgName() {
      return this.orgName;
   }

   public String getPositionName() {
      return this.positionName;
   }

   public String getPositionLevel() {
      return this.positionLevel;
   }

   public String getDirectorId() {
      return this.directorId;
   }

   public String getPositionJson() {
      return this.positionJson;
   }

   public String getLastLoginIp() {
      return this.lastLoginIp;
   }

   public String getLastLoginAddress() {
      return this.lastLoginAddress;
   }

   public Date getLastLoginTime() {
      return this.lastLoginTime;
   }

   public String getLastLoginDevice() {
      return this.lastLoginDevice;
   }

   public String getLatestLoginIp() {
      return this.latestLoginIp;
   }

   public String getLatestLoginAddress() {
      return this.latestLoginAddress;
   }

   public Date getLatestLoginTime() {
      return this.latestLoginTime;
   }

   public String getLatestLoginDevice() {
      return this.latestLoginDevice;
   }

   public String getUserStatus() {
      return this.userStatus;
   }

   public Integer getSortCode() {
      return this.sortCode;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public List<String> getButtonCodeList() {
      return this.buttonCodeList;
   }

   public List<String> getMobileButtonCodeList() {
      return this.mobileButtonCodeList;
   }

   public List<String> getPermissionCodeList() {
      return this.permissionCodeList;
   }

   public List<String> getRoleCodeList() {
      return this.roleCodeList;
   }

   public List<SaBaseLoginUser.DataScope> getDataScopeList() {
      return this.dataScopeList;
   }

   public String getPassword() {
      return this.password;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setAvatar(final String avatar) {
      this.avatar = avatar;
   }

   public void setSignature(final String signature) {
      this.signature = signature;
   }

   public void setAccount(final String account) {
      this.account = account;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setNickname(final String nickname) {
      this.nickname = nickname;
   }

   public void setGender(final String gender) {
      this.gender = gender;
   }

   public void setAge(final String age) {
      this.age = age;
   }

   public void setBirthday(final String birthday) {
      this.birthday = birthday;
   }

   public void setNation(final String nation) {
      this.nation = nation;
   }

   public void setNativePlace(final String nativePlace) {
      this.nativePlace = nativePlace;
   }

   public void setHomeAddress(final String homeAddress) {
      this.homeAddress = homeAddress;
   }

   public void setMailingAddress(final String mailingAddress) {
      this.mailingAddress = mailingAddress;
   }

   public void setIdCardType(final String idCardType) {
      this.idCardType = idCardType;
   }

   public void setIdCardNumber(final String idCardNumber) {
      this.idCardNumber = idCardNumber;
   }

   public void setCultureLevel(final String cultureLevel) {
      this.cultureLevel = cultureLevel;
   }

   public void setPoliticalOutlook(final String politicalOutlook) {
      this.politicalOutlook = politicalOutlook;
   }

   public void setCollege(final String college) {
      this.college = college;
   }

   public void setEducation(final String education) {
      this.education = education;
   }

   public void setEduLength(final String eduLength) {
      this.eduLength = eduLength;
   }

   public void setDegree(final String degree) {
      this.degree = degree;
   }

   public void setPhone(final String phone) {
      this.phone = phone;
   }

   public void setEmail(final String email) {
      this.email = email;
   }

   public void setTelegramBound(final Boolean telegramBound) {
      this.telegramBound = telegramBound;
   }

   public void setTelegramChatId(final String telegramChatId) {
      this.telegramChatId = telegramChatId;
   }

   public void setTelegramUserId(final String telegramUserId) {
      this.telegramUserId = telegramUserId;
   }

   public void setTelegramUsername(final String telegramUsername) {
      this.telegramUsername = telegramUsername;
   }

   public void setHomeTel(final String homeTel) {
      this.homeTel = homeTel;
   }

   public void setOfficeTel(final String officeTel) {
      this.officeTel = officeTel;
   }

   public void setEmergencyContact(final String emergencyContact) {
      this.emergencyContact = emergencyContact;
   }

   public void setEmergencyPhone(final String emergencyPhone) {
      this.emergencyPhone = emergencyPhone;
   }

   public void setEmergencyAddress(final String emergencyAddress) {
      this.emergencyAddress = emergencyAddress;
   }

   public void setEmpNo(final String empNo) {
      this.empNo = empNo;
   }

   public void setEntryDate(final String entryDate) {
      this.entryDate = entryDate;
   }

   public void setOrgName(final String orgName) {
      this.orgName = orgName;
   }

   public void setPositionName(final String positionName) {
      this.positionName = positionName;
   }

   public void setPositionLevel(final String positionLevel) {
      this.positionLevel = positionLevel;
   }

   public void setDirectorId(final String directorId) {
      this.directorId = directorId;
   }

   public void setPositionJson(final String positionJson) {
      this.positionJson = positionJson;
   }

   public void setLastLoginIp(final String lastLoginIp) {
      this.lastLoginIp = lastLoginIp;
   }

   public void setLastLoginAddress(final String lastLoginAddress) {
      this.lastLoginAddress = lastLoginAddress;
   }

   public void setLastLoginTime(final Date lastLoginTime) {
      this.lastLoginTime = lastLoginTime;
   }

   public void setLastLoginDevice(final String lastLoginDevice) {
      this.lastLoginDevice = lastLoginDevice;
   }

   public void setLatestLoginIp(final String latestLoginIp) {
      this.latestLoginIp = latestLoginIp;
   }

   public void setLatestLoginAddress(final String latestLoginAddress) {
      this.latestLoginAddress = latestLoginAddress;
   }

   public void setLatestLoginTime(final Date latestLoginTime) {
      this.latestLoginTime = latestLoginTime;
   }

   public void setLatestLoginDevice(final String latestLoginDevice) {
      this.latestLoginDevice = latestLoginDevice;
   }

   public void setUserStatus(final String userStatus) {
      this.userStatus = userStatus;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }

   public void setButtonCodeList(final List<String> buttonCodeList) {
      this.buttonCodeList = buttonCodeList;
   }

   public void setMobileButtonCodeList(final List<String> mobileButtonCodeList) {
      this.mobileButtonCodeList = mobileButtonCodeList;
   }

   public void setPermissionCodeList(final List<String> permissionCodeList) {
      this.permissionCodeList = permissionCodeList;
   }

   public void setRoleCodeList(final List<String> roleCodeList) {
      this.roleCodeList = roleCodeList;
   }

   public void setDataScopeList(final List<SaBaseLoginUser.DataScope> dataScopeList) {
      this.dataScopeList = dataScopeList;
   }

   @JsonIgnore
   public void setPassword(final String password) {
      this.password = password;
   }

   public static class DataScope {
      @Schema(
         description = "API接口"
      )
      private String apiUrl;
      @Schema(
         description = "数据范围"
      )
      private List<String> dataScope;

      public String getApiUrl() {
         return this.apiUrl;
      }

      public List<String> getDataScope() {
         return this.dataScope;
      }

      public void setApiUrl(final String apiUrl) {
         this.apiUrl = apiUrl;
      }

      public void setDataScope(final List<String> dataScope) {
         this.dataScope = dataScope;
      }
   }
}
