package com.dangjia.acg.service.member;

import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.nimserver.NIMPost;
import com.dangjia.acg.common.util.nimserver.apply.NimGroupService;
import com.dangjia.acg.common.util.nimserver.apply.NimMessageService;
import com.dangjia.acg.common.util.nimserver.apply.NimUserService;
import com.dangjia.acg.common.util.nimserver.dto.NimUserInfo;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.group.GroupDTO;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.group.IGroupMapper;
import com.dangjia.acg.mapper.group.IGroupNotifyInfoMapper;
import com.dangjia.acg.mapper.group.IGroupUserConfigMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.group.Group;
import com.dangjia.acg.modle.group.GroupNotifyInfo;
import com.dangjia.acg.modle.group.GroupUserConfig;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.util.Utils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 *
 */
@Service
public class GroupInfoService {


    //客服
    public final static String KEFU = "业主您好！我是当家装修的客服NAME，我将负责帮您协调传达信息，有任何装修问题都可以给我发消息。";
    //设计师
    public final static String SHEJISHI = "业主您好！我是您的室内设计师，我将负责贵府的装修设计事宜，按照与您沟通的结果完成设计工作。";
    //精算师
    public final static String JINGSUANSHI = "业主您好！我是您的装修精算师，我将负责贵府的装修精算事宜，按照与您沟通的结果完成精算工作。";
    //大管家
    public final static String DAGUANGJIA = "业主您好！我是您的大管家，我将负责贵府的装修工程管理事宜，确保施工过程符合工艺标准，监督使用正确材料，如期交付。";
    //普通工匠
    public final static String GONGJIANG = "业主您好！我是您的WORKERNAME，我将负责贵府的WORKERNAME施工，确保使用正确的施工材料，按照工艺标准，如期交付。";

    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IGroupMapper groupMapper;
    @Autowired
    private IGroupNotifyInfoMapper groupNotifyInfoMapper;
    @Autowired
    private IGroupUserConfigMapper groupUserConfigMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IMemberMapper memberMapper;


    /**
     * 获取群组列表
     *
     * @param group
     * @return
     */
    public ServerResponse getGroups(HttpServletRequest request, PageDTO pageDTO, Group group) {
        Example example = new Example(Group.class);
        Example.Criteria criteria = example.createCriteria();
        if (!CommonUtil.isEmpty(group.getUserId())) {
            criteria.andEqualTo(Group.USER_ID, group.getUserId());
        }
        if (!CommonUtil.isEmpty(group.getHouseName())) {
            criteria.andLike(Group.HOUSE_NAME, "%" + group.getHouseName() + "%");
        }
        if (!CommonUtil.isEmpty(group.getHouseId())) {
            criteria.andLike(Group.HOUSE_ID, group.getHouseId());
        }
        example.orderBy(Group.CREATE_DATE).desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<Group> list = groupMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(list);
        List<GroupDTO> listdto = new ArrayList<>();
        for (Group g : list) {
            if (!CommonUtil.isEmpty(g.getGroupId())) {
                GroupDTO dto = new GroupDTO();
                BeanUtils.beanToBean(g, dto);
                List<NimUserInfo> members = NimGroupService.getGroupInfoMembers(AppType.GONGJIANG.getDesc(),g.getGroupId());
                if (members != null) {
                    dto.setMembers(members);
                }
                listdto.add(dto);
            }
        }
        pageResult.setList(listdto);
        return ServerResponse.createBySuccess("ok", pageResult);
    }

    /**
     * 群组通知
     *
     * @param groupNotifyInfo
     * @return
     */
    public ServerResponse sendGroupsNotify(HttpServletRequest request, GroupNotifyInfo groupNotifyInfo) {
        NimMessageService.sendGroupTextByAdmin(AppType.GONGJIANG.getDesc(), groupNotifyInfo.getGroupId(), groupNotifyInfo.getUserId(), groupNotifyInfo.getText());
        groupNotifyInfoMapper.insert(groupNotifyInfo);
        return ServerResponse.createBySuccessMessage("ok");
    }


