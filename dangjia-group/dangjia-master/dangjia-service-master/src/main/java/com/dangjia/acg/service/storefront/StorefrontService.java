package com.dangjia.acg.service.storefront;

import cn.jiguang.common.utils.StringUtils;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.storefront.IStorefrontMapper;
import com.dangjia.acg.model.storefront.Storefront;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorefrontService {

    /**
     * 声明日志
     */
    private static Logger logger = LoggerFactory.getLogger(StorefrontService.class);

    @Autowired
    private IStorefrontMapper istorefrontMapper;

    public ServerResponse addStorefront(String userToken, Storefront storefront) {

        try {
            //店铺名称不能大于10个字
            String storefrontName=storefront.getStorefrontName();
            if(storefrontName.length()>10)
            {
                ServerResponse.createByErrorMessage("店铺名称不能大于10个字!");
            }
            //店铺地址限制字数30个字，支持字母、数字、汉字
            String storefrontAddress=storefront.getStorefrontAddress();
            if(storefrontAddress.length()>30)
            {
                ServerResponse.createByErrorMessage("店铺地址不能大于30个字!");
            }
            //店铺介绍限制字数20个字，支持字母、数字、汉字
            String storefrontDesc=storefront.getStorefrontDesc();
            if(storefrontDesc.length()>20)
            {
                ServerResponse.createByErrorMessage("店铺介绍不能大于20个字!");
            }
            int i = istorefrontMapper.insert(storefront);
            if (i > 0) {
                ServerResponse.createBySuccessMessage("新增成功!");
            } else {
                ServerResponse.createBySuccessMessage("新增失败!");
            }
        } catch (Exception e) {
            logger.error("查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
        return null;
    }

    public ServerResponse updateStorefront(String userToken, Storefront storefront) {

        try {

            int i = istorefrontMapper.updateByPrimaryKey(storefront);
            if (i > 0) {
                ServerResponse.createBySuccessMessage("修改成功!");
            } else {
                ServerResponse.createBySuccessMessage("修改失败!");
            }
        } catch (Exception e) {
            logger.error("查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
        return null;
    }
}
