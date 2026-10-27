package com.rxinyun.member.auth.modular.monitor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.auth.modular.monitor.param.AuthExitSessionParam;
import com.rxinyun.member.auth.modular.monitor.param.AuthExitTokenParam;
import com.rxinyun.member.auth.modular.monitor.param.AuthSessionPageParam;
import com.rxinyun.member.auth.modular.monitor.result.AuthSessionAnalysisResult;
import com.rxinyun.member.auth.modular.monitor.result.AuthSessionPageResult;
import java.util.List;

public interface MemberAuthSessionService {
   AuthSessionAnalysisResult analysis();

   Page<AuthSessionPageResult> pageForC(AuthSessionPageParam authSessionPageParam);

   void exitSessionForB(List<AuthExitSessionParam> authExitSessionParamList);

   void exitSessionForC(List<AuthExitSessionParam> authExitSessionParamList);

   void exitTokenForB(List<AuthExitTokenParam> authExitTokenParamList);

   void exitTokenForC(List<AuthExitTokenParam> authExitTokenParamList);
}
