package com.dangjia.acg.dto.supplier;

import com.dangjia.acg.common.model.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 11/10/2019
 * Time: 下午 2:17
 */
@Data
public class DjSupSupplierProductDTO extends BaseEntity {

    private String goodsId;
    private String goodsName;
    private String productId;
    private String productName;
    private String image;
    private String attributeIdArr;//标签id逗号分隔
    private Double price;//价格
    private Integer stock;//库存
    private Double supplyPrice;//供应价
    private Double salesPrice;//销售价
    private String isElevatorFee;//是否收取上楼费
    private String supplyRelationship;//供应关系 0:供应 1:停供
    private Double adjustPrice;//调后价格
    private Date adjustTime;//调价时间
}
