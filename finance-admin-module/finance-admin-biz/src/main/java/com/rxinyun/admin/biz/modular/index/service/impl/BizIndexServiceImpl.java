package com.rxinyun.admin.biz.modular.index.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.biz.modular.index.param.BizIndexNoticeIdParam;
import com.rxinyun.admin.biz.modular.index.param.BizIndexNoticeListParam;
import com.rxinyun.admin.biz.modular.index.param.BizIndexSlideshowListParam;
import com.rxinyun.admin.biz.modular.index.result.BizIndexNoticeListResult;
import com.rxinyun.admin.biz.modular.index.result.BizIndexSlideshowDetailResult;
import com.rxinyun.admin.biz.modular.index.result.BizIndexSlideshowListResult;
import com.rxinyun.admin.biz.modular.index.service.BizIndexService;
import com.rxinyun.admin.biz.modular.notice.entity.BizNotice;
import com.rxinyun.admin.biz.modular.notice.enums.BizNoticeStatusEnum;
import com.rxinyun.admin.biz.modular.notice.service.BizNoticeService;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.dev.api.DevSlideshowApi;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BizIndexServiceImpl implements BizIndexService {
   @Resource
   private DevSlideshowApi devSlideshowApi;
   @Resource
   private BizNoticeService bizNoticeService;

   @Override
   public List<BizIndexSlideshowListResult> slideshowListByPlace(BizIndexSlideshowListParam bizIndexSlideshowListParam) {
      return this.devSlideshowApi
         .getListByPlace(bizIndexSlideshowListParam.getPlace())
         .stream()
         .map(jsonObject -> (BizIndexSlideshowListResult)JSONUtil.toBean(jsonObject, BizIndexSlideshowListResult.class))
         .collect(Collectors.toList());
   }

   @Override
   public List<BizIndexNoticeListResult> noticeListByLimit(BizIndexNoticeListParam bizIndexNoticeListParam) {
      return this.bizNoticeService
         .list(
            (Wrapper<BizNotice>)((LambdaQueryWrapper<BizNotice>)new LambdaQueryWrapper<BizNotice>().eq(BizNotice::getStatus, BizNoticeStatusEnum.ENABLE.getValue()))
               .orderByAsc(CommonEntity::getCreateTime)
         )
         .stream()
         .limit(ObjectUtil.isNotEmpty(bizIndexNoticeListParam.getLimit()) ? bizIndexNoticeListParam.getLimit().longValue() : 10L)
         .map(notice -> (BizIndexNoticeListResult)JSONUtil.toBean(JSONUtil.toJsonStr(notice), BizIndexNoticeListResult.class))
         .collect(Collectors.toList());
   }

   @Override
   public BizIndexSlideshowDetailResult noticeDetailById(BizIndexNoticeIdParam bizIndexNoticeIdParam) {
      BizIndexSlideshowDetailResult result = new BizIndexSlideshowDetailResult();
      BeanUtil.copyProperties(this.bizNoticeService.getById(bizIndexNoticeIdParam.getId()), result, new String[0]);
      return result;
   }
}
