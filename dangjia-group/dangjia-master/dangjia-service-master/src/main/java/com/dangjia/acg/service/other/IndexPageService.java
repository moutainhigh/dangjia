package com.dangjia.acg.service.other;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.other.HouseDetailsDTO;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.deliver.IOrderMapper;
import com.dangjia.acg.mapper.design.IHouseDesignImageMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.design.HouseDesignImage;
import com.dangjia.acg.modle.house.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2019/1/4 0004
 * Time: 9:48
 */
@Service
public class IndexPageService {
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IHouseDesignImageMapper houseDesignImageMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IOrderMapper orderMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;

    /**
     * 施工现场
     */
    public ServerResponse houseDetails(String houseId){
        try {
            House house = houseMapper.selectByPrimaryKey(houseId);
            HouseDetailsDTO houseDetailsDTO = new HouseDetailsDTO();
            HouseDesignImage houseDesignImage = houseDesignImageMapper.planeGraph(houseId);
            if (houseDesignImage == null){
                Example example = new Example(HouseDesignImage.class);
                example.createCriteria().andEqualTo(HouseDesignImage.HOUSE_ID, houseId);
                List<HouseDesignImage> houseDesignImageList = houseDesignImageMapper.selectByExample(example);
                houseDesignImage = houseDesignImageList.get(0);
            }
            houseDetailsDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + houseDesignImage.getImageurl());
            houseDetailsDTO.setHouseName(house.getResidential()+"***"+house.getNumber()+"房");
            String[] liangArr = {};
            if (house.getLiangDian() != null){
                liangArr = house.getLiangDian().split(",");
            }
            List<String> dianList = new ArrayList<>();
            dianList.add(house.getStyle());
            for (int i=0; i<liangArr.length;  i++){
                dianList.add(liangArr[i]);
            }
            dianList.add(house.getSquare() + "㎡");
            houseDetailsDTO.setDianList(dianList);
            List<Map<String,Object>> mapList = new ArrayList<>();
            Map<String,Object> mapReady = new HashMap<>();
            mapReady.put("name","准备阶段");
            if (house.getDecorationType() == 2){//自带设计
                mapReady.put("typeA", "￥" + 0);
            }else {
                Order order = orderMapper.getWorkerOrder(houseId,"1");
                mapReady.put("typeA", "￥" + order.getTotalAmount());
            }
            Order order = orderMapper.getWorkerOrder(houseId,"2");
            mapReady.put("typeB", "￥" + (order == null? 0 : order.getTotalAmount()));
            order = orderMapper.getWorkerOrder(houseId,"3");
            mapReady.put("typeC", "￥" + (order == null? 0 : order.getTotalAmount()));
            mapList.add(mapReady);

            Example example = new Example(HouseFlow.class);
            example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, houseId).andEqualTo(HouseFlow.WORK_TYPE,4).andGreaterThan(HouseFlow.WORKER_TYPE,3);
            List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
            for (HouseFlow houseFlow : houseFlowList){
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
                Map<String,Object> map = new HashMap<>();
                map.put("name",workerType.getName());
                order = orderMapper.getWorkerOrder(houseId,houseFlow.getWorkerTypeId());
                map.put("typeE",  "￥" + (order == null? 0 : order.getTotalAmount()));
                mapList.add(map);
            }
            houseDetailsDTO.setMapList(mapList);
            return ServerResponse.createBySuccess("查询成功",houseDetailsDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取数据失败");
        }
    }
}
