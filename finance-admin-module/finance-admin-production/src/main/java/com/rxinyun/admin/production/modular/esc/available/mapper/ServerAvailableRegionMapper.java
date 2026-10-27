package com.rxinyun.admin.production.modular.esc.available.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.available.entity.ServerAvailableRegion;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionPageParam;
import org.apache.ibatis.annotations.Param;

public interface ServerAvailableRegionMapper extends BaseMapper<ServerAvailableRegion> {
   Page<ServerAvailableRegion> selectPage(@Param("page") Page<ServerAvailableRegion> page, @Param("pageParam") ServerAvailableRegionPageParam pageParam);
}
