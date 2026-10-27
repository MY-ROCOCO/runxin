package com.rxinyun.admin.workorder.modular.workorder.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.workorder.modular.workorder.entity.WorkOrder;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.WorkOrderPageParam;
import com.rxinyun.admin.workorder.modular.workorder.response.WorkOrderFindResponse;
import com.rxinyun.admin.workorder.modular.workorder.response.WorkOrderPageResponse;
import com.rxinyun.common.pojo.CommonResult;
import java.util.List;

public interface WorkOrderService {
   CommonResult<WorkOrderFindResponse> findMessage(String orderId);

   CommonResult setStatusOut(String orderId);

   Page<WorkOrderPageResponse> page(WorkOrderPageParam workOrderPageParam);

   List<WorkOrder> getOrderIsExist(String typeId);
}
