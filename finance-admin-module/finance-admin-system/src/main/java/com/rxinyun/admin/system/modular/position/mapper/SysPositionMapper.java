package com.rxinyun.admin.system.modular.position.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.system.modular.position.entity.SysPosition;
import org.apache.ibatis.annotations.Param;

public interface SysPositionMapper extends BaseMapper<SysPosition> {
   @InterceptorIgnore(
      tenantLine = "true"
   )
   void deleteIgnoreInterceptor(@Param("ew") LambdaQueryWrapper<SysPosition> lambdaQueryWrapper);
}
