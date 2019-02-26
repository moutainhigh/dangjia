package com.dangjia.acg.dto.other;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
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
    private String cityId;
    private List<String> dianList;
    private List<Map<String,Object>> mapList;
}
