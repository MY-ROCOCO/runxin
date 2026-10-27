package com.rxinyun.admin.news.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.news.model.dto.NewsTypeDto;
import com.rxinyun.admin.news.model.dto.NewsTypePageParam;
import com.rxinyun.admin.news.model.po.NewsType;

public interface NewsTypeService {
   void edit(NewsTypeDto newsTypeDto);

   void delete(String id);

   void add(NewsTypeDto newsTypeDto);

   Page<NewsType> list(NewsTypePageParam newsTypePageParam);

   NewsType searchNewsTypeById(String id);
}
