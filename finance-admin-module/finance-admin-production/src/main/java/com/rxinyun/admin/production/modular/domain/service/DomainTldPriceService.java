package com.rxinyun.admin.production.modular.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.domain.entity.DomainTldPrice;
import com.rxinyun.admin.production.modular.domain.param.DomainTldPriceAddParam;
import com.rxinyun.admin.production.modular.domain.param.DomainTldPriceEditParam;
import com.rxinyun.admin.production.modular.domain.param.DomainTldPriceIdParam;
import com.rxinyun.admin.production.modular.domain.param.DomainTldPricePageParam;
import java.util.List;

public interface DomainTldPriceService {
   Page<DomainTldPrice> page(DomainTldPricePageParam pageParam);

   void add(DomainTldPriceAddParam addParam);

   void edit(DomainTldPriceEditParam editParam);

   DomainTldPrice detail(DomainTldPriceIdParam idParam);

   DomainTldPrice queryEntity(String id);

   void delete(List<DomainTldPriceIdParam> idParamList);
}
