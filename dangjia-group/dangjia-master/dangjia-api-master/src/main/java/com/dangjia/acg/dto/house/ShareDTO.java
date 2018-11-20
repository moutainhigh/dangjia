package com.dangjia.acg.dto.house;

import lombok.Data;

import java.util.List;
@Data
public class ShareDTO {
	private String name;//户型名或者房号
	private String imageNum;//设计图数量
	private String jianzhumianji;//建筑面积
	private String jvillageacreage;//计算面积
	private String price;//精算总价
	private String type;//0为户型      1为房间
	private String houseId;//房间 id
	private String villageId;//小区id
	private String villageName;//小区名字
	private String layoutId;//户型id
	private String layoutleft;//户型名称
	private String show;
	private String image;//图片
	private int showHouse;//是否展示 0展示，1不展示,默认生成 为0
	private String biaoqian[];//房子标签
	private String url;//跳转h5
}
