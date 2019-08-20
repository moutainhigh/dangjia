package com.dangjia.acg.dto.sale.residential;

import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import com.dangjia.acg.modle.sale.residential.ResidentialRange;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/23
 * Time: 15:51
 */
@Data
public class ResidentialRangeDTO {
    private String villageId;//小区id
    private String villagename;//小区名称
    private List<ResidentialBuilding> list=new ArrayList<>();//小区楼栋集合
}
