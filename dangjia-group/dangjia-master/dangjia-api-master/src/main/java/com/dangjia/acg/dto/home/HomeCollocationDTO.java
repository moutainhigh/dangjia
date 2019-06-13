package com.dangjia.acg.dto.home;

import com.dangjia.acg.modle.home.HomeCollocation;
import lombok.Data;

import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 首页配置返回体
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/13 2:25 PM
 */
@Data
public class HomeCollocationDTO extends HomeCollocation {
    private String userName;//操作人姓名
    private String userMobile;//操作人电话
    private List<HomeMasterplateDTO> masterplateList; //实际模块排版
}
