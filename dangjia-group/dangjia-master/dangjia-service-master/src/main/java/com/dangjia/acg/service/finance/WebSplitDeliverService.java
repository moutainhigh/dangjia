package com.dangjia.acg.service.finance;

import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.SplitDeliverDTO;
import com.dangjia.acg.dto.deliver.WebOrderDTO;
import com.dangjia.acg.dto.finance.WebSplitDeliverDTO;
import com.dangjia.acg.mapper.activity.IActivityRedPackMapper;
import com.dangjia.acg.mapper.activity.IActivityRedPackRecordMapper;
import com.dangjia.acg.mapper.deliver.*;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.sup.Supplier;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ysl
 * Date: 2019/1/24 0008
 * Time: 16:48
 */
@Service
public class WebSplitDeliverService {
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private IBusinessOrderMapper iBusinessOrderMapper;
    @Autowired
    private ISplitDeliverMapper iSplitDeliverMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;


    /*所有供应商发货订单*/
    public ServerResponse getAllSplitDeliver(PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());

            List<WebSplitDeliverDTO> webSplitDeliverDTOList = new ArrayList<>();
            List<SplitDeliver> splitDeliverList = iSplitDeliverMapper.getAllSplitDeliver();

            for (SplitDeliver splitDeliver : splitDeliverList) {
                WebSplitDeliverDTO webSplitDeliverDTO = new WebSplitDeliverDTO();
                webSplitDeliverDTO.setCreateDate(splitDeliver.getCreateDate());
                webSplitDeliverDTO.setModifyDate(splitDeliver.getModifyDate());
                webSplitDeliverDTO.setSupName(splitDeliver.getShipName());
                Supplier supplier = forMasterAPI.getSupplier(splitDeliver.getSupplierId());
                webSplitDeliverDTO.setSupMobile(supplier.getTelephone());
                webSplitDeliverDTO.setSupName(supplier.getName());
                webSplitDeliverDTO.setTotalAmount(splitDeliver.getTotalAmount());

                webSplitDeliverDTOList.add(webSplitDeliverDTO);
            }

            PageInfo pageResult = new PageInfo(splitDeliverList);
            pageResult.setList(webSplitDeliverDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


}

