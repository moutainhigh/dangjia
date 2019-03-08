package com.dangjia.acg.service.repair;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseDetailMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.*;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.house.WarehouseDetail;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.*;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2019/1/18 0018
 * Time: 14:44
 */
@Service
public class MendOrderCheckService {
    @Autowired
    private IMendOrderCheckMapper mendOrderCheckMapper;
    @Autowired
    private IChangeOrderMapper changeOrderMapper;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private IMendWorkerMapper mendWorkerMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IMendMaterialMapper mendMaterialMapper;
    @Autowired
    private IWarehouseDetailMapper warehouseDetailMapper;
    @Autowired
    private IWarehouseMapper warehouseMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;
    @Autowired
    private ConfigMessageService configMessageService;


    /**
     * 根据mendOrderId查询审核情况
     */
    public ServerResponse auditSituation(String mendOrderId){
        try {
            Example example = new Example(MendOrderCheck.class);
            example.createCriteria().andEqualTo(MendOrderCheck.MEND_ORDER_ID,mendOrderId);
            example.orderBy(MendOrderCheck.SORT).asc();
            List<MendOrderCheck> mendOrderCheckList = mendOrderCheckMapper.selectByExample(example);

            return ServerResponse.createBySuccess("查询成功",mendOrderCheckList);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /** 审核补退单
     *  roleType 角色  1业主,2管家,3工匠,4材料员,5供应商
     *  state  1不通过,2通过
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse checkMendOrder(String userToken,String mendOrderId,String roleType,Integer state){
        try {
            String auditorId;
            if(roleType.equals("4")){
                auditorId = "4";
            }else {
                AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
                Member member = accessToken.getMember();
                auditorId = member.getId();
            }
            MendOrderCheck mendOrderCheck = mendOrderCheckMapper.getByMendOrderId(mendOrderId,roleType);
            if(mendOrderCheck == null){
                return ServerResponse.createByErrorMessage("审核流程不存在该角色");
            }
            mendOrderCheck.setState(state);
            mendOrderCheck.setAuditorId(auditorId);//审核人
            mendOrderCheck.setModifyDate(new Date());
            mendOrderCheckMapper.updateByPrimaryKeySelective(mendOrderCheck);

            MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
            if (state == 1){
                mendOrder.setState(2);//不通过取消
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                if (mendOrder.getType()==1 || mendOrder.getType()==3){//补退人工
                    ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(mendOrder.getChangeOrderId());
                    changeOrder.setState(3);//管家提交的数量单取消 需重新提交
                    changeOrderMapper.updateByPrimaryKeySelective(changeOrder);
                }
            }else {
                boolean flag = true;
                Example example = new Example(MendOrderCheck.class);
                example.createCriteria().andEqualTo(MendOrderCheck.MEND_ORDER_ID, mendOrderId);
                List<MendOrderCheck> mendOrderCheckList = mendOrderCheckMapper.selectByExample(example);//所有审核角色
                for (MendOrderCheck m : mendOrderCheckList){
                    if (m.getState() != 2 ){
                        flag = false;
                        break;
                    }
                }
                if (flag){//审核流程全部通过
                    mendOrder.setState(3);//流程全部通过
                    mendOrder.setCarriage(0.0);//运费
                    mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                    /*全部通过执行补退单不同操作  计算运费*/
                    return this.settleMendOrder(mendOrder);
                }
            }
            return ServerResponse.createBySuccessMessage("审核成功");
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 审核完毕 结算补退单
     * type  0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料
     */
    private ServerResponse settleMendOrder(MendOrder mendOrder){
        try{
            if(mendOrder.getType() == 1){
                ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(mendOrder.getChangeOrderId());
                changeOrder.setState(5);//待业主支付
                changeOrderMapper.updateByPrimaryKeySelective(changeOrder);
            }

            if (mendOrder.getType() == 3){//退人工
                ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(mendOrder.getChangeOrderId());
                changeOrder.setState(6);//退人工完成
                changeOrderMapper.updateByPrimaryKeySelective(changeOrder);

                HouseWorkerOrder houseWorkerOrder = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(mendOrder.getHouseId(), mendOrder.getWorkerTypeId());
                BigDecimal refund = new BigDecimal(mendOrder.getTotalAmount());
                houseWorkerOrder.setWorkPrice(houseWorkerOrder.getWorkPrice().subtract(refund));//减掉工钱
                houseWorkerOrderMapper.updateByPrimaryKeySelective(houseWorkerOrder);

                List<MendWorker> mendWorkerList = mendWorkerMapper.byMendOrderId(mendOrder.getId());
                /*记录退数量*/
                for (MendWorker mendWorker : mendWorkerList){
                    forMasterAPI.backCount(mendOrder.getHouseId(), mendWorker.getWorkerGoodsId(), mendWorker.getShopCount());
                }
                House house = houseMapper.selectByPrimaryKey(houseWorkerOrder.getHouseId());
                Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                member.setSurplusMoney(member.getSurplusMoney().add(refund));
                member.setHaveMoney(member.getHaveMoney().add(refund));
                memberMapper.updateByPrimaryKeySelective(member);

                //记录流水
                WorkerDetail workerDetail = new WorkerDetail();
                workerDetail.setName("退人工退款");
                workerDetail.setWorkerId(member.getId());
                workerDetail.setWorkerName(member.getName() == null?member.getNickName() : member.getName());
                workerDetail.setHouseId(mendOrder.getHouseId());
                workerDetail.setMoney(new BigDecimal(mendOrder.getTotalAmount()));
                workerDetail.setApplyMoney(new BigDecimal(mendOrder.getTotalAmount()));
                workerDetail.setWalletMoney(member.getHaveMoney());
                workerDetail.setState(6);//退人工退款
                workerDetailMapper.insert(workerDetail);

                mendOrder.setState(4);
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
            }

            if(mendOrder.getType() == 2 || mendOrder.getType() == 4){//工匠退剩余材料管家退服务,业主退
                /*审核通过修改仓库数量,记录流水*/
                List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrder.getId());
                for (MendMateriel mendMateriel : mendMaterielList){
                    Warehouse warehouse = warehouseMapper.getByProductId(mendMateriel.getProductId(), mendOrder.getHouseId());
                    warehouse.setBackCount(warehouse.getBackCount() + mendMateriel.getShopCount());//更新退数量
                    warehouse.setBackTime(warehouse.getBackTime() + 1);//更新退次数
                    warehouseMapper.updateByPrimaryKeySelective(warehouse);
                }

                WarehouseDetail warehouseDetail = new WarehouseDetail();
                warehouseDetail.setHouseId(mendOrder.getHouseId());
                warehouseDetail.setRelationId(mendOrder.getId());
                if (mendOrder.getType() == 2){
                    warehouseDetail.setRecordType(3);//工匠退 登记剩余
                }else {
                    warehouseDetail.setRecordType(4);//业主退
                }
                warehouseDetailMapper.insert(warehouseDetail);

                /*退钱给业主*/
                Member member = memberMapper.selectByPrimaryKey(houseMapper.selectByPrimaryKey(mendOrder.getHouseId()).getMemberId());
                //记录流水
                WorkerDetail workerDetail = new WorkerDetail();
                workerDetail.setName("退材料退款");
                workerDetail.setWorkerId(member.getId());
                workerDetail.setWorkerName(member.getName() == null?member.getNickName() : member.getName());
                workerDetail.setHouseId(mendOrder.getHouseId());
                workerDetail.setMoney(new BigDecimal(mendOrder.getTotalAmount()));
                workerDetail.setApplyMoney(new BigDecimal(mendOrder.getTotalAmount()));
                workerDetail.setWalletMoney(member.getHaveMoney());
                if (mendOrder.getType() == 2){
                    workerDetail.setState(5);//进钱//工匠退 登记剩余
                }else {
                    workerDetail.setState(4);//进钱//业主退
                }
                workerDetailMapper.insert(workerDetail);

                member.setHaveMoney(member.getHaveMoney().add(new BigDecimal(mendOrder.getTotalAmount())));
                member.setSurplusMoney(member.getSurplusMoney().add(new BigDecimal(mendOrder.getTotalAmount())));
                memberMapper.updateByPrimaryKeySelective(member);

                mendOrder.setState(4);
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);

                if (mendOrder.getType() == 4){//业主退款成功即业主退材料
                    configMessageService.addConfigMessage(null,"gj",member.getId(),"0","退款结果", DjConstants.PushMessage.REFUND_SUCCESS ,"");
                }
            }

            return ServerResponse.createBySuccessMessage("流程全部通过");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("流程全部通过后异常");
        }
    }
}
