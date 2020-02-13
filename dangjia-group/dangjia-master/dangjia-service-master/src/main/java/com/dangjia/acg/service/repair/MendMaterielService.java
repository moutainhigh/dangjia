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
import com.dangjia.acg.dto.deliver.OrderSplitItemDTO;
import com.dangjia.acg.dto.refund.OrderProgressDTO;
import com.dangjia.acg.dto.repair.*;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.delivery.ISplitDeliverMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.*;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.*;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.product.MasterStorefrontService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/12/11 0011
 * Time: 9:41
 */
@Service
public class MendMaterielService {
    protected static final Logger logger = LoggerFactory.getLogger(MendMaterielService.class);
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
    private MasterStorefrontService masterStorefrontService;

    @Autowired
    private IMendTypeRoleMapper mendTypeRoleMapper;
    @Autowired
    private IMendOrderCheckMapper mendOrderCheckMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IMendDeliverMapper mendDeliverMapper ;

    @Autowired
    private IComplainMapper iComplainMapper;
    /**
     * 售后管理--退货退款--分发供应商列表
     * @param request
     * @param cityId
     * @param userId
     * @param mendOrderId 退货申请单ID
     * @return
     */
    public ServerResponse searchReturnRefundMaterielList(HttpServletRequest request, String cityId, String userId, String mendOrderId) {
        try{
            Map<String,Object> resultMap=new HashMap();
            MendOrder mendOrder=mendOrderMapper.selectByPrimaryKey(mendOrderId);
            if(mendOrder==null){
                return ServerResponse.createByErrorMessage("未找到符合条件的退货单");
            }
            if(mendOrder.getType()!=2&&mendOrder.getType()!=5){
                return ServerResponse.createByErrorMessage("此单不属于退货退款单，不能分发");
            }
            if(mendOrder.getState()!=1){
                return ServerResponse.createByErrorMessage("不是处理中的单，不能操作分发");
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
            //查询申请退货单明细
            List<OrderSplitItemDTO> mendMaterialList=mendMaterialMapper.searchReturnRefundMaterielList(mendOrderId);
            if(mendMaterialList!=null&&mendMaterialList.size()>0){
                for (OrderSplitItemDTO sd:mendMaterialList){
                    sd.setImageUrl(StringTool.getImageSingle(sd.getImage(),address));
                    //2.2给当前店铺当前房子供过此商品的供应商
                    List<Map<String,Object>> supplierIdlist = mendMaterialMapper.getsupplierInfoList(mendOrder.getStorefrontId(),sd.getProductId(),mendOrder.getHouseId());
                    if(supplierIdlist==null||supplierIdlist.size()<=0){
                        //若未查到线上可发货的供应商，则查询非平台供应商给到页面选择
                        supplierIdlist=splitDeliverMapper.queryNonPlatformSupplier();
                    }
                    sd.setSupplierIdlist(supplierIdlist);
                }
            }
            resultMap.put("createDate",mendOrder.getCreateDate());//申请时间
            resultMap.put("state",mendOrder.getState());//申请状态，1待分配
            resultMap.put("imageUrl",StringTool.getImage(mendOrder.getImageArr(),address));//退货图片，相关凭证
            resultMap.put("mendOrderId",mendOrderId);
            resultMap.put("mendMaterialList",mendMaterialList);//要货单明细表
            return ServerResponse.createBySuccess("查询成功", resultMap);
        } catch (Exception e) {
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 店铺退货分发供应商
     * @param mendOrderId
     * @param userId
     * @param actualCountList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse returnProductDistributionSupplier(String mendOrderId, String userId,String cityId, String actualCountList) {
        try {
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            MendOrder mendOrder=mendOrderMapper.selectByPrimaryKey(mendOrderId);//补退订单表
            if(mendOrder==null)
                  return ServerResponse.createByErrorMessage("不存在退货单");
            //String aa="[{\"mendOrderId\":\"865634841567998628778\",\"actualCount\":9,supplierId:\"xxxxx"\}]";
            List<Map<String,Object>> list= JSON.parseObject(actualCountList, List.class);
            for (Map<String, Object> stringStringMap : list) {
                String id=stringStringMap.get("id").toString();//商品id
                String actualCount=stringStringMap.get("actualCount").toString();//实际退货数
                String supplierId=stringStringMap.get("supplierId").toString();//供应商id
                DjSupplier djSupplier = djSupplierAPI.queryDjSupplierByPass(supplierId);//供应商信息

                mendOrder.setState(3);//（0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回,6已关闭7，已审核待处理 8，部分退货）
                mendOrder.setModifyDate(new Date());//更新时间
                Integer i = mendOrderMapper.updateByPrimaryKeySelective(mendOrder);

                if (i <= 0)
                    return ServerResponse.createBySuccessMessage("全部退货失败");
                MendMateriel mendMateriel=mendMaterialMapper.selectByPrimaryKey(id);
                mendMateriel.setActualCount(Double.parseDouble(actualCount));
                mendMaterialMapper.updateByPrimaryKey(mendMateriel);

                Example example = new Example(MendDeliver.class);
                MendDeliver mendDeliver=new MendDeliver();//供应商退货单
                mendDeliver.setNumber(mendOrder.getNumber() + "00" + mendDeliverMapper.selectCountByExample(example));//发货单号
                mendDeliver.setHouseId(mendOrder.getHouseId());//房子id
                mendDeliver.setMendOrderId(mendOrderId);//退货订单号
                mendDeliver.setTotalAmount(mendMateriel.getPrice()*Double.parseDouble(actualCount));//退货单总额
                mendDeliver.setDeliveryFee(0d);//运费
                //mendDeliver.setApplyMoney();//供应商申请结算的价格
                //mendDeliver.setApplyState();//供应商申请结算的状态
                //mendDeliver.setReason();//不同意理由
                //mendDeliver.setShipName();//退货人姓名
                //mendDeliver.setShipMobile();//退货人手机
                mendDeliver.setSupplierTelephone(djSupplier.getTelephone());//供应商联系电话
                mendDeliver.setSupplierName(djSupplier.getCheckPeople());//供应商姓名
                mendDeliver.setSupplierId(supplierId);//供应商id
                mendDeliver.setStorefrontId(storefront.getId());//店铺id
                mendDeliverMapper.insertSelective(mendDeliver);
            }
            return ServerResponse.createBySuccessMessage("店铺退货分发供应商成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("店铺退货分发供应商异常");
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
    public ServerResponse confirmReturnMendMaterial(String mendOrderId, String userId, Integer type, String actualCountList,String returnReason,String supplierId) {
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
            return ServerResponse.createByErrorMessage("退货失败");
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
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
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
        Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
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

    /**
     *店铺--售后处理--待处理列表
     * @param request
     * @param cityId 城市ID
     * @param userId 用户ID
     * @param pageDTO
     * @param state 状态默认：1待处理，2已处理
     * @param likeAddress
     * @param type 查询类型：1退货退款，2仅退款
     * @return
     */
    public ServerResponse searchReturnRrefundList(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, Integer state, String likeAddress,Integer type) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            //判断是否有维护店铺信息，若未维护，则返回提示
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            List<MendOrderDTO> mendOrderList = mendOrderMapper.searchReturnRrefundList(storefront.getId(), type, state, likeAddress);
            PageInfo pageResult = new PageInfo(mendOrderList);
            pageResult.setList(mendOrderList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
           logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    /**
     *店铺--售后处理--待处理列表
     * @param request
     * @param cityId 城市ID
     * @param userId 用户ID
     * @param pageDTO
     * @param state 状态默认：1.已分发供应商 2.已结束
     * @param likeAddress 单号或地址
     * @return
     */
    public ServerResponse searchReturnRefundSplitList(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, Integer state, String likeAddress) {
       try{
           //判断是否有维护店铺信息，若未维护，则返回提示
           Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
           if (storefront == null) {
               return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
           }
           PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
           List<MendDeliverDTO> mendDeliverList = mendDeliverMapper.searchReturnRefundSplitList(storefront.getId(), state, likeAddress);
           PageInfo pageResult = new PageInfo(mendDeliverList);
           pageResult.setList(mendDeliverList);
           return ServerResponse.createBySuccess("查询成功",pageResult);
       }catch(Exception e){
           logger.error("查询失败",e);
           return ServerResponse.createByErrorMessage("查询失败");
       }
    }
    //店铺管理—售后管理—业主退货退款(处理中)
   /* public ServerResponse ownerReturnProssing(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, String state, String likeAddress) {
        try {
            try {
                PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
                Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
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
    }*/
    //店铺管理—售后管理—业主退货退款(已经处理)
    public ServerResponse ownerReturnHandle(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, String state, String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            //通过缓存查询店铺信息
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
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
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
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
     *//*
    public ServerResponse materialBackStateProcessing(String userId, String cityId, PageDTO pageDTO, String state, String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            //通过缓存查询店铺信息
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
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
    }*/

    /**
     * 房子id查询退货单列表
     * material_back_state
     * 0生成中,1平台审核中，2平台审核不通过，3审核通过，4管家取消
     */
    public ServerResponse materialBackState(String userId, String cityId, PageDTO pageDTO, String state, String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            //通过缓存查询店铺信息
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
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
        if (mendOrder == null) {
            return ServerResponse.createByErrorMessage("不存在当前补退订单");
        }
        House house = houseMapper.selectByPrimaryKey(mendOrder.getHouseId());//房子信息
        ReturnMendMaterielDTO returnMendMaterielDTO=new ReturnMendMaterielDTO();

        List<ReturnOrderProgressDTO> mendMaterielProgressList= mendMaterialMapper.queryMendMaterielProgress(mendOrderId);
        if (mendMaterielProgressList == null || mendMaterielProgressList.size() <= 0) {
            return ServerResponse.createByErrorMessage("查无数据！");
        }
        returnMendMaterielDTO.setMendMaterielProgressList(mendMaterielProgressList);


        List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrderId);
        Double  totalPrice=0d;
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        returnMendMaterielDTO.setImageArr(mendOrder.getImageArr()!=""?address+mendOrder.getImageArr():"");//相关凭证
        returnMendMaterielDTO.setReturnReason(mendOrder.getReturnReason()!=null?mendOrder.getReturnReason():"");//失败原因
        returnMendMaterielDTO.setState(mendOrder.getState()!=null?mendOrder.getState()+"":"");
        returnMendMaterielDTO.setType(mendOrder.getType()!=null?mendOrder.getType()+"":"");
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
            if(supplierIdList.size()==0)
            {
                //非平台供應商
                supplierIdList=splitDeliverMapper.queryNonPlatformSupplier();
                mendMateriel.setSupplierIdList(supplierIdList);//查看有那些供应商供应
            }
            else
            {
                //map.put("supplierIdlist",supplierIdlist);//正常供應商
                mendMateriel.setSupplierIdList(supplierIdList);//查看有那些供应商供应
            }
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
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
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
            Member worker = memberMapper.selectByPrimaryKey(mendOrder.getApplyMemberId());//申请人id
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


    /**
     * 业主清点剩余材料
     * @param data
     * @return
     */
    public ServerResponse querySurplusMaterial(String data) {
        try {
            ArrSurplusMaterialDTO arrSurplusMaterialDTO = new ArrSurplusMaterialDTO();
            List<SurplusMaterialDTO> surplusMaterialDTOS = mendOrderMapper.querySurplusMaterial(data);
            if(surplusMaterialDTOS != null && surplusMaterialDTOS.size() >0){
                String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                for (SurplusMaterialDTO surplusMaterialDTO : surplusMaterialDTOS) {
                    surplusMaterialDTO.setImage(imageAddress + surplusMaterialDTO.getImage());
                }
                arrSurplusMaterialDTO.setList(surplusMaterialDTOS);
                arrSurplusMaterialDTO.setCreateDate(surplusMaterialDTOS.get(0).getCreateDate());
                arrSurplusMaterialDTO.setMobile(surplusMaterialDTOS.get(0).getMobile());
                arrSurplusMaterialDTO.setName(surplusMaterialDTOS.get(0).getName());
                arrSurplusMaterialDTO.setHouseId(surplusMaterialDTOS.get(0).getHouseId());
                arrSurplusMaterialDTO.setWorkerId(surplusMaterialDTOS.get(0).getWorkerId());
            }
            return ServerResponse.createBySuccess("查询成功", arrSurplusMaterialDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 业主审核部分退货
     * @param data
     * @return
     */
    public ServerResponse queryTrialRetreatMaterial(String data) {
        try {
            ArrSurplusMaterialDTO arrSurplusMaterialDTO = new ArrSurplusMaterialDTO();
            List<SurplusMaterialDTO> surplusMaterialDTOS = mendOrderMapper.queryTrialRetreatMaterial(data);
            if(surplusMaterialDTOS != null && surplusMaterialDTOS.size() >0){
                String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                for (SurplusMaterialDTO surplusMaterialDTO : surplusMaterialDTOS) {
                    surplusMaterialDTO.setImage(imageAddress + surplusMaterialDTO.getImage());
                }
                arrSurplusMaterialDTO.setList(surplusMaterialDTOS);
                arrSurplusMaterialDTO.setMendOrderId(surplusMaterialDTOS.get(0).getMendOrderId());
                arrSurplusMaterialDTO.setStorefrontName(surplusMaterialDTOS.get(0).getStorefrontName());
                arrSurplusMaterialDTO.setStorefrontMobile(surplusMaterialDTOS.get(0).getStorefrontMobile());
                arrSurplusMaterialDTO.setReturnReason(surplusMaterialDTOS.get(0).getReturnReason());
                arrSurplusMaterialDTO.setBusinessOrderNumber(surplusMaterialDTOS.get(0).getBusinessOrderNumber());
                arrSurplusMaterialDTO.setType(surplusMaterialDTOS.get(0).getType());
            }
            return ServerResponse.createBySuccess("查询成功", arrSurplusMaterialDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }



    /**
     * 申请平台介入
     * @param mendOrderId
     * @return
     */
    public ServerResponse addPlatformComplain(String userToken,String mendOrderId,String description){
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }

        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
        if(mendOrder ==null){
            return ServerResponse.createByErrorMessage("该订单不存在");
        }
        Member member = memberMapper.selectByPrimaryKey(accessToken.getUserId());
        if(member ==null){
            return ServerResponse.createByErrorMessage("该用户不存在");
        }
        Complain complain = new Complain();
        complain.setMemberId(accessToken.getUserId());
        complain.setComplainType(7);
        complain.setStatus(0);
        complain.setDescription(description);
        complain.setBusinessId(mendOrderId);
        complain.setUserName(member.getName());
        complain.setUserNickName(member.getNickName());
        complain.setUserMobile(member.getMobile());
        complain.setHouseId(mendOrder.getHouseId());
        iComplainMapper.insert(complain);
        return ServerResponse.createByErrorMessage("新增成功");
    }

}
