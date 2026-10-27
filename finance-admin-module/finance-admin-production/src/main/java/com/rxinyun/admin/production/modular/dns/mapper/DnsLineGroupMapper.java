package com.rxinyun.admin.production.modular.dns.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.production.modular.dns.entity.DnsLineGroup;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface DnsLineGroupMapper extends BaseMapper<DnsLineGroup> {
   @Delete({"<script>DELETE FROM dns_line_group WHERE ID IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>"})
   int physicalDeleteByIds(@Param("ids") List<String> ids);
}
