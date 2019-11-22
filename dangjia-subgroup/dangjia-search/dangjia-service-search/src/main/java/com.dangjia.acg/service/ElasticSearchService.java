package com.dangjia.acg.service;

import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageBean;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.Validator;
import com.dangjia.acg.config.ElasticsearchConfiguration;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


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
   * 批量保存内容到ES
   */
  public List<String> saveESJsonList(List<String> jsonStr,String tableTypeName) {
    List<String> esidlist=new ArrayList<String>();
    try {
      IndexRequestBuilder indexRequestBuilder= client.prepareIndex(indexName,tableTypeName);
      for(int i = 0;i<jsonStr.size(); i++) {
        IndexResponse indexResponse= indexRequestBuilder.setSource(JSONObject.parseObject(jsonStr.get(i))).get();
        LOGGER.info("ES 插入完成");
        esidlist.add(indexResponse.getId());
      }
    }catch (RuntimeException e){
      throw new BaseException(ServerCode.JSON_TYPE_ERROR,"JSON格式不正确，请检查JSON");
    }catch(Exception e){
      throw new BaseException(ServerCode.ES_ERROR,"保存搜索引擎失败");
    }
    return esidlist;
  }

  /**
   * 在ES中模糊搜索内容
   * @param fieldList
   * @param searchContent
   * @param sortMap
   * @param tableTypeName
   * @return
   */
  public List<String> searchESJson(List<String> fieldList, String searchContent,Map<String,Integer> sortMap, String tableTypeName){

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
  public PageBean<String> searchESJsonPage(PageDTO pageDTO,Map<String,String> paramMap, List<String> fieldList, String searchContent,Map<String,Integer> sortMap,  String tableTypeName){

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
   * @param pageDTO
   * @param paramMap
   * @param tableTypeName
   * @return
   */
  public PageBean<String> searchPreciseJsonPage(PageDTO pageDTO, Map<String,String> paramMap,Map<String,Integer> sortMap,  String tableTypeName){
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
   * @param tableTypeName
   * @return
   */
  public List<String> searchPreciseJson(String fildsName,Map<String,String> paramMap,List<String> objectList,Map<String,Integer> sortMap,  String tableTypeName){
    try {

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

  public String getSearchJsonId( String tableTypeName,String prepareId){
    List<String> strings = new ArrayList<String>();
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





  /**
   * 处理查询数据(不分页)
   * @param searchRequestBuilder
   * @return
   */
   public List<String> searchResponse(SearchRequestBuilder searchRequestBuilder,Map<String,Integer> sortMap){
     //拼接排序规则
     setSortTerm(searchRequestBuilder,sortMap);
     LOGGER.debug("searchRequestBuilder不分页:"+searchRequestBuilder);
     SearchResponse response = searchRequestBuilder.get();

     List<String> arrList = new ArrayList<String>();
     SearchHits hits = response.getHits();
     if (null != hits && hits.totalHits() > 0) {
       for (SearchHit hit : hits) {
         String json = hit.getSourceAsString();
         arrList.add(json);
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
     List<String> arrList = new ArrayList<String>();
     SearchHits hits = response.getHits();
     if (null != hits && hits.totalHits() > 0) {
       for (SearchHit hit : hits) {
         String json = hit.getSourceAsString();
         arrList.add(json);
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
     DeleteRequestBuilder deleteResponse = client.prepareDelete(indexName,tableTypeName,prepareId);
     deleteResponse.execute().actionGet();

   }

  /**
   * 修改
   * @param jsonStr
   * @param tableTypeName
   * @param prepareId
   */
  public void updateResponse(String jsonStr, String tableTypeName,String prepareId){
    UpdateRequestBuilder updateRequestBuilder = ElasticsearchConfiguration.client.prepareUpdate(indexName,tableTypeName,prepareId);
    updateRequestBuilder.setDoc(jsonStr).get();
  }

  private void setParamTerm(BoolQueryBuilder subCodeQuery,Map paramMap){
    if(paramMap != null) {
      Iterator it = paramMap.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry entry = (Map.Entry) it.next();
        Object key = entry.getKey();
        Object value = entry.getValue();
        subCodeQuery.must(QueryBuilders.termQuery(key.toString(), value));
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
        searchRequestBuilder.addSort(key.toString(), SortOrder.DESC);
      }
    }
  }
}
