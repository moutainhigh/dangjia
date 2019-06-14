package com.dangjia.acg.service.home;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ruking.Cheng
 * @descrilbe 首页配置实现
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/13 3:22 PM
 */
@Service
public class HomeModularService {
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private IWorkerTypeMapper iWorkerTypeMapper;
    @Autowired
    private IHouseFlowApplyMapper iHouseFlowApplyMapper;

    public ServerResponse getBroadcastList() {
        PageHelper.startPage(1, 20);
        Example example = new Example(HouseFlowApply.class);
        //过滤掉提前结束的房子
        example.createCriteria()
                .andNotEqualTo(HouseFlowApply.APPLY_TYPE, 8)
                .andNotEqualTo(HouseFlowApply.APPLY_TYPE, 9);
        example.orderBy(HouseFlowApply.CREATE_DATE).desc();
        List<HouseFlowApply> houseFlowApplies = iHouseFlowApplyMapper.selectByExample(example);
        if (houseFlowApplies.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        List<Map<String, Object>> listMap = new ArrayList<>();
        for (HouseFlowApply houseFlowApply : houseFlowApplies) {
            Map<String, Object> map = new HashMap<>();
            StringBuffer describe = new StringBuffer();
            House house = iHouseMapper.selectByPrimaryKey(houseFlowApply.getHouseId());
            if (house == null) {
                continue;
            }
            describe.append(house.getNoNumberHouseName());
            Member member = iMemberMapper.selectByPrimaryKey(houseFlowApply.getWorkerId());
            if (member != null) {
                WorkerType workerType = iWorkerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
                if (workerType != null) {
                    describe.append(" ");
                    describe.append(workerType.getName());
                }
            }
            switch (houseFlowApply.getApplyType()) {
                case 0:
                    describe.append("今日已完工");
                    break;
                case 1:
                    describe.append("今日阶段完工");
                    break;
                case 2:
                    describe.append("今日整体完工");
                    break;
                case 3:
                    describe.append("今日已停工");
                    break;
                case 4:
                    describe.append("已开工");
                    break;
                default:
                    describe.append("今日已巡查");
                    break;
            }
            map.put("describe", describe);
            map.put("houseId", houseFlowApply.getHouseId());
            listMap.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", listMap);
    }

}
