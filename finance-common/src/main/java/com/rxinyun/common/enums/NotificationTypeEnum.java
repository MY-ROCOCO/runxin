package com.rxinyun.common.enums;

public enum NotificationTypeEnum {
   MEMBER_REGISTER_VERIFY("member_register_verify", "会员注册验证码"),
   MEMBER_RESET_PASSWORD_VERIFY("member_reset_password_verify", "会员重置密码验证码"),
   MEMBER_LOGIN_VERIFY("member_login_verify", "会员登录验证码"),
   MEMBER_BIND_PHONE_VERIFY("member_bind_phone_verify", "会员绑定手机验证码"),
   MEMBER_BIND_EMAIL_VERIFY("member_bind_email_verify", "会员绑定邮箱验证码"),
   MEMBER_ORDER_PAYMENT_SUCCESS("member_order_payment_success", "订单付款成功通知"),
   MEMBER_CLOUD_SERVER_ACTIVE("member_cloud_server_active", "云服务器开通成功通知"),
   MEMBER_CLOUD_SERVER_EXPIRING("member_cloud_server_expiring", "云服务器即将到期通知"),
   MEMBER_CLOUD_SERVER_DELETED("member_cloud_server_deleted", "云服务器删除通知"),
   MEMBER_DEDICATED_SERVER_ACTIVE("member_dedicated_server_active", "独立服务器开通成功通知"),
   MEMBER_DEDICATED_SERVER_EXPIRING("member_dedicated_server_expiring", "独立服务器即将到期通知"),
   MEMBER_DEDICATED_SERVER_DELETED("member_dedicated_server_deleted", "独立服务器删除通知"),
   MEMBER_CLOUD_SERVER_AUTO_RENEW_SUCCESS("member_cloud_server_auto_renew_success", "云服务器自动续费成功通知"),
   MEMBER_CLOUD_SERVER_AUTO_RENEW_BALANCE_FAIL("member_cloud_server_auto_renew_balance_fail", "云服务器自动续费余额不足通知"),
   MEMBER_DEDICATED_SERVER_AUTO_RENEW_SUCCESS("member_dedicated_server_auto_renew_success", "独立服务器自动续费成功通知"),
   MEMBER_DEDICATED_SERVER_AUTO_RENEW_BALANCE_FAIL("member_dedicated_server_auto_renew_balance_fail", "独立服务器自动续费余额不足通知"),
   MEMBER_TICKET_SERVICE("member_ticket_service", "工单服务通知"),
   ADMIN_TICKET_SERVICE("admin_ticket_service", "工单服务通知"),
   ADMIN_DEDICATED_SERVER_MANUAL("admin_dedicated_server_manual", "独立服务器人工服务通知"),
   ADMIN_ORDER_PAYMENT_SUCCESS("admin_order_payment_success", "订单付款成功通知");

   private final String code;
   private final String description;

   private NotificationTypeEnum(String code, String description) {
      this.code = code;
      this.description = description;
   }

   public String getCode() {
      return this.code;
   }

   public String getDescription() {
      return this.description;
   }
}
