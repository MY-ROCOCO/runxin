package com.rxinyun.admin.biz.modular.user.service.impl;

import cn.afterturn.easypoi.cache.manager.POICacheManager;
import cn.afterturn.easypoi.entity.ImageEntity;
import cn.afterturn.easypoi.word.WordExportUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.metadata.data.WriteCellData;
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
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.biz.core.enums.BizBuildInEnum;
import com.rxinyun.admin.biz.core.enums.BizDataTypeEnum;
import com.rxinyun.admin.biz.modular.org.entity.BizOrg;
import com.rxinyun.admin.biz.modular.org.service.BizOrgService;
import com.rxinyun.admin.biz.modular.position.entity.BizPosition;
import com.rxinyun.admin.biz.modular.position.service.BizPositionService;
import com.rxinyun.admin.biz.modular.user.entity.BizUser;
import com.rxinyun.admin.biz.modular.user.enums.BizRoleCategoryEnum;
import com.rxinyun.admin.biz.modular.user.enums.BizUserStatusEnum;
import com.rxinyun.admin.biz.modular.user.mapper.BizUserMapper;
import com.rxinyun.admin.biz.modular.user.param.BizUserAddParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserEditParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserExportParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserGrantRoleParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserIdParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserPageParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserSelectorOrgListParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserSelectorPositionParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserSelectorRoleParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserSelectorUserParam;
import com.rxinyun.admin.biz.modular.user.result.BizUserExportResult;
import com.rxinyun.admin.biz.modular.user.result.BizUserRoleResult;
import com.rxinyun.admin.biz.modular.user.service.BizUserService;
import com.rxinyun.auth.core.util.StpLoginUserUtil;
import com.rxinyun.biz.BizUserApi;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.excel.CommonExcelCustomMergeStrategy;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.listener.CommonDataChangeEventCenter;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.util.CommonAvatarUtil;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.common.util.CommonDownloadUtil;
import com.rxinyun.common.util.CommonEmailUtil;
import com.rxinyun.common.util.CommonResponseUtil;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.sys.api.SysRoleApi;
import com.rxinyun.sys.api.SysUserApi;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BizUserServiceImpl extends ServiceImpl<BizUserMapper, BizUser> implements BizUserService, BizUserApi {
   private static final String RUNXIN_SYS_DEFAULT_PASSWORD_KEY = "RUNXIN_SYS_DEFAULT_PASSWORD";
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private SysUserApi sysUserApi;
   @Resource
   private SysRoleApi sysRoleApi;
   @Resource
   private BizOrgService bizOrgService;
   @Resource
   private BizPositionService bizPositionService;

   @Override
   public Page<BizUser> page(BizUserPageParam bizUserPageParam) {
      QueryWrapper<BizUser> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(bizUserPageParam.getSearchKey())) {
         queryWrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<BizUser>)((LambdaQueryWrapper<BizUser>)q.like(BizUser::getAccount, bizUserPageParam.getSearchKey())).or())
                  .like(BizUser::getName, bizUserPageParam.getSearchKey())
            );
      }

      if (ObjectUtil.isNotEmpty(bizUserPageParam.getOrgId())) {
         queryWrapper.lambda().eq(BizUser::getOrgId, bizUserPageParam.getOrgId());
      }

      if (ObjectUtil.isNotEmpty(bizUserPageParam.getUserStatus())) {
         queryWrapper.lambda().eq(BizUser::getUserStatus, bizUserPageParam.getUserStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{bizUserPageParam.getSortField(), bizUserPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(bizUserPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, bizUserPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(bizUserPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(BizUser::getSortCode);
      }

      queryWrapper.lambda().ne(BizUser::getAccount, BizBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue());
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         queryWrapper.lambda().in(BizUser::getOrgId, loginUserDataScope);
      } else {
         queryWrapper.lambda().eq(BizUser::getId, StpUtil.getLoginIdAsString());
      }

      return (Page<BizUser>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(BizUserAddParam bizUserAddParam) {
      this.checkParam(bizUserAddParam);
      BizUser bizUser = (BizUser)BeanUtil.toBean(bizUserAddParam, BizUser.class);
      if (ObjectUtil.isEmpty(bizUser.getAvatar())) {
         bizUser.setAvatar(CommonAvatarUtil.generateImg(bizUser.getName()));
      }

      bizUser.setPassword(CommonCryptogramUtil.doHashValue(this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_PASSWORD")));
      bizUser.setUserStatus(BizUserStatusEnum.ENABLE.getValue());
      this.save(bizUser);
      CommonDataChangeEventCenter.doAddWithData(BizDataTypeEnum.USER.getValue(), JSONUtil.createArray().put(bizUser));
   }

   private void checkParam(BizUserAddParam bizUserAddParam) {
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         if (!loginUserDataScope.contains(bizUserAddParam.getOrgId())) {
            throw new CommonException("您没有权限在该机构下增加人员，机构id：{}", new Object[]{bizUserAddParam.getOrgId()});
         } else if (this.count((Wrapper<BizUser>)new LambdaQueryWrapper<BizUser>().eq(BizUser::getAccount, bizUserAddParam.getAccount())) > 0L) {
            throw new CommonException("存在重复的账号，账号为：{}", new Object[]{bizUserAddParam.getAccount()});
         } else {
            if (ObjectUtil.isNotEmpty(bizUserAddParam.getPhone())) {
               if (!PhoneUtil.isMobile(bizUserAddParam.getPhone())) {
                  throw new CommonException("手机号码：{}格式错误", new Object[]{bizUserAddParam.getPhone()});
               }

               if (this.count((Wrapper<BizUser>)new LambdaQueryWrapper<BizUser>().eq(BizUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(bizUserAddParam.getPhone()))) > 0L) {
                  throw new CommonException("存在重复的手机号，手机号为：{}", new Object[]{bizUserAddParam.getPhone()});
               }
            }

            if (ObjectUtil.isNotEmpty(bizUserAddParam.getEmail())) {
               if (!CommonEmailUtil.isEmail(bizUserAddParam.getEmail())) {
                  throw new CommonException("邮箱：{}格式错误", new Object[]{bizUserAddParam.getEmail()});
               }

               if (this.count((Wrapper<BizUser>)new LambdaQueryWrapper<BizUser>().eq(BizUser::getEmail, bizUserAddParam.getEmail())) > 0L) {
                  throw new CommonException("存在重复的邮箱，邮箱为：{}", new Object[]{bizUserAddParam.getEmail()});
               }
            }
         }
      } else {
         throw new CommonException("您没有权限在该机构下增加人员，机构id：{}", new Object[]{bizUserAddParam.getOrgId()});
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(BizUserEditParam bizUserEditParam) {
      BizUser bizUser = this.queryEntity(bizUserEditParam.getId());
      this.checkParam(bizUserEditParam);
      boolean updateSuperAdminAccount = bizUser.getAccount().equals(BizBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue())
         && !bizUserEditParam.getAccount().equals(BizBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue());
      if (updateSuperAdminAccount) {
         throw new CommonException("不可修改系统内置超管人员账号", new Object[0]);
      } else {
         BeanUtil.copyProperties(bizUserEditParam, bizUser, new String[0]);
         this.updateById(bizUser);
         CommonDataChangeEventCenter.doUpdateWithData(BizDataTypeEnum.USER.getValue(), JSONUtil.createArray().put(bizUser));
      }
   }

   private void checkParam(BizUserEditParam bizUserEditParam) {
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         if (!loginUserDataScope.contains(bizUserEditParam.getOrgId())) {
            throw new CommonException("您没有权限编辑该机构下的人员，机构id：{}", new Object[]{bizUserEditParam.getOrgId()});
         }
      } else if (!bizUserEditParam.getId().equals(StpUtil.getLoginIdAsString())) {
         throw new CommonException("您没有权限编辑该机构下的人员，机构id：{}", new Object[]{bizUserEditParam.getOrgId()});
      }

      if (this.count(
            (Wrapper<BizUser>)((LambdaQueryWrapper<BizUser>)new LambdaQueryWrapper<BizUser>().eq(BizUser::getAccount, bizUserEditParam.getAccount()))
               .ne(BizUser::getId, bizUserEditParam.getId())
         )
         > 0L) {
         throw new CommonException("存在重复的账号，账号为：{}", new Object[]{bizUserEditParam.getAccount()});
      } else {
         if (ObjectUtil.isNotEmpty(bizUserEditParam.getPhone())) {
            if (!PhoneUtil.isMobile(bizUserEditParam.getPhone())) {
               throw new CommonException("手机号码：{}格式错误", new Object[]{bizUserEditParam.getPhone()});
            }

            if (this.count(
                  (Wrapper<BizUser>)((LambdaQueryWrapper<BizUser>)new LambdaQueryWrapper<BizUser>()
                        .eq(BizUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(bizUserEditParam.getPhone())))
                     .ne(BizUser::getId, bizUserEditParam.getId())
               )
               > 0L) {
               throw new CommonException("存在重复的手机号，手机号为：{}", new Object[]{bizUserEditParam.getPhone()});
            }
         }

         if (ObjectUtil.isNotEmpty(bizUserEditParam.getEmail())) {
            if (!CommonEmailUtil.isEmail(bizUserEditParam.getEmail())) {
               throw new CommonException("邮箱：{}格式错误", new Object[]{bizUserEditParam.getEmail()});
            }

            if (this.count(
                  (Wrapper<BizUser>)((LambdaQueryWrapper<BizUser>)new LambdaQueryWrapper<BizUser>().eq(BizUser::getEmail, bizUserEditParam.getEmail()))
                     .ne(BizUser::getId, bizUserEditParam.getId())
               )
               > 0L) {
               throw new CommonException("存在重复的邮箱，邮箱为：{}", new Object[]{bizUserEditParam.getEmail()});
            }
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<BizUserIdParam> bizUserIdParamList) {
      List<String> bizUserIdList = CollStreamUtil.toList(bizUserIdParamList, BizUserIdParam::getId);
      if (ObjectUtil.isNotEmpty(bizUserIdList)) {
         boolean containsSuperAdminAccount = this.listByIds(bizUserIdList)
            .stream()
            .map(BizUser::getAccount)
            .collect(Collectors.toSet())
            .contains(BizBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue());
         if (containsSuperAdminAccount) {
            throw new CommonException("不可删除系统内置超管人员", new Object[0]);
         }

         Set<String> userOrgIdList = this.listByIds(bizUserIdList).stream().map(BizUser::getOrgId).collect(Collectors.toSet());
         List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
         if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
            if (!loginUserDataScope.containsAll(userOrgIdList)) {
               throw new CommonException("您没有权限删除这些机构下的人员，机构id：{}", new Object[]{CollectionUtil.subtract(userOrgIdList, loginUserDataScope)});
            }
         } else if (bizUserIdList.size() != 1 || !bizUserIdList.get(0).equals(StpUtil.getLoginIdAsString())) {
            throw new CommonException("您没有权限删除这些机构下的人员，机构id：{}", new Object[]{userOrgIdList});
         }

         this.update((Wrapper<BizUser>)((LambdaUpdateWrapper<BizUser>)new LambdaUpdateWrapper<BizUser>().in(BizUser::getDirectorId, bizUserIdList)).set(BizUser::getDirectorId, null));
         this.list((Wrapper<BizUser>)new LambdaQueryWrapper<BizUser>().isNotNull(BizUser::getPositionJson))
            .forEach(
               bizUser -> {
                  List<JSONObject> handledJsonObjectList = JSONUtil.toList(JSONUtil.parseArray(bizUser.getPositionJson()), JSONObject.class)
                     .stream()
                     .peek(jsonObject -> {
                        String directorId = jsonObject.getStr("directorId");
                        if (ObjectUtil.isNotEmpty(directorId) && bizUserIdList.contains(directorId)) {
                           jsonObject.remove("directorId");
                        }
                     })
                     .collect(Collectors.toList());
                  this.update(
                     (Wrapper<BizUser>)((LambdaUpdateWrapper<BizUser>)new LambdaUpdateWrapper<BizUser>().eq(BizUser::getId, bizUser.getId()))
                        .set(BizUser::getPositionJson, JSONUtil.toJsonStr(handledJsonObjectList))
                  );
               }
            );
         this.bizOrgService
            .update((Wrapper<BizOrg>)((LambdaUpdateWrapper<BizOrg>)new LambdaUpdateWrapper<BizOrg>().in(BizOrg::getDirectorId, bizUserIdList)).set(BizOrg::getDirectorId, null));
         this.removeByIds(bizUserIdList);
         CommonDataChangeEventCenter.doDeleteWithDataId(BizDataTypeEnum.USER.getValue(), bizUserIdList);
      }
   }

   @Override
   public BizUser detail(BizUserIdParam bizUserIdParam) {
      return this.queryEntity(bizUserIdParam.getId());
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void disableUser(BizUserIdParam bizUserIdParam) {
      BizUser bizUser = this.detail(bizUserIdParam);
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         if (!loginUserDataScope.contains(bizUser.getOrgId())) {
            throw new CommonException("您没有权限禁用该机构下的人员：{}，机构id：{}", new Object[]{bizUser.getName(), bizUser.getOrgId()});
         }
      } else if (!bizUser.getId().equals(StpUtil.getLoginIdAsString())) {
         throw new CommonException("您没有权限禁用该机构下的人员：{}，机构id：{}", new Object[]{bizUser.getName(), bizUser.getOrgId()});
      }

      this.update(
         (Wrapper<BizUser>)((LambdaUpdateWrapper<BizUser>)new LambdaUpdateWrapper<BizUser>().eq(BizUser::getId, bizUserIdParam.getId()))
            .set(BizUser::getUserStatus, BizUserStatusEnum.DISABLED.getValue())
      );
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void enableUser(BizUserIdParam bizUserIdParam) {
      BizUser bizUser = this.detail(bizUserIdParam);
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         if (!loginUserDataScope.contains(bizUser.getOrgId())) {
            throw new CommonException("您没有权限启用该机构下的人员：{}，机构id：{}", new Object[]{bizUser.getName(), bizUser.getOrgId()});
         }
      } else if (!bizUser.getId().equals(StpUtil.getLoginIdAsString())) {
         throw new CommonException("您没有权限启用该机构下的人员：{}，机构id：{}", new Object[]{bizUser.getName(), bizUser.getOrgId()});
      }

      this.update(
         (Wrapper<BizUser>)((LambdaUpdateWrapper<BizUser>)new LambdaUpdateWrapper<BizUser>().eq(BizUser::getId, bizUserIdParam.getId()))
            .set(BizUser::getUserStatus, BizUserStatusEnum.ENABLE.getValue())
      );
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void resetPassword(BizUserIdParam bizUserIdParam) {
      BizUser bizUser = this.detail(bizUserIdParam);
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         if (!loginUserDataScope.contains(bizUser.getOrgId())) {
            throw new CommonException("您没有权限为该机构下的人员：{}重置密码，机构id：{}", new Object[]{bizUser.getName(), bizUser.getOrgId()});
         }
      } else if (!bizUser.getId().equals(StpUtil.getLoginIdAsString())) {
         throw new CommonException("您没有权限为该机构下的人员：{}重置密码，机构id：{}", new Object[]{bizUser.getName(), bizUser.getOrgId()});
      }

      this.update(
         (Wrapper<BizUser>)((LambdaUpdateWrapper<BizUser>)new LambdaUpdateWrapper<BizUser>().eq(BizUser::getId, bizUserIdParam.getId()))
            .set(BizUser::getPassword, CommonCryptogramUtil.doHashValue(this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_PASSWORD")))
      );
   }

   @Override
   public List<String> ownRole(BizUserIdParam bizUserIdParam) {
      return this.sysUserApi.ownRole(bizUserIdParam.getId());
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void grantRole(BizUserGrantRoleParam bizUserGrantRoleParam) {
      BizUser bizUser = this.queryEntity(bizUserGrantRoleParam.getId());
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         if (!loginUserDataScope.contains(bizUser.getOrgId())) {
            throw new CommonException("您没有权限为该机构下的人员：{}授权角色，机构id：{}", new Object[]{bizUser.getName(), bizUser.getOrgId()});
         }
      } else if (!bizUser.getId().equals(StpUtil.getLoginIdAsString())) {
         throw new CommonException("您没有权限为该机构下的人员：{}授权角色，机构id：{}", new Object[]{bizUser.getName(), bizUser.getOrgId()});
      }

      this.sysUserApi.grantRole(bizUserGrantRoleParam.getId(), bizUserGrantRoleParam.getRoleIdList());
   }

   @Override
   public void exportUser(BizUserExportParam bizUserExportParam, HttpServletResponse response) throws IOException {
      File tempFile = null;

      try {
         QueryWrapper<BizUser> queryWrapper = new QueryWrapper().checkSqlInjection();
         queryWrapper.lambda().ne(BizUser::getAccount, BizBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue());
         List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
         if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
            queryWrapper.lambda().in(BizUser::getOrgId, loginUserDataScope);
         } else {
            queryWrapper.lambda().eq(BizUser::getId, StpUtil.getLoginIdAsString());
         }

         if (ObjectUtil.isNotEmpty(bizUserExportParam.getUserIds())) {
            queryWrapper.lambda().in(BizUser::getId, StrUtil.split(bizUserExportParam.getUserIds(), ","));
         } else {
            if (ObjectUtil.isNotEmpty(bizUserExportParam.getSearchKey())) {
               queryWrapper.lambda()
                  .and(
                     q -> ((LambdaQueryWrapper<BizUser>)((LambdaQueryWrapper<BizUser>)((LambdaQueryWrapper<BizUser>)((LambdaQueryWrapper<BizUser>)q.like(
                                    BizUser::getAccount, bizUserExportParam.getSearchKey()
                                 ))
                                 .or())
                              .like(BizUser::getName, bizUserExportParam.getSearchKey()))
                           .or())
                        .like(BizUser::getPhone, bizUserExportParam.getSearchKey())
                  );
            }

            if (ObjectUtil.isNotEmpty(bizUserExportParam.getUserStatus())) {
               queryWrapper.lambda().eq(BizUser::getUserStatus, bizUserExportParam.getUserStatus());
            }
         }

         String fileName = "RUNXIN系统B端人员信息清单.xlsx";
         List<BizUser> bizUserList = this.list(queryWrapper);
         if (ObjectUtil.isEmpty(bizUserList)) {
            throw new CommonException("无数据可导出", new Object[0]);
         }

         bizUserList = CollectionUtil.sort(bizUserList, Comparator.comparing(BizUser::getOrgId));
         List<BizUserExportResult> bizUserExportResultList = bizUserList.stream().map(bizUser -> {
            BizUserExportResult bizUserExportResult = new BizUserExportResult();
            BeanUtil.copyProperties(bizUser, bizUserExportResult, new String[0]);
            bizUserExportResult.setGroupName(ObjectUtil.isNotEmpty(bizUserExportResult.getOrgName()) ? bizUserExportResult.getOrgName() : "无组织");
            bizUserExportResult.setUserStatus(bizUserExportResult.getUserStatus().equalsIgnoreCase(BizUserStatusEnum.ENABLE.getValue()) ? "正常" : "停用");
            if (ObjectUtil.isNotEmpty(bizUser.getAvatar())) {
               bizUserExportResult.setAvatar(ImgUtil.toBytes(ImgUtil.toImage((String)StrUtil.split(bizUser.getAvatar(), ",").get(1)), "png"));
            }

            return bizUserExportResult;
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
                              tempFile.getPath(), BizUserExportResult.class
                           )
                           .registerWriteHandler(horizontalCellStyleStrategy))
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()))
                     .registerWriteHandler(
                        new CommonExcelCustomMergeStrategy(
                           bizUserExportResultList.stream().map(BizUserExportResult::getGroupName).collect(Collectors.toList()), 0
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
            .sheet("人员信息")
            .doWrite(bizUserExportResultList);
         CommonDownloadUtil.download(tempFile, response);
      } catch (Exception var17) {
         this.log.error(">>> 人员导出异常：", var17);
         CommonResponseUtil.renderError(response, "导出失败");
      } finally {
         FileUtil.del(tempFile);
      }
   }

   @Override
   public void exportUserInfo(BizUserIdParam bizUserIdParam, HttpServletResponse response) throws IOException {
      File destTemplateFile = null;
      File resultFile = null;

      try {
         BizUser bizUser = this.queryEntity(bizUserIdParam.getId());
         InputStream inputStream = POICacheManager.getFile("userExportTemplate.docx");
         destTemplateFile = FileUtil.writeFromStream(inputStream, FileUtil.file(FileUtil.getTmpDir() + File.separator + "userExportTemplate.docx"));
         Map<String, Object> map = BeanUtil.beanToMap(bizUser, new String[0]);
         String avatarBase64;
         if (ObjectUtil.isNotEmpty(bizUser.getAvatar())) {
            avatarBase64 = bizUser.getAvatar();
         } else {
            avatarBase64 = CommonAvatarUtil.generateImg(bizUser.getName());
         }

         ImageEntity imageEntity = new ImageEntity(ImgUtil.toBytes(ImgUtil.toImage((String)StrUtil.split(avatarBase64, ",").get(1)), "png"), 120, 160);
         map.put("avatar", imageEntity);
         if (ObjectUtil.isNotEmpty(bizUser.getBirthday())) {
            try {
               long age = DateUtil.betweenYear(DateUtil.parseDate(bizUser.getBirthday()), TimeUtil.toDate(TimeUtil.now()), true);
               if (age != 0L) {
                  map.put("age", age + "岁");
               }
            } catch (Exception var16) {
            }
         }

         map.put("exportDateTime", TimeUtil.format(TimeUtil.now(), "yyyy年MM月dd日"));
         XWPFDocument doc = WordExportUtil.exportWord07(destTemplateFile.getAbsolutePath(), map);
         resultFile = FileUtil.file(FileUtil.getTmpDir() + File.separator + "RUNXIN系统B端人员信息_" + bizUser.getName() + ".docx");
         BufferedOutputStream outputStream = FileUtil.getOutputStream(resultFile);
         doc.write(outputStream);
         outputStream.close();
         CommonDownloadUtil.download(resultFile, response);
      } catch (Exception var17) {
         this.log.error(">>> 导出人员个人信息异常：", var17);
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
   public BizUser queryEntity(String id) {
      BizUser bizUser = (BizUser)this.getById(id);
      if (ObjectUtil.isEmpty(bizUser)) {
         throw new CommonException("人员不存在，id值为：{}", new Object[]{id});
      } else {
         return bizUser;
      }
   }

   @Override
   public List<Tree<String>> orgTreeSelector() {
      LambdaQueryWrapper<BizOrg> lambdaQueryWrapper = new LambdaQueryWrapper();
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      Set<BizOrg> bizOrgSet = CollectionUtil.newHashSet(new BizOrg[0]);
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         List<BizOrg> allOrgList = this.bizOrgService.list();
         loginUserDataScope.forEach(orgId -> bizOrgSet.addAll(this.bizOrgService.getParentListById(allOrgList, orgId, true)));
         List<String> loginUserDataScopeFullList = bizOrgSet.stream().map(BizOrg::getId).collect(Collectors.toList());
         lambdaQueryWrapper.in(BizOrg::getId, loginUserDataScopeFullList);
         lambdaQueryWrapper.orderByAsc(BizOrg::getSortCode);
         List<BizOrg> filteredOrgList = this.bizOrgService.list(lambdaQueryWrapper);
         List<TreeNode<String>> treeNodeList = filteredOrgList.stream()
            .map(bizOrg -> new TreeNode<String>(bizOrg.getId(), bizOrg.getParentId(), bizOrg.getName(), bizOrg.getSortCode()))
            .collect(Collectors.toList());
         return TreeUtil.build(treeNodeList, "0");
      } else {
         return CollectionUtil.newArrayList(new Tree[0]);
      }
   }

   @Override
   public Page<BizOrg> orgListSelector(BizUserSelectorOrgListParam bizUserSelectorOrgListParam) {
      LambdaQueryWrapper<BizOrg> lambdaQueryWrapper = new LambdaQueryWrapper();
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         lambdaQueryWrapper.in(BizOrg::getId, loginUserDataScope);
         lambdaQueryWrapper.select(BizOrg::getId, BizOrg::getParentId, BizOrg::getName, BizOrg::getCategory, BizOrg::getSortCode);
         if (ObjectUtil.isNotEmpty(bizUserSelectorOrgListParam.getParentId())) {
            lambdaQueryWrapper.eq(BizOrg::getParentId, bizUserSelectorOrgListParam.getParentId());
         }

         if (ObjectUtil.isNotEmpty(bizUserSelectorOrgListParam.getSearchKey())) {
            lambdaQueryWrapper.like(BizOrg::getName, bizUserSelectorOrgListParam.getSearchKey());
         }

         lambdaQueryWrapper.orderByAsc(BizOrg::getSortCode);
         return (Page<BizOrg>)this.bizOrgService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
      } else {
         return new Page();
      }
   }

   @Override
   public Page<BizPosition> positionSelector(BizUserSelectorPositionParam bizUserSelectorPositionParam) {
      LambdaQueryWrapper<BizPosition> lambdaQueryWrapper = new LambdaQueryWrapper();
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         lambdaQueryWrapper.in(BizPosition::getOrgId, loginUserDataScope);
         lambdaQueryWrapper.select(
            BizPosition::getId, BizPosition::getOrgId, BizPosition::getName, BizPosition::getCategory, BizPosition::getSortCode
         );
         if (ObjectUtil.isNotEmpty(bizUserSelectorPositionParam.getOrgId())) {
            lambdaQueryWrapper.eq(BizPosition::getOrgId, bizUserSelectorPositionParam.getOrgId());
         }

         if (ObjectUtil.isNotEmpty(bizUserSelectorPositionParam.getSearchKey())) {
            lambdaQueryWrapper.like(BizPosition::getName, bizUserSelectorPositionParam.getSearchKey());
         }

         lambdaQueryWrapper.orderByAsc(BizPosition::getSortCode);
         return (Page<BizPosition>)this.bizPositionService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
      } else {
         return new Page();
      }
   }

   @Override
   public Page<BizUserRoleResult> roleSelector(BizUserSelectorRoleParam bizUserSelectorRoleParam) {
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         if (ObjectUtil.isNotEmpty(bizUserSelectorRoleParam.getOrgId())) {
            return loginUserDataScope.contains(bizUserSelectorRoleParam.getOrgId())
               ? (Page)BeanUtil.toBean(
                  this.sysRoleApi
                     .roleSelector(
                        bizUserSelectorRoleParam.getOrgId(),
                        bizUserSelectorRoleParam.getCategory(),
                        bizUserSelectorRoleParam.getSearchKey(),
                        loginUserDataScope,
                        true
                     ),
                  Page.class
               )
               : new Page();
         } else {
            return ObjectUtil.isNotEmpty(bizUserSelectorRoleParam.getCategory())
                  & BizRoleCategoryEnum.GLOBAL.getValue().equals(bizUserSelectorRoleParam.getCategory())
               ? (Page)BeanUtil.toBean(
                  this.sysRoleApi.roleSelector(null, bizUserSelectorRoleParam.getCategory(), bizUserSelectorRoleParam.getSearchKey(), null, true), Page.class
               )
               : (Page)BeanUtil.toBean(
                  this.sysRoleApi.roleSelector(null, bizUserSelectorRoleParam.getCategory(), bizUserSelectorRoleParam.getSearchKey(), loginUserDataScope, true),
                  Page.class
               );
         }
      } else {
         return new Page();
      }
   }

   @Override
   public Page<BizUser> userSelector(BizUserSelectorUserParam bizUserSelectorUserParam) {
      LambdaQueryWrapper<BizUser> lambdaQueryWrapper = new LambdaQueryWrapper();
      List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
      if (ObjectUtil.isNotEmpty(loginUserDataScope)) {
         lambdaQueryWrapper.in(BizUser::getOrgId, loginUserDataScope);
         lambdaQueryWrapper.select(
            
               BizUser::getId,
               BizUser::getAvatar,
               BizUser::getOrgId,
               BizUser::getPositionId,
               BizUser::getAccount,
               BizUser::getName,
               BizUser::getSortCode,
               BizUser::getGender,
               BizUser::getEntryDate
            
         );
         if (ObjectUtil.isNotEmpty(bizUserSelectorUserParam.getOrgId())) {
            List<String> childOrgIdList = CollStreamUtil.toList(
               this.bizOrgService.getChildListById(this.bizOrgService.getAllOrgList(), bizUserSelectorUserParam.getOrgId(), true), BizOrg::getId
            );
            if (!ObjectUtil.isNotEmpty(childOrgIdList)) {
               return new Page();
            }

            lambdaQueryWrapper.in(BizUser::getOrgId, childOrgIdList);
         }

         if (ObjectUtil.isNotEmpty(bizUserSelectorUserParam.getSearchKey())) {
            lambdaQueryWrapper.like(BizUser::getName, bizUserSelectorUserParam.getSearchKey());
         }

         lambdaQueryWrapper.orderByAsc(BizUser::getSortCode);
         return (Page<BizUser>)this.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
      } else {
         return new Page();
      }
   }

   public boolean verifyPassword(String adminId, String password) {
      BizUser bizUser = (BizUser)this.getById(adminId);
      return bizUser.getPassword().equals(password);
   }
}
