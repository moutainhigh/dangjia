package com.dangjia.acg.service.finance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.SupplierDeliverDTO;
import com.dangjia.acg.dto.finance.WebSplitDeliverItemDTO;
import com.dangjia.acg.dto.receipt.ReceiptDTO;
import com.dangjia.acg.mapper.account.IMasterAccountFlowRecordMapper;
import com.dangjia.acg.mapper.delivery.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.delivery.ISplitDeliverMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontMapper;
import com.dangjia.acg.mapper.receipt.IReceiptMapper;
import com.dangjia.acg.mapper.repair.IMendDeliverMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.supplier.IMasterSupplierMapper;
import com.dangjia.acg.modle.account.AccountFlowRecord;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.receipt.Receipt;
import com.dangjia.acg.modle.repair.MendDeliver;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.sup.SupplierProduct;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ysl
 * Date: 2019/1/24 0008
 * Time: 16:48
 */
@Service
public class WebSplitDeliverService {
    @Autowired
    private ISplitDeliverMapper iSplitDeliverMapper;
    @Autowired
    private IMendDeliverMapper iMendDeliverMapper;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IReceiptMapper iReceiptMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IOrderSplitItemMapper iOrderSplitItemMapper;

    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IMasterStorefrontMapper iMasterStorefrontMapper;
    @Autowired
    private IMasterSupplierMapper iMasterSupplierMapper;
    @Autowired
    private IMasterAccountFlowRecordMapper iMasterAccountFlowRecordMapper;

