package com.dangjia.acg.controller.web.system;

import com.dangjia.acg.api.web.system.SystemAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.system.Department;
import com.dangjia.acg.modle.system.Job;
import com.dangjia.acg.service.system.SystemServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController implements SystemAPI {

	@Autowired
	private SystemServices systemServices;
	@Override
	@ApiMethod
	public ServerResponse queryDepartment(String user_id,String parentId) {
		return systemServices.queryDepartment(user_id,parentId);
	}
	@Override
	@ApiMethod
	public ServerResponse queryDepartmentAll() {
		return systemServices.queryDepartmentAll();
	}
	@Override
	@ApiMethod
	public ServerResponse editDepartment(String user_id, Department department) {
		return systemServices.editDepartment(user_id,department);
	}

	@Override
	@ApiMethod
	public ServerResponse queryJob(String cityId, String departmentId, PageDTO pageDTO) {
		return systemServices.queryJob(cityId,departmentId,pageDTO);
	}

	@Override
	@ApiMethod
	public ServerResponse getJobAndRoles(String id) {
		return systemServices.getJobAndRoles(id);
	}

	@Override
	@ApiMethod
	public ServerResponse setJob(String user_id,Job job, String roleIds) {
		return systemServices.setJob(user_id,job,roleIds);
	}
}
