package com.rxinyun.member.business.modular.dns.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.member.business.modular.dns.entity.MemberDnsRecord;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface MemberDnsRecordMapper extends BaseMapper<MemberDnsRecord> {
   @Delete({"<script>DELETE FROM dns_record WHERE ID IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>"})
   int physicalDeleteByIds(@Param("ids") List<String> ids);

   @Delete({"<script>DELETE FROM dns_record WHERE ZONE_ID IN <foreach collection='zoneIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>"})
   int physicalDeleteByZoneIds(@Param("zoneIds") List<String> zoneIds);
}
