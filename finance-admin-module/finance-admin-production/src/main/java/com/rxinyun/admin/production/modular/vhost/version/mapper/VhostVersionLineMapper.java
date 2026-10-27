package com.rxinyun.admin.production.modular.vhost.version.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.production.modular.vhost.version.entity.VhostVersionLine;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface VhostVersionLineMapper extends BaseMapper<VhostVersionLine> {
   @Delete({"DELETE FROM VHOST_VERSION_LINE WHERE VERSION_ID = #{versionId}"})
   int physicalDeleteByVersionId(@Param("versionId") String versionId);
}
