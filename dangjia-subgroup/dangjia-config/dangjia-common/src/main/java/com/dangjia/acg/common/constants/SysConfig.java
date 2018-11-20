package com.dangjia.acg.common.constants;



import com.dangjia.acg.common.model.ConfigBean;

/***
 * 系统默认常量配置/需在t_config重新配置 通过read方法读取
 *
 * @author QiYuXiang
 */
abstract public  class SysConfig {

    /**
     * 图片路径我本机
     */
    public static final ConfigBean<String> DANGJIA_IMAGE_LOCAL = new ConfigBean<String>("DANGJIA_IMAGE_LOCAL",
            "http://172.16.30.97/", "图片路径我本机", DictionaryConstants.APP_TYPE);

    /**
     * APP页面路径
     */
    public static final ConfigBean<String> PUBLIC_DANGJIA_APP_ADDRESS = new ConfigBean<String>("PUBLIC_DANGJIA_APP_ADDRESS",
            "http://172.16.30.95/#/", "APP页面路径", DictionaryConstants.APP_TYPE);

    /** 获取微信access_token **/
    public static final ConfigBean<String> WECHAT_ACCESS_TOKEN_URL = new ConfigBean<String>("WECHAT_ACCESS_TOKEN_URL",
            "https://api.weixin.qq.com/cgi-bin/token", "获取微信access_token");

    public static final ConfigBean<String> WECHAT_ACCESS_TICKET_URL = new ConfigBean<String>("WECHAT_ACCESS_TICKET_URL",
        "https://api.weixin.qq.com/cgi-bin/ticket/getticket", "获取微信分享链接");
    /** 发送微信模板消息 **/
    public static final ConfigBean<String> WECHAT_TEMPLATE_MSG_URL = new ConfigBean<String>("WECHAT_TEMPLATE_MSG_URL",
            "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=ACCESS_TOKEN", "发送微信模板消息");

    /** 生成小程序二维码 **/
    public static final ConfigBean<String> WECHAT_QR_CODE_URL = new ConfigBean<String>("WECHAT_QR_CODE_URL",
            "https://api.weixin.qq.com/wxa/getwxacode?access_token=ACCESS_TOKEN", "生成小程序二维码");



    /** 保存活动导航图片url **/
    public static final ConfigBean<String> CLASS_ROOM_ACTIVITY_NAVIGATION = new ConfigBean<String>("CLASS_ROOM_ACTIVITY_NAVIGATION",
        "activityNavigation/", "活动导航图片url", DictionaryConstants.APP_TYPE_ZX);

    /** 公共静态文件访问地址 **/
    public static final ConfigBean<String> PUBLIC_DANGJIA_ADDRESS = new ConfigBean<String>("PUBLIC_DANGJIA_ADDRESS",
        "http://172.16.30.95/", "公共静态文件路径", DictionaryConstants.APP_TYPE);

    /** 当家平台保存路径  /usr/wwwroot/dangjia/  **/
    public static final ConfigBean<String> PUBLIC_DANGJIA_PATH = new ConfigBean<String>("PUBLIC_DANGJIA_PATH",
        "D:/dangjia/", "当家平台保存路径", DictionaryConstants.APP_TYPE);

    /** 保存文件暂时路径 **/
    public static final ConfigBean<String> PUBLIC_TEMPORARY_FILE_ADDRESS = new ConfigBean<String>("PUBLIC_TEMPORARY_FILE_ADDRESS",
        "temporary/", "保存文件暂时路径", DictionaryConstants.APP_TYPE);



    /** 按钮图片保存路径 **/
    public static final ConfigBean<String> PUBLIC_BUTTON_IMG_PATH = new ConfigBean<String>("PUBLIC_BUTTON_IMG_PATH",
        "button/", "按钮图片保存路径", DictionaryConstants.APP_TYPE);

    /** 二维码保存目录 **/
    public static final ConfigBean<String> PUBLIC_QRCODE_PATH = new ConfigBean<String>("PUBLIC_QRCODE_PATH",
        "qrcode/", "二维码保存目录", DictionaryConstants.APP_TYPE);




    /** 搜索引擎查询字段 **/
    public static final ConfigBean<String []> ES_LIKE_FIELD= new ConfigBean<String []>("ES_LIKE_FIELD",
        new String[]{"teacherName","title","curriculumName","activityTypeName"}, "搜索引擎查询字段", DictionaryConstants.APP_TYPE_GJ);


    public static final ConfigBean<String> WECHAT_RETURN_URL = new ConfigBean<String>("WECHAT_RETURN_URL",
        "https://store.shiguangkey.com/classroom/authorizationCallback", "微信html授权回调地址");

    public static final ConfigBean<String> WECHAT_TOKEN_URL = new ConfigBean<String>("WECHAT_TOKEN_URL",
        "https://api.weixin.qq.com/sns/oauth2/access_token", "微信回调");

    public static final ConfigBean<String> WECHAT_USERINFO_URL = new ConfigBean<String>("WECHAT_USERINFO_URL",
        "https://api.weixin.qq.com/sns/userinfo", "微信获取用户信息");


}
