package com.rxinyun.admin.marketing.modular.marketing.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.marketing.modular.marketing.entity.DiscountTemplate;
import com.rxinyun.admin.marketing.modular.marketing.param.DiscountTemplateAddParam;
import com.rxinyun.admin.marketing.modular.marketing.param.DiscountTemplateEditParam;
import com.rxinyun.admin.marketing.modular.marketing.param.DiscountTemplatePageParam;
import com.rxinyun.admin.marketing.modular.marketing.service.DiscountTemplateService;
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
   name = "折扣模板管理"
)
@RestController
@RequestMapping({"/admin/marketing/discount/template"})
public class DiscountTemplateController {
   @Resource
   private DiscountTemplateService discountTemplateService;

   @Operation(
      summary = "获取折扣模板分页列表"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<DiscountTemplate>> page(DiscountTemplatePageParam discountTemplatePageParam) {
      return CommonResult.data(this.discountTemplateService.page(discountTemplatePageParam));
   }

   @Operation(
      summary = "添加折扣模板"
   )
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody DiscountTemplateAddParam discountTemplateAddParam) {
      this.discountTemplateService.add(discountTemplateAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑折扣模板"
   )
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody DiscountTemplateEditParam discountTemplateEditParam) {
      this.discountTemplateService.edit(discountTemplateEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除折扣模板"
   )
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody List<String> ids) {
      this.discountTemplateService.delete(ids);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取折扣模板详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<DiscountTemplate> detail(@RequestParam String id) {
      return CommonResult.data(this.discountTemplateService.detail(id));
   }
}
