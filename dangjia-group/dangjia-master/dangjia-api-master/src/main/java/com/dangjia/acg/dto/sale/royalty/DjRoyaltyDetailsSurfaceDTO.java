package com.dangjia.acg.dto.sale.royalty;

import com.dangjia.acg.modle.sale.royalty.DjRoyaltyDetailsSurface;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Entity;
import java.util.List;


@Data
@Entity
@ApiModel(description = "提成详情list")
@FieldNameConstants(prefix = "")
public class DjRoyaltyDetailsSurfaceDTO{


    @ApiModelProperty("提成详情lists")
    List<DjRoyaltyDetailsSurface> lists;

}
