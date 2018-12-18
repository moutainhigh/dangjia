package com.dangjia.acg.dto.core;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 工匠端我的
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2018/12/1 5:23 PM
 */
@Data
public class HomePageBean {

    private BigDecimal evaluation;//积分
    private String favorable;//好评率
    private String gradeName;//工匠等级别称
    private String ioflow;//工匠头像
    private String workerId;//工匠ID
    private String workerName;//工匠名称
    private List<ListBean> list;

    @Data
    public static class ListBean {
        private String imageUrl;//菜单图片地址
        private String name;//菜单名称
        private String url;//点击URL
        private int type;//0:h5;1:我的资料
    }
}
