package com.rxinyun.member.business.modular.esc.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.member.business.modular.esc.entity.MemberServer;
import com.rxinyun.member.business.modular.esc.entity.MemberServerTransferRecord;
import com.rxinyun.member.business.modular.esc.mapper.MemberServerMapper;
import com.rxinyun.member.business.modular.esc.mapper.MemberServerTransferRecordMapper;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordPageParam;
import com.rxinyun.member.business.modular.esc.service.MemberServerTransferRecordService;
import com.rxinyun.member.business.modular.independent.entity.MemberIndependent;
import com.rxinyun.member.business.modular.independent.mapper.MemberIndependentMapper;
import com.rxinyun.member.business.modular.vhost.entity.MemberVhost;
import com.rxinyun.member.business.modular.vhost.mapper.MemberVhostMapper;
import com.rxinyun.member.member.api.MemberAuthApi;
import com.rxinyun.member.member.api.MemberSaBaseLoginUserApi;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.payments.api.PaymentsApi;
import jakarta.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServerTransferRecordServiceImpl
   extends ServiceImpl<MemberServerTransferRecordMapper, MemberServerTransferRecord>
   implements MemberServerTransferRecordService {
   private static final Logger log = LoggerFactory.getLogger(MemberServerTransferRecordServiceImpl.class);
   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
   @Resource
   private MemberSaBaseLoginUserApi memberSaBaseLoginUserApi;
   @Resource
   private MemberServerTransferRecordMapper memberServerTransferRecordMapper;
   @Resource
   private MemberServerMapper memberServerMapper;
   @Resource
   private MemberIndependentMapper memberIndependentMapper;
   @Resource
   private MemberVhostMapper memberVhostMapper;
   @Resource
   private MemberAuthApi memberAuthApi;
   @Resource
   private PaymentsApi paymentsApi;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult add(MemberServerTransferRecordAddParam serverTransferRecordAddParam) {
      if (serverTransferRecordAddParam.getProductType().equals(ProductTypeEnum.CLOUD_SERVER.getCode())) {
         String hostName = serverTransferRecordAddParam.getHostName();
         LambdaQueryWrapper<MemberServer> serverQuery = new LambdaQueryWrapper();
         serverQuery.eq(MemberServer::getHostName, hostName);
         MemberServer server = (MemberServer)this.memberServerMapper.selectOne(serverQuery);
         if (server == null) {
            throw new CommonException("未找到对应主机名的云服务器", new Object[0]);
         } else {
            this.assertCloudServerNotLocked(server);
            LambdaQueryWrapper<MemberServerTransferRecord> recordQuery = new LambdaQueryWrapper();
            ((LambdaQueryWrapper<MemberServerTransferRecord>)recordQuery.eq(MemberServerTransferRecord::getHostName, hostName)).eq(MemberServerTransferRecord::getStatus, 0);
            Long count = this.memberServerTransferRecordMapper.selectCount(recordQuery);
            if (count > 0L) {
               throw new CommonException("该主机名下已存在未完成的转让记录，无法重复操作", new Object[0]);
            } else {
               SaBaseClientLoginUser saBaseClientLoginUser = this.memberSaBaseLoginUserApi
                  .getClientUserById(Long.valueOf(serverTransferRecordAddParam.getTargetMemberNo()));
               if (saBaseClientLoginUser == null) {
                  throw new CommonException("目标用户不存在", new Object[0]);
               } else {
                  SaBaseClientLoginUser derive = this.memberSaBaseLoginUserApi.getClientUserById(Long.valueOf(server.getMemberNo()));
                  if (derive == null) {
                     throw new CommonException("发起人不存在,根据会员编号查询为null", new Object[0]);
                  } else if (serverTransferRecordAddParam.getTargetMemberNo().equals(server.getMemberNo())) {
                     throw new CommonException("不能过户给自己", new Object[0]);
                  } else if (serverTransferRecordAddParam.getMemberPassword().isEmpty()) {
                     throw new CommonException("密码为空", new Object[0]);
                  } else {
                     String password;
                     try {
                        password = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(serverTransferRecordAddParam.getMemberPassword()));
                     } catch (Exception var13) {
                        throw new CommonException("会员密码解析失败", new Object[0]);
                     }

                     SaBaseClientLoginUser clientLoginUser = this.memberAuthApi.getClientLoginUser();
                     if (clientLoginUser == null) {
                        throw new CommonException("无法根据token获取用户信息", new Object[0]);
                     } else {
                        SaBaseClientLoginUser user = this.memberSaBaseLoginUserApi.getClientUserById(clientLoginUser.getId());
                        if (!password.equals(user.getPassword())) {
                           throw new CommonException("输入的密码错误，请重新输入", new Object[0]);
                        } else {
                           MemberServerTransferRecord entity = new MemberServerTransferRecord();
                           entity.setHostName(hostName);
                           entity.setProductType(serverTransferRecordAddParam.getProductType());
                           entity.setMemberNo(String.valueOf(server.getMemberNo()));
                           entity.setTargetMemberNo(serverTransferRecordAddParam.getTargetMemberNo());
                           entity.setPayAmount(serverTransferRecordAddParam.getPayAmount());
                           entity.setOperateTime(new Date());
                           this.memberServerTransferRecordMapper.insert(entity);
                           return CommonResult.data("转让记录生成成功");
                        }
                     }
                  }
               }
            }
         }
      } else if (serverTransferRecordAddParam.getProductType().equals(ProductTypeEnum.INDEPENDENT_SERVER.getCode())) {
         String hostName = serverTransferRecordAddParam.getHostName();
         LambdaQueryWrapper<MemberIndependent> serverQuery = new LambdaQueryWrapper();
         serverQuery.eq(MemberIndependent::getServerName, hostName);
         MemberIndependent server = (MemberIndependent)this.memberIndependentMapper.selectOne(serverQuery);
         if (server == null) {
            throw new CommonException("未找到对应主机名的独立服务器", new Object[0]);
         } else {
            this.assertIndependentServerNotLocked(server);
            LambdaQueryWrapper<MemberServerTransferRecord> recordQuery = new LambdaQueryWrapper();
            ((LambdaQueryWrapper<MemberServerTransferRecord>)recordQuery.eq(MemberServerTransferRecord::getHostName, hostName)).eq(MemberServerTransferRecord::getStatus, 0);
            Long count = this.memberServerTransferRecordMapper.selectCount(recordQuery);
            if (count > 0L) {
               throw new CommonException("该主机名下已存在未完成的转让记录，无法重复操作", new Object[0]);
            } else {
               SaBaseClientLoginUser saBaseClientLoginUser = this.memberSaBaseLoginUserApi
                  .getClientUserById(Long.valueOf(serverTransferRecordAddParam.getTargetMemberNo()));
               if (saBaseClientLoginUser == null) {
                  throw new CommonException("目标用户不存在", new Object[0]);
               } else {
                  SaBaseClientLoginUser derive = this.memberSaBaseLoginUserApi.getClientUserById(server.getMemberNo());
                  if (derive == null) {
                     throw new CommonException("发起人不存在,根据会员编号查询为null", new Object[0]);
                  } else if (serverTransferRecordAddParam.getTargetMemberNo().equals(server.getMemberNo())) {
                     throw new CommonException("不能过户给自己", new Object[0]);
                  } else if (serverTransferRecordAddParam.getMemberPassword().isEmpty()) {
                     throw new CommonException("密码为空", new Object[0]);
                  } else {
                     String passwordx;
                     try {
                        passwordx = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(serverTransferRecordAddParam.getMemberPassword()));
                     } catch (Exception var14) {
                        throw new CommonException("会员密码解析失败", new Object[0]);
                     }

                     SaBaseClientLoginUser clientLoginUser = this.memberAuthApi.getClientLoginUser();
                     if (clientLoginUser == null) {
                        throw new CommonException("无法根据token获取用户信息", new Object[0]);
                     } else {
                        SaBaseClientLoginUser user = this.memberSaBaseLoginUserApi.getClientUserById(clientLoginUser.getId());
                        if (!passwordx.equals(user.getPassword())) {
                           throw new CommonException("输入的密码错误，请重新输入", new Object[0]);
                        } else {
                           MemberServerTransferRecord entity = new MemberServerTransferRecord();
                           entity.setHostName(hostName);
                           entity.setProductType(serverTransferRecordAddParam.getProductType());
                           entity.setMemberNo(String.valueOf(server.getMemberNo()));
                           entity.setTargetMemberNo(serverTransferRecordAddParam.getTargetMemberNo());
                           entity.setPayAmount(serverTransferRecordAddParam.getPayAmount());
                           entity.setOperateTime(new Date());
                           this.memberServerTransferRecordMapper.insert(entity);
                           return CommonResult.data("转让记录生成成功");
                        }
                     }
                  }
               }
            }
         }
      } else if (serverTransferRecordAddParam.getProductType().equals(ProductTypeEnum.VHOST.getCode())) {
         String hostName = serverTransferRecordAddParam.getHostName();
         MemberVhost host = this.findVhostByHostName(hostName);
         if (host == null) {
            throw new CommonException("未找到对应主机名的虚拟主机", new Object[0]);
         } else {
            this.assertVhostTransferable(host);
            LambdaQueryWrapper<MemberServerTransferRecord> recordQuery = new LambdaQueryWrapper();
            ((LambdaQueryWrapper<MemberServerTransferRecord>)((LambdaQueryWrapper<MemberServerTransferRecord>)recordQuery.eq(MemberServerTransferRecord::getHostName, hostName))
                  .eq(MemberServerTransferRecord::getProductType, ProductTypeEnum.VHOST.getCode()))
               .eq(MemberServerTransferRecord::getStatus, 0);
            if (this.memberServerTransferRecordMapper.selectCount(recordQuery) > 0L) {
               throw new CommonException("该主机名下已存在未完成的转让记录，无法重复操作", new Object[0]);
            } else {
               SaBaseClientLoginUser saBaseClientLoginUser = this.memberSaBaseLoginUserApi
                  .getClientUserById(Long.valueOf(serverTransferRecordAddParam.getTargetMemberNo()));
               if (saBaseClientLoginUser == null) {
                  throw new CommonException("目标用户不存在", new Object[0]);
               } else {
                  SaBaseClientLoginUser derive = this.memberSaBaseLoginUserApi.getClientUserById(Long.valueOf(host.getMemberNo()));
                  if (derive == null) {
                     throw new CommonException("发起人不存在,根据会员编号查询为null", new Object[0]);
                  } else if (serverTransferRecordAddParam.getTargetMemberNo().equals(host.getMemberNo())) {
                     throw new CommonException("不能过户给自己", new Object[0]);
                  } else if (serverTransferRecordAddParam.getMemberPassword().isEmpty()) {
                     throw new CommonException("密码为空", new Object[0]);
                  } else {
                     String passwordxx;
                     try {
                        passwordxx = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(serverTransferRecordAddParam.getMemberPassword()));
                     } catch (Exception var15) {
                        throw new CommonException("会员密码解析失败", new Object[0]);
                     }

                     SaBaseClientLoginUser clientLoginUser = this.memberAuthApi.getClientLoginUser();
                     if (clientLoginUser == null) {
                        throw new CommonException("无法根据token获取用户信息", new Object[0]);
                     } else {
                        SaBaseClientLoginUser user = this.memberSaBaseLoginUserApi.getClientUserById(clientLoginUser.getId());
                        if (!passwordxx.equals(user.getPassword())) {
                           throw new CommonException("输入的密码错误，请重新输入", new Object[0]);
                        } else {
                           MemberServerTransferRecord entity = new MemberServerTransferRecord();
                           entity.setHostName(hostName);
                           entity.setProductType(serverTransferRecordAddParam.getProductType());
                           entity.setMemberNo(host.getMemberNo());
                           entity.setTargetMemberNo(serverTransferRecordAddParam.getTargetMemberNo());
                           entity.setPayAmount(serverTransferRecordAddParam.getPayAmount());
                           entity.setOperateTime(new Date());
                           this.memberServerTransferRecordMapper.insert(entity);
                           return CommonResult.data("转让记录生成成功");
                        }
                     }
                  }
               }
            }
         }
      } else {
         return CommonResult.error("参数错误");
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult updateStatus(MemberServerTransferRecordIdParam param) {
      MemberServerTransferRecord record = (MemberServerTransferRecord)this.memberServerTransferRecordMapper.selectById(param.getId());
      if (record == null) {
         throw new CommonException("未找到该转让记录", new Object[0]);
      } else if (record.getStatus() != 0) {
         throw new CommonException("该记录已处理，不可重复操作", new Object[0]);
      } else {
         SaBaseClientLoginUser clientLoginUser = this.memberAuthApi.getClientLoginUser();
         if (clientLoginUser == null) {
            throw new CommonException("无法根据token获取用户信息", new Object[0]);
         } else {
            SaBaseClientLoginUser user = this.memberSaBaseLoginUserApi.getClientUserById(clientLoginUser.getId());
            if (record.getProductType().equals(ProductTypeEnum.CLOUD_SERVER.getCode())) {
               if (param.getStatus() == 1) {
                  if (String.valueOf(user.getId()).equals(record.getMemberNo())) {
                     throw new CommonException("当前用户不是接收方", new Object[0]);
                  }

                  SaBaseClientLoginUser saBaseClientLoginUser = this.memberSaBaseLoginUserApi.getClientUserById(Long.valueOf(record.getTargetMemberNo()));
                  if (saBaseClientLoginUser == null) {
                     throw new CommonException("目标用户不存在,无法进行转让", new Object[0]);
                  }

                  String orderNo = this.generateOrderNo();
                  this.paymentsApi.pay(4, "", "PUSH过户,扣减金额：" + record.getPayAmount(), Long.valueOf(record.getTargetMemberNo()), orderNo, record.getPayAmount());
                  LambdaQueryWrapper<MemberServer> serverQuery = new LambdaQueryWrapper();
                  serverQuery.eq(MemberServer::getHostName, record.getHostName());
                  MemberServer server = (MemberServer)this.memberServerMapper.selectOne(serverQuery);
                  this.assertCloudServerNotLocked(server);
                  server.setMemberNo(record.getTargetMemberNo());
                  this.memberServerMapper.updateById(server);
                  record.setStatus(1);
               } else if (param.getStatus() == 2) {
                  record.setStatus(2);
               }
            } else if (record.getProductType().equals(ProductTypeEnum.INDEPENDENT_SERVER.getCode())) {
               if (param.getStatus() == 1) {
                  if (String.valueOf(user.getId()).equals(record.getMemberNo())) {
                     throw new CommonException("当前用户不是接收方", new Object[0]);
                  }

                  SaBaseClientLoginUser saBaseClientLoginUser = this.memberSaBaseLoginUserApi.getClientUserById(Long.valueOf(record.getTargetMemberNo()));
                  if (saBaseClientLoginUser == null) {
                     throw new CommonException("目标用户不存在,无法进行转让", new Object[0]);
                  }

                  String orderNo = this.generateOrderNo();
                  this.paymentsApi.pay(4, "", "PUSH过户,扣减金额：" + record.getPayAmount(), Long.valueOf(record.getTargetMemberNo()), orderNo, record.getPayAmount());
                  LambdaQueryWrapper<MemberIndependent> serverQuery = new LambdaQueryWrapper();
                  serverQuery.eq(MemberIndependent::getServerName, record.getHostName());
                  MemberIndependent server = (MemberIndependent)this.memberIndependentMapper.selectOne(serverQuery);
                  this.assertIndependentServerNotLocked(server);
                  server.setMemberNo(Long.valueOf(record.getTargetMemberNo()));
                  this.memberIndependentMapper.updateById(server);
                  record.setStatus(1);
               } else if (param.getStatus() == 2) {
                  record.setStatus(2);
               }
            } else if (record.getProductType().equals(ProductTypeEnum.VHOST.getCode())) {
               if (param.getStatus() == 1) {
                  if (String.valueOf(user.getId()).equals(record.getMemberNo())) {
                     throw new CommonException("当前用户不是接收方", new Object[0]);
                  }

                  SaBaseClientLoginUser saBaseClientLoginUser = this.memberSaBaseLoginUserApi.getClientUserById(Long.valueOf(record.getTargetMemberNo()));
                  if (saBaseClientLoginUser == null) {
                     throw new CommonException("目标用户不存在,无法进行转让", new Object[0]);
                  }

                  String orderNo = this.generateOrderNo();
                  this.paymentsApi.pay(4, "", "PUSH过户,扣减金额：" + record.getPayAmount(), Long.valueOf(record.getTargetMemberNo()), orderNo, record.getPayAmount());
                  MemberVhost host = this.findVhostByHostName(record.getHostName());
                  this.assertVhostTransferable(host);
                  host.setMemberNo(record.getTargetMemberNo());
                  this.memberVhostMapper.updateById(host);
                  record.setStatus(1);
               } else if (param.getStatus() == 2) {
                  record.setStatus(2);
               }
            }

            record.setOperateTime(new Date());
            this.memberServerTransferRecordMapper.updateById(record);
            return CommonResult.data("操作成功");
         }
      }
   }

   private void assertCloudServerNotLocked(MemberServer server) {
      if (server != null && ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
         throw new CommonException("云服务器已锁定，请先解锁后再操作", new Object[0]);
      }
   }

   private void assertIndependentServerNotLocked(MemberIndependent server) {
      if (server != null && ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
         throw new CommonException("独立服务器已锁定，请先解锁后再操作", new Object[0]);
      }
   }

   private MemberVhost findVhostByHostName(String hostName) {
      return StrUtil.isBlank(hostName)
         ? null
         : (MemberVhost)this.memberVhostMapper
            .selectOne((Wrapper<MemberVhost>)((LambdaQueryWrapper<MemberVhost>)new LambdaQueryWrapper<MemberVhost>().eq(MemberVhost::getHostName, hostName)).last("limit 1"));
   }

   private void assertVhostTransferable(MemberVhost host) {
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
   public Page<MemberServerTransferRecord> page(MemberServerTransferRecordPageParam pageParam) {
      SaBaseClientLoginUser clientLoginUser = this.memberAuthApi.getClientLoginUser();
      if (clientLoginUser == null) {
         throw new CommonException("无法根据token获取用户信息", new Object[0]);
      } else {
         SaBaseClientLoginUser user = this.memberSaBaseLoginUserApi.getClientUserById(clientLoginUser.getId());
         QueryWrapper<MemberServerTransferRecord> queryWrapper = new QueryWrapper().checkSqlInjection();
         if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
            queryWrapper.lambda()
               .and(
                  q -> ((LambdaQueryWrapper<MemberServerTransferRecord>)((LambdaQueryWrapper<MemberServerTransferRecord>)q.like(MemberServerTransferRecord::getHostName, pageParam.getSearchKey())).or())
                     .like(MemberServerTransferRecord::getTargetMemberNo, pageParam.getSearchKey())
               );
         }

         ((LambdaQueryWrapper<MemberServerTransferRecord>)((LambdaQueryWrapper<MemberServerTransferRecord>)queryWrapper.lambda().eq(MemberServerTransferRecord::getMemberNo, user.getId())).or())
            .eq(MemberServerTransferRecord::getTargetMemberNo, user.getId());
         if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
            CommonSortOrderEnum.validate(pageParam.getSortOrder());
            queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
         } else {
            queryWrapper.lambda().orderByDesc(MemberServerTransferRecord::getCreateTime);
         }

         return (Page<MemberServerTransferRecord>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      }
   }
}
