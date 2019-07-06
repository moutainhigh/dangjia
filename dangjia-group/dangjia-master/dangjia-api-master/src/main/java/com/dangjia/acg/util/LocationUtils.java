package com.dangjia.acg.util;

/**
 * @author Ruking.Cheng
 * @descrilbe TODO
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/7/6 4:29 PM
 */
public class LocationUtils {
    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 通过经纬度获取距离(单位：米)
     *
     * @param latitude1
     * @param longitude1
     * @param latitude2
     * @param longitude2
     * @return 距离
     */
    public static double getDistance(double latitude1, double longitude1, double latitude2,
                                     double longitude2) {
        double radLat1 = rad(latitude1);
        double radLat2 = rad(latitude2);
        double a = radLat1 - radLat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;
        return s;
    }

    public static void main(String[] args) {
        double distance = getDistance(34.2675560000, 108.9534750000,
                34.2464320000, 108.9534750000);
        System.out.println("距离" + distance + "公里");
    }
}
