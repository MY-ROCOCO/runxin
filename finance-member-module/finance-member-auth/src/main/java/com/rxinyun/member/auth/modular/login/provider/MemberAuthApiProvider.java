package com.rxinyun.member.auth.modular.login.provider;

import cn.dev33.satoken.session.SaSession;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rxinyun.member.auth.modular.login.service.MemberAuthService;
import com.rxinyun.member.auth.modular.third.service.MemberAuthThirdService;
import com.rxinyun.member.core.util.StpClientUtil;
import com.rxinyun.member.member.api.MemberAuthApi;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MemberAuthApiProvider implements MemberAuthApi {
   @Resource
   private MemberAuthThirdService authThirdService;
   @Resource
   private MemberAuthService memberAuthService;

   public JSONObject getUserSessionCount() {
      JSONObject resultObj = new JSONObject();
      List<JSONObject> sessionListC = StpClientUtil.searchSessionId("", -1, -1, true).stream().map(sessionId -> {
         JSONObject jsonObject = JSONUtil.createObj();
         String userId = (String)StrUtil.split(sessionId, ":").get(3);
         SaSession saSession = StpClientUtil.getSessionByLoginId(userId, false);
         int tokenCount = saSession.getTokenSignList().size();
         long createTime = saSession.getCreateTime();
         jsonObject.set("userId", userId);
         jsonObject.set("tokenCount", tokenCount);
         jsonObject.set("createTime", DateTime.of(createTime));
         return jsonObject;
      }).collect(Collectors.toList());
      resultObj.set("clientUserSessionCount", sessionListC.size());
      return resultObj;
   }

   public Long getThirdUserCount() {
      return this.authThirdService.count();
   }

   public SaBaseClientLoginUser getClientLoginUser() {
      return this.memberAuthService.getClientLoginUser();
   }
}
