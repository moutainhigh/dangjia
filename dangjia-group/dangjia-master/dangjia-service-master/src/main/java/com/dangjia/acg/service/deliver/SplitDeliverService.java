package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.api.supplier.DjSupplierAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.excel.ExportExcel;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.SplitDeliverDTO;
import com.dangjia.acg.dto.deliver.SplitDeliverItemDTO;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.delivery.IOrderItemMapper;
import com.dangjia.acg.mapper.delivery.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.delivery.ISplitDeliverMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/5 0005
 * Time: 20:31
 */
@Service
public class SplitDeliverService {

    @Autowired
    private ISplitDeliverMapper splitDeliverMapper;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private IWarehouseMapper warehouseMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private DjSupplierAPI djSupplierAPI ;

    @Autowired
    private IOrderItemMapper orderItemMapper;
    /**
     * 部分收货
     */
    public ServerResponse partSplitDeliver(String userToken, String splitDeliverId, String image, String splitItemList) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member operator = (Member) object;
            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            splitDeliver.setOperatorId(operator.getId());
            splitDeliver.setShippingState(4);//部分收货
            splitDeliver.setImage(image);//收货图片
            splitDeliver.setRecTime(new Date());
            double applyMoney = 0d;
            JSONArray arr = JSONArray.parseArray(splitItemList);
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String id = obj.getString("id");
                Double receive = Double.parseDouble(obj.getString("receive"));//本次收货数量
                OrderSplitItem orderSplitItem = orderSplitItemMapper.selectByPrimaryKey(id);
                orderSplitItem.setReceive(receive);//本次收货数量
                orderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);
                /*统计收货数量*/
                Warehouse warehouse = warehouseMapper.getByProductId(orderSplitItem.getProductId(), splitDeliver.getHouseId());
                warehouse.setReceive(warehouse.getReceive() + receive);
                //部分收货则未收货的商品数量要退回到业主仓库中
                //未收货的数量
                Double noReceive =warehouse.getAskCount()-(orderSplitItem.getNum()-receive);
                warehouse.setAskCount(noReceive);
                warehouseMapper.updateByPrimaryKeySelective(warehouse);
                applyMoney += orderSplitItem.getSupCost() * orderSplitItem.getReceive();
            }
            splitDeliver.setApplyMoney(applyMoney);
            splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            House house = houseMapper.selectByPrimaryKey(splitDeliver.getHouseId());
            //业主
            configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "装修材料部分收货", String.format
                    (DjConstants.PushMessage.YZ_S_001, house.getHouseName()), "");
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     *  确认安装
     * @param userToken
     * @param splitDeliverId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse confirmInstallation( String userToken,String splitDeliverId) {

        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member operator = (Member) object;

            if(StringUtil.isEmpty(splitDeliverId))
            {
                return ServerResponse.createByErrorMessage("发货单ID不能为空");
            }
            //发货单
            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            String orderSplitId=null;//要货单明细表Id
            if(splitDeliver==null)
            {
                return ServerResponse.createByErrorMessage("不存在发货单信息");
            }
            else
            {
                splitDeliver.setShippingState(8);//配送状态（0待发货,1已发待收货,2已收货,3取消,4部分收,5已结算,6材料员撤回(只待发货才能撤回),7待安装，8已完成））
                splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);

                orderSplitId=splitDeliver.getOrderSplitId();
                OrderSplitItem  orderSplitItem=orderSplitItemMapper.selectByPrimaryKey(orderSplitId); //要货单
                String orderItemId=null;
                if(orderSplitItem!=null)
                {
                     orderItemId=orderSplitItem.getOrderItemId();
                }
                String args[]=orderItemId.split(",");
                for(String str:args)
                {
                    OrderItem orderItem =orderItemMapper.selectByPrimaryKey(str);
                    orderItem.setOrderStatus("4");//订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭,8待安装
                    orderItemMapper.updateByPrimaryKeySelective(orderItem);
                }
                //订单明细
                return ServerResponse.createBySuccessMessage("确认安装成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }

    }

    /**
     * 确认收货
     */
    public ServerResponse affirmSplitDeliver(String userToken, String splitDeliverId, String image) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member operator = (Member) object;
            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            splitDeliver.setShippingState(2);//收货
            splitDeliver.setApplyState(0);
            splitDeliver.setOperatorId(operator.getId());
            splitDeliver.setImage(image);//收货图片
            splitDeliver.setRecTime(new Date());
            splitDeliver.setModifyDate(new Date());//收货时间
            orderSplitItemMapper.affirmSplitDeliver(splitDeliverId);
            double applyMoney = 0d;
            /*统计收货数量*/
            Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, splitDeliverId);
            List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
            for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                Warehouse warehouse = warehouseMapper.getByProductId(orderSplitItem.getProductId(), splitDeliver.getHouseId());
                warehouse.setReceive(warehouse.getReceive() + orderSplitItem.getNum());
                warehouseMapper.updateByPrimaryKeySelective(warehouse);
                applyMoney += orderSplitItem.getSupCost() * orderSplitItem.getReceive();

            }
            splitDeliver.setApplyMoney(applyMoney);
            splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            House house = houseMapper.selectByPrimaryKey(splitDeliver.getHouseId());
            //业主
            configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "装修材料已收货", String.format
                    (DjConstants.PushMessage.YZ_S_001, house.getHouseName()), "");
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 委托大管家收货
     */
    public ServerResponse supState(String splitDeliverId) {
        try {
            splitDeliverMapper.supState(splitDeliverId);
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
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            SplitDeliverDTO splitDeliverDTO = new SplitDeliverDTO();
            splitDeliverDTO.setShipState(splitDeliver.getShippingState());//发货状态
            splitDeliverDTO.setNumber(splitDeliver.getNumber());
            splitDeliverDTO.setCreateDate(splitDeliver.getCreateDate());
            splitDeliverDTO.setSendTime(splitDeliver.getSendTime());
            splitDeliverDTO.setSubmitTime(splitDeliver.getSubmitTime());
            splitDeliverDTO.setRecTime(splitDeliver.getRecTime());//收货时间
            splitDeliverDTO.setTotalAmount(splitDeliver.getTotalAmount());
            splitDeliverDTO.setSupState(splitDeliver.getSupState());//大管家收货状态
            splitDeliverDTO.setSupName(splitDeliver.getSupplierName());
            splitDeliverDTO.setSupId(splitDeliver.getSupervisorId());
            splitDeliverDTO.setSupMobile(splitDeliver.getShipMobile());
            if (StringUtil.isNotEmpty(splitDeliver.getOperatorId())) {//收货人名
                Member operator = memberMapper.selectByPrimaryKey(splitDeliver.getOperatorId());
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(operator.getWorkerTypeId());
                if (workerType != null) {
                    splitDeliverDTO.setOperatorName(workerType.getName() + "-" + (operator.getName() == null ? operator.getNickName() : operator.getName()));
                } else {
                    splitDeliverDTO.setOperatorName("业主-" + (operator.getName() == null ? operator.getNickName() : operator.getName()));
                }
            }

            if (!CommonUtil.isEmpty(splitDeliver.getImage())) {
                List<String> imageList = new ArrayList<>();
                String[] imageArr = splitDeliver.getImage().split(",");
                for (String anImageArr : imageArr) {
                    String image = address + anImageArr;
                    imageList.add(image);
                }
                splitDeliverDTO.setImageList(imageList);
            }
            Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, splitDeliver.getId());
            example.orderBy(OrderSplitItem.CATEGORY_ID).desc();
            List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
            List<SplitDeliverItemDTO> splitDeliverItemDTOList = new ArrayList<>();
            House house = houseMapper.selectByPrimaryKey(splitDeliver.getHouseId());
            double sumprice = 0d;
            for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                if (orderSplitItem.getReceive() == null) {
                    orderSplitItem.setReceive(0D);
                }
                DjBasicsProductTemplate product = forMasterAPI.getProduct(house.getCityId(), orderSplitItem.getProductId());
                SplitDeliverItemDTO splitDeliverItemDTO = new SplitDeliverItemDTO();
                splitDeliverItemDTO.setImage(address + product.getImage());
                splitDeliverItemDTO.setProductName(product.getName());
                if (splitDeliver.getShippingState() == 2 || splitDeliver.getShippingState() == 4 || splitDeliver.getShippingState() == 5) {
                    splitDeliverItemDTO.setTotalPrice(orderSplitItem.getPrice() * orderSplitItem.getReceive());
                    sumprice += orderSplitItem.getPrice() * orderSplitItem.getReceive();
                } else {
                    splitDeliverItemDTO.setTotalPrice(orderSplitItem.getPrice() * orderSplitItem.getNum());
                    sumprice += orderSplitItem.getPrice() * orderSplitItem.getNum();
                }
                splitDeliverItemDTO.setShopCount(orderSplitItem.getShopCount());
                splitDeliverItemDTO.setNum(orderSplitItem.getNum());
                splitDeliverItemDTO.setUnitName(orderSplitItem.getUnitName());
                //splitDeliverItemDTO.setBrandSeriesName(forMasterAPI.brandSeriesName(house.getCityId(), orderSplitItem.getProductId()));
                splitDeliverItemDTO.setPrice(orderSplitItem.getPrice());
                splitDeliverItemDTO.setCost(orderSplitItem.getCost());
                splitDeliverItemDTO.setId(orderSplitItem.getId());
                splitDeliverItemDTO.setReceive(orderSplitItem.getReceive());//收货数量
                splitDeliverItemDTO.setHouseName(house.getHouseName());
                splitDeliverItemDTO.setSupCost(orderSplitItem.getSupCost());
                splitDeliverItemDTO.setSupCostTotal(orderSplitItem.getReceive()*orderSplitItem.getSupCost());
                splitDeliverItemDTO.setAskCount(orderSplitItem.getAskCount());
                splitDeliverItemDTOList.add(splitDeliverItemDTO);
            }
            splitDeliverDTO.setTotalAmount(sumprice);//金额总计
            splitDeliverDTO.setSplitDeliverItemDTOList(splitDeliverItemDTOList);//明细
            return ServerResponse.createBySuccess("查询成功", splitDeliverDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    /**
     * 发货单明细(excel导出 )
     */
    public ServerResponse exportDeliverDetail(HttpServletResponse response,Integer deliverType, String splitDeliverId) {
        try {
            ExportExcel exportExcel = new ExportExcel();//创建表格实例
            //预留退货单明细导出（暂不做）
//            if(deliverType==1) {
                SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
                Example example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, splitDeliver.getId());
                example.orderBy(OrderSplitItem.CATEGORY_ID).desc();
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
                List<SplitDeliverItemDTO> splitDeliverItemDTOList = new ArrayList<>();
                House house = houseMapper.selectByPrimaryKey(splitDeliver.getHouseId());
                for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                    if (orderSplitItem.getReceive() == null) {
                        orderSplitItem.setReceive(0D);
                    }
                    SplitDeliverItemDTO splitDeliverItemDTO = new SplitDeliverItemDTO();
                    splitDeliverItemDTO.setShopCount(orderSplitItem.getShopCount());
                    splitDeliverItemDTO.setProductName(orderSplitItem.getProductName());
                    splitDeliverItemDTO.setNum(orderSplitItem.getNum());
                    splitDeliverItemDTO.setUnitName(orderSplitItem.getUnitName());
                    splitDeliverItemDTO.setPrice(orderSplitItem.getPrice());
                    splitDeliverItemDTO.setCost(orderSplitItem.getCost());
                    splitDeliverItemDTO.setId(orderSplitItem.getId());
                    splitDeliverItemDTO.setReceive(orderSplitItem.getReceive());//收货数量
                    splitDeliverItemDTO.setHouseName(house.getHouseName());
                    splitDeliverItemDTO.setSupplierName(splitDeliver.getSupplierName());
                    splitDeliverItemDTO.setSupCost(orderSplitItem.getSupCost());
                    splitDeliverItemDTO.setSupCostTotal(orderSplitItem.getReceive() * orderSplitItem.getSupCost());
                    splitDeliverItemDTO.setAskCount(orderSplitItem.getAskCount());
                    splitDeliverItemDTOList.add(splitDeliverItemDTO);
                }
                exportExcel.setDataList("结算单-详情", SplitDeliverItemDTO.class, splitDeliverItemDTOList);
                exportExcel.write(response, splitDeliver.getNumber() + ".xlsx");
//            }
            return ServerResponse.createByErrorMessage("导出成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("导出失败");
        }
    }
    /**
     * 收货列表
     * shipState  0待发货,1已发待收货,2已收货,3取消  5所有
     */
    public ServerResponse splitDeliverList(String houseId, int shipState) {
        try {
            Example example = new Example(SplitDeliver.class);
            if (shipState == 5) {
                example.createCriteria().andEqualTo(SplitDeliver.ORDER_SPLIT_ID, houseId);//中台用
            } else if (shipState == 2) {
                example.createCriteria().andEqualTo(SplitDeliver.HOUSE_ID, houseId).andCondition(" shipping_state in(2,4) ");
            } else {
                example.createCriteria().andEqualTo(SplitDeliver.HOUSE_ID, houseId).andEqualTo(SplitDeliver.SHIPPING_STATE, shipState);
            }
            example.orderBy(SplitDeliver.CREATE_DATE).desc();
            List<SplitDeliver> splitDeliverList = splitDeliverMapper.selectByExample(example);
            List<SplitDeliverDTO> splitDeliverDTOList = new ArrayList<>();
            for (SplitDeliver splitDeliver : splitDeliverList) {
                House house = houseMapper.selectByPrimaryKey(splitDeliver.getHouseId());
                SplitDeliverDTO splitDeliverDTO = new SplitDeliverDTO();
                splitDeliverDTO.setSplitDeliverId(splitDeliver.getId());
                splitDeliverDTO.setCreateDate(splitDeliver.getCreateDate());
                splitDeliverDTO.setShipState(splitDeliver.getShippingState());
                splitDeliverDTO.setNumber(splitDeliver.getNumber());
                splitDeliverDTO.setSendTime(splitDeliver.getSendTime());//发货时间
                splitDeliverDTO.setRecTime(splitDeliver.getRecTime());//收货时间
                //Supplier supplier = forMasterAPI.getSupplier(house.getCityId(), splitDeliver.getSupplierId());
                DjSupplier djSupplier =djSupplierAPI.queryDjSupplierByPass(splitDeliver.getSupplierId());
                if (djSupplier != null) {
                    splitDeliverDTO.setSupId(djSupplier.getId());//供应商id
                    splitDeliverDTO.setSupMobile(djSupplier.getTelephone());
                    splitDeliverDTO.setSupName(djSupplier.getName());
                }
                splitDeliverDTO.setTotalAmount(splitDeliver.getTotalAmount());
                example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, splitDeliver.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
                splitDeliverDTO.setTol(orderSplitItemList.size());//几种
                if (orderSplitItemList.size() > 0) {
                    splitDeliverDTO.setTotalPrice(0d);
                    for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                        splitDeliverDTO.setTotalPrice(splitDeliverDTO.getTotalPrice() + orderSplitItem.getTotalPrice());
                    }
                    splitDeliverDTO.setName(orderSplitItemList.get(0).getProductName());
                    splitDeliverDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + orderSplitItemList.get(0).getImage());
                }
                splitDeliverDTOList.add(splitDeliverDTO);
            }

            return ServerResponse.createBySuccess("查询成功", splitDeliverDTOList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
