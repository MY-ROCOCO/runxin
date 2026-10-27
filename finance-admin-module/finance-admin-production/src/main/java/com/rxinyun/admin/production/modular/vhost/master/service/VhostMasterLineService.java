package com.rxinyun.admin.production.modular.vhost.master.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface VhostMasterLineService {
   void replace(String masterId, List<String> lineIds);

   Map<String, List<String>> groupLineIds(Collection<String> masterIds);

   boolean existsByLineId(String lineId);

   void physicalDeleteByMasterIds(Collection<String> masterIds);
}
