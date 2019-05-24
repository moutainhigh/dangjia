package com.dangjia.acg.dto.complain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComPlainStopDTO {
    private String workerTypeId;
    private String workerTypeName;
    private BigDecimal havaMoney;
}
