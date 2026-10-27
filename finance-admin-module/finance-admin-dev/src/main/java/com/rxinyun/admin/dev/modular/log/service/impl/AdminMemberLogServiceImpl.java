package com.rxinyun.admin.dev.modular.log.service.impl;

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
import com.rxinyun.admin.dev.modular.log.entity.MemberLog;
import com.rxinyun.admin.dev.modular.log.enums.DevLogCategoryEnum;
import com.rxinyun.admin.dev.modular.log.mapper.AdminMemberLogMapper;
import com.rxinyun.admin.dev.modular.log.param.LogDeleteParam;
import com.rxinyun.admin.dev.modular.log.param.LogIdParam;
import com.rxinyun.admin.dev.modular.log.param.LogPageParam;
import com.rxinyun.admin.dev.modular.log.result.DevLogOpBarChartDataResult;
import com.rxinyun.admin.dev.modular.log.result.DevLogOpPieChartDataResult;
import com.rxinyun.admin.dev.modular.log.result.DevLogVisLineChartDataResult;
import com.rxinyun.admin.dev.modular.log.result.DevLogVisPieChartDataResult;
import com.rxinyun.admin.dev.modular.log.service.AdminMemberLogService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.util.TimeUtil;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AdminMemberLogServiceImpl extends ServiceImpl<AdminMemberLogMapper, MemberLog> implements AdminMemberLogService {
   @Override
   public Page<MemberLog> page(LogPageParam devLogPageParam) {
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

      if (ObjectUtil.isNotEmpty(devLogPageParam.getMemberNo())) {
         queryWrapper.lambda().eq(MemberLog::getMemberNo, devLogPageParam.getMemberNo());
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
   public MemberLog detail(LogIdParam devLogIdParam) {
      MemberLog devLog = (MemberLog)this.getById(devLogIdParam.getId());
      if (ObjectUtil.isEmpty(devLog)) {
         throw new CommonException("该日志不存在，id值为：{}", new Object[]{devLog.getId()});
      } else {
         return devLog;
      }
   }

   @Override
   public void delete(LogDeleteParam devLogDeleteParam) {
      this.remove((Wrapper<MemberLog>)new LambdaQueryWrapper<MemberLog>().eq(MemberLog::getCategory, devLogDeleteParam.getCategory()));
   }

   @Override
   public List<DevLogVisLineChartDataResult> visLogLineChartData() {
      LocalDateTime lastWeek = TimeUtil.now().minusWeeks(1L);
      LocalDateTime now = TimeUtil.now();
      Map<String, List<JSONObject>> listMap = this.list(
            (Wrapper<MemberLog>)((LambdaQueryWrapper<MemberLog>)((LambdaQueryWrapper<MemberLog>)new LambdaQueryWrapper<MemberLog>()
                     .in(MemberLog::getCategory, new Object[]{DevLogCategoryEnum.LOGIN.getValue(), DevLogCategoryEnum.LOGOUT.getValue()}))
                  .between(MemberLog::getOpTime, TimeUtil.toDate(lastWeek), TimeUtil.toDate(now)))
               .orderByAsc(MemberLog::getOpTime)
         )
         .stream()
         .map(devLog -> JSONUtil.parseObj(devLog).set("date", TimeUtil.formatDate(TimeUtil.toLocalDateTime(devLog.getOpTime()).toLocalDate())))
         .collect(Collectors.groupingBy(jsonObject -> jsonObject.getStr("date")));
      long between = ChronoUnit.DAYS.between(lastWeek, now);
      List<DevLogVisLineChartDataResult> resultList = CollectionUtil.newArrayList(new DevLogVisLineChartDataResult[0]);

      for (int i = 1; i <= between; i++) {
         DevLogVisLineChartDataResult devLogVisLineChartDataResult = new DevLogVisLineChartDataResult();
         String date = TimeUtil.formatDate(lastWeek.toLocalDate().plusDays(i));
         devLogVisLineChartDataResult.setDate(date);
         List<JSONObject> jsonObjectList = listMap.get(date);
         if (ObjectUtil.isNotEmpty(jsonObjectList)) {
            devLogVisLineChartDataResult.setLoginCount(
               jsonObjectList.stream().filter(jsonObject -> jsonObject.getStr("category").equals(DevLogCategoryEnum.LOGIN.getValue())).count()
            );
            devLogVisLineChartDataResult.setLogoutCount(
               jsonObjectList.stream().filter(jsonObject -> jsonObject.getStr("category").equals(DevLogCategoryEnum.LOGOUT.getValue())).count()
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
   public List<DevLogVisPieChartDataResult> visLogPieChartData() {
      List<DevLogVisPieChartDataResult> resultList = CollectionUtil.newArrayList(new DevLogVisPieChartDataResult[0]);
      DevLogVisPieChartDataResult devLogLoginPieChartDataResult = new DevLogVisPieChartDataResult();
      devLogLoginPieChartDataResult.setType("登录");
      devLogLoginPieChartDataResult.setValue(
         Convert.toLong(this.count((Wrapper<MemberLog>)new LambdaQueryWrapper<MemberLog>().eq(MemberLog::getCategory, DevLogCategoryEnum.LOGIN.getValue())))
      );
      resultList.add(devLogLoginPieChartDataResult);
      DevLogVisPieChartDataResult devLogLogoutPieChartDataResult = new DevLogVisPieChartDataResult();
      devLogLogoutPieChartDataResult.setType("登出");
      devLogLogoutPieChartDataResult.setValue(
         Convert.toLong(this.count((Wrapper<MemberLog>)new LambdaQueryWrapper<MemberLog>().eq(MemberLog::getCategory, DevLogCategoryEnum.LOGOUT.getValue())))
      );
      resultList.add(devLogLogoutPieChartDataResult);
      return resultList;
   }

   @Override
   public List<DevLogOpBarChartDataResult> opLogBarChartData() {
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
                     .in(MemberLog::getCategory, new Object[]{DevLogCategoryEnum.OPERATE.getValue(), DevLogCategoryEnum.EXCEPTION.getValue()}))
                  .between(MemberLog::getOpTime, TimeUtil.toDate(lastWeek), TimeUtil.toDate(now)))
               .orderByAsc(MemberLog::getOpTime)
         )
         .stream()
         .map(devLog -> JSONUtil.parseObj(devLog).set("date", TimeUtil.formatDate(TimeUtil.toLocalDateTime(devLog.getOpTime()).toLocalDate())))
         .collect(Collectors.groupingBy(jsonObject -> jsonObject.getStr("date")));
      long between = ChronoUnit.DAYS.between(lastWeek, now);
      List<DevLogOpBarChartDataResult> resultList = CollectionUtil.newArrayList(new DevLogOpBarChartDataResult[0]);

      for (int i = 1; i <= between; i++) {
         String date = TimeUtil.formatDate(lastWeek.toLocalDate().plusDays(i));
         DevLogOpBarChartDataResult devLogOperateBarChartDataResult = new DevLogOpBarChartDataResult();
         devLogOperateBarChartDataResult.setDate(date);
         devLogOperateBarChartDataResult.setName("操作日志");
         DevLogOpBarChartDataResult devLogExceptionBarChartDataResult = new DevLogOpBarChartDataResult();
         devLogExceptionBarChartDataResult.setDate(date);
         devLogExceptionBarChartDataResult.setName("异常日志");
         List<JSONObject> jsonObjectList = listMap.get(date);
         if (ObjectUtil.isNotEmpty(jsonObjectList)) {
            devLogOperateBarChartDataResult.setCount(
               jsonObjectList.stream().filter(jsonObject -> jsonObject.getStr("category").equals(DevLogCategoryEnum.OPERATE.getValue())).count()
            );
            devLogExceptionBarChartDataResult.setCount(
               jsonObjectList.stream().filter(jsonObject -> jsonObject.getStr("category").equals(DevLogCategoryEnum.EXCEPTION.getValue())).count()
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
   public List<DevLogOpPieChartDataResult> opLogPieChartData() {
      List<DevLogOpPieChartDataResult> resultList = CollectionUtil.newArrayList(new DevLogOpPieChartDataResult[0]);
      DevLogOpPieChartDataResult devLogOperatePieChartDataResult = new DevLogOpPieChartDataResult();
      devLogOperatePieChartDataResult.setType("操作日志");
      devLogOperatePieChartDataResult.setValue(
         Convert.toLong(this.count((Wrapper<MemberLog>)new LambdaQueryWrapper<MemberLog>().eq(MemberLog::getCategory, DevLogCategoryEnum.OPERATE.getValue())))
      );
      resultList.add(devLogOperatePieChartDataResult);
      DevLogOpPieChartDataResult devLogExceptionPieChartDataResult = new DevLogOpPieChartDataResult();
      devLogExceptionPieChartDataResult.setType("异常日志");
      devLogExceptionPieChartDataResult.setValue(
         Convert.toLong(this.count((Wrapper<MemberLog>)new LambdaQueryWrapper<MemberLog>().eq(MemberLog::getCategory, DevLogCategoryEnum.EXCEPTION.getValue())))
      );
      resultList.add(devLogExceptionPieChartDataResult);
      return resultList;
   }
}
