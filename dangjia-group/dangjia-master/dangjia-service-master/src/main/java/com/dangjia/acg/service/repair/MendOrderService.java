package com.dangjia.acg.service.repair;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.repair.IMendWorkerMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.repair.MendWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/** 补退货管理
 * author: zmj
 * Date: 2018/11/8 0008
 * Time: 11:48
 */
@Service
public class MendOrderService {
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IMendMaterialMapper mendMaterialMapper;
    @Autowired
    private IMendWorkerMapper mendWorkerMapper;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;


    /**
     * 确认退人工
     */
    public ServerResponse confirmBackMendWorker(String houseId){
        try{
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 3)
                    .andEqualTo(MendOrder.WORKER_BACK_STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0){
                return ServerResponse.createBySuccessMessage("没有生成中退人工单");
            }else if (mendOrderList.size() > 1){
                return ServerResponse.createByErrorMessage("生成多个未提交退人工单,异常联系平台部");
            }else {
                MendOrder mendOrder = mendOrderList.get(0);
                mendOrder.setWorkerBackState(1);//工匠审核中
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                return ServerResponse.createBySuccessMessage("操作成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 已添加退人工单明细
     */
    public ServerResponse backMendWorkerList(String houseId){
        try{
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 3)//退人工
                    .andEqualTo(MendOrder.WORKER_BACK_STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0){
                return ServerResponse.createBySuccessMessage("没有生成中退人工单");
            }else if (mendOrderList.size() > 1){
                return ServerResponse.createByErrorMessage("生成多个未提交退人工单,异常联系平台部");
            }else {
                MendOrder mendOrder = mendOrderList.get(0);
                /*限制金额不能退多了*/
                HouseWorkerOrder houseWorkerOrder = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseId,mendOrder.getWorkerTypeId());
                if (houseWorkerOrder != null){
                    BigDecimal totalAmount = new BigDecimal(mendOrder.getTotalAmount());//退的钱
                    BigDecimal remain = houseWorkerOrder.getWorkPrice().add(houseWorkerOrder.getRepairPrice()).subtract(houseWorkerOrder.getHaveMoney());//剩下的
                    if(remain.compareTo(totalAmount) < 0){
                        return ServerResponse.createByErrorMessage("工钱退超过剩余,退多了");
                    }
                }

                List<MendWorker> mendWorkerList = mendWorkerMapper.byMendOrderId(mendOrder.getId());
                for (MendWorker v : mendWorkerList){
                    v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                }
                return ServerResponse.createBySuccess("查询成功", mendWorkerList);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 提交退人工
     */
    public ServerResponse backMendWorker(String userToken,String houseId, String workerGoodsArr,String workerTypeId){
        try{
            AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member steward = accessToken.getMember();//管家

            HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseId,workerTypeId);
            if(houseFlow.getWorkSteta() == 1 || houseFlow.getWorkSteta() ==2){
                return ServerResponse.createByErrorMessage("该工种已阶段完工,不能退人工!");
            }

            List<MendOrder> mendOrderList = mendOrderMapper.backWorker(houseId);
            if(mendOrderList.size() > 0){
                return ServerResponse.createByErrorMessage("有未处理退人工单");
            }
            List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.unCheckByWorkerTypeId(houseId,workerTypeId);
            if (houseFlowApplyList.size() > 0){
                return ServerResponse.createByErrorMessage("该工种有未处理完工申请");
            }

            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 3)
                    .andEqualTo(MendOrder.WORKER_BACK_STATE, 0);
            mendOrderList = mendOrderMapper.selectByExample(example);

            MendOrder mendOrder;
            if (mendOrderList.size() > 0){
                mendOrder = mendOrderList.get(0);
                mendOrder.setWorkerTypeId(workerTypeId);
                mendOrder.setOrderName("退人工");
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                /*删除之前子项*/
                example = new Example(MendWorker.class);
                example.createCriteria().andEqualTo(MendWorker.MEND_ORDER_ID, mendOrder.getId());
                mendWorkerMapper.deleteByExample(example);
            }else {
                mendOrder = new MendOrder();
                mendOrder.setNumber(DateUtil.dateToString(mendOrder.getModifyDate(),"yyyy-MM-dd HH:mm:ss"));//订单号
                mendOrder.setHouseId(houseId);
                mendOrder.setApplyMemberId(steward.getId());
                mendOrder.setType(3);//退人工
                mendOrder.setOrderName("退人工");
                mendOrder.setWorkerTypeId(workerTypeId);
                mendOrder.setWorkerBackState(0);//工匠审核中
                mendOrder.setTotalAmount(0.0);
                mendOrderMapper.insert(mendOrder);
            }

            if (this.addMendWorker(workerGoodsArr,mendOrder,workerTypeId)){
                return  ServerResponse.createBySuccessMessage("保存成功");
            }else {
                return  ServerResponse.createByErrorMessage("添加明细失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return  ServerResponse.createByErrorMessage("保存失败");
        }
    }

    /**
     * 确认补人工
     */
    public ServerResponse confirmMendWorker(String houseId){
        try{
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 1)
                    .andEqualTo(MendOrder.WORKER_ORDER_STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0){
                return ServerResponse.createBySuccessMessage("没有生成中补人工单");
            }else if (mendOrderList.size() > 1){
                return ServerResponse.createByErrorMessage("生成多个未提交补人工单,异常联系平台部");
            }else {
                MendOrder mendOrder = mendOrderList.get(0);
                mendOrder.setWorkerOrderState(1);//工匠审核中
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                return ServerResponse.createBySuccessMessage("操作成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 已添加补人工单明细
     */
    public ServerResponse getMendWorkerList(String houseId){
        try{
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 1)//补人工
                    .andEqualTo(MendOrder.WORKER_ORDER_STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0){
                return ServerResponse.createBySuccessMessage("没有生成中补人工单");
            }else if (mendOrderList.size() > 1){
                return ServerResponse.createByErrorMessage("生成多个未提交补人工单,异常联系平台部");
            }else {
                MendOrder mendOrder = mendOrderList.get(0);
                List<MendWorker> mendWorkerList = mendWorkerMapper.byMendOrderId(mendOrder.getId());
                for (MendWorker v : mendWorkerList){
                    v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                }
                return ServerResponse.createBySuccess("查询成功", mendWorkerList);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 提交补人工
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveMendWorker(String userToken,String houseId, String workerGoodsArr,String workerTypeId){
        try{
            AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member steward = accessToken.getMember();//管家

            List<MendOrder> mendOrderList = mendOrderMapper.untreatedWorker(houseId);//补人工 1 3 5都查限制提交
            if (mendOrderList.size() > 0) {
                return ServerResponse.createByErrorMessage("有未处理补人工单");
            }
            List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.unCheckByWorkerTypeId(houseId,workerTypeId);
            if (houseFlowApplyList.size() > 0){
                return ServerResponse.createByErrorMessage("该工种有未处理完工申请");
            }

            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 1)
                    .andEqualTo(MendOrder.WORKER_ORDER_STATE, 0);
            mendOrderList = mendOrderMapper.selectByExample(example);
            MendOrder mendOrder;
            if (mendOrderList.size() > 0){
                mendOrder = mendOrderList.get(0);
                mendOrder.setWorkerTypeId(workerTypeId);
                mendOrder.setOrderName("补人工");
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                /*删除之前子项*/
                example = new Example(MendWorker.class);
                example.createCriteria().andEqualTo(MendWorker.MEND_ORDER_ID, mendOrder.getId());
                mendWorkerMapper.deleteByExample(example);
            }else {
                mendOrder = new MendOrder();
                mendOrder.setNumber(DateUtil.dateToString(mendOrder.getModifyDate(),"yyyy-MM-dd HH:mm:ss"));//订单号
                mendOrder.setHouseId(houseId);
                mendOrder.setApplyMemberId(steward.getId());
                mendOrder.setType(1);//补人工
                mendOrder.setOrderName("补人工");
                mendOrder.setWorkerTypeId(workerTypeId);
                mendOrder.setWorkerOrderState(0);//工匠审核中
                mendOrder.setTotalAmount(0.0);
                mendOrderMapper.insert(mendOrder);
            }

            if (this.addMendWorker(workerGoodsArr,mendOrder,workerTypeId)){
                return  ServerResponse.createBySuccessMessage("保存成功");
            }else {
                return  ServerResponse.createByErrorMessage("添加明细失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return  ServerResponse.createByErrorMessage("保存失败");
        }
    }
    /**保存补退人工明细mendWorker*/
    private boolean addMendWorker(String workerGoodsArr, MendOrder mendOrder,String workerTypeId){
        try{
            JSONArray jsonArray = JSONArray.parseArray(workerGoodsArr);
            for(int i=0; i<jsonArray.size(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                MendWorker mendWorker = new MendWorker();//补退人工
                String workerGoodsId = obj.getString("workerGoodsId");
                double num = Double.parseDouble(obj.getString("num"));
                WorkerGoods workerGoods = forMasterAPI.getWorkerGoods(workerGoodsId);
                if(!workerGoods.getWorkerTypeId().equals(workerTypeId)){
                    System.out.println("所选人工商品与所选工种不符");
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return false;
                }
                mendWorker.setMendOrderId(mendOrder.getId());
                mendWorker.setWorkerGoodsId(workerGoodsId);
                mendWorker.setWorkerGoodsName(workerGoods.getName());
                mendWorker.setWorkerGoodsSn(workerGoods.getWorkerGoodsSn());
                mendWorker.setUnitName(workerGoods.getUnitName());
                mendWorker.setPrice(workerGoods.getPrice());
                mendWorker.setImage(workerGoods.getImage());
                mendWorker.setShopCount(num);
                mendWorker.setTotalPrice(num * workerGoods.getPrice());
                mendOrder.setTotalAmount(mendOrder.getTotalAmount() + mendWorker.getTotalPrice());
                mendWorkerMapper.insertSelective(mendWorker);
            }
            mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 确认退货
     */
    public ServerResponse confirmBackMendMaterial(String houseId){
        try{
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 2)
                    .andEqualTo(MendOrder.MATERIAL_BACK_STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0){
                return ServerResponse.createBySuccessMessage("没有生成中退货单");
            }else if (mendOrderList.size() > 1){
                return ServerResponse.createByErrorMessage("生成多个未提交退货单,异常联系平台部");
            }else {
                MendOrder mendOrder = mendOrderList.get(0);
                mendOrder.setMaterialBackState(1);//平台审核
                mendOrder.setModifyDate(new Date());//更新退货
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                return ServerResponse.createBySuccessMessage("操作成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 已添加退货单明细
     */
    public ServerResponse backMendMaterialList(String houseId){
        try{
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 2)
                    .andEqualTo(MendOrder.MATERIAL_BACK_STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0){
                return ServerResponse.createBySuccessMessage("没有生成中退货单");
            }else if (mendOrderList.size() > 1){
                return ServerResponse.createByErrorMessage("生成多个未提交退货单,异常联系平台部");
            }else {
                List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrderList.get(0).getId());
                for (MendMateriel mendMateriel : mendMaterielList){
                    mendMateriel.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                }
                return ServerResponse.createBySuccess("查询成功", mendMaterielList);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 提交退货(退材料)
     */
    public ServerResponse backMendMaterial(String userToken,String houseId,String productArr){
        try{
            AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member steward = accessToken.getMember();//管家

            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 2)//退材料
                    .andLessThan(MendOrder.MATERIAL_BACK_STATE, 2);//小于2 包括审核中状态
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            MendOrder mendOrder;
            if (mendOrderList.size() > 0){
                mendOrder = mendOrderList.get(0);
                /*删除之前子项*/
                example = new Example(MendMateriel.class);
                example.createCriteria().andEqualTo(MendMateriel.MEND_ORDER_ID, mendOrder.getId());
                mendMaterialMapper.deleteByExample(example);
            }else {
                mendOrder = new MendOrder();
                mendOrder.setNumber(DateUtil.dateToString(mendOrder.getModifyDate(),"yyyy-MM-dd HH:mm:ss"));//订单号
                mendOrder.setHouseId(houseId);
                mendOrder.setApplyMemberId(steward.getId());
                mendOrder.setType(2);//退材料
                mendOrder.setMaterialBackState(0);//生成中
                mendOrder.setTotalAmount(0.0);
                mendOrderMapper.insert(mendOrder);
            }

            if (this.addMendMateriel(productArr,mendOrder)){
                return  ServerResponse.createBySuccessMessage("保存成功");
            }else {
                return  ServerResponse.createByErrorMessage("添加明细失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return  ServerResponse.createByErrorMessage("保存失败");
        }
    }

    /**
     * 确认补货
     */
    public ServerResponse confirmMendMaterial(String houseId){
        try{
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 0)
                    .andEqualTo(MendOrder.MATERIAL_ORDER_STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0){
                return ServerResponse.createBySuccessMessage("没有生成中补货单");
            }else if (mendOrderList.size() > 1){
                return ServerResponse.createByErrorMessage("生成多个未提交补货单,异常联系平台部");
            }else {
                MendOrder mendOrder = mendOrderList.get(0);
                mendOrder.setMaterialOrderState(1);//平台审核
                mendOrder.setModifyDate(new Date());
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                return ServerResponse.createBySuccessMessage("操作成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 返回已添加补材料单明细
     */
    public ServerResponse getMendMaterialList(String houseId){
        try{
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 0)
                    .andEqualTo(MendOrder.MATERIAL_ORDER_STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0){
                return ServerResponse.createBySuccessMessage("没有生成中补货单");
            }else if (mendOrderList.size() > 1){
                return ServerResponse.createByErrorMessage("生成多个未提交补货单,异常联系平台部");
            }else {
                List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrderList.get(0).getId());
                for (MendMateriel v : mendMaterielList){
                    v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                }
                return ServerResponse.createBySuccess("查询成功", mendMaterielList);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 提交补材料
     */
    public ServerResponse saveMendMaterial(String userToken,String houseId,String productArr){
        try{
            AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member steward = accessToken.getMember();//管家

            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 0)
                    .andLessThan(MendOrder.MATERIAL_ORDER_STATE, 2);//小于2 包括审核中状态
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            MendOrder mendOrder;
            if (mendOrderList.size() > 0){
                mendOrder = mendOrderList.get(0);
                /*删除之前子项*/
                example = new Example(MendMateriel.class);
                example.createCriteria().andEqualTo(MendMateriel.MEND_ORDER_ID, mendOrder.getId());
                mendMaterialMapper.deleteByExample(example);
            }else {
                mendOrder = new MendOrder();
                mendOrder.setNumber(DateUtil.dateToString(mendOrder.getModifyDate(),"yyyy-MM-dd HH:mm:ss"));//订单号
                mendOrder.setHouseId(houseId);
                mendOrder.setApplyMemberId(steward.getId());
                mendOrder.setType(0);//补材料
                mendOrder.setMaterialOrderState(0);//生成中
                mendOrder.setTotalAmount(0.0);
                mendOrderMapper.insert(mendOrder);
            }

            if (this.addMendMateriel(productArr,mendOrder)){
                return  ServerResponse.createBySuccessMessage("保存成功");
            }else {
                return  ServerResponse.createByErrorMessage("添加明细失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return  ServerResponse.createByErrorMessage("保存失败");
        }
    }

    /**保存mendMateriel*/
    private boolean addMendMateriel(String productArr, MendOrder mendOrder){
        try{
            JSONArray jsonArray = JSONArray.parseArray(productArr);
            for(int i=0; i<jsonArray.size(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                MendMateriel mendMateriel = new MendMateriel();//补退材料明细
                String productId = obj.getString("productId");
                double num = Double.parseDouble(obj.getString("num"));
                Product product = forMasterAPI.getProduct(productId);
                mendMateriel.setMendOrderId(mendOrder.getId());
                mendMateriel.setProductId(productId);
                mendMateriel.setProductSn(product.getProductSn());
                mendMateriel.setProductName(product.getName());
                mendMateriel.setPrice(product.getPrice());
                mendMateriel.setCost(product.getCost());
                mendMateriel.setUnitName(product.getUnitName());
                mendMateriel.setShopCount(num);
                mendMateriel.setTotalPrice(num * product.getPrice());
                mendOrder.setTotalAmount(mendOrder.getTotalAmount() + mendMateriel.getTotalPrice());//修改总价
                mendMateriel.setProductType(forMasterAPI.getGoods(product.getGoodsId()).getType());//0：材料；1：服务
                mendMateriel.setCategoryId(product.getCategoryId());
                mendMateriel.setImage(product.getImage());
                mendMaterialMapper.insertSelective(mendMateriel);
            }
            mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
