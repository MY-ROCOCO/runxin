package com.rxinyun.admin.system.modular.resource.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.system.modular.resource.entity.SysButton;
import org.apache.ibatis.annotations.Param;

public interface SysButtonMapper extends BaseMapper<SysButton> {
   @InterceptorIgnore(
      tenantLine = "true"
   )
   void deleteIgnoreInterceptor(@Param("ew") LambdaQueryWrapper<SysButton> lambdaQueryWrapper);
}
