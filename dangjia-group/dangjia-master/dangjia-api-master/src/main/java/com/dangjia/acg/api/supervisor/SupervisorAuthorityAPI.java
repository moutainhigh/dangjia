package com.dangjia.acg.api.supervisor;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Api(description = "督导权限配置接口")
@FeignClient("dangjia-service-master")
public interface SupervisorAuthorityAPI {

    /**
     * showdoc
     *
     * @param pageNum    必选 int 页码
     * @param pageSize   必选 int 记录数
     * @param cityId     必选 string 城市ID
     * @param memberId   必选 string 督导ID
     * @param visitState 必选 string -1：全部,0:待确认开工,1:装修中,2:休眠中,3:已完工
     * @param searchKey  必选 string 搜索关键字
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 工匠端升级/中台/督导
     * @title （web）督导授权待选择列表
     * @description 督导授权待选择列表
     * @method POST
     * @url master/web/supervisor/getStayAuthorityList
     * @return_param houseId string 房子ID
     * @return_param houseName string 房子名称
     * @return_param memberName string 业主名称
     * @return_param memberId string 业主ID
     * @return_param memberPhone string 业主手机号
     * @return_param visitState Integer 0待确认开工,1装修中,2休眠中,3已完工,4提前结束装修 5提前结束装修申请中
     * @return_param constructionDate Date 开工时间
     * @return_param selection Boolean 当前督导是否选中
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2020/1/7 8:28 PM
     */
    @PostMapping("web/supervisor/getStayAuthorityList")
    @ApiOperation(value = "督导授权待选择列表", notes = "督导授权待选择列表")
    ServerResponse getStayAuthorityList(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("pageDTO") PageDTO pageDTO,
                                        @RequestParam("cityId") String cityId,
                                        @RequestParam("memberId") String memberId,
                                        @RequestParam("visitState") Integer visitState,
                                        @RequestParam("searchKey") String searchKey);

    /**
     * showdoc
     *
     * @param pageNum    必选 int 页码
     * @param pageSize   必选 int 记录数
     * @param memberId   必选 string 督导ID
     * @param visitState 必选 string -1：全部,0:待确认开工,1:装修中,2:休眠中,3:已完工
     * @param searchKey  必选 string 搜索关键字
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 工匠端升级/中台/督导
     * @title （web）获取督导授权列表
     * @description 获取督导授权列表
     * @method POST
     * @url master/web/supervisor/getAuthorityList
     * @return_param houseId string 房子ID
     * @return_param houseName string 房子名称
     * @return_param memberName string 业主名称
     * @return_param memberId string 业主ID
     * @return_param memberPhone string 业主手机号
     * @return_param visitState Integer 0待确认开工,1装修中,2休眠中,3已完工,4提前结束装修 5提前结束装修申请中
     * @return_param constructionDate Date 开工时间
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 2
     * @Author: Ruking 18075121944
     * @Date: 2020/1/7 8:44 PM
     */
    @PostMapping("web/supervisor/getAuthorityList")
    @ApiOperation(value = "获取督导授权列表", notes = "获取督导授权列表")
    ServerResponse getAuthorityList(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("pageDTO") PageDTO pageDTO,
                                    @RequestParam("memberId") String memberId,
                                    @RequestParam("visitState") Integer visitState,
                                    @RequestParam("searchKey") String searchKey);

    /**
     * showdoc
     *
     * @param memberId 必选 string 督导ID
     * @param houseIds 必选 string 房子ID","号分割
     * @param userId   必选 string 中台登录的用户ID
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/中台/督导
     * @title （web）督导添加授权
     * @description 督导添加授权
     * @method POST
     * @url master/web/supervisor/addAuthority
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 3
     * @Author: Ruking 18075121944
     * @Date: 2020/1/7 8:45 PM
     */
    @PostMapping("web/supervisor/addAuthority")
    @ApiOperation(value = "督导添加授权", notes = "督导添加授权")
    ServerResponse addAuthority(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("memberId") String memberId,
                                @RequestParam("houseIds") String houseIds,
                                @RequestParam("userId") String userId);

