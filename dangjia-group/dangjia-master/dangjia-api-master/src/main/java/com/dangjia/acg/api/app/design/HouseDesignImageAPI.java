package com.dangjia.acg.api.app.design;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 11:17
 */
@FeignClient("dangjia-service-master")
@Api(value = "设计图接口", description = "设计图接口")
public interface HouseDesignImageAPI {


    /**
     * showdoc
     *
     * @param houseId houseId 必选 string 房子ID
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/设计模块
     * @title 设计师将设计图或施工图发送给业主
     * @description 设计师将设计图或施工图发送给业主
     * @method POST
     * @url master/web/design/sendPictures
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2019/5/7 1:55 PM
     */
    @PostMapping("web/design/sendPictures")
    @ApiOperation(value = "发送设计图给业主", notes = "发送设计图给业主")
    ServerResponse sendPictures(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("houseId") String houseId);


    /**
     * showdoc
     *
     * @param pageNum      必选 int 页码
     * @param pageSize     必选 int 记录数
     * @param designerType 必选 int 0：未支付和设计师未抢单，1：带量房，2：平面图，3：施工图，4：完工
     * @param searchKey    可选 string 业主手机号/房子名称
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 当家接口文档/设计模块
     * @title 设计任务列表
     * @description 设计任务列表
     * @method POST
     * @url master/web/design/getList
     * @return_param designerOk int 设计状态,0未确定设计师,4设计待抢单,1已支付-设计师待量房,9量房图发给业主,5平面图发给业主,6平面图审核不通过,7通过平面图待发施工图,2已发给业主施工图,8施工图片审核不通过,3施工图(全部图)审核通过
     * @return_param houseId string houseId
     * @return_param residential string 小区名
     * @return_param building string building
     * @return_param unit string unit
     * @return_param number string number
     * @return_param schedule string 进度
     * @return_param name string 业主姓名
     * @return_param nickName string 昵称
     * @return_param mobile string 电话
     * @return_param image string 平面图
     * @return_param imageUrl string 平面图URL
     * @return_param decorationType int 装修类型0表示没有开始，1远程设计，2自带设计，3共享装修
     * @return_param operatorId string 操作人ID
     * @return_param operatorName string 操作人名字
     * @return_param operatorMobile string 操作人电话
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 2
     * @Author: Ruking 18075121944
     * @Date: 2019/5/7 1:57 PM
     */
    @PostMapping("web/design/getList")
    @ApiOperation(value = "设计师任务列表", notes = "设计师任务列表")
    ServerResponse getDesignList(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("pageDTO") PageDTO pageDTO,
                                 @RequestParam("designerType") int designerType,
                                 @RequestParam("searchKey") String searchKey,
                                 @RequestParam("workerKey") String workerKey);


    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param houseId   必选 string houseId
     * @param type      必选 int 0:不通过,1:通过
     * @return {"res":1000,"msg":{"resultCode":1000,"resultObj":{resultCode=1009时，返回参数说明},"resultMsg":"成功"} }
     * @catalog 当家接口文档/设计模块
     * @title 业主审核平面图或施工图
     * @description 业主审核平面图或施工图
     * @method POST
     * @url master/app/design/houseDesignImage/checkPass
     * @return_param message string 头部提示信息
     * @return_param butName string 协议名称
     * @return_param butUrl string 协议地址
     * @return_param moneyMessage string 金额描叙
     * @return_param businessOrderNumber string 订单ID
     * @return_param type int 支付任务type
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 3
     * @Author: Ruking 18075121944
     * @Date: 2019/5/7 2:04 PM
     */
    @PostMapping("app/design/houseDesignImage/checkPass")
    @ApiOperation(value = "设计通过", notes = "设计通过")
    ServerResponse checkPass(@RequestParam("userToken") String userToken,
                             @RequestParam("houseId") String houseId,
                             @RequestParam("type") int type);


    /**
     * showdoc
     *
     * @param houseId 必选 string houseId
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/设计模块
     * @title 房子作废
     * @description 房子作废
     * @method POST
     * @url master/web/house/invalid
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 4
     * @Author: Ruking 18075121944
     * @Date: 2019/5/7 2:09 PM
     */
    @PostMapping("web/house/invalid")
    @ApiOperation(value = "房子作废", notes = "房子作废")
    ServerResponse invalidHouse(@RequestParam("houseId") String houseId);

