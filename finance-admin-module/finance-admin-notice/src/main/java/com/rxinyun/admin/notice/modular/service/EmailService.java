package com.rxinyun.admin.notice.modular.service;

import com.rxinyun.admin.notice.modular.domain.dto.TemplateEmailDTO;
import com.rxinyun.admin.notice.modular.domain.entity.TemplateEmail;

public interface EmailService {
   void add(TemplateEmailDTO templateEmailDTO);

   TemplateEmail getById(String id);

   void edit(TemplateEmailDTO templateEmailDTO);
}
