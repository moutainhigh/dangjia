package com.dangjia.acg.service.other;

import com.alibaba.fastjson.JSONArray;
import com.dangjia.acg.api.actuary.BudgetMaterialAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.BudgetStageCostDTO;
import com.dangjia.acg.dto.other.HouseDetailsDTO;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.deliver.IOrderMapper;
import com.dangjia.acg.mapper.design.IHouseDesignImageMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.house.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
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
    @Autowired
    private BudgetMaterialAPI budgetMaterialAPI;


    /**
     * 施工现场
     */
    public ServerResponse houseDetails(HttpServletRequest request, String houseId){
        try {
            BigDecimal totalPrice=new BigDecimal(0);//总计
            House house = houseMapper.selectByPrimaryKey(houseId);
            request.setAttribute(Constants.CITY_ID,house.getCityId());
            HouseDetailsDTO houseDetailsDTO = new HouseDetailsDTO();
            houseDetailsDTO.setCityId(house.getCityId());
            houseDetailsDTO.setHouseId(house.getId());
            houseDetailsDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + house.getImage());
            houseDetailsDTO.setHouseName(house.getResidential()+"***"+house.getNumber()+"房");
            String[] liangArr = {};
            if (house.getLiangDian() != null){
                liangArr = house.getLiangDian().split(",");
            }
            List<String> dianList = new ArrayList<>();
            if(!CommonUtil.isEmpty(house.getStyle())) {
                dianList.add(house.getStyle());

            }
            if(!CommonUtil.isEmpty(house.getLiangDian())) {
                for (int i = 0; i < liangArr.length; i++) {
                    dianList.add(liangArr[i]);
                }
            }
            if(!CommonUtil.isEmpty(house.getSquare())) {
                dianList.add(house.getSquare() + "㎡");
            }
            houseDetailsDTO.setDianList(dianList);
            List<Map<String,Object>> mapList = new ArrayList<>();
            Map<String,Object> mapReady = new HashMap<>();
            mapReady.put("name","准备阶段");
            if (house.getDecorationType() == 2){//自带设计
                mapReady.put("typeA", "¥"  + 0);
            }else {
                Order order = orderMapper.getWorkerOrder(houseId,"1");
                if(order!=null) {
                    mapReady.put("typeA", "¥" + String.format("%.2f", order.getTotalAmount().doubleValue()));
                    totalPrice=totalPrice.add(order.getTotalAmount());
                }else{
                    mapReady.put("typeA", "¥"  + 0);
                }
            }
            Order order = orderMapper.getWorkerOrder(houseId,"2");
            if(order!=null) {
                mapReady.put("typeB","¥" + (order == null? 0 : String.format("%.2f", order.getTotalAmount().doubleValue())));
                totalPrice=totalPrice.add(order.getTotalAmount());
            }else{
                mapReady.put("typeB", "¥"  + 0);
            }
            mapList.add(mapReady);

            Example example = new Example(HouseFlow.class);
            example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, houseId).andGreaterThan(HouseFlow.WORKER_TYPE,2);
            example.orderBy(HouseFlow.WORKER_TYPE);
            List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
            for (HouseFlow houseFlow : houseFlowList){
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
                Map<String,Object> map = new HashMap<>();
                map.put("name",workerType.getName());
                ServerResponse serverResponse=budgetMaterialAPI.getHouseBudgetStageCost(request,houseId,houseFlow.getWorkerTypeId());
                JSONArray pageInfo=(JSONArray)serverResponse.getResultObj();
                List<BudgetStageCostDTO>  budgetStageCostDTOS= pageInfo.toJavaList(BudgetStageCostDTO.class);
                for (BudgetStageCostDTO budgetStageCostDTO : budgetStageCostDTOS) {
                    totalPrice=totalPrice.add(budgetStageCostDTO.getTotalAmount());
                }
                if(budgetStageCostDTOS.size()>0) {
                    map.put("workers", serverResponse.getResultObj());
                    mapList.add(map);
                }
            }
            houseDetailsDTO.setMapList(mapList);
            houseDetailsDTO.setTotalPrice(totalPrice);
            return ServerResponse.createBySuccess("查询成功",houseDetailsDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取数据失败");
        }
    }
}
