package com.rxinyun.admin.dev.modular.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.dev.modular.message.entity.ToolsSmsTemplate;
import com.rxinyun.admin.dev.modular.message.mapper.ToolsSmsTemplateMapper;
import com.rxinyun.admin.dev.modular.message.param.ToolsSmsTemplateAddParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsSmsTemplateEditParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsSmsTemplateIdParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsSmsTemplatePageParam;
import com.rxinyun.admin.dev.modular.message.service.ToolsSmsTemplateService;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import org.springframework.stereotype.Service;

@Service
public class ToolsSmsTemplateServiceImpl extends ServiceImpl<ToolsSmsTemplateMapper, ToolsSmsTemplate> implements ToolsSmsTemplateService {
   @Override
   public Page<ToolsSmsTemplate> page(ToolsSmsTemplatePageParam toolsSmsTemplatePageParam) {
      QueryWrapper<ToolsSmsTemplate> queryWrapper = new QueryWrapper().checkSqlInjection();
      return (Page<ToolsSmsTemplate>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public ToolsSmsTemplate detail(ToolsSmsTemplateIdParam toolsSmsTemplateIdParam) {
      return this.queryEntity(toolsSmsTemplateIdParam.getId());
   }

   @Override
   public ToolsSmsTemplate queryEntity(String id) {
      ToolsSmsTemplate toolsSmsTemplate = (ToolsSmsTemplate)this.getById(id);
      if (ObjectUtil.isEmpty(toolsSmsTemplate)) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{id});
      } else {
         return toolsSmsTemplate;
      }
   }

   @Override
   public void add(ToolsSmsTemplateAddParam toolsSmsTemplateAddParam) {
      ToolsSmsTemplate toolsSmsTemplate = (ToolsSmsTemplate)BeanUtil.toBean(toolsSmsTemplateAddParam, ToolsSmsTemplate.class);
      this.save(toolsSmsTemplate);
   }

   @Override
   public void edit(ToolsSmsTemplateEditParam toolsSmsTemplateEditParam) {
      ToolsSmsTemplate toolsSmsTemplate = this.queryEntity(toolsSmsTemplateEditParam.getId());
      BeanUtil.copyProperties(toolsSmsTemplateEditParam, toolsSmsTemplate, new String[0]);
      this.updateById(toolsSmsTemplate);
   }

   @Override
   public ToolsSmsTemplate getByScene(String scene) {
      if (StrUtil.isEmpty(scene)) {
         throw new CommonException("场景标识不能为空", new Object[0]);
      } else {
         QueryWrapper<ToolsSmsTemplate> queryWrapper = new QueryWrapper();
         ((LambdaQueryWrapper<ToolsSmsTemplate>)((LambdaQueryWrapper<ToolsSmsTemplate>)((LambdaQueryWrapper<ToolsSmsTemplate>)queryWrapper.lambda().eq(ToolsSmsTemplate::getScene, scene))
                  .eq(ToolsSmsTemplate::getStatus, 1))
               .eq(CommonEntity::getDeleteFlag, "NOT_DELETE"))
            .last("LIMIT 1");
         ToolsSmsTemplate template = (ToolsSmsTemplate)this.getOne(queryWrapper);
         if (ObjectUtil.isEmpty(template)) {
            throw new CommonException("未找到场景为【{}】的有效短信模板", new Object[]{scene});
         } else {
            return template;
         }
      }
   }
}
