package com.rxinyun.admin.marketing.modular.marketing.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.marketing.modular.marketing.entity.DiscountTemplate;
import com.rxinyun.admin.marketing.modular.marketing.param.DiscountTemplateAddParam;
import com.rxinyun.admin.marketing.modular.marketing.param.DiscountTemplateEditParam;
import com.rxinyun.admin.marketing.modular.marketing.param.DiscountTemplatePageParam;
import java.util.List;

public interface DiscountTemplateService extends IService<DiscountTemplate> {
   Page<DiscountTemplate> page(DiscountTemplatePageParam discountTemplatePageParam);

   void add(DiscountTemplateAddParam discountTemplateAddParam);

   void edit(DiscountTemplateEditParam discountTemplateEditParam);

   void delete(List<String> ids);

   DiscountTemplate detail(String id);
}
