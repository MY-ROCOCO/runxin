package com.rxinyun.admin.order.modular.api.adapter;

import com.rxinyun.admin.production.modular.api.response.CloudServerLineResponse;
import com.rxinyun.admin.production.modular.api.response.IndependentMachineResponse;
import com.rxinyun.dev.core.pojo.BaseResourcePool;
import java.util.List;

public interface ResourcePoolAdapter {
   List<CloudServerLineResponse> queryCloudServerLines(BaseResourcePool resourcePool);

   List<IndependentMachineResponse> queryIndependentMachines(BaseResourcePool resourcePool);
}
