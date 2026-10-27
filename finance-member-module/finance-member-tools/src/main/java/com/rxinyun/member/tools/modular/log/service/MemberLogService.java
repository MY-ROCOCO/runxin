package com.rxinyun.member.tools.modular.log.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.member.tools.modular.log.entity.MemberLog;
import com.rxinyun.member.tools.modular.log.param.MemberLogDeleteParam;
import com.rxinyun.member.tools.modular.log.param.MemberLogIdParam;
import com.rxinyun.member.tools.modular.log.param.MemberLogPageParam;
import com.rxinyun.member.tools.modular.log.result.MemberLogOpBarChartDataResult;
import com.rxinyun.member.tools.modular.log.result.MemberLogOpPieChartDataResult;
import com.rxinyun.member.tools.modular.log.result.MemberLogVisLineChartDataResult;
import com.rxinyun.member.tools.modular.log.result.MemberLogVisPieChartDataResult;
import java.util.List;

public interface MemberLogService extends IService<MemberLog> {
   Page<MemberLog> page(MemberLogPageParam devLogPageParam);

   Page<MemberLog> currentMemberPage(MemberLogPageParam memberLogPageParam);

   Page<MemberLog> currentMemberLoginPage(MemberLogPageParam memberLogPageParam);

   MemberLog detail(MemberLogIdParam devLogIdParam);

   MemberLog currentMemberDetail(MemberLogIdParam memberLogIdParam);

   void delete(MemberLogDeleteParam devLogDeleteParam);

   List<MemberLogVisLineChartDataResult> visLogLineChartData();

   List<MemberLogVisPieChartDataResult> visLogPieChartData();

   List<MemberLogOpBarChartDataResult> opLogBarChartData();

   List<MemberLogOpPieChartDataResult> opLogPieChartData();
}
