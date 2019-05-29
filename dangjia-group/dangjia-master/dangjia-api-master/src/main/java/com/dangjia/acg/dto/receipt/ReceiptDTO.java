package com.dangjia.acg.dto.receipt;

import com.dangjia.acg.dto.deliver.SupplierDeliverDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/5/28
 * Time: 11:33
 */
@Data
public class ReceiptDTO {
    private String id;//回执Id
    private Date createDate;//合併結算時間
    private List<SupplierDeliverDTO> list;//結算單號詳情
    private Double amount;//结算金额
}
