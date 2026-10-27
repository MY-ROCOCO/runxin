package com.rxinyun.admin.dev.modular.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.api.entity.ApiKey;
import com.rxinyun.admin.dev.modular.api.entity.ResourcePool;
import com.rxinyun.admin.dev.modular.api.param.ApiKeyAddParam;
import com.rxinyun.admin.dev.modular.api.param.ApiKeyEditParam;
import com.rxinyun.admin.dev.modular.api.param.ApiKeyIdParam;
import com.rxinyun.admin.dev.modular.api.param.ApiKeyPageParam;
import com.rxinyun.admin.dev.modular.api.param.ApiKeyStatusParam;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolAddParam;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolEditParam;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolIdParam;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolPageParam;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolQueryParam;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolStatusParam;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolSyncSelectedParam;
import com.rxinyun.admin.dev.modular.api.result.ApiKeyCreateResult;
import com.rxinyun.admin.dev.modular.api.service.ApiKeyService;
import com.rxinyun.admin.dev.modular.api.service.ResourcePoolService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.core.pojo.BaseCloudServerLineResponse;
import com.rxinyun.dev.core.pojo.BaseIndependentMachineResponse;
import com.rxinyun.dev.core.pojo.BaseProductSyncParam;
import com.rxinyun.production.api.ProductApiServiceApi;
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
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "API密钥控制器"
)
@RestController
@Validated
public class ApiKeyController {
   @Resource
   private ApiKeyService apiKeyService;
   @Resource
   private ResourcePoolService resourcePoolService;
   @Resource
   private ProductApiServiceApi productApiServiceApi;

   @Operation(
      summary = "获取API密钥分页"
   )
   @GetMapping({"/admin/dev/api/key/page"})
   public CommonResult<Page<ApiKey>> page(ApiKeyPageParam apiKeyPageParam) {
      return CommonResult.data(this.apiKeyService.page(apiKeyPageParam));
   }

   @Operation(
      summary = "添加API密钥"
   )
   @CommonLog("添加API密钥")
   @PostMapping({"/admin/dev/api/key/add"})
   public CommonResult<ApiKeyCreateResult> add(@RequestBody @Valid ApiKeyAddParam apiKeyAddParam) {
      ApiKeyCreateResult result = this.apiKeyService.add(apiKeyAddParam);
      return CommonResult.data(result);
   }

