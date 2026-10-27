package com.rxinyun.admin.notice.modular.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.notice.modular.domain.dto.TemplateDTO;
import com.rxinyun.admin.notice.modular.domain.dto.TemplatePageParam;
import com.rxinyun.admin.notice.modular.domain.entity.Template;
import com.rxinyun.admin.notice.modular.domain.vo.TemplateVO;

public interface TemplateService {
   void add(TemplateDTO templateDTO);

   Page<TemplateVO> page(TemplatePageParam pageParam);

   void edit(TemplateDTO templateDTO);

   Template getById(String id);
}
