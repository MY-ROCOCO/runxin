package com.rxinyun.member.tools.modular.log.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.tools.modular.log.param.MemberCloudServerLogPageParam;
import com.rxinyun.member.tools.modular.log.param.MemberIndependentLogPageParam;
import com.rxinyun.member.tools.modular.log.result.MemberServerOperationLogResult;

public interface MemberServerOperationLogService {
   Page<MemberServerOperationLogResult> cloudPage(MemberCloudServerLogPageParam memberCloudServerLogPageParam);

   Page<MemberServerOperationLogResult> independentPage(MemberIndependentLogPageParam memberIndependentLogPageParam);
}
