package com.dangjia.acg.modle.worker;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.common.util.ImageUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 - 个人精选案例
 */
@Data
@Entity
@Table(name = "dj_core_worker_choice_case")
@ApiModel(description = "个人精选案例")
@FieldNameConstants(prefix = "")
public class WorkerChoiceCase extends BaseEntity {


    @Column(name = "worker_id")
    @Desc(value = "工人ID")
    @ApiModelProperty("工人ID")
    private String workerId;


    @Column(name = "image")
    @Desc(value = "案例主图")
    @ApiModelProperty("案例主图")
    private String image;


    @Column(name = "remark")
    @Desc(value = "备注信息")
    @ApiModelProperty("备注信息")
    private String remark;


    @Column(name = "text_content")
    @Desc(value = "图片内容")
    @ApiModelProperty("图片内容，多长，逗号分隔")
    private String textContent;

    //所有图片字段加入域名和端口，形成全路径
    public void initPath(String address) {
        this.image = StringUtils.isEmpty(this.image) ? null : address + this.image;
        this.textContent = ImageUtil.getImageAddress(address,this.textContent);
    }

}