package com.rxinyun.admin.notice.modular.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.notice.modular.domain.dto.TemplateDTO;
import com.rxinyun.admin.notice.modular.domain.dto.TemplatePageParam;
import com.rxinyun.admin.notice.modular.domain.entity.Template;
import com.rxinyun.admin.notice.modular.domain.entity.TemplateEmail;
import com.rxinyun.admin.notice.modular.domain.entity.TemplateSms;
import com.rxinyun.admin.notice.modular.domain.vo.TemplateVO;
import com.rxinyun.admin.notice.modular.mapper.EmailMapper;
import com.rxinyun.admin.notice.modular.mapper.SmsMapper;
import com.rxinyun.admin.notice.modular.mapper.TemplateMapper;
import com.rxinyun.admin.notice.modular.service.TemplateService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.page.CommonPageRequest;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, Template> implements TemplateService {
   private static final Logger log = LoggerFactory.getLogger(TemplateServiceImpl.class);
   @Resource
   TemplateMapper templateMapper;
   @Resource
   EmailMapper emailMapper;
   @Resource
   SmsMapper smsMapper;

   @Override
   public void add(TemplateDTO templateDTO) {
      Template template = (Template)BeanUtil.toBean(templateDTO, Template.class);
      this.templateMapper.insert(template);
   }

   @Override
   public Page<TemplateVO> page(TemplatePageParam pageParam) {
      QueryWrapper<Template> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         String searchKey = "%" + pageParam.getSearchKey() + "%";
         queryWrapper.and(q -> q.like("name", searchKey));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getSymbol())) {
         queryWrapper.lambda().and(q -> q.eq(Template::getSymbol, pageParam.getSymbol()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(Template::getId);
      }

      Page<Template> page = (Page<Template>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      List<Template> newsList = page.getRecords();
      List<TemplateVO> newsVoList = newsList.stream()
         .map(
            a -> {
               TemplateVO templateVO = new TemplateVO();
               BeanUtils.copyProperties(a, templateVO);
               if (a.getHavingEmail() == 1) {
                  templateVO.setEmailStatus(
                     ((TemplateEmail)this.emailMapper.selectOne((Wrapper)new QueryWrapper().eq("template_id", templateVO.getId()))).getStatus()
                  );
               }

               if (a.getHavingSms() == 1) {
                  templateVO.setHavingSms(
                     ((TemplateSms)this.smsMapper.selectOne((Wrapper)new QueryWrapper().eq("template_id", templateVO.getId()))).getStatus()
                  );
               }

               return templateVO;
            }
         )
         .collect(Collectors.toList());
      Page<TemplateVO> resultPage = new Page(page.getCurrent(), page.getSize(), page.getTotal());
      resultPage.setRecords(newsVoList);
      return resultPage;
   }

   @Override
   public void edit(TemplateDTO templateDTO) {
      Template template = (Template)BeanUtil.toBean(templateDTO, Template.class);
      this.updateById(template);
   }

   @Override
   public Template getById(String id) {
      return (Template)this.templateMapper.selectById(id);
   }
}
