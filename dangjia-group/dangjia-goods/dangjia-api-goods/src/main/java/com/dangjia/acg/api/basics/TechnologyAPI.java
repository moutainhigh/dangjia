package com.dangjia.acg.api.basics;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
   * @类 名： TechnologyController
   * @功能描述： TODO
   * @作者信息： zmj
   * @创建时间： 2018-9-10上午9:25:10
 */
@Api(description = "工艺管理接口")
@FeignClient("dangjia-service-goods")
public interface TechnologyAPI {

    /**
     * 新增工艺说明
     * @Title: insertTechnology
     * @Description: TODO
     * @param: @param name
     * @param: @param content
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
    @PostMapping("/basics/technology/insertTechnology")
    @ApiOperation(value = "新增工艺说明", notes = "新增工艺说明")
    public ServerResponse insertTechnology(@RequestParam("name")String name, @RequestParam("content")String content,
                                           @RequestParam("workerTypeId")String workerTypeId,@RequestParam("type")Integer type,
                                           @RequestParam("image")String image,@RequestParam("materialOrWorker")Integer materialOrWorker);
    /**
    * 修改工艺说明
    * @Title: updateTechnology
    * @Description: TODO
    * @param: @param id
    * @param: @param name
    * @param: @param content
    * @param: @return
    * @return: JsonResult
    * @throws
    */
    @PostMapping("/basics/technology/updateTechnology")
    @ApiOperation(value = "修改工艺说明", notes = "修改工艺说明")
    public ServerResponse updateTechnology(@RequestParam("id")String id,@RequestParam("name")String name,
                                           @RequestParam("content")String content,@RequestParam("type")Integer type,
                                           @RequestParam("image")String image);
    /**
     * 删除工艺说明
     * @Title: deleteTechnology
     * @Description: TODO
     * @param: @param id
     * @param: @return
     * @return: JsonResult
     * @throws
     */
    @PostMapping("/basics/technology/deleteTechnology")
    @ApiOperation(value = "删除工艺说明", notes = "删除工艺说明")
    public ServerResponse deleteTechnology(String id);
    /**
     * 查询所有工艺说明
     * @Title: queryTechnology
     * @Description: TODO
     * @param: @param name
     * @param: @param content
     * @param: @return
     * @return: JsonResult
     * @throws
     */
    @PostMapping("/basics/technology/queryTechnology")
    @ApiOperation(value = "查询所有工艺说明", notes = "查询所有工艺说明")
    public ServerResponse<PageInfo> queryTechnology(@RequestParam("pageDTO") PageDTO pageDTO,@RequestParam("workerTypeId") String workerTypeId,@RequestParam("name")String name,
                                                    @RequestParam("materialOrWorker")Integer materialOrWorker);
   /**
    * 新增人工商品关联工艺
    * @Title: insertWokerTechnology
    * @Description: TODO
    * @param: @param workerGoodsId
    * @param: @param tIdArr
    * @param: @return
    * @return: JsonResult
    * @throws
    */
   @PostMapping("/basics/technology/insertWokerTechnology")
   @ApiOperation(value = "新增人工商品关联工艺", notes = "新增人工商品关联工艺")
    public ServerResponse insertWokerTechnology(String workerGoodsId,String tIdArr);

   /**
    * 根据商品id查询人工商品关联工艺实体
    * @Title: queryTechnologyByWgId
    * @Description: TODO
    * @param: @param worker_goods_id
    * @param: @return
    * @return: JsonResult
    * @throws
    */
   @PostMapping("/basics/technology/queryTechnologyByWgId")
   @ApiOperation(value = "根据商品id查询人工商品关联工艺实体", notes = "根据商品id查询人工商品关联工艺实体")
    public ServerResponse queryTechnologyByWgId(String workerGoodsId);

    /**
     * 根据houseFlow查询精算下的验收工艺
     */



    
}
