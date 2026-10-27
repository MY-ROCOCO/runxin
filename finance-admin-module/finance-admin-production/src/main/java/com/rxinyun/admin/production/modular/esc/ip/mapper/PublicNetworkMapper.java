package com.rxinyun.admin.production.modular.esc.ip.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.ip.entity.PublicNetwork;
import com.rxinyun.admin.production.modular.esc.ip.param.PublicNetworkPageParam;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PublicNetworkMapper extends BaseMapper<PublicNetwork> {
   Page<PublicNetwork> selectPage(@Param("page") Page<PublicNetwork> page, @Param("pageParam") PublicNetworkPageParam pageParam);

   void removeByIds(List<String> list);

   PublicNetwork findByIpAddress(String ipAddress);
}
