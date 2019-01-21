package com.dangjia.acg.service.repair;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.repair.MendOrderDTO;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendOrder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private IHouseMapper houseMapper;
    @Autowired
    private IMemberMapper memberMapper;

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
                mendOrderDTO.setState(mendOrder.getState());
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
                mendOrderDTO.setState(mendOrder.getState());
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
                mendOrderDTO.setState(mendOrder.getState());
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
