package com.rxinyun.admin.production.modular.domain.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.domain.entity.DomainTldPrice;
import com.rxinyun.admin.production.modular.domain.mapper.DomainTldPriceMapper;
import com.rxinyun.admin.production.modular.domain.param.DomainTldPriceAddParam;
import com.rxinyun.admin.production.modular.domain.param.DomainTldPriceEditParam;
import com.rxinyun.admin.production.modular.domain.param.DomainTldPriceIdParam;
import com.rxinyun.admin.production.modular.domain.param.DomainTldPricePageParam;
import com.rxinyun.admin.production.modular.domain.service.DomainTldPriceService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DomainTldPriceServiceImpl extends ServiceImpl<DomainTldPriceMapper, DomainTldPrice> implements DomainTldPriceService {
   @Resource
   private DomainTldPriceMapper domainTldPriceMapper;

   @Override
   public Page<DomainTldPrice> page(DomainTldPricePageParam pageParam) {
      QueryWrapper<DomainTldPrice> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().like(DomainTldPrice::getTld, pageParam.getSearchKey());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         queryWrapper.lambda().eq(DomainTldPrice::getStatus, pageParam.getStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<DomainTldPrice>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(DomainTldPriceAddParam addParam) {
      DomainTldPrice entity = (DomainTldPrice)BeanUtil.toBean(addParam, DomainTldPrice.class);
      entity.setId(IdWorker.getIdStr());
      if (entity.getStatus() == null) {
         entity.setStatus(1);
      }

      this.save(entity);
   }

   @Override
   public void edit(DomainTldPriceEditParam editParam) {
      DomainTldPrice entity = this.queryEntity(editParam.getId());
      BeanUtil.copyProperties(editParam, entity, new String[0]);
      this.updateById(entity);
   }

   @Override
   public DomainTldPrice detail(DomainTldPriceIdParam idParam) {
      return this.queryEntity(idParam.getId());
   }

   @Override
   public DomainTldPrice queryEntity(String id) {
      DomainTldPrice entity = (DomainTldPrice)this.getById(id);
      if (ObjectUtil.isEmpty(entity)) {
         throw new CommonException("域名价格不存在，id：{}", new Object[]{id});
      } else {
         return entity;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<DomainTldPriceIdParam> idParamList) {
      List<String> ids = CollStreamUtil.toList(idParamList, DomainTldPriceIdParam::getId);
      if (ObjectUtil.isNotEmpty(ids)) {
         this.removeByIds(ids);
      }
   }
}
