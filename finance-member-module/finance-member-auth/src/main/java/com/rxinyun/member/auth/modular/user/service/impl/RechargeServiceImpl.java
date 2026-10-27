package com.rxinyun.member.auth.modular.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.util.date.enums.DateResponseKeyEnum;
import com.rxinyun.member.auth.modular.user.entity.Recharge;
import com.rxinyun.member.auth.modular.user.mapper.RechargeMapper;
import com.rxinyun.member.auth.modular.user.param.RechargeAddParam;
import com.rxinyun.member.auth.modular.user.param.RechargeEditParam;
import com.rxinyun.member.auth.modular.user.param.RechargeIdParam;
import com.rxinyun.member.auth.modular.user.param.RechargePageParam;
import com.rxinyun.member.auth.modular.user.service.RechargeService;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.api.RechargeBalanceApi;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RechargeServiceImpl extends ServiceImpl<RechargeMapper, Recharge> implements RechargeService, RechargeBalanceApi {
   private static final Logger log = LoggerFactory.getLogger(RechargeServiceImpl.class);
   @Autowired
   private RechargeMapper rechargeMapper;

   public BigDecimal getRechargeMoneyByDate(HashMap<String, LocalDateTime> map, Long userId) {
      LocalDateTime start = map.get(DateResponseKeyEnum.START_TIME_NAME.getValue());
      LocalDateTime end = map.get(DateResponseKeyEnum.END_TIME_NAME.getValue());
      QueryWrapper<Recharge> queryWrapper = new QueryWrapper();
      queryWrapper.lambda()
         .ge(start != null, Recharge::getCreateTime, start)
         .le(end != null, Recharge::getCreateTime, end)
         .eq(userId != null, Recharge::getMemberNo, userId)
         .eq(Recharge::getStatus, 2);
      List<Recharge> list = this.list(queryWrapper);
      BigDecimal money = new BigDecimal(0);

      for (Recharge recharge : list) {
         money = money.add(recharge.getAmount());
      }

      return money;
   }

   @Override
   public Page<Recharge> page(RechargePageParam rechargePageParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      QueryWrapper<Recharge> queryWrapper = new QueryWrapper().checkSqlInjection();
      ((LambdaQueryWrapper<Recharge>)queryWrapper.lambda().eq(Recharge::getMemberNo, clientLoginUser.getId())).orderByDesc(CommonEntity::getCreateTime);
      return (Page<Recharge>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public Recharge detailForCurrentMember(RechargeIdParam rechargeIdParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      Recharge recharge = this.queryEntity(rechargeIdParam.getId());
      if (!ObjectUtil.equal(recharge.getMemberNo(), clientLoginUser.getId())) {
         throw new CommonException("充值记录不存在或无权查看", new Object[0]);
      } else {
         return recharge;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public String add(RechargeAddParam rechargeAddParam) {
      Recharge recharge = (Recharge)BeanUtil.toBean(rechargeAddParam, Recharge.class);
      this.save(recharge);
      return recharge.getId();
   }

   @Override
   public void edit(RechargeEditParam rechargeEditParam) {
      Recharge recharge = this.queryEntity(rechargeEditParam.getId());
      log.info("编辑用户信息：{}", JSON.toJSONString(recharge));
      BeanUtil.copyProperties(rechargeEditParam, recharge, new String[0]);
      log.info("编辑用户信息clientUser：{}", JSON.toJSONString(recharge));
      this.updateById(recharge);
   }

   @Override
   public Recharge detail(RechargeIdParam rechargeIdParam) {
      return this.queryEntity(rechargeIdParam.getId());
   }

   @Override
   public Recharge queryEntity(String id) {
      Recharge recharge = (Recharge)this.getById(id);
      if (ObjectUtil.isEmpty(recharge)) {
         throw new CommonException("充值记录不存在，id值为：{}", new Object[]{id});
      } else {
         return recharge;
      }
   }

   @Override
   public List<Recharge> listByStatusAndPayType(Integer status, Integer payType) {
      return ((this.lambdaQuery().eq(Recharge::getStatus, status)).eq(Recharge::getPayType, payType)).list();
   }

   @Override
   public List<Recharge> listByStatusAndPayTypeAndCreateTimeAfter(Integer status, Integer payType, Date afterTime) {
      LambdaQueryChainWrapper<Recharge> query = (LambdaQueryChainWrapper<Recharge>)(this.lambdaQuery().eq(Recharge::getStatus, status))
         .eq(Recharge::getPayType, payType);
      if (afterTime != null) {
         query.ge(CommonEntity::getCreateTime, afterTime);
      }

      return query.list();
   }

   @Override
   public void updateExpectedUsdtAmount(String id, BigDecimal amountUsdt) {
      if (id != null && amountUsdt != null) {
         Recharge r = new Recharge();
         r.setId(id);
         r.setExpectedUsdtAmount(amountUsdt);
         this.updateById(r);
      }
   }

   @Override
   public int expireUnpaidRecharges(Date expireBefore) {
      if (expireBefore == null) {
         return 0;
      } else {
         List<Recharge> unpaidList = ((this.lambdaQuery().eq(Recharge::getStatus, 1))
               .lt(CommonEntity::getCreateTime, expireBefore))
            .list();
         if (unpaidList != null && !unpaidList.isEmpty()) {
            for (Recharge recharge : unpaidList) {
               Recharge update = new Recharge();
               update.setId(recharge.getId());
               update.setStatus(-1);
               this.updateById(update);
               log.info("未支付充值单超时关闭 rechargeId={}", recharge.getId());
            }

            return unpaidList.size();
         } else {
            return 0;
         }
      }
   }
}
