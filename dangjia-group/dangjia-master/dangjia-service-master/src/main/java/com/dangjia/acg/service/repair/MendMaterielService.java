package com.dangjia.acg.service.repair;

import com.alibaba.fastjson.JSON;
import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.api.supplier.DjSupplierAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.refund.OrderProgressDTO;
import com.dangjia.acg.dto.repair.MendOrderDTO;
import com.dangjia.acg.dto.repair.ReturnMendMaterielDTO;
import com.dangjia.acg.dto.repair.ReturnOrderProgressDTO;
import com.dangjia.acg.mapper.delivery.ISplitDeliverMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.mapper.repair.IMendOrderCheckMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.repair.IMendTypeRoleMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.repair.MendOrderCheck;
import com.dangjia.acg.modle.repair.MendTypeRole;
import com.dangjia.acg.modle.storefront.Storefront;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
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
    private DjSupplierAPI djSupplierAPI;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private BasicsStorefrontAPI basicsStorefrontAPI;

    @Autowired
    private IMendTypeRoleMapper mendTypeRoleMapper;
    @Autowired
    private IMendOrderCheckMapper mendOrderCheckMapper;


    /**
     * 店铺退货分发供应商列表
     * @param request
     * @param cityId
     * @param userId
     * @param pageDTO
     * @param likeAddress
     * @return
     */
    public ServerResponse storeReturnDistributionSupplier(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, String likeAddress) {
        try{

            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            List<MendOrder> mendOrderList = mendOrderMapper.storeReturnDistributionSupplier(storefront.getId(), likeAddress);
            PageInfo pageResult = new PageInfo(mendOrderList);
            List<MendOrderDTO> mendOrderDTOS = getMendOrderDTOList(mendOrderList);
            pageResult.setList(mendOrderDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("店铺退货分发供应商异常");
        }
    }

    /**
     *業主審核部分退貨-申请平台介入
     * @param request
     * @param cityId
     * @param houseId
     * @param pageDTO
     * @return
     */
    public ServerResponse applyPlatformAccess(HttpServletRequest request, String cityId, String houseId, PageDTO pageDTO) {
        try{
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("業主審核部分退貨-申请平台介入异常");
        }
    }

    /**
     *业主申请部分退货-接受
     * @param request
     * @param cityId
     * @param houseId
     * @param pageDTO
     * @return
     */
    public ServerResponse acceptPartialReturn(HttpServletRequest request, String cityId, String houseId, PageDTO pageDTO) {
        try{

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("业主申请部分退货-接受异常");
        }
    }

    /**
     * 业主审核部分退货列表
     * @param request
     * @param cityId
     * @param houseId
     * @param pageDTO
     * @return
     */
    public ServerResponse reviewReturnList(HttpServletRequest request, String cityId, String houseId, PageDTO pageDTO) {
        try{
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("业主申请部分退货-接受异常");
        }
    }


    /**
     * 确认退货
     * @param mendOrderId
     * @param userId
     * @param type
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse confirmReturnMendMaterial(String mendOrderId, String userId, Integer type, String actualCountList,String returnReason) {
        try {
            if (type == 0) {
                //String aa="[{\"mendOrderId\":\"865634841567998628778\",\"actualCount\":9}]";
                List<Map<String,Object>> list= JSON.parseObject(actualCountList, List.class);
                for (Map<String, Object> stringStringMap : list) {
                    String id=stringStringMap.get("id").toString();
                    String actualCount=stringStringMap.get("actualCount").toString();
                    //全部退货
                    MendOrder mendOrder = new MendOrder();
                    mendOrder.setId(mendOrderId);
                    mendOrder.setState(3);//（0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回,6已关闭7，已审核待处理 8，部分退货）
                    mendOrder.setModifyDate(new Date());//更新时间
                    Integer i = mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                    if (i <= 0)
                        return ServerResponse.createBySuccessMessage("全部退货失败");

                    MendMateriel mendMateriel=new MendMateriel();
                    mendMateriel.setId(id);
                    mendMateriel.setActualCount(Double.parseDouble(actualCount));

                    mendMaterialMapper.updateByPrimaryKey(mendMateriel);

                    return ServerResponse.createBySuccessMessage("全部退货成功");
                }

            } else {
                List<Map<String,Object>> list= JSON.parseObject(actualCountList, List.class);
                for (Map<String, Object> stringStringMap : list) {
                    String id=stringStringMap.get("id").toString();
                    String actualCount=stringStringMap.get("actualCount").toString();
                    //部分退货
                    MendOrder mendOrder = new MendOrder();
                    mendOrder.setId(mendOrderId);
                    mendOrder.setState(8);//状态（0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回,6已关闭7，已审核待处理 8，部分退货）
                    mendOrder.setReturnReason(returnReason);
                    mendOrder.setModifyDate(new Date());//更新时间
                    Integer j = mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                    if (j <= 0)
                        return ServerResponse.createBySuccessMessage("全部退货失败");
                    MendMateriel mendMateriel=new MendMateriel();
                    mendMateriel.setId(id);
                    mendMateriel.setActualCount(Double.parseDouble(actualCount));
                    mendMaterialMapper.updateByPrimaryKey(mendMateriel);
                    if (j > 0) {
                        MendOrder myMendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
                        MendTypeRole mendTypeRole = mendTypeRoleMapper.getByType(myMendOrder.getType());
                        String[] roleArr = mendTypeRole.getRoleArr().split(",");
                        for (int i = 0; i < roleArr.length; i++) {
                            MendOrderCheck mendOrderCheck = new MendOrderCheck();
                            mendOrderCheck.setMendOrderId(myMendOrder.getId());
                            mendOrderCheck.setRoleType(roleArr[i]);
                            mendOrderCheck.setState(0);
                            mendOrderCheck.setSort(i + 1);//顺序
                            mendOrderCheckMapper.insert(mendOrderCheck);
                        }
                        return ServerResponse.createBySuccessMessage("部分退货成功");
                    }
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

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
    public ServerResponse landlordState(String userId, String cityId, PageDTO pageDTO, String state, String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
//            List<MendOrder> mendOrderList = mendOrderMapper.landlordState(houseId);
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddress(storefront.getId(), 4, state, likeAddress);
            PageInfo pageResult = new PageInfo(mendOrderList);
            List<MendOrderDTO> mendOrderDTOS = getMendOrderDTOList(mendOrderList);
            pageResult.setList(mendOrderDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    //业主仅退款(已经处理)
    public ServerResponse landlordStateHandle(HttpServletRequest request, String cityId,  PageDTO pageDTO, String state, String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        //通过缓存查询店铺信息
        String userId = request.getParameter("userId");
        Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
        if (storefront == null) {
            return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
        }
        List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddressHandle(storefront.getId(), 4, state, likeAddress);
        PageInfo pageResult = new PageInfo(mendOrderList);
        List<MendOrderDTO> mendOrderDTOS = getMendOrderDTOList(mendOrderList);
        pageResult.setList(mendOrderDTOS);
       return ServerResponse.createBySuccess("查询成功", pageResult);
    } catch (Exception e) {
        e.printStackTrace();
        return ServerResponse.createByErrorMessage("查询失败");
    }
    }
    //店铺管理—售后管理—业主退货退款(待处理)
    public ServerResponse ownerReturnHandleIng(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, String state, String likeAddress) {
        try {
            try {
                PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());

                //通过缓存查询店铺信息
                Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
                if (storefront == null) {
                    return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
                }
                List<MendOrder> mendOrderList = mendOrderMapper.materialBackStateProcessing(storefront.getId(), 5, state, likeAddress);
                PageInfo pageResult = new PageInfo(mendOrderList);
                List<MendOrderDTO> mendOrderDTOS = getMendOrderDTOList(mendOrderList);
                pageResult.setList(mendOrderDTOS);
                return ServerResponse.createBySuccess("查询成功", pageResult);
            } catch (Exception e) {
                e.printStackTrace();
                return ServerResponse.createByErrorMessage("查询失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    //店铺管理—售后管理—业主退货退款(处理中)
    public ServerResponse ownerReturnProssing(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, String state, String likeAddress) {
        try {
            try {
                PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
                //通过缓存查询店铺信息
                Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
                if (storefront == null) {
                    return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
                }
                List<MendOrder> mendOrderList = mendOrderMapper.materialBackStateProcessing(storefront.getId(), 5, state, likeAddress);
                PageInfo pageResult = new PageInfo(mendOrderList);
                List<MendOrderDTO> mendOrderDTOS = getMendOrderDTOList(mendOrderList);
                pageResult.setList(mendOrderDTOS);
                return ServerResponse.createBySuccess("查询成功", pageResult);
            } catch (Exception e) {
                e.printStackTrace();
                return ServerResponse.createByErrorMessage("查询失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    //店铺管理—售后管理—业主退货退款(已经处理)
    public ServerResponse ownerReturnHandle(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, String state, String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            //通过缓存查询店铺信息
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
//            List<MendOrder> mendOrderList = mendOrderMapper.materialBackState(houseId); 2
            List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddressHandle(storefront.getId(), 5, state, likeAddress);
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
             * @param request
             * @param cityId
             * @param pageDTO
             * @param state       状态：（0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回,5已关闭）
             * @param likeAddress 模糊查询参数
             * @return
             */
    public ServerResponse materialBackStateHandle(HttpServletRequest request, String userId,String cityId, PageDTO pageDTO, String state, String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            //通过缓存查询店铺信息
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddressHandle(storefront.getId(), 2, state, likeAddress);
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
     * @param userId
     * @param cityId
     * @param pageDTO
     * @param state       状态：（0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回,6已关闭 7已审核待处理）
     * @param likeAddress 模糊查询参数
     * @return
     */
    public ServerResponse materialBackStateProcessing(String userId, String cityId, PageDTO pageDTO, String state, String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            //通过缓存查询店铺信息
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            List<MendOrder> mendOrderList = mendOrderMapper.materialBackStateProcessing(storefront.getId(), 2, state, likeAddress);
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
    public ServerResponse materialBackState(String userId, String cityId, PageDTO pageDTO, String state, String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            //通过缓存查询店铺信息
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddress(storefront.getId(), 2,  state, likeAddress);
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
     * 新版查看补退单明细
     * @param mendOrderId
     * @param userId
     * @return
     */
    public ServerResponse queryMendMaterialList(String mendOrderId, String userId) {
        //合计付款
        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);//补退订单表
        House house = houseMapper.selectByPrimaryKey(mendOrder.getHouseId());//房子信息
        ReturnMendMaterielDTO returnMendMaterielDTO=new ReturnMendMaterielDTO();

        List<ReturnOrderProgressDTO> mendMaterielProgressList= mendMaterialMapper.queryMendMaterielProgress(mendOrderId);
        if(mendMaterielProgressList!=null&&mendMaterielProgressList.size()>0)
        returnMendMaterielDTO.setMendMaterielProgressList(mendMaterielProgressList);


        List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrderId);
        Double  totalPrice=0d;
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        returnMendMaterielDTO.setImageArr(mendOrder.getImageArr()!=""?address+mendOrder.getImageArr():"");//相关凭证
        returnMendMaterielDTO.setReturnReason(mendOrder.getReturnReason()!=null?mendOrder.getReturnReason():"");//失败原因
        returnMendMaterielDTO.setState(mendOrder.getState()!=null?mendOrder.getState()+"":"");
        for (MendMateriel mendMateriel : mendMaterielList) {
            mendMateriel.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
            Warehouse warehouse = warehouseMapper.getByProductId(mendMateriel.getProductId(), mendOrder.getHouseId());//材料仓库统计
            if (warehouse == null) {
                mendMateriel.setReceive(0d);//收货总数
            } else {
                //工匠退材料新增已收货数量字段
                if (mendOrder.getType() == 2) {
                    mendMateriel.setReceive(warehouse.getReceive() == null ? 0d : warehouse.getReceive());
                }
                //业主退材料增加未发货数量
                if (mendOrder.getType() == 4) {
                    //未发货数量=已要 - 已收
                    mendMateriel.setReceive(warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()));
                }

            }
            //合计退款
            totalPrice+=mendMateriel.getTotalPrice();
            //判断商品有哪些供应商供应
            List<Map<String,Object>> supplierIdList = splitDeliverMapper.getMendMaterialSupplierId(mendOrder.getHouseId(), mendMateriel.getProductId());
            mendMateriel.setSupplierIdList(supplierIdList);//查看有那些供应商供应
        }
        returnMendMaterielDTO.setMendMaterielList(mendMaterielList);
        returnMendMaterielDTO.setTotalPrice(totalPrice);
        return ServerResponse.createBySuccess("查询成功", returnMendMaterielDTO);
    }

    /**
     * 根据mendOrderId查明细
     */
    public ServerResponse mendMaterialList(String mendOrderId, String userId) {
        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
        House house = houseMapper.selectByPrimaryKey(mendOrder.getHouseId());
        List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrderId);
        List<Map> mendMaterielMaps = new ArrayList<>();
        for (MendMateriel mendMateriel : mendMaterielList) {
            mendMateriel.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
            Map map = BeanUtils.beanToMap(mendMateriel);
            Warehouse warehouse = warehouseMapper.getByProductId(mendMateriel.getProductId(), mendOrder.getHouseId());//材料仓库统计
            if (warehouse == null) {
                map.put(Warehouse.RECEIVE, "0");//收货总数
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
            //判断商品有哪些供应商供应
            List<Map<String,Object>> supplierIdList = splitDeliverMapper.getSupplierGoodsId(mendOrder.getHouseId(), mendMateriel.getProductSn());
            if (supplierIdList!=null)
                map.put("suppliers", supplierIdList);
            mendMaterielMaps.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", mendMaterielMaps);
    }

    /**
     * 房子id查询补货单列表
     * materialOrderState
     * 0生成中,1平台审核中，2平台审核不通过，3平台审核通过待业主支付,4业主已支付，5业主不同意，6管家取消
     */
    public ServerResponse materialOrderState(String userId, String cityId,String houseId, PageDTO pageDTO, String beginDate, String endDate, String state, String likeAddress) {
        try {
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if (!CommonUtil.isEmpty(beginDate) && !CommonUtil.isEmpty(endDate)) {
                if (beginDate.equals(endDate)) {
                    beginDate = beginDate + " " + "00:00:00";
                    endDate = endDate + " " + "23:59:59";
                }
            }
//            List<MendOrder> mendOrderList = mendOrderMapper.materialOrderState(houseId);
            List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddress(storefront.getId(), 0, state, likeAddress);
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
            if (worker != null) {
                mendOrderDTO.setApplyMemberId(worker.getId());
                mendOrderDTO.setApplyName(CommonUtil.isEmpty(worker.getName()) ? worker.getNickName() : worker.getName());
                mendOrderDTO.setApplyMobile(worker.getMobile());
            }
            mendOrderDTO.setType(mendOrder.getType());
            mendOrderDTO.setState(mendOrder.getState());
            mendOrderDTO.setTotalAmount(mendOrder.getTotalAmount());

            mendOrderDTO.setDeliverNumber(mendOrder.getDeliverNumber());
            mendOrderDTO.setSupplierName(mendOrder.getSupplierName());

            mendOrderDTOS.add(mendOrderDTO);

        }

        return mendOrderDTOS;
    }


}
