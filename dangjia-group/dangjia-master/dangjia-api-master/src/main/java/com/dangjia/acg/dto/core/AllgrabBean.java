package com.dangjia.acg.dto.core;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.util.Date;
import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 抢单列表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2018/12/1 10:22 AM
 */
@Data
public class AllgrabBean {
    private String houseName;
    private String houseFlowId;//任务id
    private String square;//面积
    private Integer type;//0=装修(人工费用+施工图纸+其他) 1=体验(体验商品)  2=维修（维修商品）
    private Integer orderType;//0=无 1=新单 2=二手
    private String houseMember;//	业主名称
    private String workerTypeId;//	工种类型的id
    private String workertotal;//价格
    private String releaseTime;//发布时间
    private String butType;//按钮状态  0=抢单  1=已被抢单（灰色）
    private Long countDownTime;//倒计时（可抢单时间）
    private Date createDate;// 创建日期


    private Integer schedulingDay;//预计工期天数
    private Date startDate;//管家排期的开工开始时间
    private Date endDate;//管家排期的阶段结束/整体（仅拆除）结束时间


    private String latitude;//纬度
    private String longitude;//经度

    private List goodsData;//体验/维保商品数据
}
