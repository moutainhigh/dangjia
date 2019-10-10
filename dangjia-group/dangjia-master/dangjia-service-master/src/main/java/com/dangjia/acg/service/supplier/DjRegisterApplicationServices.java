package com.dangjia.acg.service.supplier;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.supplier.DjRegisterApplicationMapper;
import com.dangjia.acg.modle.supplier.DjRegisterApplication;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
            djRegisterApplication.setPassward(DigestUtils.md5Hex(djRegisterApplication.getPassward()));
            if(djRegisterApplicationMapper.insert(djRegisterApplication)>0)
                return ServerResponse.createBySuccessMessage("申请成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("申请失败");
        }
        return ServerResponse.createByErrorMessage("申请失败");
    }

}
