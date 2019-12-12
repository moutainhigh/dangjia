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



    @Column(name = "worker_type")
    @Desc(value = "工种类别0业主，1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工")
    @ApiModelProperty("工种类别0业主，1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工")
    private Integer workerType ;


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


    @Column(name = "type")
    @Desc(value = "任务类型0跳转网页，1支付任务,2补货补人工,3审核验收任务,4大管家审核退,5审核工匠")
    @ApiModelProperty("任务类型0跳转网页，1支付任务,2补货补人工,3审核验收任务,4大管家审核退,5审核工匠")
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
