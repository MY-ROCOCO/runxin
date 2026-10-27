package com.rxinyun.common.page;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.util.CommonServletUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonPageRequest {
   private static final Logger log = LoggerFactory.getLogger(CommonPageRequest.class);
   private static final String PAGE_SIZE_PARAM_NAME = "size";
   private static final String PAGE_PARAM_NAME = "current";
   private static final Integer PAGE_SIZE_MAX_VALUE = 100;

   public static <T> Page<T> defaultPage() {
      return defaultPage(null);
   }

   public static <T> Page<T> defaultPage(List<OrderItem> orderItemList) {
      int size = 20;
      int page = 1;
      String pageSizeString = CommonServletUtil.getParamFromRequest("size");
      if (ObjectUtil.isNotEmpty(pageSizeString)) {
         try {
            size = Convert.toInt(pageSizeString);
            if (size > PAGE_SIZE_MAX_VALUE) {
               size = PAGE_SIZE_MAX_VALUE;
            }
         } catch (Exception var7) {
            log.error(">>> 分页条数转换异常：", var7);
            size = 20;
         }
      }

      String pageString = CommonServletUtil.getParamFromRequest("current");
      if (ObjectUtil.isNotEmpty(pageString)) {
         try {
            page = Convert.toInt(pageString);
         } catch (Exception var6) {
            log.error(">>> 分页页数转换异常：", var6);
            page = 1;
         }
      }

      Page<T> objectPage = new Page(page, size);
      if (ObjectUtil.isNotEmpty(orderItemList)) {
         objectPage.setOrders(orderItemList);
      }

      return objectPage;
   }
}
