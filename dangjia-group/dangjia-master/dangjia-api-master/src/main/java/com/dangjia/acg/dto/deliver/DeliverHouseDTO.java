package com.dangjia.acg.dto.deliver;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * author: Ronalcheng
 * Date: 2018/12/5 0005
 * Time: 16:29
 */
@Data
public class DeliverHouseDTO {

    private Date createDate;// 创建日期
    private String houseId;
    private String houseName;
    private String name;//业主名字
    private String mobile;// 手机
    protected Date constructionDate;//开工时间
    private int sent;
    private int wait;
}
