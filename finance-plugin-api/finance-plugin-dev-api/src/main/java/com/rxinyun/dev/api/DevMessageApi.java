package com.rxinyun.dev.api;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

public interface DevMessageApi {
   void sendMessage(List<String> receiverIdList, String subject);

   void sendMessage(List<String> receiverIdList, String category, String subject);

   void sendMessageWithContent(List<String> receiverIdList, String subject, String content);

   void sendMessageWithContent(List<String> receiverIdList, String category, String subject, String content);

   List<JSONObject> list(List<String> receiverIdList, Integer limit);

   Long unreadCount(String loginId);

   Page<JSONObject> page(List<String> receiverIdList, String category);

   JSONObject detail(String id);

   void allMessageMarkRead();
}
