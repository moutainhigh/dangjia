package com.dangjia.acg.service.finance;

import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dto.finance.WebSplitDeliverDTO;
import com.dangjia.acg.dto.finance.WebSplitDeliverItemDTO;
import com.dangjia.acg.mapper.deliver.ISplitDeliverMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.sup.Supplier;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private ISplitDeliverMapper iSplitDeliverMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    private static Logger LOG = LoggerFactory.getLogger(WebSplitDeliverService.class);

    /*所有供应商发货订单*/
    public ServerResponse getAllSplitDeliver(PageDTO pageDTO, Integer applyState, String beginDate, String endDate) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());

            LOG.info(" getAllSplitDeliver applyState:" + applyState + " " + beginDate + " " + endDate);
//            List<WebSplitDeliverDTO> webSplitDeliverDTOList = new ArrayList<>();

            List<SplitDeliver> splitDeliverList = iSplitDeliverMapper.getAllSplitDeliver(applyState, beginDate, endDate);
//            List<SplitDeliver> splitDeliverList = iSplitDeliverMapper.getAllSplitDeliver(null, null, null);
            LOG.info(" getAllWithdraw splitDeliverList:" + splitDeliverList);

            String strBeginDate = DateUtil.dateToString(DateUtil.getMondayOfThisWeek(), "yyyy-MM-dd HH:mm:ss");
            String strEndDate = DateUtil.dateToString(DateUtil.getSundayOfThisWeek(), "yyyy-MM-dd HH:mm:ss");
//            LOG.info("本周一 ：" + strBeginDate + "本周日 ：" + strEndDate);
            //applyState 供应商申请结算的状态 0申请中(待处理)；1不通过(驳回)；2通过(同意)
            List<SplitDeliver> curWeekAddList = iSplitDeliverMapper.getAllCurWeek(-1, strBeginDate, strEndDate);
//            webSplitDeliverDTO.setCurWeekAddNum(curWeekAddList.size());//本周新增

            List<SplitDeliver> curWeekSuccessList = iSplitDeliverMapper.getAllCurWeek(2, strBeginDate, strEndDate);
//            webSplitDeliverDTO.setCurWeekSuccessNum(curWeekSuccessList.size());//本周 成功处理的

            List<SplitDeliver> curWeekNoHandleList = iSplitDeliverMapper.getAllCurWeek(0, strBeginDate, strEndDate);
//            webSplitDeliverDTO.setCurWeekNoHandleNum(curWeekNoHandleList.size());//本周 待处理的

            List<SplitDeliver> allNoHandleList = iSplitDeliverMapper.getAllCurWeek(0, null, null);
//            webSplitDeliverDTO.setAllNoHandleNum(allNoHandleList.size());//所有待处理的

            LOG.info("本周一 ：" + curWeekAddList.size() + " " + curWeekSuccessList.size() +
                    " " + curWeekNoHandleList.size() + " " + allNoHandleList.size());

            LOG.info("splitDeliverList size:" + splitDeliverList.size());
            WebSplitDeliverDTO webSplitDeliverDTO = new WebSplitDeliverDTO();
            List<WebSplitDeliverItemDTO> webSplitDeliverItemDTOLists = new ArrayList<>();
            for (SplitDeliver splitDeliver : splitDeliverList) {
                WebSplitDeliverItemDTO webSplitDeliverItemDTO = new WebSplitDeliverItemDTO();
                webSplitDeliverItemDTO.setModifyDate(splitDeliver.getModifyDate());
                webSplitDeliverItemDTO.setNumber(splitDeliver.getNumber());
                webSplitDeliverItemDTO.setApplyMoney(splitDeliver.getApplyMoney());
                webSplitDeliverItemDTO.setApplyState(splitDeliver.getApplyState());
                webSplitDeliverItemDTO.setShipAddress(splitDeliver.getShipAddress());
                webSplitDeliverItemDTO.setSplitDeliverId(splitDeliver.getId());
                Supplier supplier = forMasterAPI.getSupplier(splitDeliver.getSupplierId());
                webSplitDeliverItemDTO.setSupMobile(supplier.getTelephone());
                webSplitDeliverItemDTO.setSupplierId(splitDeliver.getSupplierId());
                webSplitDeliverItemDTO.setSupName(supplier.getName());
                webSplitDeliverItemDTO.setTotalAmount(splitDeliver.getTotalAmount());
                webSplitDeliverItemDTO.setCreateDate(splitDeliver.getCreateDate());
                webSplitDeliverItemDTO.setCurWeekAddNum(curWeekAddList.size());//本周新增
                webSplitDeliverItemDTO.setCurWeekSuccessNum(curWeekSuccessList.size());//本周 成功处理的
                webSplitDeliverItemDTO.setCurWeekNoHandleNum(curWeekNoHandleList.size());//本周 待处理的
                webSplitDeliverItemDTO.setAllNoHandleNum(allNoHandleList.size());//所有待处理的

                webSplitDeliverItemDTOLists.add(webSplitDeliverItemDTO);
            }
            webSplitDeliverDTO.setWebSplitDeliverItemDTOLists(webSplitDeliverItemDTOLists);

            PageInfo pageResult = new PageInfo(splitDeliverList);
            pageResult.setList(webSplitDeliverDTO.getWebSplitDeliverItemDTOLists());
            return ServerResponse.createBySuccess("查询成功", pageResult);
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
            LOG.info("setSplitDeliver :" + splitDeliver);
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

                iSplitDeliverMapper.updateByPrimaryKey(srcSplitDeliver);
            }

            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


}

