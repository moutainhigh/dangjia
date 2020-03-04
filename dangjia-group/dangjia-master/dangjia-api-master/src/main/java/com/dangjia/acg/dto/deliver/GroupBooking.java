package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2020/3/4
 * Time: 19:50
 */
@Data
public class GroupBooking {

    private String productId;
    private String image;
    private String productName;
    private Integer spellGroup;//拼团人数
    private Double rushPurchasePrice;//拼团价
    private String unitName;//单位
    private List<Map<String,Object>> list;
    private Date orderGenerationTime;//订单生成时间
    private Integer shortProple;//还差人数
    private Double sellPrice;//销售价
}
