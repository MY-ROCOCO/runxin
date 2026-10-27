package com.rxinyun.admin.dev.modular.telegram.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.dev.modular.telegram.entity.DevTelegram;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramIdParam;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramPageParam;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramSendParam;
import java.util.List;

public interface DevTelegramService extends IService<DevTelegram> {
   void sendTelegramAsync(DevTelegramSendParam devTelegramSendParam);

   List<DevTelegram> listPendingTelegram(int limit);

   boolean markSending(String id);

   void dispatchQueuedTelegram(DevTelegram devTelegram);

   Page<DevTelegram> page(DevTelegramPageParam devTelegramPageParam);

   void delete(List<DevTelegramIdParam> devTelegramIdParamList);

   DevTelegram detail(DevTelegramIdParam devTelegramIdParam);

   DevTelegram queryEntity(String id);
}
