package com.rxinyun.member.business.modular.vhost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.member.business.modular.vhost.entity.MemberVhost;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface MemberVhostMapper extends BaseMapper<MemberVhost> {
   @Insert({"INSERT INTO VHOST_AUTO_TASK\n(ID, HOST_ID, TASK_TYPE, TASK_STATUS, TASK_DESC, PLATFORM, RETRY_COUNT, MAX_RETRY, NEXT_RETRY_TIME, DELETE_FLAG, CREATE_TIME)\nSELECT #{id}, #{hostId}, 'CREATE', 1, #{taskDesc}, #{platform}, 0, 3, NOW(), 'NOT_DELETE', NOW()\nFROM DUAL\nWHERE NOT EXISTS (\n    SELECT 1 FROM VHOST_AUTO_TASK t\n    WHERE t.HOST_ID = #{hostId} AND t.TASK_STATUS IN (1, 2) AND t.DELETE_FLAG = 'NOT_DELETE'\n)\n"})
   int insertCreateTask(@Param("id") String id, @Param("hostId") String hostId, @Param("taskDesc") String taskDesc, @Param("platform") String platform);

   @Insert({"INSERT INTO VHOST_AUTO_TASK\n(ID, MASTER_ID, HOST_ID, TASK_TYPE, TASK_STATUS, TASK_DESC, PLATFORM, RETRY_COUNT, MAX_RETRY, NEXT_RETRY_TIME, DELETE_FLAG, CREATE_TIME)\nSELECT #{id}, #{masterId}, #{hostId}, 'RENEW', 1, #{taskDesc}, #{platform}, 0, 3, NOW(), 'NOT_DELETE', NOW()\nFROM DUAL\nWHERE NOT EXISTS (\n    SELECT 1 FROM VHOST_AUTO_TASK t\n    WHERE t.HOST_ID = #{hostId} AND t.TASK_STATUS IN (1, 2) AND t.DELETE_FLAG = 'NOT_DELETE'\n)\n"})
   int insertRenewTask(
      @Param("id") String id,
      @Param("hostId") String hostId,
      @Param("masterId") String masterId,
      @Param("taskDesc") String taskDesc,
      @Param("platform") String platform
   );

   @Insert({"INSERT INTO VHOST_AUTO_TASK\n(ID, MASTER_ID, HOST_ID, TASK_TYPE, TASK_STATUS, TASK_DESC, PLATFORM, RETRY_COUNT, MAX_RETRY, NEXT_RETRY_TIME, DELETE_FLAG, CREATE_TIME)\nSELECT #{id}, #{masterId}, #{hostId}, #{taskType}, 1, #{taskDesc}, #{platform}, 0, 3, NOW(), 'NOT_DELETE', NOW()\nFROM DUAL\nWHERE NOT EXISTS (\n    SELECT 1 FROM VHOST_AUTO_TASK t\n    WHERE t.HOST_ID = #{hostId} AND t.TASK_STATUS IN (1, 2) AND t.DELETE_FLAG = 'NOT_DELETE'\n)\n"})
   int insertOperateTask(
      @Param("id") String id,
      @Param("hostId") String hostId,
      @Param("masterId") String masterId,
      @Param("taskType") String taskType,
      @Param("taskDesc") String taskDesc,
      @Param("platform") String platform
   );

   @Insert({"INSERT INTO VHOST_AUTO_TASK\n(ID, MASTER_ID, HOST_ID, TASK_TYPE, TASK_STATUS, TASK_DESC, PLATFORM, RETRY_COUNT, MAX_RETRY, NEXT_RETRY_TIME, DELETE_FLAG, CREATE_TIME)\nSELECT #{id}, #{masterId}, #{hostId}, 'DELETE', 1, #{taskDesc}, #{platform}, 0, 3, NOW(), 'NOT_DELETE', NOW()\nFROM DUAL\nWHERE NOT EXISTS (\n    SELECT 1 FROM VHOST_AUTO_TASK t\n    WHERE t.HOST_ID = #{hostId} AND t.TASK_TYPE = 'DELETE'\n      AND t.TASK_STATUS IN (1, 2) AND t.DELETE_FLAG = 'NOT_DELETE'\n)\n"})
   int insertDeleteTask(
      @Param("id") String id,
      @Param("hostId") String hostId,
      @Param("masterId") String masterId,
      @Param("taskDesc") String taskDesc,
      @Param("platform") String platform
   );

   @Select({"SELECT ID AS id, CONTROLLED_VERSION AS controlledVersion, PANEL_URL AS panelUrl, API_KEY AS apiKey\nFROM VHOST_MASTER\nWHERE DELETE_FLAG = 'NOT_DELETE' AND ID = #{id}\n"})
   Map<String, Object> selectMasterById(@Param("id") String id);

   @Select({"SELECT ID AS id, NAME AS name, PHP_VERSIONS AS phpVersions, MYSQL_VERSIONS AS mysqlVersions,\n       TRAFFIC_PRICE AS trafficPrice\nFROM VHOST_VERSION\nWHERE DELETE_FLAG = 'NOT_DELETE' AND ID = #{id}\n"})
   Map<String, Object> selectVersionById(@Param("id") String id);

   @Select({"<script>\nSELECT ID AS id, NAME AS name, PHP_VERSIONS AS phpVersions, MYSQL_VERSIONS AS mysqlVersions,\n       TRAFFIC_PRICE AS trafficPrice\nFROM VHOST_VERSION\nWHERE DELETE_FLAG = 'NOT_DELETE' AND ID IN\n<foreach collection=\"ids\" item=\"id\" open=\"(\" separator=\",\" close=\")\">\n    #{id}\n</foreach>\n</script>\n"})
   List<Map<String, Object>> selectVersionByIds(@Param("ids") Collection<String> ids);
}
