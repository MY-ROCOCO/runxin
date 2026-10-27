package com.rxinyun.admin.dev.modular.slideshow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.dev.modular.slideshow.entity.DevSlideshow;
import com.rxinyun.admin.dev.modular.slideshow.enums.DevSlideshowStatusEnum;
import com.rxinyun.admin.dev.modular.slideshow.mapper.DevSlideshowMapper;
import com.rxinyun.admin.dev.modular.slideshow.param.DevSlideshowAddParam;
import com.rxinyun.admin.dev.modular.slideshow.param.DevSlideshowEditParam;
import com.rxinyun.admin.dev.modular.slideshow.param.DevSlideshowIdParam;
import com.rxinyun.admin.dev.modular.slideshow.param.DevSlideshowPageParam;
import com.rxinyun.admin.dev.modular.slideshow.service.DevSlideshowService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DevSlideshowServiceImpl extends ServiceImpl<DevSlideshowMapper, DevSlideshow> implements DevSlideshowService {
   @Override
   public Page<DevSlideshow> page(DevSlideshowPageParam devSlideshowPageParam) {
      QueryWrapper<DevSlideshow> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(devSlideshowPageParam.getTitle())) {
         queryWrapper.lambda().like(DevSlideshow::getTitle, devSlideshowPageParam.getTitle());
      }

      if (ObjectUtil.isNotEmpty(devSlideshowPageParam.getStatus())) {
         queryWrapper.lambda().eq(DevSlideshow::getStatus, devSlideshowPageParam.getStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{devSlideshowPageParam.getSortField(), devSlideshowPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(devSlideshowPageParam.getSortOrder());
         queryWrapper.orderBy(
            true,
            devSlideshowPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
            StrUtil.toUnderlineCase(devSlideshowPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(DevSlideshow::getSortCode);
      }

      return (Page<DevSlideshow>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(DevSlideshowAddParam devSlideshowAddParam) {
      DevSlideshow devSlideshow = (DevSlideshow)BeanUtil.toBean(devSlideshowAddParam, DevSlideshow.class);
      devSlideshow.setStatus(DevSlideshowStatusEnum.DISABLE.getValue());
      this.save(devSlideshow);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(DevSlideshowEditParam devSlideshowEditParam) {
      DevSlideshow devSlideshow = this.queryEntity(devSlideshowEditParam.getId());
      BeanUtil.copyProperties(devSlideshowEditParam, devSlideshow, new String[0]);
      this.updateById(devSlideshow);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<DevSlideshowIdParam> devSlideshowIdParamList) {
      this.removeByIds(CollStreamUtil.toList(devSlideshowIdParamList, DevSlideshowIdParam::getId));
   }

   @Override
   public DevSlideshow queryEntity(String id) {
      DevSlideshow devSlideshow = (DevSlideshow)this.getById(id);
      if (ObjectUtil.isEmpty(devSlideshow)) {
         throw new CommonException("轮播图不存在，id值为：{}", new Object[]{id});
      } else {
         return devSlideshow;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void disableStatus(DevSlideshowIdParam devSlideshowIdParam) {
      this.update(
         (Wrapper<DevSlideshow>)((LambdaUpdateWrapper<DevSlideshow>)new LambdaUpdateWrapper<DevSlideshow>().eq(DevSlideshow::getId, devSlideshowIdParam.getId()))
            .set(DevSlideshow::getStatus, DevSlideshowStatusEnum.DISABLE.getValue())
      );
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void enableStatus(DevSlideshowIdParam devSlideshowIdParam) {
      this.update(
         (Wrapper<DevSlideshow>)((LambdaUpdateWrapper<DevSlideshow>)new LambdaUpdateWrapper<DevSlideshow>().eq(DevSlideshow::getId, devSlideshowIdParam.getId()))
            .set(DevSlideshow::getStatus, DevSlideshowStatusEnum.ENABLE.getValue())
      );
   }

   @Override
   public List<JSONObject> getListByPlace(String place) {
      List<JSONObject> resultList = CollectionUtil.newArrayList(new JSONObject[0]);
      List<DevSlideshow> slideshowList = this.list(
         (Wrapper<DevSlideshow>)((LambdaQueryWrapper<DevSlideshow>)new LambdaQueryWrapper<DevSlideshow>().eq(DevSlideshow::getStatus, DevSlideshowStatusEnum.ENABLE.getValue()))
            .orderByDesc(DevSlideshow::getSortCode)
      );
      slideshowList.forEach(item -> {
         JSONObject obj = new JSONObject();
         obj.set("id", item.getId());
         obj.set("title", item.getTitle());
         obj.set("image", item.getImage());
         obj.set("jumpLink", item.getJumpLink());
         obj.set("description", item.getDescription());
         obj.set("buttonVisible", item.getButtonVisible());
         obj.set("buttonName", item.getButtonName());
         obj.set("pathDetails", null);
         resultList.add(obj);
      });
      if (resultList.size() == 0) {
         JSONObject staticObj = new JSONObject();
         staticObj.set("id", IdWorker.getIdStr());
         staticObj.set("title", "静态文件");
         staticObj.set(
            "image",
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAfQAAADcCAIAAAAJPMQyAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyZpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDcuMi1jMDAwIDc5LjU2NmViYzViNCwgMjAyMi8wNS8wOS0wODoyNTo1NSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIDIzLjQgKFdpbmRvd3MpIiB4bXBNTTpJbnN0YW5jZUlEPSJ4bXAuaWlkOjE5RDQ0MENFMDc2RjExRURBRTU5RTM4OUFDNjE5MDUyIiB4bXBNTTpEb2N1bWVudElEPSJ4bXAuZGlkOjE5RDQ0MENGMDc2RjExRURBRTU5RTM4OUFDNjE5MDUyIj4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6MTlENDQwQ0MwNzZGMTFFREFFNTlFMzg5QUM2MTkwNTIiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6MTlENDQwQ0QwNzZGMTFFREFFNTlFMzg5QUM2MTkwNTIiLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz7jEwzPAAAFT0lEQVR42uzdW3JTRxSG0YanMAGYU+ZflQFgxxAnuBKXJFvWJRwCpHAUWUc6l9271xrCfvgeus4vvfr5l30BCO/dm/L2J2c41WsnAKrw26osN84g7kAu+1KulmXnrUHcgWQet+Vm5QziDqTz6bHcPzmDuAPpXC3LZucM4g7k8rns10tnEHcgnb+euvcZxB3I5mZV1h5nxB1IZrcv7xfFh5HiDmSz3JTbB2cQdyAds1VxBxLqZqsLs1VxB9J53JmtijuQkdmquAM5ma2KO5CQ2aq4AzmZrYo7kJPZqrgDCZmtijuQ03LTLZsQdyCb2wezVXEH0tmX7nGm8dmquAMJrZufrYo7kNOnx+7jSHEHyOa64dmquANpfS77VauzVXEHMrtvdbYq7kByN6vuZ4HFHSCV3b77Q4/WPowUdyC/Bmer4g40obXZqrgDTWhttiruQCuamq2KO9CQdmar4g605XrRxGxV3IG2bPZNzFbFHWhOC7NVcQda1M1Wt+IOkMvuy+NM4g8jxR1oVO7ZqrgD7Uo8WxV3oF2JZ6viDjQt62xV3IHWpZytijtAwtmquAMknK2KO0An2WxV3AG+yjRbFXeArzLNVsUd4F9pZqviDvCDHLNVcQf4QY7ZqrgDPJdgtiruAAfUPlsVd4DDqp6tijvAYVXPVsUd4H/VO1sVd4BjKp2tijvAMbt9eV/hbFXcAV6wqnC2Ku4AL6tutiruAC+rbrYq7gAnWe/Kr/U8zog7wKnu6pmtijtAD7XMVsUdoIdaZqviDtDP/VP5I/xsVdwBevsQfrYq7gC9xZ+tijvAOYLPVsUd4EyRZ6viDnCmyLNVcQc4X9jZqrgDXCTmbFXcAS4VcLYq7gCXCjhbFXeAAUSbrYo7wDBCzVbFHWAYoWar4g4wmDizVXEHGFKQ2aq4AwwpyGxV3AEGFmG2Ku4Aw5t9tiruAKOYd7Yq7gCjmHe2Ku4AY5lxtiruACOaa7Yq7gAjmmu2Ku4A45pltiruAKObfrYq7gCjm362Ku4AU5h4tiruABOZcrYq7gDTmWy2Ku4A05lstiruAJOaZrYq7gBTm2C2Ku4AU+tmq4txZ6viDjCD1Xbc2aq4A8xj1NmquAPMY9TZqrgDzGa82aq4A8xppNmquAPM7GqE2aq4A8xsO8JsVdwB5jf4bFXcAUIYdrYq7gAhDDtbFXeAKAacrYo7QCBDzVbFHSCQoWar4g4QSzdbvfjLSHEHCOdufelsVdwBIrpwtiruABFdOFsVd4CgLpmtijtAXGfPVsUdIK6zZ6viDhDaebNVcQeI7vahLHrOVsUdILr9ly8je81WxR2gAn1nq+IOUIdes1VxB6jG6bNVcQeoxumzVXEHqMmJs1VxB6jMKbNVcQeozCmzVXEHqM+Ls1VxB6jS8dmquANU6fhsVdwBanVktiruABW7W5c/1+IOkM718sBsVdwB6nZwtiruANX772xV3AEyeDZbFXeADJ7NVsUdIInVtnxciTtAOr9/m62KO0Ae32er4g6Qyj+zVXEHyOZuLe4AGYk7gLgDIO4AiDsA4g6AuAOIOwDiDoC4AyDuAIg7gLg7AYC4AyDuAIg7AOIOgLgDiDsA4g6AuAMg7gCIO4C4AyDuAIg7AOIOgLgDIO4A4g6AuAMg7gCIOwDiDiDuAIg7AOIOgLgDIO4AiDuAuAMg7gCIOwDiDoC4A4g7AOIOgLgDIO4AiDsA4g4g7gCIOwDiDoC4AyDuAOIOgLgDIO4AiDsA4g6AuAOIOwDiDoC4AyDuAIg7gLgDIO4AiDsA4g6AuAMg7gDiDoC4AxDD3wIMAJ+nRtxsLfxoAAAAAElFTkSuQmCC"
         );
         staticObj.set("pathDetails", null);
         resultList.add(staticObj);
         return resultList;
      } else {
         return resultList;
      }
   }
}
