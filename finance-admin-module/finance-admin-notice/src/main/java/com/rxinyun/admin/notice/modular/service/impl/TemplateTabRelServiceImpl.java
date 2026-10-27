package com.rxinyun.admin.notice.modular.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.notice.modular.domain.dto.TabEmailSmsParam;
import com.rxinyun.admin.notice.modular.domain.entity.TemplateTabRel;
import com.rxinyun.admin.notice.modular.mapper.TemplateTabRelMapper;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TemplateTabRelServiceImpl extends ServiceImpl<TemplateTabRelMapper, TemplateTabRel> {
   @Resource
   TemplateTabRelMapper templateTabRelMapper;

   public void insertList(List<TabEmailSmsParam> list) {
      List<TemplateTabRel> templateTabRels = BeanUtil.copyToList(list, TemplateTabRel.class);

      for (int i = 0; i < list.size(); i++) {
         this.templateTabRelMapper.insert(templateTabRels.get(i));
      }
   }
}
