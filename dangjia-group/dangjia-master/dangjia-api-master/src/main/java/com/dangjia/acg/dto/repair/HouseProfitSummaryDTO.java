package com.dangjia.acg.dto.repair;

import lombok.Data;

import java.util.Date;

@Data
public class HouseProfitSummaryDTO {

    private String number;
    private String info;
    private String type;
    private Double money;
    private String plus;
    private Date date;
}
