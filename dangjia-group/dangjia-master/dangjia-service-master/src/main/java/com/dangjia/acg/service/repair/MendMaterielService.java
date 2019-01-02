package com.dangjia.acg.service.repair;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.repair.MendOrderDTO;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseDetailMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.house.WarehouseDetail;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/11 0011
 * Time: 9:41
 */
@Service
public class MendMaterielService {
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IMendMaterialMapper mendMaterialMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IWarehouseDetailMapper warehouseDetailMapper;
    @Autowired
    private IWarehouseMapper warehouseMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;

    /**
     * 通过 不通过
     */
    public ServerResponse checkLandlordState(String mendOrderId,int state,Double carriage){
        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
        if (state == 2){//不通过
            mendOrder.setLandlordState(state);
            mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
            return ServerResponse.createBySuccessMessage("操作成功");
        }else if(state == 3){
            /*审核通过修改仓库数量,记录流水*/
            List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrderId);
            for (MendMateriel mendMateriel : mendMaterielList){
                Warehouse warehouse = warehouseMapper.getByProductId(mendMateriel.getProductId(), mendOrder.getHouseId());
                warehouse.setBackCount(warehouse.getBackCount() + mendMateriel.getShopCount());//更新退数量
                warehouse.setBackTime(warehouse.getBackTime() + 1);//更新退次数
                warehouseMapper.updateByPrimaryKeySelective(warehouse);
            }
            mendOrder.setLandlordState(state);
            mendOrder.setCarriage(carriage);//运费
            mendOrderMapper.updateByPrimaryKeySelective(mendOrder);

            WarehouseDetail warehouseDetail = new WarehouseDetail();
            warehouseDetail.setHouseId(mendOrder.getHouseId());
            warehouseDetail.setRelationId(mendOrder.getId());
            warehouseDetail.setRecordType(3);//退货
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
            workerDetail.setState(0);//进钱
            workerDetailMapper.insert(workerDetail);

            member.setHaveMoney(member.getHaveMoney().add(new BigDecimal(mendOrder.getTotalAmount())));
            member.setSurplusMoney(member.getSurplusMoney().add(new BigDecimal(mendOrder.getTotalAmount())));
            memberMapper.updateByPrimaryKeySelective(member);

            return ServerResponse.createBySuccessMessage("操作成功");
        }else {
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 房子id查询业主退货单列表
     * landlordState
     * 0生成中,1平台审核中,2不通过,3通过
     */
    public ServerResponse landlordState(String houseId,Integer pageNum, Integer pageSize){
        try{
            if(pageNum == null){
                pageNum = 1;
            }
            if(pageSize == null){
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<MendOrder> mendOrderList = mendOrderMapper.landlordState(houseId);
            PageInfo pageResult = new PageInfo(mendOrderList);
            List<MendOrderDTO> mendOrderDTOS = new ArrayList<MendOrderDTO>();
            for (MendOrder mendOrder : mendOrderList){
                MendOrderDTO mendOrderDTO = new MendOrderDTO();
                mendOrderDTO.setMendOrderId(mendOrder.getId());
                mendOrderDTO.setNumber(mendOrder.getNumber());
                mendOrderDTO.setCreateDate(mendOrder.getCreateDate());
                House house = houseMapper.selectByPrimaryKey(mendOrder.getHouseId());
                mendOrderDTO.setAddress(house.getResidential()+house.getBuilding()+"栋"+house.getUnit()+"单元"+house.getNumber());
                Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                mendOrderDTO.setMemberName(member.getNickName() == null ? member.getName() : member.getNickName());
                mendOrderDTO.setMemberMobile(member.getMobile());

                Member worker = memberMapper.selectByPrimaryKey(mendOrder.getApplyMemberId());
                mendOrderDTO.setApplyName(worker.getName());
                mendOrderDTO.setApplyMobile(worker.getMobile());
                mendOrderDTO.setType(mendOrder.getType());
                mendOrderDTO.setLandlordState(mendOrder.getLandlordState());//1平台审核中,2不通过,3通过
                mendOrderDTO.setTotalAmount(mendOrder.getTotalAmount());
                mendOrderDTOS.add(mendOrderDTO);
            }
            pageResult.setList(mendOrderDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 通过 不通过
     */
    public ServerResponse checkMaterialBackState(String mendOrderId,int state,Double carriage){
        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
        if (state == 2){//不通过
            mendOrder.setMaterialBackState(state);
            mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
            return ServerResponse.createBySuccessMessage("操作成功");
        }else if(state == 3){
            /*审核通过修改仓库数量,记录流水*/
            List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrderId);
            for (MendMateriel mendMateriel : mendMaterielList){
                Warehouse warehouse = warehouseMapper.getByProductId(mendMateriel.getProductId(), mendOrder.getHouseId());
                warehouse.setBackCount(warehouse.getBackCount() + mendMateriel.getShopCount());//更新退数量
                warehouse.setBackTime(warehouse.getBackTime() + 1);//更新退次数
                warehouseMapper.updateByPrimaryKeySelective(warehouse);
            }
            mendOrder.setMaterialBackState(state);
            mendOrder.setCarriage(carriage);//运费
            mendOrderMapper.updateByPrimaryKeySelective(mendOrder);

            WarehouseDetail warehouseDetail = new WarehouseDetail();
            warehouseDetail.setHouseId(mendOrder.getHouseId());
            warehouseDetail.setRelationId(mendOrder.getId());
            warehouseDetail.setRecordType(3);//退货
            warehouseDetailMapper.insert(warehouseDetail);

            return ServerResponse.createBySuccessMessage("操作成功");
        }else {
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 房子id查询退货单列表
     * material_back_state
     * 0生成中,1平台审核中，2平台审核不通过，3审核通过，4管家取消
     */
    public ServerResponse materialBackState(String houseId,Integer pageNum, Integer pageSize){
        try{
            if(pageNum == null){
                pageNum = 1;
            }
            if(pageSize == null){
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<MendOrder> mendOrderList = mendOrderMapper.materialBackState(houseId);
            PageInfo pageResult = new PageInfo(mendOrderList);
            List<MendOrderDTO> mendOrderDTOS = new ArrayList<MendOrderDTO>();
            for (MendOrder mendOrder : mendOrderList){
                MendOrderDTO mendOrderDTO = new MendOrderDTO();
                mendOrderDTO.setMendOrderId(mendOrder.getId());
                mendOrderDTO.setNumber(mendOrder.getNumber());
                mendOrderDTO.setCreateDate(mendOrder.getCreateDate());
                House house = houseMapper.selectByPrimaryKey(mendOrder.getHouseId());
                mendOrderDTO.setAddress(house.getResidential()+house.getBuilding()+"栋"+house.getUnit()+"单元"+house.getNumber());
                Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                mendOrderDTO.setMemberName(member.getNickName() == null ? member.getName() : member.getNickName());
                mendOrderDTO.setMemberMobile(member.getMobile());

                Member worker = memberMapper.selectByPrimaryKey(mendOrder.getApplyMemberId());
                mendOrderDTO.setApplyName(worker.getName());
                mendOrderDTO.setApplyMobile(worker.getMobile());
                mendOrderDTO.setType(mendOrder.getType());
                mendOrderDTO.setMaterialBackState(mendOrder.getMaterialBackState());//退货审核状态 1平台审核中，2平台审核不通过，3审核通过，4管家取消
                mendOrderDTO.setTotalAmount(mendOrder.getTotalAmount());
                mendOrderDTOS.add(mendOrderDTO);
            }
            pageResult.setList(mendOrderDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 通过 不通过
     */
    public ServerResponse checkMaterialOrderState(String mendOrderId,int state, Double carriage){
        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
        if (state == 2 || state == 3){
            mendOrder.setMaterialOrderState(state);
            mendOrder.setCarriage(carriage);//运费
            mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
            return ServerResponse.createBySuccessMessage("操作成功");
        }else {
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 根据mendOrderId查明细
     */
    public ServerResponse mendMaterialList(String mendOrderId){
        List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrderId);
        for (MendMateriel mendMateriel : mendMaterielList){
            mendMateriel.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
        }
        return ServerResponse.createBySuccess("查询成功", mendMaterielList);
    }

    /**
     * 房子id查询补货单列表
     * materialOrderState
     * 0生成中,1平台审核中，2平台审核不通过，3平台审核通过待业主支付,4业主已支付，5业主不同意，6管家取消
     */
    public ServerResponse materialOrderState(String houseId,Integer pageNum, Integer pageSize){
        try{
            if(pageNum == null){
                pageNum = 1;
            }
            if(pageSize == null){
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<MendOrder> mendOrderList = mendOrderMapper.materialOrderState(houseId);
            PageInfo pageResult = new PageInfo(mendOrderList);
            List<MendOrderDTO> mendOrderDTOS = new ArrayList<MendOrderDTO>();
            for (MendOrder mendOrder : mendOrderList){
                MendOrderDTO mendOrderDTO = new MendOrderDTO();
                mendOrderDTO.setMendOrderId(mendOrder.getId());
                mendOrderDTO.setNumber(mendOrder.getNumber());
                mendOrderDTO.setCreateDate(mendOrder.getCreateDate());
                House house = houseMapper.selectByPrimaryKey(mendOrder.getHouseId());
                mendOrderDTO.setAddress(house.getResidential()+house.getBuilding()+"栋"+house.getUnit()+"单元"+house.getNumber());
                Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                mendOrderDTO.setMemberName(member.getNickName() == null ? member.getName() : member.getNickName());
                mendOrderDTO.setMemberMobile(member.getMobile());

                Member worker = memberMapper.selectByPrimaryKey(mendOrder.getApplyMemberId());
                mendOrderDTO.setApplyName(worker.getName());
                mendOrderDTO.setApplyMobile(worker.getMobile());
                mendOrderDTO.setType(mendOrder.getType());
                mendOrderDTO.setMaterialOrderState(mendOrder.getMaterialOrderState());
                mendOrderDTO.setTotalAmount(mendOrder.getTotalAmount());
                mendOrderDTOS.add(mendOrderDTO);
            }
            pageResult.setList(mendOrderDTOS);

            return ServerResponse.createBySuccess("查询成功", pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


}
