package com.dangjia.acg.dto.actuary;

import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/15 0015
 * Time: 20:09
 */
@Data
public class FlowDetailsDTO {
    private String name;//工序
    private List<DetailsDTO> detailsDTOList;
}
