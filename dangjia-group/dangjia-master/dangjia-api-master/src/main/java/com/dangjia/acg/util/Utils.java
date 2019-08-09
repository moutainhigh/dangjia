package com.dangjia.acg.util;

import com.dangjia.acg.dto.core.ButtonListBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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


    public static String generateWord() {
        String[] beforeShuffle = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z"};
        List<String> list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (String aList : list) {
            sb.append(aList);
        }
        String afterShuffle = sb.toString();
        return afterShuffle.substring(5, 9);
    }

    /**
     * 客户详情页
     *
     * //TODO 检查
     *
     * @param memberId    客户ID
     * @param clueId
     * @param phaseStatus
     * @param stage
     * @return
     */
    public static String getCustomerDetails(String memberId, String clueId, Integer phaseStatus, String stage) {
        return String.format("customerDetails?title=客户详情&memberId=%s&clueId=%s&phaseStatus=%s&stage=%s",
                memberId, clueId, phaseStatus + "", stage);
    }

    /**
     * 随机获取图片
     *
     * @return
     */
    public static String getHead() {
        String[] heads = {"qrcode/img_tx01.png", "qrcode/img_tx02.png", "qrcode/img_tx03.png", "qrcode/img_tx04.png", "qrcode/img_tx05.png"};
        Random r = new Random();
        return heads[r.nextInt(heads.length)];
    }

}
