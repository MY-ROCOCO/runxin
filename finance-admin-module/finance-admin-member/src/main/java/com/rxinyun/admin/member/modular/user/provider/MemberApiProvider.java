package com.rxinyun.admin.member.modular.user.provider;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.member.modular.user.entity.AdminClientUser;
import com.rxinyun.admin.member.modular.user.enums.ClientUserStatusEnum;
import com.rxinyun.admin.member.modular.user.param.ClientUserIdParam;
import com.rxinyun.admin.member.modular.user.service.AdminClientUserService;
import com.rxinyun.member.member.api.MemberApi;
import com.rxinyun.member.member.pojo.BaseMember;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemberApiProvider implements MemberApi {
   @Resource
   private AdminClientUserService userService;

   public BaseMember detail(Long memberId) {
      ClientUserIdParam clientUserIdParam = new ClientUserIdParam();
      clientUserIdParam.setId(memberId);
      AdminClientUser adminClientUser = this.userService.detail(clientUserIdParam);
      return null == adminClientUser ? null : (BaseMember)BeanUtil.toBean(adminClientUser, BaseMember.class);
   }

   public List<BaseMember> listEnabledMembers() {
      return this.userService
         .list((Wrapper<AdminClientUser>)new LambdaQueryWrapper<AdminClientUser>().eq(AdminClientUser::getUserStatus, ClientUserStatusEnum.ENABLE.getValue()))
         .stream()
         .map(item -> (BaseMember)BeanUtil.toBean(item, BaseMember.class))
         .toList();
   }
}
