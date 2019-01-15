package com.dangjia.acg.dto.user;

import lombok.Data;

import java.util.Date;
@Data
public class UserRoleDTO {
	private String id;

	private String username;

	private String mobile;

	private String email;

	private String password;

	private String insertUid;

	private Date createDate;// 创建日期

	private Date modifyDate;// 修改日期

	private Boolean isDel;

	private Boolean isJob;

	private Boolean isReceive;

	private String roleNames;

	private Integer version;


}