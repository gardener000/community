package com.example.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要登录才能访问的方法
 */
@Target(ElementType.METHOD)    // 说明这个注解是写在方法上的
@Retention(RetentionPolicy.RUNTIME) // 说明这个注解在程序运行时有效
public @interface LoginRequired {
}