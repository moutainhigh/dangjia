package com.dangjia.acg.dto.other;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2019/1/4 0004
 * Time: 10:05
 */
@Data
public class HouseDetailsDTO {
    private String image;
    private String houseName;
    private List<String> dianList;
    private List<Map<String,Object>> mapList;
}
