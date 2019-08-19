package com.dangjia.acg.common.constants;



import com.dangjia.acg.common.model.ConfigBean;

/***
 * 系统默认常量配置/需在t_config重新配置 通过read方法读取
 *
 * @author QiYuXiang
 */
abstract public  class SysConfig {
    /**
     * start 应用类型
     */

    /**
     * 应用类型: 当家平台
     **/
    public static final Integer APP_TYPE = 0;

    /**
     * 应用类型: 装修
     **/
    public static final Integer APP_TYPE_ZX = 1;
    /**
     * 应用类型: 工匠
     **/
    public static final Integer APP_TYPE_GJ = 2;


    /**
     * API路径
     */
    public static final ConfigBean<String> DANGJIA_API_LOCAL = new ConfigBean<String>("DANGJIA_API_LOCAL",
            "http://172.16.30.95/", "API路径", APP_TYPE);


    /**
     * 图片路径
     */
    public static final ConfigBean<String> PUBLIC_APP_ADDRESS = new ConfigBean<String>("PUBLIC_DANGJIA_APP_ADDRESS",
            "http://172.16.30.95:7001/#/", "静态页面访问路径（APP）", APP_TYPE);
    /**
     * 图片路径
     */
    public static final ConfigBean<String> PUBLIC_WEB_ADDRESS = new ConfigBean<String>("PUBLIC_DANGJIA_WEB_ADDRESS",
            "http://172.16.30.95:7002/#/", "静态页面访问路径（WEB）", APP_TYPE);
    /**
     * 图片路径
     */
    public static final ConfigBean<String> DANGJIA_IMAGE_LOCAL = new ConfigBean<String>("DANGJIA_IMAGE_LOCAL",
            "http://172.16.30.95/", "图片路径", APP_TYPE);

    /** 公共静态文件访问地址 **/
    public static final ConfigBean<String> PUBLIC_DANGJIA_ADDRESS = new ConfigBean<String>("PUBLIC_DANGJIA_ADDRESS",
        "http://172.16.30.95/", "公共静态文件路径", APP_TYPE);

    /** 当家平台保存路径  /usr/wwwroot/dangjia/  **/
    public static final ConfigBean<String> PUBLIC_DANGJIA_PATH = new ConfigBean<String>("PUBLIC_DANGJIA_PATH",
        "D:/dangjia/", "当家平台保存路径", APP_TYPE);

    /** 保存文件暂时路径 **/
    public static final ConfigBean<String> PUBLIC_TEMPORARY_FILE_ADDRESS = new ConfigBean<String>("PUBLIC_TEMPORARY_FILE_ADDRESS",
        "temporary/", "保存文件暂时路径", APP_TYPE);


    /** 二维码保存目录 **/
    public static final ConfigBean<String> PUBLIC_QRCODE_PATH = new ConfigBean<String>("PUBLIC_QRCODE_PATH",
        "qrcode/", "二维码保存目录", APP_TYPE);

    public static final ConfigBean<String> INSURANCE_MONEY = new ConfigBean<String>("INSURANCE_MONEY",
            "100", "工人购买保险金额", APP_TYPE);



}
