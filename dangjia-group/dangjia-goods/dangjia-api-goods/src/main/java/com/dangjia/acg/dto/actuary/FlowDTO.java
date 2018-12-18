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
    private int type;//type 人工1 材料2 服务3
    private String sumTotal;//合计
    private List<FlowActuaryDTO> flowActuaryDTOList;
}
