package com.rxinyun.member.member.api;

import com.rxinyun.member.member.pojo.BaseMember;
import java.util.List;

public interface MemberApi {
   BaseMember detail(Long memberId);

   List<BaseMember> listEnabledMembers();
}
