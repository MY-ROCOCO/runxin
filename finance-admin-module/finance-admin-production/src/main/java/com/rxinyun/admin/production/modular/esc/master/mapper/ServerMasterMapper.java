package com.rxinyun.admin.production.modular.esc.master.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.master.entity.ServerMaster;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterPageParam;
import org.apache.ibatis.annotations.Param;

public interface ServerMasterMapper extends BaseMapper<ServerMaster> {
   Page<ServerMaster> selectPage(@Param("page") Page<ServerMaster> page, @Param("pageParam") ServerMasterPageParam pageParam);
}