    @PostMapping("app/design/houseDesignImage/upgradeDesign")
    @ApiOperation(value = "升级设计", notes = "升级设计")
    ServerResponse upgradeDesign(@RequestParam("userToken") String userToken,
                                 @RequestParam("houseId") String houseId,
                                 @RequestParam("designImageTypeId") String designImageTypeId,
                                 @RequestParam("selected") int selected);

    /**
     * showdoc
     *
     * @param userToken 可选 string 可以为空
     * @param houseId   必选 string 房子ID
     * @param userId    可选 string 可以为空
     * @param image     必选 string 图片只上传一张
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/设计模块
     * @title 添加平面图
     * @description 添加平面图
     * @method POST
     * @url master/app/design/setPlaneMap
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 5
     * @Author: Ruking 18075121944
     * @Date: 2019/5/7 2:11 PM
     */
    @PostMapping("app/design/setPlaneMap")
    @ApiOperation(value = "添加平面图", notes = "添加平面图")
    ServerResponse setPlaneMap(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("userToken") String userToken,
                               @RequestParam("houseId") String houseId,
                               @RequestParam("userId") String userId,
                               @RequestParam("image") String image);

    /**
     * showdoc
     *
     * @param userToken 可选 string 可以为空
     * @param houseId   必选 string 房子ID
     * @param userId    可选 string 可以为空
     * @param imageJson 必选 string 图片Json串[{"name":"图片名称","image":"图片地址","sort":1},{"name":"图片名称","image":"图片地址","sort":1}],sort为优先级，数字越小越靠前
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/设计模块
     * @title 添加施工图
     * @description 添加施工图
     * @method POST
     * @url master/app/design/setConstructionPlans
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 6
     * @Author: Ruking 18075121944
     * @Date: 2019/5/7 2:14 PM
     */
    @PostMapping("app/design/setConstructionPlans")
    @ApiOperation(value = "添加施工图", notes = "添加施工图")
    ServerResponse setConstructionPlans(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("userToken") String userToken,
                                        @RequestParam("houseId") String houseId,
                                        @RequestParam("userId") String userId,
                                        @RequestParam("imageJson") String imageJson);

    /**
     * showdoc
     *
     * @param userToken 可选 string 可以为空
     * @param houseId   必选 string 房子ID
     * @param userId    可选 string 可以为空
     * @param images    必选 string 图片","号分割
     * @param elevator  必选 int 是否电梯房：0:否，1：是
     * @param floor     必选 string 楼层
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/设计模块
     * @title 添加量房
     * @description 添加量房
     * @method POST
     * @url master/app/design/setQuantityRoom
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 7
     * @Author: Ruking 18075121944
     * @Date: 2019/5/7 2:16 PM
     */
    @PostMapping("app/design/setQuantityRoom")
    @ApiOperation(value = "添加量房", notes = "添加量房")
    ServerResponse setQuantityRoom(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("userToken") String userToken,
                                   @RequestParam("houseId") String houseId,
                                   @RequestParam("userId") String userId,
                                   @RequestParam("images") String images,
                                   @RequestParam("elevator") Integer elevator,
                                   @RequestParam("floor") String floor);

    /**
     * showdoc
     *
     * @param houseId 必选 string houseId
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/设计模块
     * @title 查询量房
     * @description 查询量房
     * @method POST
     * @url master/app/design/getQuantityRoom
     * @return_param id int id
     * @return_param modifyDate string 修改时间
     * @return_param createDate string 创建时间
     * @return_param houseId string 房子ID
     * @return_param memberId string 处理人的用户ID
     * @return_param userId string 中台处理人的用户ID
     * @return_param type int 事务类型：0:量房，1平面图，2施工图
     * @return_param operationType int 操作类型：0:执行，1：跳过
     * @return_param userType int 操作人类型：-1为未知，0为App，1为中台
     * @return_param userName string 操作人名称
     * @return_param elevator int 是否电梯房：0:否，1：是
     * @return_param floor string 楼层
     * @return_param images List 图片集合
     * @return_param images-id string id
     * @return_param images-modifyDate string 修改时间
     * @return_param images-createDate string 创建时间
     * @return_param images-houseId string 房子ID
     * @return_param images-quantityRoomId string 操作ID
     * @return_param images-name string 名称
     * @return_param images-image string 图片地址
     * @return_param images-sort int 优先顺序
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 8
     * @Author: Ruking 18075121944
     * @Date: 2019/5/7 2:18 PM
     */
    @PostMapping("app/design/getQuantityRoom")
    @ApiOperation(value = "查询量房", notes = "查询量房")
    ServerResponse getQuantityRoom(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("houseId") String houseId);

