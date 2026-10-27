package com.rxinyun.member.member.api;

import com.rxinyun.common.pojo.CommonResult;
import java.time.LocalDateTime;
import java.util.HashMap;

public interface CompanyRealApi {
   Long getCompanyRealCount(HashMap<String, LocalDateTime> map);

   CommonResult updateByStatus(Long memberNo, String status, String reason);
}
