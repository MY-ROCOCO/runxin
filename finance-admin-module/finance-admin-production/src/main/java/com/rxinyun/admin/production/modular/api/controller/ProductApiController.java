package com.rxinyun.admin.production.modular.api.controller;

import com.rxinyun.admin.production.modular.api.param.ProductSyncParam;
import com.rxinyun.admin.production.modular.api.response.CloudServerAvailableRegionResponse;
import com.rxinyun.admin.production.modular.api.response.CloudServerLineResponse;
import com.rxinyun.admin.production.modular.api.response.CloudServerOsClassResponse;
import com.rxinyun.admin.production.modular.api.response.CloudServerOsVersionResponse;
import com.rxinyun.admin.production.modular.api.response.CloudServerRegionResponse;
import com.rxinyun.admin.production.modular.api.response.IndependentLineResponse;
import com.rxinyun.admin.production.modular.api.response.IndependentMachineResponse;
import com.rxinyun.admin.production.modular.api.response.IndependentOsClassResponse;
import com.rxinyun.admin.production.modular.api.response.IndependentOsVersionResponse;
import com.rxinyun.admin.production.modular.api.response.IndependentRegionResponse;
import com.rxinyun.admin.production.modular.api.service.ProductApiService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "产品信息查询API"
)
@RestController
@RequestMapping({"/api/product"})
@Validated
public class ProductApiController {
   private static final Logger log = LoggerFactory.getLogger(ProductApiController.class);
   @Resource
   private ProductApiService productApiService;

   @Operation(
      summary = "查询云服务器线路列表"
   )
   @GetMapping({"/cloud-server/lines"})
   public CommonResult<List<CloudServerLineResponse>> getCloudServerLines() {
      List<CloudServerLineResponse> lines = this.productApiService.getCloudServerLines();
      return CommonResult.data(lines);
   }

   @Operation(
      summary = "查询独立服务器机型列表"
   )
   @GetMapping({"/independent-server/machines"})
   public CommonResult<List<IndependentMachineResponse>> getIndependentMachines() {
      List<IndependentMachineResponse> machines = this.productApiService.getIndependentMachines();
      return CommonResult.data(machines);
   }

   @Operation(
      summary = "同步云服务器线路"
   )
   @PostMapping({"/cloud-server/lines/sync"})
   public CommonResult<String> syncCloudServerLines(@Valid @RequestBody ProductSyncParam syncParam) {
      this.productApiService.syncCloudServerLines(syncParam);
      return CommonResult.ok("同步成功");
   }

   @Operation(
      summary = "同步独立服务器机型"
   )
   @PostMapping({"/independent-server/machines/sync"})
   public CommonResult<String> syncIndependentMachines(@Valid @RequestBody ProductSyncParam syncParam) {
      this.productApiService.syncIndependentMachines(syncParam);
      return CommonResult.ok("同步成功");
   }

   @Operation(
      summary = "从资源池查询产品信息"
   )
   @GetMapping({"/cloud-server/lines/queryFromPool"})
   public CommonResult<List<CloudServerLineResponse>> queryCloudServerLinesFromPool(@RequestParam("resourcePoolId") String resourcePoolId) {
      List<CloudServerLineResponse> lines = this.productApiService.queryCloudServerLinesFromPool(resourcePoolId);
      return CommonResult.data(lines);
   }

   @Operation(
      summary = "从资源池查询独立服务器机型"
   )
   @GetMapping({"/independent-server/machines/queryFromPool"})
   public CommonResult<List<IndependentMachineResponse>> queryIndependentMachinesFromPool(@RequestParam("resourcePoolId") String resourcePoolId) {
      List<IndependentMachineResponse> machines = this.productApiService.queryIndependentMachinesFromPool(resourcePoolId);
      return CommonResult.data(machines);
   }

   @Operation(
      summary = "查询云服务器地域列表"
   )
   @GetMapping({"/cloud-server/regions"})
   public CommonResult<List<CloudServerRegionResponse>> getCloudServerRegions() {
      List<CloudServerRegionResponse> regions = this.productApiService.getCloudServerRegions();
      return CommonResult.data(regions);
   }

   @Operation(
      summary = "查询云服务器可用区列表"
   )
   @GetMapping({"/cloud-server/available-regions"})
   public CommonResult<List<CloudServerAvailableRegionResponse>> getCloudServerAvailableRegions() {
      List<CloudServerAvailableRegionResponse> availableRegions = this.productApiService.getCloudServerAvailableRegions();
      return CommonResult.data(availableRegions);
   }

   @Operation(
      summary = "查询独立服务器地域列表"
   )
   @GetMapping({"/independent-server/regions"})
   public CommonResult<List<IndependentRegionResponse>> getIndependentRegions() {
      List<IndependentRegionResponse> regions = this.productApiService.getIndependentRegions();
      return CommonResult.data(regions);
   }

   @Operation(
      summary = "查询独立服务器线路列表"
   )
   @GetMapping({"/independent-server/lines"})
   public CommonResult<List<IndependentLineResponse>> getIndependentLines() {
      List<IndependentLineResponse> lines = this.productApiService.getIndependentLines();
      return CommonResult.data(lines);
   }

