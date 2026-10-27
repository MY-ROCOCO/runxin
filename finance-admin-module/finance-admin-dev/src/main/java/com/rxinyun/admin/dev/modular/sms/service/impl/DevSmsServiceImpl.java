package com.rxinyun.admin.dev.modular.sms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.dev.modular.message.enums.NotificationSendStatusEnum;
import com.rxinyun.admin.dev.modular.message.service.ToolsSmsTemplateService;
import com.rxinyun.admin.dev.modular.sms.entity.DevSms;
import com.rxinyun.admin.dev.modular.sms.enums.DevSmsEngineTypeEnum;
import com.rxinyun.admin.dev.modular.sms.mapper.DevSmsMapper;
import com.rxinyun.admin.dev.modular.sms.param.DevSmsIdParam;
import com.rxinyun.admin.dev.modular.sms.param.DevSmsPageParam;
import com.rxinyun.admin.dev.modular.sms.param.DevSmsSendParam;
import com.rxinyun.admin.dev.modular.sms.service.DevSmsService;
import com.rxinyun.admin.dev.modular.sms.util.DevSmsAliyunUtil;
import com.rxinyun.admin.dev.modular.sms.util.DevSmsBaoUtil;
import com.rxinyun.admin.dev.modular.sms.util.DevSmsSaiyouUtil;
import com.rxinyun.admin.dev.modular.sms.util.DevSmsTencentUtil;
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.util.CommonIpAddressUtil;
import com.rxinyun.common.util.CommonServletUtil;
import com.rxinyun.dev.api.DevConfigApi;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DevSmsServiceImpl extends ServiceImpl<DevSmsMapper, DevSms> implements DevSmsService {
   private static final Logger log = LoggerFactory.getLogger(DevSmsServiceImpl.class);
   private static final String EXT_JSON_TEMPLATE_PARAM_KEY = "templateParam";
   private static final String RUNXIN_SMS_SMSBAO_DEFAULT_SIGN_NAME_KEY = "RUNXIN_SMS_SMSBAO_DEFAULT_SIGN_NAME";
   private static final String RUNXIN_SMS_SAIYOU_DEFAULT_SIGN_NAME_KEY = "RUNXIN_SMS_SAIYOU_DEFAULT_SIGN_NAME";
   private static final String RUNXIN_SMS_TENCENT_DEFAULT_SIGN_NAME_KEY = "RUNXIN_SMS_TENCENT_DEFAULT_SIGN_NAME";
   private static final String RUNXIN_SMS_ALIYUN_DEFAULT_SIGN_NAME_KEY = "RUNXIN_SMS_ALIYUN_DEFAULT_SIGN_NAME";
   private static final String RUNXIN_SMS_ALIYUN_TEMPLATE_CODE_KEY = "RUNXIN_SMS_ALIYUN_TEMPLATE_CODE";
   private static final String RUNXIN_SMS_TENCENT_TEMPLATE_CODE_KEY = "RUNXIN_SMS_TENCENT_TEMPLATE_CODE";
   @Resource
   private CommonCacheOperator commonCacheOperator;
   @Resource
   private ToolsSmsTemplateService toolsSmsTemplateService;
   @Resource
   private DevSmsService devSmsService;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void sendSms(DevSmsSendParam devSmsSendParam) {
      this.validPhone(devSmsSendParam.getPhoneNumbers());
      String ip = null;

      try {
         HttpServletRequest request = CommonServletUtil.getRequest();
         if (request != null) {
            ip = CommonIpAddressUtil.getIp(request);
            devSmsSendParam.setIp(ip);
         }
      } catch (Exception var5) {
         log.debug("非Web上下文,无法获取IP地址");
      }

      if (ip != null) {
         String ipRateLimitKey = "rate-limit:ip:" + ip;
         Integer ipCount = (Integer)this.commonCacheOperator.get(ipRateLimitKey);
         if (ipCount == null) {
            this.commonCacheOperator.put(ipRateLimitKey, 1, 6000L);
         } else {
            if (ipCount >= 5) {
               throw new CommonException("当前IP请求过于频繁，请稍后再试", new Object[0]);
            }

            this.commonCacheOperator.put(ipRateLimitKey, ipCount + 1, 6000L);
         }
      }

      String rateLimitKey = "rate-limit:phone:" + devSmsSendParam.getPhoneNumbers();
      Integer count = (Integer)this.commonCacheOperator.get(rateLimitKey);
      if (count == null) {
         this.commonCacheOperator.put(rateLimitKey, 1, 600L);
      } else {
         if (count >= 3) {
            throw new CommonException("验证码已发送过多，请5分钟后重试", new Object[0]);
         }

         this.commonCacheOperator.put(rateLimitKey, count + 1, 600L);
      }

      this.savePendingSms(devSmsSendParam);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void sendSmsAsync(DevSmsSendParam devSmsSendParam) {
      this.validPhone(devSmsSendParam.getPhoneNumbers());
      this.savePendingSms(devSmsSendParam);
   }

   @Transactional(
      propagation = Propagation.REQUIRES_NEW,
      rollbackFor = {Exception.class}
   )
   @Override
   public void saveFailedRecordInNewTx(DevSmsSendParam devSmsSendParam, String engineKey, String errorMessage) {
      try {
         DevSms devSms = new DevSms();
         BeanUtil.copyProperties(devSmsSendParam, devSms, new String[0]);
         this.fillTemplateParamWithContent(devSms, devSmsSendParam);
         devSms.setEngine(ObjectUtil.isNotEmpty(engineKey) ? engineKey : "UNKNOWN");
         devSms.setReceiptInfo("发送失败：" + (StrUtil.isNotBlank(errorMessage) ? errorMessage : "未知错误"));
         devSms.setSendStatus(NotificationSendStatusEnum.FAILED.getValue());
         devSms.setFailReason(errorMessage);
         devSms.setSendTime(new Date());
         devSms.setRetryCount(1);
         devSms.setIp(devSmsSendParam.getIp());
         this.save(devSms);
         log.warn("短信发送失败已落库记录，手机号：{}，原因：{}", devSmsSendParam.getPhoneNumbers(), errorMessage);
      } catch (Exception var5) {
         log.error("保存短信失败记录时异常", var5);
      }
   }

   private DevSmsServiceImpl.SmsDispatchResult doSendByEngine(DevConfigApi devConfigApi, String defaultSms, DevSmsSendParam devSmsSendParam) {
      if (defaultSms.equals("SMS_ALIYUN")) {
         String signName = devConfigApi.getValueByKey("RUNXIN_SMS_ALIYUN_DEFAULT_SIGN_NAME");
         if (ObjectUtil.isEmpty(signName)) {
            throw new CommonException("阿里云短信操作客户端未正确配置：signName", new Object[0]);
         } else if (ObjectUtil.isEmpty(devSmsSendParam.getTemplateCode())) {
            throw new CommonException("短信模板编码不能为空", new Object[0]);
         } else {
            devSmsSendParam.setSignName(signName);
            return new DevSmsServiceImpl.SmsDispatchResult(DevSmsEngineTypeEnum.ALIYUN.getValue(), signName, this.sendAliyun(devSmsSendParam));
         }
      } else if (defaultSms.equals("SMS_TENCENT")) {
         String signName = devConfigApi.getValueByKey("RUNXIN_SMS_TENCENT_DEFAULT_SIGN_NAME");
         if (ObjectUtil.isEmpty(signName)) {
            throw new CommonException("腾讯云短信操作客户端未正确配置：signName", new Object[0]);
         } else if (ObjectUtil.isEmpty(devSmsSendParam.getTemplateCode())) {
            throw new CommonException("短信模板编码不能为空", new Object[0]);
         } else {
            devSmsSendParam.setSignName(signName);
            return new DevSmsServiceImpl.SmsDispatchResult(DevSmsEngineTypeEnum.TENCENT.getValue(), signName, this.sendTencent(devSmsSendParam));
         }
      } else if (defaultSms.equals("SMSBAO")) {
         String signName = devConfigApi.getValueByKey("RUNXIN_SMS_SMSBAO_DEFAULT_SIGN_NAME");
         if (ObjectUtil.isEmpty(signName)) {
            throw new CommonException("短信宝短信操作客户端未正确配置：signName", new Object[0]);
         } else {
            devSmsSendParam.setSignName(signName);
            return new DevSmsServiceImpl.SmsDispatchResult(DevSmsEngineTypeEnum.SMSBAO.getValue(), signName, this.sendSmsBao(devSmsSendParam));
         }
      } else if (defaultSms.equals("SAIYOU")) {
         String signName = devConfigApi.getValueByKey("RUNXIN_SMS_SAIYOU_DEFAULT_SIGN_NAME");
         if (ObjectUtil.isEmpty(signName)) {
            throw new CommonException("赛邮短信操作客户端未正确配置：signName", new Object[0]);
         } else {
            devSmsSendParam.setSignName(signName);
            return new DevSmsServiceImpl.SmsDispatchResult(DevSmsEngineTypeEnum.SAIYOU.getValue(), signName, this.sendSaiyou(devSmsSendParam));
         }
      } else {
         throw new CommonException("不支持的短信通道：{}", new Object[]{defaultSms});
      }
   }

   private String sendAliyun(DevSmsSendParam devSmsSendParam) {
      this.validPhone(devSmsSendParam.getPhoneNumbers());
      String templateParam = devSmsSendParam.getTemplateParam();
      if (ObjectUtil.isEmpty(templateParam)) {
         templateParam = "{}";
      }

      log.info("阿里云短信发送参数 - 模板编码：{}，JSON参数：{}", devSmsSendParam.getTemplateCode(), templateParam);
      String receiptInfo = DevSmsAliyunUtil.sendSms(
         devSmsSendParam.getPhoneNumbers(), devSmsSendParam.getSignName(), devSmsSendParam.getTemplateCode(), templateParam
      );
      log.info("阿里云短信发送成功：{}", receiptInfo);
      return receiptInfo;
   }

   private String sendTencent(DevSmsSendParam devSmsSendParam) {
      this.validPhone(devSmsSendParam.getPhoneNumbers());
      String templateParam = devSmsSendParam.getTemplateParam();
      if (ObjectUtil.isEmpty(templateParam)) {
         templateParam = "{}";
      }

      log.info("腾讯云短信发送参数 - 模板编码：{}，JSON参数：{}", devSmsSendParam.getTemplateCode(), templateParam);
      String receiptInfo = DevSmsTencentUtil.sendSms(
         devSmsSendParam.getSdkAppId(), devSmsSendParam.getPhoneNumbers(), devSmsSendParam.getSignName(), devSmsSendParam.getTemplateCode(), templateParam
      );
      log.info("腾讯云短信发送成功：{}", receiptInfo);
      return receiptInfo;
   }

   private String sendSmsBao(DevSmsSendParam devSmsSendParam) {
      this.validPhone(devSmsSendParam.getPhoneNumbers());
      if (ObjectUtil.isEmpty(devSmsSendParam.getTemplateContent())) {
         throw new CommonException("短信宝发送时模板内容不能为空，请检查模板配置", new Object[0]);
      } else {
         log.info("短信宝发送 - 手机号：{}，模板内容：{}", devSmsSendParam.getPhoneNumbers(), devSmsSendParam.getTemplateContent());
         String receiptInfo = DevSmsBaoUtil.sendSms(devSmsSendParam.getPhoneNumbers(), devSmsSendParam.getTemplateContent(), devSmsSendParam.getSignName());
         log.info("短信宝发送成功：{}", receiptInfo);
         return receiptInfo;
      }
   }

   private String sendSaiyou(DevSmsSendParam devSmsSendParam) {
      this.validPhone(devSmsSendParam.getPhoneNumbers());
      if (ObjectUtil.isEmpty(devSmsSendParam.getTemplateContent())) {
         throw new CommonException("赛邮发送时模板内容不能为空，请检查模板配置", new Object[0]);
      } else {
         log.info("赛邮发送 - 手机号：{}，模板内容：{}", devSmsSendParam.getPhoneNumbers(), devSmsSendParam.getTemplateContent());
         String receiptInfo = DevSmsSaiyouUtil.sendSms(devSmsSendParam.getPhoneNumbers(), devSmsSendParam.getTemplateContent(), devSmsSendParam.getSignName());
         log.info("赛邮发送成功：{}", receiptInfo);
         return receiptInfo;
      }
   }

   private void fillTemplateParamWithContent(DevSms devSms, DevSmsSendParam devSmsSendParam) {
      if (StrUtil.isNotBlank(devSmsSendParam.getTemplateContent())) {
         devSms.setTemplateParam(devSmsSendParam.getTemplateContent());
      }
   }

   @Override
   public List<DevSms> listPendingSms(int limit) {
      return this.list(
         (Wrapper<DevSms>)((LambdaQueryWrapper<DevSms>)((LambdaQueryWrapper<DevSms>)new LambdaQueryWrapper<DevSms>().eq(DevSms::getSendStatus, NotificationSendStatusEnum.PENDING.getValue()))
               .orderByAsc(CommonEntity::getCreateTime))
            .last("LIMIT " + limit)
      );
   }

   @Override
   public boolean markSending(String id) {
      return this.update(
         (Wrapper<DevSms>)((LambdaUpdateWrapper<DevSms>)((LambdaUpdateWrapper<DevSms>)new LambdaUpdateWrapper<DevSms>().eq(DevSms::getId, id))
               .eq(DevSms::getSendStatus, NotificationSendStatusEnum.PENDING.getValue()))
            .set(DevSms::getSendStatus, NotificationSendStatusEnum.SENDING.getValue())
      );
   }

   @Override
   public void dispatchQueuedSms(DevSms devSms) {
      DevSmsSendParam sendParam = this.buildSendParam(devSms);

      try {
         DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
         String defaultSms = this.toConfigEngineKey(devSms.getEngine());
         if (ObjectUtil.isEmpty(defaultSms)) {
            throw new CommonException("未配置默认短信发送通道", new Object[0]);
         }

         DevSmsServiceImpl.SmsDispatchResult result = this.doSendByEngine(devConfigApi, defaultSms, sendParam);
         this.update(
            (Wrapper<DevSms>)((LambdaUpdateWrapper<DevSms>)((LambdaUpdateWrapper<DevSms>)((LambdaUpdateWrapper<DevSms>)((LambdaUpdateWrapper<DevSms>)((LambdaUpdateWrapper<DevSms>)((LambdaUpdateWrapper<DevSms>)new LambdaUpdateWrapper<DevSms>()
                                 .eq(DevSms::getId, devSms.getId()))
                              .set(DevSms::getEngine, result.engine()))
                           .set(DevSms::getSignName, result.signName()))
                        .set(DevSms::getReceiptInfo, result.receiptInfo()))
                     .set(DevSms::getSendStatus, NotificationSendStatusEnum.SUCCESS.getValue()))
                  .set(DevSms::getSendTime, new Date()))
               .set(DevSms::getFailReason, null)
         );
         log.info("队列短信发送成功，id={}，手机号={}", devSms.getId(), devSms.getPhoneNumbers());
      } catch (Exception var6) {
         this.update(
            (Wrapper<DevSms>)((LambdaUpdateWrapper<DevSms>)((LambdaUpdateWrapper<DevSms>)((LambdaUpdateWrapper<DevSms>)((LambdaUpdateWrapper<DevSms>)((LambdaUpdateWrapper<DevSms>)new LambdaUpdateWrapper<DevSms>()
                              .eq(DevSms::getId, devSms.getId()))
                           .set(DevSms::getSendStatus, NotificationSendStatusEnum.FAILED.getValue()))
                        .set(DevSms::getReceiptInfo, "发送失败：" + var6.getMessage()))
                     .set(DevSms::getFailReason, var6.getMessage()))
                  .set(DevSms::getSendTime, new Date()))
               .setSql("RETRY_COUNT = IFNULL(RETRY_COUNT, 0) + 1", new Object[0])
         );
         log.error("队列短信发送失败，id={}，手机号={}，错误={}", new Object[]{devSms.getId(), devSms.getPhoneNumbers(), var6.getMessage(), var6});
      }
   }

   private void savePendingSms(DevSmsSendParam sendParam) {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String defaultSms = devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_SMS");
      if (ObjectUtil.isEmpty(defaultSms)) {
         throw new CommonException("未配置默认短信发送通道", new Object[0]);
      } else {
         DevSmsServiceImpl.SmsDispatchConfig config = this.resolveDispatchConfig(devConfigApi, defaultSms);
         DevSms devSms = new DevSms();
         BeanUtil.copyProperties(sendParam, devSms, new String[0]);
         if (DevSmsEngineTypeEnum.ALIYUN.getValue().equals(config.engine()) || DevSmsEngineTypeEnum.TENCENT.getValue().equals(config.engine())) {
            devSms.setExtJson(JSONUtil.createObj().set("templateParam", sendParam.getTemplateParam()).toString());
         }

         this.fillTemplateParamWithContent(devSms, sendParam);
         devSms.setEngine(config.engine());
         devSms.setSignName(config.signName());
         devSms.setSendStatus(NotificationSendStatusEnum.PENDING.getValue());
         devSms.setRetryCount(0);
         this.save(devSms);
         log.info("短信已进入发送队列，手机号={}，通道={}", sendParam.getPhoneNumbers(), config.engine());
      }
   }

   private DevSmsSendParam buildSendParam(DevSms devSms) {
      DevSmsSendParam sendParam = new DevSmsSendParam();
      sendParam.setPhoneNumbers(devSms.getPhoneNumbers());
      sendParam.setTemplateCode(devSms.getTemplateCode());
      sendParam.setTemplateParam(this.resolveTemplateParam(devSms));
      sendParam.setSignName(devSms.getSignName());
      sendParam.setIp(devSms.getIp());
      if (DevSmsEngineTypeEnum.SMSBAO.getValue().equals(devSms.getEngine()) || DevSmsEngineTypeEnum.SAIYOU.getValue().equals(devSms.getEngine())) {
         sendParam.setTemplateContent(devSms.getTemplateParam());
      }

      return sendParam;
   }

   private String resolveTemplateParam(DevSms devSms) {
      if ((DevSmsEngineTypeEnum.ALIYUN.getValue().equals(devSms.getEngine()) || DevSmsEngineTypeEnum.TENCENT.getValue().equals(devSms.getEngine()))
         && StrUtil.isNotBlank(devSms.getExtJson())) {
         try {
            String templateParam = JSONUtil.parseObj(devSms.getExtJson()).getStr("templateParam");
            if (StrUtil.isNotBlank(templateParam)) {
               return templateParam;
            }
         } catch (Exception var3) {
            log.warn("解析短信队列扩展参数失败，id={}，extJson={}", devSms.getId(), devSms.getExtJson());
         }
      }

      return devSms.getTemplateParam();
   }

   private DevSmsServiceImpl.SmsDispatchConfig resolveDispatchConfig(DevConfigApi devConfigApi, String defaultSms) {
      if (defaultSms.equals("SMS_ALIYUN")) {
         String signName = devConfigApi.getValueByKey("RUNXIN_SMS_ALIYUN_DEFAULT_SIGN_NAME");
         if (ObjectUtil.isEmpty(signName)) {
            throw new CommonException("阿里云短信操作客户端未正确配置：signName", new Object[0]);
         } else {
            return new DevSmsServiceImpl.SmsDispatchConfig(DevSmsEngineTypeEnum.ALIYUN.getValue(), signName);
         }
      } else if (defaultSms.equals("SMS_TENCENT")) {
         String signName = devConfigApi.getValueByKey("RUNXIN_SMS_TENCENT_DEFAULT_SIGN_NAME");
         if (ObjectUtil.isEmpty(signName)) {
            throw new CommonException("腾讯云短信操作客户端未正确配置：signName", new Object[0]);
         } else {
            return new DevSmsServiceImpl.SmsDispatchConfig(DevSmsEngineTypeEnum.TENCENT.getValue(), signName);
         }
      } else if (defaultSms.equals("SMSBAO")) {
         String signName = devConfigApi.getValueByKey("RUNXIN_SMS_SMSBAO_DEFAULT_SIGN_NAME");
         if (ObjectUtil.isEmpty(signName)) {
            throw new CommonException("短信宝短信操作客户端未正确配置：signName", new Object[0]);
         } else {
            return new DevSmsServiceImpl.SmsDispatchConfig(DevSmsEngineTypeEnum.SMSBAO.getValue(), signName);
         }
      } else if (defaultSms.equals("SAIYOU")) {
         String signName = devConfigApi.getValueByKey("RUNXIN_SMS_SAIYOU_DEFAULT_SIGN_NAME");
         if (ObjectUtil.isEmpty(signName)) {
            throw new CommonException("赛邮短信操作客户端未正确配置：signName", new Object[0]);
         } else {
            return new DevSmsServiceImpl.SmsDispatchConfig(DevSmsEngineTypeEnum.SAIYOU.getValue(), signName);
         }
      } else {
         throw new CommonException("不支持的短信通道：{}", new Object[]{defaultSms});
      }
   }

   private String toConfigEngineKey(String engine) {
      if (DevSmsEngineTypeEnum.ALIYUN.getValue().equals(engine)) {
         return "SMS_ALIYUN";
      } else if (DevSmsEngineTypeEnum.TENCENT.getValue().equals(engine)) {
         return "SMS_TENCENT";
      } else if (DevSmsEngineTypeEnum.SMSBAO.getValue().equals(engine)) {
         return "SMSBAO";
      } else {
         return DevSmsEngineTypeEnum.SAIYOU.getValue().equals(engine) ? "SAIYOU" : engine;
      }
   }

   @Override
   public Page<DevSms> page(DevSmsPageParam devSmsPageParam) {
      QueryWrapper<DevSms> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(devSmsPageParam.getEngine())) {
         queryWrapper.lambda().eq(DevSms::getEngine, devSmsPageParam.getEngine());
      }

      if (ObjectUtil.isNotEmpty(devSmsPageParam.getSendStatus())) {
         queryWrapper.lambda().eq(DevSms::getSendStatus, devSmsPageParam.getSendStatus());
      }

      if (ObjectUtil.isNotEmpty(devSmsPageParam.getSearchKey())) {
         queryWrapper.lambda().like(DevSms::getPhoneNumbers, devSmsPageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{devSmsPageParam.getSortField(), devSmsPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(devSmsPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, devSmsPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(devSmsPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<DevSms>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public void delete(List<DevSmsIdParam> devSmsIdParamList) {
      this.removeByIds(CollStreamUtil.toList(devSmsIdParamList, DevSmsIdParam::getId));
   }

   @Override
   public DevSms detail(DevSmsIdParam devSmsIdParam) {
      return this.queryEntity(devSmsIdParam.getId());
   }

   @Override
   public DevSms queryEntity(String id) {
      DevSms devSms = (DevSms)this.getById(id);
      if (ObjectUtil.isEmpty(devSms)) {
         throw new CommonException("短信不存在，id值为：{}", new Object[]{id});
      } else {
         return devSms;
      }
   }

   private void validPhone(String phones) {
      StrUtil.split(phones, ",").forEach(phone -> {
         if (!PhoneUtil.isMobile(phone)) {
            throw new CommonException("手机号码：{}格式错误", new Object[]{phone});
         }
      });
   }

   private record SmsDispatchConfig(String engine, String signName) {
   }

   private record SmsDispatchResult(String engine, String signName, String receiptInfo) {
   }
}
