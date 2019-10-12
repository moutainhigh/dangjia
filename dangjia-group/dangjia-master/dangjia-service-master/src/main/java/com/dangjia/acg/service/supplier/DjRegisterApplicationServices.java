package com.dangjia.acg.service.supplier;

import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.mapper.supplier.DjRegisterApplicationMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.supplier.DjRegisterApplication;
import com.dangjia.acg.modle.user.MainUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private DjRegisterApplicationMapper djRegisterApplicationMapper;

    @Autowired
    private UserMapper userMapper;
    /**
     * 供应商/店铺注册
     * @param djRegisterApplication
     * @return
     */
    public ServerResponse registerSupAndStorefront(DjRegisterApplication djRegisterApplication) {
        try {
            if(CommonUtil.isEmpty(djRegisterApplication.getUserName()))
                return ServerResponse.createByErrorMessage("用户名不能为空");
            if(CommonUtil.isEmpty(djRegisterApplication.getMobile()))
                return ServerResponse.createByErrorMessage("电话号码不能为空");
            if(CommonUtil.isEmpty(djRegisterApplication.getCardNumber()))
                return ServerResponse.createByErrorMessage("身份证号码不能为空");
            Example example=new Example(DjRegisterApplication.class);
            example.createCriteria().andEqualTo(DjRegisterApplication.CITY_ID,djRegisterApplication.getCityId())
                    .andEqualTo(DjRegisterApplication.DATA_STATUS,0)
                    .andEqualTo(DjRegisterApplication.MOBILE,djRegisterApplication.getMobile())
                    .andNotEqualTo(DjRegisterApplication.APPLICATION_STATUS,2)
                    .andNotEqualTo(DjRegisterApplication.APPLICATION_TYPE,djRegisterApplication.getApplicationType());
            if(djRegisterApplicationMapper.selectByExample(example).size()>0)
                return ServerResponse.createByErrorMessage("申请已存在");
            djRegisterApplication.setDataStatus(0);
            djRegisterApplication.setPassword(DigestUtils.md5Hex(djRegisterApplication.getPassword()));
            if(djRegisterApplicationMapper.insert(djRegisterApplication)>0)
                return ServerResponse.createBySuccessMessage("申请成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("申请失败");
        }
        return ServerResponse.createByErrorMessage("申请失败");
    }


    /**
     *
     * @param mobile
     * @return
     */
    public ServerResponse querySupAndStorefront(String mobile,String cityId) {
        try {
            Example example=new Example(DjRegisterApplication.class);
            example.createCriteria().andEqualTo(DjRegisterApplication.CITY_ID,cityId)
                    .andEqualTo(DjRegisterApplication.DATA_STATUS,0)
                    .andEqualTo(DjRegisterApplication.MOBILE,mobile)
                    .andNotEqualTo(DjRegisterApplication.APPLICATION_STATUS,2);
            List<DjRegisterApplication> djRegisterApplications = djRegisterApplicationMapper.selectByExample(example);
            if(djRegisterApplications.size()>0)
                return ServerResponse.createBySuccess("查询成功",djRegisterApplications.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
        return ServerResponse.createByErrorMessage("查询失败");
    }


     /**
     * 提交审核注册的供应商或店铺
     * @param registerId 申请ID
     * @param isAdopt 是否通过 1=通过  2=不通过
     * @param departmentId 部门ID
     * @param jobId  岗位ID
     * @return
     */
    public ServerResponse checkSupAndStorefront(HttpServletRequest request, String registerId, Integer isAdopt, String departmentId, String jobId) {
        try {

            String userID = request.getParameter(Constants.USERID);
            DjRegisterApplication djRegisterApplication = djRegisterApplicationMapper.selectByPrimaryKey(registerId);

            djRegisterApplication.setApplicationStatus(isAdopt);
            djRegisterApplication.setAuditUserId(userID);//审核人
            djRegisterApplication.setModifyDate(new Date());//最后一次操作时间
            djRegisterApplicationMapper.updateByPrimaryKeySelective(djRegisterApplication);
            Map map=new HashMap();
            if(isAdopt==1){
                map.put("msg","审核通过");
            }else{
                map.put("msg","审核失败");
            }

            //注入用户信息
            MainUser user=new MainUser();
            user.setPassword(djRegisterApplication.getPassword());
            user.setUsername(djRegisterApplication.getName());
            user.setMobile(djRegisterApplication.getMobile());
            user.setUserType(djRegisterApplication.getApplicationType());
            user.setInsertUid(userID);
            user.setDepartmentId(departmentId);
            user.setJobId(jobId);
            addUser(user);
            //发送短信通知至注册申请人手机成功与否
            JsmsUtil.sendSMS(djRegisterApplication.getMobile(),"16885",map);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
        return ServerResponse.createByErrorMessage("查询失败");
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 30000, rollbackFor = {
            RuntimeException.class, Exception.class})
    public ServerResponse addUser(MainUser user) {

        // 判断用户是否已经存在
        MainUser existUser = this.userMapper.findUserByMobile(user.getMobile());
        if (null != existUser) {
            user.setId(existUser.getId());
            this.userMapper.updateByPrimaryKeySelective(user);
        }else {
            // 新增用户
            user.setId((int) (Math.random() * 50000000) + 50000000 + "" + System.currentTimeMillis());
            user.setCreateDate(new Date());
            user.setIsDel(false);
            user.setIsJob(false);
            this.userMapper.insert(user);
        }
        return ServerResponse.createBySuccessMessage("ok");
    }

}
