package com.dangjia.acg.dto.sale.royalty;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Data
public class DjAreaMatchDTO {


    private Integer startSingle;//开始单
    private Integer overSingle;//结束单
    private Integer royalty;//每单提成
    private String buildingId;//楼栋id
    private String buildingName;//楼栋名称
    private String villageId;//小区id
    private String villageName;//小区名称
}
