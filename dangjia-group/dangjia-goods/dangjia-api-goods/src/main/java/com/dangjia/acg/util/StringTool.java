package com.dangjia.acg.util;

import com.github.pagehelper.util.StringUtil;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringTool {

    public static void getImages(String address, String[] imgArr, StringBuilder imgStr, StringBuilder imgUrlStr) {
        for (int i = 0; i < imgArr.length; i++) {
            if (i == imgArr.length - 1) {
                imgStr.append(address).append(imgArr[i]);
                imgUrlStr.append(imgArr[i]);
            } else {
                imgStr.append(address).append(imgArr[i]).append(",");
                imgUrlStr.append(imgArr[i]).append(",");
            }
        }
    }

    //取第一张图
    public static String getImage(String images, String imageLocal) {
        try {
            if (StringUtil.isNotEmpty(images)) {
                String[] imageArr = images.split(",");
                for (int i = 0; i < imageArr.length; i++) {
                    imageArr[i] = imageLocal + imageArr[i];
                }
                return StringUtils.join(imageArr, ",");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";//图片上传错误
        }
        return "";//暂无图片
    }

    public static String equalEmptyResult(String result) {
        if (result == null || result.isEmpty())
            return "";
        else if ("".equals(result))
            return "";
        else
            return result;

    }

    public static String equalUrlResult(String url) {
        if (url == null)
            return "";
        if ("".equals(url) || url.isEmpty())
            return "";
        else
            return "http://115.29.141.201:8080" + url;
    }

    public static boolean isFloatNum(String fnum) {
        Pattern pattern = Pattern.compile("\\d+(.\\d+)?");
        Matcher matcher = pattern.matcher(fnum);
        return matcher.matches();
    }

    public static boolean isDotBefore(double num, int index) {

        return index != String.valueOf(num).indexOf(".");
    }

    public static boolean isDotAfter(double num, int index) {

        return index > (String.valueOf(num).length()
                - String.valueOf(num).indexOf(".") - 1);
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static String getUserNameByJid(String jid) {
        if (!jid.contains("@"))
            return jid;
        return jid.substring(0, jid.indexOf("@"));
    }

    /**
     * 两个字符串拼接
     *
     * @param str1
     * @param str2
     * @return
     */
    public static String getStringMosaic(String str1, String str2) {
        if (str2.indexOf(str1) == -1) {
            StringBuffer buf = new StringBuffer();
            buf.append(str1);
            buf.append(str2);
            return buf.toString();
        } else {
            System.out.println("包含了就不处理了");
            return str2;
        }
    }

    /**
     * 获取取
     *
     * @return
     */
    public static Integer getStringTaskopreation(int taskopreation, int workertype) {
        return Integer.valueOf(String.valueOf(taskopreation).replace("4", String.valueOf(workertype)));
    }

    /**
     * 获取s
     *
     * @return
     */
    public static String getUrl(HttpServletRequest request) {
        String url = "https://" + request.getServerName() //服务器地址
                + ":"
                + request.getServerPort()           //端口号  
                + request.getContextPath();     //项目名称
        return url;
    }

}
