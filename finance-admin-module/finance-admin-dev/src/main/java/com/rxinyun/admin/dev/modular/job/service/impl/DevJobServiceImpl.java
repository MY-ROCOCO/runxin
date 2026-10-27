package com.rxinyun.admin.dev.modular.job.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.dev.modular.job.entity.DevJob;
import com.rxinyun.admin.dev.modular.job.enums.DevJobCategoryEnum;
import com.rxinyun.admin.dev.modular.job.enums.DevJobStatusEnum;
import com.rxinyun.admin.dev.modular.job.mapper.DevJobMapper;
import com.rxinyun.admin.dev.modular.job.param.DevJobAddParam;
import com.rxinyun.admin.dev.modular.job.param.DevJobEditParam;
import com.rxinyun.admin.dev.modular.job.param.DevJobIdParam;
import com.rxinyun.admin.dev.modular.job.param.DevJobListParam;
import com.rxinyun.admin.dev.modular.job.param.DevJobPageParam;
import com.rxinyun.admin.dev.modular.job.result.DevJobActionClassResult;
import com.rxinyun.admin.dev.modular.job.service.DevJobService;
import com.rxinyun.admin.dev.modular.job.support.DevJobTaskDispatcher;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.aop.support.AopUtils;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DevJobServiceImpl extends ServiceImpl<DevJobMapper, DevJob> implements DevJobService {
   @Resource
   private DevJobTaskDispatcher devJobTaskDispatcher;

   @Override
   public Page<DevJob> page(DevJobPageParam devJobPageParam) {
      QueryWrapper<DevJob> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda()
         .select(
            
               DevJob::getId,
               DevJob::getName,
               DevJob::getCategory,
               DevJob::getActionClass,
               DevJob::getCronExpression,
               DevJob::getJobStatus,
               DevJob::getSortCode
            
         );
      if (ObjectUtil.isNotEmpty(devJobPageParam.getCategory())) {
         queryWrapper.lambda().eq(DevJob::getCategory, devJobPageParam.getCategory());
      }

      if (ObjectUtil.isNotEmpty(devJobPageParam.getSearchKey())) {
         queryWrapper.lambda().like(DevJob::getName, devJobPageParam.getSearchKey());
      }

      if (ObjectUtil.isNotEmpty(devJobPageParam.getJobStatus())) {
         queryWrapper.lambda().like(DevJob::getJobStatus, devJobPageParam.getJobStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{devJobPageParam.getSortField(), devJobPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(devJobPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, devJobPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(devJobPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(DevJob::getSortCode);
      }

      return (Page<DevJob>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public List<DevJob> list(DevJobListParam devJobListParam) {
      LambdaQueryWrapper<DevJob> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.select(
         
            DevJob::getId, DevJob::getName, DevJob::getCategory, DevJob::getActionClass, DevJob::getCronExpression, DevJob::getJobStatus, DevJob::getSortCode
         
      );
      if (ObjectUtil.isNotEmpty(devJobListParam.getCategory())) {
         lambdaQueryWrapper.eq(DevJob::getCategory, devJobListParam.getCategory());
      }

      if (ObjectUtil.isNotEmpty(devJobListParam.getSearchKey())) {
         lambdaQueryWrapper.like(DevJob::getName, devJobListParam.getSearchKey());
      }

      if (ObjectUtil.isNotEmpty(devJobListParam.getJobStatus())) {
         lambdaQueryWrapper.like(DevJob::getJobStatus, devJobListParam.getJobStatus());
      }

      return this.list(lambdaQueryWrapper);
   }

   @Override
   public void add(DevJobAddParam devJobAddParam) {
      this.checkParam(devJobAddParam);
      DevJob devJob = (DevJob)BeanUtil.toBean(devJobAddParam, DevJob.class);
      devJob.setCode(RandomUtil.randomString(10));
      devJob.setJobStatus(DevJobStatusEnum.STOPPED.getValue());
      this.save(devJob);
   }

   private void checkParam(DevJobAddParam devJobAddParam) {
      DevJobCategoryEnum.validate(devJobAddParam.getCategory());

      try {
         CronPattern.of(devJobAddParam.getCronExpression());
      } catch (Exception var4) {
         throw new CommonException("cron表达式：{}格式不正确", new Object[]{devJobAddParam.getCronExpression()});
      }

      try {
         Class<?> actionClass = Class.forName(devJobAddParam.getActionClass());
         if (!CommonTimerTaskRunner.class.isAssignableFrom(actionClass)) {
            List<String> actionClassArr = StrUtil.split(devJobAddParam.getActionClass(), ".");
            throw new CommonException("定时任务对应的类：{}不符合要求", new Object[]{actionClassArr.get(actionClassArr.size() - 1)});
         }
      } catch (ClassNotFoundException var5) {
         throw new CommonException("定时任务找不到对应的类，名称为：{}", new Object[]{devJobAddParam.getActionClass()});
      }

      boolean hasSameJob = this.count(
            (Wrapper<DevJob>)((LambdaQueryWrapper<DevJob>)new LambdaQueryWrapper<DevJob>().eq(DevJob::getActionClass, devJobAddParam.getActionClass()))
               .eq(DevJob::getCronExpression, devJobAddParam.getCronExpression())
         )
         > 0L;
      if (hasSameJob) {
         throw new CommonException("存在重复执行的定时任务，名称为：{}", new Object[]{devJobAddParam.getName()});
      }
   }

   @Override
   public void edit(DevJobEditParam devJobEditParam) {
      DevJob devJob = this.queryEntity(devJobEditParam.getId());
      if (devJob.getJobStatus().equals(DevJobStatusEnum.RUNNING.getValue())) {
         throw new CommonException("运行中的定时任务不可编辑，id值为：{}", new Object[]{devJob.getId()});
      } else {
         this.checkParam(devJobEditParam);
         BeanUtil.copyProperties(devJobEditParam, devJob, new String[0]);
         this.updateById(devJob);
      }
   }

   private void checkParam(DevJobEditParam devJobEditParam) {
      DevJobCategoryEnum.validate(devJobEditParam.getCategory());
      if (!CronExpression.isValidExpression(devJobEditParam.getCronExpression())) {
         throw new CommonException("cron表达式：{}格式不正确", new Object[]{devJobEditParam.getCronExpression()});
      } else {
         try {
            Class<?> actionClass = Class.forName(devJobEditParam.getActionClass());
            if (!CommonTimerTaskRunner.class.isAssignableFrom(actionClass)) {
               List<String> actionClassArr = StrUtil.split(devJobEditParam.getActionClass(), ".");
               throw new CommonException("定时任务对应的类：{}不符合要求", new Object[]{actionClassArr.get(actionClassArr.size() - 1)});
            }
         } catch (ClassNotFoundException var4) {
            throw new CommonException("定时任务找不到对应的类，名称为：{}", new Object[]{devJobEditParam.getActionClass()});
         }

         boolean hasSameJob = this.count(
               (Wrapper<DevJob>)((LambdaQueryWrapper<DevJob>)((LambdaQueryWrapper<DevJob>)new LambdaQueryWrapper<DevJob>().eq(DevJob::getActionClass, devJobEditParam.getActionClass()))
                     .eq(DevJob::getCronExpression, devJobEditParam.getCronExpression()))
                  .ne(DevJob::getId, devJobEditParam.getId())
            )
            > 0L;
         if (hasSameJob) {
            throw new CommonException("存在重复的定时任务，名称为：{}", new Object[]{devJobEditParam.getName()});
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<DevJobIdParam> devJobIdParamList) {
      List<String> devJobIdList = CollStreamUtil.toList(devJobIdParamList, DevJobIdParam::getId);
      if (ObjectUtil.isNotEmpty(devJobIdList)) {
         devJobIdList.forEach(CronUtil::remove);
         this.removeByIds(devJobIdList);
      }
   }

   @Override
   public DevJob detail(DevJobIdParam devJobIdParam) {
      return this.queryEntity(devJobIdParam.getId());
   }

   @Override
   public DevJob queryEntity(String id) {
      DevJob devJob = (DevJob)this.getById(id);
      if (ObjectUtil.isEmpty(devJob)) {
         throw new CommonException("定时任务不存在，id值为：{}", new Object[]{id});
      } else {
         return devJob;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void stopJob(DevJobIdParam devJobIdParam) {
      DevJob devJob = this.detail(devJobIdParam);
      if (devJob.getJobStatus().equals(DevJobStatusEnum.STOPPED.getValue())) {
         throw new CommonException("定时任务已经处于停止状态，id值为：{}", new Object[]{devJob.getId()});
      } else {
         CronUtil.remove(devJob.getId());
         this.update(
            (Wrapper<DevJob>)((LambdaUpdateWrapper<DevJob>)new LambdaUpdateWrapper<DevJob>().eq(DevJob::getId, devJobIdParam.getId()))
               .set(DevJob::getJobStatus, DevJobStatusEnum.STOPPED.getValue())
         );
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void runJob(DevJobIdParam devJobIdParam) {
      DevJob devJob = this.detail(devJobIdParam);
      if (devJob.getJobStatus().equals(DevJobStatusEnum.RUNNING.getValue())) {
         throw new CommonException("定时任务已经处于运行状态，id值为：{}", new Object[]{devJob.getId()});
      } else {
         CronUtil.schedule(devJob.getId(), devJob.getCronExpression(), this.devJobTaskDispatcher.wrap(devJob));
         this.update(
            (Wrapper<DevJob>)((LambdaUpdateWrapper<DevJob>)new LambdaUpdateWrapper<DevJob>().eq(DevJob::getId, devJobIdParam.getId()))
               .set(DevJob::getJobStatus, DevJobStatusEnum.RUNNING.getValue())
         );
      }
   }

   @Override
   public void runJobNow(DevJobIdParam devJobIdParam) {
      DevJob devJob = this.detail(devJobIdParam);
      if (devJob.getJobStatus().equals(DevJobStatusEnum.STOPPED.getValue())) {
         this.runJob(devJobIdParam);
      }

      try {
         ((CommonTimerTaskRunner)SpringUtil.getBean(Class.forName(devJob.getActionClass()))).action();
      } catch (ClassNotFoundException var4) {
         throw new CommonException("定时任务找不到对应的类，名称为：{}", new Object[]{devJob.getActionClass()});
      }
   }

   @Override
   public List<DevJobActionClassResult> getActionClass() {
      Map<String, CommonTimerTaskRunner> commonTimerTaskRunnerMap = SpringUtil.getBeansOfType(CommonTimerTaskRunner.class);
      if (ObjectUtil.isNotEmpty(commonTimerTaskRunnerMap)) {
         Collection<CommonTimerTaskRunner> values = commonTimerTaskRunnerMap.values();
         return values.stream().map(commonTimerTaskRunner -> {
            Class<?> clazz = null;
            if (AopUtils.isAopProxy(commonTimerTaskRunner)) {
               clazz = AopUtils.getTargetClass(commonTimerTaskRunner);
            } else {
               clazz = commonTimerTaskRunner.getClass();
            }

            DevJobActionClassResult result = new DevJobActionClassResult();
            result.setActionClass(clazz.getName());
            result.setActionClassName(this.getActionClassName(clazz.getSimpleName()));
            return result;
         }).collect(Collectors.toList());
      } else {
         return CollectionUtil.newArrayList(new DevJobActionClassResult[0]);
      }
   }

   private String getActionClassName(String className) {
      return switch (className) {
         case "OrderUnpaidTaskRunner" -> "订单超时关闭";
         case "ServerEndTimeTaskRunner" -> "云服务器到期关机";
         case "ServerDeleteTimeTaskRunner" -> "云服务器到期删除";
         case "IndependentEndTimeTaskRunner" -> "独立服务器到期检测";
         case "IndependentDeleteTimeRunner" -> "独立服务器到期删除";
         case "UsdtChainCheckTaskRunner" -> "USDT到账检测";
         case "DomainExpiryTaskRunner" -> "域名到期检测";
         case "DomainTemplateStatusTaskRunner" -> "域名信息模板状态检测";
         case "DomainRegistrationStatusTaskRunner" -> "域名注册状态检测";
         case "ResourcePoolRequestTaskRunner" -> "资源池请求定时任务";
         case "ResourcePoolSyncTaskRunner" -> "资源池业务状态定时任务";
         case "DnsNodeHeartbeatService" -> "DNS节点心跳检测";
         case "DnsNodeTaskDispatchService" -> "DNS节点任务下发";
         case "DnsNodeTaskQueryService" -> "DNS节点任务查询";
         case "DnsPackageInstanceExpireService" -> "DNS套餐到期暂停";
         case "AutoTaskRunner" -> "云服务器自动任务执行";
         case "VhostAutoTaskRunner" -> "虚拟主机自动任务执行";
         case "VhostEndTimeTaskRunner" -> "虚拟主机到期停用";
         case "VhostDeleteTimeTaskRunner" -> "虚拟主机到期删除";
         case "AutoRenewTaskRunner" -> "云服务器/独立服务器自动续费";
         default -> className;
      };
   }
}
