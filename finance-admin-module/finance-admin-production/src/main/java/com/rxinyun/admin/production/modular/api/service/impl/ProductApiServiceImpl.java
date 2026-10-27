package com.rxinyun.admin.production.modular.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.production.modular.api.param.ProductSyncParam;
import com.rxinyun.admin.production.modular.api.response.CloudServerAvailableRegionResponse;
import com.rxinyun.admin.production.modular.api.response.CloudServerLineResponse;
import com.rxinyun.admin.production.modular.api.response.CloudServerLineSkuResponse;
import com.rxinyun.admin.production.modular.api.response.CloudServerOsClassResponse;
import com.rxinyun.admin.production.modular.api.response.CloudServerOsVersionResponse;
import com.rxinyun.admin.production.modular.api.response.CloudServerRegionResponse;
import com.rxinyun.admin.production.modular.api.response.IndependentLineResponse;
import com.rxinyun.admin.production.modular.api.response.IndependentMachineResponse;
import com.rxinyun.admin.production.modular.api.response.IndependentOsClassResponse;
import com.rxinyun.admin.production.modular.api.response.IndependentOsVersionResponse;
import com.rxinyun.admin.production.modular.api.response.IndependentRegionResponse;
import com.rxinyun.admin.production.modular.api.service.ProductApiService;
import com.rxinyun.admin.production.modular.esc.available.entity.ServerAvailableRegion;
import com.rxinyun.admin.production.modular.esc.available.mapper.ServerAvailableRegionMapper;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionAddParam;
import com.rxinyun.admin.production.modular.esc.available.service.ServerAvailableRegionService;
import com.rxinyun.admin.production.modular.esc.cloud.entity.ServerLineSku;
import com.rxinyun.admin.production.modular.esc.cloud.entity.ServerLocalLine;
import com.rxinyun.admin.production.modular.esc.cloud.mapper.ServerLineSkuMapper;
import com.rxinyun.admin.production.modular.esc.cloud.mapper.ServerLocalLineMapper;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLineSkuAddParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLineAddParam;
import com.rxinyun.admin.production.modular.esc.cloud.service.ServerLineSkuService;
import com.rxinyun.admin.production.modular.esc.cloud.service.ServerLocalLineService;
import com.rxinyun.admin.production.modular.esc.os.entity.OsClass;
import com.rxinyun.admin.production.modular.esc.os.entity.OsVersion;
import com.rxinyun.admin.production.modular.esc.os.mapper.OsClassMapper;
import com.rxinyun.admin.production.modular.esc.os.mapper.OsVersionMapper;
import com.rxinyun.admin.production.modular.esc.os.param.OsClassAddParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsVersionAddParam;
import com.rxinyun.admin.production.modular.esc.os.service.OsClassService;
import com.rxinyun.admin.production.modular.esc.os.service.OsVersionService;
import com.rxinyun.admin.production.modular.esc.region.entity.ServerRegion;
import com.rxinyun.admin.production.modular.esc.region.mapper.ServerRegionMapper;
import com.rxinyun.admin.production.modular.esc.region.param.ServerRegionAddParam;
import com.rxinyun.admin.production.modular.esc.region.service.ServerRegionService;
import com.rxinyun.admin.production.modular.independent.cloud.entity.IndependentLine;
import com.rxinyun.admin.production.modular.independent.cloud.mapper.IndependentLineMapper;
import com.rxinyun.admin.production.modular.independent.cloud.param.IndependentLineAddParam;
import com.rxinyun.admin.production.modular.independent.cloud.service.IndependentLineService;
import com.rxinyun.admin.production.modular.independent.machine.entity.Machine;
import com.rxinyun.admin.production.modular.independent.machine.entity.MachineLine;
import com.rxinyun.admin.production.modular.independent.machine.mapper.MachineLineMapper;
import com.rxinyun.admin.production.modular.independent.machine.mapper.MachineMapper;
import com.rxinyun.admin.production.modular.independent.machine.param.MachineAddParam;
import com.rxinyun.admin.production.modular.independent.machine.service.MachineLineService;
import com.rxinyun.admin.production.modular.independent.machine.service.MachineService;
import com.rxinyun.admin.production.modular.independent.os.entity.IndependentOsClass;
import com.rxinyun.admin.production.modular.independent.os.entity.IndependentOsVersion;
import com.rxinyun.admin.production.modular.independent.os.mapper.OsIndependentClassMapper;
import com.rxinyun.admin.production.modular.independent.os.mapper.OsIndependentVersionMapper;
import com.rxinyun.admin.production.modular.independent.region.mapper.RegionMapper;
import com.rxinyun.admin.production.modular.independent.region.model.dto.RegionDTO;
import com.rxinyun.admin.production.modular.independent.region.model.entity.Region;
import com.rxinyun.admin.production.modular.independent.region.service.IRegionService;
import com.rxinyun.admin.production.modular.util.ResourcePoolDisplayHelper;
import com.rxinyun.common.context.ApiContext;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.dev.core.pojo.BaseResourcePool;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductApiServiceImpl implements ProductApiService {
   private static final Logger log = LoggerFactory.getLogger(ProductApiServiceImpl.class);
   private static final int SERVER_LINE_MONTH_DAY = 31;
   @Resource
   private ServerLocalLineMapper serverLocalLineMapper;
   @Resource
   private MachineMapper machineMapper;
   @Resource
   private MachineLineService machineLineService;
   @Resource
   private MachineLineMapper machineLineMapper;
   @Resource
   private MachineService machineService;
   @Resource
   private ResourcePoolApi resourcePoolApi;
   @Resource
   private ServerRegionService serverRegionService;
   @Resource
   private ServerRegionMapper serverRegionMapper;
   @Resource
   private ServerAvailableRegionService serverAvailableRegionService;
   @Resource
   private ServerAvailableRegionMapper serverAvailableRegionMapper;
   @Resource
   private IRegionService independentRegionService;
   @Resource
   private RegionMapper regionMapper;
   @Resource
   private IndependentLineService independentLineService;
   @Resource
   private IndependentLineMapper independentLineMapper;
   @Resource
   private ServerLocalLineService serverLocalLineService;
   @Resource
   private ServerLineSkuService serverLineSkuService;
   @Resource
   private ServerLineSkuMapper serverLineSkuMapper;
   @Resource
   private OsVersionMapper osVersionMapper;
   @Resource
   private OsVersionService osVersionService;
   @Resource
   private OsIndependentVersionMapper osIndependentVersionMapper;
   @Resource
   private com.rxinyun.admin.production.modular.independent.os.service.OsVersionService independentOsVersionService;
   @Resource
   private OsClassMapper osClassMapper;
   @Resource
   private OsClassService osClassService;
   @Resource
   private OsIndependentClassMapper osIndependentClassMapper;
   @Resource
   private com.rxinyun.admin.production.modular.independent.os.service.OsClassService independentOsClassService;

   @Override
   public List<CloudServerLineResponse> getCloudServerLines() {
      List<ServerLocalLine> lines = this.serverLocalLineMapper
         .selectList(
            (Wrapper<ServerLocalLine>)((LambdaQueryWrapper<ServerLocalLine>)((LambdaQueryWrapper<ServerLocalLine>)new LambdaQueryWrapper<ServerLocalLine>().eq(ServerLocalLine::getStatus, 1))
                  .eq(ServerLocalLine::getDockingStatus, 1))
               .orderByAsc(ServerLocalLine::getSortId)
         );
      return lines.stream()
         .map(
            line -> {
               CloudServerLineResponse response = (CloudServerLineResponse)BeanUtil.toBean(line, CloudServerLineResponse.class);
               response.setAppendIpPrice(line.getAppendIpPrice());
               List<ServerLineSku> skuList = this.serverLineSkuService.selectListByLineId(line.getId());
               if (ObjectUtil.isNotEmpty(skuList)) {
                  List<CloudServerLineSkuResponse> skuResponseList = skuList.stream()
                     .map(sku -> (CloudServerLineSkuResponse)BeanUtil.toBean(sku, CloudServerLineSkuResponse.class))
                     .collect(Collectors.toList());
                  response.setSkuList(skuResponseList);
               }

               return response;
            }
         )
         .collect(Collectors.toList());
   }

   @Override
   public List<CloudServerRegionResponse> getCloudServerRegions() {
      List<ServerRegion> regions = this.serverRegionMapper
         .selectList((Wrapper<ServerRegion>)((LambdaQueryWrapper<ServerRegion>)new LambdaQueryWrapper<ServerRegion>().eq(ServerRegion::getStatus, 1)).orderByAsc(ServerRegion::getSortId));
      return regions.stream().map(region -> (CloudServerRegionResponse)BeanUtil.toBean(region, CloudServerRegionResponse.class)).collect(Collectors.toList());
   }

   @Override
   public List<CloudServerAvailableRegionResponse> getCloudServerAvailableRegions() {
      List<ServerAvailableRegion> availableRegions = this.serverAvailableRegionMapper
         .selectList(
            (Wrapper<ServerAvailableRegion>)((LambdaQueryWrapper<ServerAvailableRegion>)new LambdaQueryWrapper<ServerAvailableRegion>().eq(ServerAvailableRegion::getStatus, 1)).orderByAsc(ServerAvailableRegion::getSortId)
         );
      return availableRegions.stream()
         .map(availableRegion -> (CloudServerAvailableRegionResponse)BeanUtil.toBean(availableRegion, CloudServerAvailableRegionResponse.class))
         .collect(Collectors.toList());
   }

   @Override
   public List<IndependentRegionResponse> getIndependentRegions() {
      List<Region> regions = this.regionMapper
         .selectList((Wrapper<Region>)((LambdaQueryWrapper<Region>)new LambdaQueryWrapper<Region>().eq(Region::getStatus, 1)).orderByAsc(Region::getSort));
      return regions.stream().map(region -> (IndependentRegionResponse)BeanUtil.toBean(region, IndependentRegionResponse.class)).collect(Collectors.toList());
   }

   @Override
   public List<IndependentLineResponse> getIndependentLines() {
      List<IndependentLine> lines = this.independentLineMapper
         .selectList((Wrapper<IndependentLine>)((LambdaQueryWrapper<IndependentLine>)new LambdaQueryWrapper<IndependentLine>().eq(IndependentLine::getStatus, 1)).orderByDesc(IndependentLine::getWeight));
      return lines.stream().map(line -> (IndependentLineResponse)BeanUtil.toBean(line, IndependentLineResponse.class)).collect(Collectors.toList());
   }

   @Override
   public List<CloudServerOsClassResponse> getCloudServerOsClasses() {
      List<OsClass> osClasses = this.osClassMapper
         .selectList((Wrapper<OsClass>)((LambdaQueryWrapper<OsClass>)new LambdaQueryWrapper<OsClass>().eq(OsClass::getStatus, 1)).orderByDesc(CommonEntity::getCreateTime));
      return osClasses.stream().map(osClass -> {
         CloudServerOsClassResponse r = (CloudServerOsClassResponse)BeanUtil.toBean(osClass, CloudServerOsClassResponse.class);
         ResourcePoolDisplayHelper.ifPresent(this.resourcePoolApi, r.getResourcePoolId(), pool -> {
            r.setResourcePoolType(pool.getPoolType());
            r.setResourcePoolName(pool.getName());
         });
         return r;
      }).collect(Collectors.toList());
   }

   @Override
   public List<CloudServerOsVersionResponse> getCloudServerOsVersions(String lineId) {
      LambdaQueryWrapper<OsVersion> queryWrapper = (LambdaQueryWrapper<OsVersion>)((LambdaQueryWrapper<OsVersion>)new LambdaQueryWrapper<OsVersion>().eq(OsVersion::getStatus, "1"))
         .orderByDesc(CommonEntity::getCreateTime);
      if (ObjectUtil.isNotEmpty(lineId)) {
         try {
            ServerLocalLine line = this.serverLocalLineService.queryEntity(lineId);
            if (line != null && ObjectUtil.isNotEmpty(line.getAvailableId())) {
               queryWrapper.like(OsVersion::getSupportedAvailabilityZone, line.getAvailableId());
            }
         } catch (Exception var4) {
            log.warn("查询线路信息失败，线路ID：{}，错误：{}", lineId, var4.getMessage());
         }
      }

      List<OsVersion> osVersions = this.osVersionMapper.selectList(queryWrapper);
      return osVersions.stream().map(osVersion -> {
         CloudServerOsVersionResponse response = (CloudServerOsVersionResponse)BeanUtil.toBean(osVersion, CloudServerOsVersionResponse.class);
         if (ObjectUtil.isNotEmpty(lineId)) {
            response.setLineId(lineId);
         }

         ResourcePoolDisplayHelper.ifPresent(this.resourcePoolApi, response.getResourcePoolId(), pool -> {
            response.setResourcePoolType(pool.getPoolType());
            response.setResourcePoolName(pool.getName());
         });
         return response;
      }).collect(Collectors.toList());
   }

   @Override
   public List<IndependentOsClassResponse> getIndependentOsClasses() {
      List<IndependentOsClass> osClasses = this.osIndependentClassMapper
         .selectList((Wrapper<IndependentOsClass>)((LambdaQueryWrapper<IndependentOsClass>)new LambdaQueryWrapper<IndependentOsClass>().eq(IndependentOsClass::getStatus, 1)).orderByDesc(CommonEntity::getCreateTime));
      return osClasses.stream().map(osClass -> {
         IndependentOsClassResponse r = (IndependentOsClassResponse)BeanUtil.toBean(osClass, IndependentOsClassResponse.class);
         ResourcePoolDisplayHelper.ifPresent(this.resourcePoolApi, r.getResourcePoolId(), pool -> {
            r.setResourcePoolType(pool.getPoolType());
            r.setResourcePoolName(pool.getName());
         });
         return r;
      }).collect(Collectors.toList());
   }

   @Override
   public List<IndependentOsVersionResponse> getIndependentOsVersions(String lineId) {
      LambdaQueryWrapper<IndependentOsVersion> queryWrapper = (LambdaQueryWrapper<IndependentOsVersion>)((LambdaQueryWrapper<IndependentOsVersion>)new LambdaQueryWrapper<IndependentOsVersion>()
            .eq(IndependentOsVersion::getStatus, 1))
         .orderByDesc(CommonEntity::getCreateTime);
      if (ObjectUtil.isNotEmpty(lineId)) {
         queryWrapper.apply("FIND_IN_SET({0}, REPLACE(line_id, ' ', '')) > 0", new Object[]{lineId});
      }

      List<IndependentOsVersion> osVersions = this.osIndependentVersionMapper.selectList(queryWrapper);
      return osVersions.stream().map(osVersion -> {
         IndependentOsVersionResponse r = (IndependentOsVersionResponse)BeanUtil.toBean(osVersion, IndependentOsVersionResponse.class);
         ResourcePoolDisplayHelper.ifPresent(this.resourcePoolApi, r.getResourcePoolId(), pool -> {
            r.setResourcePoolType(pool.getPoolType());
            r.setResourcePoolName(pool.getName());
         });
         return r;
      }).collect(Collectors.toList());
   }

   @Override
   public List<IndependentMachineResponse> getIndependentMachines() {
      List<Machine> machines = this.machineMapper
         .selectList(
            (Wrapper<Machine>)((LambdaQueryWrapper<Machine>)((LambdaQueryWrapper<Machine>)new LambdaQueryWrapper<Machine>().eq(Machine::getStatus, 1)).eq(Machine::getDockingStatus, 1))
               .orderByDesc(Machine::getWeight)
         );
      return machines.stream().map(machine -> {
         IndependentMachineResponse response = (IndependentMachineResponse)BeanUtil.toBean(machine, IndependentMachineResponse.class);
         List<MachineLine> machineLines = this.machineLineService.findLineMachine(machine.getId());
         List<String> lineNameList = new ArrayList<>();
         Set<String> regionNameSet = new HashSet<>();

         for (MachineLine machineLine : machineLines) {
            IndependentLine independentLine = (IndependentLine)this.independentLineMapper.selectById(machineLine.getLineId());
            if (independentLine != null) {
               lineNameList.add(independentLine.getName());
               Region region = this.independentRegionService.searchRegionById(independentLine.getRegionId());
               if (region != null) {
                  regionNameSet.add(region.getName());
               }
            }
         }

         response.setLineName(lineNameList);
         response.setRegionName(new ArrayList<>(regionNameSet));
         if (ObjectUtil.isNotEmpty(machine.getResourcePoolId())) {
            response.setResourcePoolId(machine.getResourcePoolId());
            BaseResourcePool resourcePool = this.resourcePoolApi.getById(machine.getResourcePoolId());
            if (ObjectUtil.isNotEmpty(resourcePool)) {
               response.setResourcePoolType(resourcePool.getPoolType());
               response.setResourcePoolName(resourcePool.getName());
            }
         }

         return response;
      }).collect(Collectors.toList());
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void syncCloudServerLines(ProductSyncParam syncParam) {
      Long memberNo = ApiContext.getMemberNo();
      if (ObjectUtil.isNotEmpty(memberNo)) {
         log.info("开始同步云服务器线路，会员编号：{}，产品数量：{}", memberNo, syncParam.getProducts().size());
      } else {
         log.info("开始同步云服务器线路（后台管理），产品数量：{}", syncParam.getProducts().size());
      }

      for (Object product : syncParam.getProducts()) {
         JSONObject productJson = JSONUtil.parseObj(product);
         CloudServerLineResponse lineResponse = (CloudServerLineResponse)JSONUtil.toBean(productJson, CloudServerLineResponse.class);
         if (ObjectUtil.isEmpty(lineResponse.getAvailableId()) && ObjectUtil.isNotEmpty(syncParam.getResourcePoolId())) {
            try {
               log.info("线路数据缺少availableId，从上游API重新获取完整数据，线路ID：{}", lineResponse.getId());
               List<CloudServerLineResponse> fullLines = this.queryCloudServerLinesFromPool(syncParam.getResourcePoolId());
               CloudServerLineResponse fullLine = fullLines.stream().filter(line -> line.getId().equals(lineResponse.getId())).findFirst().orElse(null);
               if (fullLine != null) {
                  lineResponse.setAvailableId(fullLine.getAvailableId());
                  lineResponse.setLineName(fullLine.getLineName());
                  lineResponse.setRemark(fullLine.getRemark());
                  if (ObjectUtil.isEmpty(lineResponse.getBandwidthPrice())) {
                     lineResponse.setBandwidthPrice(fullLine.getBandwidthPrice());
                  }

                  if (ObjectUtil.isEmpty(lineResponse.getAppendIpPrice())) {
                     lineResponse.setAppendIpPrice(fullLine.getAppendIpPrice());
                  }

                  if (ObjectUtil.isEmpty(lineResponse.getDefensePrice())) {
                     lineResponse.setDefensePrice(fullLine.getDefensePrice());
                  }

                  if (ObjectUtil.isEmpty(lineResponse.getSkuList()) && ObjectUtil.isNotEmpty(fullLine.getSkuList())) {
                     lineResponse.setSkuList(fullLine.getSkuList());
                  }

                  if (ObjectUtil.isEmpty(lineResponse.getSystemDiskMinSize())) {
                     lineResponse.setSystemDiskMinSize(fullLine.getSystemDiskMinSize());
                  }

                  if (ObjectUtil.isEmpty(lineResponse.getSystemDiskMaxSize())) {
                     lineResponse.setSystemDiskMaxSize(fullLine.getSystemDiskMaxSize());
                  }

                  if (ObjectUtil.isEmpty(lineResponse.getDataDiskMinSize())) {
                     lineResponse.setDataDiskMinSize(fullLine.getDataDiskMinSize());
                  }

                  if (ObjectUtil.isEmpty(lineResponse.getDataDiskMaxSize())) {
                     lineResponse.setDataDiskMaxSize(fullLine.getDataDiskMaxSize());
                  }

                  if (ObjectUtil.isEmpty(lineResponse.getBandwidthMinSize())) {
                     lineResponse.setBandwidthMinSize(fullLine.getBandwidthMinSize());
                  }

                  if (ObjectUtil.isEmpty(lineResponse.getBandwidthMaxSize())) {
                     lineResponse.setBandwidthMaxSize(fullLine.getBandwidthMaxSize());
                  }

                  if (lineResponse.getSortId() == null) {
                     lineResponse.setSortId(fullLine.getSortId());
                  }

                  if (lineResponse.getUpgradeStatus() == null) {
                     lineResponse.setUpgradeStatus(fullLine.getUpgradeStatus());
                  }

                  if (lineResponse.getRenewStatus() == null) {
                     lineResponse.setRenewStatus(fullLine.getRenewStatus());
                  }

                  if (lineResponse.getMasterIndex() == null) {
                     lineResponse.setMasterIndex(fullLine.getMasterIndex());
                  }

                  if (lineResponse.getIpRand() == null) {
                     lineResponse.setIpRand(fullLine.getIpRand());
                  }

                  if (lineResponse.getSnapPriceType() == null) {
                     lineResponse.setSnapPriceType(fullLine.getSnapPriceType());
                  }

                  if (lineResponse.getSnapOpenMax() == null) {
                     lineResponse.setSnapOpenMax(fullLine.getSnapOpenMax());
                  }

                  if (lineResponse.getSystemDiskFreeSize() == null) {
                     lineResponse.setSystemDiskFreeSize(fullLine.getSystemDiskFreeSize());
                  }

                  if (lineResponse.getSystemDiskStep() == null) {
                     lineResponse.setSystemDiskStep(fullLine.getSystemDiskStep());
                  }

                  if (lineResponse.getDataDiskFreeSize() == null) {
                     lineResponse.setDataDiskFreeSize(fullLine.getDataDiskFreeSize());
                  }

                  if (lineResponse.getDataDiskStep() == null) {
                     lineResponse.setDataDiskStep(fullLine.getDataDiskStep());
                  }

                  if (lineResponse.getBandwidthFreeSize() == null) {
                     lineResponse.setBandwidthFreeSize(fullLine.getBandwidthFreeSize());
                  }

                  if (lineResponse.getBandwidthInType() == null) {
                     lineResponse.setBandwidthInType(fullLine.getBandwidthInType());
                  }

                  if (lineResponse.getBandwidthInValue() == null) {
                     lineResponse.setBandwidthInValue(fullLine.getBandwidthInValue());
                  }

                  if (lineResponse.getDockingStatus() == null) {
                     lineResponse.setDockingStatus(fullLine.getDockingStatus());
                  }

                  if (ObjectUtil.isEmpty(lineResponse.getDefenseSize())) {
                     lineResponse.setDefenseSize(fullLine.getDefenseSize());
                  }

                  if (lineResponse.getDefenseFreeSize() == null) {
                     lineResponse.setDefenseFreeSize(fullLine.getDefenseFreeSize());
                  }

                  log.info("已从上游API获取完整线路数据，线路ID：{}，可用区ID：{}", lineResponse.getId(), lineResponse.getAvailableId());
               } else {
                  log.warn("从上游API未找到线路数据，线路ID：{}", lineResponse.getId());
               }
            } catch (Exception var12) {
               log.error("从上游API获取完整线路数据失败，线路ID：{}", lineResponse.getId(), var12);
            }
         }

         if (!ObjectUtil.isNotEmpty(lineResponse.getAvailableId())) {
            throw new CommonException("同步线路失败，线路缺少可用区ID，线路名称：{}", new Object[]{lineResponse.getLineName()});
         }

         try {
            this.serverAvailableRegionService.queryEntity(lineResponse.getAvailableId());
         } catch (Exception var14) {
            throw new CommonException("同步线路失败，可用区ID不存在：{}，线路名称：{}，请先同步可用区信息", new Object[]{lineResponse.getAvailableId(), lineResponse.getLineName()});
         }

         ServerLocalLine existing = null;

         try {
            existing = this.serverLocalLineService.queryEntity(lineResponse.getId());
         } catch (Exception var13) {
         }

         ServerLocalLineAddParam addParam = new ServerLocalLineAddParam();
         addParam.setId(lineResponse.getId());
         addParam.setAvailableId(lineResponse.getAvailableId());
         addParam.setLineName(lineResponse.getLineName());
         addParam.setRemark(lineResponse.getRemark());
         addParam.setStatus(lineResponse.getStatus() != null ? lineResponse.getStatus() : 1);
         addParam.setMaxOpenNum(lineResponse.getMaxOpenNum() != null ? lineResponse.getMaxOpenNum() : 0);
         addParam.setPurchaseDurations(lineResponse.getPurchaseDurations());
         addParam.setSortId(lineResponse.getSortId());
         addParam.setUpgradeStatus(lineResponse.getUpgradeStatus() != null ? lineResponse.getUpgradeStatus() : 1);
         addParam.setRenewStatus(lineResponse.getRenewStatus() != null ? lineResponse.getRenewStatus() : 1);
         addParam.setMasterIndex(lineResponse.getMasterIndex() != null ? lineResponse.getMasterIndex() : 1);
         addParam.setIpRand(lineResponse.getIpRand() != null ? lineResponse.getIpRand() : 0);
         addParam.setSnapPriceType(lineResponse.getSnapPriceType() != null ? lineResponse.getSnapPriceType() : 0);
         addParam.setSnapOpenMax(lineResponse.getSnapOpenMax() != null ? lineResponse.getSnapOpenMax() : 0);
         addParam.setHardDiskMinSize(0);
         addParam.setHardDiskMaxSize(0);
         addParam.setHardDiskStep(1);
         addParam.setBandwidthMinSize(lineResponse.getBandwidthMinSize() != null ? lineResponse.getBandwidthMinSize() : 0);
         addParam.setBandwidthMaxSize(lineResponse.getBandwidthMaxSize() != null ? lineResponse.getBandwidthMaxSize() : 0);
         addParam.setBandwidthFreeSize(lineResponse.getBandwidthFreeSize() != null ? lineResponse.getBandwidthFreeSize() : 0);
         addParam.setBandwidthInType(lineResponse.getBandwidthInType() != null ? lineResponse.getBandwidthInType() : 0);
         addParam.setBandwidthInValue(lineResponse.getBandwidthInValue() != null ? lineResponse.getBandwidthInValue() : 0);
         addParam.setDockingStatus(lineResponse.getDockingStatus() != null ? lineResponse.getDockingStatus() : 1);
         addParam.setBandwidthPrice(lineResponse.getBandwidthPrice() != null ? lineResponse.getBandwidthPrice() : BigDecimal.ZERO);
         addParam.setDefensePrice(lineResponse.getDefensePrice() != null ? lineResponse.getDefensePrice() : BigDecimal.ZERO);
         addParam.setDefenseSize(lineResponse.getDefenseSize() != null ? lineResponse.getDefenseSize() : "");
         addParam.setDefenseFreeSize(lineResponse.getDefenseFreeSize() != null ? lineResponse.getDefenseFreeSize() : 0);
         addParam.setSystemDiskMinSize(lineResponse.getSystemDiskMinSize() != null ? lineResponse.getSystemDiskMinSize() : 0);
         addParam.setSystemDiskMaxSize(lineResponse.getSystemDiskMaxSize() != null ? lineResponse.getSystemDiskMaxSize() : 0);
         addParam.setSystemDiskFreeSize(lineResponse.getSystemDiskFreeSize() != null ? lineResponse.getSystemDiskFreeSize() : 0);
         addParam.setSystemDiskStep(lineResponse.getSystemDiskStep() != null ? lineResponse.getSystemDiskStep() : 1);
         addParam.setSystemDiskMonthPrice(lineResponse.getSystemDiskMonthPrice() != null ? lineResponse.getSystemDiskMonthPrice() : BigDecimal.ZERO);
         addParam.setDataDiskMinSize(lineResponse.getDataDiskMinSize() != null ? lineResponse.getDataDiskMinSize() : 0);
         addParam.setDataDiskMaxSize(lineResponse.getDataDiskMaxSize() != null ? lineResponse.getDataDiskMaxSize() : 0);
         addParam.setDataDiskFreeSize(lineResponse.getDataDiskFreeSize() != null ? lineResponse.getDataDiskFreeSize() : 0);
         addParam.setDataDiskStep(lineResponse.getDataDiskStep() != null ? lineResponse.getDataDiskStep() : 1);
         addParam.setDataDiskMonthPrice(lineResponse.getDataDiskMonthPrice() != null ? lineResponse.getDataDiskMonthPrice() : BigDecimal.ZERO);
         addParam.setAppendIpStatus(0);
         addParam.setAppendIpPrice(lineResponse.getAppendIpPrice() != null ? lineResponse.getAppendIpPrice() : BigDecimal.ZERO);
         addParam.setIpMinSize(lineResponse.getIpMinSize() != null ? lineResponse.getIpMinSize() : 1);
         addParam.setIpMaxSize(lineResponse.getIpMaxSize() != null ? lineResponse.getIpMaxSize() : 1);
         addParam.setIpFreeSize(lineResponse.getIpFreeSize() != null ? lineResponse.getIpFreeSize() : 0);
         if (ObjectUtil.isNotEmpty(syncParam.getResourcePoolId())) {
            addParam.setResourcePoolId(syncParam.getResourcePoolId());
            addParam.setType(1);
         } else {
            addParam.setType(0);
         }

         if (existing != null) {
            this.updateServerLocalLineFromSyncAddParam(addParam);
            log.info("云服务器线路已存在，已按上游数据更新，ID：{}，名称：{}", lineResponse.getId(), lineResponse.getLineName());
         } else {
            this.serverLocalLineService.add(addParam);
            log.info("同步云服务器线路成功，ID：{}，名称：{}", lineResponse.getId(), lineResponse.getLineName());
         }

         if (ObjectUtil.isNotEmpty(lineResponse.getSkuList())) {
            if (existing != null) {
               this.serverLineSkuService.deleteByLineIdList(List.of(lineResponse.getId()));
               log.info("删除线路旧SKU配置，线路ID：{}", lineResponse.getId());
            }

            for (CloudServerLineSkuResponse skuResponse : lineResponse.getSkuList()) {
               ServerLineSkuAddParam skuAddParam = new ServerLineSkuAddParam();
               skuAddParam.setLineId(lineResponse.getId());
               skuAddParam.setName(skuResponse.getName());
               skuAddParam.setCpu(skuResponse.getCpu() != null ? skuResponse.getCpu() : 0);
               skuAddParam.setRam(skuResponse.getRam() != null ? skuResponse.getRam() : 0);
               skuAddParam.setCoreMonthPrice(skuResponse.getCoreMonthPrice() != null ? skuResponse.getCoreMonthPrice() : BigDecimal.ZERO);
               skuAddParam.setStatus(skuResponse.getStatus() != null ? skuResponse.getStatus() : 1);
               skuAddParam.setRemark(skuResponse.getRemark());
               skuAddParam.setRecommendStatus(skuResponse.getRecommendStatus() != null ? skuResponse.getRecommendStatus() : 0);
               skuAddParam.setRecommendContent(skuResponse.getRecommendContent());
               skuAddParam.setSortId(skuResponse.getSortId());
               skuAddParam.setOsHardDiskSize(0);
               skuAddParam.setHardDiskSize(0);
               if (ObjectUtil.isNotEmpty(syncParam.getResourcePoolId())) {
                  skuAddParam.setResourcePoolId(syncParam.getResourcePoolId());
                  skuAddParam.setType(1);
               } else {
                  skuAddParam.setType(0);
               }

               this.serverLineSkuService.add(skuAddParam);
               log.debug("同步线路SKU配置成功，线路ID：{}，SKU名称：{}", lineResponse.getId(), skuResponse.getName());
            }

            log.info("同步线路SKU配置完成，线路ID：{}，SKU数量：{}", lineResponse.getId(), lineResponse.getSkuList().size());
         }
      }

      if (ObjectUtil.isNotEmpty(memberNo)) {
         log.info("云服务器线路同步完成，会员编号：{}", memberNo);
      } else {
         log.info("云服务器线路同步完成（后台管理）");
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void syncIndependentMachines(ProductSyncParam syncParam) {
      Long memberNo = ApiContext.getMemberNo();
      if (ObjectUtil.isNotEmpty(memberNo)) {
         log.info("开始同步独立服务器机型，会员编号：{}，产品数量：{}", memberNo, syncParam.getProducts().size());
      } else {
         log.info("开始同步独立服务器机型（后台管理），产品数量：{}", syncParam.getProducts().size());
      }

      for (Object product : syncParam.getProducts()) {
         JSONObject productJson = JSONUtil.parseObj(product);
         IndependentMachineResponse machineResponse = (IndependentMachineResponse)JSONUtil.toBean(productJson, IndependentMachineResponse.class);
         Machine existing = null;

         try {
            existing = (Machine)this.machineMapper.selectById(machineResponse.getId());
         } catch (Exception var10) {
         }

         MachineAddParam addParam = new MachineAddParam();
         addParam.setName(machineResponse.getName());
         addParam.setInventory(machineResponse.getInventory() != null ? machineResponse.getInventory() : 0);
         addParam.setAutomation(machineResponse.getAutomation() != null ? machineResponse.getAutomation() : 0);
         addParam.setMachineMonthPrice(machineResponse.getMachineMonthPrice() != null ? machineResponse.getMachineMonthPrice() : BigDecimal.ZERO);
         addParam.setCpuInfo(machineResponse.getCpuInfo());
         addParam.setRamInfo(machineResponse.getRamInfo());
         addParam.setDisksInfo(machineResponse.getDisksInfo());
         addParam.setIpFreeSize(machineResponse.getIpFreeSize() != null ? machineResponse.getIpFreeSize() : 0);
         addParam.setDefenseFreeSize(machineResponse.getDefenseFreeSize() != null ? machineResponse.getDefenseFreeSize() : 0);
         addParam.setBandwidthFreeSize(machineResponse.getBandwidthFreeSize() != null ? machineResponse.getBandwidthFreeSize() : 0);
         addParam.setBandwidthMinSize(machineResponse.getBandwidthMinSize() != null ? machineResponse.getBandwidthMinSize() : 0);
         addParam.setBandwidthMaxSize(machineResponse.getBandwidthMaxSize() != null ? machineResponse.getBandwidthMaxSize() : 0);
         addParam.setIpMinSize(machineResponse.getIpMinSize() != null ? machineResponse.getIpMinSize() : 0);
         addParam.setIpMaxSize(machineResponse.getIpMaxSize() != null ? machineResponse.getIpMaxSize() : 0);
         addParam.setDefenseMaxSize(machineResponse.getDefenseMaxSize());
         addParam.setBandwidthMonthPrice(machineResponse.getBandwidthMonthPrice() != null ? machineResponse.getBandwidthMonthPrice() : BigDecimal.ZERO);
         addParam.setDefenseMonthPrice(machineResponse.getDefenseMonthPrice() != null ? machineResponse.getDefenseMonthPrice() : BigDecimal.ZERO);
         addParam.setIpMonthPrice(machineResponse.getIpMonthPrice() != null ? machineResponse.getIpMonthPrice() : BigDecimal.ZERO);
         addParam.setStatus(machineResponse.getStatus() != null ? machineResponse.getStatus() : 1);
         addParam.setUpgrades(machineResponse.getUpgrades() != null ? machineResponse.getUpgrades() : 0);
         addParam.setWeight(machineResponse.getWeight() != null ? machineResponse.getWeight() : 0);
         addParam.setDockingStatus(machineResponse.getDockingStatus() != null ? machineResponse.getDockingStatus() : 1);
         addParam.setLineId(new ArrayList<>());
         if (ObjectUtil.isNotEmpty(syncParam.getResourcePoolId())) {
            addParam.setResourcePoolId(syncParam.getResourcePoolId());
            addParam.setType(1);
         } else {
            addParam.setType(0);
         }

         Machine machine = (Machine)BeanUtil.toBean(addParam, Machine.class);
         machine.setId(machineResponse.getId());
         if (machine.getMachineMonthPrice() != null && machine.getMachineMonthPrice().compareTo(BigDecimal.ZERO) > 0) {
            machine.setMachineDayPrice(machine.getMachineMonthPrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
         }

         if (machine.getBandwidthMonthPrice() != null && machine.getBandwidthMonthPrice().compareTo(BigDecimal.ZERO) > 0) {
            machine.setBandwidthDayPrice(machine.getBandwidthMonthPrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
         }

         if (machine.getDefenseMonthPrice() != null && machine.getDefenseMonthPrice().compareTo(BigDecimal.ZERO) > 0) {
            machine.setDefenseDayPrice(machine.getDefenseMonthPrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
         }

         if (machine.getIpMonthPrice() != null && machine.getIpMonthPrice().compareTo(BigDecimal.ZERO) > 0) {
            machine.setIpDayPrice(machine.getIpMonthPrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
         }

         if (existing != null) {
            this.machineMapper.updateById(machine);
            log.info("独立服务器机型已存在，已按上游数据更新，ID：{}，名称：{}", machineResponse.getId(), machineResponse.getName());
         } else {
            this.machineMapper.insert(machine);
            log.info("同步独立服务器机型成功，ID：{}，名称：{}", machineResponse.getId(), machineResponse.getName());
         }

         this.syncMachineLineRelations(machineResponse.getId(), machineResponse.getLineName());
      }

      if (ObjectUtil.isNotEmpty(memberNo)) {
         log.info("独立服务器机型同步完成，会员编号：{}", memberNo);
      } else {
         log.info("独立服务器机型同步完成（后台管理）");
      }
   }

   @Override
   public List<CloudServerLineResponse> queryCloudServerLinesFromPool(String resourcePoolId) {
      BaseResourcePool resourcePool = this.resourcePoolApi.getById(resourcePoolId);
      if (ObjectUtil.isEmpty(resourcePool)) {
         throw new CommonException("资源池不存在或已禁用，资源池ID为：{}", new Object[]{resourcePoolId});
      } else {
         log.info("从资源池查询云服务器线路，资源池ID：{}，资源池类型：{}，API地址：{}", new Object[]{resourcePoolId, resourcePool.getPoolType(), resourcePool.getApiUrl()});

         try {
            String apiSecret = CommonCryptogramUtil.doSm4CbcDecrypt(resourcePool.getApiSecret());
            String apiUrl = resourcePool.getApiUrl();
            if (!apiUrl.endsWith("/")) {
               apiUrl = apiUrl + "/";
            }

            String requestUrl = apiUrl + "api/product/cloud-server/lines";
            HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.get(requestUrl).header("X-Api-Key", resourcePool.getApiKey()))
                  .header("X-Api-Secret", apiSecret))
               .timeout(30000)
               .execute();
            if (response.getStatus() != 200) {
               throw new CommonException("调用上游API失败，状态码：{}，响应：{}", new Object[]{response.getStatus(), response.body()});
            } else {
               String responseBody = response.body();
               JSONObject jsonResponse = JSONUtil.parseObj(responseBody);
               if (jsonResponse.getInt("code") != 200) {
                  throw new CommonException("上游API返回错误：{}", new Object[]{jsonResponse.getStr("msg")});
               } else {
                  JSONArray dataArray = jsonResponse.getJSONArray("data");
                  List<CloudServerLineResponse> lines = new ArrayList<>();
                  if (dataArray != null) {
                     for (int i = 0; i < dataArray.size(); i++) {
                        JSONObject productJson = dataArray.getJSONObject(i);
                        CloudServerLineResponse line = (CloudServerLineResponse)JSONUtil.toBean(productJson, CloudServerLineResponse.class);
                        lines.add(line);
                     }
                  }

                  List<CloudServerLineResponse> filteredLines = lines.stream()
                     .filter(linex -> this.isDockingEnabled(linex.getDockingStatus()))
                     .collect(Collectors.toList());
                  log.info("从资源池查询云服务器线路成功，资源池ID：{}，产品数量：{}，可对接数量：{}", new Object[]{resourcePoolId, lines.size(), filteredLines.size()});
                  return filteredLines;
               }
            }
         } catch (Exception var14) {
            log.error("从资源池查询云服务器线路失败，资源池ID：{}", resourcePoolId, var14);
            throw new CommonException("从资源池查询云服务器线路失败：{}", new Object[]{var14.getMessage()});
         }
      }
   }

   @Override
   public List<IndependentMachineResponse> queryIndependentMachinesFromPool(String resourcePoolId) {
      BaseResourcePool resourcePool = this.resourcePoolApi.getById(resourcePoolId);
      if (ObjectUtil.isEmpty(resourcePool)) {
         throw new CommonException("资源池不存在或已禁用，资源池ID为：{}", new Object[]{resourcePoolId});
      } else {
         log.info("从资源池查询独立服务器机型，资源池ID：{}，资源池类型：{}，API地址：{}", new Object[]{resourcePoolId, resourcePool.getPoolType(), resourcePool.getApiUrl()});

         try {
            String apiSecret = CommonCryptogramUtil.doSm4CbcDecrypt(resourcePool.getApiSecret());
            String apiUrl = resourcePool.getApiUrl();
            if (!apiUrl.endsWith("/")) {
               apiUrl = apiUrl + "/";
            }

            String requestUrl = apiUrl + "api/product/independent-server/machines";
            HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.get(requestUrl).header("X-Api-Key", resourcePool.getApiKey()))
                  .header("X-Api-Secret", apiSecret))
               .timeout(30000)
               .execute();
            if (response.getStatus() != 200) {
               throw new CommonException("调用上游API失败，状态码：{}，响应：{}", new Object[]{response.getStatus(), response.body()});
            } else {
               String responseBody = response.body();
               JSONObject jsonResponse = JSONUtil.parseObj(responseBody);
               if (jsonResponse.getInt("code") != 200) {
                  throw new CommonException("上游API返回错误：{}", new Object[]{jsonResponse.getStr("msg")});
               } else {
                  JSONArray dataArray = jsonResponse.getJSONArray("data");
                  List<IndependentMachineResponse> machines = new ArrayList<>();
                  if (dataArray != null) {
                     for (int i = 0; i < dataArray.size(); i++) {
                        JSONObject productJson = dataArray.getJSONObject(i);
                        IndependentMachineResponse machine = (IndependentMachineResponse)JSONUtil.toBean(productJson, IndependentMachineResponse.class);
                        machines.add(machine);
                     }
                  }

                  List<IndependentMachineResponse> filteredMachines = machines.stream()
                     .filter(machinex -> this.isDockingEnabled(machinex.getDockingStatus()))
                     .collect(Collectors.toList());
                  log.info("从资源池查询独立服务器机型成功，资源池ID：{}，产品数量：{}，可对接数量：{}", new Object[]{resourcePoolId, machines.size(), filteredMachines.size()});
                  return filteredMachines;
               }
            }
         } catch (Exception var14) {
            log.error("从资源池查询独立服务器机型失败，资源池ID：{}", resourcePoolId, var14);
            throw new CommonException("从资源池查询独立服务器机型失败：{}", new Object[]{var14.getMessage()});
         }
      }
   }

   @Override
   public List<Object> queryCloudServerRegionsFromPool(String resourcePoolId) {
      return this.queryFromUpstreamApi(resourcePoolId, "api/product/cloud-server/regions", CloudServerRegionResponse.class);
   }

   @Override
   public List<Object> queryCloudServerAvailableRegionsFromPool(String resourcePoolId) {
      return this.queryFromUpstreamApi(resourcePoolId, "api/product/cloud-server/available-regions", CloudServerAvailableRegionResponse.class);
   }

   @Override
   public List<Object> queryIndependentRegionsFromPool(String resourcePoolId) {
      return this.queryFromUpstreamApi(resourcePoolId, "api/product/independent-server/regions", IndependentRegionResponse.class);
   }

   @Override
   public List<Object> queryIndependentLinesFromPool(String resourcePoolId) {
      return this.queryFromUpstreamApi(resourcePoolId, "api/product/independent-server/lines", IndependentLineResponse.class);
   }

   private <T> List<Object> queryFromUpstreamApi(String resourcePoolId, String apiPath, Class<T> responseClass) {
      BaseResourcePool resourcePool = this.resourcePoolApi.getById(resourcePoolId);
      if (ObjectUtil.isEmpty(resourcePool)) {
         throw new CommonException("资源池不存在或已禁用，资源池ID为：{}", new Object[]{resourcePoolId});
      } else {
         log.info("从资源池查询数据，资源池ID：{}，API路径：{}", resourcePoolId, apiPath);

         try {
            String apiSecret = CommonCryptogramUtil.doSm4CbcDecrypt(resourcePool.getApiSecret());
            log.info("资源池API Secret解密后长度: {}", apiSecret != null ? apiSecret.length() : 0);
            String apiUrl = resourcePool.getApiUrl();
            if (!apiUrl.endsWith("/")) {
               apiUrl = apiUrl + "/";
            }

            String requestUrl = apiUrl + apiPath;
            log.info("请求上游API URL：{}", requestUrl);
            HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.get(requestUrl).header("X-Api-Key", resourcePool.getApiKey()))
                  .header("X-Api-Secret", apiSecret))
               .timeout(30000)
               .execute();
            if (response.getStatus() != 200) {
               throw new CommonException("调用上游API失败，状态码：{}，响应：{}", new Object[]{response.getStatus(), response.body()});
            } else {
               String responseBody = response.body();
               log.info("上游API响应内容：{}", responseBody);
               JSONObject jsonResponse = JSONUtil.parseObj(responseBody);
               if (jsonResponse.getInt("code") != 200) {
                  throw new CommonException("上游API返回错误：{}", new Object[]{jsonResponse.getStr("msg")});
               } else {
                  JSONArray dataArray = jsonResponse.getJSONArray("data");
                  List<Object> results = new ArrayList<>();
                  if (dataArray != null) {
                     log.info("上游API返回数据数量：{}", dataArray.size());

                     for (int i = 0; i < dataArray.size(); i++) {
                        try {
                           JSONObject itemJson = dataArray.getJSONObject(i);
                           log.debug("解析第{}条数据：{}", i + 1, itemJson);
                           T item = (T)JSONUtil.toBean(itemJson, responseClass);
                           results.add(item);
                        } catch (Exception var16) {
                           log.error("解析第{}条数据失败，数据内容：{}", new Object[]{i + 1, dataArray.getJSONObject(i), var16});
                           throw new CommonException("解析上游API返回数据失败，第{}条数据：{}", new Object[]{i + 1, var16.getMessage()});
                        }
                     }
                  } else {
                     log.warn("上游API返回的data字段为空或null");
                  }

                  log.info("从资源池查询数据成功，资源池ID：{}，数据数量：{}", resourcePoolId, results.size());
                  return results;
               }
            }
         } catch (Exception var17) {
            log.error("从资源池查询数据失败，资源池ID：{}，API路径：{}", new Object[]{resourcePoolId, apiPath, var17});
            throw new CommonException("从资源池查询数据失败：{}", new Object[]{var17.getMessage()});
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void syncCloudServerRegions(ProductSyncParam syncParam) {
      log.info("开始同步云服务器地域，产品数量：{}", syncParam.getProducts().size());

      for (Object product : syncParam.getProducts()) {
         JSONObject productJson = JSONUtil.parseObj(product);
         CloudServerRegionResponse regionResponse = (CloudServerRegionResponse)JSONUtil.toBean(productJson, CloudServerRegionResponse.class);
         ServerRegion existing = (ServerRegion)this.serverRegionMapper.selectById(regionResponse.getId());
         ServerRegionAddParam addParam = new ServerRegionAddParam();
         addParam.setId(regionResponse.getId());
         addParam.setRegionName(regionResponse.getRegionName());
         addParam.setRemark(regionResponse.getRemark());
         addParam.setStatus(regionResponse.getStatus() != null ? regionResponse.getStatus() : 1);
         addParam.setSortId(regionResponse.getSortId() != null ? regionResponse.getSortId() : 0);
         if (ObjectUtil.isNotEmpty(syncParam.getResourcePoolId())) {
            addParam.setResourcePoolId(syncParam.getResourcePoolId());
            addParam.setType(1);
         } else {
            addParam.setType(0);
         }

         if (existing != null) {
            ServerRegion toUpdate = (ServerRegion)BeanUtil.toBean(addParam, ServerRegion.class);
            this.serverRegionMapper.updateById(toUpdate);
            log.info("云服务器地域已存在，已按上游数据更新，ID：{}，名称：{}", regionResponse.getId(), regionResponse.getRegionName());
         } else {
            this.serverRegionService.add(addParam);
            log.info("同步云服务器地域成功，ID：{}，名称：{}", regionResponse.getId(), regionResponse.getRegionName());
         }
      }

      log.info("云服务器地域同步完成");
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void syncCloudServerAvailableRegions(ProductSyncParam syncParam) {
      log.info("开始同步云服务器可用区，产品数量：{}", syncParam.getProducts().size());

      for (Object product : syncParam.getProducts()) {
         JSONObject productJson = JSONUtil.parseObj(product);
         CloudServerAvailableRegionResponse availableRegionResponse = (CloudServerAvailableRegionResponse)JSONUtil.toBean(
            productJson, CloudServerAvailableRegionResponse.class
         );

         try {
            this.serverRegionService.queryEntity(availableRegionResponse.getRegionId());
         } catch (Exception var9) {
            throw new CommonException("同步可用区失败，地域ID不存在：{}，请先同步地域信息", new Object[]{availableRegionResponse.getRegionId()});
         }

         ServerAvailableRegion existing = (ServerAvailableRegion)this.serverAvailableRegionMapper.selectById(availableRegionResponse.getId());
         ServerAvailableRegionAddParam addParam = new ServerAvailableRegionAddParam();
         addParam.setId(availableRegionResponse.getId());
         addParam.setRegionId(availableRegionResponse.getRegionId());
         addParam.setAvailableName(availableRegionResponse.getAvailableName());
         addParam.setRemark(availableRegionResponse.getRemark());
         addParam.setStatus(availableRegionResponse.getStatus() != null ? availableRegionResponse.getStatus() : 1);
         addParam.setSortId(availableRegionResponse.getSortId() != null ? availableRegionResponse.getSortId() : 0);
         if (ObjectUtil.isNotEmpty(syncParam.getResourcePoolId())) {
            addParam.setResourcePoolId(syncParam.getResourcePoolId());
            addParam.setType(1);
         } else {
            addParam.setType(0);
         }

         if (existing != null) {
            ServerAvailableRegion toUpdate = (ServerAvailableRegion)BeanUtil.toBean(addParam, ServerAvailableRegion.class);
            this.serverAvailableRegionMapper.updateById(toUpdate);
            log.info("云服务器可用区已存在，已按上游数据更新，ID：{}，名称：{}", availableRegionResponse.getId(), availableRegionResponse.getAvailableName());
         } else {
            this.serverAvailableRegionService.add(addParam);
            log.info("同步云服务器可用区成功，ID：{}，名称：{}", availableRegionResponse.getId(), availableRegionResponse.getAvailableName());
         }
      }

      log.info("云服务器可用区同步完成");
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void syncIndependentRegions(ProductSyncParam syncParam) {
      log.info("开始同步独立服务器地域，产品数量：{}", syncParam.getProducts().size());

      for (Object product : syncParam.getProducts()) {
         JSONObject productJson = JSONUtil.parseObj(product);
         IndependentRegionResponse regionResponse = (IndependentRegionResponse)JSONUtil.toBean(productJson, IndependentRegionResponse.class);
         Region existing = (Region)this.regionMapper.selectById(regionResponse.getId());
         RegionDTO regionDTO = new RegionDTO();
         regionDTO.setId(regionResponse.getId());
         regionDTO.setName(regionResponse.getName());
         regionDTO.setDescription(regionResponse.getDescription());
         regionDTO.setStatus(regionResponse.getStatus() != null ? regionResponse.getStatus() : 1);
         regionDTO.setSort(regionResponse.getSort() != null ? regionResponse.getSort() : 0);
         if (ObjectUtil.isNotEmpty(syncParam.getResourcePoolId())) {
            regionDTO.setResourcePoolId(syncParam.getResourcePoolId());
            regionDTO.setType(1);
         } else {
            regionDTO.setType(0);
         }

         if (existing != null) {
            Region toUpdate = (Region)BeanUtil.toBean(regionDTO, Region.class);
            this.regionMapper.updateById(toUpdate);
            log.info("独立服务器地域已存在，已按上游数据更新，ID：{}，名称：{}", regionResponse.getId(), regionResponse.getName());
         } else {
            this.independentRegionService.add(regionDTO);
            log.info("同步独立服务器地域成功，ID：{}，名称：{}", regionResponse.getId(), regionResponse.getName());
         }
      }

      log.info("独立服务器地域同步完成");
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void syncIndependentLines(ProductSyncParam syncParam) {
      log.info("开始同步独立服务器线路，产品数量：{}", syncParam.getProducts().size());

      for (Object product : syncParam.getProducts()) {
         JSONObject productJson = JSONUtil.parseObj(product);
         IndependentLineResponse lineResponse = (IndependentLineResponse)JSONUtil.toBean(productJson, IndependentLineResponse.class);

         try {
            this.independentRegionService.searchRegionById(lineResponse.getRegionId());
         } catch (Exception var9) {
            throw new CommonException("同步线路失败，地域ID不存在：{}，请先同步地域信息", new Object[]{lineResponse.getRegionId()});
         }

         IndependentLine existing = (IndependentLine)this.independentLineMapper.selectById(lineResponse.getId());
         IndependentLineAddParam addParam = new IndependentLineAddParam();
         addParam.setId(lineResponse.getId());
         addParam.setRegionId(lineResponse.getRegionId());
         addParam.setName(lineResponse.getName());
         addParam.setYearNumber(lineResponse.getYearNumber() != null ? lineResponse.getYearNumber() : 1);
         addParam.setRemark(lineResponse.getRemark());
         addParam.setStashIp(lineResponse.getStashIp());
         addParam.setStatus(lineResponse.getStatus() != null ? lineResponse.getStatus() : 1);
         addParam.setWeight(lineResponse.getWeight() != null ? lineResponse.getWeight() : 0);
         addParam.setDiscountTemplateId(lineResponse.getDiscountTemplateId());
         if (ObjectUtil.isNotEmpty(syncParam.getResourcePoolId())) {
            addParam.setResourcePoolId(syncParam.getResourcePoolId());
            addParam.setType(1);
         } else {
            addParam.setType(0);
         }

         if (existing != null) {
            IndependentLine toUpdate = (IndependentLine)BeanUtil.toBean(addParam, IndependentLine.class);
            this.independentLineMapper.updateById(toUpdate);
            log.info("独立服务器线路已存在，已按上游数据更新，ID：{}，名称：{}", lineResponse.getId(), lineResponse.getName());
         } else {
            this.independentLineService.add(addParam);
            log.info("同步独立服务器线路成功，ID：{}，名称：{}", lineResponse.getId(), lineResponse.getName());
         }
      }

      log.info("独立服务器线路同步完成");
   }

   private void syncMachineLineRelations(String machineId, List<String> lineNames) {
      if (!ObjectUtil.isEmpty(lineNames) && !lineNames.isEmpty()) {
         this.machineLineMapper.delete((Wrapper<MachineLine>)new LambdaQueryWrapper<MachineLine>().eq(MachineLine::getMachineId, machineId));
         log.debug("删除机型 {} 的旧线路关联", machineId);

         for (String lineName : lineNames) {
            if (!ObjectUtil.isEmpty(lineName)) {
               IndependentLine independentLine = (IndependentLine)this.independentLineMapper
                  .selectOne((Wrapper<IndependentLine>)((LambdaQueryWrapper<IndependentLine>)new LambdaQueryWrapper<IndependentLine>().eq(IndependentLine::getName, lineName)).last("LIMIT 1"));
               if (independentLine != null) {
                  MachineLine existingRelation = (MachineLine)this.machineLineMapper
                     .selectOne(
                        (Wrapper<MachineLine>)((LambdaQueryWrapper<MachineLine>)new LambdaQueryWrapper<MachineLine>().eq(MachineLine::getMachineId, machineId))
                           .eq(MachineLine::getLineId, independentLine.getId())
                     );
                  if (existingRelation == null) {
                     MachineLine machineLine = new MachineLine();
                     machineLine.setId(SnowflakeIdGenerator.generateId());
                     machineLine.setMachineId(machineId);
                     machineLine.setLineId(independentLine.getId());
                     this.machineLineMapper.insert(machineLine);
                     log.debug("创建机型 {} 与线路 {} ({}) 的关联", new Object[]{machineId, lineName, independentLine.getId()});
                  } else {
                     log.debug("机型 {} 与线路 {} ({}) 的关联已存在，跳过", new Object[]{machineId, lineName, independentLine.getId()});
                  }
               } else {
                  log.warn("根据线路名称 {} 未找到对应的线路，机型ID：{}", lineName, machineId);
               }
            }
         }
      } else {
         log.debug("机型 {} 没有关联线路，跳过同步", machineId);
      }
   }

   @Override
   public List<Object> queryCloudServerOsVersionsFromPool(String resourcePoolId, String lineId) {
      BaseResourcePool resourcePool = this.resourcePoolApi.getById(resourcePoolId);
      if (ObjectUtil.isEmpty(resourcePool)) {
         throw new CommonException("资源池不存在或已禁用，资源池ID为：{}", new Object[]{resourcePoolId});
      } else {
         log.info("从资源池查询云服务器操作系统版本，资源池ID：{}，线路ID：{}", resourcePoolId, lineId);

         try {
            String apiSecret = CommonCryptogramUtil.doSm4CbcDecrypt(resourcePool.getApiSecret());
            String apiUrl = resourcePool.getApiUrl();
            if (!apiUrl.endsWith("/")) {
               apiUrl = apiUrl + "/";
            }

            String requestUrl = apiUrl + "api/product/cloud-server/os-versions";
            if (ObjectUtil.isNotEmpty(lineId)) {
               requestUrl = requestUrl + "?lineId=" + lineId;
            }

            HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.get(requestUrl).header("X-Api-Key", resourcePool.getApiKey()))
                  .header("X-Api-Secret", apiSecret))
               .timeout(30000)
               .execute();
            if (response.getStatus() != 200) {
               throw new CommonException("调用上游API失败，状态码：{}，响应：{}", new Object[]{response.getStatus(), response.body()});
            } else {
               String responseBody = response.body();
               JSONObject jsonResponse = JSONUtil.parseObj(responseBody);
               if (jsonResponse.getInt("code") != 200) {
                  throw new CommonException("上游API返回错误：{}", new Object[]{jsonResponse.getStr("msg")});
               } else {
                  JSONArray dataArray = jsonResponse.getJSONArray("data");
                  List<Object> osVersions = new ArrayList<>();
                  if (dataArray != null) {
                     for (int i = 0; i < dataArray.size(); i++) {
                        Object item = dataArray.get(i);
                        Map<String, Object> mapItem = this.convertJsonObjectToMap(item);
                        osVersions.add(mapItem);
                     }
                  }

                  log.info("从资源池查询云服务器操作系统版本成功，数量：{}", osVersions.size());
                  return osVersions;
               }
            }
         } catch (Exception var15) {
            log.error("从资源池查询云服务器操作系统版本失败，资源池ID：{}", resourcePoolId, var15);
            throw new CommonException("查询云服务器操作系统版本失败：{}", new Object[]{var15.getMessage()});
         }
      }
   }

   @Override
   public List<Object> queryIndependentOsVersionsFromPool(String resourcePoolId, String lineId) {
      BaseResourcePool resourcePool = this.resourcePoolApi.getById(resourcePoolId);
      if (ObjectUtil.isEmpty(resourcePool)) {
         throw new CommonException("资源池不存在或已禁用，资源池ID为：{}", new Object[]{resourcePoolId});
      } else {
         log.info("从资源池查询独立服务器操作系统版本，资源池ID：{}，线路ID：{}", resourcePoolId, lineId);

         try {
            String apiSecret = CommonCryptogramUtil.doSm4CbcDecrypt(resourcePool.getApiSecret());
            String apiUrl = resourcePool.getApiUrl();
            if (!apiUrl.endsWith("/")) {
               apiUrl = apiUrl + "/";
            }

            String requestUrl = apiUrl + "api/product/independent-server/os-versions";
            if (ObjectUtil.isNotEmpty(lineId)) {
               requestUrl = requestUrl + "?lineId=" + lineId;
            }

            HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.get(requestUrl).header("X-Api-Key", resourcePool.getApiKey()))
                  .header("X-Api-Secret", apiSecret))
               .timeout(30000)
               .execute();
            if (response.getStatus() != 200) {
               throw new CommonException("调用上游API失败，状态码：{}，响应：{}", new Object[]{response.getStatus(), response.body()});
            } else {
               String responseBody = response.body();
               JSONObject jsonResponse = JSONUtil.parseObj(responseBody);
               if (jsonResponse.getInt("code") != 200) {
                  throw new CommonException("上游API返回错误：{}", new Object[]{jsonResponse.getStr("msg")});
               } else {
                  JSONArray dataArray = jsonResponse.getJSONArray("data");
                  List<Object> osVersions = new ArrayList<>();
                  if (dataArray != null) {
                     for (int i = 0; i < dataArray.size(); i++) {
                        Object item = dataArray.get(i);
                        Map<String, Object> mapItem = this.convertJsonObjectToMap(item);
                        osVersions.add(mapItem);
                     }
                  }

                  log.info("从资源池查询独立服务器操作系统版本成功，数量：{}", osVersions.size());
                  return osVersions;
               }
            }
         } catch (Exception var15) {
            log.error("从资源池查询独立服务器操作系统版本失败，资源池ID：{}", resourcePoolId, var15);
            throw new CommonException("查询独立服务器操作系统版本失败：{}", new Object[]{var15.getMessage()});
         }
      }
   }

   private void fillOsVersionAddParamFromCloudServerResponse(CloudServerOsVersionResponse osVersionResponse, OsVersionAddParam addParam) {
      addParam.setOsName(osVersionResponse.getOsName());
      addParam.setOsClassId(osVersionResponse.getOsClassId());
      addParam.setOsClassName(osVersionResponse.getOsClassName());
      addParam.setDefaultUserName(osVersionResponse.getDefaultUserName() != null ? osVersionResponse.getDefaultUserName() : "root");
      addParam.setDefaultPort(osVersionResponse.getDefaultPort() != null ? osVersionResponse.getDefaultPort() : 22);
      addParam.setPortMode(osVersionResponse.getPortMode());
      addParam.setPortRange(osVersionResponse.getPortRange());
      addParam.setImageDirectoryName(osVersionResponse.getImageDirectoryName());
      addParam.setStatus(osVersionResponse.getStatus() != null ? osVersionResponse.getStatus() : "1");
      addParam.setSupportedAvailabilityZone(osVersionResponse.getSupportedAvailabilityZone());
      addParam.setSupportedProductType(osVersionResponse.getSupportedProductType());
      addParam.setControlledType(osVersionResponse.getControlledType());
   }

   private void applyResourcePoolFromSyncParam(ProductSyncParam syncParam, OsClassAddParam addParam) {
      if (ObjectUtil.isNotEmpty(syncParam.getResourcePoolId())) {
         addParam.setResourcePoolId(syncParam.getResourcePoolId());
         addParam.setType(1);
      } else {
         addParam.setType(0);
      }
   }

   private void applyResourcePoolFromSyncParam(ProductSyncParam syncParam, OsVersionAddParam addParam) {
      if (ObjectUtil.isNotEmpty(syncParam.getResourcePoolId())) {
         addParam.setResourcePoolId(syncParam.getResourcePoolId());
         addParam.setType(1);
      } else {
         addParam.setType(0);
      }
   }

   private void applyResourcePoolFromSyncParam(ProductSyncParam syncParam, com.rxinyun.admin.production.modular.independent.os.param.OsClassAddParam addParam) {
      if (ObjectUtil.isNotEmpty(syncParam.getResourcePoolId())) {
         addParam.setResourcePoolId(syncParam.getResourcePoolId());
         addParam.setType(1);
      } else {
         addParam.setType(0);
      }
   }

   private void applyResourcePoolFromSyncParam(ProductSyncParam syncParam, com.rxinyun.admin.production.modular.independent.os.param.OsVersionAddParam addParam) {
      if (ObjectUtil.isNotEmpty(syncParam.getResourcePoolId())) {
         addParam.setResourcePoolId(syncParam.getResourcePoolId());
         addParam.setType(1);
      } else {
         addParam.setType(0);
      }
   }

   private void updateServerLocalLineFromSyncAddParam(ServerLocalLineAddParam addParam) {
      ServerLocalLine serverLocalLine = (ServerLocalLine)BeanUtil.toBean(addParam, ServerLocalLine.class);
      BigDecimal monthDays = new BigDecimal(31);
      serverLocalLine.setSystemDiskDayPrice(serverLocalLine.getSystemDiskMonthPrice().divide(monthDays, 2, RoundingMode.HALF_UP));
      serverLocalLine.setDataDiskDayPrice(serverLocalLine.getDataDiskMonthPrice().divide(monthDays, 2, RoundingMode.HALF_UP));
      serverLocalLine.setBandwidthDayPrice(serverLocalLine.getBandwidthPrice().divide(monthDays, 2, RoundingMode.HALF_UP));
      serverLocalLine.setDefenseDayPrice(serverLocalLine.getDefensePrice().divide(monthDays, 2, RoundingMode.HALF_UP));
      this.serverLocalLineMapper.updateById(serverLocalLine);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void syncCloudServerOsVersions(ProductSyncParam syncParam) {
      Long memberNo = ApiContext.getMemberNo();
      if (ObjectUtil.isNotEmpty(memberNo)) {
         log.info("开始同步云服务器操作系统版本，会员编号：{}，产品数量：{}", memberNo, syncParam.getProducts().size());
      } else {
         log.info("开始同步云服务器操作系统版本（后台管理），产品数量：{}", syncParam.getProducts().size());
      }

      for (Object product : syncParam.getProducts()) {
         JSONObject productJson = JSONUtil.parseObj(product);
         CloudServerOsVersionResponse osVersionResponse = (CloudServerOsVersionResponse)JSONUtil.toBean(productJson, CloudServerOsVersionResponse.class);
         String lineId = osVersionResponse.getLineId();
         if (ObjectUtil.isNotEmpty(lineId)) {
            try {
               ServerLocalLine line = this.serverLocalLineService.queryEntity(lineId);
               if (line != null && ObjectUtil.isNotEmpty(line.getAvailableId())) {
                  if (ObjectUtil.isEmpty(osVersionResponse.getSupportedAvailabilityZone())) {
                     osVersionResponse.setSupportedAvailabilityZone(line.getAvailableId());
                  } else {
                     String supportedZones = osVersionResponse.getSupportedAvailabilityZone();
                     if (!supportedZones.contains(line.getAvailableId())) {
                        osVersionResponse.setSupportedAvailabilityZone(supportedZones + "," + line.getAvailableId());
                     }
                  }
               }
            } catch (Exception var11) {
               log.warn("查询线路信息失败，线路ID：{}，错误：{}", lineId, var11.getMessage());
            }
         }

         if (ObjectUtil.isEmpty(osVersionResponse.getOsClassId())) {
            throw new CommonException("同步操作系统版本失败，缺少操作系统类别ID，操作系统名称：{}", new Object[]{osVersionResponse.getOsName()});
         }

         OsVersion existing = (OsVersion)this.osVersionMapper.selectById(osVersionResponse.getId());
         OsVersionAddParam addParam = new OsVersionAddParam();
         this.fillOsVersionAddParamFromCloudServerResponse(osVersionResponse, addParam);
         this.applyResourcePoolFromSyncParam(syncParam, addParam);
         if (existing != null) {
            BeanUtil.copyProperties(addParam, existing, new String[0]);
            existing.setId(osVersionResponse.getId());
            this.osVersionMapper.updateById(existing);
            log.info("云服务器操作系统版本已存在，已按上游数据更新，ID：{}，名称：{}", osVersionResponse.getId(), osVersionResponse.getOsName());
         } else {
            OsVersion osVersion = (OsVersion)BeanUtil.toBean(addParam, OsVersion.class);
            osVersion.setId(osVersionResponse.getId());
            this.osVersionMapper.insert(osVersion);
            log.info("同步云服务器操作系统版本成功，ID：{}，名称：{}", osVersionResponse.getId(), osVersionResponse.getOsName());
         }
      }

      if (ObjectUtil.isNotEmpty(memberNo)) {
         log.info("云服务器操作系统版本同步完成，会员编号：{}", memberNo);
      } else {
         log.info("云服务器操作系统版本同步完成（后台管理）");
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void syncIndependentOsVersions(ProductSyncParam syncParam) {
      Long memberNo = ApiContext.getMemberNo();
      if (ObjectUtil.isNotEmpty(memberNo)) {
         log.info("开始同步独立服务器操作系统版本，会员编号：{}，产品数量：{}", memberNo, syncParam.getProducts().size());
      } else {
         log.info("开始同步独立服务器操作系统版本（后台管理），产品数量：{}", syncParam.getProducts().size());
      }

      for (Object product : syncParam.getProducts()) {
         JSONObject productJson = JSONUtil.parseObj(product);
         IndependentOsVersionResponse osVersionResponse = (IndependentOsVersionResponse)JSONUtil.toBean(productJson, IndependentOsVersionResponse.class);
         if (ObjectUtil.isEmpty(osVersionResponse.getLineId())) {
            throw new CommonException("同步操作系统版本失败，缺少线路ID，操作系统名称：{}", new Object[]{osVersionResponse.getOsName()});
         }

         try {
            this.independentLineService.queryEntity(osVersionResponse.getLineId());
         } catch (Exception var10) {
            throw new CommonException("同步操作系统版本失败，线路ID不存在：{}，请先同步线路信息", new Object[]{osVersionResponse.getLineId()});
         }

         if (ObjectUtil.isEmpty(osVersionResponse.getOsClassId())) {
            throw new CommonException("同步操作系统版本失败，缺少操作系统类别ID，操作系统名称：{}", new Object[]{osVersionResponse.getOsName()});
         }

         IndependentOsVersion existing = (IndependentOsVersion)this.osIndependentVersionMapper.selectById(osVersionResponse.getId());
         com.rxinyun.admin.production.modular.independent.os.param.OsVersionAddParam addParam = new com.rxinyun.admin.production.modular.independent.os.param.OsVersionAddParam();
         addParam.setOsName(osVersionResponse.getOsName());
         addParam.setOsClassId(osVersionResponse.getOsClassId());
         addParam.setOsClassName(osVersionResponse.getOsClassName());
         addParam.setDefaultUserName(osVersionResponse.getDefaultUserName() != null ? osVersionResponse.getDefaultUserName() : "root");
         addParam.setDefaultPort(osVersionResponse.getDefaultPort() != null ? osVersionResponse.getDefaultPort() : 22);
         addParam.setImageDirectoryName(osVersionResponse.getImageDirectoryName());
         addParam.setLineId(osVersionResponse.getLineId());
         addParam.setStatus(osVersionResponse.getStatus() != null ? osVersionResponse.getStatus() : 1);
         this.applyResourcePoolFromSyncParam(syncParam, addParam);
         if (existing != null) {
            IndependentOsVersion toUpdate = (IndependentOsVersion)BeanUtil.toBean(addParam, IndependentOsVersion.class);
            toUpdate.setId(osVersionResponse.getId());
            this.osIndependentVersionMapper.updateById(toUpdate);
            log.info("独立服务器操作系统版本已存在，已按上游数据更新，ID：{}，名称：{}", osVersionResponse.getId(), osVersionResponse.getOsName());
         } else {
            IndependentOsVersion osVersion = (IndependentOsVersion)BeanUtil.toBean(addParam, IndependentOsVersion.class);
            osVersion.setId(osVersionResponse.getId());
            this.osIndependentVersionMapper.insert(osVersion);
            log.info("同步独立服务器操作系统版本成功，ID：{}，名称：{}", osVersionResponse.getId(), osVersionResponse.getOsName());
         }
      }

      if (ObjectUtil.isNotEmpty(memberNo)) {
         log.info("独立服务器操作系统版本同步完成，会员编号：{}", memberNo);
      } else {
         log.info("独立服务器操作系统版本同步完成（后台管理）");
      }
   }

   @Override
   public List<Object> queryCloudServerOsClassesFromPool(String resourcePoolId) {
      BaseResourcePool resourcePool = this.resourcePoolApi.getById(resourcePoolId);
      if (ObjectUtil.isEmpty(resourcePool)) {
         throw new CommonException("资源池不存在或已禁用，资源池ID为：{}", new Object[]{resourcePoolId});
      } else {
         log.info("从资源池查询云服务器操作系统类别，资源池ID：{}", resourcePoolId);

         try {
            String apiSecret = CommonCryptogramUtil.doSm4CbcDecrypt(resourcePool.getApiSecret());
            String apiUrl = resourcePool.getApiUrl();
            if (!apiUrl.endsWith("/")) {
               apiUrl = apiUrl + "/";
            }

            String requestUrl = apiUrl + "api/product/cloud-server/os-classes";
            HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.get(requestUrl).header("X-Api-Key", resourcePool.getApiKey()))
                  .header("X-Api-Secret", apiSecret))
               .timeout(30000)
               .execute();
            if (response.getStatus() != 200) {
               throw new CommonException("调用上游API失败，状态码：{}，响应：{}", new Object[]{response.getStatus(), response.body()});
            } else {
               String responseBody = response.body();
               JSONObject jsonResponse = JSONUtil.parseObj(responseBody);
               if (jsonResponse.getInt("code") != 200) {
                  throw new CommonException("上游API返回错误：{}", new Object[]{jsonResponse.getStr("msg")});
               } else {
                  JSONArray dataArray = jsonResponse.getJSONArray("data");
                  List<Object> osClasses = new ArrayList<>();
                  if (dataArray != null) {
                     for (int i = 0; i < dataArray.size(); i++) {
                        Object item = dataArray.get(i);
                        Map<String, Object> mapItem = this.convertJsonObjectToMap(item);
                        osClasses.add(mapItem);
                     }
                  }

                  log.info("从资源池查询云服务器操作系统类别成功，数量：{}", osClasses.size());
                  return osClasses;
               }
            }
         } catch (Exception var14) {
            log.error("从资源池查询云服务器操作系统类别失败，资源池ID：{}", resourcePoolId, var14);
            throw new CommonException("查询云服务器操作系统类别失败：{}", new Object[]{var14.getMessage()});
         }
      }
   }

   @Override
   public List<Object> queryIndependentOsClassesFromPool(String resourcePoolId) {
      BaseResourcePool resourcePool = this.resourcePoolApi.getById(resourcePoolId);
      if (ObjectUtil.isEmpty(resourcePool)) {
         throw new CommonException("资源池不存在或已禁用，资源池ID为：{}", new Object[]{resourcePoolId});
      } else {
         log.info("从资源池查询独立服务器操作系统类别，资源池ID：{}", resourcePoolId);

         try {
            String apiSecret = CommonCryptogramUtil.doSm4CbcDecrypt(resourcePool.getApiSecret());
            String apiUrl = resourcePool.getApiUrl();
            if (!apiUrl.endsWith("/")) {
               apiUrl = apiUrl + "/";
            }

            String requestUrl = apiUrl + "api/product/independent-server/os-classes";
            HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.get(requestUrl).header("X-Api-Key", resourcePool.getApiKey()))
                  .header("X-Api-Secret", apiSecret))
               .timeout(30000)
               .execute();
            if (response.getStatus() != 200) {
               throw new CommonException("调用上游API失败，状态码：{}，响应：{}", new Object[]{response.getStatus(), response.body()});
            } else {
               String responseBody = response.body();
               JSONObject jsonResponse = JSONUtil.parseObj(responseBody);
               if (jsonResponse.getInt("code") != 200) {
                  throw new CommonException("上游API返回错误：{}", new Object[]{jsonResponse.getStr("msg")});
               } else {
                  JSONArray dataArray = jsonResponse.getJSONArray("data");
                  List<Object> osClasses = new ArrayList<>();
                  if (dataArray != null) {
                     for (int i = 0; i < dataArray.size(); i++) {
                        Object item = dataArray.get(i);
                        Map<String, Object> mapItem = this.convertJsonObjectToMap(item);
                        osClasses.add(mapItem);
                     }
                  }

                  log.info("从资源池查询独立服务器操作系统类别成功，数量：{}", osClasses.size());
                  return osClasses;
               }
            }
         } catch (Exception var14) {
            log.error("从资源池查询独立服务器操作系统类别失败，资源池ID：{}", resourcePoolId, var14);
            throw new CommonException("查询独立服务器操作系统类别失败：{}", new Object[]{var14.getMessage()});
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void syncCloudServerOsClasses(ProductSyncParam syncParam) {
      Long memberNo = ApiContext.getMemberNo();
      if (ObjectUtil.isNotEmpty(memberNo)) {
         log.info("开始同步云服务器操作系统类别，会员编号：{}，产品数量：{}", memberNo, syncParam.getProducts().size());
      } else {
         log.info("开始同步云服务器操作系统类别（后台管理），产品数量：{}", syncParam.getProducts().size());
      }

      for (Object product : syncParam.getProducts()) {
         JSONObject productJson = JSONUtil.parseObj(product);
         CloudServerOsClassResponse osClassResponse = (CloudServerOsClassResponse)JSONUtil.toBean(productJson, CloudServerOsClassResponse.class);
         OsClass existing = (OsClass)this.osClassMapper.selectById(osClassResponse.getId());
         OsClassAddParam addParam = new OsClassAddParam();
         addParam.setOsClassName(osClassResponse.getOsClassName());
         addParam.setRemark(osClassResponse.getRemark());
         addParam.setStatus(osClassResponse.getStatus() != null ? osClassResponse.getStatus() : 1);
         this.applyResourcePoolFromSyncParam(syncParam, addParam);
         if (existing != null) {
            BeanUtil.copyProperties(addParam, existing, new String[0]);
            existing.setId(osClassResponse.getId());
            this.osClassMapper.updateById(existing);
            log.info("云服务器操作系统类别已存在，已按上游数据更新，ID：{}，名称：{}", osClassResponse.getId(), osClassResponse.getOsClassName());
         } else {
            OsClass osClass = (OsClass)BeanUtil.toBean(addParam, OsClass.class);
            osClass.setId(osClassResponse.getId());
            this.osClassMapper.insert(osClass);
            log.info("同步云服务器操作系统类别成功，ID：{}，名称：{}", osClassResponse.getId(), osClassResponse.getOsClassName());
         }
      }

      if (ObjectUtil.isNotEmpty(memberNo)) {
         log.info("云服务器操作系统类别同步完成，会员编号：{}", memberNo);
      } else {
         log.info("云服务器操作系统类别同步完成（后台管理）");
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void syncIndependentOsClasses(ProductSyncParam syncParam) {
      Long memberNo = ApiContext.getMemberNo();
      if (ObjectUtil.isNotEmpty(memberNo)) {
         log.info("开始同步独立服务器操作系统类别，会员编号：{}，产品数量：{}", memberNo, syncParam.getProducts().size());
      } else {
         log.info("开始同步独立服务器操作系统类别（后台管理），产品数量：{}", syncParam.getProducts().size());
      }

      for (Object product : syncParam.getProducts()) {
         JSONObject productJson = JSONUtil.parseObj(product);
         IndependentOsClassResponse osClassResponse = (IndependentOsClassResponse)JSONUtil.toBean(productJson, IndependentOsClassResponse.class);
         IndependentOsClass existing = (IndependentOsClass)this.osIndependentClassMapper.selectById(osClassResponse.getId());
         com.rxinyun.admin.production.modular.independent.os.param.OsClassAddParam addParam = new com.rxinyun.admin.production.modular.independent.os.param.OsClassAddParam();
         addParam.setOsClassName(osClassResponse.getOsClassName());
         addParam.setRemark(osClassResponse.getRemark());
         addParam.setStatus(osClassResponse.getStatus() != null ? osClassResponse.getStatus() : 1);
         this.applyResourcePoolFromSyncParam(syncParam, addParam);
         if (existing != null) {
            BeanUtil.copyProperties(addParam, existing, new String[0]);
            existing.setId(osClassResponse.getId());
            this.osIndependentClassMapper.updateById(existing);
            log.info("独立服务器操作系统类别已存在，已按上游数据更新，ID：{}，名称：{}", osClassResponse.getId(), osClassResponse.getOsClassName());
         } else {
            IndependentOsClass osClass = (IndependentOsClass)BeanUtil.toBean(addParam, IndependentOsClass.class);
            osClass.setId(osClassResponse.getId());
            this.osIndependentClassMapper.insert(osClass);
            log.info("同步独立服务器操作系统类别成功，ID：{}，名称：{}", osClassResponse.getId(), osClassResponse.getOsClassName());
         }
      }

      if (ObjectUtil.isNotEmpty(memberNo)) {
         log.info("独立服务器操作系统类别同步完成，会员编号：{}", memberNo);
      } else {
         log.info("独立服务器操作系统类别同步完成（后台管理）");
      }
   }

   private Map<String, Object> convertJsonObjectToMap(Object item) {
      if (item == null) {
         return null;
      } else {
         Map<String, Object> result = new HashMap<>();
         if (item instanceof JSONObject jsonObject) {
            for (String key : jsonObject.keySet()) {
               Object value = jsonObject.get(key);
               if (value instanceof JSONNull) {
                  result.put(key, null);
               } else if (value instanceof JSONObject) {
                  result.put(key, this.convertJsonObjectToMap(value));
               } else if (!(value instanceof JSONArray jsonArray)) {
                  result.put(key, value);
               } else {
                  List<Object> list = new ArrayList<>();

                  for (int i = 0; i < jsonArray.size(); i++) {
                     Object arrayItem = jsonArray.get(i);
                     if (arrayItem instanceof JSONObject || arrayItem instanceof JSONArray) {
                        list.add(this.convertJsonObjectToMap(arrayItem));
                     } else if (arrayItem instanceof JSONNull) {
                        list.add(null);
                     } else {
                        list.add(arrayItem);
                     }
                  }

                  result.put(key, list);
               }
            }
         } else {
            if (!(item instanceof Map<?, ?> map)) {
               try {
                  JSONObject jsonObject = JSONUtil.parseObj(item);
                  return this.convertJsonObjectToMap(jsonObject);
               } catch (Exception var11) {
                  log.warn("无法将对象转换为Map，对象类型：{}", item.getClass().getName());
                  return null;
               }
            }

            for (Entry<?, ?> entry : map.entrySet()) {
               Object keyx = entry.getKey();
               Object value = entry.getValue();
               if (value instanceof JSONNull) {
                  result.put(String.valueOf(keyx), null);
               } else if (!(value instanceof JSONObject) && !(value instanceof JSONArray)) {
                  result.put(String.valueOf(keyx), value);
               } else {
                  result.put(String.valueOf(keyx), this.convertJsonObjectToMap(value));
               }
            }
         }

         return result;
      }
   }

   private boolean isDockingEnabled(Integer dockingStatus) {
      return dockingStatus == null || dockingStatus == 1;
   }
}
