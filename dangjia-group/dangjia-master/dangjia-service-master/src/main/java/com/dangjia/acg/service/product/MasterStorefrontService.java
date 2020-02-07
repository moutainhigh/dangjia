package com.dangjia.acg.service.product;

import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.mapper.product.IMasterStorefrontMapper;
import com.dangjia.acg.modle.storefront.Storefront;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
public class MasterStorefrontService {
    @Autowired
    private IMasterStorefrontMapper iMasterStorefrontMapper;

    public Storefront getStorefrontByUserId(String userId,String cityId){
        Example example=new Example(Storefront.class);
        example.createCriteria().andEqualTo(Storefront.USER_ID,userId)
        .andEqualTo(Storefront.CITY_ID,cityId)
        .andEqualTo(Storefront.DATA_STATUS,0);
        return iMasterStorefrontMapper.selectOneByExample(example);
    }

    public Storefront getStorefrontById(String storefrontId){
        return iMasterStorefrontMapper.selectByPrimaryKey(storefrontId);
    }

}
