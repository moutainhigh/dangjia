package com.dangjia.acg.service.repair;

import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.repair.MendOrderDTO;
import com.dangjia.acg.mapper.deliver.ISplitDeliverMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.sup.Supplier;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private IWarehouseMapper warehouseMapper;

    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private ISplitDeliverMapper splitDeliverMapper;

    /**
     * 要货退货 查询补材料
     */
    public List<MendMateriel>  askAndQuit(String workerTypeId, String houseId, String categoryId, String name) {
        List<MendMateriel> mendMaterielList = mendMaterialMapper.askAndQuit(workerTypeId, houseId, categoryId, name);
        return mendMaterielList;
    }

    /**
     * 房子id查询业主退货单列表
     * landlordState
     * 0生成中,1平台审核中,2不通过,3通过
     */
    public ServerResponse landlordState(String houseId, Integer pageNum, Integer pageSize, String beginDate, String endDate, String likeAddress) {
        try {
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
//            List<MendOrder> mendOrderList = mendOrderMapper.landlordState(houseId);
            List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddress(houseId, 4, beginDate, endDate, likeAddress);
            PageInfo pageResult = new PageInfo(mendOrderList);
            List<MendOrderDTO> mendOrderDTOS = getMendOrderDTOList(mendOrderList);
            pageResult.setList(mendOrderDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 房子id查询退货单列表
     * material_back_state
     * 0生成中,1平台审核中，2平台审核不通过，3审核通过，4管家取消
     */
    public ServerResponse materialBackState(String houseId, Integer pageNum, Integer pageSize, String beginDate, String endDate, String likeAddress) {
        try {
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
//            List<MendOrder> mendOrderList = mendOrderMapper.materialBackState(houseId); 2
            List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddress(houseId, 2, beginDate, endDate, likeAddress);
            PageInfo pageResult = new PageInfo(mendOrderList);
            List<MendOrderDTO> mendOrderDTOS = getMendOrderDTOList(mendOrderList);
            pageResult.setList(mendOrderDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 根据mendOrderId查明细
     */
    public ServerResponse mendMaterialList(String mendOrderId) {
        MendOrder mendOrder= mendOrderMapper.selectByPrimaryKey(mendOrderId);
        House house = houseMapper.selectByPrimaryKey(mendOrder.getHouseId());
        List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrderId);
        List<Map> mendMaterielMaps=new ArrayList<>();
        for (MendMateriel mendMateriel : mendMaterielList) {
            mendMateriel.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
            Map map = BeanUtils.beanToMap(mendMateriel);
            Warehouse warehouse=warehouseMapper.getByProductId(mendMateriel.getProductId(),mendOrder.getHouseId());
            if(warehouse==null){
                map.put(Warehouse.RECEIVE, "0");
            }else {
                //工匠退材料新增已收货数量字段
                if (mendOrder.getType() == 2) {
                    map.put(Warehouse.RECEIVE, warehouse.getReceive() == null ? 0d : warehouse.getReceive());
                }
                //业主退材料增加未发货数量
                if (mendOrder.getType() == 4) {
                    //未发货数量=已要 - 已收
                    map.put(Warehouse.RECEIVE, warehouse.getShopCount() - (warehouse.getOwnerBack()==null?0D:warehouse.getOwnerBack()) - warehouse.getAskCount());
                }
            }
            List<String> supplierId = splitDeliverMapper.getSupplierGoodsId(mendOrder.getHouseId(), mendMateriel.getProductSn());
            List<Supplier> suppliers=new ArrayList<>();
            if (supplierId.size()>0) {
                for (int i = 0; i < supplierId.size(); i++) {
                    Supplier supplier = forMasterAPI.getSupplier(house.getCityId(), supplierId.get(i));
                    suppliers.add(supplier);
                }
                map.put("suppliers", suppliers);
            }
            mendMaterielMaps.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", mendMaterielMaps);
    }

    /**
     * 房子id查询补货单列表
     * materialOrderState
     * 0生成中,1平台审核中，2平台审核不通过，3平台审核通过待业主支付,4业主已支付，5业主不同意，6管家取消
     */
    public ServerResponse materialOrderState(String houseId, Integer pageNum, Integer pageSize, String beginDate, String endDate, String likeAddress) {
        try {
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            if(beginDate!=null && beginDate!="" && endDate!=null && endDate!=""){
                if(beginDate.equals(endDate)){
                    beginDate=beginDate+" "+"00:00:00";
                    endDate=endDate+" "+"23:59:59";
                }
            }
            PageHelper.startPage(pageNum, pageSize);
//            List<MendOrder> mendOrderList = mendOrderMapper.materialOrderState(houseId);
            List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddress(houseId, 0, beginDate, endDate, likeAddress);
            PageInfo pageResult = new PageInfo(mendOrderList);
            List<MendOrderDTO> mendOrderDTOS = getMendOrderDTOList(mendOrderList);
            pageResult.setList(mendOrderDTOS);

            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    public List<MendOrderDTO> getMendOrderDTOList(List<MendOrder> mendOrderList) {

        List<MendOrderDTO> mendOrderDTOS = new ArrayList<MendOrderDTO>();
        for (MendOrder mendOrder : mendOrderList) {
            MendOrderDTO mendOrderDTO = new MendOrderDTO();
            mendOrderDTO.setMendOrderId(mendOrder.getId());
            mendOrderDTO.setNumber(mendOrder.getNumber());
            mendOrderDTO.setCreateDate(mendOrder.getCreateDate());
            House house = houseMapper.selectByPrimaryKey(mendOrder.getHouseId());
            if (house != null) {
                if (house.getVisitState() != 0) {
//                    mendOrderDTO.setAddress(house.getResidential() + house.getBuilding() + "栋" + house.getUnit() + "单元" + house.getNumber());
                    mendOrderDTO.setAddress(house.getHouseName());
                    Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                    mendOrderDTO.setMemberName(member.getNickName() == null ? member.getName() : member.getNickName());
                    mendOrderDTO.setMemberId(member.getId());
                    mendOrderDTO.setMemberMobile(member.getMobile());

                    Member worker = memberMapper.selectByPrimaryKey(mendOrder.getApplyMemberId());
                    mendOrderDTO.setApplyMemberId(worker.getId());
                    mendOrderDTO.setApplyName(CommonUtil.isEmpty(worker.getName())?worker.getNickName():worker.getName());
                    mendOrderDTO.setApplyMobile(worker.getMobile());
                    mendOrderDTO.setType(mendOrder.getType());
                    mendOrderDTO.setState(mendOrder.getState());
                    mendOrderDTO.setTotalAmount(mendOrder.getTotalAmount());
                    mendOrderDTOS.add(mendOrderDTO);

                }
            }
        }

        return mendOrderDTOS;
    }


}
