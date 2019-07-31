package com.dangjia.acg.api.sale.rob;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.member.CustomerRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * 抢单模块 API
 * author: ljl
 * Date: 2019/7/27
 * Time: 9:59
 */
@FeignClient("dangjia-service-master")
@Api(value = "抢单模块", description = "抢单模块")
public interface RobAPI {

    /**
     * showdoc
     * @catalog TODO 当家接口文档/设计模块
     * @title TODO
     * @description TODO
     * @method POST
     * @url TODO master/
     * @param request 必选/可选 string TODO
     * @param userId 必选/可选 string TODO
     * @param storeId 必选/可选 string TODO
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @return_param groupid int 用户组id
     * @return_param name string 用户昵称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 99
     * @Author: ljl 18075121944
     * @Date: 2019/7/31 0031 18:04
     */
    @PostMapping(value = "sale/rob/queryRobSingledata")
    @ApiOperation(value = "抢单列表查询", notes = "抢单列表查询")
    ServerResponse queryRobSingledata(@RequestParam("request")HttpServletRequest request,
                                      @RequestParam("userId")String userId,
                                      @RequestParam("storeId")String storeId);

    /**
     * showdoc
     * @catalog TODO 当家接口文档/设计模块
     * @title TODO
     * @description TODO
     * @method POST
     * @url TODO master/
     * @param request 必选/可选 string TODO
     * @param houseId 必选/可选 string TODO
     * @param labelIdArr 必选/可选 string TODO
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @return_param groupid int 用户组id
     * @return_param name string 用户昵称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 99
     * @Author: ljl 18075121944
     * @Date: 2019/7/31 0031 18:04
     */
    @PostMapping(value = "sale/rob/queryCustomerInfo")
    @ApiOperation(value = "抢单详情查询", notes = "抢单详情查询")
    ServerResponse queryCustomerInfo(@RequestParam("request")HttpServletRequest request,
                                     @RequestParam("houseId")String houseId,
                                     @RequestParam("labelIdArr")String labelIdArr,
                                     @RequestParam("memberId")String memberId);

    /**
     * showdoc
     * @catalog
    TODO 当家接口文档/设计模块
     * @title TODO
     * @description TODO
     * @method POST
     * @url TODO master/
     * @param request 必选/可选 string TODO
     * @param memberId 必选/可选 string TODO
     * @param labelId 必选/可选 string TODO
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @return_param groupid int 用户组id
     * @return_param name string 用户昵称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 99
     * @Author: ljl 18075121944
     * @Date: 2019/7/31 0031 18:04
     */
    @PostMapping(value = "sale/rob/addLabel")
    @ApiOperation(value = "新增标签", notes = "新增标签")
    ServerResponse addLabel(@RequestParam("request")HttpServletRequest request,
                            @RequestParam("memberId")String memberId,
                            @RequestParam("labelId")String labelId);


   /**
    * showdoc
    * @catalog TODO 当家接口文档/设计模块
    * @title TODO
    * @description TODO
    * @method POST
    * @url TODO master/
    * @param request 必选/可选 string TODO
    * @param customerRecord 必选/可选 string TODO
    * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
    * @return_param groupid int 用户组id
    * @return_param name string 用户昵称
    * @remark 更多返回错误代码请看首页的错误代码描述
    * @number 99
    * @Author: ljl 18075121944
    * @Date: 2019/7/31 0031 19:33
    */
    @PostMapping(value = "sale/rob/addDescribes")
    @ApiOperation(value = "新增标签", notes = "新增标签")
    ServerResponse addDescribes(@RequestParam("request")HttpServletRequest request,
                                @RequestBody CustomerRecord customerRecord);

    /**
     * showdoc
     * @catalog TODO 当家接口文档/设计模块
     * @title TODO
     * @description TODO
     * @method POST
     * @url TODO master/
     * @param request 必选/可选 string TODO
     * @param clue 必选/可选 string TODO
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @return_param groupid int 用户组id
     * @return_param name string 用户昵称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 99
     * @Author: ljl 18075121944
     * @Date: 2019/7/31 0031 20:06
     */
    @PostMapping(value = "sale/rob/upDateCustomerInfo")
    @ApiOperation(value = "修改客户信息", notes = "修改客户信息")
    ServerResponse upDateCustomerInfo(HttpServletRequest request, Clue clue);
}
