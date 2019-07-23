package com.dangjia.acg.controller.user;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.user.MainUserAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.Validator;
import com.dangjia.acg.dto.user.PermissionVO;
import com.dangjia.acg.dto.user.UserDTO;
import com.dangjia.acg.dto.user.UserSearchDTO;
import com.dangjia.acg.mapper.system.IDepartmentMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.system.Department;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.service.member.GroupInfoService;
import com.dangjia.acg.service.user.MainAuthService;
import com.dangjia.acg.service.user.MainUserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class MainUserController implements MainUserAPI {

    private static final Logger logger = LoggerFactory
            .getLogger(MainUserController.class);
    @Autowired
    private MainUserService userService;

    @Autowired
    private MainAuthService mainAuthService;

    @Autowired
    private GroupInfoService groupInfoService;
    @Autowired
    private UserMapper userMapper;
    /****
     * 注入配置
     */
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private IDepartmentMapper departmentMapper;
    /**
     * 系统来源切换
     *
     * @return ok/fail
     */
    @Override
    @ApiMethod
    public ServerResponse sysSwitching(HttpServletRequest request, Integer source) {
//		SessionUtils.setSession("sysSource",source);
        String userID = request.getParameter(Constants.USERID);
        redisClient.put("sysSource:" + userID, source);
        return ServerResponse.createBySuccessMessage("ok");
    }

    /**
     * 分页查询用户列表
     *
     * @return ok/fail
     */
    @Override
    @ApiMethod
    public ServerResponse getUsers(HttpServletRequest request, PageDTO pageDTO, UserSearchDTO userSearch,Integer isJob) {
        return userService.getUsers(userSearch, pageDTO,isJob);
    }

    /**
     * 设置用户是否离职
     *
     * @return ok/fail
     */
    @Override
    @ApiMethod
    public ServerResponse setJobUser(HttpServletRequest request, String id, boolean isJob) {
        logger.debug("设置用户是否离职！id:" + id + ",isJob:" + isJob);
        ServerResponse msg = null;
        try {
            if (null == id) {
                logger.debug("设置用户是否离职，结果=请求参数有误，请您稍后再试");
                ServerResponse.createByErrorMessage("请求参数有误，请您稍后再试");
            }
            String userID = request.getParameter(Constants.USERID);
            MainUser existUser = redisClient.getCache(Constants.USER_KEY + userID, MainUser.class);
            if (null == existUser) {
                throw new BaseException(ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN, ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN.getDesc());
            }
            // 设置用户是否离职
            msg = userService.setJobUser(id, isJob, existUser.getId());
            logger.info("设置用户是否离职成功！userID=" + id + ",isJob:" + isJob
                    + "，操作的用户ID=" + existUser.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("设置用户是否离职异常！", e);
            msg = ServerResponse.createByErrorMessage("操作异常，请您稍后再试！");
        }
        return msg;
    }

    @Override
    @ApiMethod
    public ServerResponse setReceiveUser(HttpServletRequest request, String id, Integer type) {
        return userService.setReceiveUser(id, type);
    }

    /**
     * 设置用户[新增或更新]
     *
     * @return ok/fail
     */
    @Override
    @ApiMethod
    public ServerResponse setUser(HttpServletRequest request, String roleIds, MainUser user) {
        logger.debug("设置用户[新增或更新]！member:" + user + ",roleIds:" + roleIds);
        try {
            if (null == user) {
                logger.debug("置用户[新增或更新]，结果=请您填写用户信息");
                return ServerResponse.createByErrorMessage("请您填写用户信息");
            }
//            if (StringUtils.isEmpty(roleIds)) {
//                logger.debug("置用户[新增或更新]，结果=请您给用户设置角色");
//                return ServerResponse.createByErrorMessage("请您给用户设置角色");
//            }
            String userID = request.getParameter(Constants.USERID);
            MainUser existUser = redisClient.getCache(Constants.USER_KEY + userID, MainUser.class);
            if (null == existUser) {
                throw new BaseException(ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN, ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN.getDesc());
            }
            user.setInsertUid(existUser.getId());
            // 设置用户[新增或更新]
            logger.info("设置用户[新增或更新]成功！member=" + user + ",roleIds=" + roleIds
                    + "，操作的用户ID=" + existUser.getId());
            return userService.setUser(user, roleIds);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("设置用户[新增或更新]异常！", e);
            return ServerResponse.createByErrorMessage("操作异常，请您稍后再试");
        }
    }

    /**
     * 设置用户[新增或更新]
     *
     * @return ok/fail
     */
    @Override
    @ApiMethod
    public ServerResponse addUser(HttpServletRequest request, String roleIds, MainUser user) {
        logger.debug("设置用户[新增或更新]！member:" + user + ",roleIds:" + roleIds);
        try {
            if (null == user) {
                logger.debug("置用户[新增或更新]，结果=请您填写用户信息");
                return ServerResponse.createByErrorMessage("请您填写用户信息");
            }
//            if (StringUtils.isEmpty(roleIds)) {
//                logger.debug("置用户[新增或更新]，结果=请您给用户设置角色");
//                return ServerResponse.createByErrorMessage("请您给用户设置角色");
//            }
            String userID = request.getParameter(Constants.USERID);
            MainUser existUser = redisClient.getCache(Constants.USER_KEY + userID, MainUser.class);
            if (null == existUser) {
                throw new BaseException(ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN, ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN.getDesc());
            }
            user.setInsertUid(existUser.getId());
            // 设置用户[新增或更新]
            logger.info("设置用户[新增或更新]成功！member=" + user + ",roleIds=" + roleIds
                    + "，操作的用户ID=" + existUser.getId());
            return userService.addUser(user, roleIds);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("设置用户[新增或更新]异常！", e);
            return ServerResponse.createByErrorMessage("操作异常，请您稍后再试");
        }
    }

    /**
     * 删除用户
     *
     * @return ok/fail
     */
    @Override
    @ApiMethod
    public ServerResponse delUser(HttpServletRequest request, String id) {
        logger.debug("删除用户！id:" + id);
        ServerResponse msg;
        try {
            if (null == id) {
                logger.debug("删除用户，结果=请求参数有误，请您稍后再试");
                return ServerResponse.createByErrorMessage("请求参数有误，请您稍后再试");
            }
            String userID = request.getParameter(Constants.USERID);
            MainUser existUser = redisClient.getCache(Constants.USER_KEY + userID, MainUser.class);
            if (null == existUser) {
                throw new BaseException(ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN, ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN.getDesc());
            }
            // 删除用户
            msg = userService.setDelUser(id, false, existUser.getId());
            logger.info("删除用户:" + msg + "。userId=" + id + "，操作用户id:"
                    + existUser.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("删除用户异常！", e);

            msg = ServerResponse.createByErrorMessage("操作异常，请您稍后再试");
        }
        return msg;
    }

    /**
     * @param id
     * @return
     * @描述：恢复用户
     * @创建时间：2018年4月27日 上午9:49:14
     */
    @Override
    @ApiMethod
    public ServerResponse recoverUser(HttpServletRequest request, String id) {
        logger.debug("恢复用户！id:" + id);
        ServerResponse msg;
        try {
            String userID = request.getParameter(Constants.USERID);
            MainUser existUser = redisClient.getCache(Constants.USER_KEY + userID, MainUser.class);
            if (null == existUser) {
                throw new BaseException(ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN, ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN.getDesc());
            }
            if (null == id) {
                return ServerResponse.createByErrorMessage("请求参数有误，请您稍后再试");
            }
            // 删除用户
            msg = userService.setDelUser(id, true, existUser.getId());
            logger.info("恢复用户【" + this.getClass().getName() + ".recoverUser】"
                    + msg + "。用户userId=" + id + "，操作的用户ID=" + existUser.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("恢复用户【" + this.getClass().getName()
                    + ".recoverUser】用户异常！", e);
            msg = ServerResponse.createByErrorMessage("操作异常，请您稍后再试");
        }
        return msg;
    }

    /**
     * 查询用户数据
     *
     * @return map
     */
    @Override
    @ApiMethod
    public ServerResponse getUserAndRoles(HttpServletRequest request, String id) {
        logger.debug("查询用户数据！id:" + id);
        try {
            if (null == id) {
                logger.debug("查询用户数据==请求参数有误，请您稍后再试");
                return ServerResponse.createByErrorMessage("请求参数有误，请您稍后再试");
            }
            // 查询用户
            ServerResponse urvo = userService.getUserAndRoles(id);
            if (null == urvo.getResultObj()) {
                return ServerResponse.createByErrorMessage("查询用户信息有误，请您稍后再试");
            }
            return urvo;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询用户数据异常！", e);
            return ServerResponse.createByErrorMessage("查询用户错误，请您稍后再试");
        }
    }


    /**
     * 登录【使用shiro中自带的HashedCredentialsMatcher结合ehcache（记录输错次数）配置进行密码输错次数限制】
     * </br>缺陷是，无法友好的在后台提供解锁用户的功能，当然，可以直接提供一种解锁操作，清除ehcache缓存即可，不记录在用户表中；
     * </br>
     *
     * @param user
     * @param rememberMe
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse login(HttpServletRequest request,
                                UserDTO user, boolean rememberMe) {
        logger.debug("用户登录，请求参数=member:" + user + "，是否记住我：" + rememberMe);
        ServerResponse msg;
        if (null == user) {
            return ServerResponse.createByErrorMessage("请求参数有误，请您稍后再试");
        }
        String cityId = request.getParameter(Constants.CITY_ID);
        // 用户是否存在
        MainUser existUser = this.userService.findUserByMobile(user.getMobile());
        if (existUser == null) {
            return ServerResponse.createByErrorMessage("该用户不存在，请您联系管理员");
        } else {
            // 校验
            if (StringUtils.isEmpty(user.getPassword())) {
                return ServerResponse.createByErrorMessage("请填写密码！");
            }
            // 是否离职
            if (existUser.getIsJob()) {
                return ServerResponse.createByErrorMessage("登录用户已离职，请您联系管理员");
            }
            //用户名或密码不正确
            String pwd = DigestUtils.md5Hex(user.getPassword());
            if (!existUser.getPassword().equals(pwd)) {
                return ServerResponse.createByErrorMessage("用户名或密码不正确！");
            }
        }
        if(CommonUtil.isEmpty(existUser.getDepartmentId())){
            return ServerResponse.createByErrorMessage("登录用户暂未分配所属部门，请您联系管理员");
        }
        Department department=departmentMapper.selectByPrimaryKey(existUser.getDepartmentId());
        if(department==null){
            return ServerResponse.createByErrorMessage("登录用户暂未分配所属部门，请您联系管理员");
        }
//        if(CommonUtil.isEmpty(cityId)||department.getCityId().indexOf(cityId)==-1){
//            return ServerResponse.createByErrorMessage("登录用户只能在("+department.getCityName()+")下登录，请选择正确的城市");
//        }
        // 用户登录
        try {
            logger.debug("用户登录，用户验证开始！member=" + user.getMobile());
            redisClient.put(Constants.USER_KEY + existUser.getId(), existUser);
            redisClient.put(Constants.CITY_KEY + existUser.getId(), department.getCityId());
            groupInfoService.registerJGUsers(AppType.GONGJIANG.getDesc(), new String[]{existUser.getId()}, new String[1]);
            logger.info("用户登录，用户验证通过！member=" + user.getMobile());
            msg = ServerResponse.createBySuccess("用户登录，用户验证通过！member=" + user.getMobile(), existUser.getId());
            MainUser mainUser = userMapper.selectByPrimaryKey(existUser.getId());
            if(mainUser!=null&&CommonUtil.isEmpty(mainUser.getMemberId())) {
                //插入MemberId
                userMapper.insertMemberId(user.getMobile());
            }
        } catch (Exception e) {
            logger.error("用户登录，用户验证未通过：操作异常，异常信息如下！member=" + user.getMobile(), e);
            msg = ServerResponse.createByErrorMessage("用户登录失败，请您稍后再试");
        }
        return msg;
    }

    /**
     * 检测指定用户是否拥有指定权限Code
     *
     * @param rcode 权限code
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse checkAuth(HttpServletRequest request, String rcode) {
        try {
            // 判断用户是否登录
            String userID = request.getParameter(Constants.USERID);
            MainUser existUser = redisClient.getCache(Constants.USER_KEY + userID, MainUser.class);
            if (null == existUser) {
                throw new BaseException(ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN, ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN.getDesc());
            }
            List<PermissionVO> source = redisClient.getListCache("userPerms:" + existUser.getId(), PermissionVO.class);
            if (source == null || source.size() == 0) {
                ServerResponse pvo = mainAuthService.getUserPerms(userID,existUser.getId());
                source = (List) pvo.getResultObj();
                redisClient.putListCache("userPerms" + existUser.getId(), source);
            }
            if (source != null && source.size() > 0) {
                for (PermissionVO permissionVO : source) {
                    if (rcode.equals(permissionVO.getCode())) {
                        return ServerResponse.createBySuccess("ok", true);
                    }
                }
            }
        } catch (Exception e) {
            return ServerResponse.createBySuccess("ok", false);
        }
        return ServerResponse.createBySuccess("ok", false);
    }

    /**
     * 修改密码之确认手机号
     *
     * @param mobile
     * @param picCode
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updatePwd(HttpServletRequest request, String mobile, String picCode, String mobileCode) {
        logger.debug("修改密码之确认手机号！mobile:" + mobile + ",picCode=" + picCode
                + ",mobileCode=" + mobileCode);
        try {
            if (!Validator.isMobileNo(mobile)) {
                return ServerResponse.createByErrorMessage("手机号格式有误，请您重新填写");
            }

            // 判断用户是否登录
            String userID = request.getParameter(Constants.USERID);
            MainUser existUser = redisClient.getCache(Constants.USER_KEY + userID, MainUser.class);
            if (null == existUser) {
                throw new BaseException(ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN, ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN.getDesc());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改密码之确认手机号异常");
        }
        return ServerResponse.createBySuccessMessage("ok");
    }

    /**
     * 修改密码
     *
     * @param pwd
     * @param isPwd
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse setPwd(HttpServletRequest request, String pwd, String isPwd) {
        logger.debug("修改密码！pwd:" + pwd + ",isPwd=" + isPwd);
        try {
            if (!Validator.isSimplePassword(pwd)
                    || !Validator.isSimplePassword(isPwd)) {
                return ServerResponse.createByErrorMessage("密码格式有误，请您重新填写");
            }
            if (!pwd.equals(isPwd)) {
                return ServerResponse.createByErrorMessage("两次密码输入不一致，请您重新填写");
            }
            // 判断用户是否登录
            String userID = request.getParameter(Constants.USERID);
            MainUser existUser = redisClient.getCache(Constants.USER_KEY + userID, MainUser.class);
            if (null == existUser) {
                throw new BaseException(ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN, ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN.getDesc());
            }
            // 修改密码
            int num = this.userService.updatePwd(existUser.getId(),
                    DigestUtils.md5Hex(pwd));
            if (num != 1) {
                return ServerResponse.createByErrorMessage("修改密码失败，已经离职或该用户被删除！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改密码异常！");
        }
        return ServerResponse.createBySuccessMessage("ok");
    }

    public ServerResponse findUserByMobile(HttpServletRequest request, String mobile) {
        return ServerResponse.createBySuccess("ok", this.userService.findUserByMobile(mobile));
    }
}
