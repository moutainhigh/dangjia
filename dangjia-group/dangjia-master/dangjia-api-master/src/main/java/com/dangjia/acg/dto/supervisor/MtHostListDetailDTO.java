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
    private String  sincePurchaseAmount; //维修金额
    private String  remark;  //维修备注
    //维修进度（待优化）
    //维修参与人员
    //维修参与人员电话
    //维保参与人员
    //维保商品列表

}
