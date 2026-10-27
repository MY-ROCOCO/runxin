package com.rxinyun.admin.biz.modular.notice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.biz.modular.notice.entity.BizNotice;
import com.rxinyun.admin.biz.modular.notice.param.BizNoticeAddParam;
import com.rxinyun.admin.biz.modular.notice.param.BizNoticeEditParam;
import com.rxinyun.admin.biz.modular.notice.param.BizNoticeIdParam;
import com.rxinyun.admin.biz.modular.notice.param.BizNoticePageParam;
import java.util.List;

public interface BizNoticeService extends IService<BizNotice> {
   Page<BizNotice> page(BizNoticePageParam bizNoticePageParam);

   void add(BizNoticeAddParam bizNoticeAddParam);

   void edit(BizNoticeEditParam bizNoticeEditParam);

   void delete(List<BizNoticeIdParam> bizNoticeIdParamList);

   BizNotice detail(BizNoticeIdParam bizNoticeIdParam);

   BizNotice queryEntity(String id);

   void disableStatus(BizNoticeIdParam bizNoticeIdParam);

   void enableStatus(BizNoticeIdParam bizNoticeIdParam);
}
