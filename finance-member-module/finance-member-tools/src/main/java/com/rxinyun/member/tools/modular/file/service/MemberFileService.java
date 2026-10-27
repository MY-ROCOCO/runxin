package com.rxinyun.member.tools.modular.file.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.member.tools.modular.file.entity.MemberFile;
import com.rxinyun.member.tools.modular.file.param.MemberFileIdParam;
import com.rxinyun.member.tools.modular.file.param.MemberFileListParam;
import com.rxinyun.member.tools.modular.file.param.MemberFilePageParam;
import com.rxinyun.member.tools.modular.file.param.MemberFileUrlListParam;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface MemberFileService extends IService<MemberFile> {
   String uploadReturnId(String engine, MultipartFile file);

   String uploadReturnUrl(String engine, MultipartFile file);

   Page<MemberFile> page(MemberFilePageParam devFilePageParam);

   List<MemberFile> list(MemberFileListParam devFileListParam);

   void download(MemberFileIdParam devFileIdParam, HttpServletResponse response) throws IOException;

   void delete(List<MemberFileIdParam> devFileIdParamList);

   MemberFile detail(MemberFileIdParam devFileIdParam);

   List<MemberFile> getFileListByUrlList(MemberFileUrlListParam devFileUrlListParam);

   MemberFile queryEntity(String id);
}
