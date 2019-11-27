package com.dangjia.acg.dto.order;

import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.dto.delivery.AppointmentListDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 25/11/2019
 * Time: 下午 7:29
 */
@Data
public class PaymentToBeMadeDTO extends BaseEntity {

    private String houseName;
    private List<AppointmentListDTO> appointmentListDTOS;
    private BigDecimal actualPaymentPrice;//实付总价
    private BigDecimal totalAmount;//总价（不含运费）
    private BigDecimal totalTransportationCost;//运费
    private BigDecimal totalStevedorageCost;//搬运费
    private BigDecimal totalDiscountPrice;//优惠券
    private Date createDate;
    private String orderNumber;
    private Date modifyDate;
    private Integer splitDeliverCount;//关联发货单数量
    private String splitDeliverId;
    private String houseId;

}
