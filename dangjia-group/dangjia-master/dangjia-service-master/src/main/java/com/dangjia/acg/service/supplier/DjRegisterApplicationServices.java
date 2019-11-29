package com.dangjia.acg.service.supplier;

import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.supplier.RegisterApplicationDTO;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.mapper.supplier.DjRegisterApplicationMapper;
import com.dangjia.acg.mapper.system.IJobMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.modle.supplier.DjRegisterApplication;
import com.dangjia.acg.modle.system.Job;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 8/10/2019
 * Time: 下午 2:22
 */
@Service
public class DjRegisterApplicationServices {
    private static Logger logger = LoggerFactory.getLogger(DjRegisterApplicationServices.class);

    @Autowired
    private DjRegisterApplicationMapper djRegisterApplicationMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private ICityMapper iCityMapper;
    @Autowired
    private IJobMapper jobMapper;


    public String getUserIdExamine(String userId) {
        return djRegisterApplicationMapper.getUserIdExamine(userId);
    }

    /**
     * 供应商/店铺注册
     *
     * @param djRegisterApplication
     * @return
     */
    public ServerResponse registerSupAndStorefront(DjRegisterApplication djRegisterApplication) {
        try {
            if (CommonUtil.isEmpty(djRegisterApplication.getUserName()))
                return ServerResponse.createByErrorMessage("用户名不能为空");
            if (CommonUtil.isEmpty(djRegisterApplication.getMobile()))
                return ServerResponse.createByErrorMessage("电话号码不能为空");
            if (CommonUtil.isEmpty(djRegisterApplication.getCardNumber()))
                return ServerResponse.createByErrorMessage("身份证号码不能为空");
            if (CommonUtil.isEmpty(djRegisterApplication.getCityId()))
                return ServerResponse.createByErrorMessage("城市不能为空");
            if (CommonUtil.isEmpty(djRegisterApplication.getCardNumber()))
                return ServerResponse.createByErrorMessage("身份证不能为空");
            if (CommonUtil.isEmpty(djRegisterApplication.getPayPassword()))
                return ServerResponse.createByErrorMessage("支付密码不能为空");
            if (djRegisterApplication.getPayPassword().length() != 6)
                return ServerResponse.createByErrorMessage("请设置6位数支付密码");
            Example example = new Example(DjRegisterApplication.class);
            example.createCriteria().andEqualTo(DjRegisterApplication.CITY_ID, djRegisterApplication.getCityId())
                    .andEqualTo(DjRegisterApplication.DATA_STATUS, 0)
                    .andEqualTo(DjRegisterApplication.MOBILE, djRegisterApplication.getMobile())
                    .andNotEqualTo(DjRegisterApplication.APPLICATION_STATUS, 2)
                    .andEqualTo(DjRegisterApplication.APPLICATION_TYPE, djRegisterApplication.getApplicationType());
            if (djRegisterApplicationMapper.selectByExample(example).size() > 0)
                return ServerResponse.createByErrorMessage("申请已存在");
            djRegisterApplication.setDataStatus(0);
            djRegisterApplication.setApplicationStatus(0);
            djRegisterApplication.setPassWord(DigestUtils.md5Hex(djRegisterApplication.getPassWord()));
            djRegisterApplication.setPayPassword(DigestUtils.md5Hex(djRegisterApplication.getPayPassword()));
            if (djRegisterApplicationMapper.insert(djRegisterApplication) > 0)
                return ServerResponse.createBySuccessMessage("申请成功");
        } catch (Exception e) {
            logger.error("申请失败", e);
            return ServerResponse.createByErrorMessage("申请失败");
        }
        return ServerResponse.createByErrorMessage("申请失败");
    }