   @Operation(
      summary = "查询云服务器操作系统类别列表"
   )
   @GetMapping({"/cloud-server/os-classes"})
   public CommonResult<List<CloudServerOsClassResponse>> getCloudServerOsClasses() {
      List<CloudServerOsClassResponse> osClasses = this.productApiService.getCloudServerOsClasses();
      return CommonResult.data(osClasses);
   }

   @Operation(
      summary = "查询云服务器操作系统版本列表"
   )
   @GetMapping({"/cloud-server/os-versions"})
   public CommonResult<List<CloudServerOsVersionResponse>> getCloudServerOsVersions(@RequestParam(value = "lineId",required = false) String lineId) {
      List<CloudServerOsVersionResponse> osVersions = this.productApiService.getCloudServerOsVersions(lineId);
      return CommonResult.data(osVersions);
   }

   @Operation(
      summary = "查询独立服务器操作系统类别列表"
   )
   @GetMapping({"/independent-server/os-classes"})
   public CommonResult<List<IndependentOsClassResponse>> getIndependentOsClasses() {
      List<IndependentOsClassResponse> osClasses = this.productApiService.getIndependentOsClasses();
      return CommonResult.data(osClasses);
   }

   @Operation(
      summary = "查询独立服务器操作系统版本列表"
   )
   @GetMapping({"/independent-server/os-versions"})
   public CommonResult<List<IndependentOsVersionResponse>> getIndependentOsVersions(@RequestParam(value = "lineId",required = false) String lineId) {
      List<IndependentOsVersionResponse> osVersions = this.productApiService.getIndependentOsVersions(lineId);
      return CommonResult.data(osVersions);
   }

   @Operation(
      summary = "从资源池查询云服务器操作系统版本"
   )
   @GetMapping({"/cloud-server/os-versions/queryFromPool"})
   public CommonResult<List<Object>> queryCloudServerOsVersionsFromPool(
      @RequestParam("resourcePoolId") String resourcePoolId, @RequestParam(value = "lineId",required = false) String lineId
   ) {
      List<Object> osVersions = this.productApiService.queryCloudServerOsVersionsFromPool(resourcePoolId, lineId);
      return CommonResult.data(osVersions);
   }

   @Operation(
      summary = "从资源池查询独立服务器操作系统版本"
   )
   @GetMapping({"/independent-server/os-versions/queryFromPool"})
   public CommonResult<List<Object>> queryIndependentOsVersionsFromPool(
      @RequestParam("resourcePoolId") String resourcePoolId, @RequestParam(value = "lineId",required = false) String lineId
   ) {
      List<Object> osVersions = this.productApiService.queryIndependentOsVersionsFromPool(resourcePoolId, lineId);
      return CommonResult.data(osVersions);
   }

   @Operation(
      summary = "同步云服务器操作系统版本"
   )
   @PostMapping({"/cloud-server/os-versions/sync"})
   public CommonResult<String> syncCloudServerOsVersions(@Valid @RequestBody ProductSyncParam syncParam) {
      this.productApiService.syncCloudServerOsVersions(syncParam);
      return CommonResult.ok("同步成功");
   }

   @Operation(
      summary = "同步独立服务器操作系统版本"
   )
   @PostMapping({"/independent-server/os-versions/sync"})
   public CommonResult<String> syncIndependentOsVersions(@Valid @RequestBody ProductSyncParam syncParam) {
      this.productApiService.syncIndependentOsVersions(syncParam);
      return CommonResult.ok("同步成功");
   }

   @Operation(
      summary = "从资源池查询云服务器操作系统类别"
   )
   @GetMapping({"/cloud-server/os-classes/queryFromPool"})
   public CommonResult<List<Object>> queryCloudServerOsClassesFromPool(@RequestParam("resourcePoolId") String resourcePoolId) {
      List<Object> osClasses = this.productApiService.queryCloudServerOsClassesFromPool(resourcePoolId);
      return CommonResult.data(osClasses);
   }

   @Operation(
      summary = "从资源池查询独立服务器操作系统类别"
   )
   @GetMapping({"/independent-server/os-classes/queryFromPool"})
   public CommonResult<List<Object>> queryIndependentOsClassesFromPool(@RequestParam("resourcePoolId") String resourcePoolId) {
      List<Object> osClasses = this.productApiService.queryIndependentOsClassesFromPool(resourcePoolId);
      return CommonResult.data(osClasses);
   }

   @Operation(
      summary = "同步云服务器操作系统类别"
   )
   @PostMapping({"/cloud-server/os-classes/sync"})
   public CommonResult<String> syncCloudServerOsClasses(@Valid @RequestBody ProductSyncParam syncParam) {
      this.productApiService.syncCloudServerOsClasses(syncParam);
      return CommonResult.ok("同步成功");
   }

   @Operation(
      summary = "同步独立服务器操作系统类别"
   )
   @PostMapping({"/independent-server/os-classes/sync"})
   public CommonResult<String> syncIndependentOsClasses(@Valid @RequestBody ProductSyncParam syncParam) {
      this.productApiService.syncIndependentOsClasses(syncParam);
      return CommonResult.ok("同步成功");
   }
}
