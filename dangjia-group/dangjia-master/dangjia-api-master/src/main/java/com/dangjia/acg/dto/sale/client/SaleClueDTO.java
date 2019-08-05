package com.dangjia.acg.dto.sale.client;

import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/20
 * Time: 14:49
 */
@Data
public class SaleClueDTO{

    private String id;

    private String owername;//业主名

    private String phone;//手机号码

    private List<SaleMemberLabelDTO> list=new ArrayList<>();//标签

    private Date reportDate;//报备时间

    private Date createDate;//录入时间

    private Date CommunicationDate;//沟通时间

    private Date modifyDate;//最新跟进时间

    private Integer clueType;//线索类型 1：跨域下单  0：正常

    private String username;

    private Integer phaseStatus;//阶段

    private String clueId;

    private String mcId;


}
