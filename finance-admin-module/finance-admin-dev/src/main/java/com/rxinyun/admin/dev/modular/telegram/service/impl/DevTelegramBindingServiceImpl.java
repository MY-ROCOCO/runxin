package com.rxinyun.admin.dev.modular.telegram.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.dev.modular.telegram.entity.DevTelegramBinding;
import com.rxinyun.admin.dev.modular.telegram.mapper.DevTelegramBindingMapper;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramBindLinkResult;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramBindTokenParam;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramBindingPageParam;
import com.rxinyun.admin.dev.modular.telegram.service.DevTelegramBindingService;
import com.rxinyun.admin.dev.modular.telegram.util.DevTelegramUtil;
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.dev.api.DevConfigApi;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DevTelegramBindingServiceImpl extends ServiceImpl<DevTelegramBindingMapper, DevTelegramBinding> implements DevTelegramBindingService {
   private static final Logger log = LoggerFactory.getLogger(DevTelegramBindingServiceImpl.class);
   private static final String TELEGRAM_BIND_TOKEN_CACHE_PREFIX = "telegram:bind:";
   private static final long TELEGRAM_BIND_TOKEN_TTL_SECONDS = 600L;
   private static final String TELEGRAM_BOT_USERNAME_KEY = "TELEGRAM_BOT_USERNAME";
   private static final String TELEGRAM_BOT_TOKEN_KEY = "TELEGRAM_BOT_TOKEN";
   private static final String TELEGRAM_WEBHOOK_SECRET_KEY = "TELEGRAM_WEBHOOK_SECRET";
   @Resource
   private CommonCacheOperator commonCacheOperator;
   @Resource
   private DevConfigApi devConfigApi;

   @Override
   public DevTelegramBindLinkResult createBindLink(DevTelegramBindTokenParam devTelegramBindTokenParam) {
      this.validateUserType(devTelegramBindTokenParam.getUserType());
      if (StrUtil.isBlank(devTelegramBindTokenParam.getUserId())) {
         throw new CommonException("用户ID不能为空", new Object[0]);
      } else {
         String botUsername = this.devConfigApi.getValueByKey("TELEGRAM_BOT_USERNAME");
         if (StrUtil.isBlank(botUsername)) {
            throw new CommonException("未配置Telegram机器人用户名，请配置 TELEGRAM_BOT_USERNAME", new Object[0]);
         } else {
            String token = IdUtil.fastSimpleUUID();
            JSONObject payload = new JSONObject();
            payload.set("userType", devTelegramBindTokenParam.getUserType());
            payload.set("userId", devTelegramBindTokenParam.getUserId());
            this.commonCacheOperator.put("telegram:bind:" + token, JSONUtil.toJsonStr(payload), 600L);
            return new DevTelegramBindLinkResult(token, "https://t.me/" + botUsername + "?start=" + token);
         }
      }
   }

   @Override
   public DevTelegramBinding getEnabledBinding(String userType, String userId) {
      this.validateUserType(userType);
      return StrUtil.isBlank(userId)
         ? null
         : (DevTelegramBinding)this.getOne(
            (Wrapper<DevTelegramBinding>)((LambdaQueryWrapper<DevTelegramBinding>)((LambdaQueryWrapper<DevTelegramBinding>)((LambdaQueryWrapper<DevTelegramBinding>)new LambdaQueryWrapper<DevTelegramBinding>().eq(DevTelegramBinding::getUserType, userType))
                     .eq(DevTelegramBinding::getUserId, userId))
                  .eq(DevTelegramBinding::getStatus, 1))
               .last("LIMIT 1")
         );
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void bindByTelegramStartToken(String token, String chatId, String telegramUserId, String telegramUsername) {
      if (StrUtil.isBlank(token)) {
         throw new CommonException("绑定token不能为空", new Object[0]);
      } else if (StrUtil.isBlank(chatId)) {
         throw new CommonException("Telegram chatId不能为空", new Object[0]);
      } else {
         Object cachedValue = this.commonCacheOperator.getAndRemove("telegram:bind:" + token);
         if (ObjectUtil.isEmpty(cachedValue)) {
            throw new CommonException("绑定链接已过期，请重新生成", new Object[0]);
         } else {
            JSONObject payload = JSONUtil.parseObj(cachedValue);
            String userType = payload.getStr("userType");
            String userId = payload.getStr("userId");
            this.validateUserType(userType);
            DevTelegramBinding chatBinding = (DevTelegramBinding)this.getOne(
               (Wrapper<DevTelegramBinding>)((LambdaQueryWrapper<DevTelegramBinding>)new LambdaQueryWrapper<DevTelegramBinding>().eq(DevTelegramBinding::getChatId, chatId)).last("LIMIT 1")
            );
            if (chatBinding == null || userType.equals(chatBinding.getUserType()) && userId.equals(chatBinding.getUserId())) {
               DevTelegramBinding binding = (DevTelegramBinding)this.getOne(
                  (Wrapper<DevTelegramBinding>)((LambdaQueryWrapper<DevTelegramBinding>)((LambdaQueryWrapper<DevTelegramBinding>)new LambdaQueryWrapper<DevTelegramBinding>().eq(DevTelegramBinding::getUserType, userType))
                        .eq(DevTelegramBinding::getUserId, userId))
                     .last("LIMIT 1")
               );
               if (binding == null) {
                  binding = new DevTelegramBinding();
                  binding.setUserType(userType);
                  binding.setUserId(userId);
                  binding.setChatId(chatId);
                  binding.setTelegramUserId(telegramUserId);
                  binding.setTelegramUsername(telegramUsername);
                  binding.setStatus(1);
                  this.save(binding);
               } else {
                  binding.setChatId(chatId);
                  binding.setTelegramUserId(telegramUserId);
                  binding.setTelegramUsername(telegramUsername);
                  binding.setStatus(1);
                  this.updateById(binding);
               }
            } else {
               throw new CommonException("该Telegram会话已绑定其他账号", new Object[0]);
            }
         }
      }
   }

   @Override
   public void handleWebhook(JSONObject update, String secretToken) {
      log.info("收到Telegram webhook回调：{}", update);
      this.validateWebhookSecret(secretToken);
      JSONObject message = update.getJSONObject("message");
      if (message == null) {
         log.info("Telegram webhook未包含message，跳过处理");
      } else {
         JSONObject chat = message.getJSONObject("chat");
         JSONObject from = message.getJSONObject("from");
         String text = message.getStr("text");
         if (chat != null && !StrUtil.isBlank(text) && text.startsWith("/start")) {
            String chatId = chat.getStr("id");
            String token = StrUtil.trim(StrUtil.removePrefix(text, "/start"));
            String telegramUserId = from == null ? null : from.getStr("id");
            String telegramUsername = from == null ? null : from.getStr("username");
            log.info(
               "开始处理Telegram绑定回调，chatId={}，telegramUserId={}，telegramUsername={}，token={}",
               new Object[]{chatId, telegramUserId, telegramUsername, this.maskToken(token)}
            );

            try {
               this.bindByTelegramStartToken(token, chatId, telegramUserId, telegramUsername);
               log.info("Telegram绑定成功，chatId={}，telegramUserId={}，telegramUsername={}", new Object[]{chatId, telegramUserId, telegramUsername});
               this.sendBindResult(chatId, "Telegram绑定成功");
            } catch (Exception var12) {
               log.warn("Telegram绑定失败，chatId={}，原因={}", chatId, var12.getMessage());
               this.sendBindResult(chatId, "Telegram绑定失败：" + var12.getMessage());
            }
         } else {
            log.info("Telegram webhook非/start绑定消息，text={}", text);
         }
      }
   }

   @Override
   public Page<DevTelegramBinding> page(DevTelegramBindingPageParam devTelegramBindingPageParam) {
      QueryWrapper<DevTelegramBinding> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (StrUtil.isNotBlank(devTelegramBindingPageParam.getUserType())) {
         queryWrapper.lambda().eq(DevTelegramBinding::getUserType, devTelegramBindingPageParam.getUserType());
      }

      if (StrUtil.isNotBlank(devTelegramBindingPageParam.getUserId())) {
         queryWrapper.lambda().eq(DevTelegramBinding::getUserId, devTelegramBindingPageParam.getUserId());
      }

      if (StrUtil.isNotBlank(devTelegramBindingPageParam.getSearchKey())) {
         queryWrapper.lambda()
            .and(
               wrapper -> ((LambdaQueryWrapper<DevTelegramBinding>)((LambdaQueryWrapper<DevTelegramBinding>)wrapper.like(DevTelegramBinding::getChatId, devTelegramBindingPageParam.getSearchKey()))
                     .or())
                  .like(DevTelegramBinding::getTelegramUsername, devTelegramBindingPageParam.getSearchKey())
            );
      }

      queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      return (Page<DevTelegramBinding>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   private void validateUserType(String userType) {
      if (!"MEMBER".equals(userType) && !"ADMIN".equals(userType)) {
         throw new CommonException("不支持的Telegram绑定用户类型：{}", new Object[]{userType});
      }
   }

   private void validateWebhookSecret(String secretToken) {
      String expectedSecret = this.devConfigApi.getValueByKey("TELEGRAM_WEBHOOK_SECRET");
      if (StrUtil.isNotBlank(expectedSecret) && !expectedSecret.equals(secretToken)) {
         throw new CommonException("Telegram Webhook secret校验失败", new Object[0]);
      }
   }

   private String maskToken(String token) {
      return !StrUtil.isBlank(token) && token.length() > 8 ? token.substring(0, 4) + "***" + token.substring(token.length() - 4) : "***";
   }

   private void sendBindResult(String chatId, String content) {
      try {
         String botToken = this.devConfigApi.getValueByKey("TELEGRAM_BOT_TOKEN");
         DevTelegramUtil.sendMessage(botToken, chatId, content);
      } catch (Exception var4) {
         log.warn("Telegram绑定结果消息发送失败，chatId={}，原因={}", chatId, var4.getMessage());
      }
   }
}
