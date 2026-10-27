package com.rxinyun.admin.marketing.modular.coupon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.marketing.modular.coupon.entity.CouponTemplate;
import com.rxinyun.admin.marketing.modular.coupon.param.CouponTemplateAddParam;
import com.rxinyun.admin.marketing.modular.coupon.param.CouponTemplateEditParam;
import com.rxinyun.admin.marketing.modular.coupon.param.CouponTemplatePageParam;
import java.util.List;

public interface CouponTemplateService extends IService<CouponTemplate> {
   Page<CouponTemplate> page(CouponTemplatePageParam pageParam);

   void add(CouponTemplateAddParam addParam);

   void edit(CouponTemplateEditParam editParam);

   void delete(List<String> ids);

   CouponTemplate detail(String id);
}
