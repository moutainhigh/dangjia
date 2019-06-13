package com.dangjia.acg.dto.home;

import com.dangjia.acg.modle.home.HomeMasterplate;
import lombok.Data;

/**
 * @author Ruking.Cheng
 * @descrilbe 模块返回体
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/13 2:27 PM
 */
@Data
public class HomeMasterplateDTO extends HomeMasterplate {
    private String imageAddress;//图片全地址
    private String userName;//操作人姓名
    private String userMobile;//操作人电话
    private Integer sort;//优先顺序
}
