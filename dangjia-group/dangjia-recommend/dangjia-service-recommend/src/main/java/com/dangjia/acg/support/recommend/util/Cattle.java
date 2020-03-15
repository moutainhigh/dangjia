package com.dangjia.acg.support.recommend.util;

import com.alibaba.fastjson.JSON;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.recommend.LatticeContent;
import com.dangjia.acg.modle.recommend.RecommendTargetInfo;
import com.dangjia.acg.support.recommend.dto.RecommendComposeChunk;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @Description: 黄牛 干活的
 * @author: luof
 * @date: 2020-3-11
 */
public class Cattle {

    /** 声明日志 */
    private static Logger logger = LoggerFactory.getLogger(Cattle.class);

    /**
     * @Description: 过滤重复 第一次
     * @author: luof
     * @date: 2020-3-11
     */
    public static void filterRepeatOne(RecommendComposeChunk recommendComposeChunk){

        List<RecommendTargetInfo> browseGoodsRecommendList = recommendComposeChunk.getBrowseGoodsRecommendList();
        List<RecommendTargetInfo> browseCaseRecommendList = recommendComposeChunk.getBrowseCaseRecommendList();

        List<RecommendTargetInfo> browseCaseRecommendListCopy = filterRepeat(browseGoodsRecommendList, browseCaseRecommendList);
        int repeatNum = browseCaseRecommendListCopy.size() - browseCaseRecommendList.size();

        List<RecommendTargetInfo> totalRecommendList = new ArrayList<RecommendTargetInfo>();
        totalRecommendList.addAll(browseGoodsRecommendList);
        totalRecommendList.addAll(browseCaseRecommendListCopy);
        recommendComposeChunk.setTotalRecommendList(totalRecommendList);

        repeatNum += recommendComposeChunk.getLabelAttribRecommendNumber();
        recommendComposeChunk.setLabelAttribRecommendNumber(repeatNum);
    }

    /**
     * @Description: 过滤重复 第二次
     * @author: luof
     * @date: 2020-3-11
     */
    public static void filterRepeatTwo(RecommendComposeChunk recommendComposeChunk){

        List<RecommendTargetInfo> totalRecommendList = recommendComposeChunk.getTotalRecommendList();
        List<RecommendTargetInfo> labelAttribRecommendList = recommendComposeChunk.getLabelAttribRecommendList();

        List<RecommendTargetInfo> labelAttribRecommendListCopy = filterRepeat(totalRecommendList, labelAttribRecommendList);

        recommendComposeChunk.getTotalRecommendList().addAll(labelAttribRecommendListCopy);
    }

    /**
     * @Description: 过滤重复
     * @author: luof
     * @date: 2020-3-11
     */
    public static List<RecommendTargetInfo> filterRepeat(List<RecommendTargetInfo> sourceList, List<RecommendTargetInfo> noumenonList){

        List<RecommendTargetInfo> noumenonListCopy = new ArrayList<RecommendTargetInfo>();
        for( RecommendTargetInfo noumenon : noumenonList ){
            boolean repeat = false;
            for( RecommendTargetInfo source : sourceList ){
                if( noumenon.getId().equals(source.getId()) ){
                    repeat = true;
                    break;
                }
            }
            if( !repeat ){
                noumenonListCopy.add(noumenon);
            }
        }
        return noumenonListCopy;
    }

    /**
     * @Description: 得到条数 - 根据来源
     * @author: luof
     * @date: 2020-3-11
     */
    public static int getNumberBySource(RecommendComposeChunk recommendComposeChunk, int source){
        if( RecommendSource.browse_goods.getCode() == source ){
            return recommendComposeChunk.getBrowseGoodsRecommendNumber();
        }else if( RecommendSource.browse_case.getCode() == source ){
            return recommendComposeChunk.getBrowseCaseRecommendNumber();
        }else if( RecommendSource.label_attrib.getCode() == source ){
            return recommendComposeChunk.getLabelAttribRecommendNumber();
        }
        return 0;
    }

