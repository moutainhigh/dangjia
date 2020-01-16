package com.dangjia.acg.service.engineer;

import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.api.StorefrontConfigAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.ActuarialProductDTO;
import com.dangjia.acg.dto.actuary.app.ActuarialProductAppDTO;
import com.dangjia.acg.dto.engineer.*;
import com.dangjia.acg.dto.product.StorefrontProductDTO;
import com.dangjia.acg.dto.refund.OrderProgressDTO;
import com.dangjia.acg.mapper.IConfigMapper;
import com.dangjia.acg.mapper.account.IMasterAccountFlowRecordMapper;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.delivery.IOrderMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordContentMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordProductMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordResponsiblePartyMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.product.IMasterProductTemplateMapper;
import com.dangjia.acg.mapper.product.IMasterProductTemplateRatioMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeOrderMapper;
import com.dangjia.acg.mapper.task.IMasterTaskStackMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.model.Config;
import com.dangjia.acg.modle.account.AccountFlowRecord;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecord;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecordContent;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecordProduct;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecordResponsibleParty;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.TaskStack;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.product.BasicsProductTemplateRatio;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import com.dangjia.acg.modle.safe.WorkerTypeSafeOrder;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.acquisition.MasterCostAcquisitionService;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.core.TaskStackService;
import com.dangjia.acg.service.house.HouseService;
import com.dangjia.acg.service.pay.PaymentService;
import com.dangjia.acg.service.product.MasterProductTemplateService;
import com.dangjia.acg.service.safe.WorkerTypeSafeOrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ExampleProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 13/12/2019
 * Time: 上午 10:01
 */
@Service
public class DjMaintenanceRecordService {

    @Autowired
    private DjMaintenanceRecordMapper djMaintenanceRecordMapper;
    @Autowired
    private DjMaintenanceRecordResponsiblePartyMapper djMaintenanceRecordResponsiblePartyMapper;
    @Autowired
    private DjMaintenanceRecordProductMapper djMaintenanceRecordProductMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private IMasterAccountFlowRecordMapper iMasterAccountFlowRecordMapper;
    @Autowired
    private IMasterStorefrontMapper iMasterStorefrontMapper;
    @Autowired
    IWorkerDetailMapper iWorkerDetailMapper;
    @Autowired
    private MasterCostAcquisitionService masterCostAcquisitionService;

    @Autowired
    private IConfigMapper iConfigMapper;
    @Autowired
    private IOrderMapper iOrderMapper;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IComplainMapper iComplainMapper;
    @Autowired
    private HouseService houseService;

    @Autowired
    private IMasterTaskStackMapper iMasterTaskStackMapper;
    @Autowired
    private DjMaintenanceRecordContentMapper djMaintenanceRecordContentMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;

    @Autowired
    private IWorkerTypeSafeOrderMapper workerTypeSafeOrderMapper;
    @Autowired
    private MasterProductTemplateService imasterProductTemplateService;

    @Autowired
    private IHouseFlowMapper iHouseFlowMapper;

    @Autowired
    private TaskStackService taskStackService;

    @Autowired
    private BasicsStorefrontAPI basicsStorefrontAPI;

    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private StorefrontConfigAPI storefrontConfigAPI;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IMasterProductTemplateMapper iMasterProductTemplateMapper;
    @Autowired
    private IMasterStorefrontProductMapper iMasterStorefrontProductMapper;
    @Autowired
    private WorkerTypeSafeOrderService workerTypeSafeOrderService;
    @Autowired
    private ConfigMessageService configMessageService;

    @Autowired
    private IMasterProductTemplateRatioMapper iMasterProductTemplateRatioMapper;
    @Autowired
    private PaymentService paymentService;
    private static Logger logger = LoggerFactory.getLogger(DjMaintenanceRecordService.class);


    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveMaintenanceRecord(String userToken, String houseId, String workerTypeSafeOrderId,
                                                String remark, String images, String productId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        if (productId == null || StringUtils.isBlank(productId)) {
            return ServerResponse.createByErrorMessage("请选择对应的维保商品");
        }
        //1.判断当前房子下是否有正在处理中的质保
        List<DjMaintenanceRecord> maintenanceRecordList = djMaintenanceRecordMapper.selectMaintenanceRecoredByHouseId(houseId, workerTypeSafeOrderId);
        if (maintenanceRecordList != null && maintenanceRecordList.size() > 0) {
            return ServerResponse.createByErrorMessage("已有质保流程在处理中！");
        }
        //查询保险订单对应的工种
        WorkerTypeSafeOrder workerTypeSafeOrder = workerTypeSafeOrderMapper.selectByPrimaryKey(workerTypeSafeOrderId);
        Member member = (Member) object;//业主信息
        //判断维保商品是否存在,是否为正确的维保商品
        StorefrontProduct storefrontProduct = iMasterStorefrontProductMapper.selectByPrimaryKey(productId);
        if (storefrontProduct == null || StringUtils.isBlank(storefrontProduct.getId())) {
            return ServerResponse.createByErrorMessage("请选择正确的维保商品！");
        }
        //4.判断当前商品质保卡是否在质保期内
        Integer serviveState = 1;//已过保
        if (workerTypeSafeOrder.getForceTime() != null && workerTypeSafeOrder.getExpirationDate() != null && DateUtil.compareDate(workerTypeSafeOrder.getExpirationDate(), new Date())) {
            serviveState = 0;//未过保
        }
        //2.添加质保信息
        DjMaintenanceRecord djMaintenanceRecord=new DjMaintenanceRecord();
        djMaintenanceRecord.setHouseId(houseId);
        djMaintenanceRecord.setMemberId(member.getId());
        djMaintenanceRecord.setOwnerName(member.getName());
        djMaintenanceRecord.setOwnerMobile(member.getMobile());
        djMaintenanceRecord.setWorkerTypeSafeOrderId(workerTypeSafeOrderId);
        djMaintenanceRecord.setWorkerTypeId(workerTypeSafeOrder.getWorkerTypeId());
        djMaintenanceRecord.setState(serviveState                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         );
        djMaintenanceRecordMapper.insert(djMaintenanceRecord);
        //3.添加质保对应的图片、备注信息
        DjMaintenanceRecordContent djMaintenanceRecordContent=new DjMaintenanceRecordContent();
        djMaintenanceRecordContent.setMaintenanceRecordId(djMaintenanceRecord.getId());
        djMaintenanceRecordContent.setRemark(remark);
        djMaintenanceRecordContent.setImage(images);
        djMaintenanceRecordContent.setMemberId(member.getId());
        djMaintenanceRecordContent.setType(3);
        djMaintenanceRecordContentMapper.insert(djMaintenanceRecordContent);

        //5.添加维保商品
        insertMaintenanceProductInfo(productId,djMaintenanceRecord,serviveState);
        return getMaintenaceProductList(djMaintenanceRecord.getId(),1);//业主所选维保商品
    }

    /**
     * 添加维保商品信息
     *
     * @param productId
     * @param djMaintenanceRecord
     */
    public void insertMaintenanceProductInfo(String productId, DjMaintenanceRecord djMaintenanceRecord, Integer serviceState) {
        StorefrontProduct storefrontProduct = iMasterStorefrontProductMapper.selectByPrimaryKey(productId);
        DjMaintenanceRecordProduct mrp = new DjMaintenanceRecordProduct();
        mrp.setProductId(productId);
        mrp.setMaintenanceRecordId(djMaintenanceRecord.getId());
        mrp.setHouseId(djMaintenanceRecord.getHouseId());
        mrp.setMaintenanceMemberId(djMaintenanceRecord.getMemberId());
        mrp.setMaintenanceProductType(1);//业主所选维保商品
        mrp.setPrice(storefrontProduct.getSellPrice());
        mrp.setShopCount(1d);
        mrp.setTotalPrice(storefrontProduct.getSellPrice());
        mrp.setOverProtection(serviceState);
        mrp.setStorefrontId(storefrontProduct.getStorefrontId());
       if (serviceState == 1) {
            mrp.setPayPrice(mrp.getTotalPrice());//已过保需支付金额为当前金额
        }else{
            mrp.setPayPrice(0d);//未过保需支付金额为0
        }
        mrp.setPayState(1);
        djMaintenanceRecordProductMapper.insert(mrp);//添加维保商品
    }

    /**
     * 判断是否需在大管家勘查商品
     *
     * @param mrProductList
     * @param serviveState
     * @param maintenanceRecordId
     * @return
     */
    public Integer updateMaitenanceProductInfo(List<DjMaintenanceRecordProduct> mrProductList,Integer serviveState,String maintenanceRecordId){
        Integer stewardExploration=0;//是否需要管家勘查,默认为否
        //查询业主添加的维保商品
        if(mrProductList!=null&&mrProductList.size()>0){
            for(DjMaintenanceRecordProduct mrp:mrProductList){
               if(stewardExploration==0){
                   //判断当前商品是否需要管家勘查
                   StorefrontProduct storefrontProduct=iMasterStorefrontProductMapper.selectByPrimaryKey(mrp.getProductId());
                   DjBasicsProductTemplate djBasicsProductTemplate=iMasterProductTemplateMapper.selectByPrimaryKey(storefrontProduct.getProdTemplateId());
                   stewardExploration=djBasicsProductTemplate.getStewardExploration();
               }
               //修改当前商品的过期状态
                mrp.setMaintenanceRecordId(maintenanceRecordId);
                mrp.setOverProtection(serviveState);
                if(serviveState==1){
                    mrp.setPayPrice(mrp.getTotalPrice());//已过保需支付金额为当前金额
                }else{
                    mrp.setPayPrice(0d);//未过保需支付金额为0
                }
                mrp.setModifyDate(new Date());
                djMaintenanceRecordProductMapper.updateByPrimaryKeySelective(mrp);//修改对应的商品过保状态
            }

        }

        return stewardExploration;
    }

