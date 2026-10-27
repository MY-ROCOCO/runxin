package com.rxinyun.admin.production.modular.esc.ip.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.esc.ip.entity.PrivateNetwork;
import com.rxinyun.admin.production.modular.esc.ip.mapper.PrivateNetworkMapper;
import com.rxinyun.admin.production.modular.esc.ip.param.PrivateNetworkAddParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PrivateNetworkEditParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PrivateNetworkIdParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PrivateNetworkPageParam;
import com.rxinyun.admin.production.modular.esc.ip.service.PrivateNetworkService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrivateNetworkServiceImpl extends ServiceImpl<PrivateNetworkMapper, PrivateNetwork> implements PrivateNetworkService {
   private static final Logger log = LoggerFactory.getLogger(PrivateNetworkServiceImpl.class);
   private static final Pattern IP_PATTERN = Pattern.compile("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
   @Resource
   private PrivateNetworkMapper PrivateNetworkMapper;
   @Autowired
   private PrivateNetworkMapper privateNetworkMapper;
   @Resource
   private ServerIpApi serverIpApi;

   @Override
   public Page<PrivateNetwork> page(PrivateNetworkPageParam pageParam) {
      Page<PrivateNetwork> page = new Page(pageParam.getCurrent().intValue(), pageParam.getSize().intValue());
      Page<PrivateNetwork> pages = this.PrivateNetworkMapper.selectPage(page, pageParam);

      for (PrivateNetwork privateNetwork : pages.getRecords()) {
         if (Integer.valueOf(CommonEnum.YES.getCode()).equals(privateNetwork.getEnable())) {
            privateNetwork.setHostName(this.serverIpApi.getHostNameByIp(privateNetwork.getIp(), "PRIVATE"));
         }
      }

      return pages;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<String> add(PrivateNetworkAddParam privateNetworkAddParam) {
      log.info("新增ip数据:{}", JSON.toJSONString(privateNetworkAddParam));
      int count = 0;
      List<String> existingIps = new ArrayList<>();
      List<String> invalidIps = new ArrayList<>();
      if (privateNetworkAddParam.getIp().contains("-")) {
         String baseIp = privateNetworkAddParam.getIp().substring(0, privateNetworkAddParam.getIp().lastIndexOf(46) + 1);
         int start = Integer.parseInt(
            privateNetworkAddParam.getIp().substring(privateNetworkAddParam.getIp().lastIndexOf(46) + 1, privateNetworkAddParam.getIp().indexOf(45))
         );
         int end = Integer.parseInt(privateNetworkAddParam.getIp().substring(privateNetworkAddParam.getIp().indexOf(45) + 1));

         for (int i = start; i <= end; i++) {
            String ipAddress = baseIp + i;
            if (!IP_PATTERN.matcher(ipAddress).matches()) {
               invalidIps.add(ipAddress);
            } else if (this.exists(ipAddress)) {
               existingIps.add(ipAddress);
            } else {
               PrivateNetwork privateNetwork = new PrivateNetwork();
               BeanUtil.copyProperties(privateNetworkAddParam, privateNetwork, new String[0]);
               privateNetwork.setIp(ipAddress);
               count++;
               this.save(privateNetwork);
            }
         }
      } else {
         String ipAddress = privateNetworkAddParam.getIp();
         if (!IP_PATTERN.matcher(ipAddress).matches()) {
            invalidIps.add(ipAddress);
         }

         if (this.exists(ipAddress)) {
            existingIps.add(ipAddress);
         } else {
            PrivateNetwork privateNetwork = new PrivateNetwork();
            BeanUtil.copyProperties(privateNetworkAddParam, privateNetwork, new String[0]);
            privateNetwork.setIp(ipAddress);
            count++;
            this.save(privateNetwork);
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
      return this.privateNetworkMapper.findByIpAddress(ipAddress) != null;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(PrivateNetworkEditParam PrivateNetworkEditParam) {
      PrivateNetwork PrivateNetwork = this.queryEntity(PrivateNetworkEditParam.getId());
      BeanUtil.copyProperties(PrivateNetworkEditParam, PrivateNetwork, new String[0]);
      this.updateById(PrivateNetwork);
   }

   @Override
   public PrivateNetwork detail(PrivateNetworkIdParam PrivateNetworkIdParam) {
      return this.queryEntity(PrivateNetworkIdParam.getId());
   }

   @Override
   public PrivateNetwork queryEntity(String id) {
      PrivateNetwork PrivateNetwork = (PrivateNetwork)this.getById(id);
      if (ObjectUtil.isEmpty(PrivateNetwork)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return PrivateNetwork;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<PrivateNetworkIdParam> PrivateNetworkIdParamList) {
      List<String> PrivateNetworkIdList = CollStreamUtil.toList(PrivateNetworkIdParamList, PrivateNetworkIdParam::getId);
      LambdaQueryWrapper<PrivateNetwork> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.in(PrivateNetwork::getId, PrivateNetworkIdList);
      List<PrivateNetwork> serverIpList = this.list(lambdaQueryWrapper);
      if (serverIpList.stream().anyMatch(serverIp -> Integer.valueOf(CommonEnum.YES.getCode()).equals(serverIp.getEnable()))) {
         throw new CommonException("存在已占用私网IP，不允许删除", new Object[0]);
      } else {
         if (ObjectUtil.isNotEmpty(PrivateNetworkIdList)) {
            this.PrivateNetworkMapper.removeByIds(PrivateNetworkIdList);
         }
      }
   }

   @Override
   public Boolean isBindingPrivateNetWork(String id) {
      if (ObjectUtil.isEmpty(id)) {
         throw new CommonException("宿主机id为空", new Object[0]);
      } else {
         QueryWrapper<PrivateNetwork> queryWrapper = (QueryWrapper<PrivateNetwork>)new QueryWrapper().eq("master_id", id);
         List<PrivateNetwork> independentOsVersions = this.privateNetworkMapper.selectList(queryWrapper);
         return !independentOsVersions.isEmpty();
      }
   }
}
