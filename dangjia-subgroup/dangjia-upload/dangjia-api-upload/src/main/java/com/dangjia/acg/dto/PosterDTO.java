package com.dangjia.acg.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Qiyuxiang
 * @Description 类说明
 * @Date 2018/7/27
 * @Time 下午4:17
 * @Version V1.0.0
 */
@Data
@ApiModel
public class PosterDTO {

  @ApiModelProperty("content")
  private String content;

  @ApiModelProperty("top")
  private Integer top;

  @ApiModelProperty("left")
  private Integer left;

  @ApiModelProperty("width")
  private Integer width;

  @ApiModelProperty("height")
  private Integer height;

  /**
   * 透明度
   */
  @ApiModelProperty("alpha")
  private Float alpha;

  @ApiModelProperty("isImage")
  private Boolean isImage;

  @ApiModelProperty("fontName")
  private String fontName;

  @ApiModelProperty("color")
  private String color; // ff0000

  @ApiModelProperty("size")
  private Integer size;

  @ApiModelProperty("isRadius")
  private Boolean isRadius = false;

  @ApiModelProperty("isQrCode")
  private Boolean isQrCode = false;

  @ApiModelProperty("isCenter")
  private Boolean isCenter = false;

  @ApiModelProperty("isMiddle")
  private Boolean isMiddle = false;

}
