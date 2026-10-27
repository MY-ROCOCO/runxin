package com.rxinyun.member.production.modular.ecs.line.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.member.production.modular.ecs.line.entity.MemberLineSku;
import com.rxinyun.member.production.modular.ecs.line.entity.MemberLocalLine;
import com.rxinyun.member.production.modular.ecs.line.mapper.MemberLocalLineMapper;
import com.rxinyun.member.production.modular.ecs.line.param.MemberLocalLineIdParam;
import com.rxinyun.member.production.modular.ecs.line.param.MemberLocalLinePageParam;
import com.rxinyun.member.production.modular.ecs.line.service.MemberLineSkuService;
import com.rxinyun.member.production.modular.ecs.line.service.MemberLocalLineService;
import com.rxinyun.member.production.modular.ecs.line.vo.MemberLocalLineVO;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberLocalLineServiceImpl extends ServiceImpl<MemberLocalLineMapper, MemberLocalLine> implements MemberLocalLineService {
   private static final Logger log = LoggerFactory.getLogger(MemberLocalLineServiceImpl.class);
   @Resource
   private MemberLocalLineMapper memberLocalLineMapper;
   @Resource
   private MemberLineSkuService memberLineSkuService;

   @Override
   public Page<MemberLocalLine> page(MemberLocalLinePageParam pageParam) {
      QueryWrapper<MemberLocalLine> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(MemberLocalLine::getStatus, 1);
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(MemberLocalLine::getLineName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getAvailableId())) {
         queryWrapper.lambda().and(q -> q.eq(MemberLocalLine::getAvailableId, pageParam.getAvailableId()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<MemberLocalLine>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public MemberLocalLineVO detail(MemberLocalLineIdParam serverLocalLineIdParam) {
      MemberLocalLine serverLocalLine = this.queryEntity(serverLocalLineIdParam.getId());
      QueryWrapper<MemberLineSku> queryWrapper = new QueryWrapper();
      ((LambdaQueryWrapper<MemberLineSku>)queryWrapper.lambda().eq(MemberLineSku::getLineId, serverLocalLine.getId())).eq(MemberLineSku::getStatus, 1);
      if (ObjectUtil.isEmpty(serverLocalLine)) {
         throw new CommonException("线路不存在，id值为：{}", new Object[]{serverLocalLineIdParam.getId()});
      } else {
         MemberLocalLineVO serverLocalLineVO = (MemberLocalLineVO)BeanUtil.toBean(serverLocalLine, MemberLocalLineVO.class);
         List<MemberLineSku> serverLineSkuList = this.memberLineSkuService.selectListByLineId(serverLocalLine.getId());
         if (CollectionUtils.isNotEmpty(serverLineSkuList)) {
            serverLocalLineVO.setServerLineSkuList(serverLineSkuList);
         }

         return serverLocalLineVO;
      }
   }

   @Override
   public MemberLocalLine queryEntity(String id) {
      return this.memberLocalLineMapper.getById(id);
   }

   @Override
   public List<Map<String, Object>> queryByAvailableId(String availableId) {
      QueryWrapper<MemberLocalLine> queryWrapper = new QueryWrapper();
      queryWrapper.select(new String[]{"id", "line_name"});
      queryWrapper.eq("available_id", availableId);
      queryWrapper.eq("status", 1);
      queryWrapper.orderByAsc("sort_id");
      return this.memberLocalLineMapper.selectMaps(queryWrapper);
   }
}
