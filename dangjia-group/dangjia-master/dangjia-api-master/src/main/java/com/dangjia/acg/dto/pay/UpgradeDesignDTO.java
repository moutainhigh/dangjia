package com.dangjia.acg.dto.pay;

import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/7 0007
 * Time: 18:04
 * 升级设计
 */
@Data
public class UpgradeDesignDTO {
    private String title;//名称
    private int type;//1多选,0单选
    private List<DesignImageDTO> designImageDTOList;
}
