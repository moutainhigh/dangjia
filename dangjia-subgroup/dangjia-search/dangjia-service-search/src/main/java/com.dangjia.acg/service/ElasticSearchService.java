package com.dangjia.acg.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageBean;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.Validator;
import com.dangjia.acg.config.ElasticsearchConfiguration;
import com.dangjia.acg.dto.ElasticSearchDTO;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @author: QiYuXiang
 * @date: 2018/5/3
 */
@Service
public class ElasticSearchService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchService.class);
  private  TransportClient client = ElasticsearchConfiguration.client;
  private String indexName="dangjia";

  public String saveESJson(String jsonStr,String tableTypeName) {
    IndexResponse indexResponse;
    try {
      indexResponse = client.prepareIndex(indexName, tableTypeName).setSource(JSONObject.parseObject(jsonStr)).get();
      LOGGER.info("ES 插入完成");
    } catch (RuntimeException e) {
      throw new BaseException(ServerCode.JSON_TYPE_ERROR, "JSON格式不正确，请检查JSON");
    } catch (Exception e) {
      throw new BaseException(ServerCode.ES_ERROR, "保存搜索引擎失败");
    }
    return indexResponse.getId();
  }



  /**
   * 在ES中模糊搜索内容
   * @return
   */
  public List<JSONObject> searchESJson( ElasticSearchDTO elasticSearchDTO){

    List<String> fieldList = elasticSearchDTO.getFieldList();
    String searchContent = elasticSearchDTO.getSearchContent();
    Map<String,Integer> sortMap = elasticSearchDTO.getSortMap();
    String tableTypeName = elasticSearchDTO.getTableTypeName();
    String[] fields = new String[fieldList.size()];
    fieldList.toArray(fields);
    try {
      //查询搜索对象
      SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName);
      searchRequestBuilder.setTypes(tableTypeName).setQuery(searchContent == null ?QueryBuilders.matchAllQuery() : QueryBuilders.multiMatchQuery(searchContent, fields).slop(1));
      return searchResponse(searchRequestBuilder,sortMap);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 在ES中模糊搜索内容（分页）
   */
  public PageBean<JSONObject> searchESJsonPage(ElasticSearchDTO elasticSearchDTO){


    PageDTO pageDTO = elasticSearchDTO.getPageDTO();
    Map<String,String> paramMap = elasticSearchDTO.getParamMap();
    List<String> fieldList = elasticSearchDTO.getFieldList();
    String searchContent = elasticSearchDTO.getSearchContent();
    Map<String,Integer> sortMap = elasticSearchDTO.getSortMap();
    String tableTypeName = elasticSearchDTO.getTableTypeName();

    LOGGER.info("searchContent",searchContent);
    String[] fields = new String[fieldList.size()];
    fieldList.toArray(fields);
    try {
      SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName).setTypes(tableTypeName);
      BoolQueryBuilder subCodeQuery = QueryBuilders.boolQuery();
      setParamTerm(subCodeQuery, paramMap);
      if(searchContent == null) {
        subCodeQuery.must(QueryBuilders.matchAllQuery());
      }else{
        subCodeQuery.must(QueryBuilders.multiMatchQuery(searchContent, fields).slop(1));
      }
        //查询
      searchRequestBuilder.setQuery(subCodeQuery);

      return searchResponse(searchRequestBuilder,sortMap,pageDTO);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 在ES中精准搜索内容（分页）
   * @return
   */
  public PageBean<JSONObject> searchPreciseJsonPage(ElasticSearchDTO elasticSearchDTO){
    PageDTO pageDTO = elasticSearchDTO.getPageDTO();
    Map<String,String> paramMap = elasticSearchDTO.getParamMap();
    Map<String,Integer> sortMap = elasticSearchDTO.getSortMap();
    String tableTypeName = elasticSearchDTO.getTableTypeName();

    Validator.notNull(paramMap, "过滤字段不能为空");
    BoolQueryBuilder subCodeQuery = QueryBuilders.boolQuery();
    setParamTerm(subCodeQuery, paramMap);
    try {
      SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName);
      //查询
      searchRequestBuilder.setTypes(tableTypeName).setQuery(subCodeQuery);

      return searchResponse(searchRequestBuilder,sortMap,pageDTO);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
    }
    return null;
  }


  /**
   * 根据单个字段查询数据
   * @return
   */
  public List<JSONObject> searchPreciseJson(ElasticSearchDTO elasticSearchDTO){
    try {
      Map<String,String> paramMap = elasticSearchDTO.getParamMap();
      List<String> objectList = elasticSearchDTO.getFieldNameValue();
      String fildsName = elasticSearchDTO.getFieldName();
      Map<String,Integer> sortMap = elasticSearchDTO.getSortMap();
      String tableTypeName = elasticSearchDTO.getTableTypeName();

      SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName);
      BoolQueryBuilder subCodeQuery = QueryBuilders.boolQuery();
      setParamTerm(subCodeQuery, paramMap);
      subCodeQuery.must(QueryBuilders.termsQuery(fildsName,objectList));
      //查询
      searchRequestBuilder.setTypes(tableTypeName).setQuery(subCodeQuery).get();
      return searchResponse(searchRequestBuilder,sortMap);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
    }
    return null;
  }


  /**
   * 根据ESID查询数据
   * @param tableTypeName
   * @return
   */

  public JSONObject getSearchJsonId( String tableTypeName,String prepareId){
    List<JSONObject> strings = new ArrayList<JSONObject>();
    try {
      SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName).setQuery(QueryBuilders.termsQuery("id",prepareId)).setTypes(tableTypeName);
      strings = searchResponse(searchRequestBuilder,null);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
    }
    if(strings == null || strings.size() <=0){
      return null;
    }else {
      return strings.get(0);
    }
  }

  public String getESId(String tableTypeName,String prepareId){
    JSONObject strings = getSearchJsonId(tableTypeName,prepareId);
    if(!CommonUtil.isEmpty(strings)&&strings.get("esId")!=null){
      return (String)strings.get("esId");
    }
    return null;
  }



  /**
   * 处理查询数据(不分页)
   * @param searchRequestBuilder
   * @return
   */
   public List<JSONObject> searchResponse(SearchRequestBuilder searchRequestBuilder,Map<String,Integer> sortMap){
     //拼接排序规则
     setSortTerm(searchRequestBuilder,sortMap);
     LOGGER.debug("searchRequestBuilder不分页:"+searchRequestBuilder);
     //分页
     searchRequestBuilder.setSize(1000);
     SearchResponse response = searchRequestBuilder.get();

     List<JSONObject> arrList = new ArrayList<JSONObject>();
     SearchHits hits = response.getHits();
     if (null != hits && hits.totalHits() > 0) {
       for (SearchHit hit : hits) {
         String json = hit.getSourceAsString();
         JSONObject jsonObject=JSON.parseObject(json);
         jsonObject.put("esId",hit.getId());
         arrList.add(jsonObject);
       }
     } else {
       LOGGER.info("没有查询到任何结果！");
     }
     return arrList;
   }

  /**
   * 处理查询数据（分页）
   * @param searchRequestBuilder
   * @param pageDTO
   * @return
   */
   public PageBean searchResponse(SearchRequestBuilder searchRequestBuilder,Map<String,Integer> sortMap,PageDTO pageDTO){
     //拼接排序规则
     setSortTerm(searchRequestBuilder,sortMap);
     //分页
     searchRequestBuilder.setFrom(pageDTO.getPageNum()*pageDTO.getPageSize()).setSize(pageDTO.getPageSize());
     LOGGER.debug("searchRequestBuilder分页:"+searchRequestBuilder);
     SearchResponse response = searchRequestBuilder.get();
     List<JSONObject> arrList = new ArrayList<JSONObject>();
     SearchHits hits = response.getHits();
     if (null != hits && hits.totalHits() > 0) {
       for (SearchHit hit : hits) {
         String json = hit.getSourceAsString();
         JSONObject jsonObject=JSON.parseObject(json);
         jsonObject.put("esId",hit.getId());
         arrList.add(jsonObject);
       }
     } else {
       LOGGER.info("没有查询到任何结果！");
     }
     PageBean pageBean = new PageBean();
     pageBean.setList(arrList);
     pageBean.setTotal(hits.getTotalHits());
     pageBean.setPages(Integer.valueOf((int) hits.getTotalHits())/pageDTO.getPageSize());
     pageBean.setPageNum(pageDTO.getPageNum());
     pageBean.setPageSize(pageDTO.getPageSize());
     return pageBean;
   }

  /**
   * 删除
   * @param tableTypeName
   * @param prepareId
   */
   public void deleteResponse( String tableTypeName,String prepareId){
     String eid=getESId(tableTypeName,prepareId);
     DeleteRequestBuilder deleteResponse = client.prepareDelete(indexName,tableTypeName,eid);
     deleteResponse.execute().actionGet();

   }

  /**
   * 修改
   * @param jsonStr
   * @param tableTypeName
   * @param prepareId
   */
  public void updateResponse(String jsonStr, String tableTypeName,String prepareId){
    String eid=getESId(tableTypeName,prepareId);
    UpdateRequestBuilder updateRequestBuilder = ElasticsearchConfiguration.client.prepareUpdate(indexName,tableTypeName,eid);
    updateRequestBuilder.setDoc(jsonStr).get();
  }

  private void setParamTerm(BoolQueryBuilder subCodeQuery,Map paramMap){
    if(paramMap != null) {
      Iterator it = paramMap.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry entry = (Map.Entry) it.next();
        Object key = entry.getKey();
        Object value = entry.getValue();
        String[] values =  StringUtils.split(String.valueOf(value),",");
        if(values.length>1){
            subCodeQuery.must(QueryBuilders.termsQuery(key.toString(), Arrays.asList(values)));
        }else {
            subCodeQuery.must(QueryBuilders.termQuery(key.toString(), value));
        }
      }
    }
  }
  private void setSortTerm(SearchRequestBuilder searchRequestBuilder,Map sortMap){
    //拼接排序规则
    if(!CommonUtil.isEmpty(sortMap)){
      Iterator it = sortMap.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry entry = (Map.Entry) it.next();
        Object key = entry.getKey();
        Integer value = (Integer) entry.getValue();
        if(value==0){
          searchRequestBuilder.addSort(key.toString(), SortOrder.ASC);
        }else{
          searchRequestBuilder.addSort(key.toString(), SortOrder.DESC);
        }
      }
    }
  }
}
