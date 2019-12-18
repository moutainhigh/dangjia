package com.dangjia.acg.dto.supervisor;

import com.dangjia.acg.modle.safe.WorkerTypeSafeOrder;
import lombok.Data;

import java.util.List;

@Data
public class MtHostListDetailDTO {
    private String houseId ; //房子id
    private String  address; //房子地址
    private String  latitude;   //维度
    private String  longitude;  //经度
    //维修进度
    //维修参与人员
    //维修参与人员电话
    private String  sincePurchaseAmount; //维修金额
    private String  remark;  //维修备注

    List<WorkerTypeSafeOrder> list;//我的质保单

}
