package com.rxinyun.admin.dev.modular.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.dev.modular.message.entity.ToolsEmailTemplate;
import com.rxinyun.admin.dev.modular.message.mapper.ToolsEmailTemplateMapper;
import com.rxinyun.admin.dev.modular.message.param.ToolsEmailTemplateAddParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsEmailTemplateEditParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsEmailTemplateIdParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsEmailTemplatePageParam;
import com.rxinyun.admin.dev.modular.message.service.ToolsEmailTemplateService;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import org.springframework.stereotype.Service;

@Service
public class ToolsEmailTemplateServiceImpl extends ServiceImpl<ToolsEmailTemplateMapper, ToolsEmailTemplate> implements ToolsEmailTemplateService {
   @Override
   public Page<ToolsEmailTemplate> page(ToolsEmailTemplatePageParam toolsEmailTemplatePageParam) {
      QueryWrapper<ToolsEmailTemplate> queryWrapper = new QueryWrapper().checkSqlInjection();
      return (Page<ToolsEmailTemplate>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public ToolsEmailTemplate detail(ToolsEmailTemplateIdParam toolsEmailTemplateIdParam) {
      return this.queryEntity(toolsEmailTemplateIdParam.getId());
   }

   @Override
   public ToolsEmailTemplate queryEntity(String id) {
      ToolsEmailTemplate toolsEmailTemplate = (ToolsEmailTemplate)this.getById(id);
      if (ObjectUtil.isEmpty(toolsEmailTemplate)) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{id});
      } else {
         return toolsEmailTemplate;
      }
   }

   @Override
   public void add(ToolsEmailTemplateAddParam toolsEmailTemplateAddParam) {
      ToolsEmailTemplate toolsEmailTemplate = (ToolsEmailTemplate)BeanUtil.toBean(toolsEmailTemplateAddParam, ToolsEmailTemplate.class);
      this.save(toolsEmailTemplate);
   }

   @Override
   public void edit(ToolsEmailTemplateEditParam toolsEmailTemplateEditParam) {
      ToolsEmailTemplate toolsEmailTemplate = this.queryEntity(toolsEmailTemplateEditParam.getId());
      BeanUtil.copyProperties(toolsEmailTemplateEditParam, toolsEmailTemplate, new String[0]);
      this.updateById(toolsEmailTemplate);
   }
}
