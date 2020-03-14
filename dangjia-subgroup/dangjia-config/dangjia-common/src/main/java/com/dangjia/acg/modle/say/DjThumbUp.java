package com.dangjia.acg.modle.say;

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
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2020/2/24
 * Time: 10:45
 */
@Data
@Entity
@Table(name = "dj_thumb_up")
@ApiModel(description = "点赞记录")
@FieldNameConstants(prefix = "")
public class DjThumbUp extends BaseEntity {

    @Column(name = "member_id")
    @Desc(value = "用户id")
    @ApiModelProperty("用户id")
    private String memberId;

    @Column(name = "record_id")
    @Desc(value = "点赞内容id")
    @ApiModelProperty("点赞内容id")
    private String recordId;
}
