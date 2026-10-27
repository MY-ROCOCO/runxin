package com.rxinyun.member.order.modular.ecs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.order.modular.ecs.entity.MemberServerRefundRecord;
import com.rxinyun.member.order.modular.ecs.param.MemberServerRefundRecordPageParam;

public interface MemberServerRefundRecordService {
   Page<MemberServerRefundRecord> page(MemberServerRefundRecordPageParam pageParam);
}
