package com.dangjia.acg.common.model;


/***
 * 系统变量实体封装类
 *
 * @param <T>
 * @author QiYuXiang
 */
public class ConfigBean<T> {

    /**
     * 变量名
     */
    public String name;

    /***
     * 默认值
     */
    public Object defaultValue;
    /**
     * 详细描述
     **/
    public String desc;

    /**
     * 应用
     */
    public Integer appType;

    public ConfigBean(String name, T defaultValue, String desc) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.desc = desc;
    }

    public ConfigBean(String name, T defaultValue, String desc, Integer appType) {

        this.name = name;
        this.defaultValue = defaultValue;
        this.desc = desc;
        this.appType = appType;
    }

}
