package com.dangjia.acg.common.pay.domain;

import com.alipay.api.AlipayObject;
import com.alipay.api.internal.mapping.ApiField;
import com.alipay.api.internal.mapping.ApiListField;

import java.util.List;

public class Data
  extends AlipayObject
{
  private static final long serialVersionUID = 8819409019742210991L;
  @ApiField("id")
  private Integer id;
  @ApiField("user_info")
  private UserInfo userInfo;
  @ApiField("user_info")
  @ApiListField("list_user_info")
  private List<UserInfo> listUserInfo;
  @ApiField("point")
  @ApiListField("list_point")
  private List<Point> listPoint;
  @ApiField("string")
  @ApiListField("list_id")
  private List<String> listId;
  
  public String toString()
  {
    return 
      "{id:" + this.id + ",userInfo:" + this.userInfo + ",listUserInfo:" + this.listUserInfo + ",listPoint:" + this.listPoint + ",listId:" + this.listId + "}";
  }
  
  public Integer getId()
  {
    return this.id;
  }
  
  public void setId(Integer id)
  {
    this.id = id;
  }
  
  public List<UserInfo> getListUserInfo()
  {
    return this.listUserInfo;
  }
  
  public void setListUserInfo(List<UserInfo> listUserInfo)
  {
    this.listUserInfo = listUserInfo;
  }
  
  public UserInfo getUserInfo()
  {
    return this.userInfo;
  }
  
  public void setUserInfo(UserInfo userInfo)
  {
    this.userInfo = userInfo;
  }
  
  public List<String> getListId()
  {
    return this.listId;
  }
  
  public void setListId(List<String> listId)
  {
    this.listId = listId;
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
