package com.rxinyun.admin.system.modular.index.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.rxinyun.admin.system.modular.index.service.SysIndexService;
import com.rxinyun.admin.system.modular.org.service.SysOrgService;
import com.rxinyun.admin.system.modular.position.service.SysPositionService;
import com.rxinyun.admin.system.modular.relation.entity.SysRelation;
import com.rxinyun.admin.system.modular.relation.enums.SysRelationCategoryEnum;
import com.rxinyun.admin.system.modular.relation.service.SysRelationService;
import com.rxinyun.admin.system.modular.role.service.SysRoleService;
import com.rxinyun.admin.system.modular.user.service.SysUserService;
import com.rxinyun.auth.api.AuthApi;
import com.rxinyun.auth.core.pojo.SaBaseLoginUser;
import com.rxinyun.auth.core.util.StpLoginUserUtil;
import com.rxinyun.common.sse.CommonSseParam;
import com.rxinyun.dev.api.DevApi;
import com.rxinyun.dev.api.DevLogApi;
import com.rxinyun.dev.api.DevMessageApi;
import com.rxinyun.dev.api.DevSseApi;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SysIndexServiceImpl implements SysIndexService {
   @Resource
   private SysRelationService sysRelationService;
   @Resource
   private DevMessageApi devMessageApi;
   @Resource
   private DevLogApi devLogApi;
   @Resource
   private DevSseApi devSseApi;
   @Resource
   private SysUserService sysUserService;
   @Resource
   private SysRoleService sysRoleService;
   @Resource
   private SysOrgService sysOrgService;
   @Resource
   private SysPositionService sysPositionService;
   @Resource
   private DevApi devApi;
   @Resource
   private AuthApi authApi;

   @Override
   public void addSchedule(SysIndexScheduleAddParam sysIndexScheduleAddParam) {
      SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
      sysIndexScheduleAddParam.setScheduleUserId(loginUser.getId());
      sysIndexScheduleAddParam.setScheduleUserName(loginUser.getName());
      this.sysRelationService
         .saveRelationWithAppend(
            loginUser.getId(),
            sysIndexScheduleAddParam.getScheduleDate(),
            SysRelationCategoryEnum.SYS_USER_SCHEDULE_DATA.getValue(),
            JSONUtil.toJsonStr(sysIndexScheduleAddParam)
         );
   }

   @Override
   public void deleteSchedule(List<SysIndexScheduleIdParam> sysIndexScheduleIdParamList) {
      List<String> scheduleIdList = sysIndexScheduleIdParamList.stream().map(SysIndexScheduleIdParam::getId).collect(Collectors.toList());
      if (ObjectUtil.isNotEmpty(scheduleIdList)) {
         this.sysRelationService.removeByIds(scheduleIdList);
      }
   }

   @Override
   public List<SysIndexScheduleListResult> scheduleList(SysIndexScheduleListParam sysIndexScheduleListParam) {
      return this.sysRelationService
         .list(
            (Wrapper<SysRelation>)((LambdaQueryWrapper<SysRelation>)((LambdaQueryWrapper<SysRelation>)new LambdaQueryWrapper<SysRelation>().eq(SysRelation::getObjectId, StpUtil.getLoginIdAsString()))
                  .eq(SysRelation::getTargetId, sysIndexScheduleListParam.getScheduleDate()))
               .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_USER_SCHEDULE_DATA.getValue())
         )
         .stream()
         .map(
            sysRelation -> {
               SysIndexScheduleListResult sysIndexScheduleListResult = (SysIndexScheduleListResult)JSONUtil.toBean(
                  sysRelation.getExtJson(), SysIndexScheduleListResult.class
               );
               sysIndexScheduleListResult.setId(sysRelation.getId());
               return sysIndexScheduleListResult;
            }
         )
         .collect(Collectors.toList());
   }

   @Override
   public List<SysIndexMessageListResult> messageList(SysIndexMessageListParam sysIndexMessageListParam) {
      return this.devMessageApi
         .list(
            CollectionUtil.newArrayList(new String[]{StpUtil.getLoginIdAsString()}),
            ObjectUtil.isEmpty(sysIndexMessageListParam.getLimit()) ? 10 : sysIndexMessageListParam.getLimit()
         )
         .stream()
         .map(jsonObject -> (SysIndexMessageListResult)JSONUtil.toBean(jsonObject, SysIndexMessageListResult.class))
         .collect(Collectors.toList());
   }

   @Override
   public SysIndexMessageDetailResult messageDetail(SysIndexMessageIdParam sysIndexMessageIdParam) {
      return (SysIndexMessageDetailResult)JSONUtil.toBean(this.devMessageApi.detail(sysIndexMessageIdParam.getId()), SysIndexMessageDetailResult.class);
   }

   @Override
   public void allMessageMarkRead() {
      this.devMessageApi.allMessageMarkRead();
   }

   @Override
   public List<SysIndexVisLogListResult> visLogList() {
      return this.devLogApi
         .currentUserVisLogList()
         .stream()
         .map(jsonObject -> (SysIndexVisLogListResult)JSONUtil.toBean(jsonObject, SysIndexVisLogListResult.class))
         .collect(Collectors.toList());
   }

   @Override
   public List<SysIndexOpLogListResult> opLogList() {
      return this.devLogApi
         .currentUserOpLogList()
         .stream()
         .map(jsonObject -> (SysIndexOpLogListResult)JSONUtil.toBean(jsonObject, SysIndexOpLogListResult.class))
         .collect(Collectors.toList());
   }

   @Override
   public SseEmitter createSseConnect(String clientId) {
      Consumer<CommonSseParam> consumer = m -> {
         long unreadMessageNum = this.devMessageApi.unreadCount(m.getLoginId());
         this.devSseApi.sendMessageToOneClient(m.getClientId(), String.valueOf(unreadMessageNum));
      };
      return this.devSseApi.createSseConnect(clientId, true, false, consumer);
   }

   @Override
   public SysBizDataCountResult getBizDataCount() {
      SysBizDataCountResult result = new SysBizDataCountResult();
      result.setUserCount(this.sysUserService.count());
      result.setRoleCount(this.sysRoleService.count());
      result.setOrgCount(this.sysOrgService.count());
      result.setPositionCount(this.sysPositionService.count());
      return result;
   }

   @Override
   public SysOpDataCountResult getOpDataCount() {
      JSONObject devObj = this.devApi.getDevOpCount();
      SysOpDataCountResult result = (SysOpDataCountResult)BeanUtil.toBean(devObj, SysOpDataCountResult.class);
      JSONObject authObj = this.authApi.getUserSessionCount();
      result.setBackUserSessionCount(authObj.getLong("backUserSessionCount"));
      result.setClientUserSessionCount(authObj.getLong("clientUserSessionCount"));
      result.setThirdUserCount(this.authApi.getThirdUserCount());
      return result;
   }

   @Override
   public SysToolDataCountResult getToolDataCount() {
      return (SysToolDataCountResult)BeanUtil.toBean(this.devApi.getToolDataCount(), SysToolDataCountResult.class);
   }
}
