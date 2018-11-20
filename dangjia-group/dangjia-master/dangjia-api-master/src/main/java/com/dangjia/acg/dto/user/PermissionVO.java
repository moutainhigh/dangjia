package com.dangjia.acg.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PermissionVO implements Serializable{
	private static final long serialVersionUID = -2783081162690878303L;
	private String id;

	private String name;

	private String pid;

	private String istype;

	private String code;

	private String page;

	private String icon;

	private String zindex;

	private boolean checked;

	private boolean open;

	private String sysId;
	private String  domainId;

	private String sysName;
	private String domainName;
	private String domainPath;

	private List<PermissionVO> children;

}