    /**
     * @Description: 加载数据处理
     * @author: luof
     * @date: 2020-3-11
     */
    public static void loadDataHandle(ServerResponse serverResponse,
                                      RecommendComposeChunk recommendComposeChunk,
                                      int source,
                                      int number){
        if( serverResponse.isSuccess() ){
            PageInfo<RecommendTargetInfo> pageResult = (PageInfo)serverResponse.getResultObj();
            if( RecommendSource.browse_goods.getCode() == source ){
                recommendComposeChunk.setBrowseGoodsRecommendList(pageResult.getList());
            }else if( RecommendSource.browse_case.getCode() == source ){
                recommendComposeChunk.setBrowseCaseRecommendList(pageResult.getList());
            }else if( RecommendSource.label_attrib.getCode() == source ){
                recommendComposeChunk.setLabelAttribRecommendList(pageResult.getList());
            }
            if( RecommendSource.browse_goods.getCode() == source || RecommendSource.browse_case.getCode() == source ){
                number -= pageResult.getList().size();
                number += recommendComposeChunk.getLabelAttribRecommendNumber();
                recommendComposeChunk.setLabelAttribRecommendNumber(number);
            }
        }else{
            List<RecommendTargetInfo> emptyList = new ArrayList<RecommendTargetInfo>();
            if( RecommendSource.browse_goods.getCode() == source ){
                recommendComposeChunk.setBrowseGoodsRecommendList(emptyList);
            }else if( RecommendSource.browse_case.getCode() == source ){
                recommendComposeChunk.setBrowseCaseRecommendList(emptyList);
            }else if( RecommendSource.label_attrib.getCode() == source ){
                recommendComposeChunk.setLabelAttribRecommendList(emptyList);
            }
            if( RecommendSource.browse_goods.getCode() == source || RecommendSource.browse_case.getCode() == source ){
                number += recommendComposeChunk.getLabelAttribRecommendNumber();
                recommendComposeChunk.setLabelAttribRecommendNumber(number);
            }
        }

    }

    /** 分解范围 */
    public static List<Integer> splitScope(int scope){

        List<Integer> values = new ArrayList<Integer>();

        String twoLadder = toTwoLadder(scope);
        int len = twoLadder.length();
        for( int i = 0; i < len; i++ ){
            if( twoLadder.charAt(i) == 49 ){
                values.add(twoValueList[i+(twoValueList.length-len)]);
            }
        }

        return values;
    }
    private static Integer[] twoValueList = new Integer[]{2048,1024,512,256,128,64,32,16,8,4,2,1};
    private static String toTwoLadder(int num){
        String a = "";//用字符串拼接
        while(num!=0) {//利用十进制转二进制除2法
            a=num%2+a;
            num=num/2;
        }
        return a;
    }

    /** 检查方格内容JSON串 */
    public static List<LatticeContent> checkContentListJsonStr(String contentListJsonStr){
        try{
            List<LatticeContent> latticeContentList = JSON.parseArray(contentListJsonStr, LatticeContent.class);
            for( LatticeContent content : latticeContentList ){

            }
            return latticeContentList;
        }catch(Exception e){
            logger.error("解析方格内容JSON串 失败:", e);
            return null;
        }
    }

    /** 判断 二进制包含 */
    public static boolean twoContain(int value, int values){

        String twoLadder1 = toTwoLadder(value);
        String twoLadder2 = toTwoLadder(values);
        int len1 = twoLadder1.length();
        int len2 = twoLadder2.length();

        if( len1 > len2 ){
            return false;
        }
        if( len1 < len2 ){
            int lack = len2 - len1;
            String ts = "";
            for( int i = 0; i < lack; i++ ){
                ts = "0" + ts;
            }
            twoLadder1 = ts + twoLadder1;
        }

        for( int i = 0; i < len2; i++ ){
            if( twoLadder1.charAt(i) == 49 && twoLadder2.charAt(i) == 49 ){
                return true;
            }
        }

        return false;
    }

    /** 判断 组合块完整 */
    public static boolean whole(List<String> codingNameList, int rows, int cols){

        // 行编号=key,所有的列编号=value
        Map<String, SortedSet<Integer>> rcsm = new HashMap<String, SortedSet<Integer>>();
        for( String codingName : codingNameList ){
            String[] rc = codingName.split("_");
//            System.out.println(JSON.toJSONString(codingName.split("_")));
            if( rcsm.get(rc[0]) != null ){
                rcsm.get(rc[0]).add(Integer.parseInt(rc[1]));
            }else{
                SortedSet<Integer> cs = new TreeSet<Integer>();
                cs.add(Integer.parseInt(rc[1]));
                rcsm.put(rc[0], cs);
            }
        }
//        System.out.println(JSON.toJSONString(rcsm));

        // 行 数量判断
        Set<String> keys = rcsm.keySet();
        if( keys.size() != rows ){
            return false;
        }

        if( rows > 1 ){

            // 排序一下
            Iterator<String> ki = keys.iterator();
            SortedSet<String> ks = new TreeSet<String>();
            while( ki.hasNext() ){
                ks.add(ki.next());
            }
//            System.out.println(JSON.toJSONString(ks));

            // 转成数组
            String[] kss = new String[rows];
            Iterator<String> ksi = ks.iterator();
            int index = 0;
            while( ksi.hasNext() ){
                kss[index++] = ksi.next();
            }
//            System.out.println(JSON.toJSONString(kss));

            // 判断 是否有序
            for( int i = 0; i< kss.length-1; i++ ){
//                System.out.println("kss["+i+"]="+kss[i].charAt(0)+",kss["+(i+1)+"]="+kss[i+1].charAt(0));
                if( kss[i].charAt(0)+1 != kss[i+1].charAt(0) ){
                    return false;
                }
            }
        }

        int index = 0, oneValue = 0;
        for( Map.Entry<String, SortedSet<Integer>> entry : rcsm.entrySet() ){

            // 列(每行数量) 数量判断
            SortedSet<Integer> cs = entry.getValue();
            if( cs.size() != cols ){
                return false;
            }

            if( cols > 1 ){

                // 转成数组
                Integer[] css = new Integer[cols];
                Iterator<Integer> csi = cs.iterator();
                int i = 0;
                while( csi.hasNext() ){
                    css[i++] = csi.next();
                }


                for( i = 0; i < css.length-1; i++ ){
//                    System.out.println("key["+entry.getKey()+"] css["+i+"]="+css[i]+",css["+(i+1)+"]="+css[i+1]);
                    // 判断 是否有序
                    if( css[i]+1 != css[i+1] ){
                        return false;
                    }
                    if( i == 0 ){
                        // 第一个是否相同
                        if( 0 == index ){
                            oneValue = css[i];
                        }else{
                            if( oneValue != css[i] ){
                                return false;
                            }
                        }
                    }
                }

            }else{
                // 第一个是否相同
                if( 0 == index ){
                    oneValue = cs.iterator().next();
                }else{
                    if( oneValue != cs.iterator().next() ){
                        return false;
                    }
                }
            }
            index++;
        }
        return true;
    }

