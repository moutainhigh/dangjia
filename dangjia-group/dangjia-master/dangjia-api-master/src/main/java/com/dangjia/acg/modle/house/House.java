package com.dangjia.acg.modle.house;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.apache.http.util.TextUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实体类 - 房间
 */
@Data
@Entity
@Table(name = "dj_house")
@ApiModel(description = "房子")
@FieldNameConstants(prefix = "")
public class House extends BaseEntity {

    @Column(name = "member_id")
    @Desc(value = "用户ID")
    @ApiModelProperty("用户ID")
    private String memberId;//memberid

    @Column(name = "city_name")
    @Desc(value = "城市名")
    @ApiModelProperty("城市名")
    private String cityName;//

    @Column(name = "city_id")
    @Desc(value = "城市id")
    @ApiModelProperty("城市id")
    private String cityId;//cityid

    @Column(name = "residential")
    @Desc(value = "小区名")
    @ApiModelProperty("小区名")
    private String residential;//

    @Column(name = "village_id")
    @Desc(value = "小区ID")
    @ApiModelProperty("小区ID")
    private String villageId;//villageid

    @Column(name = "modeling_layout_id")
    @Desc(value = "户型id")
    @ApiModelProperty("户型id")
    private String modelingLayoutId;//layoutid

    @Column(name = "building")
    @Desc(value = "楼栋，后台客服填写")
    @ApiModelProperty("楼栋，后台客服填写")
    private String building;//

    @Column(name = "unit")
    @Desc(value = "单元号，后台客服填写")
    @ApiModelProperty("单元号，后台客服填写")
    private String unit;//

    @Column(name = "number")
    @Desc(value = "房间号，后台客服填写")
    @ApiModelProperty("房间号，后台客服填写")
    private String number;//housenumber

    @Column(name = "square")
    @Desc(value = "外框面积，后台客服填写,计算面积")
    @ApiModelProperty("外框面积，后台客服填写,计算面积")
    private BigDecimal square;//

    @Column(name = "build_square")
    @Desc(value = "建筑面积，跟户型一致")
    @ApiModelProperty("建筑面积，跟户型一致")
    private BigDecimal buildSquare;//buildsquare

    @Column(name = "refer_house_id")
    @Desc(value = "参考房子id")
    @ApiModelProperty("参考房子id")
    private String referHouseId;//

    @Column(name = "style")
    @Desc(value = "装修风格,存字符")
    @ApiModelProperty("装修风格,存字符")
    private String style;

    @Column(name = "type")
    @Desc(value = "默认0，1用户点击了开始装修，2代表老用户,3用户自己撤回")    //迁移数据时设置老用户
    @ApiModelProperty("默认0，1用户点击了开始装修，2代表老用户,3用户自己撤回")
    private Integer type;

    @Column(name = "show_house")
    @Desc(value = "是否展示 0不展示，1展示,默认0")
    @ApiModelProperty("是否展示 0不展示，1展示,默认0")
    private Integer showHouse;//showhouse

    @Column(name = "site_display")
    @Desc(value = "是否在施工现场展示（默认展示）：0：展示，1：不展示")
    @ApiModelProperty("是否在施工现场展示（默认展示）：0：展示，1：不展示")
    private Integer siteDisplay;//site_display

    @Column(name = "task_number")
    @Desc(value = "未处理任务数,默认生成为0")
    @ApiModelProperty("未处理任务数,默认生成为0")
    private Integer taskNumber;//tasknumber

