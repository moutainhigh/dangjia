package com.dangjia.acg.dto.house;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class HouseWorkDTO {
    private String workName;
    private Date modifyDate;// 修改日期
    private String phone;
    private BigDecimal haveMoney;//已获工钱
    private String workerId;
}
