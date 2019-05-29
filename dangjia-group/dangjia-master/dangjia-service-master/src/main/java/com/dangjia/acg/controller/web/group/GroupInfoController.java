package com.dangjia.acg.controller.web.group;

import com.dangjia.acg.api.web.group.GroupInfoAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.group.Group;
import com.dangjia.acg.modle.group.GroupNotifyInfo;
import com.dangjia.acg.service.member.GroupInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxaing
 */
@RestController
public class GroupInfoController implements GroupInfoAPI {

    @Autowired
    private GroupInfoService groupInfoService;

    /**
     * 获取群组列表
     * @param group
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getGroups(HttpServletRequest request, PageDTO pageDTO,  Group group) {
        return groupInfoService.getGroups(request,pageDTO,group);
    }
    /**
     * 群组通知
     * @param groupNotifyInfo
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse sendGroupsNotify(HttpServletRequest request, GroupNotifyInfo groupNotifyInfo) {
        return groupInfoService.sendGroupsNotify(request,groupNotifyInfo);
    }
    /**
     *  批量更新群组成员
     * @param groupId gid群组ID
     * @param addList 添加到群组的用户,多个以逗号分割（任选）
     * @param removeList 从群组删除的用户,多个以逗号分割（任选）addList和removeList  两者至少要有一个
     *
     */
    @Override
    @ApiMethod
    public  ServerResponse editManageGroup(int groupId, String addList,String removeList){
        return groupInfoService.editManageGroup(groupId,addList,removeList);
    }
    /**
     * 新增群组客服成员配置表
     * @param group
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addGroup(HttpServletRequest request,Group group,String members,String prefixs) {
        return groupInfoService.addGroup(request,group, members, prefixs);
    }

    /**
     * 获取群组客服成员配置表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getGroupUserConfigs(HttpServletRequest request) {
        return groupInfoService.getGroupUserConfigs(request);
    }
    /**
     * 在线客服-一对一聊天
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getOnlineService(HttpServletRequest request,Integer type) {
        return groupInfoService.getOnlineService(request,type);
    }

    /**
     * 群组客服成员配置
     * @param id
     * @param userId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editGroupUserConfig(HttpServletRequest request, String id,String userId) {
        return groupInfoService.editGroupUserConfig(request,id,userId);
    }

}
