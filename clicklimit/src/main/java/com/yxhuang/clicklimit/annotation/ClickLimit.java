package com.yxhuang.clicklimit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yxhuang
 * Date: 2019/6/21
 * Description:
 */
@Target({ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ClickLimit {

    int value() default 500;
}
