package com.dangjia.acg.controller;

import com.dangjia.acg.api.ElasticSearchAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.model.PageBean;
import com.dangjia.acg.dto.ElasticSearchDTO;
import com.dangjia.acg.service.ElasticSearchService;
import com.dangjia.acg.service.ElasticSppechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: QiYuXiang
 * @date: 2018/5/3
 */
@RestController
public class ElasticSearchController implements ElasticSearchAPI {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private ElasticSppechService elasticSppechService;

    @Override
    @ApiMethod
    public String saveESJson(String jsonStr, String indexName, String tableTypeName) {
        return elasticSearchService.saveESJson(jsonStr, indexName, tableTypeName);
    }

    @Override
    @ApiMethod
    public List<String> saveESJsonList(List<String> jsonStr, String indexName, String tableTypeName) {
        List<String> esidlist= elasticSearchService.saveESJsonList(jsonStr, indexName, tableTypeName);
        return esidlist;
    }

    @Override
    @ApiMethod
    public List<String> searchESJson(@RequestBody ElasticSearchDTO elasticSearchDTO) {
        return elasticSearchService.searchESJson(elasticSearchDTO.getFieldList(), elasticSearchDTO.getSearchContent(), elasticSearchDTO.getSortMap(), elasticSearchDTO.getIndexName(), elasticSearchDTO.getTableTypeName());
    }

    @Override
    @ApiMethod
    public Object searchSpeechJson(@RequestBody ElasticSearchDTO elasticSearchDTO) {
        return elasticSppechService.searchESJson(elasticSearchDTO.getFieldList(), elasticSearchDTO.getParamMap(), elasticSearchDTO.getSearchContent(), elasticSearchDTO.getSortMap(), elasticSearchDTO.getIndexName(), elasticSearchDTO.getTableTypeName());
    }

    @Override
    @ApiMethod
    public PageBean<String> searchESJsonPage(@RequestBody ElasticSearchDTO elasticSearchDTO) {
        return elasticSearchService.searchESJsonPage(elasticSearchDTO.getPageDTO(), elasticSearchDTO.getParamMap(), elasticSearchDTO.getFieldList(), elasticSearchDTO.getSearchContent(), elasticSearchDTO.getSortMap(), elasticSearchDTO.getIndexName(), elasticSearchDTO.getTableTypeName());
    }

    @Override
    @ApiMethod
    public PageBean<String> searchPreciseJsonPage(@RequestBody ElasticSearchDTO elasticSearchDTO) {
        return elasticSearchService.searchPreciseJsonPage(elasticSearchDTO.getPageDTO(), elasticSearchDTO.getParamMap(), elasticSearchDTO.getSortMap(), elasticSearchDTO.getIndexName(), elasticSearchDTO.getTableTypeName());
    }

    @Override
    @ApiMethod
    public List<String> searchPreciseJson(@RequestBody ElasticSearchDTO elasticSearchDTO) {
        return elasticSearchService.searchPreciseJson(elasticSearchDTO.getFieldName(), elasticSearchDTO.getParamMap(), elasticSearchDTO.<String>getFieldNameValue(), elasticSearchDTO.getSortMap(), elasticSearchDTO.getIndexName(), elasticSearchDTO.getTableTypeName());
    }

    @Override
    @ApiMethod
    public String deleteResponse(String indexName, String tableTypeName, String prepareId) {
        elasticSearchService.deleteResponse(indexName, tableTypeName, prepareId);
        return Constants.SUCCESS;
    }

    @Override
    @ApiMethod
    public String getSearchJsonId(String indexName, String tableTypeName, String prepareId) {
        return elasticSearchService.getSearchJsonId(indexName, tableTypeName, prepareId);
    }


    @Override
    @ApiMethod
    public String updateResponse(String jsonStr, String indexName, String tableTypeName, String prepareId) {
        elasticSearchService.updateResponse(jsonStr, indexName, tableTypeName, prepareId);
        return Constants.SUCCESS;
    }
}
