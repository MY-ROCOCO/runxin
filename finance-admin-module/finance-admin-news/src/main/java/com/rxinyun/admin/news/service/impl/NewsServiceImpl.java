package com.rxinyun.admin.news.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.news.mapper.NewsMapper;
import com.rxinyun.admin.news.mapper.NewsTypeMapper;
import com.rxinyun.admin.news.model.dto.NewsDto;
import com.rxinyun.admin.news.model.dto.NewsPageParam;
import com.rxinyun.admin.news.model.po.News;
import com.rxinyun.admin.news.model.po.NewsType;
import com.rxinyun.admin.news.model.vo.NewsVo;
import com.rxinyun.admin.news.service.NewsService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {
   private static final Logger log = LoggerFactory.getLogger(NewsServiceImpl.class);
   @Resource
   private NewsMapper newsMapper;
   @Resource
   private NewsService newsService;
   @Resource
   private NewsTypeMapper newsTypeMapper;

   @Override
   public Page<NewsVo> page(NewsPageParam pageParam) {
      QueryWrapper<News> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         String searchKey = "%" + pageParam.getSearchKey() + "%";
         List<NewsType> newsTypes = this.newsTypeMapper.selectList((Wrapper)new QueryWrapper().like("name", searchKey));
         Set<String> typeIds = newsTypes.stream().map(NewsType::getId).collect(Collectors.toSet());
         queryWrapper.and(
            q -> ((QueryWrapper)((QueryWrapper)((QueryWrapper)((QueryWrapper)q.like("title", searchKey)).or()).like("content", searchKey)).or())
               .in(!typeIds.isEmpty(), "type_id", typeIds)
         );
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         queryWrapper.lambda().and(q -> q.eq(News::getStatus, pageParam.getStatus()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<News> page = (Page<News>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      List<News> newsList = page.getRecords();
      Set<String> typeIds = newsList.stream().map(News::getTypeId).collect(Collectors.toSet());
      Map<String, NewsType> typeMap;
      if (!typeIds.isEmpty()) {
         List<NewsType> newsTypes = this.newsTypeMapper.selectBatchIds(typeIds);
         typeMap = newsTypes.stream().collect(Collectors.toMap(NewsType::getId, Function.identity()));
      } else {
         typeMap = new HashMap<>();
      }

      List<NewsVo> newsVoList = newsList.stream().map(news -> {
         NewsVo newsVo = new NewsVo();
         BeanUtils.copyProperties(news, newsVo);
         NewsType newsType = typeMap.get(news.getTypeId());
         if (newsType != null) {
            newsVo.setType(newsType.getName());
         }

         return newsVo;
      }).collect(Collectors.toList());
      Page<NewsVo> resultPage = new Page(page.getCurrent(), page.getSize(), page.getTotal());
      resultPage.setRecords(newsVoList);
      return resultPage;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(NewsDto newsDto) {
      this.checkParam(newsDto);
      News news = (News)BeanUtil.toBean(newsDto, News.class);
      if (news.getContentFormat() == null) {
         news.setContentFormat(0);
      }

      this.save(news);
   }

   private void checkParam(NewsDto newsDto) {
      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      Validator validator = factory.getValidator();
      Set<ConstraintViolation<NewsDto>> violations = validator.validate(newsDto, new Class[0]);
      if (violations.isEmpty()) {
         if (this.selectByName(newsDto.getTitle())) {
            throw new CommonException("新闻标题重复", new Object[0]);
         }
      } else {
         StringBuilder errorMessage = new StringBuilder("参数验证失败: ");

         for (ConstraintViolation<NewsDto> violation : violations) {
            errorMessage.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
         }

         throw new CommonException(errorMessage.toString(), new Object[0]);
      }
   }

   public Boolean selectByName(String title) {
      if (title != null && !title.trim().isEmpty()) {
         LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper();
         queryWrapper.eq(News::getTitle, title);
         News news = (News)this.newsMapper.selectOne(queryWrapper);
         return news != null;
      } else {
         return false;
      }
   }

   @Transactional
   @Override
   public void edit(NewsDto newsDto) {
      News news = (News)BeanUtil.toBean(newsDto, News.class);
      this.updateById(news);
   }

   @Override
   public void delete(String id) {
      this.newsMapper.deleteById(id);
   }

   @Override
   public News searchNewsById(String id) {
      return (News)this.newsMapper.selectById(id);
   }
}
