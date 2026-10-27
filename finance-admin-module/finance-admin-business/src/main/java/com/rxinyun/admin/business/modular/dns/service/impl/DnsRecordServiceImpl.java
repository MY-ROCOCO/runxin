package com.rxinyun.admin.business.modular.dns.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.dns.entity.DnsDomain;
import com.rxinyun.admin.business.modular.dns.entity.DnsRecord;
import com.rxinyun.admin.business.modular.dns.entity.DnsResolveLog;
import com.rxinyun.admin.business.modular.dns.mapper.DnsDomainMapper;
import com.rxinyun.admin.business.modular.dns.mapper.DnsRecordMapper;
import com.rxinyun.admin.business.modular.dns.mapper.DnsResolveLogMapper;
import com.rxinyun.admin.business.modular.dns.param.DnsRecordParam;
import com.rxinyun.admin.business.modular.dns.service.DnsRecordService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.production.api.DnsPackageInstanceApi;
import com.rxinyun.production.api.dto.DnsRecordCommandParam;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DnsRecordServiceImpl extends ServiceImpl<DnsRecordMapper, DnsRecord> implements DnsRecordService {
   private static final Logger log = LoggerFactory.getLogger(DnsRecordServiceImpl.class);
   private static final int ENABLED = 1;
   private static final int DISABLED = 0;
   private static final Set<String> SUPPORTED_TYPES = Set.of("A", "AAAA", "CNAME", "MX", "NS", "PTR", "TXT");
   @Resource
   private DnsDomainMapper dnsDomainMapper;
   @Resource
   private DnsPackageInstanceApi dnsPackageInstanceApi;
   @Resource
   private DnsResolveLogMapper dnsResolveLogMapper;

   @Override
   public Page<DnsRecord> page(DnsRecordParam.PageParam pageParam) {
      QueryWrapper<DnsRecord> wrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         wrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<DnsRecord>)((LambdaQueryWrapper<DnsRecord>)((LambdaQueryWrapper<DnsRecord>)((LambdaQueryWrapper<DnsRecord>)q.like(DnsRecord::getHost, pageParam.getSearchKey()))
                           .or())
                        .like(DnsRecord::getName, pageParam.getSearchKey()))
                     .or())
                  .like(DnsRecord::getValue, pageParam.getSearchKey())
            );
      }

      if (ObjectUtil.isNotEmpty(pageParam.getZoneId())) {
         wrapper.lambda().eq(DnsRecord::getZoneId, pageParam.getZoneId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getType())) {
         wrapper.lambda().eq(DnsRecord::getType, this.normalizeType(pageParam.getType()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getEnabled())) {
         wrapper.lambda().eq(DnsRecord::getEnabled, pageParam.getEnabled());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         wrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         wrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<DnsRecord>)this.page(CommonPageRequest.defaultPage(), wrapper);
   }

   @Override
   public DnsRecord detail(DnsRecordParam.IdParam idParam) {
      return this.queryEntity(idParam.getId());
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void updateStatus(DnsRecordParam.StatusParam statusParam) {
      DnsRecord record = this.queryEntity(statusParam.getId());
      String beforeContent = JSONUtil.toJsonStr(record);
      record.setEnabled(this.normalizeEnabled(statusParam.getEnabled()));
      this.updateById(record);
      DnsDomain zone = this.queryZone(record.getZoneId());
      this.saveResolveLog("STATUS", zone, record, beforeContent, JSONUtil.toJsonStr(record));
      this.safeDnsAutomation("DNS解析记录状态任务创建失败：" + record.getId(), () -> this.dnsPackageInstanceApi.setRecordEnabled(this.buildRecordCommandParam(record, zone)));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<DnsRecordParam.IdParam> idParams) {
      List<String> ids = CollStreamUtil.toList(idParams, DnsRecordParam.IdParam::getId);
      if (!CollUtil.isEmpty(ids)) {
         for (String id : ids) {
            DnsRecord record = this.queryEntity(id);
            DnsDomain zone = this.queryZone(record.getZoneId());
            this.saveResolveLog("DELETE", zone, record, JSONUtil.toJsonStr(record), null);
            this.safeDnsAutomation(
               "DNS解析记录删除任务创建失败：" + record.getId(), () -> this.dnsPackageInstanceApi.deleteRecord(this.buildRecordCommandParam(record, zone))
            );
         }

         ((DnsRecordMapper)this.baseMapper).physicalDeleteByIds(ids);
      }
   }

   private DnsRecord queryEntity(String id) {
      DnsRecord record = (DnsRecord)this.getById(id);
      if (record == null) {
         throw new CommonException("DNS解析记录不存在：{}", new Object[]{id});
      } else {
         return record;
      }
   }

   private DnsRecordCommandParam buildRecordCommandParam(DnsRecord record) {
      return this.buildRecordCommandParam(record, this.queryZone(record.getZoneId()));
   }

   private DnsRecordCommandParam buildRecordCommandParam(DnsRecord record, DnsDomain zone) {
      DnsRecordCommandParam param = new DnsRecordCommandParam();
      param.setRecordId(record.getId());
      param.setZoneId(record.getZoneId());
      param.setPackageInstanceId(zone.getPackageInstanceId());
      param.setName(record.getName());
      param.setType(record.getType());
      param.setValue(record.getValue());
      param.setTtl(record.getTtl());
      param.setPriority(record.getPriority());
      param.setEnabled(Integer.valueOf(1).equals(record.getEnabled()));
      param.setLine(record.getLine());
      param.setCountry(record.getCountry());
      param.setRegion(record.getRegion());
      param.setProvince(record.getProvince());
      param.setCity(record.getCity());
      param.setIsp(record.getIsp());
      return param;
   }

   private DnsDomain queryZone(String zoneId) {
      DnsDomain zone = (DnsDomain)this.dnsDomainMapper.selectById(zoneId);
      if (zone == null) {
         throw new CommonException("DNS托管域名不存在：{}", new Object[]{zoneId});
      } else {
         return zone;
      }
   }

   private void saveResolveLog(String operateType, DnsDomain zone, DnsRecord record, String beforeContent, String afterContent) {
      DnsResolveLog resolveLog = new DnsResolveLog();
      resolveLog.setId(IdUtil.getSnowflakeNextIdStr());
      resolveLog.setMemberId(zone.getMemberId());
      resolveLog.setZoneId(record.getZoneId());
      resolveLog.setRecordId(record.getId());
      resolveLog.setDomainName(zone.getDomainName());
      resolveLog.setRecordName(record.getName());
      resolveLog.setRecordType(record.getType());
      resolveLog.setRecordValue(record.getValue());
      resolveLog.setTtl(record.getTtl());
      resolveLog.setPriority(record.getPriority());
      resolveLog.setLine(record.getLine());
      resolveLog.setEnabled(record.getEnabled());
      resolveLog.setOperateType(operateType);
      resolveLog.setOperateSource("ADMIN");
      resolveLog.setOperateUser(this.getCurrentAdminId());
      resolveLog.setBeforeContent(beforeContent);
      resolveLog.setAfterContent(afterContent);
      resolveLog.setOperateTime(new Date());
      this.dnsResolveLogMapper.insert(resolveLog);
   }

   private String getCurrentAdminId() {
      try {
         return StpUtil.getLoginIdAsString();
      } catch (Exception var2) {
         return null;
      }
   }

   private void safeDnsAutomation(String message, Runnable runnable) {
      try {
         runnable.run();
      } catch (Exception var4) {
         log.warn("{}，error={}", message, var4.getMessage());
      }
   }

   private String normalizeType(String type) {
      String value = StrUtil.trim(type).toUpperCase(Locale.ROOT);
      if (!SUPPORTED_TYPES.contains(value)) {
         throw new CommonException("解析记录类型不支持：{}", new Object[]{type});
      } else {
         return value;
      }
   }

   private Integer normalizeEnabled(Integer enabled) {
      if (!Integer.valueOf(1).equals(enabled) && !Integer.valueOf(0).equals(enabled)) {
         throw new CommonException("启用状态只能是1启用或0暂停", new Object[0]);
      } else {
         return enabled;
      }
   }
}
