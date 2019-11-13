package com.dangjia.acg.service.finance;

import com.ctc.wstx.util.DataUtil;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.OrderItemByDTO;
import com.dangjia.acg.dto.deliver.WebOrderDTO;
import com.dangjia.acg.mapper.activity.IActivityRedPackMapper;
import com.dangjia.acg.mapper.activity.IActivityRedPackRecordMapper;
import com.dangjia.acg.mapper.activity.IActivityRedPackRuleMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.pay.IPayOrderMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import com.dangjia.acg.modle.activity.ActivityRedPackRule;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.pay.PayOrder;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.util.Utils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ysl
 * Date: 2019/1/24 0008
 * Time: 16:48
 */
@Service
public class WebOrderService {
    @Autowired
    private IBusinessOrderMapper iBusinessOrderMapper;
    @Autowired
    private IActivityRedPackRecordMapper iActivityRedPackRecordMapper;
    @Autowired
    private IActivityRedPackMapper iActivityRedPackMapper;

    @Autowired
    private IPayOrderMapper iPayOrderMapper;
    @Autowired
    private IStoreMapper iStoreMapper;


    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IMendOrderMapper mendOrderMapper;

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private IActivityRedPackRuleMapper activityRedPackRuleMapper;
    /**
     * 所有订单流水
     *
     * @param pageDTO   分页
     * @param state     处理状态 -1全部， 1刚生成(可编辑),2去支付(不修改),3已支付
     * @param searchKey 模糊搜索：订单号,房屋信息,电话,支付单号(业务订单号)
     * @return
     */
    public ServerResponse getAllOrders(PageDTO pageDTO,String cityId, Integer state, String searchKey,String storefrontId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            if (state == null) {
                state = -1;
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<WebOrderDTO> orderList = iBusinessOrderMapper.getWebOrderList(cityId,state, searchKey,storefrontId);
            PageInfo pageResult = new PageInfo(orderList);
            for (WebOrderDTO webOrderDTO : orderList) {
                if(!CommonUtil.isEmpty(webOrderDTO.getPayOrderNumber())) {
                    PayOrder payOrder= iPayOrderMapper.getByNumber(webOrderDTO.getPayOrderNumber());
                    if(payOrder!=null){
                        webOrderDTO.setCreateDate(payOrder.getCreateDate());
                        webOrderDTO.setPayType(Integer.parseInt(payOrder.getPayState()));
                    }
                }
                if(!CommonUtil.isEmpty(webOrderDTO.getStoreName())) {
                    Example example=new Example(Store.class);
                    example.createCriteria().andLike(Store.VILLAGES,"%" + webOrderDTO.getStoreName() + "%");
                    List<Store> stores = iStoreMapper.selectByExample(example);
                    if(stores.size()>0){
                        webOrderDTO.setStoreName(stores.size()>0?stores.get(0).getStoreName():null);
                    }
                }

                webOrderDTO.setImage(Utils.getImageAddress(address,webOrderDTO.getImage()));
                if (webOrderDTO.getState() == 3) {
                    if (webOrderDTO.getType() == 1) {//工序支付
                        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(webOrderDTO.getTaskId());
                        if (houseFlow != null) {
                            webOrderDTO.setTypeText(workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId()).getName() + "抢单");
                        }else{
                            webOrderDTO.setTypeText("工序抢单");
                        }
                    }
                    if (webOrderDTO.getType() == 2) {//补货补人工
                        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(webOrderDTO.getTaskId());
                        if (mendOrder != null) {
                            WorkerType workerType=workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId());
                            if (mendOrder.getType() == 0) {
                                if(workerType.getType()==3){
                                    webOrderDTO.setTypeText(workerType.getName() + "补包工包料");
                                }else{
                                    webOrderDTO.setTypeText(workerType.getName() + "补材料");
                                }

                            } else {
                                webOrderDTO.setTypeText(workerType.getName() + "补人工");
                            }
                        }
                    }
                    if (webOrderDTO.getType() == 4) {//只付材料
                        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(webOrderDTO.getTaskId());
                        if (houseFlow != null) {
                            webOrderDTO.setTypeText(workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId()).getName() + "付材料");
                        }else{
                            webOrderDTO.setTypeText("工序付材料");
                        }
                    }
                    if (webOrderDTO.getType() == 5) {
                        webOrderDTO.setTypeText("验房分销");
                    }
                    // 换货单
                    if (webOrderDTO.getType() == 6) {
                        webOrderDTO.setTypeText("换货补差价");
                    } // 换货单
                    if (webOrderDTO.getType() == 7) {
                        webOrderDTO.setTypeText("设计/精算改图");
                    }
                    if (webOrderDTO.getType() == 8) {
                        webOrderDTO.setTypeText("业主购买");
                    }
                    if (webOrderDTO.getType() == 9) {
                        webOrderDTO.setTypeText("工人保险");
                    }
                }
                //优惠卷
                ActivityRedPackRecord activityRedPackRecord = iActivityRedPackRecordMapper.getRedPackRecordsByBusinessOrderNumber(webOrderDTO.getOrderId());
                if (activityRedPackRecord != null) {
                    ActivityRedPack activityRedPack = iActivityRedPackMapper.selectByPrimaryKey(activityRedPackRecord.getRedPackId());
                    webOrderDTO.setRedPackName(activityRedPack.getName());
                }
            }
            pageResult.setList(orderList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 获取订单详情
     * @param businessNumber
     * @return
     */
    public ServerResponse getOrderItem(PageDTO pageDTO,String businessNumber){
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<OrderItemByDTO> orderItemList= iBusinessOrderMapper.getOrderItem(businessNumber);
        PageInfo pageResult = new PageInfo(orderItemList);
        for (OrderItemByDTO orderItemByDTO : orderItemList) {
            orderItemByDTO.setImage(imageAddress+orderItemByDTO.getImage());
        }
        pageResult.setList(orderItemList);
        return ServerResponse.createBySuccess("查询成功", pageResult);

    }
    /**
     * 获取订单优惠券详情
     * @param businessId
     * @return
     */
    public ServerResponse getOrderRedItem(String businessId){
        BusinessOrder businessOrder = iBusinessOrderMapper.selectByPrimaryKey(businessId);
        ActivityRedPackRecord activityRedPackRecord = iActivityRedPackRecordMapper.getRedPackRecordsByBusinessOrderNumber(businessOrder.getNumber());

        String red;
        if(activityRedPackRecord!=null) {
            ActivityRedPack activityRedPack = iActivityRedPackMapper.selectByPrimaryKey(activityRedPackRecord.getRedPackId());
            ActivityRedPackRule activityRedPackRule = activityRedPackRuleMapper.selectByPrimaryKey(activityRedPackRecord.getRedPackRuleId());
            //0为减免金额券 1 为折扣券 2代金券'
            if (activityRedPack.getType() == 0) {
                red = "满" + activityRedPackRule.getSatisfyMoney().stripTrailingZeros().toPlainString() + "减" + activityRedPackRule.getMoney().stripTrailingZeros().toPlainString();
            } else if (activityRedPack.getType() == 1) {
                red = "折扣券" + activityRedPackRule.getMoney().stripTrailingZeros().toPlainString() + "折";
            } else {
                red = "代金券" + activityRedPackRule.getMoney().stripTrailingZeros().toPlainString() + "元";
            }
        }else {
            red="无";
        }
        Map map=new HashMap<>();
        map.put("red",red);
        map.put("redPackAmount",businessOrder.getDiscountsPrice());
        return ServerResponse.createBySuccess("查询成功", map);

    }
    /**
     * 查询到时业主未审核申请
     */
    public void autoOrderCancel(){
        BusinessOrder businessOrder=new BusinessOrder();
        businessOrder.setId(null);
        businessOrder.setCreateDate(null);
        businessOrder.setState(4);
        Example example = new Example(BusinessOrder.class);
        example.createCriteria().andEqualTo(BusinessOrder.STATE, 1);
        iBusinessOrderMapper.updateByExampleSelective(businessOrder,example);
    }
}

