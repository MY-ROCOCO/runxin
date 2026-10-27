package com.rxinyun.admin.dev.modular.message.provider;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.message.param.DevMessageIdParam;
import com.rxinyun.admin.dev.modular.message.param.DevMessageListParam;
import com.rxinyun.admin.dev.modular.message.param.DevMessageSendParam;
import com.rxinyun.admin.dev.modular.message.service.DevMessageService;
import com.rxinyun.admin.dev.modular.relation.entity.DevRelation;
import com.rxinyun.admin.dev.modular.relation.enums.DevRelationCategoryEnum;
import com.rxinyun.admin.dev.modular.relation.service.DevRelationService;
import com.rxinyun.dev.api.DevMessageApi;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DevMessageProvider implements DevMessageApi {
   @Resource
   private DevMessageService devMessageService;
   @Resource
   private DevRelationService devRelationService;

   public void sendMessage(List<String> receiverIdList, String subject) {
      this.sendMessage(receiverIdList, subject, null);
   }

   public void sendMessage(List<String> receiverIdList, String subject, String content) {
      this.sendMessageWithContent(receiverIdList, subject, null);
   }

   public void sendMessageWithContent(List<String> receiverIdList, String subject, String content) {
      this.sendMessageWithContent(receiverIdList, null, subject, content);
   }

   public void sendMessageWithContent(List<String> receiverIdList, String category, String subject, String content) {
      DevMessageSendParam devMessageSendParam = new DevMessageSendParam();
      devMessageSendParam.setReceiverIdList(receiverIdList);
      devMessageSendParam.setCategory(category);
      devMessageSendParam.setSubject(subject);
      devMessageSendParam.setContent(ObjectUtil.isEmpty(content) ? subject : content);
      this.devMessageService.send(devMessageSendParam);
   }

   public List<JSONObject> list(List<String> receiverIdList, Integer limit) {
      DevMessageListParam devMessageListParam = new DevMessageListParam();
      devMessageListParam.setReceiverIdList(receiverIdList);
      devMessageListParam.setLimit(limit);
      return this.devMessageService.list(devMessageListParam).stream().<JSONObject>map(JSONUtil::parseObj).collect(Collectors.toList());
   }

   public Long unreadCount(String loginId) {
      return this.devMessageService.unreadCount(loginId);
   }

   public Page<JSONObject> page(List<String> receiverIdList, String category) {
      return this.devMessageService.page(receiverIdList, category);
   }

   public JSONObject detail(String id) {
      DevMessageIdParam devMessageIdParam = new DevMessageIdParam();
      devMessageIdParam.setId(id);
      return JSONUtil.parseObj(this.devMessageService.detail(devMessageIdParam));
   }

   public void allMessageMarkRead() {
      String myMessageExtJson = "{\"read\":true}";
      this.devRelationService
         .update(
            (Wrapper<DevRelation>)((LambdaUpdateWrapper<DevRelation>)((LambdaUpdateWrapper<DevRelation>)new LambdaUpdateWrapper<DevRelation>().eq(DevRelation::getTargetId, StpUtil.getLoginIdAsString()))
                  .eq(DevRelation::getCategory, DevRelationCategoryEnum.MSG_TO_USER.getValue()))
               .set(DevRelation::getExtJson, myMessageExtJson)
         );
   }
}
