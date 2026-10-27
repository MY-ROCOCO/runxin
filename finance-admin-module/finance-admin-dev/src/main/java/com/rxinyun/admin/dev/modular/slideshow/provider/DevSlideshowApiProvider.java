package com.rxinyun.admin.dev.modular.slideshow.provider;

import cn.hutool.json.JSONObject;
import com.rxinyun.admin.dev.modular.slideshow.service.DevSlideshowService;
import com.rxinyun.dev.api.DevSlideshowApi;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DevSlideshowApiProvider implements DevSlideshowApi {
   @Resource
   private DevSlideshowService devSlideshowService;

   public List<JSONObject> getListByPlace(String place) {
      return this.devSlideshowService.getListByPlace(place);
   }
}
