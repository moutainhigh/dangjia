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
 * 实体类 - 剩余材料的临时仓库
 */
@Data
@Entity
@Table(name = "dj_house_surplus_ware_house")
@ApiModel(description = "剩余材料的临时仓库")
@FieldNameConstants(prefix = "")
public class SurplusWareHouse extends BaseEntity {
	@Column(name = "house_id")
	@Desc(value = "房子ID 公司仓库时，为null")
	@ApiModelProperty("房子ID 公司仓库时，为null")
	private String houseId;

	@Column(name = "member_id")
	@Desc(value = "大管家id")
	@ApiModelProperty("大管家id")
	private String memberId;

	@Column(name = "address")
	@Desc(value = "仓库地址")
	@ApiModelProperty("仓库地址")
	private String address;

	@Column(name = "state")
	@Desc(value = " 待清点0, 已清点1  默认：0")
	@ApiModelProperty(" 待清点0, 已清点1  默认：0")
	private Integer state;

	@Column(name = "type")
	@Desc(value = " 1:公司仓库 2：业主房子的临时仓库")
	@ApiModelProperty(" 1:公司仓库 2：业主房子的临时仓库")
	private Integer type;

}