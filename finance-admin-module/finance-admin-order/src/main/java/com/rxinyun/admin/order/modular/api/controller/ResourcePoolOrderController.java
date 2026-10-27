package com.rxinyun.admin.order.modular.api.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.order.modular.ecs.entity.Order;
import com.rxinyun.admin.order.modular.ecs.entity.OrderServer;
import com.rxinyun.admin.order.modular.ecs.param.BandwidthUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.CalculatePriceParam;
import com.rxinyun.admin.order.modular.ecs.param.CoreUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.DefenseUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.DiskUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.OrderServerAddParam;
import com.rxinyun.admin.order.modular.ecs.param.OrderServerConfigAddParam;
import com.rxinyun.admin.order.modular.ecs.param.RenewOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.ServerRefundParam;
import com.rxinyun.admin.order.modular.ecs.response.CalculatePriceResponse;
import com.rxinyun.admin.order.modular.ecs.service.CalculatePriceService;
import com.rxinyun.admin.order.modular.ecs.service.OrderServerConfigService;
import com.rxinyun.admin.order.modular.ecs.service.OrderServerService;
import com.rxinyun.admin.order.modular.ecs.service.OrderService;
import com.rxinyun.admin.order.modular.ecs.service.ServerRefundService;
import com.rxinyun.business.api.ServerApi;
import com.rxinyun.business.core.pojo.BasePoolPasswordParam;
import com.rxinyun.business.core.pojo.BasePoolPortAddParam;
import com.rxinyun.business.core.pojo.BasePoolPortDeleteParam;
import com.rxinyun.business.core.pojo.BasePoolReinstallParam;
import com.rxinyun.business.core.pojo.BaseServer;
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
import com.rxinyun.dev.api.ResourcePoolRequestHandlerApi;
import com.rxinyun.dev.core.pojo.BaseResourceChangeLog;
import com.rxinyun.member.admin.api.BalanceApi;
import com.rxinyun.member.admin.pojo.BaseBalance;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "资源池订单API"
)
@RestController
@RequestMapping({"/api/order"})
@Validated
public class ResourcePoolOrderController {
   private static final Logger log = LoggerFactory.getLogger(ResourcePoolOrderController.class);
   @Resource(
      name = "resourcePoolRequestHandlerApiProvider"
   )
   private ResourcePoolRequestHandlerApi resourcePoolRequestHandler;
   @Resource
   private BalanceApi balanceApi;
   @Resource
   private CalculatePriceService calculatePriceService;
   @Resource
   private OrderService orderService;
   @Resource
   private OrderServerConfigService orderServerConfigService;
   @Resource
   private OrderServerService orderServerService;
   @Resource
   private ApplicationContext applicationContext;
   @Resource
   private ServerApi serverApi;
   @Resource
   private ResourceChangeLogApi resourceChangeLogApi;
   @Resource
   private ServerRefundService serverRefundService;

