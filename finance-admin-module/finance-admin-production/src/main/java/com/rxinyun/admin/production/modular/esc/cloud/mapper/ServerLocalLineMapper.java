package com.rxinyun.admin.production.modular.esc.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.cloud.entity.ServerLocalLine;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLinePageParam;
import org.apache.ibatis.annotations.Param;

public interface ServerLocalLineMapper extends BaseMapper<ServerLocalLine> {
   Page<ServerLocalLine> selectPage(@Param("page") Page<ServerLocalLine> page, @Param("pageParam") ServerLocalLinePageParam pageParam);
}
