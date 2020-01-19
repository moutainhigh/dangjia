package com.dangjia.acg.service.node;

import com.dangjia.acg.dto.refund.OrderProgressDTO;
import com.dangjia.acg.mapper.delivery.IMasterOrderProgressMapper;
import com.dangjia.acg.modle.order.OrderProgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NodeProgressService {
    @Autowired
    private IMasterOrderProgressMapper iMasterOrderProgressMapper;

    public List<OrderProgressDTO> queryProgressListByOrderId(String progressOrderId){

         return    iMasterOrderProgressMapper.queryProgressListByOrderId(progressOrderId);
    }

    /**
     * //添加进度信息
     *
     * @param orderId      订单ID
     * @param progressType 订单类型
     * @param nodeType     节点类型
     * @param nodeCode     节点编码
     * @param userId       用户id
     */
    public void updateOrderProgressInfo(String orderId, String progressType, String nodeType, String nodeCode, String userId) {
        OrderProgress orderProgress = new OrderProgress();
        orderProgress.setProgressOrderId(orderId);
        orderProgress.setProgressType(progressType);
        orderProgress.setNodeType(nodeType);
        orderProgress.setNodeCode(nodeCode);
        orderProgress.setCreateBy(userId);
        orderProgress.setUpdateBy(userId);
        orderProgress.setCreateDate(new Date());
        orderProgress.setModifyDate(new Date());
        iMasterOrderProgressMapper.insert(orderProgress);
    }
}
