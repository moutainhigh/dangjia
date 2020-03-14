package com.dangjia.acg.dto.pay;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * author: Ronalcheng
 * Date: 2018/11/7 0007
 * Time: 17:30
 */
@Data
public class WorkerDTO {

    private String houseWorkerId;//换人接口入参
    private String head;//头像
    private Integer workerType;//工种名
    private String workerTypeId;//工种名
    private String workerTypeName;//工种名
    private String workerTypeImage;
    private String workerTypeColor;
    private String workerId;//工人ID
    private String name;//姓名
    private String mobile;//电话号码
    private int change;//1可换人 0不可换人
}
