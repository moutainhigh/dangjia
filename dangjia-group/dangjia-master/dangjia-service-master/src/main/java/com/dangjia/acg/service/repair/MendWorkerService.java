package com.dangjia.acg.service.repair;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.repair.IMendWorkerMapper;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.repair.MendWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/11 0011
 * Time: 11:40
 */
@Service
public class MendWorkerService {

    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IMendWorkerMapper mendWorkerMapper;
    @Autowired
    private ConfigUtil configUtil;



    /**
     * 通过 不通过
     * 1工匠审核中，2工匠审核不通过，3工匠审核通过即平台审核中，4平台不同意，5平台审核通过,6管家取消
     */
    public ServerResponse checkWorkerBackState(String mendOrderId,int state){
        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
        mendOrder.setWorkerBackState(state);
        mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
        return ServerResponse.createBySuccessMessage("操作成功");

    }

    /**
     * 房子id查询退人工
     * workerBackState
     * 0生成中,1工匠审核中，2工匠审核不通过，3工匠审核通过即平台审核中，4平台不同意，5平台审核通过,6管家取消
     */
    public ServerResponse workerBackState(String houseId){
        try{
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 3)
                    .andGreaterThan(MendOrder.WORKER_BACK_STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            return ServerResponse.createBySuccess("查询成功", mendOrderList);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 审核补人工
     */
    public ServerResponse checkWorkerOrderState(String mendOrderId,int state){
        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
        mendOrder.setWorkerOrderState(state);
        mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 人工单明细mendOrderId
     */
    public ServerResponse mendWorkerList(String mendOrderId){
        List<MendWorker> mendWorkerList = mendWorkerMapper.byMendOrderId(mendOrderId);
        for (MendWorker mendWorker : mendWorkerList){
            mendWorker.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
        }
        return ServerResponse.createBySuccess("查询成功", mendWorkerList);
    }
    /**
     * 普通工匠房子id查询补人工单列表
     * 补人工审核状态
     * 0生成中,1工匠审核中，2工匠不同意，3工匠同意即平台审核中，4平台不同意,5平台同意即待业主支付，6业主已支付，7业主不同意, 8管家取消
     */
    public ServerResponse workerOrderState(String houseId){
        try{
            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 1)
                    .andGreaterThan(MendOrder.WORKER_ORDER_STATE, 0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            return ServerResponse.createBySuccess("查询成功", mendOrderList);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
