package com.rxinyun.member.auth.modular.real.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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
import com.rxinyun.member.auth.modular.real.entity.PersonalRealInfo;
import com.rxinyun.member.auth.modular.real.mapper.MemberPersonalRealInfoMapper;
import com.rxinyun.member.auth.modular.real.param.PersonalRealInfoAddOrUpdateParam;
import com.rxinyun.member.auth.modular.real.param.PersonalRealInfoIdParam;
import com.rxinyun.member.auth.modular.real.param.PersonalRealInfoPageParam;
import com.rxinyun.member.auth.modular.real.service.MemberPersonalRealInfoService;
import com.rxinyun.member.auth.modular.real.service.RealNameVerificationService;
import com.rxinyun.member.auth.modular.user.service.ClientUserService;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.api.PersonalRealApi;
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
public class MemberPersonalRealInfoServiceImpl
   extends ServiceImpl<MemberPersonalRealInfoMapper, PersonalRealInfo>
   implements MemberPersonalRealInfoService,
   PersonalRealApi {
   private static final Logger log = LoggerFactory.getLogger(MemberPersonalRealInfoServiceImpl.class);
   @Resource(
      name = "realNameVerificationServiceImpl"
   )
   private RealNameVerificationService faceVerificationService;
   @Resource
   private ClientUserService clientUserService;

   @Override
   public Page<PersonalRealInfo> page(PersonalRealInfoPageParam pageParam) {
      QueryWrapper<PersonalRealInfo> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(PersonalRealInfo::getName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<PersonalRealInfo>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   private void verifyParam(PersonalRealInfoAddOrUpdateParam personalRealInfoAddParam) {
      if (personalRealInfoAddParam.getCertType().equals("IDENTITY_CARD") && !Validator.isCitizenId(personalRealInfoAddParam.getCertNumber())) {
         throw new CommonException("身份证号码：{}格式错误", new Object[]{personalRealInfoAddParam.getCertNumber()});
      } else if (!Validator.isMobile(personalRealInfoAddParam.getPhoneNumber())) {
         throw new CommonException("手机号码：{}格式错误", new Object[]{personalRealInfoAddParam.getPhoneNumber()});
      }
   }

   @Transactional(
      rollbackFor = {Exception.class, CommonException.class}
   )
   @Override
   public CommonResult addOrUpdate(PersonalRealInfoAddOrUpdateParam PersonalRealInfoAddParam) {
      this.verifyParam(PersonalRealInfoAddParam);

      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         PersonalRealInfo personalRealInfo = (PersonalRealInfo)BeanUtil.toBean(PersonalRealInfoAddParam, PersonalRealInfo.class);
         personalRealInfo.setMemberNo(clientLoginUser.getId());
         personalRealInfo.setStatus(CertificationStatusEnum.PENDING.getKey());
         QueryWrapper<PersonalRealInfo> queryWrapper = new QueryWrapper();
         queryWrapper.eq("member_no", clientLoginUser.getId());
         PersonalRealInfo existingRecord = (PersonalRealInfo)this.getOne(queryWrapper);
         if (existingRecord == null) {
            this.save(personalRealInfo);
         } else {
            personalRealInfo.setId(existingRecord.getId());
            this.updateById(personalRealInfo);
         }

         log.info("新增人脸核验认证信息：{}", personalRealInfo);
         String certifyId = this.faceVerificationService.initializeFaceVerification(personalRealInfo.getName(), personalRealInfo.getCertNumber());
         log.info("完善用户信息：用户ID{},用户名称{}", clientLoginUser.getId(), personalRealInfo.getName());
         this.clientUserService.updateByRealUser(clientLoginUser.getId(), personalRealInfo.getName(), UserTypeEnum.PERSONAL.getKey());
         return CommonResult.data(certifyId);
      } catch (CommonException var7) {
         log.error("新增人脸核验认证信息失败，参数校验失败，错误信息为：{}", var7.getMessage(), var7);
         return CommonResult.error(var7.getMessage());
      } catch (Exception var8) {
         log.error("新增人脸核验认证信息失败，参数校验失败，错误信息为：{}", var8.getMessage(), var8);
         throw new RuntimeException("新增人脸核验认证信息失败", var8);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult updateByStatus(Long memberNo, String status) {
      (((this.lambdaUpdate().eq(PersonalRealInfo::getMemberNo, memberNo))
               .eq(PersonalRealInfo::getStatus, CertificationStatusEnum.PENDING.getKey()))
            .set(PersonalRealInfo::getStatus, status))
         .update();
      return CommonResult.ok();
   }

   @Override
   public PersonalRealInfo detail(PersonalRealInfoIdParam PersonalRealInfoIdParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      return (PersonalRealInfo)this.getOne((Wrapper<PersonalRealInfo>)new LambdaQueryWrapper<PersonalRealInfo>().eq(PersonalRealInfo::getMemberNo, clientLoginUser.getId()));
   }

   @Override
   public PersonalRealInfo queryEntity(String id) {
      return (PersonalRealInfo)this.getById(id);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<PersonalRealInfoIdParam> PersonalRealInfoIdParamList) {
      List<String> serverLocalLineIdList = CollStreamUtil.toList(PersonalRealInfoIdParamList, PersonalRealInfoIdParam::getId);
      if (ObjectUtil.isNotEmpty(serverLocalLineIdList)) {
         this.removeByIds(serverLocalLineIdList);
      }
   }

   @Override
   public PersonalRealInfo seleteByMemberNo(Long memberNo) {
      return (PersonalRealInfo)this.getOne((Wrapper<PersonalRealInfo>)new LambdaQueryWrapper<PersonalRealInfo>().eq(PersonalRealInfo::getMemberNo, memberNo));
   }

   public Long getPersonalRealCount(HashMap<String, LocalDateTime> map) {
      LocalDateTime start = map.get(DateResponseKeyEnum.START_TIME_NAME.getValue());
      LocalDateTime end = map.get(DateResponseKeyEnum.END_TIME_NAME.getValue());
      return (((this.lambdaQuery().ge(CommonEntity::getUpdateTime, start))
               .le(CommonEntity::getUpdateTime, end))
            .eq(PersonalRealInfo::getStatus, CertificationStatusEnum.APPROVED.getKey()))
         .count();
   }
}