    /**
     * showdoc
     *
     * @param memberId 必选 string 督导ID
     * @param houseIds 必选 string 房子ID","号分割
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/中台/督导
     * @title （web）督导移除授权
     * @description 督导移除授权
     * @method POST
     * @url master/web/supervisor/deleteAuthority
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 4
     * @Author: Ruking 18075121944
     * @Date: 2020/1/7 8:47 PM
     */
    @PostMapping("web/supervisor/deleteAuthority")
    @ApiOperation(value = "督导移除授权", notes = "督导移除授权")
    ServerResponse deleteAuthority(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("memberId") String memberId,
                                   @RequestParam("houseIds") String houseIds);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param cityId    必选 string 城市ID
     * @return {"res":1000,"msg":{"resultObj":[{返回参数说明}],"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/中台/督导
     * @title （APP）督导首页数据获取
     * @description 督导首页数据获取
     * @method POST
     * @url master/app/supervisor/getSupHomePage
     * @return_param num Integer 工地数量
     * @return_param name string 条目名称
     * @return_param sortNum Integer 1：全部，2：在施工地，3：本周新开，4：本月竣工，5：今日开工，6：连续3天未开工，7：超期施工，8：维保工地
     * @return_param image string 条目图标
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 5
     * @Author: Ruking 18075121944
     * @Date: 2020/1/10 11:22 AM
     */
    @PostMapping("app/supervisor/getSupHomePage")
    @ApiOperation(value = "督导首页数据获取", notes = "督导首页数据获取")
    ServerResponse getSupHomePage(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("userToken") String userToken,
                                  @RequestParam("cityId") String cityId);

    /**
     * showdoc
     *
     * @param pageNum   必选 int 页码
     * @param pageSize  必选 int 记录数
     * @param userToken 必选 string userToken
     * @param cityId    必选 string 城市ID
     * @param sortNum   必选 Integer 1：全部，2：在施工地，3：本周新开，4：本月竣工，5：今日开工，6：连续3天未开工，7：超期施工，8：维保工地
     * @param type      必选 Integer 0：默认，1：附近，2：价格降序（维保才有），3：价格升序（维保才有）
     * @param latitude  可选 string 用户纬度
     * @param longitude 可选 string 用户经度
     * @param searchKey 可选 string 搜索关键字
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 工匠端升级/中台/督导
     * @title （APP）督导获取工地列表
     * @description 督导获取工地列表
     * @method POST
     * @url master/app/supervisor/getSupHouseList
     * @return_param houseId string 房子ID
     * @return_param houseName string 房子名称
     * @return_param personnel string 今日施工/参与人员
     * @return_param constructionPeriod string 工期
     * @return_param address string 地址
     * @return_param price string 价格
     * @return_param maintenanceRecordId string 质保申请ID
     * @return_param type int 0:施工,1:维保
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 6
     * @Author: Ruking 18075121944
     * @Date: 2020/1/10 11:26 AM
     */
    @PostMapping("app/supervisor/getSupHouseList")
    @ApiOperation(value = "督导获取工地列表", notes = "督导获取工地列表")
    ServerResponse getSupHouseList(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("pageDTO") PageDTO pageDTO,
                                   @RequestParam("userToken") String userToken,
                                   @RequestParam("cityId") String cityId,
                                   @RequestParam("sortNum") Integer sortNum,
                                   @RequestParam("type") Integer type,
                                   @RequestParam("latitude") String latitude,
                                   @RequestParam("longitude") String longitude,
                                   @RequestParam("searchKey") String searchKey);

    /**
     * showdoc
     *
     * @param userToken           必选 string userToken
     * @param houseId             必选 string 房子ID
     * @param maintenanceRecordId 可选 string 质保申请ID
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/中台/督导
     * @title （APP）督导获取工地详情
     * @description 督导获取工地详情
     * @method POST
     * @url master/app/supervisor/getSupHouseDetails
     * @return_param houseId string 房子ID
     * @return_param houseName string 房子名称
     * @return_param address string 地址
     * @return_param latitude string 纬度
     * @return_param longitude string 经度
     * @return_param type Integer 0:施工,1:维保
     * @return_param buttonType Integer 0:显示底部按钮,1:不显示底部按钮
     * @return_param flowDatas List<> 记录集合
     * @return_param dataMap Map 维保数据集
     * @return_param ---- flowDatas ----
     * @return_param memberName string 工匠名称
     * @return_param memberId string 工匠ID
     * @return_param workerTypeName string 工种名称
     * @return_param workerTypeId string workertyid
     * @return_param workerType string workertype
     * @return_param image string 工种图片
     * @return_param completion string 进程名称，为空不显示
     * @return_param mapList List<> 进程数据集合
     * @return_param ---- flowDatas-mapList ----
     * @return_param keyName string 键，如：工期
     * @return_param valueName string 值，如：35/75
     * @return_param ---- dataMap ----
     * @return_param orderProgressList List<> 节点流水记录
     * @return_param workerList List<> 维修参与人员
     * @return_param productList List<> 维保商品列表
     * @return_param ---- dataMap-orderProgressList ----
     * @return_param id string 节点id
     * @return_param createDate string 创建时间
     * @return_param progressOrderId string 订单ID（各种订单）
     * @return_param progressType string 订单类型
     * @return_param nodeCode string 节点编码
     * @return_param nodeName string 节点名称
     * @return_param nodeDescribe string 节点描述
     * @return_param associatedOperation String 按钮编码
     * @return_param associatedOperationName string 按钮描述(有就显示）
     * @return_param nodeStatus int 节点显示状态(1黑勾，2黑叉，3红叉，4灰掉，5红点，6白点）
     * @return_param ---- dataMap-workerList ----
     * @return_param workerId string 工人ID
     * @return_param workerName string 工人名称
     * @return_param labelName string 工种名
     * @return_param headImage string 工人头像
     * @return_param mobile string 工人手机
     * @return_param ---- dataMap-productList ----
     * @return_param productId string 商品ID
     * @return_param productName string 商品名称
     * @return_param productTemplateId string 商品模板ID
     * @return_param image string 商品图片
     * @return_param imageSingle string 商品图片(单张)
     * @return_param price double 商品单价
     * @return_param storefrontId string 店铺ID
     * @return_param storefrontName string 店铺名称
     * @return_param storefrontIcon string 店铺图标
     * @return_param valueNameArr string 商品属性规格名称
     * @return_param valueIdArr string 商品属性规格ID
     * @return_param unitId string 单位ID
     * @return_param unitName string 商品单位名称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 7
     * @Author: Ruking 18075121944
     * @Date: 2020/1/10 6:34 PM
     */
    @PostMapping("app/supervisor/getSupHouseDetails")
    @ApiOperation(value = "督导获取工地详情", notes = "督导获取工地详情")
    ServerResponse getSupHouseDetails(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("userToken") String userToken,
                                      @RequestParam("houseId") String houseId,
                                      @RequestParam("maintenanceRecordId") String maintenanceRecordId);

}
