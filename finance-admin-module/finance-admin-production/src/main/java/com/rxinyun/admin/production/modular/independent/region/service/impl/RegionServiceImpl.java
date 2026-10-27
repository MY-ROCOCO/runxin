package com.rxinyun.admin.production.modular.independent.region.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.independent.cloud.service.IndependentLineService;
import com.rxinyun.admin.production.modular.independent.region.mapper.RegionMapper;
import com.rxinyun.admin.production.modular.independent.region.model.dto.RegionDTO;
import com.rxinyun.admin.production.modular.independent.region.model.dto.RegionPageParam;
import com.rxinyun.admin.production.modular.independent.region.model.entity.Region;
import com.rxinyun.admin.production.modular.independent.region.model.vo.RegionVO;
import com.rxinyun.admin.production.modular.independent.region.service.IRegionService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.dev.core.pojo.BaseResourcePool;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements IRegionService {
   @Resource
   private RegionMapper regionMapper;
   @Resource
   private IndependentLineService independentLineService;
   @Resource
   private ResourcePoolApi resourcePoolApi;
   @Resource
   private JdbcTemplate jdbcTemplate;

   @Override
   public void add(RegionDTO regionDTO) {
      this.checkParam(regionDTO);
      if (this.selectByName(regionDTO.getName())) {
         throw new CommonException("地域名称重复", new Object[0]);
      } else {
         Region region = (Region)BeanUtil.toBean(regionDTO, Region.class);
         this.save(region);
      }
   }

   @Override
   public Region searchRegionById(String id) {
      return (Region)this.regionMapper.selectById(id);
   }

   @Override
   public void edit(RegionDTO regionDTO) {
      this.checkParam(regionDTO);
      Region region = (Region)BeanUtil.toBean(regionDTO, Region.class);
      this.updateById(region);
   }

   @Override
   public void delete(String id) {
      if (this.independentLineService.isBindingRegion(id)) {
         throw new CommonException("有线路绑定本地域", new Object[0]);
      } else {
         this.jdbcTemplate.update("DELETE FROM INDEPENDENT_REGION WHERE id = ?", new Object[]{id});
      }
   }

   @Override
   public CommonResult<List<Region>> subduct() {
      return CommonResult.data(
         ((this.lambdaQuery().eq(Region::getStatus, 1)).orderBy(true, true, Region::getSort)).list()
      );
   }

   private void checkParam(RegionDTO regionDTO) {
      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      Validator validator = factory.getValidator();
      Set<ConstraintViolation<RegionDTO>> violations = validator.validate(regionDTO, new Class[0]);
      if (!violations.isEmpty()) {
         StringBuilder errorMessage = new StringBuilder("参数验证失败: ");

         for (ConstraintViolation<RegionDTO> violation : violations) {
            errorMessage.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
         }

         throw new CommonException(errorMessage.toString(), new Object[0]);
      }
   }

   public Boolean selectByName(String name) {
      if (name != null && !name.trim().isEmpty()) {
         LambdaQueryWrapper<Region> queryWrapper = new LambdaQueryWrapper();
         queryWrapper.eq(Region::getName, name);
         Region region = (Region)this.regionMapper.selectOne(queryWrapper);
         return region != null;
      } else {
         return false;
      }
   }

   @Override
   public Page<RegionVO> page(RegionPageParam pageParam) {
      QueryWrapper<Region> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         String searchKey = "%" + pageParam.getSearchKey() + "%";
         queryWrapper.and(q -> q.like("name", searchKey));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         queryWrapper.lambda().and(q -> q.eq(Region::getStatus, pageParam.getStatus()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(
            true, pageParam.getSortOrder().toUpperCase().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(Region::getId);
      }

      queryWrapper.lambda().orderByAsc(true, Region::getSort);
      Page<Region> page = (Page<Region>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      List<Region> records = page.getRecords();
      List<RegionVO> temp = BeanUtil.copyToList(records, RegionVO.class);
      if (ObjectUtil.isNotEmpty(temp)) {
         for (RegionVO regionVO : temp) {
            if (ObjectUtil.isNotEmpty(regionVO.getResourcePoolId())) {
               BaseResourcePool resourcePool = this.resourcePoolApi.getById(regionVO.getResourcePoolId());
               if (ObjectUtil.isNotEmpty(resourcePool)) {
                  regionVO.setResourcePoolType(resourcePool.getPoolType());
                  regionVO.setResourcePoolName(resourcePool.getName());
               }
            }
         }
      }

      Page<RegionVO> result = new Page(page.getCurrent(), page.getSize(), page.getTotal());
      result.setRecords(temp);
      return result;
   }
}
