package com.rxinyun.admin.biz.modular.user.result;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import java.util.Date;

public class BizUserExportResult {
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 40
   )
   @ExcelProperty({"人员信息", "机构分组"})
   private String groupName;
   @ColumnWidth(8)
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "头像"})
   private byte[] avatar;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "账号"})
   private String account;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "姓名"})
   private String name;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "昵称"})
   private String nickname;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "性别"})
   private String gender;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "年龄"})
   private String age;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "出生日期"})
   private String birthday;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "民族"})
   private String nation;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "籍贯"})
   private String nativePlace;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "家庭住址"})
   private String homeAddress;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "通信地址"})
   private String mailingAddress;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "证件类型"})
   private String idCardType;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "证件号码"})
   private String idCardNumber;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "文化程度"})
   private String cultureLevel;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "政治面貌"})
   private String politicalOutlook;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "毕业院校"})
   private String college;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "学历"})
   private String education;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "学制"})
   private String eduLength;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "学位"})
   private String degree;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "手机"})
   private String phone;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "邮箱"})
   private String email;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "家庭电话"})
   private String homeTel;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "办公电话"})
   private String officeTel;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "紧急联系人"})
   private String emergencyContact;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "紧急联系人电话"})
   private String emergencyPhone;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 29
   )
   @ExcelProperty({"人员信息", "基本信息", "紧急联系人地址"})
   private String emergencyAddress;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 34
   )
   @ExcelProperty({"人员信息", "员工信息", "员工编号"})
   private String empNo;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 34
   )
   @ExcelProperty({"人员信息", "员工信息", "入职日期"})
   private String entryDate;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 34
   )
   @ExcelProperty({"人员信息", "员工信息", "组织名称"})
   private String orgName;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 34
   )
   @ExcelProperty({"人员信息", "员工信息", "职位名称"})
   private String positionName;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 34
   )
   @ExcelProperty({"人员信息", "员工信息", "主管名称"})
   private String directorName;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 34
   )
   @ExcelProperty({"人员信息", "员工信息", "职级"})
   private String positionLevel;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 42
   )
   @ExcelProperty({"人员信息", "系统信息", "上次登录ip"})
   private String lastLoginIp;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 42
   )
   @ExcelProperty({"人员信息", "系统信息", "上次登录地点"})
   private String lastLoginAddress;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 42
   )
   @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
   @ExcelProperty({"人员信息", "系统信息", "上次登录时间"})
   private Date lastLoginTime;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 42
   )
   @ExcelProperty({"人员信息", "系统信息", "上次登录设备"})
   private String lastLoginDevice;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 42
   )
   @ExcelProperty({"人员信息", "系统信息", "最新登录ip"})
   private String latestLoginIp;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 42
   )
   @ExcelProperty({"人员信息", "系统信息", "最新登录地点"})
   private String latestLoginAddress;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 42
   )
   @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
   @ExcelProperty({"人员信息", "系统信息", "最新登录时间"})
   private Date latestLoginTime;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 42
   )
   @ExcelProperty({"人员信息", "系统信息", "最新登录设备"})
   private String latestLoginDevice;
   @HeadStyle(
      fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
      fillForegroundColor = 42
   )
   @ExcelProperty({"人员信息", "系统信息", "人员状态"})
   private String userStatus;

   public String getGroupName() {
      return this.groupName;
   }

   public byte[] getAvatar() {
      return this.avatar;
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

   public String getDirectorName() {
      return this.directorName;
   }

   public String getPositionLevel() {
      return this.positionLevel;
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

   public void setGroupName(final String groupName) {
      this.groupName = groupName;
   }

   public void setAvatar(final byte[] avatar) {
      this.avatar = avatar;
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

   public void setDirectorName(final String directorName) {
      this.directorName = directorName;
   }

   public void setPositionLevel(final String positionLevel) {
      this.positionLevel = positionLevel;
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
}
