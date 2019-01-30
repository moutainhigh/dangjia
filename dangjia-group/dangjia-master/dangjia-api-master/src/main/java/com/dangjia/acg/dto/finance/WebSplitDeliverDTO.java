package com.dangjia.acg.dto.finance;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.dto.deliver.SplitDeliverItemDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

/**
 * author: ysl
 * Date: 2019/1/25 0018
 * Time: 14:41
 * 供应商发货信息
 */
@Data
public class WebSplitDeliverDTO {
    private Integer curWeekAddNum;//本周新增
    private Integer curWeekSuccessNum;//本周 成功处理的
    private Integer curWeekNoHandleNum;//本周 待处理的
    private Integer allNoHandleNum;//所有待处理的

    private List<WebSplitDeliverItemDTO> webSplitDeliverItemDTOLists;
}
