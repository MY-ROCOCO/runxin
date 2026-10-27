package com.rxinyun.member.business.modular.independent.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.OperateTypeEnum;
import com.rxinyun.common.enums.ProcessingStatusEnum;
import com.rxinyun.member.business.modular.independent.async.DedicatedServerManualWorkNotificationPublisher;
import com.rxinyun.member.business.modular.independent.entity.MemberIndependent;
import com.rxinyun.member.business.modular.independent.entity.MemberIndependentWork;
import com.rxinyun.member.business.modular.independent.mapper.MemberIndependentMapper;
import com.rxinyun.member.business.modular.independent.mapper.MemberIndependentWorkMapper;
import com.rxinyun.member.business.modular.independent.param.IndependentWorkAddParam;
import com.rxinyun.member.business.modular.independent.service.MemberIndependentWorkService;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberIndependentWorkServiceImpl extends ServiceImpl<MemberIndependentWorkMapper, MemberIndependentWork> implements MemberIndependentWorkService {
   private static final Logger log = LoggerFactory.getLogger(MemberIndependentWorkServiceImpl.class);
   @Resource
   private DedicatedServerManualWorkNotificationPublisher dedicatedServerManualWorkNotificationPublisher;
   @Resource
   private MemberIndependentMapper memberIndependentMapper;

   @Override
   public void add(IndependentWorkAddParam independentWorkAddParam) {
      MemberIndependentWork independentWork = (MemberIndependentWork)BeanUtil.toBean(independentWorkAddParam, MemberIndependentWork.class);
      this.save(independentWork);
      String independentIp = null;
      if (StrUtil.isNotBlank(independentWorkAddParam.getIndependentId())) {
         MemberIndependent independent = (MemberIndependent)this.memberIndependentMapper.selectById(independentWorkAddParam.getIndependentId());
         if (independent != null) {
            independentIp = independent.getServerIp();
         }
      }

      this.dedicatedServerManualWorkNotificationPublisher
         .publishAfterWorkCreated(independentWorkAddParam.getIndependentId(), independentIp, independentWorkAddParam.getOperateType());
   }

   @Override
   public boolean isExistWorkingTask(String independentId) {
      MemberIndependentWork independentWork = new MemberIndependentWork();
      independentWork.setIndependentId(independentId);
      independentWork.setBusinessStatus(0);
      List<MemberIndependentWork> independentWorkList = this.list(new LambdaQueryWrapper(independentWork));
      return independentWorkList.size() > 0;
   }

   @Override
   public void closePendingExpireStopWorks(String independentId) {
      if (!StrUtil.isBlank(independentId)) {
         boolean closed = this.update(
            (Wrapper<MemberIndependentWork>)((LambdaUpdateWrapper<MemberIndependentWork>)((LambdaUpdateWrapper<MemberIndependentWork>)((LambdaUpdateWrapper<MemberIndependentWork>)((LambdaUpdateWrapper<MemberIndependentWork>)new LambdaUpdateWrapper<MemberIndependentWork>()
                           .eq(MemberIndependentWork::getIndependentId, independentId))
                        .eq(MemberIndependentWork::getOperateType, OperateTypeEnum.EXPIRE_STOP.getCode()))
                     .in(
                        MemberIndependentWork::getBusinessStatus,
                        new Object[]{ProcessingStatusEnum.UNPROCESSED.getCode(), ProcessingStatusEnum.PROCESSING.getCode()}
                     ))
                  .set(MemberIndependentWork::getBusinessStatus, ProcessingStatusEnum.PROCESSING_COMPLETE.getCode()))
               .set(MemberIndependentWork::getCompleteTime, new Date())
         );
         if (closed) {
            log.info("续费后已关闭未完成的到期关机工单 independentId={}", independentId);
         }
      }
   }
}
