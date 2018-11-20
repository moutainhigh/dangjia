package com.dangjia.acg.service;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jmessage.api.sensitiveword.SensitiveWordClient;
import cn.jmessage.api.sensitiveword.SensitiveWordListResult;
import cn.jmessage.api.sensitiveword.SensitiveWordStatusResult;
import org.springframework.stereotype.Service;


/**
 * 敏感词维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@Service
public class SensitiveWordService extends BaseService {


    /**
     * 添加敏感词
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param words  敏感词数组一个词长度最多为10，默认支持100个敏感词
     */
    public  void addSensitiveWord(String appType,String[] words) {
        try {

            SensitiveWordClient client = new SensitiveWordClient(getAppkey(appType), getMasterSecret(appType));
            ResponseWrapper result = client.addSensitiveWords(words);
            LOG.info("response code: " + result.responseCode + " content " + result.responseContent);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     * 修改敏感词
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param newWord 新的敏感词
     * @param oldWord 旧的敏感词
     */
    public  void updateSensitiveWord(String appType,String newWord, String oldWord) {
        try {

            SensitiveWordClient client = new SensitiveWordClient(getAppkey(appType), getMasterSecret(appType));
            ResponseWrapper result = client.updateSensitiveWord(newWord, oldWord);
            LOG.info("response code: " + result.responseCode + " content " + result.responseContent);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }
    /**
     * 修改敏感词
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param word 被删除的敏感词
     */
    public  void deleteSensitiveWord(String appType,String word) {
        try {

            SensitiveWordClient client = new SensitiveWordClient(getAppkey(appType), getMasterSecret(appType));
            ResponseWrapper result = client.deleteSensitiveWord(word);
            LOG.info("response code: " + result.responseCode + " content " + result.responseContent);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     * 获取敏感词列表
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param start 起始序号从0开始
     * @param count 查询条数，最多2000
     */
    public  SensitiveWordListResult getSensitiveWordList(String appType,int start, int count) {
        try {

            SensitiveWordClient client = new SensitiveWordClient(getAppkey(appType), getMasterSecret(appType));
            SensitiveWordListResult result = client.getSensitiveWordList(start, count);
            LOG.info(result.toString());
            return  result;
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            return  null;
        } catch (APIRequestException e) {
            LOG.error("Error response from server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return  null;
        }
    }

    /**
     * 更新敏感词功能状态
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param status 敏感词开关状态，1表示开启过滤，0表示关闭敏感词过滤
     */
    public  void updateSensitiveWordStatus(String appType,int status) {
        try {

            SensitiveWordClient client = new SensitiveWordClient(getAppkey(appType), getMasterSecret(appType));
            ResponseWrapper result = client.updateSensitiveWordStatus(status);
            LOG.info("response code: " + result.responseCode + " content " + result.responseContent);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     * 获取敏感词功能状态
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     *
     * @return 敏感词开关状态，1表示开启过滤，0表示关闭敏感词过滤，-1表示获取失败
     */
    public  Integer getSensitiveWordStatus(String appType) {
        try {

            SensitiveWordClient client = new SensitiveWordClient(getAppkey(appType), getMasterSecret(appType));
            SensitiveWordStatusResult result = client.getSensitiveWordStatus();
            LOG.info(result.toString());
            return result.getStatus();
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            return -1;
        } catch (APIRequestException e) {
            LOG.error("Error response from server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return -1;
        }
    }
}
