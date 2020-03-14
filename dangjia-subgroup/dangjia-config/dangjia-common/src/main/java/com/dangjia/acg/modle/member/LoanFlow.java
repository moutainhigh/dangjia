package com.dangjia.acg.modle.member;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Ruking.Cheng
 * @descrilbe 贷款操作列表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/2/27 11:14 AM
 */
@Data
@Entity
@Table(name = "dj_member_loan_flow")
@ApiModel(description = "贷款操作列表")
@FieldNameConstants(prefix = "")
public class LoanFlow extends BaseEntity {

    @Column(name = "loan_id")
    @Desc(value = "贷款id")
    @ApiModelProperty("贷款id")
    private String loanId;

    @Column(name = "follow_up_id")
    @Desc(value = "跟进人ID")
    @ApiModelProperty("跟进人ID")
    private String followUpId;

    @Column(name = "state")
    @Desc(value = "贷款状态：0:待处理，1:无意向，2:转到银行，3：已放款，4:无法放款")
    @ApiModelProperty("贷款状态：0:待处理，1:无意向，2:转到银行，3：已放款，4:无法放款")
    private Integer state;

    @Column(name = "state_describe")
    @Desc(value = "贷款描述")
    @ApiModelProperty("贷款描述")
    private String stateDescribe;

}
