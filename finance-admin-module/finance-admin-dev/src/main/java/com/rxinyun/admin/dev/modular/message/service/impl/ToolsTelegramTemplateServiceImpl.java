package com.rxinyun.admin.dev.modular.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.dev.modular.message.entity.ToolsTelegramTemplate;
import com.rxinyun.admin.dev.modular.message.mapper.ToolsTelegramTemplateMapper;
import com.rxinyun.admin.dev.modular.message.param.ToolsTelegramTemplateAddParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsTelegramTemplateEditParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsTelegramTemplateIdParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsTelegramTemplatePageParam;
import com.rxinyun.admin.dev.modular.message.service.ToolsTelegramTemplateService;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import org.springframework.stereotype.Service;

@Service
public class ToolsTelegramTemplateServiceImpl extends ServiceImpl<ToolsTelegramTemplateMapper, ToolsTelegramTemplate> implements ToolsTelegramTemplateService {
   @Override
   public Page<ToolsTelegramTemplate> page(ToolsTelegramTemplatePageParam toolsTelegramTemplatePageParam) {
      QueryWrapper<ToolsTelegramTemplate> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(toolsTelegramTemplatePageParam.getSearchKey())) {
         queryWrapper.lambda().like(ToolsTelegramTemplate::getTemplateName, toolsTelegramTemplatePageParam.getSearchKey());
      }

      return (Page<ToolsTelegramTemplate>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public ToolsTelegramTemplate detail(ToolsTelegramTemplateIdParam toolsTelegramTemplateIdParam) {
      return this.queryEntity(toolsTelegramTemplateIdParam.getId());
   }

   @Override
   public ToolsTelegramTemplate queryEntity(String id) {
      ToolsTelegramTemplate toolsTelegramTemplate = (ToolsTelegramTemplate)this.getById(id);
      if (ObjectUtil.isEmpty(toolsTelegramTemplate)) {
         throw new CommonException("Telegram模板不存在，id值为：{}", new Object[]{id});
      } else {
         return toolsTelegramTemplate;
      }
   }

   @Override
   public void add(ToolsTelegramTemplateAddParam toolsTelegramTemplateAddParam) {
      ToolsTelegramTemplate toolsTelegramTemplate = (ToolsTelegramTemplate)BeanUtil.toBean(toolsTelegramTemplateAddParam, ToolsTelegramTemplate.class);
      this.save(toolsTelegramTemplate);
   }

   @Override
   public void edit(ToolsTelegramTemplateEditParam toolsTelegramTemplateEditParam) {
      ToolsTelegramTemplate toolsTelegramTemplate = this.queryEntity(toolsTelegramTemplateEditParam.getId());
      BeanUtil.copyProperties(toolsTelegramTemplateEditParam, toolsTelegramTemplate, new String[0]);
      this.updateById(toolsTelegramTemplate);
   }
}
