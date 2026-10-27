package com.rxinyun.admin.production.modular.esc.ip.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.esc.ip.entity.PublicNetwork;
import com.rxinyun.admin.production.modular.esc.ip.mapper.PublicNetworkMapper;
import com.rxinyun.admin.production.modular.esc.ip.param.PublicNetworkAddParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PublicNetworkEditParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PublicNetworkIdParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PublicNetworkPageParam;
import com.rxinyun.admin.production.modular.esc.ip.service.PublicNetworkService;
import com.rxinyun.business.api.ServerIpApi;
import com.rxinyun.common.enums.CommonEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublicNetworkServiceImpl extends ServiceImpl<PublicNetworkMapper, PublicNetwork> implements PublicNetworkService {
   private static final Logger log = LoggerFactory.getLogger(PublicNetworkServiceImpl.class);
   private static final Pattern IP_PATTERN = Pattern.compile("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
   @Resource
   private PublicNetworkMapper publicNetworkMapper;
   @Resource
   private ServerIpApi serverIpApi;

   @Override
   public Page<PublicNetwork> page(PublicNetworkPageParam pageParam) {
      Page<PublicNetwork> page = new Page(pageParam.getCurrent().intValue(), pageParam.getSize().intValue());
      Page<PublicNetwork> pages = this.publicNetworkMapper.selectPage(page, pageParam);

      for (PublicNetwork publicNetwork : pages.getRecords()) {
         if (Integer.valueOf(CommonEnum.YES.getCode()).equals(publicNetwork.getEnable())) {
            publicNetwork.setHostName(this.serverIpApi.getHostNameByIp(publicNetwork.getIp(), "PUBLIC"));
         }
      }

      return pages;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<String> add(PublicNetworkAddParam publicNetworkAddParam) {
      log.info("新增公网IP数据:{}", JSON.toJSONString(publicNetworkAddParam));
      int count = 0;
      List<String> existingIps = new ArrayList<>();
      List<String> invalidIps = new ArrayList<>();
      if (publicNetworkAddParam.getIp().contains("-")) {
         String baseIp = publicNetworkAddParam.getIp().substring(0, publicNetworkAddParam.getIp().lastIndexOf(46) + 1);
         int start = Integer.parseInt(
            publicNetworkAddParam.getIp().substring(publicNetworkAddParam.getIp().lastIndexOf(46) + 1, publicNetworkAddParam.getIp().indexOf(45))
         );
         int end = Integer.parseInt(publicNetworkAddParam.getIp().substring(publicNetworkAddParam.getIp().indexOf(45) + 1));

         for (int i = start; i <= end; i++) {
            String ipAddress = baseIp + i;
            if (!IP_PATTERN.matcher(ipAddress).matches()) {
               invalidIps.add(ipAddress);
            } else if (this.exists(ipAddress)) {
               existingIps.add(ipAddress);
            } else {
               PublicNetwork publicNetwork = new PublicNetwork();
               BeanUtil.copyProperties(publicNetworkAddParam, publicNetwork, new String[0]);
               publicNetwork.setIp(ipAddress);
               log.info("新增公网IP: {}", JSON.toJSONString(publicNetwork));
               this.save(publicNetwork);
               count++;
            }
         }
      } else {
         String ipAddress = publicNetworkAddParam.getIp();
         if (!IP_PATTERN.matcher(ipAddress).matches()) {
            invalidIps.add(ipAddress);
         }

         if (this.exists(ipAddress)) {
            existingIps.add(ipAddress);
         } else {
            PublicNetwork publicNetwork = new PublicNetwork();
            BeanUtil.copyProperties(publicNetworkAddParam, publicNetwork, new String[0]);
            publicNetwork.setIp(ipAddress);
            log.info("新增公网IP: {}", JSON.toJSONString(publicNetwork));
            this.save(publicNetwork);
            count++;
         }
      }

      StringBuilder messageBuilder = new StringBuilder();
      if (!invalidIps.isEmpty()) {
         messageBuilder.append("以下IP地址格式非法: ").append(invalidIps).append("; ");
      }

      if (!existingIps.isEmpty()) {
         messageBuilder.append("以下IP地址已存在: ").append(existingIps).append("; ");
      }

      messageBuilder.append("本次新增成功: ").append(count);
      return CommonResult.ok(messageBuilder.toString());
   }

   public boolean exists(String ipAddress) {
      return this.publicNetworkMapper.findByIpAddress(ipAddress) != null;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(PublicNetworkEditParam publicNetworkEditParam) {
      PublicNetwork serverIp = this.queryEntity(publicNetworkEditParam.getId());
      BeanUtil.copyProperties(publicNetworkEditParam, serverIp, new String[0]);
      this.updateById(serverIp);
   }

   @Override
   public PublicNetwork detail(PublicNetworkIdParam serverIpIdParam) {
      return this.queryEntity(serverIpIdParam.getId());
   }

   @Override
   public PublicNetwork queryEntity(String id) {
      PublicNetwork serverIp = (PublicNetwork)this.getById(id);
      if (ObjectUtil.isEmpty(serverIp)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return serverIp;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<PublicNetworkIdParam> publicNetworkIdParamList) {
      List<String> publicNetworkIdList = CollStreamUtil.toList(publicNetworkIdParamList, PublicNetworkIdParam::getId);
      LambdaQueryWrapper<PublicNetwork> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.in(PublicNetwork::getId, publicNetworkIdList);
      List<PublicNetwork> serverIpList = this.list(lambdaQueryWrapper);
      if (serverIpList.stream().anyMatch(serverIp -> Integer.valueOf(CommonEnum.YES.getCode()).equals(serverIp.getEnable()))) {
         throw new CommonException("存在已占用公网IP，不允许删除", new Object[0]);
      } else {
         if (ObjectUtil.isNotEmpty(publicNetworkIdList)) {
            this.publicNetworkMapper.removeByIds(publicNetworkIdList);
         }
      }
   }

   @Override
   public Boolean isBindingPublicNetWork(String id) {
      if (ObjectUtil.isEmpty(id)) {
         throw new CommonException("宿主机id为空", new Object[0]);
      } else {
         QueryWrapper<PublicNetwork> queryWrapper = (QueryWrapper<PublicNetwork>)new QueryWrapper().eq("master_id", id);
         List<PublicNetwork> independentOsVersions = this.publicNetworkMapper.selectList(queryWrapper);
         return !independentOsVersions.isEmpty();
      }
   }
}
