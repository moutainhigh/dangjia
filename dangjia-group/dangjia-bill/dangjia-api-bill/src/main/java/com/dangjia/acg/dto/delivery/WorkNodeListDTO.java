package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Date: 30/10/2019
 * Time: 下午 3:22
 */
@Data
public class WorkNodeListDTO {

    //1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工
    private List<Map<String,Object>> oneList;//1设计师
    private List<Map<String,Object>> twoList;//2精算师
    private List<Map<String,Object>> threeList;//3大管家
    private List<Map<String,Object>> fourList;//4拆除
    private List<Map<String,Object>> sixList;//6水电工
    private List<Map<String,Object>> sevenList;//7防水
    private List<Map<String,Object>> eightList;//8泥工
    private List<Map<String,Object>> nineList;//9木工
    private List<Map<String,Object>> tenList;//10油漆工




}
