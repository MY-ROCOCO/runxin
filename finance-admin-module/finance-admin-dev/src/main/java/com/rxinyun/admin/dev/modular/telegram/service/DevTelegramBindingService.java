package com.rxinyun.admin.dev.modular.telegram.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.dev.modular.telegram.entity.DevTelegramBinding;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramBindLinkResult;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramBindTokenParam;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramBindingPageParam;

public interface DevTelegramBindingService extends IService<DevTelegramBinding> {
   String USER_TYPE_MEMBER = "MEMBER";
   String USER_TYPE_ADMIN = "ADMIN";

   DevTelegramBindLinkResult createBindLink(DevTelegramBindTokenParam devTelegramBindTokenParam);

   DevTelegramBinding getEnabledBinding(String userType, String userId);

   void bindByTelegramStartToken(String token, String chatId, String telegramUserId, String telegramUsername);

   void handleWebhook(JSONObject update, String secretToken);

   Page<DevTelegramBinding> page(DevTelegramBindingPageParam devTelegramBindingPageParam);
}
