package com.dangjia.acg.dto.member;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;

/**
 * 大管家信息
 */
@Data
@ApiModel(description = "大管家信息")
@Entity
public class WorkerTypeDTO {

    @ApiModelProperty("大管家姓名")
    private String name;

    @ApiModelProperty("手机号码")
    private String mobile;

    @ApiModelProperty("图片")
    private String head;

    @ApiModelProperty("1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工")
    private Integer type;

    @ApiModelProperty("工种ID")
    private String workerTypeId;

    @ApiModelProperty("施工状态，0未开始 ，1阶段完工通过，2整体完工通过，3待交底，4施工中，5收尾施工")
    private String workSteta;



    public String getTypeName(){
        if(null != getType() &&getType() == 1){
            return "设计师";
        }else if(null != getType() &&getType() == 2){
            return "精算师";
        }else if(null != getType() &&getType() == 3){
            return "大管家";
        }else if(null != getType() &&getType() == 4){
            return "拆除";
        }else if(null != getType() &&getType() == 5){
            return "打孔";
        }else if(null != getType() &&getType() == 6){
            return "水电工";
        }else if(null != getType() &&getType() == 7){
            return "防水";
        }else if(null != getType() &&getType() == 8){
            return "泥工";
        }else if(null != getType() &&getType() == 9){
            return "木工";
        }else if(null != getType() &&getType() == 10){
            return "油漆工";
        }else{
            return "";
        }
    }
}
