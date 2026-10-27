package com.rxinyun.admin.dev.modular.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.api.entity.ResourcePoolRequest;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolRequestPageParam;
import java.util.List;

public interface ResourcePoolRequestService {
   void create(ResourcePoolRequest resourcePoolRequest);

   ResourcePoolRequest getByOrderNo(String orderNo);

   List<ResourcePoolRequest> listPendingRequests();

   void updateStatus(String id, Integer status, String callbackData);

   ResourcePoolRequest getById(String id);

   Page<ResourcePoolRequest> page(ResourcePoolRequestPageParam param);
}
