package com.rxinyun.admin.business.modular.domain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.domain.entity.DomainRegistrationRequest;
import com.rxinyun.admin.business.modular.domain.param.DomainRegistrationRequestQueryParam;
import com.rxinyun.admin.business.modular.domain.param.DomainRegistrationRetryParam;
import com.rxinyun.admin.business.modular.domain.service.DomainRegistrationRequestService;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.production.api.DomainRegistrationRetryApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "业务域名管理"
)
@RestController
@RequestMapping({"/admin/domain"})
@Validated
public class DomainRegistrationRequestController {
   @Resource
   private DomainRegistrationRequestService domainRegistrationRequestService;
   @Resource
   private DomainRegistrationRetryApi domainRegistrationRetryApi;

   @Operation(
      summary = "分页查询上游注册请求记录（订单号、域名、状态可选筛选）"
   )
   @GetMapping({"/registrationRequest/list"})
   public CommonResult<Page<DomainRegistrationRequest>> page(@Validated DomainRegistrationRequestQueryParam param) {
      return CommonResult.data(this.domainRegistrationRequestService.page(param));
   }

   @Operation(
      summary = "重新发起域名注册（仅失败记录可重试）"
   )
   @PostMapping({"/registrationRequest/retry"})
   public CommonResult<Void> retry(@RequestBody @Validated DomainRegistrationRetryParam param) {
      this.domainRegistrationRetryApi.retry(param.getId());
      return CommonResult.ok();
   }
}
