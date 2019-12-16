package com.dangjia.acg.dto.supervisor;

import lombok.Data;

import java.util.List;

/**
 * 验收动态
 */
@Data
public class AcceptanceTrendDTO {
    private String id;
    private String state;//验收状态
    private String createDate;//验收时间
    List<AcceptanceTrendDetailDTO> listDetail;
}
