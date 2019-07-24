package com.dangjia.acg.dto.sale.store;

import lombok.Data;

import java.util.Date;

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
    private Integer type;//类别：0:场内销售，1:场外销售
    private String userName;//用户名
    private String userMobile;//手机
    private String userHead;//头像
    private Boolean isJob;//是否在职（0：正常；1，离职）
    protected Date createDate;// 创建日期
    protected Date modifyDate;// 修改日期
}
