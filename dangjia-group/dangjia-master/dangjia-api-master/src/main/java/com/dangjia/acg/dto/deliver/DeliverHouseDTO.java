package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.util.Date;

/**
 * author: Ronalcheng
 * Date: 2018/12/5 0005
 * Time: 16:29
 */
@Data
public class DeliverHouseDTO {

    private Date createDate;// 创建日期
    private String houseId;//房子ID
    private String storefrontId;//店铺ID
    private String addressId;//地址ID
    private String houseName;//详情地址
    private String memberId;//接收人ID
    private String name;//业主名字
    private String mobile;// 手机
    protected Date constructionDate;//开工时间
    private int sent;//已处理
    private int wait;//待处理
}
