package com.rxinyun.order.api;

import com.rxinyun.order.core.pojo.BaseOrderServer;
import java.util.List;

public interface OrderServerApi {
   List<BaseOrderServer> queryByOrderNo(String orderNo);
}
