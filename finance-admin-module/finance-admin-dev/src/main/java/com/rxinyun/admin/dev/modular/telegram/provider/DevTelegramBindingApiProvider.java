package com.rxinyun.admin.dev.modular.telegram.provider;

import com.rxinyun.admin.dev.modular.telegram.entity.DevTelegramBinding;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramBindLinkResult;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramBindTokenParam;
import com.rxinyun.admin.dev.modular.telegram.service.DevTelegramBindingService;
import com.rxinyun.dev.api.DevTelegramBindingApi;
import com.rxinyun.dev.pojo.DevTelegramBindLinkInfo;
import com.rxinyun.dev.pojo.DevTelegramBindingInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class DevTelegramBindingApiProvider implements DevTelegramBindingApi {
   @Resource
   private DevTelegramBindingService devTelegramBindingService;

   public DevTelegramBindingInfo getBindingInfo(String userType, String userId) {
      DevTelegramBindingInfo info = new DevTelegramBindingInfo();
      DevTelegramBinding binding = this.devTelegramBindingService.getEnabledBinding(userType, userId);
      info.setBound(binding != null);
      if (binding != null) {
         info.setChatId(binding.getChatId());
         info.setTelegramUserId(binding.getTelegramUserId());
         info.setTelegramUsername(binding.getTelegramUsername());
      }

      return info;
   }

   public DevTelegramBindLinkInfo createBindLink(String userType, String userId) {
      DevTelegramBindTokenParam param = new DevTelegramBindTokenParam();
      param.setUserType(userType);
      param.setUserId(userId);
      DevTelegramBindLinkResult result = this.devTelegramBindingService.createBindLink(param);
      DevTelegramBindLinkInfo info = new DevTelegramBindLinkInfo();
      info.setToken(result.getToken());
      info.setBindUrl(result.getBindUrl());
      return info;
   }
}
