package com.rxinyun.dev.api;

import com.rxinyun.dev.pojo.DevTelegramBindLinkInfo;
import com.rxinyun.dev.pojo.DevTelegramBindingInfo;

public interface DevTelegramBindingApi {
   DevTelegramBindingInfo getBindingInfo(String userType, String userId);

   DevTelegramBindLinkInfo createBindLink(String userType, String userId);
}
