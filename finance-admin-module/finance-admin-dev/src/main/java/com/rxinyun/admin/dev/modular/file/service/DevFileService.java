package com.rxinyun.admin.dev.modular.file.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.dev.modular.file.entity.DevFile;
import com.rxinyun.admin.dev.modular.file.param.DevFileIdParam;
import com.rxinyun.admin.dev.modular.file.param.DevFileListParam;
import com.rxinyun.admin.dev.modular.file.param.DevFilePageParam;
import com.rxinyun.admin.dev.modular.file.param.DevFileUrlListParam;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface DevFileService extends IService<DevFile> {
   String uploadReturnId(String engine, MultipartFile file);

   String uploadReturnUrl(String engine, MultipartFile file);

   Page<DevFile> page(DevFilePageParam devFilePageParam);

   List<DevFile> list(DevFileListParam devFileListParam);

   void download(DevFileIdParam devFileIdParam, HttpServletResponse response) throws IOException;

   void delete(List<DevFileIdParam> devFileIdParamList);

   DevFile detail(DevFileIdParam devFileIdParam);

   List<DevFile> getFileListByUrlList(DevFileUrlListParam devFileUrlListParam);

   DevFile queryEntity(String id);
}
