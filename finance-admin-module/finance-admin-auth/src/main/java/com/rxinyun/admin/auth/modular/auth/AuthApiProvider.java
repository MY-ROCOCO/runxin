package com.rxinyun.admin.auth.modular.auth;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rxinyun.admin.auth.modular.third.service.AuthThirdService;
import com.rxinyun.auth.api.AuthApi;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AuthApiProvider implements AuthApi {
   @Resource
   private AuthThirdService authThirdService;

   public JSONObject getUserSessionCount() {
      JSONObject resultObj = new JSONObject();
      List<JSONObject> sessionListB = StpUtil.searchSessionId("", -1, -1, true).stream().map(sessionId -> {
         JSONObject jsonObject = JSONUtil.createObj();
         String userId = (String)StrUtil.split(sessionId, ":").get(3);
         SaSession saSession = StpUtil.getSessionByLoginId(userId, false);
         int tokenCount = saSession.getTokenSignList().size();
         long createTime = saSession.getCreateTime();
         jsonObject.set("userId", userId);
         jsonObject.set("tokenCount", tokenCount);
         jsonObject.set("createTime", DateTime.of(createTime));
         return jsonObject;
      }).collect(Collectors.toList());
      resultObj.set("backUserSessionCount", sessionListB.size());
      return resultObj;
   }

   public Long getThirdUserCount() {
      return this.authThirdService.count();
   }
}
