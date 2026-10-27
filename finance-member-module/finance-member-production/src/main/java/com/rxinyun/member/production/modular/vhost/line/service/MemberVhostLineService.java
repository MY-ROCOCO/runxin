package com.rxinyun.member.production.modular.vhost.line.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.vhost.line.entity.MemberVhostLine;
import com.rxinyun.member.production.modular.vhost.line.param.MemberVhostLinePageParam;

public interface MemberVhostLineService {
   Page<MemberVhostLine> page(MemberVhostLinePageParam pageParam);
}
