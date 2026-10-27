package com.rxinyun.member.business.modular.dns.service.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.member.business.modular.dns.entity.MemberDnsDomain;
import com.rxinyun.member.business.modular.dns.entity.MemberDnsResolveLog;
import com.rxinyun.member.business.modular.dns.mapper.MemberDnsDomainMapper;
import com.rxinyun.member.business.modular.dns.mapper.MemberDnsResolveLogMapper;
import com.rxinyun.member.business.modular.dns.param.MemberDnsResolveLogParam;
import com.rxinyun.member.business.modular.dns.service.MemberDnsResolveLogService;
import com.rxinyun.production.api.CurrentMemberIdApi;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemberDnsResolveLogServiceImpl extends ServiceImpl<MemberDnsResolveLogMapper, MemberDnsResolveLog> implements MemberDnsResolveLogService {
   @Resource
   private CurrentMemberIdApi currentMemberIdApi;
   @Resource
   private MemberDnsDomainMapper memberDnsDomainMapper;

   @Override
   public Page<MemberDnsResolveLog> page(MemberDnsResolveLogParam.PageParam pageParam) {
      String memberId = this.getCurrentMemberId();
      List<String> ownZoneIds = this.ownZoneIds(memberId);
      if (CollUtil.isEmpty(ownZoneIds)) {
         return new Page();
      } else {
         QueryWrapper<MemberDnsResolveLog> wrapper = new QueryWrapper().checkSqlInjection();
         ((LambdaQueryWrapper<MemberDnsResolveLog>)wrapper.lambda().eq(MemberDnsResolveLog::getMemberId, memberId)).in(MemberDnsResolveLog::getZoneId, ownZoneIds);
         if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
            wrapper.lambda()
               .and(
                  q -> ((LambdaQueryWrapper<MemberDnsResolveLog>)((LambdaQueryWrapper<MemberDnsResolveLog>)((LambdaQueryWrapper<MemberDnsResolveLog>)((LambdaQueryWrapper<MemberDnsResolveLog>)q.like(
                                 MemberDnsResolveLog::getDomainName, pageParam.getSearchKey()
                              ))
                              .or())
                           .like(MemberDnsResolveLog::getRecordName, pageParam.getSearchKey()))
                        .or())
                     .like(MemberDnsResolveLog::getRecordValue, pageParam.getSearchKey())
               );
         }

         if (ObjectUtil.isNotEmpty(pageParam.getZoneId())) {
            this.queryOwnZone(pageParam.getZoneId(), memberId);
            wrapper.lambda().eq(MemberDnsResolveLog::getZoneId, pageParam.getZoneId());
         }

         if (ObjectUtil.isNotEmpty(pageParam.getRecordId())) {
            wrapper.lambda().eq(MemberDnsResolveLog::getRecordId, pageParam.getRecordId());
         }

         if (ObjectUtil.isNotEmpty(pageParam.getDomainName())) {
            wrapper.lambda().eq(MemberDnsResolveLog::getDomainName, pageParam.getDomainName());
         }

         if (ObjectUtil.isNotEmpty(pageParam.getRecordName())) {
            wrapper.lambda().like(MemberDnsResolveLog::getRecordName, pageParam.getRecordName());
         }

         if (ObjectUtil.isNotEmpty(pageParam.getRecordType())) {
            wrapper.lambda().eq(MemberDnsResolveLog::getRecordType, pageParam.getRecordType().toUpperCase());
         }

         if (ObjectUtil.isNotEmpty(pageParam.getOperateType())) {
            wrapper.lambda().eq(MemberDnsResolveLog::getOperateType, pageParam.getOperateType().toUpperCase());
         }

         if (ObjectUtil.isNotEmpty(pageParam.getStartTime())) {
            wrapper.lambda().ge(MemberDnsResolveLog::getOperateTime, pageParam.getStartTime());
         }

         if (ObjectUtil.isNotEmpty(pageParam.getEndTime())) {
            wrapper.lambda().le(MemberDnsResolveLog::getOperateTime, pageParam.getEndTime());
         }

         if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
            CommonSortOrderEnum.validate(pageParam.getSortOrder());
            wrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
         } else {
            wrapper.lambda().orderByDesc(MemberDnsResolveLog::getOperateTime);
         }

         return (Page<MemberDnsResolveLog>)this.page(CommonPageRequest.defaultPage(), wrapper);
      }
   }

   private MemberDnsDomain queryOwnZone(String zoneId, String memberId) {
      MemberDnsDomain zone = (MemberDnsDomain)this.memberDnsDomainMapper
         .selectOne((Wrapper<MemberDnsDomain>)((LambdaQueryWrapper<MemberDnsDomain>)new LambdaQueryWrapper<MemberDnsDomain>().eq(MemberDnsDomain::getId, zoneId)).eq(MemberDnsDomain::getMemberId, memberId));
      if (zone == null) {
         throw new CommonException("DNS托管域名不存在：{}", new Object[]{zoneId});
      } else {
         return zone;
      }
   }

   private List<String> ownZoneIds(String memberId) {
      List<MemberDnsDomain> zones = this.memberDnsDomainMapper.selectList((Wrapper<MemberDnsDomain>)new LambdaQueryWrapper<MemberDnsDomain>().eq(MemberDnsDomain::getMemberId, memberId));
      return CollStreamUtil.toList(zones, MemberDnsDomain::getId);
   }

   private String getCurrentMemberId() {
      String memberId = this.currentMemberIdApi.getCurrentMemberId();
      if (StrUtil.isBlank(memberId)) {
         throw new CommonException("请先登录", new Object[0]);
      } else {
         return memberId;
      }
   }
}
