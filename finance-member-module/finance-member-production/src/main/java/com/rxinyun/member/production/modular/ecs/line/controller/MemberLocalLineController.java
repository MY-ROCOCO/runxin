package com.rxinyun.member.production.modular.ecs.line.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.ecs.line.entity.MemberLocalLine;
import com.rxinyun.member.production.modular.ecs.line.param.MemberLocalLineIdParam;
import com.rxinyun.member.production.modular.ecs.line.param.MemberLocalLinePageParam;
import com.rxinyun.member.production.modular.ecs.line.service.MemberLocalLineService;
import com.rxinyun.member.production.modular.ecs.line.vo.MemberLocalLineVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "服务器线路管理控制器"
)
@RestController
@RequestMapping({"/member"})
@Validated
public class MemberLocalLineController {
   @Resource
   private MemberLocalLineService serverLocalLineService;

   @Operation(
      summary = "获取线路分页"
   )
   @GetMapping({"/line/page"})
   public CommonResult<Page<MemberLocalLine>> page(MemberLocalLinePageParam pageParam) {
      return CommonResult.data(this.serverLocalLineService.page(pageParam));
   }

   @Operation(
      summary = "获取线路详情"
   )
   @GetMapping({"/line/detail"})
   public CommonResult<MemberLocalLineVO> detail(@Valid MemberLocalLineIdParam serverLocalLineIdParam) {
      return CommonResult.data(this.serverLocalLineService.detail(serverLocalLineIdParam));
   }

   @Operation(
      summary = "根据可用区查询线路"
   )
   @GetMapping({"/line/available"})
   public CommonResult<List<Map<String, Object>>> queryByArea(@RequestParam("availableId") String availableId) {
      return CommonResult.data(this.serverLocalLineService.queryByAvailableId(availableId));
   }
}
