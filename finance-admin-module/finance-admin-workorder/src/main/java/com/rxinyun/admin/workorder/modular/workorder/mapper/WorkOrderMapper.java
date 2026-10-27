package com.rxinyun.admin.workorder.modular.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.workorder.modular.workorder.entity.WorkOrder;
import java.util.List;

public interface WorkOrderMapper extends BaseMapper<WorkOrder> {
   List<WorkOrder> selectByTypeId(String typeId);
}
