package com.rxinyun.admin.workbench.service.impl;

import com.rxinyun.admin.workbench.pojo.vo.BusinessVO;
import com.rxinyun.admin.workbench.pojo.vo.DisplayPanelVO;
import com.rxinyun.admin.workbench.pojo.vo.FinanceVO;
import com.rxinyun.admin.workbench.pojo.vo.MemberVO;
import com.rxinyun.admin.workbench.service.BenchWorkOrderService;
import com.rxinyun.business.api.BusinessDomainApi;
import com.rxinyun.business.api.IndependentApi;
import com.rxinyun.business.api.ServerApi;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.common.util.date.DateConversionFactoryUtil;
import com.rxinyun.common.util.date.enums.DateResponseKeyEnum;
import com.rxinyun.common.util.date.enums.DateUtilParamName;
import com.rxinyun.member.member.api.ClientUserApi;
import com.rxinyun.member.member.api.CompanyRealApi;
import com.rxinyun.member.member.api.PersonalRealApi;
import com.rxinyun.member.member.api.RechargeBalanceApi;
import com.rxinyun.order.api.OrderApi;
import com.rxinyun.workorder.api.AdminWorkOrederApi;
import com.rxinyun.workorder.pojo.WorkOrderStatisticsResponse;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import org.springframework.stereotype.Service;

@Service
public class BenchWorkOrderServiceImpl implements BenchWorkOrderService {
   @Resource
   private AdminWorkOrederApi adminWorkOrederApi;
   @Resource
   private CompanyRealApi companyRealApi;
   @Resource
   private PersonalRealApi personalRealApi;
   @Resource
   private ClientUserApi clientUserApi;
   @Resource
   private OrderApi orderApi;
   @Resource
   private ServerApi serverApi;
   @Resource
   private IndependentApi independentApi;
   @Resource
   private BusinessDomainApi businessDomainApi;
   @Resource
   private RechargeBalanceApi rechargeBalanceApi;

   @Override
   public DisplayPanelVO getTop() {
      HashMap<String, LocalDateTime> allTime = DateConversionFactoryUtil.conversion(DateUtilParamName.ALLTIME.getValue());
      HashMap<String, LocalDateTime> toDay = DateConversionFactoryUtil.conversion(DateUtilParamName.TO_DAY.getValue());
      DisplayPanelVO displayPanelVO = new DisplayPanelVO();
      displayPanelVO.setClientNum(this.clientUserApi.getUserCountByCreateTime(allTime));
      WorkOrderStatisticsResponse workOrderStatisticsResponse = this.adminWorkOrederApi.getInstance(toDay);
      displayPanelVO.setWorkOrderMissNum(workOrderStatisticsResponse.getNoAccepted());
      displayPanelVO.setOrderNonPaymentNum(this.orderApi.CountByPayStatus(toDay, OrderStatusEnum.UNPAID.getCode()));
      displayPanelVO.setOrderPayNum(this.orderApi.CountByPayStatus(toDay, OrderStatusEnum.PAID.getCode()));
      displayPanelVO.setOrderPayMoney(this.orderApi.OrderPayByCreateTime(toDay, null));
      displayPanelVO.setInvoicePendingNum(this.orderApi.countPendingInvoice());
      displayPanelVO.setInvoicePendingMoney(this.orderApi.sumPendingInvoiceAmount());
      return displayPanelVO;
   }

   @Override
   public WorkOrderStatisticsResponse statisticsWorkOrder(String time) {
      HashMap<String, LocalDateTime> map = DateConversionFactoryUtil.conversion(time);
      return this.adminWorkOrederApi.getInstance(map);
   }

   @Override
   public MemberVO statisticsReal(String time) {
      HashMap<String, LocalDateTime> map = DateConversionFactoryUtil.conversion(time);
      MemberVO memberVO = new MemberVO();
      memberVO.setRegister(this.clientUserApi.getUserCountByCreateTime(map));
      memberVO.setDeal(this.orderApi.OrderCountByCreateTimeStatus(map));
      memberVO.setCompanyReal(this.companyRealApi.getCompanyRealCount(map));
      memberVO.setPersonReal(this.personalRealApi.getPersonalRealCount(map));
      return memberVO;
   }

   @Override
   public FinanceVO finance(String time) {
      HashMap<String, LocalDateTime> map = DateConversionFactoryUtil.conversion(time);
      FinanceVO financeVO = new FinanceVO();
      financeVO.setRechargeMoney(this.rechargeBalanceApi.getRechargeMoneyByDate(map, null));
      financeVO.setOrderPayMoney(this.orderApi.OrderPayByCreateTime(map, null));
      financeVO.setOrderNum(this.orderApi.OrderCountByCreateTime(map));
      financeVO.setRefundMoney(this.orderApi.OrderPayByCreateTimeAndStatus(map));
      return financeVO;
   }

   @Override
   public BusinessVO getBusiness(String time) {
      HashMap<String, LocalDateTime> map = DateConversionFactoryUtil.conversion(time);
      BusinessVO businessVO = new BusinessVO();
      businessVO.setServerNum(this.serverApi.getServerCount(map));
      businessVO.setIndependentServerNum(this.independentApi.getServerCount(map));
      businessVO.setDomainNum(
         this.businessDomainApi
            .getDomainCountByCreateTime(map.get(DateResponseKeyEnum.START_TIME_NAME.getValue()), map.get(DateResponseKeyEnum.END_TIME_NAME.getValue()))
      );
      return businessVO;
   }
}
