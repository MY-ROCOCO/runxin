package com.rxinyun.member.production.modular.vhost.version.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.member.production.modular.vhost.line.entity.MemberVhostLine;
import com.rxinyun.member.production.modular.vhost.line.mapper.MemberVhostLineMapper;
import com.rxinyun.member.production.modular.vhost.version.entity.MemberVhostVersion;
import com.rxinyun.member.production.modular.vhost.version.entity.MemberVhostVersionLine;
import com.rxinyun.member.production.modular.vhost.version.mapper.MemberVhostVersionLineMapper;
import com.rxinyun.member.production.modular.vhost.version.mapper.MemberVhostVersionMapper;
import com.rxinyun.member.production.modular.vhost.version.param.MemberVhostVersionPageParam;
import com.rxinyun.member.production.modular.vhost.version.service.MemberVhostVersionService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemberVhostVersionServiceImpl extends ServiceImpl<MemberVhostVersionMapper, MemberVhostVersion> implements MemberVhostVersionService {
   @Resource
   private MemberVhostLineMapper memberVhostLineMapper;
   @Resource
   private MemberVhostVersionLineMapper memberVhostVersionLineMapper;

   @Override
   public Page<MemberVhostVersion> page(MemberVhostVersionPageParam pageParam) {
      MemberVhostLine line = (MemberVhostLine)this.memberVhostLineMapper.selectById(pageParam.getLineId());
      if (line != null && Integer.valueOf(1).equals(line.getStatus())) {
         List<String> versionIds = this.memberVhostVersionLineMapper
            .selectList(
               (Wrapper<MemberVhostVersionLine>)new LambdaQueryWrapper<MemberVhostVersionLine>()
                  .select(MemberVhostVersionLine::getVersionId)
                  .eq(MemberVhostVersionLine::getLineId, pageParam.getLineId())
            )
            .stream()
            .map(MemberVhostVersionLine::getVersionId)
            .filter(CharSequenceUtil::isNotBlank)
            .distinct()
            .toList();
         Page<MemberVhostVersion> page = CommonPageRequest.defaultPage();
         if (CollUtil.isEmpty(versionIds)) {
            page.setRecords(List.of());
            page.setTotal(0L);
            return page;
         } else {
            QueryWrapper<MemberVhostVersion> queryWrapper = new QueryWrapper().checkSqlInjection();
            ((LambdaQueryWrapper<MemberVhostVersion>)queryWrapper.lambda().in(MemberVhostVersion::getId, versionIds)).eq(MemberVhostVersion::getStatus, 1);
            if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
               queryWrapper.lambda().like(MemberVhostVersion::getName, pageParam.getSearchKey());
            }

            if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
               CommonSortOrderEnum.validate(pageParam.getSortOrder());
               queryWrapper.orderBy(
                  true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField())
               );
            } else {
               ((LambdaQueryWrapper<MemberVhostVersion>)queryWrapper.lambda().orderByAsc(MemberVhostVersion::getSortId)).orderByDesc(MemberVhostVersion::getId);
            }

            return (Page<MemberVhostVersion>)this.page(page, queryWrapper);
         }
      } else {
         throw new CommonException("线路不存在或已停用", new Object[0]);
      }
   }
}
