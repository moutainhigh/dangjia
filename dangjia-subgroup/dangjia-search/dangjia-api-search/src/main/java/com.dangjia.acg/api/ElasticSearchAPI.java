package com.dangjia.acg.api;

import com.dangjia.acg.common.model.PageBean;
import com.dangjia.acg.dto.ElasticSearchDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: QiYuXiang
 * @date: 2018/5/3
 */
@FeignClient("dangjia-service-search")
@Api(value = "搜索引擎管理接口", description = "搜索引擎管理接口")
public interface ElasticSearchAPI {

  @RequestMapping(value = "saveESJson", method = RequestMethod.POST)
  @ApiOperation(value = "保存单条数据", notes = "保存单条数据")
  String saveESJson(@ApiParam(name ="jsonStr",value = "JSON数据")@RequestParam("jsonStr") String jsonStr,@ApiParam(name ="indexName",value = "数据库")@RequestParam("indexName") String indexName,@ApiParam(name ="tableTypeName",value = "表")@RequestParam("tableTypeName") String tableTypeName);

  @RequestMapping(value = "saveESJsonList", method = RequestMethod.POST)
  @ApiOperation(value = "保存多条数据", notes = "保存多条数据")
  String saveESJsonList(@ApiParam(name ="jsonStrList",value = "JSONList数据")@RequestBody List<String> jsonStr,@ApiParam(name ="indexName",value = "数据库")@RequestParam("indexName") String indexName,@ApiParam(name ="tableTypeName",value = "表")@RequestParam("tableTypeName") String tableTypeName);

   @RequestMapping(value = "searchESJson", method = RequestMethod.POST)
  @ApiOperation(value = "模糊搜索", notes = "模糊搜索")
  List<String> searchESJson(@ApiParam(name ="elasticSearchDTO",value = "要查询的字段")@RequestBody ElasticSearchDTO elasticSearchDTO);

  @RequestMapping(value = "searchSpeechJson", method = RequestMethod.POST)
  @ApiOperation(value = "语音匹配", notes = "语音匹配")
  Object searchSpeechJson(@ApiParam(name ="elasticSearchDTO",value = "要查询的字段")@RequestBody ElasticSearchDTO elasticSearchDTO);


  @RequestMapping(value = "searchESJsonPage", method = RequestMethod.POST)
  @ApiOperation(value = "模糊搜索（分页）", notes = "模糊搜索（分页）")
  PageBean<String> searchESJsonPage(@ApiParam(name ="elasticSearchDTO",value = "要查询的字段")@RequestBody ElasticSearchDTO elasticSearchDTO);


  @RequestMapping(value = "searchPreciseJsonPage", method = RequestMethod.POST)
  @ApiOperation(value = "精准搜索（分页）", notes = "精准搜索（分页）")
  PageBean<String> searchPreciseJsonPage(@ApiParam(name ="elasticSearchDTO",value = "要查询的字段")@RequestBody ElasticSearchDTO elasticSearchDTO);

  @RequestMapping(value = "searchPupdateResponsereciseJson", method = RequestMethod.POST)
  @ApiOperation(value = "精准搜索单个字段多个value", notes = "精准搜索单个字段多个value")
  List<String> searchPreciseJson(@ApiParam(name ="elasticSearchDTO",value = "要查询的字段")@RequestBody ElasticSearchDTO elasticSearchDTO);

  @RequestMapping(value = "deleteResponse", method = RequestMethod.POST)
  @ApiOperation(value = "根据ID删除", notes = "根据ID删除")
  String deleteResponse(@ApiParam(name ="indexName",value = "数据库")@RequestParam("indexName")String indexName,@ApiParam(name ="tableTypeName",value = "表")@RequestParam("tableTypeName") String tableTypeName,@ApiParam(name ="prepareId",value = "ID")@RequestParam("prepareId")String prepareId);

  @RequestMapping(value = "updateResponse", method = RequestMethod.POST)
  @ApiOperation(value = "根据ID删除", notes = "根据ID删除")
  String updateResponse(@ApiParam(name ="jsonStr",value = "JSON数据")@RequestParam("jsonStr") String jsonStr,@ApiParam(name ="indexName",value = "数据库")@RequestParam("indexName")String indexName,@ApiParam(name ="tableTypeName",value = "表")@RequestParam("tableTypeName") String tableTypeName,@ApiParam(name ="prepareId",value = "ID")@RequestParam("prepareId")String prepareId);

  @RequestMapping(value = "getSearchJsonId", method = RequestMethod.POST)
  @ApiOperation(value = "根据ESID查询", notes = "根据ESID查询")
  String getSearchJsonId(@RequestParam("indexName")String indexName,@RequestParam("tableTypeName")String tableTypeName,@RequestParam("prepareId")String prepareId);


  }
