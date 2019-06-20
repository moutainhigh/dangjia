package com.dangjia.acg.dto.core;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/11/6 0006
 * Time: 17:56
 * 节点
 */
@Data
public class NodeDTO {
    private String nameA;//工序名
    private String image;//工序图标
    private String nameB;//节点名
    private String nameC;//工序详情
    private String url;//详情url
    private int total;//总共节点
    private int rank;//第几
    private String color;//工序颜色
    private Map member;//工人明细
    private Map progress;//进度明细 map:{工序名称，预计总装修天，停工延期天，提前完工天，进度集合：{序号，状态名称，ICON}}
    private int state;//列表状态 0代表不显示红点，1显示红点
    private List<HouseResult.ListMapBean> bigList;//菜单
}
