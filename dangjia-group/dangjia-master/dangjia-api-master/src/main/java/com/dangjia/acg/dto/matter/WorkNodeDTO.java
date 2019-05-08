package com.dangjia.acg.dto.matter;

import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2019/4/2 0002
 * Time: 19:31
 */
@Data
public class WorkNodeDTO {
    private String tecName; //工艺名
    private List<TechnologyRecordDTO> trList;

}
