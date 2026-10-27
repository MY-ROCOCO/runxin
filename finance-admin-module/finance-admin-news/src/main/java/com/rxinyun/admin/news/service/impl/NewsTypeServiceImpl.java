package com.rxinyun.admin.news.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.news.mapper.NewsTypeMapper;
import com.rxinyun.admin.news.model.dto.NewsTypeDto;
import com.rxinyun.admin.news.model.dto.NewsTypePageParam;
import com.rxinyun.admin.news.model.po.NewsType;
import com.rxinyun.admin.news.service.NewsTypeService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewsTypeServiceImpl extends ServiceImpl<NewsTypeMapper, NewsType> implements NewsTypeService {
   private static final Logger log = LoggerFactory.getLogger(NewsTypeServiceImpl.class);
   @Resource
   private NewsTypeMapper newsTypeMapper;
   @Resource
   private NewsTypeService newsTypeService;

   @Override
   public void edit(NewsTypeDto newsTypeDto) {
      NewsType newsType = (NewsType)BeanUtil.toBean(newsTypeDto, NewsType.class);
      this.updateById(newsType);
   }

   @Override
   public void delete(String id) {
      this.newsTypeMapper.deleteById(id);
   }

   @Transactional
   @Override
   public void add(NewsTypeDto newsTypeDto) {
      this.checkParam(newsTypeDto);
      NewsType newsType = (NewsType)BeanUtil.toBean(newsTypeDto, NewsType.class);
      this.save(newsType);
   }

   @Override
   public Page<NewsType> list(NewsTypePageParam pageParam) {
      QueryWrapper<NewsType> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         queryWrapper.lambda().and(q -> q.eq(NewsType::getStatus, pageParam.getStatus()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(NewsType::getSort);
      }

      return (Page<NewsType>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public NewsType searchNewsTypeById(String id) {
      if (Objects.equals(id, "")) {
         throw new CommonException("id为空", new Object[0]);
      } else {
         return (NewsType)this.newsTypeMapper.selectById(id);
      }
   }

   private void checkParam(NewsTypeDto newsTypeDto) {
      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      Validator validator = factory.getValidator();
      Set<ConstraintViolation<NewsTypeDto>> violations = validator.validate(newsTypeDto, new Class[0]);
      if (violations.isEmpty()) {
         if (this.selectByName(newsTypeDto.getName())) {
            throw new CommonException("类型名字重复", new Object[0]);
         }
      } else {
         StringBuilder errorMessage = new StringBuilder("参数验证失败: ");

         for (ConstraintViolation<NewsTypeDto> violation : violations) {
            errorMessage.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
         }

         throw new CommonException(errorMessage.toString(), new Object[0]);
      }
   }

   public Boolean selectByName(String name) {
      if (name != null && !name.trim().isEmpty()) {
         LambdaQueryWrapper<NewsType> queryWrapper = new LambdaQueryWrapper();
         queryWrapper.eq(NewsType::getName, name);
         NewsType newsType = (NewsType)this.newsTypeMapper.selectOne(queryWrapper);
         return newsType != null;
      } else {
         return false;
      }
   }
}
