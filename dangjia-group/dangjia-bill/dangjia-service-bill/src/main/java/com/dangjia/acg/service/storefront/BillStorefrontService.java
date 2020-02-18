package com.dangjia.acg.service.storefront;

import com.dangjia.acg.mapper.storeFront.IBillStorefrontMapper;
import com.dangjia.acg.modle.storefront.Storefront;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
public class BillStorefrontService {
    @Autowired
    private IBillStorefrontMapper billStorefrontMapper;

    public Storefront getStorefrontByUserId(String userId,String cityId){
        Example example=new Example(Storefront.class);
        example.createCriteria().andEqualTo(Storefront.USER_ID,userId)
        .andEqualTo(Storefront.CITY_ID,cityId)
        .andEqualTo(Storefront.DATA_STATUS,0);
        return billStorefrontMapper.selectOneByExample(example);
    }

    public Storefront getStorefrontById(String storefrontId){
        return billStorefrontMapper.selectByPrimaryKey(storefrontId);
    }

}
