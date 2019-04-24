package com.dangjia.acg.dto.finance;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 14:41
 * 用户流水
 */
@Data
public class WebWorkerDetailDTO {
    private String id;//id
    private String name;//说明
    private String workerName;//用户姓名
    private String memberId;//用户id
    private String mobile;//电话
    private String houseName;//房子名字
    private BigDecimal money;// 本次金额
    private Integer state;// 0工钱收入,1提现,2自定义增加金额,3自定义减少金额,4退材料退款,5剩余材料退款,6退人工退款
    private String definedName;//自定义流水说明
    private BigDecimal haveMoney;//工匠订单当时拿到的钱
    private Date createDate;// 创建日期
    private Date modifyDate;// 修改日期
    private Integer star;//星级
    private BigDecimal walletMoney;//钱包当时钱
}
