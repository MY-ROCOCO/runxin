package com.rxinyun.admin.dev.modular.message.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.dev.modular.message.entity.DevMessage;
import com.rxinyun.admin.dev.modular.message.enums.DevMessageCategoryEnum;
import com.rxinyun.admin.dev.modular.message.mapper.DevMessageMapper;
import com.rxinyun.admin.dev.modular.message.param.DevMessageIdParam;
import com.rxinyun.admin.dev.modular.message.param.DevMessageListParam;
import com.rxinyun.admin.dev.modular.message.param.DevMessagePageParam;
import com.rxinyun.admin.dev.modular.message.param.DevMessageSendParam;
import com.rxinyun.admin.dev.modular.message.result.DevMessageResult;
import com.rxinyun.admin.dev.modular.message.service.DevMessageService;
import com.rxinyun.admin.dev.modular.relation.entity.DevRelation;
import com.rxinyun.admin.dev.modular.relation.enums.DevRelationCategoryEnum;
import com.rxinyun.admin.dev.modular.relation.service.DevRelationService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.sys.api.SysUserApi;
import jakarta.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DevMessageServiceImpl extends ServiceImpl<DevMessageMapper, DevMessage> implements DevMessageService {
   @Resource
   private SysUserApi sysUserApi;
   @Resource
   private DevRelationService devRelationService;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void send(DevMessageSendParam devMessageSendParam) {
      devMessageSendParam.setContent(ObjectUtil.isEmpty(devMessageSendParam.getContent()) ? devMessageSendParam.getSubject() : devMessageSendParam.getContent());
      devMessageSendParam.setCategory(
         ObjectUtil.isEmpty(devMessageSendParam.getCategory()) ? DevMessageCategoryEnum.SYS.getValue() : devMessageSendParam.getCategory()
      );
      DevMessage devMessage = (DevMessage)BeanUtil.toBean(devMessageSendParam, DevMessage.class);
      this.save(devMessage);
      List<String> receiverIdList = devMessageSendParam.getReceiverIdList();
      if (ObjectUtil.isNotEmpty(receiverIdList)) {
         List<String> extJsonList = receiverIdList.stream()
            .map(userId -> JSONUtil.toJsonStr(JSONUtil.createObj().set("read", false)))
            .collect(Collectors.toList());
         this.devRelationService.saveRelationBatchWithAppend(devMessage.getId(), receiverIdList, DevRelationCategoryEnum.MSG_TO_USER.getValue(), extJsonList);
      }
   }

   @Override
   public Page<DevMessage> page(DevMessagePageParam devMessagePageParam) {
      QueryWrapper<DevMessage> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(devMessagePageParam.getSearchKey())) {
         queryWrapper.lambda().like(DevMessage::getSubject, devMessagePageParam.getSearchKey());
      }

      if (ObjectUtil.isNotEmpty(devMessagePageParam.getReceiveUserId())) {
         queryWrapper.lambda()
            .in(
               DevMessage::getId,
               this.devRelationService
                  .list(
                     (Wrapper<DevRelation>)((LambdaQueryWrapper<DevRelation>)new LambdaQueryWrapper<DevRelation>().eq(DevRelation::getCategory, DevRelationCategoryEnum.MSG_TO_USER.getValue()))
                        .eq(DevRelation::getTargetId, devMessagePageParam.getReceiveUserId())
                  )
                  .stream()
                  .map(DevRelation::getObjectId)
                  .collect(Collectors.toList())
            );
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{devMessagePageParam.getSortField(), devMessagePageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(devMessagePageParam.getSortOrder());
         queryWrapper.orderBy(
            true, devMessagePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(devMessagePageParam.getSortField())
         );
      }

      return (Page<DevMessage>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public Page<JSONObject> page(List<String> receiverIdList, String category) {
      if (ObjectUtil.isNotEmpty(receiverIdList)) {
         List<DevRelation> messageRelationList = this.devRelationService
            .getRelationListByTargetIdListAndCategory(receiverIdList, DevRelationCategoryEnum.MSG_TO_USER.getValue());
         List<String> messageIdList = messageRelationList.stream().map(DevRelation::getObjectId).collect(Collectors.toList());
         if (ObjectUtil.isNotEmpty(messageIdList)) {
            LambdaQueryWrapper<DevMessage> lambdaQueryWrapper = new LambdaQueryWrapper();
            ((LambdaQueryWrapper<DevMessage>)lambdaQueryWrapper.in(DevMessage::getId, messageIdList)).orderByDesc(CommonEntity::getCreateTime);
            if (ObjectUtil.isNotEmpty(category)) {
               lambdaQueryWrapper.eq(DevMessage::getCategory, category);
            }

            Page<JSONObject> resultPage = new Page();
            Page<DevMessage> devMessagePage = (Page<DevMessage>)this.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
            BeanUtil.copyProperties(devMessagePage, resultPage, new String[0]);
            List<String> relationMessageIdList = CollStreamUtil.toList(messageRelationList, DevRelation::getObjectId);
            List<JSONObject> jsonObjectList = devMessagePage.getRecords()
               .stream()
               .map(
                  obj -> {
                     JSONObject jsonObject = JSONUtil.parseObj(obj);
                     jsonObject.set(
                        "read", JSONUtil.parseObj(messageRelationList.get(relationMessageIdList.indexOf(jsonObject.getStr("id"))).getExtJson()).getBool("read")
                     );
                     return jsonObject;
                  }
               )
               .collect(Collectors.toList());
            CollectionUtil.sort(jsonObjectList, Comparator.comparingInt(jsonObject -> Convert.toInt(jsonObject.getBool("read"))));
            resultPage.setRecords(jsonObjectList);
            return resultPage;
         } else {
            return CommonPageRequest.defaultPage();
         }
      } else {
         return CommonPageRequest.defaultPage();
      }
   }

   @Override
   public List<DevMessage> list(DevMessageListParam devMessageListParam) {
      if (ObjectUtil.isNotEmpty(devMessageListParam.getReceiverIdList())) {
         List<String> messageIdList = this.devRelationService
            .getRelationListByTargetIdListAndCategory(devMessageListParam.getReceiverIdList(), DevRelationCategoryEnum.MSG_TO_USER.getValue())
            .stream()
            .filter(devRelation -> JSONUtil.parseObj(devRelation.getExtJson()).getBool("read").equals(false))
            .map(DevRelation::getObjectId)
            .collect(Collectors.toList());
         if (ObjectUtil.isNotEmpty(messageIdList)) {
            LambdaQueryWrapper<DevMessage> lambdaQueryWrapper = new LambdaQueryWrapper();
            ((LambdaQueryWrapper<DevMessage>)lambdaQueryWrapper.in(DevMessage::getId, messageIdList)).orderByDesc(CommonEntity::getCreateTime);
            return ((Page)this.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper)).getRecords();
         } else {
            return CollectionUtil.newArrayList(new DevMessage[0]);
         }
      } else {
         return CollectionUtil.newArrayList(new DevMessage[0]);
      }
   }

   @Override
   public Long unreadCount(String loginId) {
      return this.devRelationService
         .getRelationListByTargetIdAndCategory(loginId, DevRelationCategoryEnum.MSG_TO_USER.getValue())
         .stream()
         .filter(devRelation -> JSONUtil.parseObj(devRelation.getExtJson()).getBool("read").equals(false))
         .count();
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<DevMessageIdParam> devMessageIdParamList) {
      List<String> devMessageIdList = CollStreamUtil.toList(devMessageIdParamList, DevMessageIdParam::getId);
      if (ObjectUtil.isNotEmpty(devMessageIdList)) {
         this.devRelationService
            .remove(
               (Wrapper<DevRelation>)((LambdaUpdateWrapper<DevRelation>)new LambdaUpdateWrapper<DevRelation>().eq(DevRelation::getCategory, DevRelationCategoryEnum.MSG_TO_USER.getValue()))
                  .in(DevRelation::getObjectId, devMessageIdList)
            );
         this.removeByIds(devMessageIdList);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public DevMessageResult detail(DevMessageIdParam devMessageIdParam) {
      DevMessageResult devMessageResult = new DevMessageResult();
      DevMessage devMessage = this.queryEntity(devMessageIdParam.getId());
      BeanUtil.copyProperties(devMessage, devMessageResult, new String[0]);
      DevRelation myMessage = (DevRelation)this.devRelationService
         .getOne(
            (Wrapper<DevRelation>)((LambdaQueryWrapper<DevRelation>)((LambdaQueryWrapper<DevRelation>)new LambdaQueryWrapper<DevRelation>().eq(DevRelation::getObjectId, devMessage.getId()))
                  .eq(DevRelation::getTargetId, StpUtil.getLoginIdAsString()))
               .eq(DevRelation::getCategory, DevRelationCategoryEnum.MSG_TO_USER.getValue())
         );
      if (ObjectUtil.isNotEmpty(myMessage)) {
         String myMessageExtJson = JSONUtil.toJsonStr(JSONUtil.parseObj(myMessage.getExtJson()).set("read", true));
         this.devRelationService
            .update(
               (Wrapper<DevRelation>)((LambdaUpdateWrapper<DevRelation>)((LambdaUpdateWrapper<DevRelation>)((LambdaUpdateWrapper<DevRelation>)new LambdaUpdateWrapper<DevRelation>()
                           .eq(DevRelation::getObjectId, devMessage.getId()))
                        .eq(DevRelation::getTargetId, StpUtil.getLoginIdAsString()))
                     .eq(DevRelation::getCategory, DevRelationCategoryEnum.MSG_TO_USER.getValue()))
                  .set(DevRelation::getExtJson, myMessageExtJson)
            );
      }

      List<DevMessageResult.DevReceiveInfo> receiveInfoList = this.devRelationService
         .getRelationListByObjectIdAndCategory(devMessage.getId(), DevRelationCategoryEnum.MSG_TO_USER.getValue())
         .stream()
         .map(devRelation -> {
            DevMessageResult.DevReceiveInfo devReceiveInfo = new DevMessageResult.DevReceiveInfo();
            JSONObject userObj = null;

            try {
               userObj = this.sysUserApi.getUserByIdWithException(devRelation.getTargetId());
            } catch (Exception var5x) {
            }

            String userName = "未知用户";
            if (ObjectUtil.isNotEmpty(userObj)) {
               userName = userObj.getStr("name");
            }

            devReceiveInfo.setReceiveUserId(devRelation.getTargetId());
            devReceiveInfo.setReceiveUserName(userName);
            devReceiveInfo.setRead(JSONUtil.parseObj(devRelation.getExtJson()).getBool("read"));
            return devReceiveInfo;
         })
         .collect(Collectors.toList());
      devMessageResult.setReceiveInfoList(receiveInfoList);
      return devMessageResult;
   }

   @Override
   public DevMessage queryEntity(String id) {
      DevMessage devMessage = (DevMessage)this.getById(id);
      if (ObjectUtil.isEmpty(devMessage)) {
         throw new CommonException("站内信不存在，id值为：{}", new Object[]{id});
      } else {
         return devMessage;
      }
   }
}
