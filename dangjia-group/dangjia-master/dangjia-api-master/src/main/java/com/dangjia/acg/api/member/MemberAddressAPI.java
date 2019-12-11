package com.dangjia.acg.api.member;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * @author Ruking.Cheng
 * @descrilbe 地址接口
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/10 8:40 PM
 */
@FeignClient("dangjia-service-master")
@Api(value = "地址接口", description = "地址接口")
public interface MemberAddressAPI {

    /**
     * showdoc
     *
     * @param userToken      必选 string userToken
     * @param renovationType 必选 int 是否是装修地址:0：否，1：是
     * @param defaultType    必选 int 是否是默认地址:0：否，1：是
     * @param name           必选 string 业主姓名
     * @param mobile         必选 string 业主手机
     * @param cityName       必选 string 省/市/区
     * @param address        必选 string 详细地址
     * @param inputArea      必选 BigDecimal 录入面积
     * @param longitude      必选 string 经度
     * @param latitude       必选 string 纬度
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 商品3.0---app端/我的地址
     * @title 新增地址
     * @description 新增地址
     * @method POST
     * @url master/member/address/insertAddress
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2019/12/11 2:24 PM
     */
    @PostMapping("member/address/insertAddress")
    @ApiOperation(value = "新增地址", notes = "新增地址")
    ServerResponse insertAddress(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("userToken") String userToken,
                                 @RequestParam("renovationType") int renovationType,
                                 @RequestParam("defaultType") int defaultType,
                                 @RequestParam("name") String name,
                                 @RequestParam("mobile") String mobile,
                                 @RequestParam("cityName") String cityName,
                                 @RequestParam("address") String address,
                                 @RequestParam("inputArea") BigDecimal inputArea,
                                 @RequestParam("longitude") String longitude,
                                 @RequestParam("latitude") String latitude);

    /**
     * showdoc
     *
     * @param userToken   必选 string userToken
     * @param addressId   必选 string 地址ID
     * @param defaultType 必选 int 是否是默认地址:0：否，1：是
     * @param name        必选 string 业主姓名
     * @param mobile      必选 string 业主手机
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 商品3.0---app端/我的地址
     * @title 业主修改地址
     * @description 业主修改地址
     * @method POST
     * @url master/member/address/updataAddress
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 2
     * @Author: Ruking 18075121944
     * @Date: 2019/12/11 2:39 PM
     */
    @PostMapping("member/address/updataAddress")
    @ApiOperation(value = "业主修改地址", notes = "业主修改地址")
    ServerResponse updataAddress(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("userToken") String userToken,
                                 @RequestParam("addressId") String addressId,
                                 @RequestParam("defaultType") int defaultType,
                                 @RequestParam("name") String name,
                                 @RequestParam("mobile") String mobile);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param addressId 必选 string 地址ID
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 商品3.0---app端/我的地址
     * @title 删除地址
     * @description 删除地址
     * @method POST
     * @url master/member/address/deleteAddress
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 3
     * @Author: Ruking 18075121944
     * @Date: 2019/12/11 2:42 PM
     */
    @PostMapping("member/address/deleteAddress")
    @ApiOperation(value = "删除地址", notes = "删除地址")
    ServerResponse deleteAddress(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("userToken") String userToken,
                                 @RequestParam("addressId") String addressId);

    /**
     * showdoc
     *
     * @param addressId 必选 string 地址ID
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 商品3.0---app端/我的地址
     * @title 查找用户地址
     * @description 查找用户地址
     * @method POST
     * @url master/member/address/selectAddress
     * @return_param id string id
     * @return_param createDate string 创建时间
     * @return_param modifyDate string 修改时间
     * @return_param dataStatus int 数据状态:0=正常，1=删除
     * @return_param memberId string 会员编号
     * @return_param houseId string 房子ID
     * @return_param renovationType Integer 是否是装修地址:0：否，1：是
     * @return_param defaultType Integer 是否是默认地址:0：否，1：是
     * @return_param cityName string 省/市/区
     * @return_param address string 详细地址
     * @return_param inputArea BigDecimal 录入面积
     * @return_param longitude string 经度
     * @return_param latitude string 纬度
     * @return_param name string 业主姓名
     * @return_param mobile string 业主手机
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 4
     * @Author: Ruking 18075121944
     * @Date: 2019/12/11 2:43 PM
     */
    @PostMapping("member/address/selectAddress")
    @ApiOperation(value = "查找用户地址", notes = "查找用户地址")
    ServerResponse selectAddress(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("addressId") String addressId);

    /**
     * showdoc
     *
     * @param pageNum        必选 int 页码
     * @param pageSize       必选 int 记录数
     * @param userToken      必选 string userToken
     * @param renovationType 必选 int -1:全部，0：非装修地址，1：装修地址
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 商品3.0---app端/我的地址
     * @title 查找用户地址列表
     * @description 查找用户地址列表
     * @method POST
     * @url master/member/address/selectAddressList
     * @return_param id string id
     * @return_param createDate string 创建时间
     * @return_param modifyDate string 修改时间
     * @return_param dataStatus int 数据状态:0=正常，1=删除
     * @return_param memberId string 会员编号
     * @return_param houseId string 房子ID
     * @return_param renovationType Integer 是否是装修地址:0：否，1：是
     * @return_param defaultType Integer 是否是默认地址:0：否，1：是
     * @return_param cityName string 省/市/区
     * @return_param address string 详细地址
     * @return_param inputArea BigDecimal 录入面积
     * @return_param longitude string 经度
     * @return_param latitude string 纬度
     * @return_param name string 业主姓名
     * @return_param mobile string 业主手机
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 5
     * @Author: Ruking 18075121944
     * @Date: 2019/12/11 3:12 PM
     */
    @PostMapping("member/address/selectAddressList")
    @ApiOperation(value = "查找用户地址列表", notes = "查找用户地址列表")
    ServerResponse selectAddressList(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("pageDTO") PageDTO pageDTO,
                                     @RequestParam("userToken") String userToken,
                                     @RequestParam("renovationType") Integer renovationType);


}
