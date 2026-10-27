package com.rxinyun.admin.business.modular.dns.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.business.modular.dns.entity.DnsDomain;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface DnsDomainMapper extends BaseMapper<DnsDomain> {
   @Delete({"<script>DELETE FROM dns_domain WHERE ID IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>"})
   int physicalDeleteByIds(@Param("ids") List<String> ids);
}
