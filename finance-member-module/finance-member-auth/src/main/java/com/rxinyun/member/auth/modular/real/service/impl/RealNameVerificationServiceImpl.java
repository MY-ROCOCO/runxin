package com.rxinyun.member.auth.modular.real.service.impl;

import cn.hutool.core.util.StrUtil;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.auth.modular.real.service.AlipayFaceVerificationService;
import com.rxinyun.member.auth.modular.real.service.McloudRealNameVerificationService;
import com.rxinyun.member.auth.modular.real.service.RealNameVerificationService;
import com.rxinyun.member.auth.modular.real.service.ReckonkvmRealNameVerificationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class RealNameVerificationServiceImpl implements RealNameVerificationService {
   private static final String RUNXIN_SYS_DEFAULT_REAL = "RUNXIN_SYS_DEFAULT_REAL";
   private static final String REAL_ROUTE_ALIPAY = "ALIPAY_REAL";
   private static final String REAL_ROUTE_MCLOUD = "MCLOUD_REAL";
   private static final String REAL_ROUTE_RECKONKVM = "RECKONKVM_REAL";
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private AlipayFaceVerificationService alipayFaceVerificationService;
   @Resource
   private McloudRealNameVerificationService mcloudRealNameVerificationService;
   @Resource
   private ReckonkvmRealNameVerificationService reckonkvmRealNameVerificationService;

   @Override
   public String initializeFaceVerification(String certName, String certNo) {
      return this.selectedService().initializeFaceVerification(certName, certNo);
   }

   @Override
   public CommonResult certifyFace(String certifyId) {
      return this.selectedService().certifyFace(certifyId);
   }

   @Override
   public CommonResult certifyQuery(String certifyId) {
      return this.selectedService().certifyQuery(certifyId);
   }

   private RealNameVerificationService selectedService() {
      String realRoute = StrUtil.blankToDefault(this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_REAL"), "ALIPAY_REAL").trim();
      if ("ALIPAY_REAL".equalsIgnoreCase(realRoute)) {
         return this.alipayFaceVerificationService;
      } else if ("MCLOUD_REAL".equalsIgnoreCase(realRoute)) {
         return this.mcloudRealNameVerificationService;
      } else if ("RECKONKVM_REAL".equalsIgnoreCase(realRoute)) {
         return this.reckonkvmRealNameVerificationService;
      } else {
         throw new CommonException("不支持的实名认证接口：{}", new Object[]{realRoute});
      }
   }
}
