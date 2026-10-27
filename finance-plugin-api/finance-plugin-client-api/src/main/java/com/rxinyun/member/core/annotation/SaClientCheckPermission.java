package com.rxinyun.member.core.annotation;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@SaCheckPermission(
   type = "C"
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SaClientCheckPermission {
   @AliasFor(
      annotation = SaCheckPermission.class
   )
   String[] value() default {};

   @AliasFor(
      annotation = SaCheckPermission.class
   )
   SaMode mode() default SaMode.AND;
}
