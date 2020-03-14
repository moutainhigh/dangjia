package com.dangjia.acg.dto.product;

import com.dangjia.acg.modle.product.DjBasicsLabelValue;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Data
public class DjBasicsLabelDTO {
    private String id;
    private String name;
    private List<DjBasicsLabelValue> labelValueList;
}
