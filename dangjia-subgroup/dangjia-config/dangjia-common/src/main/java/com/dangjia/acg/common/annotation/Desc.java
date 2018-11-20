package com.dangjia.acg.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 实体类注解-字段描述
 *
 * @author QiYuXiang 2018/3/17
 */
@Target( {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Desc {

    /***
     * 字段描述
     * @return
     */
    public String value() default "未命名";

    /***
     * 枚举名
     * @return
     */
    public String enumName() default "";

    /***
     * 字典名
     * @return
     */
    public String dicName() default "";

}
