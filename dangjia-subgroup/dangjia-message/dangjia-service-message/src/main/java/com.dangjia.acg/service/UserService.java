package com.dangjia.acg.service;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jmessage.api.JMessageClient;
import cn.jmessage.api.common.model.NoDisturbPayload;
import cn.jmessage.api.common.model.RegisterInfo;
import cn.jmessage.api.common.model.UserPayload;
import cn.jmessage.api.common.model.friend.FriendNote;
import cn.jmessage.api.user.UserInfoResult;
import cn.jmessage.api.user.UserListResult;
import cn.jmessage.api.user.UserStateListResult;
import cn.jmessage.api.user.UserStateResult;
import com.dangjia.acg.dto.UserInfoResultDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 用户维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@Service
public class UserService extends BaseService {




    /**
     *  用户注册
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param usernames 用户名（必填）用户名
     *                      开头：字母或者数字
     *                      字母，数字，下划线
     *                      英文点，减号，@
     * @param passwords 密码（必填）用户密码。极光IM服务器会MD5加密保存。
     */
    public  void registerUsers(String appType,String[] usernames,String[] passwords) {
        try {
            usernames=getUserTags(usernames);
            passwords=getUserTags(passwords);
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            List<RegisterInfo> users = new ArrayList<RegisterInfo>();
            for (int i = 0; i < usernames.length; i++) {
                RegisterInfo user = RegisterInfo.newBuilder()
                        .setUsername(usernames[i])
                        .setPassword(passwords[i])
                        .build();
                users.add(user);
            }
            RegisterInfo[] regUsers = new RegisterInfo[users.size()];
            String res = client.registerUsers(users.toArray(regUsers));
            LOG.info(res);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }


    /**
     * 获取用户信息
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @return
     */
    public  UserInfoResultDTO getUserInfo(String appType,String username) {
        try {

            username=getUserTag(username);
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            UserInfoResult res = client.getUserInfo(username);
            UserInfoResultDTO resultDTO=new UserInfoResultDTO();
            resultDTO.setUsername(res.getUsername());
            resultDTO.setNickname(res.getNickname());
            LOG.info(res.getUsername());
            return resultDTO;
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            return null;
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }
    /**
     * 用户在线状态查询
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @return
     */
    public  UserStateResult getUserState(String appType,String username) {
        try {

            username=getUserTag(username);
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            UserStateResult result = client.getUserState(username);
            return result;
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            return null;
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }
    /**
     * 批量用户在线状态查询
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param usernames 用户名数组
     * @return
     */
    public  UserStateListResult[]  getUsersState(String appType,String[] usernames) {
        try {

            usernames=getUserTags(usernames);
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            UserStateListResult[] results = client.getUsersState(usernames);
            return  results;
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            return null;
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }

    /**
     *  修改用户密码
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param password 新密码
     */
    public  void updatePassword(String appType,String username, String password) {
        try {

            username=getUserTag(username);
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            client.updateUserPassword(username, password);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param nickname （选填）用户昵称
     *                      不支持的字符：英文字符： \n \r\n
     * @param avatar （选填）头像
     *                      需要填上从文件上传接口获得的media_id
     * @param birthday （选填）生日 example: 1990-01-24
     *                      yyyy-MM-dd
     * @param signature （选填）签名
     *                      支持的字符：全部，包括 Emoji
     * @param gender （选填） 性别
     *                      0 - 未知， 1 - 男 ，2 - 女
     * @param phone （选填）手机号
     * @param address （选填）地址
     *                      支持的字符：全部，包括 Emoji
     *
     */
    public  void updateUserInfo(String appType,String username, String nickname, String birthday, String signature, int gender,
                                String phone, String address, String avatar) {
        try {

            username=getUserTag(username);
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            UserPayload payload = UserPayload.newBuilder()
                    .setNickname(nickname)
                    .setBirthday(birthday)
                    .setSignature(signature)
                    .setGender(gender)
                    .setAddress(address)
                    .setAvatar(avatar)
                    .addExtra("phone",phone)
                    .build();
            client.updateUserInfo(username, payload);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }
    /**
     *  获取用户列表
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param start 起始序号从0开始
     * @param count 查询条数，最多2000
     * @return
     */
    public  UserListResult getUsers(String appType,int start,int count) {
        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            UserListResult res = client.getUserList(start, count);
            LOG.info(res.getOriginalContent());
            return res;
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            return null;
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }
    /**
     *  删除用户
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     */
    public  void deleteUser(String appType,String username) {
        try {

            username=getUserTag(username);
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            client.deleteUser(username);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }
    
    /**
     * 获取应用管理员列表
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param start 起始序号从0开始
     * @param count 查询条数，最多2000
     * @return
     */
    public  UserListResult getAdminListByAppkey(String appType,int start,int count) {
        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
			UserListResult res = client.getAdminListByAppkey(start, count);
			LOG.info(res.getOriginalContent());
			return res;
		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);
			return null;
		} catch (APIRequestException e) {
			LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return null;
		}
    }


    /**
     *  黑名单列表
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @return
     */
    public  UserInfoResult[] getBlackList(String appType,String username) {
        try {

            username=getUserTag(username);
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            UserInfoResult[] result = client.getBlackList(username);
            return result;
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            return null;
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }

    /**
     *  移除黑名单
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param usernames 移除的用户名数组
     */
    public  void removeBlacklist(String appType,String username,String[] usernames) {
        try {

            usernames=getUserTags(usernames);
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            ResponseWrapper response = client.removeBlacklist(username, usernames);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     *  添加黑名单
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param usernames 移除的用户名数组
     */
    public  void addBlackList(String appType,String username,String[] usernames) {
        try {

            usernames=getUserTags(usernames);
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            ResponseWrapper response = client.addBlackList(username, usernames);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     *  免打扰设置
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param groupId 群聊免打扰，支持add remove数组（可选）
     * @param singleUsername 单聊免打扰，支持add remove数组 （可选）
     */
    public  void setNoDisturb(String appType,String username,Long[] groupId,String[] singleUsername ) {
        try {

            username=getUserTag(username);
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            NoDisturbPayload payload = new NoDisturbPayload.Builder()
                    .setAddSingleUsers(singleUsername)
                    .setAddGroupIds(groupId)
                    .build();
            ResponseWrapper response = client.setNoDisturb(username, payload);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     *  添加好友
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 当前用户名
     * @param users 添加的好友数组
     */
    public  void addFriends(String appType,String username, String[] users) {
        try {

            username=getUserTag(username);
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            ResponseWrapper response = client.addFriends(username, users);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     * 删除好友
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 当前用户名
     * @param users 删除的好友数组
     */
    public  void deleteFriends(String appType,String username, String[] users) {
        try {

            username=getUserTag(username);
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            ResponseWrapper response = client.deleteFriends(username, users);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     * 批量更新好友备注
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 当前用户名
     * @param noteNames 表示要添加的好友列表
     * @param others  其他备注信息
     * @param usernames 用户usernames数组 支持批量修改 最大限制500个
     */
    public  void updateFriendsNote(String appType,String username,String[] noteNames,String[] others,String[] usernames) {
        try {

            username=getUserTag(username);
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            List<FriendNote> friendNotes = new ArrayList<FriendNote>();
            for (int i = 0; i <usernames.length ; i++) {
                FriendNote friendNote = new FriendNote.Builder()
                        .setNoteName(noteNames[i])
                        .setOthers(others[i])
                        .setUsername(usernames[i])
                        .builder();
                friendNotes.add(friendNote);
            }
            FriendNote[] array = new FriendNote[friendNotes.size()];
            ResponseWrapper result = client.updateFriendsNote(username, friendNotes.toArray(array));
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     *  获取好友列表
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 当前用户名
     */
    public  UserInfoResult[] getFriends(String appType,String username) {
        try {

            username=getUserTag(username);
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            UserInfoResult[] userInfoArray = client.getFriendsInfo(username);
            return userInfoArray;
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            return null;
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }

    /**
     * 禁用用户
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 当前用户名
     */
    public  void forbidUser(String appType,String username) {
        try {

            username=getUserTag(username);
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            ResponseWrapper result = client.forbidUser(username, true);
            LOG.info("response code: " + result.responseCode);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

}

