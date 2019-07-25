package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.DeliverHouseDTO;
import com.dangjia.acg.dto.deliver.OrderSplitItemDTO;
import com.dangjia.acg.dto.deliver.SplitDeliverDetailDTO;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitMapper;
import com.dangjia.acg.mapper.deliver.ISplitDeliverMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.modle.sup.SupplierProduct;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/12/5 0005
 * Time: 14:30
 * <p>
 * 生成发货单 发货操作类
 */
@Service
public class OrderSplitService {

    @Autowired
    private ISplitDeliverMapper splitDeliverMapper;
    @Autowired
    private IOrderSplitMapper orderSplitMapper;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IWarehouseMapper warehouseMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IComplainMapper complainMapper;
    @Autowired
    private ConfigMessageService configMessageService;

    /**
     * 修改 供应商结算状态
     * id 供应商结算id
     * deliveryFee 配送费用
     * applyMoney   供应商申请结算的价格
     */
    public ServerResponse setSplitDeliver(SplitDeliver splitDeliver) {
        try {
            if (!StringUtils.isNoneBlank(splitDeliver.getId()))
                return ServerResponse.createByErrorMessage("id 不能为null");
            SplitDeliver srcSplitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliver.getId());
            if (srcSplitDeliver == null)
                return ServerResponse.createByErrorMessage("无供应商结算单");
            //配送状态（0待发货,1已发待收货,2已收货,3取消,4部分收）
            if (!(srcSplitDeliver.getShippingState() == 2 || srcSplitDeliver.getShippingState() == 4 || srcSplitDeliver.getShippingState() == 6))
                return ServerResponse.createByErrorMessage("当前为未收货状态，不能申请结算");
            Example example = new Example(Complain.class);
            example.createCriteria()
                    .andEqualTo(Complain.COMPLAIN_TYPE, 4)
                    .andEqualTo(Complain.BUSINESS_ID, splitDeliver.getId())
                    .andEqualTo(Complain.STATUS, 0);
            List list = complainMapper.selectByExample(example);
            if (list.size() > 0) {
                return ServerResponse.createByErrorMessage("请勿重复提交申请！");
            }
            srcSplitDeliver.setDeliveryFee(splitDeliver.getDeliveryFee());
//            srcSplitDeliver.setApplyMoney(splitDeliver.getApplyMoney());
            srcSplitDeliver.setApplyState(0);//供应商申请结算的状态0申请中；1不通过；2通过
            splitDeliverMapper.updateByPrimaryKeySelective(srcSplitDeliver);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 供应商发货
     */
    public ServerResponse sentSplitDeliver(String splitDeliverId) {
        try {
            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            System.out.println(splitDeliver);
            if (splitDeliver.getShippingState() == 6) {
                return ServerResponse.createBySuccessMessage("材料员已撤回！");
            }
            splitDeliver.setSendTime(new Date());
            splitDeliver.setShippingState(1);//已发待收
            splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            House house = houseMapper.selectByPrimaryKey(splitDeliver.getHouseId());
            //业主
            configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "供应商发货", String.format
                    (DjConstants.PushMessage.YZ_F_001, house.getHouseName()), "");
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 工匠拒绝收货，打回供应商待发货
     */
    public ServerResponse rejectionSplitDeliver(String splitDeliverId) {
        try {
            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            if (splitDeliver.getShippingState() == 0) {
                return ServerResponse.createBySuccessMessage("您已经拒收！");
            }
            splitDeliver.setSendTime(null);
            splitDeliver.setShippingState(0);//已发待收
            splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 发货单明细
     */
    public ServerResponse splitDeliverDetail(String splitDeliverId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            SplitDeliverDetailDTO detailDTO = new SplitDeliverDetailDTO();
            detailDTO.setHouseId(splitDeliver.getHouseId());
            detailDTO.setNumber(splitDeliver.getNumber());
            detailDTO.setShipName(splitDeliver.getShipName());
            detailDTO.setShipAddress(splitDeliver.getShipAddress());
            detailDTO.setShippingState(splitDeliver.getShippingState());
            detailDTO.setApplyState(splitDeliver.getApplyState());
            detailDTO.setShipMobile(splitDeliver.getShipMobile());
            Member sup = memberMapper.selectByPrimaryKey(splitDeliver.getSupervisorId());//管家
            detailDTO.setSupMobile(sup.getMobile());
            detailDTO.setSupName(sup.getName());
            detailDTO.setMemo(splitDeliver.getMemo());
            detailDTO.setReason(splitDeliver.getReason());
            detailDTO.setTotalAmount(0.0);
            detailDTO.setApplyMoney(0.0);

            Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, splitDeliverId);
            example.orderBy(OrderSplitItem.CATEGORY_ID).desc();
            List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
            List<OrderSplitItemDTO> orderSplitItemDTOS = new ArrayList<>();
            House house = houseMapper.selectByPrimaryKey(splitDeliver.getHouseId());
            for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                if (orderSplitItem.getReceive() == null) {
                    orderSplitItem.setReceive(0D);
                }
                Product product=forMasterAPI.getProduct(house.getCityId(), orderSplitItem.getProductId());
                OrderSplitItemDTO orderSplitItemDTO = new OrderSplitItemDTO();
                orderSplitItemDTO.setProductName(product.getName());
                orderSplitItemDTO.setNum(orderSplitItem.getNum());
                orderSplitItemDTO.setCost(product.getCost());
                orderSplitItemDTO.setSupCost(orderSplitItem.getSupCost());
                orderSplitItemDTO.setUnitName(orderSplitItem.getUnitName());
                orderSplitItemDTO.setAskCount(orderSplitItem.getAskCount());
                orderSplitItemDTO.setShopCount(String.valueOf(orderSplitItem.getShopCount()));
                orderSplitItemDTO.setImage(address + product.getImage());
                orderSplitItemDTO.setReceive(orderSplitItem.getReceive());
                orderSplitItemDTO.setBrandSeriesName(forMasterAPI.brandSeriesName(house.getCityId(), orderSplitItem.getProductId()));
                orderSplitItemDTO.setBrandName(forMasterAPI.brandName(house.getCityId(), orderSplitItem.getProductId()));
                if (splitDeliver.getShippingState() == 2 || splitDeliver.getShippingState() == 4 || splitDeliver.getShippingState() == 5) {
                    orderSplitItemDTO.setTotalPrice(new BigDecimal(orderSplitItem.getSupCost() * orderSplitItem.getReceive()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    orderSplitItemDTO.setTotalPrice(new BigDecimal(orderSplitItem.getSupCost() * orderSplitItem.getNum()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                }
                orderSplitItemDTOS.add(orderSplitItemDTO);
                detailDTO.setApplyMoney(new BigDecimal(detailDTO.getApplyMoney() + orderSplitItemDTO.getTotalPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                detailDTO.setTotalAmount(new BigDecimal(detailDTO.getTotalAmount() + (orderSplitItem.getSupCost() * orderSplitItem.getNum())).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            detailDTO.setSize(orderSplitItemList.size());
            detailDTO.setOrderSplitItemDTOS(orderSplitItemDTOS);
            return ServerResponse.createBySuccess("查询成功", detailDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 供应商端发货列表
     * shipState  0待发货,1已发待收货,2已收货,3取消,4部分收,5已结算
     */
    public ServerResponse splitDeliverList(String supplierId, int shipState) {
        Example example = new Example(SplitDeliver.class);
        if (shipState == 2) {
            example.createCriteria().andEqualTo(SplitDeliver.SUPPLIER_ID, supplierId)
                    .andCondition(" shipping_state in(2,4) ");
            example.orderBy(SplitDeliver.CREATE_DATE).desc();
            example.orderBy(SplitDeliver.APPLY_STATE).asc();
        } else {
            example.createCriteria().andEqualTo(SplitDeliver.SUPPLIER_ID, supplierId)
                    .andEqualTo(SplitDeliver.SHIPPING_STATE, shipState);
            example.orderBy(SplitDeliver.CREATE_DATE).desc();
            example.orderBy(SplitDeliver.APPLY_STATE).asc();
        }
        List<SplitDeliver> splitDeliverList = splitDeliverMapper.selectByExample(example);
        for (SplitDeliver splitDeliver : splitDeliverList) {
            System.out.println(splitDeliver);
        }
        return ServerResponse.createBySuccess("查询成功", splitDeliverList);
    }


    /**
     * 撤回供应商待发货的订单（整单撤回）
     */
    public ServerResponse withdrawSupplier(String orderSplitId) {
        try {
            //将发货单设置为撤回状态
            SplitDeliver splitDeliver=splitDeliverMapper.selectByPrimaryKey(orderSplitId);
            if (splitDeliver.getShippingState()==1) {
                return ServerResponse.createBySuccessMessage("供应商已发货！");
            }
            splitDeliver.setShippingState(6);
            splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            return ServerResponse.createBySuccessMessage("撤回成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("撤回失败");
        }
    }

    /**
     * 发送供应商
     * 分发不同供应商
     */
    public ServerResponse sentSupplier(String orderSplitId, String splitItemList) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
            OrderSplit orderSplit = orderSplitMapper.selectByPrimaryKey(orderSplitId);
            House house = houseMapper.selectByPrimaryKey(orderSplit.getHouseId());
            Member supervisor = memberMapper.getSupervisor(house.getId());//管家
            Member member = memberMapper.selectByPrimaryKey(house.getMemberId());

            JSONArray arr = JSONArray.parseArray(splitItemList);
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String id = obj.getString("id");
                String supplierId = obj.getString("supplierId");
                Supplier supplier = forMasterAPI.getSupplier(house.getCityId(), supplierId);
                JsmsUtil.sendSupplier(supplier.getTelephone(), address + "submitNumber");


                OrderSplitItem orderSplitItem = orderSplitItemMapper.selectByPrimaryKey(id);
                Example example = new Example(SplitDeliver.class);
                example.createCriteria().andEqualTo(SplitDeliver.HOUSE_ID, orderSplit.getHouseId()).andEqualTo(SplitDeliver.SUPPLIER_ID, supplierId)
                        .andEqualTo(SplitDeliver.SHIPPING_STATE, 0).andEqualTo(SplitDeliver.ORDER_SPLIT_ID, orderSplitId);
                List<SplitDeliver> splitDeliverList = splitDeliverMapper.selectByExample(example);
                SplitDeliver splitDeliver;
                if (splitDeliverList.size() > 0) {
                    splitDeliver = splitDeliverList.get(0);
                } else {
                    example = new Example(SplitDeliver.class);
                    splitDeliver = new SplitDeliver();
                    splitDeliver.setNumber(orderSplit.getNumber() + "00" + splitDeliverMapper.selectCountByExample(example));//发货单号
                    splitDeliver.setHouseId(house.getId());
                    splitDeliver.setOrderSplitId(orderSplitId);
                    splitDeliver.setTotalAmount(0.0);
                    splitDeliver.setDeliveryFee(0.0);
                    splitDeliver.setApplyMoney(0.0);
                    splitDeliver.setShipName(member.getNickName() == null ? member.getName() : member.getNickName());
                    splitDeliver.setShipMobile(member.getMobile());
                    splitDeliver.setShipAddress(house.getHouseName());
                    splitDeliver.setSupplierId(supplierId);//供应商id
                    splitDeliver.setSupplierTelephone(supplier.getTelephone());//供应商联系电话
                    splitDeliver.setSupplierName(supplier.getName());//供应商供应商名称
                    splitDeliver.setSupervisorId(supervisor.getId());//管家id
                    splitDeliver.setSubmitTime(new Date());
                    splitDeliver.setSupState(0);
                    splitDeliver.setShippingState(0);//待发货状态
                    splitDeliver.setApplyState(null);
                    splitDeliverMapper.insert(splitDeliver);
                }

                SupplierProduct supplierProduct = forMasterAPI.getSupplierProduct(house.getCityId(), supplierId, orderSplitItem.getProductId());
                orderSplitItem.setSupCost(supplierProduct.getPrice());//供应价
                orderSplitItem.setSplitDeliverId(splitDeliver.getId());
                orderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);

                splitDeliver.setTotalAmount(supplierProduct.getPrice() * orderSplitItem.getNum() + splitDeliver.getTotalAmount());//累计供应商价总价
                splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
//                orderSplitItemMapper.setSupplierId(id, splitDeliver.getId());
            }
            orderSplit.setApplyStatus(2);//发给供应商
            orderSplitMapper.updateByPrimaryKeySelective(orderSplit);

            /*
             * 计算是否超过免费要货次数,收取工匠运费
             */
//            Example example = new Example(OrderSplit.class);
//            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, orderSplit.getHouseId()).andEqualTo(OrderSplit.WORKER_TYPE_ID, orderSplit.getWorkerTypeId());
//            List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
//            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(orderSplit.getWorkerTypeId());
//            if (orderSplitList.size() > workerType.getSafeState()) {//超过免费次数收工匠运费
//                //TODO 计算运费 暂无准确计算公式
//                BigDecimal yunFei = new BigDecimal(0.1);
//                example = new Example(OrderSplitItem.class);
//                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
//                List<OrderSplitItem> osiList = orderSplitItemMapper.selectByExample(example);
//                for (OrderSplitItem osi : osiList) {
//                    //Product product = forMasterAPI.getProduct(osi.getProductId());
//                    //yunFei += osi.getTotalPrice() * 0.1;
//                }
//
//                Member worker = memberMapper.selectByPrimaryKey(orderSplit.getSupervisorId());//要货人
//                BigDecimal haveMoney = worker.getHaveMoney().subtract(yunFei);
//                BigDecimal surplusMoneys = worker.getSurplusMoney().subtract(yunFei);
//                WorkerDetail workerDetail = new WorkerDetail();
//                workerDetail.setName(workerType.getName()+"要货运费");
//                workerDetail.setWorkerId(worker.getId());
//                workerDetail.setWorkerName(worker.getName());
//                workerDetail.setHouseId(orderSplit.getHouseId());
//                workerDetail.setMoney(yunFei);
//                workerDetail.setState(7);//收取运费
//                workerDetail.setWalletMoney(haveMoney);
//                workerDetail.setApplyMoney(yunFei);
//                workerDetailMapper.insert(workerDetail);
//
//                worker.setHaveMoney(haveMoney);
//                worker.setSurplusMoney(surplusMoneys);
//                memberMapper.updateByPrimaryKeySelective(worker);
//            }

            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 取消(打回)
     * 返回数量
     */
    public ServerResponse cancelOrderSplit(String orderSplitId) {
        try {
            OrderSplit orderSplit = orderSplitMapper.selectByPrimaryKey(orderSplitId);
            Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
            List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
            for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                Warehouse warehouse = warehouseMapper.getByProductId(orderSplitItem.getProductId(), orderSplit.getHouseId());
                if (warehouse != null) {
                    warehouse.setAskCount(warehouse.getAskCount() - orderSplitItem.getNum());
                    warehouseMapper.updateByPrimaryKeySelective(warehouse);
                }
            }

            orderSplit.setApplyStatus(3);
            orderSplitMapper.updateByPrimaryKey(orderSplit);

            if (!CommonUtil.isEmpty(orderSplit.getMendNumber())) {
                MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(orderSplit.getMendNumber());
                mendOrder.setState(2);//不通过取消
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 发货单打回
     */
    public ServerResponse cancelSplitDeliver(String splitDeliverId) {
        try {
            //将发货单设置为撤回状态
            SplitDeliver splitDeliver=splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            if (splitDeliver.getShippingState()==6) {
                Example example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, splitDeliver.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
                for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                    Warehouse warehouse = warehouseMapper.getByProductId(orderSplitItem.getProductId(), splitDeliver.getHouseId());
                    if (warehouse != null) {
                        warehouse.setAskCount(warehouse.getAskCount() - orderSplitItem.getNum());
                        warehouseMapper.updateByPrimaryKeySelective(warehouse);
                    }
                }
                splitDeliver.setShippingState(3);//取消发货单
                splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            }
            return ServerResponse.createBySuccessMessage("打回成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("打回成功");
        }
    }

    /**
     * 要货单看明细
     */
    public ServerResponse orderSplitItemList(String orderSplitId) {
        try {
            Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplitId);
            example.orderBy(OrderSplitItem.CATEGORY_ID).desc();
            List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
            List<Map> mapList = new ArrayList<>();
            for (OrderSplitItem v : orderSplitItemList) {
                boolean isAdd=false;
                if (!CommonUtil.isEmpty(v.getSplitDeliverId())) {
                    SplitDeliver deliver = splitDeliverMapper.selectByPrimaryKey(v.getSplitDeliverId());
                    if (deliver.getShippingState() == 6) {
                        isAdd=true;
                    }
                }else {
                    isAdd=true;
                }
                if(isAdd) {
                    v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                    Map map = BeanUtils.beanToMap(v);
                    List<String> supplierId = splitDeliverMapper.getSupplierGoodsId(v.getHouseId(), v.getProductSn());
                    if (supplierId.size() > 0) {
                        map.put(SplitDeliver.SUPPLIER_ID, supplierId.get(0));
                    }
                    mapList.add(map);
                }
            }
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 材料员看房子列表
     */
    public ServerResponse getHouseList(PageDTO pageDTO, String likeAddress,String startDate, String endDate) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
//        List<House> houseList = houseMapper.selectAll();
            List<House> houseList = houseMapper.getByLikeAddress(likeAddress,startDate,endDate);
            PageInfo pageResult = new PageInfo(houseList);

            List<DeliverHouseDTO> deliverHouseDTOList = new ArrayList<DeliverHouseDTO>();
            for (House house : houseList) {
                Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                DeliverHouseDTO deliverHouseDTO = new DeliverHouseDTO();
                deliverHouseDTO.setHouseId(house.getId());
                deliverHouseDTO.setCreateDate(house.getCreateDate());
                deliverHouseDTO.setHouseName(house.getHouseName());
                deliverHouseDTO.setConstructionDate(house.getConstructionDate());
                deliverHouseDTO.setName("-");
                deliverHouseDTO.setMobile("-");
                if (member != null) {
                    deliverHouseDTO.setName(member.getName() == null ? member.getNickName() : member.getName());
                    deliverHouseDTO.setMobile(member.getMobile());
                }
                Example example = new Example(OrderSplit.class);
                example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, house.getId()).andEqualTo(OrderSplit.APPLY_STATUS, 2);//已发给供应商
                deliverHouseDTO.setSent(orderSplitMapper.selectCountByExample(example));
                example = new Example(OrderSplit.class);
                example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, house.getId()).andEqualTo(OrderSplit.APPLY_STATUS, 1);//要货申请中
                deliverHouseDTO.setWait(orderSplitMapper.selectCountByExample(example));
                deliverHouseDTOList.add(deliverHouseDTO);
            }
            pageResult.setList(deliverHouseDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 根据房子id查询要货单列表
     */
    public ServerResponse getOrderSplitList(String houseId) {
        try {
            Example example = new Example(OrderSplit.class);
            example.createCriteria()
                    .andEqualTo(OrderSplit.HOUSE_ID, houseId)
                    .andGreaterThan(OrderSplit.APPLY_STATUS, 0)//大于0
                    .andNotEqualTo(OrderSplit.APPLY_STATUS, 4);//过滤业主未支付
            example.orderBy(OrderSplit.CREATE_DATE).desc();
            List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
            List<Map> orderSplitMaps = new ArrayList<>();
            //查询时候存在待发货的单据，用于撤回待发货的发货单
            for (OrderSplit orderSplit : orderSplitList) {
                Map map = BeanUtils.beanToMap(orderSplit);
                example = new Example(SplitDeliver.class);
                example.createCriteria().andEqualTo(SplitDeliver.HOUSE_ID, orderSplit.getHouseId())
                        .andCondition(" shipping_state in (0,6)").andEqualTo(SplitDeliver.ORDER_SPLIT_ID, orderSplit.getId());
                int splitDeliverList = splitDeliverMapper.selectCountByExample(example);
                map.put("num", splitDeliverList);
                orderSplitMaps.add(map);
            }
            return ServerResponse.createBySuccess("查询成功", orderSplitMaps);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
