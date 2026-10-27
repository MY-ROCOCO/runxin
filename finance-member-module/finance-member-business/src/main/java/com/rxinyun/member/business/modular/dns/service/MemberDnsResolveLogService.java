package com.rxinyun.member.business.modular.dns.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.member.business.modular.dns.entity.MemberDnsResolveLog;
import com.rxinyun.member.business.modular.dns.param.MemberDnsResolveLogParam;

public interface MemberDnsResolveLogService extends IService<MemberDnsResolveLog> {
   Page<MemberDnsResolveLog> page(MemberDnsResolveLogParam.PageParam pageParam);
}
