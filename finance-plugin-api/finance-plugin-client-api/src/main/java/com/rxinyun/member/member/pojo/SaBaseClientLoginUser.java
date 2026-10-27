package com.rxinyun.member.member.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public abstract class SaBaseClientLoginUser {
   @Schema(
      description = "id"
   )
   private Long id;
   @Schema(
      description = "用户组ID"
   )
   private String groupId;
   @Schema(
      description = "用户类型： 企业：ENTERPRISE， 个人：PERSONAL"
   )
   private String userType;
   @Schema(
      description = "头像，图片base64"
   )
   private String avatar;
   @Schema(
      description = "签名，图片base64"
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
      description = "通知开关：1=开启，0=关闭"
   )
   private Integer notificationEnabled;
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
      description = "实名认证状态：0 否 1 是"
   )
   private String realNameVerified;
   @Schema(
      description = "注册时间"
   )
   private Date registerTime;
   @Schema(
      description = "是否已绑定微信第三方登录"
   )
   private Boolean bindWechat;
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
      description = "会员本月消费"
   )
   private BigDecimal currentMonthConsumeAmount;
   @Schema(
      description = "会员本月充值"
   )
   private BigDecimal currentMonthRechargeAmount;
   @Schema(
      description = "会员当日消费"
   )
   private BigDecimal todayConsumeAmount;
   @Schema(
      description = "会员总消费"
   )
   private BigDecimal totalConsumeAmount;
   @Schema(
      description = "未完成的订单数"
   )
   private Long unfinishedOrderCount;
   @Schema(
      description = "未处理的工单数"
   )
   private Long unprocessedWorkOrderCount;
   @Schema(
      description = "可开票订单数量"
   )
   private Long invoicableOrderCount;
   @Schema(
      description = "可开票金额"
   )
   private BigDecimal invoicableOrderAmount;
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
      description = "权限码集合",
      hidden = true
   )
   private List<String> permissionCodeList;
   @Schema(
      description = "角色码集合",
      hidden = true
   )
   private List<String> roleCodeList;
   @Schema(
      description = "数据范围集合",
      hidden = true
   )
   private List<SaBaseClientLoginUser.DataScope> dataScopeList;
   @JsonIgnore
   @Schema(
      description = "用户密码hash值"
   )
   private String password;

   public abstract Boolean getEnabled();

   public Long getId() {
      return this.id;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public String getUserType() {
      return this.userType;
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

   public Integer getNotificationEnabled() {
      return this.notificationEnabled;
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

   public String getRealNameVerified() {
      return this.realNameVerified;
   }

   public Date getRegisterTime() {
      return this.registerTime;
   }

   public Boolean getBindWechat() {
      return this.bindWechat;
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

   public BigDecimal getCurrentMonthConsumeAmount() {
      return this.currentMonthConsumeAmount;
   }

   public BigDecimal getCurrentMonthRechargeAmount() {
      return this.currentMonthRechargeAmount;
   }

   public BigDecimal getTodayConsumeAmount() {
      return this.todayConsumeAmount;
   }

   public BigDecimal getTotalConsumeAmount() {
      return this.totalConsumeAmount;
   }

   public Long getUnfinishedOrderCount() {
      return this.unfinishedOrderCount;
   }

   public Long getUnprocessedWorkOrderCount() {
      return this.unprocessedWorkOrderCount;
   }

   public Long getInvoicableOrderCount() {
      return this.invoicableOrderCount;
   }

   public BigDecimal getInvoicableOrderAmount() {
      return this.invoicableOrderAmount;
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

   public List<SaBaseClientLoginUser.DataScope> getDataScopeList() {
      return this.dataScopeList;
   }

   public String getPassword() {
      return this.password;
   }

   public void setId(final Long id) {
      this.id = id;
   }

   public void setGroupId(final String groupId) {
      this.groupId = groupId;
   }

   public void setUserType(final String userType) {
      this.userType = userType;
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

   public void setNotificationEnabled(final Integer notificationEnabled) {
      this.notificationEnabled = notificationEnabled;
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

   public void setRealNameVerified(final String realNameVerified) {
      this.realNameVerified = realNameVerified;
   }

   public void setRegisterTime(final Date registerTime) {
      this.registerTime = registerTime;
   }

   public void setBindWechat(final Boolean bindWechat) {
      this.bindWechat = bindWechat;
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

   public void setCurrentMonthConsumeAmount(final BigDecimal currentMonthConsumeAmount) {
      this.currentMonthConsumeAmount = currentMonthConsumeAmount;
   }

   public void setCurrentMonthRechargeAmount(final BigDecimal currentMonthRechargeAmount) {
      this.currentMonthRechargeAmount = currentMonthRechargeAmount;
   }

   public void setTodayConsumeAmount(final BigDecimal todayConsumeAmount) {
      this.todayConsumeAmount = todayConsumeAmount;
   }

   public void setTotalConsumeAmount(final BigDecimal totalConsumeAmount) {
      this.totalConsumeAmount = totalConsumeAmount;
   }

   public void setUnfinishedOrderCount(final Long unfinishedOrderCount) {
      this.unfinishedOrderCount = unfinishedOrderCount;
   }

   public void setUnprocessedWorkOrderCount(final Long unprocessedWorkOrderCount) {
      this.unprocessedWorkOrderCount = unprocessedWorkOrderCount;
   }

   public void setInvoicableOrderCount(final Long invoicableOrderCount) {
      this.invoicableOrderCount = invoicableOrderCount;
   }

   public void setInvoicableOrderAmount(final BigDecimal invoicableOrderAmount) {
      this.invoicableOrderAmount = invoicableOrderAmount;
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

   public void setDataScopeList(final List<SaBaseClientLoginUser.DataScope> dataScopeList) {
      this.dataScopeList = dataScopeList;
   }

   @JsonIgnore
   public void setPassword(final String password) {
      this.password = password;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SaBaseClientLoginUser other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$id = this.getId();
         Object other$id = other.getId();
         if (this$id == null ? other$id == null : this$id.equals(other$id)) {
            Object this$notificationEnabled = this.getNotificationEnabled();
            Object other$notificationEnabled = other.getNotificationEnabled();
            if (this$notificationEnabled == null ? other$notificationEnabled == null : this$notificationEnabled.equals(other$notificationEnabled)) {
               Object this$bindWechat = this.getBindWechat();
               Object other$bindWechat = other.getBindWechat();
               if (this$bindWechat == null ? other$bindWechat == null : this$bindWechat.equals(other$bindWechat)) {
                  Object this$telegramBound = this.getTelegramBound();
                  Object other$telegramBound = other.getTelegramBound();
                  if (this$telegramBound == null ? other$telegramBound == null : this$telegramBound.equals(other$telegramBound)) {
                     Object this$unfinishedOrderCount = this.getUnfinishedOrderCount();
                     Object other$unfinishedOrderCount = other.getUnfinishedOrderCount();
                     if (this$unfinishedOrderCount == null ? other$unfinishedOrderCount == null : this$unfinishedOrderCount.equals(other$unfinishedOrderCount)) {
                        Object this$unprocessedWorkOrderCount = this.getUnprocessedWorkOrderCount();
                        Object other$unprocessedWorkOrderCount = other.getUnprocessedWorkOrderCount();
                        if (this$unprocessedWorkOrderCount == null
                           ? other$unprocessedWorkOrderCount == null
                           : this$unprocessedWorkOrderCount.equals(other$unprocessedWorkOrderCount)) {
                           Object this$invoicableOrderCount = this.getInvoicableOrderCount();
                           Object other$invoicableOrderCount = other.getInvoicableOrderCount();
                           if (this$invoicableOrderCount == null
                              ? other$invoicableOrderCount == null
                              : this$invoicableOrderCount.equals(other$invoicableOrderCount)) {
                              Object this$sortCode = this.getSortCode();
                              Object other$sortCode = other.getSortCode();
                              if (this$sortCode == null ? other$sortCode == null : this$sortCode.equals(other$sortCode)) {
                                 Object this$groupId = this.getGroupId();
                                 Object other$groupId = other.getGroupId();
                                 if (this$groupId == null ? other$groupId == null : this$groupId.equals(other$groupId)) {
                                    Object this$userType = this.getUserType();
                                    Object other$userType = other.getUserType();
                                    if (this$userType == null ? other$userType == null : this$userType.equals(other$userType)) {
                                       Object this$avatar = this.getAvatar();
                                       Object other$avatar = other.getAvatar();
                                       if (this$avatar == null ? other$avatar == null : this$avatar.equals(other$avatar)) {
                                          Object this$signature = this.getSignature();
                                          Object other$signature = other.getSignature();
                                          if (this$signature == null ? other$signature == null : this$signature.equals(other$signature)) {
                                             Object this$account = this.getAccount();
                                             Object other$account = other.getAccount();
                                             if (this$account == null ? other$account == null : this$account.equals(other$account)) {
                                                Object this$name = this.getName();
                                                Object other$name = other.getName();
                                                if (this$name == null ? other$name == null : this$name.equals(other$name)) {
                                                   Object this$nickname = this.getNickname();
                                                   Object other$nickname = other.getNickname();
                                                   if (this$nickname == null ? other$nickname == null : this$nickname.equals(other$nickname)) {
                                                      Object this$gender = this.getGender();
                                                      Object other$gender = other.getGender();
                                                      if (this$gender == null ? other$gender == null : this$gender.equals(other$gender)) {
                                                         Object this$age = this.getAge();
                                                         Object other$age = other.getAge();
                                                         if (this$age == null ? other$age == null : this$age.equals(other$age)) {
                                                            Object this$birthday = this.getBirthday();
                                                            Object other$birthday = other.getBirthday();
                                                            if (this$birthday == null ? other$birthday == null : this$birthday.equals(other$birthday)) {
                                                               Object this$nation = this.getNation();
                                                               Object other$nation = other.getNation();
                                                               if (this$nation == null ? other$nation == null : this$nation.equals(other$nation)) {
                                                                  Object this$nativePlace = this.getNativePlace();
                                                                  Object other$nativePlace = other.getNativePlace();
                                                                  if (this$nativePlace == null
                                                                     ? other$nativePlace == null
                                                                     : this$nativePlace.equals(other$nativePlace)) {
                                                                     Object this$homeAddress = this.getHomeAddress();
                                                                     Object other$homeAddress = other.getHomeAddress();
                                                                     if (this$homeAddress == null
                                                                        ? other$homeAddress == null
                                                                        : this$homeAddress.equals(other$homeAddress)) {
                                                                        Object this$mailingAddress = this.getMailingAddress();
                                                                        Object other$mailingAddress = other.getMailingAddress();
                                                                        if (this$mailingAddress == null
                                                                           ? other$mailingAddress == null
                                                                           : this$mailingAddress.equals(other$mailingAddress)) {
                                                                           Object this$idCardType = this.getIdCardType();
                                                                           Object other$idCardType = other.getIdCardType();
                                                                           if (this$idCardType == null
                                                                              ? other$idCardType == null
                                                                              : this$idCardType.equals(other$idCardType)) {
                                                                              Object this$idCardNumber = this.getIdCardNumber();
                                                                              Object other$idCardNumber = other.getIdCardNumber();
                                                                              if (this$idCardNumber == null
                                                                                 ? other$idCardNumber == null
                                                                                 : this$idCardNumber.equals(other$idCardNumber)) {
                                                                                 Object this$cultureLevel = this.getCultureLevel();
                                                                                 Object other$cultureLevel = other.getCultureLevel();
                                                                                 if (this$cultureLevel == null
                                                                                    ? other$cultureLevel == null
                                                                                    : this$cultureLevel.equals(other$cultureLevel)) {
                                                                                    Object this$politicalOutlook = this.getPoliticalOutlook();
                                                                                    Object other$politicalOutlook = other.getPoliticalOutlook();
                                                                                    if (this$politicalOutlook == null
                                                                                       ? other$politicalOutlook == null
                                                                                       : this$politicalOutlook.equals(other$politicalOutlook)) {
                                                                                       Object this$college = this.getCollege();
                                                                                       Object other$college = other.getCollege();
                                                                                       if (this$college == null
                                                                                          ? other$college == null
                                                                                          : this$college.equals(other$college)) {
                                                                                          Object this$education = this.getEducation();
                                                                                          Object other$education = other.getEducation();
                                                                                          if (this$education == null
                                                                                             ? other$education == null
                                                                                             : this$education.equals(other$education)) {
                                                                                             Object this$eduLength = this.getEduLength();
                                                                                             Object other$eduLength = other.getEduLength();
                                                                                             if (this$eduLength == null
                                                                                                ? other$eduLength == null
                                                                                                : this$eduLength.equals(other$eduLength)) {
                                                                                                Object this$degree = this.getDegree();
                                                                                                Object other$degree = other.getDegree();
                                                                                                if (this$degree == null
                                                                                                   ? other$degree == null
                                                                                                   : this$degree.equals(other$degree)) {
                                                                                                   Object this$phone = this.getPhone();
                                                                                                   Object other$phone = other.getPhone();
                                                                                                   if (this$phone == null
                                                                                                      ? other$phone == null
                                                                                                      : this$phone.equals(other$phone)) {
                                                                                                      Object this$email = this.getEmail();
                                                                                                      Object other$email = other.getEmail();
                                                                                                      if (this$email == null
                                                                                                         ? other$email == null
                                                                                                         : this$email.equals(other$email)) {
                                                                                                         Object this$homeTel = this.getHomeTel();
                                                                                                         Object other$homeTel = other.getHomeTel();
                                                                                                         if (this$homeTel == null
                                                                                                            ? other$homeTel == null
                                                                                                            : this$homeTel.equals(other$homeTel)) {
                                                                                                            Object this$officeTel = this.getOfficeTel();
                                                                                                            Object other$officeTel = other.getOfficeTel();
                                                                                                            if (this$officeTel == null
                                                                                                               ? other$officeTel == null
                                                                                                               : this$officeTel.equals(other$officeTel)) {
                                                                                                               Object this$emergencyContact = this.getEmergencyContact();
                                                                                                               Object other$emergencyContact = other.getEmergencyContact();
                                                                                                               if (this$emergencyContact == null
                                                                                                                  ? other$emergencyContact == null
                                                                                                                  : this$emergencyContact.equals(
                                                                                                                     other$emergencyContact
                                                                                                                  )) {
                                                                                                                  Object this$emergencyPhone = this.getEmergencyPhone();
                                                                                                                  Object other$emergencyPhone = other.getEmergencyPhone();
                                                                                                                  if (this$emergencyPhone == null
                                                                                                                     ? other$emergencyPhone == null
                                                                                                                     : this$emergencyPhone.equals(
                                                                                                                        other$emergencyPhone
                                                                                                                     )) {
                                                                                                                     Object this$emergencyAddress = this.getEmergencyAddress();
                                                                                                                     Object other$emergencyAddress = other.getEmergencyAddress();
                                                                                                                     if (this$emergencyAddress == null
                                                                                                                        ? other$emergencyAddress == null
                                                                                                                        : this$emergencyAddress.equals(
                                                                                                                           other$emergencyAddress
                                                                                                                        )) {
                                                                                                                        Object this$lastLoginIp = this.getLastLoginIp();
                                                                                                                        Object other$lastLoginIp = other.getLastLoginIp();
                                                                                                                        if (this$lastLoginIp == null
                                                                                                                           ? other$lastLoginIp == null
                                                                                                                           : this$lastLoginIp.equals(
                                                                                                                              other$lastLoginIp
                                                                                                                           )) {
                                                                                                                           Object this$lastLoginAddress = this.getLastLoginAddress();
                                                                                                                           Object other$lastLoginAddress = other.getLastLoginAddress();
                                                                                                                           if (this$lastLoginAddress == null
                                                                                                                              ? other$lastLoginAddress == null
                                                                                                                              : this$lastLoginAddress.equals(
                                                                                                                                 other$lastLoginAddress
                                                                                                                              )) {
                                                                                                                              Object this$lastLoginTime = this.getLastLoginTime();
                                                                                                                              Object other$lastLoginTime = other.getLastLoginTime();
                                                                                                                              if (this$lastLoginTime == null
                                                                                                                                 ? other$lastLoginTime == null
                                                                                                                                 : this$lastLoginTime.equals(
                                                                                                                                    other$lastLoginTime
                                                                                                                                 )) {
                                                                                                                                 Object this$lastLoginDevice = this.getLastLoginDevice();
                                                                                                                                 Object other$lastLoginDevice = other.getLastLoginDevice();
                                                                                                                                 if (this$lastLoginDevice
                                                                                                                                       == null
                                                                                                                                    ? other$lastLoginDevice
                                                                                                                                       == null
                                                                                                                                    : this$lastLoginDevice.equals(
                                                                                                                                       other$lastLoginDevice
                                                                                                                                    )) {
                                                                                                                                    Object this$latestLoginIp = this.getLatestLoginIp();
                                                                                                                                    Object other$latestLoginIp = other.getLatestLoginIp();
                                                                                                                                    if (this$latestLoginIp
                                                                                                                                          == null
                                                                                                                                       ? other$latestLoginIp
                                                                                                                                          == null
                                                                                                                                       : this$latestLoginIp.equals(
                                                                                                                                          other$latestLoginIp
                                                                                                                                       )) {
                                                                                                                                       Object this$latestLoginAddress = this.getLatestLoginAddress();
                                                                                                                                       Object other$latestLoginAddress = other.getLatestLoginAddress();
                                                                                                                                       if (this$latestLoginAddress
                                                                                                                                             == null
                                                                                                                                          ? other$latestLoginAddress
                                                                                                                                             == null
                                                                                                                                          : this$latestLoginAddress.equals(
                                                                                                                                             other$latestLoginAddress
                                                                                                                                          )) {
                                                                                                                                          Object this$latestLoginTime = this.getLatestLoginTime();
                                                                                                                                          Object other$latestLoginTime = other.getLatestLoginTime();
                                                                                                                                          if (this$latestLoginTime
                                                                                                                                                == null
                                                                                                                                             ? other$latestLoginTime
                                                                                                                                                == null
                                                                                                                                             : this$latestLoginTime.equals(
                                                                                                                                                other$latestLoginTime
                                                                                                                                             )) {
                                                                                                                                             Object this$latestLoginDevice = this.getLatestLoginDevice();
                                                                                                                                             Object other$latestLoginDevice = other.getLatestLoginDevice();
                                                                                                                                             if (this$latestLoginDevice
                                                                                                                                                   == null
                                                                                                                                                ? other$latestLoginDevice
                                                                                                                                                   == null
                                                                                                                                                : this$latestLoginDevice.equals(
                                                                                                                                                   other$latestLoginDevice
                                                                                                                                                )) {
                                                                                                                                                Object this$userStatus = this.getUserStatus();
                                                                                                                                                Object other$userStatus = other.getUserStatus();
                                                                                                                                                if (this$userStatus
                                                                                                                                                      == null
                                                                                                                                                   ? other$userStatus
                                                                                                                                                      == null
                                                                                                                                                   : this$userStatus.equals(
                                                                                                                                                      other$userStatus
                                                                                                                                                   )) {
                                                                                                                                                   Object this$realNameVerified = this.getRealNameVerified();
                                                                                                                                                   Object other$realNameVerified = other.getRealNameVerified();
                                                                                                                                                   if (this$realNameVerified
                                                                                                                                                         == null
                                                                                                                                                      ? other$realNameVerified
                                                                                                                                                         == null
                                                                                                                                                      : this$realNameVerified.equals(
                                                                                                                                                         other$realNameVerified
                                                                                                                                                      )) {
                                                                                                                                                      Object this$registerTime = this.getRegisterTime();
                                                                                                                                                      Object other$registerTime = other.getRegisterTime();
                                                                                                                                                      if (this$registerTime
                                                                                                                                                            == null
                                                                                                                                                         ? other$registerTime
                                                                                                                                                            == null
                                                                                                                                                         : this$registerTime.equals(
                                                                                                                                                            other$registerTime
                                                                                                                                                         )) {
                                                                                                                                                         Object this$telegramChatId = this.getTelegramChatId();
                                                                                                                                                         Object other$telegramChatId = other.getTelegramChatId();
                                                                                                                                                         if (this$telegramChatId
                                                                                                                                                               == null
                                                                                                                                                            ? other$telegramChatId
                                                                                                                                                               == null
                                                                                                                                                            : this$telegramChatId.equals(
                                                                                                                                                               other$telegramChatId
                                                                                                                                                            )) {
                                                                                                                                                            Object this$telegramUserId = this.getTelegramUserId();
                                                                                                                                                            Object other$telegramUserId = other.getTelegramUserId();
                                                                                                                                                            if (this$telegramUserId
                                                                                                                                                                  == null
                                                                                                                                                               ? other$telegramUserId
                                                                                                                                                                  == null
                                                                                                                                                               : this$telegramUserId.equals(
                                                                                                                                                                  other$telegramUserId
                                                                                                                                                               )
                                                                                                                                                               )
                                                                                                                                                             {
                                                                                                                                                               Object this$telegramUsername = this.getTelegramUsername();
                                                                                                                                                               Object other$telegramUsername = other.getTelegramUsername();
                                                                                                                                                               if (this$telegramUsername
                                                                                                                                                                     == null
                                                                                                                                                                  ? other$telegramUsername
                                                                                                                                                                     == null
                                                                                                                                                                  : this$telegramUsername.equals(
                                                                                                                                                                     other$telegramUsername
                                                                                                                                                                  )
                                                                                                                                                                  )
                                                                                                                                                                {
                                                                                                                                                                  Object this$currentMonthConsumeAmount = this.getCurrentMonthConsumeAmount();
                                                                                                                                                                  Object other$currentMonthConsumeAmount = other.getCurrentMonthConsumeAmount();
                                                                                                                                                                  if (this$currentMonthConsumeAmount
                                                                                                                                                                        == null
                                                                                                                                                                     ? other$currentMonthConsumeAmount
                                                                                                                                                                        == null
                                                                                                                                                                     : this$currentMonthConsumeAmount.equals(
                                                                                                                                                                        other$currentMonthConsumeAmount
                                                                                                                                                                     )
                                                                                                                                                                     )
                                                                                                                                                                   {
                                                                                                                                                                     Object this$currentMonthRechargeAmount = this.getCurrentMonthRechargeAmount();
                                                                                                                                                                     Object other$currentMonthRechargeAmount = other.getCurrentMonthRechargeAmount();
                                                                                                                                                                     if (this$currentMonthRechargeAmount
                                                                                                                                                                           == null
                                                                                                                                                                        ? other$currentMonthRechargeAmount
                                                                                                                                                                           == null
                                                                                                                                                                        : this$currentMonthRechargeAmount.equals(
                                                                                                                                                                           other$currentMonthRechargeAmount
                                                                                                                                                                        )
                                                                                                                                                                        )
                                                                                                                                                                      {
                                                                                                                                                                        Object this$todayConsumeAmount = this.getTodayConsumeAmount();
                                                                                                                                                                        Object other$todayConsumeAmount = other.getTodayConsumeAmount();
                                                                                                                                                                        if (this$todayConsumeAmount
                                                                                                                                                                              == null
                                                                                                                                                                           ? other$todayConsumeAmount
                                                                                                                                                                              == null
                                                                                                                                                                           : this$todayConsumeAmount.equals(
                                                                                                                                                                              other$todayConsumeAmount
                                                                                                                                                                           )
                                                                                                                                                                           )
                                                                                                                                                                         {
                                                                                                                                                                           Object this$totalConsumeAmount = this.getTotalConsumeAmount();
                                                                                                                                                                           Object other$totalConsumeAmount = other.getTotalConsumeAmount();
                                                                                                                                                                           if (this$totalConsumeAmount
                                                                                                                                                                                 == null
                                                                                                                                                                              ? other$totalConsumeAmount
                                                                                                                                                                                 == null
                                                                                                                                                                              : this$totalConsumeAmount.equals(
                                                                                                                                                                                 other$totalConsumeAmount
                                                                                                                                                                              )
                                                                                                                                                                              )
                                                                                                                                                                            {
                                                                                                                                                                              Object this$invoicableOrderAmount = this.getInvoicableOrderAmount();
                                                                                                                                                                              Object other$invoicableOrderAmount = other.getInvoicableOrderAmount();
                                                                                                                                                                              if (this$invoicableOrderAmount
                                                                                                                                                                                    == null
                                                                                                                                                                                 ? other$invoicableOrderAmount
                                                                                                                                                                                    == null
                                                                                                                                                                                 : this$invoicableOrderAmount.equals(
                                                                                                                                                                                    other$invoicableOrderAmount
                                                                                                                                                                                 )
                                                                                                                                                                                 )
                                                                                                                                                                               {
                                                                                                                                                                                 Object this$extJson = this.getExtJson();
                                                                                                                                                                                 Object other$extJson = other.getExtJson();
                                                                                                                                                                                 if (this$extJson
                                                                                                                                                                                       == null
                                                                                                                                                                                    ? other$extJson
                                                                                                                                                                                       == null
                                                                                                                                                                                    : this$extJson.equals(
                                                                                                                                                                                       other$extJson
                                                                                                                                                                                    )
                                                                                                                                                                                    )
                                                                                                                                                                                  {
                                                                                                                                                                                    Object this$buttonCodeList = this.getButtonCodeList();
                                                                                                                                                                                    Object other$buttonCodeList = other.getButtonCodeList();
                                                                                                                                                                                    if (this$buttonCodeList
                                                                                                                                                                                          == null
                                                                                                                                                                                       ? other$buttonCodeList
                                                                                                                                                                                          == null
                                                                                                                                                                                       : this$buttonCodeList.equals(
                                                                                                                                                                                          other$buttonCodeList
                                                                                                                                                                                       )
                                                                                                                                                                                       )
                                                                                                                                                                                     {
                                                                                                                                                                                       Object this$mobileButtonCodeList = this.getMobileButtonCodeList();
                                                                                                                                                                                       Object other$mobileButtonCodeList = other.getMobileButtonCodeList();
                                                                                                                                                                                       if (this$mobileButtonCodeList
                                                                                                                                                                                             == null
                                                                                                                                                                                          ? other$mobileButtonCodeList
                                                                                                                                                                                             == null
                                                                                                                                                                                          : this$mobileButtonCodeList.equals(
                                                                                                                                                                                             other$mobileButtonCodeList
                                                                                                                                                                                          )
                                                                                                                                                                                          )
                                                                                                                                                                                        {
                                                                                                                                                                                          Object this$permissionCodeList = this.getPermissionCodeList();
                                                                                                                                                                                          Object other$permissionCodeList = other.getPermissionCodeList();
                                                                                                                                                                                          if (this$permissionCodeList
                                                                                                                                                                                                == null
                                                                                                                                                                                             ? other$permissionCodeList
                                                                                                                                                                                                == null
                                                                                                                                                                                             : this$permissionCodeList.equals(
                                                                                                                                                                                                other$permissionCodeList
                                                                                                                                                                                             )
                                                                                                                                                                                             )
                                                                                                                                                                                           {
                                                                                                                                                                                             Object this$roleCodeList = this.getRoleCodeList();
                                                                                                                                                                                             Object other$roleCodeList = other.getRoleCodeList();
                                                                                                                                                                                             if (this$roleCodeList
                                                                                                                                                                                                   == null
                                                                                                                                                                                                ? other$roleCodeList
                                                                                                                                                                                                   == null
                                                                                                                                                                                                : this$roleCodeList.equals(
                                                                                                                                                                                                   other$roleCodeList
                                                                                                                                                                                                )
                                                                                                                                                                                                )
                                                                                                                                                                                              {
                                                                                                                                                                                                Object this$dataScopeList = this.getDataScopeList();
                                                                                                                                                                                                Object other$dataScopeList = other.getDataScopeList();
                                                                                                                                                                                                if (this$dataScopeList
                                                                                                                                                                                                      == null
                                                                                                                                                                                                   ? other$dataScopeList
                                                                                                                                                                                                      == null
                                                                                                                                                                                                   : this$dataScopeList.equals(
                                                                                                                                                                                                      other$dataScopeList
                                                                                                                                                                                                   )
                                                                                                                                                                                                   )
                                                                                                                                                                                                 {
                                                                                                                                                                                                   Object this$password = this.getPassword();
                                                                                                                                                                                                   Object other$password = other.getPassword();
                                                                                                                                                                                                   return this$password
                                                                                                                                                                                                         == null
                                                                                                                                                                                                      ? other$password
                                                                                                                                                                                                         == null
                                                                                                                                                                                                      : this$password.equals(
                                                                                                                                                                                                         other$password
                                                                                                                                                                                                      );
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
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof SaBaseClientLoginUser;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $notificationEnabled = this.getNotificationEnabled();
      result = result * 59 + ($notificationEnabled == null ? 43 : $notificationEnabled.hashCode());
      Object $bindWechat = this.getBindWechat();
      result = result * 59 + ($bindWechat == null ? 43 : $bindWechat.hashCode());
      Object $telegramBound = this.getTelegramBound();
      result = result * 59 + ($telegramBound == null ? 43 : $telegramBound.hashCode());
      Object $unfinishedOrderCount = this.getUnfinishedOrderCount();
      result = result * 59 + ($unfinishedOrderCount == null ? 43 : $unfinishedOrderCount.hashCode());
      Object $unprocessedWorkOrderCount = this.getUnprocessedWorkOrderCount();
      result = result * 59 + ($unprocessedWorkOrderCount == null ? 43 : $unprocessedWorkOrderCount.hashCode());
      Object $invoicableOrderCount = this.getInvoicableOrderCount();
      result = result * 59 + ($invoicableOrderCount == null ? 43 : $invoicableOrderCount.hashCode());
      Object $sortCode = this.getSortCode();
      result = result * 59 + ($sortCode == null ? 43 : $sortCode.hashCode());
      Object $groupId = this.getGroupId();
      result = result * 59 + ($groupId == null ? 43 : $groupId.hashCode());
      Object $userType = this.getUserType();
      result = result * 59 + ($userType == null ? 43 : $userType.hashCode());
      Object $avatar = this.getAvatar();
      result = result * 59 + ($avatar == null ? 43 : $avatar.hashCode());
      Object $signature = this.getSignature();
      result = result * 59 + ($signature == null ? 43 : $signature.hashCode());
      Object $account = this.getAccount();
      result = result * 59 + ($account == null ? 43 : $account.hashCode());
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $nickname = this.getNickname();
      result = result * 59 + ($nickname == null ? 43 : $nickname.hashCode());
      Object $gender = this.getGender();
      result = result * 59 + ($gender == null ? 43 : $gender.hashCode());
      Object $age = this.getAge();
      result = result * 59 + ($age == null ? 43 : $age.hashCode());
      Object $birthday = this.getBirthday();
      result = result * 59 + ($birthday == null ? 43 : $birthday.hashCode());
      Object $nation = this.getNation();
      result = result * 59 + ($nation == null ? 43 : $nation.hashCode());
      Object $nativePlace = this.getNativePlace();
      result = result * 59 + ($nativePlace == null ? 43 : $nativePlace.hashCode());
      Object $homeAddress = this.getHomeAddress();
      result = result * 59 + ($homeAddress == null ? 43 : $homeAddress.hashCode());
      Object $mailingAddress = this.getMailingAddress();
      result = result * 59 + ($mailingAddress == null ? 43 : $mailingAddress.hashCode());
      Object $idCardType = this.getIdCardType();
      result = result * 59 + ($idCardType == null ? 43 : $idCardType.hashCode());
      Object $idCardNumber = this.getIdCardNumber();
      result = result * 59 + ($idCardNumber == null ? 43 : $idCardNumber.hashCode());
      Object $cultureLevel = this.getCultureLevel();
      result = result * 59 + ($cultureLevel == null ? 43 : $cultureLevel.hashCode());
      Object $politicalOutlook = this.getPoliticalOutlook();
      result = result * 59 + ($politicalOutlook == null ? 43 : $politicalOutlook.hashCode());
      Object $college = this.getCollege();
      result = result * 59 + ($college == null ? 43 : $college.hashCode());
      Object $education = this.getEducation();
      result = result * 59 + ($education == null ? 43 : $education.hashCode());
      Object $eduLength = this.getEduLength();
      result = result * 59 + ($eduLength == null ? 43 : $eduLength.hashCode());
      Object $degree = this.getDegree();
      result = result * 59 + ($degree == null ? 43 : $degree.hashCode());
      Object $phone = this.getPhone();
      result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
      Object $email = this.getEmail();
      result = result * 59 + ($email == null ? 43 : $email.hashCode());
      Object $homeTel = this.getHomeTel();
      result = result * 59 + ($homeTel == null ? 43 : $homeTel.hashCode());
      Object $officeTel = this.getOfficeTel();
      result = result * 59 + ($officeTel == null ? 43 : $officeTel.hashCode());
      Object $emergencyContact = this.getEmergencyContact();
      result = result * 59 + ($emergencyContact == null ? 43 : $emergencyContact.hashCode());
      Object $emergencyPhone = this.getEmergencyPhone();
      result = result * 59 + ($emergencyPhone == null ? 43 : $emergencyPhone.hashCode());
      Object $emergencyAddress = this.getEmergencyAddress();
      result = result * 59 + ($emergencyAddress == null ? 43 : $emergencyAddress.hashCode());
      Object $lastLoginIp = this.getLastLoginIp();
      result = result * 59 + ($lastLoginIp == null ? 43 : $lastLoginIp.hashCode());
      Object $lastLoginAddress = this.getLastLoginAddress();
      result = result * 59 + ($lastLoginAddress == null ? 43 : $lastLoginAddress.hashCode());
      Object $lastLoginTime = this.getLastLoginTime();
      result = result * 59 + ($lastLoginTime == null ? 43 : $lastLoginTime.hashCode());
      Object $lastLoginDevice = this.getLastLoginDevice();
      result = result * 59 + ($lastLoginDevice == null ? 43 : $lastLoginDevice.hashCode());
      Object $latestLoginIp = this.getLatestLoginIp();
      result = result * 59 + ($latestLoginIp == null ? 43 : $latestLoginIp.hashCode());
      Object $latestLoginAddress = this.getLatestLoginAddress();
      result = result * 59 + ($latestLoginAddress == null ? 43 : $latestLoginAddress.hashCode());
      Object $latestLoginTime = this.getLatestLoginTime();
      result = result * 59 + ($latestLoginTime == null ? 43 : $latestLoginTime.hashCode());
      Object $latestLoginDevice = this.getLatestLoginDevice();
      result = result * 59 + ($latestLoginDevice == null ? 43 : $latestLoginDevice.hashCode());
      Object $userStatus = this.getUserStatus();
      result = result * 59 + ($userStatus == null ? 43 : $userStatus.hashCode());
      Object $realNameVerified = this.getRealNameVerified();
      result = result * 59 + ($realNameVerified == null ? 43 : $realNameVerified.hashCode());
      Object $registerTime = this.getRegisterTime();
      result = result * 59 + ($registerTime == null ? 43 : $registerTime.hashCode());
      Object $telegramChatId = this.getTelegramChatId();
      result = result * 59 + ($telegramChatId == null ? 43 : $telegramChatId.hashCode());
      Object $telegramUserId = this.getTelegramUserId();
      result = result * 59 + ($telegramUserId == null ? 43 : $telegramUserId.hashCode());
      Object $telegramUsername = this.getTelegramUsername();
      result = result * 59 + ($telegramUsername == null ? 43 : $telegramUsername.hashCode());
      Object $currentMonthConsumeAmount = this.getCurrentMonthConsumeAmount();
      result = result * 59 + ($currentMonthConsumeAmount == null ? 43 : $currentMonthConsumeAmount.hashCode());
      Object $currentMonthRechargeAmount = this.getCurrentMonthRechargeAmount();
      result = result * 59 + ($currentMonthRechargeAmount == null ? 43 : $currentMonthRechargeAmount.hashCode());
      Object $todayConsumeAmount = this.getTodayConsumeAmount();
      result = result * 59 + ($todayConsumeAmount == null ? 43 : $todayConsumeAmount.hashCode());
      Object $totalConsumeAmount = this.getTotalConsumeAmount();
      result = result * 59 + ($totalConsumeAmount == null ? 43 : $totalConsumeAmount.hashCode());
      Object $invoicableOrderAmount = this.getInvoicableOrderAmount();
      result = result * 59 + ($invoicableOrderAmount == null ? 43 : $invoicableOrderAmount.hashCode());
      Object $extJson = this.getExtJson();
      result = result * 59 + ($extJson == null ? 43 : $extJson.hashCode());
      Object $buttonCodeList = this.getButtonCodeList();
      result = result * 59 + ($buttonCodeList == null ? 43 : $buttonCodeList.hashCode());
      Object $mobileButtonCodeList = this.getMobileButtonCodeList();
      result = result * 59 + ($mobileButtonCodeList == null ? 43 : $mobileButtonCodeList.hashCode());
      Object $permissionCodeList = this.getPermissionCodeList();
      result = result * 59 + ($permissionCodeList == null ? 43 : $permissionCodeList.hashCode());
      Object $roleCodeList = this.getRoleCodeList();
      result = result * 59 + ($roleCodeList == null ? 43 : $roleCodeList.hashCode());
      Object $dataScopeList = this.getDataScopeList();
      result = result * 59 + ($dataScopeList == null ? 43 : $dataScopeList.hashCode());
      Object $password = this.getPassword();
      return result * 59 + ($password == null ? 43 : $password.hashCode());
   }

   @Override
   public String toString() {
      return "SaBaseClientLoginUser(id="
         + this.getId()
         + ", groupId="
         + this.getGroupId()
         + ", userType="
         + this.getUserType()
         + ", avatar="
         + this.getAvatar()
         + ", signature="
         + this.getSignature()
         + ", account="
         + this.getAccount()
         + ", name="
         + this.getName()
         + ", nickname="
         + this.getNickname()
         + ", gender="
         + this.getGender()
         + ", age="
         + this.getAge()
         + ", birthday="
         + this.getBirthday()
         + ", nation="
         + this.getNation()
         + ", nativePlace="
         + this.getNativePlace()
         + ", homeAddress="
         + this.getHomeAddress()
         + ", mailingAddress="
         + this.getMailingAddress()
         + ", idCardType="
         + this.getIdCardType()
         + ", idCardNumber="
         + this.getIdCardNumber()
         + ", cultureLevel="
         + this.getCultureLevel()
         + ", politicalOutlook="
         + this.getPoliticalOutlook()
         + ", college="
         + this.getCollege()
         + ", education="
         + this.getEducation()
         + ", eduLength="
         + this.getEduLength()
         + ", degree="
         + this.getDegree()
         + ", phone="
         + this.getPhone()
         + ", email="
         + this.getEmail()
         + ", notificationEnabled="
         + this.getNotificationEnabled()
         + ", homeTel="
         + this.getHomeTel()
         + ", officeTel="
         + this.getOfficeTel()
         + ", emergencyContact="
         + this.getEmergencyContact()
         + ", emergencyPhone="
         + this.getEmergencyPhone()
         + ", emergencyAddress="
         + this.getEmergencyAddress()
         + ", lastLoginIp="
         + this.getLastLoginIp()
         + ", lastLoginAddress="
         + this.getLastLoginAddress()
         + ", lastLoginTime="
         + this.getLastLoginTime()
         + ", lastLoginDevice="
         + this.getLastLoginDevice()
         + ", latestLoginIp="
         + this.getLatestLoginIp()
         + ", latestLoginAddress="
         + this.getLatestLoginAddress()
         + ", latestLoginTime="
         + this.getLatestLoginTime()
         + ", latestLoginDevice="
         + this.getLatestLoginDevice()
         + ", userStatus="
         + this.getUserStatus()
         + ", realNameVerified="
         + this.getRealNameVerified()
         + ", registerTime="
         + this.getRegisterTime()
         + ", bindWechat="
         + this.getBindWechat()
         + ", telegramBound="
         + this.getTelegramBound()
         + ", telegramChatId="
         + this.getTelegramChatId()
         + ", telegramUserId="
         + this.getTelegramUserId()
         + ", telegramUsername="
         + this.getTelegramUsername()
         + ", currentMonthConsumeAmount="
         + this.getCurrentMonthConsumeAmount()
         + ", currentMonthRechargeAmount="
         + this.getCurrentMonthRechargeAmount()
         + ", todayConsumeAmount="
         + this.getTodayConsumeAmount()
         + ", totalConsumeAmount="
         + this.getTotalConsumeAmount()
         + ", unfinishedOrderCount="
         + this.getUnfinishedOrderCount()
         + ", unprocessedWorkOrderCount="
         + this.getUnprocessedWorkOrderCount()
         + ", invoicableOrderCount="
         + this.getInvoicableOrderCount()
         + ", invoicableOrderAmount="
         + this.getInvoicableOrderAmount()
         + ", sortCode="
         + this.getSortCode()
         + ", extJson="
         + this.getExtJson()
         + ", buttonCodeList="
         + this.getButtonCodeList()
         + ", mobileButtonCodeList="
         + this.getMobileButtonCodeList()
         + ", permissionCodeList="
         + this.getPermissionCodeList()
         + ", roleCodeList="
         + this.getRoleCodeList()
         + ", dataScopeList="
         + this.getDataScopeList()
         + ", password="
         + this.getPassword()
         + ")";
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
