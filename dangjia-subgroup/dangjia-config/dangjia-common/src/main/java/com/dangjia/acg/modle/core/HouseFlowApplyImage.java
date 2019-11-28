package com.dangjia.acg.modle.core;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 - 申请管理图片
 */
@Data
@Entity
@Table(name = "dj_core_house_flow_apply_image")
@ApiModel(description = "申请管理图片")
@FieldNameConstants(prefix = "")
public class HouseFlowApplyImage extends BaseEntity {

	@Column(name = "house_flow_apply_id")
	@Desc(value = "进程申请表id")
	@ApiModelProperty("进程申请表id")
	private String houseFlowApplyId;//houseflowapplyid

	@Column(name = "image_url")
	@Desc(value = "图片地址")
	@ApiModelProperty("图片地址")
	private String imageUrl; //

	@Column(name = "image_type")
	@Desc(value = "图片类型 0：材料照片；1：进度照片；2:其他 3:节点图  4:水电管路图")
	@ApiModelProperty("图片类型 0：材料照片；1：进度照片；2:其他 3:节点图  4:水电管路图")
	private Integer imageType; //

	@Column(name = "image_type_name")
	@Desc(value = "图片类型名称 例如：材料照片；进度照片")
	@ApiModelProperty("图片类型名称 例如：材料照片；进度照片")
	private String imageTypeName;

	@Column(name = "house_id")
	@Desc(value = "房子id")
	@ApiModelProperty("房子id")
	private String houseId;

	//所有图片字段加入域名和端口，形成全路径
	public void initPath(String address){
		this.imageUrl= StringUtils.isEmpty(this.imageUrl)?null:address+this.imageUrl;//二维码
	}
}