package com.rxinyun.admin.system.modular.resource.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.system.modular.resource.entity.SysModule;
import org.apache.ibatis.annotations.Param;

public interface SysModuleMapper extends BaseMapper<SysModule> {
   @InterceptorIgnore(
      tenantLine = "true"
   )
   void deleteIgnoreInterceptor(@Param("ew") LambdaQueryWrapper<SysModule> lambdaQueryWrapper);
}
