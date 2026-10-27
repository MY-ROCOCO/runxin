package com.rxinyun.admin.business.modular.esc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.business.modular.esc.entity.CallbackLog;

public interface CallbackLogService extends IService<CallbackLog> {
   void logCallback(String callbackType, String action, String requestData, String responseData, String serverId, Integer status, String errorMsg);
}
