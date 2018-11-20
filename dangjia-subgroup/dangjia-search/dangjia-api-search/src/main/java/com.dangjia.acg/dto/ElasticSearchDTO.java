package com.dangjia.acg.dto;

import com.dangjia.acg.common.model.PageDTO;

import java.util.List;
import java.util.Map;

/**
 * @author: QiYuXiang
 * @date: 2018/5/15
 */
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


  public PageDTO getPageDTO() {
    return pageDTO;
  }

  public void setPageDTO(PageDTO pageDTO) {
    this.pageDTO = pageDTO;
  }

  public List<String> getFieldList() {
    return fieldList;
  }

  public void setFieldList(List<String> fieldList) {
    this.fieldList = fieldList;
  }

  public Map<String, Integer> getSortMap() {
    return sortMap;
  }

  public void setSortMap(Map<String, Integer> sortMap) {
    this.sortMap = sortMap;
  }

  public String getIndexName() {
    return indexName;
  }

  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  public String getTableTypeName() {
    return tableTypeName;
  }

  public void setTableTypeName(String tableTypeName) {
    this.tableTypeName = tableTypeName;
  }

  public String getSearchContent() {
    return searchContent;
  }

  public void setSearchContent(String searchContent) {
    this.searchContent = searchContent;
  }

  public Map<String, String> getParamMap() {
    return paramMap;
  }

  public void setParamMap(Map<String, String> paramMap) {
    this.paramMap = paramMap;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public <T> List<T> getFieldNameValue() {
    return fieldNameValue;
  }

  public <T> void setFieldNameValue(List<T> fieldNameValue) {
    this.fieldNameValue = fieldNameValue;
  }
}
