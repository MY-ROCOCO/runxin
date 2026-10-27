package com.rxinyun.admin.marketing.modular.marketing.controller;

import com.rxinyun.admin.marketing.modular.marketing.entity.DiscountDetail;
import com.rxinyun.admin.marketing.modular.marketing.service.DiscountDetailService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "折扣详情管理"
)
@RestController
@RequestMapping({"/admin/marketing/discount/detail"})
public class DiscountDetailController {
   @Resource
   private DiscountDetailService discountDetailService;

   @Operation(
      summary = "根据模板ID获取折扣详情列表"
   )
   @GetMapping({"/listByTemplateId"})
   public CommonResult<List<DiscountDetail>> listByTemplateId(@RequestParam String templateId) {
      return CommonResult.data(this.discountDetailService.listByTemplateId(templateId));
   }

   @Operation(
      summary = "批量添加折扣详情"
   )
   @PostMapping({"/batchAdd"})
   public CommonResult<Void> batchAdd(@RequestBody List<DiscountDetail> discountDetails) {
      this.discountDetailService.batchAdd(discountDetails);
      return CommonResult.ok();
   }
}