    /**
     * 批量更新群组成员(跨域)
     *
     * @param groupId    gid群组ID
     * @param addList    添加到群组的用户,多个以逗号分割（任选）
     * @param removeList 从群组删除的用户,多个以逗号分割（任选）addList和removeList  两者至少要有一个
     */
    public ServerResponse editManageGroup(String groupId, String addList, String removeList) {
        String[] adds = StringUtils.split(addList, ",");
        String[] removes = StringUtils.split(removeList, ",");
        if (!CommonUtil.isEmpty(addList)) {
            registerJGUsers(AppType.SALE.getDesc(), adds, new String[adds.length]);
        }
        //夸应用添加删除
        NimGroupService.manageGroup(AppType.GONGJIANG.getDesc(), AppType.SALE.getDesc(), groupId, adds, removes);
        if (CommonUtil.isEmpty(addList)) {
            return ServerResponse.createBySuccessMessage("ok");
        }
        //给业主发送默认提示语
        for (String userid : adds) {
            Member member = memberMapper.selectByPrimaryKey(userid);
            if (member != null) {
                String text = "";
                if (member.getWorkerType() == 1) {
                    text = SHEJISHI;
                }
                if (member.getWorkerType() == 2) {
                    text = JINGSUANSHI;
                }
                if (member.getWorkerType() == 3) {
                    text = DAGUANGJIA;
                }
                if (member.getWorkerType() > 3) {
                    WorkerType workerType = workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
                    text = GONGJIANG.replaceAll("WORKERNAME", workerType.getName());
                }
                if (!CommonUtil.isEmpty(text)) {
                    NimMessageService.sendGroupTextByAdmin(AppType.GONGJIANG.getDesc(), groupId, userid, text);
                }
            }
        }
        return ServerResponse.createBySuccessMessage("ok");
    }

