package com.rxinyun.member.production.modular.vhost.line.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.member.production.modular.vhost.line.entity.MemberVhostLine;
import com.rxinyun.member.production.modular.vhost.line.mapper.MemberVhostLineMapper;
import com.rxinyun.member.production.modular.vhost.line.param.MemberVhostLinePageParam;
import com.rxinyun.member.production.modular.vhost.line.service.MemberVhostLineService;
import com.rxinyun.member.production.modular.vhost.region.entity.MemberVhostRegion;
import com.rxinyun.member.production.modular.vhost.region.mapper.MemberVhostRegionMapper;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemberVhostLineServiceImpl extends ServiceImpl<MemberVhostLineMapper, MemberVhostLine> implements MemberVhostLineService {
   @Resource
   private MemberVhostRegionMapper memberVhostRegionMapper;

   @Override
   public Page<MemberVhostLine> page(MemberVhostLinePageParam pageParam) {
      MemberVhostRegion region = (MemberVhostRegion)this.memberVhostRegionMapper.selectById(pageParam.getRegionId());
      if (region != null && Integer.valueOf(1).equals(region.getStatus())) {
         QueryWrapper<MemberVhostLine> queryWrapper = new QueryWrapper().checkSqlInjection();
         ((LambdaQueryWrapper<MemberVhostLine>)queryWrapper.lambda().eq(MemberVhostLine::getRegionId, pageParam.getRegionId())).eq(MemberVhostLine::getStatus, 1);
         if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
            queryWrapper.lambda().like(MemberVhostLine::getName, pageParam.getSearchKey());
         }

         if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
            CommonSortOrderEnum.validate(pageParam.getSortOrder());
            queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
         } else {
            ((LambdaQueryWrapper<MemberVhostLine>)queryWrapper.lambda().orderByAsc(MemberVhostLine::getSortId)).orderByDesc(MemberVhostLine::getId);
         }

         Page<MemberVhostLine> page = (Page<MemberVhostLine>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
         this.fillRegionName(page.getRecords(), region.getName());
         return page;
      } else {
         throw new CommonException("地域不存在或已停用", new Object[0]);
      }
   }

   private void fillRegionName(List<MemberVhostLine> lines, String regionName) {
      if (!CollUtil.isEmpty(lines)) {
         lines.forEach(line -> line.setRegionName(regionName));
      }
   }
}
