package com.dangjia.acg.modle.house;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Ruking.Cheng
 * @descrilbe 任务栈表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/12 3:02 PM
 */
@Data
@Entity
@Table(name="dj_task_stack")
@ApiModel(description="任务栈表")
@FieldNameConstants(prefix = "")
public class TaskStack extends BaseEntity {



    @Column(name = "member_id")
    @Desc(value = "用户ID")
    @ApiModelProperty("用户ID")
    private String memberId ;


    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId ;


    @Column(name = "name")
    @Desc(value = "任务名称")
    @ApiModelProperty("任务名称")
    private String name ;


    @Column(name = "image")
    @Desc(value = "任务图标")
    @ApiModelProperty("任务图标")
    private String image ;

    /**
     * 1001：量房后产生的补差价（业主端）
     *  *      1002：（工序）**待支付（业主端）
     *  *      1003：（工序）审核抢单（业主端）
     *  *      1004：审核精算（业主端）
     *  *      1005：审核（工序）补人工（业主端）
     *  *
     *  *
     *  *       1006：（工序）提醒您退剩余材料（业主端）
     *  *       1007：审核设计图（业主端）
     *  *       1008：审核施工图（业主端）
     *  *       1009：审核平面图（业主端）
     *  *       1010：设计图纸不合格（业主端）
     *  *       1011：请查收您的验房结果（业主端）
     *  *       1012：（工序）申请验收（业主端+工匠端）
     *  *       1013：您有一笔退人工处理
     *  *       1014：您有一笔补人工处理
     *  *       1015：上传水电管路图
     *  *       1016：（工序）申请验收（工匠端,未上传水电管路图）
     *  *
     *  *
     *  *       1017：维保申请验收
     *  *       1018：在（工序）保质期内有维修单
     *  *       1019：缴纳质保金
     *  *       1020：在**质保期内有维修单
     *  *       1021：质保责任划分通知
     */
    @Desc(value = "任务类型0跳转网页，1支付任务,2补货补人工,3审核验收任务,4大管家审核退,5审核工匠,6图纸不合格,7是否提交补差价订单,8工匠缴纳质保金,9维保责任划分通知,10管家定损后通知,11工匠发起退材料,12业主退人工，13工匠申请维保验收,14:维保商品费用,15工匠申请报销,16精算审核" +
            "17业主审核部分退货")
    @ApiModelProperty("任务类型0跳转网页，1支付任务,2补货补人工,3审核验收任务,4大管家审核退,5审核工匠,6图纸不合格,7是否提交补差价订单,8工匠缴纳质保金,9维保责任划分通知,10管家定损后通知,11工匠发起退材料,12为主退人工，13工匠申请维保验收,14:维保商品费用,15工匠申请报销,16精算审核" +
            "17业主审核部分退货")
    private Integer type ;


    @Column(name = "data")
    @Desc(value = "type==0时为跳转URL，其他任务对应业务ID")
    @ApiModelProperty("type==0时为跳转URL，其他任务对应业务ID")
    private String data ;


    @Column(name = "state")
    @Desc(value = "处理状态0待处理，1已处理")
    @ApiModelProperty("处理状态0待处理，1已处理")
    private Integer state ;


    @Column(name = "remarks")
    @Desc(value = "备注")
    @ApiModelProperty("备注")
    private String remarks ;
}
