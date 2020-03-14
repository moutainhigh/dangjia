package com.dangjia.acg.dto.product;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
public class BrowseRecordDTO
{
    private String memberId;
    private String productId;
    private String visitsNum;
    private String vistsType;
    private Object object;
}