    /**
     * 新增群组客服成员配置表
     *
     * @param group
     * @return
     */
    public ServerResponse addGroup(HttpServletRequest request, Group group, String members, String prefixs) {
        Member user = memberMapper.selectByPrimaryKey(group.getUserId());
        group.setUserName(user.getNickName());
        group.setUserMobile(user.getMobile());

        House house = houseMapper.selectByPrimaryKey(group.getHouseId());
        group.setHouseName(house.getHouseName());
        group.setAdminId(house.getMemberId());
        //获取默认成员
        List<String> memberlist=Arrays.asList(StringUtils.split(members, ","));
        List<String> prefixlist=Arrays.asList(StringUtils.split(prefixs, ","));
        memberlist.add(group.getUserId());
        prefixlist.add("业主");
        //检查用户是否注册，不存在自动注册(工匠端)
        registerJGUsers(AppType.GONGJIANG.getDesc(), memberlist.toArray(new String[0]), prefixlist.toArray(new String[0]));

        //创建群组
        String groupResult = NimGroupService.createGroup(AppType.GONGJIANG.getDesc(), house.getMemberId(), group.getHouseName(), memberlist.toArray(new String[0]), "", "");
        if (groupResult != null) {
            group.setGroupId(groupResult);
            if (memberlist != null)
                for (String userid : memberlist) {
                    if(!group.getUserId().equals(userid)) {
                        String nickname = getUserName(userid);
                        //给业主发送默认提示语
                        String text = KEFU.replaceAll("NAME", nickname);
                        NimMessageService.sendGroupTextByAdmin(AppType.GONGJIANG.getDesc(), group.getGroupId(), userid, text);
                    }
                }
        }
        if (this.groupMapper.insertSelective(group) > 0) {
            return ServerResponse.createBySuccessMessage("ok");
        } else {
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }

    public String getUserName(String userid) {
        String nickname = "";
        MainUser user = userMapper.selectByPrimaryKey(userid);
        if (user == null) {
            Member member = memberMapper.selectByPrimaryKey(userid);
            nickname = member.getName();
        } else {
            nickname = user.getUsername();
        }
        return nickname;
    }

    public void registerJGUsers(String appType, String[] username, String[] prefixs) {
        List<NimUserInfo> userInfos = NimUserService.getUserInfo(appType, Arrays.toString(username));
        if (userInfos != null && userInfos.size() > 0) {
            for (int i = 0; i < userInfos.size(); i++) {
                NimUserInfo userInfoResult =userInfos.get(i);
                boolean result  = Arrays.asList(username).contains(userInfoResult.getAccid());
                if (result) {
                    String nickname;
                    String phone;
                    String avatar;
                    String signature = "";
                    String prefix = prefixs[i];
                    MainUser user = userMapper.selectByPrimaryKey(username[i]);
                    if (user == null) {
                        Member member = memberMapper.selectByPrimaryKey(username[i]);
                        if (member != null) {
                            if (!CommonUtil.isEmpty(member.getWorkerTypeId()) && CommonUtil.isEmpty(prefix)) {
                                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
                                if (workerType != null) {
                                    prefix = workerType.getName();
                                    signature = String.valueOf(workerType.getType());
                                }
                            }
                            phone = member.getMobile();
                            nickname = member.getName();
                            avatar = StringUtils.isEmpty(member.getHead()) ? null :
                                    configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + member.getHead();
                        } else {
                            return;
                        }
                    } else {
                        Member member = memberMapper.selectByPrimaryKey(user.getMemberId());
                        if (member != null) {
                            if (!CommonUtil.isEmpty(member.getWorkerTypeId()) && CommonUtil.isEmpty(prefix)) {
                                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
                                if (workerType != null) {
                                    signature = String.valueOf(workerType.getType());
                                    prefix = workerType.getName();
                                }
                            }
                            phone = member.getMobile();
                            nickname = member.getName();
                            avatar = StringUtils.isEmpty(member.getHead()) ? null :
                                    configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + member.getHead();
                        } else {
                            phone = "400-168-1231";
                            nickname = user.getUsername();
                            avatar = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + Utils.getHead();
                        }
                    }
                    if (!CommonUtil.isEmpty(prefix)) {
                        nickname = prefix + "-" + nickname;
                    }
                    NimUserInfo userInfo=new NimUserInfo();
                    userInfo.setAccid(username[i]);
                    userInfo.setName(nickname);
                    userInfo.setIcon(avatar);
                    userInfo.setSign(signature);
                    userInfo.setMobile(phone);
                    userInfo.setEx(prefix);
                    NimUserService.registerUsers(appType,  userInfo);
                }
            }
        }
    }

    /**
     * 在线客服-一对一聊天
     *
     * @return
     */
    public ServerResponse getOnlineService(HttpServletRequest request, Integer type) {
        String cityId = request.getParameter(Constants.CITY_ID);
        MainUser user = userMapper.getUserByReceive(cityId,type);
        if (user!=null) {
            Map map = new HashMap();
            map.put("targetId", user.getId());
            map.put("targetAppKey", NIMPost.APPKEY);
            String text = null;
            if (type == 1) {
                text = "业主您好！我是您的售前客服！";
            }
            if (type == 2) {
                text = "业主您好！我是您的售中客服！";
            }
            if (type == 3) {
                text = "业主您好！我是您的材料顾问！";
            }
            if (type == 4) {
                text = "业主您好！我是您的工程顾问！";
            }
            map.put("text", text);
            return ServerResponse.createBySuccess("ok", map);
        } else {
            return ServerResponse.createByErrorMessage("暂无在线客服，如有疑问请致电400-168-1231 ");
        }
    }

    /**
     * 获取群组客服成员配置表
     *
     * @return
     */
    public ServerResponse getGroupUserConfigs(HttpServletRequest request) {
        Example example = new Example(GroupUserConfig.class);
        example.orderBy(GroupUserConfig.CREATE_DATE).desc();
        List<GroupUserConfig> list = groupUserConfigMapper.selectByExample(example);
        return ServerResponse.createBySuccess("ok", list.get(0));
    }


    /**
     * 群组客服成员配置表设置保存
     *
     * @param id
     * @param userId 更新的客服ID
     * @return
     */
    public ServerResponse editGroupUserConfig(HttpServletRequest request, String id, String userId) {
        GroupUserConfig groupUserConfig = new GroupUserConfig();
        groupUserConfig.setId(id);
        groupUserConfig.setUserId(userId);
        MainUser user = userMapper.selectByPrimaryKey(userId);
        groupUserConfig.setName(user.getUsername());
        if (this.groupUserConfigMapper.updateByPrimaryKeySelective(groupUserConfig) > 0) {
            return ServerResponse.createBySuccessMessage("ok");
        } else {
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }

}
