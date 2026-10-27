package com.rxinyun.admin.system.modular.role.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.system.modular.role.entity.SysRole;
import org.apache.ibatis.annotations.Param;

public interface SysRoleMapper extends BaseMapper<SysRole> {
   @InterceptorIgnore(
      tenantLine = "true"
   )
   void deleteIgnoreInterceptor(@Param("ew") LambdaQueryWrapper<SysRole> lambdaQueryWrapper);
}
