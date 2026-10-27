package com.rxinyun.member.production.modular.domain.provider;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.member.production.modular.domain.template.entity.DomainTemplate;
import com.rxinyun.member.production.modular.domain.template.mapper.MemberDomainTemplateMapper;
import com.rxinyun.member.production.modular.domain.template.service.MemberDomainTemplateService;
import com.rxinyun.member.production.modular.domain.template.service.XinwangSubmitResult;
import com.rxinyun.production.api.DomainTemplateManageApi;
import com.rxinyun.production.api.dto.DomainTemplateSubmitResultVO;
import com.rxinyun.production.api.dto.DomainTemplateVO;
import com.rxinyun.production.api.param.AdminDomainTemplatePageParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class DomainTemplateManageApiProvider implements DomainTemplateManageApi {
   @Resource
   private MemberDomainTemplateMapper memberDomainTemplateMapper;
   @Resource
   private MemberDomainTemplateService memberDomainTemplateService;

   public Page<DomainTemplateVO> page(AdminDomainTemplatePageParam param) {
      QueryWrapper<DomainTemplate> qw = new QueryWrapper().checkSqlInjection();
      qw.lambda()
         .select(
            
               DomainTemplate::getId,
               DomainTemplate::getMemberId,
               DomainTemplate::getName,
               DomainTemplate::getStatus,
               DomainTemplate::getAuditStatus,
               DomainTemplate::getAuditReason,
               CommonEntity::getCreateTime,
               CommonEntity::getUpdateTime
            
         );
      if (StrUtil.isNotBlank(param.getMemberId())) {
         qw.lambda().eq(DomainTemplate::getMemberId, param.getMemberId());
      }

      if (param.getAuditStatus() != null) {
         qw.lambda().eq(DomainTemplate::getAuditStatus, param.getAuditStatus());
      }

      if (StrUtil.isNotBlank(param.getSearchKey())) {
         qw.lambda().like(DomainTemplate::getName, param.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{param.getSortField(), param.getSortOrder()})) {
         CommonSortOrderEnum.validate(param.getSortOrder());
         qw.orderBy(true, "ASCEND".equalsIgnoreCase(param.getSortOrder()), StrUtil.toUnderlineCase(param.getSortField()));
      } else {
         qw.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      long current = param.getCurrent() != null && param.getCurrent() > 0 ? param.getCurrent().intValue() : 1L;
      long size = param.getSize() != null && param.getSize() > 0 ? param.getSize().intValue() : 20L;
      Page<DomainTemplate> page = (Page<DomainTemplate>)this.memberDomainTemplateMapper.selectPage(new Page(current, size), qw);
      Page<DomainTemplateVO> voPage = new Page(page.getCurrent(), page.getSize(), page.getTotal());
      voPage.setRecords(page.getRecords().stream().map(this::toVO).toList());
      return voPage;
   }

   public DomainTemplateVO getDetail(String templateId) {
      DomainTemplate t = this.getTemplateOrThrow(templateId);
      return this.toVO(t);
   }

   public DomainTemplateSubmitResultVO submit(String templateId) {
      DomainTemplate t = this.getTemplateOrThrow(templateId);
      XinwangSubmitResult r = this.memberDomainTemplateService.submitTemplate(t);
      DomainTemplateSubmitResultVO vo = new DomainTemplateSubmitResultVO();
      vo.setCode(r.getCode());
      vo.setMsg(r.getMsg());
      vo.setRequestId(r.getRequestId());
      vo.setSuccess(r.isSuccess());
      return vo;
   }

   public void delete(String templateId) {
      this.getTemplateOrThrow(templateId);
      this.memberDomainTemplateMapper.deleteById(templateId);
   }

   private DomainTemplate getTemplateOrThrow(String templateId) {
      if (StrUtil.isBlank(templateId)) {
         throw new CommonException("模板ID不能为空", new Object[0]);
      } else {
         DomainTemplate t = (DomainTemplate)this.memberDomainTemplateMapper.selectById(templateId);
         if (t == null) {
            throw new CommonException("模板不存在", new Object[0]);
         } else {
            return t;
         }
      }
   }

   private DomainTemplateVO toVO(DomainTemplate t) {
      DomainTemplateVO vo = new DomainTemplateVO();
      vo.setId(t.getId());
      vo.setMemberId(t.getMemberId());
      vo.setName(t.getName());
      vo.setStatus(t.getStatus());
      vo.setAuditStatus(t.getAuditStatus());
      vo.setAuditReason(t.getAuditReason());
      vo.setExtJson(t.getExtJson());
      vo.setCreateTime(t.getCreateTime());
      vo.setUpdateTime(t.getUpdateTime());
      return vo;
   }
}