    /**
     * showdoc
     *
     * @param houseId 必选 string houseId
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/设计模块
     * @title 获取平面图
     * @description 获取平面图
     * @method POST
     * @url master/app/design/getPlaneMap
     * @return_param id int id
     * @return_param modifyDate string 修改时间
     * @return_param createDate string 创建时间
     * @return_param houseId string 房子ID
     * @return_param memberId string 处理人的用户ID
     * @return_param userId string 中台处理人的用户ID
     * @return_param type int 事务类型：0:量房，1平面图，2施工图
     * @return_param operationType int 操作类型：0:执行，1：跳过
     * @return_param userType int 操作人类型：-1为未知，0为App，1为中台
     * @return_param userName string 操作人名称
     * @return_param images List 图片集合
     * @return_param images-id string id
     * @return_param images-modifyDate string 修改时间
     * @return_param images-createDate string 创建时间
     * @return_param images-houseId string 房子ID
     * @return_param images-quantityRoomId string 操作ID
     * @return_param images-name string 名称
     * @return_param images-image string 图片地址
     * @return_param images-sort int 优先顺序
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 9
     * @Author: Ruking 18075121944
     * @Date: 2019/5/7 2:28 PM
     */
    @PostMapping("app/design/getPlaneMap")
    @ApiOperation(value = "获取平面图", notes = "获取平面图")
    ServerResponse getPlaneMap(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("houseId") String houseId);

    /**
     * showdoc
     *
     * @param houseId 必选 string houseId
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/设计模块
     * @title 获取施工图
     * @description 获取施工图
     * @method POST
     * @url master/app/design/getConstructionPlans
     * @return_param id int id
     * @return_param modifyDate string 修改时间
     * @return_param createDate string 创建时间
     * @return_param houseId string 房子ID
     * @return_param memberId string 处理人的用户ID
     * @return_param userId string 中台处理人的用户ID
     * @return_param type int 事务类型：0:量房，1平面图，2施工图
     * @return_param operationType int 操作类型：0:执行，1：跳过
     * @return_param userType int 操作人类型：-1为未知，0为App，1为中台
     * @return_param userName string 操作人名称
     * @return_param images List 图片集合
     * @return_param images-id string id
     * @return_param images-modifyDate string 修改时间
     * @return_param images-createDate string 创建时间
     * @return_param images-houseId string 房子ID
     * @return_param images-quantityRoomId string 操作ID
     * @return_param images-name string 名称
     * @return_param images-image string 图片地址
     * @return_param images-sort int 优先顺序
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 10
     * @Author: Ruking 18075121944
     * @Date: 2019/5/7 2:31 PM
     */
    @PostMapping("app/design/getConstructionPlans")
    @ApiOperation(value = "获取施工图", notes = "获取施工图")
    ServerResponse getConstructionPlans(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("houseId") String houseId);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param houseId   必选 string houseId
     * @param type      必选 Integer 0:查看页面，1：审核页面
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/设计模块
     * @title 获取设计图
     * @description 获取设计图
     * @method POST
     * @url master/app/design/getDesign
     * @return_param message String 头部提示信息
     * @return_param historyRecord int 是否暂时历史记录0：不显示,1：显示
     * @return_param buttonList List 按钮
     * @return_param buttonList-buttonType int 0：跳转URL，1：需要修改设计，2：确认，3：申请额外修改设计，4：申请后需要修改设计，5：申请后确认
     * @return_param buttonList-buttonTypeName string 显示名称
     * @return_param buttonList-url string buttonType==0是的跳转路由
     * @return_param data List 图片集合
     * @return_param data-id string id
     * @return_param data-modifyDate string 修改时间
     * @return_param data-createDate string 创建时间
     * @return_param data-houseId string 房子ID
     * @return_param data-quantityRoomId string 操作ID
     * @return_param data-name string 名称
     * @return_param data-image string 图片地址
     * @return_param data-sort int 优先顺序
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 11
     * @Author: Ruking 18075121944
     * @Date: 2019/5/7 2:33 PM
     */
    @PostMapping("app/design/getDesign")
    @ApiOperation(value = "获取设计图", notes = "获取设计图")
    ServerResponse getDesign(@RequestParam("request") HttpServletRequest request,
                             @RequestParam("userToken") String userToken,
                             @RequestParam("houseId") String houseId,
                             @RequestParam("type") Integer type);

