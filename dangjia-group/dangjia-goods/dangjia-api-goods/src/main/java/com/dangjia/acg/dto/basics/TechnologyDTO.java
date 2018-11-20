package com.dangjia.acg.dto.basics;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Ruking.Cheng
 * @descrilbe TODO
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2018/9/20 上午10:47
 */
@Data
@ApiModel
public class TechnologyDTO {
    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("name")
    private String name;
    @ApiModelProperty("workerTypeId")
    private String workerTypeId;
    @ApiModelProperty("content")
    private String content;//内容
    @ApiModelProperty("createDate")
    private String createDate;
    @ApiModelProperty("modifyDate")
    private String modifyDate;
    @ApiModelProperty("image")
    private String image;//图片
}
