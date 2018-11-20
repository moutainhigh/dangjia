package com.dangjia.acg.dto.actuary;

import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/15 0015
 * Time: 19:55
 */
@Data
public class ActuaryDetailsDTO {
    private String houseId;
    private List<FlowDetailsDTO> flowDetailsDTOList;
}
