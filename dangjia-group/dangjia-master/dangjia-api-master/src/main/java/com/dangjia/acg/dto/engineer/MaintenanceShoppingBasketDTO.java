package com.dangjia.acg.dto.engineer;

import lombok.Data;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 8/1/2020
 * Time: 下午 9:18
 */
@Data
public class MaintenanceShoppingBasketDTO {

    private String id;//类别id

    private String name;//类别名称

    private List<DjMaintenanceRecordProductDTO> djMaintenanceRecordProductDTOS;
}
