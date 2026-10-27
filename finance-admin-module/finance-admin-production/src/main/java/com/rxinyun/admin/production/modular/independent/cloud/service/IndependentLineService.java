package com.rxinyun.admin.production.modular.independent.cloud.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.independent.cloud.entity.IndependentLine;
import com.rxinyun.admin.production.modular.independent.cloud.param.IndependentLineAddParam;
import com.rxinyun.admin.production.modular.independent.cloud.param.IndependentLineEditParam;
import com.rxinyun.admin.production.modular.independent.cloud.param.IndependentLinePageParam;
import com.rxinyun.admin.production.modular.independent.cloud.param.IndependentLinePageVO;
import com.rxinyun.admin.production.modular.independent.cloud.param.ServerLocalLineUpdateDiscountTemplateParam;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public interface IndependentLineService {
   void add(IndependentLineAddParam independentLineParam);

   void delete(@NotBlank String id);

   Page<IndependentLinePageVO> page(IndependentLinePageParam independentLinePageParam);

   void update(IndependentLineEditParam independentLineParam);

   List<IndependentLine> getLineName();

   IndependentLine detail(@NotBlank String id);

   boolean isBindingRegion(String id);

   void updateDiscountTemplate(ServerLocalLineUpdateDiscountTemplateParam serverLocalLineUpdateTemplateParam);

   IndependentLine queryEntity(String id);
}
