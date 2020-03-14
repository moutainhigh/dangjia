package com.dangjia.acg.dto.supplier;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 19/11/2019
 * Time: 下午 5:46
 */
@Data
public class AccountFlowRecordDTO {
    private Integer state;
    private String houseOrderId;
    private Double money;//实拿
    private Date createDate;// 结算日期
    private String reason;
    private Integer depositeState;//0未处理,1同意 2不同意(驳回)
    private String image;
    private String memo ;//附言
}
