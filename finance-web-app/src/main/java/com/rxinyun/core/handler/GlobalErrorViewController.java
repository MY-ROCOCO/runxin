package com.rxinyun.core.handler;

import cn.hutool.core.util.ObjectUtil;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalErrorViewController {
   private static final Logger log = LoggerFactory.getLogger(GlobalErrorViewController.class);

   public GlobalErrorViewController() {
   }

   @RequestMapping({"/errorView"})
   public CommonResult<String> globalError(HttpServletRequest request) {
      CommonResult<String> commonResult = new CommonResult(404, "路径不存在", null);
      Object model = request.getAttribute("model");
      if (ObjectUtil.isNotEmpty(model) && model instanceof Exception) {
         if (model instanceof CommonException exception) {
            Integer code = exception.getCode();
            String msg = exception.getMsg();
            if (ObjectUtil.isAllNotEmpty(new Object[]{code, msg})) {
               commonResult.setCode(code).setMsg(msg);
            } else if (ObjectUtil.isNotEmpty(msg)) {
               commonResult = CommonResult.error(msg);
            } else {
               commonResult = CommonResult.error();
            }
         } else {
            commonResult = CommonResult.error();
            log.error(">>> 服务器未知异常，具体信息：", (Exception)model);
         }
      }

      return commonResult;
   }
}
