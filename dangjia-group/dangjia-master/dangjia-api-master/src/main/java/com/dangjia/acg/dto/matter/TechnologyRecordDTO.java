package com.dangjia.acg.dto.matter;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/12/26 0026
 * Time: 15:00
 */
@Data
public class TechnologyRecordDTO {
    private String id;
    private String name;
    private Integer state;//验收状态0:未验收;1:已验收,2:已退, 3:勾选中
}
