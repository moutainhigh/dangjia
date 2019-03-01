package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.DeliverHouseDTO;
import com.dangjia.acg.dto.deliver.OrderSplitItemDTO;
import com.dangjia.acg.dto.deliver.SplitDeliverDetailDTO;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitMapper;
import com.dangjia.acg.mapper.deliver.ISplitDeliverMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.sup.SupplierProduct;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;

    private static Logger LOG = LoggerFactory.getLogger(OrderSplitService.class);


    //TODO 供应商其它功能  供应商结算

    /**
     * 修改 供应商结算状态
     * id 供应商结算id
     * deliveryFee 配送费用
     * applyMoney   供应商申请结算的价格
     *
     */
    public ServerResponse setSplitDeliver(SplitDeliver splitDeliver) {
        try {
            if (!StringUtils.isNoneBlank(splitDeliver.getId()))
                return ServerResponse.createByErrorMessage("id 不能为null");

            SplitDeliver srcSplitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliver.getId());
            if (srcSplitDeliver == null)
                return ServerResponse.createByErrorMessage("无供应商结算单");

            //配送状态（0待发货,1已发待收货,2已收货,3取消,4部分收）
            if (srcSplitDeliver.getShipState() != 2)
                return ServerResponse.createByErrorMessage("当前为未收货状态，不能申请结算");

            srcSplitDeliver.setDeliveryFee(splitDeliver.getDeliveryFee());
            srcSplitDeliver.setApplyMoney(splitDeliver.getApplyMoney());
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
            splitDeliver.setSendTime(new Date());
            splitDeliver.setShipState(1);//已发待收
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
            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            SplitDeliverDetailDTO detailDTO = new SplitDeliverDetailDTO();
            detailDTO.setNumber(splitDeliver.getNumber());
            detailDTO.setShipName(splitDeliver.getShipName());
            detailDTO.setShipAddress(splitDeliver.getShipAddress());
            detailDTO.setShipMobile(splitDeliver.getShipMobile());
            Member sup = memberMapper.selectByPrimaryKey(splitDeliver.getSupervisorId());//管家
            detailDTO.setSupMobile(sup.getMobile());
            detailDTO.setSupName(sup.getName());
            detailDTO.setMemo(splitDeliver.getMemo());
            detailDTO.setReason(splitDeliver.getReason());
            detailDTO.setTotalAmount(splitDeliver.getTotalAmount());


            Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, splitDeliverId);
            List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
            List<OrderSplitItemDTO> orderSplitItemDTOS = new ArrayList<>();
            for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                OrderSplitItemDTO orderSplitItemDTO = new OrderSplitItemDTO();
                orderSplitItemDTO.setProductName(orderSplitItem.getProductName());
                orderSplitItemDTO.setNum(orderSplitItem.getNum());
                orderSplitItemDTO.setCost(orderSplitItem.getCost());
                orderSplitItemDTO.setUnitName(orderSplitItem.getUnitName());
                orderSplitItemDTO.setTotalPrice(orderSplitItem.getCost() * orderSplitItem.getNum());//成本价 * 数量
                orderSplitItemDTO.setBrandName("品牌名");
                orderSplitItemDTOS.add(orderSplitItemDTO);
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
     * shipState  0待发货,1已发待收货,2已收货,3取消,4部分收
     */
    public ServerResponse splitDeliverList(String supplierId, int shipState) {
        Example example = new Example(SplitDeliver.class);
        example.createCriteria().andEqualTo(SplitDeliver.SUPPLIER_ID, supplierId).andEqualTo(SplitDeliver.SHIP_STATE, shipState);
        List<SplitDeliver> splitDeliverList = splitDeliverMapper.selectByExample(example);
        return ServerResponse.createBySuccess("查询成功", splitDeliverList);
    }


    /**
     * 发送供应商
     * 分发不同供应商
     */
    public ServerResponse sentSupplier(String orderSplitId, String splitItemList) {
        try {
            OrderSplit orderSplit = orderSplitMapper.selectByPrimaryKey(orderSplitId);
            House house = houseMapper.selectByPrimaryKey(orderSplit.getHouseId());
            Member supervisor = memberMapper.getSupervisor(house.getId());//管家
            Member member = memberMapper.selectByPrimaryKey(house.getMemberId());

            JSONArray arr = JSONArray.parseArray(splitItemList);
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String id = obj.getString("id");
                String supplierId = obj.getString("supplierId");
                OrderSplitItem orderSplitItem = orderSplitItemMapper.selectByPrimaryKey(id);

                Example example = new Example(SplitDeliver.class);
                example.createCriteria().andEqualTo(SplitDeliver.HOUSE_ID, orderSplit.getHouseId()).andEqualTo(SplitDeliver.SUPPLIER_ID, supplierId)
                        .andEqualTo(SplitDeliver.SHIP_STATE, 0).andEqualTo(SplitDeliver.ORDER_SPLIT_ID,orderSplitId);
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
                    splitDeliver.setSupervisorId(supervisor.getId());//管家id
                    splitDeliver.setSubmitTime(new Date());
                    splitDeliver.setSupState(0);
                    splitDeliver.setShipState(0);//待发货状态
                    splitDeliverMapper.insert(splitDeliver);
                }

                SupplierProduct supplierProduct = forMasterAPI.getSupplierProduct(house.getCityId(),supplierId,orderSplitItem.getProductId());
                orderSplitItem.setSupCost(supplierProduct.getPrice());//供应价
                orderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);

                splitDeliver.setTotalAmount(supplierProduct.getPrice() * orderSplitItem.getNum() + splitDeliver.getTotalAmount());//累计供应商价总价
                splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
                orderSplitItemMapper.setSupplierId(id, splitDeliver.getId());
            }
            orderSplit.setApplyStatus(2);//发给供应商
            orderSplitMapper.updateByPrimaryKeySelective(orderSplit);

            /*
             * 计算是否超过免费要货次数,收取工匠运费
             */
            Example example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, orderSplit.getHouseId()).andEqualTo(OrderSplit.WORKER_TYPE_ID, orderSplit.getWorkerTypeId());
            List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(orderSplit.getWorkerTypeId());
            if (orderSplitList.size() > workerType.getSafeState()){//超过免费次数收工匠运费
                //TODO 计算运费 暂无准确计算公式
                Double yunFei = 0.0;
                example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
                List<OrderSplitItem> osiList = orderSplitItemMapper.selectByExample(example);
                for(OrderSplitItem osi : osiList){
                    //Product product = forMasterAPI.getProduct(osi.getProductId());
                    yunFei += osi.getTotalPrice() * 0.1;
                }

                Member worker = memberMapper.selectByPrimaryKey(orderSplit.getSupervisorId());//要货人
                WorkerDetail workerDetail = new WorkerDetail();
                workerDetail.setName("要货运费");
                workerDetail.setWorkerId(worker.getId());
                workerDetail.setWorkerName(worker.getName());
                workerDetail.setHouseId(orderSplit.getHouseId());
                workerDetail.setMoney(new BigDecimal(yunFei));
                workerDetail.setState(7);//收取运费
                workerDetail.setWalletMoney(worker.getHaveMoney());
                workerDetail.setApplyMoney(new BigDecimal(yunFei));
                workerDetailMapper.insert(workerDetail);

                worker.setHaveMoney(worker.getHaveMoney().subtract(new BigDecimal(yunFei)));
                worker.setSurplusMoney(worker.getSurplusMoney().subtract(new BigDecimal(yunFei)));
                memberMapper.updateByPrimaryKeySelective(worker);
            }

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
            example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplitId);
            List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
            for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                Warehouse warehouse = warehouseMapper.getByProductId(orderSplitItem.getProductId(), orderSplit.getHouseId());
                warehouse.setAskCount(warehouse.getAskCount() - orderSplitItem.getNum());
                warehouseMapper.updateByPrimaryKeySelective(warehouse);
            }

            orderSplitMapper.cancelOrderSplit(orderSplitId);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 要货单看明细
     */
    public ServerResponse orderSplitItemList(String orderSplitId) {
        Example example = new Example(OrderSplitItem.class);
        example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplitId);
        List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
        for (OrderSplitItem v : orderSplitItemList) {
            v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
        }
        return ServerResponse.createBySuccess("查询成功", orderSplitItemList);
    }

    /**
     * 材料员看房子列表
     */
    public ServerResponse getHouseList(Integer pageNum, Integer pageSize) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<House> houseList = houseMapper.selectAll();
        PageInfo pageResult = new PageInfo(houseList);

        List<DeliverHouseDTO> deliverHouseDTOList = new ArrayList<DeliverHouseDTO>();
        for (House house : houseList) {
            Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
            DeliverHouseDTO deliverHouseDTO = new DeliverHouseDTO();
            deliverHouseDTO.setHouseId(house.getId());
            deliverHouseDTO.setCreateDate(house.getCreateDate());
            deliverHouseDTO.setHouseName(house.getHouseName());
            deliverHouseDTO.setName(member.getName() == null ? member.getNickName() : member.getName());
            deliverHouseDTO.setMobile(member.getMobile());

            Example example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, house.getId()).andGreaterThan(OrderSplit.APPLY_STATUS, 2);//已发货
            deliverHouseDTO.setSent(orderSplitMapper.selectCountByExample(example));
            example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, house.getId()).andGreaterThan(OrderSplit.APPLY_STATUS, 1);//申请中
            deliverHouseDTO.setWait(orderSplitMapper.selectCountByExample(example));
            deliverHouseDTOList.add(deliverHouseDTO);
        }
        pageResult.setList(deliverHouseDTOList);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /**
     * 根据房子id查询要货单列表
     */
    public ServerResponse getOrderSplitList(String houseId) {
        Example example = new Example(OrderSplit.class);
        example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andGreaterThan(OrderSplit.APPLY_STATUS, 0);//大于0
        List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
        return ServerResponse.createBySuccess("查询成功", orderSplitList);
    }
}
