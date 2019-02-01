package com.dangjia.acg.dto.house;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

/**
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 14:41
 * 剩余材料的临时仓库
 */
@Data
public class SurplusWareHouseDTO {
    private String id;//id
//    private String houseId;//房子ID
    private Integer type;//1:公司仓库 2：业主房子的临时仓库
    private String address;//地址
    private Integer state;// 状态类型 待清点0, 已清点1  默认：0
    private Date createDate;// 创建日期
    private Date modifyDate;// 修改日期

//    private String memberId;//大管家id
    private String memberName;//大管家名字
    private String memberPhone;//大管家电话

    private Integer surplusWareHouseProductAllCount;//仓库的商品总数
    private Date minDivertDate;// 最近挪货时间

}
