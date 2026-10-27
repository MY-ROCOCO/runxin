package com.rxinyun.admin.notice.modular.service;

import com.rxinyun.admin.notice.modular.domain.dto.TabEmailSmsParam;
import com.rxinyun.admin.notice.modular.domain.dto.TemplateTabDTO;
import com.rxinyun.admin.notice.modular.domain.entity.TemplateTab;
import java.util.List;

public interface TabService {
   void add(TemplateTabDTO a);

   void edit(TemplateTabDTO a);

   void save(List<TabEmailSmsParam> list);

   TemplateTab getById(String id);

   List<TemplateTab> tabList();
}