    @Column(name = "visit_state")
    @Desc(value = "0待确认开工,1装修中,2休眠中,3已完工")
    @ApiModelProperty("0待确认开工,1装修中,2休眠中,3已完工")
    private Integer visitState;//1开工，2有意向，3无装修需求，4恶意操作，默认为0
    /**
     * 发送设计图业主
     * 设计状态:
     * 0=未确定设计师
     * 4=设计待抢单
     * 1=已支付-设计师待量房
     * 9=量房图确认，设计师待发平面图
     * 5=平面图发给业主
     * 6=平面图审核不通过
     * 7=通过平面图待发施工图
     * 2=已发给业主施工图
     * 8=施工图片审核不通过
     * 3=施工图(全部图)审核通过
     * 。。。。。。。。。。。。。。。。。⦧--6。。⦧--8
     * 远程设计流程：0---4---1---9---5---7---2---3
     *
     * 。。。。。。。。。。。。⦧--6。。⦧--8
     * 自带设计流程：0---1---5---7---2---3
     */
    @Column(name = "designer_ok")
    @Desc(value = "设计状态,默认0未确定设计师,4有设计抢单待支付,1已支付设计师待发平面图,5平面图发给业主,6平面图审核不通过,7通过平面图待发施工图,2已发给业主施工图" +
            ",8施工图片审核不通过,3施工图(全部图)审核通过")
    @ApiModelProperty("设计状态,0未确定设计师,4设计待抢单,1已支付-设计师待量房,9量房图发给业主,5平面图发给业主,6平面图审核不通过,7通过平面图待发施工图," +
            "2已发给业主施工图,8施工图片审核不通过,3施工图(全部图)审核通过"
    )
    private Integer designerOk;

    /**
     * 。。。。。。。。。。。。。。。。⦧--4
     * 精算状态：0---5---1--- -1 ---2---3
     */
    @Column(name = "budget_ok")
    @Desc(value = "精算状态:-1已精算没有发给业主,默认0未开始,1已开始精算,2已发给业主,3审核通过,4审核不通过,5业主待支付")
    @ApiModelProperty("精算状态:-1已精算没有发给业主,默认0未开始,1已开始精算,2已发给业主,3审核通过,4审核不通过,5业主待支付")
    private Integer budgetOk;

    @Column(name = "decoration_type")
    @Desc(value = "装修类型: 0表示没有开始，1远程设计，2自带设计，3共享装修")
    @ApiModelProperty("装修类型: 0表示没有开始，1远程设计，2自带设计，3共享装修")
    private Integer decorationType;//haveimagestate

    @Column(name = "record_type")
    @Desc(value = "记录用户装修方式")
    @ApiModelProperty("记录用户装修方式")
    private Integer recordType;//recordHaveimagestate

    @Column(name = "have_complete")
    @Desc(value = "装修完成，0未完成，1已完成")
    @ApiModelProperty("装修完成，0未完成，1已完成")
    private Integer haveComplete;//havecomplete

    @Column(name = "image")
    @Desc(value = "放一张设计完成图")
    @ApiModelProperty("放一张设计完成图")
    private String image;//

    @Column(name = "money")
    @Desc(value = "项目总钱")
    @ApiModelProperty("项目总钱")
    private BigDecimal money;//

    @Column(name = "pause")
    @Desc(value = "施工状态: 0正常,1暂停")
    @ApiModelProperty("施工状态: 0正常,1暂停")
    private Integer pause;//

    @Column(name = "again")
    @Desc(value = "第几套房产")
    @ApiModelProperty("第几套房产")
    private Integer again;

    @Column(name = "house_type")
    @Desc(value = "装修的房子类型0：新房；1：老房")
    @ApiModelProperty("装修的房子类型0：新房；1：老房")
    private Integer houseType;

    @Column(name = "drawings")
    @Desc(value = "有无图纸0：无图纸；1：有图纸")
    @ApiModelProperty("有无图纸0：无图纸；1：有图纸")
    private Integer drawings;

    @Column(name = "is_select")
    @Desc(value = "是否选中当前房产0:未选中,1选中")
    @ApiModelProperty("是否选中当前房产0:未选中,1选中")
    private Integer isSelect;

    @Column(name = "liang_dian")
    @Desc(value = "亮点标签")
    @ApiModelProperty("亮点标签")
    private String liangDian;

    @Column(name = "custom_sort")
    @Desc(value = "自定义房子工序顺序 5,4,3逗号分隔")
    @ApiModelProperty("自定义房子工序顺序 5,4,3逗号分隔")
    private String customSort;

