package com.rxinyun.admin.notice.modular.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.notice.modular.domain.dto.TemplateEmailDTO;
import com.rxinyun.admin.notice.modular.domain.entity.TemplateEmail;
import com.rxinyun.admin.notice.modular.mapper.EmailMapper;
import com.rxinyun.admin.notice.modular.service.EmailService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl extends ServiceImpl<EmailMapper, TemplateEmail> implements EmailService {
   private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
   @Resource
   EmailMapper emailMapper;

   @Override
   public void add(TemplateEmailDTO templateEmailDTO) {
      TemplateEmail templateEmail = (TemplateEmail)BeanUtil.toBean(templateEmailDTO, TemplateEmail.class);
      this.emailMapper.insert(templateEmail);
   }

   @Override
   public TemplateEmail getById(String id) {
      return (TemplateEmail)this.emailMapper.selectById(id);
   }

   @Override
   public void edit(TemplateEmailDTO templateEmailDTO) {
      TemplateEmail templateEmail = (TemplateEmail)BeanUtil.toBean(templateEmailDTO, TemplateEmail.class);
      this.emailMapper.updateById(templateEmail);
   }
}
