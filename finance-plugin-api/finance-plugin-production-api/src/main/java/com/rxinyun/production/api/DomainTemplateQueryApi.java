package com.rxinyun.production.api;

import java.util.List;
import java.util.Map;

public interface DomainTemplateQueryApi {
   Map<String, String> getTemplateNamesByIds(List<String> templateIds);
}
