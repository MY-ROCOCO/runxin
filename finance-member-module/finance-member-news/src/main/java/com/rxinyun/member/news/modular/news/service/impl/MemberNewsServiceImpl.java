package com.rxinyun.member.news.modular.news.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.news.modular.news.mapper.MemberNewsMapper;
import com.rxinyun.member.news.modular.news.mapper.MemberNewsTypeMapper;
import com.rxinyun.member.news.modular.news.model.dto.NewsPageParam;
import com.rxinyun.member.news.modular.news.model.po.News;
import com.rxinyun.member.news.modular.news.model.po.NewsType;
import com.rxinyun.member.news.modular.news.model.vo.NewsVo;
import com.rxinyun.member.news.modular.news.service.NewsService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class MemberNewsServiceImpl extends ServiceImpl<MemberNewsMapper, News> implements NewsService {
   private static final Logger log = LoggerFactory.getLogger(MemberNewsServiceImpl.class);
   @Resource
   private MemberNewsMapper memberNewsMapper;
   @Resource
   private MemberNewsTypeMapper memberNewsTypeMapper;

   @Override
   public Page<News> page(NewsPageParam pageParam) {
      if (ObjectUtil.isEmpty(pageParam.getTypeId())) {
         throw new CommonException("新闻类型id为空", new Object[0]);
      } else {
         QueryWrapper<News> queryWrapper = new QueryWrapper().checkSqlInjection();
         if (ObjectUtil.isNotEmpty(pageParam.getTypeId())) {
            queryWrapper.lambda().and(q -> q.eq(News::getTypeId, pageParam.getTypeId()));
         }

         if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
            queryWrapper.lambda().and(q -> q.eq(News::getStatus, pageParam.getStatus()));
         }

         if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
            CommonSortOrderEnum.validate(pageParam.getSortOrder());
            queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
         } else {
            queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
         }

         return (Page<News>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      }
   }

   @Override
   public Page<NewsVo> noticePage(NewsPageParam pageParam) {
      QueryWrapper<News> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         queryWrapper.lambda().and(q -> q.eq(News::getStatus, pageParam.getStatus()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<News> page = (Page<News>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      List<News> newsList = page.getRecords();
      List<NewsVo> newsVoList = newsList.stream().map(news -> {
         NewsVo newsVo = new NewsVo();
         BeanUtils.copyProperties(news, newsVo);
         NewsType newsType = (NewsType)this.memberNewsTypeMapper.selectById(news.getTypeId());
         if (newsType != null) {
            newsVo.setType(newsType.getName());
         }

         return newsVo;
      }).collect(Collectors.toList());
      Page<NewsVo> resultPage = new Page(page.getCurrent(), page.getSize(), page.getTotal());
      resultPage.setRecords(newsVoList);
      return resultPage;
   }

   @Override
   public List<List<NewsVo>> homeSort() {
      List<NewsType> newsTypes = this.memberNewsTypeMapper
         .selectList(
            (Wrapper<NewsType>)((LambdaQueryWrapper<NewsType>)((LambdaQueryWrapper<NewsType>)new LambdaQueryWrapper<NewsType>().eq(NewsType::getStatus, 1)).orderByAsc(NewsType::getSort))
               .last("LIMIT 3")
         );
      List<String> topCategoryIds = newsTypes.stream().map(NewsType::getId).toList();
      return topCategoryIds.stream()
         .map(
            categoryId -> {
               List<News> newsList = this.list(
                  (Wrapper<News>)((LambdaQueryWrapper<News>)((LambdaQueryWrapper<News>)((LambdaQueryWrapper<News>)((LambdaQueryWrapper<News>)new LambdaQueryWrapper<News>()
                                 .eq(News::getTypeId, categoryId))
                              .eq(News::getStatus, 1))
                           .orderByDesc(News::getProperty))
                        .orderByDesc(News::getUpdateTime))
                     .last("LIMIT 5")
               );
               return this.convertToNewsVoList(newsList);
            }
         )
         .collect(Collectors.toList());
   }

   private List<NewsVo> convertToNewsVoList(List<News> newsList) {
      if (Objects.isNull(newsList)) {
         CommonResult.error("没有新闻", newsList);
      }

      return newsList.stream().map(news -> {
         NewsVo vo = (NewsVo)BeanUtil.toBean(news, NewsVo.class);
         vo.setType(this.getTypeNameById(news.getTypeId()));
         return vo;
      }).toList();
   }

   private String getTypeNameById(String typeId) {
      NewsType newsType = (NewsType)this.memberNewsTypeMapper.selectById(typeId);
      return newsType != null ? newsType.getName() : "未知分类";
   }
}
