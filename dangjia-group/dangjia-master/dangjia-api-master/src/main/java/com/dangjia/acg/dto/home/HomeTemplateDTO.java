package com.dangjia.acg.dto.home;

import com.dangjia.acg.modle.home.HomeTemplate;
import lombok.Data;

/**
 * @author Ruking.Cheng
 * @descrilbe 首页模版表返回体
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/14 2:14 PM
 */
@Data
public class HomeTemplateDTO extends HomeTemplate {
    private String userName;//操作人姓名
    private String userMobile;//操作人电话
}
