package com.dangjia.acg.dto.sale.store;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Data
public class GrabSheetDTO {
    private String houseId;//房子id
    private String customerId;
    private String name;//业主名字
    private Date createDate;//开始时间
    private String visitState;//阶段
    private String memberId;
    private String userId;
    private Integer phaseStatus;
    private String clueId;
}
