package com.rxinyun.admin.dev.modular.email.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.dev.modular.email.entity.DevEmail;
import com.rxinyun.admin.dev.modular.email.param.DevEmailIdParam;
import com.rxinyun.admin.dev.modular.email.param.DevEmailPageParam;
import com.rxinyun.admin.dev.modular.email.param.DevEmailSendLocalHtmlParam;
import com.rxinyun.admin.dev.modular.email.param.DevEmailSendLocalTxtParam;
import com.rxinyun.admin.dev.modular.email.param.DevEmailSendParam;
import java.util.List;

public interface DevEmailService extends IService<DevEmail> {
   void sendLocal(DevEmailSendLocalTxtParam devEmailSendLocalTxtParam);

   void sendLocal(DevEmailSendLocalHtmlParam devEmailSendLocalHtmlParam);

   Page<DevEmail> page(DevEmailPageParam devEmailPageParam);

   void delete(List<DevEmailIdParam> devEmailIdParamList);

   DevEmail detail(DevEmailIdParam devEmailIdParam);

   DevEmail queryEntity(String id);

   void sendEmail(DevEmailSendParam devEmailSendParam);

   List<DevEmail> listPendingEmails(int limit);

   boolean markSending(String id);

   void dispatchQueuedEmail(DevEmail devEmail);
}