    /**
     * 查询符合条件的商品
     *
     * @param maintenanceRecordId
     * @param maintenanceRecordType
     * @return
     */
    public ServerResponse getMaintenaceProductList(String maintenanceRecordId,Integer maintenanceRecordType){
        Map<String,Object> paramMap=new HashMap<>();
        if(maintenanceRecordType==2){
            paramMap.put("titleName","您在维保时是否出现了问题呢？如若不清楚问题所属哪项维保服务项目可先进行勘查。");
        }
        //2.查询对应工总的质保商品总 额
        Double totalPrice=0d;
        Double payPrice=0d;
        List<Map<String,Object>> workerTypeList=djMaintenanceRecordProductMapper.selectWorkerTypeListById(maintenanceRecordId,maintenanceRecordType);
        if(workerTypeList!=null&&workerTypeList.size()>0){
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            for(Map map:workerTypeList){
               // String workerTypeId=(String)map.get("workerTypeId");
                String workerTypeImage=(String)map.get("workerTypeImage");
                Integer overProtection=(Integer)map.get("overProtection");
                map.put("workerTypeImageUrl",address+workerTypeImage);
                map.put("workerTypeName",map.get("workerTypeName")+"维保卡");
                totalPrice= MathUtil.add(totalPrice,(Double)map.get("totalPrice"));
                payPrice= MathUtil.add(payPrice,(Double)map.get("payPrice"));
                //查对应的商品信息
                Example example=new Example(DjMaintenanceRecordProduct.class);
                example.createCriteria().andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_RECORD_ID,maintenanceRecordId)
                                   .andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_PRODUCT_TYPE,maintenanceRecordType);
                List<DjMaintenanceRecordProduct> mrProductList=djMaintenanceRecordProductMapper.selectByExample(example);
                List productList=workerTypeSafeOrderService.getRecordProductList(mrProductList);
                if(overProtection==1){
                    map.put("overProtectionName","已过保");
                }else{
                    map.put("overProtectionName","质保期内");
                }
                map.put("productList",productList);
            }
        }
        //加上工种列表
        paramMap.put("workerTypeList",workerTypeList);
        //设置商品总额，及需支付总额
        paramMap.put("totalPrice",totalPrice);
        paramMap.put("payPrice",payPrice);
        paramMap.put("maintenanceRecordType",maintenanceRecordType);//维保商品类型
        return  ServerResponse.createBySuccess("查询成功",paramMap);
    }



    /**
     * 消息弹窗，维保材料商品订单
     *
     * @param userToken 用户token
     * @param houseId 房子IDss
     * @param taskId 任务ID
     * @return
     */
    public ServerResponse searchMaintenanceProduct(String userToken,String houseId,String taskId){
        TaskStack taskStack=taskStackService.selectTaskStackById(taskId);
        if(taskStack!=null&&taskStack.getState()==0){
            Map<String,Object> resultMap=new HashMap<>();
            //1.查询最新需要处理的商品信息
            DjMaintenanceRecord djMaintenanceRecord=djMaintenanceRecordMapper.selectByPrimaryKey(taskStack.getData());
            List list=new ArrayList();
            Map<String,Object> param=new HashMap<>();
            BigDecimal paymentPrice = new BigDecimal(0);//总共钱
            BigDecimal totalPrice = new BigDecimal(0);//总共钱
            BigDecimal freightPrice = new BigDecimal(0);//总运费
            BigDecimal totalMoveDost = new BigDecimal(0);//搬运费
            if(djMaintenanceRecord!=null&&StringUtils.isNotBlank(djMaintenanceRecord.getId())){
                String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                List<DjMaintenanceRecordProduct> storeProductList=djMaintenanceRecordProductMapper.selectStorefrontIdByTypeId(djMaintenanceRecord.getId(),3,1);//查询维保商品，按店铺划分
                if(storeProductList!=null&&storeProductList.size()>0){
                    for(DjMaintenanceRecordProduct mrp:storeProductList){
                        param=new HashMap<>();
                        Double freight=storefrontConfigAPI.getFreightPrice(mrp.getStorefrontId(),mrp.getTotalPrice());
                        freightPrice=freightPrice.add(new BigDecimal(freight));
                        //查询符合条件的商品的末级分类
                        List<Map<String,Object>> categoryList=djMaintenanceRecordProductMapper.selectCategoryByRecordId(djMaintenanceRecord.getId(),3,mrp.getStorefrontId());
                        for(Map<String,Object> pMap:categoryList){
                            String categoryId=(String)pMap.get("categoryId");
                            List<ActuarialProductAppDTO> productlist = djMaintenanceRecordProductMapper.selectMaintenaceProductByCategoryId(djMaintenanceRecord.getId(),3,mrp.getStorefrontId(),categoryId);
                            houseService.getProductList(productlist, address);
                            //计算搬运费
                            for(ActuarialProductAppDTO product:productlist){
                                if(StringUtils.isNotBlank(product.getProductId())){
                                    //搬运费运算
                                    Double moveDost=masterCostAcquisitionService.getStevedorageCost(djMaintenanceRecord.getHouseId(),product.getProductId(),product.getShopCount());
                                    totalMoveDost=totalMoveDost.add(new BigDecimal(moveDost));
                                }
                            }

                        }
                        param.put("storefrontId",mrp.getStorefrontId());//店铺ID
                        Storefront storefront=iMasterStorefrontMapper.selectByPrimaryKey(mrp.getStorefrontId());
                        param.put("storefrontName",storefront.getStorefrontName());//店铺名称
                        param.put("totalPrice",mrp.getTotalPrice());
                        param.put("categoryList",categoryList);
                        totalPrice=totalPrice.add(BigDecimal.valueOf(mrp.getTotalPrice()));//汇总总价
                    }
                }
                resultMap.put("totalPrice",totalPrice);//订单总额
                if(djMaintenanceRecord.getOverProtection()==1){
                    resultMap.put("payPrice",totalPrice.add(totalMoveDost).add(freightPrice));//支付总额
                }else{
                    resultMap.put("payPrice",0);//支付总额
                }
                resultMap.put("stevedorageCost",totalMoveDost);//搬运费
                resultMap.put("transportationCost",freightPrice);//运费
                //返回符合条件的数据给前端
                return ServerResponse.createBySuccess("查询成功","");
            }
        }
        return ServerResponse.createBySuccess("未找到需处理的任务");
    }



    /**
     * 消息弹窗--报销商品订单
     * @param userToken
     * @param houseId
     * @param taskId
     * @return
     */
    public ServerResponse searchExpenseMaintenanceProduct(String userToken,String houseId,String taskId){
        try{
            TaskStack taskStack=taskStackService.selectTaskStackById(taskId);
            if(taskStack!=null&&taskStack.getState()==0){
                Example example=new Example(Complain.class);
                example.createCriteria().andEqualTo(Complain.HOUSE_ID,houseId)
                        .andEqualTo(Complain.BUSINESS_ID,taskStack.getData())
                        .andEqualTo(Complain.COMPLAIN_TYPE,9)
                        .andEqualTo(Complain.STATUS,2);
                Complain complain=iComplainMapper.selectOneByExample(example);
                return ServerResponse.createBySuccess("查询成功",complain);
            }
           return ServerResponse.createBySuccess("未找到需处理的任务");
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 消息弹窗--验收申请单
     *
     * @param userToken
     * @param houseId
     * @param taskId
     * @return
     */
    public ServerResponse searchAcceptanceApplication(String userToken,String houseId,String taskId){
        try{
            TaskStack taskStack=taskStackService.selectTaskStackById(taskId);
            if(taskStack!=null&&taskStack.getState()==0){
                Map<String,Object> map=new HashMap();
                DjMaintenanceRecord djMaintenanceRecord=djMaintenanceRecordMapper.selectByPrimaryKey(taskStack.getData());
                if(djMaintenanceRecord!=null&& !("2".equals(djMaintenanceRecord.getState())||!"4".equals(djMaintenanceRecord.getState()))&&StringUtils.isNotBlank(djMaintenanceRecord.getWorkerMemberId())){
                    Member member=iMemberMapper.selectByPrimaryKey(djMaintenanceRecord.getWorkerMemberId());
                    WorkerType workerType=workerTypeMapper.selectByPrimaryKey(djMaintenanceRecord.getWorkerTypeId());
                    map.put("workerId",member.getId());
                    map.put("workerName",member.getName());
                    map.put("labelName",workerType.getName());
                    map.put("headImage",member.getHead());
                    //查询申请内容，申请时间
                    Example example=new Example(DjMaintenanceRecordContent.class);
                    example.createCriteria().andEqualTo(DjMaintenanceRecordContent.MAINTENANCE_RECORD_ID,djMaintenanceRecord.getId())
                            .andEqualTo(DjMaintenanceRecordContent.TYPE,1);//查询工匠的信息
                    DjMaintenanceRecordContent djMaintenanceRecordContent=djMaintenanceRecordContentMapper.selectOneByExample(example);
                    if(djMaintenanceRecordContent!=null){
                        map.putAll(BeanUtils.beanToMap(djMaintenanceRecordContent));
                        map.put("reparirRemainingTime",getRemainingTime(djMaintenanceRecordContent.getCreateDate()));//剩余处理时间戳
                    }
                    return ServerResponse.createBySuccess("查询成功",map);
                }else{
                    return ServerResponse.createByErrorMessage("此维保已处理完成，请勿重复处理。");
                }

            }
            return ServerResponse.createBySuccess("未找到需处理的任务");
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 获取当前阶段剩余可处理时间
     *
     * @return
     */
    private long getRemainingTime(Date createDate){
        try{
            String parayKey="";
            if(parayKey!=null&&StringUtils.isNotBlank(parayKey)){
                Config config=iConfigMapper.selectConfigInfoByParamKey(parayKey);//获取对应阶段需处理剩余时间
                if(config!=null&&StringUtils.isNotBlank(config.getId())){
                    String hour=config.getParamValue();
                    Date newDate=DateUtil.addDateHours(createDate,Integer.parseInt(hour));
                    return DateUtil.daysBetweenTime(new Date(),newDate);
                }
            }
        }catch (Exception e){
            logger.error("获取剩余时间异常:",e);
        }

        return 0;
    }

    /**
     * 消息弹窗--提交维保商品
     * @param userToken
     * @param houseId
     * @param taskId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveMaintenanceProduct(String userToken,String houseId,String taskId,String cityId){
        TaskStack taskStack = taskStackService.selectTaskStackById(taskId);
        if (taskStack != null && taskStack.getState() == 0) {
            DjMaintenanceRecord djMaintenanceRecord=djMaintenanceRecordMapper.selectByPrimaryKey(taskStack.getData());
            if(djMaintenanceRecord!=null&&djMaintenanceRecord.getOverProtection()==1){//维保期外的订单才需要支付
             return   paymentService.generateMaintenanceRecordOrder(userToken,djMaintenanceRecord.getId(),3,cityId,null);
            }
            taskStack.setState(1);
            taskStack.setModifyDate(new Date());
            taskStackService.updateTaskStackInfo(taskStack);
        }
        return ServerResponse.createBySuccess("未找到需处理的任务");
    }

    /**
     * 消息弹窗--提交报销商品
     * @param userToken
     * @param houseId
     * @param taskId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveExpenseMaintenanceProduct(String userToken,String houseId,String taskId,String cityId){
        TaskStack taskStack = taskStackService.selectTaskStackById(taskId);
        if (taskStack != null && taskStack.getState() == 0) {
            DjMaintenanceRecordProduct djMaintenanceRecordProduct=djMaintenanceRecordProductMapper.selectByPrimaryKey(taskStack.getData());
            DjMaintenanceRecord djMaintenanceRecord=djMaintenanceRecordMapper.selectByPrimaryKey(djMaintenanceRecordProduct.getMaintenanceRecordId());
            if(djMaintenanceRecord!=null&&djMaintenanceRecord.getOverProtection()==1){//维保期外的报销单需走业主支付
                return   paymentService.generateMaintenanceRecordOrder(userToken,djMaintenanceRecord.getId(),2,cityId,djMaintenanceRecordProduct.getId());
            }
            taskStack.setState(1);
            taskStack.setModifyDate(new Date());
            taskStackService.updateTaskStackInfo(taskStack);
        }
        return ServerResponse.createBySuccess("未找到需处理的任务");
    }
    /**
     * 消息弹窗--提交验收申请结果
     * @param userToken 用户token
     * @param houseId 房子ID
     * @param taskId 任务ID
     * @param auditResult 审核结果1通过，2不通过
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveAcceptanceApplication(String userToken,String houseId,String taskId,Integer auditResult){
        if(auditResult==null||(auditResult!=1||auditResult!=2)){
            return ServerResponse.createByErrorMessage("请选择你的审批意见！");
        }
        Map<String,Object> map=new HashMap<>();
        TaskStack taskStack = taskStackService.selectTaskStackById(taskId);
        if (taskStack != null && taskStack.getState() == 0) {
            DjMaintenanceRecord djMaintenanceRecord=djMaintenanceRecordMapper.selectByPrimaryKey(taskStack.getData());
            Member member=iMemberMapper.selectByPrimaryKey(djMaintenanceRecord.getMemberId());
            if(djMaintenanceRecord!=null&&auditResult==2){
              //验收不通过，改状态需要工匠重新申请
              djMaintenanceRecord.setState(3);
              djMaintenanceRecord.setModifyDate(new Date());
              djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);
              //发送消息给工匠，需要重新处理
                configMessageService.addConfigMessage( AppType.GONGJIANG, djMaintenanceRecord.getWorkerMemberId(),
                        "0", "业主拒绝通过", String.format(DjConstants.CommonMessage.YEZHU_REFUSE,member.getName()),2, "业主拒绝通过");

           }else if(djMaintenanceRecord!=null&&auditResult==1){
                //验收通过，修改状态为验收通过
                djMaintenanceRecord.setState(4);
                djMaintenanceRecord.setModifyDate(new Date());
                djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);
                //质保期内的维保，分担责任给到对应的工匠和店铺
                if(djMaintenanceRecord.getOverProtection()==0){
                    insertRecordResponsibleParty(djMaintenanceRecord,houseId);
                }
                configMessageService.addConfigMessage( AppType.GONGJIANG, djMaintenanceRecord.getWorkerMemberId(),
                        "0", "业主审核通过", String.format(DjConstants.CommonMessage.YEZHU_ACCEPT,member.getName()),2, "业主审核通过");

                Member worker=iMemberMapper.selectByPrimaryKey(djMaintenanceRecord.getWorkerMemberId());
                WorkerType workerType=workerTypeMapper.selectByPrimaryKey(djMaintenanceRecord.getWorkerTypeId());
                map.put("workerId",worker.getId());
                map.put("workerName",worker.getName());
                map.put("labelName",workerType.getName());
                map.put("headImage",worker.getHead());
            }
            taskStack.setState(1);
            taskStack.setModifyDate(new Date());
            taskStackService.updateTaskStackInfo(taskStack);
            return ServerResponse.createBySuccess("提交成功",map);
        }
        return ServerResponse.createBySuccess("未找到需处理的任务");
    }

    /**
     * 添加分摊比例及价钱
     * @param djMaintenanceRecord
     * @param houseId
     */
    public void insertRecordResponsibleParty(DjMaintenanceRecord djMaintenanceRecord,String houseId){
        //查对应的商品信息
        Example example=new Example(DjMaintenanceRecordProduct.class);
        example.createCriteria().andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_RECORD_ID,djMaintenanceRecord.getId())
                .andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_PRODUCT_TYPE,1);
        List<DjMaintenanceRecordProduct> mrProductList=djMaintenanceRecordProductMapper.selectByExample(example);
        if(mrProductList!=null&&mrProductList.size()>0){
            DjMaintenanceRecordProduct djMaintenanceRecordProduct=mrProductList.get(0);
            StorefrontProduct storefrontProduct=iMasterStorefrontProductMapper.selectByPrimaryKey(djMaintenanceRecordProduct.getProductId());
            //1.查询维保付费商品的总价（除大管家勘查费用以外的商品费用）
            Double totalPayPrice=djMaintenanceRecordProductMapper.selectTotalPayPriceByRecordId(djMaintenanceRecord.getId());
            //2.查询出已付费商品的运费，搬运费
            Map<String,Object> priceMap=djMaintenanceRecordProductMapper.selectProductCostByRecordId(djMaintenanceRecordProduct.getId());
            Double stevedorageCost=0d;
            Double transportationCost=0d;
            if(priceMap!=null){
                stevedorageCost=(Double)priceMap.get("stevedorageCost");
                transportationCost=(Double)priceMap.get("transportationCost");
            }
            //3.需分摊商品的总价
            Double totalPrice=MathUtil.add(MathUtil.add(totalPayPrice,stevedorageCost),transportationCost);
            //查询责任占比列表
            example=new Example(BasicsProductTemplateRatio.class);
            example.createCriteria().andEqualTo(BasicsProductTemplateRatio.PRODUCT_TEMPLATE_ID,storefrontProduct.getProdTemplateId());
            List<BasicsProductTemplateRatio> templateRatioList=iMasterProductTemplateRatioMapper.selectByExample(example);
            Integer productResponsibleType=0;
            String responsiblePartyId="0";
            Double retio=0d;
            Double maintenanceTotalPrice=0d;
            if(templateRatioList!=null&&templateRatioList.size()>0){
                for(BasicsProductTemplateRatio templateRatio:templateRatioList){
                    //2.获取需分摊的价钱，保留两位小数
                    retio=MathUtil.div(templateRatio.getProductRatio(),100);
                    //1.查询对应的责任方，及责任方占比
                    productResponsibleType=templateRatio.getProductResponsibleType();
                    maintenanceTotalPrice=MathUtil.mul(totalPrice,retio);
                    if(productResponsibleType==1){//找商家店铺
                        responsiblePartyId=djMaintenanceRecordProductMapper.selectStorefrontIdByHouseId(houseId,templateRatio.getProductResponsibleId());
                       // 扣除责任方的钱，扣除店铺的钱
                        maintenanceMinusStorefront(djMaintenanceRecord.getId(),houseId,responsiblePartyId,maintenanceTotalPrice);
                    }else{//找原工匠
                        HouseFlow houseFlow=iHouseFlowMapper.getByWorkerTypeId(houseId,templateRatio.getProductResponsibleId());
                        responsiblePartyId=houseFlow.getWorkerId();//工匠ID
                        Member worker=iMemberMapper.selectByPrimaryKey(responsiblePartyId);
                        //扣除责任方的钱(工匠）
                        maintenanceMinusDetention(worker,djMaintenanceRecord.getHouseId(),BigDecimal.valueOf(maintenanceTotalPrice),djMaintenanceRecord.getId());
                        //推送扣款通知给原工匠
                        //=====================fzh==================
                    }

                    //3.保存对应的数据到占比表中去
                    DjMaintenanceRecordResponsibleParty recordResponsibleParty=new DjMaintenanceRecordResponsibleParty();
                    recordResponsibleParty.setMaintenanceRecordId(djMaintenanceRecord.getId());
                    recordResponsibleParty.setResponsiblePartyId(responsiblePartyId);
                    recordResponsibleParty.setProportion(templateRatio.getProductRatio());
                    recordResponsibleParty.setResponsiblePartyType(productResponsibleType);
                    recordResponsibleParty.setTotalPrice(totalPrice);
                    recordResponsibleParty.setStevedorageCost(stevedorageCost);
                    recordResponsibleParty.setTransportationCost(transportationCost);
                    recordResponsibleParty.setMaintenanceTotalPrice(maintenanceTotalPrice);
                    djMaintenanceRecordResponsiblePartyMapper.insert(recordResponsibleParty);
                }

            }

        }

    }


    /**
     * 保存质保订单表
     *
     * @param userToken
     * @param houseId
     * @param maintenanceRecordId 质保ID
     * @param maintenanceRecordType 质保订单类型（１业主维保商品，２管家勘查商品，３工匠维修要货商品，４工匠报销费用）
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveMaintenanceRecordOrder(String userToken,String houseId,String maintenanceRecordId,Integer maintenanceRecordType,String cityId) {

        return paymentService.generateMaintenanceRecordOrder(userToken, maintenanceRecordId, maintenanceRecordType, cityId,null);
    }


    /**
     * 提前结束，返回维保订单商品
     *
     * @param userToken
     * @param houseId
     * @param maintenanceRecordId
     * @param cityId
     * @return
     */
    public ServerResponse endMaintenanceSearchProduct(String userToken,String houseId,String maintenanceRecordId,String cityId){
        List<Map<String,Object>> workerTypeList=djMaintenanceRecordProductMapper.selectWorkerTypeListById(maintenanceRecordId,2);//查询是否已添加勘查费用的商品
        DjMaintenanceRecord djMaintenanceRecord=djMaintenanceRecordMapper.selectByPrimaryKey(maintenanceRecordId);
        if(workerTypeList==null||workerTypeList.size()<=0){
            //添加维保勘查费用的商品
            Example example=new Example(DjBasicsProductTemplate.class);
            example.createCriteria().andEqualTo(DjBasicsProductTemplate.MAINTENANCE_INVESTIGATION,1);
            DjBasicsProductTemplate djBasicsProductTemplate=iMasterProductTemplateMapper.selectOneByExample(example);
            if(djBasicsProductTemplate!=null&& StringUtils.isNotBlank(djBasicsProductTemplate.getId())){
                StorefrontProductDTO storefrontProductDTO=imasterProductTemplateService.getStorefrontProductByTemplateId(djBasicsProductTemplate.getId());
                DjMaintenanceRecordProduct mrp=new DjMaintenanceRecordProduct();
                mrp.setProductId(storefrontProductDTO.getStorefrontProductId());
                mrp.setMaintenanceRecordId(djMaintenanceRecord.getId());
                mrp.setHouseId(houseId);
                mrp.setMaintenanceMemberId(djMaintenanceRecord.getMemberId());
                mrp.setMaintenanceProductType(2);
                mrp.setPrice(storefrontProductDTO.getSellPrice());
                mrp.setShopCount(1d);
                mrp.setTotalPrice(storefrontProductDTO.getSellPrice());
                mrp.setOverProtection(djMaintenanceRecord.getOverProtection());
                mrp.setStorefrontId(storefrontProductDTO.getStorefrontId());
                if(djMaintenanceRecord.getOverProtection()==1){
                    mrp.setPayPrice(mrp.getTotalPrice());//已过保需支付金额为当前金额
                }else{
                    mrp.setPayPrice(0d);//未过保需支付金额为0
                }
                mrp.setPayState(1);
                djMaintenanceRecordProductMapper.insert(mrp);//添加维保勘查费用商品
            }else{
                //结束维保
                return endMaintenanceRecord(userToken,houseId,maintenanceRecordId,cityId);
            }
        }
        return getMaintenaceProductList(djMaintenanceRecord.getId(),2);//业主所选维保商品

    }

    /**
     * 提前结束，结束维保
     *
     * @param userToken
     * @param houseId
     * @param maintenanceRecordId
     * @param cityId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse endMaintenanceRecord(String userToken,String houseId,String maintenanceRecordId,String cityId){
        //1.结束当前维保
        DjMaintenanceRecord djMaintenanceRecord=djMaintenanceRecordMapper.selectByPrimaryKey(maintenanceRecordId);
        if(djMaintenanceRecord!=null&&!("2".equals(djMaintenanceRecord.getState())||"4".equals(djMaintenanceRecord.getState()))){
            //修改质保单为结束
            djMaintenanceRecord.setState(4);
            djMaintenanceRecord.setModifyDate(new Date());
            djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);
            //2.判断是否为质保期外的，且有已支付的订单
            if(djMaintenanceRecord.getOverProtection()==1){
                Double payPriceOne=djMaintenanceRecordProductMapper.queryMaintenanceRecordMoney(maintenanceRecordId,1);
                Double payPriceTwo=djMaintenanceRecordProductMapper.queryMaintenanceRecordMoney(maintenanceRecordId,2);
                if(payPriceOne==null){
                    payPriceOne=0d;
                }
                if(payPriceTwo==null){
                    payPriceTwo=0d;
                }
                Double totalPrice=MathUtil.add(payPriceOne,payPriceTwo);
                if(totalPrice>0){
                    //3.退已支付钱给业主
                    /*退钱给业主*/
                    Member member = iMemberMapper.selectByPrimaryKey(djMaintenanceRecord.getMemberId());
                    BigDecimal haveMoney = member.getHaveMoney().add(new BigDecimal(totalPrice));
                    BigDecimal surplusMoney = member.getSurplusMoney().add(new BigDecimal(totalPrice));
                    //记录流水
                    WorkerDetail workerDetail = new WorkerDetail();
                    workerDetail.setName("系统自动退款,提前结束维保单");
                    workerDetail.setWorkerId(member.getId());
                    workerDetail.setWorkerName(CommonUtil.isEmpty(member.getName()) ? member.getNickName() : member.getName());
                    workerDetail.setHouseId(djMaintenanceRecord.getHouseId());
                    workerDetail.setMoney(new BigDecimal(totalPrice));
                    workerDetail.setApplyMoney(new BigDecimal(totalPrice));
                    workerDetail.setWalletMoney(surplusMoney);
                    workerDetail.setState(2);//进钱//业主退
                    iWorkerDetailMapper.insert(workerDetail);

                    member.setHaveMoney(haveMoney);
                    member.setSurplusMoney(surplusMoney);
                    iMemberMapper.updateByPrimaryKeySelective(member);
                    //4.修改当前商品为已退款
                    djMaintenanceRecordProductMapper.updateRecordProductInfoByRecordId(maintenanceRecordId);
                    //5.通知业主已退款
                    //推送消息给业主退货退款通知
                    configMessageService.addConfigMessage( AppType.ZHUANGXIU, member.getId(),
                            "0", "有退款到账啦", String.format(DjConstants.PushMessage.YEZHUENDMAINTENANCE),7, "提前结束维保订单");

                }
            }

        }else{
            return ServerResponse.createByErrorMessage("当前订单已结束，请勿重复提交！");
        }
        return ServerResponse.createBySuccessMessage("提交成功");
    }
        /**
         * 查询质保审核列表
         *
         * @param pageDTO
         * @param searchKey
         * @return
         */

    public ServerResponse queryDjMaintenanceRecordList(PageDTO pageDTO, String searchKey, Integer state) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjMaintenanceRecordDTO> djMaintenanceRecordDTOS =
                    djMaintenanceRecordMapper.queryDjMaintenanceRecordList(searchKey, state);
            if (djMaintenanceRecordDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            PageInfo pageInfo = new PageInfo(djMaintenanceRecordDTOS);
            return ServerResponse.createBySuccess("查询成功", pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    private List<String> getImage(String image) {
        List<String> strList = new ArrayList<>();
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        List<String> result = Arrays.asList(image.split(","));
        for (int i = 0; i < result.size(); i++) {
            String str = imageAddress + result.get(i);
            strList.add(str);
        }
        return strList;
    }


    /**
     * 查询质保审核详情
     *
     * @param id
     * @return
     */
    public ServerResponse queryDjMaintenanceRecordDetail(String id) {
        try {
            DjMaintenanceRecordDTO djMaintenanceRecordDTOS =
                    djMaintenanceRecordMapper.queryDjMaintenanceRecordDetail(id);

            //维保商品列表
            List<DjMaintenanceRecordProductDTO> djMaintenanceRecordProductDTOS =
                    djMaintenanceRecordProductMapper.queryDjMaintenanceRecordProductList(id);
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            djMaintenanceRecordProductDTOS.forEach(djMaintenanceRecordProductDTO -> {
                djMaintenanceRecordProductDTO.setImage(imageAddress + djMaintenanceRecordProductDTO.getImage());
            });
            djMaintenanceRecordDTOS.setDjMaintenanceRecordProductDTOS(djMaintenanceRecordProductDTOS);

            ///维保责任方
            Example example = new Example(DjMaintenanceRecordResponsibleParty.class);
            example.createCriteria().andEqualTo(DjMaintenanceRecordResponsibleParty.MAINTENANCE_RECORD_ID, id)
                    .andEqualTo(DjMaintenanceRecordResponsibleParty.DATA_STATUS, 0);
            List<DjMaintenanceRecordResponsibleParty> djMaintenanceRecordResponsibleParties =
                    djMaintenanceRecordResponsiblePartyMapper.selectByExample(example);
            List<DjMaintenanceRecordResponsiblePartyDTO> djMaintenanceRecordResponsiblePartyDTOS = new ArrayList<>();
            djMaintenanceRecordResponsibleParties.forEach(djMaintenanceRecordResponsibleParty -> {
                DjMaintenanceRecordResponsiblePartyDTO djMaintenanceRecordResponsiblePartyDTO =
                        djMaintenanceRecordResponsiblePartyMapper.queryDjMaintenanceRecordResponsibleParty(
                                djMaintenanceRecordResponsibleParty.getId(),
                                djMaintenanceRecordResponsibleParty.getResponsiblePartyType());
                djMaintenanceRecordResponsiblePartyDTO.setImage(imageAddress + djMaintenanceRecordResponsiblePartyDTO.getImage());
                djMaintenanceRecordResponsiblePartyDTOS.add(djMaintenanceRecordResponsiblePartyDTO);
            });
            djMaintenanceRecordDTOS.setDjMaintenanceRecordResponsiblePartyDTOS(djMaintenanceRecordResponsiblePartyDTOS);

            if (!CommonUtil.isEmpty(djMaintenanceRecordDTOS.getWorkerImage())) {
                //工匠上传图片
                djMaintenanceRecordDTOS.setWorkerImages(this.getImage(djMaintenanceRecordDTOS.getWorkerImage()));
            }

            if (!CommonUtil.isEmpty(djMaintenanceRecordDTOS.getStewardImage())) {
                //业主上传图片
                djMaintenanceRecordDTOS.setOwnerImages(this.getImage(djMaintenanceRecordDTOS.getOwnerImage()));
            }

            if (!CommonUtil.isEmpty(djMaintenanceRecordDTOS.getStewardImage())) {
                //管家上传图片
                djMaintenanceRecordDTOS.setStewardImages(this.getImage(djMaintenanceRecordDTOS.getStewardImage()));
            }

            return ServerResponse.createBySuccess("查询成功",djMaintenanceRecordDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 处理质保审核
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setDjMaintenanceRecord(String id, Integer state, String userId) {
        try {
            DjMaintenanceRecord djMaintenanceRecord;
            if (state == 2) {//通过
                Example example = new Example(DjMaintenanceRecordResponsibleParty.class);
                DjMaintenanceRecord djMaintenanceRecord1 = djMaintenanceRecordMapper.selectByPrimaryKey(id);
                example.createCriteria().andEqualTo(DjMaintenanceRecordResponsibleParty.MAINTENANCE_RECORD_ID, id)
                        .andEqualTo(DjMaintenanceRecordResponsibleParty.DATA_STATUS, 0);
                List<DjMaintenanceRecordResponsibleParty> djMaintenanceRecordResponsibleParties =
                        djMaintenanceRecordResponsiblePartyMapper.selectByExample(example);
                djMaintenanceRecordResponsibleParties.forEach(djMaintenanceRecordResponsibleParty -> {
                    //扣除金额
                    Double amountDeducted =0d;// (djMaintenanceRecordResponsibleParty.getProportion() / 100) * (djMaintenanceRecord1.getSincePurchaseAmount() + djMaintenanceRecord1.getEnoughAmount());
                    if (djMaintenanceRecordResponsibleParty.getResponsiblePartyType() == 1) {
                        AccountFlowRecord accountFlowRecord = new AccountFlowRecord();
                        accountFlowRecord.setState(3);
                        accountFlowRecord.setDefinedAccountId(djMaintenanceRecordResponsibleParty.getResponsiblePartyId());
                        accountFlowRecord.setCreateBy(userId);
                        accountFlowRecord.setHouseOrderId(djMaintenanceRecordResponsibleParty.getId());
                        Storefront storefront =
                                iMasterStorefrontMapper.selectByPrimaryKey(djMaintenanceRecordResponsibleParty.getResponsiblePartyId());
                        accountFlowRecord.setAmountBeforeMoney(storefront.getRetentionMoney());//入账前金额
                        storefront.setRetentionMoney(storefront.getRetentionMoney() - amountDeducted);
                        //扣除店铺占比金额
                        iMasterStorefrontMapper.updateByPrimaryKeySelective(storefront);
                        accountFlowRecord.setAmountAfterMoney(storefront.getRetentionMoney());//入账后金额
                        accountFlowRecord.setFlowType("1");
                        accountFlowRecord.setMoney(amountDeducted);
                        accountFlowRecord.setDefinedName("店铺维保占比,扣除滞留金：" + amountDeducted);
                        //记录流水
                        iMasterAccountFlowRecordMapper.insert(accountFlowRecord);


                    } else if (djMaintenanceRecordResponsibleParty.getResponsiblePartyType() == 3) {
                        WorkerDetail workerDetail = new WorkerDetail();
                        workerDetail.setName("维保占比,扣除滞留金：" + amountDeducted);
                        workerDetail.setWorkerId(djMaintenanceRecordResponsibleParty.getResponsiblePartyId());
                        Member member =
                                iMemberMapper.selectByPrimaryKey(djMaintenanceRecordResponsibleParty.getResponsiblePartyId());
                        //扣除工匠占比金额
                        member.setRetentionMoney(member.getRetentionMoney().subtract(new BigDecimal(amountDeducted)));
                        iMemberMapper.updateByPrimaryKeySelective(member);
                        workerDetail.setWorkerName(member.getName());
                        workerDetail.setMoney(new BigDecimal(amountDeducted));
                        workerDetail.setState(3);
                        workerDetail.setDefinedWorkerId(djMaintenanceRecordResponsibleParty.getId());
                        //记录流水
                        iWorkerDetailMapper.insert(workerDetail);
                        //消息推送
                        if (member.getRetentionMoney().intValue() < 0) {
                            //工匠缴纳质保金通知
                            pushNews(8, member, djMaintenanceRecordResponsibleParty, djMaintenanceRecord1);
                        }

                        //责任人通知
                        pushNews(9, member, djMaintenanceRecordResponsibleParty, djMaintenanceRecord1);

                        //查询管家是否定损
                        Example example1 = new Example(HouseFlowApply.class);
                        example1.createCriteria().andEqualTo(DjMaintenanceRecordContent.MAINTENANCE_RECORD_ID, djMaintenanceRecord1.getId())
                                .andEqualTo(DjMaintenanceRecordContent.DATA_STATUS, 0)
                                .andEqualTo(DjMaintenanceRecordContent.TYPE, 2);
                        List<DjMaintenanceRecordContent> dmrc = djMaintenanceRecordContentMapper.selectByExample(example1);
                        if (dmrc != null && dmrc.size() > 0) {
                            //管家定损后通知
                            pushNews(10, member, djMaintenanceRecordResponsibleParty, djMaintenanceRecord1);
                        }
                    }
                });
                djMaintenanceRecord = new DjMaintenanceRecord();
                djMaintenanceRecord.setId(id);
                djMaintenanceRecord.setState(2);
                //djMaintenanceRecord.setPaymentDate(new Date());
                djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);
            } else if (state == 3) {//拒绝
                djMaintenanceRecord = new DjMaintenanceRecord();
                djMaintenanceRecord.setId(id);
                djMaintenanceRecord.setState(3);
                //djMaintenanceRecord.setStewardState(1);
                djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);
            }
            return ServerResponse.createBySuccessMessage("处理成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("处理失败");
        }
    }


    private void pushNews(Integer type,
                          Member member,
                          DjMaintenanceRecordResponsibleParty djMaintenanceRecordResponsibleParty,
                          DjMaintenanceRecord djMaintenanceRecord1) {
        TaskStack taskStack = new TaskStack();
        if (type == 8) {
            WorkerType w = workerTypeMapper.selectByPrimaryKey(member.getWorkerType());
            taskStack.setName(w.getName() + "缴纳质保金");
            taskStack.setType(type);
            taskStack.setData(djMaintenanceRecordResponsibleParty.getId());
        } else if (type == 9) {
            taskStack.setName("维保责任划分通知");
            taskStack.setType(type);
            taskStack.setData(djMaintenanceRecordResponsibleParty.getMaintenanceRecordId());
        } else if (type == 10) {
            taskStack.setName("管家定损后通知");
            taskStack.setType(type);
            taskStack.setData(djMaintenanceRecordResponsibleParty.getMaintenanceRecordId());
        }
        taskStack.setMemberId(djMaintenanceRecordResponsibleParty.getResponsiblePartyId());
        taskStack.setHouseId(djMaintenanceRecord1.getHouseId());
        taskStack.setImage("icon/sheji.png");
        taskStack.setState(0);
        iMasterTaskStackMapper.insert(taskStack);


    }

    /**
     * 查询督导列表
     *
     * @param pageDTO
     * @param name
     * @return
     */
    public ServerResponse queryMemberList(PageDTO pageDTO, String name) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Example example = new Example(Member.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(Member.WORKER_TYPE, 12)
                    .andEqualTo(Member.DATA_STATUS, 0);
            if (!CommonUtil.isEmpty(name)) {
                criteria.andLike(Member.NAME, "%" + name + "%");
            }
            List<Member> members = iMemberMapper.selectByExample(example);

            if (members.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            PageInfo pageInfo = new PageInfo(members);
            return ServerResponse.createBySuccess("查询成功", pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 处理申诉
     *
     * @param supervisorId
     * @return
     */
    public ServerResponse upDateMaintenanceInFo(String supervisorId,
                                                Integer stewardSubsidy,
                                                String serviceRemark,
                                                String userId,
                                                String id,
                                                Integer handleType) {
        try {
            DjMaintenanceRecord djMaintenanceRecord = new DjMaintenanceRecord();
            if (handleType != null && handleType == 3) {
                //确定处理
                /*djMaintenanceRecord.setUserId(userId);
                djMaintenanceRecord.setSupervisorId(supervisorId);
                djMaintenanceRecord.setStewardSubsidy(stewardSubsidy);
                djMaintenanceRecord.setServiceRemark(serviceRemark);*/
                djMaintenanceRecord.setId(id);
                djMaintenanceRecord.setState(1);
                djMaintenanceRecord.setCreateDate(null);
               // djMaintenanceRecord.setHandleType(handleType);
                djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);
                return ServerResponse.createBySuccess("提交成功");
            } else if (handleType != null && handleType == 4) {
                //结束流程
               /* djMaintenanceRecord.setUserId(userId);
                djMaintenanceRecord.setServiceRemark(serviceRemark);*/
                djMaintenanceRecord.setId(id);
                djMaintenanceRecord.setState(2);
                djMaintenanceRecord.setCreateDate(null);
                //djMaintenanceRecord.setHandleType(handleType);
                djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);
                return ServerResponse.createBySuccess("提交成功");
            }
            return ServerResponse.createByErrorMessage("提交失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }

    /**
     * 修改消息状态
     *
     * @param id
     */
    public ServerResponse updateTaskStackData(String id) {
        try {
            TaskStack taskStack = new TaskStack();
            taskStack.setCreateDate(null);
            taskStack.setId(id);
            taskStack.setState(1);
            iMasterTaskStackMapper.updateByPrimaryKeySelective(taskStack);
            return ServerResponse.createByErrorMessage("提交失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }

    /**
     * 查询维保责任记录
     *
     * @param memberId
     * @return
     */
    public ServerResponse queryDimensionRecord(String memberId) {
        try {
            List<DimensionRecordDTO> dimensionRecordDTOS = djMaintenanceRecordResponsiblePartyMapper.queryDimensionRecord(memberId);
            return ServerResponse.createBySuccess("查询成功", dimensionRecordDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 查询维保详情
     *
     * @param mrId
     * @return
     */
    public ServerResponse queryDimensionRecordInFo(String mrId) {
        try {

            DimensionRecordDTO dimensionRecordDTOS = djMaintenanceRecordResponsiblePartyMapper.queryDimensionRecordInFo(mrId, 3);

            //查询房子信息
            Example example = new Example(HouseFlowApply.class);
            example.createCriteria().andEqualTo(HouseFlowApply.WORKER_ID, dimensionRecordDTOS.getResponsiblePartyId())
                    .andEqualTo(HouseFlowApply.DATA_STATUS, 0)
                    .andEqualTo(HouseFlowApply.APPLY_TYPE, 2);
            example.orderBy(HouseFlowApply.CREATE_DATE).desc();
            List<HouseFlowApply> houseFlowApplies = houseFlowApplyMapper.selectByExample(example);
            String dateStr = "";
            if (houseFlowApplies != null && houseFlowApplies.size() > 0) {
                dateStr = DateUtil.dateToString(houseFlowApplies.get(0).getCreateDate(), "yyyy-MM-dd");
            }

            dimensionRecordDTOS.setStr("您于" + dateStr + "申请整体完工的工地" + "“" + dimensionRecordDTOS.getHouseName() + "”," +
                    "业主申请了质保,经管家实地查看,平台合适确定,您需要负担" + dimensionRecordDTOS.getProportion() + "%的责任," +
                    "已从您的滞留金中扣除总维保金额的" + dimensionRecordDTOS.getProportion() + "%,请悉知,如有疑问可申诉。");

            //维保商品列表
            List<DjMaintenanceRecordProductDTO> djMaintenanceRecordProductDTOS =
                    djMaintenanceRecordProductMapper.queryDjMaintenanceRecordProductList(dimensionRecordDTOS.getMrId());
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            djMaintenanceRecordProductDTOS.forEach(djMaintenanceRecordProductDTO -> {
                djMaintenanceRecordProductDTO.setValueNameArr(imasterProductTemplateService.getNewValueNameArr(djMaintenanceRecordProductDTO.getValueIdArr()));
                djMaintenanceRecordProductDTO.setImage(imageAddress + djMaintenanceRecordProductDTO.getImage());
            });
            dimensionRecordDTOS.setDjMaintenanceRecordProductDTOS(djMaintenanceRecordProductDTOS);


            //查询申诉状态
            example = new Example(Complain.class);
            example.createCriteria().andEqualTo(Complain.MEMBER_ID, dimensionRecordDTOS.getResponsiblePartyId())
                    .andEqualTo(Complain.DATA_STATUS, 0)
                    .andEqualTo(Complain.HOUSE_ID, dimensionRecordDTOS.getHouseId())
                    .andEqualTo(Complain.COMPLAIN_TYPE, 10);
            example.orderBy(HouseFlowApply.CREATE_DATE).desc();
            List<Complain> complains = iComplainMapper.selectByExample(example);
            //0-申诉 1-申诉中  2-已完成
            if (complains != null && complains.size() > 0) {
                dimensionRecordDTOS.setType(complains.get(0).getStatus() == 0 ? 1 : 2);
            } else {
                dimensionRecordDTOS.setType(0);
            }


            return ServerResponse.createBySuccess("查询成功", dimensionRecordDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 新增工匠申诉
     *
     * @param responsiblePartyId
     * @return
     */
    public ServerResponse insertResponsibleParty(String responsiblePartyId,
                                                 String houseId,
                                                 String description,
                                                 String image) {
        try {
            Complain complain = new Complain();
            complain.setComplainType(10);
            complain.setStatus(0);
            complain.setHouseId(houseId);
            complain.setMemberId(responsiblePartyId);
            complain.setDescription(description);
            complain.setImage(image);
            iComplainMapper.insert(complain);
            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "保存失败");
        }
    }


    /**
     * 查询工匠申诉
     *
     * @param responsiblePartyId
     * @return
     */
    public ServerResponse queryResponsibleParty(String responsiblePartyId, String houseId) {
        try {
            ComplainDataDTO complainInFoDTO = djMaintenanceRecordResponsiblePartyMapper.queryResponsibleParty(responsiblePartyId, houseId);
            if (complainInFoDTO != null) {
                String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                complainInFoDTO.setHead(imageAddress + complainInFoDTO.getHead());
                //1-投诉中 2-已完成
                complainInFoDTO.setType(complainInFoDTO.getStatus() == 0 ? 1 : 2);
                complainInFoDTO.setImages(getImage(complainInFoDTO.getImage()));
                return ServerResponse.createBySuccess("查询待抢单列表", complainInFoDTO);
            } else {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
    }


    /**
     * 查询工匠缴纳质保金
     *
     * @param data
     * @return
     */
    public ServerResponse toQualityMoney(String data) {
        try {
            ToQualityMoneyDTO toQualityMoneyDTO = djMaintenanceRecordResponsiblePartyMapper.toQualityMoney(data);
            if (toQualityMoneyDTO != null) {
                String dateStr = DateUtil.dateToString(toQualityMoneyDTO.getCreateDate(), "yyyy-MM-dd");
                String workerName = workerTypeMapper.selectByPrimaryKey(toQualityMoneyDTO.getWorkerTypeId()).getName();
                toQualityMoneyDTO.setStr("“" + toQualityMoneyDTO.getHouseName() + "”于" + dateStr +
                        "申请了" + workerName + "质保,经大管家实地勘察后,确定你责任占比" + toQualityMoneyDTO.getProportion() +
                        "%,因您质保不足则需支付质保金。");

                toQualityMoneyDTO.setToQualityAmount(toQualityMoneyDTO.getSincePurchaseAmount() + toQualityMoneyDTO.getEnoughAmount());
                toQualityMoneyDTO.setNeedAmount(toQualityMoneyDTO.getPaymentAmount() - toQualityMoneyDTO.getRetentionMoney());
            }

            return ServerResponse.createBySuccess("查询成功", toQualityMoneyDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
    }

    /**
     * 查询工匠抢单详情
     *
     * @param data
     * @return
     */
    public ServerResponse queryRobOrderInFo(String userToken, String workerId, String houseId, String data) {
        try {

            DimensionRecordDTO dimensionRecordDTOS = djMaintenanceRecordResponsiblePartyMapper.queryDimensionRecordInFo(data, 3);

            //维保记录内容
            Example example = new Example(DjMaintenanceRecordContent.class);
            example.createCriteria().andEqualTo(DjMaintenanceRecordContent.MAINTENANCE_RECORD_ID, dimensionRecordDTOS.getMrId())
                    .andEqualTo(DjMaintenanceRecordContent.DATA_STATUS, 0);
            example.orderBy(DjMaintenanceRecordContent.CREATE_DATE).desc();
            List<DjMaintenanceRecordContent> dmrc = djMaintenanceRecordContentMapper.selectByExample(example);
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> map = null;
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            for (DjMaintenanceRecordContent d : dmrc) {
                Member member = iMemberMapper.selectByPrimaryKey(d.getMemberId());
                if (d.getType() == 3) {
                    dimensionRecordDTOS.setCreateDate(d.getCreateDate());
                    dimensionRecordDTOS.setOwnerName(member.getName());
                }
                map = new HashMap<>();

                map.put("name", member.getName());
                map.put("head", imageAddress + member.getHead());
                map.put("createDate", d.getCreateDate());
                map.put("memberType", d.getType());//类型 1:工匠 2:大管家 3：业主
                map.put("remark", d.getRemark());
                map.put("images", getImage(d.getImage()));
                if (d.getType() == 2) {
                    //工匠先进场
                    dimensionRecordDTOS.setPageType(2);
                    //维保商品列表
                    List<DjMaintenanceRecordProductDTO> djMaintenanceRecordProductDTOS =
                            djMaintenanceRecordProductMapper.queryDjMaintenanceRecordProductList(dimensionRecordDTOS.getMrId());
                    djMaintenanceRecordProductDTOS.forEach(djMaintenanceRecordProductDTO -> {
                        djMaintenanceRecordProductDTO.setValueNameArr(imasterProductTemplateService.getNewValueNameArr(djMaintenanceRecordProductDTO.getValueIdArr()));
                        djMaintenanceRecordProductDTO.setImage(imageAddress + djMaintenanceRecordProductDTO.getImage());
                    });
                    map.put("djMaintenanceRecordProductDTOS", djMaintenanceRecordProductDTOS);
                } else {
                    dimensionRecordDTOS.setPageType(1);//工匠先进场
                }
                list.add(map);
            }

            example = new Example(HouseFlowApply.class);
            example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, dimensionRecordDTOS.getHouseId())
                    .andEqualTo(HouseFlow.DATA_STATUS, 0)
                    .andEqualTo(HouseFlow.WORKER_TYPE_ID, dimensionRecordDTOS.getWorkerTypeId());
            example.orderBy(HouseFlow.CREATE_DATE).desc();
            List<HouseFlow> houseFlows = iHouseFlowMapper.selectByExample(example);
            if (houseFlows.get(0).getWorkerId().equals(workerId)) {
                dimensionRecordDTOS.setPrimaryType(1);//原工匠
            } else {
                dimensionRecordDTOS.setPrimaryType(2);//非原工匠
            }


            dimensionRecordDTOS.setList(list);
            return ServerResponse.createBySuccess("查询成功", dimensionRecordDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
    }


    /**
     * 查询工匠质保金是否弹框
     * type 0- 不弹框 1-弹框
     *
     * @param memberId
     * @return
     */
    public Integer queryRetentionMoney(String memberId) {
        Member member = iMemberMapper.selectByPrimaryKey(memberId);
        Integer type = 0; //0- 不弹框 1-弹框
        if (member != null && member.getRetentionMoney().intValue() < 0) {
            type = 1;
            return type;
        }
        return type;
    }
    /**
     * 确认申请验收
     * @param houseId
     * @return
     */
//    public ServerResponse applicationAcceptance(String houseId) {
//        try {
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
//        }
//    }

    /**
     * 缴纳质保金列表
     *
     * @param pageDTO
     * @param userId
     * @param cityId
     * @return
     */
    public ServerResponse queryGuaranteeMoneyList(PageDTO pageDTO, String userId, String cityId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息!");
            }
            List<ResponsiblePartyDTO> list = djMaintenanceRecordResponsiblePartyMapper.queryGuaranteeMoneyList(storefront.getId());
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 缴纳质保金详情
     */
    public ServerResponse queryGuaranteeMoneyDetail(String userId, String cityId, String id) {
        try {
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息!");
            }
            ResponsiblePartyDetailDTO responsiblePartyDetailDTO = djMaintenanceRecordResponsiblePartyMapper.queryGuaranteeMoneyDetail(id);
            if (responsiblePartyDetailDTO != null) {
                responsiblePartyDetailDTO.setRetentionMoney(storefront.getRetentionMoney());//现有滞留金

                responsiblePartyDetailDTO.setPaidRetentionMoney((responsiblePartyDetailDTO.getTotalAmount() *
                        Double.parseDouble(responsiblePartyDetailDTO.getProportion())) / 100 - storefront.getRetentionMoney());//需交滞留金

                String workerTypeId = responsiblePartyDetailDTO.getWorkerTypeId();
                String workerTypeName = null;
                if (workerTypeId != null) {
                    WorkerType workerType = workerTypeMapper.selectByPrimaryKey(workerTypeId);//查询工种
                    workerTypeName = workerType != null ? workerType.getName() : "";
                    responsiblePartyDetailDTO.setWorkerTypeName(workerTypeName);//工种名称
                }
                StringBuffer str = new StringBuffer();
                str.append(responsiblePartyDetailDTO.getAddress() + "于").
                        append(responsiblePartyDetailDTO.getCreateDate() + "申请了" + workerTypeName + "质保，经大管家实地勘察后，确认你责任占比")
                        .append(responsiblePartyDetailDTO.getProportion() + "%，您需要缴纳质保金后才能进行店铺的其他操作，有任何疑问请及时联系当家客服。");
                responsiblePartyDetailDTO.setContent(str.toString());//缴纳详情
                responsiblePartyDetailDTO.setNeedRetentionMoney(responsiblePartyDetailDTO.getTotalAmount() * Double.parseDouble(responsiblePartyDetailDTO.getProportion()) / 100);
                responsiblePartyDetailDTO.setProportion(responsiblePartyDetailDTO.getProportion() + "%");//责任占比
            }
            return ServerResponse.createBySuccess("查询成功", responsiblePartyDetailDTO);//所需质保金
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 已解决
     *
     * @return
     */
    public ServerResponse resolved(String userToken, String remark, String houseId, String image, String id, String workerTypeSafeOrderId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;//业主信息
            String url = configUtil.getValue(SysConfig.PUBLIC_SALE_APP_ADDRESS, String.class);
            House house = houseMapper.selectByPrimaryKey(houseId);

            DjMaintenanceRecordContent record = new DjMaintenanceRecordContent();
            record.setMaintenanceRecordId(id);
            record.setRemark(remark);
            record.setImage(image);
            record.setMemberId(member.getId());
            record.setType(1);

            //            configMessageService.addConfigMessage(AppType.SALE, member.getId(), "开工提醒",
//                    "您申请的维修已经解决【" + house.getHouseName() + "】", 0, url
//                            + Utils.getCustomerDetails(customer.getMemberId(), djAlreadyRobSingle1.get(0).getId(), 1, "4"));


            djMaintenanceRecordContentMapper.insertSelective(record);
            return ServerResponse.createBySuccessMessage("已解决");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * (自购金额确认)发送给业主
     *
     * @return
     */
    public ServerResponse sendingOwners(String userToken, String houseId, String remark, String enoughAmount) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;//业主信息
            StringBuffer sb = new StringBuffer();
            sb.append(enoughAmount).append(",").append(remark);
            taskStackService.insertTaskStackInfo(houseId, member.getId(), "(自购金额确认)发送给业主", null, 10, sb.toString());
            return ServerResponse.createBySuccessMessage("发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("(自购金额确认)发送给业主异常");
        }
    }

    /**
     * 管家审核维修
     *
     * @param userToken
     * @param remark
     * @param houseId
     * @param image
     * @param id
     * @param state
     * @param workerTypeSafeOrderId
     * @return
     */
    public ServerResponse auditMaintenance(String userToken, String remark, String houseId, String image, String id, Integer state, String workerTypeSafeOrderId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;//业主信息
            DjMaintenanceRecord djMaintenanceRecord = djMaintenanceRecordMapper.selectByPrimaryKey(id);
            //djMaintenanceRecord.setRemark(remark);
            djMaintenanceRecord.setHouseId(houseId);
            djMaintenanceRecord.setState(state);
            djMaintenanceRecord.setWorkerTypeSafeOrderId(workerTypeSafeOrderId);
            djMaintenanceRecord.setMemberId(member.getId());
           // djMaintenanceRecord.setStewardState(2);//管家处理状态 1：待处理 2：已处理
            djMaintenanceRecord.setWorkerTypeId(null);
            int i = djMaintenanceRecordMapper.updateByPrimaryKey(djMaintenanceRecord);
            if (i <= 0)
                return ServerResponse.createByErrorMessage("审核失败");
            return ServerResponse.createBySuccessMessage("审核成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("管家审核维修异常");
        }
    }


    /**
     * 提交质保处理
     *
     * @param userToken
     * @param remark
     * @param houseId
     * @param image
     * @param id
     * @param state
     * @param workerTypeSafeOrderId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse submitQualityAssurance(String userToken, String houseId,
                                                 String remark, String image,
                                                 String id, Integer state,
                                                 String productId, Double price, Double shopCount,
                                                 String workerTypeSafeOrderId) {
        try {
            //获取业主信息
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;//业主信息

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            WorkerTypeSafeOrder workerTypeSafeOrder = workerTypeSafeOrderMapper.selectByPrimaryKey(workerTypeSafeOrderId);//查询保险订单
            Date createDate = workerTypeSafeOrder.getCreateDate();//人工保险订单的创建时间
            Date today = new Date(); // new Date()为获取当前系统时间//获取当前时间
            //质保过期
            if (today.getTime() > createDate.getTime()) {
                //生成订单
                return ServerResponse.createByErrorMessage("质保过期");
            }

            //维保商品
            DjMaintenanceRecordProduct djMaintenanceRecordProduct = new DjMaintenanceRecordProduct();
            djMaintenanceRecordProduct.setProductId(productId);
            djMaintenanceRecordProduct.setMaintenanceRecordId(id);
            djMaintenanceRecordProduct.setShopCount(shopCount);
            djMaintenanceRecordProduct.setPrice(price);
            djMaintenanceRecordProductMapper.insertSelective(djMaintenanceRecordProduct);
            // 定则
            DjMaintenanceRecordResponsibleParty djMaintenanceRecordResponsibleParty = new DjMaintenanceRecordResponsibleParty();
            djMaintenanceRecordResponsiblePartyMapper.insertSelective(djMaintenanceRecordResponsibleParty);
            return ServerResponse.createBySuccessMessage("提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("提交质保处理异常");
        }
    }


    /**
     * 添加维保商品到购物篮
     *
     * @param userToken
     * @param houseId
     * @param maintenanceRecordId
     * @param productId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse insertMaintenanceRecordProduct(String userToken, String houseId, String maintenanceRecordId, String productId, Double shopCount) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        Example example = new Example(DjMaintenanceRecordProduct.class);
        example.createCriteria().andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_MEMBER_ID, worker.getId())
                .andEqualTo(DjMaintenanceRecordProduct.DATA_STATUS, 0)
                .andEqualTo(DjMaintenanceRecordProduct.HOUSE_ID, houseId)
                .andEqualTo(DjMaintenanceRecordProduct.PRODUCT_ID, productId)
                .andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_RECORD_ID, maintenanceRecordId);
        DjMaintenanceRecordProduct djMaintenanceRecordProduct = djMaintenanceRecordProductMapper.selectOneByExample(example);
        //商品存在 对商品数量进行操作
        if (djMaintenanceRecordProduct!=null) {
            if(shopCount==0){
                return ServerResponse.createBySuccessMessage("操作成功");
            }
            djMaintenanceRecordProduct.setShopCount(djMaintenanceRecordProduct.getShopCount() + shopCount);
            if(djMaintenanceRecordProduct.getShopCount()==0){
                djMaintenanceRecordProductMapper.delete(djMaintenanceRecordProduct);
                return ServerResponse.createBySuccessMessage("操作成功");
            }
            djMaintenanceRecordProduct.setTotalPrice(djMaintenanceRecordProduct.getShopCount() * djMaintenanceRecordProduct.getPrice());
            djMaintenanceRecordProductMapper.updateByPrimaryKeySelective(djMaintenanceRecordProduct);
            return ServerResponse.createBySuccessMessage("操作成功");
        }
        StorefrontProduct storefrontProduct = iMasterStorefrontProductMapper.selectByPrimaryKey(productId);
        DjMaintenanceRecord djMaintenanceRecord = djMaintenanceRecordMapper.selectByPrimaryKey(maintenanceRecordId);
        DjMaintenanceRecordProduct djMaintenanceRecordProduct1 = new DjMaintenanceRecordProduct();
        djMaintenanceRecordProduct1.setHouseId(houseId);
        djMaintenanceRecordProduct1.setProductId(storefrontProduct.getId());
        djMaintenanceRecordProduct1.setMaintenanceRecordId(maintenanceRecordId);
        djMaintenanceRecordProduct1.setShopCount(shopCount);
        djMaintenanceRecordProduct1.setPrice(storefrontProduct.getSellPrice());
        djMaintenanceRecordProduct1.setMaintenanceMemberId(worker.getId());
        djMaintenanceRecordProduct1.setMaintenanceProductType(3);
        djMaintenanceRecordProduct1.setOverProtection(djMaintenanceRecord.getOverProtection());
        djMaintenanceRecordProduct1.setTotalPrice(djMaintenanceRecordProduct1.getShopCount() * storefrontProduct.getSellPrice());
        djMaintenanceRecordProduct1.setPayPrice(0d);
        djMaintenanceRecordProduct1.setPayState(1);
        djMaintenanceRecordProduct1.setTotalPrice(djMaintenanceRecordProduct1.getTotalPrice());
        djMaintenanceRecordProduct1.setStorefrontId(storefrontProduct.getId());
        djMaintenanceRecordProductMapper.insert(djMaintenanceRecordProduct1);
        return ServerResponse.createBySuccessMessage("操作成功");
    }


    /**
     * 管家/工匠维保购物篮处理
     *
     * @param userToken
     * @param houseId
     * @param maintenanceRecordId
     * @return
     */
    public ServerResponse setMaintenanceRecordProduct(String userToken, String houseId, String maintenanceRecordId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            if (StringUtils.isNotBlank(maintenanceRecordId)) {
                List<DjMaintenanceRecordProduct> djMaintenanceRecordProducts;
                Example example = new Example(DjMaintenanceRecordProduct.class);
                DjMaintenanceRecord djMaintenanceRecord = djMaintenanceRecordMapper.selectByPrimaryKey(maintenanceRecordId);
                Integer memberType;
                if (djMaintenanceRecord.getStewardId().equals(worker.getId())) {//管家维保购物篮
                    memberType = 2;
                    example.createCriteria().andEqualTo(DjMaintenanceRecordProduct.DATA_STATUS, 0)
                            .andEqualTo(DjMaintenanceRecordProduct.HOUSE_ID, houseId)
                            //.andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_MEMBER_TYPE, 2)
                            .andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_RECORD_ID, maintenanceRecordId)
                            .andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_MEMBER_ID, worker.getId());
                    djMaintenanceRecordProducts =
                            djMaintenanceRecordProductMapper.selectByExample(example);
                    if (djMaintenanceRecordProducts.size() <= 0) {//如果大管家购物篮无维保商品，查询业主的
                        example = new Example(DjMaintenanceRecordProduct.class);
                        example.createCriteria().andEqualTo(DjMaintenanceRecordProduct.DATA_STATUS, 0)
                                .andEqualTo(DjMaintenanceRecordProduct.HOUSE_ID, houseId)
                                //.andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_MEMBER_TYPE, 1)
                                .andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_RECORD_ID, maintenanceRecordId);
                        djMaintenanceRecordProducts =
                                djMaintenanceRecordProductMapper.selectByExample(example);
                    }
                } else {//工匠
                    memberType = 3;
                    example.createCriteria().andEqualTo(DjMaintenanceRecordProduct.DATA_STATUS, 0)
                            .andEqualTo(DjMaintenanceRecordProduct.HOUSE_ID, houseId)
                           // .andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_MEMBER_TYPE, 3)
                            .andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_RECORD_ID, maintenanceRecordId)
                            .andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_MEMBER_ID, worker.getId());
                    djMaintenanceRecordProducts =
                            djMaintenanceRecordProductMapper.selectByExample(example);
                    if (djMaintenanceRecordProducts.size() <= 0) {//如果工匠购物篮无维保商品，查询大管家的
                        example = new Example(DjMaintenanceRecordProduct.class);
                        example.createCriteria().andEqualTo(DjMaintenanceRecordProduct.DATA_STATUS, 0)
                                .andEqualTo(DjMaintenanceRecordProduct.HOUSE_ID, houseId)
                                //.andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_MEMBER_TYPE, 2)
                                .andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_RECORD_ID, maintenanceRecordId);
                        djMaintenanceRecordProducts =
                                djMaintenanceRecordProductMapper.selectByExample(example);
                        if (djMaintenanceRecordProducts.size() <= 0) {//大管家购物篮为空,查询业主的
                            example = new Example(DjMaintenanceRecordProduct.class);
                            example.createCriteria().andEqualTo(DjMaintenanceRecordProduct.DATA_STATUS, 0)
                                    .andEqualTo(DjMaintenanceRecordProduct.HOUSE_ID, houseId)
                                   // .andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_MEMBER_TYPE, 1)
                                    .andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_RECORD_ID, maintenanceRecordId);
                            djMaintenanceRecordProducts =
                                    djMaintenanceRecordProductMapper.selectByExample(example);
                        }
                    }
                }
                djMaintenanceRecordProducts.forEach(djMaintenanceRecordProduct -> {
                    djMaintenanceRecordProduct.setId((Math.random() * 50000000) + 50000000 + "" + System.currentTimeMillis());
                   // djMaintenanceRecordProduct.setWorkerTypeId(worker.getWorkerTypeId());
                    djMaintenanceRecordProduct.setMaintenanceMemberId(worker.getId());
                   // djMaintenanceRecordProduct.setMaintenanceMemberType(memberType);
                    djMaintenanceRecordProduct.setPayState(1);
                    djMaintenanceRecordProductMapper.insert(djMaintenanceRecordProduct);
                });
                return ServerResponse.createBySuccessMessage("操作成功");
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("操作失败", e);
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 维保购物篮
     *
     * @param userToken
     * @param houseId
     * @return
     */
    public ServerResponse queryMaintenanceShoppingBasket(String userToken, String houseId, String maintenanceRecordId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            Map<String, Object> map = new HashMap<>();
            map.put("maintenanceMemberId", worker.getId());
            map.put("houseId", houseId);
            map.put("maintenanceRecordId", maintenanceRecordId);
            map.put("maintenanceMemberType", 3);
            List<BasicsGoodsCategory> basicsGoodsCategories =
                    djMaintenanceRecordProductMapper.queryGroupByGoodsCategory(map);
            List<MaintenanceShoppingBasketDTO> maintenanceShoppingBasketDTOS = new ArrayList<>();
            basicsGoodsCategories.forEach(basicsGoodsCategory -> {
                MaintenanceShoppingBasketDTO maintenanceShoppingBasketDTO = new MaintenanceShoppingBasketDTO();
                maintenanceShoppingBasketDTO.setId(basicsGoodsCategory.getId());
                maintenanceShoppingBasketDTO.setName(basicsGoodsCategory.getName());
                map.put("parentTop",basicsGoodsCategory.getId());
                maintenanceShoppingBasketDTO.setDjMaintenanceRecordProductDTOS(djMaintenanceRecordProductMapper.queryMaintenanceShoppingBasket(map));
                maintenanceShoppingBasketDTOS.add(maintenanceShoppingBasketDTO);
            });
            if (maintenanceShoppingBasketDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询购物篮成功", maintenanceShoppingBasketDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询失败", e);
            return ServerResponse.createBySuccessMessage("查询失败");
        }
    }


    /**
     * 管家质保已解决
     *
     * @param userToken
     * @param maintenanceRecordId
     * @param remark
     * @param image
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setMaintenanceSolve(String userToken, String maintenanceRecordId, String remark, String image) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        DjMaintenanceRecord djMaintenanceRecord=new DjMaintenanceRecord();
        djMaintenanceRecord.setId(maintenanceRecordId);
        djMaintenanceRecord.setState(2);
        //djMaintenanceRecord.setStewardState(2);
        djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);
        DjMaintenanceRecordContent djMaintenanceRecordContent=new DjMaintenanceRecordContent();
        djMaintenanceRecordContent.setImage(image);
        djMaintenanceRecordContent.setMaintenanceRecordId(maintenanceRecordId);
        djMaintenanceRecordContent.setMemberId(worker.getId());
        djMaintenanceRecordContent.setRemark(remark);
        djMaintenanceRecordContent.setType(2);
        djMaintenanceRecordContent.setWorkerTypeId(worker.getWorkerTypeId());
        djMaintenanceRecordContentMapper.insert(djMaintenanceRecordContent);
        return ServerResponse.createBySuccessMessage("操作成功");
    }


    /**
     * 删除购物篮商品
     *
     * @param id
     * @return
     */
    public ServerResponse deleteMaintenanceRecordProduct(String id) {
        try {
            DjMaintenanceRecordProduct djMaintenanceRecordProduct = djMaintenanceRecordProductMapper.selectByPrimaryKey(id);
            if(djMaintenanceRecordProduct.getPayState()==2)
                return ServerResponse.createByErrorMessage("商品已支付不能修改");
            djMaintenanceRecordProductMapper.deleteByPrimaryKey(id);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createBySuccessMessage("删除失败");
        }
    }




    /**
     * 申请报销
     *
     * @param money
     * @param description
     * @param image
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addApplyNewspaper(String userToken,
                                             String memberId,
                                             Double money,
                                             String description,
                                             String image,
                                             String houseId,
                                             String businessId){
        try {
            Member member = iMemberMapper.selectByPrimaryKey(memberId);
            if(member == null){
                return  ServerResponse.createByErrorMessage("用户不存在");
            }
            Complain complain = new Complain();
            complain.setMemberId(memberId);
            complain.setUserName(member.getName());
            complain.setUserNickName(member.getNickName());
            complain.setUserMobile(member.getMobile());
            complain.setComplainType(9);
            complain.setHouseId(houseId);
            complain.setDescription(description);
            complain.setApplyMoney(money);
            complain.setImage(image);
            complain.setStatus(0);
            complain.setBusinessId(businessId);
            iComplainMapper.insert(complain);
            return ServerResponse.createBySuccessMessage("提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }


    /**
     * 查询报销记录
     *
     * @param userToken
     * @param memberId
     * @return
     */
    public ServerResponse queryComplain(String userToken,String memberId){
        try {
            Example example = new Example(Complain.class);
            example.createCriteria().andEqualTo(Complain.MEMBER_ID,memberId)
                    .andEqualTo(Complain.DATA_STATUS,0).
                    andEqualTo(Complain.COMPLAIN_TYPE,9);
            example.orderBy(Complain.MODIFY_DATE).desc();
            List<Complain> member = iComplainMapper.selectByExample(example);
            if (member.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            return ServerResponse.createBySuccess("查询成功", member);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询报销记录详情
     *
     * @param id
     * @return
     */
    public ServerResponse queryComplainInFo(String id){
        try {
            if (CommonUtil.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("id不能为空");
            }

            Complain complain = iComplainMapper.selectByPrimaryKey(id);
            Map<String,Object> map = new HashMap<>();

            if(complain != null ){
                House house = houseMapper.selectByPrimaryKey(complain.getHouseId());
                if(house != null){
                    map.put("houseName",house.getResidential() + house.getBuilding() + "栋" +
                            house.getUnit() + "单元" + house.getNumber() + "号");
                }
                map.put("complainType", complain.getComplainType());
                map.put("status", complain.getStatus());
                map.put("modifyDate", complain.getModifyDate());
                map.put("actualMoney", complain.getActualMoney());
                map.put("rejectReason", complain.getRejectReason());
                map.put("createDate", complain.getCreateDate());
                map.put("applyMoney", complain.getApplyMoney());
                map.put("description", complain.getDescription());
                map.put("images", getImage(complain.getImage()));
                map.put("memberName", complain.getUserName());
                map.put("memberMobile", complain.getUserMobile());
                map.put("id", complain.getId());
                map.put("businessId", complain.getBusinessId());
                map.put("memberId", complain.getMemberId());
            }
            return ServerResponse.createBySuccess("查询成功", map);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 处理工匠报销申诉
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse handleAppeal(String id,
                                       Integer type,
                                       Double actualMoney,
                                       String operateId,
                                       String rejectReason){
        try {
            if (CommonUtil.isEmpty(type)) {
                return ServerResponse.createByErrorMessage("type不能为空");
            }
            if (CommonUtil.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("id不能为空");
            }
            if (CommonUtil.isEmpty(operateId)) {
                return ServerResponse.createByErrorMessage("operateId不能为空");
            }

            Complain complain = iComplainMapper.selectByPrimaryKey(id);
            MainUser mainUser = userMapper.selectByPrimaryKey(operateId);
            complain.setOperateName(mainUser.getUsername());
            complain.setOperateId(operateId);
            complain.setModifyDate(new Date());
            complain.setCreateDate(null);

            //type: 0 -确定处理 1-结束流程
            if (type == 0) {
                complain.setStatus(2);
                complain.setActualMoney(actualMoney);
                //给工匠加上申诉金额
//                Member member = iMemberMapper.selectByPrimaryKey(complain.getMemberId());
//                if (member == null) {
//                    return ServerResponse.createByErrorMessage("用户不存在");
//                }
//                member.setSurplusMoney(new BigDecimal(actualMoney).add(member.getSurplusMoney()));
//                member.setModifyDate(new Date());
//                iMemberMapper.updateByPrimaryKeySelective(member);

                //查询维保任务
                DjMaintenanceRecord djMaintenanceRecord = djMaintenanceRecordMapper.selectByPrimaryKey(complain.getBusinessId());
                if(djMaintenanceRecord == null){
                    return ServerResponse.createByErrorMessage("该条任务异常");
                }

                //增加维保商品记录
                DjMaintenanceRecordProduct djMaintenanceRecordProduct = new DjMaintenanceRecordProduct();
                djMaintenanceRecordProduct.setHouseId(djMaintenanceRecord.getHouseId());
                djMaintenanceRecordProduct.setMaintenanceRecordId(djMaintenanceRecord.getId());
                djMaintenanceRecordProduct.setMaintenanceMemberId(complain.getMemberId());
                djMaintenanceRecordProduct.setMaintenanceProductType(4);
                djMaintenanceRecordProduct.setTotalPrice(complain.getApplyMoney());
                //是否过保  1是，0否
                if(djMaintenanceRecord.getOverProtection() == 0){
                    //未过保
                    djMaintenanceRecordProduct.setPayPrice(0d);
                    djMaintenanceRecordProduct.setOverProtection(djMaintenanceRecord.getOverProtection());
                    djMaintenanceRecordProduct.setPayState(2);
                }else if(djMaintenanceRecord.getOverProtection() == 1){
                    //已过保
                    djMaintenanceRecordProduct.setPayState(1);
                    djMaintenanceRecordProduct.setPayPrice(actualMoney);
                    djMaintenanceRecordProduct.setOverProtection(djMaintenanceRecord.getOverProtection());

                    TaskStack taskStack = new TaskStack();
                    taskStack.setData(djMaintenanceRecordProduct.getId());
                    taskStack.setName("工匠申请报销");
                    taskStack.setType(15);//工匠报销费用申请
                    taskStack.setMemberId(djMaintenanceRecord.getMemberId());
                    taskStack.setHouseId(djMaintenanceRecord.getHouseId());
                    taskStack.setImage("icon/sheji.png");
                    taskStack.setState(0);
                    iMasterTaskStackMapper.insert(taskStack);
                }
                djMaintenanceRecordProduct.setComplainId(complain.getId());
                djMaintenanceRecordProductMapper.insert(djMaintenanceRecordProduct);

            }else if(type == 1){
                complain.setStatus(1);
                complain.setRejectReason(rejectReason);
            }
            iComplainMapper.updateByPrimaryKeySelective(complain);


            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 工匠申请维保验收
     *
     * @param id
     * @param remarks
     * @param image
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse workerApplyCollect(String id,String remarks,String image){
        try {
            if (CommonUtil.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("任务不存在");
            }

            DjMaintenanceRecord djMaintenanceRecord = djMaintenanceRecordMapper.selectByPrimaryKey(id);

            if(djMaintenanceRecord == null){
                return ServerResponse.createByErrorMessage("该任务不存在");
            }

            //增加工匠验收内容
            DjMaintenanceRecordContent djMaintenanceRecordContent = new DjMaintenanceRecordContent();
            djMaintenanceRecordContent.setImage(image);
            djMaintenanceRecordContent.setRemark(remarks);
            djMaintenanceRecordContent.setMaintenanceRecordId(id);
            djMaintenanceRecordContent.setMemberId(djMaintenanceRecord.getMemberId());
            djMaintenanceRecordContent.setType(1);
            djMaintenanceRecordContent.setWorkerTypeId(djMaintenanceRecord.getWorkerTypeId());
            djMaintenanceRecordContentMapper.insert(djMaintenanceRecordContent);

            //修改维保状态
            djMaintenanceRecord.setState(1);//业主待验收
            djMaintenanceRecord.setModifyDate(new Date());
            djMaintenanceRecord.setApplyCollectTime(new Date());
            djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);

            //工匠申请验收，给业主推送消息
            TaskStack taskStack = new TaskStack();
            taskStack.setData(id);
            taskStack.setName("工匠申请维保验收");
            taskStack.setType(13);//工匠维保申请验收
            taskStack.setMemberId(djMaintenanceRecord.getMemberId());
            taskStack.setHouseId(djMaintenanceRecord.getHouseId());
            taskStack.setImage("icon/sheji.png");
            taskStack.setState(0);
            taskStack.setRemarks(remarks);
            iMasterTaskStackMapper.insert(taskStack);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }



    /**
     * 已确认可开工
     *
     * @param businessId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse confirmStart(String businessId){
        try {
            DjMaintenanceRecord djMaintenanceRecord = new DjMaintenanceRecord();
            djMaintenanceRecord.setState(5);
            djMaintenanceRecord.setId(businessId);
            djMaintenanceRecord.setModifyDate(new Date());
            djMaintenanceRecord.setCreateDate(null);
            djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 管家质保处理提交
     *
     * @param maintenanceRecordId
     * @param remark
     * @param image
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setMaintenanceHandlesSubmissions(String userToken, String maintenanceRecordId, String remark, String image) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        DjMaintenanceRecord djMaintenanceRecord = djMaintenanceRecordMapper.selectByPrimaryKey(maintenanceRecordId);
        djMaintenanceRecord.setState(4);
        djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);
        DjMaintenanceRecordContent djMaintenanceRecordContent = new DjMaintenanceRecordContent();
        djMaintenanceRecordContent.setImage(image);
        djMaintenanceRecordContent.setMaintenanceRecordId(maintenanceRecordId);
        djMaintenanceRecordContent.setMemberId(worker.getId());
        djMaintenanceRecordContent.setRemark(remark);
        djMaintenanceRecordContent.setType(2);
        djMaintenanceRecordContent.setWorkerTypeId(worker.getWorkerTypeId());
        djMaintenanceRecordContentMapper.insert(djMaintenanceRecordContent);
        //扣除管家滞留金
        if(djMaintenanceRecord.equals("0")) {//维保期内
            Example example = new Example(DjMaintenanceRecordProduct.class);
            example.createCriteria().andEqualTo(DjMaintenanceRecordProduct.DATA_STATUS, 0)
                    .andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_RECORD_ID, maintenanceRecordId)
                    .andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_PRODUCT_TYPE, 2)
                    .andEqualTo(DjMaintenanceRecordProduct.PAY_STATE, 2);
            List<DjMaintenanceRecordProduct> djMaintenanceRecordProducts =
                    djMaintenanceRecordProductMapper.selectByExample(example);
            BigDecimal sumPrice = new BigDecimal(0);
            for (DjMaintenanceRecordProduct djMaintenanceRecordProduct : djMaintenanceRecordProducts) {
                sumPrice.add(BigDecimal.valueOf(djMaintenanceRecordProduct.getPrice() * djMaintenanceRecordProduct.getShopCount()));
            }
            this.maintenanceMinusDetention(worker,djMaintenanceRecord.getHouseId(),sumPrice,maintenanceRecordId);
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 维保扣除滞留金（工匠）
     * @param worker
     * @param houseId
     * @param sumPrice
     * @param maintenanceRecordId
     */
    private void maintenanceMinusDetention(Member worker,String houseId,BigDecimal sumPrice,String maintenanceRecordId){
        worker.setRetentionMoney(worker.getRetentionMoney().subtract(sumPrice));
        worker.setHaveMoney(worker.getHaveMoney().subtract(sumPrice));
        //生成流水
        WorkerDetail workerDetail = new WorkerDetail();
        workerDetail.setName("质保维修扣除费用");
        workerDetail.setWorkerId(worker.getId());
        workerDetail.setWorkerName(worker.getName());
        workerDetail.setHouseId(houseId);
        workerDetail.setMoney(sumPrice);
        workerDetail.setState(14);
        workerDetail.setDefinedWorkerId(maintenanceRecordId);
        workerDetail.setDefinedName("质保维修扣除费用");
        workerDetail.setHouseWorkerOrderId(maintenanceRecordId);
        workerDetail.setHaveMoney(sumPrice);
        workerDetail.setApplyMoney(sumPrice);
        workerDetail.setWalletMoney(worker.getHaveMoney());
        workerDetail.setDataStatus(0);
        iWorkerDetailMapper.insert(workerDetail);
        iMemberMapper.updateByPrimaryKeySelective(worker);
    }

    /**
     * 扣除店铺的滞留金
     * @param maintenanceRecordId
     * @param houseId
     */
    public void maintenanceMinusStorefront( String maintenanceRecordId, String houseId,String storefrontId,Double sumPrice) {
        AccountFlowRecord accountFlowRecord = new AccountFlowRecord();
        accountFlowRecord.setState(3);
        accountFlowRecord.setHouseOrderId(houseId);
        accountFlowRecord.setDefinedAccountId(maintenanceRecordId);
        accountFlowRecord.setCreateBy("SYSTEM");
        accountFlowRecord.setHouseOrderId(maintenanceRecordId);
        Storefront storefront =
                iMasterStorefrontMapper.selectByPrimaryKey(storefrontId);
        accountFlowRecord.setAmountBeforeMoney(storefront.getRetentionMoney());//入账前金额
        storefront.setRetentionMoney(MathUtil.sub(storefront.getRetentionMoney(),sumPrice));
        //扣除店铺占比金额
        iMasterStorefrontMapper.updateByPrimaryKeySelective(storefront);
        accountFlowRecord.setAmountAfterMoney(storefront.getRetentionMoney());//入账后金额
        accountFlowRecord.setFlowType("1");
        accountFlowRecord.setMoney(sumPrice);
        accountFlowRecord.setDefinedName("店铺维保占比,扣除滞留金：" + sumPrice);
        //记录流水
        iMasterAccountFlowRecordMapper.insert(accountFlowRecord);
    }



    /**
     * 工匠维保要货
     *
     * @param userToken
     * @param maintenanceRecordId
     * @param houseId
     * @return
     */
    public ServerResponse setWorkerMaintenanceGoods(String userToken, String maintenanceRecordId, String houseId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        House house = houseMapper.selectByPrimaryKey(houseId);
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
        DjMaintenanceRecord djMaintenanceRecord = djMaintenanceRecordMapper.selectByPrimaryKey(maintenanceRecordId);
        Map<String,Object> map=new HashMap<>();
        map.put("houseId",houseId);
        map.put("maintenanceMemberId",worker.getId());
        map.put("maintenanceRecordId",maintenanceRecordId);
        if (djMaintenanceRecord.getOverProtection() == 1) {//过保
            map.put("payState",1);
            map.put("totalPrice",null);
            djMaintenanceRecordProductMapper.setWorkerMaintenanceGoods(map);
            taskStackService.insertTaskStackInfo(houseId,house.getMemberId(),"维保商品费用",workerType.getImage(),14,maintenanceRecordId);
            return ServerResponse.createBySuccessMessage("提交成功,待业主审核");
        } else {//未过保
            map.put("payState",1);
            map.put("totalPrice",0);
            djMaintenanceRecordProductMapper.setWorkerMaintenanceGoods(map);
            paymentService.generateMaintenanceRecordOrder(userToken,maintenanceRecordId,3,house.getCityId(),null);
        }
        return ServerResponse.createBySuccessMessage("提交成功");
    }



}

