package com.rxinyun.admin.dev.modular.dev;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.dev.modular.dict.entity.DevDict;
import com.rxinyun.admin.dev.modular.dict.enums.DevDictCategoryEnum;
import com.rxinyun.admin.dev.modular.dict.service.DevDictService;
import com.rxinyun.admin.dev.modular.email.service.DevEmailService;
import com.rxinyun.admin.dev.modular.file.service.DevFileService;
import com.rxinyun.admin.dev.modular.job.entity.DevJob;
import com.rxinyun.admin.dev.modular.job.enums.DevJobStatusEnum;
import com.rxinyun.admin.dev.modular.job.service.DevJobService;
import com.rxinyun.admin.dev.modular.message.service.DevMessageService;
import com.rxinyun.admin.dev.modular.sms.service.DevSmsService;
import com.rxinyun.dev.api.DevApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class DevApiProvider implements DevApi {
   @Resource
   private DevDictService devDictService;
   @Resource
   private DevJobService devJobService;
   @Resource
   private DevFileService devFileService;
   @Resource
   private DevSmsService devSmsService;
   @Resource
   private DevEmailService devEmailService;
   @Resource
   private DevMessageService devMessageService;

   public JSONObject getDevOpCount() {
      Long sysDictCount = this.devDictService.count((Wrapper<DevDict>)new LambdaQueryWrapper<DevDict>().eq(DevDict::getCategory, DevDictCategoryEnum.FRM.getValue()));
      Long bizDictCount = this.devDictService.count((Wrapper<DevDict>)new LambdaQueryWrapper<DevDict>().eq(DevDict::getCategory, DevDictCategoryEnum.BIZ.getValue()));
      Long jobCount = this.devJobService.count((Wrapper<DevJob>)new LambdaQueryWrapper<DevJob>().eq(DevJob::getJobStatus, DevJobStatusEnum.RUNNING.getValue()));
      JSONObject json = new JSONObject();
      json.set("sysDictCount", sysDictCount);
      json.set("bizDictCount", bizDictCount);
      json.set("jobCount", jobCount);
      return json;
   }

   public JSONObject getToolDataCount() {
      JSONObject json = new JSONObject();
      json.set("fileCount", this.devFileService.count());
      json.set("smsCount", this.devSmsService.count());
      json.set("emailCount", this.devEmailService.count());
      json.set("messageCount", this.devMessageService.count());
      return json;
   }
}
