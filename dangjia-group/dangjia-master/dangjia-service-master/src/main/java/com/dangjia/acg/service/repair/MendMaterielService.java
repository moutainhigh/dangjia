package com.dangjia.acg.service.repair;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.house.IWarehouseDetailMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.house.WarehouseDetail;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendOrder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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



    /**
     * 通过 不通过
     */
    public ServerResponse checkMaterialBackState(String mendOrderId,int state){
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
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 2)
                    .andGreaterThan(MendOrder.MATERIAL_BACK_STATE, 0);

            if(pageNum == null){
                pageNum = 1;
            }
            if(pageSize == null){
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<MendOrder> mendOrderList = mendOrderMapper.materialBackState(houseId);
            PageInfo pageResult = new PageInfo(mendOrderList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 通过 不通过
     */
    public ServerResponse checkMaterialOrderState(String mendOrderId,int state){
        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
        if (state == 2 || state == 3){
            mendOrder.setMaterialOrderState(state);
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
            return ServerResponse.createBySuccess("查询成功", pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


}
