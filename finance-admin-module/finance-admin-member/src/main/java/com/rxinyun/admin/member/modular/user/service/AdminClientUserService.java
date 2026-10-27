package com.rxinyun.admin.member.modular.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.member.modular.user.entity.AdminClientUser;
import com.rxinyun.admin.member.modular.user.param.AdminMemberAddParam;
import com.rxinyun.admin.member.modular.user.param.ClientEmailUpdateParam;
import com.rxinyun.admin.member.modular.user.param.ClientPhoneUpdateParam;
import com.rxinyun.admin.member.modular.user.param.ClientResetPasswordParam;
import com.rxinyun.admin.member.modular.user.param.ClientSwitchLoginTicketParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserAddParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserDataStatisticsParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserEditParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserIdParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserPageParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserStatusParam;
import com.rxinyun.admin.member.modular.user.param.UpdateMemberGroupParam;
import com.rxinyun.admin.member.modular.user.response.ClientUserResponse;
import com.rxinyun.payments.api.pojo.PaymentsAdminBalancePram;
import java.util.List;

public interface AdminClientUserService extends IService<AdminClientUser> {
   Page<ClientUserResponse> page(ClientUserPageParam clientUserPageParam);

   void add(ClientUserAddParam clientUserAddParam);

   void edit(ClientUserEditParam clientUserEditParam);

   void delete(List<ClientUserIdParam> clientUserIdParamList);

   AdminClientUser detail(ClientUserIdParam clientUserIdParam);

   void updateUserLoginInfo(Long userId, String device);

   AdminClientUser queryEntity(Long id);

   void updateStatus(ClientUserStatusParam clientUserStatusParam);

   void resetPassword(ClientResetPasswordParam clientResetPasswordParam);

   void adminAdjustAdd(PaymentsAdminBalancePram paymentsAdminAddBalancePram);

   ClientUserDataStatisticsParam findDatastatistics(Long userId);

   void updateMemberGroup(UpdateMemberGroupParam updateMemberGroupParam);

   Long countGroupNumber(String groupId);

   void updatePhone(ClientPhoneUpdateParam updatePhoneParam);

   void updateEmail(ClientEmailUpdateParam updateEmailParam);

   void addMemberByAdmin(AdminMemberAddParam addParam);

   String createSwitchLoginTicket(ClientSwitchLoginTicketParam param);
}
