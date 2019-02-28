package com.dangjia.acg.dto.member;

import lombok.Data;

import java.util.Date;

/**
 * @author Ruking.Cheng
 * @descrilbe 贷款申请列表返回体
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/2/27 4:40 PM
 */
@Data
public class LoanDTO {
    private String id;
    private String loanId;
    private Date createDate;// 创建日期
    private Date modifyDate;// 修改日期
    private int dataStatus;
    private String name;//贷款人姓名
    private String memberId;//贷款人id
    private String memberMobile;//贷款人手机
    private String followUpId;//跟进人ID
    private String followUpName;//跟进人姓名
    private Integer state;//贷款状态：0:待处理，1:无意向，2:转到银行，3：已放款，4:无法放款
    private String stateDescribe;//贷款描述
    private String bankName;//银行名称
}
