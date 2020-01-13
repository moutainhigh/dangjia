package com.dangjia.acg.controller.member;

import com.dangjia.acg.api.member.MemberAddressAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.service.member.MemberAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * @author Ruking.Cheng
 * @descrilbe 地址接口实现
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/10 8:44 PM
 */
@RestController
public class MemberAddressController implements MemberAddressAPI {

    @Autowired
    private MemberAddressService memberAddressService;

    @Override
    @ApiMethod
    public ServerResponse insertAddress(HttpServletRequest request, String userToken, Integer renovationType,
                                        Integer defaultType, String name, String mobile, String cityId, String cityName,
                                        String address, BigDecimal inputArea, String longitude, String latitude) {
        return memberAddressService.insertAddress(userToken, renovationType,
                defaultType, name, mobile, cityId, cityName, address, inputArea, longitude, latitude);
    }

    @Override
    @ApiMethod
    public ServerResponse updataAddress(HttpServletRequest request, String userToken, MemberAddress memberAddress) {
        return memberAddressService.updataAddress(userToken, memberAddress);
    }

    @Override
    @ApiMethod
    public ServerResponse deleteAddress(HttpServletRequest request, String userToken, String addressId) {
        return memberAddressService.deleteAddress(userToken, addressId);
    }

    @Override
    @ApiMethod
    public ServerResponse selectAddress(HttpServletRequest request, String addressId) {
        request.setAttribute("isShow", "true");
        return memberAddressService.selectAddress(addressId);
    }

    @Override
    @ApiMethod
    public ServerResponse selectAddressList(HttpServletRequest request, PageDTO pageDTO, String userToken, Integer renovationType) {
        request.setAttribute("isShow", "true");
        return memberAddressService.selectAddressList(pageDTO, userToken, renovationType);
    }
}
