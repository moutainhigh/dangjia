package com.dangjia.acg.common.pay.domain;

import com.alipay.api.AlipayObject;
import com.alipay.api.internal.mapping.ApiField;
import com.alipay.api.internal.mapping.ApiListField;

import java.util.List;

public class UserInfo
  extends AlipayObject
{
  private static final long serialVersionUID = 4767604747954841921L;
  @ApiField("out_id")
  private Integer outId;
  @ApiField("point")
  private Point point;
  @ApiField("point")
  @ApiListField("list_point")
  private List<Point> listPoint;
  
  public String toString()
  {
    return "{outId:" + this.outId + ",point:" + this.point + ",listPoint:" + this.listPoint + "}";
  }
  
  public Integer getOutId()
  {
    return this.outId;
  }
  
  public void setOutId(Integer outId)
  {
    this.outId = outId;
  }
  
  public Point getPoint()
  {
    return this.point;
  }
  
  public void setPoint(Point point)
  {
    this.point = point;
  }
  
  public List<Point> getListPoint()
  {
    return this.listPoint;
  }
  
  public void setListPoint(List<Point> listPoint)
  {
    this.listPoint = listPoint;
  }
}
