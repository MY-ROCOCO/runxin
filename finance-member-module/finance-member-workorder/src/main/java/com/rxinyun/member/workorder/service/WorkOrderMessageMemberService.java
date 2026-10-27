package com.rxinyun.member.workorder.service;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.workorder.pojo.dto.WorkOrderMessageAddParam;
import com.rxinyun.member.workorder.pojo.vo.WorkOrderMessageVO;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public interface WorkOrderMessageMemberService {
   CommonResult add(WorkOrderMessageAddParam workOrderMessageAddParam);

   List<WorkOrderMessageVO> findMessage(@NotBlank String workOrderId);
}
