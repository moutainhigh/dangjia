package com.dangjia.acg.dto;

import com.dangjia.acg.common.model.PageDTO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: QiYuXiang
 * @date: 2018/5/15
 */

@Data
public class ElasticSearchDTO {

  /**分页*/
  private PageDTO pageDTO;

  /**过滤字段*/
  private List<String> fieldList;

  /**排序字段*/
  private Map<String,Integer> sortMap;

  /**数据库*/
  private String indexName;

  /**表*/
  private String tableTypeName;

  /**搜索字*/
  private String searchContent;

  /**精准搜索多字段*/
  private Map<String,String> paramMap;

  /**精准搜索单字段*/
  private String fieldName;

  /**精准搜索多Value*/
  public List fieldNameValue;

}
