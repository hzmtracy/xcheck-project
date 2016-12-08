package com.c.j.w.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 杭州动享互联网技术有限公司
 *
 * @Author chenjw
 * @Date 2016年12月06日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Security {

    /** 模块名称 */
    String module() default "";

    /** 默认过期时间30秒 */
    int seconds() default 60;

    /** 请求次数限制 */
    int limit() default 3;
}
