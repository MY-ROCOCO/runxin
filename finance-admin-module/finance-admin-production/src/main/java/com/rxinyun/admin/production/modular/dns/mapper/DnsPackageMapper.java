package com.rxinyun.admin.production.modular.dns.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.production.modular.dns.entity.DnsPackage;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface DnsPackageMapper extends BaseMapper<DnsPackage> {
   @Delete({"<script>DELETE FROM dns_package WHERE ID IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>"})
   int physicalDeleteByIds(@Param("ids") List<String> ids);
}
