package com.rxinyun.member.tools.modular.log.provider;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.dev.api.MemberLogApi;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.tools.modular.log.entity.MemberLog;
import com.rxinyun.member.tools.modular.log.enums.MemberLogCategoryEnum;
import com.rxinyun.member.tools.modular.log.service.MemberLogService;
import com.rxinyun.member.tools.modular.log.util.MemberLogUtil;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MemberLogApiProvider implements MemberLogApi {
   @Resource
   private MemberLogService memberLogService;

   public void executeLoginLog(String memberNo, String userName) {
      MemberLogUtil.executeLoginLog(memberNo, userName);
   }

   public void executeLogoutLog(String memberNo, String userName) {
      MemberLogUtil.executeLogoutLog(memberNo, userName);
   }

   public List<JSONObject> currentUserVisLogList() {
      return this.memberLogService
            .page(
               CommonPageRequest.defaultPage(),
               (Wrapper<MemberLog>)((LambdaQueryWrapper<MemberLog>)((LambdaQueryWrapper<MemberLog>)new LambdaQueryWrapper<MemberLog>()
                        .select(MemberLog::getName, MemberLog::getOpUser, MemberLog::getOpTime, MemberLog::getOpAddress, MemberLog::getOpIp)
                        .eq(MemberLog::getOpUser, StpClientLoginUserUtil.getClientLoginUser().getName()))
                     .in(MemberLog::getCategory, new Object[]{MemberLogCategoryEnum.LOGIN.getValue(), MemberLogCategoryEnum.LOGOUT.getValue()}))
                  .orderByDesc(MemberLog::getCreateTime)
            )
         .getRecords()
         .stream()
         .<JSONObject>map(JSONUtil::parseObj)
         .collect(Collectors.toList());
   }

   public List<JSONObject> currentUserOpLogList() {
      return this.memberLogService
            .page(
               CommonPageRequest.defaultPage(),
               (Wrapper<MemberLog>)((LambdaQueryWrapper<MemberLog>)((LambdaQueryWrapper<MemberLog>)new LambdaQueryWrapper<MemberLog>()
                        .select(MemberLog::getName, MemberLog::getOpUser, MemberLog::getOpTime, MemberLog::getOpAddress, MemberLog::getOpIp)
                        .eq(MemberLog::getOpUser, StpClientLoginUserUtil.getClientLoginUser().getName()))
                     .in(MemberLog::getCategory, new Object[]{MemberLogCategoryEnum.OPERATE.getValue(), MemberLogCategoryEnum.EXCEPTION.getValue()}))
                  .orderByDesc(MemberLog::getCreateTime)
            )
         .getRecords()
         .stream()
         .<JSONObject>map(JSONUtil::parseObj)
         .collect(Collectors.toList());
   }
}
