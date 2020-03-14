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



    private List<Map<String,Object>> lists;

    private String image;

    private String workName;

    private Integer hundred;


}
