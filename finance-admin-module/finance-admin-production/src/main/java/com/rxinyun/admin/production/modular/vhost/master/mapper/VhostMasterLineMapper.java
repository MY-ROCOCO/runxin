package com.rxinyun.admin.production.modular.vhost.master.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.production.modular.vhost.master.entity.VhostMasterLine;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface VhostMasterLineMapper extends BaseMapper<VhostMasterLine> {
   @Delete({"DELETE FROM VHOST_MASTER_LINE WHERE MASTER_ID = #{masterId}"})
   int physicalDeleteByMasterId(@Param("masterId") String masterId);
}
