package com.dangjia.acg.dto.repair;

import com.dangjia.acg.modle.repair.MendMateriel;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReturnMendMaterielDTO {
    private List<MendMateriel> mendMaterielList;
    List<ReturnOrderProgressDTO> MendMaterielProgressList;
    private Double  totalPrice;
    private String imageArr; //相关凭证
    private String returnReason;//退货原因
    private String state;//状态：（0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回,6已关闭7，已审核待处理 8，部分退货）
    private String type;//类型：（0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料）;5业主退货退款

}
