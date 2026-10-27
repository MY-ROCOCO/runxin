package com.rxinyun.admin.production.modular.dns.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.production.modular.dns.entity.DnsPackageInstance;
import com.rxinyun.admin.production.modular.dns.vo.DnsNodeReplaceConfigRecord;
import com.rxinyun.admin.production.modular.dns.vo.DnsNodeReplaceConfigZone;
import com.rxinyun.production.api.dto.DnsBoundDomainVO;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface DnsPackageInstanceMapper extends BaseMapper<DnsPackageInstance> {
   @Update({"UPDATE dns_package_instance SET USED_QUOTA = USED_QUOTA + 1 WHERE ID = #{id} AND MEMBER_ID = #{memberId} AND STATUS = 1 AND DELETE_FLAG = 'NOT_DELETE' AND USED_QUOTA < DOMAIN_QUOTA"})
   int occupyQuota(@Param("id") String id, @Param("memberId") String memberId);

   @Update({"UPDATE dns_package_instance SET USED_QUOTA = USED_QUOTA - 1 WHERE ID = #{id} AND DELETE_FLAG = 'NOT_DELETE' AND USED_QUOTA > 0"})
   int releaseQuota(@Param("id") String id);

   @Delete({"<script>DELETE FROM dns_package_instance WHERE ID IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>"})
   int physicalDeleteByIds(@Param("ids") List<String> ids);

   @Select({"<script>SELECT ID AS id, PACKAGE_INSTANCE_ID AS packageInstanceId, DOMAIN_NAME AS domainName, EMAIL AS email, STATUS AS status FROM dns_domain WHERE DELETE_FLAG = 'NOT_DELETE' AND PACKAGE_INSTANCE_ID IN <foreach collection='instanceIds' item='id' open='(' separator=',' close=')'>#{id}</foreach> ORDER BY CREATE_TIME DESC</script>"})
   List<DnsBoundDomainVO> selectBoundDomainsByInstanceIds(@Param("instanceIds") List<String> instanceIds);

   @Select({"SELECT d.ID AS zoneId,\n       d.DOMAIN_NAME AS name,\n       d.EMAIL AS adminEmail,\n       COALESCE(pi.MIN_TTL, p.MIN_TTL, 300) AS soaTtl,\n       60 AS negativeTtl,\n       p.PRIMARY_NS AS primaryNs,\n       p.SECONDARY_NS AS secondaryNs\nFROM dns_domain d\nINNER JOIN dns_package_instance pi ON pi.ID = d.PACKAGE_INSTANCE_ID AND pi.DELETE_FLAG = 'NOT_DELETE'\nINNER JOIN dns_package p ON p.ID = pi.PACKAGE_ID AND p.DELETE_FLAG = 'NOT_DELETE'\nINNER JOIN dns_line_group_node lgn ON lgn.GROUP_ID = p.LINE_GROUP_ID AND lgn.DELETE_FLAG = 'NOT_DELETE'\nWHERE d.DELETE_FLAG = 'NOT_DELETE'\n  AND d.STATUS = 1\n  AND lgn.NODE_ID = #{nodeId}\nORDER BY d.CREATE_TIME ASC\n"})
   List<DnsNodeReplaceConfigZone> selectReplaceConfigZonesByNodeId(@Param("nodeId") String nodeId);

   @Select({"<script>\nSELECT r.ID AS recordId,\n       r.ZONE_ID AS zoneId,\n       r.NAME AS name,\n       r.TYPE AS type,\n       r.VALUE AS value,\n       r.TTL AS ttl,\n       r.PRIORITY AS priority,\n       CASE WHEN r.ENABLED = 1 THEN TRUE ELSE FALSE END AS enabled,\n       r.LINE AS line,\n       r.COUNTRY AS country,\n       r.REGION AS region,\n       r.PROVINCE AS province,\n       r.CITY AS city,\n       r.ISP AS isp\nFROM dns_record r\nWHERE r.DELETE_FLAG = 'NOT_DELETE'\n  AND r.ZONE_ID IN <foreach collection='zoneIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>\nORDER BY r.CREATE_TIME ASC\n</script>\n"})
   List<DnsNodeReplaceConfigRecord> selectReplaceConfigRecordsByZoneIds(@Param("zoneIds") List<String> zoneIds);

   @Select({"SELECT * FROM dns_package_instance WHERE STATUS = 1 AND NEXT_DUE_TIME <= #{now} AND DELETE_FLAG = 'NOT_DELETE' ORDER BY NEXT_DUE_TIME ASC LIMIT #{limit}"})
   List<DnsPackageInstance> selectExpiredActiveInstances(@Param("now") Date now, @Param("limit") int limit);

   @Update({"UPDATE dns_package_instance SET STATUS = -1, UPDATE_TIME = NOW() WHERE ID = #{id} AND STATUS = 1 AND DELETE_FLAG = 'NOT_DELETE'"})
   int markExpired(@Param("id") String id);

   @Update({"UPDATE dns_domain SET STATUS = 0, UPDATE_TIME = NOW() WHERE PACKAGE_INSTANCE_ID = #{instanceId} AND DELETE_FLAG = 'NOT_DELETE'"})
   int pauseDomainsByInstanceId(@Param("instanceId") String instanceId);

   @Update({"UPDATE dns_domain SET STATUS = 1, UPDATE_TIME = NOW() WHERE PACKAGE_INSTANCE_ID = #{instanceId} AND DELETE_FLAG = 'NOT_DELETE'"})
   int enableDomainsByInstanceId(@Param("instanceId") String instanceId);
}
