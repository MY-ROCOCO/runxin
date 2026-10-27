package com.rxinyun.admin.marketing.modular.coupon.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.marketing.modular.coupon.entity.CouponTemplate;
import com.rxinyun.admin.marketing.modular.coupon.param.CouponTemplateAddParam;
import com.rxinyun.admin.marketing.modular.coupon.param.CouponTemplateEditParam;
import com.rxinyun.admin.marketing.modular.coupon.param.CouponTemplatePageParam;
import com.rxinyun.admin.marketing.modular.coupon.service.CouponTemplateService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "优惠券模板管理"
)
@RestController
@RequestMapping({"/admin/marketing/coupon/template"})
public class CouponTemplateController {
   @Resource
   private CouponTemplateService couponTemplateService;

   @Operation(
      summary = "优惠券模板分页",
      description = "分页查询优惠券模板列表，供后台管理页面展示"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<CouponTemplate>> page(CouponTemplatePageParam pageParam) {
      return CommonResult.data(this.couponTemplateService.page(pageParam));
   }

   @Operation(
      summary = "新增优惠券模板",
      description = "创建优惠券模板，支持固定金额券、折扣券、满减券"
   )
   @PostMapping({"/add"})
   public CommonResult<String> add(@Valid @RequestBody CouponTemplateAddParam addParam) {
      this.couponTemplateService.add(addParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑优惠券模板",
      description = "修改优惠券模板的基本信息、适用范围和生效时间"
   )
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@Valid @RequestBody CouponTemplateEditParam editParam) {
      this.couponTemplateService.edit(editParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除优惠券模板",
      description = "按模板ID批量删除，已发放过的模板通常不允许删除"
   )
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "模板ID列表，支持批量删除",required = true,content = {@Content(
   mediaType = "application/json",
   array = @ArraySchema(
      schema = @Schema(
         type = "string",
         example = "2039000000000000001"
      )
   )
)}) @NotEmpty(message = "模板ID列表不能为空") @RequestBody List<String> ids) {
      this.couponTemplateService.delete(ids);
      return CommonResult.ok();
   }

   @Operation(
      summary = "优惠券模板详情",
      description = "根据模板ID查询优惠券模板详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<CouponTemplate> detail(@Parameter(description = "模板ID",required = true,example = "2039000000000000001") @RequestParam String id) {
      return CommonResult.data(this.couponTemplateService.detail(id));
   }
}
