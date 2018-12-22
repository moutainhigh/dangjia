package com.dangjia.acg.dto.deliver;

import com.dangjia.acg.modle.deliver.OrderSplitItem;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/5 0005
 * Time: 20:37
 */
@Data
public class SplitDeliverDTO {

    private String splitDeliverId;
    private int shipState;//配送状态
    private String number;
    private Date sendTime; //发货时间
    private Date submitTime;//下单时间
    private Date modifyDate;//收货时间
    private Double totalAmount;
    private int tol;//多少种商品

    private String name;//一件名
    private String image;//一张图
    private Integer supState;//0:大管家不可收货;1:大管家可收货
    private List<OrderSplitItem> orderSplitItemList;
}
