package com.dangjia.acg.service.finance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.deliver.SupplierDeliverDTO;
import com.dangjia.acg.dto.finance.WebSplitDeliverItemDTO;
import com.dangjia.acg.dto.receipt.ReceiptDTO;
import com.dangjia.acg.mapper.deliver.ISplitDeliverMapper;
import com.dangjia.acg.mapper.receipt.IReceiptMapper;
import com.dangjia.acg.mapper.repair.IMendDeliverMapper;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.receipt.Receipt;
import com.dangjia.acg.modle.repair.MendDeliver;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

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
    private IMendDeliverMapper iMendDeliverMapper;
    @Autowired
    private IReceiptMapper iReceiptMapper;

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

            //根据供应商id统计已处理未处理的数量
            for (WebSplitDeliverItemDTO webSplitDeliverItemDTOList : webSplitDeliverItemDTOLists) {
                //已处理数量
                Example example = new Example(SplitDeliver.class);
                example.createCriteria().andEqualTo(SplitDeliver.SUPPLIER_ID,webSplitDeliverItemDTOList.getSupplierId())
                        .andEqualTo(SplitDeliver.DATA_STATUS,0)
                        .andCondition("apply_state in(1,2)");
//                int sent=iSplitDeliverMapper.selectCountByExample(example);
//                //退货已处理数量
//                example=new Example(MendDeliver.class);
//                example.createCriteria().andEqualTo(MendDeliver.DATA_STATUS,0)
//                        .andEqualTo(MendDeliver.SHIPPING_STATE,2);
//                sent+=iMendDeliverMapper.selectCountByExample(example);
                example=new Example(Receipt.class);
                example.createCriteria().andEqualTo(Receipt.SUPPLIER_ID,webSplitDeliverItemDTOList.getSupplierId());
                int sent=iReceiptMapper.selectCountByExample(example);
                webSplitDeliverItemDTOList.setSent(sent);
                //待处理数量
                example = new Example(SplitDeliver.class);
                example.createCriteria().andEqualTo(SplitDeliver.SUPPLIER_ID,webSplitDeliverItemDTOList.getSupplierId())
                        .andEqualTo(SplitDeliver.APPLY_STATE,0)
                        .andEqualTo(SplitDeliver.DATA_STATUS,0)
                        .andCondition(" shipping_state IN (2,4)");
                int wait=iSplitDeliverMapper.selectCountByExample(example);
                //退货待处理数量
                example=new Example(MendDeliver.class);
                example.createCriteria().andEqualTo(MendDeliver.DATA_STATUS,0)
                        .andEqualTo(MendDeliver.APPLY_STATE,0)
                        .andEqualTo(MendDeliver.SUPPLIER_ID,webSplitDeliverItemDTOList.getSupplierId())
                        .andCondition(" shipping_state=1");
                wait+=iMendDeliverMapper.selectCountByExample(example);
                webSplitDeliverItemDTOList.setWait(wait);
            }
            PageInfo pageResult = new PageInfo(webSplitDeliverItemDTOLists);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 根据供应商id查询要货单列表/模糊查询要货单列表
     * @param pageDTO
     * @param supplierId
     * @param searchKey
     * @param beginDate
     * @param endDate
     * @return
     */
    public ServerResponse getOrderSplitList(PageDTO pageDTO,String supplierId,String searchKey,String beginDate,String endDate){
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if(beginDate!=null && beginDate!="" && endDate!=null && endDate!=""){
                if(beginDate.equals(endDate)){
                    beginDate=beginDate+" "+"00:00:00";
                    endDate=endDate+" "+"23:59:59";
                }
            }
            List<WebSplitDeliverItemDTO> orderSplitList = iSplitDeliverMapper.getOrderSplitList(supplierId,searchKey,beginDate,endDate);
            PageInfo pageResult=new PageInfo(orderSplitList);
            return ServerResponse.createBySuccess("查询成功",pageResult);
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


    /**
     * 供应商查看货单列表
     * @param supplierId
     * @param shipAddress
     * @param beginDate
     * @param endDate
     * @return
     */
    public ServerResponse mendDeliverList(String supplierId, String shipAddress, String beginDate, String endDate,Integer applyState){
        try {
            if(beginDate!=null && beginDate!="" && endDate!=null && endDate!=""){
                if(beginDate.equals(endDate)){
                    beginDate=beginDate+" "+"00:00:00";
                    endDate=endDate+" "+"23:59:59";
                }
            }
            List<SupplierDeliverDTO> supplierDeliverDTOS = iSplitDeliverMapper.mendDeliverList(supplierId, shipAddress, beginDate, endDate,applyState);
            for (SupplierDeliverDTO supplierDeliverDTO : supplierDeliverDTOS) {
                supplierDeliverDTO.setDeliverType(1);
            }
            List<SupplierDeliverDTO> supplierDeliverDTOS1 = iMendDeliverMapper.mendDeliverList(supplierId, shipAddress, beginDate, endDate,applyState);
            for (SupplierDeliverDTO supplierDeliverDTO : supplierDeliverDTOS1) {
                supplierDeliverDTO.setDeliverType(2);
            }
            supplierDeliverDTOS.addAll(supplierDeliverDTOS1);
            return ServerResponse.createBySuccess("查询成功",supplierDeliverDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 供应商结算
     * @param image 图片
     * @param merge 表的id和类型 1发货单；2退货单
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse Settlemen(String image,String merge,String supplierId) throws RuntimeException{
        try {
            if(StringUtils.isNotEmpty(merge)){
                JSONArray itemObjArr = JSON.parseArray(merge);
                double splitDeliverPrice=0d;
                double mendDeliverPrice=0d;
                for (int i = 0; i < itemObjArr.size(); i++) {
                    JSONObject jsonObject = itemObjArr.getJSONObject(i);
                    String id=jsonObject.getString("id");
                    int  deliverType=jsonObject.getInteger("deliverType");
                    if(deliverType==1){
                        //发货单结算通过
                        SplitDeliver splitDeliver=new SplitDeliver();
                        splitDeliver.setId(id);
                        splitDeliver.setApplyState(2);
                        this.setSplitDeliver(splitDeliver);
                        splitDeliverPrice+=iSplitDeliverMapper.selectByPrimaryKey(id).getTotalAmount();
                    }else if(deliverType==2){
                        //退货单结算通过
                        MendDeliver mendDeliver=new MendDeliver();
                        mendDeliver.setId(id);
                        mendDeliver.setApplyState(2);
                        iMendDeliverMapper.updateByPrimaryKeySelective(mendDeliver);
                        mendDeliverPrice+=iMendDeliverMapper.selectByPrimaryKey(id).getTotalAmount();
                    }
                }
                //添加回执
                Receipt receipt=new Receipt();
                receipt.setImage(image);
                receipt.setMerge(merge);
                receipt.setCreateDate(new Date());
                receipt.setSupplierId(supplierId);
                receipt.setTotalAmount(splitDeliverPrice-mendDeliverPrice);
                iReceiptMapper.insert(receipt);
            }
            return ServerResponse.createBySuccess("结算成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("结算失败");
        }
    }


    /**
     * 已结算货单列表
     * @param shipAddress
     * @param beginDate
     * @param endDate
     * @return
     */
    public ServerResponse ClsdMendDeliverList(String shipAddress, String beginDate, String endDate,String supplierId){
        try {
            if(beginDate!=null && beginDate!="" && endDate!=null && endDate!=""){
                if(beginDate.equals(endDate)){
                    beginDate=beginDate+" "+"00:00:00";
                    endDate=endDate+" "+"23:59:59";
                }
            }
            Example example=new Example(Receipt.class);
            example.createCriteria().andEqualTo(Receipt.SUPPLIER_ID,supplierId);
            List<Receipt> receipts = iReceiptMapper.selectByExample(example);
            System.out.println(receipts);
            List<ReceiptDTO> list=new ArrayList();
            for (Receipt receipt : receipts) {
                List<SupplierDeliverDTO> supplierDeliverDTOList=new ArrayList<>();
                double amount=0D;
                double sd=0D;
                double md=0D;
                JSONArray itemObjArr = JSON.parseArray(receipt.getMerge());
                ReceiptDTO receiptDTO=new ReceiptDTO();
                for (int i = 0; i < itemObjArr.size(); i++) {
                    SupplierDeliverDTO supplierDeliverDTO=new SupplierDeliverDTO();
                    JSONObject jsonObject = itemObjArr.getJSONObject(i);
                    String id=jsonObject.getString("id");
                    int  deliverType=jsonObject.getInteger("deliverType");
                    if(deliverType==1){
                        SplitDeliver splitDeliver = iSplitDeliverMapper.selectClsd(id,shipAddress,beginDate,endDate);
                        if(null!=splitDeliver) {
                            supplierDeliverDTO.setId(splitDeliver.getId());
                            supplierDeliverDTO.setNumber(splitDeliver.getNumber());
                            supplierDeliverDTO.setShipAddress(splitDeliver.getShipAddress());
                            supplierDeliverDTO.setTotalAmount(splitDeliver.getTotalAmount());
                            supplierDeliverDTO.setDeliverType(1);
                            sd += splitDeliver.getTotalAmount();
                        }
                    }else if(deliverType==2){
                        MendDeliver mendDeliver = iMendDeliverMapper.selectClsd(id,shipAddress,beginDate,endDate);
                        if(null!=mendDeliver) {
                            supplierDeliverDTO.setId(mendDeliver.getId());
                            supplierDeliverDTO.setNumber(mendDeliver.getNumber());
                            supplierDeliverDTO.setShipAddress(mendDeliver.getShipAddress());
                            supplierDeliverDTO.setTotalAmount(mendDeliver.getTotalAmount());
                            supplierDeliverDTO.setDeliverType(2);
                            md += mendDeliver.getTotalAmount();
                        }
                    }
                    supplierDeliverDTOList.add(supplierDeliverDTO);
                }
                //结算金额
                amount = sd - md;
                receiptDTO.setAmount(amount);
                receiptDTO.setList(supplierDeliverDTOList);
                receiptDTO.setCreateDate(receipt.getCreateDate());
                receiptDTO.setId(receipt.getId());
                list.add(receiptDTO);
            }
            return ServerResponse.createBySuccess("查询成功",list);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查看回执
     * @param id
     * @return
     */
    public ServerResponse selectReceipt(String id){
        try {
            Receipt receipt = iReceiptMapper.selectByPrimaryKey(id);
            JSONArray itemObjArr = JSON.parseArray(receipt.getImage());
            return ServerResponse.createBySuccess("查询成功",itemObjArr);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 退货单查看详情
     * @param id
     * @return
     */
    public ServerResponse mendDeliverDetail(String id){
        try {
            List<MendMateriel> mendMateriels = iMendDeliverMapper.mendDeliverDetail(id);
            return  ServerResponse.createBySuccess("查询成功",mendMateriels);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}

