package com.dangjia.acg.common.pay.domain;

import com.alipay.api.AlipayObject;
import com.alipay.api.internal.mapping.ApiField;

public class Location
  extends AlipayObject
{
  private static final long serialVersionUID = 464181093539634040L;
  @ApiField("x")
  private Integer x;
  
  public String toString()
  {
    return "{x:" + this.x + "}";
  }
  
  public Integer getX()
  {
    return this.x;
  }
  
  public void setX(Integer x)
  {
    this.x = x;
  }
}
