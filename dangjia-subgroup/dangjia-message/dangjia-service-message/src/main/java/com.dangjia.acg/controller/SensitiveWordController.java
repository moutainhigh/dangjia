package com.dangjia.acg.controller;

import cn.jmessage.api.sensitiveword.SensitiveWordListResult;
import com.dangjia.acg.api.SensitiveWordAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.service.SensitiveWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


/**
 * 敏感词维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@RestController
public class SensitiveWordController implements SensitiveWordAPI {

    @Autowired
    private SensitiveWordService sensitiveWordService;

    /**
     * 添加敏感词
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param words  敏感词数组一个词长度最多为10，默认支持100个敏感词
     */
    @Override
    @ApiMethod
    public  void addSensitiveWord(String appType,String[] words) {
        sensitiveWordService.addSensitiveWord( appType, words);
    }

    /**
     * 修改敏感词
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param newWord 新的敏感词
     * @param oldWord 旧的敏感词
     */
    @Override
    @ApiMethod
    public  void updateSensitiveWord(String appType,String newWord, String oldWord) {
        sensitiveWordService.updateSensitiveWord( appType, newWord,oldWord);
    }
    /**
     * 修改敏感词
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param word 被删除的敏感词
     */
    @Override
    @ApiMethod
    public  void deleteSensitiveWord(String appType,String word) {
        sensitiveWordService.deleteSensitiveWord( appType, word);
    }

    /**
     * 获取敏感词列表
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param start 起始序号从0开始
     * @param count 查询条数，最多2000
     */
    @Override
    @ApiMethod
    public  SensitiveWordListResult getSensitiveWordList(String appType,int start, int count) {
        return   sensitiveWordService.getSensitiveWordList( appType, start, count);
    }

    /**
     * 更新敏感词功能状态
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param status 敏感词开关状态，1表示开启过滤，0表示关闭敏感词过滤
     */
    @Override
    @ApiMethod
    public  void updateSensitiveWordStatus(String appType,int status) {
        sensitiveWordService.updateSensitiveWordStatus( appType, status);
    }

    /**
     * 获取敏感词功能状态
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     *
     * @return 敏感词开关状态，1表示开启过滤，0表示关闭敏感词过滤，-1表示获取失败
     */
    @Override
    @ApiMethod
    public  Integer getSensitiveWordStatus(String appType) {
        return sensitiveWordService.getSensitiveWordStatus( appType);
    }
}
