package com.rxinyun.admin.business.modular.vhost.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.business.modular.vhost.task.entity.VhostAutoTask;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface VhostAutoTaskMapper extends BaseMapper<VhostAutoTask> {
   @Select({"<script>\nSELECT ID AS id, NAME AS name\nFROM VHOST_MASTER\nWHERE DELETE_FLAG = 'NOT_DELETE'\n  AND ID IN\n  <foreach collection=\"ids\" item=\"id\" open=\"(\" separator=\",\" close=\")\">\n    #{id}\n  </foreach>\n</script>\n"})
   List<Map<String, Object>> selectMasterNames(@Param("ids") Collection<String> ids);

   @Select({"SELECT m.ID AS id, m.CONTROLLED_VERSION AS controlledVersion, m.PANEL_URL AS panelUrl,\n       m.API_KEY AS apiKey, m.FTP_ADDRESS AS ftpAddress, m.WEIGHT AS weight\nFROM VHOST_MASTER m\nINNER JOIN VHOST_MASTER_LINE ml ON ml.MASTER_ID = m.ID AND ml.DELETE_FLAG = 'NOT_DELETE'\nWHERE m.DELETE_FLAG = 'NOT_DELETE' AND m.STATUS = 1 AND ml.LINE_ID = #{lineId}\n"})
   List<Map<String, Object>> selectEnabledMastersByLine(@Param("lineId") String lineId);

   @Select({"SELECT ID AS id, CONTROLLED_VERSION AS controlledVersion, PANEL_URL AS panelUrl,\n       API_KEY AS apiKey, FTP_ADDRESS AS ftpAddress, WEIGHT AS weight\nFROM VHOST_MASTER\nWHERE DELETE_FLAG = 'NOT_DELETE' AND ID = #{id}\n"})
   Map<String, Object> selectMasterById(@Param("id") String id);

   @Insert({"INSERT INTO VHOST_AUTO_TASK\n(ID, MASTER_ID, HOST_ID, TASK_TYPE, TASK_STATUS, TASK_DESC, PLATFORM, RETRY_COUNT, MAX_RETRY, NEXT_RETRY_TIME, DELETE_FLAG, CREATE_TIME)\nSELECT #{id}, #{masterId}, #{hostId}, #{taskType}, 1, #{taskDesc}, #{platform}, 0, 3, NOW(), 'NOT_DELETE', NOW()\nFROM DUAL\nWHERE NOT EXISTS (\n    SELECT 1 FROM VHOST_AUTO_TASK t\n    WHERE t.HOST_ID = #{hostId} AND t.TASK_STATUS IN (1, 2) AND t.DELETE_FLAG = 'NOT_DELETE'\n)\n"})
   int insertOperateTask(
      @Param("id") String id,
      @Param("hostId") String hostId,
      @Param("masterId") String masterId,
      @Param("taskType") String taskType,
      @Param("taskDesc") String taskDesc,
      @Param("platform") String platform
   );
}