    /**
     * @param mobile
     * @return
     */
    public ServerResponse querySupAndStorefront(String mobile, String cityId) {
        try {
            Example example = new Example(DjRegisterApplication.class);
            example.createCriteria().andEqualTo(DjRegisterApplication.CITY_ID, cityId)
                    .andEqualTo(DjRegisterApplication.DATA_STATUS, 0)
                    .andEqualTo(DjRegisterApplication.MOBILE, mobile)
                    .andNotEqualTo(DjRegisterApplication.APPLICATION_STATUS, 2);
            List<DjRegisterApplication> djRegisterApplications = djRegisterApplicationMapper.selectByExample(example);
            if (djRegisterApplications.size() > 0)
                return ServerResponse.createBySuccess("查询成功", djRegisterApplications.get(0));
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
        return ServerResponse.createByErrorMessage("查询失败");
    }


    /**
     * 提交审核注册的供应商或店铺
     *
     * @param registerId   申请ID
     * @param isAdopt      是否通过 1=通过  2=不通过
     * @param departmentId 部门ID
     * @param jobId        岗位ID
     * @param failReason   不通过原因
     * @return
     */
    public ServerResponse checkSupAndStorefront(HttpServletRequest request, String registerId, Integer isAdopt, String departmentId, String jobId, String failReason) {
        try {

            String userID = request.getParameter(Constants.USERID);
            DjRegisterApplication djRegisterApplication = djRegisterApplicationMapper.selectByPrimaryKey(registerId);

            djRegisterApplication.setApplicationStatus(isAdopt);
            djRegisterApplication.setFailReason(failReason);//审核失败原因
            djRegisterApplication.setAuditUserId(userID);//审核人
            djRegisterApplication.setModifyDate(new Date());//最后一次操作时间
            djRegisterApplicationMapper.updateByPrimaryKeySelective(djRegisterApplication);
            Map map = new HashMap();
            if ("1".equals(djRegisterApplication.getApplicationType())) {
                map.put("name", "店铺");
            }
            if ("2".equals(djRegisterApplication.getApplicationType())) {
                map.put("name", "供应商");
            }
            if (isAdopt == 1) {
                map.put("checkType", "已通过");
                //注入用户信息
                MainUser user = new MainUser();
                user.setPassword(djRegisterApplication.getPassWord());
                user.setUsername(djRegisterApplication.getName());
                user.setMobile(djRegisterApplication.getMobile());
                user.setUserType(djRegisterApplication.getApplicationType());
                user.setInsertUid(userID);
                user.setDepartmentId(departmentId);
                user.setJobId(jobId);
                user.setPayPassword(djRegisterApplication.getPayPassword());
                addUser(user);
            } else {
                map.put("checkType", "未通过，原因:" + failReason);
            }
            //发送短信通知至注册申请人手机成功与否
            JsmsUtil.sendSMS(djRegisterApplication.getMobile(), "171137", map);
        } catch (Exception e) {
            logger.error("操作失败", e);
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 30000, rollbackFor = {
            RuntimeException.class, Exception.class})
    public ServerResponse addUser(MainUser user) {

        // 判断用户是否已经存在
        MainUser existUser = this.userMapper.findUserByMobile(user.getMobile());
        if (null != existUser) {
            user.setId(existUser.getId());
            this.userMapper.updateByPrimaryKeySelective(user);
        } else {
            // 新增用户
            user.setId((int) (Math.random() * 50000000) + 50000000 + "" + System.currentTimeMillis());
            user.setCreateDate(new Date());
            user.setIsDel(false);
            user.setIsJob(false);
            this.userMapper.insert(user);
        }
        return ServerResponse.createBySuccessMessage("ok");
    }

    /**
     * 查询已申请注册的用户列表
     *
     * @param pageDTO
     * @return
     */
    public ServerResponse<PageInfo> getRegisterList(PageDTO pageDTO, String applicationStatus, String searchKey) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        try {
            List<RegisterApplicationDTO> registerList = djRegisterApplicationMapper.getAllRegistList(applicationStatus, searchKey);
            PageInfo pageResult = new PageInfo(registerList);
            pageResult.setList(registerList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }

    /**
     * 根据申请ID查询对应的申请信息
     *
     * @param id 审核ID
     * @return
     */
    public ServerResponse getRegisterInfoById(String id) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            DjRegisterApplication djRegisterApplication = djRegisterApplicationMapper.selectByPrimaryKey(id);
            String[] imgArr = djRegisterApplication.getCardImage().split(",");
            StringBuilder imgStr = new StringBuilder();
            StringBuilder imgUrlStr = new StringBuilder();
            StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
            djRegisterApplication.setCardImage(imgStr.toString());
            Map<String, Object> map = BeanUtils.beanToMap(djRegisterApplication);
            map.put("cardImageUrl", imgUrlStr.toString());
            map.put("businessLicenseUrl", address + djRegisterApplication.getBusinessLicense());
            City city = iCityMapper.selectByPrimaryKey(djRegisterApplication.getCityId());
            if (city != null && StringUtils.isNotBlank(city.getId())) {
                map.put("cityName", city.getName());
            }
            //查询用户当前已拥有的部门、岗位
            MainUser mainUser = userMapper.findUserByMobile(djRegisterApplication.getUserName());
            if (mainUser != null && StringUtils.isNotBlank(mainUser.getJobId())) {
                Job job = jobMapper.selectByPrimaryKey(mainUser.getJobId());
                //获取部门名称，岗位名称
                map.put("departmentName", job.getDepartmentName());
                map.put("jobName", job.getName());
            }
            return ServerResponse.createBySuccess("查询成功", map);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }


    /**
     * 申请新身份
     *
     * @param djRegisterApplication
     * @return
     */
    public ServerResponse insertApplyNewStatus(String userId, DjRegisterApplication djRegisterApplication) {
        try {
            MainUser mainUser = userMapper.selectByPrimaryKey(userId);
            Map<String,Object> map=new HashMap();
            map.put("cityId",djRegisterApplication.getCityId());
            map.put("mobile",mainUser.getMobile());
            djRegisterApplication.getApplicationType().split(",");
            map.put("split",djRegisterApplication.getApplicationType().split(","));
            List<DjRegisterApplication> djRegisterApplications = djRegisterApplicationMapper.queryDeWeight(map);
            if (djRegisterApplications.size() > 0)
                return ServerResponse.createByErrorMessage("请勿重复申请");
            Example example = new Example(DjRegisterApplication.class);
            example.createCriteria().andEqualTo(DjRegisterApplication.CITY_ID, djRegisterApplication.getCityId())
                    .andEqualTo(DjRegisterApplication.DATA_STATUS, 0)
                    .andEqualTo(DjRegisterApplication.MOBILE, mainUser.getMobile())
                    .andNotEqualTo(DjRegisterApplication.APPLICATION_STATUS, 2);
            djRegisterApplications = djRegisterApplicationMapper.selectByExample(example);
            if (djRegisterApplications.size() <= 0)
                return ServerResponse.createByErrorMessage("暂无店铺供应商信息,请先注册");
            djRegisterApplication.setUserName(djRegisterApplications.get(0).getUserName());
            djRegisterApplication.setPassWord(djRegisterApplications.get(0).getPassWord());
            djRegisterApplication.setName(djRegisterApplications.get(0).getName());
            djRegisterApplication.setMobile(djRegisterApplications.get(0).getMobile());
            djRegisterApplication.setCardNumber(djRegisterApplications.get(0).getCardNumber());
            djRegisterApplication.setCardImage(djRegisterApplications.get(0).getCardImage());
            if (djRegisterApplicationMapper.insert(djRegisterApplication) > 0)
                return ServerResponse.createBySuccessMessage("申请成功");
        } catch (Exception e) {
            logger.error("申请失败", e);
            return ServerResponse.createByErrorMessage("申请失败");
        }
        return ServerResponse.createByErrorMessage("申请失败");
    }


}
