package com.dangjia.acg.dto.design;

import com.dangjia.acg.modle.design.QuantityRoom;
import com.dangjia.acg.modle.design.QuantityRoomImages;
import lombok.Data;

import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 量房返回体
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/4/27 2:04 PM
 */
@Data
public class QuantityRoomDTO extends QuantityRoom {
    private int userType;//操作人类型：-1为未知，0为App，1为中台
    private String userName;//操作人名称
    private List<QuantityRoomImages> images;
}
