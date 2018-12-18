package com.dangjia.acg.api.web.group;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.group.Group;
import com.dangjia.acg.modle.group.GroupNotifyInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2018/11/28 0003
 * Time: 16:30
 * web端聊天接口
 */
@FeignClient("dangjia-service-master")
@Api(value = "聊天管理操作接口", description = "聊天管理操作接口")
public interface GroupInfoAPI {
    /**
     * 获取群组列表
     *
     * @param group
     * @return
     */
    @RequestMapping(value = "/group/list", method = RequestMethod.POST)
    @ApiOperation(value = "获取群组列表", notes = "获取群组列表")
    ServerResponse getGroups(HttpServletRequest request, PageDTO pageDTO, Group group);

    /**
     * 群组通知
     *
     * @param groupNotifyInfo
     * @return
     */
    @RequestMapping(value = "/group/notify", method = RequestMethod.POST)
    @ApiOperation(value = "群组通知", notes = "群组通知")
    ServerResponse sendGroupsNotify(HttpServletRequest request, GroupNotifyInfo groupNotifyInfo);

    /**
     * 批量更新群组成员
     *
     * @param groupId    gid群组ID
     * @param addList    添加到群组的用户,多个以逗号分割（任选）
     * @param removeList 从群组删除的用户,多个以逗号分割（任选）addList和removeList  两者至少要有一个
     */
    @RequestMapping(value = "/group/member/edit", method = RequestMethod.POST)
    @ApiOperation(value = "批量更新群组成员", notes = "批量更新群组成员,addList和removeList  多个以逗号分割,两者至少要有一个")
    ServerResponse editManageGroup(int groupId, String addList, String removeList);

    /**
     * 新增群组客服成员配置表
     *
     * @param group
     * @return
     */
    @RequestMapping(value = "/group/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增群组客服成员配置表", notes = "新增群组客服成员配置表")
    ServerResponse addGroup(HttpServletRequest request, Group group,String members,String prefixs);


    /**
     * 获取群组客服成员配置表
     *
     * @return
     */
    @RequestMapping(value = "/group/cfg/get", method = RequestMethod.POST)
    @ApiOperation(value = "获取群组客服成员配置表", notes = "获取群组客服成员配置表")
    ServerResponse getGroupUserConfigs(HttpServletRequest request);
    /**
     * 在线客服-一对一聊天
     * @return
     */
    @RequestMapping(value = "/group/cfg/online", method = RequestMethod.POST)
    @ApiOperation(value = "获取群组客服成员配置表", notes = "获取群组客服成员配置表")
    public ServerResponse getOnlineService(HttpServletRequest request,String userToken);

    /**
     * 群组客服成员配置表设置为管理员/取消管理员
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/group/cfg/edit", method = RequestMethod.POST)
    @ApiOperation(value = "群组客服成员配置表-更新", notes = "群组客服成员配置表-更新")
    ServerResponse editGroupUserConfig(HttpServletRequest request, String id,String userId);

}
