package com.rxinyun.member.workorder.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.workorder.pojo.dto.WorkOrderAddParam;
import com.rxinyun.member.workorder.pojo.dto.WorkOrderPageParam;
import com.rxinyun.member.workorder.pojo.vo.WorkOrderPageResponse;
import jakarta.validation.constraints.NotBlank;

public interface WorkOrderMemberService {
   CommonResult add(WorkOrderAddParam workOrderAddParam);

   Page<WorkOrderPageResponse> page(WorkOrderPageParam workOrderPageParam);

   CommonResult setStatusOut(@NotBlank String id);

   CommonResult getMessById(@NotBlank String id);
}
