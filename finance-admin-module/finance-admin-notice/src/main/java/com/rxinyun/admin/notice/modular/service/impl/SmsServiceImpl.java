package com.rxinyun.admin.notice.modular.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.notice.modular.domain.dto.TemplateSmsDTO;
import com.rxinyun.admin.notice.modular.domain.entity.TemplateSms;
import com.rxinyun.admin.notice.modular.mapper.SmsMapper;
import com.rxinyun.admin.notice.modular.service.SmsService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl extends ServiceImpl<SmsMapper, TemplateSms> implements SmsService {
   private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);
   @Resource
   SmsMapper smsMapper;

   @Override
   public void add(TemplateSmsDTO a) {
      TemplateSms templateSms = (TemplateSms)BeanUtil.toBean(a, TemplateSms.class);
      this.smsMapper.insert(templateSms);
   }

   @Override
   public void edit(TemplateSmsDTO a) {
      TemplateSms templateSms = (TemplateSms)BeanUtil.toBean(a, TemplateSms.class);
      this.smsMapper.updateById(templateSms);
   }

   @Override
   public TemplateSms getById(String id) {
      return (TemplateSms)this.smsMapper.selectById(id);
   }
}
