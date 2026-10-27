package com.rxinyun.member.auth.modular.monitor.service.impl;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.util.CommonTimeFormatUtil;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.member.auth.modular.monitor.param.AuthExitSessionParam;
import com.rxinyun.member.auth.modular.monitor.param.AuthExitTokenParam;
import com.rxinyun.member.auth.modular.monitor.param.AuthSessionPageParam;
import com.rxinyun.member.auth.modular.monitor.result.AuthSessionAnalysisResult;
import com.rxinyun.member.auth.modular.monitor.result.AuthSessionPageResult;
import com.rxinyun.member.auth.modular.monitor.service.MemberAuthSessionService;
import com.rxinyun.member.core.util.StpClientUtil;
import com.rxinyun.member.member.api.MemberSaBaseLoginUserApi;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MemberAuthSessionServiceImpl implements MemberAuthSessionService {
   @Resource
   private MemberSaBaseLoginUserApi clientLoginUserApi;

   @Override
   public AuthSessionAnalysisResult analysis() {
      AuthSessionAnalysisResult authSessionAnalysisResult = new AuthSessionAnalysisResult();
      List<JSONObject> sessionListB = StpUtil.searchSessionId("", -1, -1, true).stream().map(sessionId -> {
         JSONObject jsonObject = JSONUtil.createObj();
         String userId = (String)StrUtil.split(sessionId, ":").get(3);
         SaSession saSession = StpUtil.getSessionByLoginId(userId, false);
         int tokenCount = saSession.getTokenSignList().size();
         long createTime = saSession.getCreateTime();
         jsonObject.set("userId", userId);
         jsonObject.set("tokenCount", tokenCount);
         jsonObject.set("createTime", new Date(createTime));
         return jsonObject;
      }).collect(Collectors.toList());
      List<JSONObject> sessionListC = StpClientUtil.searchSessionId("", -1, -1, true).stream().map(sessionId -> {
         JSONObject jsonObject = JSONUtil.createObj();
         String userId = (String)StrUtil.split(sessionId, ":").get(3);
         SaSession saSession = StpClientUtil.getSessionByLoginId(userId, false);
         int tokenCount = saSession.getTokenSignList().size();
         long createTime = saSession.getCreateTime();
         jsonObject.set("userId", userId);
         jsonObject.set("tokenCount", tokenCount);
         jsonObject.set("createTime", new Date(createTime));
         return jsonObject;
      }).collect(Collectors.toList());
      List<Integer> tokenCountList = CollectionUtil.newArrayList(new Integer[0]);
      tokenCountList.addAll(sessionListB.stream().map(jsonObject -> jsonObject.getInt("tokenCount")).collect(Collectors.toList()));
      tokenCountList.addAll(sessionListC.stream().map(jsonObject -> jsonObject.getInt("tokenCount")).collect(Collectors.toList()));
      CollectionUtil.sort(tokenCountList, Comparator.comparingInt(Integer::intValue));
      int currentSessionTotalCount = sessionListB.size() + sessionListC.size();
      authSessionAnalysisResult.setCurrentSessionTotalCount(Convert.toStr(currentSessionTotalCount));
      authSessionAnalysisResult.setMaxTokenCount(Convert.toStr(tokenCountList.get(tokenCountList.size() - 1)));
      List<Date> sessionCreateTimeList = CollectionUtil.newArrayList(new Date[0]);
      sessionCreateTimeList.addAll(sessionListB.stream().map(jsonObject -> jsonObject.getDate("createTime")).collect(Collectors.toList()));
      sessionCreateTimeList.addAll(sessionListC.stream().map(jsonObject -> jsonObject.getDate("createTime")).collect(Collectors.toList()));
      LocalDateTime oneHourAgo = TimeUtil.now().minusHours(1L);
      authSessionAnalysisResult.setOneHourNewlyAdded(
         Convert.toStr(sessionCreateTimeList.stream().filter(date -> date.after(TimeUtil.toDate(oneHourAgo))).count())
      );
      authSessionAnalysisResult.setProportionOfBAndC(sessionListB.size() + "/" + sessionListC.size());
      return authSessionAnalysisResult;
   }

   @Override
   public Page<AuthSessionPageResult> pageForC(AuthSessionPageParam authSessionPageParam) {
      Page<AuthSessionPageResult> defaultPage = CommonPageRequest.defaultPage();
      long current = defaultPage.getCurrent();
      int total = StpClientUtil.searchSessionId("", -1, Convert.toInt(defaultPage.getSize()), true).size();
      if (ObjectUtil.isNotEmpty(total)) {
         defaultPage = new Page(current, defaultPage.getSize(), total);
         String keyword = "";
         if (ObjectUtil.isNotEmpty(authSessionPageParam.getUserId())) {
            keyword = authSessionPageParam.getUserId();
         }

         List<String> userIdList = StpClientUtil.searchSessionId(
               keyword, Convert.toInt((current - 1L) * defaultPage.getSize()), Convert.toInt(defaultPage.getSize()), true
            )
            .stream()
            .map(sessionId -> (String)StrUtil.split(sessionId, ":").get(3))
            .collect(Collectors.toList());
         if (ObjectUtil.isNotEmpty(userIdList)) {
            List<AuthSessionPageResult> authSessionPageResultList = this.clientLoginUserApi
               .listUserByUserIdList(userIdList)
               .stream()
               .map(
                  userJsonObject -> {
                     SaSession saSession = StpClientUtil.getSessionByLoginId(userJsonObject.getStr("id"), false);
                     AuthSessionPageResult authSessionPageResult = (AuthSessionPageResult)JSONUtil.toBean(userJsonObject, AuthSessionPageResult.class);
                     authSessionPageResult.setSessionId(saSession.getId());
                     authSessionPageResult.setSessionCreateTime(new Date(saSession.getCreateTime()));
                     long sessionTimeOut = saSession.getTimeout();
                     if (sessionTimeOut == -1L) {
                        authSessionPageResult.setSessionTimeout("永久");
                     } else {
                        authSessionPageResult.setSessionTimeout(CommonTimeFormatUtil.formatSeconds(saSession.getTimeout()));
                     }

                     List<AuthSessionPageResult.TokenSignInfo> tokenInfoList = saSession.getTokenSignList()
                        .stream()
                        .filter(tokenSign -> {
                           long tokenTimeout = SaManager.getSaTokenDao().getTimeout(StpClientUtil.stpLogic.splicingKeyTokenValue(tokenSign.getValue()));
                           return tokenTimeout != -2L;
                        })
                        .map(
                           tokenSign -> {
                              AuthSessionPageResult.TokenSignInfo tokenSignInfo = new AuthSessionPageResult.TokenSignInfo();
                              tokenSignInfo.setTokenValue(tokenSign.getValue());
                              tokenSignInfo.setTokenDevice(tokenSign.getDevice());
                              long tokenTimeout = SaManager.getSaTokenDao().getTimeout(StpClientUtil.stpLogic.splicingKeyTokenValue(tokenSign.getValue()));
                              long tokenTimeoutConfig = StpClientUtil.stpLogic.getConfigOrGlobal().getTimeout();
                              if (tokenTimeout == -1L) {
                                 tokenSignInfo.setTokenTimeout("永久");
                                 tokenSignInfo.setTokenTimeoutPercent(100.0);
                              } else {
                                 tokenSignInfo.setTokenTimeout(
                                    CommonTimeFormatUtil.formatSeconds(
                                       SaManager.getSaTokenDao().getTimeout(StpClientUtil.stpLogic.splicingKeyTokenValue(tokenSign.getValue()))
                                    )
                                 );
                                 if (tokenTimeoutConfig == -1L) {
                                    tokenSignInfo.setTokenTimeoutPercent(0.0);
                                 } else {
                                    tokenSignInfo.setTokenTimeoutPercent(NumberUtil.div((float)tokenTimeout, (float)tokenTimeoutConfig));
                                 }
                              }

                              return tokenSignInfo;
                           }
                        )
                        .collect(Collectors.toList());
                     authSessionPageResult.setTokenCount(tokenInfoList.size());
                     authSessionPageResult.setTokenSignList(tokenInfoList);
                     return authSessionPageResult;
                  }
               )
               .collect(Collectors.toList());
            defaultPage.setRecords(authSessionPageResultList);
         }
      }

      return defaultPage;
   }

   @Override
   public void exitSessionForB(List<AuthExitSessionParam> authExitSessionParamList) {
      authExitSessionParamList.forEach(authExitSessionParam -> StpUtil.logout(authExitSessionParam.getUserId()));
   }

   @Override
   public void exitSessionForC(List<AuthExitSessionParam> authExitSessionParamList) {
      authExitSessionParamList.forEach(authExitSessionParam -> StpClientUtil.logout(authExitSessionParam.getUserId()));
   }

   @Override
   public void exitTokenForB(List<AuthExitTokenParam> authExitTokenParamList) {
      authExitTokenParamList.forEach(authExitTokenParam -> StpUtil.logoutByTokenValue(authExitTokenParam.getTokenValue()));
   }

   @Override
   public void exitTokenForC(List<AuthExitTokenParam> authExitTokenParamList) {
      authExitTokenParamList.forEach(authExitTokenParam -> StpClientUtil.logoutByTokenValue(authExitTokenParam.getTokenValue()));
   }
}
