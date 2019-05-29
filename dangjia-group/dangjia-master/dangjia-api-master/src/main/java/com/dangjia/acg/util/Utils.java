package com.dangjia.acg.util;

import com.dangjia.acg.dto.core.ButtonListBean;

/**
 * @author Ruking.Cheng
 * @descrilbe 公共其他工具方法
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/5/28 6:12 PM
 */
public class Utils {

    /**
     * 获取按钮对象
     *
     * @param name 按钮名称
     * @param type 按钮类型
     * @return 按钮对象
     */
    public static ButtonListBean getButton(String name, int type) {
        return getButton(name, null, type);
    }

    /**
     * 获取按钮对象
     *
     * @param name 按钮名称
     * @param url  跳转的URL
     * @param type 按钮类型
     * @return 按钮对象
     */
    public static ButtonListBean getButton(String name, String url, int type) {
        ButtonListBean buttonListBean = new ButtonListBean();
        buttonListBean.setButtonType(type);
        buttonListBean.setUrl(url);
        buttonListBean.setButtonTypeName(name);
        return buttonListBean;
    }
}
