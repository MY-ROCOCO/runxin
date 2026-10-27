package com.rxinyun.member.workorder.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.workorder.mapper.WorkOrderTypeMemberMapper;
import com.rxinyun.member.workorder.pojo.entity.WorkOrderType;
import com.rxinyun.member.workorder.pojo.vo.WorkOrderTypeList;
import com.rxinyun.member.workorder.service.WorkOrderTypeMemberService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderTypeMemberServiceImpl extends ServiceImpl<WorkOrderTypeMemberMapper, WorkOrderType> implements WorkOrderTypeMemberService {
   @Resource
   WorkOrderTypeMemberMapper workOrderTypeMemberMapper;

   @Override
   public CommonResult findList() {
      List<WorkOrderType> list = (this.lambdaQuery().eq(WorkOrderType::getStatus, 1)).list();
      List<WorkOrderTypeList> lists = BeanUtil.copyToList(list, WorkOrderTypeList.class);
      return CommonResult.data(lists);
   }
}
