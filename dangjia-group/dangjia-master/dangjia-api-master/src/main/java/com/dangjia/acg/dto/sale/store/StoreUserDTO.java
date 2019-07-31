package com.dangjia.acg.dto.sale.store;

import com.dangjia.acg.dto.sale.residential.ResidentialRangeDTO;
import com.dangjia.acg.modle.sale.store.MonthlyTarget;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 门店成员返回体
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/7/24 2:25 PM
 */
@Data
public class StoreUserDTO {
    private String storeUserId;//门店成员ID
    private String userId;//成员用户ID
    private String storeId;//门店ID
    private Integer type;//类别：0:内场销售，1:外场销售
    private String userName;//用户名
    private String userMobile;//手机
    private String userHead;//头像
    private Boolean isJob;//是否在职（0：正常；1，离职）
    private Date createDate;// 创建日期
    private Date modifyDate;// 修改日期
    private String storeName;//门店——岗位名称
    private String appKey;//极光聊天的Key
    private List<MonthlyTarget> monthlyTarget;//当前月份的目标
    private List<ResidentialRangeDTO> outField;//销售范围
}
