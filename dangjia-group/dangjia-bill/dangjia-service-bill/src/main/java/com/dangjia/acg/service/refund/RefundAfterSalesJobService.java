package com.dangjia.acg.service.refund;

import com.dangjia.acg.dto.refund.RefundRepairOrderDTO;
import com.dangjia.acg.mapper.config.IBillComplainMapper;
import com.dangjia.acg.mapper.order.IBillOrderProgressMapper;
import com.dangjia.acg.mapper.refund.IBillMendOrderMapper;
import com.dangjia.acg.mapper.refund.RefundAfterSalesMapper;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.order.OrderProgress;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.service.order.BillMendOrderCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RefundAfterSalesJobService {
    protected static final Logger logger = LoggerFactory.getLogger(RefundAfterSalesJobService.class);

    @Autowired
    private IBillOrderProgressMapper iBillOrderProgressMapper;
    @Autowired
    private RefundAfterSalesMapper refundAfterSalesMapper;
    @Autowired
    private IBillMendOrderMapper iBillMendOrderMapper;
    @Autowired
    private IBillComplainMapper iBillComplainMapper;

    @Autowired
    private RefundAfterSalesService refundAfterSalesService;
    @Autowired
    private BillMendOrderCheckService billMendOrderCheckService;
    /**
     * 店铺申请等待商家处理（到期自动处理)
     * 处理：若到期店铺款处理，则默认自动退款(修改订单的状态为已同意，增加节点状态，退款至业主帐户）
     */
    @Transactional(rollbackFor = Exception.class)
    public void returnMechantProcessTime() {

        //查询符合条件的语句，作对应的退款扣款处理 状态为RA_002,RETURN_MERCHANT_PROCESS_TIME
        List<Map<String,Object>> refundJobList=refundAfterSalesMapper.queryRefundJobList("RA_002","RETURN_MERCHANT_PROCESS_TIME");
        if(refundJobList!=null&&refundJobList.size()>0){
            for (Map jobMap:refundJobList){
                String repairMendOrderId= (String) jobMap.get("repairMendOrderId");
                //修改退款申诉的状态
                MendOrder mendOrder=new MendOrder();
                mendOrder.setId(repairMendOrderId);
                mendOrder.setState(3);
                mendOrder.setModifyDate(new Date());
                iBillMendOrderMapper.updateByPrimaryKeySelective(mendOrder);//修改退款申请单的状态同意
                //更新平台介入按钮的状态为已删除
                iBillOrderProgressMapper.updateOrderStatusByNodeCode(repairMendOrderId,"REFUND_AFTER_SALES","RA_002");
                //添加对应的流水记录节点信息,商家已同总退款
                updateOrderProgressInfo(repairMendOrderId,"2","REFUND_AFTER_SALES","RA_003","SYSTEM");
                //添加对应的流水记录节点信息，退款成功
                updateOrderProgressInfo(repairMendOrderId,"2","REFUND_AFTER_SALES","RA_008","SYSTEM");
                //将钱打给用户，扣除店铺的钱
                //Double totalAmount= (Double) jobMap.get("totalAmount");
                billMendOrderCheckService.settleMendOrder(repairMendOrderId);
            }
        }


    }
    /**
     * //添加进度信息
     * @param orderId 订单ID
     * @param progressType 订单类型
     * @param nodeType 节点类型
     * @param nodeCode 节点编码
     * @param userId 用户id
     */
    private void updateOrderProgressInfo(String orderId,String progressType,String nodeType,String nodeCode,String userId){
        OrderProgress orderProgress=new OrderProgress();
        orderProgress.setProgressOrderId(orderId);
        orderProgress.setProgressType(progressType);
        orderProgress.setNodeType(nodeType);
        orderProgress.setNodeCode(nodeCode);
        orderProgress.setCreateBy(userId);
        orderProgress.setUpdateBy(userId);
        orderProgress.setCreateDate(new Date());
        orderProgress.setModifyDate(new Date());
        iBillOrderProgressMapper.insert(orderProgress);
    }

    /**
     * 店铺拒绝退货，等待申请平台介入(到期自动处理）
     * 处理：到期未申请平台介入，则自动关闭该订单，节点装态改为定时关闭
     */
    @Transactional(rollbackFor = Exception.class)
    public void returnPlatformInterventionTime() {

        //查询符合条件的语句，作对应的退款扣款处理 状态为RA_004,RETURN_PLATFORM_INTERVENTION_TIME
        List<Map<String,Object>> refundJobList=refundAfterSalesMapper.queryRefundJobList("RA_004","RETURN_PLATFORM_INTERVENTION_TIME");
        if(refundJobList!=null&&refundJobList.size()>0){
            for (Map jobMap:refundJobList){
                String repairMendOrderId= (String) jobMap.get("repairMendOrderId");
                RefundRepairOrderDTO refundRepairOrderDTO=refundAfterSalesMapper.queryRefundOnlyHistoryOrderInfo(repairMendOrderId);//退款订单详情查询
                if(!("1".equals(refundRepairOrderDTO.getState())||"2".equals(refundRepairOrderDTO.getState()))){
                    continue;
                }
                refundAfterSalesService.updateRepairOrderInfo(refundRepairOrderDTO,repairMendOrderId, 6,refundRepairOrderDTO.getType());//退款关闭
               //添加对应的流水记录节点信息,超时关闭
                updateOrderProgressInfo(repairMendOrderId,"2","REFUND_AFTER_SALES","RA_010","SYSTEM");
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
        List<Map<String,Object>> refundJobList=refundAfterSalesMapper.queryRefundJobList("RA_005","RETURN_PLATFORM_PROCESS_TIME");
        if(refundJobList!=null&&refundJobList.size()>0){
            for (Map jobMap:refundJobList){
                String repairMendOrderId= (String) jobMap.get("repairMendOrderId");
                String memberId=(String)jobMap.get("memberId");
                //修改退款申诉的状态
                MendOrder mendOrder=new MendOrder();
                mendOrder.setId(repairMendOrderId);
                mendOrder.setState(3);
                mendOrder.setModifyDate(new Date());
                iBillMendOrderMapper.updateByPrimaryKeySelective(mendOrder);//修改退款申请单的状态同意
                //更新平台介入按钮的状态为已删除
                iBillOrderProgressMapper.updateOrderStatusByNodeCode(repairMendOrderId,"REFUND_AFTER_SALES","RA_005");
                //添加对应的流水记录节点信息,平台已同意
                updateOrderProgressInfo(repairMendOrderId,"2","REFUND_AFTER_SALES","RA_006","SYSTEM");
                //添加对应的流水记录节点信息，退款成功
                updateOrderProgressInfo(repairMendOrderId,"2","REFUND_AFTER_SALES","RA_008","SYSTEM");
                //查询已申诉的单，更新状态为同意
                Example example = new Example(Complain.class);
                example.createCriteria()
                        .andEqualTo(Complain.MEMBER_ID, memberId)
                        .andEqualTo(Complain.COMPLAIN_TYPE, "7")
                        .andEqualTo(Complain.BUSINESS_ID, repairMendOrderId)
                        .andEqualTo(Complain.STATUS, 0);
                Complain complain = iBillComplainMapper.selectOneByExample(example);
                if (complain!=null) {
                    complain.setStatus(2);
                    complain.setUserId("SYSTEM");
                    complain.setDescription("处理超时，自动同意！");
                }
                iBillComplainMapper.updateByPrimaryKeySelective(complain);
                //将钱打给用户，扣除店铺的钱
                Double totalAmount= (Double) jobMap.get("totalAmount");
                billMendOrderCheckService.settleMendOrder(repairMendOrderId);
            }
        }
    }
}
