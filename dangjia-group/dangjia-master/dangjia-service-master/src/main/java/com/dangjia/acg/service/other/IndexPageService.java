package com.dangjia.acg.service.other;

import com.alibaba.fastjson.JSONArray;
import com.dangjia.acg.api.actuary.BudgetMaterialAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.BudgetStageCostDTO;
import com.dangjia.acg.dto.design.QuantityRoomDTO;
import com.dangjia.acg.dto.house.ShareDTO;
import com.dangjia.acg.dto.label.OptionalLabelDTO;
import com.dangjia.acg.dto.other.HouseDetailsDTO;
import com.dangjia.acg.mapper.core.IHouseFlowApplyImageMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.deliver.IOrderMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IModelingLayoutMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.label.OptionalLabelMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.design.QuantityRoomImages;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.ModelingLayout;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.label.OptionalLabel;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
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
    private IHouseFlowApplyImageMapper houseFlowApplyImageMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private IModelingLayoutMapper modelingLayoutMapper;
    @Autowired
    private DesignDataService designDataService;

    /**
     * 根据城市，小区，最小最大面积查询房子
     */
    public ServerResponse queryHouseDistance(HttpServletRequest request, String cityId, String villageId, Double square, PageDTO pageDTO) {
        try {
            String locationx = null;
            String Locationy = null;
            if (!CommonUtil.isEmpty(villageId)) {
                ModelingVillage modelingVillage = modelingVillageMapper.selectByPrimaryKey(villageId);
                locationx = modelingVillage.getLocationx();
                Locationy = modelingVillage.getLocationy();
            }
            Double minSquare = square - 15;
            Double maxSquare = square + 15;
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<House> houseList;
            if (!CommonUtil.isEmpty(villageId)) {
                houseList = houseMapper.getSameLayoutDistance(cityId, locationx, Locationy, minSquare, maxSquare, villageId);
            } else {
                houseList = houseMapper.getSameLayout(cityId, null, minSquare, maxSquare, null);
            }
            if (houseList.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageResult = new PageInfo(houseList);
            List<Map> houseMap = new ArrayList<>();
            for (House house : houseList) {
                house = setHouseTotalPrice(request, house);
                Map map = BeanUtils.beanToMap(house);
                map.put("houseName", house.getHouseName());
                map.put("imageUrl", configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + houseFlowApplyImageMapper.getHouseFlowApplyImage(house.getId(), null));
                houseMap.add(map);
            }
            pageResult.setList(houseMap);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取数据失败");
        }
    }

    /**
     * 根据城市，小区，最小最大面积查询房子
     */
    public ServerResponse queryHouseByCity(String userToken, String cityId, String villageId,
                                           Double minSquare, Double maxSquare, Integer houseType, PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Object object = constructionService.getMember(userToken);
            Member member = null;
            if (object instanceof Member) {
                member = (Member) object;
            }
            boolean isReferenceBudget = false;
            if (villageId != null && villageId.contains("#")) {
                isReferenceBudget = true;
                villageId = villageId.replaceAll("#", "");
            }

            List<House> houseList = iHouseMapper.getSameLayout(cityId, villageId, minSquare, maxSquare, houseType);
            PageInfo pageResult = new PageInfo(houseList);
            List<ShareDTO> srlist = new ArrayList<>();
            if (houseList.size() > 0) {//根据条件查询所选小区总价最少的房子
                for (House house : houseList) {
                    srlist.add(convertHouse(house, member));
                }
            } else {
                if (isReferenceBudget) {
                    houseList = iHouseMapper.getSameLayout(cityId, null, minSquare, maxSquare, houseType);
                    pageResult = new PageInfo(houseList);
                    if (houseList.size() > 0) {//根据条件查询所选小区总价最少的房子
                        for (House house : houseList) {
                            srlist.add(convertHouse(house, member));
                        }
                    } else {
                        return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
                    }
                } else {
                    return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
                }
            }
            pageResult.setList(srlist);
            return ServerResponse.createBySuccess(null, pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取数据失败");
        }
    }


    private ShareDTO convertHouse(House house, Member member) {
        ModelingLayout ml = modelingLayoutMapper.selectByPrimaryKey(house.getModelingLayoutId());
        ShareDTO shareDTO = new ShareDTO();
        shareDTO.setType("1");
        if (house.getShowHouse() == 0) {
//            if (accessToken != null) {
//                shareDTO.setName(house.getHouseName());
//            } else {
            shareDTO.setHouseName(house.getHouseName());
            shareDTO.setNoNumberHouseName(house.getResidential() + "**" + "栋" + (CommonUtil.isEmpty(house.getUnit()) ? "" : house.getUnit() + "单元") + house.getNumber() + "房");
//            }
        } else {
            shareDTO.setHouseName(house.getHouseName());
            shareDTO.setNoNumberHouseName("*栋*单元*号");
        }
        shareDTO.setJianzhumianji("建筑面积:" + (house.getBuildSquare() == null ? "0" : house.getBuildSquare()) + "m²");//建筑面积
        shareDTO.setJvillageacreage("计算面积:" + (house.getSquare() == null ? "0" : house.getSquare()) + "m²");//计算面积
        String biaoqian = house.getLiangDian();//标签
        List<String> biaoqians = new ArrayList<>();
        if (!CommonUtil.isEmpty(biaoqian)) {
            for (String s1 : biaoqian.split(",")) {
                if (!CommonUtil.isEmpty(s1)) {
                    biaoqians.add(s1);
                }
            }
        }
        biaoqians.add((house.getBuildSquare() == null ? "0" : house.getBuildSquare()) + "m²");
        shareDTO.setBiaoqian(biaoqians);//亮点标签
        BigDecimal money = house.getMoney();
        shareDTO.setPrice("***" + (member != null && money != null && money.toString().length() > 2 ?
                money.toString().substring(money.toString().length() - 2) : "00"));//精算总价
        shareDTO.setShowHouse(house.getShowHouse());
        shareDTO.setHouseId(house.getId());
        shareDTO.setVisitState(house.getVisitState());
        shareDTO.setVillageId(house.getVillageId());//小区id
        shareDTO.setVillageName(house.getResidential());//小区名
        shareDTO.setLayoutId(house.getModelingLayoutId());//户型id
        shareDTO.setLayoutleft(ml == null ? "" : ml.getName());//户型名称
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        String jobLocationDetail = address + String.format(DjConstants.YZPageAddress.JOBLOCATIONDETAIL, "", house.getCityId(), "施工现场") + "&houseId=" + house.getId();
        shareDTO.setUrl(jobLocationDetail);
        shareDTO.setImageNum(0 + "张图片");
        shareDTO.setImage(address + houseFlowApplyImageMapper.getHouseFlowApplyImage(house.getId(), null));//户型图片
        ServerResponse serverResponse = designDataService.getConstructionPlans(house.getId());
        if (serverResponse.isSuccess()) {
            QuantityRoomDTO quantityRoomDTO = (QuantityRoomDTO) serverResponse.getResultObj();
            List<QuantityRoomImages> images = quantityRoomDTO.getImages();
            if (images != null && images.size() > 0) {
//                shareDTO.setImage(images.get(0).getImage() + "?x-image-process=image/resize,w_500,h_500/quality,q_80");
                shareDTO.setImageNum(quantityRoomDTO.getImages().size() + "张图片");
            }
        }
        return shareDTO;
    }


    /**
     * 施工现场
     *
     * @param request
     * @param latitude
     * @param longitude
     * @return
     */
    public ServerResponse jobLocation(HttpServletRequest request, String latitude, String longitude, Integer limit) {
        if (CommonUtil.isEmpty(latitude) || "0".equals(latitude)) {
            latitude = "28.228259";
        }
        if (CommonUtil.isEmpty(longitude) || "0".equals(longitude)) {
            longitude = "112.938904";
        }
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        List<House> houses = new ArrayList<>();
        Integer endDistance = 3;
        Integer beginDistance = 0;
        List<House> houses1 = modelingVillageMapper.jobLocation(latitude, longitude, beginDistance, 3 * (limit / 2), limit);
        for (int i = 1; i < limit / 2 + 1; i++) {
            List<House> lsHouse = new ArrayList<>();
            Map map = new HashMap();
            for (House house : houses1) {
                if (lsHouse.size() == 2) {
                    break;
                }
                if (map.get(house.getVillageId()) == null) {
                    if (house.getJuli() >= (beginDistance * 1000) && house.getJuli() <= (endDistance * 1000)) {
                        lsHouse.add(house);
                    }
                }
            }
            if (lsHouse.size() > 0) {
                for (House house : lsHouse) {
                    house.setHouseId(house.getId());
                    house = this.getHouseImage(address, house);
                    houses.add(house);
                    map.put(house.getVillageId(), "Y");
                }
            }
            beginDistance = endDistance;
            endDistance += 3;
        }
//        if (houses.size() <= 0) {
//            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
//        }
        if (houses.size() == 0) {
            List<House> houseslist = modelingVillageMapper.jobModelingVillage(latitude, longitude, limit);
            for (House house : houseslist) {
                houses.add(getHouseImage(address, house));
            }
        }
        return ServerResponse.createBySuccess("查询成功", houses);
    }

    private House getHouseImage(String address, House house) {
        String image = houseFlowApplyImageMapper.getHouseFlowApplyImage(house.getId(), null);
        if (CommonUtil.isEmpty(image)) {
            image = houseFlowApplyImageMapper.getHouseFlowApplyImage(house.getId(), 0);
        }
        house.setImage(address + image);
        house.setHouseId(house.getId());
        if (CommonUtil.isEmpty(image)) {
            house.setImage(address + house.getImage());
        }
        return house;
    }


    /**
     * 参考花费返回1004时调
     *
     * @param request
     * @param latitude
     * @param longitude
     * @param limit
     * @return
     */
    public ServerResponse getRecommended(HttpServletRequest request, String latitude, String longitude, Integer limit) {
        try {
            if (CommonUtil.isEmpty(latitude)) {
                latitude = "28.228259";
            }
            if (CommonUtil.isEmpty(longitude)) {
                longitude = "112.938904";
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<House> houses = houseMapper.getRecommended(latitude, longitude, limit);
            for (int i = 0; i < houses.size(); i++) {
                House house = houses.get(i);
                house = setHouseTotalPrice(request, house);
                house = this.getHouseImage(address, house);
                houses.remove(i);
                houses.add(i, house);
            }
            return ServerResponse.createBySuccess("查询成功", houses);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询出错,获取数据失败");
        }
    }

    public House setHouseTotalPrice(HttpServletRequest request, House house) {
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
        request.setAttribute(Constants.CITY_ID, house.getCityId());
        BigDecimal totalAmount = budgetMaterialAPI.getHouseBudgetTotalAmount(request, house.getCityId(), house.getId());
        if (totalAmount != null) {
            totalPrice = totalPrice.add(totalAmount);
        }
        house.setMoney(totalPrice);
        return house;
    }

    /**
     * 施工现场详情
     */
    public ServerResponse houseOtherDetails(HttpServletRequest request, String houseId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            House house = houseMapper.selectByPrimaryKey(houseId);
            //获得装修总花费
            house = setHouseTotalPrice(request, house);
            HouseDetailsDTO houseDetailsDTO = new HouseDetailsDTO();
            houseDetailsDTO.setCityId(house.getCityId());
            houseDetailsDTO.setCityName(house.getCityName());
            houseDetailsDTO.setSquare(house.getSquare());
            houseDetailsDTO.setHouseId(house.getId());
            houseDetailsDTO.setResidential(house.getResidential());
            houseDetailsDTO.setImage(address + house.getImage());
            houseDetailsDTO.setHouseName(house.getHouseName());
            houseDetailsDTO.setOptionalLabel(house.getOptionalLabel());
            houseDetailsDTO.setVisitState(house.getVisitState());
            houseDetailsDTO.setNoNumberHouseName(house.getNoNumberHouseName());
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
            houseDetailsDTO.setTotalPrice(house.getMoney());
            return ServerResponse.createBySuccess("查询成功", houseDetailsDTO);
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
            houseDetailsDTO.setImage(address + house.getImage());
            houseDetailsDTO.setHouseName(house.getHouseName());
            houseDetailsDTO.setOptionalLabel(house.getOptionalLabel());
            houseDetailsDTO.setVisitState(house.getVisitState());
            houseDetailsDTO.setNoNumberHouseName(house.getNoNumberHouseName());
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
                request.setAttribute(Constants.CITY_ID, house.getCityId());
                ServerResponse serverResponse = budgetMaterialAPI.getHouseBudgetStageCost(request, house.getCityId(), houseId, houseFlow.getWorkerTypeId());
                JSONArray pageInfo = (JSONArray) serverResponse.getResultObj();
                if (!CommonUtil.isEmpty(pageInfo)) {
                    List<BudgetStageCostDTO> budgetStageCostDTOS = pageInfo.toJavaList(BudgetStageCostDTO.class);
                    for (BudgetStageCostDTO budgetStageCostDTO : budgetStageCostDTOS) {
                        totalPrice = totalPrice.add(budgetStageCostDTO.getTotalAmount());
                    }
                    if (budgetStageCostDTOS.size() > 0) {
                        map.put("workers", serverResponse.getResultObj());
                        mapList.add(map);
                    }
                }
            }
            houseDetailsDTO.setMapList(mapList);
            houseDetailsDTO.setTotalPrice(totalPrice);
            if (!CommonUtil.isEmpty(house.getOptionalLabel())) {
                List<OptionalLabelDTO> fieldValues = new ArrayList<>();
                String[] optionalLabel = house.getOptionalLabel().split(",");
//                for (String s : optionalLabel) {
//                    fieldValues.add(s);
//                };
                List<OptionalLabel> optionalLabels = optionalLabelMapper.selectAll();
                for (OptionalLabel label : optionalLabels) {
                    OptionalLabelDTO optionalLabelDTO = new OptionalLabelDTO();
                    optionalLabelDTO.setId(label.getId());
                    optionalLabelDTO.setLabelName(label.getLabelName());
                    optionalLabelDTO.setStatus("1");
                    for (String s : optionalLabel) {
                        if (s.equals(label.getId())) {
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
     * 工地标签详情
     */
    public ServerResponse getHouseLabels(HttpServletRequest request, String houseId) {
        try {
            House house = houseMapper.selectByPrimaryKey(houseId);
            List<OptionalLabelDTO> fieldValues = new ArrayList<>();
            List<OptionalLabel> optionalLabels = optionalLabelMapper.selectAll();
            for (OptionalLabel label : optionalLabels) {
                OptionalLabelDTO optionalLabelDTO = new OptionalLabelDTO();
                optionalLabelDTO.setId(label.getId());
                optionalLabelDTO.setLabelName(label.getLabelName());
                optionalLabelDTO.setStatus("1");
                if (!CommonUtil.isEmpty(house.getOptionalLabel())) {
                    String[] optionalLabel = house.getOptionalLabel().split(",");
                    for (String s : optionalLabel) {
                        if (s.equals(label.getId())) {
                            optionalLabelDTO.setStatus("0");
                            break;
                        }
                    }
                }
                fieldValues.add(optionalLabelDTO);
            }
            return ServerResponse.createBySuccess("查询成功", fieldValues);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取数据失败");
        }
    }
}
