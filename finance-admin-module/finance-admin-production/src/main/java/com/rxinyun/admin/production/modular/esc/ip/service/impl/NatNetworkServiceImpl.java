package com.rxinyun.admin.production.modular.esc.ip.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.esc.ip.entity.NatNetwork;
import com.rxinyun.admin.production.modular.esc.ip.mapper.NatNetworkMapper;
import com.rxinyun.admin.production.modular.esc.ip.param.NatNetworkAddParam;
import com.rxinyun.admin.production.modular.esc.ip.param.NatNetworkEditParam;
import com.rxinyun.admin.production.modular.esc.ip.param.NatNetworkIdParam;
import com.rxinyun.admin.production.modular.esc.ip.param.NatNetworkPageParam;
import com.rxinyun.admin.production.modular.esc.ip.service.NatNetworkService;
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
public class NatNetworkServiceImpl extends ServiceImpl<NatNetworkMapper, NatNetwork> implements NatNetworkService {
   private static final Logger log = LoggerFactory.getLogger(NatNetworkServiceImpl.class);
   private static final Pattern IP_PATTERN = Pattern.compile("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
   @Resource
   private NatNetworkMapper NatNetworkMapper;
   @Resource
   private ServerIpApi serverIpApi;

   @Override
   public Page<NatNetwork> page(NatNetworkPageParam pageParam) {
      Page<NatNetwork> page = new Page(pageParam.getCurrent().intValue(), pageParam.getSize().intValue());
      Page<NatNetwork> pages = this.NatNetworkMapper.selectPage(page, pageParam);

      for (NatNetwork natNetwork : pages.getRecords()) {
         if (Integer.valueOf(CommonEnum.YES.getCode()).equals(natNetwork.getEnable())) {
            natNetwork.setHostName(this.serverIpApi.getHostNameByIp(natNetwork.getIp(), "NAT"));
         }
      }

      return pages;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<String> add(NatNetworkAddParam natNetworkAddParam) {
      log.info("新增NAT IP数据:{}", JSON.toJSONString(natNetworkAddParam));
      int count = 0;
      List<String> existingIps = new ArrayList<>();
      List<String> invalidIps = new ArrayList<>();
      if (natNetworkAddParam.getIp().contains("-")) {
         String baseIp = natNetworkAddParam.getIp().substring(0, natNetworkAddParam.getIp().lastIndexOf(46) + 1);
         int start = Integer.parseInt(
            natNetworkAddParam.getIp().substring(natNetworkAddParam.getIp().lastIndexOf(46) + 1, natNetworkAddParam.getIp().indexOf(45))
         );
         int end = Integer.parseInt(natNetworkAddParam.getIp().substring(natNetworkAddParam.getIp().indexOf(45) + 1));

         for (int i = start; i <= end; i++) {
            String ipAddress = baseIp + i;
            if (!IP_PATTERN.matcher(ipAddress).matches()) {
               invalidIps.add(ipAddress);
            } else if (this.exists(ipAddress)) {
               existingIps.add(ipAddress);
            } else {
               NatNetwork natNetwork = new NatNetwork();
               BeanUtil.copyProperties(natNetworkAddParam, natNetwork, new String[0]);
               natNetwork.setIp(ipAddress);
               this.save(natNetwork);
               count++;
            }
         }
      } else {
         String ipAddress = natNetworkAddParam.getIp();
         if (!IP_PATTERN.matcher(ipAddress).matches()) {
            invalidIps.add(ipAddress);
         }

         if (this.exists(ipAddress)) {
            existingIps.add(ipAddress);
         } else {
            NatNetwork natNetwork = new NatNetwork();
            BeanUtil.copyProperties(natNetworkAddParam, natNetwork, new String[0]);
            natNetwork.setIp(ipAddress);
            this.save(natNetwork);
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
      return this.NatNetworkMapper.findByIpAddress(ipAddress) != null;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(NatNetworkEditParam NatNetworkEditParam) {
      NatNetwork serverIp = this.queryEntity(NatNetworkEditParam.getId());
      BeanUtil.copyProperties(NatNetworkEditParam, serverIp, new String[0]);
      this.updateById(serverIp);
   }

   @Override
   public NatNetwork detail(NatNetworkIdParam serverIpIdParam) {
      return this.queryEntity(serverIpIdParam.getId());
   }

   @Override
   public NatNetwork queryEntity(String id) {
      NatNetwork serverIp = (NatNetwork)this.getById(id);
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
   public void delete(List<NatNetworkIdParam> NatNetworkIdParamList) {
      List<String> NatNetworkIdList = CollStreamUtil.toList(NatNetworkIdParamList, NatNetworkIdParam::getId);
      LambdaQueryWrapper<NatNetwork> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.in(NatNetwork::getId, NatNetworkIdList);
      List<NatNetwork> serverIpList = this.list(lambdaQueryWrapper);
      if (serverIpList.stream().anyMatch(serverIp -> Integer.valueOf(CommonEnum.YES.getCode()).equals(serverIp.getEnable()))) {
         throw new CommonException("存在已占用NAT IP，不允许删除", new Object[0]);
      } else {
         if (ObjectUtil.isNotEmpty(NatNetworkIdList)) {
            this.NatNetworkMapper.removeByIds(NatNetworkIdList);
         }
      }
   }

   @Override
   public Boolean isBindingNatNetWork(String id) {
      if (ObjectUtil.isEmpty(id)) {
         throw new CommonException("宿主机id为空", new Object[0]);
      } else {
         QueryWrapper<NatNetwork> queryWrapper = (QueryWrapper<NatNetwork>)new QueryWrapper().eq("master_id", id);
         List<NatNetwork> independentOsVersions = this.NatNetworkMapper.selectList(queryWrapper);
         return !independentOsVersions.isEmpty();
      }
   }
}
