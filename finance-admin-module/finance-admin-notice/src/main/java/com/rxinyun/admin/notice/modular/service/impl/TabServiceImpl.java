package com.rxinyun.admin.notice.modular.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.notice.modular.domain.dto.TabEmailSmsParam;
import com.rxinyun.admin.notice.modular.domain.dto.TemplateTabDTO;
import com.rxinyun.admin.notice.modular.domain.entity.TemplateTab;
import com.rxinyun.admin.notice.modular.mapper.TabMapper;
import com.rxinyun.admin.notice.modular.service.TabService;
import com.rxinyun.common.exception.CommonException;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TabServiceImpl extends ServiceImpl<TabMapper, TemplateTab> implements TabService {
   private static final Logger log = LoggerFactory.getLogger(TabServiceImpl.class);
   @Resource
   TabMapper tabMapper;
   @Resource
   TemplateTabRelServiceImpl templateTabRelService;

   @Override
   public void add(TemplateTabDTO a) {
      this.checkParam(a);
      TemplateTab templateTab = (TemplateTab)BeanUtil.toBean(a, TemplateTab.class);
      this.tabMapper.insert(templateTab);
   }

   private void checkParam(TemplateTabDTO newsDto) {
      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      Validator validator = factory.getValidator();
      Set<ConstraintViolation<TemplateTabDTO>> violations = validator.validate(newsDto, new Class[0]);
      if (violations.isEmpty()) {
         if (this.selectByName(newsDto.getName())) {
            throw new CommonException("标签重复", new Object[0]);
         }
      } else {
         StringBuilder errorMessage = new StringBuilder("参数验证失败: ");

         for (ConstraintViolation<TemplateTabDTO> violation : violations) {
            errorMessage.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
         }

         throw new CommonException(errorMessage.toString(), new Object[0]);
      }
   }

   public Boolean selectByName(String name) {
      if (name != null && !name.trim().isEmpty()) {
         LambdaQueryWrapper<TemplateTab> queryWrapper = new LambdaQueryWrapper();
         queryWrapper.eq(TemplateTab::getName, name);
         TemplateTab tab = (TemplateTab)this.tabMapper.selectOne(queryWrapper);
         return tab != null;
      } else {
         return false;
      }
   }

   @Override
   public void edit(TemplateTabDTO a) {
      TemplateTab templateTab = (TemplateTab)BeanUtil.toBean(a, TemplateTab.class);
      this.tabMapper.updateById(templateTab);
   }

   @Override
   public void save(List<TabEmailSmsParam> list) {
      this.templateTabRelService.insertList(list);
   }

   @Override
   public TemplateTab getById(String id) {
      return (TemplateTab)this.tabMapper.selectById(id);
   }

   @Override
   public List<TemplateTab> tabList() {
      return this.list();
   }
}
