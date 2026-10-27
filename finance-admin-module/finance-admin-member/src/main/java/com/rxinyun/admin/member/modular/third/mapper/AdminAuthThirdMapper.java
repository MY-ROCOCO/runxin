package com.rxinyun.admin.member.modular.third.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.member.modular.third.entity.AuthThirdUser;
import java.util.List;

public interface AdminAuthThirdMapper extends BaseMapper<AuthThirdUser> {
   void removeByUserIds(List<Long> clientUserIdParamList);
}