   @PostMapping({"/create"})
   @Operation(
      summary = "创建云服务器订单（资源池，仅云机）"
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   public CommonResult createOrder(@RequestBody JSONObject requestParams) {
      try {
         log.info("收到资源池创建服务器请求，参数：{}", requestParams);
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
                     CalculatePriceParam calculatePriceParam = new CalculatePriceParam();
                     calculatePriceParam.setMemberNo(accountMemberNo);
                     calculatePriceParam.setBillingMode(requestParams.getInt("billingMode", 1));
                     calculatePriceParam.setUnit(requestParams.getStr("unit", "m"));
                     calculatePriceParam.setLineId(requestParams.getStr("lineId"));
                     calculatePriceParam.setCpu(requestParams.getInt("cpu"));
                     calculatePriceParam.setRam(requestParams.getInt("ram"));
                     calculatePriceParam.setSystemDisk(requestParams.getInt("systemDisk"));
                     Object dataDisksObj = requestParams.get("dataDisks");
                     List<Integer> dataDisks = new ArrayList<>();
                     if (dataDisksObj instanceof JSONArray dataDisksArray) {
                        for (int i = 0; i < dataDisksArray.size(); i++) {
                           dataDisks.add(dataDisksArray.getInt(i));
                        }
                     } else if (dataDisksObj instanceof List) {
                        dataDisks = (List<Integer>)dataDisksObj;
                     }

                     calculatePriceParam.setDataDisks(dataDisks);
                     calculatePriceParam.setBandwidth(requestParams.getInt("bandwidth"));
                     calculatePriceParam.setDefense(requestParams.getInt("defense"));
                     calculatePriceParam.setIpNum(requestParams.getInt("ipNum"));
                     calculatePriceParam.setDuration(requestParams.getInt("duration"));
                     calculatePriceParam.setNumber(requestParams.getInt("number"));
                     CalculatePriceResponse calculatePriceResponse = null;
                     BigDecimal payPrice = null;

                     try {
                        CommonResult<CalculatePriceResponse> priceResult = this.calculatePriceService.calculatePrice(calculatePriceParam);
                        if (priceResult.getCode() != 200 || priceResult.getData() == null) {
                           log.error("计算价格失败，订单号：{}，错误：{}", orderNo, priceResult.getMsg());
                           return CommonResult.error("计算价格失败：" + priceResult.getMsg());
                        }

                        calculatePriceResponse = (CalculatePriceResponse)priceResult.getData();
                        payPrice = calculatePriceResponse.getPayPrice();
                        if (payPrice == null || payPrice.compareTo(BigDecimal.ZERO) <= 0) {
                           log.error("计算出的价格不合法，订单号：{}，金额：{}", orderNo, payPrice);
                           return CommonResult.error("计算出的价格不合法");
                        }
                     } catch (Exception var19) {
                        log.error("计算价格异常，订单号：{}，错误：{}", new Object[]{orderNo, var19.getMessage(), var19});
                        return CommonResult.error("计算价格失败：" + var19.getMessage());
                     }

                     String pwdMsg = OsInstancePasswordValidator.validate(requestParams.getStr("systemPassword"));
                     if (pwdMsg != null) {
                        return CommonResult.error(pwdMsg);
                     } else {
                        BaseBalance memberBalance = this.balanceApi.selectByMemberNo(accountMemberNo);
                        if (memberBalance == null) {
                           log.error("账号余额记录不存在，会员编号：{}，订单号：{}", accountMemberNo, orderNo);
                           return CommonResult.error("账号余额记录不存在");
                        } else if (memberBalance.getBalance().compareTo(payPrice) < 0) {
                           log.error("账号余额不足，会员编号：{}，订单号：{}，当前余额：{}，需要金额：{}", new Object[]{accountMemberNo, orderNo, memberBalance.getBalance(), payPrice});
                           return CommonResult.error("账号余额不足，当前余额：" + memberBalance.getBalance() + "，需要金额：" + payPrice);
                        } else {
                           Date payTime = new Date();
                           Order order = new Order();
                           order.setId(SnowflakeIdGenerator.generateId());
                           order.setOrderNo(orderNo);
                           order.setBillingMode(calculatePriceParam.getBillingMode());
                           order.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
                           order.setOrderType(OrderTypeEnum.NEW_PURCHASE.getCode());
                           order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
                           order.setMemberNo(accountMemberNo);
                           order.setMemberName(requestParams.getStr("memberName"));
                           order.setMemberPhone(requestParams.getStr("memberPhone"));
                           order.setTotalPrice(calculatePriceResponse.getTotalPrice());
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
                           this.balanceApi.deductAndRecord(accountMemberNo, payPrice, orderNo, "资源池-新购云服务器", requestParams.toString(), payTime);
                           OrderServerConfigAddParam orderServerConfigAddParam = new OrderServerConfigAddParam();
                           orderServerConfigAddParam.setOrderNo(orderNo);
                           orderServerConfigAddParam.setLineId(calculatePriceParam.getLineId());
                           orderServerConfigAddParam.setCpu(calculatePriceResponse.getCpu());
                           orderServerConfigAddParam.setRam(calculatePriceResponse.getRam());
                           orderServerConfigAddParam.setCoreDayPrice(calculatePriceResponse.getCoreDayPrice());
                           orderServerConfigAddParam.setCoreMonthPrice(calculatePriceResponse.getCoreMonthPrice());
                           orderServerConfigAddParam.setSystemDisk(calculatePriceResponse.getSystemDisk());
                           orderServerConfigAddParam.setSystemDiskFreeSize(calculatePriceResponse.getSystemDiskFreeSize());
                           orderServerConfigAddParam.setSystemDiskDayPrice(calculatePriceResponse.getSystemDiskDayPrice());
                           orderServerConfigAddParam.setSystemDiskMonthPrice(calculatePriceResponse.getSystemDiskMonthPrice());
                           orderServerConfigAddParam.setDataDisks(calculatePriceResponse.getDataDisks());
                           orderServerConfigAddParam.setDataDisksFreeSize(calculatePriceResponse.getDataDisksFreeSize());
                           orderServerConfigAddParam.setDataDisksDayPrice(calculatePriceResponse.getDataDisksDayPrice());
                           orderServerConfigAddParam.setDataDisksMonthPrice(calculatePriceResponse.getDataDisksMonthPrice());
                           orderServerConfigAddParam.setBandwidth(calculatePriceResponse.getBandwidth());
                           orderServerConfigAddParam.setBandwidthFreeSize(calculatePriceResponse.getBandwidthFreeSize());
                           orderServerConfigAddParam.setBandwidthDayPrice(calculatePriceResponse.getBandwidthDayPrice());
                           orderServerConfigAddParam.setBandwidthMonthPrice(calculatePriceResponse.getBandwidthMonthPrice());
                           orderServerConfigAddParam.setIpNum(calculatePriceResponse.getIpNum());
                           orderServerConfigAddParam.setIpFreeSize(calculatePriceResponse.getIpFreeSize());
                           orderServerConfigAddParam.setIpMonthPrice(calculatePriceResponse.getIpMonthPrice());
                           orderServerConfigAddParam.setDefense(calculatePriceResponse.getDefense());
                           orderServerConfigAddParam.setDefenseFreeSize(calculatePriceResponse.getDefenseFreeSize());
                           orderServerConfigAddParam.setDefenseDayPrice(calculatePriceResponse.getDefenseDayPrice());
                           orderServerConfigAddParam.setDefenseMonthPrice(calculatePriceResponse.getDefenseMonthPrice());
                           orderServerConfigAddParam.setUnit(calculatePriceResponse.getUnit());
                           orderServerConfigAddParam.setDuration(calculatePriceResponse.getDuration());
                           orderServerConfigAddParam.setUnitPrice(calculatePriceResponse.getUnitPrice());
                           orderServerConfigAddParam.setNumber(calculatePriceResponse.getNumber());
                           orderServerConfigAddParam.setTotalPrice(calculatePriceResponse.getTotalPrice());
                           orderServerConfigAddParam.setPayPrice(payPrice);
                           orderServerConfigAddParam.setRenewPrice(calculatePriceResponse.getRenewPrice());
                           orderServerConfigAddParam.setSystemId(requestParams.getStr("systemId"));
                           orderServerConfigAddParam.setLoginPassword(requestParams.getStr("systemPassword"));
                           this.orderServerConfigService.add(orderServerConfigAddParam);
                           log.info("创建订单配置成功，订单号：{}", orderNo);

                           try {
                              log.info("开始处理支付成功逻辑，订单号：{}", orderNo);
                              this.orderService.paySuccessSync(orderNo, payTime, null, PayTypeEnum.BALANCE.getCode());
                              log.info("支付成功逻辑处理完成，订单号：{}", orderNo);
                              this.logCreateChangeLogsForCreatedServers(orderNo, accountMemberNo);
                           } catch (Exception var18) {
                              log.error("处理支付成功逻辑异常，订单号：{}，错误：{}", new Object[]{orderNo, var18.getMessage(), var18});
                              throw new RuntimeException("处理支付成功逻辑失败：" + var18.getMessage(), var18);
                           }

                           JSONObject data = this.buildResourcePoolCreateResponsePayload(orderNo);
                           return CommonResult.get(200, "请求已接收，余额已扣除，订单已创建", data);
                        }
                     }
                  }
               }
            }
         } else {
            return CommonResult.error("参数不能为空");
         }
      } catch (Exception var20) {
         log.error("处理资源池创建服务器请求失败：{}", var20.getMessage(), var20);
         return CommonResult.error("处理请求失败：" + var20.getMessage());
      }
   }

   @PostMapping({"/renew"})
   @Operation(
      summary = "续费服务器（资源池）"
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   public CommonResult renew(@RequestBody JSONObject requestParams) {
      try {
         log.info("收到资源池续费服务器请求，参数：{}", requestParams);
         if (requestParams != null && !requestParams.isEmpty()) {
            String downstreamOrderNo = requestParams.getStr("downstreamOrderNo");
            if (ObjectUtil.isEmpty(downstreamOrderNo)) {
               downstreamOrderNo = requestParams.getStr("orderNo");
            }

            String serverPk = resolvePoolCloudServerPrimaryKey(requestParams);
            Integer months = requestParams.getInt("months");
            if (ObjectUtil.isEmpty(downstreamOrderNo)) {
               return CommonResult.error("下游订单号不能为空");
            } else if (StrUtil.isBlank(serverPk)) {
               return CommonResult.error("须传 upstreamServerId（与 B 库 UPSTREAM_SERVER_ID 相同，即本系统云服务器主键）");
            } else if (months != null && months > 0) {
               Long accountMemberNo = ApiContext.getMemberNo();
               if (accountMemberNo == null) {
                  return CommonResult.error("无法获取账号信息，请检查 API Key 配置");
               } else {
                  Order existingOrder = (Order)this.orderService.getOne((Wrapper<Order>)new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, downstreamOrderNo));
                  if (existingOrder != null) {
                     log.warn("订单已存在（B 续费单号已处理），B 单号：{}", downstreamOrderNo);
                     return CommonResult.error("订单已存在");
                  } else {
                     BaseServer server = this.serverApi.selectByServerId(serverPk);
                     if (server == null) {
                        throw new CommonException("服务器不存在", new Object[0]);
                     } else if (BusinessStatusEnum.DELETE.getCode().equals(server.getBusinessStatus())) {
                        return CommonResult.error("已删除服务器不支持续费");
                     } else {
                        RenewOrderParam renewOrderParam = new RenewOrderParam();
                        renewOrderParam.setServerId(server.getId());
                        renewOrderParam.setDuration(months);
                        CalculatePriceResponse calculatePriceResponse = null;
                        BigDecimal payPrice = null;

                        try {
                           CommonResult<CalculatePriceResponse> priceResult = this.calculatePriceService.renew(renewOrderParam);
                           if (priceResult.getCode() != 200 || priceResult.getData() == null) {
                              log.error("计算价格失败，订单号：{}，错误：{}", downstreamOrderNo, priceResult.getMsg());
                              return CommonResult.error("计算价格失败：" + priceResult.getMsg());
                           }

                           calculatePriceResponse = (CalculatePriceResponse)priceResult.getData();
                           payPrice = calculatePriceResponse.getPayPrice();
                           if (payPrice == null || payPrice.compareTo(BigDecimal.ZERO) <= 0) {
                              log.error("计算出的价格不合法，订单号：{}，金额：{}", downstreamOrderNo, payPrice);
                              return CommonResult.error("计算出的价格不合法");
                           }
                        } catch (Exception var17) {
                           log.error("计算价格异常，订单号：{}，错误：{}", new Object[]{downstreamOrderNo, var17.getMessage(), var17});
                           return CommonResult.error("计算价格失败：" + var17.getMessage());
                        }

                        Date payTime = new Date();
                        Order order = new Order();
                        order.setId(SnowflakeIdGenerator.generateId());
                        order.setOrderNo(downstreamOrderNo);
                        order.setBillingMode(requestParams.getInt("billingMode", 1));
                        order.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
                        order.setOrderType(OrderTypeEnum.RENEW.getCode());
                        order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
                        order.setMemberNo(accountMemberNo);
                        order.setMemberName(requestParams.getStr("memberName"));
                        order.setMemberPhone(requestParams.getStr("memberPhone"));
                        order.setTotalPrice(calculatePriceResponse.getTotalPrice());
                        order.setPayPrice(calculatePriceResponse.getPayPrice());
                        order.setPayType(PayTypeEnum.BALANCE.getCode());
                        order.setUpstreamServerIds(server.getId());
                        order.setCreateTime(new Date());
                        order.setUpdateTime(new Date());
                        this.orderService.save(order);
                        this.balanceApi.deductAndRecord(accountMemberNo, payPrice, downstreamOrderNo, "资源池-云服务器续费", requestParams.toString(), payTime);
                        OrderServerConfigAddParam configParam = new OrderServerConfigAddParam();
                        configParam.setOrderNo(downstreamOrderNo);
                        configParam.setLineId(server.getLineId());
                        configParam.setCpu(calculatePriceResponse.getCpu());
                        configParam.setRam(calculatePriceResponse.getRam());
                        configParam.setCoreDayPrice(calculatePriceResponse.getCoreDayPrice());
                        configParam.setCoreMonthPrice(calculatePriceResponse.getCoreMonthPrice());
                        configParam.setSystemDisk(calculatePriceResponse.getSystemDisk());
                        configParam.setSystemDiskFreeSize(calculatePriceResponse.getSystemDiskFreeSize());
                        configParam.setSystemDiskDayPrice(calculatePriceResponse.getSystemDiskDayPrice());
                        configParam.setSystemDiskMonthPrice(calculatePriceResponse.getSystemDiskMonthPrice());
                        configParam.setDataDisks(calculatePriceResponse.getDataDisks());
                        configParam.setDataDisksFreeSize(calculatePriceResponse.getDataDisksFreeSize());
                        configParam.setDataDisksDayPrice(calculatePriceResponse.getDataDisksDayPrice());
                        configParam.setDataDisksMonthPrice(calculatePriceResponse.getDataDisksMonthPrice());
                        configParam.setBandwidth(calculatePriceResponse.getBandwidth());
                        configParam.setBandwidthFreeSize(calculatePriceResponse.getBandwidthFreeSize());
                        configParam.setBandwidthDayPrice(calculatePriceResponse.getBandwidthDayPrice());
                        configParam.setBandwidthMonthPrice(calculatePriceResponse.getBandwidthMonthPrice());
                        configParam.setIpNum(calculatePriceResponse.getIpNum());
                        configParam.setIpFreeSize(calculatePriceResponse.getIpFreeSize());
                        configParam.setIpMonthPrice(calculatePriceResponse.getIpMonthPrice());
                        configParam.setDefense(calculatePriceResponse.getDefense());
                        configParam.setDefenseFreeSize(calculatePriceResponse.getDefenseFreeSize());
                        configParam.setDefenseDayPrice(calculatePriceResponse.getDefenseDayPrice());
                        configParam.setDefenseMonthPrice(calculatePriceResponse.getDefenseMonthPrice());
                        configParam.setUnit("m");
                        configParam.setDuration(months);
                        configParam.setUnitPrice(calculatePriceResponse.getUnitPrice());
                        configParam.setNumber(1);
                        configParam.setTotalPrice(calculatePriceResponse.getTotalPrice());
                        configParam.setPayPrice(calculatePriceResponse.getPayPrice());
                        configParam.setRenewPrice(calculatePriceResponse.getRenewPrice());
                        this.orderServerConfigService.add(configParam);
                        OrderServerAddParam orderServerAddParam = new OrderServerAddParam();
                        orderServerAddParam.setOrderNo(downstreamOrderNo);
                        orderServerAddParam.setServerId(server.getId());
                        orderServerAddParam.setServerName(server.getHostName());
                        this.orderServerService.add(orderServerAddParam);

                        try {
                           log.info("开始处理支付成功逻辑（B 续费单号={}）", downstreamOrderNo);
                           this.orderService.paySuccessSync(downstreamOrderNo, payTime, null, PayTypeEnum.BALANCE.getCode());
                           log.info("支付成功逻辑处理完成，订单号：{}", downstreamOrderNo);
                        } catch (Exception var16) {
                           log.error("处理支付成功逻辑异常，订单号：{}，错误：{}", new Object[]{downstreamOrderNo, var16.getMessage(), var16});
                           throw new RuntimeException("处理支付成功逻辑失败：" + var16.getMessage(), var16);
                        }

                        JSONObject response = new JSONObject();
                        response.put("code", 200);
                        response.put("msg", "请求已接收，余额已扣除，订单已创建");
                        response.put("data", null);
                        log.info("资源池续费处理完成，订单号：{}，serverPk={}，upstreamServerIds={}", new Object[]{downstreamOrderNo, serverPk, order.getUpstreamServerIds()});
                        return CommonResult.data(response);
                     }
                  }
               }
            } else {
               return CommonResult.error("续费月数不能为空");
            }
         } else {
            return CommonResult.error("参数不能为空");
         }
      } catch (Exception var18) {
         log.error("处理资源池续费请求失败：{}", var18.getMessage(), var18);
         return CommonResult.error("续费失败：" + var18.getMessage());
      }
   }

   @PostMapping({"/upgrade/disk"})
   @Operation(
      summary = "升级磁盘（资源池）"
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   public CommonResult upgradeDisk(@RequestBody JSONObject requestParams) {
      try {
         log.info("收到资源池升级磁盘请求，参数：{}", requestParams);
         String downstreamOrderNo = requestParams.getStr("downstreamOrderNo");
         String serverPk = resolvePoolCloudServerPrimaryKey(requestParams);
         if (ObjectUtil.isEmpty(downstreamOrderNo)) {
            return CommonResult.error("下游订单号不能为空");
         } else if (StrUtil.isBlank(serverPk)) {
            return CommonResult.error("须传 upstreamServerId（与 B 库 UPSTREAM_SERVER_ID 相同，即本系统云服务器主键）");
         } else {
            Long accountMemberNo = ApiContext.getMemberNo();
            if (accountMemberNo == null) {
               return CommonResult.error("无法获取账号信息，请检查 API Key 配置");
            } else {
               BaseServer server = this.serverApi.selectByServerId(serverPk);
               if (server == null) {
                  throw new CommonException("服务器不存在", new Object[0]);
               } else if (BusinessStatusEnum.DELETE.getCode().equals(server.getBusinessStatus())) {
                  return CommonResult.error("已删除服务器不支持升级");
               } else {
                  DiskUpgradeOrderParam upgradeOrderParam = new DiskUpgradeOrderParam();
                  upgradeOrderParam.setServerId(server.getId());
                  upgradeOrderParam.setDataDisks(this.parseIntList(requestParams.get("dataDisks")));
                  Integer systemDisk = requestParams.getInt("systemDisk");
                  if (systemDisk == null) {
                     systemDisk = server.getSystemDisk();
                  }

                  if (systemDisk == null) {
                     return CommonResult.error("系统盘大小不能为空且无法从服务器记录推断");
                  } else {
                     upgradeOrderParam.setSystemDisk(systemDisk);
                     CommonResult<CalculatePriceResponse> priceResult = this.calculatePriceService.upgradeDisk(upgradeOrderParam);
                     if (priceResult.getCode() == 200 && priceResult.getData() != null) {
                        CalculatePriceResponse calculatePriceResponse = (CalculatePriceResponse)priceResult.getData();
                        BigDecimal payPrice = calculatePriceResponse.getPayPrice();
                        if (payPrice != null && payPrice.compareTo(BigDecimal.ZERO) > 0) {
                           BigDecimal renewPrice = ((CalculatePriceResponse)priceResult.getData()).getRenewPrice();
                           Date payTime = new Date();
                           BaseBalance memberBalance = this.balanceApi.selectByMemberNo(accountMemberNo);
                           if (memberBalance == null) {
                              return CommonResult.error("账号余额记录不存在");
                           } else if (memberBalance.getBalance().compareTo(payPrice) < 0) {
                              return CommonResult.error("账号余额不足，当前余额：" + memberBalance.getBalance() + "，需要金额：" + payPrice);
                           } else {
                              this.balanceApi.deductAndRecord(accountMemberNo, payPrice, downstreamOrderNo, "资源池-云服务器升级磁盘", requestParams.toString(), payTime);
                              Order order = new Order();
                              order.setId(SnowflakeIdGenerator.generateId());
                              order.setOrderNo(downstreamOrderNo);
                              order.setBillingMode(requestParams.getInt("billingMode", 1));
                              order.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
                              order.setOrderType(OrderTypeEnum.UPGRADE.getCode());
                              order.setUpgradeItem(UpgradeItemEnum.UPGRADE_HARD_DISK.getCode());
                              order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
                              order.setMemberNo(accountMemberNo);
                              order.setMemberName(requestParams.getStr("memberName"));
                              order.setMemberPhone(requestParams.getStr("memberPhone"));
                              order.setTotalPrice(calculatePriceResponse.getTotalPrice());
                              order.setPayPrice(payPrice);
                              order.setPayType(PayTypeEnum.BALANCE.getCode());
                              order.setUpstreamServerIds(server.getId());
                              order.setCreateTime(new Date());
                              order.setUpdateTime(new Date());
                              this.orderService.save(order);
                              OrderServerConfigAddParam configParam = new OrderServerConfigAddParam();
                              configParam.setOrderNo(downstreamOrderNo);
                              configParam.setLineId(server.getLineId());
                              configParam.setCpu(calculatePriceResponse.getCpu());
                              configParam.setRam(calculatePriceResponse.getRam());
                              configParam.setCoreDayPrice(calculatePriceResponse.getCoreDayPrice());
                              configParam.setCoreMonthPrice(calculatePriceResponse.getCoreMonthPrice());
                              configParam.setSystemDisk(calculatePriceResponse.getSystemDisk());
                              configParam.setSystemDiskFreeSize(calculatePriceResponse.getSystemDiskFreeSize());
                              configParam.setSystemDiskDayPrice(calculatePriceResponse.getSystemDiskDayPrice());
                              configParam.setSystemDiskMonthPrice(calculatePriceResponse.getSystemDiskMonthPrice());
                              configParam.setDataDisks(calculatePriceResponse.getDataDisks());
                              configParam.setDataDisksFreeSize(calculatePriceResponse.getDataDisksFreeSize());
                              configParam.setDataDisksDayPrice(calculatePriceResponse.getDataDisksDayPrice());
                              configParam.setDataDisksMonthPrice(calculatePriceResponse.getDataDisksMonthPrice());
                              configParam.setBandwidth(calculatePriceResponse.getBandwidth());
                              configParam.setBandwidthFreeSize(calculatePriceResponse.getBandwidthFreeSize());
                              configParam.setBandwidthDayPrice(calculatePriceResponse.getBandwidthDayPrice());
                              configParam.setBandwidthMonthPrice(calculatePriceResponse.getBandwidthMonthPrice());
                              configParam.setIpNum(calculatePriceResponse.getIpNum());
                              configParam.setIpFreeSize(calculatePriceResponse.getIpFreeSize());
                              configParam.setIpMonthPrice(calculatePriceResponse.getIpMonthPrice());
                              configParam.setDefense(calculatePriceResponse.getDefense());
                              configParam.setDefenseFreeSize(calculatePriceResponse.getDefenseFreeSize());
                              configParam.setDefenseDayPrice(calculatePriceResponse.getDefenseDayPrice());
                              configParam.setDefenseMonthPrice(calculatePriceResponse.getDefenseMonthPrice());
                              configParam.setUnit("m");
                              configParam.setDuration(calculatePriceResponse.getDuration());
                              configParam.setUnitPrice(calculatePriceResponse.getUnitPrice());
                              configParam.setNumber(1);
                              configParam.setTotalPrice(calculatePriceResponse.getTotalPrice());
                              configParam.setPayPrice(payPrice);
                              configParam.setRenewPrice(renewPrice);
                              this.orderServerConfigService.add(configParam);
                              this.addOrderServerRelation(downstreamOrderNo, server.getId(), server.getHostName());
                              this.orderService.paySuccess(downstreamOrderNo, payTime, null, PayTypeEnum.BALANCE.getCode());
                              log.info("资源池升级磁盘处理完成，订单号：{}，服务器 ID：{}", downstreamOrderNo, server.getId());
                              return CommonResult.ok("升级磁盘成功");
                           }
                        } else {
                           return CommonResult.error("升级金额不能为空");
                        }
                     } else {
                        log.error("计算升级价格失败，服务器 ID：{}，错误：{}", server.getId(), priceResult.getMsg());
                        return CommonResult.error("计算升级价格失败：" + priceResult.getMsg());
                     }
                  }
               }
            }
         }
      } catch (Exception var16) {
         log.error("处理资源池升级磁盘请求失败：{}", var16.getMessage(), var16);
         return CommonResult.error("升级磁盘失败：" + var16.getMessage());
      }
   }

   @PostMapping({"/upgrade/bandwidth"})
   @Operation(
      summary = "升级带宽（资源池）"
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   public CommonResult upgradeBandwidth(@RequestBody JSONObject requestParams) {
      try {
         log.info("收到资源池升级带宽请求，参数：{}", requestParams);
         String originalOrderNo = StrUtil.trimToNull(requestParams.getStr("orderNo"));
         Long accountMemberNo = ApiContext.getMemberNo();
         if (accountMemberNo == null) {
            return CommonResult.error("无法获取账号信息，请检查API Key配置");
         } else {
            String serverId = this.resolveServerIdForPoolUpgrade(accountMemberNo, requestParams);
            if (ObjectUtil.isEmpty(serverId)) {
               return CommonResult.error("须传 upstreamServerId（与 B 库 UPSTREAM_SERVER_ID 相同，即本系统云服务器主键）");
            } else if (requestParams.getInt("bandwidth") == null) {
               return CommonResult.error("带宽不能为空");
            } else {
               BandwidthUpgradeOrderParam upgradeParam = new BandwidthUpgradeOrderParam();
               upgradeParam.setServerId(serverId);
               upgradeParam.setBandwidth(requestParams.getInt("bandwidth"));
               CommonResult<CalculatePriceResponse> priceResult = this.calculatePriceService.upgradeBandwidth(upgradeParam);
               if (priceResult.getCode() == 200
                  && priceResult.getData() != null
                  && ((CalculatePriceResponse)priceResult.getData()).getPayPrice() != null
                  && ((CalculatePriceResponse)priceResult.getData()).getPayPrice().compareTo(BigDecimal.ZERO) > 0) {
                  BigDecimal payPrice = ((CalculatePriceResponse)priceResult.getData()).getPayPrice();
                  BaseServer server = this.serverApi.getById(serverId);
                  if (server == null) {
                     return CommonResult.error("服务器不存在");
                  } else {
                     BigDecimal renewPrice = ((CalculatePriceResponse)priceResult.getData()).getRenewPrice();
                     String newOrderNo = SnowflakeIdGenerator.generateId();
                     Date payTime = new Date();
                     BaseBalance memberBalance = this.balanceApi.selectByMemberNo(accountMemberNo);
                     if (memberBalance == null) {
                        return CommonResult.error("账号余额记录不存在");
                     } else if (memberBalance.getBalance().compareTo(payPrice) < 0) {
                        return CommonResult.error("账号余额不足，当前余额：" + memberBalance.getBalance() + "，需要金额：" + payPrice);
                     } else {
                        this.balanceApi.deductAndRecord(accountMemberNo, payPrice, newOrderNo, "资源池-云服务器升级带宽", requestParams.toString(), payTime);
                        Order order = this.buildUpgradeOrder(
                           newOrderNo, originalOrderNo, accountMemberNo, UpgradeItemEnum.UPGRADE_BANDWIDTH.getCode(), payPrice, requestParams, serverId
                        );
                        this.orderService.save(order);
                        this.saveUpgradeOrderServerConfig(newOrderNo, (CalculatePriceResponse)priceResult.getData(), payPrice, renewPrice, serverId);
                        this.addOrderServerRelation(newOrderNo, serverId, server.getHostName());
                        this.orderService.paySuccess(newOrderNo, payTime, null, PayTypeEnum.BALANCE.getCode());
                        log.info("资源池升级带宽处理完成，关联订单号：{}，新订单号：{}，服务器ID：{}", new Object[]{originalOrderNo, newOrderNo, serverId});
                        return CommonResult.ok("升级带宽成功");
                     }
                  }
               } else {
                  return CommonResult.error("升级金额不能为空");
               }
            }
         }
      } catch (Exception var14) {
         log.error("处理资源池升级带宽请求失败：{}", var14.getMessage(), var14);
         return CommonResult.error("升级带宽失败：" + var14.getMessage());
      }
   }

   @PostMapping({"/upgrade/cpu-ram"})
   @Operation(
      summary = "升级CPU/内存（资源池）"
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   public CommonResult upgradeCpuRam(@RequestBody JSONObject requestParams) {
      try {
         log.info("收到资源池升级CPU/内存请求，参数：{}", requestParams);
         String originalOrderNo = StrUtil.trimToNull(requestParams.getStr("orderNo"));
         Long accountMemberNo = ApiContext.getMemberNo();
         if (accountMemberNo == null) {
            return CommonResult.error("无法获取账号信息，请检查API Key配置");
         } else {
            String serverId = this.resolveServerIdForPoolUpgrade(accountMemberNo, requestParams);
            if (ObjectUtil.isEmpty(serverId)) {
               return CommonResult.error("须传 upstreamServerId（与 B 库 UPSTREAM_SERVER_ID 相同，即本系统云服务器主键）");
            } else if (requestParams.getInt("cpu") != null && requestParams.getInt("ram") != null) {
               CoreUpgradeOrderParam upgradeParam = new CoreUpgradeOrderParam();
               upgradeParam.setServerId(serverId);
               upgradeParam.setCpu(requestParams.getInt("cpu"));
               upgradeParam.setRam(requestParams.getInt("ram"));
               CommonResult<CalculatePriceResponse> priceResult = this.calculatePriceService.upgradeCore(upgradeParam);
               if (priceResult.getCode() == 200
                  && priceResult.getData() != null
                  && ((CalculatePriceResponse)priceResult.getData()).getPayPrice() != null
                  && ((CalculatePriceResponse)priceResult.getData()).getPayPrice().compareTo(BigDecimal.ZERO) > 0) {
                  BigDecimal payPrice = ((CalculatePriceResponse)priceResult.getData()).getPayPrice();
                  BaseServer server = this.serverApi.getById(serverId);
                  if (server == null) {
                     return CommonResult.error("服务器不存在");
                  } else {
                     BigDecimal renewPrice = ((CalculatePriceResponse)priceResult.getData()).getRenewPrice();
                     String newOrderNo = SnowflakeIdGenerator.generateId();
                     Date payTime = new Date();
                     BaseBalance memberBalance = this.balanceApi.selectByMemberNo(accountMemberNo);
                     if (memberBalance == null) {
                        return CommonResult.error("账号余额记录不存在");
                     } else if (memberBalance.getBalance().compareTo(payPrice) < 0) {
                        return CommonResult.error("账号余额不足，当前余额：" + memberBalance.getBalance() + "，需要金额：" + payPrice);
                     } else {
                        this.balanceApi.deductAndRecord(accountMemberNo, payPrice, newOrderNo, "资源池-云服务器升级CPU内存", requestParams.toString(), payTime);
                        Order order = this.buildUpgradeOrder(
                           newOrderNo, originalOrderNo, accountMemberNo, UpgradeItemEnum.UPGRADE_CORE.getCode(), payPrice, requestParams, serverId
                        );
                        this.orderService.save(order);
                        this.saveUpgradeOrderServerConfig(newOrderNo, (CalculatePriceResponse)priceResult.getData(), payPrice, renewPrice, serverId);
                        this.addOrderServerRelation(newOrderNo, serverId, server.getHostName());
                        this.orderService.paySuccess(newOrderNo, payTime, null, PayTypeEnum.BALANCE.getCode());
                        log.info("资源池升级CPU/内存处理完成，关联订单号：{}，新订单号：{}，服务器ID：{}", new Object[]{originalOrderNo, newOrderNo, serverId});
                        return CommonResult.ok("升级CPU/内存成功");
                     }
                  }
               } else {
                  return CommonResult.error("升级金额不能为空");
               }
            } else {
               return CommonResult.error("CPU/内存不能为空");
            }
         }
      } catch (Exception var14) {
         log.error("处理资源池升级CPU/内存请求失败：{}", var14.getMessage(), var14);
         return CommonResult.error("升级CPU/内存失败：" + var14.getMessage());
      }
   }

   @PostMapping({"/upgrade/defense"})
   @Operation(
      summary = "升级防御（资源池）"
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   public CommonResult upgradeDefense(@RequestBody JSONObject requestParams) {
      try {
         log.info("收到资源池升级防御请求，参数：{}", requestParams);
         String originalOrderNo = StrUtil.trimToNull(requestParams.getStr("orderNo"));
         Long accountMemberNo = ApiContext.getMemberNo();
         if (accountMemberNo == null) {
            return CommonResult.error("无法获取账号信息，请检查API Key配置");
         } else {
            String serverId = this.resolveServerIdForPoolUpgrade(accountMemberNo, requestParams);
            if (ObjectUtil.isEmpty(serverId)) {
               return CommonResult.error("须传 upstreamServerId（与 B 库 UPSTREAM_SERVER_ID 相同，即本系统云服务器主键）");
            } else if (requestParams.getInt("defense") == null) {
               return CommonResult.error("防御不能为空");
            } else {
               DefenseUpgradeOrderParam upgradeParam = new DefenseUpgradeOrderParam();
               upgradeParam.setServerId(serverId);
               upgradeParam.setDefense(requestParams.getInt("defense"));
               CommonResult<CalculatePriceResponse> priceResult = this.calculatePriceService.upgradeDefense(upgradeParam);
               if (priceResult.getCode() == 200
                  && priceResult.getData() != null
                  && ((CalculatePriceResponse)priceResult.getData()).getPayPrice() != null
                  && ((CalculatePriceResponse)priceResult.getData()).getPayPrice().compareTo(BigDecimal.ZERO) > 0) {
                  BigDecimal payPrice = ((CalculatePriceResponse)priceResult.getData()).getPayPrice();
                  BaseServer server = this.serverApi.getById(serverId);
                  if (server == null) {
                     return CommonResult.error("服务器不存在");
                  } else {
                     BigDecimal renewPrice = ((CalculatePriceResponse)priceResult.getData()).getRenewPrice();
                     String newOrderNo = SnowflakeIdGenerator.generateId();
                     Date payTime = new Date();
                     BaseBalance memberBalance = this.balanceApi.selectByMemberNo(accountMemberNo);
                     if (memberBalance == null) {
                        return CommonResult.error("账号余额记录不存在");
                     } else if (memberBalance.getBalance().compareTo(payPrice) < 0) {
                        return CommonResult.error("账号余额不足，当前余额：" + memberBalance.getBalance() + "，需要金额：" + payPrice);
                     } else {
                        this.balanceApi.deductAndRecord(accountMemberNo, payPrice, newOrderNo, "资源池-云服务器升级防御", requestParams.toString(), payTime);
                        Order order = this.buildUpgradeOrder(
                           newOrderNo, originalOrderNo, accountMemberNo, UpgradeItemEnum.UPGRADE_DEFENSE.getCode(), payPrice, requestParams, serverId
                        );
                        this.orderService.save(order);
                        this.saveUpgradeOrderServerConfig(newOrderNo, (CalculatePriceResponse)priceResult.getData(), payPrice, renewPrice, serverId);
                        this.addOrderServerRelation(newOrderNo, serverId, server.getHostName());
                        this.orderService.paySuccess(newOrderNo, payTime, null, PayTypeEnum.BALANCE.getCode());
                        log.info("资源池升级防御处理完成，关联订单号：{}，新订单号：{}，服务器ID：{}", new Object[]{originalOrderNo, newOrderNo, serverId});
                        return CommonResult.ok("升级防御成功");
                     }
                  }
               } else {
                  return CommonResult.error("升级金额不能为空");
               }
            }
         }
      } catch (Exception var14) {
         log.error("处理资源池升级防御请求失败：{}", var14.getMessage(), var14);
         return CommonResult.error("升级防御失败：" + var14.getMessage());
      }
   }

   @PostMapping({"/delete"})
   @Operation(
      summary = "删除服务器（资源池）"
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   public CommonResult deleteServer(@RequestBody JSONObject requestParams) {
      try {
         log.info("收到资源池删除服务器请求，参数：{}", requestParams);
         String serverId = resolvePoolCloudServerPrimaryKey(requestParams);
         if (ObjectUtil.isEmpty(serverId)) {
            return CommonResult.error("须传 upstreamServerId（与 B 库 UPSTREAM_SERVER_ID 相同，即本系统云服务器主键）");
         } else {
            Long accountMemberNo = ApiContext.getMemberNo();
            if (accountMemberNo == null) {
               return CommonResult.error("无法获取账号信息，请检查API Key配置");
            } else {
               this.serverApi.deleteServer(serverId);
               log.info("资源池删除服务器处理完成，服务器ID：{}", serverId);
               return CommonResult.ok("删除服务器请求已接收");
            }
         }
      } catch (Exception var4) {
         log.error("处理资源池删除服务器请求失败：{}", var4.getMessage(), var4);
         return CommonResult.error("删除服务器失败：" + var4.getMessage());
      }
   }

   @PostMapping({"/refund/execute"})
   @Operation(
      summary = "执行云服务器退款（资源池）"
   )
   public CommonResult<String> refundExecute(@RequestBody JSONObject requestParams) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), requestParams.getStr("upstreamServerId"));
         if (s != null && !StrUtil.isBlank(s.getId())) {
            String refundMode = StrUtil.blankToDefault(requestParams.getStr("refundMode"), "FULL");
            ServerRefundParam p = new ServerRefundParam();
            p.setServerId(s.getId());
            p.setRefundMode(refundMode);
            p.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
            p.setReason(requestParams.getStr("reason"));
            p.setMemberNo(ApiContext.getMemberNo());
            return this.serverRefundService.execute(p);
         } else {
            return CommonResult.error("缺少上游服务器主键 upstreamServerId（与 B 库 UPSTREAM_SERVER_ID 相同，即 A 侧实例主键）");
         }
      } catch (Exception var5) {
         log.error("处理资源池云服务器退款失败：{}", var5.getMessage(), var5);
         return CommonResult.error("退款失败：" + var5.getMessage());
      }
   }

   @PostMapping({"/server/detail"})
   @Operation(
      summary = "云服务器详情（资源池/下游）"
   )
   public CommonResult<?> serverDetail(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return CommonResult.data(this.serverApi.detail(s.getId()));
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("查询云服务器详情失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/start"})
   @Operation(
      summary = "开启云服务器（资源池/下游）"
   )
   public CommonResult serverStart(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.startCloudServer(s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("开启云服务器失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/stop"})
   @Operation(
      summary = "关闭云服务器（资源池/下游）"
   )
   public CommonResult serverStop(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.stopCloudServer(s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("关闭云服务器失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/restart"})
   @Operation(
      summary = "重启云服务器（资源池/下游）"
   )
   public CommonResult serverRestart(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.restartCloudServer(s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("重启云服务器失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/power"})
   @Operation(
      summary = "断开云服务器电源（资源池/下游）"
   )
   public CommonResult serverPower(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.powerCloudServer(s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("断开云服务器电源失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/monitor"})
   @Operation(
      summary = "查询云服务器监控（资源池/下游）"
   )
   public CommonResult<?> serverMonitor(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.monitorCloudServer(s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("查询云服务器监控失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/reinstall"})
   @Operation(
      summary = "重装云服务器系统（资源池/下游）"
   )
   public CommonResult serverReinstall(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         String pwdMsg = OsInstancePasswordValidator.validate(body.getStr("systemPassword"));
         if (pwdMsg != null) {
            return CommonResult.error(pwdMsg);
         } else {
            BasePoolReinstallParam p = new BasePoolReinstallParam();
            p.setId(s.getId());
            p.setHostName(body.getStr("hostName"));
            p.setSystemClassId(body.getStr("systemClassId"));
            p.setSystemId(body.getStr("systemId"));
            p.setSystemName(body.getStr("systemName"));
            p.setSystemPassword(body.getStr("systemPassword"));
            p.setRemotePort(body.getInt("remotePort"));
            p.setFormatDataDisk(body.getInt("formatDataDisk"));
            return this.serverApi.reinstallCloudServer(p);
         }
      } catch (CommonException var5) {
         return CommonResult.error(var5.getMessage());
      } catch (Exception var6) {
         log.error("重装云服务器失败：{}", var6.getMessage(), var6);
         return CommonResult.error(var6.getMessage());
      }
   }

   @PostMapping({"/server/lock"})
   @Operation(
      summary = "锁定云服务器（资源池/下游）"
   )
   public CommonResult serverLock(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         String lockReason = body.getStr("lockReason");
         return StrUtil.isBlank(lockReason) ? CommonResult.error("锁定原因不能为空") : this.serverApi.lockCloudServer(s.getId(), lockReason);
      } catch (CommonException var4) {
         return CommonResult.error(var4.getMessage());
      } catch (Exception var5) {
         log.error("锁定云服务器失败：{}", var5.getMessage(), var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   @PostMapping({"/server/unlock"})
   @Operation(
      summary = "解锁云服务器（资源池/下游）"
   )
   public CommonResult serverUnlock(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.unlockCloudServer(s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("解锁云服务器失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/rebuild"})
   @Operation(
      summary = "重建云服务器（资源池/下游）"
   )
   public CommonResult serverRebuild(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.rebuildCloudServer(s.getId(), body.getInt("fullRebuild"));
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("重建云服务器失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/updatePassword"})
   @Operation(
      summary = "修改云服务器密码（资源池/下游）"
   )
   public CommonResult serverUpdatePassword(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         String osPassword = body.getStr("osPassword");
         if (ObjectUtil.isEmpty(osPassword)) {
            return CommonResult.error("系统密码不能为空");
         } else {
            String pwdMsg = OsInstancePasswordValidator.validate(osPassword);
            if (pwdMsg != null) {
               return CommonResult.error(pwdMsg);
            } else {
               BasePoolPasswordParam p = new BasePoolPasswordParam();
               p.setId(s.getId());
               p.setHostName(body.getStr("hostName"));
               p.setOsPassword(osPassword);
               return this.serverApi.updateCloudServerPassword(p);
            }
         }
      } catch (CommonException var6) {
         return CommonResult.error(var6.getMessage());
      } catch (Exception var7) {
         log.error("修改云服务器密码失败：{}", var7.getMessage(), var7);
         return CommonResult.error(var7.getMessage());
      }
   }

   @PostMapping({"/server/thumbnail"})
   @Operation(
      summary = "获取云服务器运行缩略图（资源池/下游）"
   )
   public CommonResult serverThumbnail(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.thumbnailCloudServer(s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("获取云服务器缩略图失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/sync"})
   @Operation(
      summary = "同步云服务器状态（资源池/下游）"
   )
   public CommonResult serverSync(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.syncCloudServerStatus(s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("同步云服务器状态失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/clearStatus"})
   @Operation(
      summary = "清除运行状态为运行中（资源池/下游）"
   )
   public CommonResult serverClearStatus(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.clearCloudServerStatus(s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("清除云服务器运行状态失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/disconnect"})
   @Operation(
      summary = "断开云服务器网络（资源池/下游）"
   )
   public CommonResult serverDisconnect(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.disconnectCloudServerNetwork(s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("断开云服务器网络失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/reconnect"})
   @Operation(
      summary = "恢复云服务器网络（资源池/下游）"
   )
   public CommonResult serverReconnect(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.reconnectCloudServerNetwork(s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("恢复云服务器网络失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/resetIp"})
   @Operation(
      summary = "重置云服务器IP（资源池/下游）"
   )
   public CommonResult serverResetIp(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.resetIpCloudServer(s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("重置云服务器IP失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/vnc"})
   @Operation(
      summary = "获取云服务器 VNC 地址（资源池/下游）"
   )
   public CommonResult serverVnc(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.vncCloudServer(s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("获取云服务器 VNC 失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/networkHistory"})
   @Operation(
      summary = "查询云服务器网络历史（资源池/下游）"
   )
   public CommonResult<?> serverNetworkHistory(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.networkHistoryCloudServer(s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("查询云服务器网络历史失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/cpuHistory"})
   @Operation(
      summary = "查询云服务器 CPU 历史（资源池/下游）"
   )
   public CommonResult<?> serverCpuHistory(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.cpuHistoryCloudServer(s.getId());
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("查询云服务器 CPU 历史失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/backup/create"})
   @Operation(
      summary = "创建云服务器备份（资源池/下游）"
   )
   public CommonResult<?> serverBackupCreate(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         String backupName = body.getStr("backupName");
         return StrUtil.isBlank(backupName) ? CommonResult.error("备份名称不能为空") : this.serverApi.createBackupCloudServer(s.getId(), backupName);
      } catch (CommonException var4) {
         return CommonResult.error(var4.getMessage());
      } catch (Exception var5) {
         log.error("创建云服务器备份失败：{}", var5.getMessage(), var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   @PostMapping({"/server/backup/restore"})
   @Operation(
      summary = "还原云服务器备份（资源池/下游）"
   )
   public CommonResult<?> serverBackupRestore(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         String backupName = body.getStr("backupName");
         return StrUtil.isBlank(backupName) ? CommonResult.error("备份名称不能为空") : this.serverApi.restoreBackupCloudServer(s.getId(), backupName);
      } catch (CommonException var4) {
         return CommonResult.error(var4.getMessage());
      } catch (Exception var5) {
         log.error("还原云服务器备份失败：{}", var5.getMessage(), var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   @PostMapping({"/server/backup/delete"})
   @Operation(
      summary = "删除云服务器备份（资源池/下游）"
   )
   public CommonResult<?> serverBackupDelete(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         String backupName = body.getStr("backupName");
         return StrUtil.isBlank(backupName) ? CommonResult.error("备份名称不能为空") : this.serverApi.deleteBackupCloudServer(s.getId(), backupName);
      } catch (CommonException var4) {
         return CommonResult.error(var4.getMessage());
      } catch (Exception var5) {
         log.error("删除云服务器备份失败：{}", var5.getMessage(), var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   @PostMapping({"/server/snapshot/add"})
   @Operation(
      summary = "创建云服务器快照（资源池/下游）"
   )
   public CommonResult<?> serverSnapshotAdd(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         return this.serverApi.createSnapshotCloudServer(s.getId(), body.getStr("snapshotName"));
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("创建云服务器快照失败：{}", var4.getMessage(), var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @PostMapping({"/server/snapshot/restore"})
   @Operation(
      summary = "还原云服务器快照（资源池/下游）"
   )
   public CommonResult<?> serverSnapshotRestore(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         String snapshotName = body.getStr("snapshotName");
         return StrUtil.isBlank(snapshotName) ? CommonResult.error("快照名称不能为空") : this.serverApi.restoreSnapshotCloudServer(s.getId(), snapshotName);
      } catch (CommonException var4) {
         return CommonResult.error(var4.getMessage());
      } catch (Exception var5) {
         log.error("还原云服务器快照失败：{}", var5.getMessage(), var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   @PostMapping({"/server/snapshot/remove"})
   @Operation(
      summary = "删除云服务器快照（资源池/下游）"
   )
   public CommonResult<?> serverSnapshotRemove(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         String snapshotName = body.getStr("snapshotName");
         return StrUtil.isBlank(snapshotName) ? CommonResult.error("快照名称不能为空") : this.serverApi.deleteSnapshotCloudServer(s.getId(), snapshotName);
      } catch (CommonException var4) {
         return CommonResult.error(var4.getMessage());
      } catch (Exception var5) {
         log.error("删除云服务器快照失败：{}", var5.getMessage(), var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   @PostMapping({"/server/port/add"})
   @Operation(
      summary = "添加 NAT 端口映射（共享 IP，资源池/下游）"
   )
   public CommonResult<?> serverPortAdd(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         BasePoolPortAddParam p = new BasePoolPortAddParam();
         p.setServerId(s.getId());
         p.setPortName(body.getStr("portName"));
         p.setPrivatePort(body.getInt("privatePort"));
         p.setPublicPort(body.getInt("publicPort"));
         return this.serverApi.addPortMappingCloudServer(p);
      } catch (CommonException var4) {
         return CommonResult.error(var4.getMessage());
      } catch (Exception var5) {
         log.error("添加端口映射失败：{}", var5.getMessage(), var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   @PostMapping({"/server/port/delete"})
   @Operation(
      summary = "删除 NAT 端口映射（资源池/下游）"
   )
   public CommonResult<?> serverPortDelete(@RequestBody JSONObject body) {
      try {
         BaseServer s = this.requireOwnedServer(ApiContext.getMemberNo(), body.getStr("upstreamServerId"));
         BasePoolPortDeleteParam p = new BasePoolPortDeleteParam();
         p.setServerId(s.getId());
         p.setPrivatePort(body.getInt("privatePort"));
         p.setPublicPort(body.getInt("publicPort"));
         return this.serverApi.deletePortMappingCloudServer(p);
      } catch (CommonException var4) {
         return CommonResult.error(var4.getMessage());
      } catch (Exception var5) {
         log.error("删除端口映射失败：{}", var5.getMessage(), var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   private static String resolvePoolCloudServerPrimaryKey(JSONObject requestParams) {
      return StrUtil.trimToNull(requestParams.getStr("upstreamServerId"));
   }

   private String resolveServerIdForPoolUpgrade(Long memberNo, JSONObject requestParams) {
      String raw = resolvePoolCloudServerPrimaryKey(requestParams);
      if (ObjectUtil.isEmpty(raw)) {
         return null;
      } else {
         BaseServer s = this.serverApi.selectByServerId(raw);
         if (s != null && !ObjectUtil.isEmpty(s.getId())) {
            if (memberNo != null && !String.valueOf(memberNo).equals(s.getMemberNo())) {
               throw new CommonException("无权操作该服务器", new Object[0]);
            } else {
               return s.getId();
            }
         } else {
            return null;
         }
      }
   }

   private BaseServer requireOwnedServer(Long memberNo, String upstreamServerId) {
      if (ObjectUtil.isEmpty(upstreamServerId)) {
         throw new CommonException("upstreamServerId不能为空", new Object[0]);
      } else if (memberNo == null) {
         throw new CommonException("无法获取账号信息，请检查 API Key 配置", new Object[0]);
      } else {
         BaseServer server = this.serverApi.selectByServerId(upstreamServerId);
         if (server != null && !ObjectUtil.isEmpty(server.getId())) {
            if (!String.valueOf(memberNo).equals(server.getMemberNo())) {
               throw new CommonException("无权操作该服务器", new Object[0]);
            } else {
               return server;
            }
         } else {
            throw new CommonException("服务器不存在", new Object[0]);
         }
      }
   }

   private Order buildUpgradeOrder(
      String newOrderNo, String originalOrderNo, Long memberNo, Integer upgradeItem, BigDecimal payPrice, JSONObject requestParams, String poolServerIdForOrder
   ) {
      Order order = new Order();
      order.setId(SnowflakeIdGenerator.generateId());
      order.setOrderNo(newOrderNo);
      order.setBillingMode(requestParams.getInt("billingMode", 1));
      order.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
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

   private List<Integer> parseIntList(Object obj) {
      List<Integer> result = new ArrayList<>();
      if (obj instanceof JSONArray arr) {
         for (int i = 0; i < arr.size(); i++) {
            result.add(arr.getInt(i));
         }
      } else if (obj instanceof List) {
         result = (List<Integer>)obj;
      }

      return result;
   }

   private void addOrderServerRelation(String orderNo, String serverId, String serverName) {
      OrderServerAddParam orderServerAddParam = new OrderServerAddParam();
      orderServerAddParam.setOrderNo(orderNo);
      orderServerAddParam.setServerId(serverId);
      orderServerAddParam.setServerName(serverName);
      this.orderServerService.add(orderServerAddParam);
   }

   private void saveUpgradeOrderServerConfig(
      String orderNo, CalculatePriceResponse calculatePriceResponse, BigDecimal payPrice, BigDecimal renewPrice, String serverId
   ) {
      if (calculatePriceResponse != null && !ObjectUtil.isEmpty(orderNo)) {
         OrderServerConfigAddParam configParam = new OrderServerConfigAddParam();
         configParam.setOrderNo(orderNo);
         BaseServer server = this.serverApi.getById(serverId);
         configParam.setLineId(server != null ? server.getLineId() : null);
         configParam.setCpu(calculatePriceResponse.getCpu());
         configParam.setRam(calculatePriceResponse.getRam());
         configParam.setCoreDayPrice(calculatePriceResponse.getCoreDayPrice());
         configParam.setCoreMonthPrice(calculatePriceResponse.getCoreMonthPrice());
         configParam.setSystemDisk(calculatePriceResponse.getSystemDisk());
         configParam.setSystemDiskFreeSize(calculatePriceResponse.getSystemDiskFreeSize());
         configParam.setSystemDiskDayPrice(calculatePriceResponse.getSystemDiskDayPrice());
         configParam.setSystemDiskMonthPrice(calculatePriceResponse.getSystemDiskMonthPrice());
         configParam.setDataDisks(calculatePriceResponse.getDataDisks());
         configParam.setDataDisksFreeSize(calculatePriceResponse.getDataDisksFreeSize());
         configParam.setDataDisksDayPrice(calculatePriceResponse.getDataDisksDayPrice());
         configParam.setDataDisksMonthPrice(calculatePriceResponse.getDataDisksMonthPrice());
         configParam.setBandwidth(calculatePriceResponse.getBandwidth());
         configParam.setBandwidthFreeSize(calculatePriceResponse.getBandwidthFreeSize());
         configParam.setBandwidthDayPrice(calculatePriceResponse.getBandwidthDayPrice());
         configParam.setBandwidthMonthPrice(calculatePriceResponse.getBandwidthMonthPrice());
         configParam.setIpNum(calculatePriceResponse.getIpNum());
         configParam.setIpFreeSize(calculatePriceResponse.getIpFreeSize());
         configParam.setIpMonthPrice(calculatePriceResponse.getIpMonthPrice());
         configParam.setDefense(calculatePriceResponse.getDefense());
         configParam.setDefenseFreeSize(calculatePriceResponse.getDefenseFreeSize());
         configParam.setDefenseDayPrice(calculatePriceResponse.getDefenseDayPrice());
         configParam.setDefenseMonthPrice(calculatePriceResponse.getDefenseMonthPrice());
         configParam.setUnit(calculatePriceResponse.getUnit());
         configParam.setDuration(calculatePriceResponse.getDuration());
         configParam.setUnitPrice(calculatePriceResponse.getUnitPrice());
         configParam.setNumber(calculatePriceResponse.getNumber() == null ? 1 : calculatePriceResponse.getNumber());
         configParam.setTotalPrice(calculatePriceResponse.getTotalPrice());
         configParam.setPayPrice(payPrice);
         configParam.setRenewPrice(renewPrice);
         this.orderServerConfigService.add(configParam);
         log.info("资源池升级补写 ORDER_SERVER_CONFIG 成功，orderNo={}, serverId={}", orderNo, serverId);
      }
   }

   private JSONObject buildResourcePoolCreateResponsePayload(String orderNo) {
      List<OrderServer> rows = this.orderServerService.queryByList(orderNo);
      List<String> ids = rows.stream().map(OrderServer::getServerId).filter(CharSequenceUtil::isNotBlank).toList();
      JSONObject data = new JSONObject();
      if (ids.isEmpty()) {
         log.warn("资源池新购完成但未查询到 ORDER_SERVER.server_id，orderNo={}", orderNo);
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

   private void logCreateChangeLogsForCreatedServers(String orderNo, Long memberNo) {
      List<OrderServer> orderServers = this.orderServerService.queryByList(orderNo);
      if (ObjectUtil.isEmpty(orderServers)) {
         log.warn("创建订单后未找到 ORDER_SERVER 记录，无法写入 CREATE 变更日志，订单号：{}", orderNo);
      } else {
         for (OrderServer os : orderServers) {
            if (os != null && !ObjectUtil.isEmpty(os.getServerId())) {
               BaseServer server = this.serverApi.getById(os.getServerId());
               if (server == null) {
                  log.warn("创建订单后未找到服务器记录，跳过写入 CREATE 变更日志，订单号：{}，serverId：{}", orderNo, os.getServerId());
               } else {
                  BaseResourceChangeLog changeLog = new BaseResourceChangeLog();
                  changeLog.setId(SnowflakeIdGenerator.generateId());
                  changeLog.setOrderNo(orderNo);
                  changeLog.setMemberNo(memberNo);
                  changeLog.setChangeType("CREATE");
                  changeLog.setProcessed(0);
                  changeLog.setUpstreamServerId(StrUtil.blankToDefault(server.getUpstreamServerId(), server.getId()));
                  changeLog.setPoolSyncTarget("CLOUD");
                  changeLog.setCreateTime(new Date());
                  this.resourceChangeLogApi.save(changeLog);
               }
            }
         }

         log.info("✅ 创建订单后已补写 CREATE 变更日志，订单号：{}，数量：{}", orderNo, orderServers.size());
      }
   }
}
