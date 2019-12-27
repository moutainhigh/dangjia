package com.dangjia.acg.service.safe;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordProductMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeOrderMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecord;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecordProduct;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.safe.WorkerTypeSafe;
import com.dangjia.acg.modle.safe.WorkerTypeSafeOrder;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 11:48
 */
@Service
public class WorkerTypeSafeOrderService {
    @Autowired
    private IWorkerTypeSafeOrderMapper workerTypeSafeOrderMapper;
    @Autowired
    private IWorkerTypeSafeMapper workerTypeSafeMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;

    @Autowired
    private DjMaintenanceRecordMapper djMaintenanceRecordMapper;
    @Autowired
    private DjMaintenanceRecordProductMapper djMaintenanceRecordProductMapper;
    @Autowired
    private IMasterStorefrontProductMapper iMasterStorefrontProductMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private IWorkerTypeMapper iWorkerTypeMapper;
    /**
     * 切换保险
     */
    public ServerResponse changeSafeType(String userToken, String houseFlowId, String workerTypeSafeId, int selected) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        try {
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            Example example = new Example(WorkerTypeSafeOrder.class);
            example.createCriteria().andEqualTo(WorkerTypeSafeOrder.HOUSE_ID, houseFlow.getHouseId()).andEqualTo(WorkerTypeSafeOrder.WORKER_TYPE_ID, houseFlow.getWorkerTypeId());
            workerTypeSafeOrderMapper.deleteByExample(example);
            if (selected == 0) {//未勾选
                WorkerTypeSafe workerTypeSafe = workerTypeSafeMapper.selectByPrimaryKey(workerTypeSafeId);
                House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
                //生成工种保险服务订单
                WorkerTypeSafeOrder workerTypeSafeOrder = new WorkerTypeSafeOrder();
                workerTypeSafeOrder.setWorkerTypeSafeId(workerTypeSafeId); // 向保险订单中存入保险服务类型的id
                workerTypeSafeOrder.setHouseId(houseFlow.getHouseId()); // 存入房子id
                workerTypeSafeOrder.setWorkerTypeId(houseFlow.getWorkerTypeId()); // 工种id
                workerTypeSafeOrder.setWorkerType(houseFlow.getWorkerType());
                workerTypeSafeOrder.setPrice(workerTypeSafe.getPrice().multiply(house.getSquare()));
                workerTypeSafeOrder.setState(0);  //未支付
                workerTypeSafeOrderMapper.insert(workerTypeSafeOrder);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 我的质保卡
     */
    public ServerResponse queryMySafeTypeOrder(String userToken, String houseId, PageDTO pageDTO) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Example example = new Example(WorkerTypeSafeOrder.class);
        example.createCriteria()
                .andEqualTo(WorkerTypeSafeOrder.HOUSE_ID, houseId)
                .andIsNotNull(WorkerTypeSafeOrder.FORCE_TIME)
                .andEqualTo(WorkerTypeSafeOrder.DATA_STATUS, 0);
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<WorkerTypeSafeOrder> list = workerTypeSafeOrderMapper.selectByExample(example);
        List<Map> listMap = new ArrayList<>();
        PageInfo pageResult = new PageInfo(list);
        for (WorkerTypeSafeOrder wtso : list) {
            Map map = BeanUtils.beanToMap(wtso);
            WorkerTypeSafe wts = workerTypeSafeMapper.selectByPrimaryKey(wtso.getWorkerTypeSafeId());//获得类型算出时间
            map.put("workerTypeSafe", wts);
            listMap.add(map);
        }
        pageResult.setList(listMap);
        return ServerResponse.createBySuccess("ok", pageResult);
    }

    /*
     *我的质保卡明细
     */
    public ServerResponse getMySafeTypeOrderDetail(String id) {
        //1.查询质保明细
        WorkerTypeSafeOrder wtso = workerTypeSafeOrderMapper.selectByPrimaryKey(id);

       /* WorkerTypeSafe wts = workerTypeSafeMapper.selectByPrimaryKey(wtso.getWorkerTypeSafeId());//获得类型算出时间
        map.put("workerTypeSafe",wts);
        List<HouseFlowApplyImage> imglist=houseFlowApplyImageMapper.getHouseFlowApplyImageList(wtso.getWorkerTypeId(), String.valueOf(wtso.getWorkerType()), wtso.getHouseId(), wtso.getHouseFlowId(), "0");
        for (HouseFlowApplyImage msg:imglist) {
            msg.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
        }
        map.put("imglist",imglist);*/
       //判断是否过保
        if(wtso.getExpirationDate()!=null&& DateUtil.compareDate(new Date(),wtso.getExpirationDate())){
            wtso.setServiceState(2);//已过保
        }else{
            wtso.setServiceState(1);//未过保
        }

        Map map = BeanUtils.beanToMap(wtso);
        List maintenanceRecordList=new ArrayList();
        Map recordMap;
       //2.查询历史质保记录
        Example example=new Example(DjMaintenanceRecord.class);
        example.createCriteria().andEqualTo(DjMaintenanceRecord.WORKER_TYPE_SAFE_ORDER_ID,id);
        List<DjMaintenanceRecord> recordList=djMaintenanceRecordMapper.selectByExample(example);
        if(recordList!=null&&recordList.size()>0){
            for(DjMaintenanceRecord mr:recordList){
                recordMap=new HashMap();
                recordMap.put("applicationDate",mr.getCreateDate());//质保申请时间
                //2.1查询维保商品记录
                example=new Example(DjMaintenanceRecordProduct.class);
                example.createCriteria().andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_RECORD_ID,mr.getId());
                List<DjMaintenanceRecordProduct> mrProductList=djMaintenanceRecordProductMapper.selectByExample(example);
                List productList=getRecordProductList(mrProductList);
                recordMap.put("productList",productList);//商品列表
                //2.2查询人工信息
                recordMap.put("workerList",getWorkerList(mr));
                maintenanceRecordList.add(recordMap);
            }
        }
        map.put("historyRecordList",maintenanceRecordList);
        return ServerResponse.createBySuccess("查询成功", map);
    }

