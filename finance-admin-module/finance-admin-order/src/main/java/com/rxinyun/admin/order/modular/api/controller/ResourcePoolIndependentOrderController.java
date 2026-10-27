package com.rxinyun.admin.order.modular.api.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.order.modular.api.helper.IndependentPoolUpgradePricing;
import com.rxinyun.admin.order.modular.api.helper.IndependentResourcePoolPriceCalculator;
import com.rxinyun.admin.order.modular.api.helper.IndependentResourcePoolUpgradeCalculator;
import com.rxinyun.admin.order.modular.ecs.entity.IndependentOrderServerConfig;
import com.rxinyun.admin.order.modular.ecs.entity.Order;
import com.rxinyun.admin.order.modular.ecs.entity.OrderServer;
import com.rxinyun.admin.order.modular.ecs.param.OrderServerAddParam;
import com.rxinyun.admin.order.modular.ecs.param.ServerRefundParam;
import com.rxinyun.admin.order.modular.ecs.service.IndependentOrderServerConfigService;
import com.rxinyun.admin.order.modular.ecs.service.OrderServerService;
import com.rxinyun.admin.order.modular.ecs.service.OrderService;
import com.rxinyun.admin.order.modular.ecs.service.ServerRefundService;
import com.rxinyun.business.api.IndependentApi;
import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.common.context.ApiContext;
import com.rxinyun.common.enums.UpgradeItemEnum;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.PayTypeEnum;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.OsInstancePasswordValidator;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import com.rxinyun.dev.api.ResourceChangeLogApi;
import com.rxinyun.dev.core.pojo.BaseResourceChangeLog;
import com.rxinyun.member.admin.api.BalanceApi;
import com.rxinyun.member.admin.pojo.BaseBalance;
import com.rxinyun.production.api.AdminMachineApi;
import com.rxinyun.production.api.MemberIndependentSystemVersionApi;
import com.rxinyun.production.core.pojo.BaseAdminMachine;
import com.rxinyun.production.core.pojo.BaseMemberIndependentSystemVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "资源池独立服务器订单API"
)
@RestController
@RequestMapping({"/api/order/independent"})
@Validated
public class ResourcePoolIndependentOrderController {
   private static final Logger log = LoggerFactory.getLogger(ResourcePoolIndependentOrderController.class);
   @Resource
   private BalanceApi balanceApi;
   @Resource
   private OrderService orderService;
   @Resource
   private IndependentOrderServerConfigService independentOrderServerConfigService;
   @Resource
   private OrderServerService orderServerService;
   @Resource
   private IndependentApi independentApi;
   @Resource
   private IndependentResourcePoolPriceCalculator independentResourcePoolPriceCalculator;
   @Resource
   private AdminMachineApi adminMachineApi;
   @Resource
   private MemberIndependentSystemVersionApi memberIndependentSystemVersionApi;
   @Resource
   private ResourceChangeLogApi resourceChangeLogApi;
   @Resource
   private IndependentResourcePoolUpgradeCalculator independentResourcePoolUpgradeCalculator;
   @Resource
   private ServerRefundService serverRefundService;

