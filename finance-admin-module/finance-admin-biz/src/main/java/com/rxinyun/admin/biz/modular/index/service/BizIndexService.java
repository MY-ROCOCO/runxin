package com.rxinyun.admin.biz.modular.index.service;

import com.rxinyun.admin.biz.modular.index.param.BizIndexNoticeIdParam;
import com.rxinyun.admin.biz.modular.index.param.BizIndexNoticeListParam;
import com.rxinyun.admin.biz.modular.index.param.BizIndexSlideshowListParam;
import com.rxinyun.admin.biz.modular.index.result.BizIndexNoticeListResult;
import com.rxinyun.admin.biz.modular.index.result.BizIndexSlideshowDetailResult;
import com.rxinyun.admin.biz.modular.index.result.BizIndexSlideshowListResult;
import java.util.List;
import javax.validation.Valid;

public interface BizIndexService {
   List<BizIndexSlideshowListResult> slideshowListByPlace(BizIndexSlideshowListParam bizIndexSlideshowListParam);

   List<BizIndexNoticeListResult> noticeListByLimit(@Valid BizIndexNoticeListParam bizIndexNoticeListParam);

   BizIndexSlideshowDetailResult noticeDetailById(BizIndexNoticeIdParam bizIndexNoticeIdParam);
}
