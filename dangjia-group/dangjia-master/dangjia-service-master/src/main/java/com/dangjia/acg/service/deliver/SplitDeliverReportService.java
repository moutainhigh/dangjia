package com.dangjia.acg.service.deliver;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.SplitReportDeliverOrderDTO;
import com.dangjia.acg.dto.deliver.SplitReportDeliverOrderItemDTO;
import com.dangjia.acg.dto.deliver.SplitReportSupplierDTO;
import com.dangjia.acg.mapper.delivery.IOrderSplitItemMapper;
import com.dangjia.acg.modle.storefront.Storefront;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * author: qyx
 * Date: 2019/04/09
 * Time: 16:31
 */
@Service
public class SplitDeliverReportService {

    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private RedisClient redisClient;

    public ServerResponse getSplitReportSuppliers(HttpServletRequest request, String houseId){
        String userID = request.getParameter(Constants.USERID);
        //通过缓存查询店铺信息
        Storefront storefront =redisClient.getCache(Constants.FENGJIAN_STOREFRONT+userID,Storefront.class);

        List<SplitReportSupplierDTO> splitDeliverDTOList=orderSplitItemMapper.getSplitReportSuppliers(houseId,storefront.getId());
        return ServerResponse.createBySuccess("查询成功", splitDeliverDTOList);
    }

    public ServerResponse getSplitReportDeliverOrders(String houseId,String supplierId){
        List<SplitReportDeliverOrderDTO>  splitReportDeliverOrderDTOS=orderSplitItemMapper.getSplitReportDeliverOrders(houseId,supplierId);
        return ServerResponse.createBySuccess("查询成功", splitReportDeliverOrderDTOS);
    }

    public ServerResponse getSplitReportDeliverOrderItems(String number){
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        List<SplitReportDeliverOrderItemDTO>  splitReportDeliverOrderItemDTOS=orderSplitItemMapper.getSplitReportDeliverOrderItems(number);
        for (SplitReportDeliverOrderItemDTO splitReportDeliverOrderItemDTO : splitReportDeliverOrderItemDTOS) {
            splitReportDeliverOrderItemDTO.setImage(address+splitReportDeliverOrderItemDTO.getImage());
        }
        return ServerResponse.createBySuccess("查询成功", splitReportDeliverOrderItemDTOS);
    }


    public ServerResponse getSplitReportGoodsSuppliers(String houseId,String productSn){
        List<SplitReportSupplierDTO> splitDeliverDTOList=orderSplitItemMapper.getSplitReportGoodsSuppliers(houseId,productSn);
        return ServerResponse.createBySuccess("查询成功", splitDeliverDTOList);
    }


    public ServerResponse getSplitReportGoodsOrderItems(PageDTO pageDTO,String houseId){
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<SplitReportDeliverOrderItemDTO>  splitReportDeliverOrderItemDTOS=orderSplitItemMapper.getSplitReportGoodsOrderItems(houseId);
        PageInfo pageResult = new PageInfo(splitReportDeliverOrderItemDTOS);
        for (SplitReportDeliverOrderItemDTO splitReportDeliverOrderItemDTO : splitReportDeliverOrderItemDTOS) {
            splitReportDeliverOrderItemDTO.setImage(address+splitReportDeliverOrderItemDTO.getImage());
        }
        pageResult.setList(splitReportDeliverOrderItemDTOS);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /*指定供应商所有发货的房子*/
    public ServerResponse getSplitReportHouse(String supplierId){
        List<SplitReportSupplierDTO> splitReportHouse = orderSplitItemMapper.getSplitReportHouse(supplierId);
        return ServerResponse.createBySuccess("查询成功", splitReportHouse);
    }



}
