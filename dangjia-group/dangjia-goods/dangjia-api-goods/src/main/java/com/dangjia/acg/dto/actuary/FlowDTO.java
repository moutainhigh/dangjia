package com.dangjia.acg.dto.actuary;

import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/16 0016
 * Time: 17:38
 */
@Data
public class FlowDTO {
    private String name;
    private List<FlowActuaryDTO> flowActuaryDTOList;
}
