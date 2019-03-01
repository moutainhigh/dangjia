package com.dangjia.acg.api.user;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.user.Permission;
import com.dangjia.acg.modle.user.Role;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2018/10/31 0031
 * Time: 20:01
 */
@FeignClient("dangjia-service-master")
@Api(value = "角色权限管理接口", description = "角色权限管理接口")
public interface MainAuthAPI {

    @PostMapping("/auth/sysList")
    @ApiOperation(value = "获取所有系统来源", notes = "获取所有系统来源")
    ServerResponse selectSysAll(@RequestParam("request") HttpServletRequest request);

    @PostMapping("/auth/domainList")
    @ApiOperation(value = "获取所有域名来源", notes = "获取所有域名来源")
    ServerResponse selectDomainAll(@RequestParam("request") HttpServletRequest request);

    /**
     * 添加权限【test】
     *
     * @return ok/fail
     */
    @PostMapping("/auth/addPermission")
    @ApiOperation(value = "返回待业主支付精算列表", notes = "返回待业主支付精算列表")
    ServerResponse addPermission(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("permission") Permission permission);

    /**
     * 权限列表
     *
     * @return ok/fail
     */
    @PostMapping("/auth/permList")
    @ApiOperation(value = "权限列表", notes = "权限列表")
    ServerResponse permList(@RequestParam("request") HttpServletRequest request);

    /**
     * 添加权限
     *
     * @param request
     * @param type       [0：编辑；1：新增子节点权限]
     * @param permission
     * @return ModelAndView ok/fail
     */
    @PostMapping("/auth/setPerm")
    @ApiOperation(value = "添加权限", notes = "添加权限")
    ServerResponse setPerm(@RequestParam("request") HttpServletRequest request,
                           @RequestParam("type") int type,
                           @RequestParam("permission") Permission permission);

    /**
     * 获取权限
     *
     * @param id
     * @return
     */
    @PostMapping("/auth/getPerm")
    @ApiOperation(value = "获取权限", notes = "获取权限")
    ServerResponse getPerm(@RequestParam("request") HttpServletRequest request,
                           @RequestParam("id") String id);

    /**
     * 删除权限
     *
     * @param id
     * @return
     */
    @PostMapping("/auth/del")
    @ApiOperation(value = "删除权限", notes = "删除权限")
    ServerResponse del(@RequestParam("request") HttpServletRequest request,
                       @RequestParam("id") String id);


    /**
     * 角色列表
     *
     * @return ok/fail
     */
    @PostMapping("/auth/getRoleList")
    @ApiOperation(value = "角色列表", notes = "角色列表")
    ServerResponse getRoleList(@RequestParam("request") HttpServletRequest request);

    /**
     * 查询权限树数据
     *
     * @return PermTreeDTO
     */
    @PostMapping("/auth/findPerms")
    @ApiOperation(value = "查询权限树数据", notes = "查询权限树数据")
    ServerResponse findPerms(@RequestParam("request") HttpServletRequest request);

    /**
     * 添加角色并授权
     *
     * @return PermTreeDTO
     */
    @PostMapping("/auth/addRole")
    @ApiOperation(value = "添加角色并授权", notes = "添加角色并授权")
    ServerResponse addRole(@RequestParam("request") HttpServletRequest request,
                           @RequestParam("permIds") String permIds,
                           @RequestParam("role") Role role);

    /**
     * 根据id查询角色
     *
     * @return PermTreeDTO
     */
    @PostMapping("/auth/updateRole")
    @ApiOperation(value = "根据id查询角色", notes = "根据id查询角色")
    ServerResponse updateRole(@RequestParam("request") HttpServletRequest request,
                              @RequestParam("id") String id);

    /**
     * 更新角色并授权
     *
     * @return PermTreeDTO
     */
    @PostMapping("/auth/setRole")
    @ApiOperation(value = "更新角色并授权", notes = "更新角色并授权")
    ServerResponse setRole(@RequestParam("request") HttpServletRequest request,
                           @RequestParam("permIds") String permIds,
                           @RequestParam("role") Role role);

    /**
     * 删除角色以及它对应的权限
     *
     * @param id
     * @return
     */
    @PostMapping("/auth/delRole")
    @ApiOperation(value = "删除角色以及它对应的权限", notes = "删除角色以及它对应的权限")
    ServerResponse delRole(@RequestParam("request") HttpServletRequest request,
                           @RequestParam("id") String id);

    /**
     * 查找所有角色
     *
     * @return
     */
    @PostMapping("/auth/getRoles")
    @ApiOperation(value = "查找所有角色", notes = "查找所有角色")
    ServerResponse getRoles(@RequestParam("request") HttpServletRequest request);

    /**
     * 根据用户id查询权限树数据
     *
     * @return PermTreeDTO
     */
    @PostMapping("/auth/getUserPerms")
    @ApiOperation(value = "根据用户id查询权限树数据", notes = "根据用户id查询权限树数据")
    ServerResponse getUserPerms(@RequestParam("request") HttpServletRequest request);
}
