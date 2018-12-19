package com.dangjia.acg.dto.member;

import lombok.Data;

import java.util.Date;

/**
 * author: Ronalcheng
 * Date: 2018/12/18 0018
 * Time: 19:11
 */
@Data
public class DetailDTO {
    private String workerDetailId;//流水id
    private String image;//图标
    private String name;
    private Date createDate;
    private String money;//钱
}
