package com.dangjia.acg.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by QiYuXiang on 2018/3/20.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelField {

    String titile() default "";

    int type() default 0;  // 0 导入导出，1，仅导入，2，仅导出

    int offset() default 0; //导出时偏移量

    String dicName() default ""; // 字典值

    String dateFormat() default "";

    Class<?> fieldType() default Class.class;
}
