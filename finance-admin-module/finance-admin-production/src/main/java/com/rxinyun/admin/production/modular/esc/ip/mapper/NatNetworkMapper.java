package com.rxinyun.admin.production.modular.esc.ip.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.ip.entity.NatNetwork;
import com.rxinyun.admin.production.modular.esc.ip.param.NatNetworkPageParam;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface NatNetworkMapper extends BaseMapper<NatNetwork> {
   Page<NatNetwork> selectPage(@Param("page") Page<NatNetwork> page, @Param("pageParam") NatNetworkPageParam pageParam);

   void removeByIds(List<String> list);

   NatNetwork findByIpAddress(String ipAddress);
}