    @Column(name = "work_deposit_id")
    @Desc(value = "结算比例ID")
    @ApiModelProperty("结算比例ID")
    private String workDepositId;


    public House() {

    }

    public House(boolean isIni) {
        if (isIni) {
            this.type = 1;//0默认，1用户点击了开始装修，2代表老用户,默认生成 为0
            this.visitState = 0;//默认
            this.showHouse = 0;// 是否展示 0展示，1不展示,默认生成 为0
            this.taskNumber = 0;// 未处理任务数,默认生成为0
            this.designerOk = 0;// 设计师操作 ,0未确定设计师,1已确定设计师,2已发给业主,3审核通过,默认生成 为0
            this.budgetOk = 0;// 精算操作// ，-1已经精算，但是没有发给业主,0未刚才这一环节,1已开始精算,2已发给业主,3审核通过,默认生成为0
            this.decorationType = 0;//装修类型: 0表示没有开始，1远程设计，2自带设计，3共享装修
            this.haveComplete = 0;//房子装修是否已经完成，0未完成，1已完成
            this.money = new BigDecimal(0);
            this.isSelect = 1;//默认选中
            this.pause = 0;
        }
    }

    public String getHouseName() {
        return (TextUtils.isEmpty(getResidential()) ? "*" : getResidential())
                + (TextUtils.isEmpty(getBuilding()) ? "*" : getBuilding()) + "栋"
                + (TextUtils.isEmpty(getUnit()) ? "*" : getUnit()) + "单元"
                + (TextUtils.isEmpty(getNumber()) ? "*" : getNumber()) + "号";
    }

    public String getNoNumberHouseName() {
        return (TextUtils.isEmpty(getResidential()) ? "*" : getResidential()) + "**栋**单元"
                + (TextUtils.isEmpty(getNumber()) ? "*" : getNumber()) + "号";
    }

    /**
     * 。。。。。。。。。。。。。。。。⦧--4
     * 精算状态：0---5---1--- -1 ---2---3
     *
     * @return
     */
    public Map<String, Object> getBudgetDatas() {
        Map<String, Object> dataMap = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        dataMap.put("total", 4);
        String[] nameBs = {"开始精算", "业主支付", "精算阶段", "完成"};
        switch (getBudgetOk()) {
            case 0:
                dataMap.put("rank", 1);
                dataMap.put("nameB", "待抢单");
                break;
            case 5:
                dataMap.put("rank", 2);
                dataMap.put("nameB", "待业主支付");
                break;
            case 1:
                dataMap.put("rank", 3);
                dataMap.put("nameB", "精算中");
                break;
            case -1:
                dataMap.put("rank", 3);
                dataMap.put("nameB", "未发送精算");
                break;
            case 2:
                dataMap.put("rank", 3);
                dataMap.put("nameB", "待审核精算");
                break;
            case 4:
                dataMap.put("rank", 3);
                dataMap.put("nameB", "修改精算");
                break;
            case 3:
                dataMap.put("rank", 4);
                dataMap.put("nameB", "完成");
                break;
        }
        setMapDatas(dataMap, dataList, nameBs);
        return dataMap;
    }

    private void setMapDatas(Map<String, Object> dataMap, List<Map<String, Object>> dataList, String[] nameBs) {
        int rank = (Integer) dataMap.get("rank");
        int total = (Integer) dataMap.get("total");
        for (int i = 0; i < nameBs.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", nameBs[i]);
            map.put("begin", i < rank);
            String describe = "";
            if (rank != total && i + 1 == rank) {
                describe = (String) dataMap.get("nameB");
            }
            map.put("describe", describe);
            dataList.add(map);
        }
        dataMap.put("dataList", dataList);
    }


