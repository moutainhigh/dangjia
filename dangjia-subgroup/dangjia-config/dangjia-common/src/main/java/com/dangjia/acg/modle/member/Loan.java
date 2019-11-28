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
 * @descrilbe 贷款申请列表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/2/27 11:14 AM
 */
@Data
@Entity
@Table(name = "dj_member_loan")
@ApiModel(description = "贷款申请列表")
@FieldNameConstants(prefix = "")
public class Loan extends BaseEntity {

    @Column(name = "name")
    @Desc(value = "贷款人姓名")
    @ApiModelProperty("贷款人姓名")
    private String name;

    @Column(name = "member_id")
    @Desc(value = "贷款人id")
    @ApiModelProperty("贷款人id")
    private String memberId;

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

    @Column(name = "bank_name")
    @Desc(value = "银行名称")
    @ApiModelProperty("银行名称")
    private String bankName;

    @Column(name = "city_id")
    @Desc(value = "城市id")
    @ApiModelProperty("城市id")
    private String cityId;

    @Column(name = "city_name")
    @Desc(value = "城市名")
    @ApiModelProperty("城市名")
    private String cityName;

}