   @Operation(
      summary = "编辑API密钥"
   )
   @CommonLog("编辑API密钥")
   @PostMapping({"/admin/dev/api/key/edit"})
   public CommonResult<String> edit(@RequestBody @Valid ApiKeyEditParam apiKeyEditParam) {
      this.apiKeyService.edit(apiKeyEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除API密钥"
   )
   @CommonLog("删除API密钥")
   @PostMapping({"/admin/dev/api/key/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<ApiKeyIdParam> apiKeyIdParamList) {
      this.apiKeyService.delete(apiKeyIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取API密钥详情"
   )
   @GetMapping({"/admin/dev/api/key/detail"})
   public CommonResult<ApiKey> detail(@Valid ApiKeyIdParam apiKeyIdParam) {
      return CommonResult.data(this.apiKeyService.detail(apiKeyIdParam));
   }

   @Operation(
      summary = "启用/禁用API密钥"
   )
   @CommonLog("启用/禁用API密钥")
   @PostMapping({"/admin/dev/api/key/changeStatus"})
   public CommonResult<String> changeStatus(@RequestBody @Valid ApiKeyStatusParam apiKeyStatusParam) {
      this.apiKeyService.changeStatus(apiKeyStatusParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取资源池分页"
   )
   @GetMapping({"/admin/dev/api/resourcePool/page"})
   public CommonResult<Page<ResourcePool>> resourcePoolPage(ResourcePoolPageParam resourcePoolPageParam) {
      return CommonResult.data(this.resourcePoolService.page(resourcePoolPageParam));
   }

   @Operation(
      summary = "添加资源池"
   )
   @CommonLog("添加资源池")
   @PostMapping({"/admin/dev/api/resourcePool/add"})
   public CommonResult<String> resourcePoolAdd(@RequestBody @Valid ResourcePoolAddParam resourcePoolAddParam) {
      this.resourcePoolService.add(resourcePoolAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑资源池"
   )
   @CommonLog("编辑资源池")
   @PostMapping({"/admin/dev/api/resourcePool/edit"})
   public CommonResult<String> resourcePoolEdit(@RequestBody @Valid ResourcePoolEditParam resourcePoolEditParam) {
      this.resourcePoolService.edit(resourcePoolEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除资源池"
   )
   @CommonLog("删除资源池")
   @PostMapping({"/admin/dev/api/resourcePool/delete"})
   public CommonResult<String> resourcePoolDelete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<ResourcePoolIdParam> resourcePoolIdParamList) {
      this.resourcePoolService.delete(resourcePoolIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取资源池详情"
   )
   @GetMapping({"/admin/dev/api/resourcePool/detail"})
   public CommonResult<ResourcePool> resourcePoolDetail(@Valid ResourcePoolIdParam resourcePoolIdParam) {
      return CommonResult.data(this.resourcePoolService.detail(resourcePoolIdParam));
   }

   @Operation(
      summary = "启用/禁用资源池"
   )
   @CommonLog("启用/禁用资源池")
   @PostMapping({"/admin/dev/api/resourcePool/changeStatus"})
   public CommonResult<String> resourcePoolChangeStatus(@RequestBody @Valid ResourcePoolStatusParam resourcePoolStatusParam) {
      this.resourcePoolService.changeStatus(resourcePoolStatusParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "查询资源池产品信息（支持地域、可用区、线路、操作系统类别、操作系统版本等）"
   )
   @GetMapping({"/admin/dev/api/resourcePool/queryDetail"})
   public CommonResult<?> resourcePoolQueryDetail(@Valid ResourcePoolQueryParam resourcePoolQueryParam) {
      String resourcePoolId = resourcePoolQueryParam.getResourcePoolId();
      String productType = resourcePoolQueryParam.getProductType();
      String lineId = resourcePoolQueryParam.getLineId();
      if ("cloud-server-region".equals(productType)) {
         List<Object> regions = this.productApiServiceApi.queryCloudServerRegionsFromPool(resourcePoolId);
         return CommonResult.data(regions);
      } else if ("cloud-server-available-region".equals(productType)) {
         List<Object> availableRegions = this.productApiServiceApi.queryCloudServerAvailableRegionsFromPool(resourcePoolId);
         return CommonResult.data(availableRegions);
      } else if ("independent-server-region".equals(productType)) {
         List<Object> regions = this.productApiServiceApi.queryIndependentRegionsFromPool(resourcePoolId);
         return CommonResult.data(regions);
      } else if ("independent-server-line".equals(productType)) {
         List<Object> lines = this.productApiServiceApi.queryIndependentLinesFromPool(resourcePoolId);
         return CommonResult.data(lines);
      } else if ("cloud-server".equals(productType) || "cloud-server-line".equals(productType)) {
         List<BaseCloudServerLineResponse> lines = this.productApiServiceApi.queryCloudServerLinesFromPool(resourcePoolId);
         return CommonResult.data(lines);
      } else if ("independent-server".equals(productType) || "independent-server-machine".equals(productType)) {
         List<BaseIndependentMachineResponse> machines = this.productApiServiceApi.queryIndependentMachinesFromPool(resourcePoolId);
         return CommonResult.data(machines);
      } else if ("cloud-server-os-class".equals(productType)) {
         List<Object> osClasses = this.productApiServiceApi.queryCloudServerOsClassesFromPool(resourcePoolId);
         return CommonResult.data(osClasses);
      } else if ("cloud-server-os-version".equals(productType)) {
         List<Object> osVersions = this.productApiServiceApi.queryCloudServerOsVersionsFromPool(resourcePoolId, lineId);
         return CommonResult.data(osVersions);
      } else if ("independent-server-os-class".equals(productType)) {
         List<Object> osClasses = this.productApiServiceApi.queryIndependentOsClassesFromPool(resourcePoolId);
         return CommonResult.data(osClasses);
      } else if ("independent-server-os-version".equals(productType)) {
         List<Object> osVersions = this.productApiServiceApi.queryIndependentOsVersionsFromPool(resourcePoolId, lineId);
         return CommonResult.data(osVersions);
      } else {
         return CommonResult.error("不支持的产品类型：" + productType);
      }
   }

   @Operation(
      summary = "同步资源池产品到本地数据库（支持勾选数据）"
   )
   @CommonLog("同步资源池产品（勾选）")
   @PostMapping({"/admin/dev/api/resourcePool/syncSelected"})
   public CommonResult<String> resourcePoolSyncSelected(@RequestBody @Valid ResourcePoolSyncSelectedParam syncSelectedParam) {
      String resourcePoolId = syncSelectedParam.getResourcePoolId();
      String productType = syncSelectedParam.getProductType();
      List<Object> selectedProducts = syncSelectedParam.getSelectedProducts();
      BaseProductSyncParam syncParam = new BaseProductSyncParam();
      syncParam.setProducts(selectedProducts);
      syncParam.setResourcePoolId(resourcePoolId);

      try {
         if ("cloud-server-region".equals(productType)) {
            this.productApiServiceApi.syncCloudServerRegions(syncParam);
            return CommonResult.ok("云服务器地域同步成功，共同步 " + selectedProducts.size() + " 条数据");
         } else if ("cloud-server-available-region".equals(productType)) {
            this.productApiServiceApi.syncCloudServerAvailableRegions(syncParam);
            return CommonResult.ok("云服务器可用区同步成功，共同步 " + selectedProducts.size() + " 条数据");
         } else if ("independent-server-region".equals(productType)) {
            this.productApiServiceApi.syncIndependentRegions(syncParam);
            return CommonResult.ok("独立服务器地域同步成功，共同步 " + selectedProducts.size() + " 条数据");
         } else if ("independent-server-line".equals(productType)) {
            this.productApiServiceApi.syncIndependentLines(syncParam);
            return CommonResult.ok("独立服务器线路同步成功，共同步 " + selectedProducts.size() + " 条数据");
         } else if ("cloud-server".equals(productType) || "cloud-server-line".equals(productType)) {
            this.productApiServiceApi.syncCloudServerLines(syncParam);
            return CommonResult.ok("云服务器线路同步成功，共同步 " + selectedProducts.size() + " 条数据");
         } else if ("independent-server".equals(productType) || "independent-server-machine".equals(productType)) {
            this.productApiServiceApi.syncIndependentMachines(syncParam);
            return CommonResult.ok("独立服务器机型同步成功，共同步 " + selectedProducts.size() + " 条数据");
         } else if ("cloud-server-os-class".equals(productType)) {
            this.productApiServiceApi.syncCloudServerOsClasses(syncParam);
            return CommonResult.ok("云服务器操作系统类别同步成功，共同步 " + selectedProducts.size() + " 条数据");
         } else if ("cloud-server-os-version".equals(productType)) {
            this.productApiServiceApi.syncCloudServerOsVersions(syncParam);
            return CommonResult.ok("云服务器操作系统版本同步成功，共同步 " + selectedProducts.size() + " 条数据");
         } else if ("independent-server-os-class".equals(productType)) {
            this.productApiServiceApi.syncIndependentOsClasses(syncParam);
            return CommonResult.ok("独立服务器操作系统类别同步成功，共同步 " + selectedProducts.size() + " 条数据");
         } else if ("independent-server-os-version".equals(productType)) {
            this.productApiServiceApi.syncIndependentOsVersions(syncParam);
            return CommonResult.ok("独立服务器操作系统版本同步成功，共同步 " + selectedProducts.size() + " 条数据");
         } else {
            return CommonResult.error("不支持的产品类型：" + productType);
         }
      } catch (CommonException var7) {
         return CommonResult.error(var7.getMessage());
      }
   }
}