    /** 判断 重复 */
    public static boolean repeat(List<String> codingNameList){

        List<String> listWithoutDuplicates = codingNameList.stream().distinct().collect(Collectors.toList());
        return codingNameList.size() == listWithoutDuplicates.size();
    }

    /** 判断 全部格子完整 */
    public static boolean wholeLatticeAll(List<String> codingNameList){

        // 行编号=key,所有的列编号=value
        Map<String, SortedSet<Integer>> rcsm = new HashMap<String, SortedSet<Integer>>();
        for( String codingName : codingNameList ){
            String[] rc = codingName.split("_");
//            System.out.println(JSON.toJSONString(codingName.split("_")));
            if( rcsm.get(rc[0]) != null ){
                rcsm.get(rc[0]).add(Integer.parseInt(rc[1]));
            }else{
                SortedSet<Integer> cs = new TreeSet<Integer>();
                cs.add(Integer.parseInt(rc[1]));
                rcsm.put(rc[0], cs);
            }
        }
//        System.out.println(JSON.toJSONString(rcsm));

        // 行 数量
        Set<String> keys = rcsm.keySet();
        int rows = keys.size();
        if( rows > 3 ){
            return false;
        }

        // 有无 第一行
        boolean existA = false;
        Iterator<String> ki = keys.iterator();
        while( ki.hasNext() ){
            if( ki.next().equals("A") ){
                existA = true;
                break;
            }
        }
        if( !existA ){
            return false;
        }

        return whole(codingNameList, rows, 4);
    }

    public static void main(String[] args){

//        List<String> codingNameList = new ArrayList<>(Arrays.asList("C_3","C_2","C_1","B_3","B_2","B_1","D_3","D_2","D_1"));
//        System.out.println(whole(codingNameList, 3, 3));

//        List<String> codingNameList = new ArrayList<>(Arrays.asList("C_2","C_2","C_1","B_3","B_2","B_1","D_3","D_2","D_1"));
//        System.out.println(repeat(codingNameList));

//        List<String> codingNameList = new ArrayList<>(
//                Arrays.asList(
//                        "A_3","A_2","A_1","A_4"
//                        ,"B_3","B_2","B_1","B_4"
//                        ,"C_3","C_2","C_1","C_4"
//                        ,"D_3","D_2","D_1","D_4"
//                ));
//        System.out.println(wholeLatticeAll(codingNameList));

        String contentListJsonStr = "[{\"image\":\"xxx.png\",\"areaName\":\"518宣传口号 1X4\",\"styleId\":\"3\",\"areaScope\":15,\"typeId\":\"1\",\"url\":\"www.baidu.com\"},{\"areaName\":\"年中大团购 2X3\",\"styleId\":\"6\",\"areaScope\":1904,\"contentId\":\"581547061582784400389\",\"typeId\":\"4\"},{\"areaName\":\"精品 1X1\",\"styleId\":\"1\",\"areaScope\":128,\"contentId\":\"1155304859918706327\",\"typeId\":\"2\"},{\"areaName\":\"超低价 1X1\",\"styleId\":\"1\",\"areaScope\":2048,\"contentId\":\"1155304859918706327\",\"typeId\":\"2\"}]";
        List<LatticeContent> contentList = checkContentListJsonStr(contentListJsonStr);
        System.out.println(JSON.toJSONString(contentList));
    }
}
