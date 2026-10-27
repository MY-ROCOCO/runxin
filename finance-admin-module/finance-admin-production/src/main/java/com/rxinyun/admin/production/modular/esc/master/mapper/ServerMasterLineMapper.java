package com.rxinyun.admin.production.modular.esc.master.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.production.modular.esc.master.entity.ServerMasterLine;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface ServerMasterLineMapper extends BaseMapper<ServerMasterLine> {
   @Delete({"DELETE FROM server_master_line WHERE MASTER_ID = #{masterId}"})
   int physicalDeleteByMasterId(@Param("masterId") String masterId);
}
