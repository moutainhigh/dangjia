package com.dangjia.acg.dto.supplier;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 19/11/2019
 * Time: 下午 5:07
 */
@Data
public class DjSupplierDeliverDTOList {
    private String name;//供应商名称
    private String telephone;//供应商电话
    private String image;//回执单
    private Date createDate;// 创建日期
    private Double totalMoney;// 总金额
    List<DjSupplierDeliverDTO> djSupplierDeliverDTOList;
}


