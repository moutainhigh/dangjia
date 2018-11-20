package com.dangjia.acg.service;

import com.dangjia.acg.config.ElasticsearchConfiguration;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author: QiYuXiang
 * @date: 2018/7/9
 */
@Service
public class ElasticSppechService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSppechService.class);
  /**
   * 在ES中模糊搜索内容
   * @param fieldList
   * @param searchContent
   * @param sortMap
   * @param indexName
   * @param tableTypeName
   * @return
   */
  public Object searchESJson(List<String> fieldList, Map<String,String> paramMap, String searchContent, Map<String,Integer> sortMap, String indexName, String tableTypeName){

    String[] fields = new String[fieldList.size()];
    fieldList.toArray(fields);
    try {
      //查询搜索对象
      SearchRequestBuilder searchRequestBuilder = ElasticsearchConfiguration.client.prepareSearch(indexName).setTypes(tableTypeName);
      BoolQueryBuilder subCodeQuery = QueryBuilders.boolQuery();
      if(paramMap != null) {
        Iterator it = paramMap.entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry entry = (Map.Entry) it.next();
          Object key = entry.getKey();
          Object value = entry.getValue();
          subCodeQuery.must(QueryBuilders.termQuery(key.toString(), value));
        }
      }
      if(searchContent == null) {
        subCodeQuery.must(QueryBuilders.matchAllQuery());
      }else{
        subCodeQuery.must(QueryBuilders.multiMatchQuery(searchContent,fields).slop(3));
      }
      //设置高亮
      String preTags = "<strong>";
      String postTags = "</strong>";
      HighlightBuilder highlightBuilder = new HighlightBuilder();
      highlightBuilder.preTags(preTags);//设置前缀
      highlightBuilder.postTags(postTags);//设置后缀
      highlightBuilder.field(fields[0]);
      // 查询
      SearchResponse response = searchRequestBuilder.setQuery(subCodeQuery).highlighter(highlightBuilder).get();
      System.out.println("查询数据JSON"+searchRequestBuilder);
      SearchHits hits = response.getHits();
      return hits;
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

}
