package com.rxinyun.admin.dev.modular.log.provider;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.admin.business.modular.esc.service.ServerService;
import com.rxinyun.admin.business.modular.independent.entity.Independent;
import com.rxinyun.admin.business.modular.independent.service.IndependentService;
import com.rxinyun.admin.dev.modular.log.entity.SysLog;
import com.rxinyun.admin.dev.modular.log.enums.DevLogCategoryEnum;
import com.rxinyun.admin.dev.modular.log.param.IndependentLogPageParam;
import com.rxinyun.admin.dev.modular.log.param.ServerLogPageParam;
import com.rxinyun.admin.dev.modular.log.result.ServerOperationLogResult;
import com.rxinyun.admin.dev.modular.log.service.IndependentLogService;
import com.rxinyun.admin.dev.modular.log.service.ServerLogService;
import com.rxinyun.admin.dev.modular.log.service.SysLogService;
import com.rxinyun.admin.dev.modular.log.util.LogUtil;
import com.rxinyun.auth.core.util.StpLoginUserUtil;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.dev.api.DevLogApi;
import com.rxinyun.dev.core.pojo.BaseIndependentLogPageParam;
import com.rxinyun.dev.core.pojo.BaseServerLogPageParam;
import com.rxinyun.dev.core.pojo.BaseServerOperationLogResult;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class LogApiProvider implements DevLogApi {
   @Resource
   private SysLogService devLogService;
   @Resource
   private ServerLogService serverLogService;
   @Resource
   private IndependentLogService independentLogService;
   @Resource
   private ServerService serverService;
   @Resource
   private IndependentService independentService;

   public void executeLoginLog(String userName) {
      LogUtil.executeLoginLog(userName);
   }

   public void executeLogoutLog(String userName) {
      LogUtil.executeLogoutLog(userName);
   }

   public List<JSONObject> currentUserVisLogList() {
      return this.devLogService
            .page(
               CommonPageRequest.defaultPage(),
               (Wrapper<SysLog>)((LambdaQueryWrapper<SysLog>)((LambdaQueryWrapper<SysLog>)new LambdaQueryWrapper<SysLog>()
                        .select(SysLog::getName, SysLog::getOpUser, SysLog::getOpTime, SysLog::getOpAddress, SysLog::getOpIp)
                        .eq(SysLog::getOpUser, StpLoginUserUtil.getLoginUser().getName()))
                     .in(SysLog::getCategory, new Object[]{DevLogCategoryEnum.LOGIN.getValue(), DevLogCategoryEnum.LOGOUT.getValue()}))
                  .orderByDesc(SysLog::getCreateTime)
            )
         .getRecords()
         .stream()
         .<JSONObject>map(JSONUtil::parseObj)
         .collect(Collectors.toList());
   }

   public List<JSONObject> currentUserOpLogList() {
      return this.devLogService
            .page(
               CommonPageRequest.defaultPage(),
               (Wrapper<SysLog>)((LambdaQueryWrapper<SysLog>)((LambdaQueryWrapper<SysLog>)new LambdaQueryWrapper<SysLog>()
                        .select(SysLog::getName, SysLog::getOpUser, SysLog::getOpTime, SysLog::getOpAddress, SysLog::getOpIp)
                        .eq(SysLog::getOpUser, StpLoginUserUtil.getLoginUser().getName()))
                     .in(SysLog::getCategory, new Object[]{DevLogCategoryEnum.OPERATE.getValue(), DevLogCategoryEnum.EXCEPTION.getValue()}))
                  .orderByDesc(SysLog::getCreateTime)
            )
         .getRecords()
         .stream()
         .<JSONObject>map(JSONUtil::parseObj)
         .collect(Collectors.toList());
   }

   public Page<BaseServerOperationLogResult> cloudServerLogPage(BaseServerLogPageParam param) {
      if (param.getMemberNo() != null) {
         Server server = (Server)this.serverService
            .getOne(
               (Wrapper<Server>)((LambdaQueryWrapper<Server>)new LambdaQueryWrapper<Server>().eq(Server::getId, param.getServerId()))
                  .eq(Server::getMemberNo, String.valueOf(param.getMemberNo()))
            );
         if (server == null) {
            return new Page(this.defaultCurrent(param.getCurrent()), this.defaultSize(param.getSize()), 0L);
         }
      }

      ServerLogPageParam serverLogPageParam = new ServerLogPageParam();
      BeanUtil.copyProperties(param, serverLogPageParam, new String[0]);
      return this.convertPage(this.serverLogService.page(serverLogPageParam));
   }

   public Page<BaseServerOperationLogResult> independentServerLogPage(BaseIndependentLogPageParam param) {
      if (param.getMemberNo() != null) {
         Independent independent = (Independent)this.independentService
            .getOne(
               (Wrapper<Independent>)((LambdaQueryWrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, param.getServerId()))
                  .eq(Independent::getMemberNo, param.getMemberNo())
            );
         if (independent == null) {
            return new Page(this.defaultCurrent(param.getCurrent()), this.defaultSize(param.getSize()), 0L);
         }
      }

      IndependentLogPageParam independentLogPageParam = new IndependentLogPageParam();
      BeanUtil.copyProperties(param, independentLogPageParam, new String[0]);
      return this.convertPage(this.independentLogService.page(independentLogPageParam));
   }

   private Page<BaseServerOperationLogResult> convertPage(Page<ServerOperationLogResult> sourcePage) {
      Page<BaseServerOperationLogResult> page = new Page(sourcePage.getCurrent(), sourcePage.getSize(), sourcePage.getTotal());
      page.setRecords(
         sourcePage.getRecords()
            .stream()
            .map(record -> (BaseServerOperationLogResult)BeanUtil.toBean(record, BaseServerOperationLogResult.class))
            .collect(Collectors.toList())
      );
      return page;
   }

   private long defaultCurrent(Integer current) {
      return current != null && current >= 1 ? current.intValue() : 1L;
   }

   private long defaultSize(Integer size) {
      return size != null && size >= 1 ? size.intValue() : 10L;
   }
}
