package com.rxinyun.admin.payments.modular.recharge.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.payments.modular.recharge.entity.Recharge;
import com.rxinyun.admin.payments.modular.recharge.param.RechargePageParam;
import com.rxinyun.admin.payments.modular.recharge.service.RechargeRecordsService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "充值记录"
)
@RestController
@RequestMapping({"/admin/recharge"})
public class RechargeRecordsController {
   private static final Logger log = LoggerFactory.getLogger(RechargeRecordsController.class);
   @Autowired
   private RechargeRecordsService rechargeRecordsService;

   @Operation(
      summary = "获取用户充值记录分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<Recharge>> page(RechargePageParam rechargePageParam) {
      return CommonResult.data(this.rechargeRecordsService.page(rechargePageParam));
   }
}
