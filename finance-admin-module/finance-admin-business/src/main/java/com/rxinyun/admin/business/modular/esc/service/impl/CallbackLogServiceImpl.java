package com.rxinyun.admin.business.modular.esc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.esc.entity.CallbackLog;
import com.rxinyun.admin.business.modular.esc.mapper.CallbackLogMapper;
import com.rxinyun.admin.business.modular.esc.service.CallbackLogService;
import com.rxinyun.common.util.TimeUtil;
import org.springframework.stereotype.Service;

@Service
public class CallbackLogServiceImpl extends ServiceImpl<CallbackLogMapper, CallbackLog> implements CallbackLogService {
   @Override
   public void logCallback(String callbackType, String action, String requestData, String responseData, String serverId, Integer status, String errorMsg) {
      CallbackLog log = new CallbackLog();
      log.setCallbackType(callbackType);
      log.setCallbackParam(serverId);
      log.setAction(action);
      log.setRequestData(requestData);
      log.setResponseData(responseData);
      log.setServerId(serverId);
      log.setStatus(status);
      log.setErrorMsg(errorMsg);
      log.setCallbackTime(TimeUtil.toDate(TimeUtil.now()));
      this.save(log);
   }
}
