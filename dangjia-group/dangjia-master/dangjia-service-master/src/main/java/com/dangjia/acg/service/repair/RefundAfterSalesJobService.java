package com.dangjia.acg.service.repair;

import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.dto.shell.HomeShellOrderDTO;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.delivery.IMasterOrderProgressMapper;
import com.dangjia.acg.mapper.repair.IMendDeliverMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.shell.IHomeShellOrderMapper;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.repair.MendDeliver;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.service.node.NodeProgressService;
import com.dangjia.acg.service.shell.HomeShellOrderService;
import com.dangjia.acg.service.shell.HomeShellProductService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RefundAfterSalesJobService {
    protected static final Logger logger = LoggerFactory.getLogger(RefundAfterSalesJobService.class);

    @Autowired
    private IMasterOrderProgressMapper orderProgressMapper;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IComplainMapper complainMapper;

    @Autowired
    private MendOrderCheckService mendOrderCheckService;
    @Autowired
    private IMendDeliverMapper mendDeliverMapper;
    @Autowired
    private NodeProgressService nodeProgressService;
    @Autowired MendMaterielService mendMaterielService;

    @Autowired
    private HomeShellOrderService homeShellOrderService;
    @Autowired
    private IHomeShellOrderMapper homeShellOrderMapper;

    /**
     * 店铺申请等待商家处理（到期自动处理)
     * 处理：若到期店铺款处理，则默认自动退款(修改订单的状态为已同意，增加节点状态，退款至业主帐户）
     */
    @Transactional(rollbackFor = Exception.class)
    public void returnMechantProcessTime() {

        //查询符合条件的语句，作对应的退款扣款处理 状态为RA_002,RETURN_MERCHANT_PROCESS_TIME
        //查询仅退款符合条件的数据
        List<Map<String,Object>> refundJobList=mendDeliverMapper.queryRefundJobList("RETURN_MERCHANT_PROCESS_TIME");
        if(refundJobList!=null&&refundJobList.size()>0){
            for (Map jobMap:refundJobList){
                String mendDeliverId= (String) jobMap.get("mendDeliverId");
                String repairMendOrderId= (String) jobMap.get("repairMendOrderId");
                String strMendId=repairMendOrderId;
                if(StringUtils.isNotBlank(mendDeliverId)){//退货退款
                    MendDeliver mendDeliver=mendDeliverMapper.selectByPrimaryKey(mendDeliverId);
                    mendDeliver.setShippingState(1);//已确认全部退货
                    mendDeliver.setApplyState(0);//供应商结算状态
                    mendDeliver.setModifyDate(new Date());
                    mendDeliverMapper.updateByPrimaryKeySelective(mendDeliver);
                    strMendId=mendDeliverId;
                    mendMaterielService.updateNewMendMaterialList(mendDeliver,1);
                    //打钱给业主（扣店铺的总额和可提现余额),业主仓库中的退货量减少
                    mendOrderCheckService.setMendMoneyOrder(mendDeliverId,"SYSTEM");

                }else{//仅退款
                    //修改退款申诉的状态
                    MendOrder mendOrder=mendOrderMapper.selectByPrimaryKey(repairMendOrderId);
                    mendOrder.setState(4);//已结算
                    mendOrder.setModifyDate(new Date());
                    mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                    //1.退钱给业主，修改材料仓库
                    mendOrderCheckService.settleMendOrder(mendOrder);
                }
                //更新平台介入按钮的状态为已删除
                orderProgressMapper.updateOrderStatusByNodeCode(repairMendOrderId,"REFUND_AFTER_SALES","RA_002");
                //添加对应的流水记录节点信息,商家已同意退款
                nodeProgressService.updateOrderProgressInfo(strMendId,"2","REFUND_AFTER_SALES","RA_003","SYSTEM");
                //添加对应的流水记录节点信息，退款成功
                nodeProgressService.updateOrderProgressInfo(strMendId,"2","REFUND_AFTER_SALES","RA_008","SYSTEM");
                //将钱打给用户，扣除店铺的钱
                //Double totalAmount= (Double) jobMap.get("totalAmount");

            }
        }

    }


    /**
     * 店铺部分退货，等待申请平台介入(到期自动处理）
     * 处理：到期未申请平台介入，则自动关闭该订单，按部分退货退钱
     */
    @Transactional(rollbackFor = Exception.class)
    public void returnPlatformInterventionTime() {

        //查询符合条件的语句，作对应的退款扣款处理 状态为RA_004,RETURN_PLATFORM_INTERVENTION_TIME
        List<Map<String,Object>> refundJobList=mendDeliverMapper.queryRefundDeliverJobList("RA_004","RETURN_PLATFORM_INTERVENTION_TIME");
        if(refundJobList!=null&&refundJobList.size()>0){
            for (Map jobMap:refundJobList){
                String mendDeliverId= (String) jobMap.get("mendDeliverId");//退货单ID
                MendDeliver mendDeliver=mendDeliverMapper.selectByPrimaryKey(mendDeliverId);
                mendDeliver.setShippingState(6);//业主认可部分退货
                mendDeliver.setApplyState(0);//供应商结算状态
                mendDeliver.setModifyDate(new Date());
                mendDeliverMapper.updateByPrimaryKeySelective(mendDeliver);
               //添加对应的流水记录节点信息,超时关闭
                nodeProgressService.updateOrderProgressInfo(mendDeliverId,"2","REFUND_AFTER_SALES","RA_010","SYSTEM");//退款关闭
                mendMaterielService.updateNewMendMaterialList(mendDeliver,2);//业主认可部分收货
                //打钱给业主（扣店铺的总额和可提现余额),业主仓库中的退货量减少
                mendOrderCheckService.setMendMoneyOrder(mendDeliver.getId(),"SYSTEM");

            }
        }

    }

    /**
     * 业主申诉后，等待平台处理(到期自动处理）
     * 平台到期未处理，则自动将该单处理成功，将钱从店铺钱包中扣除，退还至业主帐号
     */
    @Transactional(rollbackFor = Exception.class)
    public void returnPlatformProcessTime() {

        //查询符合条件的语句，作对应的退款扣款处理 状态为RRA_005,RETURN_PLATFORM_PROCESS_TIME
        List<Map<String,Object>> refundJobList=mendDeliverMapper.queryRefundDeliverJobList("RA_005","RETURN_PLATFORM_PROCESS_TIME");
        if(refundJobList!=null&&refundJobList.size()>0){
            for (Map jobMap:refundJobList){
                String mendDeliverId= (String) jobMap.get("mendDeliverId");
                String memberId=(String)jobMap.get("memberId");

                mendMaterielService.updatePlatformComplainInfo(mendDeliverId,"SYSTEM",7);  //查询已申诉的单，更新状态为同意
                Example example = new Example(Complain.class);
                example.createCriteria()
                        .andEqualTo(Complain.MEMBER_ID, memberId)
                        .andEqualTo(Complain.COMPLAIN_TYPE, "7")
                        .andEqualTo(Complain.BUSINESS_ID, mendDeliverId)
                        .andEqualTo(Complain.STATUS, 0);
                Complain complain = complainMapper.selectOneByExample(example);
                if (complain!=null) {
                    complain.setStatus(2);
                    complain.setUserId("SYSTEM");
                    complain.setDescription("处理超时，自动同意！");
                }
                complainMapper.updateByPrimaryKeySelective(complain);
            }
        }
    }
    /**
     *当家贝商城--待收货时间
     */
    public void homeShellOrderReceiveTime(){
        List<HomeShellOrderDTO> list =homeShellOrderMapper.queryHomeShellOrderList("SHELL_PRODUCT_RECIEVE_TIME",1);
        if(list==null){
            for(HomeShellOrderDTO shellOrderDTO:list){
               homeShellOrderService.updateConvertedProductInfo(null,shellOrderDTO.getShellOrderId(),1);//确认收货
            }
        }
    }
    /**
     *当家贝商城--待退款时间
     */
    public void homeShellOrderRefundTime(){
        List<HomeShellOrderDTO> list =homeShellOrderMapper.queryHomeShellOrderList("SHELL_PRODUCT_REFUND_TIME",2);
        if(list==null){
            for(HomeShellOrderDTO shellOrderDTO:list){
                homeShellOrderService.updateOrderInfo(shellOrderDTO.getShellOrderId(),5);//确认退货
            }
        }
    }

    /**
     * 当家贝商城--待支付时间(到期取消支付)
     */
    public void homeShellOrderPayTime(){
        List<HomeShellOrderDTO> list =homeShellOrderMapper.queryHomeShellOrderList("SHELL_PRODUCT_PAY_TIME",3);
        if(list==null){
            for(HomeShellOrderDTO shellOrderDTO:list){
                homeShellOrderService.cancelOrderInfo(shellOrderDTO);//确认退货
            }
        }
    }

   
}
