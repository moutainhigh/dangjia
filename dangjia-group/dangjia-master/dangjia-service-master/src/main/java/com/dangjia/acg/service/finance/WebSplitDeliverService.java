package com.dangjia.acg.service.finance;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.finance.WebSplitDeliverItemDTO;
import com.dangjia.acg.mapper.deliver.ISplitDeliverMapper;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * ysl
 * Date: 2019/1/24 0008
 * Time: 16:48
 */
@Service
public class WebSplitDeliverService {
    @Autowired
    private ISplitDeliverMapper iSplitDeliverMapper;

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
    public ServerResponse getAllSplitDeliver(PageDTO pageDTO, Integer applyState, String searchKey, String beginDate, String endDate) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if (applyState == null) {
                applyState = -1;
            }
            if (!CommonUtil.isEmpty(beginDate) && !CommonUtil.isEmpty(endDate) && (applyState == 0 || applyState == -1)) {
                applyState = -2;
            }
            if(beginDate!=null && beginDate!="" && endDate!=null && endDate!=""){
                if(beginDate.equals(endDate)){
                    beginDate=beginDate+" "+"00:00:00";
                    endDate=endDate+" "+"23:59:59";
                }
            }
            List<WebSplitDeliverItemDTO> webSplitDeliverItemDTOLists = iSplitDeliverMapper.getWebSplitDeliverList(applyState, searchKey, beginDate, endDate);
            Example example = new Example(SplitDeliver.class);
            //根据供应商id统计已处理未处理的数量
            for (WebSplitDeliverItemDTO webSplitDeliverItemDTOList : webSplitDeliverItemDTOLists) {
                //已处理数量
                example.createCriteria().andEqualTo(SplitDeliver.SUPPLIER_ID,webSplitDeliverItemDTOList.getSupplierId())
                        .andEqualTo(SplitDeliver.DATA_STATUS,0)
                        .andCondition("apply_state in(1,2)");
                webSplitDeliverItemDTOList.setSent(iSplitDeliverMapper.selectCountByExample(example));
                //待处理数量
                example = new Example(SplitDeliver.class);
                example.createCriteria().andEqualTo(SplitDeliver.SUPPLIER_ID,webSplitDeliverItemDTOList.getSupplierId())
                        .andEqualTo(SplitDeliver.APPLY_STATE,0)
                        .andEqualTo(SplitDeliver.DATA_STATUS,0);
                webSplitDeliverItemDTOList.setWait(iSplitDeliverMapper.selectCountByExample(example));
            }
            PageInfo pageResult = new PageInfo(webSplitDeliverItemDTOLists);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     *
     * 根据供应商id查询要货单列表
     * @param supplierId
     * @return
     */
    public ServerResponse getOrderSplitList(String supplierId){
        try {
            List<WebSplitDeliverItemDTO> orderSplitList = iSplitDeliverMapper.getOrderSplitList(supplierId);
            return ServerResponse.createBySuccess("查询成功",orderSplitList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 供应商端发货列表
     * @param splitDeliverId
     * @return
     */
    public ServerResponse splitDeliverList(String splitDeliverId){
        try {
            List<WebSplitDeliverItemDTO> webSplitDeliverItemDTOS = iSplitDeliverMapper.splitDeliverList(splitDeliverId);
            return ServerResponse.createBySuccess("查询成功",webSplitDeliverItemDTOS);
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


}

