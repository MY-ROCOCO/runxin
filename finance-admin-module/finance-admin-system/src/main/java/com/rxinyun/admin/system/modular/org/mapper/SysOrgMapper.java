package com.rxinyun.admin.system.modular.org.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.system.modular.org.entity.SysOrg;
import org.apache.ibatis.annotations.Param;

public interface SysOrgMapper extends BaseMapper<SysOrg> {
   @InterceptorIgnore(
      tenantLine = "true"
   )
   void deleteIgnoreInterceptor(@Param("ew") LambdaQueryWrapper<SysOrg> lambdaQueryWrapper);
}
