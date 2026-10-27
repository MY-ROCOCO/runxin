package com.rxinyun.admin.system.modular.user.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.system.modular.user.entity.SysUser;
import org.apache.ibatis.annotations.Param;

public interface SysUserMapper extends BaseMapper<SysUser> {
   @InterceptorIgnore(
      tenantLine = "true"
   )
   void deleteIgnoreInterceptor(@Param("ew") LambdaQueryWrapper<SysUser> lambdaQueryWrapper);
}
