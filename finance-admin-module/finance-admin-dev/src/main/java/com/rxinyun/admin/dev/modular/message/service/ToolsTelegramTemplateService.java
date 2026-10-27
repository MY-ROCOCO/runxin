package com.rxinyun.admin.dev.modular.message.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.message.entity.ToolsTelegramTemplate;
import com.rxinyun.admin.dev.modular.message.param.ToolsTelegramTemplateAddParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsTelegramTemplateEditParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsTelegramTemplateIdParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsTelegramTemplatePageParam;

public interface ToolsTelegramTemplateService {
   Page<ToolsTelegramTemplate> page(ToolsTelegramTemplatePageParam toolsTelegramTemplatePageParam);

   ToolsTelegramTemplate detail(ToolsTelegramTemplateIdParam toolsTelegramTemplateIdParam);

   ToolsTelegramTemplate queryEntity(String id);

   void add(ToolsTelegramTemplateAddParam toolsTelegramTemplateAddParam);

   void edit(ToolsTelegramTemplateEditParam toolsTelegramTemplateEditParam);
}
