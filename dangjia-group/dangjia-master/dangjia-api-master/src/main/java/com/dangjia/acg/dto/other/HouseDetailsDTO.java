package com.dangjia.acg.dto.other;

import com.dangjia.acg.dto.label.OptionalLabelDTO;
import com.dangjia.acg.dto.label.OptionalLaelDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2019/1/4 0004
 * Time: 10:05
 */
@Data
public class HouseDetailsDTO {
    private String image;
    private String houseName;
    private String houseId;
    private String residential;//
    private String cityId;
    private String cityName;//
    private BigDecimal square;//
    private Integer visitState;//
    private BigDecimal totalPrice;//总计
    private List<String> dianList;
    private List<OptionalLaelDetail> labelList;//选配标签名称
    private List<Map<String,Object>> mapList;
    private String optionalLabel;//选配标签id(逗号分隔)
    private String noNumberHouseName;
}
