package com.dangjia.acg.model;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 系统配置实体
 * Created by QiYuXiang on 2017/8/3.
 */
@Entity
@Table(name = "dj_config")
public class Config extends BaseEntity {

  /****
   *参数key
   */
  @Column(name = "param_key")
  @Desc("参数key")
  private String paramKey;

  /****
   *参数value
   */
  @Column(name = "param_value")
  @Desc("参数value")
  private String paramValue;

  /****
   *参数描述
   */
  @Column(name = "param_desc")
  @Desc("参数描述")
  private String paramDesc;

  /****
   *应用类型
   */
  @Column(name = "app_type")
  @Desc("平台类型")
  private Integer appType;

  /****
   *参数key
   */
  public String getParamKey() {
    return this.paramKey;
  }

  /****
   *参数key
   *
   * @param paramKey 参数key
   */
  public void setParamKey(String paramKey) {
    this.paramKey = paramKey;
  }

  /****
   *参数value
   */
  public String getParamValue() {
    return this.paramValue;
  }

  /****
   *参数value
   *
   * @param paramValue 参数value
   */
  public void setParamValue(String paramValue) {
    this.paramValue = paramValue;
  }

  /****
   *参数描述
   */
  public String getParamDesc() {
    return this.paramDesc;
  }

  /****
   *参数描述
   *
   * @param paramDesc 参数描述
   */
  public void setParamDesc(String paramDesc) {
    this.paramDesc = paramDesc;
  }

  public Integer getAppType() {
    return appType;
  }

  public void setAppType(Integer appType) {
    this.appType = appType;
  }
}
