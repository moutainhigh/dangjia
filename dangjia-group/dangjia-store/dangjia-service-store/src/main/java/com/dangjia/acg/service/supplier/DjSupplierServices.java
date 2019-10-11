package com.dangjia.acg.service.supplier;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.supplier.DjSupplierMapper;
import com.dangjia.acg.modle.supplier.DjSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 8/10/2019
 * Time: 下午 2:22
 */
@Service
public class DjSupplierServices {

    @Autowired
    private DjSupplierMapper djSupplierMapper;


    /**
     * 供应商基础信息维护
     * @param djSupplier
     * @return
     */
    public ServerResponse updateBasicInformation(DjSupplier djSupplier) {
        try {
            if(CommonUtil.isEmpty(djSupplier.getName()))
                return ServerResponse.createByErrorMessage("用户名不能为空");
            if(CommonUtil.isEmpty(djSupplier.getTelephone()))
                return ServerResponse.createByErrorMessage("电话号码不能为空");
            if(CommonUtil.isEmpty(djSupplier.getAddress()))
                return ServerResponse.createByErrorMessage("地址不能为空");
            if(CommonUtil.isEmpty(djSupplier.getEmail()))
                return ServerResponse.createByErrorMessage("邮件不能为空");
            if(CommonUtil.isEmpty(djSupplier.getCheckPeople()))
                return ServerResponse.createByErrorMessage("联系人不能为空");
            if(djSupplierMapper.updateByPrimaryKeySelective(djSupplier)>0)
                return ServerResponse.createBySuccessMessage("编辑成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("编辑失败");
        }
        return ServerResponse.createByErrorMessage("编辑失败");
    }
}
