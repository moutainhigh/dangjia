package com.dangjia.acg.service.supplier;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.supplier.DjRegisterApplicationMapper;
import com.dangjia.acg.modle.supplier.DjRegisterApplication;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


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
            djRegisterApplication.setPassward(DigestUtils.md5Hex(djRegisterApplication.getPassward()));
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

}