    /**
     * showdoc
     *
     * @param pageNum  必选 int 页码
     * @param pageSize 必选 int 记录数
     * @param houseId  必选 string houseId
     * @param type     必选 string 事务类型：0:量房，1平面图，2施工图
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 当家接口文档/设计模块
     * @title 获取历史记录
     * @description 获取历史记录
     * @method POST
     * @url master/app/design/getOdlQuantityRoomList
     * @return_param id int id
     * @return_param modifyDate string 修改时间
     * @return_param createDate string 创建时间
     * @return_param houseId string 房子ID
     * @return_param memberId string 处理人的用户ID
     * @return_param userId string 中台处理人的用户ID
     * @return_param type int 事务类型：0:量房，1平面图，2施工图
     * @return_param operationType int 操作类型：0:执行，1：跳过
     * @return_param userType int 操作人类型：-1为未知，0为App，1为中台
     * @return_param userName string 操作人名称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 12
     * @Author: Ruking 18075121944
     * @Date: 2019/5/7 2:33 PM
     */
    @PostMapping("app/design/getOdlQuantityRoomList")
    @ApiOperation(value = "获取历史记录", notes = "获取历史记录")
    ServerResponse getOdlQuantityRoomList(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("pageDTO") PageDTO pageDTO,
                                          @RequestParam("houseId") String houseId,
                                          @RequestParam("type") int type);

    /**
     * showdoc
     *
     * @param quantityRoomId 必选 string 记录ID
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/设计模块
     * @title 通过ID获取对应的信息
     * @description 通过ID获取对应的信息
     * @method POST
     * @url master/app/design/getIdQuantityRoom
     * @return_param id int id
     * @return_param modifyDate string 修改时间
     * @return_param createDate string 创建时间
     * @return_param houseId string 房子ID
     * @return_param memberId string 处理人的用户ID
     * @return_param userId string 中台处理人的用户ID
     * @return_param type int 事务类型：0:量房，1平面图，2施工图
     * @return_param operationType int 操作类型：0:执行，1：跳过
     * @return_param userType int 操作人类型：-1为未知，0为App，1为中台
     * @return_param userName string 操作人名称
     * @return_param images List 图片集合
     * @return_param images-id string id
     * @return_param images-modifyDate string 修改时间
     * @return_param images-createDate string 创建时间
     * @return_param images-houseId string 房子ID
     * @return_param images-quantityRoomId string 操作ID
     * @return_param images-name string 名称
     * @return_param images-image string 图片地址
     * @return_param images-sort int 优先顺序
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 13
     * @Author: Ruking 18075121944
     * @Date: 2019/5/7 2:38 PM
     */
    @PostMapping("app/design/getIdQuantityRoom")
    @ApiOperation(value = "通过ID获取对应的信息", notes = "通过ID获取对应的信息")
    ServerResponse getIdQuantityRoom(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("quantityRoomId") String quantityRoomId);


    @PostMapping("web/house/statistics")
    @ApiOperation(value = "精算统计或设计统计", notes = "精算统计或设计统计")
    ServerResponse getHouseStatistics(@RequestParam("cityId") String cityId,
                                      @RequestParam("workerTypeId") String workerTypeId,
                                      @RequestParam("pageDTO") PageDTO pageDTO,
                                      @RequestParam("startDate") String startDate,
                                      @RequestParam("endDate") String endDate);
}
