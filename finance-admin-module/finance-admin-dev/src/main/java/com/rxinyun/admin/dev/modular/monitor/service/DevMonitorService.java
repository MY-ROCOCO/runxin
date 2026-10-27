package com.rxinyun.admin.dev.modular.monitor.service;

import com.rxinyun.admin.dev.modular.monitor.result.DevMonitorServerResult;

public interface DevMonitorService {
   DevMonitorServerResult serverInfo();

   DevMonitorServerResult networkInfo();
}
