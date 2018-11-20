package com.dangjia.acg.dto.user;

import com.dangjia.acg.modle.user.RolePermission;
import lombok.Data;

import java.util.List;
@Data
public class RoleVO {

	private String id;

	private String roleName;

	private String descpt;

	private String code;

	private String insertUid;

	private String createDate;
	//角色下的权限ids
	private List<RolePermission> rolePerms;
//
//	//获取全部权限数据
//	private List<PermissionVO> pvos;

}
