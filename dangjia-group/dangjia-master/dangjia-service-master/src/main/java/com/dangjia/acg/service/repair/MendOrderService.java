package com.dangjia.acg.service.repair;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.repair.MendOrderInfoDTO;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.ISurplusWareHouseMapper;
import com.dangjia.acg.mapper.repair.*;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.SurplusWareHouse;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.*;
import com.dangjia.acg.service.config.ConfigMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 补退货管理
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
    @Autowired
    private IMendTypeRoleMapper mendTypeRoleMapper;
    @Autowired
    private IMendOrderCheckMapper mendOrderCheckMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private ConfigMessageService configMessageService;

    @Autowired
    private ISurplusWareHouseMapper iSurplusWareHouseMapper;
    @Autowired
    private IChangeOrderMapper changeOrderMapper;


    /**
     * 补材料明细
     * workerTypeId 即 mendOrderId
     */
    public MendOrderInfoDTO getMendDetail(String workerTypeId, String type) {
        MendOrderInfoDTO mendOrderInfoDTO = new MendOrderInfoDTO();
        mendOrderInfoDTO.setTotalAmount(0.0);
        try {
            if("0".equals(type)){
                List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(workerTypeId);
                for (MendMateriel v : mendMaterielList) {
                    v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                    mendOrderInfoDTO.setTotalAmount(mendOrderInfoDTO.getTotalAmount() + v.getTotalPrice());
                }
                mendOrderInfoDTO.setMendMateriels(mendMaterielList);
            }else if("1".equals(type)){
                List<MendWorker> mendWorkerList = mendWorkerMapper.byMendOrderId(workerTypeId);
                for (MendWorker v : mendWorkerList) {
                    v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                    mendOrderInfoDTO.setTotalAmount(mendOrderInfoDTO.getTotalAmount() + v.getTotalPrice());
                }
                mendOrderInfoDTO.setMendWorkers(mendWorkerList);
            }
            return mendOrderInfoDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return mendOrderInfoDTO;
        }
    }

    /**
     * 业主确认退货
     */
    public ServerResponse confirmLandlordState(String houseId) {
        try {
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 4)
                    .andEqualTo(MendOrder.STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0) {
                return ServerResponse.createBySuccessMessage("没有退货单");
            } else if (mendOrderList.size() > 1) {
                return ServerResponse.createByErrorMessage("生成多个退货单,异常联系平台部");
            } else {
                MendOrder mendOrder = mendOrderList.get(0);
                mendOrder.setState(1);//平台审核
                mendOrder.setModifyDate(new Date());//更新时间
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                return ServerResponse.createBySuccessMessage("操作成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 业主已添加退货单明细
     */
    public ServerResponse landlordBackDetail(String houseId) {
        try {
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 4)
                    .andEqualTo(MendOrder.STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0) {
                return ServerResponse.createBySuccessMessage("未生成退货单");
            } else if (mendOrderList.size() > 1) {
                return ServerResponse.createByErrorMessage("生成多个退货单,异常联系平台部");
            } else {
                List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrderList.get(0).getId());
                for (MendMateriel mendMateriel : mendMaterielList) {
                    mendMateriel.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                }
                return ServerResponse.createBySuccess("查询成功", mendMaterielList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 业主退材料
     */
    public ServerResponse landlordBack(String userToken, String houseId, String productArr) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member member = accessToken.getMember();//业主

            House house = houseMapper.selectByPrimaryKey(houseId);
            if (house.getVisitState() == 3 || house.getHaveComplete() == 1){
                return ServerResponse.createByErrorMessage("该房子已完工");
            }

            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 4)//业主退材料
                    .andLessThan(MendOrder.STATE, 2);//小于2 包括审核中状态
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            MendOrder mendOrder;
            if (mendOrderList.size() > 0) {
                mendOrder = mendOrderList.get(0);
                mendOrder.setState(0);//生成中
                /*删除之前子项*/
                example = new Example(MendMateriel.class);
                example.createCriteria().andEqualTo(MendMateriel.MEND_ORDER_ID, mendOrder.getId());
                mendMaterialMapper.deleteByExample(example);
            } else {
                example = new Example(MendOrder.class);
                mendOrder = new MendOrder();
                mendOrder.setNumber("DJZX" + 40000 + mendOrderMapper.selectCountByExample(example));//订单号
                mendOrder.setHouseId(houseId);
                mendOrder.setApplyMemberId(member.getId());
                mendOrder.setType(4);//业主退材料
                mendOrder.setOrderName("业主退材料");
                mendOrder.setState(0);//生成中
                mendOrder.setTotalAmount(0.0);

                if (!this.createMendCheck(mendOrder)) {
                    return ServerResponse.createByErrorMessage("添加审核流程失败");
                }
                mendOrderMapper.insert(mendOrder);
            }

            if (this.addMendMateriel(productArr, mendOrder)) {
                return ServerResponse.createBySuccessMessage("保存成功");
            } else {
                return ServerResponse.createByErrorMessage("添加明细失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("保存失败");
        }
    }

    /**
     * 确认退人工
     */
    public ServerResponse confirmBackMendWorker(String houseId, String workerTypeId) {
        try {
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 3)
                    .andEqualTo(MendOrder.WORKER_TYPE_ID, workerTypeId)
                    .andEqualTo(MendOrder.STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0) {
                return ServerResponse.createBySuccessMessage("没有生成中退人工单");
            } else if (mendOrderList.size() > 1) {
                return ServerResponse.createByErrorMessage("生成多个未提交退人工单,异常联系平台部");
            } else {
                MendOrder mendOrder = mendOrderList.get(0);
                mendOrder.setState(1);
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);

                ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(mendOrder.getChangeOrderId());
                changeOrder.setState(2);//通过->工匠业主审核
                changeOrderMapper.updateByPrimaryKeySelective(changeOrder);

                House house = houseMapper.selectByPrimaryKey(houseId);
                configMessageService.addConfigMessage(null, "gj", house.getMemberId(), "0", "退人工变更", String.format
                        (DjConstants.PushMessage.CRAFTSMAN_T_WORK, house.getHouseName()), "");
                return ServerResponse.createBySuccessMessage("操作成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 已添加退人工单明细
     */
    public ServerResponse backMendWorkerList(String houseId, String workerTypeId) {
        try {
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 3)//退人工
                    .andEqualTo(MendOrder.WORKER_TYPE_ID, workerTypeId)
                    .andEqualTo(MendOrder.STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0) {
                return ServerResponse.createBySuccessMessage("没有生成中退人工单");
            } else if (mendOrderList.size() > 1) {
                return ServerResponse.createByErrorMessage("生成多个未提交退人工单,异常联系平台部");
            } else {
                MendOrder mendOrder = mendOrderList.get(0);
                /*限制金额不能退多了*/
                HouseWorkerOrder houseWorkerOrder = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseId, mendOrder.getWorkerTypeId());
                if (houseWorkerOrder != null) {
                    BigDecimal totalAmount = new BigDecimal(mendOrder.getTotalAmount());//退的钱
                    BigDecimal remain = houseWorkerOrder.getWorkPrice().add(houseWorkerOrder.getRepairPrice()).subtract(houseWorkerOrder.getHaveMoney());//剩下的
                    if (remain.compareTo(totalAmount) < 0) {
                        return ServerResponse.createByErrorMessage("工钱退超过剩余,退多了");
                    }
                }

                List<MendWorker> mendWorkerList = mendWorkerMapper.byMendOrderId(mendOrder.getId());
                for (MendWorker v : mendWorkerList) {
                    v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                }
                return ServerResponse.createBySuccess("查询成功", mendWorkerList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 提交退人工
     */
    public ServerResponse backMendWorker(String userToken, String houseId, String workerGoodsArr, String workerTypeId, String changeOrderId) {
        try {
            if(StringUtil.isEmpty(changeOrderId)){
                return ServerResponse.createByErrorMessage("未传变更单id");
            }

            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member steward = accessToken.getMember();//管家

            HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseId, workerTypeId);
            if (houseFlow.getWorkSteta() == 1 || houseFlow.getWorkSteta() == 2) {
                return ServerResponse.createByErrorMessage("该工种已阶段完工,不能退人工!");
            }

            List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.unCheckByWorkerTypeId(houseId, workerTypeId);
            if (houseFlowApplyList.size() > 0) {
                return ServerResponse.createByErrorMessage("该工种有未处理完工申请");
            }

            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 3)
                    .andEqualTo(MendOrder.WORKER_TYPE_ID, workerTypeId)
                    .andEqualTo(MendOrder.STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);

            MendOrder mendOrder;
            if (mendOrderList.size() > 0) {
                mendOrder = mendOrderList.get(0);
                mendOrder.setChangeOrderId(changeOrderId);
                mendOrder.setWorkerTypeId(workerTypeId);
                mendOrder.setOrderName("退人工");
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                /*删除之前子项*/
                example = new Example(MendWorker.class);
                example.createCriteria().andEqualTo(MendWorker.MEND_ORDER_ID, mendOrder.getId());
                mendWorkerMapper.deleteByExample(example);
            } else {
                example = new Example(MendOrder.class);
                mendOrder = new MendOrder();
                mendOrder.setChangeOrderId(changeOrderId);
                mendOrder.setNumber("DJZX" + 30000 + mendOrderMapper.selectCountByExample(example));//订单号
                mendOrder.setHouseId(houseId);
                mendOrder.setApplyMemberId(steward.getId());
                mendOrder.setType(3);//退人工
                mendOrder.setOrderName("退人工");
                mendOrder.setWorkerTypeId(workerTypeId);
                mendOrder.setState(0);
                mendOrder.setTotalAmount(0.0);

                if (!this.createMendCheck(mendOrder)) {
                    return ServerResponse.createByErrorMessage("添加审核流程失败");
                }
                mendOrderMapper.insert(mendOrder);
            }

            if (this.addMendWorker(workerGoodsArr, mendOrder, workerTypeId)) {
                return ServerResponse.createBySuccessMessage("保存成功");
            } else {
                return ServerResponse.createByErrorMessage("添加明细失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("保存失败");
        }
    }

    /**
     * 管家
     * 确认补人工
     */
    public ServerResponse confirmMendWorker(String houseId, String workerTypeId) {
        try {
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 1)
                    .andEqualTo(MendOrder.WORKER_TYPE_ID, workerTypeId)
                    .andEqualTo(MendOrder.STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0) {
                return ServerResponse.createBySuccessMessage("没有生成中补人工单");
            } else if (mendOrderList.size() > 1) {
                return ServerResponse.createByErrorMessage("生成多个未提交补人工单,异常联系平台部");
            } else {
                MendOrder mendOrder = mendOrderList.get(0);
                mendOrder.setState(1);
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);

                ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(mendOrder.getChangeOrderId());
                changeOrder.setState(2);//通过->工匠业主审核
                changeOrderMapper.updateByPrimaryKeySelective(changeOrder);

                House house = houseMapper.selectByPrimaryKey(houseId);
                configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "补人工", String.format
                        (DjConstants.PushMessage.CRAFTSMAN_B_WORK, house.getHouseName()), "");
                return ServerResponse.createBySuccessMessage("操作成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 已添加补人工单明细
     */
    public ServerResponse getMendWorkerList(String houseId, String workerTypeId) {
        try {
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 1)//补人工
                    .andEqualTo(MendOrder.WORKER_TYPE_ID, workerTypeId)
                    .andEqualTo(MendOrder.STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0) {
                return ServerResponse.createBySuccessMessage("没有生成中补人工单");
            } else if (mendOrderList.size() > 1) {
                return ServerResponse.createByErrorMessage("生成多个未提交补人工单,异常联系平台部");
            } else {
                MendOrder mendOrder = mendOrderList.get(0);
                List<MendWorker> mendWorkerList = mendWorkerMapper.byMendOrderId(mendOrder.getId());
                for (MendWorker v : mendWorkerList) {
                    v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                }
                return ServerResponse.createBySuccess("查询成功", mendWorkerList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    /**
     * 明细
     */
    public MendOrderInfoDTO getMendMendOrderInfo(String houseId, String workerTypeId, String type, String state) {
        MendOrderInfoDTO mendOrderInfoDTO = new MendOrderInfoDTO();
        mendOrderInfoDTO.setTotalAmount(0.0);
        try {
            Example example = new Example(MendOrder.class);
            Example.Criteria criteria=example.createCriteria();
            criteria.andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, type)//补人工
                    .andEqualTo(MendOrder.WORKER_TYPE_ID, workerTypeId);
            if(!CommonUtil.isEmpty(state)){
                criteria.andEqualTo(MendOrder.STATE, state);
            }
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0) {
                return mendOrderInfoDTO;
            } else if (mendOrderList.size() > 1) {
                return mendOrderInfoDTO;
            } else {
                MendOrder mendOrder = mendOrderList.get(0);
                BeanUtils.beanToBean(mendOrder,mendOrderInfoDTO);
                if("0".equals(type)||"2".equals(type)||"4".equals(type)){
                    List<MendMateriel> mendMateriels = mendMaterialMapper.byMendOrderId(mendOrder.getId());
                    for (MendMateriel v : mendMateriels) {
                        v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                        mendOrderInfoDTO.setTotalAmount(mendOrderInfoDTO.getTotalAmount() + v.getTotalPrice());
                    }
                    mendOrderInfoDTO.setMendMateriels(mendMateriels);
                }else {
                    List<MendWorker> mendWorkerList = mendWorkerMapper.byMendOrderId(mendOrder.getId());
                    for (MendWorker v : mendWorkerList) {
                        v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                        mendOrderInfoDTO.setTotalAmount(mendOrderInfoDTO.getTotalAmount() + v.getTotalPrice());
                    }
                    mendOrderInfoDTO.setMendWorkers(mendWorkerList);
                }
                return mendOrderInfoDTO;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return mendOrderInfoDTO;
        }
    }
    /**
     * 管家
     * 提交补人工
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveMendWorker(String userToken, String houseId, String workerGoodsArr, String workerTypeId, String changeOrderId) {
        try {
            if(StringUtil.isEmpty(changeOrderId)){
                return ServerResponse.createByErrorMessage("未传变更单id");
            }

            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member steward = accessToken.getMember();//管家

            List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.unCheckByWorkerTypeId(houseId, workerTypeId);
            if (houseFlowApplyList.size() > 0) {
                return ServerResponse.createByErrorMessage("该工种有未处理完工申请");
            }

            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 1)
                    .andEqualTo(MendOrder.WORKER_TYPE_ID, workerTypeId)
                    .andEqualTo(MendOrder.STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            MendOrder mendOrder;
            if (mendOrderList.size() > 0) {
                mendOrder = mendOrderList.get(0);
                mendOrder.setChangeOrderId(changeOrderId);
                mendOrder.setWorkerTypeId(workerTypeId);
                mendOrder.setOrderName("补人工");
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                /*删除之前子项*/
                example = new Example(MendWorker.class);
                example.createCriteria().andEqualTo(MendWorker.MEND_ORDER_ID, mendOrder.getId());
                mendWorkerMapper.deleteByExample(example);
            } else {
                example = new Example(MendOrder.class);
                mendOrder = new MendOrder();
                mendOrder.setChangeOrderId(changeOrderId);
                mendOrder.setNumber("DJZX" + 20000 + mendOrderMapper.selectCountByExample(example));//订单号
                mendOrder.setHouseId(houseId);
                mendOrder.setApplyMemberId(steward.getId());
                mendOrder.setType(1);//补人工
                mendOrder.setOrderName("补人工");
                mendOrder.setWorkerTypeId(workerTypeId);
                mendOrder.setState(0);
                mendOrder.setTotalAmount(0.0);

                if (!this.createMendCheck(mendOrder)) {
                    return ServerResponse.createByErrorMessage("添加审核流程失败");
                }
                mendOrderMapper.insert(mendOrder);
            }

            if (this.addMendWorker(workerGoodsArr, mendOrder, workerTypeId)) {
                return ServerResponse.createBySuccessMessage("保存成功");
            } else {
                return ServerResponse.createByErrorMessage("添加明细失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("保存失败");
        }
    }

    /**
     * 保存补退人工明细mendWorker
     */
    private boolean addMendWorker(String workerGoodsArr, MendOrder mendOrder, String workerTypeId) {
        try {
            mendOrder.setTotalAmount(0.0);
            JSONArray jsonArray = JSONArray.parseArray(workerGoodsArr);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                MendWorker mendWorker = new MendWorker();//补退人工
                String workerGoodsId = obj.getString("workerGoodsId");
                double num = Double.parseDouble(obj.getString("num"));
                WorkerGoods workerGoods = forMasterAPI.getWorkerGoods(workerGoodsId);
                if (!workerGoods.getWorkerTypeId().equals(workerTypeId)) {
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
            mendOrder.setModifyDate(new Date());
            mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 确认退货
     * 管家退服务
     */
    public ServerResponse confirmBackMendMaterial(String userToken, String houseId, String imageArr) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();//工匠
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 2)
                    .andEqualTo(MendOrder.WORKER_TYPE_ID, worker.getWorkerTypeId())
                    .andEqualTo(MendOrder.STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0) {
                return ServerResponse.createBySuccessMessage("没有生成中退货单");
            } else if (mendOrderList.size() > 1) {
                return ServerResponse.createByErrorMessage("生成多个未提交退货单,异常联系平台部");
            } else {
                MendOrder mendOrder = mendOrderList.get(0);
                mendOrder.setImageArr(imageArr);//照片
                mendOrder.setState(1);//处理中
                mendOrder.setModifyDate(new Date());//更新退货
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);

                House house = houseMapper.selectByPrimaryKey(houseId);
                if (worker.getWorkerType() == 3) {
                    configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "大管家退服务", String.format
                            (DjConstants.PushMessage.STEWARD_T_SERVER, house.getHouseName()), "");
                } else {
                    configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "工匠退材料", String.format
                            (DjConstants.PushMessage.CRAFTSMAN_T_MATERIAL, house.getHouseName()), "");
                }

                //生成 退货材料的剩余临时仓库
                SurplusWareHouse srcSurplusWareHouse = iSurplusWareHouseMapper.getSurplusWareHouseByHouseId(house.getId());
                if (srcSurplusWareHouse == null) {
//                    return ServerResponse.createByErrorMessage("无该临时仓库");
                    SurplusWareHouse surplusWareHouse = new SurplusWareHouse();
                    surplusWareHouse.setHouseId(house.getId());
                    surplusWareHouse.setMemberId(house.getMemberId());
                    surplusWareHouse.setState(0);//待清点0, 已清点1  默认：0
                    surplusWareHouse.setType(2);// 1:公司仓库 2：业主房子的临时仓库
                    surplusWareHouse.setAddress(house.getHouseName());
                    iSurplusWareHouseMapper.insert(surplusWareHouse);
                } else {
                    srcSurplusWareHouse.setState(0);
                    iSurplusWareHouseMapper.updateByPrimaryKeySelective(srcSurplusWareHouse);
                }
                return ServerResponse.createBySuccessMessage("操作成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 已添加退货单明细
     */
    public ServerResponse backMendMaterialList(String userToken, String houseId) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();//工匠
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 2)
                    .andEqualTo(MendOrder.WORKER_TYPE_ID, worker.getWorkerTypeId())
                    .andEqualTo(MendOrder.STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0) {
                return ServerResponse.createBySuccessMessage("没有生成中退货单");
            } else if (mendOrderList.size() > 1) {
                return ServerResponse.createByErrorMessage("生成多个未提交退货单,异常联系平台部");
            } else {
                List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrderList.get(0).getId());
                for (MendMateriel mendMateriel : mendMaterielList) {
                    mendMateriel.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                }
                return ServerResponse.createBySuccess("查询成功", mendMaterielList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 工匠
     * 提交退货(登记剩余材料)
     * WorkerTypeId 3 管家退服务
     */
    public ServerResponse backMendMaterial(String userToken, String houseId, String productArr) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();//工匠

            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 2)//退材料
                    .andEqualTo(MendOrder.WORKER_TYPE_ID, worker.getWorkerTypeId())
                    .andLessThan(MendOrder.STATE, 2);//小于2 包括审核中状态
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            MendOrder mendOrder;
            if (mendOrderList.size() > 0) {
                mendOrder = mendOrderList.get(0);
                mendOrder.setState(0);//生成中
                /*删除之前子项*/
                example = new Example(MendMateriel.class);
                example.createCriteria().andEqualTo(MendMateriel.MEND_ORDER_ID, mendOrder.getId());
                mendMaterialMapper.deleteByExample(example);
            } else {
                example = new Example(MendOrder.class);
                mendOrder = new MendOrder();
                mendOrder.setNumber("DJZX" + 10000 + mendOrderMapper.selectCountByExample(example));//订单号
                mendOrder.setHouseId(houseId);
                mendOrder.setWorkerTypeId(worker.getWorkerTypeId());
                mendOrder.setApplyMemberId(worker.getId());
                mendOrder.setType(2);//退材料
                mendOrder.setOrderName("退材料");
                mendOrder.setState(0);//生成中
                mendOrder.setTotalAmount(0.0);

                if (!this.createMendCheck(mendOrder)) {
                    return ServerResponse.createByErrorMessage("添加审核流程失败");
                }
                mendOrderMapper.insert(mendOrder);

                if (worker.getWorkerType() == 3) {//管家退服务
                    MendOrderCheck mendOrderCheck = mendOrderCheckMapper.getByMendOrderId(mendOrder.getId(), "2");
                    if (mendOrderCheck != null) {
                        mendOrderCheck.setState(2);
                        mendOrderCheck.setAuditorId(worker.getId());//审核人
                        mendOrderCheck.setModifyDate(new Date());
                        mendOrderCheckMapper.updateByPrimaryKeySelective(mendOrderCheck);
                    }
                }
            }

            if (this.addMendMateriel(productArr, mendOrder)) {
                return ServerResponse.createBySuccessMessage("保存成功");
            } else {
                return ServerResponse.createByErrorMessage("添加明细失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("保存失败");
        }
    }

    /**
     * 确认补货
     * 管家补服务
     */
    public ServerResponse confirmMendMaterial(String userToken, String houseId) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();//工匠
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 0)
                    .andEqualTo(MendOrder.WORKER_TYPE_ID, worker.getWorkerTypeId())
                    .andEqualTo(MendOrder.STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0) {
                return ServerResponse.createBySuccessMessage("没有生成中补货单");
            } else if (mendOrderList.size() > 1) {
                return ServerResponse.createByErrorMessage("生成多个未提交补货单,异常联系平台部");
            } else {
                MendOrder mendOrder = mendOrderList.get(0);
                mendOrder.setState(1);//处理中
                mendOrder.setModifyDate(new Date());
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);

                House house = houseMapper.selectByPrimaryKey(houseId);
                if (worker.getWorkerType() == 3) {
                    configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "大管家补服务", String.format
                            (DjConstants.PushMessage.STEWARD_B_SERVER, house.getHouseName()), "");
                } else {
                    configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "工匠补材料", String.format
                            (DjConstants.PushMessage.CRAFTSMAN_B_MATERIAL, house.getHouseName()), "");
                }
                return ServerResponse.createBySuccessMessage("操作成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 返回已添加补材料单明细
     */
    public ServerResponse getMendMaterialList(String userToken, String houseId) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();//工匠
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 0)
                    .andEqualTo(MendOrder.WORKER_TYPE_ID, worker.getWorkerTypeId())
                    .andEqualTo(MendOrder.STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() == 0) {
                return ServerResponse.createBySuccessMessage("没有生成中补货单");
            } else if (mendOrderList.size() > 1) {
                return ServerResponse.createByErrorMessage("生成多个未提交补货单,异常联系平台部");
            } else {
                List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrderList.get(0).getId());
                for (MendMateriel v : mendMaterielList) {
                    v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                }
                return ServerResponse.createBySuccess("查询成功", mendMaterielList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 工匠
     * 提交补材料
     * WorkerTypeId 3 管家补服务
     */
    public ServerResponse saveMendMaterial(String userToken, String houseId, String productArr) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();//工匠

            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 0)
                    .andEqualTo(MendOrder.WORKER_TYPE_ID, worker.getWorkerTypeId())
                    .andLessThan(MendOrder.STATE, 2);//处理中
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            MendOrder mendOrder;
            if (mendOrderList.size() > 0) {
                mendOrder = mendOrderList.get(0);
                mendOrder.setState(0);//生成中
                /*删除之前子项*/
                example = new Example(MendMateriel.class);
                example.createCriteria().andEqualTo(MendMateriel.MEND_ORDER_ID, mendOrder.getId());
                mendMaterialMapper.deleteByExample(example);
            } else {
                example = new Example(MendOrder.class);
                mendOrder = new MendOrder();
                mendOrder.setNumber("DJZX" + 00000 + mendOrderMapper.selectCountByExample(example));//订单号
                mendOrder.setHouseId(houseId);
                mendOrder.setWorkerTypeId(worker.getWorkerTypeId());
                mendOrder.setApplyMemberId(worker.getId());
                mendOrder.setType(0);//补材料
                mendOrder.setOrderName("补材料");
                mendOrder.setState(0);//生成中
                mendOrder.setTotalAmount(0.0);
                if (!this.createMendCheck(mendOrder)) {
                    return ServerResponse.createByErrorMessage("添加审核流程失败");
                }
                mendOrderMapper.insert(mendOrder);
            }

            if (this.addMendMateriel(productArr, mendOrder)) {
                return ServerResponse.createBySuccessMessage("保存成功");
            } else {
                return ServerResponse.createByErrorMessage("添加明细失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("保存失败");
        }
    }

    /**
     * 保存mendMateriel
     */
    private boolean addMendMateriel(String productArr, MendOrder mendOrder) {
        try {
            mendOrder.setTotalAmount(0.0);
            JSONArray jsonArray = JSONArray.parseArray(productArr);
            for (int i = 0; i < jsonArray.size(); i++) {
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
                String unitName = forMasterAPI.getUnitName(product.getConvertUnit());
                mendMateriel.setUnitName(unitName);
                mendMateriel.setShopCount(num);
                mendMateriel.setTotalPrice(num * product.getPrice());
                mendOrder.setTotalAmount(mendOrder.getTotalAmount() + mendMateriel.getTotalPrice());//修改总价
                mendMateriel.setProductType(forMasterAPI.getGoods(product.getGoodsId()).getType());//0：材料；1：服务
                mendMateriel.setCategoryId(product.getCategoryId());
                mendMateriel.setImage(product.getImage());
                mendMaterialMapper.insertSelective(mendMateriel);
            }
            mendOrder.setModifyDate(new Date());
            mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 生成审核流程
     */
    private boolean createMendCheck(MendOrder mendOrder) {
        try {
            MendTypeRole mendTypeRole = mendTypeRoleMapper.getByType(mendOrder.getType());
            String[] roleArr = mendTypeRole.getRoleArr().split(",");
            for (int i = 0; i < roleArr.length; i++) {
                MendOrderCheck mendOrderCheck = new MendOrderCheck();
                mendOrderCheck.setMendOrderId(mendOrder.getId());
                mendOrderCheck.setRoleType(roleArr[i]);
                mendOrderCheck.setState(0);
                mendOrderCheck.setSort(i + 1);//顺序
                mendOrderCheckMapper.insert(mendOrderCheck);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
