package com.dangjia.acg.dto.house;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/13
 * Time: 9:42
 */
@Data
public class HouseChoiceCaseDTO {
    private String buildingNames;//楼盘名称

    private BigDecimal area;//面积

    private String style;//风格

    private BigDecimal cost;//费用

    private List<TextContentDTO> TextContentDTO=new ArrayList<>();//图文描述

}
