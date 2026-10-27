package com.rxinyun.admin.production.modular.domain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.domain.entity.DomainTldPrice;
import com.rxinyun.admin.production.modular.domain.param.DomainTldPriceAddParam;
import com.rxinyun.admin.production.modular.domain.param.DomainTldPriceEditParam;
import com.rxinyun.admin.production.modular.domain.param.DomainTldPriceIdParam;
import com.rxinyun.admin.production.modular.domain.param.DomainTldPricePageParam;
import com.rxinyun.admin.production.modular.domain.service.DomainTldPriceService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "域名产品价格管理"
)
@RestController
@RequestMapping({"/admin/domain/price"})
@Validated
public class DomainTldPriceController {
   @Resource
   private DomainTldPriceService domainTldPriceService;

   @Operation(
      summary = "域名价格分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<DomainTldPrice>> page(DomainTldPricePageParam pageParam) {
      return CommonResult.data(this.domainTldPriceService.page(pageParam));
   }

   @Operation(
      summary = "新增域名价格"
   )
   @CommonLog("新增域名价格")
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody @Valid DomainTldPriceAddParam addParam) {
      this.domainTldPriceService.add(addParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑域名价格"
   )
   @CommonLog("编辑域名价格")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody @Valid DomainTldPriceEditParam editParam) {
      this.domainTldPriceService.edit(editParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "域名价格详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<DomainTldPrice> detail(@Valid DomainTldPriceIdParam idParam) {
      return CommonResult.data(this.domainTldPriceService.detail(idParam));
   }

   @Operation(
      summary = "删除域名价格"
   )
   @CommonLog("删除域名价格")
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<DomainTldPriceIdParam> idParamList) {
      this.domainTldPriceService.delete(idParamList);
      return CommonResult.ok();
   }
}
