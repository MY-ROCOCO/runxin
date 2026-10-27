package com.rxinyun.admin.business.modular.independent.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.independent.entity.Independent;
import com.rxinyun.admin.business.modular.independent.entity.IndependentIp;
import com.rxinyun.admin.business.modular.independent.mapper.IndependentIpMapper;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpAddParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpDeleteParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpEditParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpIdParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpPageParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpQueryParam;
import com.rxinyun.admin.business.modular.independent.service.IndependentIpService;
import com.rxinyun.admin.business.modular.independent.service.IndependentService;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IndependentIpServiceImpl extends ServiceImpl<IndependentIpMapper, IndependentIp> implements IndependentIpService {
   private static final Logger log = LoggerFactory.getLogger(IndependentIpServiceImpl.class);
   @Resource
   private IndependentIpMapper independentIpMapper;
   @Resource
   private IndependentService independentService;

   @Override
   public Page<IndependentIp> page(IndependentIpPageParam pageParam) {
      QueryWrapper<IndependentIp> queryWrapper = new QueryWrapper().checkSqlInjection();
      Independent memberIndependent = this.independentService.queryEntity(pageParam.getServerId());
      if (null == memberIndependent) {
         throw new CommonException("服务器不存在，serverId值为：{}", new Object[]{pageParam.getServerId()});
      } else {
         if (ObjectUtil.isNotEmpty(pageParam.getType())) {
            queryWrapper.lambda().and(q -> q.eq(IndependentIp::getType, pageParam.getType()));
         }

         if (ObjectUtil.isNotEmpty(pageParam.getServerId())) {
            queryWrapper.lambda().and(q -> q.eq(IndependentIp::getServerId, pageParam.getServerId()));
         }

         ((LambdaQueryWrapper<IndependentIp>)((LambdaQueryWrapper<IndependentIp>)queryWrapper.lambda().orderByDesc(IndependentIp::getIsMain)).orderByDesc(CommonEntity::getUpdateTime))
            .orderByDesc(IndependentIp::getId);
         return (Page<IndependentIp>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(IndependentIpAddParam serverIpAddParam) {
      String ip = serverIpAddParam.getIp();
      if (StrUtil.isNotBlank(ip) && ip.contains("-")) {
         String[] parts = ip.split("-");
         if (parts.length == 2) {
            String startIp = parts[0];
            String endPart = parts[1];
            String[] startParts = startIp.split("\\.");
            String endIp = startParts[0] + "." + startParts[1] + "." + startParts[2] + "." + endPart;
            List<IndependentIp> ipList = this.generateIpRange(serverIpAddParam, startIp, endIp);
            this.checkIfIpsExist(ipList.stream().map(IndependentIp::getIp).collect(Collectors.toList()));
            log.info("批量新增IP数据，数量:{}", ipList.size());
            this.saveBatch(ipList);
            return;
         }
      }

      this.checkIfIpExists(ip);
      IndependentIp publicNetwork = (IndependentIp)BeanUtil.toBean(serverIpAddParam, IndependentIp.class);
      log.info("新增单个IP数据:{}", JSON.toJSONString(publicNetwork));
      this.save(publicNetwork);
   }

   private void checkIfIpsExist(List<String> ips) {
      if (!CollectionUtils.isEmpty(ips)) {
         LambdaQueryWrapper<IndependentIp> queryWrapper = new LambdaQueryWrapper();
         queryWrapper.in(IndependentIp::getIp, ips);
         List<IndependentIp> existingIps = this.list(queryWrapper);
         if (CollectionUtils.isNotEmpty(existingIps)) {
            String existingIpList = existingIps.stream().map(IndependentIp::getIp).collect(Collectors.joining(", "));
            throw new CommonException("以下IP地址已存在: {}", new Object[]{existingIpList});
         }
      }
   }

   private void checkIfIpExists(String ip) {
      LambdaQueryWrapper<IndependentIp> queryWrapper = new LambdaQueryWrapper();
      queryWrapper.eq(IndependentIp::getIp, ip);
      if (this.count(queryWrapper) > 0L) {
         throw new CommonException("IP地址 {} 已存在", new Object[]{ip});
      }
   }

   private List<IndependentIp> generateIpRange(IndependentIpAddParam param, String startIp, String endIp) {
      List<IndependentIp> result = new ArrayList<>();
      long startIpLong = this.ipToLong(startIp);
      long endIpLong = this.ipToLong(endIp);
      if (startIpLong > endIpLong) {
         throw new CommonException("起始IP不能大于结束IP", new Object[0]);
      } else {
         for (long i = startIpLong; i <= endIpLong; i++) {
            IndependentIp ipEntity = (IndependentIp)BeanUtil.toBean(param, IndependentIp.class);
            ipEntity.setIp(this.longToIp(i));
            ipEntity.setId(null);
            result.add(ipEntity);
         }

         return result;
      }
   }

   private long ipToLong(String ipAddress) {
      String[] parts = ipAddress.split("\\.");
      long result = 0L;

      for (int i = 0; i < 4; i++) {
         result |= Long.parseLong(parts[i]) << 24 - 8 * i;
      }

      return result & 4294967295L;
   }

   private String longToIp(long ip) {
      return (ip >> 24 & 255L) + "." + (ip >> 16 & 255L) + "." + (ip >> 8 & 255L) + "." + (ip & 255L);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(IndependentIpEditParam serverIpEditParam) {
      IndependentIp serverIp = this.queryEntity(serverIpEditParam.getId());
      BeanUtil.copyProperties(serverIpEditParam, serverIp, new String[0]);
      this.updateById(serverIp);
   }

   @Override
   public IndependentIp detail(IndependentIpIdParam serverIpIdParam) {
      return this.queryEntity(serverIpIdParam.getId());
   }

   @Override
   public IndependentIp queryEntity(String id) {
      IndependentIp serverIp = (IndependentIp)this.getById(id);
      if (ObjectUtil.isEmpty(serverIp)) {
         throw new CommonException("服务器IP不存在，id值为：{}", new Object[]{id});
      } else {
         return serverIp;
      }
   }

   @Override
   public List<IndependentIp> queryByServerId(IndependentIpQueryParam queryParam) {
      LambdaQueryWrapper<IndependentIp> queryWrapper = (LambdaQueryWrapper<IndependentIp>)((LambdaQueryWrapper<IndependentIp>)new LambdaQueryWrapper<IndependentIp>()
            .eq(IndependentIp::getServerId, queryParam.getServerId()))
         .eq(IndependentIp::getType, queryParam.getType());
      return this.list(queryWrapper);
   }

   @Override
   public List<IndependentIp> listAllByServerId(String serverId) {
      return (List<IndependentIp>)(StrUtil.isBlank(serverId)
         ? new ArrayList<>()
         : this.list((Wrapper<IndependentIp>)new LambdaQueryWrapper<IndependentIp>().eq(IndependentIp::getServerId, serverId)));
   }

   @Override
   public List<String> listServerIdByIp(String ip) {
      if (StrUtil.isBlank(ip)) {
         return new ArrayList<>();
      } else {
         LambdaQueryWrapper<IndependentIp> q = ((LambdaQueryWrapper<IndependentIp>)new LambdaQueryWrapper<IndependentIp>().like(IndependentIp::getIp, ip))
            .select(IndependentIp::getServerId);
         return this.list(q).stream().map(IndependentIp::getServerId).distinct().collect(Collectors.toList());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<IndependentIpDeleteParam> serverIpIdParamList) {
      log.info("批量删除独立服务器IP，参数：{}", JSON.toJSONString(serverIpIdParamList));
      List<String> serverIpIdList = CollStreamUtil.toList(serverIpIdParamList, IndependentIpDeleteParam::getId);
      if (ObjectUtil.isNotEmpty(serverIpIdList)) {
         this.removeByIds(serverIpIdList);
      }
   }

   @Override
   public void setMainIp(IndependentIpIdParam independentIpIdParam) {
      LambdaUpdateWrapper<IndependentIp> updateWrapper = (LambdaUpdateWrapper<IndependentIp>)((LambdaUpdateWrapper<IndependentIp>)((LambdaUpdateWrapper<IndependentIp>)new LambdaUpdateWrapper<IndependentIp>()
               .eq(IndependentIp::getServerId, independentIpIdParam.getServerId()))
            .set(IndependentIp::getIsMain, 0))
         .set(CommonEntity::getUpdateTime, new Date());
      this.update(updateWrapper);
      IndependentIp independentIp = this.queryEntity(independentIpIdParam.getId());
      independentIp.setIsMain(independentIpIdParam.getIsMain());
      independentIp.setUpdateTime(new Date());
      this.updateById(independentIp);
      Independent independent = new Independent();
      independent.setId(independentIp.getServerId());
      independent.setServerIp(independentIp.getIp());
      independent.setUpdateTime(new Date());
      this.independentService.updateById(independent);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void replaceFromResourcePoolUpstreamSnapshot(String serverId, JSONObject dataSnapshot) {
      if (!StrUtil.isBlank(serverId) && dataSnapshot != null) {
         this.independentIpMapper.deleteByServerId(serverId);
         JSONArray ipList = dataSnapshot.getJSONArray("ipList");
         boolean hasPublicInList = false;
         if (ipList != null && !ipList.isEmpty()) {
            for (int i = 0; i < ipList.size(); i++) {
               JSONObject ipObj = ipList.getJSONObject(i);
               String ipType = ipObj.getString("ipType");
               if (StrUtil.isBlank(ipType)) {
                  ipType = ipObj.getString("type");
               }

               String ipTypeUpper = ipType != null ? ipType.trim().toUpperCase() : "";
               if ("PUBLIC".equals(ipTypeUpper) || "PUBLIC_IP".equals(ipTypeUpper) || "WAN".equals(ipTypeUpper)) {
                  hasPublicInList = true;
                  break;
               }
            }
         }

         boolean firstPublic = true;
         boolean firstPrivate = true;
         if (ipList != null && !ipList.isEmpty()) {
            for (int i = 0; i < ipList.size(); i++) {
               JSONObject ipObjx = ipList.getJSONObject(i);
               String ip = ipObjx.getString("ip");
               if (StrUtil.isBlank(ip)) {
                  ip = ipObjx.getString("ipAddress");
               }

               if (!StrUtil.isBlank(ip)) {
                  String ipTypex = ipObjx.getString("ipType");
                  if (StrUtil.isBlank(ipTypex)) {
                     ipTypex = ipObjx.getString("type");
                  }

                  String ipTypeUpper = ipTypex != null ? ipTypex.trim().toUpperCase() : "";
                  String type;
                  if (!"PRIVATE".equals(ipTypeUpper) && !"PRIVATE_IP".equals(ipTypeUpper) && !"LAN".equals(ipTypeUpper)) {
                     type = "PUBLIC";
                  } else {
                     type = "PRIVATE";
                  }

                  int isMain;
                  if ("PUBLIC".equals(type)) {
                     isMain = firstPublic ? 1 : 0;
                     firstPublic = false;
                  } else {
                     isMain = !hasPublicInList && firstPrivate ? 1 : 0;
                     firstPrivate = false;
                  }

                  String mask = firstNonBlank(ipObjx.getString("subnetMask"), ipObjx.getString("netmask"), ipObjx.getString("mask"));
                  IndependentIp row = new IndependentIp();
                  row.setId(SnowflakeIdGenerator.generateId());
                  row.setServerId(serverId);
                  row.setIp(ip.trim());
                  row.setType(type);
                  row.setGateway(StrUtil.blankToDefault(ipObjx.getString("gateway"), ""));
                  row.setMask(StrUtil.blankToDefault(mask, ""));
                  row.setMac(StrUtil.blankToDefault(ipObjx.getString("mac"), ""));
                  row.setVlanId(0);
                  row.setIsMain(isMain);
                  this.save(row);
               }
            }
         } else {
            String fallback = firstNonBlank(dataSnapshot.getString("serverIp"), dataSnapshot.getString("publicIp"), dataSnapshot.getString("public_ip"));
            if (StrUtil.isBlank(fallback)) {
               log.debug("资源池独立机快照无 ipList 且无 serverIp/publicIp，已清空 BUSINESS_INDEPENDENT_IP，serverId={}", serverId);
            } else {
               IndependentIp row = new IndependentIp();
               row.setId(SnowflakeIdGenerator.generateId());
               row.setServerId(serverId);
               row.setIp(fallback.trim());
               row.setType("PUBLIC");
               row.setGateway("");
               row.setMask("");
               row.setIsMain(1);
               row.setVlanId(0);
               this.save(row);
            }
         }
      }
   }

   private static String firstNonBlank(String... vals) {
      if (vals == null) {
         return null;
      } else {
         for (String v : vals) {
            if (StrUtil.isNotBlank(v)) {
               return v;
            }
         }

         return null;
      }
   }
}
