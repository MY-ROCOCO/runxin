package com.rxinyun.admin.order.modular.ecs.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.order.modular.ecs.entity.ServerRefundRecord;
import com.rxinyun.admin.order.modular.ecs.mapper.ServerRefundRecordMapper;
import com.rxinyun.admin.order.modular.ecs.param.ServerRefundParam;
import com.rxinyun.admin.order.modular.ecs.param.ServerRefundRecordIdParam;
import com.rxinyun.admin.order.modular.ecs.param.ServerRefundRecordPageParam;
import com.rxinyun.admin.order.modular.ecs.service.ServerRefundRecordService;
import com.rxinyun.admin.order.modular.ecs.service.ServerRefundService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.order.ServerRefundModeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServerRefundRecordServiceImpl extends ServiceImpl<ServerRefundRecordMapper, ServerRefundRecord> implements ServerRefundRecordService {
   @Resource
   private ServerRefundService serverRefundService;

   @Override
   public Page<ServerRefundRecord> page(ServerRefundRecordPageParam pageParam) {
      QueryWrapper<ServerRefundRecord> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getMemberNo())) {
         queryWrapper.lambda().eq(ServerRefundRecord::getMemberNo, pageParam.getMemberNo());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getProductType())) {
         queryWrapper.lambda().eq(ServerRefundRecord::getProductType, pageParam.getProductType());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         queryWrapper.lambda().eq(ServerRefundRecord::getStatus, pageParam.getStatus());
      }

      if (StrUtil.isNotBlank(pageParam.getSearchKey())) {
         queryWrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<ServerRefundRecord>)((LambdaQueryWrapper<ServerRefundRecord>)((LambdaQueryWrapper<ServerRefundRecord>)((LambdaQueryWrapper<ServerRefundRecord>)q.like(
                              ServerRefundRecord::getServerId, pageParam.getSearchKey()
                           ))
                           .or())
                        .like(ServerRefundRecord::getServerName, pageParam.getSearchKey()))
                     .or())
                  .like(ServerRefundRecord::getPublicIp, pageParam.getSearchKey())
            );
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(ServerRefundRecord::getCreateTime);
      }

      return (Page<ServerRefundRecord>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<String> approve(ServerRefundRecordIdParam param) {
      ServerRefundRecord record = (ServerRefundRecord)this.getById(param.getId());
      if (record == null) {
         return CommonResult.error("退款记录不存在");
      } else if (!Integer.valueOf(0).equals(record.getStatus())) {
         return CommonResult.error("退款记录已处理，请勿重复操作");
      } else if (Integer.valueOf(-1).equals(param.getStatus())) {
         record.setStatus(-1);
         record.setUpdateTime(new Date());
         boolean updated = this.updateById(record);
         if (!updated) {
            throw new CommonException("拒绝退款成功，但更新退款记录状态失败", new Object[0]);
         } else {
            return CommonResult.data("已拒绝退款申请");
         }
      } else if (!Integer.valueOf(1).equals(param.getStatus())) {
         return CommonResult.error("不支持的审核状态");
      } else {
         ServerRefundParam refundParam = new ServerRefundParam();
         refundParam.setServerId(record.getServerId());
         refundParam.setProductType(record.getProductType());
         refundParam.setRefundMode(ServerRefundModeEnum.PRORATED.name());
         refundParam.setMemberNo(record.getMemberNo());
         CommonResult<String> executeResult = this.serverRefundService.execute(refundParam);
         if (executeResult.getCode() != 200) {
            return executeResult;
         } else {
            record.setStatus(1);
            record.setUpdateTime(new Date());
            boolean updated = this.updateById(record);
            if (!updated) {
               throw new CommonException("退款成功，但更新退款记录状态失败", new Object[0]);
            } else {
               return CommonResult.data((String)executeResult.getData());
            }
         }
      }
   }
}
