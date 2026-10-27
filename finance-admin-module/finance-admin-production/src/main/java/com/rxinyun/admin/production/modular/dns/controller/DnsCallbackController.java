package com.rxinyun.admin.production.modular.dns.controller;

import com.alibaba.fastjson.JSON;
import com.rxinyun.admin.production.modular.dns.param.DnsCallbackParam;
import com.rxinyun.admin.production.modular.dns.service.DnsProductionService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "DNS节点回调"
)
@RestController
@RequestMapping({"/api/dns"})
public class DnsCallbackController {
   @Resource
   private DnsProductionService dnsProductionService;

   @Operation(
      summary = "DNS节点统一回调"
   )
   @PostMapping({"/callback"})
   public CommonResult<String> callback(@RequestBody String requestBody, HttpServletRequest request) {
      DnsCallbackParam callbackParam = (DnsCallbackParam)JSON.parseObject(requestBody, DnsCallbackParam.class);
      this.dnsProductionService
         .handleNodeCallback(callbackParam, request.getHeader("Authorization"), request.getHeader("X-DNSgo-Event-ID"), this.headers(request), requestBody);
      return CommonResult.ok("回调已接收");
   }

   private Map<String, String> headers(HttpServletRequest request) {
      Map<String, String> headers = new HashMap<>();
      Enumeration<String> names = request.getHeaderNames();

      while (names != null && names.hasMoreElements()) {
         String name = names.nextElement();
         headers.put(name, request.getHeader(name));
      }

      return headers;
   }
}
