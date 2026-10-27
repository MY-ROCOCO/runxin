package com.rxinyun.admin.dev.modular.log.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.log.entity.MemberLog;
import com.rxinyun.admin.dev.modular.log.entity.SysLog;
import com.rxinyun.admin.dev.modular.log.enums.DevLogCategoryEnum;
import com.rxinyun.admin.dev.modular.log.param.IndependentLogPageParam;
import com.rxinyun.admin.dev.modular.log.result.ServerOperationLogResult;
import com.rxinyun.admin.dev.modular.log.service.AdminMemberLogService;
import com.rxinyun.admin.dev.modular.log.service.IndependentLogService;
import com.rxinyun.admin.dev.modular.log.service.SysLogService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class IndependentLogServiceImpl implements IndependentLogService {
   private static final String ADMIN_IDENTITY = "管理员";
   private static final String MEMBER_IDENTITY = "会员";
   private static final String INDEPENDENT_LOG_PREFIX = "/admin/independent/";
   private static final String INDEPENDENT_MEMBER_LOG_PREFIX = "/member/independent/";
   private static final String INDEPENDENT_CONTROLLER = "com.rxinyun.admin.business.modular.independent.controller.IndependentController";
   private static final String INDEPENDENT_MEMBER_CONTROLLER = "com.rxinyun.member.business.modular.independent.controller.MemberIndependentController";
   @Resource
   private SysLogService sysLogService;
   @Resource
   private AdminMemberLogService adminMemberLogService;

   @Override
   public Page<ServerOperationLogResult> page(IndependentLogPageParam independentLogPageParam) {
      List<ServerOperationLogResult> records = new ArrayList<>();
      records.addAll(this.queryAdminLogs(independentLogPageParam));
      records.addAll(this.queryMemberLogs(independentLogPageParam));
      records.sort(this.buildComparator(independentLogPageParam.getSortField(), independentLogPageParam.getSortOrder()));
      long current = independentLogPageParam.getCurrent() != null && independentLogPageParam.getCurrent() >= 1
         ? independentLogPageParam.getCurrent().intValue()
         : 1L;
      long size = independentLogPageParam.getSize() != null && independentLogPageParam.getSize() >= 1 ? independentLogPageParam.getSize().intValue() : 10L;
      long total = records.size();
      int fromIndex = (int)Math.min((current - 1L) * size, total);
      int toIndex = (int)Math.min(fromIndex + size, total);
      List<ServerOperationLogResult> pageRecords = fromIndex >= toIndex ? Collections.emptyList() : records.subList(fromIndex, toIndex);
      Page<ServerOperationLogResult> page = new Page(current, size, total);
      page.setRecords(pageRecords);
      return page;
   }

   private List<ServerOperationLogResult> queryAdminLogs(IndependentLogPageParam independentLogPageParam) {
      QueryWrapper<SysLog> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.select(new String[]{"id", "name", "category", "op_time", "op_user"});
      ((QueryWrapper)((QueryWrapper)queryWrapper.likeRight("class_name", "com.rxinyun.admin.business.modular.independent.controller.IndependentController"))
            .likeRight("req_url", "/admin/independent/"))
         .like("param_json", independentLogPageParam.getServerId());
      if (ObjectUtil.isNotEmpty(independentLogPageParam.getCategory())) {
         queryWrapper.eq("category", independentLogPageParam.getCategory());
      }

      if (ObjectUtil.isNotEmpty(independentLogPageParam.getSearchKey())) {
         queryWrapper.like("name", independentLogPageParam.getSearchKey());
      }

      return this.sysLogService.list(queryWrapper).stream().map(this::toAdminResult).collect(Collectors.toList());
   }

   private List<ServerOperationLogResult> queryMemberLogs(IndependentLogPageParam independentLogPageParam) {
      QueryWrapper<MemberLog> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.select(new String[]{"id", "name", "category", "op_time", "op_user"});
      ((QueryWrapper)((QueryWrapper)queryWrapper.likeRight(
               "class_name", "com.rxinyun.member.business.modular.independent.controller.MemberIndependentController"
            ))
            .likeRight("req_url", "/member/independent/"))
         .like("param_json", independentLogPageParam.getServerId());
      if (ObjectUtil.isNotEmpty(independentLogPageParam.getCategory())) {
         queryWrapper.eq("category", independentLogPageParam.getCategory());
      }

      if (ObjectUtil.isNotEmpty(independentLogPageParam.getSearchKey())) {
         queryWrapper.like("name", independentLogPageParam.getSearchKey());
      }

      return this.adminMemberLogService.list(queryWrapper).stream().map(this::toMemberResult).collect(Collectors.toList());
   }

   private ServerOperationLogResult toAdminResult(SysLog sysLog) {
      ServerOperationLogResult result = new ServerOperationLogResult();
      result.setId(sysLog.getId());
      result.setOperationContent(sysLog.getName());
      result.setOperationType(this.resolveOperationType(sysLog.getCategory()));
      result.setOperationTime(sysLog.getOpTime());
      result.setOperatorName(sysLog.getOpUser());
      result.setOperatorIdentity(this.resolveAdminIdentity(sysLog.getOpUser()));
      return result;
   }

   private ServerOperationLogResult toMemberResult(MemberLog memberLog) {
      ServerOperationLogResult result = new ServerOperationLogResult();
      result.setId(memberLog.getId());
      result.setOperationContent(memberLog.getName());
      result.setOperationType(this.resolveOperationType(memberLog.getCategory()));
      result.setOperationTime(memberLog.getOpTime());
      result.setOperatorName(memberLog.getOpUser());
      result.setOperatorIdentity("会员");
      return result;
   }

   private Comparator<ServerOperationLogResult> buildComparator(String sortField, String sortOrder) {
      Comparator<ServerOperationLogResult> comparator;
      if ("operationContent".equals(sortField) || "name".equals(sortField)) {
         comparator = Comparator.comparing(ServerOperationLogResult::getOperationContent, Comparator.nullsLast(String::compareTo));
      } else if ("operationType".equals(sortField) || "category".equals(sortField)) {
         comparator = Comparator.comparing(ServerOperationLogResult::getOperationType, Comparator.nullsLast(String::compareTo));
      } else if ("operatorName".equals(sortField) || "opUser".equals(sortField)) {
         comparator = Comparator.comparing(ServerOperationLogResult::getOperatorName, Comparator.nullsLast(String::compareTo));
      } else if ("operatorIdentity".equals(sortField)) {
         comparator = Comparator.comparing(ServerOperationLogResult::getOperatorIdentity, Comparator.nullsLast(String::compareTo));
      } else {
         comparator = Comparator.comparing(ServerOperationLogResult::getOperationTime, Comparator.nullsLast(Date::compareTo));
      }

      if (ObjectUtil.isNotEmpty(sortOrder)) {
         CommonSortOrderEnum.validate(sortOrder);
         if (!CommonSortOrderEnum.ASC.getValue().equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
         }
      } else {
         comparator = comparator.reversed();
      }

      return comparator;
   }

   private String resolveOperationType(String category) {
      if (DevLogCategoryEnum.OPERATE.getValue().equals(category)) {
         return "操作日志";
      } else if (DevLogCategoryEnum.EXCEPTION.getValue().equals(category)) {
         return "异常日志";
      } else if (DevLogCategoryEnum.LOGIN.getValue().equals(category)) {
         return "登录日志";
      } else {
         return DevLogCategoryEnum.LOGOUT.getValue().equals(category) ? "登出日志" : category;
      }
   }

   private String resolveAdminIdentity(String opUser) {
      return "系统任务".equals(opUser) ? "系统" : "管理员";
   }
}
