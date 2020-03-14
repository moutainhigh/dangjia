package com.dangjia.acg.dto.matter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2019/4/2 0002
 * Time: 19:31
 */
@Data
public class WorkNodeDTO {
    @ApiModelProperty("商品ID")
    private String productId;
    @ApiModelProperty("商品名称")
    private String productName;
    private List<TechnologyRecordDTO> trList;//节点列表

}
