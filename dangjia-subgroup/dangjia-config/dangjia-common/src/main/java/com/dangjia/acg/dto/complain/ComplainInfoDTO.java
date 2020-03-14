package com.dangjia.acg.dto.complain;

import lombok.Data;

import java.util.List;


/**
 * author: ljl
 */
@Data
public class ComplainInfoDTO {

    private List<String> changeList;//更换原因
    private List<String> imageList;//图片
    private List<String> imageURLList;//图片(全路径)
    private String rejectReason;//驳回原因
    private String houseId;//工地ID
    private Integer status;//处理状态.-1:无。0:待处理。1.驳回。2.接受。

}
