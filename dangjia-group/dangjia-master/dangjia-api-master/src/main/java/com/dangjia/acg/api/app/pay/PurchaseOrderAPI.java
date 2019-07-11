package com.dangjia.acg.api.app.pay;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(description = "购买单")
@FeignClient("dangjia-service-master")
public interface PurchaseOrderAPI {
    /**
     * showdoc
     *
     * @param houseId 必选 string 房子ID
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/商品模块/未购买模块
     * @title 获取未购买的商品
     * @description 获取未购买的商品
     * @method POST
     * @url master/purchaseOrder/getBudgetMaterialList
     * @return_param price Double 总价
     * @return_param purchaseOrderId string purchaseOrderId
     * @return_param datas List 商品
     * @return_param datas_budgetMaterialId string budgetMaterialId
     * @return_param datas_id string 货品ID
     * @return_param datas_typeName string 人工，材料，服务
     * @return_param datas_type int 1：人工，2：材料，3：服务
     * @return_param datas_name string 商品名
     * @return_param datas_image string 图片
     * @return_param datas_shopCount Double 购买总数
     * @return_param datas_convertCount Double 小单位转成大单位转换后的购买总数
     * @return_param datas_url string 商品详情
     * @return_param datas_buy string 购买性质0：必买；1可取消；2自购
     * @return_param datas_attribute string 属性
     * @return_param datas_price string 最新价格
     * @return_param datas_unitName string 单位
     * @return_param datas_name string 用户昵称
     * @return_param datas_totalPrice string 总价
     * @return_param datas_selection Integer 是否选中：0：否，1：是
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2019/6/29 6:45 PM
     */
    @PostMapping("purchaseOrder/getBudgetMaterialList")
    @ApiOperation(value = "获取未购买的商品", notes = "获取未购买的商品")
    ServerResponse getBudgetMaterialList(@RequestParam("houseId") String houseId);

    /**
     * showdoc
     *
     * @param houseId   必选 string 房子ID
     * @param budgetIds 必选 string 选中的budgetMaterialId“,”分割
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/商品模块/未购买模块
     * @title 添加选中未购买的商品
     * @description 添加选中未购买的商品
     * @method POST
     * @url master/purchaseOrder/setPurchaseOrder
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 2
     * @Author: Ruking 18075121944
     * @Date: 2019/6/29 7:02 PM
     */
    @PostMapping("purchaseOrder/setPurchaseOrder")
    @ApiOperation(value = "添加选中未购买的商品", notes = "添加选中未购买的商品")
    ServerResponse setPurchaseOrder(@RequestParam("houseId") String houseId, @RequestParam("budgetIds") String budgetIds);
}
