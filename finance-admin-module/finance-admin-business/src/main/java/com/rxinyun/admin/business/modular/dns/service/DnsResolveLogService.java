package com.rxinyun.admin.business.modular.dns.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.business.modular.dns.entity.DnsResolveLog;
import com.rxinyun.admin.business.modular.dns.param.DnsResolveLogParam;

public interface DnsResolveLogService extends IService<DnsResolveLog> {
   Page<DnsResolveLog> page(DnsResolveLogParam.PageParam pageParam);
}
