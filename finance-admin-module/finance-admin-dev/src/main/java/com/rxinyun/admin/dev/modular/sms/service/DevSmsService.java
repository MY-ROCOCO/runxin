package com.rxinyun.admin.dev.modular.sms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.dev.modular.sms.entity.DevSms;
import com.rxinyun.admin.dev.modular.sms.param.DevSmsIdParam;
import com.rxinyun.admin.dev.modular.sms.param.DevSmsPageParam;
import com.rxinyun.admin.dev.modular.sms.param.DevSmsSendParam;
import java.util.List;

public interface DevSmsService extends IService<DevSms> {
   void sendSms(DevSmsSendParam devSmsSendParam);

   void sendSmsAsync(DevSmsSendParam devSmsSendParam);

   void saveFailedRecordInNewTx(DevSmsSendParam devSmsSendParam, String engineKey, String errorMessage);

   List<DevSms> listPendingSms(int limit);

   boolean markSending(String id);

   void dispatchQueuedSms(DevSms devSms);

   Page<DevSms> page(DevSmsPageParam devSmsPageParam);

   void delete(List<DevSmsIdParam> devSmsIdParamList);

   DevSms detail(DevSmsIdParam devSmsIdParam);

   DevSms queryEntity(String id);
}
