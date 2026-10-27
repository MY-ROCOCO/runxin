package com.rxinyun.production.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.production.api.dto.DomainTemplateSubmitResultVO;
import com.rxinyun.production.api.dto.DomainTemplateVO;
import com.rxinyun.production.api.param.AdminDomainTemplatePageParam;

public interface DomainTemplateManageApi {
   Page<DomainTemplateVO> page(AdminDomainTemplatePageParam param);

   DomainTemplateVO getDetail(String templateId);

   DomainTemplateSubmitResultVO submit(String templateId);

   void delete(String templateId);
}
