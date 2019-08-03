package com.dangjia.acg.dto.sale.client;

import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/23
 * Time: 17:19
 */
@Data
public class OrdersCustomerDTO {
    private String memberId;//用户Id
    private String houseId;//房子Id
    private String houseName;//房子名称
    private String mobile;//手机号码
    private String name;//客户名称
    private Date createDate;//下单时间
    private Date completedDate;//竣工时间
    private String userName;//跟进人
    private Date modifyDate;//最新跟进时间
    private String clueId;
    private String mcId;
    private List<SaleMemberLabelDTO> list=new ArrayList<>();//标签
}
