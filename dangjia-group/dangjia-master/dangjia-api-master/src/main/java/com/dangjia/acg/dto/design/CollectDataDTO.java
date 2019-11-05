package com.dangjia.acg.dto.design;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @date on 2019/11/5
 */
@Data
public class CollectDataDTO {
    private Date date;

    private List<Map<String,Object>> list;

}

