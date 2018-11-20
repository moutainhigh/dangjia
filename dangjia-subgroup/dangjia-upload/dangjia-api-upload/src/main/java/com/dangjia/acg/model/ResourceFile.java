package com.dangjia.acg.model;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.common.model.NewBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author: QiYuXiang
 * @date: 2018/4/21
 */
@Entity
@Table(name = "dj_resource_file")
@ApiModel(description = "静态资源表")
public class ResourceFile extends BaseEntity {

  @Column(name = "file_name")
  @Desc(value = "文件名称")
  @ApiModelProperty("文件名称")
  private String fileName;

  @Column(name = "address")
  @Desc(value = "文件地址")
  @ApiModelProperty("文件地址")
  private String address;

  @Column(name = "path")
  @Desc(value = "文件路径")
  @ApiModelProperty("文件路径")
  private String path;


  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
