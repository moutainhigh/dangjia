package com.dangjia.acg.dto.product;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.List;


@Data
@FieldNameConstants(prefix = "")
public class BasicsGoodArrDTO {
    List<BasicsGoodDTO> List;
    private Double priceArr;
}