    /**
     * 获取设计进度节点
     * 发送设计图业主
     * 设计状态:
     * 0=未确定设计师
     * 4=设计待抢单
     * 1=已支付-设计师待量房
     * 9=量房图确认，设计师待发平面图
     * 5=平面图发给业主
     * 6=平面图审核不通过
     * 7=通过平面图待发施工图
     * 2=已发给业主施工图
     * 8=施工图片审核不通过
     * 3=施工图(全部图)审核通过
     * 。。。。。。。。。。。。。。。。。⦧--6。。⦧--8
     * 远程设计流程：0---4---1---9---5---7---2---3
     *
     * 。。。。。。。。。。。。⦧--6。。⦧--8
     * 自带设计流程：0---1---5---7---2---3
     *
     * @return
     */
    public Map<String, Object> getDesignDatas() {
        Map<String, Object> dataMap = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        String[] nameBs;
        if (getDecorationType() == 2) {//自带设计流程
            dataMap.put("total", 4);
            nameBs = new String[]{"开始自带设计", "平面图阶段", "施工图阶段", "完成"};
            switch (getDesignerOk()) {
                case 0://0未确定设计师
                    dataMap.put("rank", 1);
                    dataMap.put("nameB", "待抢单");
                    break;
                case 4://4设计待抢单
                case 1://1已支付-设计师待量房
                case 9://9量房图发给业主
                    dataMap.put("rank", 2);
                    dataMap.put("nameB", "待上传平面图");
                    break;
                case 5://5平面图发给业主 （发给了业主）
                    dataMap.put("rank", 2);
                    dataMap.put("nameB", "待审核平面图");
                    break;
                case 6://6平面图审核不通过（NG，可编辑平面图）
                    dataMap.put("rank", 2);
                    dataMap.put("nameB", "待修改平面图");
                    break;
                case 7://7通过平面图待发施工图（OK，可编辑施工图）
                    dataMap.put("rank", 3);
                    dataMap.put("nameB", "待上传施工图");
                    break;
                case 2://2已发给业主施工图 （发给了业主）
                    dataMap.put("rank", 3);
                    dataMap.put("nameB", "待审核施工图");
                    break;
                case 8://8施工图片审核不通过（NG，可编辑施工图）
                    dataMap.put("rank", 3);
                    dataMap.put("nameB", "待修改施工图");
                    break;
                case 3://施工图(全部图)审核通过（OK，完成）
                    dataMap.put("rank", 4);
                    dataMap.put("nameB", "完成");
                    break;
            }
        } else {
            dataMap.put("total", 6);
            nameBs = new String[]{"开始远程设计", "业主支付", "量房阶段", "平面图阶段", "施工图阶段", "完成"};
            switch (getDesignerOk()) {
                case 0://0未确定设计师
                    dataMap.put("rank", 1);
                    dataMap.put("nameB", "待抢单");
                    break;
                case 4://4设计待抢单
                    dataMap.put("rank", 2);
                    dataMap.put("nameB", "待业主支付");
                    break;
                case 1://1已支付-设计师待量房
                    dataMap.put("rank", 3);
                    dataMap.put("nameB", "待量房");
                    break;
                case 9://9量房图发给业主
                    dataMap.put("rank", 4);
                    dataMap.put("nameB", "待上传平面图");
                    break;
                case 5://5平面图发给业主 （发给了业主）
                    dataMap.put("rank", 4);
                    dataMap.put("nameB", "待审核平面图");
                    break;
                case 6://6平面图审核不通过（NG，可编辑平面图）
                    dataMap.put("rank", 4);
                    dataMap.put("nameB", "待修改平面图");
                    break;
                case 7://7通过平面图待发施工图（OK，可编辑施工图）
                    dataMap.put("rank", 5);
                    dataMap.put("nameB", "待上传施工图");
                    break;
                case 2://2已发给业主施工图 （发给了业主）
                    dataMap.put("rank", 5);
                    dataMap.put("nameB", "待审核施工图");
                    break;
                case 8://8施工图片审核不通过（NG，可编辑施工图）
                    dataMap.put("rank", 5);
                    dataMap.put("nameB", "待修改施工图");
                    break;
                case 3://施工图(全部图)审核通过（OK，完成）
                    dataMap.put("rank", 6);
                    dataMap.put("nameB", "完成");
                    break;
            }
        }
        setMapDatas(dataMap, dataList, nameBs);
        return dataMap;
    }
}