package com.rxinyun.member.tools.modular.log.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.tools.modular.log.entity.MemberLog;
import com.rxinyun.member.tools.modular.log.enums.MemberLogCategoryEnum;
import com.rxinyun.member.tools.modular.log.mapper.MemberLogMapper;
import com.rxinyun.member.tools.modular.log.param.MemberLogDeleteParam;
import com.rxinyun.member.tools.modular.log.param.MemberLogIdParam;
import com.rxinyun.member.tools.modular.log.param.MemberLogPageParam;
import com.rxinyun.member.tools.modular.log.result.MemberLogOpBarChartDataResult;
import com.rxinyun.member.tools.modular.log.result.MemberLogOpPieChartDataResult;
import com.rxinyun.member.tools.modular.log.result.MemberLogVisLineChartDataResult;
import com.rxinyun.member.tools.modular.log.result.MemberLogVisPieChartDataResult;
import com.rxinyun.member.tools.modular.log.service.MemberLogService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MemberLogServiceImpl extends ServiceImpl<MemberLogMapper, MemberLog> implements MemberLogService {
   @Override
   public Page<MemberLog> page(MemberLogPageParam devLogPageParam) {
      QueryWrapper<MemberLog> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.select(
         MemberLog.class,
         info -> !info.getColumn().equalsIgnoreCase("param_json")
            && !info.getColumn().equalsIgnoreCase("result_json")
            && !info.getColumn().equalsIgnoreCase("exe_message")
            && !info.getColumn().equalsIgnoreCase("sign_data")
      );
      if (ObjectUtil.isNotEmpty(devLogPageParam.getCategory())) {
         queryWrapper.lambda().eq(MemberLog::getCategory, devLogPageParam.getCategory());
      }

      if (ObjectUtil.isNotEmpty(devLogPageParam.getSearchKey())) {
         queryWrapper.lambda().like(MemberLog::getName, devLogPageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{devLogPageParam.getSortField(), devLogPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(devLogPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, devLogPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(devLogPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(MemberLog::getCreateTime);
      }

      return (Page<MemberLog>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public Page<MemberLog> currentMemberPage(MemberLogPageParam memberLogPageParam) {
      QueryWrapper<MemberLog> queryWrapper = this.buildCurrentMemberQueryWrapper(memberLogPageParam);
      return (Page<MemberLog>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public Page<MemberLog> currentMemberLoginPage(MemberLogPageParam memberLogPageParam) {
      QueryWrapper<MemberLog> queryWrapper = this.buildCurrentMemberQueryWrapper(memberLogPageParam);
      queryWrapper.lambda().in(MemberLog::getCategory, new Object[]{MemberLogCategoryEnum.LOGIN.getValue(), MemberLogCategoryEnum.LOGOUT.getValue()});
      return (Page<MemberLog>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public MemberLog detail(MemberLogIdParam devLogIdParam) {
      MemberLog devLog = (MemberLog)this.getById(devLogIdParam.getId());
      if (ObjectUtil.isEmpty(devLog)) {
         throw new CommonException("该日志不存在，id值为：{}", new Object[]{devLog.getId()});
      } else {
         return devLog;
      }
   }

   @Override
   public MemberLog currentMemberDetail(MemberLogIdParam memberLogIdParam) {
      String currentMemberId = String.valueOf(StpClientLoginUserUtil.getClientLoginUser().getId());
      MemberLog memberLog = (MemberLog)this.getById(memberLogIdParam.getId());
      if (ObjectUtil.isEmpty(memberLog)) {
         throw new CommonException("该日志不存在，id值为：{}", new Object[]{memberLogIdParam.getId()});
      } else {
         boolean owned = currentMemberId.equals(memberLog.getCreateUser()) || currentMemberId.equals(memberLog.getMemberNo());
         if (!owned) {
            throw new CommonException("该日志不存在，id值为：{}", new Object[]{memberLogIdParam.getId()});
         } else {
            return memberLog;
         }
      }
   }

   @Override
   public void delete(MemberLogDeleteParam devLogDeleteParam) {
      this.remove((Wrapper<MemberLog>)new LambdaQueryWrapper<MemberLog>().eq(MemberLog::getCategory, devLogDeleteParam.getCategory()));
   }

   @Override
   public List<MemberLogVisLineChartDataResult> visLogLineChartData() {
      LocalDateTime lastWeek = TimeUtil.now().minusWeeks(1L);
      LocalDateTime now = TimeUtil.now();
      Map<String, List<JSONObject>> listMap = this.list(
            (Wrapper<MemberLog>)((LambdaQueryWrapper<MemberLog>)((LambdaQueryWrapper<MemberLog>)new LambdaQueryWrapper<MemberLog>()
                     .in(MemberLog::getCategory, new Object[]{MemberLogCategoryEnum.LOGIN.getValue(), MemberLogCategoryEnum.LOGOUT.getValue()}))
                  .between(MemberLog::getOpTime, TimeUtil.toDate(lastWeek), TimeUtil.toDate(now)))
               .orderByAsc(MemberLog::getOpTime)
         )
         .stream()
         .map(devLog -> JSONUtil.parseObj(devLog).set("date", TimeUtil.formatDate(TimeUtil.toLocalDateTime(devLog.getOpTime()).toLocalDate())))
         .collect(Collectors.groupingBy(jsonObject -> jsonObject.getStr("date")));
      long between = ChronoUnit.DAYS.between(lastWeek, now);
      List<MemberLogVisLineChartDataResult> resultList = CollectionUtil.newArrayList(new MemberLogVisLineChartDataResult[0]);

      for (int i = 1; i <= between; i++) {
         MemberLogVisLineChartDataResult devLogVisLineChartDataResult = new MemberLogVisLineChartDataResult();
         String date = TimeUtil.formatDate(lastWeek.toLocalDate().plusDays(i));
         devLogVisLineChartDataResult.setDate(date);
         List<JSONObject> jsonObjectList = listMap.get(date);
         if (ObjectUtil.isNotEmpty(jsonObjectList)) {
            devLogVisLineChartDataResult.setLoginCount(
               jsonObjectList.stream().filter(jsonObject -> jsonObject.getStr("category").equals(MemberLogCategoryEnum.LOGIN.getValue())).count()
            );
            devLogVisLineChartDataResult.setLogoutCount(
               jsonObjectList.stream().filter(jsonObject -> jsonObject.getStr("category").equals(MemberLogCategoryEnum.LOGOUT.getValue())).count()
            );
         } else {
            devLogVisLineChartDataResult.setLoginCount(0L);
            devLogVisLineChartDataResult.setLogoutCount(0L);
         }

         resultList.add(devLogVisLineChartDataResult);
      }

      return resultList;
   }

   @Override
   public List<MemberLogVisPieChartDataResult> visLogPieChartData() {
      List<MemberLogVisPieChartDataResult> resultList = CollectionUtil.newArrayList(new MemberLogVisPieChartDataResult[0]);
      MemberLogVisPieChartDataResult devLogLoginPieChartDataResult = new MemberLogVisPieChartDataResult();
      devLogLoginPieChartDataResult.setType("登录");
      devLogLoginPieChartDataResult.setValue(
         Convert.toLong(this.count((Wrapper<MemberLog>)new LambdaQueryWrapper<MemberLog>().eq(MemberLog::getCategory, MemberLogCategoryEnum.LOGIN.getValue())))
      );
      resultList.add(devLogLoginPieChartDataResult);
      MemberLogVisPieChartDataResult devLogLogoutPieChartDataResult = new MemberLogVisPieChartDataResult();
      devLogLogoutPieChartDataResult.setType("登出");
      devLogLogoutPieChartDataResult.setValue(
         Convert.toLong(this.count((Wrapper<MemberLog>)new LambdaQueryWrapper<MemberLog>().eq(MemberLog::getCategory, MemberLogCategoryEnum.LOGOUT.getValue())))
      );
      resultList.add(devLogLogoutPieChartDataResult);
      return resultList;
   }

   @Override
   public List<MemberLogOpBarChartDataResult> opLogBarChartData() {
      LocalDateTime lastWeek = TimeUtil.now().minusWeeks(1L);
      LocalDateTime now = TimeUtil.now();
      Map<String, List<JSONObject>> listMap = this.list(
            (Wrapper<MemberLog>)((LambdaQueryWrapper<MemberLog>)((LambdaQueryWrapper<MemberLog>)new LambdaQueryWrapper<MemberLog>()
                     .select(
                        
                           MemberLog::getId,
                           MemberLog::getName,
                           MemberLog::getOpIp,
                           MemberLog::getOpAddress,
                           MemberLog::getCategory,
                           MemberLog::getClassName,
                           MemberLog::getMethodName,
                           MemberLog::getOpTime,
                           MemberLog::getOpUser
                        
                     )
                     .in(MemberLog::getCategory, new Object[]{MemberLogCategoryEnum.OPERATE.getValue(), MemberLogCategoryEnum.EXCEPTION.getValue()}))
                  .between(MemberLog::getOpTime, TimeUtil.toDate(lastWeek), TimeUtil.toDate(now)))
               .orderByAsc(MemberLog::getOpTime)
         )
         .stream()
         .map(devLog -> JSONUtil.parseObj(devLog).set("date", TimeUtil.formatDate(TimeUtil.toLocalDateTime(devLog.getOpTime()).toLocalDate())))
         .collect(Collectors.groupingBy(jsonObject -> jsonObject.getStr("date")));
      long between = ChronoUnit.DAYS.between(lastWeek, now);
      List<MemberLogOpBarChartDataResult> resultList = CollectionUtil.newArrayList(new MemberLogOpBarChartDataResult[0]);

      for (int i = 1; i <= between; i++) {
         String date = TimeUtil.formatDate(lastWeek.toLocalDate().plusDays(i));
         MemberLogOpBarChartDataResult devLogOperateBarChartDataResult = new MemberLogOpBarChartDataResult();
         devLogOperateBarChartDataResult.setDate(date);
         devLogOperateBarChartDataResult.setName("操作日志");
         MemberLogOpBarChartDataResult devLogExceptionBarChartDataResult = new MemberLogOpBarChartDataResult();
         devLogExceptionBarChartDataResult.setDate(date);
         devLogExceptionBarChartDataResult.setName("异常日志");
         List<JSONObject> jsonObjectList = listMap.get(date);
         if (ObjectUtil.isNotEmpty(jsonObjectList)) {
            devLogOperateBarChartDataResult.setCount(
               jsonObjectList.stream().filter(jsonObject -> jsonObject.getStr("category").equals(MemberLogCategoryEnum.OPERATE.getValue())).count()
            );
            devLogExceptionBarChartDataResult.setCount(
               jsonObjectList.stream().filter(jsonObject -> jsonObject.getStr("category").equals(MemberLogCategoryEnum.EXCEPTION.getValue())).count()
            );
         } else {
            devLogOperateBarChartDataResult.setCount(0L);
            devLogExceptionBarChartDataResult.setCount(0L);
         }

         resultList.add(devLogOperateBarChartDataResult);
         resultList.add(devLogExceptionBarChartDataResult);
      }

      return resultList;
   }

   @Override
   public List<MemberLogOpPieChartDataResult> opLogPieChartData() {
      List<MemberLogOpPieChartDataResult> resultList = CollectionUtil.newArrayList(new MemberLogOpPieChartDataResult[0]);
      MemberLogOpPieChartDataResult devLogOperatePieChartDataResult = new MemberLogOpPieChartDataResult();
      devLogOperatePieChartDataResult.setType("操作日志");
      devLogOperatePieChartDataResult.setValue(
         Convert.toLong(this.count((Wrapper<MemberLog>)new LambdaQueryWrapper<MemberLog>().eq(MemberLog::getCategory, MemberLogCategoryEnum.OPERATE.getValue())))
      );
      resultList.add(devLogOperatePieChartDataResult);
      MemberLogOpPieChartDataResult devLogExceptionPieChartDataResult = new MemberLogOpPieChartDataResult();
      devLogExceptionPieChartDataResult.setType("异常日志");
      devLogExceptionPieChartDataResult.setValue(
         Convert.toLong(this.count((Wrapper<MemberLog>)new LambdaQueryWrapper<MemberLog>().eq(MemberLog::getCategory, MemberLogCategoryEnum.EXCEPTION.getValue())))
      );
      resultList.add(devLogExceptionPieChartDataResult);
      return resultList;
   }

   private QueryWrapper<MemberLog> buildCurrentMemberQueryWrapper(MemberLogPageParam memberLogPageParam) {
      String currentMemberId = String.valueOf(StpClientLoginUserUtil.getClientLoginUser().getId());
      QueryWrapper<MemberLog> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.select(
         MemberLog.class,
         info -> !info.getColumn().equalsIgnoreCase("param_json")
            && !info.getColumn().equalsIgnoreCase("result_json")
            && !info.getColumn().equalsIgnoreCase("exe_message")
            && !info.getColumn().equalsIgnoreCase("sign_data")
      );
      queryWrapper.and(wrapper -> ((QueryWrapper)((QueryWrapper)wrapper.eq("create_user", currentMemberId)).or()).eq("member_no", currentMemberId));
      if (ObjectUtil.isNotEmpty(memberLogPageParam.getCategory())) {
         queryWrapper.lambda().eq(MemberLog::getCategory, memberLogPageParam.getCategory());
      }

      if (ObjectUtil.isNotEmpty(memberLogPageParam.getSearchKey())) {
         queryWrapper.lambda().like(MemberLog::getName, memberLogPageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{memberLogPageParam.getSortField(), memberLogPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(memberLogPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, memberLogPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(memberLogPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(MemberLog::getCreateTime);
      }

      return queryWrapper;
   }
}
