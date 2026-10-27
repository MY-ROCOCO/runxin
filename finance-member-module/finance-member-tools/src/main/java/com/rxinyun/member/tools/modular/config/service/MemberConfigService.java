package com.rxinyun.member.tools.modular.config.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.member.tools.modular.config.entity.MemberConfig;
import com.rxinyun.member.tools.modular.config.param.MemberConfigAddParam;
import com.rxinyun.member.tools.modular.config.param.MemberConfigBatchParam;
import com.rxinyun.member.tools.modular.config.param.MemberConfigEditParam;
import com.rxinyun.member.tools.modular.config.param.MemberConfigIdParam;
import com.rxinyun.member.tools.modular.config.param.MemberConfigListParam;
import com.rxinyun.member.tools.modular.config.param.MemberConfigPageParam;
import java.util.List;

public interface MemberConfigService extends IService<MemberConfig> {
   String getValueByKey(String key);

   Page<MemberConfig> page(MemberConfigPageParam memberConfigPageParam);

   List<MemberConfig> sysBaseList();

   List<MemberConfig> list(MemberConfigListParam memberConfigListParam);

   void add(MemberConfigAddParam memberConfigAddParam);

   void edit(MemberConfigEditParam memberConfigEditParam);

   void delete(List<MemberConfigIdParam> memberConfigIdParamList);

   MemberConfig detail(MemberConfigIdParam memberConfigIdParam);

   MemberConfig queryEntity(String id);

   void editBatch(List<MemberConfigBatchParam> devConfigBatchParamList);
}
