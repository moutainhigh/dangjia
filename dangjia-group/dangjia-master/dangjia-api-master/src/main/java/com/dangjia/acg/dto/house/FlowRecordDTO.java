package com.dangjia.acg.dto.house;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/12/20 0020
 * Time: 14:28
 */
@Data
public class FlowRecordDTO {
    private List<Map<String, Object>> houseWorkerMap;
    private List<Map<String, Object>> flowApplyMap;
}
