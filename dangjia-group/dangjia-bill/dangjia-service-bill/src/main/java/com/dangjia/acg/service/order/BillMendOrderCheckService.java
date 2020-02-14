package com.dangjia.acg.service.order;

import com.dangjia.acg.api.config.ConfigMessageAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dto.refund.RefundRepairOrderMaterialDTO;
import com.dangjia.acg.mapper.order.IBillHouseMapper;
import com.dangjia.acg.mapper.order.IBillWarehouseDetailMapper;
import com.dangjia.acg.mapper.order.IBillWarehouseMapper;
import com.dangjia.acg.mapper.order.IBillWorkerDetailMapper;
import com.dangjia.acg.mapper.refund.IBillMendOrderMapper;
import com.dangjia.acg.mapper.refund.RefundAfterSalesMapper;
import com.dangjia.acg.mapper.sale.IBillMemberMapper;
import com.dangjia.acg.mapper.storeFront.IBillStorefrontMapper;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.house.WarehouseDetail;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.*;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.account.BillAccountFlowRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;

/**
 * author: fzh
 *
 *
 */
@Service
public class BillMendOrderCheckService {
    protected static final Logger logger = LoggerFactory.getLogger(BillMendOrderCheckService.class);

    @Autowired
    private IBillWarehouseDetailMapper iBillWarehouseDetailMapper;
    @Autowired
    private RefundAfterSalesMapper refundAfterSalesMapper;
    @Autowired
    private IBillWarehouseMapper iBillWarehouseMapper;
    @Autowired
    private IBillMemberMapper iBillMemberMapper;
    @Autowired
    private IBillHouseMapper iBillHouseMapper;
    @Autowired
    private IBillWorkerDetailMapper iBillWorkerDetailMapper;
    @Autowired
    private IBillMendOrderMapper iBillMendOrderMapper;
    @Autowired
    private ConfigMessageAPI configMessageAPI;
    @Autowired
    private BillAccountFlowRecordService billAccountFlowRecordService;
    /**
     * 审核完毕 结算补退单
     * type  0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料,4:业主退货退款
     */
    public ServerResponse settleMendOrder(String repairMendOrderId){
        try{
            MendOrder mendOrder=iBillMendOrderMapper.selectByPrimaryKey(repairMendOrderId);//查询对应的退货申请单信息
          if(mendOrder.getType() == 4||mendOrder.getType() == 5) {//业主退款/业主退货退款
              /*审核通过修改仓库数量,记录流水*/
              List<RefundRepairOrderMaterialDTO> repairMaterialList = refundAfterSalesMapper.queryRefundOnlyHistoryOrderMaterialList(mendOrder.getId());//退款商品列表查询
              if (repairMaterialList != null && repairMaterialList.size() > 0) {
                  for (RefundRepairOrderMaterialDTO mendMateriel : repairMaterialList) {
                      Example example = new Example(Warehouse.class);
                      example.createCriteria()
                              .andEqualTo(Warehouse.PRODUCT_ID, mendMateriel.getProductId())
                              .andEqualTo(Warehouse.HOUSE_ID, mendOrder.getHouseId());
                      Warehouse warehouse = iBillWarehouseMapper.selectOneByExample(example);
                      warehouse.setBackCount(warehouse.getBackCount() + mendMateriel.getReturnCount());//更新退数量
                      warehouse.setBackTime(warehouse.getBackTime() + 1);//更新退次数
                      warehouse.setOwnerBack(warehouse.getOwnerBack() == null ? mendMateriel.getReturnCount() : (warehouse.getOwnerBack() + mendMateriel.getReturnCount())); //购买数量+业主退数量
                      iBillWarehouseMapper.updateByPrimaryKeySelective(warehouse);
                  }

                  WarehouseDetail warehouseDetail = new WarehouseDetail();
                  warehouseDetail.setHouseId(mendOrder.getHouseId());
                  warehouseDetail.setRelationId(mendOrder.getId());
                  warehouseDetail.setRecordType(4);//业主退
                  iBillWarehouseDetailMapper.insert(warehouseDetail);
                  /*退钱给业主*/
                  Member member = iBillMemberMapper.selectByPrimaryKey(iBillHouseMapper.selectByPrimaryKey(mendOrder.getHouseId()).getMemberId());
                  BigDecimal haveMoney = member.getHaveMoney().add(new BigDecimal(mendOrder.getTotalAmount()));
                  BigDecimal surplusMoney = member.getSurplusMoney().add(new BigDecimal(mendOrder.getTotalAmount()));
                  //记录流水
                  WorkerDetail workerDetail = new WorkerDetail();
                  workerDetail.setName(mendOrder.getType() == 4?"业主退材料退款":"业主退货退款");
                  workerDetail.setWorkerId(member.getId());
                  workerDetail.setWorkerName(CommonUtil.isEmpty(member.getName()) ? member.getNickName() : member.getName());
                  workerDetail.setHouseId(mendOrder.getHouseId());
                  workerDetail.setMoney(new BigDecimal(mendOrder.getTotalAmount()));
                  workerDetail.setApplyMoney(new BigDecimal(mendOrder.getTotalAmount()));
                  workerDetail.setWalletMoney(surplusMoney);
                  workerDetail.setState(4);//进钱//业主退
                  iBillWorkerDetailMapper.insert(workerDetail);

                  member.setHaveMoney(haveMoney);
                  member.setSurplusMoney(surplusMoney);
                  iBillMemberMapper.updateByPrimaryKeySelective(member);

                  if(mendOrder.getType() == 5||mendOrder.getType() == 2){//退货退款，才扣除店铺的钱
                      //修改店铺的金额(损扣减金额）
                      String storefrontId=mendOrder.getStorefrontId();
                      billAccountFlowRecordService.updateStoreAccountMoney(storefrontId,mendOrder.getHouseId(),3,mendOrder.getId(), MathUtil.mul(mendOrder.getTotalAmount(),-1),"业主退货退款，自动扣减","SYSTEM");

                  }

                  mendOrder.setState(4);
                  iBillMendOrderMapper.updateByPrimaryKeySelective(mendOrder);

                  //推送消息给业主退货退款通知
                  configMessageAPI.addRefundConfigMessage(null, AppType.ZHUANGXIU, member.getId(),
                          "0", "退货退款通知", String.format(DjConstants.PushMessage.YEZHUTUIHUO), "");

              }
          }
            return ServerResponse.createBySuccessMessage("流程全部通过");
        }catch (Exception e){
            logger.error("退款流程处理异常：",e);
            return ServerResponse.createByErrorMessage("流程全部通过后异常");
        }
    }
}
