package com.dangjia.acg.service.storefront;

import com.dangjia.acg.api.app.member.MemberAPI;
import com.dangjia.acg.common.model.PageDTO;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.storefront.StorefrontListDTO;
import com.dangjia.acg.mapper.storefront.IStorefrontMapper;
import com.dangjia.acg.modle.storefront.Storefront;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorefrontService {

    /**
     * 声明日志
     */
    private static Logger logger = LoggerFactory.getLogger(StorefrontService.class);
    @Autowired
    private IStorefrontMapper istorefrontMapper;
//    @Autowired
//    private CraftsmanConstructionService constructionService;


    public ServerResponse addStorefront(String userToken, String cityId, String storefrontName,
                                        String storefrontAddress, String storefrontDesc,
                                        String storefrontLogo, String storekeeperName,
                                        String contact, String email) {
        try {
//            Object object = constructionService.getMember(userToken);
//            if (object instanceof ServerResponse) {
//                return (ServerResponse) object;
//            }
//            Member worker = (Member) object;

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
            storefront.setUserId(null);
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
//            Object object = constructionService.getMember(userToken);
//            if (object instanceof ServerResponse) {
//                return (ServerResponse) object;
//            }
//            Member worker = (Member) object;
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


    /**
     * 查询供应商申请店铺列表
     *
     * @param searchKey
     * @return
     */
    public ServerResponse querySupplierApplicationShopList(PageDTO pageDTO, String searchKey, String supId, String applicationStatus) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<StorefrontListDTO> storefrontListDTOS = istorefrontMapper.querySupplierApplicationShopList(searchKey, supId, applicationStatus);
            PageInfo pageResult = new PageInfo(storefrontListDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