    /**
     * 获取对应处理人大管家和工匠
     * @param mr
     * @return
     */
    public List<Map<String,Object>> getWorkerList(DjMaintenanceRecord mr){
        List<Map<String,Object>> list=new ArrayList();
        if(mr.getStewardId()!=null&& StringUtils.isNotBlank(mr.getStewardId())){// 大管家信息
            list.add(getWokerMemberInfo(mr.getStewardId(),"大管家"));
        }
        if(mr.getWorkerMemberId()!=null&&StringUtils.isNotBlank(mr.getWorkerMemberId())){//工匠信息
            WorkerType workerType=iWorkerTypeMapper.selectByPrimaryKey(mr.getWorkerTypeId());
            list.add(getWokerMemberInfo(mr.getStewardId(),workerType.getName()));
        }
        return list;
    }

    /**
     * 获取对应的工匠信息
     * @param workerId
     * @return
     */
    Map<String,Object> getWokerMemberInfo(String workerId,String labelName){
        Map<String,Object> map=new HashMap<>();
        Member member=iMemberMapper.selectByPrimaryKey(workerId);
        if(member!=null&&StringUtils.isNotBlank(member.getId())){
            map.put("workerId",workerId);
            map.put("workerName",member.getName());
            map.put("labelName",labelName);
            map.put("headImage",member.getHead());
        }
        return map;
    }



    /**
     * 获取对应的图片和总价
     * @param mrProductList
     * @return
     */
    public List<Map<String,Object>> getRecordProductList(List<DjMaintenanceRecordProduct> mrProductList){
        List<Map<String,Object>> list=new ArrayList<>();
        if(mrProductList!=null&&mrProductList.size()>0){
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            for(DjMaintenanceRecordProduct mrp:mrProductList){
                StorefrontProduct storefrontProduct= iMasterStorefrontProductMapper.selectByPrimaryKey(mrp.getProductId());
                Map map=BeanUtils.beanToMap(mrp);
                if(mrp.getPrice()==null){
                    mrp.setPrice(0d);
                }
                if(mrp.getShopCount()==null){
                    mrp.setShopCount(0d);
                }
                map.put("totalPrice", MathUtil.mul(mrp.getPrice(),mrp.getShopCount()));
                if(storefrontProduct!=null){
                    map.put("productName",storefrontProduct.getProductName());
                    map.put("image",storefrontProduct.getImage());
                    map.put("imageUrl", StringTool.getImage(storefrontProduct.getImage(),address));
                }
                list.add(map);
            }
        }
        return list;
    }

}
