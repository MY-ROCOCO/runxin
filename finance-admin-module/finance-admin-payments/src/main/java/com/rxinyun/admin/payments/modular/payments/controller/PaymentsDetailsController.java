package com.rxinyun.admin.payments.modular.payments.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.payments.modular.payments.entity.PaymentsRecord;
import com.rxinyun.admin.payments.modular.payments.param.PaymentsRecordPageParam;
import com.rxinyun.admin.payments.modular.payments.service.PaymentsDetailsService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "订单收支明细"
)
@RestController
@RequestMapping({"/admin/pay"})
@Validated
public class PaymentsDetailsController {
   private static final Logger log = LoggerFactory.getLogger(PaymentsDetailsController.class);
   @Resource
   private PaymentsDetailsService paymentsService;

   @Operation(
      summary = "获取收支明细分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<PaymentsRecord>> page(PaymentsRecordPageParam param) {
      return CommonResult.data(this.paymentsService.page(param));
   }
}
