package com.rxinyun.admin.dev.modular.slideshow.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.dev.modular.slideshow.entity.DevSlideshow;
import com.rxinyun.admin.dev.modular.slideshow.param.DevSlideshowAddParam;
import com.rxinyun.admin.dev.modular.slideshow.param.DevSlideshowEditParam;
import com.rxinyun.admin.dev.modular.slideshow.param.DevSlideshowIdParam;
import com.rxinyun.admin.dev.modular.slideshow.param.DevSlideshowPageParam;
import java.util.List;

public interface DevSlideshowService extends IService<DevSlideshow> {
   Page<DevSlideshow> page(DevSlideshowPageParam devSlideshowPageParam);

   void add(DevSlideshowAddParam devSlideshowAddParam);

   void edit(DevSlideshowEditParam devSlideshowEditParam);

   void delete(List<DevSlideshowIdParam> devSlideshowIdParamList);

   DevSlideshow queryEntity(String id);

   void disableStatus(DevSlideshowIdParam devSlideshowIdParam);

   void enableStatus(DevSlideshowIdParam devSlideshowIdParam);

   List<JSONObject> getListByPlace(String place);
}
