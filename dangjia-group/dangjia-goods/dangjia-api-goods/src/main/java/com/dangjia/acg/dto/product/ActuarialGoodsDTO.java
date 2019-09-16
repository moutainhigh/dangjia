package com.dangjia.acg.dto.product;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/16
 * Time: 15:34
 * 精算添加商品DTO
 */
@Data
public class ActuarialGoodsDTO {
    private String categoryName;
    private List<Map<String, Object>> gMapList;
}
