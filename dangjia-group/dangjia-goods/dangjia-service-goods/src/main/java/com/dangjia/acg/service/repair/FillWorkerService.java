package com.dangjia.acg.service.repair;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.repair.BudgetWorkerDTO;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.IWorkerGoodsMapper;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/7 0007
 * Time: 17:12
 */
@Service
public class FillWorkerService {

    @Autowired
    private IBudgetWorkerMapper budgetWorkerMapper;
    @Autowired
    private IWorkerGoodsMapper workerGoodsMapper;

    @Autowired
    private ConfigUtil configUtil;
    protected static final Logger LOG = LoggerFactory.getLogger(FillWorkerService.class);

    /**
     * @param type 0 精算内 1 商品库
     *             <p>
     *             补人工,退人工共用此接口(精算内)
     */
    public ServerResponse repairBudgetWorker(int type, String workerTypeId, String houseId, String name, PageDTO pageDTO) {
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        if (StringUtil.isEmpty(workerTypeId)) {
            return ServerResponse.createByErrorMessage("workerTypeId不能为空");
        }
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<BudgetWorkerDTO> budgetWorkerDTOList = new ArrayList<>();
        PageInfo pageResult;
        try {
            if (type == 0) {//精算内
                Example example = new Example(BudgetWorker.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo(BudgetWorker.WORKER_TYPE_ID, workerTypeId);
                criteria.andEqualTo(BudgetWorker.HOUSE_ID, houseId);
                criteria.andNotEqualTo(BudgetWorker.DELETE_STATE, "1");
                criteria.andCondition(" ( `name` IS NOT NULL OR `name` <> '' ) ");
                if (!CommonUtil.isEmpty(name)) {
                    criteria.andLike(BudgetWorker.NAME, "%" + name + "%");
                }
                List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.selectByExample(example);
                pageResult = new PageInfo(budgetWorkerList);
                for (BudgetWorker budgetWorker : budgetWorkerList) {
                    WorkerGoods workerGoods = workerGoodsMapper.selectByPrimaryKey(budgetWorker.getWorkerGoodsId());
                    BudgetWorkerDTO budgetWorkerDTO = new BudgetWorkerDTO();
                    budgetWorkerDTO.setWorkerGoodsId(budgetWorker.getWorkerGoodsId());
                    budgetWorkerDTO.setWorkerTypeId(budgetWorker.getWorkerTypeId());
                    budgetWorkerDTO.setWorkerGoodsSn(budgetWorker.getWorkerGoodsSn());
                    budgetWorkerDTO.setName(budgetWorker.getName());
                    budgetWorkerDTO.setPrice(budgetWorker.getPrice());
                    budgetWorkerDTO.setShopCount(budgetWorker.getShopCount() - budgetWorker.getBackCount() + budgetWorker.getRepairCount());
                    budgetWorkerDTO.setUnitName(budgetWorker.getUnitName());
                    budgetWorkerDTO.setImage(address + workerGoods.getImage());
                    budgetWorkerDTOList.add(budgetWorkerDTO);
                }
            } else {
                Example example = new Example(WorkerGoods.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo(WorkerGoods.WORKER_TYPE_ID, workerTypeId);
                criteria.andEqualTo(WorkerGoods.SHOW_GOODS, 1);
                if (!CommonUtil.isEmpty(name)) {
                    criteria.andLike(WorkerGoods.NAME, "%" + name + "%");
                }
                PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
                List<WorkerGoods> workerGoodsList = workerGoodsMapper.selectByExample(example);
                pageResult = new PageInfo(workerGoodsList);
                for (WorkerGoods workerGoods : workerGoodsList) {
                    BudgetWorkerDTO budgetWorkerDTO = new BudgetWorkerDTO();
                    budgetWorkerDTO.setWorkerGoodsId(workerGoods.getId());
                    budgetWorkerDTO.setWorkerTypeId(workerGoods.getWorkerTypeId());
                    budgetWorkerDTO.setWorkerGoodsSn(workerGoods.getWorkerGoodsSn());
                    budgetWorkerDTO.setName(workerGoods.getName());
                    budgetWorkerDTO.setPrice(workerGoods.getPrice());
                    budgetWorkerDTO.setUnitName(workerGoods.getUnitName());//单位
                    budgetWorkerDTO.setImage(address + workerGoods.getImage());
                    budgetWorkerDTOList.add(budgetWorkerDTO);
                }
            }
            pageResult.setList(budgetWorkerDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
