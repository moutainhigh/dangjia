package com.dangjia.acg.dto.house;

import com.dangjia.acg.dto.repair.HouseProfitSummaryDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * author: fzh
 * Date: 2019/12/11
 * Time: 16:55
 */
@Data
public class HouseOrderDetailDTO {
    private String houseId;//房子ID
    private String image;//图片
    private String imageUrl;//图片地址
    private String productName;//商品名称
    private Double price;// 单价
    private Double totalPrice;//总价
    private Double shopCount;// 购买数量
    private String productTemplateId;// 商品模板ID
    private  String valueIdArr;//商品规格ID
    private String valueNameArr;//商品规格名称
    private String unitId;//单位ID
    private String unitName;//单位名称

}
