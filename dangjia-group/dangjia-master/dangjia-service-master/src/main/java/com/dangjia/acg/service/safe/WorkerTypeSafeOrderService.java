package com.dangjia.acg.service.safe;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeOrderMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.safe.WorkerTypeSafe;
import com.dangjia.acg.modle.safe.WorkerTypeSafeOrder;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IHouseMapper houseMapper;

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
            example.createCriteria().andEqualTo(WorkerTypeSafeOrder.HOUSE_ID, houseFlow.getHouseId())
                    .andEqualTo(WorkerTypeSafeOrder.WORKER_TYPE_ID, houseFlow.getWorkerTypeId());
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
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(WorkerTypeSafeOrder.class);
        example.createCriteria().andEqualTo(WorkerTypeSafeOrder.HOUSE_ID, houseId);
        List<WorkerTypeSafeOrder> list = workerTypeSafeOrderMapper.selectByExample(example);
        if (list == null || list.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(list);
        List<Map> listMap = new ArrayList<>();
        for (WorkerTypeSafeOrder wtso : list) {
            Map map = BeanUtils.beanToMap(wtso);
            WorkerTypeSafe wts = workerTypeSafeMapper.selectByPrimaryKey(wtso.getWorkerTypeSafeId());//获得类型算出时间
            map.put("workerTypeSafe", wts);
            listMap.add(map);
        }
        pageResult.setList(listMap);
        return ServerResponse.createBySuccess("获取质保卡成功", pageResult);
    }

    /*
     *我的质保卡明细
     */
    public ServerResponse getMySafeTypeOrderDetail(String id) {
        WorkerTypeSafeOrder workerTypeSafeOrder = workerTypeSafeOrderMapper.selectByPrimaryKey(id);
        if (workerTypeSafeOrder == null) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询成功", workerTypeSafeOrder);
    }

}
