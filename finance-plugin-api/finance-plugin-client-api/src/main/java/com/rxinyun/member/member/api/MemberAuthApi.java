package com.rxinyun.member.member.api;

import cn.hutool.json.JSONObject;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;

public interface MemberAuthApi {
   JSONObject getUserSessionCount();

   Long getThirdUserCount();

   SaBaseClientLoginUser getClientLoginUser();
}
