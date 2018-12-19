package com.dangjia.acg.service.repair;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.repair.MendOrderDTO;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.repair.IMendWorkerMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.repair.MendWorker;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/11 0011
 * Time: 11:40
 */
@Service
public class MendWorkerService {

    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IMendWorkerMapper mendWorkerMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IMemberMapper memberMapper;



    /**
     * 通过 不通过
     * 1工匠审核中，2工匠审核不通过，3工匠审核通过即平台审核中，4平台不同意，5平台审核通过,6管家取消
     */
    public ServerResponse checkWorkerBackState(String mendOrderId,int state){
        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
        mendOrder.setWorkerBackState(state);
        mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
        return ServerResponse.createBySuccessMessage("操作成功");

    }

    /**
     * 房子id查询退人工
     * workerBackState
     * 0生成中,1工匠审核中，2工匠审核不通过，3工匠审核通过即平台审核中，4平台不同意，5平台审核通过,6管家取消
     */
    public ServerResponse workerBackState(String houseId,Integer pageNum, Integer pageSize){
        try{
            if(pageNum == null){
                pageNum = 1;
            }
            if(pageSize == null){
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<MendOrder> mendOrderList = mendOrderMapper.workerBackState(houseId);
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
                mendOrderDTO.setWorkerBackState(mendOrder.getWorkerBackState());//退人工审核状态 1工匠审核中，2工匠审核不通过，3工匠审核通过即平台审核中，4平台不同意，5平台审核通过,6管家取消
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
     * 审核补人工
     */
    public ServerResponse checkWorkerOrderState(String mendOrderId,int state){
        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
        mendOrder.setWorkerOrderState(state);
        mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 人工单明细mendOrderId
     */
    public ServerResponse mendWorkerList(String mendOrderId){
        List<MendWorker> mendWorkerList = mendWorkerMapper.byMendOrderId(mendOrderId);
        for (MendWorker mendWorker : mendWorkerList){
            mendWorker.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
        }
        return ServerResponse.createBySuccess("查询成功", mendWorkerList);
    }
    /**
     * 普通工匠房子id查询补人工单列表
     * 补人工审核状态
     * 0生成中,1工匠审核中，2工匠不同意，3工匠同意即平台审核中，4平台不同意,5平台同意即待业主支付，6业主已支付，7业主不同意, 8管家取消
     */
    public ServerResponse workerOrderState(String houseId,Integer pageNum, Integer pageSize){
        try{
            if(pageNum == null){
                pageNum = 1;
            }
            if(pageSize == null){
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<MendOrder> mendOrderList = mendOrderMapper.workerOrderState(houseId);
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
                 mendOrderDTO.setWorkerOrderState(mendOrder.getWorkerOrderState());//补人工状态
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
