package com.rxinyun.member.core.annotation;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@SaCheckRole(
   type = "C"
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SaClientCheckRole {
   @AliasFor(
      annotation = SaCheckRole.class
   )
   String[] value() default {};

   @AliasFor(
      annotation = SaCheckRole.class
   )
   SaMode mode() default SaMode.AND;
}
