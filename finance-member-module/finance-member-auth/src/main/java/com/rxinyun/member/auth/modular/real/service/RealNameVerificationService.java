package com.rxinyun.member.auth.modular.real.service;

import com.rxinyun.common.pojo.CommonResult;

public interface RealNameVerificationService {
   String initializeFaceVerification(String certName, String certNo);

   CommonResult certifyFace(String certifyId);

   CommonResult certifyQuery(String certifyId);
}
