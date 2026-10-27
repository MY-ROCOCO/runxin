package com.rxinyun.admin.business.modular.vhost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.business.modular.vhost.entity.AdminVhost;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AdminVhostMapper extends BaseMapper<AdminVhost> {
   @Select({"SELECT ID AS id, NAME AS name, PHP_VERSIONS AS phpVersions, MYSQL_VERSIONS AS mysqlVersions,\n       TRAFFIC_PRICE AS trafficPrice\nFROM VHOST_VERSION\nWHERE DELETE_FLAG = 'NOT_DELETE' AND ID = #{id}\n"})
   Map<String, Object> selectVersionById(@Param("id") String id);

   @Select({"<script>\nSELECT ID AS id, NAME AS name, PHP_VERSIONS AS phpVersions, MYSQL_VERSIONS AS mysqlVersions,\n       TRAFFIC_PRICE AS trafficPrice\nFROM VHOST_VERSION\nWHERE DELETE_FLAG = 'NOT_DELETE' AND ID IN\n<foreach collection=\"ids\" item=\"id\" open=\"(\" separator=\",\" close=\")\">\n    #{id}\n</foreach>\n</script>\n"})
   List<Map<String, Object>> selectVersionByIds(@Param("ids") Collection<String> ids);

   @Select({"<script>\nSELECT ID AS id, NAME AS name\nFROM VHOST_MASTER\nWHERE DELETE_FLAG = 'NOT_DELETE' AND ID IN\n<foreach collection=\"ids\" item=\"id\" open=\"(\" separator=\",\" close=\")\">\n    #{id}\n</foreach>\n</script>\n"})
   List<Map<String, Object>> selectMasterByIds(@Param("ids") Collection<String> ids);
}
