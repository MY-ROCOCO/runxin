package com.rxinyun.member.business.modular.esc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.business.modular.esc.entity.MemberServerTransferRecord;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordPageParam;

public interface MemberServerTransferRecordService {
   CommonResult add(MemberServerTransferRecordAddParam serverTransferRecordAddParam);

   CommonResult updateStatus(MemberServerTransferRecordIdParam serverTransferRecordIdParam);

   Page<MemberServerTransferRecord> page(MemberServerTransferRecordPageParam pageParam);
}
