package com.rxinyun.member.news.modular.news.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.news.modular.news.model.dto.NewsPageParam;
import com.rxinyun.member.news.modular.news.model.po.News;
import com.rxinyun.member.news.modular.news.model.vo.NewsVo;
import java.util.List;

public interface NewsService {
   List<List<NewsVo>> homeSort();

   Page<News> page(NewsPageParam pageParam);

   Page<NewsVo> noticePage(NewsPageParam pageParam);
}
