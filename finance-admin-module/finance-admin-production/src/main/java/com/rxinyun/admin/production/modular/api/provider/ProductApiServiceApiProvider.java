package com.rxinyun.admin.production.modular.api.provider;

import cn.hutool.core.bean.BeanUtil;
import com.rxinyun.admin.production.modular.api.param.ProductSyncParam;
import com.rxinyun.admin.production.modular.api.response.CloudServerLineResponse;
import com.rxinyun.admin.production.modular.api.response.IndependentMachineResponse;
import com.rxinyun.admin.production.modular.api.service.ProductApiService;
import com.rxinyun.dev.core.pojo.BaseCloudServerLineResponse;
import com.rxinyun.dev.core.pojo.BaseCloudServerLineSkuResponse;
import com.rxinyun.dev.core.pojo.BaseIndependentMachineResponse;
import com.rxinyun.dev.core.pojo.BaseProductSyncParam;
import com.rxinyun.production.api.ProductApiServiceApi;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductApiServiceApiProvider implements ProductApiServiceApi {
   @Resource
   private ProductApiService productApiService;

   public List<BaseCloudServerLineResponse> queryCloudServerLinesFromPool(String resourcePoolId) {
      List<CloudServerLineResponse> lines = this.productApiService.queryCloudServerLinesFromPool(resourcePoolId);
      return lines.stream()
         .map(
            line -> {
               BaseCloudServerLineResponse baseLine = (BaseCloudServerLineResponse)BeanUtil.toBean(line, BaseCloudServerLineResponse.class);
               if (line.getSkuList() != null && !line.getSkuList().isEmpty()) {
                  List<BaseCloudServerLineSkuResponse> baseSkuList = line.getSkuList()
                     .stream()
                     .map(sku -> (BaseCloudServerLineSkuResponse)BeanUtil.toBean(sku, BaseCloudServerLineSkuResponse.class))
                     .collect(Collectors.toList());
                  baseLine.setSkuList(baseSkuList);
               }

               return baseLine;
            }
         )
         .collect(Collectors.toList());
   }

   public List<BaseIndependentMachineResponse> queryIndependentMachinesFromPool(String resourcePoolId) {
      List<IndependentMachineResponse> machines = this.productApiService.queryIndependentMachinesFromPool(resourcePoolId);
      return machines.stream()
         .map(machine -> (BaseIndependentMachineResponse)BeanUtil.toBean(machine, BaseIndependentMachineResponse.class))
         .collect(Collectors.toList());
   }

   public void syncCloudServerLines(BaseProductSyncParam syncParam) {
      ProductSyncParam param = new ProductSyncParam();
      param.setProducts(syncParam.getProducts());
      param.setResourcePoolId(syncParam.getResourcePoolId());
      this.productApiService.syncCloudServerLines(param);
   }

   public void syncIndependentMachines(BaseProductSyncParam syncParam) {
      ProductSyncParam param = new ProductSyncParam();
      param.setProducts(syncParam.getProducts());
      param.setResourcePoolId(syncParam.getResourcePoolId());
      this.productApiService.syncIndependentMachines(param);
   }

   public List<Object> queryCloudServerRegionsFromPool(String resourcePoolId) {
      return this.productApiService.queryCloudServerRegionsFromPool(resourcePoolId);
   }

   public List<Object> queryCloudServerAvailableRegionsFromPool(String resourcePoolId) {
      return this.productApiService.queryCloudServerAvailableRegionsFromPool(resourcePoolId);
   }

   public List<Object> queryIndependentRegionsFromPool(String resourcePoolId) {
      return this.productApiService.queryIndependentRegionsFromPool(resourcePoolId);
   }

   public List<Object> queryIndependentLinesFromPool(String resourcePoolId) {
      return this.productApiService.queryIndependentLinesFromPool(resourcePoolId);
   }

   public void syncCloudServerRegions(BaseProductSyncParam syncParam) {
      ProductSyncParam param = new ProductSyncParam();
      param.setProducts(syncParam.getProducts());
      param.setResourcePoolId(syncParam.getResourcePoolId());
      this.productApiService.syncCloudServerRegions(param);
   }

   public void syncCloudServerAvailableRegions(BaseProductSyncParam syncParam) {
      ProductSyncParam param = new ProductSyncParam();
      param.setProducts(syncParam.getProducts());
      param.setResourcePoolId(syncParam.getResourcePoolId());
      this.productApiService.syncCloudServerAvailableRegions(param);
   }

   public void syncIndependentRegions(BaseProductSyncParam syncParam) {
      ProductSyncParam param = new ProductSyncParam();
      param.setProducts(syncParam.getProducts());
      param.setResourcePoolId(syncParam.getResourcePoolId());
      this.productApiService.syncIndependentRegions(param);
   }

   public void syncIndependentLines(BaseProductSyncParam syncParam) {
      ProductSyncParam param = new ProductSyncParam();
      param.setProducts(syncParam.getProducts());
      param.setResourcePoolId(syncParam.getResourcePoolId());
      this.productApiService.syncIndependentLines(param);
   }

   public List<Object> queryCloudServerOsVersionsFromPool(String resourcePoolId, String lineId) {
      return this.productApiService.queryCloudServerOsVersionsFromPool(resourcePoolId, lineId);
   }

   public List<Object> queryIndependentOsVersionsFromPool(String resourcePoolId, String lineId) {
      return this.productApiService.queryIndependentOsVersionsFromPool(resourcePoolId, lineId);
   }

   public void syncCloudServerOsVersions(BaseProductSyncParam syncParam) {
      ProductSyncParam param = new ProductSyncParam();
      param.setProducts(syncParam.getProducts());
      param.setResourcePoolId(syncParam.getResourcePoolId());
      this.productApiService.syncCloudServerOsVersions(param);
   }

   public void syncIndependentOsVersions(BaseProductSyncParam syncParam) {
      ProductSyncParam param = new ProductSyncParam();
      param.setProducts(syncParam.getProducts());
      param.setResourcePoolId(syncParam.getResourcePoolId());
      this.productApiService.syncIndependentOsVersions(param);
   }

   public List<Object> queryCloudServerOsClassesFromPool(String resourcePoolId) {
      return this.productApiService.queryCloudServerOsClassesFromPool(resourcePoolId);
   }

   public List<Object> queryIndependentOsClassesFromPool(String resourcePoolId) {
      return this.productApiService.queryIndependentOsClassesFromPool(resourcePoolId);
   }

   public void syncCloudServerOsClasses(BaseProductSyncParam syncParam) {
      ProductSyncParam param = new ProductSyncParam();
      param.setProducts(syncParam.getProducts());
      param.setResourcePoolId(syncParam.getResourcePoolId());
      this.productApiService.syncCloudServerOsClasses(param);
   }

   public void syncIndependentOsClasses(BaseProductSyncParam syncParam) {
      ProductSyncParam param = new ProductSyncParam();
      param.setProducts(syncParam.getProducts());
      param.setResourcePoolId(syncParam.getResourcePoolId());
      this.productApiService.syncIndependentOsClasses(param);
   }
}
