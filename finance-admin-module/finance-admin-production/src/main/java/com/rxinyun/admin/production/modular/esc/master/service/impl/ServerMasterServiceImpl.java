package com.rxinyun.admin.production.modular.esc.master.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.esc.ip.service.IpMasterRelationService;
import com.rxinyun.admin.production.modular.esc.master.entity.ServerMaster;
import com.rxinyun.admin.production.modular.esc.master.mapper.ServerMasterMapper;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterAddParam;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterEditParam;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterIdParam;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterLimitedEditParam;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterPageParam;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterStatusParam;
import com.rxinyun.admin.production.modular.esc.master.service.ServerMasterLineService;
import com.rxinyun.admin.production.modular.esc.master.service.ServerMasterService;
import com.rxinyun.admin.production.modular.esc.master.vo.MasterStatusVO;
import com.rxinyun.common.controller.CloudControllerAdapter;
import com.rxinyun.common.controller.CloudControllerRouter;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.qzsystem.entity.MasterMonitoringItem;
import com.rxinyun.common.qzsystem.entity.MasterStatusParam;
import com.rxinyun.common.qzsystem.entity.SystemInfo;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServerMasterServiceImpl extends ServiceImpl<ServerMasterMapper, ServerMaster> implements ServerMasterService {
   private static final String MASTER_TABLE_ALIAS = "sm.";
   @Resource
   private ServerMasterMapper serverMasterMapper;
   @Resource
   private IpMasterRelationService ipMasterRelationService;
   @Resource
   private ServerMasterLineService serverMasterLineService;
   @Resource
   private CloudControllerRouter cloudControllerRouter;

   @Override
   public Page<ServerMaster> page(ServerMasterPageParam pageParam) {
      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         pageParam.setSortField(this.getSortColumn(pageParam.getSortField()));
         pageParam.setSortOrder(pageParam.getSortOrder().equalsIgnoreCase(CommonSortOrderEnum.ASC.getValue()) ? "ASC" : "DESC");
      } else {
         pageParam.setSortField(null);
         pageParam.setSortOrder(null);
      }

      Page<ServerMaster> page = new Page(pageParam.getCurrent().intValue(), pageParam.getSize().intValue());
      return this.serverMasterMapper.selectPage(page, pageParam);
   }

   private String getSortColumn(String sortField) {
      String column = StrUtil.toUnderlineCase(sortField);

      return switch (column) {
         case "line_name", "line_names" -> "lineNames";
         case "id", "master_name", "guard_version", "local_ip", "gateway_ip", "switch_type", "ip", "api_port", "status", "sort_id", "instance_count", "ram_remaining", "disk_remaining", "remarks", "store_type", "store_id", "root_port", "root_password", "os_path", "os_maxiops", "disks_path", "disks_maxiops", "cpu_limit", "ram_size", "ram_limit", "harddisk_size", "harddisk_limit", "hv_store_type", "hv_store_id", "node_name", "memory_dynamic", "ram_start", "vnc_url", "public_switch_name", "private_switch_name", "nat_switch_name", "share_ip", "mirror_path", "backup_path", "task_concurrency", "password", "create_time", "update_time" -> "sm."
            + column;
         default -> throw new CommonException("不支持该排序字段：{}", new Object[]{sortField});
      };
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(ServerMasterAddParam serverMasterAddParam) {
      List<String> lineIds = this.resolveLineIds(serverMasterAddParam.getLineIds());
      ServerMaster serverMaster = (ServerMaster)BeanUtil.toBean(serverMasterAddParam, ServerMaster.class);
      serverMaster.setInstanceCount(0);
      serverMaster.setRamRemaining(serverMaster.getRamSize());
      if (serverMaster.getHarddiskSize() != null) {
         serverMaster.setDiskRemaining(serverMaster.getHarddiskSize().longValue());
      }

      this.save(serverMaster);
      this.serverMasterLineService.replaceMasterLines(String.valueOf(serverMaster.getId()), lineIds);
   }

   @Override
   public void edit(ServerMasterEditParam serverMasterEditParam) {
      ServerMaster serverMaster = this.queryEntity(serverMasterEditParam.getId());
      List<String> lineIds = this.resolveLineIds(serverMasterEditParam.getLineIds());
      BeanUtil.copyProperties(serverMasterEditParam, serverMaster, new String[0]);
      this.updateById(serverMaster);
      this.serverMasterLineService.replaceMasterLines(String.valueOf(serverMaster.getId()), lineIds);
   }

   @Override
   public void edit(ServerMasterLimitedEditParam serverMasterLimitedEditParam) {
      ServerMaster serverMaster = this.queryEntity(serverMasterLimitedEditParam.getId());
      serverMaster.setPrivateSwitchName(serverMasterLimitedEditParam.getPrivateSwitchName());
      serverMaster.setPublicSwitchName(serverMasterLimitedEditParam.getPublicSwitchName());
      serverMaster.setTaskConcurrency(serverMasterLimitedEditParam.getTaskConcurrency());
      serverMaster.setMirrorPath(serverMasterLimitedEditParam.getMirrorPath());
      this.updateById(serverMaster);
   }

   @Override
   public ServerMaster detail(ServerMasterIdParam serverMasterIdParam) {
      return this.fillLineIds(this.queryEntity(serverMasterIdParam.getId()));
   }

   @Override
   public ServerMaster queryEntity(String id) {
      ServerMaster serverMaster = (ServerMaster)this.getById(id);
      if (ObjectUtil.isEmpty(serverMaster)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return this.fillLineIds(serverMaster);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<ServerMasterIdParam> serverMasterIdParamList) {
      List<String> serverMasterIdList = CollStreamUtil.toList(serverMasterIdParamList, ServerMasterIdParam::getId);
      if (!ObjectUtil.isEmpty(serverMasterIdList)) {
         boolean anyBoundToIp = serverMasterIdList.stream().anyMatch(masterId -> this.ipMasterRelationService.isBindingIp(masterId));
         if (anyBoundToIp) {
            throw new CommonException("存在已绑定IP的宿主机，无法删除", new Object[0]);
         } else {
            this.removeByIds(serverMasterIdList);
         }
      }
   }

   @Override
   public CommonResult<MasterStatusVO> selectByStatus(ServerMasterStatusParam param) {
      if (ObjectUtil.isEmpty(param.getMasterId())) {
         return CommonResult.error("宿主机ID不能为空");
      } else {
         ServerMaster master = (ServerMaster)this.getById(param.getMasterId());
         if (master == null) {
            return CommonResult.error("宿主机不存在");
         } else {
            MasterStatusParam statusParam = new MasterStatusParam();
            statusParam.setBindId(String.valueOf(master.getId()));
            statusParam.setIp(master.getIp());
            statusParam.setApiPort(master.getApiPort());
            statusParam.setApiPassword(master.getApiPassword());
            statusParam.setMfcNodeId(master.getMfcNodeId());
            statusParam.setMfcLoginAccount(master.getMfcLoginAccount());
            statusParam.setMfcLoginPassword(master.getMfcLoginPassword());
            CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(master.getGuardVersion());
            CommonResult<SystemInfo> result = adapter.getMasterStatus(statusParam);
            Integer status = result.getData() != null && ((SystemInfo)result.getData()).getStatus() != null ? ((SystemInfo)result.getData()).getStatus() : 0;
            MasterStatusVO vo = new MasterStatusVO(status);
            return result.getCode() != null && result.getCode() == 200
               ? CommonResult.data(vo)
               : CommonResult.get(result.getCode() != null ? result.getCode() : 500, result.getMsg() != null ? result.getMsg() : "请求失败", vo);
         }
      }
   }

   @Override
   public CommonResult selectByCompanyInfo(ServerMasterStatusParam param) {
      if (ObjectUtil.isEmpty(param.getMasterId())) {
         return CommonResult.error("宿主机ID不能为空");
      } else {
         ServerMaster master = (ServerMaster)this.getById(param.getMasterId());
         if (master == null) {
            return CommonResult.error("宿主机不存在");
         } else {
            MasterStatusParam statusParam = new MasterStatusParam();
            statusParam.setBindId(String.valueOf(master.getId()));
            statusParam.setIp(master.getIp());
            statusParam.setApiPort(master.getApiPort());
            statusParam.setApiPassword(master.getApiPassword());
            statusParam.setMfcNodeId(master.getMfcNodeId());
            statusParam.setMfcLoginAccount(master.getMfcLoginAccount());
            statusParam.setMfcLoginPassword(master.getMfcLoginPassword());
            CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(master.getGuardVersion());
            return adapter.getCompanyInfo(statusParam);
         }
      }
   }

   @Override
   public CommonResult<List<MasterMonitoringItem>> selectByNetworkMonitoring(ServerMasterStatusParam param) {
      if (ObjectUtil.isEmpty(param.getMasterId())) {
         return CommonResult.error("宿主机ID不能为空");
      } else {
         ServerMaster master = (ServerMaster)this.getById(param.getMasterId());
         if (master == null) {
            return CommonResult.error("宿主机不存在");
         } else {
            MasterStatusParam statusParam = new MasterStatusParam();
            statusParam.setBindId(String.valueOf(master.getId()));
            statusParam.setIp(master.getIp());
            statusParam.setApiPort(master.getApiPort());
            statusParam.setApiPassword(master.getApiPassword());
            statusParam.setMfcNodeId(master.getMfcNodeId());
            statusParam.setMfcLoginAccount(master.getMfcLoginAccount());
            statusParam.setMfcLoginPassword(master.getMfcLoginPassword());
            CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(master.getGuardVersion());
            return adapter.getNetworkMonitoring(statusParam);
         }
      }
   }

   @Override
   public Boolean isBindingMaster(String id) {
      if (ObjectUtil.isEmpty(id)) {
         throw new CommonException("线路id为空", new Object[0]);
      } else {
         return this.serverMasterLineService.existsByLineId(id);
      }
   }

   private List<String> resolveLineIds(List<String> lineIds) {
      List<String> result = new ArrayList<>();
      if (CollUtil.isNotEmpty(lineIds)) {
         result.addAll(lineIds);
      }

      return result.stream().filter(CharSequenceUtil::isNotBlank).distinct().collect(Collectors.toList());
   }

   private ServerMaster fillLineIds(ServerMaster serverMaster) {
      if (serverMaster != null && serverMaster.getId() != null) {
         List<String> lineIds = this.serverMasterLineService.listLineIdsByMasterId(String.valueOf(serverMaster.getId()));
         serverMaster.setLineIds(lineIds);
         return serverMaster;
      } else {
         return serverMaster;
      }
   }
}
