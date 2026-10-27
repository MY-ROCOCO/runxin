package com.rxinyun.member.news.modular.news.controller;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.news.modular.news.mapper.MemberNewsTypeMapper;
import com.rxinyun.member.news.modular.news.model.po.NewsType;
import com.rxinyun.member.news.modular.news.service.NewsTypeService;
import com.rxinyun.member.news.modular.news.service.impl.MemberNewsTypeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(
   name = "member新闻类型管理控制器"
)
@RequestMapping(
   path = {"/member"}
)
public class MemberNewsTypeController extends ServiceImpl<MemberNewsTypeMapper, NewsType> implements NewsTypeService {
   @Resource
   MemberNewsTypeServiceImpl memberNewsTypeService;

   @Operation(
      summary = "首页下方点击新闻标题后跳转时,查询新闻类型列表显示在左侧栏目"
   )
   @CommonLog("首页下方点击新闻标题后跳转时,查询新闻类型列表显示在左侧栏目")
   @GetMapping({"news/newsTypeList"})
   public CommonResult<List<NewsType>> newsTypeList() {
      return CommonResult.data(this.memberNewsTypeService.list());
   }
}
