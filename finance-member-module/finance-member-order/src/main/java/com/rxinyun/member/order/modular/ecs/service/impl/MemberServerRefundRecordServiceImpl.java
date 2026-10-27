package com.rxinyun.member.order.modular.ecs.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.order.modular.ecs.entity.MemberServerRefundRecord;
import com.rxinyun.member.order.modular.ecs.mapper.MemberServerRefundRecordMapper;
import com.rxinyun.member.order.modular.ecs.param.MemberServerRefundRecordPageParam;
import com.rxinyun.member.order.modular.ecs.service.MemberServerRefundRecordService;
import org.springframework.stereotype.Service;

@Service
public class MemberServerRefundRecordServiceImpl
   extends ServiceImpl<MemberServerRefundRecordMapper, MemberServerRefundRecord>
   implements MemberServerRefundRecordService {
   @Override
   public Page<MemberServerRefundRecord> page(MemberServerRefundRecordPageParam pageParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      QueryWrapper<MemberServerRefundRecord> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(MemberServerRefundRecord::getMemberNo, clientLoginUser.getId());
      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         queryWrapper.lambda().eq(MemberServerRefundRecord::getStatus, pageParam.getStatus());
      }

      if (StrUtil.isNotBlank(pageParam.getSearchKey())) {
         queryWrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<MemberServerRefundRecord>)((LambdaQueryWrapper<MemberServerRefundRecord>)((LambdaQueryWrapper<MemberServerRefundRecord>)((LambdaQueryWrapper<MemberServerRefundRecord>)q.like(
                              MemberServerRefundRecord::getServerId, pageParam.getSearchKey()
                           ))
                           .or())
                        .like(MemberServerRefundRecord::getServerName, pageParam.getSearchKey()))
                     .or())
                  .like(MemberServerRefundRecord::getPublicIp, pageParam.getSearchKey())
            );
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(MemberServerRefundRecord::getCreateTime);
      }

      return (Page<MemberServerRefundRecord>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }
}
