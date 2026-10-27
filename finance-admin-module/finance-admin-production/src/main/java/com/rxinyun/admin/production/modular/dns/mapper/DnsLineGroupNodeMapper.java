package com.rxinyun.admin.production.modular.dns.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.production.modular.dns.entity.DnsLineGroupNode;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface DnsLineGroupNodeMapper extends BaseMapper<DnsLineGroupNode> {
   @Delete({"DELETE FROM dns_line_group_node WHERE GROUP_ID = #{groupId}"})
   int physicalDeleteByGroupId(@Param("groupId") String groupId);
}
