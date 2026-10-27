package com.rxinyun.admin.dev.modular.message.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.dev.modular.message.entity.DevMessage;
import com.rxinyun.admin.dev.modular.message.param.DevMessageIdParam;
import com.rxinyun.admin.dev.modular.message.param.DevMessageListParam;
import com.rxinyun.admin.dev.modular.message.param.DevMessagePageParam;
import com.rxinyun.admin.dev.modular.message.param.DevMessageSendParam;
import com.rxinyun.admin.dev.modular.message.result.DevMessageResult;
import java.util.List;

public interface DevMessageService extends IService<DevMessage> {
   void send(DevMessageSendParam devMessageSendParam);

   Page<DevMessage> page(DevMessagePageParam devMessagePageParam);

   Page<JSONObject> page(List<String> receiverIdList, String category);

   List<DevMessage> list(DevMessageListParam devMessageListParam);

   Long unreadCount(String loginId);

   void delete(List<DevMessageIdParam> devMessageIdParamList);

   DevMessageResult detail(DevMessageIdParam devMessageIdParam);

   DevMessage queryEntity(String id);
}
