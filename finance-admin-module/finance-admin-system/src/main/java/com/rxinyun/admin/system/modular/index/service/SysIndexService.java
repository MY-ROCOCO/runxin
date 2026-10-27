package com.rxinyun.admin.system.modular.index.service;

import com.rxinyun.admin.system.modular.index.param.SysIndexMessageIdParam;
import com.rxinyun.admin.system.modular.index.param.SysIndexMessageListParam;
import com.rxinyun.admin.system.modular.index.param.SysIndexScheduleAddParam;
import com.rxinyun.admin.system.modular.index.param.SysIndexScheduleIdParam;
import com.rxinyun.admin.system.modular.index.param.SysIndexScheduleListParam;
import com.rxinyun.admin.system.modular.index.result.SysBizDataCountResult;
import com.rxinyun.admin.system.modular.index.result.SysIndexMessageDetailResult;
import com.rxinyun.admin.system.modular.index.result.SysIndexMessageListResult;
import com.rxinyun.admin.system.modular.index.result.SysIndexOpLogListResult;
import com.rxinyun.admin.system.modular.index.result.SysIndexScheduleListResult;
import com.rxinyun.admin.system.modular.index.result.SysIndexVisLogListResult;
import com.rxinyun.admin.system.modular.index.result.SysOpDataCountResult;
import com.rxinyun.admin.system.modular.index.result.SysToolDataCountResult;
import java.util.List;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SysIndexService {
   void addSchedule(SysIndexScheduleAddParam sysIndexScheduleAddParam);

   void deleteSchedule(List<SysIndexScheduleIdParam> sysIndexScheduleIdParamList);

   List<SysIndexScheduleListResult> scheduleList(SysIndexScheduleListParam sysIndexScheduleListParam);

   List<SysIndexMessageListResult> messageList(SysIndexMessageListParam sysIndexMessageListParam);

   SysIndexMessageDetailResult messageDetail(SysIndexMessageIdParam sysIndexMessageIdParam);

   void allMessageMarkRead();

   List<SysIndexVisLogListResult> visLogList();

   List<SysIndexOpLogListResult> opLogList();

   SseEmitter createSseConnect(String clientId);

   SysBizDataCountResult getBizDataCount();

   SysOpDataCountResult getOpDataCount();

   SysToolDataCountResult getToolDataCount();
}
