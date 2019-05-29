package com.dangjia.acg.dto.design;

import com.dangjia.acg.dto.core.ButtonListBean;
import com.dangjia.acg.modle.design.QuantityRoomImages;
import lombok.Data;

import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 设计师返回体
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/5/28 6:06 PM
 */
@Data
public class DesignDTO {
    private List<QuantityRoomImages> data;
    private int historyRecord;//是否暂时历史记录
    private List<ButtonListBean> buttonList;//按钮
}
