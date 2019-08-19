package com.dangjia.acg.util;

import java.awt.geom.Point2D;

/**
 * @author Ruking.Cheng
 * @descrilbe TODO
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/7/6 4:29 PM
 */
public class LocationUtils {
//    private static double EARTH_RADIUS = 6378.137;
    private static final double EARTH_RADIUS = 6371393; // 平均半径,单位：m

//    private static double rad(double d) {
//        return d * Math.PI / 180.0;
//    }

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
//        double radLat1 = rad(latitude1);
//        double radLat2 = rad(latitude2);
//        double a = radLat1 - radLat2;
//        double b = rad(longitude1) - rad(longitude2);
//        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
//                + Math.cos(radLat1) * Math.cos(radLat2)
//                * Math.pow(Math.sin(b / 2), 2)));
//        s = s * EARTH_RADIUS;
//        s = Math.round(s * 10000d) / 10000d;
//        s = s * 1000;

        Point2D pointDD = new Point2D.Double(longitude1,latitude1);
        Point2D pointXD = new Point2D.Double(longitude2,latitude2);
        return getDistance(pointDD, pointXD);
    }


    /**
     * 通过AB点经纬度获取距离
     * @param pointA A点(经，纬)
     * @param pointB B点(经，纬)
     * @return 距离(单位：米)
     */
    public static double getDistance(Point2D pointA, Point2D pointB) {
        // 经纬度（角度）转弧度。弧度用作参数，以调用Math.cos和Math.sin
        double radiansAX = Math.toRadians(pointA.getX()); // A经弧度
        double radiansAY = Math.toRadians(pointA.getY()); // A纬弧度
        double radiansBX = Math.toRadians(pointB.getX()); // B经弧度
        double radiansBY = Math.toRadians(pointB.getY()); // B纬弧度
        // 公式中“cosβ1cosβ2cos（α1-α2）+sinβ1sinβ2”的部分，得到∠AOB的cos值
        double cos = Math.cos(radiansAY) * Math.cos(radiansBY) * Math.cos(radiansAX - radiansBX)
                + Math.sin(radiansAY) * Math.sin(radiansBY);
        double acos = Math.acos(cos); // 反余弦值
        return EARTH_RADIUS * acos; // 最终结果
    }

    public static void main(String[] args) {
        double distance = getDistance( 28.228204,112.875586,
                28.221532,112.874022);
        System.out.println("距离" + distance + "米");
    }
}
