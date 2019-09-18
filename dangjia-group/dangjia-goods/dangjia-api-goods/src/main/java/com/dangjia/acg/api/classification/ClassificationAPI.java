package com.dangjia.acg.api.classification;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe 分类模块
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/19 4:37 PM
 */
@Api(description = "分类模块")
@FeignClient("dangjia-service-goods")
public interface ClassificationAPI {

    /**
     * showdoc
     *
     *   *@param cityId 必选 string 城市ID
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/分类模块
     * @title 获取所有的一级分类
     * @description 获取所有的一级分类
     * @method POST
     * @url goods/classification/getGoodsCategoryList
     * @return_param id string id
     * @return_param createDate string 创建时间
     * @return_param modifyDate string 修改时间
     * @return_param dataStatus int 数据状态 0=正常，1=删除
     * @return_param parentId string 上级id
     * @return_param parentTop string 顶级id
     * @return_param name string 名称
     * @return_param image string 图片
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2019/6/19 6:05 PM
     */
    @PostMapping("classification/getGoodsCategoryList")
    @ApiOperation(value = "获取所有的一级分类", notes = "获取所有的一级分类")
    ServerResponse getGoodsCategoryList(@RequestParam("request") HttpServletRequest request);

    /**
     * showdoc
     *
     *      *@param pageNum    必选 int 页码
     *      * @param pageSize   必选 int 记录数
     *      * @param cityId     必选 string 城市ID
     * @param categoryId 可选 string 分类ID
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 当家接口文档/分类模块
     * @title 获取一级类别下的商品
     * @description 获取一级类别下的商品
     * @method POST
     * @url goods/classification/getProductList
     * @return_param id string id
     * @return_param image string 图片
     * @return_param price Double 销售价
     * @return_param unitName string 单位
     * @return_param type Integer 0:货品，1：人工商品
     * @return_param goodsType Integer 0:材料；1：包工包料
     * @return_param name string 名称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 2
     * @Author: Ruking 18075121944
     * @Date: 2019/6/19 6:03 PM
     */
    @PostMapping("classification/getProductList")
    @ApiOperation(value = "获取一级类别下的商品", notes = "获取一级类别下的商品")
    ServerResponse getProductList(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("pageDTO") PageDTO pageDTO,
                                  @RequestParam("categoryId") String categoryId);

    /**
     * showdoc
     *
     * @param pageNum      必选 int 页码
     * @param pageSize     必选 int 记录数
     * @param cityId       必选 string 城市ID
     * @param workerTypeId 必选 string 工序ID
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 当家接口文档/分类模块
     * @title 获取一级类别下的人工
     * @description 获取一级类别下的人工
     * @method POST
     * @url goods/classification/getWorkerGoodsList
     * @return_param id string id
     * @return_param createDate string 创建时间
     * @return_param modifyDate string 修改时间
     * @return_param dataStatus int 数据状态 0=正常，1=删除
     * @return_param name string name
     * @return_param workerGoodsSn string 人工商品编号
     * @return_param image string 图片
     * @return_param unitName string 单位
     * @return_param unitId string 单位id
     * @return_param price Double 单价
     * @return_param sales int 退货性质0：可退；1不可退
     * @return_param workExplain string 工作说明
     * @return_param workerDec string 商品介绍图片
     * @return_param workerStandard string 工艺标准
     * @return_param workerTypeId string 关联工种id
     * @return_param showGoods string 是否展示1展示；0不展示
     * @return_param otherName string 人工商品别名
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 3
     * @Author: Ruking 18075121944
     * @Date: 2019/6/19 6:09 PM
     */
    /*@PostMapping("classification/getWorkerGoodsList")
    @ApiOperation(value = "获取一级类别下的人工", notes = "获取一级类别下的人工")
    ServerResponse getWorkerGoodsList(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("pageDTO") PageDTO pageDTO,
                                      @RequestParam("workerTypeId") String workerTypeId);*/
}
