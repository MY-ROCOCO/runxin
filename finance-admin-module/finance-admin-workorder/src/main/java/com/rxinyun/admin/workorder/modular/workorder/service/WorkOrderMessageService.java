package com.rxinyun.admin.workorder.modular.workorder.service;

import com.rxinyun.admin.workorder.modular.workorder.entity.param.WorkOrderMessageAddParam;
import com.rxinyun.admin.workorder.modular.workorder.entity.vo.WorkOrderMessageVO;
import com.rxinyun.common.pojo.CommonResult;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public interface WorkOrderMessageService {
   CommonResult add(WorkOrderMessageAddParam workOrderMessageAddParam);

   List<WorkOrderMessageVO> getMessage(@NotBlank String workOrderId);
}
