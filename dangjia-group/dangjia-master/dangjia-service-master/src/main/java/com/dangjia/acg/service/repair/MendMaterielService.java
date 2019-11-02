package com.dangjia.acg.service.repair;

import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.api.supplier.DjSupplierAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.repair.MendOrderDTO;
import com.dangjia.acg.dto.storefront.StorefrontDTO;
import com.dangjia.acg.mapper.delivery.ISplitDeliverMapper;
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
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    private DjSupplierAPI djSupplierAPI ;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private BasicsStorefrontAPI basicsStorefrontAPI;

    /**
     * 要货退货 查询补材料
     */
    public List<MendMateriel> askAndQuit(String workerTypeId, String houseId, String categoryId, String name) {
        List<MendMateriel> mendMaterielList = mendMaterialMapper.askAndQuit(workerTypeId, houseId, categoryId, name);
        return mendMaterielList;
    }

    /**
     * 房子id查询业主退货单列表
     * landlordState
     * 0生成中,1平台审核中,2不通过,3通过
     */
    public ServerResponse landlordState(String userId,String cityId,String houseId, PageDTO pageDTO, String beginDate, String endDate, String state,String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
//            List<MendOrder> mendOrderList = mendOrderMapper.landlordState(houseId);
            Storefront storefront= basicsStorefrontAPI.queryStorefrontByUserID(userId,cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息");
            }
            List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddress(storefront.getId(),houseId, 4, beginDate, endDate, state,likeAddress);
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
     *
     * @param request
     * @param cityId
     * @param houseId 房子id
     * @param pageDTO
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @param state 状态：（0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回,5已关闭）
     * @param likeAddress 模糊查询参数
     * @return
     */
    public ServerResponse materialBackStateHandle(HttpServletRequest request, String cityId, String houseId, PageDTO pageDTO, String beginDate, String endDate, String state, String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            //通过缓存查询店铺信息
            String userId = request.getParameter("userId");
            Storefront storefront= basicsStorefrontAPI.queryStorefrontByUserID(userId,cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息");
            }
//            List<MendOrder> mendOrderList = mendOrderMapper.materialBackState(houseId); 2
            List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddressHandle(storefront.getId(),houseId, 2, beginDate, endDate, state,likeAddress);
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
     *
     * @param userId
     * @param cityId
     * @param houseId 房子id
     * @param pageDTO
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @param state 状态：（0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回,5已关闭）
     * @param likeAddress 模糊查询参数
     * @return
     */
    public ServerResponse  materialBackStateProcessing (String userId,String cityId,String houseId, PageDTO pageDTO, String beginDate, String endDate, String state,String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            //通过缓存查询店铺信息
            Storefront storefront= basicsStorefrontAPI.queryStorefrontByUserID(userId,cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息");
            }
            List<MendOrder> mendOrderList = mendOrderMapper.materialBackStateProcessing(storefront.getId(),houseId, 2, beginDate, endDate, state,likeAddress);
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
    public ServerResponse materialBackState(String userId,String cityId,String houseId, PageDTO pageDTO, String beginDate, String endDate, String state,String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            //通过缓存查询店铺信息
            Storefront storefront= basicsStorefrontAPI.queryStorefrontByUserID(userId,cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息");
            }
//            List<MendOrder> mendOrderList = mendOrderMapper.materialBackState(houseId); 2
            List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddress(storefront.getId(),houseId, 2, beginDate, endDate, state,likeAddress);
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
    public ServerResponse mendMaterialList(String mendOrderId,String userId) {
        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
        House house = houseMapper.selectByPrimaryKey(mendOrder.getHouseId());
        List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrderId);
        List<Map> mendMaterielMaps = new ArrayList<>();
        for (MendMateriel mendMateriel : mendMaterielList) {
            mendMateriel.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
            Map map = BeanUtils.beanToMap(mendMateriel);
            Warehouse warehouse = warehouseMapper.getByProductId(mendMateriel.getProductId(), mendOrder.getHouseId());
            if (warehouse == null) {
                map.put(Warehouse.RECEIVE, "0");
            } else {
                //工匠退材料新增已收货数量字段
                if (mendOrder.getType() == 2) {
                    map.put(Warehouse.RECEIVE, warehouse.getReceive() == null ? 0d : warehouse.getReceive());
                }
                //业主退材料增加未发货数量
                if (mendOrder.getType() == 4) {
                    //未发货数量=已要 - 已收
                    map.put(Warehouse.RECEIVE, warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getAskCount());
                }
            }
            List<String> supplierId = splitDeliverMapper.getSupplierGoodsId(mendOrder.getHouseId(), mendMateriel.getProductSn());
            List<DjSupplier> djSuppliers = new ArrayList<DjSupplier>();
            if (supplierId.size() > 0) {
                for (int i = 0; i < supplierId.size(); i++) {
                    //Supplier supplier = forMasterAPI.getSupplier(house.getCityId(), supplierId.get(i));
                    DjSupplier djSupplier =djSupplierAPI.queryDjSupplierByPass(supplierId.get(i));
                    djSuppliers.add(djSupplier);
                }
                map.put("suppliers", djSuppliers);
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
    public ServerResponse materialOrderState(String storefrontId,String houseId, PageDTO pageDTO, String beginDate, String endDate,String state, String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if (!CommonUtil.isEmpty(beginDate) && !CommonUtil.isEmpty(endDate)) {
                if (beginDate.equals(endDate)) {
                    beginDate = beginDate + " " + "00:00:00";
                    endDate = endDate + " " + "23:59:59";
                }
            }
//            List<MendOrder> mendOrderList = mendOrderMapper.materialOrderState(houseId);
            List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddress(storefrontId,houseId, 0, beginDate, endDate, state,likeAddress);
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
                    mendOrderDTO.setAddress(house.getHouseName());
                    Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                    mendOrderDTO.setMemberName(member.getNickName() == null ? member.getName() : member.getNickName());
                    mendOrderDTO.setMemberId(member.getId());
                    mendOrderDTO.setMemberMobile(member.getMobile());
                }
            }
            Member worker = memberMapper.selectByPrimaryKey(mendOrder.getApplyMemberId());
            if(worker!=null) {
                mendOrderDTO.setApplyMemberId(worker.getId());
                mendOrderDTO.setApplyName(CommonUtil.isEmpty(worker.getName()) ? worker.getNickName() : worker.getName());
                mendOrderDTO.setApplyMobile(worker.getMobile());
            }
            mendOrderDTO.setType(mendOrder.getType());
            mendOrderDTO.setState(mendOrder.getState());
            mendOrderDTO.setTotalAmount(mendOrder.getTotalAmount());
            mendOrderDTOS.add(mendOrderDTO);
        }

        return mendOrderDTOS;
    }


}
