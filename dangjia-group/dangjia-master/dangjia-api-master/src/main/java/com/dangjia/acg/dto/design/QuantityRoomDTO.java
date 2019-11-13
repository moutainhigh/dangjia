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
    private String memberName;//业主名称

    private String upOName;//量房name
    private String upTName;//平面图name
    private String upFName;//施工图name

    private String upFaName;//精算图name
    private List<QuantityRoomImages> images;

    private String name;
}
