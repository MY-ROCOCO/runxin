package com.rxinyun.admin.dev.modular.message.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.message.entity.ToolsSmsTemplate;
import com.rxinyun.admin.dev.modular.message.param.ToolsSmsTemplateAddParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsSmsTemplateEditParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsSmsTemplateIdParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsSmsTemplatePageParam;

public interface ToolsSmsTemplateService {
   Page<ToolsSmsTemplate> page(ToolsSmsTemplatePageParam toolsSmsTemplatePageParam);

   ToolsSmsTemplate detail(ToolsSmsTemplateIdParam toolsSmsTemplateIdParam);

   ToolsSmsTemplate queryEntity(String id);

   void add(ToolsSmsTemplateAddParam toolsSmsTemplateAddParam);

   void edit(ToolsSmsTemplateEditParam toolsSmsTemplateEditParam);

   ToolsSmsTemplate getByScene(String scene);
}
