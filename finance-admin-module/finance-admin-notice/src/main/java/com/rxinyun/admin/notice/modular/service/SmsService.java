package com.rxinyun.admin.notice.modular.service;

import com.rxinyun.admin.notice.modular.domain.dto.TemplateSmsDTO;
import com.rxinyun.admin.notice.modular.domain.entity.TemplateSms;

public interface SmsService {
   void add(TemplateSmsDTO templateEmailDTO);

   void edit(TemplateSmsDTO a);

   TemplateSms getById(String id);
}
