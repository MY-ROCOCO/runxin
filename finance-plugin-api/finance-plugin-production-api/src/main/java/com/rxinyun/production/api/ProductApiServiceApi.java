package com.rxinyun.production.api;

import com.rxinyun.dev.core.pojo.BaseCloudServerLineResponse;
import com.rxinyun.dev.core.pojo.BaseIndependentMachineResponse;
import com.rxinyun.dev.core.pojo.BaseProductSyncParam;
import java.util.List;

public interface ProductApiServiceApi {
   List<BaseCloudServerLineResponse> queryCloudServerLinesFromPool(String resourcePoolId);

   List<BaseIndependentMachineResponse> queryIndependentMachinesFromPool(String resourcePoolId);

   List<Object> queryCloudServerRegionsFromPool(String resourcePoolId);

   List<Object> queryCloudServerAvailableRegionsFromPool(String resourcePoolId);

   List<Object> queryIndependentRegionsFromPool(String resourcePoolId);

   List<Object> queryIndependentLinesFromPool(String resourcePoolId);

   void syncCloudServerLines(BaseProductSyncParam syncParam);

   void syncIndependentMachines(BaseProductSyncParam syncParam);

   void syncCloudServerRegions(BaseProductSyncParam syncParam);

   void syncCloudServerAvailableRegions(BaseProductSyncParam syncParam);

   void syncIndependentRegions(BaseProductSyncParam syncParam);

   void syncIndependentLines(BaseProductSyncParam syncParam);

   List<Object> queryCloudServerOsVersionsFromPool(String resourcePoolId, String lineId);

   List<Object> queryIndependentOsVersionsFromPool(String resourcePoolId, String lineId);

   void syncCloudServerOsVersions(BaseProductSyncParam syncParam);

   void syncIndependentOsVersions(BaseProductSyncParam syncParam);

   List<Object> queryCloudServerOsClassesFromPool(String resourcePoolId);

   List<Object> queryIndependentOsClassesFromPool(String resourcePoolId);

   void syncCloudServerOsClasses(BaseProductSyncParam syncParam);

   void syncIndependentOsClasses(BaseProductSyncParam syncParam);
}
