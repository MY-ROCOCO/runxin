package com.rxinyun.admin.dev.modular.message.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.message.entity.ToolsEmailTemplate;
import com.rxinyun.admin.dev.modular.message.param.ToolsEmailTemplateAddParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsEmailTemplateEditParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsEmailTemplateIdParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsEmailTemplatePageParam;

public interface ToolsEmailTemplateService {
   Page<ToolsEmailTemplate> page(ToolsEmailTemplatePageParam toolsSmsTemplatePageParam);

   ToolsEmailTemplate detail(ToolsEmailTemplateIdParam toolsSmsTemplateIdParam);

   ToolsEmailTemplate queryEntity(String id);

   void add(ToolsEmailTemplateAddParam toolsSmsTemplateAddParam);

   void edit(ToolsEmailTemplateEditParam toolsSmsTemplateEditParam);
}
