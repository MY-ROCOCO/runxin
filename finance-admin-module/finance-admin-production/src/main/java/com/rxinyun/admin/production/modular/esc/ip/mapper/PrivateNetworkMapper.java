package com.rxinyun.admin.production.modular.esc.ip.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.ip.entity.PrivateNetwork;
import com.rxinyun.admin.production.modular.esc.ip.param.PrivateNetworkPageParam;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PrivateNetworkMapper extends BaseMapper<PrivateNetwork> {
   Page<PrivateNetwork> selectPage(@Param("page") Page<PrivateNetwork> page, @Param("pageParam") PrivateNetworkPageParam pageParam);

   void removeByIds(List<String> list);

   PrivateNetwork findByIpAddress(String ipAddress);
}
