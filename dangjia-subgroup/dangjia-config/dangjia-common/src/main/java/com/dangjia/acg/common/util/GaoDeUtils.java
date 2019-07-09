package com.dangjia.acg.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dangjia.acg.common.model.PolygonBase;

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * 根据点的经纬度判断是否在多边形区域内
 */
public class GaoDeUtils {
 
    public static void main(String[] args) {
        // 被检测的经纬度点
        // 区域（多边形区域经纬度集合）
        String orderLocation="117.228117,31.830429";
        String partitionLocation = "[[113.13362,28.303023],[112.887801,28.221981],[113.018264,28.062137],[113.23387,28.151776],[113.13362,28.304232]]";
        System.out.println(isInPolygon(orderLocation, partitionLocation));
    }

    /**
     * 判断当前位置是否在多边形区域内
     * @param polygonBaseStr 当前点，逗号分隔
     * @param polygonBaseJson 区域点集合（JSON格式集合）
     * @return
     */
    public static boolean isInPolygon(String  polygonBaseStr,String polygonBaseJson){
        // 被检测的经纬度点
        String[] polygonBaseStrs=polygonBaseStr.split(",");
        PolygonBase polygonBase = new PolygonBase();
        polygonBase.setLocationx(Double.parseDouble(polygonBaseStrs[0]));
        polygonBase.setLocationy(Double.parseDouble(polygonBaseStrs[1]));
        // 商业区域（百度多边形区域经纬度集合）
        List<PolygonBase> pointList= new ArrayList<>();
        JSONArray strList = JSON.parseArray(polygonBaseJson);
        for (Object o : strList) {
            JSONArray point = (JSONArray)o;
            List<BigDecimal> points= JSONArray.parseArray(point.toJSONString(), BigDecimal.class);
            if(points.size()>1) {
                PolygonBase polygonBases = new PolygonBase();
                polygonBases.setLocationx(points.get(0).doubleValue());
                polygonBases.setLocationy(points.get(1).doubleValue());
                pointList.add(polygonBases);
            }
        }
        return isInPolygon(polygonBase, pointList);
    }

    /**
     * 判断当前位置是否在多边形区域内
     * @param orderLocation 当前点
     * @param polygonBases 区域点集合
     * @return
     */
    public static boolean isInPolygon(PolygonBase orderLocation,List<PolygonBase> polygonBases){
        Point2D.Double point = new Point2D.Double(orderLocation.getLocationx(), orderLocation.getLocationy());
        List<Point2D.Double> pointList= new ArrayList<>();
        for (PolygonBase polygonBase : polygonBases){
            Point2D.Double polygonPoint = new Point2D.Double(polygonBase.getLocationx(),polygonBase.getLocationy());
            pointList.add(polygonPoint);
        }
        return IsPtInPoly(point,pointList);
    }
    /**
     * 返回一个点是否在一个多边形区域内， 如果点位于多边形的顶点或边上，不算做点在多边形内，返回false
     * @param point
     * @param polygon
     * @return
     */
    public static boolean checkWithJdkGeneralPath(Point2D.Double point, List<Point2D.Double> polygon) {
        java.awt.geom.GeneralPath p = new java.awt.geom.GeneralPath();
        Point2D.Double first = polygon.get(0);
        p.moveTo(first.x, first.y);
        polygon.remove(0);
        for (Point2D.Double d : polygon) {
            p.lineTo(d.x, d.y);
        }
        p.lineTo(first.x, first.y);
        p.closePath();
        return p.contains(point);
    }

    /**
     * 判断点是否在多边形内，如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
     * @param point 检测点
     * @param pts   多边形的顶点
     * @return      点在多边形内返回true,否则返回false
     */
    public static boolean IsPtInPoly(Point2D.Double point, List<Point2D.Double> pts){

        int N = pts.size();
        boolean boundOrVertex = true; //如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
        int intersectCount = 0;//cross points count of x
        double precision = 2e-10; //浮点类型计算时候与0比较时候的容差
        Point2D.Double p1, p2;//neighbour bound vertices
        Point2D.Double p = point; //当前点

        p1 = pts.get(0);//left vertex
        for(int i = 1; i <= N; ++i){//check all rays
            if(p.equals(p1)){
                return boundOrVertex;//p is an vertex
            }

            p2 = pts.get(i % N);//right vertex
            if(p.x < Math.min(p1.x, p2.x) || p.x > Math.max(p1.x, p2.x)){//ray is outside of our interests
                p1 = p2;
                continue;//next ray left point
            }

            if(p.x > Math.min(p1.x, p2.x) && p.x < Math.max(p1.x, p2.x)){//ray is crossing over by the algorithm (common part of)
                if(p.y <= Math.max(p1.y, p2.y)){//x is before of ray
                    if(p1.x == p2.x && p.y >= Math.min(p1.y, p2.y)){//overlies on a horizontal ray
                        return boundOrVertex;
                    }

                    if(p1.y == p2.y){//ray is vertical
                        if(p1.y == p.y){//overlies on a vertical ray
                            return boundOrVertex;
                        }else{//before ray
                            ++intersectCount;
                        }
                    }else{//cross point on the left side
                        double xinters = (p.x - p1.x) * (p2.y - p1.y) / (p2.x - p1.x) + p1.y;//cross point of y
                        if(Math.abs(p.y - xinters) < precision){//overlies on a ray
                            return boundOrVertex;
                        }

                        if(p.y < xinters){//before ray
                            ++intersectCount;
                        }
                    }
                }
            }else{//special case when ray is crossing through the vertex
                if(p.x == p2.x && p.y <= p2.y){//p crossing over p2
                    Point2D.Double p3 = pts.get((i+1) % N); //next vertex
                    if(p.x >= Math.min(p1.x, p3.x) && p.x <= Math.max(p1.x, p3.x)){//p.x lies between p1.x & p3.x
                        ++intersectCount;
                    }else{
                        intersectCount += 2;
                    }
                }
            }
            p1 = p2;//next ray left point
        }

        if(intersectCount % 2 == 0){//偶数在多边形外
            return false;
        } else { //奇数在多边形内
            return true;
        }
    }
}