package com.rxinyun.admin.business.modular.dns.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.dns.entity.DnsRecord;
import com.rxinyun.admin.business.modular.dns.param.DnsRecordParam;
import java.util.List;

public interface DnsRecordService {
   Page<DnsRecord> page(DnsRecordParam.PageParam pageParam);

   DnsRecord detail(DnsRecordParam.IdParam idParam);

   void updateStatus(DnsRecordParam.StatusParam statusParam);

   void delete(List<DnsRecordParam.IdParam> idParams);
}
