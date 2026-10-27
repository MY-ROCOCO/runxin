package com.rxinyun.member.auth.modular.real.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.real.service.RealNameVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "实名认证核验"
)
@RestController
@RequestMapping({"/member/alipay/face-verification", "/member/realname/face-verification"})
@Validated
public class AlipayFaceVerificationController {
   @Resource(
      name = "realNameVerificationServiceImpl"
   )
   private RealNameVerificationService faceVerificationService;

   @Operation(
      summary = "跳转实名认证开始认证"
   )
   @PostMapping({"/certify"})
   public CommonResult certifyFace(@RequestParam String certifyId) {
      return this.faceVerificationService.certifyFace(certifyId);
   }

   @Operation(
      summary = "查询实名认证记录"
   )
   @PostMapping({"/query"})
   public CommonResult certifyQuery(@RequestParam String certifyId) {
      return this.faceVerificationService.certifyQuery(certifyId);
   }
}
