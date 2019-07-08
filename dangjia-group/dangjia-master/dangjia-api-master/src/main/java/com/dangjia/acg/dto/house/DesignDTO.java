package com.dangjia.acg.dto.house;

import com.dangjia.acg.dto.repair.HouseProfitSummaryDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/10 0010
 * Time: 16:55
 */
@Data
public class DesignDTO {
    private String houseId;
    private int designerOk;
    private String residential;//小区名
    private String building;
    private String unit;
    private String number;
    private String houseName;//房子名称
    private String schedule;//进度
    private String name;//业主姓名
    private String nickName;//昵称
    private String mobile;// 电话
    private String image;// 平面图
    private String imageUrl;// 平面图URL
    private Integer decorationType;//haveimagestate
    private String operatorId;// 操作人ID
    private String operatorName;// 操作人名字
    private String operatorMobile;// 操作人电话
    private Integer visitState;//0待确认开工,1装修中,2休眠中,3已完工,4提前结束装修 5提前结束装修申请中
    private Double profit;// 总利润
    protected Date createDate;// 下单时间
    protected Date modifyDate;// 竣工时间
    protected Date constructionDate;// 修改日期
    private Integer showUpdata;//是否显示上传图片按钮，0否，1是
    private List<HouseProfitSummaryDTO> profitSummarys;// 利润集合

    private String storeName;// 归属分店
}
