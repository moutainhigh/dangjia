package com.dangjia.acg.api.config;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe 前端数据临时缓存
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/12 9:07 PM
 */
@FeignClient("dangjia-service-master")
@Api(value = "前端数据临时缓存接口", description = "前端数据临时缓存接口")
public interface DataCacheAPI {

    /**
     * showdoc
     *
     * @param publicKey 必选 string 缓存公用key：type为0时为houseId
     * @param type      必选 int 类型：0:确认地址保留，1扩展
     * @param dataJson  必选 string 缓存JSON
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/设计模块/数据缓存
     * @title 新增缓存
     * @description 新增缓存
     * @method POST
     * @url master/cache/addDataCache
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2019/12/12 9:34 PM
     */
    @PostMapping("cache/addDataCache")
    @ApiOperation(value = "新增缓存", notes = "新增缓存")
    ServerResponse addDataCache(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("publicKey") String publicKey,
                                @RequestParam("type") Integer type,
                                @RequestParam("dataJson") String dataJson);

    /**
     * showdoc
     *
     * @param publicKey 必选 string 缓存公用key：type为0时为houseId
     * @param type      必选 int 类型：0:确认地址保留，1扩展
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/设计模块/数据缓存
     * @title 查询缓存
     * @description 查询缓存
     * @method POST
     * @url master/cache/getDataCache
     * @return_param id string id
     * @return_param createDate string 创建时间
     * @return_param modifyDate string 修改时间
     * @return_param dataStatus int 数据状态:0=正常，1=删除
     * @return_param publicKey string 缓存公用key：type为0时为houseId
     * @return_param type int 类型：0:确认地址保留，1扩展
     * @return_param dataJson string 缓存JSON
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 2
     * @Author: Ruking 18075121944
     * @Date: 2019/12/12 9:36 PM
     */
    @PostMapping("cache/getDataCache")
    @ApiOperation(value = "查询缓存", notes = "查询缓存")
    ServerResponse getDataCache(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("publicKey") String publicKey,
                                @RequestParam("type") Integer type);
}
