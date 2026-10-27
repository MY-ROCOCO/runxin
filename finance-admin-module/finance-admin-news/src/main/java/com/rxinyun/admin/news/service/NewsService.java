package com.rxinyun.admin.news.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.news.model.dto.NewsDto;
import com.rxinyun.admin.news.model.dto.NewsPageParam;
import com.rxinyun.admin.news.model.po.News;
import com.rxinyun.admin.news.model.vo.NewsVo;

public interface NewsService {
   Page<NewsVo> page(NewsPageParam pageParam);

   void add(NewsDto newsDto);

   void edit(NewsDto newsDto);

   void delete(String id);

   News searchNewsById(String id);
}
