package com.rxinyun.admin.auth.modular.monitor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.auth.modular.monitor.param.AuthExitSessionParam;
import com.rxinyun.admin.auth.modular.monitor.param.AuthExitTokenParam;
import com.rxinyun.admin.auth.modular.monitor.param.AuthSessionPageParam;
import com.rxinyun.admin.auth.modular.monitor.result.AuthSessionAnalysisResult;
import com.rxinyun.admin.auth.modular.monitor.result.AuthSessionPageResult;
import java.util.List;

public interface AuthSessionService {
   AuthSessionAnalysisResult analysis();

   Page<AuthSessionPageResult> pageForB(AuthSessionPageParam authSessionPageParam);

   void exitSessionForB(List<AuthExitSessionParam> authExitSessionParamList);

   void exitTokenForB(List<AuthExitTokenParam> authExitTokenParamList);
}
