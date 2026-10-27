package com.rxinyun.admin.system.modular.user.service.impl;

import cn.afterturn.easypoi.cache.manager.POICacheManager;
import cn.afterturn.easypoi.entity.ImageEntity;
import cn.afterturn.easypoi.word.WordExportUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.DefaultNodeParser;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.AbstractRowHeightStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.system.core.enums.SysBuildInEnum;
import com.rxinyun.admin.system.core.enums.SysDataTypeEnum;
import com.rxinyun.admin.system.modular.org.entity.SysOrg;
import com.rxinyun.admin.system.modular.org.service.SysOrgService;
import com.rxinyun.admin.system.modular.position.entity.SysPosition;
import com.rxinyun.admin.system.modular.position.service.SysPositionService;
import com.rxinyun.admin.system.modular.relation.entity.SysRelation;
import com.rxinyun.admin.system.modular.relation.enums.SysRelationCategoryEnum;
import com.rxinyun.admin.system.modular.relation.service.SysRelationService;
import com.rxinyun.admin.system.modular.resource.entity.SysButton;
import com.rxinyun.admin.system.modular.resource.entity.SysMenu;
import com.rxinyun.admin.system.modular.resource.enums.SysResourceCategoryEnum;
import com.rxinyun.admin.system.modular.resource.enums.SysResourceMenuTypeEnum;
import com.rxinyun.admin.system.modular.resource.service.SysButtonService;
import com.rxinyun.admin.system.modular.resource.service.SysMenuService;
import com.rxinyun.admin.system.modular.role.entity.SysRole;
import com.rxinyun.admin.system.modular.role.enums.SysRoleCategoryEnum;
import com.rxinyun.admin.system.modular.role.service.SysRoleService;
import com.rxinyun.admin.system.modular.user.entity.SysUser;
import com.rxinyun.admin.system.modular.user.enums.SysUserStatusEnum;
import com.rxinyun.admin.system.modular.user.mapper.SysUserMapper;
import com.rxinyun.admin.system.modular.user.param.SysUserAddParam;
import com.rxinyun.admin.system.modular.user.param.SysUserAdminUpdatePwdParam;
import com.rxinyun.admin.system.modular.user.param.SysUserEditParam;
import com.rxinyun.admin.system.modular.user.param.SysUserExportParam;
import com.rxinyun.admin.system.modular.user.param.SysUserFindPwdByEmailParam;
import com.rxinyun.admin.system.modular.user.param.SysUserFindPwdByPhoneParam;
import com.rxinyun.admin.system.modular.user.param.SysUserGetEmailValidCodeParam;
import com.rxinyun.admin.system.modular.user.param.SysUserGetPhoneValidCodeParam;
import com.rxinyun.admin.system.modular.user.param.SysUserGrantPermissionParam;
import com.rxinyun.admin.system.modular.user.param.SysUserGrantResourceParam;
import com.rxinyun.admin.system.modular.user.param.SysUserGrantRoleParam;
import com.rxinyun.admin.system.modular.user.param.SysUserIdListParam;
import com.rxinyun.admin.system.modular.user.param.SysUserIdParam;
import com.rxinyun.admin.system.modular.user.param.SysUserImportParam;
import com.rxinyun.admin.system.modular.user.param.SysUserMessageIdParam;
import com.rxinyun.admin.system.modular.user.param.SysUserMessagePageParam;
import com.rxinyun.admin.system.modular.user.param.SysUserPageParam;
import com.rxinyun.admin.system.modular.user.param.SysUserResetPasswordParam;
import com.rxinyun.admin.system.modular.user.param.SysUserSelectorOrgListParam;
import com.rxinyun.admin.system.modular.user.param.SysUserSelectorPositionParam;
import com.rxinyun.admin.system.modular.user.param.SysUserSelectorRoleParam;
import com.rxinyun.admin.system.modular.user.param.SysUserSelectorUserParam;
import com.rxinyun.admin.system.modular.user.param.SysUserSignatureParam;
import com.rxinyun.admin.system.modular.user.param.SysUserUpdateInfoParam;
import com.rxinyun.admin.system.modular.user.param.SysUserUpdatePwdParam;
import com.rxinyun.admin.system.modular.user.param.SysUserUpdateWorkbenchParam;
import com.rxinyun.admin.system.modular.user.result.SysLoginUser;
import com.rxinyun.admin.system.modular.user.result.SysUserExportResult;
import com.rxinyun.admin.system.modular.user.result.SysUserMessageDetailResult;
import com.rxinyun.admin.system.modular.user.result.SysUserMessageResult;
import com.rxinyun.admin.system.modular.user.result.SysUserOwnPermissionResult;
import com.rxinyun.admin.system.modular.user.result.SysUserOwnResourceResult;
import com.rxinyun.admin.system.modular.user.result.SysUserPicValidCodeResult;
import com.rxinyun.admin.system.modular.user.result.SysUserPositionResult;
import com.rxinyun.admin.system.modular.user.service.SysUserService;
import com.rxinyun.auth.core.util.StpLoginUserUtil;
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.excel.CommonExcelCustomMergeStrategy;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.listener.CommonDataChangeEventCenter;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.util.CommonAvatarUtil;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.common.util.CommonDownloadUtil;
import com.rxinyun.common.util.CommonEmailUtil;
import com.rxinyun.common.util.CommonIpAddressUtil;
import com.rxinyun.common.util.CommonResponseUtil;
import com.rxinyun.common.util.CommonServletUtil;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.dev.api.DevEmailApi;
import com.rxinyun.dev.api.DevMessageApi;
import com.rxinyun.dev.api.DevSmsApi;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
   private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);
   private static final String RUNXIN_SYS_DEFAULT_PASSWORD_KEY = "RUNXIN_SYS_DEFAULT_PASSWORD";
   private static final String RUNXIN_SYS_DEFAULT_WORKBENCH_DATA_KEY = "RUNXIN_SYS_DEFAULT_WORKBENCH_DATA";
   private static final String USER_VALID_CODE_CACHE_KEY = "user-validCode:";
   public static final String USER_CACHE_ALL_KEY = "sys-user:all";
   @Resource
   private CommonCacheOperator commonCacheOperator;
   @Resource
   private DevSmsApi devSmsApi;
   @Resource
   private DevEmailApi devEmailApi;
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private DevMessageApi devMessageApi;
   @Resource
   private SysOrgService sysOrgService;
   @Resource
   private SysPositionService sysPositionService;
   @Resource
   private SysRoleService sysRoleService;
   @Resource
   private SysMenuService sysMenuService;
   @Resource
   private SysButtonService sysButtonService;
   @Resource
   private SysRelationService sysRelationService;

   @Override
   public SysLoginUser getUserById(String id) {
      SysUser sysUser = (SysUser)this.getById(id);
      return ObjectUtil.isNotEmpty(sysUser) ? (SysLoginUser)BeanUtil.copyProperties(sysUser, SysLoginUser.class, new String[0]) : null;
   }

   @Override
   public SysLoginUser getUserByAccount(String account) {
      SysUser sysUser = (SysUser)this.getOne((Wrapper<SysUser>)new LambdaQueryWrapper<SysUser>().eq(SysUser::getAccount, account));
      return ObjectUtil.isNotEmpty(sysUser) ? (SysLoginUser)BeanUtil.copyProperties(sysUser, SysLoginUser.class, new String[0]) : null;
   }

   @Override
   public SysLoginUser getUserByPhone(String phone) {
      SysUser sysUser = (SysUser)this.getOne((Wrapper<SysUser>)new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(phone)));
      return ObjectUtil.isNotEmpty(sysUser) ? (SysLoginUser)BeanUtil.copyProperties(sysUser, SysLoginUser.class, new String[0]) : null;
   }

   @Override
   public SysLoginUser getUserByEmail(String email) {
      SysUser sysUser = (SysUser)this.getOne((Wrapper<SysUser>)new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, email));
      return ObjectUtil.isNotEmpty(sysUser) ? (SysLoginUser)BeanUtil.copyProperties(sysUser, SysLoginUser.class, new String[0]) : null;
   }

   @Override
   public Page<SysUser> page(SysUserPageParam sysUserPageParam) {
      QueryWrapper<SysUser> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(sysUserPageParam.getSearchKey())) {
         queryWrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<SysUser>)((LambdaQueryWrapper<SysUser>)q.like(SysUser::getAccount, sysUserPageParam.getSearchKey())).or())
                  .like(SysUser::getName, sysUserPageParam.getSearchKey())
            );
      }

      if (ObjectUtil.isNotEmpty(sysUserPageParam.getUserStatus())) {
         queryWrapper.lambda().eq(SysUser::getUserStatus, sysUserPageParam.getUserStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{sysUserPageParam.getSortField(), sysUserPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(sysUserPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, sysUserPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(sysUserPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(SysUser::getSortCode);
      }

      return (Page<SysUser>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(SysUserAddParam sysUserAddParam) {
      this.checkParam(sysUserAddParam);
      SysUser sysUser = (SysUser)BeanUtil.toBean(sysUserAddParam, SysUser.class);
      if (ObjectUtil.isEmpty(sysUser.getAvatar())) {
         sysUser.setAvatar(CommonAvatarUtil.generateImg(sysUser.getName()));
      }

      sysUser.setPassword(CommonCryptogramUtil.doHashValue(this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_PASSWORD")));
      sysUser.setUserStatus(SysUserStatusEnum.ENABLE.getValue());
      this.save(sysUser);
      CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.USER.getValue(), JSONUtil.createArray().put(sysUser));
   }

   private void checkParam(SysUserAddParam sysUserAddParam) {
      if (this.count((Wrapper<SysUser>)new LambdaQueryWrapper<SysUser>().eq(SysUser::getAccount, sysUserAddParam.getAccount())) > 0L) {
         throw new CommonException("存在重复的账号，账号为：{}", new Object[]{sysUserAddParam.getAccount()});
      } else {
         if (ObjectUtil.isNotEmpty(sysUserAddParam.getPhone())) {
            if (!PhoneUtil.isMobile(sysUserAddParam.getPhone())) {
               throw new CommonException("手机号码：{}格式错误", new Object[]{sysUserAddParam.getPhone()});
            }

            if (this.count((Wrapper<SysUser>)new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(sysUserAddParam.getPhone()))) > 0L) {
               throw new CommonException("存在重复的手机号，手机号为：{}", new Object[]{sysUserAddParam.getPhone()});
            }
         }

         if (ObjectUtil.isNotEmpty(sysUserAddParam.getEmail())) {
            if (!CommonEmailUtil.isEmail(sysUserAddParam.getEmail())) {
               throw new CommonException("邮箱：{}格式错误", new Object[]{sysUserAddParam.getEmail()});
            }

            if (this.count((Wrapper<SysUser>)new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, sysUserAddParam.getEmail())) > 0L) {
               throw new CommonException("存在重复的邮箱，邮箱为：{}", new Object[]{sysUserAddParam.getEmail()});
            }
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(SysUserEditParam sysUserEditParam) {
      SysUser sysUser = this.queryEntity(sysUserEditParam.getId());
      this.checkParam(sysUserEditParam);
      boolean updateSuperAdminAccount = sysUser.getAccount().equals(SysBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue())
         && !sysUserEditParam.getAccount().equals(SysBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue());
      if (updateSuperAdminAccount) {
         throw new CommonException("不可修改系统内置超管用户账号", new Object[0]);
      } else {
         BeanUtil.copyProperties(sysUserEditParam, sysUser, new String[0]);
         this.updateById(sysUser);
         CommonDataChangeEventCenter.doUpdateWithData(SysDataTypeEnum.USER.getValue(), JSONUtil.createArray().put(sysUser));
      }
   }

   private void checkParam(SysUserEditParam sysUserEditParam) {
      if (this.count(
            (Wrapper<SysUser>)((LambdaQueryWrapper<SysUser>)new LambdaQueryWrapper<SysUser>().eq(SysUser::getAccount, sysUserEditParam.getAccount()))
               .ne(SysUser::getId, sysUserEditParam.getId())
         )
         > 0L) {
         throw new CommonException("存在重复的账号，账号为：{}", new Object[]{sysUserEditParam.getAccount()});
      } else {
         if (ObjectUtil.isNotEmpty(sysUserEditParam.getPhone())) {
            if (!PhoneUtil.isMobile(sysUserEditParam.getPhone())) {
               throw new CommonException("手机号码：{}格式错误", new Object[]{sysUserEditParam.getPhone()});
            }

            if (this.count(
                  (Wrapper<SysUser>)((LambdaQueryWrapper<SysUser>)new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(sysUserEditParam.getPhone())))
                     .ne(SysUser::getId, sysUserEditParam.getId())
               )
               > 0L) {
               throw new CommonException("存在重复的手机号，手机号为：{}", new Object[]{sysUserEditParam.getPhone()});
            }
         }

         if (ObjectUtil.isNotEmpty(sysUserEditParam.getEmail())) {
            if (!CommonEmailUtil.isEmail(sysUserEditParam.getEmail())) {
               throw new CommonException("邮箱：{}格式错误", new Object[]{sysUserEditParam.getEmail()});
            }

            if (this.count(
                  (Wrapper<SysUser>)((LambdaQueryWrapper<SysUser>)new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, sysUserEditParam.getEmail()))
                     .ne(SysUser::getId, sysUserEditParam.getId())
               )
               > 0L) {
               throw new CommonException("存在重复的邮箱，邮箱为：{}", new Object[]{sysUserEditParam.getEmail()});
            }
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<SysUserIdParam> sysUserIdParamList) {
      List<String> sysUserIdList = CollStreamUtil.toList(sysUserIdParamList, SysUserIdParam::getId);
      if (ObjectUtil.isNotEmpty(sysUserIdList)) {
         boolean containsSuperAdminAccount = this.listByIds(sysUserIdList)
            .stream()
            .map(SysUser::getAccount)
            .collect(Collectors.toSet())
            .contains(SysBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue());
         if (containsSuperAdminAccount) {
            throw new CommonException("不可删除系统内置超管用户", new Object[0]);
         }

         this.update((Wrapper<SysUser>)((LambdaUpdateWrapper<SysUser>)new LambdaUpdateWrapper<SysUser>().in(SysUser::getDirectorId, sysUserIdList)).set(SysUser::getDirectorId, null));
         this.list((Wrapper<SysUser>)new LambdaQueryWrapper<SysUser>().isNotNull(SysUser::getPositionJson))
            .forEach(
               sysUser -> {
                  List<JSONObject> handledJsonObjectList = JSONUtil.toList(JSONUtil.parseArray(sysUser.getPositionJson()), JSONObject.class)
                     .stream()
                     .peek(jsonObject -> {
                        String directorId = jsonObject.getStr("directorId");
                        if (ObjectUtil.isNotEmpty(directorId) && sysUserIdList.contains(directorId)) {
                           jsonObject.remove("directorId");
                        }
                     })
                     .collect(Collectors.toList());
                  this.update(
                     (Wrapper<SysUser>)((LambdaUpdateWrapper<SysUser>)new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, sysUser.getId()))
                        .set(SysUser::getPositionJson, JSONUtil.toJsonStr(handledJsonObjectList))
                  );
               }
            );
         this.sysOrgService
            .update((Wrapper<SysOrg>)((LambdaUpdateWrapper<SysOrg>)new LambdaUpdateWrapper<SysOrg>().in(SysOrg::getDirectorId, sysUserIdList)).set(SysOrg::getDirectorId, null));
         this.removeByIds(sysUserIdList);
         CommonDataChangeEventCenter.doDeleteWithDataId(SysDataTypeEnum.USER.getValue(), sysUserIdList);
      }
   }

   @Override
   public SysUser detail(SysUserIdParam sysUserIdParam) {
      return this.queryEntity(sysUserIdParam.getId());
   }

   @Override
   public void disableUser(SysUserIdParam sysUserIdParam) {
      this.update(
         (Wrapper<SysUser>)((LambdaUpdateWrapper<SysUser>)new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, sysUserIdParam.getId()))
            .set(SysUser::getUserStatus, SysUserStatusEnum.DISABLED.getValue())
      );
   }

   @Override
   public void enableUser(SysUserIdParam sysUserIdParam) {
      this.update(
         (Wrapper<SysUser>)((LambdaUpdateWrapper<SysUser>)new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, sysUserIdParam.getId()))
            .set(SysUser::getUserStatus, SysUserStatusEnum.ENABLE.getValue())
      );
   }

   @Override
   public void resetPassword(SysUserResetPasswordParam sysUserResetPasswordParam) {
      this.update(
         (Wrapper<SysUser>)((LambdaUpdateWrapper<SysUser>)new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, sysUserResetPasswordParam.getId()))
            .set(SysUser::getPassword, CommonCryptogramUtil.doHashValue(sysUserResetPasswordParam.getNewPassword()))
      );
   }

   @Override
   public SysUserPicValidCodeResult getPicCaptcha() {
      CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(100, 38, 4, 10);
      SysUserPicValidCodeResult sysUserPicValidCodeResult = new SysUserPicValidCodeResult();
      String validCode = circleCaptcha.getCode();
      String validCodeBase64 = circleCaptcha.getImageBase64Data();
      String validCodeReqNo = IdWorker.getIdStr();
      sysUserPicValidCodeResult.setValidCodeBase64(validCodeBase64);
      sysUserPicValidCodeResult.setValidCodeReqNo(validCodeReqNo);
      this.commonCacheOperator.put("user-validCode:" + validCodeReqNo, validCode, 300L);
      return sysUserPicValidCodeResult;
   }

   private void validValidCode(String phoneOrEmail, String validCode, String validCodeReqNo) {
      Object existValidCode;
      if (ObjectUtil.isEmpty(phoneOrEmail)) {
         existValidCode = this.commonCacheOperator.get("user-validCode:" + validCodeReqNo);
      } else {
         existValidCode = this.commonCacheOperator.get("user-validCode:" + phoneOrEmail + "_" + validCodeReqNo);
      }

      if (ObjectUtil.isEmpty(existValidCode)) {
         throw new CommonException("验证码错误", new Object[0]);
      } else {
         if (ObjectUtil.isEmpty(phoneOrEmail)) {
            this.commonCacheOperator.remove(new String[]{"user-validCode:" + validCodeReqNo});
         } else {
            this.commonCacheOperator.remove(new String[]{"user-validCode:" + phoneOrEmail + "_" + validCodeReqNo});
         }

         if (!validCode.equals(Convert.toStr(existValidCode).toLowerCase())) {
            throw new CommonException("验证码错误", new Object[0]);
         }
      }
   }

   @Override
   public String findPasswordGetPhoneValidCode(SysUserGetPhoneValidCodeParam sysUserGetPhoneValidCodeParam) {
      String phone = sysUserGetPhoneValidCodeParam.getPhone();
      if (!PhoneUtil.isMobile(phone)) {
         throw new CommonException("手机号码：{}格式错误", new Object[]{phone});
      } else {
         this.validValidCode(null, sysUserGetPhoneValidCodeParam.getValidCode(), sysUserGetPhoneValidCodeParam.getValidCodeReqNo());
         if (ObjectUtil.isEmpty(this.getUserByPhone(phone))) {
            throw new CommonException("手机码：{}不存在", new Object[]{phone});
         } else {
            String phoneValidCode = RandomUtil.randomNumbers(6);
            String phoneValidCodeReqNo = IdWorker.getIdStr();
            this.commonCacheOperator.put("user-validCode:" + phone + "_" + phoneValidCodeReqNo, phoneValidCode, 300L);
            return phoneValidCodeReqNo;
         }
      }
   }

   @Override
   public String findPasswordGetEmailValidCode(SysUserGetEmailValidCodeParam sysUserGetEmailValidCodeParam) {
      String email = sysUserGetEmailValidCodeParam.getEmail();
      if (!CommonEmailUtil.isEmail(email)) {
         throw new CommonException("邮箱：{}格式错误", new Object[]{email});
      } else {
         this.validValidCode(null, sysUserGetEmailValidCodeParam.getValidCode(), sysUserGetEmailValidCodeParam.getValidCodeReqNo());
         if (ObjectUtil.isEmpty(this.getUserByEmail(email))) {
            throw new CommonException("邮箱：{}不存在", new Object[]{email});
         } else {
            String emailValidCode = RandomUtil.randomNumbers(6);
            String emailValidCodeReqNo = IdWorker.getIdStr();
            String content = "您正在找回密码，验证码为：" + emailValidCode + "，5分钟内有效。";
            this.devEmailApi.sendTextEmailLocal(email, "找回密码邮件", content, CollectionUtil.newArrayList(new File[0]));
            this.commonCacheOperator.put("user-validCode:" + email + "_" + emailValidCodeReqNo, emailValidCode, 300L);
            return emailValidCodeReqNo;
         }
      }
   }

   @Override
   public void findPasswordByPhone(SysUserFindPwdByPhoneParam sysUserFindPwdByPhoneParam) {
      this.validValidCode(sysUserFindPwdByPhoneParam.getPhone(), sysUserFindPwdByPhoneParam.getValidCode(), sysUserFindPwdByPhoneParam.getValidCodeReqNo());
      this.update(
         (Wrapper<SysUser>)((LambdaUpdateWrapper<SysUser>)new LambdaUpdateWrapper<SysUser>()
               .eq(SysUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(sysUserFindPwdByPhoneParam.getPhone())))
            .set(SysUser::getPassword, CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(sysUserFindPwdByPhoneParam.getNewPassword())))
      );
   }

   @Override
   public void findPasswordByEmail(SysUserFindPwdByEmailParam sysUserFindPwdByEmailParam) {
      this.validValidCode(sysUserFindPwdByEmailParam.getEmail(), sysUserFindPwdByEmailParam.getValidCode(), sysUserFindPwdByEmailParam.getValidCodeReqNo());
      this.update(
         (Wrapper<SysUser>)((LambdaUpdateWrapper<SysUser>)new LambdaUpdateWrapper<SysUser>().eq(SysUser::getEmail, sysUserFindPwdByEmailParam.getEmail()))
            .set(SysUser::getPassword, CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(sysUserFindPwdByEmailParam.getNewPassword())))
      );
   }

   @Override
   public void updatePassword(SysUserAdminUpdatePwdParam sysUserAdminUpdatePwdParam) {
      SysUser sysUser = this.queryEntity(sysUserAdminUpdatePwdParam.getId());
      String newPassword = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(sysUserAdminUpdatePwdParam.getNewPassword()));
      this.update((Wrapper<SysUser>)((LambdaUpdateWrapper<SysUser>)new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, sysUser.getId())).set(SysUser::getPassword, newPassword));
   }

   @Override
   public void updatePassword(SysUserUpdatePwdParam sysUserUpdatePwdParam) {
      SysUser sysUser = this.queryEntity(sysUserUpdatePwdParam.getId());
      String passwordHash = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(sysUserUpdatePwdParam.getOldPassword()));
      log.info("旧密码：{}，哈希值：{}", sysUserUpdatePwdParam.getOldPassword(), passwordHash);
      if (!passwordHash.equals(sysUser.getPassword())) {
         throw new CommonException("旧密码不正确", new Object[0]);
      } else {
         String newPassword = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(sysUserUpdatePwdParam.getNewPassword()));
         this.update((Wrapper<SysUser>)((LambdaUpdateWrapper<SysUser>)new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, sysUser.getId())).set(SysUser::getPassword, newPassword));
      }
   }

   @Override
   public String updateAvatar(MultipartFile file) {
      SysUser sysUser = this.queryEntity(StpUtil.getLoginIdAsString());

      try {
         String suffix = Objects.requireNonNull(FileUtil.getSuffix(file.getOriginalFilename())).toLowerCase();
         BufferedImage image = ImgUtil.toImage(file.getBytes());
         String base64;
         if (image.getWidth() <= 200 && image.getHeight() <= 200) {
            base64 = ImgUtil.toBase64DataUri(image, suffix);
         } else {
            base64 = ImgUtil.toBase64DataUri(ImgUtil.scale(image, 200, 200, null), suffix);
         }

         this.update((Wrapper<SysUser>)((LambdaUpdateWrapper<SysUser>)new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, sysUser.getId())).set(SysUser::getAvatar, base64));
         return base64;
      } catch (IOException var6) {
         log.error(">>> 头像修改失败：", var6);
         throw new CommonException("头像修改失败，用户id值为：{}", new Object[]{sysUser.getId()});
      }
   }

   @Override
   public void updateSignature(SysUserSignatureParam sysUserSignatureParam) {
      SysUser sysUser = this.queryEntity(StpUtil.getLoginIdAsString());
      String sysUserSignatureStr = sysUserSignatureParam.getSignature();
      if (sysUserSignatureParam.getSignature().contains(",")) {
         sysUserSignatureStr = (String)StrUtil.split(sysUserSignatureStr, ",").get(1);
      }

      String base64 = ImgUtil.toBase64DataUri(ImgUtil.scale(ImgUtil.toImage(sysUserSignatureStr), 100, 50, null), "png");
      this.update((Wrapper<SysUser>)((LambdaUpdateWrapper<SysUser>)new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, sysUser.getId())).set(SysUser::getSignature, base64));
   }

   @Override
   public void updateUserLoginInfo(String userId, String device) {
      SysUser sysUser = this.queryEntity(userId);
      sysUser.setLastLoginTime(sysUser.getLatestLoginTime());
      sysUser.setLastLoginIp(sysUser.getLatestLoginIp());
      sysUser.setLastLoginAddress(sysUser.getLatestLoginAddress());
      sysUser.setLastLoginDevice(sysUser.getLatestLoginDevice());
      sysUser.setLatestLoginTime(TimeUtil.toDate(TimeUtil.now()));
      String ip = CommonIpAddressUtil.getIp(CommonServletUtil.getRequest());
      sysUser.setLatestLoginIp(ip);
      sysUser.setLatestLoginAddress(CommonIpAddressUtil.getCityInfo(ip));
      sysUser.setLatestLoginDevice(device);
      this.updateById(sysUser);
   }

   @Override
   public List<Tree<String>> ownMenu(SysUserIdParam sysUserIdParam) {
      SysUser sysUser = (SysUser)this.getById(sysUserIdParam.getId());
      if (sysUser != null && SysBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue().equals(sysUser.getAccount())) {
         List<SysMenu> allMenuList = this.sysMenuService
            .list(
               (Wrapper<SysMenu>)((LambdaQueryWrapper<SysMenu>)new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue()))
                  .orderByAsc(SysMenu::getSortCode)
            );
         return this.buildMenuTree(allMenuList);
      } else {
         List<String> roleIdList = this.ownRole(sysUserIdParam);
         List<String> menuIdList = this.sysRelationService
            .getRelationTargetIdListByObjectIdAndCategory(sysUserIdParam.getId(), SysRelationCategoryEnum.SYS_USER_HAS_RESOURCE.getValue());
         if (ObjectUtil.isNotEmpty(roleIdList)) {
            menuIdList.addAll(
               this.sysRelationService.getRelationTargetIdListByObjectIdListAndCategory(roleIdList, SysRelationCategoryEnum.SYS_ROLE_HAS_RESOURCE.getValue())
            );
         }

         List<SysMenu> allMenuList = this.sysMenuService
            .list(
               (Wrapper<SysMenu>)((LambdaQueryWrapper<SysMenu>)new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue()))
                  .orderByAsc(SysMenu::getSortCode)
            );
         List<SysMenu> resultList = CollectionUtil.newArrayList(new SysMenu[0]);
         List<SysMenu> menuList = allMenuList.stream().filter(sysMenu -> menuIdList.contains(sysMenu.getId())).collect(Collectors.toList());
         menuList.forEach(sysMenu -> this.execRecursionFindParent(allMenuList, sysMenu.getId(), resultList));
         resultList.addAll(menuList);
         return this.buildMenuTree(resultList);
      }
   }

   private List<Tree<String>> buildMenuTree(List<SysMenu> menuList) {
      if (ObjectUtil.isEmpty(menuList)) {
         return CollectionUtil.newArrayList(new Tree[0]);
      } else {
         Optional<SysMenu> firstValidMenu = menuList.stream()
            .filter(sysMenu -> !SysResourceMenuTypeEnum.CATALOG.getValue().equals(sysMenu.getMenuType()))
            .findFirst();
         Set<String> repeatMenuNameSet = menuList.stream()
            .map(SysMenu::getName)
            .filter(ObjectUtil::isNotEmpty)
            .collect(Collectors.groupingBy(name -> (String)name, Collectors.counting()))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 1L)
            .map(Entry::getKey)
            .collect(Collectors.toSet());
         List<JSONObject> resultJsonObjectList = menuList.stream()
            .map(
               sysMenu -> {
                  JSONObject menuJsonObject = JSONUtil.parseObj(sysMenu);
                  if (repeatMenuNameSet.contains(sysMenu.getName())) {
                     menuJsonObject.set("name", sysMenu.getName() + "_" + sysMenu.getId());
                  }

                  JSONObject metaJsonObject = JSONUtil.createObj();
                  metaJsonObject.set("icon", sysMenu.getIcon());
                  metaJsonObject.set("title", sysMenu.getTitle());
                  metaJsonObject.set(
                     "type",
                     ObjectUtil.isEmpty(sysMenu.getMenuType()) ? SysResourceCategoryEnum.MENU.getValue().toLowerCase() : sysMenu.getMenuType().toLowerCase()
                  );
                  if (!SysResourceMenuTypeEnum.CATALOG.getValue().equals(sysMenu.getMenuType())
                     && ObjectUtil.isNotEmpty(sysMenu.getVisible())
                     && sysMenu.getVisible().equals("FALSE")) {
                     metaJsonObject.set("hidden", true);
                  }

                  if (firstValidMenu.isPresent() && sysMenu.getId().equals(firstValidMenu.get().getId())) {
                     metaJsonObject.set("affix", true);
                  }

                  menuJsonObject.set("meta", metaJsonObject);
                  return menuJsonObject;
               }
            )
            .collect(Collectors.toList());
         List<TreeNode<String>> treeNodeList = resultJsonObjectList.stream()
            .map(
               jsonObject -> new TreeNode<String>(jsonObject.getStr("id"), jsonObject.getStr("parentId"), jsonObject.getStr("title"), jsonObject.getInt("sortCode"))
                  .setExtra(JSONUtil.parseObj(jsonObject))
            )
            .collect(Collectors.toList());
         return TreeUtil.build(treeNodeList, "0");
      }
   }

   @Override
   public List<Tree<String>> ownMobileMenu(SysUserIdParam sysUserIdParam) {
      List<String> roleIdList = this.ownRole(sysUserIdParam);
      List<Tree<String>> resultList = CollectionUtil.newArrayList(new Tree[0]);
      if (ObjectUtil.isNotEmpty(roleIdList)) {
      }

      return resultList;
   }

   private void execRecursionFindParent(List<SysMenu> originDataList, String id, List<SysMenu> resultList) {
      originDataList.forEach(item -> {
         if (item.getId().equals(id)) {
            int index = CollStreamUtil.toList(originDataList, SysMenu::getId).indexOf(id);
            SysMenu parent = index == -1 ? null : originDataList.get(index);
            if (ObjectUtil.isNotEmpty(parent) && !CollectionUtil.contains(resultList, parent)) {
               resultList.add(parent);
            }

            this.execRecursionFindParent(originDataList, item.getParentId(), resultList);
         }
      });
   }

   @Override
   public List<String> ownRole(SysUserIdParam sysUserIdParam) {
      return this.sysRelationService.getRelationTargetIdListByObjectIdAndCategory(sysUserIdParam.getId(), SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
   }

   @Override
   public void grantRole(SysUserGrantRoleParam sysUserGrantRoleParam) {
      this.sysRelationService
         .saveRelationBatchWithClear(sysUserGrantRoleParam.getId(), sysUserGrantRoleParam.getRoleIdList(), SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
   }

   @Override
   public SysUserOwnResourceResult ownResource(SysUserIdParam sysUserIdParam) {
      SysUserOwnResourceResult sysUserOwnResourceResult = new SysUserOwnResourceResult();
      sysUserOwnResourceResult.setId(sysUserIdParam.getId());
      sysUserOwnResourceResult.setGrantInfoList(
         this.sysRelationService
            .getRelationListByObjectIdAndCategory(sysUserIdParam.getId(), SysRelationCategoryEnum.SYS_USER_HAS_RESOURCE.getValue())
            .stream()
            .map(
               sysRelation -> (SysUserOwnResourceResult.SysUserOwnResource)JSONUtil.toBean(
                  sysRelation.getExtJson(), SysUserOwnResourceResult.SysUserOwnResource.class
               )
            )
            .collect(Collectors.toList())
      );
      return sysUserOwnResourceResult;
   }

   @Override
   public void grantResource(SysUserGrantResourceParam sysUserGrantResourceParam) {
      List<String> menuIdList = sysUserGrantResourceParam.getGrantInfoList()
         .stream()
         .map(SysUserGrantResourceParam.SysUserGrantResource::getMenuId)
         .collect(Collectors.toList());
      List<String> extJsonList = sysUserGrantResourceParam.getGrantInfoList().stream().<String>map(JSONUtil::toJsonStr).collect(Collectors.toList());
      this.sysRelationService
         .saveRelationBatchWithClear(sysUserGrantResourceParam.getId(), menuIdList, SysRelationCategoryEnum.SYS_USER_HAS_RESOURCE.getValue(), extJsonList);
   }

   @Override
   public SysUserOwnPermissionResult ownPermission(SysUserIdParam sysUserIdParam) {
      SysUserOwnPermissionResult sysUserOwnPermissionResult = new SysUserOwnPermissionResult();
      sysUserOwnPermissionResult.setId(sysUserIdParam.getId());
      sysUserOwnPermissionResult.setGrantInfoList(
         this.sysRelationService
            .getRelationListByObjectIdAndCategory(sysUserIdParam.getId(), SysRelationCategoryEnum.SYS_USER_HAS_PERMISSION.getValue())
            .stream()
            .map(
               sysRelation -> (SysUserOwnPermissionResult.SysUserOwnPermission)JSONUtil.toBean(
                  sysRelation.getExtJson(), SysUserOwnPermissionResult.SysUserOwnPermission.class
               )
            )
            .collect(Collectors.toList())
      );
      return sysUserOwnPermissionResult;
   }

   @Override
   public void grantPermission(SysUserGrantPermissionParam sysUserGrantPermissionParam) {
      String id = sysUserGrantPermissionParam.getId();
      List<String> apiUrlList = sysUserGrantPermissionParam.getGrantInfoList()
         .stream()
         .map(SysUserGrantPermissionParam.SysUserGrantPermission::getApiUrl)
         .collect(Collectors.toList());
      List<String> extJsonList = sysUserGrantPermissionParam.getGrantInfoList().stream().<String>map(JSONUtil::toJsonStr).collect(Collectors.toList());
      this.sysRelationService.saveRelationBatchWithClear(id, apiUrlList, SysRelationCategoryEnum.SYS_USER_HAS_PERMISSION.getValue(), extJsonList);
   }

   @Override
   public List<Tree<String>> loginOrgTree(SysUserIdParam sysUserIdParam) {
      SysUser sysUser = this.queryEntity(sysUserIdParam.getId());
      List<SysOrg> originDataList = this.sysOrgService.getAllOrgList();
      List<SysOrg> sysOrgList = this.sysOrgService.getParentListById(originDataList, sysUser.getOrgId(), true);
      List<TreeNode<String>> treeNodeList = sysOrgList.stream().map(sysOrg -> {
         TreeNode<String> treeNode = new TreeNode<String>(sysOrg.getId(), sysOrg.getParentId(), sysOrg.getName(), sysOrg.getSortCode());
         if (sysOrg.getId().equals(sysUser.getOrgId())) {
            treeNode.setExtra(JSONUtil.createObj().set("style", JSONUtil.createObj().set("color", "#FFF").set("background", "var(--primary-color)")));
         }

         return treeNode;
      }).collect(Collectors.toList());
      return TreeUtil.build(treeNodeList, "0", new TreeNodeConfig().setParentIdKey("pid").setNameKey("label"), new DefaultNodeParser());
   }

   @Override
   public void updateUserInfo(SysUserUpdateInfoParam sysUserUpdateInfoParam) {
      String id = StpLoginUserUtil.getLoginUser().getId();
      if (!StrUtil.equals(id, sysUserUpdateInfoParam.getId())) {
         throw new CommonException("禁止修改他人信息", new Object[0]);
      } else {
         SysUser sysUser = this.queryEntity(sysUserUpdateInfoParam.getId());
         if (ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getPhone())) {
            if (!PhoneUtil.isMobile(sysUserUpdateInfoParam.getPhone())) {
               throw new CommonException("手机号码：{}格式错误", new Object[]{sysUserUpdateInfoParam.getPhone()});
            }

            if (this.count(
                  (Wrapper<SysUser>)((LambdaQueryWrapper<SysUser>)new LambdaQueryWrapper<SysUser>().ne(SysUser::getId, sysUser.getId()))
                     .eq(SysUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(sysUserUpdateInfoParam.getPhone()))
               )
               > 0L) {
               throw new CommonException("存在重复的手机号，手机号为：{}", new Object[]{sysUserUpdateInfoParam.getPhone()});
            }
         }

         LambdaUpdateWrapper<SysUser> lambdaUpdateWrapper = (LambdaUpdateWrapper<SysUser>)new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, sysUser.getId());
         if (ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getName())) {
            lambdaUpdateWrapper.set(SysUser::getName, sysUserUpdateInfoParam.getName());
         }

         if (ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getPhone())) {
            lambdaUpdateWrapper.set(SysUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(sysUserUpdateInfoParam.getPhone()));
         }

         if (ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getNickname())) {
            lambdaUpdateWrapper.set(SysUser::getNickname, sysUserUpdateInfoParam.getNickname());
         }

         if (ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getGender())) {
            lambdaUpdateWrapper.set(SysUser::getGender, sysUserUpdateInfoParam.getGender());
         }

         if (ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getBirthday())) {
            lambdaUpdateWrapper.set(SysUser::getBirthday, sysUserUpdateInfoParam.getBirthday());
         }

         if (ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getEmail())) {
            lambdaUpdateWrapper.set(SysUser::getEmail, sysUserUpdateInfoParam.getEmail());
         }

         if (ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getSignature())) {
            lambdaUpdateWrapper.set(SysUser::getSignature, sysUserUpdateInfoParam.getSignature());
         }

         this.update(lambdaUpdateWrapper);
      }
   }

   @Override
   public void updateUserWorkbench(SysUserUpdateWorkbenchParam sysUserUpdateWorkbenchParam) {
      this.sysRelationService
         .saveRelationWithClear(
            StpUtil.getLoginIdAsString(), null, SysRelationCategoryEnum.SYS_USER_WORKBENCH_DATA.getValue(), sysUserUpdateWorkbenchParam.getWorkbenchData()
         );
   }

   @Override
   public String loginWorkbench(SysUserIdParam sysUserIdParam) {
      SysUser sysUser = this.queryEntity(sysUserIdParam.getId());
      SysRelation sysRelation = (SysRelation)this.sysRelationService
         .getOne(
            (Wrapper<SysRelation>)((LambdaQueryWrapper<SysRelation>)new LambdaQueryWrapper<SysRelation>().eq(SysRelation::getObjectId, sysUser.getId()))
               .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_USER_WORKBENCH_DATA.getValue())
         );
      return ObjectUtil.isNotEmpty(sysRelation) ? sysRelation.getExtJson() : this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_WORKBENCH_DATA");
   }

   @Override
   public SysUser queryEntity(String id) {
      SysUser sysUser = (SysUser)this.getById(id);
      if (ObjectUtil.isEmpty(sysUser)) {
         throw new CommonException("用户不存在，id值为：{}", new Object[]{id});
      } else {
         return sysUser;
      }
   }

   @Override
   public List<JSONObject> getRoleList(String userId) {
      List<String> roleIdList = this.sysRelationService
         .getRelationTargetIdListByObjectIdAndCategory(userId, SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
      return (List<JSONObject>)(ObjectUtil.isNotEmpty(roleIdList)
         ? this.sysRoleService.listByIds(roleIdList).stream().<JSONObject>map(JSONUtil::parseObj).collect(Collectors.toList())
         : CollectionUtil.newArrayList(new JSONObject[0]));
   }

   @Override
   public List<String> getButtonCodeList(List<String> userAndRoleIdList) {
      List<String> buttonIdList = CollectionUtil.newArrayList(new String[0]);
      this.sysRelationService
         .list(
            (Wrapper<SysRelation>)((LambdaQueryWrapper<SysRelation>)new LambdaQueryWrapper<SysRelation>().in(SysRelation::getObjectId, userAndRoleIdList))
               .in(
                  SysRelation::getCategory,
                  new Object[]{SysRelationCategoryEnum.SYS_USER_HAS_RESOURCE.getValue(), SysRelationCategoryEnum.SYS_ROLE_HAS_RESOURCE.getValue()}
               )
         )
         .forEach(sysRelation -> {
            if (ObjectUtil.isNotEmpty(sysRelation.getExtJson())) {
               buttonIdList.addAll(JSONUtil.parseObj(sysRelation.getExtJson()).getBeanList("buttonInfo", String.class));
            }
         });
      return (List<String>)(ObjectUtil.isNotEmpty(buttonIdList)
         ? this.sysButtonService.listByIds(buttonIdList).stream().map(SysButton::getCode).collect(Collectors.toList())
         : CollectionUtil.newArrayList(new String[0]));
   }

   @Override
   public List<String> getMobileButtonCodeList(List<String> userAndRoleIdList) {
      List<String> buttonIdList = CollectionUtil.newArrayList(new String[0]);
      this.sysRelationService
         .getRelationListByObjectIdListAndCategory(userAndRoleIdList, SysRelationCategoryEnum.SYS_ROLE_HAS_MOBILE_MENU.getValue())
         .forEach(sysRelation -> {
            if (ObjectUtil.isNotEmpty(sysRelation.getExtJson())) {
               buttonIdList.addAll(JSONUtil.parseObj(sysRelation.getExtJson()).getBeanList("buttonInfo", String.class));
            }
         });
      if (ObjectUtil.isNotEmpty(buttonIdList)) {
      }

      return CollectionUtil.newArrayList(new String[0]);
   }

   @Override
   public List<JSONObject> getPermissionList(List<String> userAndRoleIdList, String orgId) {
      Map<String, List<SysRelation>> map = this.sysRelationService
         .list(
            (Wrapper<SysRelation>)((LambdaQueryWrapper<SysRelation>)new LambdaQueryWrapper<SysRelation>().in(SysRelation::getObjectId, userAndRoleIdList))
               .in(
                  SysRelation::getCategory,
                  new Object[]{SysRelationCategoryEnum.SYS_USER_HAS_PERMISSION.getValue(), SysRelationCategoryEnum.SYS_ROLE_HAS_PERMISSION.getValue()}
               )
         )
         .stream()
         .collect(Collectors.groupingBy(SysRelation::getTargetId));
      return this.getScopeListByMap(map, orgId);
   }

   public List<JSONObject> getScopeListByMap(Map<String, List<SysRelation>> groupMap, String orgId) {
      List<JSONObject> resultList = CollectionUtil.newArrayList(new JSONObject[0]);
      groupMap.forEach((key, value) -> {
         JSONObject jsonObject = JSONUtil.createObj().set("apiUrl", key);
         resultList.add(jsonObject.set("dataScope", CollectionUtil.newArrayList(new Object[0])));
      });
      return resultList;
   }

   @Override
   public void downloadImportUserTemplate(HttpServletResponse response) throws IOException {
      try {
         InputStream inputStream = POICacheManager.getFile("userImportTemplate.xlsx");
         byte[] bytes = IoUtil.readBytes(inputStream);
         CommonDownloadUtil.download("RUNXIN系统B端用户导入模板.xlsx", bytes, response);
      } catch (Exception var4) {
         log.error(">>> 下载用户导入模板失败：", var4);
         CommonResponseUtil.renderError(response, "下载用户导入模板失败");
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public JSONObject importUser(MultipartFile file) {
      try {
         int successCount = 0;
         int errorCount = 0;
         JSONArray errorDetail = JSONUtil.createArray();
         File tempFile = FileUtil.writeBytes(file.getBytes(), FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + "userImportTemplate.xlsx"));
         List<SysUserImportParam> sysUserImportParamList = ((ExcelReaderSheetBuilder)((ExcelReaderBuilder)EasyExcel.read(tempFile)
                  .head(SysUserImportParam.class))
               .sheet()
               .headRowNumber(2))
            .doReadSync();
         List<SysUser> allUserList = this.list();

         for (int i = 0; i < sysUserImportParamList.size(); i++) {
            JSONObject jsonObject = this.doImport(allUserList, sysUserImportParamList.get(i), i);
            if (jsonObject.getBool("success")) {
               successCount++;
            } else {
               errorCount++;
               errorDetail.add(jsonObject);
            }
         }

         return JSONUtil.createObj()
            .set("totalCount", sysUserImportParamList.size())
            .set("successCount", successCount)
            .set("errorCount", errorCount)
            .set("errorDetail", errorDetail);
      } catch (Exception var10) {
         log.error(">>> 用户导入失败：", var10);
         throw new CommonException("用户导入失败", new Object[0]);
      }
   }

   public JSONObject doImport(List<SysUser> allUserList, SysUserImportParam sysUserImportParam, int i) {
      String account = sysUserImportParam.getAccount();
      String name = sysUserImportParam.getName();
      if (ObjectUtil.hasEmpty(new Object[]{account, name})) {
         return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
      } else {
         try {
            int index = CollStreamUtil.toList(allUserList, SysUser::getAccount).indexOf(account);
            SysUser sysUser = new SysUser();
            boolean isAdd = false;
            if (index == -1) {
               isAdd = true;
            } else {
               sysUser = allUserList.get(index);
            }

            String phone = sysUserImportParam.getPhone();
            String email = sysUserImportParam.getEmail();
            if (ObjectUtil.isNotEmpty(phone)) {
               if (isAdd) {
                  boolean repeatPhone = allUserList.stream()
                     .anyMatch(tempSysUser -> ObjectUtil.isNotEmpty(tempSysUser.getPhone()) && tempSysUser.getPhone().equals(phone));
                  if (repeatPhone) {
                     sysUserImportParam.setPhone(null);
                  }
               } else {
                  String finalExistUserId = sysUser.getId();
                  boolean repeatPhone = allUserList.stream()
                     .anyMatch(
                        tempSysUser -> ObjectUtil.isNotEmpty(tempSysUser.getPhone())
                           && tempSysUser.getPhone().equals(phone)
                           && !tempSysUser.getId().equals(finalExistUserId)
                     );
                  if (repeatPhone) {
                     sysUser.setPhone(sysUser.getPhone());
                  }
               }
            }

            if (ObjectUtil.isNotEmpty(email)) {
               if (isAdd) {
                  boolean repeatEmail = allUserList.stream()
                     .anyMatch(tempSysUser -> ObjectUtil.isNotEmpty(tempSysUser.getEmail()) && tempSysUser.getEmail().equals(email));
                  if (repeatEmail) {
                     sysUserImportParam.setEmail(null);
                  }
               } else {
                  String finalExistUserId = sysUser.getId();
                  boolean repeatEmail = allUserList.stream()
                     .anyMatch(
                        tempSysUser -> ObjectUtil.isNotEmpty(tempSysUser.getEmail())
                           && tempSysUser.getEmail().equals(email)
                           && !tempSysUser.getId().equals(finalExistUserId)
                     );
                  if (repeatEmail) {
                     sysUser.setEmail(sysUser.getEmail());
                  }
               }
            }

            BeanUtil.copyProperties(sysUserImportParam, sysUser, new String[0]);
            sysUser.setOrgId(null);
            sysUser.setPositionId(null);
            sysUser.setPositionJson(null);
            if (isAdd) {
               sysUser.setId(IdWorker.getIdStr());
               sysUser.setAvatar(CommonAvatarUtil.generateImg(sysUser.getName()));
               sysUser.setPassword(CommonCryptogramUtil.doHashValue(this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_PASSWORD")));
               sysUser.setSortCode(99);
               sysUser.setUserStatus(SysUserStatusEnum.ENABLE.getValue());
               CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.USER.getValue(), JSONUtil.createArray().put(sysUser));
               allUserList.add(sysUser);
            } else {
               CommonDataChangeEventCenter.doUpdateWithData(SysDataTypeEnum.USER.getValue(), JSONUtil.createArray().put(sysUser));
               allUserList.remove(index);
               allUserList.add(index, sysUser);
            }

            this.saveOrUpdate(sysUser);
            return JSONUtil.createObj().set("success", true);
         } catch (Exception var13) {
            log.error(">>> 数据导入异常：", var13);
            return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
         }
      }
   }

   @Override
   public void exportUser(SysUserExportParam sysUserExportParam, HttpServletResponse response) throws IOException {
      File tempFile = null;

      try {
         QueryWrapper<SysUser> queryWrapper = new QueryWrapper().checkSqlInjection();
         if (ObjectUtil.isNotEmpty(sysUserExportParam.getUserIds())) {
            queryWrapper.lambda().in(SysUser::getId, StrUtil.split(sysUserExportParam.getUserIds(), ","));
         } else {
            if (ObjectUtil.isNotEmpty(sysUserExportParam.getSearchKey())) {
               queryWrapper.lambda()
                  .and(
                     q -> ((LambdaQueryWrapper<SysUser>)((LambdaQueryWrapper<SysUser>)((LambdaQueryWrapper<SysUser>)((LambdaQueryWrapper<SysUser>)q.like(
                                    SysUser::getAccount, sysUserExportParam.getSearchKey()
                                 ))
                                 .or())
                              .like(SysUser::getName, sysUserExportParam.getSearchKey()))
                           .or())
                        .like(SysUser::getPhone, sysUserExportParam.getSearchKey())
                  );
            }

            if (ObjectUtil.isNotEmpty(sysUserExportParam.getUserStatus())) {
               queryWrapper.lambda().eq(SysUser::getUserStatus, sysUserExportParam.getUserStatus());
            }
         }

         String fileName = "RUNXIN系统B端用户信息清单.xlsx";
         List<SysUser> sysUserList = this.list(queryWrapper);
         if (ObjectUtil.isEmpty(sysUserList)) {
            throw new CommonException("无数据可导出", new Object[0]);
         }

         sysUserList = CollectionUtil.sort(sysUserList, Comparator.comparing(SysUser::getOrgId));
         List<SysUserExportResult> sysUserExportResultList = sysUserList.stream().map(sysUser -> {
            SysUserExportResult sysUserExportResult = new SysUserExportResult();
            BeanUtil.copyProperties(sysUser, sysUserExportResult, new String[0]);
            sysUserExportResult.setGroupName(ObjectUtil.isNotEmpty(sysUserExportResult.getOrgName()) ? sysUserExportResult.getOrgName() : "无组织");
            sysUserExportResult.setUserStatus(sysUserExportResult.getUserStatus().equalsIgnoreCase(SysUserStatusEnum.ENABLE.getValue()) ? "正常" : "停用");
            if (ObjectUtil.isNotEmpty(sysUser.getAvatar())) {
               sysUserExportResult.setAvatar(ImgUtil.toBytes(ImgUtil.toImage((String)StrUtil.split(sysUser.getAvatar(), ",").get(1)), "png"));
            }

            return sysUserExportResult;
         }).collect(Collectors.toList());
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         WriteCellStyle headWriteCellStyle = new WriteCellStyle();
         WriteFont headWriteFont = new WriteFont();
         headWriteFont.setFontHeightInPoints((short)14);
         headWriteCellStyle.setWriteFont(headWriteFont);
         headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
         headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
         WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
         contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
         contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
         WriteFont contentWriteFont = new WriteFont();
         contentWriteFont.setFontHeightInPoints((short)12);
         contentWriteCellStyle.setWriteFont(contentWriteFont);
         contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
         contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
         contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
         contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
         contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
         contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
         HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
         ((ExcelWriterBuilder)((ExcelWriterBuilder)((ExcelWriterBuilder)((ExcelWriterBuilder)((ExcelWriterBuilder)EasyExcel.write(
                              tempFile.getPath(), SysUserExportResult.class
                           )
                           .registerWriteHandler(horizontalCellStyleStrategy))
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()))
                     .registerWriteHandler(
                        new CommonExcelCustomMergeStrategy(
                           sysUserExportResultList.stream().map(SysUserExportResult::getGroupName).collect(Collectors.toList()), 0
                        )
                     ))
                  .registerWriteHandler(new CellWriteHandler() {
                     public void afterCellDispose(CellWriteHandlerContext context) {
                        WriteCellData<?> cellData = context.getFirstCellData();
                        WriteCellStyle writeCellStyle = cellData.getOrCreateStyle();
                        Integer rowIndex = context.getRowIndex();
                        if (rowIndex == 0) {
                           WriteFont headWriteFontx = new WriteFont();
                           headWriteFontx.setFontName("宋体");
                           headWriteFontx.setBold(true);
                           headWriteFontx.setFontHeightInPoints((short)16);
                           writeCellStyle.setWriteFont(headWriteFontx);
                        }
                     }
                  }))
               .registerWriteHandler(new AbstractRowHeightStyleStrategy() {
                  protected void setHeadColumnHeight(Row row, int relativeRowIndex) {
                     if (relativeRowIndex == 0) {
                        row.setHeightInPoints(34.0F);
                     } else {
                        row.setHeightInPoints(30.0F);
                     }
                  }

                  protected void setContentColumnHeight(Row row, int relativeRowIndex) {
                     row.setHeightInPoints(20.0F);
                  }
               }))
            .sheet("用户信息")
            .doWrite(sysUserExportResultList);
         CommonDownloadUtil.download(tempFile, response);
      } catch (Exception var16) {
         log.error(">>> 用户导出失败：", var16);
         CommonResponseUtil.renderError(response, "用户导出失败");
      } finally {
         FileUtil.del(tempFile);
      }
   }

   @Override
   public void exportUserInfo(SysUserIdParam sysUserIdParam, HttpServletResponse response) throws IOException {
      File destTemplateFile = null;
      File resultFile = null;

      try {
         SysUser sysUser = this.queryEntity(sysUserIdParam.getId());
         InputStream inputStream = POICacheManager.getFile("userExportTemplate.docx");
         destTemplateFile = FileUtil.writeFromStream(inputStream, FileUtil.file(FileUtil.getTmpDir() + File.separator + "userExportTemplate.docx"));
         Map<String, Object> map = BeanUtil.beanToMap(sysUser, new String[0]);
         String avatarBase64;
         if (ObjectUtil.isNotEmpty(sysUser.getAvatar())) {
            avatarBase64 = sysUser.getAvatar();
         } else {
            avatarBase64 = CommonAvatarUtil.generateImg(sysUser.getName());
         }

         ImageEntity imageEntity = new ImageEntity(ImgUtil.toBytes(ImgUtil.toImage((String)StrUtil.split(avatarBase64, ",").get(1)), "png"), 120, 160);
         map.put("avatar", imageEntity);
         if (ObjectUtil.isNotEmpty(sysUser.getBirthday())) {
            try {
               long age = DateUtil.betweenYear(DateUtil.parseDate(sysUser.getBirthday()), TimeUtil.toDate(TimeUtil.now()), true);
               if (age != 0L) {
                  map.put("age", age + "岁");
               }
            } catch (Exception var16) {
            }
         }

         map.put("exportDateTime", TimeUtil.format(TimeUtil.now(), "yyyy年MM月dd日"));
         XWPFDocument doc = WordExportUtil.exportWord07(destTemplateFile.getAbsolutePath(), map);
         resultFile = FileUtil.file(FileUtil.getTmpDir() + File.separator + "RUNXIN系统B端用户信息_" + sysUser.getName() + ".docx");
         BufferedOutputStream outputStream = FileUtil.getOutputStream(resultFile);
         doc.write(outputStream);
         outputStream.close();
         CommonDownloadUtil.download(resultFile, response);
      } catch (Exception var17) {
         log.error(">>> 导出用户个人信息失败：", var17);
         CommonResponseUtil.renderError(response, "导出失败");
      } finally {
         if (ObjectUtil.isNotEmpty(destTemplateFile)) {
            FileUtil.del(destTemplateFile);
         }

         if (ObjectUtil.isNotEmpty(resultFile)) {
            FileUtil.del(resultFile);
         }
      }
   }

   @Override
   public List<SysUserPositionResult> loginPositionInfo(SysUserIdParam sysUserIdParam) {
      SysUser sysUser = this.queryEntity(sysUserIdParam.getId());
      List<SysUserPositionResult> sysUserPositionResultList = CollectionUtil.newArrayList(new SysUserPositionResult[0]);
      if (ObjectUtil.hasEmpty(new Object[]{sysUser.getOrgId(), sysUser.getPositionId()})) {
         return sysUserPositionResultList;
      } else {
         List<SysOrg> sysOrgList = this.sysOrgService.getAllOrgList();
         String primaryOrgId = sysUser.getOrgId();
         SysOrg primarySysOrg = this.sysOrgService.getById(sysOrgList, primaryOrgId);
         if (ObjectUtil.isEmpty(primarySysOrg)) {
            return sysUserPositionResultList;
         } else {
            String primaryOrgName = primarySysOrg.getName();
            List<SysPosition> sysPositionList = this.sysPositionService.list();
            String primaryPositionId = sysUser.getPositionId();
            SysPosition primaryPosition = this.sysPositionService.getById(sysPositionList, primaryPositionId);
            if (ObjectUtil.isEmpty(primaryPosition)) {
               return sysUserPositionResultList;
            } else {
               String primaryPositionName = primaryPosition.getName();
               SysUserPositionResult primarySysUserPositionResult = new SysUserPositionResult();
               primarySysUserPositionResult.setOrgId(primaryOrgId);
               primarySysUserPositionResult.setOrgName(primaryOrgName);
               primarySysUserPositionResult.setPositionId(primaryPositionId);
               primarySysUserPositionResult.setPositionName(primaryPositionName);
               primarySysUserPositionResult.setCategory(primarySysOrg.getCategory());
               primarySysUserPositionResult.setType("primary");
               sysUserPositionResultList.add(primarySysUserPositionResult);
               String positionJson = sysUser.getPositionJson();
               if (ObjectUtil.isNotEmpty(positionJson)) {
                  JSONArray jsonArray = JSONUtil.parseArray(positionJson);
                  if (ObjectUtil.isNotEmpty(jsonArray)) {
                     jsonArray.forEach(obj -> {
                        JSONObject jsonObject = JSONUtil.parseObj(obj);
                        String slaveOrgId = jsonObject.getStr("orgId");
                        String slavePositionId = jsonObject.getStr("positionId");
                        if (!ObjectUtil.hasEmpty(new Object[]{slaveOrgId, slavePositionId})) {
                           SysOrg slaveSysOrg = this.sysOrgService.getById(sysOrgList, slaveOrgId);
                           if (!ObjectUtil.isEmpty(slaveSysOrg)) {
                              String slaveOrgName = slaveSysOrg.getName();
                              SysPosition slavePosition = this.sysPositionService.getById(sysPositionList, slavePositionId);
                              if (!ObjectUtil.isEmpty(slavePosition)) {
                                 String slavePositionName = slavePosition.getName();
                                 SysUserPositionResult slaveSysUserPositionResult = new SysUserPositionResult();
                                 slaveSysUserPositionResult.setOrgId(slaveOrgId);
                                 slaveSysUserPositionResult.setOrgName(slaveOrgName);
                                 slaveSysUserPositionResult.setPositionId(slavePositionId);
                                 slaveSysUserPositionResult.setPositionName(slavePositionName);
                                 slaveSysUserPositionResult.setCategory(slaveSysOrg.getCategory());
                                 slaveSysUserPositionResult.setType("slave");
                                 sysUserPositionResultList.add(slaveSysUserPositionResult);
                              }
                           }
                        }
                     });
                  }
               }

               return sysUserPositionResultList;
            }
         }
      }
   }

   @Override
   public Page<SysUser> getAllUserSelectorList() {
      return (Page<SysUser>)this.page(
         CommonPageRequest.defaultPage(),
         (Wrapper<SysUser>)new LambdaQueryWrapper<SysUser>()
            .select(
               
                  SysUser::getId, SysUser::getAvatar, SysUser::getAccount, SysUser::getName, SysUser::getSortCode, SysUser::getGender, SysUser::getEntryDate
               
            )
            .orderByAsc(SysUser::getSortCode)
      );
   }

   @Override
   public List<Tree<String>> orgTreeSelector() {
      List<SysOrg> sysOrgList = this.sysOrgService.getAllOrgList();
      List<TreeNode<String>> treeNodeList = sysOrgList.stream()
         .map(sysOrg -> new TreeNode<String>(sysOrg.getId(), sysOrg.getParentId(), sysOrg.getName(), sysOrg.getSortCode()))
         .collect(Collectors.toList());
      return TreeUtil.build(treeNodeList, "0");
   }

   @Override
   public Page<SysOrg> orgListSelector(SysUserSelectorOrgListParam sysUserSelectorOrgListParam) {
      LambdaQueryWrapper<SysOrg> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.select(SysOrg::getId, SysOrg::getParentId, SysOrg::getName, SysOrg::getCategory, SysOrg::getSortCode);
      if (ObjectUtil.isNotEmpty(sysUserSelectorOrgListParam.getParentId())) {
         lambdaQueryWrapper.eq(SysOrg::getParentId, sysUserSelectorOrgListParam.getParentId());
      }

      if (ObjectUtil.isNotEmpty(sysUserSelectorOrgListParam.getSearchKey())) {
         lambdaQueryWrapper.like(SysOrg::getName, sysUserSelectorOrgListParam.getSearchKey());
      }

      lambdaQueryWrapper.orderByAsc(SysOrg::getSortCode);
      return (Page<SysOrg>)this.sysOrgService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
   }

   @Override
   public Page<SysPosition> positionSelector(SysUserSelectorPositionParam sysUserSelectorPositionParam) {
      LambdaQueryWrapper<SysPosition> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.select(
         SysPosition::getId, SysPosition::getOrgId, SysPosition::getName, SysPosition::getCategory, SysPosition::getSortCode
      );
      if (ObjectUtil.isNotEmpty(sysUserSelectorPositionParam.getOrgId())) {
         lambdaQueryWrapper.eq(SysPosition::getOrgId, sysUserSelectorPositionParam.getOrgId());
      }

      if (ObjectUtil.isNotEmpty(sysUserSelectorPositionParam.getSearchKey())) {
         lambdaQueryWrapper.like(SysPosition::getName, sysUserSelectorPositionParam.getSearchKey());
      }

      lambdaQueryWrapper.orderByAsc(SysPosition::getSortCode);
      return (Page<SysPosition>)this.sysPositionService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
   }

   @Override
   public Page<SysRole> roleSelector(SysUserSelectorRoleParam sysUserSelectorRoleParam) {
      LambdaQueryWrapper<SysRole> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.select(SysRole::getId, SysRole::getOrgId, SysRole::getName, SysRole::getCategory, SysRole::getSortCode);
      lambdaQueryWrapper.eq(SysRole::getCategory, SysRoleCategoryEnum.GLOBAL.getValue());
      if (ObjectUtil.isNotEmpty(sysUserSelectorRoleParam.getSearchKey())) {
         lambdaQueryWrapper.like(SysRole::getName, sysUserSelectorRoleParam.getSearchKey());
      }

      lambdaQueryWrapper.orderByAsc(SysRole::getSortCode);
      return (Page<SysRole>)this.sysRoleService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
   }

   @Override
   public Page<SysUser> userSelector(SysUserSelectorUserParam sysUserSelectorUserParam) {
      LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.select(
         
            SysUser::getId, SysUser::getAvatar, SysUser::getAccount, SysUser::getName, SysUser::getSortCode, SysUser::getGender, SysUser::getEntryDate
         
      );
      if (ObjectUtil.isEmpty(sysUserSelectorUserParam.getSearchKey())) {
         return this.getAllUserSelectorList();
      } else {
         lambdaQueryWrapper.like(SysUser::getName, sysUserSelectorUserParam.getSearchKey());
         lambdaQueryWrapper.orderByAsc(SysUser::getSortCode);
         return (Page<SysUser>)this.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
      }
   }

   @Override
   public Page<SysUserMessageResult> loginMessagePage(SysUserMessagePageParam sysUserMessagePageParam) {
      Page<SysUserMessageResult> resultPage = new Page();
      Page<JSONObject> devMessagePage = this.devMessageApi
         .page(CollectionUtil.newArrayList(new String[]{StpUtil.getLoginIdAsString()}), sysUserMessagePageParam.getCategory());
      BeanUtil.copyProperties(devMessagePage, resultPage, new String[0]);
      return resultPage;
   }

   @Override
   public SysUserMessageDetailResult loginMessageDetail(SysUserMessageIdParam sysUserMessageIdParam) {
      return (SysUserMessageDetailResult)JSONUtil.toBean(this.devMessageApi.detail(sysUserMessageIdParam.getId()), SysUserMessageDetailResult.class);
   }

   @Override
   public List<SysOrg> getOrgListByIdList(SysUserIdListParam sysUserIdListParam) {
      LambdaQueryWrapper<SysOrg> lambdaQueryWrapper = new LambdaQueryWrapper();
      ((LambdaQueryWrapper<SysOrg>)lambdaQueryWrapper.select(
               SysOrg::getId, SysOrg::getParentId, SysOrg::getName, SysOrg::getCategory, SysOrg::getSortCode
            )
            .in(SysOrg::getId, sysUserIdListParam.getIdList()))
         .orderByAsc(SysOrg::getSortCode);
      return this.sysOrgService.list(lambdaQueryWrapper);
   }

   @Override
   public List<SysUser> getUserListByIdList(SysUserIdListParam sysUserIdListParam) {
      LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper();
      ((LambdaQueryWrapper<SysUser>)lambdaQueryWrapper.select(
               SysUser::getId, SysUser::getOrgId, SysUser::getAvatar, SysUser::getAccount, SysUser::getName, SysUser::getSortCode
            )
            .in(SysUser::getId, sysUserIdListParam.getIdList()))
         .orderByAsc(SysUser::getSortCode);
      return this.list(lambdaQueryWrapper);
   }

   @Override
   public List<SysPosition> getPositionListByIdList(SysUserIdListParam sysUserIdListParam) {
      LambdaQueryWrapper<SysPosition> lambdaQueryWrapper = new LambdaQueryWrapper();
      ((LambdaQueryWrapper<SysPosition>)lambdaQueryWrapper.select(
               SysPosition::getId, SysPosition::getOrgId, SysPosition::getName, SysPosition::getCategory, SysPosition::getSortCode
            )
            .in(SysPosition::getId, sysUserIdListParam.getIdList()))
         .orderByAsc(SysPosition::getSortCode);
      return this.sysPositionService.list(lambdaQueryWrapper);
   }

   @Override
   public List<SysRole> getRoleListByIdList(SysUserIdListParam sysUserIdListParam) {
      LambdaQueryWrapper<SysRole> lambdaQueryWrapper = new LambdaQueryWrapper();
      ((LambdaQueryWrapper<SysRole>)lambdaQueryWrapper.select(
               SysRole::getId, SysRole::getOrgId, SysRole::getName, SysRole::getCategory, SysRole::getSortCode
            )
            .in(SysRole::getId, sysUserIdListParam.getIdList()))
         .orderByAsc(SysRole::getSortCode);
      return this.sysRoleService.list(lambdaQueryWrapper);
   }
}
