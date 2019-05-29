package com.dangjia.acg.dto.user;

import com.dangjia.acg.modle.user.Role;
import com.dangjia.acg.modle.user.UserRoleKey;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class UserRolesVO {
	private String id;

	private String username;

	private String mobile;

	private String email;

	private String password;

	private String insertUid;

	private Date createDate;// 创建日期

	private Date modifyDate;// 修改日期

	private boolean isDel;

	private boolean isJob;
	private Integer isReceive;
	private List<UserRoleKey> userRoles;
	private List<Role> roles;

}