package com.rxinyun.admin.production.modular.esc.master.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.production.modular.esc.master.entity.ServerMasterLine;
import java.util.List;

public interface ServerMasterLineService extends IService<ServerMasterLine> {
   void replaceMasterLines(String masterId, List<String> lineIds);

   List<String> listLineIdsByMasterId(String masterId);

   boolean existsByLineId(String lineId);
}
