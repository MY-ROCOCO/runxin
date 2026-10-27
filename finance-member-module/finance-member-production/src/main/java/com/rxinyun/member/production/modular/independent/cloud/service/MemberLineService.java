package com.rxinyun.member.production.modular.independent.cloud.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.independent.cloud.entity.MemberLine;
import com.rxinyun.member.production.modular.independent.cloud.param.MemberLineIdParam;
import com.rxinyun.member.production.modular.independent.cloud.param.MemberLinePageParam;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public interface MemberLineService {
   Page<MemberLine> page(MemberLinePageParam independentLinePageParam);

   List<MemberLine> getLineName();

   MemberLine detail(MemberLineIdParam memberLineIdParam);

   List<MemberLine> findLine(@NotBlank String regionId);

   MemberLine queryEntity(String id);
}
