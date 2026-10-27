package com.rxinyun.member.auth.modular.real.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.rxinyun.common.enums.CertificationStatusEnum;
import com.rxinyun.common.enums.UserTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.auth.modular.real.entity.CompanyRealInfo;
import com.rxinyun.member.auth.modular.real.entity.PersonalRealInfo;
import com.rxinyun.member.auth.modular.real.mapper.MemberCompanyRealInfoMapper;
import com.rxinyun.member.auth.modular.real.mapper.MemberPersonalRealInfoMapper;
import com.rxinyun.member.auth.modular.real.service.McloudRealNameVerificationService;
import com.rxinyun.member.auth.modular.user.service.ClientUserService;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import jakarta.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class McloudRealNameVerificationServiceImpl implements McloudRealNameVerificationService {
   private static final Logger log = LoggerFactory.getLogger(McloudRealNameVerificationServiceImpl.class);
   private static final String MCLOUD_REAL_API = "MCLOUD_REAL_API";
   private static final String MCLOUD_REAL_KEY = "MCLOUD_REAL_KEY";
   private static final String MCLOUD_REAL_GATEWAY_URL = "https://idc.stay33.cn/realname/certapi.php";
   private static final String ACTION_INITIALIZE = "initialize";
   private static final String ACTION_CERTIFY = "certify";
   private static final String ACTION_QUERY = "query";
   private static final String QUERY_STATUS_PASSED = "通过";
   private static final int HTTP_TIMEOUT = 15000;
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private MemberPersonalRealInfoMapper memberPersonalRealInfoMapper;
   @Resource
   private MemberCompanyRealInfoMapper memberCompanyRealInfoMapper;
   @Resource
   private ClientUserService clientUserService;

   @Override
   public String initializeFaceVerification(String certName, String certNo) {
      JSONObject response = this.requestMcloud("initialize", null, certName, certNo);
      String certifyId = response.getString("certify_id");
      if (StrUtil.isBlank(certifyId)) {
         throw new CommonException("MCloud实名认证初始化失败：认证ID为空", new Object[0]);
      } else {
         log.info("MCloud实名认证初始化成功，certName={}，certifyId={}", certName, certifyId);
         return certifyId;
      }
   }

   @Override
   public CommonResult certifyFace(String certifyId) {
      try {
         JSONObject response = this.requestMcloud("certify", certifyId, null, null);
         String certifyUrl = response.getString("url");
         return StrUtil.isBlank(certifyUrl) ? CommonResult.error("MCloud实名认证表单地址为空") : CommonResult.data(certifyUrl);
      } catch (CommonException var4) {
         log.error("MCloud实名认证生成表单失败，certifyId={}", certifyId, var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult certifyQuery(String certifyId) {
      try {
         JSONObject response = this.requestMcloud("query", certifyId, null, null);
         JSONObject data = response.getJSONObject("data");
         if (!ObjectUtil.isEmpty(data) && "通过".equals(data.getString("status"))) {
            SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
            this.approveRealInfo(clientLoginUser, data);
            this.clientUserService.updateByRealStatus(clientLoginUser.getId());
            return CommonResult.ok("认证成功");
         } else {
            return CommonResult.error(StrUtil.blankToDefault(response.getString("msg"), "认证不通过"));
         }
      } catch (CommonException var5) {
         log.error("MCloud实名认证查询失败，certifyId={}", certifyId, var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   private JSONObject requestMcloud(String action, String certifyId, String certName, String certNo) {
      Map<String, Object> params = new LinkedHashMap<>();
      params.put("api", this.requiredConfig("MCLOUD_REAL_API", "MCloud实名认证APPID未配置"));
      params.put("key", this.requiredConfig("MCLOUD_REAL_KEY", "MCloud实名认证密钥未配置"));
      params.put("action", action);
      if (StrUtil.isNotBlank(certName) && StrUtil.isNotBlank(certNo)) {
         params.put("cert_name", certName);
         params.put("real_name", certName);
         params.put("cert_no", certNo);
         params.put("id_card", certNo);
      }

      if (StrUtil.isNotBlank(certifyId)) {
         params.put("certify_id", certifyId);
      }

      try {
         HttpResponse httpResponse = HttpRequest.post("https://idc.stay33.cn/realname/certapi.php").form(params).timeout(15000).execute();

         JSONObject message;
         try {
            String body = httpResponse.body();
            log.info("MCloud实名认证响应：action={}，body={}", action, body);
            JSONObject response = JSON.parseObject(body);
            if (response == null || response.getIntValue("status") != 200) {
               String messagex = response == null ? "响应为空" : response.getString("msg");
               throw new CommonException("MCloud实名认证请求失败：{}", new Object[]{StrUtil.blankToDefault(messagex, "未知错误")});
            }

            message = response;
         } catch (Throwable var11) {
            if (httpResponse != null) {
               try {
                  httpResponse.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (httpResponse != null) {
            httpResponse.close();
         }

         return message;
      } catch (CommonException var12) {
         throw var12;
      } catch (Exception var13) {
         log.error("MCloud实名认证请求异常，action={}，certifyId={}", new Object[]{action, certifyId, var13});
         throw new CommonException("MCloud实名认证请求异常", new Object[0]);
      }
   }

   private void approveRealInfo(SaBaseClientLoginUser clientLoginUser, JSONObject data) {
      String realName = data.getString("real_name");
      String idCard = data.getString("id_card");
      if (UserTypeEnum.PERSONAL.getKey().equals(clientLoginUser.getUserType())) {
         PersonalRealInfo personalRealInfo = (PersonalRealInfo)this.memberPersonalRealInfoMapper
            .selectOne((Wrapper<PersonalRealInfo>)new LambdaQueryWrapper<PersonalRealInfo>().eq(PersonalRealInfo::getMemberNo, clientLoginUser.getId()));
         if (personalRealInfo != null && this.isMcloudIdentityMatched(realName, idCard, personalRealInfo.getName(), personalRealInfo.getCertNumber())) {
            this.memberPersonalRealInfoMapper
               .update(
                  null,
                  (Wrapper<PersonalRealInfo>)((LambdaUpdateWrapper<PersonalRealInfo>)((LambdaUpdateWrapper<PersonalRealInfo>)new LambdaUpdateWrapper<PersonalRealInfo>().eq(PersonalRealInfo::getMemberNo, clientLoginUser.getId()))
                        .eq(PersonalRealInfo::getStatus, CertificationStatusEnum.PENDING.getKey()))
                     .set(PersonalRealInfo::getStatus, CertificationStatusEnum.APPROVED.getKey())
               );
            this.clientUserService.updateByRealUser(clientLoginUser.getId(), personalRealInfo.getName(), UserTypeEnum.PERSONAL.getKey());
         } else {
            throw new CommonException("MCloud实名认证信息与提交的个人实名信息不一致", new Object[0]);
         }
      } else if (UserTypeEnum.ENTERPRISE.getKey().equals(clientLoginUser.getUserType())) {
         CompanyRealInfo companyRealInfo = (CompanyRealInfo)this.memberCompanyRealInfoMapper
            .selectOne((Wrapper<CompanyRealInfo>)new LambdaQueryWrapper<CompanyRealInfo>().eq(CompanyRealInfo::getMemberNo, clientLoginUser.getId()));
         if (companyRealInfo != null && this.isCompanyCertifier(realName, idCard, companyRealInfo)) {
            this.memberCompanyRealInfoMapper
               .update(
                  null,
                  (Wrapper<CompanyRealInfo>)((LambdaUpdateWrapper<CompanyRealInfo>)((LambdaUpdateWrapper<CompanyRealInfo>)new LambdaUpdateWrapper<CompanyRealInfo>().eq(CompanyRealInfo::getMemberNo, clientLoginUser.getId()))
                        .eq(CompanyRealInfo::getStatus, CertificationStatusEnum.PENDING.getKey()))
                     .set(CompanyRealInfo::getStatus, CertificationStatusEnum.APPROVED.getKey())
               );
            this.clientUserService.updateByRealUser(clientLoginUser.getId(), companyRealInfo.getCompanyName(), UserTypeEnum.ENTERPRISE.getKey());
         } else {
            throw new CommonException("MCloud实名认证信息与提交的企业实名信息不一致", new Object[0]);
         }
      } else {
         throw new CommonException("不支持的用户类型：{}", new Object[]{clientLoginUser.getUserType()});
      }
   }

   private boolean isCompanyCertifier(String realName, String idCard, CompanyRealInfo companyRealInfo) {
      return this.isMcloudIdentityMatched(realName, idCard, companyRealInfo.getLegalRepresentative(), companyRealInfo.getLegalRepresentativeNumber())
         || this.isMcloudIdentityMatched(realName, idCard, companyRealInfo.getContactPerson(), companyRealInfo.getContactPersonNumber());
   }

   private boolean isMcloudIdentityMatched(String realName, String idCard, String submittedName, String submittedIdCard) {
      boolean hasReturnedName = StrUtil.isNotBlank(realName);
      boolean hasReturnedIdCard = StrUtil.isNotBlank(idCard);
      return !hasReturnedName && !hasReturnedIdCard
         ? false
         : (!hasReturnedName || StrUtil.equals(StrUtil.trim(realName), StrUtil.trim(submittedName)))
            && (!hasReturnedIdCard || StrUtil.equals(StrUtil.trim(idCard), StrUtil.trim(submittedIdCard)));
   }

   private String requiredConfig(String key, String message) {
      String value = this.devConfigApi.getValueByKey(key);
      if (StrUtil.isBlank(value)) {
         throw new CommonException(message, new Object[0]);
      } else {
         return value.trim();
      }
   }
}
