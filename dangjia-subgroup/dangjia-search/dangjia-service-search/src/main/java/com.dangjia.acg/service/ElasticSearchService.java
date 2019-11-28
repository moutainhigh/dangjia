package com.dangjia.acg.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.config.ElasticsearchConfiguration;
import com.dangjia.acg.dto.ElasticSearchDTO;
import com.github.pagehelper.PageInfo;
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
      LOGGER.info("ES 开始插入("+tableTypeName+")"+jsonStr);
      Map map =JSONObject.parseObject(jsonStr);
      indexResponse = client.prepareIndex(indexName+"_"+tableTypeName.toLowerCase(), tableTypeName).setSource(map).get();
      LOGGER.info("ES 插入完成"+jsonStr);
      return indexResponse.getId();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }



  /**
   * 在ES中模糊搜索内容
   * @return
   */
  public List<JSONObject> searchESJson( ElasticSearchDTO elasticSearchDTO){

    List<String> fieldList = elasticSearchDTO.getFieldList();
    String[] fields = new String[fieldList.size()];
    fieldList.toArray(fields);
    try {
      //查询搜索对象
      SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName+"_"+elasticSearchDTO.getTableTypeName().toLowerCase()).setTypes(elasticSearchDTO.getTableTypeName());
      BoolQueryBuilder subCodeQuery = QueryBuilders.boolQuery();
      setParamTerm(subCodeQuery, elasticSearchDTO.getParamMap(),elasticSearchDTO.getNotParamMap());
      if(CommonUtil.isEmpty(elasticSearchDTO.getSearchContent())) {
        subCodeQuery.must(QueryBuilders.matchAllQuery());
      }else{
        subCodeQuery.must(QueryBuilders.multiMatchQuery(elasticSearchDTO.getSearchContent(), fields).slop(1));
      }
      searchRequestBuilder.setQuery(subCodeQuery);
      return searchResponse(searchRequestBuilder,elasticSearchDTO.getSortMap());
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 在ES中模糊搜索内容（分页）
   */
  public PageInfo<JSONObject> searchESJsonPage(ElasticSearchDTO elasticSearchDTO){


    List<String> fieldList = elasticSearchDTO.getFieldList();
    String[] fields = new String[fieldList.size()];
    fieldList.toArray(fields);
    try {
      SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName+"_"+elasticSearchDTO.getTableTypeName().toLowerCase()).setTypes(elasticSearchDTO.getTableTypeName());
      BoolQueryBuilder subCodeQuery = QueryBuilders.boolQuery();
      setParamTerm(subCodeQuery,elasticSearchDTO.getParamMap(),elasticSearchDTO.getNotParamMap());
      if(CommonUtil.isEmpty(elasticSearchDTO.getSearchContent())) {
        subCodeQuery.must(QueryBuilders.matchAllQuery());
      }else{
        subCodeQuery.must(QueryBuilders.multiMatchQuery(elasticSearchDTO.getSearchContent(), fields).slop(1));
      }
        //查询
      searchRequestBuilder.setQuery(subCodeQuery);

      return searchResponse(searchRequestBuilder,elasticSearchDTO.getSortMap(),elasticSearchDTO.getPageDTO());
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
      SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName+"_"+tableTypeName.toLowerCase()).setQuery(QueryBuilders.termsQuery("id",prepareId)).setTypes(tableTypeName);
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
     if (null != hits && hits.getTotalHits() > 0) {
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
   public PageInfo searchResponse(SearchRequestBuilder searchRequestBuilder,Map<String,Integer> sortMap,PageDTO pageDTO){
     //拼接排序规则
     setSortTerm(searchRequestBuilder,sortMap);
     //分页
     searchRequestBuilder.setFrom(pageDTO.getPageNum()-1).setSize(pageDTO.getPageSize());
     LOGGER.debug("searchRequestBuilder分页:"+searchRequestBuilder);
     SearchResponse response = searchRequestBuilder.get();
     List<JSONObject> arrList = new ArrayList<JSONObject>();
     SearchHits hits = response.getHits();
     if (null != hits && hits.getTotalHits() > 0) {
       for (SearchHit hit : hits) {
         String json = hit.getSourceAsString();
         JSONObject jsonObject=JSON.parseObject(json);
         jsonObject.put("esId",hit.getId());
         arrList.add(jsonObject);
       }
     } else {
       LOGGER.info("没有查询到任何结果！");
     }
     PageInfo pageBean = new PageInfo(arrList);
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
     if(!CommonUtil.isEmpty(eid)) {
       DeleteRequestBuilder deleteResponse = client.prepareDelete(indexName+"_"+tableTypeName.toLowerCase(), tableTypeName, eid);
       deleteResponse.execute().actionGet();
     }

   }

  /**
   * 修改
   * @param jsonStr
   * @param tableTypeName
   * @param prepareId
   */
  public void updateResponse(String jsonStr, String tableTypeName,String prepareId){
    String eid=getESId(tableTypeName,prepareId);
    //未获取到则新增记录
    if(CommonUtil.isEmpty(eid)) {
      saveESJson(jsonStr, tableTypeName);
    }else{
      UpdateRequestBuilder updateRequestBuilder = ElasticsearchConfiguration.client.prepareUpdate(indexName+"_"+tableTypeName.toLowerCase(),tableTypeName,eid);
      updateRequestBuilder.setDoc(jsonStr).get();
    }
  }

  private void setParamTerm(BoolQueryBuilder subCodeQuery,Map paramMap,Map notParamMap){
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

    if(notParamMap != null) {
      Iterator it = notParamMap.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry entry = (Map.Entry) it.next();
        Object key = entry.getKey();
        Object value = entry.getValue();
        String[] values =  StringUtils.split(String.valueOf(value),",");
        if(values.length>1){
          subCodeQuery.mustNot(QueryBuilders.termsQuery(key.toString(), Arrays.asList(values)));
        }else {
          subCodeQuery.mustNot(QueryBuilders.termQuery(key.toString(), value));
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
