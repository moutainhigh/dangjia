package com.dangjia.acg.api.web.system;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.system.Department;
import com.dangjia.acg.modle.system.Job;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * author: qiyuxiang
 * Date: 2018/10/31 0031
 * Time: 20:01
 */
@FeignClient("dangjia-service-master")
@Api(value = "角色权限管理接口", description = "角色权限管理接口")
public interface SystemAPI {
    //查询组织架构
    @PostMapping("/web/system/queryDepartment")
    @ApiOperation(value = "查询组织架构指定节点集合", notes = "查询组织架构指定节点集合")
     ServerResponse queryDepartment(String user_id,String parentId) ;
    //查询组织架构
    @PostMapping("/web/system/queryDepartmentAll")
    @ApiOperation(value = "查询所有组织架构", notes = "查询所有组织架构")
    ServerResponse queryDepartmentAll() ;

    //修改组织架构信息
    @PostMapping("/web/system/editDepartment")
    @ApiOperation(value = "新增/修改指定节点", notes = "新增/修改指定节点")
     ServerResponse editDepartment(String user_id,Department department);


    //查询岗位
    @PostMapping("/web/system/queryJob")
    @ApiOperation(value = "查询岗位列表", notes = "查询岗位列表")
     ServerResponse queryJob(String cityId,String departmentId, PageDTO pageDTO);

    @PostMapping("/web/system/getJobAndRoles")
    @ApiOperation(value = "查询岗位及他对应的角色", notes = "查询岗位及他对应的角色")
     ServerResponse getJobAndRoles(String id) ;


    @PostMapping("/web/system/setJob")
    @ApiOperation(value = "新增/修改岗位", notes = "新增/修改岗位")
     ServerResponse setJob(String user_id,Job job, String roleIds);
}
