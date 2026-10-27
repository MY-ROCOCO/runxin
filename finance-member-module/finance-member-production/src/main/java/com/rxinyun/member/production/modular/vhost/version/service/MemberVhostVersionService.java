package com.rxinyun.member.production.modular.vhost.version.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.vhost.version.entity.MemberVhostVersion;
import com.rxinyun.member.production.modular.vhost.version.param.MemberVhostVersionPageParam;

public interface MemberVhostVersionService {
   Page<MemberVhostVersion> page(MemberVhostVersionPageParam pageParam);
}
