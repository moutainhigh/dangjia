package com.dangjia.acg.dto.deliver;

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
    private Date createDate;// 创建日期
    private Date sendTime; //发货时间
    private Date submitTime;//下单时间
    private Date modifyDate;//收货时间
    private Double totalAmount;
    private Integer tol;//多少种商品

    private String name;//一件名
    private String image;//一张图
    private Integer supState;//0:大管家不可收货;1:大管家可收货
    private List<SplitDeliverItemDTO> splitDeliverItemDTOList;

    private String supId;//供货商id
    private String supMobile;//供货商电话
    private String supName;//供应商名字

    private List<String> imageList;
}
