package com.rxinyun.admin.production.modular.vhost.version.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface VhostVersionLineService {
   void replace(String versionId, List<String> lineIds);

   Map<String, List<String>> groupLineIds(Collection<String> versionIds);

   boolean existsByLineId(String lineId);

   void physicalDeleteByVersionIds(Collection<String> versionIds);
}