   @PostMapping({"/create"})
   @Operation(
      summary = "创建独立服务器订单（资源池，非 /api/order/create）"
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   public CommonResult createOrder(@RequestBody JSONObject requestParams) {
      try {
         log.info("收到资源池创建独立服务器请求，参数：{}", requestParams);
         if (requestParams != null && !requestParams.isEmpty()) {
            String orderNo = requestParams.getStr("orderNo");
            if (ObjectUtil.isEmpty(orderNo)) {
               return CommonResult.error("订单号不能为空");
            } else {
               Long accountMemberNo = ApiContext.getMemberNo();
               if (accountMemberNo == null) {
                  log.error("无法获取账号信息，订单号：{}", orderNo);
                  return CommonResult.error("无法获取账号信息，请检查API Key配置");
               } else {
                  Order existingOrder = (Order)this.orderService.getOne((Wrapper<Order>)new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
                  if (existingOrder != null) {
                     log.warn("订单已存在，订单号：{}", orderNo);
                     return CommonResult.error("订单已存在");
                  } else {
                     String machineId = requestParams.getStr("machineId");
                     String lineId = requestParams.getStr("lineId");
                     String regionId = requestParams.getStr("regionId");
                     String systemId = StrUtil.blankToDefault(requestParams.getStr("systemId"), requestParams.getStr("osVersionId"));
                     String systemPassword = StrUtil.blankToDefault(requestParams.getStr("systemPassword"), requestParams.getStr("loginPassword"));
                     if (!ObjectUtil.isEmpty(machineId)
                        && !ObjectUtil.isEmpty(lineId)
                        && !ObjectUtil.isEmpty(regionId)
                        && !ObjectUtil.isEmpty(systemId)
                        && !ObjectUtil.isEmpty(systemPassword)) {
                        String pwdMsg = OsInstancePasswordValidator.validate(systemPassword);
                        if (pwdMsg != null) {
                           return CommonResult.error(pwdMsg);
                        } else {
                           String systemClassId = StrUtil.blankToDefault(requestParams.getStr("systemClassId"), requestParams.getStr("osClassId"));
                           if (StrUtil.isBlank(systemClassId)) {
                              BaseMemberIndependentSystemVersion ver = this.memberIndependentSystemVersionApi.selectByOsVersionId(systemId);
                              if (ver != null) {
                                 systemClassId = ver.getOsClassId();
                              }
                           }

                           int billingMode = requestParams.getInt("billingMode", 1);
                           String unit = requestParams.getStr("unit", "m");
                           int bandwidth = requestParams.getInt("bandwidth", 0);
                           int defense = requestParams.getInt("defense", 0);
                           int ipSize = requestParams.getInt("ipSize", 0);
                           int duration = requestParams.getInt("duration", 0);
                           int number = requestParams.getInt("number", 1);
                           if (duration > 0 && number > 0) {
                              IndependentOrderServerConfig cfg = new IndependentOrderServerConfig();
                              cfg.setOrderNo(orderNo);
                              cfg.setRegionId(regionId);
                              cfg.setLineId(lineId);
                              cfg.setLoginPassword(systemPassword);
                              cfg.setSystemClassId(systemClassId);
                              cfg.setSystemId(systemId);

                              BigDecimal payPrice;
                              try {
                                 this.independentResourcePoolPriceCalculator
                                    .fillNewPurchasePricing(cfg, billingMode, unit, machineId, lineId, bandwidth, defense, ipSize, duration, number);
                                 payPrice = cfg.getPayPrice();
                                 if (payPrice == null || payPrice.compareTo(BigDecimal.ZERO) <= 0) {
                                    log.error("计算出的价格不合法，订单号：{}，金额：{}", orderNo, payPrice);
                                    return CommonResult.error("计算出的价格不合法");
                                 }
                              } catch (CommonException var27) {
                                 log.error("计算价格失败，订单号：{}，错误：{}", orderNo, var27.getMessage());
                                 return CommonResult.error("计算价格失败：" + var27.getMessage());
                              } catch (Exception var28) {
                                 log.error("计算价格异常，订单号：{}，错误：{}", new Object[]{orderNo, var28.getMessage(), var28});
                                 return CommonResult.error("计算价格失败：" + var28.getMessage());
                              }

                              BaseBalance memberBalance = this.balanceApi.selectByMemberNo(accountMemberNo);
                              if (memberBalance == null) {
                                 log.error("账号余额记录不存在，会员编号：{}，订单号：{}", accountMemberNo, orderNo);
                                 return CommonResult.error("账号余额记录不存在");
                              } else if (memberBalance.getBalance().compareTo(payPrice) < 0) {
                                 log.error(
                                    "账号余额不足，会员编号：{}，订单号：{}，当前余额：{}，需要金额：{}", new Object[]{accountMemberNo, orderNo, memberBalance.getBalance(), payPrice}
                                 );
                                 return CommonResult.error("账号余额不足，当前余额：" + memberBalance.getBalance() + "，需要金额：" + payPrice);
                              } else {
                                 Date payTime = new Date();
                                 Order order = new Order();
                                 order.setId(SnowflakeIdGenerator.generateId());
                                 order.setOrderNo(orderNo);
                                 order.setBillingMode(billingMode);
                                 order.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
                                 order.setOrderType(OrderTypeEnum.NEW_PURCHASE.getCode());
                                 order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
                                 order.setMemberNo(accountMemberNo);
                                 order.setMemberName(requestParams.getStr("memberName"));
                                 order.setMemberPhone(requestParams.getStr("memberPhone"));
                                 order.setTotalPrice(cfg.getTotalPrice());
                                 order.setPayPrice(payPrice);
                                 order.setPayType(4);
                                 order.setCreateTime(new Date());
                                 order.setUpdateTime(new Date());
                                 String serverIdsJson = null;
                                 Object callbackParamsObj = requestParams.get("callback_params");
                                 if (callbackParamsObj != null) {
                                    if (callbackParamsObj instanceof JSONArray) {
                                       serverIdsJson = ((JSONArray)callbackParamsObj).toString();
                                    } else if (callbackParamsObj instanceof String) {
                                       serverIdsJson = (String)callbackParamsObj;
                                    } else {
                                       serverIdsJson = callbackParamsObj.toString();
                                    }

                                    if (serverIdsJson != null) {
                                       order.setUpstreamServerIds(serverIdsJson);
                                       log.info("✅ 订单已关联 {} 台 B 系统服务器，订单号：{}", JSON.parseArray(serverIdsJson).size(), orderNo);
                                    }
                                 }

                                 this.orderService.save(order);
                                 log.info("创建订单成功，订单号：{}", orderNo);
                                 this.balanceApi.deductAndRecord(accountMemberNo, payPrice, orderNo, "资源池-新购独立服务器", requestParams.toString(), payTime);
                                 this.independentOrderServerConfigService.saveNew(cfg);
                                 log.info("创建独立服务器订单配置成功，订单号：{}", orderNo);

                                 try {
                                    log.info("开始处理支付成功逻辑，订单号：{}", orderNo);
                                    this.orderService.paySuccessSync(orderNo, payTime, null, PayTypeEnum.BALANCE.getCode());
                                    log.info("支付成功逻辑处理完成，订单号：{}", orderNo);
                                    this.logCreateChangeLogsForIndependentServers(orderNo, accountMemberNo);
                                 } catch (Exception e) {
                                    log.error("处理支付成功逻辑异常，订单号：{}，错误：{}", new Object[]{orderNo, e.getMessage(), e});
                                    throw new RuntimeException("处理支付成功逻辑失败：" + e.getMessage(), e);
                                 }

                                 JSONObject data = this.buildResourcePoolCreateResponsePayload(orderNo);
                                 return CommonResult.get(200, "请求已接收，余额已扣除，订单已创建", data);
                              }
                           } else {
                              return CommonResult.error("duration、number 必须大于0");
                           }
                        }
                     } else {
                        return CommonResult.error("machineId、lineId、regionId、systemId（或 osVersionId）、systemPassword（或 loginPassword）不能为空");
                     }
                  }
               }
            }
         } else {
            return CommonResult.error("参数不能为空");
         }
      } catch (CommonException var29) {
         return CommonResult.error(var29.getMessage());
      } catch (Exception var30) {
         log.error("处理资源池创建独立服务器请求失败：{}", var30.getMessage(), var30);
         return CommonResult.error("处理请求失败：" + var30.getMessage());
      }
   }

   @PostMapping({"/refund/execute"})
   @Operation(
      summary = "执行独立服务器退款（资源池）"
   )
   public CommonResult<String> refundExecute(@RequestBody JSONObject requestParams) {
      try {
         BaseIndependent s = this.requireOwnedIndependent(ApiContext.getMemberNo(), requestParams.getStr("upstreamServerId"));
         if (s != null && !StrUtil.isBlank(s.getId())) {
            String refundMode = StrUtil.blankToDefault(requestParams.getStr("refundMode"), "FULL");
            ServerRefundParam p = new ServerRefundParam();
            p.setServerId(s.getId());
            p.setRefundMode(refundMode);
            p.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
            p.setReason(requestParams.getStr("reason"));
            p.setMemberNo(ApiContext.getMemberNo());
            return this.serverRefundService.execute(p);
         } else {
            return CommonResult.error("缺少上游服务器主键 upstreamServerId（与 B 库 UPSTREAM_SERVER_ID 相同，即 A 侧实例主键）");
         }
      } catch (Exception var5) {
         log.error("处理资源池独立服务器退款失败：{}", var5.getMessage(), var5);
         return CommonResult.error("退款失败：" + var5.getMessage());
      }
   }

   @PostMapping({"/renew"})
   @Operation(
      summary = "续费独立服务器（资源池）"
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   public CommonResult renew(@RequestBody JSONObject requestParams) {
      try {
         log.info("收到资源池续费独立服务器请求，参数：{}", requestParams);
         if (requestParams != null && !requestParams.isEmpty()) {
            String downstreamOrderNo = requestParams.getStr("downstreamOrderNo");
            if (ObjectUtil.isEmpty(downstreamOrderNo)) {
               downstreamOrderNo = requestParams.getStr("orderNo");
            }

            String serverPk = StrUtil.trimToNull(requestParams.getStr("upstreamServerId"));
            Integer months = requestParams.getInt("months");
            if (ObjectUtil.isEmpty(downstreamOrderNo)) {
               return CommonResult.error("下游订单号不能为空");
            } else if (StrUtil.isBlank(serverPk)) {
               return CommonResult.error("须传 upstreamServerId（与 B 库 UPSTREAM_SERVER_ID 相同，即本系统独立服务器主键）");
            } else if (months != null && months > 0) {
               Long accountMemberNo = ApiContext.getMemberNo();
               if (accountMemberNo == null) {
                  log.error("无法获取账号信息，B 续费单号：{}", downstreamOrderNo);
                  return CommonResult.error("无法获取账号信息，请检查 API Key 配置");
               } else {
                  Order existingOrder = (Order)this.orderService.getOne((Wrapper<Order>)new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, downstreamOrderNo));
                  if (existingOrder != null) {
                     log.warn("订单已存在（B 续费单号已处理），B 单号：{}", downstreamOrderNo);
                     return CommonResult.error("订单已存在");
                  } else {
                     BaseIndependent server = this.independentApi.getById(serverPk);
                     if (server == null) {
                        return CommonResult.error("服务器不存在");
                     } else if (BusinessStatusEnum.DELETE.getCode().equals(server.getBusinessStatus())) {
                        return CommonResult.error("已删除服务器不支持续费");
                     } else if (server.getRenewPrice() == null) {
                        return CommonResult.error("服务器续费价未配置");
                     } else {
                        BigDecimal payPrice = server.getRenewPrice().multiply(BigDecimal.valueOf((long)months.intValue()));
                        if (payPrice.compareTo(BigDecimal.ZERO) <= 0) {
                           return CommonResult.error("续费金额不合法");
                        } else {
                           BaseBalance memberBalance = this.balanceApi.selectByMemberNo(accountMemberNo);
                           if (memberBalance == null) {
                              log.error("账号余额记录不存在，会员编号：{}，订单号：{}", accountMemberNo, downstreamOrderNo);
                              return CommonResult.error("账号余额记录不存在");
                           } else if (memberBalance.getBalance().compareTo(payPrice) < 0) {
                              log.error(
                                 "账号余额不足，会员编号：{}，订单号：{}，当前余额：{}，需要金额：{}",
                                 new Object[]{accountMemberNo, downstreamOrderNo, memberBalance.getBalance(), payPrice}
                              );
                              return CommonResult.error("账号余额不足，当前余额：" + memberBalance.getBalance() + "，需要金额：" + payPrice);
                           } else {
                              Date payTime = new Date();
                              Order order = new Order();
                              order.setId(SnowflakeIdGenerator.generateId());
                              order.setOrderNo(downstreamOrderNo);
                              order.setBillingMode(requestParams.getInt("billingMode", 1));
                              order.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
                              order.setOrderType(OrderTypeEnum.RENEW.getCode());
                              order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
                              order.setMemberNo(accountMemberNo);
                              order.setMemberName(requestParams.getStr("memberName"));
                              order.setMemberPhone(requestParams.getStr("memberPhone"));
                              order.setTotalPrice(payPrice);
                              order.setPayPrice(payPrice);
                              order.setPayType(PayTypeEnum.BALANCE.getCode());
                              order.setUpstreamServerIds(server.getId());
                              order.setCreateTime(new Date());
                              order.setUpdateTime(new Date());
                              this.orderService.save(order);
                              log.info("创建续费订单成功，订单号：{}", downstreamOrderNo);
                              this.balanceApi.deductAndRecord(accountMemberNo, payPrice, downstreamOrderNo, "资源池-独立服务器续费", requestParams.toString(), payTime);
                              IndependentOrderServerConfig cfg = new IndependentOrderServerConfig();
                              cfg.setOrderNo(downstreamOrderNo);
                              cfg.setRegionId(server.getRegionId());
                              cfg.setLineId(server.getLineId());
                              cfg.setMachineId(server.getMachineId());
                              cfg.setMachineName(server.getMachineName());
                              cfg.setUnit("m");
                              cfg.setDuration(months);
                              cfg.setNumber(1);
                              cfg.setBandwidth(server.getBandwidth());
                              cfg.setDefense(server.getDefense());
                              cfg.setIpSize(server.getIpSize());
                              cfg.setCpuInfo(server.getCpuInfo());
                              cfg.setRamInfo(server.getRamInfo());
                              cfg.setDisksInfo(server.getDisksInfo());
                              cfg.setTotalPrice(payPrice);
                              cfg.setPayPrice(payPrice);
                              cfg.setRenewPrice(server.getRenewPrice());
                              cfg.setType(server.getType());
                              cfg.setAutomation(server.getAutomation());
                              if (server.getMachineId() != null) {
                                 try {
                                    BaseAdminMachine machine = this.adminMachineApi.findMachineById(server.getMachineId());
                                    if (machine != null) {
                                       cfg.setMachineMonthPrice(machine.getMachineMonthPrice());
                                       cfg.setBandwidthFreeSize(machine.getBandwidthFreeSize());
                                       cfg.setBandwidthMonthPrice(machine.getBandwidthMonthPrice());
                                       cfg.setDefenseFreeSize(machine.getDefenseFreeSize());
                                       cfg.setDefenseMonthPrice(machine.getDefenseMonthPrice());
                                       cfg.setIpFreeSize(machine.getIpFreeSize());
                                       cfg.setIpMonthPrice(machine.getIpMonthPrice());
                                    }
                                 } catch (Exception var16) {
                                 }
                              }

                              this.independentOrderServerConfigService.saveNew(cfg);
                              OrderServerAddParam orderServerAddParam = new OrderServerAddParam();
                              orderServerAddParam.setOrderNo(downstreamOrderNo);
                              orderServerAddParam.setServerId(server.getId());
                              orderServerAddParam.setServerName(server.getServerName());
                              this.orderServerService.add(orderServerAddParam);

                              try {
                                 log.info("开始处理支付成功逻辑（B 续费单号={}）", downstreamOrderNo);
                                 this.orderService.paySuccessSync(downstreamOrderNo, payTime, null, PayTypeEnum.BALANCE.getCode());
                                 log.info("支付成功逻辑处理完成，订单号：{}", downstreamOrderNo);
                              } catch (Exception var15) {
                                 log.error("处理支付成功逻辑异常，订单号：{}，错误：{}", new Object[]{downstreamOrderNo, var15.getMessage(), var15});
                                 throw new RuntimeException("处理支付成功逻辑失败：" + var15.getMessage(), var15);
                              }

                              JSONObject response = new JSONObject();
                              response.put("code", 200);
                              response.put("msg", "请求已接收，余额已扣除，订单已创建");
                              response.put("data", null);
                              log.info(
                                 "资源池独立服务器续费处理完成，订单号：{}，serverPk={}，upstreamServerIds={}",
                                 new Object[]{downstreamOrderNo, serverPk, order.getUpstreamServerIds()}
                              );
                              return CommonResult.data(response);
                           }
                        }
                     }
                  }
               }
            } else {
               return CommonResult.error("续费月数不能为空");
            }
         } else {
            return CommonResult.error("参数不能为空");
         }
      } catch (Exception var17) {
         log.error("处理资源池独立服务器续费失败：{}", var17.getMessage(), var17);
         return CommonResult.error("续费失败：" + var17.getMessage());
      }
   }

   @PostMapping({"/upgrade/bandwidth"})
   @Operation(
      summary = "升级独立服务器带宽（资源池）"
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   public CommonResult upgradeBandwidth(@RequestBody JSONObject requestParams) {
      try {
         log.info("收到资源池独立机升级带宽请求，参数：{}", requestParams);
         String originalOrderNo = StrUtil.trimToNull(requestParams.getStr("orderNo"));
         Long accountMemberNo = ApiContext.getMemberNo();
         if (accountMemberNo == null) {
            return CommonResult.error("无法获取账号信息，请检查 API Key 配置");
         } else {
            String serverId = this.resolveIndependentServerIdForPoolUpgrade(accountMemberNo, requestParams);
            if (ObjectUtil.isEmpty(serverId)) {
               return CommonResult.error("须传 upstreamServerId（与 B 库 UPSTREAM_SERVER_ID 相同，即本系统独立服务器主键）");
            } else {
               Integer bandwidth = requestParams.getInt("bandwidth");
               if (bandwidth == null) {
                  return CommonResult.error("带宽不能为空");
               } else {
                  BaseIndependent server = this.independentApi.getById(serverId);
                  if (server == null) {
                     return CommonResult.error("服务器不存在");
                  } else if (BusinessStatusEnum.DELETE.getCode().equals(server.getBusinessStatus())) {
                     return CommonResult.error("已删除服务器不支持升级");
                  } else {
                     IndependentPoolUpgradePricing pricing = this.independentResourcePoolUpgradeCalculator
                        .computeBandwidthUpgrade(server, bandwidth, accountMemberNo);
                     if (pricing.getPayPrice() != null && pricing.getPayPrice().compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal payPrice = pricing.getPayPrice();
                        String newOrderNo = SnowflakeIdGenerator.generateId();
                        Date payTime = new Date();
                        BaseBalance memberBalance = this.balanceApi.selectByMemberNo(accountMemberNo);
                        if (memberBalance == null) {
                           return CommonResult.error("账号余额记录不存在");
                        } else if (memberBalance.getBalance().compareTo(payPrice) < 0) {
                           return CommonResult.error("账号余额不足，当前余额：" + memberBalance.getBalance() + "，需要金额：" + payPrice);
                        } else {
                           this.balanceApi.deductAndRecord(accountMemberNo, payPrice, newOrderNo, "资源池-独立服务器升级带宽", requestParams.toString(), payTime);
                           Order order = this.buildIndependentUpgradeOrder(
                              newOrderNo, originalOrderNo, accountMemberNo, UpgradeItemEnum.UPGRADE_BANDWIDTH.getCode(), payPrice, requestParams, serverId
                           );
                           this.orderService.save(order);
                           IndependentOrderServerConfig cfg = pricing.getOrderConfig();
                           cfg.setOrderNo(newOrderNo);
                           this.independentOrderServerConfigService.saveNew(cfg);
                           OrderServerAddParam orderServerAddParam = new OrderServerAddParam();
                           orderServerAddParam.setOrderNo(newOrderNo);
                           orderServerAddParam.setServerId(serverId);
                           orderServerAddParam.setServerName(server.getServerName());
                           this.orderServerService.add(orderServerAddParam);
                           this.orderService.paySuccess(newOrderNo, payTime, null, PayTypeEnum.BALANCE.getCode());
                           log.info("资源池独立机升级带宽完成，关联单号：{}，新单号：{}，serverId：{}", new Object[]{originalOrderNo, newOrderNo, serverId});
                           return CommonResult.ok("升级带宽成功");
                        }
                     } else {
                        return CommonResult.error("升级金额不能为空");
                     }
                  }
               }
            }
         }
      } catch (CommonException var15) {
         return CommonResult.error(var15.getMessage());
      } catch (Exception var16) {
         log.error("处理资源池独立机升级带宽失败：{}", var16.getMessage(), var16);
         return CommonResult.error("升级带宽失败：" + var16.getMessage());
      }
   }

   @PostMapping({"/upgrade/defense"})
   @Operation(
      summary = "升级独立服务器防御（资源池）"
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   public CommonResult upgradeDefense(@RequestBody JSONObject requestParams) {
      try {
         log.info("收到资源池独立机升级防御请求，参数：{}", requestParams);
         String originalOrderNo = StrUtil.trimToNull(requestParams.getStr("orderNo"));
         Long accountMemberNo = ApiContext.getMemberNo();
         if (accountMemberNo == null) {
            return CommonResult.error("无法获取账号信息，请检查 API Key 配置");
         } else {
            String serverId = this.resolveIndependentServerIdForPoolUpgrade(accountMemberNo, requestParams);
            if (ObjectUtil.isEmpty(serverId)) {
               return CommonResult.error("须传 upstreamServerId（与 B 库 UPSTREAM_SERVER_ID 相同，即本系统独立服务器主键）");
            } else {
               Integer defense = requestParams.getInt("defense");
               if (defense == null) {
                  return CommonResult.error("防御不能为空");
               } else {
                  BaseIndependent server = this.independentApi.getById(serverId);
                  if (server == null) {
                     return CommonResult.error("服务器不存在");
                  } else if (BusinessStatusEnum.DELETE.getCode().equals(server.getBusinessStatus())) {
                     return CommonResult.error("已删除服务器不支持升级");
                  } else {
                     IndependentPoolUpgradePricing pricing = this.independentResourcePoolUpgradeCalculator
                        .computeDefenseUpgrade(server, defense, accountMemberNo);
                     if (pricing.getPayPrice() != null && pricing.getPayPrice().compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal payPrice = pricing.getPayPrice();
                        String newOrderNo = SnowflakeIdGenerator.generateId();
                        Date payTime = new Date();
                        BaseBalance memberBalance = this.balanceApi.selectByMemberNo(accountMemberNo);
                        if (memberBalance == null) {
                           return CommonResult.error("账号余额记录不存在");
                        } else if (memberBalance.getBalance().compareTo(payPrice) < 0) {
                           return CommonResult.error("账号余额不足，当前余额：" + memberBalance.getBalance() + "，需要金额：" + payPrice);
                        } else {
                           this.balanceApi.deductAndRecord(accountMemberNo, payPrice, newOrderNo, "资源池-独立服务器升级防御", requestParams.toString(), payTime);
                           Order order = this.buildIndependentUpgradeOrder(
                              newOrderNo, originalOrderNo, accountMemberNo, UpgradeItemEnum.UPGRADE_DEFENSE.getCode(), payPrice, requestParams, serverId
                           );
                           this.orderService.save(order);
                           IndependentOrderServerConfig cfg = pricing.getOrderConfig();
                           cfg.setOrderNo(newOrderNo);
                           this.independentOrderServerConfigService.saveNew(cfg);
                           OrderServerAddParam orderServerAddParam = new OrderServerAddParam();
                           orderServerAddParam.setOrderNo(newOrderNo);
                           orderServerAddParam.setServerId(serverId);
                           orderServerAddParam.setServerName(server.getServerName());
                           this.orderServerService.add(orderServerAddParam);
                           this.orderService.paySuccess(newOrderNo, payTime, null, PayTypeEnum.BALANCE.getCode());
                           log.info("资源池独立机升级防御完成，关联单号：{}，新单号：{}，serverId：{}", new Object[]{originalOrderNo, newOrderNo, serverId});
                           return CommonResult.ok("升级防御成功");
                        }
                     } else {
                        return CommonResult.error("升级金额不能为空");
                     }
                  }
               }
            }
         }
      } catch (CommonException var15) {
         return CommonResult.error(var15.getMessage());
      } catch (Exception var16) {
         log.error("处理资源池独立机升级防御失败：{}", var16.getMessage(), var16);
         return CommonResult.error("升级防御失败：" + var16.getMessage());
      }
   }

   @PostMapping({"/upgrade/ip"})
   @Operation(
      summary = "升级独立服务器 IP（资源池）"
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   public CommonResult upgradeIp(@RequestBody JSONObject requestParams) {
      try {
         log.info("收到资源池独立机升级 IP 请求，参数：{}", requestParams);
         String originalOrderNo = StrUtil.trimToNull(requestParams.getStr("orderNo"));
         Long accountMemberNo = ApiContext.getMemberNo();
         if (accountMemberNo == null) {
            return CommonResult.error("无法获取账号信息，请检查 API Key 配置");
         } else {
            String serverId = this.resolveIndependentServerIdForPoolUpgrade(accountMemberNo, requestParams);
            if (ObjectUtil.isEmpty(serverId)) {
               return CommonResult.error("须传 upstreamServerId（与 B 库 UPSTREAM_SERVER_ID 相同，即本系统独立服务器主键）");
            } else {
               Integer ipSize = requestParams.getInt("ipSize");
               if (ipSize == null) {
                  return CommonResult.error("IP 数量不能为空");
               } else {
                  BaseIndependent server = this.independentApi.getById(serverId);
                  if (server == null) {
                     return CommonResult.error("服务器不存在");
                  } else if (BusinessStatusEnum.DELETE.getCode().equals(server.getBusinessStatus())) {
                     return CommonResult.error("已删除服务器不支持升级");
                  } else {
                     IndependentPoolUpgradePricing pricing = this.independentResourcePoolUpgradeCalculator.computeIpUpgrade(server, ipSize, accountMemberNo);
                     if (pricing.getPayPrice() != null && pricing.getPayPrice().compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal payPrice = pricing.getPayPrice();
                        String newOrderNo = SnowflakeIdGenerator.generateId();
                        Date payTime = new Date();
                        BaseBalance memberBalance = this.balanceApi.selectByMemberNo(accountMemberNo);
                        if (memberBalance == null) {
                           return CommonResult.error("账号余额记录不存在");
                        } else if (memberBalance.getBalance().compareTo(payPrice) < 0) {
                           return CommonResult.error("账号余额不足，当前余额：" + memberBalance.getBalance() + "，需要金额：" + payPrice);
                        } else {
                           this.balanceApi.deductAndRecord(accountMemberNo, payPrice, newOrderNo, "资源池-独立服务器升级IP", requestParams.toString(), payTime);
                           Order order = this.buildIndependentUpgradeOrder(
                              newOrderNo, originalOrderNo, accountMemberNo, UpgradeItemEnum.UPGRADE_IP.getCode(), payPrice, requestParams, serverId
                           );
                           this.orderService.save(order);
                           IndependentOrderServerConfig cfg = pricing.getOrderConfig();
                           cfg.setOrderNo(newOrderNo);
                           this.independentOrderServerConfigService.saveNew(cfg);
                           OrderServerAddParam orderServerAddParam = new OrderServerAddParam();
                           orderServerAddParam.setOrderNo(newOrderNo);
                           orderServerAddParam.setServerId(serverId);
                           orderServerAddParam.setServerName(server.getServerName());
                           this.orderServerService.add(orderServerAddParam);
                           this.orderService.paySuccess(newOrderNo, payTime, null, PayTypeEnum.BALANCE.getCode());
                           log.info("资源池独立机升级 IP 完成，关联单号：{}，新单号：{}，serverId：{}", new Object[]{originalOrderNo, newOrderNo, serverId});
                           return CommonResult.ok("升级IP成功");
                        }
                     } else {
                        return CommonResult.error("升级金额不能为空");
                     }
                  }
               }
            }
         }
      } catch (CommonException var15) {
         return CommonResult.error(var15.getMessage());
      } catch (Exception var16) {
         log.error("处理资源池独立机升级 IP 失败：{}", var16.getMessage(), var16);
         return CommonResult.error("升级IP失败：" + var16.getMessage());
      }
   }

   private String resolveIndependentServerIdForPoolUpgrade(Long memberNo, JSONObject requestParams) {
      String raw = StrUtil.trimToNull(requestParams.getStr("upstreamServerId"));
      if (ObjectUtil.isEmpty(raw)) {
         return null;
      } else {
         BaseIndependent s = this.independentApi.getById(raw);
         if (s != null && !ObjectUtil.isEmpty(s.getId())) {
            if (memberNo != null && !Objects.equals(s.getMemberNo(), memberNo)) {
               throw new CommonException("无权操作该服务器", new Object[0]);
            } else {
               return s.getId();
            }
         } else {
            return null;
         }
      }
   }

   private Order buildIndependentUpgradeOrder(
      String newOrderNo, String originalOrderNo, Long memberNo, Integer upgradeItem, BigDecimal payPrice, JSONObject requestParams, String poolServerIdForOrder
   ) {
      Order order = new Order();
      order.setId(SnowflakeIdGenerator.generateId());
      order.setOrderNo(newOrderNo);
      order.setBillingMode(requestParams.getInt("billingMode", 1));
      order.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
      order.setOrderType(OrderTypeEnum.UPGRADE.getCode());
      order.setUpgradeItem(upgradeItem);
      order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
      order.setMemberNo(memberNo);
      order.setMemberName(requestParams.getStr("memberName"));
      order.setMemberPhone(requestParams.getStr("memberPhone"));
      order.setTotalPrice(payPrice);
      order.setPayPrice(payPrice);
      order.setPayType(PayTypeEnum.BALANCE.getCode());
      order.setOriginalOrderNo(originalOrderNo);
      order.setUpstreamServerIds(poolServerIdForOrder);
      order.setCreateTime(new Date());
      order.setUpdateTime(new Date());
      return order;
   }

   private JSONObject buildResourcePoolCreateResponsePayload(String orderNo) {
      List<OrderServer> rows = this.orderServerService.queryByList(orderNo);
      List<String> ids = rows.stream().map(OrderServer::getServerId).filter(CharSequenceUtil::isNotBlank).toList();
      JSONObject data = new JSONObject();
      if (ids.isEmpty()) {
         log.warn("资源池独立机新购完成但未查询到 ORDER_SERVER.server_id，orderNo={}", orderNo);
         return data;
      } else if (ids.size() == 1) {
         data.set("upstreamServerId", ids.get(0));
         return data;
      } else {
         JSONArray arr = new JSONArray();
         arr.addAll(ids);
         data.set("serverIds", arr);
         return data;
      }
   }

   private void logCreateChangeLogsForIndependentServers(String orderNo, Long memberNo) {
      List<OrderServer> orderServers = this.orderServerService.queryByList(orderNo);
      if (ObjectUtil.isEmpty(orderServers)) {
         log.warn("创建订单后未找到 ORDER_SERVER 记录，无法写入 CREATE 变更日志，订单号：{}", orderNo);
      } else {
         for (OrderServer os : orderServers) {
            if (os != null && !ObjectUtil.isEmpty(os.getServerId())) {
               BaseIndependent indep = this.independentApi.getById(os.getServerId());
               if (indep == null) {
                  log.warn("创建订单后未找到独立服务器记录，跳过写入 CREATE 变更日志，订单号：{}，serverId：{}", orderNo, os.getServerId());
               } else {
                  BaseResourceChangeLog changeLog = new BaseResourceChangeLog();
                  changeLog.setId(SnowflakeIdGenerator.generateId());
                  changeLog.setOrderNo(orderNo);
                  changeLog.setMemberNo(memberNo);
                  changeLog.setChangeType("CREATE");
                  changeLog.setProcessed(0);
                  changeLog.setUpstreamServerId(StrUtil.blankToDefault(indep.getUpstreamServerId(), indep.getId()));
                  changeLog.setPoolSyncTarget("INDEPENDENT");
                  changeLog.setCreateTime(new Date());
                  this.resourceChangeLogApi.save(changeLog);
               }
            }
         }

         log.info("✅ 创建订单后已补写 CREATE 变更日志（独立服务器），订单号：{}，数量：{}", orderNo, orderServers.size());
      }
   }

   @PostMapping({"/delete"})
   @Operation(
      summary = "删除独立服务器（资源池）"
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   public CommonResult deleteServer(@RequestBody JSONObject requestParams) {
      try {
         log.info("收到资源池删除独立服务器请求，参数：{}", requestParams);
         if (requestParams != null && !requestParams.isEmpty()) {
            BaseIndependent s = this.requireOwnedIndependent(ApiContext.getMemberNo(), requestParams.getStr("upstreamServerId"));
            this.independentApi.deleteServer(s.getId());
            log.info("资源池删除独立服务器处理完成，服务器ID：{}", s.getId());
            return CommonResult.ok("删除独立服务器请求已接收");
         } else {
            return CommonResult.error("参数不能为空");
         }
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("处理资源池删除独立服务器请求失败：{}", var4.getMessage(), var4);
         return CommonResult.error("删除独立服务器失败：" + var4.getMessage());
      }
   }

   @PostMapping({"/server/detail"})
   @Operation(
      summary = "独立服务器详情（资源池/下游）"
   )
   public CommonResult<?> serverDetail(@RequestBody JSONObject body) {
      try {
         BaseIndependent s = this.requireOwnedIndependent(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return CommonResult.data(this.independentApi.getById(s.getId()));
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("查询独立服务器详情失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/start"})
   @Operation(
      summary = "独立服务器开机（资源池/下游）"
   )
   public CommonResult serverStart(@RequestBody JSONObject body) {
      try {
         BaseIndependent s = this.requireOwnedIndependent(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.independentApi.poolApiStartIndependent(ApiContext.getMemberNo(), s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("独立服务器开机失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/stop"})
   @Operation(
      summary = "独立服务器关机（资源池/下游）"
   )
   public CommonResult serverStop(@RequestBody JSONObject body) {
      try {
         BaseIndependent s = this.requireOwnedIndependent(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.independentApi.poolApiStopIndependent(ApiContext.getMemberNo(), s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("独立服务器关机失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/restart"})
   @Operation(
      summary = "独立服务器重启（资源池/下游）"
   )
   public CommonResult serverRestart(@RequestBody JSONObject body) {
      try {
         BaseIndependent s = this.requireOwnedIndependent(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.independentApi.poolApiRestartIndependent(ApiContext.getMemberNo(), s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("独立服务器重启失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/power"})
   @Operation(
      summary = "独立服务器断开电源（资源池/下游）"
   )
   public CommonResult serverPower(@RequestBody JSONObject body) {
      try {
         BaseIndependent s = this.requireOwnedIndependent(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.independentApi.poolApiPowerIndependent(ApiContext.getMemberNo(), s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("独立服务器断开电源失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/reinstall"})
   @Operation(
      summary = "独立服务器重装系统（资源池/下游）"
   )
   public CommonResult serverReinstall(@RequestBody JSONObject body) {
      try {
         BaseIndependent s = this.requireOwnedIndependent(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         String osClassId = body.getStr("osClassId");
         if (StrUtil.isBlank(osClassId)) {
            osClassId = body.getStr("systemClassId");
         }

         String osVersionId = body.getStr("osVersionId");
         if (StrUtil.isBlank(osVersionId)) {
            osVersionId = body.getStr("systemId");
         }

         String osPassword = body.getStr("osPassword");
         if (StrUtil.isBlank(osPassword)) {
            osPassword = body.getStr("systemPassword");
         }

         String pwdMsg = OsInstancePasswordValidator.validate(osPassword);
         return pwdMsg != null
            ? CommonResult.error(pwdMsg)
            : this.independentApi.poolApiReinstallIndependent(ApiContext.getMemberNo(), s.getId(), osClassId, osVersionId, osPassword);
      } catch (CommonException var7) {
         return CommonResult.error(var7.getMessage());
      } catch (Exception var8) {
         log.error("独立服务器重装失败：{}", var8.getMessage(), var8);
         return CommonResult.error(var8.getMessage());
      }
   }

   @PostMapping({"/server/updatePassword"})
   @Operation(
      summary = "独立服务器修改密码（资源池/下游）"
   )
   public CommonResult serverUpdatePassword(@RequestBody JSONObject body) {
      try {
         BaseIndependent s = this.requireOwnedIndependent(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         String osPassword = body.getStr("osPassword");
         if (StrUtil.isBlank(osPassword)) {
            osPassword = body.getStr("systemPassword");
         }

         if (StrUtil.isBlank(osPassword)) {
            return CommonResult.error("系统密码不能为空");
         } else {
            String pwdMsg = OsInstancePasswordValidator.validate(osPassword);
            return pwdMsg != null
               ? CommonResult.error(pwdMsg)
               : this.independentApi.poolApiUpdatePasswordIndependent(ApiContext.getMemberNo(), s.getId(), osPassword);
         }
      } catch (CommonException var5) {
         return CommonResult.error(var5.getMessage());
      } catch (Exception var6) {
         log.error("独立服务器修改密码失败：{}", var6.getMessage(), var6);
         return CommonResult.error(var6.getMessage());
      }
   }

   @PostMapping({"/server/lock"})
   @Operation(
      summary = "锁定独立服务器（资源池/下游）"
   )
   public CommonResult serverLock(@RequestBody JSONObject body) {
      try {
         BaseIndependent s = this.requireOwnedIndependent(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         String lockReason = body.getStr("lockReason");
         return StrUtil.isBlank(lockReason)
            ? CommonResult.error("锁定原因不能为空")
            : this.independentApi.poolApiLockIndependent(ApiContext.getMemberNo(), s.getId(), lockReason);
      } catch (CommonException var4) {
         return CommonResult.error(var4.getMessage());
      } catch (Exception var5) {
         log.error("独立服务器锁定失败：{}", var5.getMessage(), var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   @PostMapping({"/server/unlock"})
   @Operation(
      summary = "解锁独立服务器（资源池/下游）"
   )
   public CommonResult serverUnlock(@RequestBody JSONObject body) {
      try {
         BaseIndependent s = this.requireOwnedIndependent(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.independentApi.poolApiUnlockIndependent(ApiContext.getMemberNo(), s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("独立服务器解锁失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   private BaseIndependent requireOwnedIndependent(Long memberNo, String upstreamServerId) {
      if (ObjectUtil.isEmpty(upstreamServerId)) {
         throw new CommonException("upstreamServerId不能为空", new Object[0]);
      } else if (memberNo == null) {
         throw new CommonException("无法获取账号信息，请检查 API Key 配置", new Object[0]);
      } else {
         BaseIndependent s = this.independentApi.getById(upstreamServerId);
         if (s != null && !ObjectUtil.isEmpty(s.getId())) {
            if (!Objects.equals(s.getMemberNo(), memberNo)) {
               throw new CommonException("无权操作该服务器", new Object[0]);
            } else {
               return s;
            }
         } else {
            throw new CommonException("服务器不存在", new Object[0]);
         }
      }
   }
}
