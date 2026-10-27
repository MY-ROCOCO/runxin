package com.rxinyun.admin.marketing.modular.marketing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.marketing.modular.marketing.entity.DiscountDetail;
import java.util.List;

public interface DiscountDetailService extends IService<DiscountDetail> {
   List<DiscountDetail> listByTemplateId(String templateId);

   void batchAdd(List<DiscountDetail> discountDetails);

   void deleteByTemplateId(String templateId);
}
