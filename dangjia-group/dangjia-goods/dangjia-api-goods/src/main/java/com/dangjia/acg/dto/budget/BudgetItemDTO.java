package com.dangjia.acg.dto.budget;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2019/2/27 0027
 * Time: 11:25
 */
@Data
public class BudgetItemDTO {
    private String rowImage;//图标
    private String rowName;//列名字
    private Double rowPrice;

    private List<GoodsItemDTO> goodsItemDTOList;

    public BudgetItemDTO() {
        goodsItemDTOList = new ArrayList<>();
    }

    public void addGoodsItemDTO(GoodsItemDTO goodsItemDTO) {
        this.goodsItemDTOList.add(goodsItemDTO);
    }
}
