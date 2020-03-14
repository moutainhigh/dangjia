package com.dangjia.acg.modle.sale.royalty;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by QiYuXiang
 */

@Data
@Entity
@Table(name = "dj_royalty_surface")
@ApiModel(description = "提成详情表")
@FieldNameConstants(prefix = "")
public class DjRoyaltySurface implements Serializable {

    @Id
    @Column(name = "id")
    protected String id;

    @Column(name = "create_date")
    @Desc(value = "创建时间")
    @ApiModelProperty("创建时间")
    protected Date createDate;// 创建日期

    @Column(name = "modify_date")
    @Desc(value = "修改时间")
    @ApiModelProperty("修改时间")
    protected Date modifyDate;// 修改日期

    @Column(name = "data_status")
    @Desc(value = "数据状态 0=正常，1=删除")
    @ApiModelProperty("数据状态 0=正常，1=删除")
    protected Integer dataStatus;
    public DjRoyaltySurface() {

        this.id = (int)(Math.random() * 50000000) + 50000000 + "" + System.currentTimeMillis();
        this.createDate = new Date();
        this.modifyDate = new Date();
        this.dataStatus=0;
    }

}
