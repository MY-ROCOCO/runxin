package com.rxinyun.admin.workbench.service;

import com.rxinyun.admin.workbench.pojo.vo.BusinessVO;
import com.rxinyun.admin.workbench.pojo.vo.DisplayPanelVO;
import com.rxinyun.admin.workbench.pojo.vo.FinanceVO;
import com.rxinyun.admin.workbench.pojo.vo.MemberVO;
import com.rxinyun.workorder.pojo.WorkOrderStatisticsResponse;

public interface BenchWorkOrderService {
   DisplayPanelVO getTop();

   WorkOrderStatisticsResponse statisticsWorkOrder(String date);

   MemberVO statisticsReal(String time);

   FinanceVO finance(String time);

   BusinessVO getBusiness(String time);
}
