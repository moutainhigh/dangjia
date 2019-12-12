package com.dangjia.acg.modle.config;

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
 * @descrilbe 公用前端编辑缓存
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/12 9:02 PM
 */
@Data
@Entity
@Table(name = "dj_data_cache")
@ApiModel(description = "前端数据临时缓存")
@FieldNameConstants(prefix = "")
public class DataCache extends BaseEntity {

    @Column(name = "public_key")
    @Desc(value = "缓存公用key：type为0时为houseId")
    @ApiModelProperty("缓存公用key：type为0时为houseId")
    private String publicKey;


    @Column(name = "type")
    @Desc(value = "类型：0:确认地址保留，1扩展")
    @ApiModelProperty("类型：0:确认地址保留，1扩展")
    private Integer type;


    @Column(name = "data_json")
    @Desc(value = "缓存JSON")
    @ApiModelProperty("缓存JSON")
    private String dataJson;
}
