package com.dangjia.acg.dto.user;

import lombok.Data;

@Data
public class UserSearchDTO {

	private Integer page;

	private Integer limit;

	private String uname;

	private String umobile;

	private String insertUid;

	private String insertTimeStart;

	private String insertTimeEnd;


}
