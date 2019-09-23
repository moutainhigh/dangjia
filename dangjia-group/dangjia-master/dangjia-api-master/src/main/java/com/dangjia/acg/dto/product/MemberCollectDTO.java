package com.dangjia.acg.dto.product;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
@Data
public class MemberCollectDTO {
    private String memberId;
    private String houseId;
    private String conditionType;
    private Object object;
}
