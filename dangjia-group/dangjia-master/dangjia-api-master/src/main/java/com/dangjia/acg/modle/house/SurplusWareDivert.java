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
import java.util.Date;

/**
 * 实体类 - 剩余材料临时仓库的挪货记录
 */
@Data
@Entity
@Table(name = "dj_house_surplus_ware_divert")
@ApiModel(description = "剩余材料临时仓库的挪货记录")
@FieldNameConstants(prefix = "")
public class SurplusWareDivert extends BaseEntity {

	@Column(name = "product_id")
	@Desc(value = "挪货的商品id")
	@ApiModelProperty("挪货的商品id")
	private String productId;

    @Column(name = "divert_count")
    @Desc(value = "挪出数量")
    @ApiModelProperty("挪出数量")
    private Integer divertCount;

    @Column(name = "divert_type")
    @Desc(value = "挪货去向类型： 1公司临时仓库 2供应商 3业主家的房子")
    @ApiModelProperty("挪货去向类型： 1公司临时仓库 2供应商 3业主家的房子")
    private Integer divertType;

    @Column(name = "src_surplus_ware_house_id")
    @Desc(value = "原仓库id")
    @ApiModelProperty("原仓库id")
    private String srcSurplusWareHouseId;

    @Column(name = "to_surplus_ware_house_id")
    @Desc(value = "挪货去向的临时仓库id (要根据 divert_type 判断是供应商id，还是临时仓库id )")
    @ApiModelProperty("挪货去向的临时仓库id (要根据 divert_type 判断是供应商id，还是临时仓库id ) ")
    private String toSurplusWareHouseId;

    @Column(name = "to_address")
    @Desc(value = "挪货去向的地址")
    @ApiModelProperty("挪货去向的地址")
    private String toAddress;

    @Column(name = "divert_date")
    @Desc(value = "挪货日期")
    @ApiModelProperty("挪货日期")
    protected Date divertDate;// 挪货日期

}