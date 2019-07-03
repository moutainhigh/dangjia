package com.dangjia.acg.dto.system;

import com.dangjia.acg.modle.system.JobRole;
import com.dangjia.acg.modle.user.Role;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class JobRolesVO {
	private String id;

	private Date createDate;// 创建日期

	private Date modifyDate;// 修改日期


	@ApiModelProperty("部门ID")
	private String departmentId;

	@ApiModelProperty("操作id")
	private String operateId;

	@ApiModelProperty("岗位名称")
	private String name;

	@ApiModelProperty("岗位编号")
	private String code;

	@ApiModelProperty("岗位描述")
	private String info;


	@ApiModelProperty("城市名称")
	private String cityName;


	@ApiModelProperty("城市ID")
	private String cityId;
	private List<JobRole> jobRoles;
	private List<Role> roles;

}