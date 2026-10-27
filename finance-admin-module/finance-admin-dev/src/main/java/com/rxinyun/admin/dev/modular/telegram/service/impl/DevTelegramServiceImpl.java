package com.rxinyun.admin.dev.modular.telegram.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.dev.modular.message.enums.NotificationSendStatusEnum;
import com.rxinyun.admin.dev.modular.telegram.entity.DevTelegram;
import com.rxinyun.admin.dev.modular.telegram.mapper.DevTelegramMapper;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramIdParam;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramPageParam;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramSendParam;
import com.rxinyun.admin.dev.modular.telegram.service.DevTelegramService;
import com.rxinyun.admin.dev.modular.telegram.util.DevTelegramUtil;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.dev.api.DevConfigApi;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DevTelegramServiceImpl extends ServiceImpl<DevTelegramMapper, DevTelegram> implements DevTelegramService {
   private static final Logger log = LoggerFactory.getLogger(DevTelegramServiceImpl.class);
   private static final String TELEGRAM_BOT_TOKEN_KEY = "TELEGRAM_BOT_TOKEN";
   private static final String EXT_PARSE_MODE_KEY = "parseMode";
   @Resource
   private DevConfigApi devConfigApi;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void sendTelegramAsync(DevTelegramSendParam devTelegramSendParam) {
      if (StrUtil.isBlank(devTelegramSendParam.getChatId())) {
         throw new CommonException("Telegram chatId不能为空", new Object[0]);
      } else if (StrUtil.isBlank(devTelegramSendParam.getContent())) {
         throw new CommonException("Telegram消息内容不能为空", new Object[0]);
      } else {
         DevTelegram devTelegram = new DevTelegram();
         BeanUtil.copyProperties(devTelegramSendParam, devTelegram, new String[0]);
         if (StrUtil.isNotBlank(devTelegramSendParam.getParseMode())) {
            devTelegram.setExtJson(JSONUtil.createObj().set("parseMode", devTelegramSendParam.getParseMode()).toString());
         }

         devTelegram.setSendStatus(NotificationSendStatusEnum.PENDING.getValue());
         devTelegram.setRetryCount(0);
         this.save(devTelegram);
         log.info("Telegram消息已进入发送队列，chatId={}", devTelegramSendParam.getChatId());
      }
   }

   @Override
   public List<DevTelegram> listPendingTelegram(int limit) {
      return this.list(
         (Wrapper<DevTelegram>)((LambdaQueryWrapper<DevTelegram>)((LambdaQueryWrapper<DevTelegram>)new LambdaQueryWrapper<DevTelegram>()
                  .eq(DevTelegram::getSendStatus, NotificationSendStatusEnum.PENDING.getValue()))
               .orderByAsc(CommonEntity::getCreateTime))
            .last("LIMIT " + limit)
      );
   }

   @Override
   public boolean markSending(String id) {
      return this.update(
         (Wrapper<DevTelegram>)((LambdaUpdateWrapper<DevTelegram>)((LambdaUpdateWrapper<DevTelegram>)new LambdaUpdateWrapper<DevTelegram>().eq(DevTelegram::getId, id))
               .eq(DevTelegram::getSendStatus, NotificationSendStatusEnum.PENDING.getValue()))
            .set(DevTelegram::getSendStatus, NotificationSendStatusEnum.SENDING.getValue())
      );
   }

   @Override
   public void dispatchQueuedTelegram(DevTelegram devTelegram) {
      try {
         String botToken = this.devConfigApi.getValueByKey("TELEGRAM_BOT_TOKEN");
         String receiptInfo = DevTelegramUtil.sendMessage(botToken, devTelegram.getChatId(), devTelegram.getContent(), this.resolveParseMode(devTelegram));
         this.update(
            (Wrapper<DevTelegram>)((LambdaUpdateWrapper<DevTelegram>)((LambdaUpdateWrapper<DevTelegram>)((LambdaUpdateWrapper<DevTelegram>)((LambdaUpdateWrapper<DevTelegram>)new LambdaUpdateWrapper<DevTelegram>()
                           .eq(DevTelegram::getId, devTelegram.getId()))
                        .set(DevTelegram::getSendStatus, NotificationSendStatusEnum.SUCCESS.getValue()))
                     .set(DevTelegram::getReceiptInfo, receiptInfo))
                  .set(DevTelegram::getSendTime, new Date()))
               .set(DevTelegram::getFailReason, null)
         );
         log.info("队列Telegram消息发送成功，id={}，chatId={}", devTelegram.getId(), devTelegram.getChatId());
      } catch (Exception var4) {
         this.update(
            (Wrapper<DevTelegram>)((LambdaUpdateWrapper<DevTelegram>)((LambdaUpdateWrapper<DevTelegram>)((LambdaUpdateWrapper<DevTelegram>)((LambdaUpdateWrapper<DevTelegram>)((LambdaUpdateWrapper<DevTelegram>)new LambdaUpdateWrapper<DevTelegram>()
                              .eq(DevTelegram::getId, devTelegram.getId()))
                           .set(DevTelegram::getSendStatus, NotificationSendStatusEnum.FAILED.getValue()))
                        .set(DevTelegram::getReceiptInfo, "发送失败：" + var4.getMessage()))
                     .set(DevTelegram::getFailReason, var4.getMessage()))
                  .set(DevTelegram::getSendTime, new Date()))
               .setSql("RETRY_COUNT = IFNULL(RETRY_COUNT, 0) + 1", new Object[0])
         );
         log.error("队列Telegram消息发送失败，id={}，chatId={}，错误={}", new Object[]{devTelegram.getId(), devTelegram.getChatId(), var4.getMessage(), var4});
      }
   }

   private String resolveParseMode(DevTelegram devTelegram) {
      if (devTelegram != null && !StrUtil.isBlank(devTelegram.getExtJson())) {
         try {
            JSONObject extJson = JSONUtil.parseObj(devTelegram.getExtJson());
            return extJson.getStr("parseMode");
         } catch (Exception var3) {
            log.warn("解析Telegram扩展参数失败，id={}，extJson={}", new Object[]{devTelegram.getId(), devTelegram.getExtJson(), var3});
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public Page<DevTelegram> page(DevTelegramPageParam devTelegramPageParam) {
      QueryWrapper<DevTelegram> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(devTelegramPageParam.getSendStatus())) {
         queryWrapper.lambda().eq(DevTelegram::getSendStatus, devTelegramPageParam.getSendStatus());
      }

      if (ObjectUtil.isNotEmpty(devTelegramPageParam.getSearchKey())) {
         queryWrapper.lambda().like(DevTelegram::getChatId, devTelegramPageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{devTelegramPageParam.getSortField(), devTelegramPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(devTelegramPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, devTelegramPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(devTelegramPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<DevTelegram>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public void delete(List<DevTelegramIdParam> devTelegramIdParamList) {
      this.removeByIds(CollStreamUtil.toList(devTelegramIdParamList, DevTelegramIdParam::getId));
   }

   @Override
   public DevTelegram detail(DevTelegramIdParam devTelegramIdParam) {
      return this.queryEntity(devTelegramIdParam.getId());
   }

   @Override
   public DevTelegram queryEntity(String id) {
      DevTelegram devTelegram = (DevTelegram)this.getById(id);
      if (ObjectUtil.isEmpty(devTelegram)) {
         throw new CommonException("Telegram发送记录不存在，id值为：{}", new Object[]{id});
      } else {
         return devTelegram;
      }
   }
}
