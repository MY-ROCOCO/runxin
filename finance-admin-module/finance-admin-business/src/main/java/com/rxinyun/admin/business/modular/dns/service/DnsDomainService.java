package com.rxinyun.admin.business.modular.dns.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.dns.entity.DnsDomain;
import com.rxinyun.admin.business.modular.dns.param.DnsDomainParam;
import com.rxinyun.common.pojo.DnsNsRecordResult;
import java.util.List;

public interface DnsDomainService {
   Page<DnsDomain> page(DnsDomainParam.PageParam pageParam);

   void add(DnsDomainParam.AddParam addParam);

   void edit(DnsDomainParam.EditParam editParam);

   DnsDomain detail(DnsDomainParam.IdParam idParam);

   List<DnsNsRecordResult> nsRecord(DnsDomainParam.NsRecordParam nsRecordParam);

   void updateStatus(DnsDomainParam.StatusParam statusParam);

   void delete(List<DnsDomainParam.IdParam> idParams);
}
