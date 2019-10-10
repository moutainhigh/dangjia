package com.dangjia.acg.service.storefront;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.storefront.IStorefrontMapper;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
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
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IStorefrontMapper istorefrontMapper;

    public ServerResponse addStorefront(String userToken, String cityId, String storefrontName,
                                        String storefrontAddress, String storefrontDesc,
                                        String storefrontLogo, String storekeeperName,
                                        String contact, String email) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            String memberId=member.getId();

            //店铺名称不能大于10个字
            if (storefrontName.length() > 10) {
                return ServerResponse.createByErrorMessage("店铺名称不能大于10个字!");
            }
            //店铺地址限制字数30个字，支持字母、数字、汉字
            if (storefrontAddress.length() > 30) {
                return ServerResponse.createByErrorMessage("店铺地址不能大于30个字!");
            }
            //店铺介绍限制字数20个字，支持字母、数字、汉字
            if (storefrontDesc.length() > 20) {
                return ServerResponse.createByErrorMessage("店铺介绍不能大于20个字!");
            }
            Storefront storefront = new Storefront();
            storefront.setMemberId(memberId);
            storefront.setCityId(cityId);
            storefront.setStorefrontName(storefrontName);
            storefront.setStorefrontAddress(storefrontAddress);
            storefront.setStorefrontDesc(storefrontDesc);
            storefront.setStorefrontLogo(storefrontLogo);
            storefront.setStorekeeperName(storekeeperName);
            storefront.setContact(contact);
            storefront.setEmail(email);
            int i = istorefrontMapper.insert(storefront);
            if (i > 0) {
                return ServerResponse.createBySuccessMessage("新增成功!");
            } else {
                return ServerResponse.createBySuccessMessage("新增失败!");
            }
        } catch (Exception e) {
            logger.error("查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    public ServerResponse updateStorefront(String userToken, Storefront storefront) {

        try {
            int i = istorefrontMapper.updateByPrimaryKey(storefront);
            if (i > 0) {
                return ServerResponse.createBySuccessMessage("修改成功!");
            } else {
                return ServerResponse.createBySuccessMessage("修改失败!");
            }
        } catch (Exception e) {
            logger.error("查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
