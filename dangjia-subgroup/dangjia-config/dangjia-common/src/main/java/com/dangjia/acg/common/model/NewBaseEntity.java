package com.dangjia.acg.common.model;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @author: QiYuXiang
 * @date: 2018/9/5
 */
@MappedSuperclass
public class NewBaseEntity implements Serializable {


  @Id
  @Column(name = "id")
  private Long id;

  @Column(name = "app_game_id")
  @Desc(value = "小程序ID")
  @ApiModelProperty("小程序ID")
  private Long appGameId;

}
