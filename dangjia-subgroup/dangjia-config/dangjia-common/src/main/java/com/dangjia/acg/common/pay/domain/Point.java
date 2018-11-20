package com.dangjia.acg.common.pay.domain;

import com.alipay.api.AlipayObject;
import com.alipay.api.internal.mapping.ApiField;

public class Point
  extends AlipayObject
{
  private static final long serialVersionUID = 4767604747954841921L;
  @ApiField("point_value")
  private Integer pointValue;
  @ApiField("location")
  private Location location;
  
  public String toString()
  {
    return "{pointValue:" + this.pointValue + ",location:" + this.location + "}";
  }
  
  public Integer getPointValue()
  {
    return this.pointValue;
  }
  
  public void setPointValue(Integer pointValue)
  {
    this.pointValue = pointValue;
  }
  
  public Location getLocation()
  {
    return this.location;
  }
  
  public void setLocation(Location location)
  {
    this.location = location;
  }
}
