package com.dangjia.acg.service.finance;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.OrderItemByDTO;
import com.dangjia.acg.dto.deliver.WebOrderDTO;
import com.dangjia.acg.mapper.activity.IActivityRedPackMapper;
import com.dangjia.acg.mapper.activity.IActivityRedPackRecordMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.repair.MendOrder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IMendOrderMapper mendOrderMapper;

    @Autowired
    private ConfigUtil configUtil;
    /**
     * 所有订单流水
     *
     * @param pageDTO   分页
     * @param state     处理状态 -1全部， 1刚生成(可编辑),2去支付(不修改),3已支付
     * @param searchKey 模糊搜索：订单号,房屋信息,电话,支付单号(业务订单号)
     * @return
     */
    public ServerResponse getAllOrders(PageDTO pageDTO, Integer state, String searchKey) {
        try {
            if (state == null) {
                state = -1;
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<WebOrderDTO> orderList = iBusinessOrderMapper.getWebOrderList(state, searchKey);
            PageInfo pageResult = new PageInfo(orderList);
            for (WebOrderDTO webOrderDTO : orderList) {
                if (webOrderDTO.getState() == 3) {
                    if (webOrderDTO.getType() == 1) {//工序支付
                        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(webOrderDTO.getTaskId());
                        if (houseFlow != null) {
                            webOrderDTO.setTypeText(workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId()).getName() + "抢单");
                        }
                    }
                    if (webOrderDTO.getType() == 2) {//补货补人工
                        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(webOrderDTO.getTaskId());
                        if (mendOrder != null) {
                            if (mendOrder.getType() == 0) {
                                webOrderDTO.setTypeText(workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId()).getName() + "补材料");
                            } else {
                                webOrderDTO.setTypeText(workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId()).getName() + "补人工");
                            }
                        }
                    }
                    if (webOrderDTO.getType() == 4) {//只付材料
                        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(webOrderDTO.getTaskId());
                        if (houseFlow != null) {
                            webOrderDTO.setTypeText(workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId()).getName() + "付材料");
                        }
                    }
                    if (webOrderDTO.getType() == 5) {
                        webOrderDTO.setTypeText("验房分销");
                    }
                }
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
        if(orderItemList!=null){
            return ServerResponse.createBySuccess("查询成功", pageResult);
        }else {
            return ServerResponse.createByErrorMessage("无数据");
        }

    }

}

