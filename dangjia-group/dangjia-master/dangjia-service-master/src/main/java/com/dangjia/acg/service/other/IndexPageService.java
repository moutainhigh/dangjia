package com.dangjia.acg.service.other;

import com.alibaba.fastjson.JSONArray;
import com.dangjia.acg.api.actuary.BudgetMaterialAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.BudgetStageCostDTO;
import com.dangjia.acg.dto.design.QuantityRoomDTO;
import com.dangjia.acg.dto.label.OptionalLabelDTO;
import com.dangjia.acg.dto.other.HouseDetailsDTO;
import com.dangjia.acg.mapper.config.IConfigMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.deliver.IOrderMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.label.OptionalLabelMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.design.QuantityRoomImages;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.label.OptionalLabel;
import com.dangjia.acg.service.design.DesignDataService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2019/1/4 0004
 * Time: 9:48
 */
@Service
public class IndexPageService {

    @Autowired
    private OptionalLabelMapper optionalLabelMapper;
    @Autowired
    private IHouseMapper houseMapper;
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
    @Autowired
    private IModelingVillageMapper modelingVillageMapper;
    @Autowired
    private IConfigMapper iConfigMapper;
    @Autowired
    private DesignDataService designDataService;

    /**
     * 根据城市，小区，最小最大面积查询房子
     */
    public ServerResponse queryHouseDistance(HttpServletRequest request, String userToken, String cityId, String villageId, Double square, PageDTO pageDTO) {
        try {
            ModelingVillage modelingVillage = modelingVillageMapper.selectByPrimaryKey(villageId);
            Double minSquare = square - 15;
            Double maxSquare = square + 15;
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<House> houseList = houseMapper.getSameLayoutDistance(cityId, modelingVillage.getLocationx(), modelingVillage.getLocationy(), minSquare, maxSquare);
            if(houseList.size()<0){
                houseList=houseMapper.getSameLayoutDistance(cityId, modelingVillage.getLocationx(), modelingVillage.getLocationy(),null,null);
                if(houseList.size()<0){
                    return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
                }
            }
            PageInfo pageResult = new PageInfo(houseList);
            List<Map> houseMap = new ArrayList<>();
            for (House house : houseList) {
                request.setAttribute(Constants.CITY_ID, house.getCityId());
                BigDecimal totalPrice = new BigDecimal(0);//总计
                if (house.getDecorationType() != 2) {//自带设计
                    Order order = orderMapper.getWorkerOrder(house.getId(), "1");
                    if (order != null) {
                        totalPrice = totalPrice.add(order.getTotalAmount());
                    }
                }
                Order order = orderMapper.getWorkerOrder(house.getId(), "2");
                if (order != null) {
                    totalPrice = totalPrice.add(order.getTotalAmount());
                }
                ServerResponse serverResponse = budgetMaterialAPI.getHouseBudgetStageCost(request, house.getId(), null);
                JSONArray pageInfo = (JSONArray) serverResponse.getResultObj();
                List<BudgetStageCostDTO> budgetStageCostDTOS = pageInfo.toJavaList(BudgetStageCostDTO.class);
                for (BudgetStageCostDTO budgetStageCostDTO : budgetStageCostDTOS) {
                    totalPrice = totalPrice.add(budgetStageCostDTO.getTotalAmount());
                }
                house.setMoney(totalPrice);
                Map map = BeanUtils.beanToMap(house);
                map.put("houseName", house.getHouseName());
                serverResponse = designDataService.getConstructionPlans(house.getId());
                if (serverResponse.isSuccess()) {
                    QuantityRoomDTO quantityRoomDTO = (QuantityRoomDTO) serverResponse.getResultObj();
                    List<QuantityRoomImages> images = quantityRoomDTO.getImages();
                    if (images != null && images.size() > 0) {
                        map.put("imageUrl", images.get(0).getImage() + "?x-image-process=image/resize,w_500,h_500/quality,q_80");
                    }
                }
                houseMap.add(map);
            }
            pageResult.setList(houseMap);
            return ServerResponse.createBySuccess(null, pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取数据失败");
        }
    }

    /**
     * 施工现场详情
     */
    public ServerResponse houseDetails(HttpServletRequest request, String houseId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            BigDecimal totalPrice = new BigDecimal(0);//总计
            House house = houseMapper.selectByPrimaryKey(houseId);
            request.setAttribute(Constants.CITY_ID, house.getCityId());
            HouseDetailsDTO houseDetailsDTO = new HouseDetailsDTO();
            houseDetailsDTO.setCityId(house.getCityId());
            houseDetailsDTO.setCityName(house.getCityName());
            houseDetailsDTO.setSquare(house.getSquare());
            houseDetailsDTO.setHouseId(house.getId());
            houseDetailsDTO.setResidential(house.getResidential());
            houseDetailsDTO.setImage(getHouseImage(house.getId()));
            houseDetailsDTO.setHouseName(house.getHouseName());
            houseDetailsDTO.setOptionalLabel(house.getOptionalLabel());
            houseDetailsDTO.setVisitState(house.getVisitState());
            String[] liangArr = {};
            if (house.getLiangDian() != null) {
                liangArr = house.getLiangDian().split(",");
            }
            List<String> dianList = new ArrayList<>();
            if (!CommonUtil.isEmpty(house.getStyle())) {
                dianList.add(house.getStyle());

            }
            if (!CommonUtil.isEmpty(house.getLiangDian())) {
                Collections.addAll(dianList, liangArr);
            }
            if (!CommonUtil.isEmpty(house.getBuildSquare())) {
                dianList.add(house.getBuildSquare() + "㎡");
            }
            houseDetailsDTO.setDianList(dianList);
            List<Map<String, Object>> mapList = new ArrayList<>();
            Map<String, Object> mapReady = new HashMap<>();
            mapReady.put("name", "准备阶段");
            if (house.getDecorationType() == 2) {//自带设计
                mapReady.put("typeA", "¥" + 0);
            } else {
                Order order = orderMapper.getWorkerOrder(houseId, "1");
                if (order != null) {
                    mapReady.put("typeA", "¥" + String.format("%.2f", order.getTotalAmount().doubleValue()));
                    totalPrice = totalPrice.add(order.getTotalAmount());
                } else {
                    mapReady.put("typeA", "¥" + 0);
                }
            }
            Order order = orderMapper.getWorkerOrder(houseId, "2");
            if (order != null) {
                mapReady.put("typeB", "¥" + (order.getTotalAmount() == null ? 0 : String.format("%.2f", order.getTotalAmount().doubleValue())));
                totalPrice = totalPrice.add(order.getTotalAmount());
            } else {
                mapReady.put("typeB", "¥" + 0);
            }
            mapList.add(mapReady);

            Example example = new Example(HouseFlow.class);
            example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, houseId).andGreaterThan(HouseFlow.WORKER_TYPE, 2);
            example.orderBy(HouseFlow.WORKER_TYPE);
            List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
            for (HouseFlow houseFlow : houseFlowList) {
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
                Map<String, Object> map = new HashMap<>();
                map.put("name", workerType.getName());
                map.put("image", address + workerType.getImage());
                map.put("workerType", workerType.getType());
                map.put("workerTypeId", workerType.getId());
                ServerResponse serverResponse = budgetMaterialAPI.getHouseBudgetStageCost(request, houseId, houseFlow.getWorkerTypeId());
                JSONArray pageInfo = (JSONArray) serverResponse.getResultObj();
                List<BudgetStageCostDTO> budgetStageCostDTOS = pageInfo.toJavaList(BudgetStageCostDTO.class);
                for (BudgetStageCostDTO budgetStageCostDTO : budgetStageCostDTOS) {
                    totalPrice = totalPrice.add(budgetStageCostDTO.getTotalAmount());
                }
                if (budgetStageCostDTOS.size() > 0) {
                    map.put("workers", serverResponse.getResultObj());
                    mapList.add(map);
                }
            }
            houseDetailsDTO.setMapList(mapList);
            houseDetailsDTO.setTotalPrice(totalPrice);
            if(!CommonUtil.isEmpty(house.getOptionalLabel())){
                List<OptionalLabelDTO> fieldValues = new ArrayList<>();
                String[] optionalLabel=house.getOptionalLabel().split(",");
//                for (String s : optionalLabel) {
//                    fieldValues.add(s);
//                };
                List<OptionalLabel> optionalLabels = optionalLabelMapper.selectAll();
                for (OptionalLabel label : optionalLabels) {
                    OptionalLabelDTO optionalLabelDTO=new OptionalLabelDTO();
                    optionalLabelDTO.setId(label.getId());
                    optionalLabelDTO.setLabelName(label.getLabelName());
                    optionalLabelDTO.setStatus("1");
                    for (String s : optionalLabel) {
                        if(s.equals(label.getId())) {
                            optionalLabelDTO.setStatus("0");
                            break;
                        }
                    }
                    fieldValues.add(optionalLabelDTO);
                }
//                example = new Example(OptionalLabel.class);
//                example.createCriteria().andIn(OptionalLabel.ID,fieldValues);
////                example.orderBy(HouseFlow.WORKER_TYPE);
//                List<OptionalLabel> optionalLabels=optionalLabelMapper.selectByExample(example);
//                for (int i = 0; i < optionalLabels.size(); i++) {
//                    fieldValues.remove(i);
//                    fieldValues.add(i,optionalLabels.get(i).getLabelName());
//                }
                houseDetailsDTO.setLabelList(fieldValues);
            }
            return ServerResponse.createBySuccess("查询成功", houseDetailsDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取数据失败");
        }
    }


    /**
     * 施工现场
     *
     * @param request
     * @param latitude
     * @param longitude
     * @return
     */
    public ServerResponse jobLocation(HttpServletRequest request, String latitude, String longitude) {
        if (latitude == null) {
            latitude = "28.228259";
        }
        if (longitude == null) {
            longitude = "112.938904";
        }
        List<House> houses = modelingVillageMapper.jobLocation(latitude, longitude);
        if (houses.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        for (House house : houses) {
            house.setImage(getHouseImage(house.getId()));
        }
        return ServerResponse.createBySuccess("查询成功", houses);
    }

    private String getHouseImage(String id) {
        ServerResponse serverResponse = designDataService.getConstructionPlans(id);
        if (serverResponse.isSuccess()) {
            QuantityRoomDTO quantityRoomDTO = (QuantityRoomDTO) serverResponse.getResultObj();
            List<QuantityRoomImages> images = quantityRoomDTO.getImages();
            if (images != null && images.size() > 0) {
                return images.get(0).getImage();
//                + "?x-image-process=image/resize,w_500,h_500/quality,q_80";
            }
        }
        return null;
    }
}
