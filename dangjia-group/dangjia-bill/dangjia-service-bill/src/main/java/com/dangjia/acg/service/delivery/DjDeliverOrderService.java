package com.dangjia.acg.service.delivery;

import cn.jiguang.common.utils.StringUtils;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.delivery.DjDeliverOrderMapper;
import com.dangjia.acg.modle.delivery.DjDeliverOrder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class DjDeliverOrderService {
    private static Logger logger = LoggerFactory.getLogger(DjDeliverOrderService.class);
    @Autowired
    private DjDeliverOrderMapper djDeliverOrderMapper;

    /**
     * 查询所有订单
     *
     * @param pageDTO
     * @param userId
     * @param cityId
     * @return
     */
    public ServerResponse queryAllDeliverOrder(PageDTO pageDTO, String userId, String cityId, String orderStatus) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if (StringUtils.isEmpty(userId)) {
                return ServerResponse.createByErrorMessage("用户ID不能为空!");
            }
            if (StringUtils.isEmpty(cityId)) {
                return ServerResponse.createByErrorMessage("城市ID不能为空!");
            }
//            if (StringUtils.isEmpty(orderStatus)) {
////                return ServerResponse.createByErrorMessage("订单状态不能为空!");
////            }
            Example example = new Example(DjDeliverOrder.class);
            example.createCriteria()
                    .andEqualTo(DjDeliverOrder.CITY_ID,cityId)
                    .andEqualTo(DjDeliverOrder.ORDER_STATUS,orderStatus)
                    .andEqualTo(DjDeliverOrder.MEMBER_ID,userId)
                    .andEqualTo(DjDeliverOrder.STOREFONT_ID,userId);
            List<DjDeliverOrder> list = djDeliverOrderMapper.selectByExample(example);
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询所有订单", pageResult);
        } catch (Exception e) {
            logger.error("查询所有订单异常", e);
            return ServerResponse.createByErrorMessage("查询所有订单异常" + e);
        }
    }
}
