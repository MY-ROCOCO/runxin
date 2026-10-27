package com.rxinyun.admin.dev.modular.email.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.dev.modular.email.entity.DevEmail;
import com.rxinyun.admin.dev.modular.email.enums.DevEmailEngineTypeEnum;
import com.rxinyun.admin.dev.modular.email.mapper.DevEmailMapper;
import com.rxinyun.admin.dev.modular.email.param.DevEmailIdParam;
import com.rxinyun.admin.dev.modular.email.param.DevEmailPageParam;
import com.rxinyun.admin.dev.modular.email.param.DevEmailSendLocalHtmlParam;
import com.rxinyun.admin.dev.modular.email.param.DevEmailSendLocalTxtParam;
import com.rxinyun.admin.dev.modular.email.param.DevEmailSendParam;
import com.rxinyun.admin.dev.modular.email.service.DevEmailService;
import com.rxinyun.admin.dev.modular.email.util.DevEmailLocalUtil;
import com.rxinyun.admin.dev.modular.message.enums.NotificationSendStatusEnum;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.util.CommonEmailUtil;
import com.rxinyun.common.util.CommonIpAddressUtil;
import com.rxinyun.common.util.CommonServletUtil;
import com.rxinyun.dev.api.DevConfigApi;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DevEmailServiceImpl extends ServiceImpl<DevEmailMapper, DevEmail> implements DevEmailService {
   private static final Logger log = LoggerFactory.getLogger(DevEmailServiceImpl.class);
   private static final String EMAIL_SEND_EMAIL_KEY = "EMAIL_SEND_EMAIL";

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void sendLocal(DevEmailSendLocalTxtParam devEmailSendLocalTxtParam) {
      CommonEmailUtil.validEmail(devEmailSendLocalTxtParam.getReceiveAccounts());
      String receiptInfo = DevEmailLocalUtil.sendTextEmail(
         devEmailSendLocalTxtParam.getReceiveAccounts(),
         devEmailSendLocalTxtParam.getSubject(),
         devEmailSendLocalTxtParam.getContent(),
         devEmailSendLocalTxtParam.getFiles()
      );
      DevEmail devEmail = new DevEmail();
      BeanUtil.copyProperties(devEmailSendLocalTxtParam, devEmail, new String[0]);
      devEmail.setEngine(DevEmailEngineTypeEnum.LOCAL.getValue());
      devEmail.setReceiptInfo(receiptInfo);
      devEmail.setSendStatus(NotificationSendStatusEnum.SUCCESS.getValue());
      devEmail.setSendTime(new Date());
      devEmail.setRetryCount(0);
      MailAccount client = DevEmailLocalUtil.getClient();
      devEmail.setSendAccount(client.getFrom());
      devEmail.setSendUser(client.getUser());
      this.save(devEmail);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void sendLocal(DevEmailSendLocalHtmlParam devEmailSendLocalHtmlParam) {
      CommonEmailUtil.validEmail(devEmailSendLocalHtmlParam.getReceiveAccounts());
      String receiptInfo = DevEmailLocalUtil.sendHtmlEmail(
         devEmailSendLocalHtmlParam.getReceiveAccounts(),
         devEmailSendLocalHtmlParam.getSubject(),
         devEmailSendLocalHtmlParam.getContent(),
         devEmailSendLocalHtmlParam.getImageMap(),
         devEmailSendLocalHtmlParam.getFiles()
      );
      DevEmail devEmail = new DevEmail();
      BeanUtil.copyProperties(devEmailSendLocalHtmlParam, devEmail, new String[0]);
      devEmail.setEngine(DevEmailEngineTypeEnum.LOCAL.getValue());
      devEmail.setReceiptInfo(receiptInfo);
      devEmail.setSendStatus(NotificationSendStatusEnum.SUCCESS.getValue());
      devEmail.setSendTime(new Date());
      devEmail.setRetryCount(0);
      MailAccount client = DevEmailLocalUtil.getClient();
      devEmail.setSendAccount(client.getFrom());
      devEmail.setSendUser(client.getUser());
      this.save(devEmail);
   }

   @Override
   public Page<DevEmail> page(DevEmailPageParam devEmailPageParam) {
      QueryWrapper<DevEmail> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(devEmailPageParam.getEngine())) {
         queryWrapper.lambda().eq(DevEmail::getEngine, devEmailPageParam.getEngine());
      }

      if (ObjectUtil.isNotEmpty(devEmailPageParam.getSendStatus())) {
         queryWrapper.lambda().eq(DevEmail::getSendStatus, devEmailPageParam.getSendStatus());
      }

      if (ObjectUtil.isNotEmpty(devEmailPageParam.getSearchKey())) {
         queryWrapper.lambda().like(DevEmail::getSubject, devEmailPageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{devEmailPageParam.getSortField(), devEmailPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(devEmailPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, devEmailPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(devEmailPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<DevEmail>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public void delete(List<DevEmailIdParam> devEmailIdParamList) {
      this.removeByIds(CollStreamUtil.toList(devEmailIdParamList, DevEmailIdParam::getId));
   }

   @Override
   public DevEmail detail(DevEmailIdParam devEmailIdParam) {
      return this.queryEntity(devEmailIdParam.getId());
   }

   @Override
   public DevEmail queryEntity(String id) {
      DevEmail devEmail = (DevEmail)this.getById(id);
      if (ObjectUtil.isEmpty(devEmail)) {
         throw new CommonException("邮件发送记录不存在，id值为：{}", new Object[]{id});
      } else {
         return devEmail;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void sendEmail(DevEmailSendParam devEmailSendParam) {
      CommonEmailUtil.validEmail(devEmailSendParam.getReceiveAccounts());

      try {
         HttpServletRequest request = CommonServletUtil.getRequest();
         if (request != null) {
            String ip = CommonIpAddressUtil.getIp(request);
            devEmailSendParam.setIp(ip);
         }
      } catch (Exception var4) {
         log.debug("非Web上下文,无法获取IP地址");
      }

      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String configFrom = devConfigApi.getValueByKey("EMAIL_SEND_EMAIL");
      if (ObjectUtil.isEmpty(configFrom)) {
         throw new CommonException("未配置发件人邮箱，请配置 EMAIL_SEND_EMAIL", new Object[0]);
      } else {
         devEmailSendParam.setSendAccount(configFrom);
         devEmailSendParam.setSendUser(configFrom);
         log.info("邮件已进入发送队列，发件人：{}，收件人：{}", configFrom, devEmailSendParam.getReceiveAccounts());
         this.savePendingEmail(devEmailSendParam);
      }
   }

   @Override
   public List<DevEmail> listPendingEmails(int limit) {
      return this.list(
         (Wrapper<DevEmail>)((LambdaQueryWrapper<DevEmail>)((LambdaQueryWrapper<DevEmail>)new LambdaQueryWrapper<DevEmail>().eq(DevEmail::getSendStatus, NotificationSendStatusEnum.PENDING.getValue()))
               .orderByAsc(CommonEntity::getCreateTime))
            .last("LIMIT " + limit)
      );
   }

   @Override
   public boolean markSending(String id) {
      return this.update(
         (Wrapper<DevEmail>)((LambdaUpdateWrapper<DevEmail>)((LambdaUpdateWrapper<DevEmail>)new LambdaUpdateWrapper<DevEmail>().eq(DevEmail::getId, id))
               .eq(DevEmail::getSendStatus, NotificationSendStatusEnum.PENDING.getValue()))
            .set(DevEmail::getSendStatus, NotificationSendStatusEnum.SENDING.getValue())
      );
   }

   @Override
   public void dispatchQueuedEmail(DevEmail devEmail) {
      try {
         CommonEmailUtil.validEmail(devEmail.getReceiveAccounts());
         String receiptInfo = DevEmailLocalUtil.sendHtmlEmail(devEmail.getReceiveAccounts(), devEmail.getSubject(), devEmail.getContent(), null, null);
         this.update(
            (Wrapper<DevEmail>)((LambdaUpdateWrapper<DevEmail>)((LambdaUpdateWrapper<DevEmail>)((LambdaUpdateWrapper<DevEmail>)((LambdaUpdateWrapper<DevEmail>)new LambdaUpdateWrapper<DevEmail>()
                           .eq(DevEmail::getId, devEmail.getId()))
                        .set(DevEmail::getSendStatus, NotificationSendStatusEnum.SUCCESS.getValue()))
                     .set(DevEmail::getReceiptInfo, receiptInfo))
                  .set(DevEmail::getSendTime, new Date()))
               .set(DevEmail::getFailReason, null)
         );
         log.info("队列邮件发送成功，id={}，收件人={}", devEmail.getId(), devEmail.getReceiveAccounts());
      } catch (Exception var3) {
         this.update(
            (Wrapper<DevEmail>)((LambdaUpdateWrapper<DevEmail>)((LambdaUpdateWrapper<DevEmail>)((LambdaUpdateWrapper<DevEmail>)((LambdaUpdateWrapper<DevEmail>)((LambdaUpdateWrapper<DevEmail>)new LambdaUpdateWrapper<DevEmail>()
                              .eq(DevEmail::getId, devEmail.getId()))
                           .set(DevEmail::getSendStatus, NotificationSendStatusEnum.FAILED.getValue()))
                        .set(DevEmail::getReceiptInfo, "发送失败：" + var3.getMessage()))
                     .set(DevEmail::getFailReason, var3.getMessage()))
                  .set(DevEmail::getSendTime, new Date()))
               .setSql("RETRY_COUNT = IFNULL(RETRY_COUNT, 0) + 1", new Object[0])
         );
         log.error("队列邮件发送失败，id={}，收件人={}，错误={}", new Object[]{devEmail.getId(), devEmail.getReceiveAccounts(), var3.getMessage(), var3});
      }
   }

   private void savePendingEmail(DevEmailSendParam param) {
      DevEmail devEmail = new DevEmail();
      BeanUtil.copyProperties(param, devEmail, new String[0]);
      devEmail.setEngine(DevEmailEngineTypeEnum.LOCAL.getValue());
      devEmail.setSendStatus(NotificationSendStatusEnum.PENDING.getValue());
      devEmail.setRetryCount(0);
      this.save(devEmail);
   }
}
