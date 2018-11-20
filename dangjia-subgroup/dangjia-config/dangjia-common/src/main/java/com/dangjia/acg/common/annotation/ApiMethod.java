package com.dangjia.acg.common.annotation;

import java.lang.annotation.*;

/**
 * Created by QiYuXiang on 2018/3/17.
 */
@Target( {ElementType.METHOD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiMethod {
}