    @Autowired
    private BasicsStorefrontAPI basicsStorefrontAPI;
    /**
     * 所有供应商
     *
     * @param pageDTO    分页参数
     * @param applyState 供应商申请结算的状态：0申请中(待处理)；1不通过(驳回)；2通过(同意),3其它(迁移)
     * @param searchKey  收货地址，供应商名称
     * @param beginDate  开始时间
     * @param endDate    结束时间
     * @return
     */
    public ServerResponse getAllSplitDeliver(PageDTO pageDTO, String cityId,String userId,Integer applyState, String searchKey, String beginDate, String endDate) {
        try {
            if (applyState == null) {
                applyState = -1;
            }
            Storefront storefront=null;
            if(applyState !=-1) {
                storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
                if (storefront == null) {
                    return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
                }
            }
            if (!CommonUtil.isEmpty(beginDate) && !CommonUtil.isEmpty(endDate) && (applyState == 0 || applyState == -1)) {
                applyState = -2;
            }
            if (!CommonUtil.isEmpty(beginDate) && !CommonUtil.isEmpty(endDate)) {
                if (beginDate.equals(endDate)) {
                    beginDate = beginDate + " " + "00:00:00";
                    endDate = endDate + " " + "23:59:59";
                }
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<WebSplitDeliverItemDTO> webSplitDeliverItemDTOLists = iSplitDeliverMapper.getWebSplitDeliverList(storefront==null?null:storefront.getId(),cityId,applyState, searchKey, beginDate, endDate);
            PageInfo pageResult = new PageInfo(webSplitDeliverItemDTOLists);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 根据供应商id查询要货单列表/模糊查询要货单列表
     *
     * @param pageDTO
     * @param supplierId
     * @param searchKey
     * @param beginDate
     * @param endDate
     * @return
     */
    public ServerResponse getOrderSplitList(PageDTO pageDTO, String supplierId, String searchKey, String beginDate, String endDate) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if (!CommonUtil.isEmpty(beginDate) && !CommonUtil.isEmpty(endDate)) {
                if (beginDate.equals(endDate)) {
                    beginDate = beginDate + " " + "00:00:00";
                    endDate = endDate + " " + "23:59:59";
                }
            }
            List<WebSplitDeliverItemDTO> orderSplitList = iSplitDeliverMapper.getOrderSplitList(supplierId, searchKey, beginDate, endDate);
            PageInfo pageResult = new PageInfo(orderSplitList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 供应商端发货列表
     *
     * @param splitDeliverId
     * @return
     */
    public ServerResponse splitDeliverList(String splitDeliverId) {
        try {
            List<WebSplitDeliverItemDTO> webSplitDeliverItemDTOS = iSplitDeliverMapper.splitDeliverList(splitDeliverId);
            return ServerResponse.createBySuccess("查询成功", webSplitDeliverItemDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 修改供应商发货 状态
     *
     * @param splitDeliver applyState  供应商申请结算的状态 0申请中(待处理)；1不通过(驳回)；2通过(同意)
     *                     reason 不同意理由
     * @return
     */
    public ServerResponse setSplitDeliver(SplitDeliver splitDeliver) {
        try {
            if (!StringUtils.isNoneBlank(splitDeliver.getId()))
                return ServerResponse.createByErrorMessage("Id 不能为null");
            SplitDeliver srcSplitDeliver = iSplitDeliverMapper.selectByPrimaryKey(splitDeliver.getId());
            if (srcSplitDeliver == null)
                return ServerResponse.createByErrorMessage("无供应商发货单");
            if (splitDeliver.getApplyState() != -1) {//供应商申请结算的状态：0申请中；1不通过；2通过
                if (splitDeliver.getApplyState() == null)
                    return ServerResponse.createByErrorMessage("该供应商结算状态 未知");
                if (splitDeliver.getApplyState() == 1) {//不通过
                    srcSplitDeliver.setApplyState(1);
                    srcSplitDeliver.setReason(splitDeliver.getReason());
                }
                if (splitDeliver.getApplyState() == 2) {//通过
                    srcSplitDeliver.setApplyState(2);
                }
                srcSplitDeliver.setModifyDate(new Date());
                iSplitDeliverMapper.updateByPrimaryKey(srcSplitDeliver);
            }
            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 供应商查看货单列表
     *
     * @param supplierId
     * @param shipAddress
     * @param beginDate
     * @param endDate
     * @return
     */
    public ServerResponse mendDeliverList(String supplierId, String shipAddress, String beginDate, String endDate, Integer applyState) {
        try {
            if (!CommonUtil.isEmpty(beginDate) && !CommonUtil.isEmpty(endDate)) {
                if (beginDate.equals(endDate)) {
                    beginDate = beginDate + " " + "00:00:00";
                    endDate = endDate + " " + "23:59:59";
                }
            }
            List<SupplierDeliverDTO> supplierDeliverDTOS = iSplitDeliverMapper.mendDeliverList(supplierId, shipAddress, beginDate, endDate, applyState);
            for (SupplierDeliverDTO supplierDeliverDTO : supplierDeliverDTOS) {
                supplierDeliverDTO.setDeliverType(1);
                Double totalAmount = iOrderSplitItemMapper.getSplitDeliverSellPrice(supplierDeliverDTO.getId());
                supplierDeliverDTO.setTotalAmount(totalAmount);
            }
            List<SupplierDeliverDTO> supplierDeliverDTOS1 = iMendDeliverMapper.mendDeliverList(supplierId, shipAddress, beginDate, endDate, applyState);
            for (SupplierDeliverDTO supplierDeliverDTO : supplierDeliverDTOS1) {
                supplierDeliverDTO.setDeliverType(2);
            }
            supplierDeliverDTOS.addAll(supplierDeliverDTOS1);
            return ServerResponse.createBySuccess("查询成功", supplierDeliverDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 供应商结算
     *
     * @param image 图片
     * @param merge 表的id和类型 1发货单；2退货单
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse settlemen(String image, String merge, String supplierId, String userId, String cityId, Double settlementAmount, String sourceType) throws RuntimeException {
        try {
            Receipt receipt = new Receipt();
            receipt.setNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
            if (StringUtils.isNotEmpty(merge)) {
                JSONArray itemObjArr = JSON.parseArray(merge);
                double splitDeliverPrice = 0d;
                double mendDeliverPrice = 0d;
                for (int i = 0; i < itemObjArr.size(); i++) {
                    JSONObject jsonObject = itemObjArr.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    int deliverType = jsonObject.getInteger("deliverType");
                    if (deliverType == 1) {
                        //发货单结算通过
                        SplitDeliver splitDeliver = new SplitDeliver();
                        splitDeliver.setId(id);
                        splitDeliver.setApplyState(2);
                        splitDeliver.setReceiptNum(receipt.getNumber());
                        this.setSplitDeliver(splitDeliver);
                        splitDeliverPrice += iSplitDeliverMapper.selectByPrimaryKey(id).getApplyMoney();
                    } else if (deliverType == 2) {
                        //退货单结算通过
                        MendDeliver mendDeliver = new MendDeliver();
                        mendDeliver.setId(id);
                        mendDeliver.setApplyState(2);
                        mendDeliver.setShippingState(2);
                        mendDeliver.setReceiptNum(receipt.getNumber());
                        iMendDeliverMapper.updateByPrimaryKeySelective(mendDeliver);
                        mendDeliverPrice += iMendDeliverMapper.selectByPrimaryKey(id).getApplyMoney();
                    }
                }
                //添加回执
                receipt.setImage(image);
                receipt.setMerge(merge);
                receipt.setCreateDate(new Date());
                receipt.setSupplierId(supplierId);
                receipt.setTotalAmount(splitDeliverPrice - mendDeliverPrice);
                receipt.setSourceType(sourceType);
                iReceiptMapper.insert(receipt);
                //店铺信息
                Example example=new Example(Storefront.class);
                example.createCriteria().andEqualTo(Storefront.CITY_ID,cityId)
                        .andEqualTo(Storefront.DATA_STATUS,0)
                        .andEqualTo(Storefront.USER_ID,userId);
                Storefront storefront = iMasterStorefrontMapper.selectOneByExample(example);

                AccountFlowRecord accountFlowRecord=new AccountFlowRecord();
                AccountFlowRecord accountFlowRecord2=new AccountFlowRecord();
                if(sourceType.equals("1")){
                    if(storefront.getSurplusMoney()<settlementAmount) {
                        return ServerResponse.createByErrorMessage("余额不足");
                    }
                    //扣除店铺余额
                    //入账前金额
                    accountFlowRecord2.setAmountBeforeMoney(storefront.getTotalAccount());
//                    storefront.setSurplusMoney(storefront.getSurplusMoney()-settlementAmount);
                    storefront.setTotalAccount(storefront.getTotalAccount()-settlementAmount);
                    //入账后金额
                    accountFlowRecord.setAmountAfterMoney(storefront.getTotalAccount());
                    iMasterStorefrontMapper.updateByPrimaryKeySelective(storefront);
                    accountFlowRecord2.setFlowType("1");
                    accountFlowRecord2.setMoney(settlementAmount);
                    accountFlowRecord2.setState(9);
                    accountFlowRecord2.setDefinedAccountId(storefront.getId());
                    accountFlowRecord2.setDefinedName("合并結算");
                    accountFlowRecord2.setCreateBy(userId);
                    //供应商加余额
                    DjSupplier djSupplier = iMasterSupplierMapper.selectByPrimaryKey(supplierId);
                    //入账前金额
                    accountFlowRecord.setAmountBeforeMoney(djSupplier.getTotalAccount());
//                    djSupplier.setSurplusMoney(CommonUtil.isEmpty(djSupplier.getSurplusMoney())?0:djSupplier.getSurplusMoney()+settlementAmount);
                    djSupplier.setTotalAccount(CommonUtil.isEmpty(djSupplier.getTotalAccount())?0:djSupplier.getTotalAccount()+settlementAmount);
                    //入账后金额
                    accountFlowRecord.setAmountAfterMoney(djSupplier.getTotalAccount());
                    iMasterSupplierMapper.updateByPrimaryKeySelective(djSupplier);
                    accountFlowRecord.setFlowType("2");
                    accountFlowRecord.setMoney(settlementAmount);
                    accountFlowRecord.setState(0);
                    accountFlowRecord.setDefinedAccountId(supplierId);
                    accountFlowRecord.setDefinedName("合并結算");
                    accountFlowRecord.setCreateBy(userId);
                }
                if(sourceType.equals("1")){
                    //供应商流水
                    accountFlowRecord.setHouseOrderId(receipt.getId());
                    iMasterAccountFlowRecordMapper.insert(accountFlowRecord);
                    //店铺流水
                    accountFlowRecord2.setHouseOrderId(receipt.getId());
                    iMasterAccountFlowRecordMapper.insert(accountFlowRecord2);
                }
            }
            return ServerResponse.createBySuccess("结算成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("结算失败");
        }
    }


    /**
     * 已结算货单列表
     *
     * @param shipAddress
     * @param beginDate
     * @param endDate
     * @return
     */
    public ServerResponse clsdMendDeliverList(String shipAddress, String beginDate, String endDate, String supplierId) {
        try {
            if (!CommonUtil.isEmpty(beginDate) && !CommonUtil.isEmpty(endDate)) {
                if (beginDate.equals(endDate)) {
                    beginDate = beginDate + " " + "00:00:00";
                    endDate = endDate + " " + "23:59:59";
                }
            }
            Example example = new Example(Receipt.class);
            example.createCriteria().andEqualTo(Receipt.SUPPLIER_ID, supplierId);
            List<Receipt> receipts = iReceiptMapper.selectByExample(example);
            List<ReceiptDTO> list = new ArrayList<>();
            for (Receipt receipt : receipts) {
                List<SupplierDeliverDTO> supplierDeliverDTOList = new ArrayList<>();
                double amount;
                double sd = 0D;
                double md = 0D;
                JSONArray itemObjArr = JSON.parseArray(receipt.getMerge());
                ReceiptDTO receiptDTO = new ReceiptDTO();
                for (int i = 0; i < itemObjArr.size(); i++) {
                    SupplierDeliverDTO supplierDeliverDTO = null;
                    JSONObject jsonObject = itemObjArr.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    int deliverType = jsonObject.getInteger("deliverType");
                    if (deliverType == 1) {
                        SplitDeliver splitDeliver = iSplitDeliverMapper.selectClsd(id, shipAddress, beginDate, endDate);
                        if (null != splitDeliver) {
                            supplierDeliverDTO = new SupplierDeliverDTO();
                            supplierDeliverDTO.setId(splitDeliver.getId());
                            supplierDeliverDTO.setNumber(splitDeliver.getNumber());
                            supplierDeliverDTO.setShipAddress(splitDeliver.getShipAddress());
                            Double totalAmount = iOrderSplitItemMapper.getSplitDeliverSellPrice(supplierDeliverDTO.getId());
                            supplierDeliverDTO.setTotalAmount(totalAmount);
//                            supplierDeliverDTO.setTotalAmount(splitDeliver.getTotalAmount());
                            supplierDeliverDTO.setApplyMoney(splitDeliver.getApplyMoney());
                            supplierDeliverDTO.setDeliverType(1);
                            sd += splitDeliver.getApplyMoney();
                        }
                    } else if (deliverType == 2) {
                        MendDeliver mendDeliver = iMendDeliverMapper.selectClsd(id, shipAddress, beginDate, endDate);
                        if (null != mendDeliver) {
                            supplierDeliverDTO = new SupplierDeliverDTO();
                            supplierDeliverDTO.setId(mendDeliver.getId());
                            supplierDeliverDTO.setNumber(mendDeliver.getNumber());
                            supplierDeliverDTO.setShipAddress(mendDeliver.getShipAddress());
                            supplierDeliverDTO.setApplyMoney(mendDeliver.getApplyMoney());
                            supplierDeliverDTO.setTotalAmount(mendDeliver.getTotalAmount());
                            supplierDeliverDTO.setDeliverType(2);
                            md += mendDeliver.getApplyMoney();
                        }
                    }
                    if (null != supplierDeliverDTO) {
                        supplierDeliverDTOList.add(supplierDeliverDTO);
                    }
                }
                if (supplierDeliverDTOList.size() > 0) {
                    //结算金额
                    amount = sd - md;
                    receiptDTO.setAmount(amount);
                    receiptDTO.setList(supplierDeliverDTOList);
                    receiptDTO.setCreateDate(receipt.getCreateDate());
                    receiptDTO.setId(receipt.getId());
                    list.add(receiptDTO);
                    //对list进行排序 根据时间降序排序
                    list.sort((r1, r2) -> r2.getCreateDate().compareTo(r1.getCreateDate()));
                }
            }
            return ServerResponse.createBySuccess("查询成功", list);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查看回执
     *
     * @param id
     * @return
     */
    public ServerResponse selectReceipt(String id) {
        try {
            Receipt receipt = iReceiptMapper.selectByPrimaryKey(id);
            JSONArray itemObjArr = JSON.parseArray(receipt.getImage());
            return ServerResponse.createBySuccess("查询成功", itemObjArr);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 退货单查看详情
     *
     * @param id
     * @return
     */
    public ServerResponse mendDeliverDetail(String id, String cityId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            MendDeliver mendDeliver = iMendDeliverMapper.selectByPrimaryKey(id);
            MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendDeliver.getMendOrderId());

            House house = houseMapper.selectByPrimaryKey(mendDeliver.getHouseId());
            List<MendMateriel> mendMateriels = iMendDeliverMapper.mendDeliverDetail(id);
            List<Map> mendMaterielsMap = new ArrayList<>();
            for (MendMateriel mendMateriel : mendMateriels) {
                mendMateriel.setImage(address + mendMateriel.getImage());
                //如果是工匠退 ，保证实际字段不为空
                if (mendOrder.getType() == 2) {
                    mendMateriel.setActualCount(mendMateriel.getActualCount() == null ? 0d : mendMateriel.getActualCount());
                    mendMateriel.setActualPrice(mendMateriel.getActualPrice() == null ? 0d : mendMateriel.getActualPrice());
                } else {
                    mendMateriel.setActualCount(null);
                    mendMateriel.setActualPrice(null);
                }
                Map map = BeanUtils.beanToMap(mendMateriel);
                if (house != null) {
                    cityId = house.getCityId();
                }
                SupplierProduct supplierProduct = forMasterAPI.getSupplierProduct(cityId, mendDeliver.getSupplierId(), mendMateriel.getProductId());
                if (supplierProduct != null) {
                    map.put("supCost", supplierProduct.getPrice());
                } else {
                    map.put("supCost", mendMateriel.getCost());
                }
                mendMaterielsMap.add(map);
            }
            return ServerResponse.createBySuccess("查询成功", mendMaterielsMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}

