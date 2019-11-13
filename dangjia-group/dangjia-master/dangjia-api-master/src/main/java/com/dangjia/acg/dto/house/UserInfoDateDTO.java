package com.dangjia.acg.dto.house;

import com.dangjia.acg.dto.repair.HouseProfitSummaryDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserInfoDateDTO {
    private String username;// 销售名称
    private String userMobile;// 销售手机号码
    private String name;// 业主名称
    private String mobile;// 业主手机号码
    private String memberId;// 业主id

    private String operatorName;// 设计师名称
    private String operatorMobile;// 设计师电话
    private String operatorId;// 设计师电话

    private String residential;//小区名
    private String building;//楼栋，后台客服填写
    private String unit;//单元号，后台客服填写
    private String number;//房间号，后台客服填写


    private String houseId;
    private int designerOk;
    private String houseName;//房子名称
    private String schedule;//进度
    private String nickName;//昵称
    private String image;// 平面图
    private String imageUrl;// 平面图URL
    private Integer decorationType;//haveimagestate
    private Integer visitState;//0待确认开工,1装修中,2休眠中,3已完工,4提前结束装修 5提前结束装修申请中
    private Double profit;// 总利润
    protected Date createDate;// 下单时间
    protected Date modifyDate;// 竣工时间
    protected Date constructionDate;// 修改日期
    private Integer showUpdata;//是否显示上传图片按钮，0否，1是
    private String storeName;// 归属分店
    protected Date startDate;//创建时间
    private String remarkInfo;//备注详情
    private Date remarkDate;//备注时间

}
