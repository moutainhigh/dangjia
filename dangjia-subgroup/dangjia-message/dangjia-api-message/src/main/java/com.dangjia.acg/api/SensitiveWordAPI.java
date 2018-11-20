package com.dangjia.acg.api;

import cn.jmessage.api.sensitiveword.SensitiveWordListResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 敏感词维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@FeignClient("dangjia-service-message")
@Api(value = "敏感词维护接口", description = "敏感词维护维护接口")
public interface SensitiveWordAPI {


    /**
     * 添加敏感词
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param words  敏感词数组一个词长度最多为10，默认支持100个敏感词
     */
    @RequestMapping(value = "addSensitiveWord", method = RequestMethod.POST)
    @ApiOperation(value = "添加敏感词", notes = "添加敏感词")
    void addSensitiveWord(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String[] words);

    /**
     * 修改敏感词
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param newWord 新的敏感词
     * @param oldWord 旧的敏感词
     */
    @RequestMapping(value = "updateSensitiveWord", method = RequestMethod.POST)
    @ApiOperation(value = "修改敏感词", notes = "修改敏感词")
    void updateSensitiveWord(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="newWord",value = "新的敏感词")@RequestParam("newWord") String newWord,
            @ApiParam(name ="oldWord",value = "旧的敏感词")@RequestParam("oldWord") String oldWord) ;
    /**
     * 修改敏感词
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param word 被删除的敏感词
     */
    @RequestMapping(value = "deleteSensitiveWord", method = RequestMethod.POST)
    @ApiOperation(value = "修改敏感词", notes = "修改敏感词")
    void deleteSensitiveWord(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="word",value = "被删除的敏感词")@RequestParam("word") String word);

    /**
     * 获取敏感词列表
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param start 起始序号从0开始
     * @param count 查询条数，最多2000
     */
    @RequestMapping(value = "getSensitiveWordList", method = RequestMethod.POST)
    @ApiOperation(value = "获取敏感词列表", notes = "获取敏感词列表")
    SensitiveWordListResult getSensitiveWordList(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="start",value = "起始序号从0开始")@RequestParam("start") int start,
            @ApiParam(name ="count",value = "查询条数，最多2000")@RequestParam("count") int count);

    /**
     * 更新敏感词功能状态
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param status 敏感词开关状态，1表示开启过滤，0表示关闭敏感词过滤
     */
    @RequestMapping(value = "updateSensitiveWordStatus", method = RequestMethod.POST)
    @ApiOperation(value = "更新敏感词功能状态", notes = "更新敏感词功能状态")
    void updateSensitiveWordStatus(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="status",value = " 敏感词开关状态，1表示开启过滤，0表示关闭敏感词过滤")@RequestParam("status") int status);

    /**
     * 获取敏感词功能状态
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     *
     * @return 敏感词开关状态，1表示开启过滤，0表示关闭敏感词过滤，-1表示获取失败
     */
    @RequestMapping(value = "getSensitiveWordStatus", method = RequestMethod.POST)
    @ApiOperation(value = "获取敏感词功能状态", notes = "获取敏感词功能状态")
    Integer getSensitiveWordStatus(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType")  String appType);
}
