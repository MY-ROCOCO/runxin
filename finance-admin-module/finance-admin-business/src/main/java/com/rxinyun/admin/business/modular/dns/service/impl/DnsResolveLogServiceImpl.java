package com.rxinyun.admin.business.modular.dns.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.dns.entity.DnsResolveLog;
import com.rxinyun.admin.business.modular.dns.mapper.DnsResolveLogMapper;
import com.rxinyun.admin.business.modular.dns.param.DnsResolveLogParam;
import com.rxinyun.admin.business.modular.dns.service.DnsResolveLogService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.page.CommonPageRequest;
import org.springframework.stereotype.Service;

@Service
public class DnsResolveLogServiceImpl extends ServiceImpl<DnsResolveLogMapper, DnsResolveLog> implements DnsResolveLogService {
   @Override
   public Page<DnsResolveLog> page(DnsResolveLogParam.PageParam pageParam) {
      QueryWrapper<DnsResolveLog> wrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         wrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<DnsResolveLog>)((LambdaQueryWrapper<DnsResolveLog>)((LambdaQueryWrapper<DnsResolveLog>)((LambdaQueryWrapper<DnsResolveLog>)q.like(
                              DnsResolveLog::getDomainName, pageParam.getSearchKey()
                           ))
                           .or())
                        .like(DnsResolveLog::getRecordName, pageParam.getSearchKey()))
                     .or())
                  .like(DnsResolveLog::getRecordValue, pageParam.getSearchKey())
            );
      }

      if (ObjectUtil.isNotEmpty(pageParam.getMemberId())) {
         wrapper.lambda().eq(DnsResolveLog::getMemberId, pageParam.getMemberId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getZoneId())) {
         wrapper.lambda().eq(DnsResolveLog::getZoneId, pageParam.getZoneId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getRecordId())) {
         wrapper.lambda().eq(DnsResolveLog::getRecordId, pageParam.getRecordId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getDomainName())) {
         wrapper.lambda().eq(DnsResolveLog::getDomainName, pageParam.getDomainName());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getRecordName())) {
         wrapper.lambda().like(DnsResolveLog::getRecordName, pageParam.getRecordName());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getRecordType())) {
         wrapper.lambda().eq(DnsResolveLog::getRecordType, pageParam.getRecordType().toUpperCase());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getOperateType())) {
         wrapper.lambda().eq(DnsResolveLog::getOperateType, pageParam.getOperateType().toUpperCase());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStartTime())) {
         wrapper.lambda().ge(DnsResolveLog::getOperateTime, pageParam.getStartTime());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getEndTime())) {
         wrapper.lambda().le(DnsResolveLog::getOperateTime, pageParam.getEndTime());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         wrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         wrapper.lambda().orderByDesc(DnsResolveLog::getOperateTime);
      }

      return (Page<DnsResolveLog>)this.page(CommonPageRequest.defaultPage(), wrapper);
   }
}
