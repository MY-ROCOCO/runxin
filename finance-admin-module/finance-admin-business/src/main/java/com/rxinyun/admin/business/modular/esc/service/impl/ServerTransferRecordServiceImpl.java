package com.rxinyun.admin.business.modular.esc.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.admin.business.modular.esc.entity.ServerTransferRecord;
import com.rxinyun.admin.business.modular.esc.mapper.ServerMapper;
import com.rxinyun.admin.business.modular.esc.mapper.ServerTransferRecordMapper;
import com.rxinyun.admin.business.modular.esc.param.ServerTransferRecordAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerTransferRecordIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerTransferRecordPageParam;
import com.rxinyun.admin.business.modular.esc.service.ServerTransferRecordService;
import com.rxinyun.admin.business.modular.independent.entity.Independent;
import com.rxinyun.admin.business.modular.independent.mapper.IndependentMapper;
import com.rxinyun.admin.business.modular.vhost.entity.AdminVhost;
import com.rxinyun.admin.business.modular.vhost.mapper.AdminVhostMapper;
import com.rxinyun.auth.api.LoginAdminApi;
import com.rxinyun.biz.BizUserApi;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.member.member.api.MemberSaBaseLoginUserApi;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.payments.api.BalanceAdjustmentApi;
import com.rxinyun.payments.api.PaymentsRecordApi;
import com.rxinyun.payments.api.pojo.PaymentsAdminBalancePram;
import jakarta.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServerTransferRecordServiceImpl extends ServiceImpl<ServerTransferRecordMapper, ServerTransferRecord> implements ServerTransferRecordService {
   private static final Logger log = LoggerFactory.getLogger(ServerTransferRecordServiceImpl.class);
   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
   @Resource
   private MemberSaBaseLoginUserApi memberSaBaseLoginUserApi;
   @Resource
   private ServerTransferRecordMapper serverTransferRecordMapper;
   @Resource
   private BalanceAdjustmentApi balanceAdjustmentApi;
   @Resource
   private ServerMapper serverMapper;
   @Resource
   private IndependentMapper independentMapper;
   @Resource
   private AdminVhostMapper adminVhostMapper;
   @Resource
   private LoginAdminApi loginAdminApi;
   @Resource
   private BizUserApi bizUserApi;
   @Resource
   private PaymentsRecordApi paymentsRecordApi;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult add(ServerTransferRecordAddParam serverTransferRecordAddParam) {
      String password;
      try {
         password = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(serverTransferRecordAddParam.getAdminPassword()));
      } catch (Exception var11) {
         throw new CommonException("密码解密失败，请检查前端公钥", new Object[0]);
      }

      String adminId = this.loginAdminApi.getLoginUser().getId();
      if (!ObjectUtil.isNotEmpty(adminId)) {
         throw new CommonException("无权限", new Object[0]);
      } else if (!this.bizUserApi.verifyPassword(adminId, password)) {
         throw new CommonException("密码错误", new Object[0]);
      } else if (serverTransferRecordAddParam.getProductType().equals(ProductTypeEnum.CLOUD_SERVER.getCode())) {
         String hostName = serverTransferRecordAddParam.getHostName();
         LambdaQueryWrapper<Server> serverQuery = new LambdaQueryWrapper();
         serverQuery.eq(Server::getHostName, hostName);
         Server server = (Server)this.serverMapper.selectOne(serverQuery);
         if (server == null) {
            throw new CommonException("未找到对应主机名的云服务器", new Object[0]);
         } else {
            this.assertCloudServerNotLocked(server);
            LambdaQueryWrapper<ServerTransferRecord> recordQuery = new LambdaQueryWrapper();
            ((LambdaQueryWrapper<ServerTransferRecord>)recordQuery.eq(ServerTransferRecord::getHostName, hostName)).eq(ServerTransferRecord::getStatus, 0);
            Long count = this.serverTransferRecordMapper.selectCount(recordQuery);
            if (count > 0L) {
               throw new CommonException("该主机名下已存在未完成的转让记录，无法重复操作", new Object[0]);
            } else {
               SaBaseClientLoginUser saBaseClientLoginUser = this.memberSaBaseLoginUserApi
                  .getClientUserById(Long.valueOf(serverTransferRecordAddParam.getTargetMemberNo()));
               if (saBaseClientLoginUser == null) {
                  throw new CommonException("目标用户不存在", new Object[0]);
               } else if (serverTransferRecordAddParam.getTargetMemberNo().equals(server.getMemberNo().toString())) {
                  throw new CommonException("不能过户给自己", new Object[0]);
               } else {
                  ServerTransferRecord entity = new ServerTransferRecord();
                  entity.setHostName(hostName);
                  entity.setProductType(serverTransferRecordAddParam.getProductType());
                  entity.setMemberNo(String.valueOf(server.getMemberNo()));
                  entity.setTargetMemberNo(serverTransferRecordAddParam.getTargetMemberNo());
                  entity.setPayAmount(serverTransferRecordAddParam.getPayAmount());
                  entity.setOperateTime(new Date());
                  this.serverTransferRecordMapper.insert(entity);
                  return CommonResult.data("转让记录生成成功");
               }
            }
         }
      } else if (serverTransferRecordAddParam.getProductType().equals(ProductTypeEnum.INDEPENDENT_SERVER.getCode())) {
         String hostName = serverTransferRecordAddParam.getHostName();
         LambdaQueryWrapper<Independent> serverQuery = new LambdaQueryWrapper();
         serverQuery.eq(Independent::getServerName, hostName);
         Independent server = (Independent)this.independentMapper.selectOne(serverQuery);
         if (server == null) {
            throw new CommonException("未找到对应主机名的云服务器", new Object[0]);
         } else {
            this.assertIndependentServerNotLocked(server);
            LambdaQueryWrapper<ServerTransferRecord> recordQuery = new LambdaQueryWrapper();
            ((LambdaQueryWrapper<ServerTransferRecord>)recordQuery.eq(ServerTransferRecord::getHostName, hostName)).eq(ServerTransferRecord::getStatus, 0);
            Long count = this.serverTransferRecordMapper.selectCount(recordQuery);
            if (count > 0L) {
               throw new CommonException("该主机名下已存在未完成的转让记录，无法重复操作", new Object[0]);
            } else {
               SaBaseClientLoginUser saBaseClientLoginUser = this.memberSaBaseLoginUserApi
                  .getClientUserById(Long.valueOf(serverTransferRecordAddParam.getTargetMemberNo()));
               if (saBaseClientLoginUser == null) {
                  throw new CommonException("目标用户不存在", new Object[0]);
               } else if (serverTransferRecordAddParam.getTargetMemberNo().equals(server.getMemberNo().toString())) {
                  throw new CommonException("不能过户给自己", new Object[0]);
               } else {
                  ServerTransferRecord entity = new ServerTransferRecord();
                  entity.setHostName(hostName);
                  entity.setProductType(serverTransferRecordAddParam.getProductType());
                  entity.setMemberNo(String.valueOf(server.getMemberNo()));
                  entity.setTargetMemberNo(serverTransferRecordAddParam.getTargetMemberNo());
                  entity.setPayAmount(serverTransferRecordAddParam.getPayAmount());
                  entity.setOperateTime(new Date());
                  this.serverTransferRecordMapper.insert(entity);
                  return CommonResult.data("转让记录生成成功");
               }
            }
         }
      } else if (serverTransferRecordAddParam.getProductType().equals(ProductTypeEnum.VHOST.getCode())) {
         String hostName = serverTransferRecordAddParam.getHostName();
         AdminVhost host = this.findVhostByHostName(hostName);
         if (host == null) {
            throw new CommonException("未找到对应主机名的虚拟主机", new Object[0]);
         } else {
            this.assertVhostTransferable(host);
            LambdaQueryWrapper<ServerTransferRecord> recordQuery = new LambdaQueryWrapper();
            ((LambdaQueryWrapper<ServerTransferRecord>)((LambdaQueryWrapper<ServerTransferRecord>)recordQuery.eq(ServerTransferRecord::getHostName, hostName))
                  .eq(ServerTransferRecord::getProductType, ProductTypeEnum.VHOST.getCode()))
               .eq(ServerTransferRecord::getStatus, 0);
            if (this.serverTransferRecordMapper.selectCount(recordQuery) > 0L) {
               throw new CommonException("该主机名下已存在未完成的转让记录，无法重复操作", new Object[0]);
            } else {
               SaBaseClientLoginUser saBaseClientLoginUser = this.memberSaBaseLoginUserApi
                  .getClientUserById(Long.valueOf(serverTransferRecordAddParam.getTargetMemberNo()));
               if (saBaseClientLoginUser == null) {
                  throw new CommonException("目标用户不存在", new Object[0]);
               } else if (serverTransferRecordAddParam.getTargetMemberNo().equals(host.getMemberNo())) {
                  throw new CommonException("不能过户给自己", new Object[0]);
               } else {
                  ServerTransferRecord entity = new ServerTransferRecord();
                  entity.setHostName(hostName);
                  entity.setProductType(serverTransferRecordAddParam.getProductType());
                  entity.setMemberNo(host.getMemberNo());
                  entity.setTargetMemberNo(serverTransferRecordAddParam.getTargetMemberNo());
                  entity.setPayAmount(serverTransferRecordAddParam.getPayAmount());
                  entity.setOperateTime(new Date());
                  this.serverTransferRecordMapper.insert(entity);
                  return CommonResult.data("转让记录生成成功");
               }
            }
         }
      } else {
         return CommonResult.error("操作失败");
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult updateStatus(ServerTransferRecordIdParam param) {
      ServerTransferRecord record = (ServerTransferRecord)this.serverTransferRecordMapper.selectById(param.getId());
      if (record == null) {
         throw new CommonException("未找到该转让记录", new Object[0]);
      } else if (record.getStatus() != 0) {
         throw new CommonException("该记录已处理，不可重复操作", new Object[0]);
      } else if (record.getProductType().equals(ProductTypeEnum.CLOUD_SERVER.getCode())) {
         if (param.getStatus() == 1) {
            SaBaseClientLoginUser saBaseClientLoginUser = this.memberSaBaseLoginUserApi.getClientUserById(Long.valueOf(record.getTargetMemberNo()));
            if (saBaseClientLoginUser == null) {
               throw new CommonException("目标用户不存在", new Object[0]);
            }

            PaymentsAdminBalancePram paymentsAdminBalancePram = new PaymentsAdminBalancePram();
            paymentsAdminBalancePram.setAdminPassword(param.getAdminPassword());
            paymentsAdminBalancePram.setMemberNo(record.getTargetMemberNo());
            paymentsAdminBalancePram.setPayAmount(record.getPayAmount());
            paymentsAdminBalancePram.setRemark("PUSH过户");
            paymentsAdminBalancePram.setIsAdd(0);
            paymentsAdminBalancePram.setMemberAccount(saBaseClientLoginUser.getAccount());
            this.balanceAdjustmentApi.adminAdjustAdd(paymentsAdminBalancePram);
            LambdaQueryWrapper<Server> serverQuery = new LambdaQueryWrapper();
            serverQuery.eq(Server::getHostName, record.getHostName());
            Server server = (Server)this.serverMapper.selectOne(serverQuery);
            this.assertCloudServerNotLocked(server);
            server.setMemberNo(record.getTargetMemberNo());
            this.serverMapper.updateById(server);
            record.setStatus(1);
         } else {
            if (param.getStatus() != 2) {
               throw new CommonException("status值不是1或2", new Object[0]);
            }

            record.setStatus(2);
         }

         record.setOperateTime(new Date());
         this.serverTransferRecordMapper.updateById(record);
         return CommonResult.data("操作成功");
      } else if (record.getProductType().equals(ProductTypeEnum.INDEPENDENT_SERVER.getCode())) {
         if (param.getStatus() == 1) {
            SaBaseClientLoginUser saBaseClientLoginUser = this.memberSaBaseLoginUserApi.getClientUserById(Long.valueOf(record.getTargetMemberNo()));
            if (saBaseClientLoginUser == null) {
               throw new CommonException("目标用户不存在", new Object[0]);
            }

            PaymentsAdminBalancePram paymentsAdminBalancePram = new PaymentsAdminBalancePram();
            paymentsAdminBalancePram.setAdminPassword(param.getAdminPassword());
            paymentsAdminBalancePram.setMemberNo(record.getTargetMemberNo());
            paymentsAdminBalancePram.setPayAmount(record.getPayAmount());
            paymentsAdminBalancePram.setRemark("PUSH过户");
            paymentsAdminBalancePram.setIsAdd(0);
            paymentsAdminBalancePram.setMemberAccount(saBaseClientLoginUser.getAccount());
            this.balanceAdjustmentApi.adminAdjustAdd(paymentsAdminBalancePram);
            LambdaQueryWrapper<Independent> serverQuery = new LambdaQueryWrapper();
            serverQuery.eq(Independent::getServerName, record.getHostName());
            Independent server = (Independent)this.independentMapper.selectOne(serverQuery);
            this.assertIndependentServerNotLocked(server);
            server.setMemberNo(saBaseClientLoginUser.getId());
            this.independentMapper.updateById(server);
            record.setStatus(1);
         } else {
            if (param.getStatus() != 2) {
               throw new CommonException("status值不是1或2", new Object[0]);
            }

            record.setStatus(2);
         }

         record.setOperateTime(new Date());
         this.serverTransferRecordMapper.updateById(record);
         return CommonResult.data("操作成功");
      } else if (record.getProductType().equals(ProductTypeEnum.VHOST.getCode())) {
         if (param.getStatus() == 1) {
            SaBaseClientLoginUser saBaseClientLoginUser = this.memberSaBaseLoginUserApi.getClientUserById(Long.valueOf(record.getTargetMemberNo()));
            if (saBaseClientLoginUser == null) {
               throw new CommonException("目标用户不存在", new Object[0]);
            }

            PaymentsAdminBalancePram paymentsAdminBalancePram = new PaymentsAdminBalancePram();
            paymentsAdminBalancePram.setAdminPassword(param.getAdminPassword());
            paymentsAdminBalancePram.setMemberNo(record.getTargetMemberNo());
            paymentsAdminBalancePram.setPayAmount(record.getPayAmount());
            paymentsAdminBalancePram.setRemark("PUSH过户");
            paymentsAdminBalancePram.setIsAdd(0);
            paymentsAdminBalancePram.setMemberAccount(saBaseClientLoginUser.getAccount());
            this.balanceAdjustmentApi.adminAdjustAdd(paymentsAdminBalancePram);
            AdminVhost host = this.findVhostByHostName(record.getHostName());
            this.assertVhostTransferable(host);
            host.setMemberNo(record.getTargetMemberNo());
            this.adminVhostMapper.updateById(host);
            record.setStatus(1);
         } else {
            if (param.getStatus() != 2) {
               throw new CommonException("status值不是1或2", new Object[0]);
            }

            record.setStatus(2);
         }

         record.setOperateTime(new Date());
         this.serverTransferRecordMapper.updateById(record);
         return CommonResult.data("操作成功");
      } else {
         return CommonResult.error("操作失败");
      }
   }

   private void assertCloudServerNotLocked(Server server) {
      if (server != null && ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
         throw new CommonException("云服务器已锁定，请先解锁后再操作", new Object[0]);
      }
   }

   private void assertIndependentServerNotLocked(Independent server) {
      if (server != null && ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
         throw new CommonException("独立服务器已锁定，请先解锁后再操作", new Object[0]);
      }
   }

   private AdminVhost findVhostByHostName(String hostName) {
      return StrUtil.isBlank(hostName)
         ? null
         : (AdminVhost)this.adminVhostMapper
            .selectOne((Wrapper<AdminVhost>)((LambdaQueryWrapper<AdminVhost>)new LambdaQueryWrapper<AdminVhost>().eq(AdminVhost::getHostName, hostName)).last("limit 1"));
   }

   private void assertVhostTransferable(AdminVhost host) {
      if (host == null) {
         throw new CommonException("未找到对应主机名的虚拟主机", new Object[0]);
      } else if (BusinessStatusEnum.DELETE.getCode().equals(host.getBusinessStatus())) {
         throw new CommonException("虚拟主机已删除，无法过户", new Object[0]);
      } else if (ServerStatusEnum.LOCKED.getCode().equals(host.getHostStatus())) {
         throw new CommonException("虚拟主机已锁定，请先解锁后再操作", new Object[0]);
      }
   }

   public String generateOrderNo() {
      String timestamp = TimeUtil.now().format(DATE_TIME_FORMATTER);
      int randomPart = ThreadLocalRandom.current().nextInt(100000, 1000000);
      return timestamp + String.format("%06d", randomPart);
   }

   @Override
   public Page<ServerTransferRecord> page(ServerTransferRecordPageParam pageParam) {
      QueryWrapper<ServerTransferRecord> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<ServerTransferRecord>)((LambdaQueryWrapper<ServerTransferRecord>)q.like(ServerTransferRecord::getHostName, pageParam.getSearchKey())).or())
                  .like(ServerTransferRecord::getTargetMemberNo, pageParam.getSearchKey())
            );
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(ServerTransferRecord::getCreateTime);
      }

      return (Page<ServerTransferRecord>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }
}
