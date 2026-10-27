package com.rxinyun.production.api;

import java.util.List;

public interface DomainTemplateTaskApi {
   List<String> listAuditingTemplateIds();

   void syncTemplateStatusByTemplateId(String templateId);
}
