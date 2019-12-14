package com.dangjia.acg.api.app.design;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe 附件管理接口
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/14 2:32 PM
 */
@FeignClient("dangjia-service-master")
@Api(value = "附件管理接口", description = "附件管理接口")
public interface EnclosureAPI {
    /**
     * showdoc
     *
     * @param userToken     可选 string userToken
     * @param userId        可选 string userId
     * @param houseId       必选 string 房子ID
     * @param name          必选 string 文件名称
     * @param enclosure     必选 string 文件地址
     * @param enclosureType 必选 string 附件类型0:设计上传的设计图，1:扩展(默认为0）
     * @param remarks       可选 string 操作描述
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 商品3.0---app端/新版设计
     * @title 添加附件
     * @description 添加附件
     * @method POST
     * @url master/web/enclosure/addEnclosure
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 31
     * @Author: Ruking 18075121944
     * @Date: 2019/12/14 2:44 PM
     */
    @PostMapping("web/enclosure/addEnclosure")
    @ApiOperation(value = "添加附件", notes = "添加附件")
    ServerResponse addEnclosure(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("userToken") String userToken,
                                @RequestParam("userId") String userId,
                                @RequestParam("houseId") String houseId,
                                @RequestParam("name") String name,
                                @RequestParam("enclosure") String enclosure,
                                @RequestParam("enclosureType") int enclosureType,
                                @RequestParam("remarks") String remarks);

    /**
     * showdoc
     *
     * @param enclosureId 必选 string 附件ID
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 商品3.0---app端/新版设计
     * @title 删除附件
     * @description 删除附件
     * @method POST
     * @url master/web/enclosure/deleteEnclosure
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 32
     * @Author: Ruking 18075121944
     * @Date: 2019/12/14 2:47 PM
     */
    @PostMapping("web/enclosure/deleteEnclosure")
    @ApiOperation(value = "删除附件", notes = "删除附件")
    ServerResponse deleteEnclosure(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("enclosureId") String enclosureId);

    /**
     * showdoc
     *
     * @param houseId       必选 string 房子ID
     * @param enclosureType 必选 string 附件类型0:设计上传的设计图，1:扩展(默认为0）
     * @return {"res":1000,"msg":{"resultObj":[{返回参数说明},{返回参数说明}],"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 商品3.0---app端/新版设计
     * @title 查询附件
     * @description 查询附件
     * @method POST
     * @url  master/web/Enclosure/selectEnclosureList
     * @return_param id string enclosureId/附件ID
     * @return_param createDate string 创建时间
     * @return_param modifyDate string 修改时间
     * @return_param dataStatus int 数据状态:0=正常，1=删除
     * @return_param name string 文件名称
     * @return_param houseId string 房子ID
     * @return_param enclosure string 文件地址
     * @return_param enclosureUrl string 文件全地址
     * @return_param enclosureType int 附件类型0:设计上传的设计图，1:扩展
     * @return_param remarks string 操作描述
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 33
     * @Author: Ruking 18075121944
     * @Date: 2019/12/14 2:48 PM
     */
    @PostMapping("web/Enclosure/selectEnclosureList")
    @ApiOperation(value = "查询附件", notes = "查询附件")
    ServerResponse selectEnclosureList(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("houseId") String houseId,
                                       @RequestParam("enclosureType") int enclosureType);
}
