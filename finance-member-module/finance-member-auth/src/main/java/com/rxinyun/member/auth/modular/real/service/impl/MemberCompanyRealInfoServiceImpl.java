package com.rxinyun.member.auth.modular.real.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CertificationStatusEnum;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.UserTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.date.enums.DateResponseKeyEnum;
import com.rxinyun.member.auth.modular.real.entity.CompanyRealInfo;
import com.rxinyun.member.auth.modular.real.mapper.MemberCompanyRealInfoMapper;
import com.rxinyun.member.auth.modular.real.param.CompanyRealInfoAddOrUpdateParam;
import com.rxinyun.member.auth.modular.real.param.CompanyRealInfoIdParam;
import com.rxinyun.member.auth.modular.real.param.CompanyRealInfoPageParam;
import com.rxinyun.member.auth.modular.real.service.MemberCompanyRealInfoService;
import com.rxinyun.member.auth.modular.real.service.RealNameVerificationService;
import com.rxinyun.member.auth.modular.user.service.ClientUserService;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.api.CompanyRealApi;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberCompanyRealInfoServiceImpl
   extends ServiceImpl<MemberCompanyRealInfoMapper, CompanyRealInfo>
   implements MemberCompanyRealInfoService,
   CompanyRealApi {
   private static final Logger log = LoggerFactory.getLogger(MemberCompanyRealInfoServiceImpl.class);
   @Resource(
      name = "realNameVerificationServiceImpl"
   )
   private RealNameVerificationService faceVerificationService;
   @Resource
   private ClientUserService clientUserService;

   @Override
   public Page<CompanyRealInfo> page(CompanyRealInfoPageParam pageParam) {
      QueryWrapper<CompanyRealInfo> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(CompanyRealInfo::getCompanyName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<CompanyRealInfo>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class, CommonException.class, AlipayApiException.class}
   )
   @Override
   public CommonResult addOrUpdate(CompanyRealInfoAddOrUpdateParam companyRealInfoAddParam) {
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         CompanyRealInfo companyRealInfo = (CompanyRealInfo)BeanUtil.toBean(companyRealInfoAddParam, CompanyRealInfo.class);
         companyRealInfo.setMemberNo(clientLoginUser.getId());
         companyRealInfo.setStatus(CertificationStatusEnum.PENDING.getKey());
         QueryWrapper<CompanyRealInfo> queryWrapper = new QueryWrapper();
         queryWrapper.eq("member_no", clientLoginUser.getId());
         CompanyRealInfo existingRecord = (CompanyRealInfo)this.getOne(queryWrapper);
         if (existingRecord == null) {
            this.save(companyRealInfo);
         } else {
            companyRealInfo.setId(existingRecord.getId());
            this.updateById(companyRealInfo);
         }

         log.info("新增人脸核验认证信息：{}", companyRealInfo);
         String certifyId = null;
         if (null != companyRealInfo.getLegalRepresentative() && null != companyRealInfo.getLegalRepresentativeNumber()) {
            log.info("认证信息为法人，开始认证:{}", JSON.toJSONString(companyRealInfo));
            certifyId = this.faceVerificationService
               .initializeFaceVerification(companyRealInfo.getLegalRepresentative(), companyRealInfo.getLegalRepresentativeNumber());
            log.info("负责人返回的认证ID为：{}", certifyId);
         } else {
            if (null == companyRealInfo.getContactPerson() || null == companyRealInfo.getContactPersonNumber()) {
               return CommonResult.error("认证信息不能为空");
            }

            log.info("认证信息为负责人，开始认证:{}", JSON.toJSONString(companyRealInfo));
            certifyId = this.faceVerificationService.initializeFaceVerification(companyRealInfo.getContactPerson(), companyRealInfo.getContactPersonNumber());
            log.info("负责人返回的认证ID为：{}", certifyId);
         }

         log.info("返回的认证ID为：{}", certifyId);
         return CommonResult.data(certifyId);
      } catch (CommonException var7) {
         log.error("新增人脸核验认证信息失败，参数校验失败，错误信息为：{}", var7.getMessage(), var7);
         return CommonResult.error(var7.getMessage());
      } catch (Exception var8) {
         log.error("新增人脸核验认证信息失败，参数校验失败，错误信息为：{}", var8.getMessage(), var8);
         throw new RuntimeException("新增人脸核验认证信息失败", var8);
      }
   }

   @Override
   public CompanyRealInfo detail(CompanyRealInfoIdParam companyRealInfoIdParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      return (CompanyRealInfo)this.getOne((Wrapper<CompanyRealInfo>)new LambdaQueryWrapper<CompanyRealInfo>().eq(CompanyRealInfo::getMemberNo, clientLoginUser.getId()));
   }

   @Override
   public CompanyRealInfo queryEntity(String id) {
      return (CompanyRealInfo)this.getById(id);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<CompanyRealInfoIdParam> companyRealInfoIdParamList) {
      List<String> serverLocalLineIdList = CollStreamUtil.toList(companyRealInfoIdParamList, CompanyRealInfoIdParam::getId);
      if (ObjectUtil.isNotEmpty(serverLocalLineIdList)) {
         this.removeByIds(serverLocalLineIdList);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult updateByStatus(Long memberNo, String status) {
      return this.updateByStatus(memberNo, status, null);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult updateByStatus(Long memberNo, String status, String reason) {
      ((((this.lambdaUpdate()
                     .eq(CompanyRealInfo::getMemberNo, memberNo))
                  .eq(CompanyRealInfo::getStatus, CertificationStatusEnum.PENDING.getKey()))
               .set(CompanyRealInfo::getStatus, status))
            .set(reason != null, CompanyRealInfo::getReason, reason))
         .update();
      if (CertificationStatusEnum.APPROVED.getKey().equals(status)) {
         CompanyRealInfo companyRealInfo = this.seleteByMemberNo(memberNo);
         if (companyRealInfo != null && companyRealInfo.getCompanyName() != null) {
            this.clientUserService.updateByRealUser(memberNo, companyRealInfo.getCompanyName(), UserTypeEnum.ENTERPRISE.getKey());
         }
      }

      return CommonResult.ok();
   }

   @Override
   public CompanyRealInfo seleteByMemberNo(Long memberNo) {
      return (CompanyRealInfo)this.getOne((Wrapper<CompanyRealInfo>)new LambdaQueryWrapper<CompanyRealInfo>().eq(CompanyRealInfo::getMemberNo, memberNo));
   }

   public Long getCompanyRealCount(HashMap<String, LocalDateTime> map) {
      LocalDateTime start = map.get(DateResponseKeyEnum.START_TIME_NAME.getValue());
      LocalDateTime end = map.get(DateResponseKeyEnum.END_TIME_NAME.getValue());
      return (((this.lambdaQuery().ge(CommonEntity::getUpdateTime, start))
               .le(CommonEntity::getUpdateTime, end))
            .eq(CompanyRealInfo::getStatus, CertificationStatusEnum.APPROVED.getKey()))
         .count();
   }
}
