package com.rxinyun.member.tools.modular.log.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.dev.api.DevLogApi;
import com.rxinyun.dev.core.pojo.BaseIndependentLogPageParam;
import com.rxinyun.dev.core.pojo.BaseServerLogPageParam;
import com.rxinyun.dev.core.pojo.BaseServerOperationLogResult;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.tools.modular.log.param.MemberCloudServerLogPageParam;
import com.rxinyun.member.tools.modular.log.param.MemberIndependentLogPageParam;
import com.rxinyun.member.tools.modular.log.result.MemberServerOperationLogResult;
import com.rxinyun.member.tools.modular.log.service.MemberServerOperationLogService;
import jakarta.annotation.Resource;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MemberServerOperationLogServiceImpl implements MemberServerOperationLogService {
   @Resource
   private DevLogApi devLogApi;

   @Override
   public Page<MemberServerOperationLogResult> cloudPage(MemberCloudServerLogPageParam memberCloudServerLogPageParam) {
      BaseServerLogPageParam apiParam = new BaseServerLogPageParam();
      BeanUtil.copyProperties(memberCloudServerLogPageParam, apiParam, new String[0]);
      apiParam.setMemberNo(this.currentMemberNo());
      return this.convertPage(this.devLogApi.cloudServerLogPage(apiParam));
   }

   @Override
   public Page<MemberServerOperationLogResult> independentPage(MemberIndependentLogPageParam memberIndependentLogPageParam) {
      BaseIndependentLogPageParam apiParam = new BaseIndependentLogPageParam();
      BeanUtil.copyProperties(memberIndependentLogPageParam, apiParam, new String[0]);
      apiParam.setMemberNo(this.currentMemberNo());
      return this.convertPage(this.devLogApi.independentServerLogPage(apiParam));
   }

   private Page<MemberServerOperationLogResult> convertPage(Page<BaseServerOperationLogResult> sourcePage) {
      Page<MemberServerOperationLogResult> page = new Page(sourcePage.getCurrent(), sourcePage.getSize(), sourcePage.getTotal());
      page.setRecords(
         sourcePage.getRecords()
            .stream()
            .map(record -> (MemberServerOperationLogResult)BeanUtil.toBean(record, MemberServerOperationLogResult.class))
            .collect(Collectors.toList())
      );
      return page;
   }

   private Long currentMemberNo() {
      return StpClientLoginUserUtil.getClientLoginUser().getId();
   }
}
