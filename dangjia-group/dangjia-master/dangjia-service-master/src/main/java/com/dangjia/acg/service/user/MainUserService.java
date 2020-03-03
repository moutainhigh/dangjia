package com.dangjia.acg.service.user;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dto.user.UserRoleDTO;
import com.dangjia.acg.dto.user.UserRolesVO;
import com.dangjia.acg.dto.user.UserSearchDTO;
import com.dangjia.acg.mapper.store.IStoreUserMapper;
import com.dangjia.acg.mapper.system.IDepartmentMapper;
import com.dangjia.acg.mapper.system.IJobMapper;
import com.dangjia.acg.mapper.user.RoleMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.system.Department;
import com.dangjia.acg.modle.system.Job;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.user.Role;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MainUserService {
    private static final Logger logger = LoggerFactory
            .getLogger(MainUserService.class);
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    //    @Autowired
//    private UserRoleMapper userRoleMapper;
    @Autowired
    private IDepartmentMapper departmentMapper;

    @Autowired
    private IJobMapper jobMapper;

    @Autowired
    private IStoreUserMapper iStoreUserMapper;
    /****
     * 注入配置
     */
    @Autowired
    private RedisClient redisClient;

    public ServerResponse getUsers(String cityId,String userID,UserSearchDTO userSearch, PageDTO pageDTO,Integer isJob) {
        // 时间处理

        userID=iStoreUserMapper.getVisitUser(userID);
        if (null != userSearch) {
            if (StringUtils.isNotEmpty(userSearch.getInsertTimeStart())
                    && StringUtils.isEmpty(userSearch.getInsertTimeEnd())) {
                userSearch.setInsertTimeEnd(DateUtil.format(new Date()));
            } else if (StringUtils.isEmpty(userSearch.getInsertTimeStart())
                    && StringUtils.isNotEmpty(userSearch.getInsertTimeEnd())) {
                userSearch.setInsertTimeStart(DateUtil.format(new Date()));
            }
            if (StringUtils.isNotEmpty(userSearch.getInsertTimeStart())
                    && StringUtils.isNotEmpty(userSearch.getInsertTimeEnd())) {
                if (userSearch.getInsertTimeEnd().compareTo(
                        userSearch.getInsertTimeStart()) < 0) {
                    String temp = userSearch.getInsertTimeStart();
                    userSearch
                            .setInsertTimeStart(userSearch.getInsertTimeEnd());
                    userSearch.setInsertTimeEnd(temp);
                }
            }
        }
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<UserRoleDTO> urList = userMapper.getUsers(cityId,userID,userSearch,isJob);
        // 获取分页查询后的数据
        PageInfo pageInfo = new PageInfo(urList);
        // 将角色名称提取到对应的字段中
        if (null != urList && urList.size() > 0) {
            for (UserRoleDTO ur : urList) {
                if(!CommonUtil.isEmpty(ur.getDepartmentId())) {
                    Department department = departmentMapper.selectByPrimaryKey(ur.getDepartmentId());
                    Job job = jobMapper.selectByPrimaryKey(ur.getJobId());
                    ur.setDepartmentName(department.getName());
                    ur.setJobName(job.getName());
                }
            }
        }
        pageInfo.setList(urList);
        return ServerResponse.createBySuccess("查询成功", pageInfo);
    }

    public ServerResponse setDelUser(String id, boolean isDel, String insertUid) {
        String msg = this.userMapper.setDelUser(id, isDel, insertUid) == 1 ? "ok"
                : "删除失败，请您稍后再试";
        return ServerResponse.createBySuccessMessage(msg);
    }


    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 30000, rollbackFor = {
            RuntimeException.class, Exception.class})
    public ServerResponse setUser(MainUser user, String roleIds) {

        // 判断用户是否已经存在
        if (!CommonUtil.isEmpty(user.getMobile())) {
            MainUser existUser = this.userMapper.findUserByMobile(user.getMobile());
            if (null != existUser && !String.valueOf(existUser.getId()).equals(
                    String.valueOf(user.getId()))) {
                return ServerResponse.createByErrorMessage("该手机号已经存在");
            }
        }
        if (!CommonUtil.isEmpty(user.getUsername())) {
            MainUser exist = this.userMapper.findUserByName(user.getUsername());
            if (null != exist
                    && !String.valueOf(exist.getId()).equals(
                    String.valueOf(user.getId()))) {
                return ServerResponse.createByErrorMessage("该用户名已经存在");
            }
        }
//        String userId;
        if (user.getId() != null) {
            MainUser dataUser = this.userMapper.selectByPrimaryKey(user.getId());
            // 更新用户
//            userId = user.getId();
            user.setModifyDate(new Date());
            // 设置加密密码
            if (!StringUtils.isEmpty(user.getPassword())) {
                user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            } else {
                user.setPassword(dataUser.getPassword());
            }
            if (CommonUtil.isEmpty(user.getMobile())) {
                user.setMobile(dataUser.getMobile());
            }
            this.userMapper.updateByPrimaryKeySelective(user);
//            // 删除之前的角色
//            List<UserRoleKey> urs = this.userRoleMapper.findByUserId(userId);
//            if (null != urs && urs.size() > 0) {
//                for (UserRoleKey ur : urs) {
//                    this.userRoleMapper.deleteByPrimaryKey(ur);
//                }
//            }
            // 如果是自己，修改完成之后，直接退出；重新登录
            MainUser adminUser = redisClient.getCache(Constants.USER_KEY + user.getId(), MainUser.class);
            if (adminUser != null
                    && adminUser.getId().equals(user.getId())) {
                logger.debug("更新自己的信息，退出重新登录！adminUser=" + adminUser);
                SecurityUtils.getSubject().logout();
                redisClient.deleteCache(Constants.USER_KEY + user.getId());
            }

            logger.debug("清除所有用户权限缓存！！！");
        } else {
            // 新增用户
            user.setId((int) (Math.random() * 50000000) + 50000000 + "" + System.currentTimeMillis());
            user.setCreateDate(new Date());
            user.setIsDel(false);
            user.setIsJob(false);
            // 设置加密密码
            if (StringUtils.isNotBlank(user.getPassword())) {
                user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            } else {
                user.setPassword(DigestUtils.md5Hex("654321"));
            }
            this.userMapper.insert(user);
//            userId = user.getId();
        }
//        // 给用户授角色
//        String[] arrays = roleIds.split(",");
//        for (String roleId : arrays) {
//            UserRoleKey urk = new UserRoleKey();
//            urk.setRoleId(roleId);
//            urk.setUserId(userId);
//            this.userRoleMapper.insert(urk);
//        }
        return ServerResponse.createBySuccessMessage("ok");
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 30000, rollbackFor = {
            RuntimeException.class, Exception.class})
    public ServerResponse addUser(MainUser user, String roleIds) {
        // 判断用户是否已经存在
        MainUser existUser = this.userMapper.findUserByMobile(user.getMobile());
        if (null != existUser) {
            return ServerResponse.createByErrorMessage("该手机号已经存在");
        }
        MainUser exist = this.userMapper.findUserByName(user.getUsername());
        if (null != exist) {
            return ServerResponse.createByErrorMessage("该用户名已经存在");
        }
        // 新增用户
        user.setId((int) (Math.random() * 50000000) + 50000000 + "" + System.currentTimeMillis());
        user.setCreateDate(new Date());
        user.setIsDel(false);
        user.setIsJob(false);
        // 设置加密密码
        if (StringUtils.isNotBlank(user.getPassword())) {
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        } else {
            user.setPassword(DigestUtils.md5Hex("654321"));
        }
        this.userMapper.insert(user);
//        String userId = user.getId();
//        // 给用户授角色
//        String[] arrays = roleIds.split(",");
//        for (String roleId : arrays) {
//            UserRoleKey urk = new UserRoleKey();
//            urk.setRoleId(roleId);
//            urk.setUserId(userId);
//            this.userRoleMapper.insert(urk);
//        }
        return ServerResponse.createBySuccessMessage("ok");
    }

    public ServerResponse setJobUser(HttpServletRequest request, String id, boolean isJob) {
        MainUser mainUser = userMapper.selectByPrimaryKey(id);
        if (mainUser == null) {
            return ServerResponse.createByErrorMessage("该用户不存在");
        }
        String userID = request.getParameter(Constants.USERID);
        if (!CommonUtil.isEmpty(mainUser.getMemberId()) && isJob) {
            String userRoleText = "role" + 3 + ":" + mainUser.getMemberId();
            String token = redisClient.getCache(userRoleText, String.class);
            redisClient.deleteCache(userRoleText);
            if (!CommonUtil.isEmpty(token)) {
                redisClient.deleteCache(token + Constants.SESSIONUSERID);
            }
        }
        mainUser.setIsJob(isJob);
        mainUser.setInsertUid(userID);
        mainUser.setModifyDate(new Date());
        userMapper.updateByPrimaryKeySelective(mainUser);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 指定某个用户为坐席
     *
     * @param id
     * @return
     */
    public ServerResponse setReceiveUser(String cityId,String id, Integer type) {
        try {
            MainUser oldMainUser = userMapper.getUserByReceive(cityId,type);
            if (oldMainUser != null) { //把之前的 坐席用户 改为 非坐席
                oldMainUser.setIsReceive(0);
                userMapper.updateByPrimaryKeySelective(oldMainUser);
            }
            MainUser newMainUser = userMapper.selectByPrimaryKey(id);
            newMainUser.setIsReceive(type);
            userMapper.updateByPrimaryKeySelective(newMainUser);
            return ServerResponse.createBySuccessMessage("ok");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败，请您稍后再试");
        }
    }

    public ServerResponse getUserAndRoles(String id) {
        // 获取用户及他对应的roleIds
        UserRolesVO userRolesVO = this.userMapper.getUserAndRoles(id);
        List<Role> list = this.roleMapper.getRoles();
        userRolesVO.setRoles(list);
        return ServerResponse.createBySuccess("ok", userRolesVO);

    }

    public ServerResponse updateMainInfoById(String userId,String userName,String email){
        MainUser mainUser=userMapper.selectByPrimaryKey(userId);
        if(mainUser==null){
            return ServerResponse.createByErrorMessage("用户信息不存在，不能修改");
        }
        if(StringUtils.isBlank(userName)){
            return ServerResponse.createByErrorMessage("用户姓名不能为空");
        }
        mainUser.setUsername(userName);
        mainUser.setEmail(email);
        userMapper.updateByPrimaryKey(mainUser);
        return ServerResponse.createBySuccessMessage("修改成功");
    }

    public ServerResponse updateMainUserPwd(String userId,String oldPwd,String newPwd){
        MainUser mainUser=userMapper.selectByPrimaryKey(userId);
        if(mainUser==null){
            return ServerResponse.createByErrorMessage("用户信息不存在，不能修改");
        }
        if(StringUtils.isBlank(oldPwd)||StringUtils.isBlank(newPwd)){
            return ServerResponse.createByErrorMessage("密码不能为空");
        }
        if(!DigestUtils.md5Hex(oldPwd).equals(mainUser.getPassword())){
            return ServerResponse.createByErrorMessage("旧密码输入不正确，不能修改");
        }
        // 修改密码
        int num = this.updatePwd(userId,
                DigestUtils.md5Hex(newPwd));
        if (num != 1) {
            return ServerResponse.createByErrorMessage("修改密码失败，已经离职或该用户被删除！");
        }
        return ServerResponse.createBySuccessMessage("修改成功");
    }
    /**
     * 查询用户个人信息
     * @param mobile
     * @return
     */
    public ServerResponse searchMainInfo(String mobile){

        Map<String,Object> map=new HashMap<>();
        MainUser mainUser=userMapper.findUserByMobile(mobile);
        if(mainUser!=null){
            map= BeanUtils.beanToMap(mainUser);
            //查询对应的部门名称
            Department department=departmentMapper.selectByPrimaryKey(mainUser.getDepartmentId());
            if(department!=null){
                map.put("departName",department.getName());
            }
            //查询对应的岗位名称
            Job job=jobMapper.selectByPrimaryKey(mainUser.getJobId());
            if(job!=null){
                map.put("jobName",job.getName());
            }
        }
        return ServerResponse.createBySuccess("查询成功",map);
    }

    public MainUser findUserByMobile(String mobile) {
        return this.userMapper.findUserByMobile(mobile);
    }


    public int updatePwd(String id, String password) {
        return this.userMapper.updatePwd(id, password);
    }

}
