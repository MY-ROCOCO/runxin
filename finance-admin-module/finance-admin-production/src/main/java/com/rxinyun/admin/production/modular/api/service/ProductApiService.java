package com.rxinyun.admin.production.modular.api.service;

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
import java.util.List;

public interface ProductApiService {
   List<CloudServerLineResponse> getCloudServerLines();

   List<IndependentMachineResponse> getIndependentMachines();

   List<CloudServerRegionResponse> getCloudServerRegions();

   List<CloudServerAvailableRegionResponse> getCloudServerAvailableRegions();

   List<IndependentRegionResponse> getIndependentRegions();

   List<IndependentLineResponse> getIndependentLines();

   List<CloudServerOsClassResponse> getCloudServerOsClasses();

   List<CloudServerOsVersionResponse> getCloudServerOsVersions(String lineId);

   List<IndependentOsClassResponse> getIndependentOsClasses();

   List<IndependentOsVersionResponse> getIndependentOsVersions(String lineId);

   void syncCloudServerLines(ProductSyncParam syncParam);

   void syncIndependentMachines(ProductSyncParam syncParam);

   List<CloudServerLineResponse> queryCloudServerLinesFromPool(String resourcePoolId);

   List<IndependentMachineResponse> queryIndependentMachinesFromPool(String resourcePoolId);

   List<Object> queryCloudServerRegionsFromPool(String resourcePoolId);

   List<Object> queryCloudServerAvailableRegionsFromPool(String resourcePoolId);

   List<Object> queryIndependentRegionsFromPool(String resourcePoolId);

   List<Object> queryIndependentLinesFromPool(String resourcePoolId);

   void syncCloudServerRegions(ProductSyncParam syncParam);

   void syncCloudServerAvailableRegions(ProductSyncParam syncParam);

   void syncIndependentRegions(ProductSyncParam syncParam);

   void syncIndependentLines(ProductSyncParam syncParam);

   List<Object> queryCloudServerOsVersionsFromPool(String resourcePoolId, String lineId);

   List<Object> queryIndependentOsVersionsFromPool(String resourcePoolId, String lineId);

   void syncCloudServerOsVersions(ProductSyncParam syncParam);

   void syncIndependentOsVersions(ProductSyncParam syncParam);

   List<Object> queryCloudServerOsClassesFromPool(String resourcePoolId);

   List<Object> queryIndependentOsClassesFromPool(String resourcePoolId);

   void syncCloudServerOsClasses(ProductSyncParam syncParam);

   void syncIndependentOsClasses(ProductSyncParam syncParam);
}
