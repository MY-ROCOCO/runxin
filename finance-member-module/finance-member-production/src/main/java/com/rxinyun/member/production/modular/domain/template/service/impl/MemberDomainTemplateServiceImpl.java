package com.rxinyun.member.production.modular.domain.template.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.business.DomainTemplateAuditStatusEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.dev.api.DevFileApi;
import com.rxinyun.dev.core.pojo.BaseDevFile;
import com.rxinyun.member.production.modular.domain.template.client.XinwangTemplateApiClient;
import com.rxinyun.member.production.modular.domain.template.dto.ChinaRegionsVO;
import com.rxinyun.member.production.modular.domain.template.dto.RegionCityVO;
import com.rxinyun.member.production.modular.domain.template.dto.RegionProvinceVO;
import com.rxinyun.member.production.modular.domain.template.dto.XinwangTemplateCreateDto;
import com.rxinyun.member.production.modular.domain.template.entity.DomainTemplate;
import com.rxinyun.member.production.modular.domain.template.mapper.MemberDomainTemplateMapper;
import com.rxinyun.member.production.modular.domain.template.param.MemberDomainTemplateAddParam;
import com.rxinyun.member.production.modular.domain.template.param.MemberDomainTemplateEditParam;
import com.rxinyun.member.production.modular.domain.template.param.MemberDomainTemplateIdParam;
import com.rxinyun.member.production.modular.domain.template.param.MemberDomainTemplatePageParam;
import com.rxinyun.member.production.modular.domain.template.service.MemberDomainTemplateService;
import com.rxinyun.member.production.modular.domain.template.service.XinwangSubmitResult;
import com.rxinyun.production.api.CurrentMemberIdApi;
import jakarta.annotation.Resource;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberDomainTemplateServiceImpl extends ServiceImpl<MemberDomainTemplateMapper, DomainTemplate> implements MemberDomainTemplateService {
   private static final String CONFIG_PRIMARY_PROVIDER = "DOMAIN_REGISTER_PRIMARY_PROVIDER";
   private static final String CONFIG_XINWANG_APP_ID = "DOMAIN_XINWANG_APP_ID";
   private static final String CONFIG_XINWANG_APP_KEY = "DOMAIN_XINWANG_APP_KEY";
   private static final String PROVIDER_XINWANG = "XINWANG";
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private XinwangTemplateApiClient xinwangTemplateApiClient;
   @Resource
   private CurrentMemberIdApi currentMemberIdApi;
   @Resource
   private DevFileApi devFileApi;

   @Override
   public Page<DomainTemplate> page(MemberDomainTemplatePageParam pageParam) {
      String memberId = this.getCurrentMemberId();
      QueryWrapper<DomainTemplate> qw = new QueryWrapper().checkSqlInjection();
      qw.lambda().eq(DomainTemplate::getMemberId, memberId);
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
      if (StrUtil.isNotBlank(pageParam.getSearchKey())) {
         qw.lambda().like(DomainTemplate::getName, pageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         qw.orderBy(true, "ASCEND".equalsIgnoreCase(pageParam.getSortOrder()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         qw.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<DomainTemplate>)this.page(CommonPageRequest.defaultPage(), qw);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(MemberDomainTemplateAddParam param) {
      String memberId = this.getCurrentMemberId();
      if (StrUtil.isBlank(param.getName())) {
         throw new CommonException("模板名称不能为空", new Object[0]);
      } else {
         long sameNameCount = ((this.lambdaQuery().eq(DomainTemplate::getMemberId, memberId))
               .eq(DomainTemplate::getName, param.getName().trim()))
            .count();
         if (sameNameCount > 0L) {
            throw new CommonException("模板名称不能重复", new Object[0]);
         } else {
            if (param.getTemplateInfo() != null && StrUtil.isBlank(param.getTemplateInfo().getTemplateName())) {
               param.getTemplateInfo().setTemplateName(param.getName());
            }

            DomainTemplate t = new DomainTemplate();
            t.setId(IdWorker.getIdStr());
            t.setMemberId(memberId);
            t.setName(param.getName());
            t.setStatus(param.getStatus() != null ? param.getStatus() : 1);
            t.setAuditStatus(DomainTemplateAuditStatusEnum.NOT_SUBMITTED.getCode());
            t.setExtJson(param.getTemplateInfo() != null ? JSONUtil.toJsonStr(param.getTemplateInfo()) : null);
            this.save(t);
         }
      }
   }

   @Override
   public void edit(MemberDomainTemplateEditParam param) {
      DomainTemplate t = this.getTemplateAndCheckMember(param.getId());
      Integer status = t.getAuditStatus();
      boolean allowEdit = DomainTemplateAuditStatusEnum.NOT_SUBMITTED.getCode().equals(status)
         || DomainTemplateAuditStatusEnum.REJECTED.getCode().equals(status);
      if (!allowEdit) {
         throw new CommonException("仅未提交或审核失败时可编辑", new Object[0]);
      } else {
         t.setName(param.getName());
         if (param.getStatus() != null) {
            t.setStatus(param.getStatus());
         }

         if (param.getTemplateInfo() != null) {
            t.setExtJson(JSONUtil.toJsonStr(param.getTemplateInfo()));
         }

         this.updateById(t);
      }
   }

   @Override
   public DomainTemplate getById(MemberDomainTemplateIdParam param) {
      return this.getTemplateAndCheckMember(param.getId());
   }

   @Override
   public XinwangSubmitResult submitToUpstream(MemberDomainTemplateIdParam param) {
      DomainTemplate t = this.getTemplateAndCheckMember(param.getId());
      return this.doSubmitToUpstream(t);
   }

   @Override
   public XinwangSubmitResult submitTemplate(DomainTemplate template) {
      return this.doSubmitToUpstream(template);
   }

   private XinwangSubmitResult doSubmitToUpstream(DomainTemplate t) {
      String primaryProvider = StrUtil.nullToEmpty(this.devConfigApi.getValueByKey("DOMAIN_REGISTER_PRIMARY_PROVIDER")).trim().toUpperCase();
      if (!"XINWANG".equals(primaryProvider)) {
         throw new CommonException("当前首选通道不是新网，无法提交模板。请先在后台将「域名注册首选接口商」配置为新网（XINWANG）", new Object[0]);
      } else if (StrUtil.isBlank(t.getExtJson())) {
         throw new CommonException("模板信息为空，请先完善模板资料", new Object[0]);
      } else {
         XinwangTemplateCreateDto dto = (XinwangTemplateCreateDto)JSONUtil.toBean(t.getExtJson(), XinwangTemplateCreateDto.class);
         if (StrUtil.isBlank(dto.getTemplateName())) {
            dto.setTemplateName(t.getName());
         }

         String organizeFileBase64 = this.resolveOrganizeFileBase64(dto.getOrganizeFile());
         if (StrUtil.isBlank(organizeFileBase64)) {
            throw new CommonException("请先上传注册所有人证件扫描件（organizeFile）后再提交", new Object[0]);
         } else {
            dto.setOrganizeFile(organizeFileBase64);
            Map<String, Object> body = this.dtoToCreateBody(dto);
            String appId = this.devConfigApi.getValueByKey("DOMAIN_XINWANG_APP_ID");
            String appKey = this.devConfigApi.getValueByKey("DOMAIN_XINWANG_APP_KEY");
            XinwangTemplateApiClient.XinwangTemplateCreateResult result = this.xinwangTemplateApiClient.createTemplate(appId, appKey, body);
            if (!"0".equals(result.getCode())) {
               String upstreamMsg = StrUtil.blankToDefault(result.getMsg(), "未知错误");
               throw new CommonException("新网提交失败：{}（code={}）", new Object[]{upstreamMsg, result.getCode()});
            } else {
               t.setAuditStatus(DomainTemplateAuditStatusEnum.AUDITING.getCode());
               this.updateById(t);
               XinwangSubmitResult out = new XinwangSubmitResult();
               out.setCode(result.getCode());
               out.setMsg(result.getMsg());
               out.setRequestId(result.getRequestId());
               out.setSuccess(true);
               return out;
            }
         }
      }
   }

   @Override
   public ChinaRegionsVO getChinaRegions() {
      String primaryProvider = StrUtil.nullToEmpty(this.devConfigApi.getValueByKey("DOMAIN_REGISTER_PRIMARY_PROVIDER")).trim().toUpperCase();
      if (!"XINWANG".equals(primaryProvider)) {
         throw new CommonException("当前首选通道不是新网，无法获取省市级联。请先在后台将「域名注册首选接口商」配置为新网（XINWANG）", new Object[0]);
      } else {
         String appId = this.devConfigApi.getValueByKey("DOMAIN_XINWANG_APP_ID");
         String appKey = this.devConfigApi.getValueByKey("DOMAIN_XINWANG_APP_KEY");
         XinwangTemplateApiClient.XinwangTemplateCreateResult result = this.xinwangTemplateApiClient.getChinaRegions(appId, appKey);
         return this.mapToChinaRegionsVO(result);
      }
   }

   private ChinaRegionsVO mapToChinaRegionsVO(XinwangTemplateApiClient.XinwangTemplateCreateResult result) {
      ChinaRegionsVO vo = new ChinaRegionsVO();
      vo.setSuccess("0".equals(result.getCode()));
      vo.setMsg(result.getMsg());
      vo.setRegions(new ArrayList<>());
      Object data = result.getData();
      if (data == null) {
         return vo;
      } else {
         JSONArray provinceArray = null;
         if (data instanceof JSONArray arr) {
            if (arr.size() == 1) {
               JSONObject first = arr.getJSONObject(0);
               if (this.isChinaRoot(first)) {
                  provinceArray = this.getChildrenArray(first);
               }

               if (provinceArray == null) {
                  provinceArray = arr;
               }
            } else {
               provinceArray = arr;
            }
         } else if (data instanceof JSONObject jo) {
            if (jo.get("regions") instanceof JSONArray) {
               provinceArray = jo.getJSONArray("regions");
            } else if (jo.get("data") instanceof JSONArray) {
               provinceArray = jo.getJSONArray("data");
            } else if (jo.get("provinces") instanceof JSONArray) {
               provinceArray = jo.getJSONArray("provinces");
            } else if (jo.get("list") instanceof JSONArray) {
               provinceArray = jo.getJSONArray("list");
            }

            if (provinceArray != null && provinceArray.size() == 1) {
               JSONObject firstx = provinceArray.getJSONObject(0);
               if (this.isChinaRoot(firstx)) {
                  provinceArray = this.getChildrenArray(firstx);
               }
            }
         }

         if (provinceArray != null) {
            for (int i = 0; i < provinceArray.size(); i++) {
               RegionProvinceVO province = this.mapJsonToProvince(provinceArray.getJSONObject(i));
               if (province != null) {
                  vo.getRegions().add(province);
               }
            }
         }

         return vo;
      }
   }

   private boolean isChinaRoot(JSONObject o) {
      String v = this.firstNonBlank(o, "code", "value");
      String l = this.firstNonBlank(o, "name", "label");
      return "910000".equals(v) || "中国".equals(l);
   }

   private JSONArray getChildrenArray(JSONObject o) {
      String[] keys = new String[]{"provinces", "children", "cities", "cityList", "childrenList", "list", "sub", "regionList", "items", "data"};

      for (String k : keys) {
         Object c = o.get(k);
         if (c instanceof JSONArray && ((JSONArray)c).size() > 0) {
            return (JSONArray)c;
         }
      }

      for (String kx : keys) {
         Object c = o.get(kx);
         if (c instanceof JSONArray) {
            return (JSONArray)c;
         }
      }

      return new JSONArray();
   }

   private RegionProvinceVO mapJsonToProvince(JSONObject o) {
      String value = this.firstNonBlank(o, "abc", "value", "code", "publicProvinceUk", "provinceCode", "provinceUk");
      String label = this.firstNonBlank(o, "name", "label", "provinceName", "nameCn", "text");
      if (value == null && label == null) {
         return null;
      } else {
         RegionProvinceVO p = new RegionProvinceVO();
         p.setValue(StrUtil.nullToEmpty(value));
         p.setLabel(StrUtil.nullToEmpty(label));
         p.setChildren(new ArrayList<>());
         Object children = o.get("cities");
         if (children == null) {
            children = o.get("children");
         }

         if (children == null) {
            children = o.get("cityList");
         }

         if (children == null) {
            children = o.get("childrenList");
         }

         if (children instanceof JSONArray arr) {
            for (int i = 0; i < arr.size(); i++) {
               Object c = arr.get(i);
               if (c instanceof JSONObject) {
                  RegionCityVO city = this.mapJsonToCity((JSONObject)c);
                  if (city != null) {
                     p.getChildren().add(city);
                  }
               }
            }
         }

         return p;
      }
   }

   private RegionCityVO mapJsonToCity(JSONObject o) {
      String value = this.firstNonBlank(o, "abc", "value", "code", "publicCityUk", "cityCode", "cityUk");
      String label = this.firstNonBlank(o, "name", "label", "cityName", "nameCn", "text");
      return value == null && label == null ? null : new RegionCityVO(StrUtil.nullToEmpty(value), StrUtil.nullToEmpty(label));
   }

   private String firstNonBlank(JSONObject o, String... keys) {
      for (String k : keys) {
         String v = o.getStr(k);
         if (StrUtil.isNotBlank(v)) {
            return v;
         }
      }

      return null;
   }

   @Override
   public void delete(MemberDomainTemplateIdParam param) {
      this.getTemplateAndCheckMember(param.getId());
      this.removeById(param.getId());
   }

   private String getCurrentMemberId() {
      String memberId = this.currentMemberIdApi.getCurrentMemberId();
      if (StrUtil.isBlank(memberId)) {
         throw new CommonException("请先登录", new Object[0]);
      } else {
         return memberId;
      }
   }

   private DomainTemplate getTemplateAndCheckMember(String id) {
      DomainTemplate t = (DomainTemplate)this.getById(id);
      if (t == null) {
         throw new CommonException("模板不存在", new Object[0]);
      } else {
         String memberId = this.getCurrentMemberId();
         if (!memberId.equals(t.getMemberId())) {
            throw new CommonException("无权操作该模板", new Object[0]);
         } else {
            return t;
         }
      }
   }

   private Map<String, Object> dtoToCreateBody(XinwangTemplateCreateDto dto) {
      Map<String, Object> map = new LinkedHashMap<>();
      map.put("templateName", dto.getTemplateName());
      map.put("userType", StrUtil.nullToEmpty(dto.getUserType()));
      map.put("organizeNameCn", dto.getOrganizeNameCn());
      map.put("organizeNameUk", dto.getOrganizeNameUk());
      map.put("userNameCn", dto.getUserNameCn());
      map.put("userSurnameUk", dto.getUserSurnameUk());
      map.put("userNameUk", dto.getUserNameUk());
      map.put("publicCountryUk", StrUtil.nullToEmpty(dto.getPublicCountryUk()));
      map.put("publicProvinceUk", StrUtil.nullToEmpty(dto.getPublicProvinceUk()));
      map.put("publicCityUk", StrUtil.nullToEmpty(dto.getPublicCityUk()));
      map.put("publicStreetCn", dto.getPublicStreetCn());
      map.put("publicStreetUk", dto.getPublicStreetUk());
      map.put("publicZipCode", dto.getPublicZipCode());
      map.put("userPhoneInter", StrUtil.blankToDefault(dto.getUserPhoneInter(), "86"));
      map.put("userPhoneArea", dto.getUserPhoneArea());
      map.put("userPhoneNumber", dto.getUserPhoneNumber());
      map.put("userPhoneExt", StrUtil.blankToDefault(dto.getUserPhoneExt(), "0"));
      map.put("userFaxInter", StrUtil.blankToDefault(dto.getUserFaxInter(), "86"));
      map.put("userFaxArea", dto.getUserFaxArea());
      map.put("userFaxNumber", dto.getUserFaxNumber());
      map.put("userFaxExt", StrUtil.blankToDefault(dto.getUserFaxExt(), "0"));
      map.put("userEmail", dto.getUserEmail());
      map.put("idType", StrUtil.nullToEmpty(dto.getIdType()));
      map.put("organCode", dto.getOrganCode());
      map.put("organizeFile", dto.getOrganizeFile());
      return map;
   }

   private String resolveOrganizeFileBase64(String organizeFile) {
      if (StrUtil.isBlank(organizeFile)) {
         return null;
      } else {
         String value = organizeFile.trim();
         if (value.startsWith("data:")) {
            return this.stripDataUrlPrefix(value);
         } else if (this.looksLikeHttpUrl(value)) {
            String fileId = this.extractFileIdFromUrl(value);
            return StrUtil.isNotBlank(fileId)
               ? this.extractBase64FromFile(this.devFileApi.getFileById(fileId))
               : this.extractBase64FromFile(this.devFileApi.getFileByUrl(value));
         } else {
            BaseDevFile fileById = this.devFileApi.getFileById(value);
            return fileById != null ? this.extractBase64FromFile(fileById) : value;
         }
      }
   }

   private String extractBase64FromFile(BaseDevFile file) {
      return file != null && !StrUtil.isBlank(file.getThumbnail()) ? this.stripDataUrlPrefix(file.getThumbnail()) : null;
   }

   private String stripDataUrlPrefix(String value) {
      int commaIndex = value.indexOf(44);
      return commaIndex >= 0 && value.substring(0, commaIndex).contains("base64") ? value.substring(commaIndex + 1) : value;
   }

   private boolean looksLikeHttpUrl(String value) {
      return StrUtil.startWithAnyIgnoreCase(value, new CharSequence[]{"http://", "https://"});
   }

   private String extractFileIdFromUrl(String value) {
      try {
         URI uri = URI.create(value);
         String query = uri.getQuery();
         if (StrUtil.isBlank(query)) {
            return null;
         }

         for (String pair : query.split("&")) {
            if (!StrUtil.isBlank(pair)) {
               String[] kv = pair.split("=", 2);
               if (kv.length == 2 && "id".equalsIgnoreCase(kv[0]) && StrUtil.isNotBlank(kv[1])) {
                  return URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
               }
            }
         }
      } catch (Exception var9) {
      }

      return null;
   }

   @Override
   public DomainTemplate syncTemplateStatus(MemberDomainTemplateIdParam param) {
      String primaryProvider = StrUtil.nullToEmpty(this.devConfigApi.getValueByKey("DOMAIN_REGISTER_PRIMARY_PROVIDER")).trim().toUpperCase();
      if (!"XINWANG".equals(primaryProvider)) {
         throw new CommonException("当前首选通道不是新网，无法同步模板状态。请先在后台将「域名注册首选接口商」配置为新网（XINWANG）", new Object[0]);
      } else {
         DomainTemplate t = this.getTemplateAndCheckMember(param.getId());
         String templateName = StrUtil.nullToEmpty(t.getName());
         String appId = this.devConfigApi.getValueByKey("DOMAIN_XINWANG_APP_ID");
         String appKey = this.devConfigApi.getValueByKey("DOMAIN_XINWANG_APP_KEY");
         XinwangTemplateApiClient.XinwangTemplateCreateResult result = this.xinwangTemplateApiClient.queryTemplate(appId, appKey, templateName);
         if (!"0".equals(result.getCode())) {
            throw new CommonException("新网查询模板失败：{}", new Object[]{StrUtil.blankToDefault(result.getMsg(), "未知错误")});
         } else {
            if (result.getData() instanceof JSONObject jo) {
               String verifyStatus = jo.getStr("verifyStatus");
               Integer auditStatus = this.mapXinwangVerifyStatusToLocal(verifyStatus);
               if (auditStatus != null) {
                  t.setAuditStatus(auditStatus);
               }

               t.setAuditReason(StrUtil.nullToEmpty(jo.getStr("reason")));
            }

            this.updateById(t);
            return (DomainTemplate)this.getById(t.getId());
         }
      }
   }

   @Override
   public DomainTemplate syncTemplateStatusByTemplateId(String templateId) {
      if (StrUtil.isBlank(templateId)) {
         return null;
      } else {
         DomainTemplate t = (DomainTemplate)this.getById(templateId);
         if (t == null) {
            return null;
         } else {
            String primaryProvider = StrUtil.nullToEmpty(this.devConfigApi.getValueByKey("DOMAIN_REGISTER_PRIMARY_PROVIDER")).trim().toUpperCase();
            if (!"XINWANG".equals(primaryProvider)) {
               return null;
            } else {
               String templateName = StrUtil.nullToEmpty(t.getName());
               String appId = this.devConfigApi.getValueByKey("DOMAIN_XINWANG_APP_ID");
               String appKey = this.devConfigApi.getValueByKey("DOMAIN_XINWANG_APP_KEY");
               XinwangTemplateApiClient.XinwangTemplateCreateResult result = this.xinwangTemplateApiClient.queryTemplate(appId, appKey, templateName);
               if (!"0".equals(result.getCode())) {
                  return null;
               } else {
                  if (result.getData() instanceof JSONObject jo) {
                     String verifyStatus = jo.getStr("verifyStatus");
                     Integer auditStatus = this.mapXinwangVerifyStatusToLocal(verifyStatus);
                     if (auditStatus != null) {
                        t.setAuditStatus(auditStatus);
                     }

                     t.setAuditReason(StrUtil.nullToEmpty(jo.getStr("reason")));
                  }

                  this.updateById(t);
                  return (DomainTemplate)this.getById(t.getId());
               }
            }
         }
      }
   }

   private Integer mapXinwangVerifyStatusToLocal(String verifyStatus) {
      if (StrUtil.isBlank(verifyStatus)) {
         return null;
      } else {
         String var2 = verifyStatus.trim().toLowerCase();

         return switch (var2) {
            case "notupload" -> DomainTemplateAuditStatusEnum.NOT_SUBMITTED.getCode();
            case "auditing" -> DomainTemplateAuditStatusEnum.AUDITING.getCode();
            case "pass" -> DomainTemplateAuditStatusEnum.APPROVED.getCode();
            case "unpass" -> DomainTemplateAuditStatusEnum.REJECTED.getCode();
            default -> null;
         };
      }
   }
}
