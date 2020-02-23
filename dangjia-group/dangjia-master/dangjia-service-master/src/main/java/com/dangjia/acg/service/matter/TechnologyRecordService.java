package com.dangjia.acg.service.matter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.matter.TechnologyRecordDTO;
import com.dangjia.acg.dto.matter.WorkNodeDTO;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.matter.IMasterTechnologyMapper;
import com.dangjia.acg.mapper.matter.ITechnologyRecordMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.mapper.repair.IMendWorkerMapper;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.matter.TechnologyRecord;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.deliver.OrderSplitItemService;
import com.dangjia.acg.util.Utils;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 2018/11/20 0001
 * Time: 17:56
 */
@Service
public class TechnologyRecordService {

    protected static final Logger logger = LoggerFactory.getLogger(TechnologyRecordService.class);
    @Autowired
    private ITechnologyRecordMapper technologyRecordMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private BudgetWorkerAPI budgetWorkerAPI;
    @Autowired
    private IWarehouseMapper warehouseMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IMendWorkerMapper mendWorkerMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private OrderSplitItemService orderSplitItemService;

    @Autowired
    private IMasterStorefrontProductMapper masterStorefrontProductMapper;
    @Autowired
    private IMasterTechnologyMapper iMasterTechnologyMapper;


    /**
     * 1.31
     * 获取上传图片列表
     * applyType 0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查
     */
    public ServerResponse nodeImageList(String nodeArr, Integer applyType, String houseFlowId) {
        try {
            if (StringUtil.isEmpty(houseFlowId))
                return ServerResponse.createByErrorMessage("houseFlowId不能为空");
            Map<String, Object> returnMap = new HashMap<>();
            if (applyType == 1 || applyType == 2) {
                List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.noPassList(houseFlowId);
                returnMap.put("hint", "温馨提示：当前免费提交审核次数<font color=red>("+houseFlowApplyList.size()+"/2)</font>,金额为100元/1次的费用.");
                if (houseFlowApplyList.size() >= 2) {
                    returnMap.put("pop", "将按照100元/次自动罚款，请知晓");
                    returnMap.put("title", "审核次数已满两次");
                } else {
                    returnMap.put("pop", "");
                    returnMap.put("title", "");
                }
            } else {
                returnMap.put("hint", "");
                returnMap.put("pop", "");
                returnMap.put("title", "");
            }

            List<Map<String, Object>> listMap = new ArrayList<>();
            Map<String, Object> map;
            if (applyType == 4) {
                map = new HashMap<>();
                map.put("imageTypeId", "");
                map.put("imageTypeName", "现场照片");
                map.put("imageType", 2);
                map.put("productId", "");
                listMap.add(0, map);
                returnMap.put("listMap", listMap);
                return ServerResponse.createBySuccess("查询成功", returnMap);
            }

            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
            if (StringUtil.isNotEmpty(nodeArr)) {
                JSONArray imageObjArr = JSON.parseArray(nodeArr);
                String webAddress = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
                for (int i = 0; i < imageObjArr.size(); i++) {//上传材料照片
                    JSONObject imageObj = imageObjArr.getJSONObject(i);
                    String imageTypeName = imageObj.getString("imageTypeName");
                    String imageTypeId = imageObj.getString("imageTypeId");
                    String productId = imageObj.getString("productId");
                    if(CommonUtil.isEmpty(imageTypeName)) {
                        Technology technology = forMasterAPI.byTechnologyId(house.getCityId(), imageTypeId);
                        imageTypeName=technology.getName();
                    }
                    if(!CommonUtil.isEmpty(productId)) {
                        StorefrontProduct storefrontProduct = masterStorefrontProductMapper.selectByPrimaryKey(productId);
                        imageTypeName=storefrontProduct.getProductName()+"-"+imageTypeName;
                    }
                    map = new HashMap<>();
                    map.put("imageTypeId", imageTypeId);
                    map.put("imageTypeName",imageTypeName);
                    map.put("productId", productId);
                    map.put("imageType", 3);  //节点照片
                    map.put("url", webAddress + "gyDetail?title=工艺详情&technologyId=" + imageTypeId);  //节点照片
                    listMap.add(map);
                }
            } else {
                map = new HashMap<>();
                map.put("imageTypeId", "");
                map.put("imageTypeName", "现场照片");
                map.put("imageType", 2);
                map.put("productId", "");
                listMap.add(0, map);
            }
            map = new HashMap<>();
            map.put("imageTypeId", "");
            map.put("imageTypeName", "材料照片");
            map.put("imageType", 0);
            map.put("productId", "");
            listMap.add(listMap.size(), map);

            returnMap.put("listMap", listMap);
            return ServerResponse.createBySuccess("查询成功", returnMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 3.0
     * 管家待巡查的节点
     */
    public ServerResponse workNodeList(String userToken, String houseFlowId) {

        String webAddress = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
        House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
        if (house.getPause() != null) {
            if (house.getPause() == 1) {
                return ServerResponse.createByErrorMessage("该房子已暂停施工,请勿提交申请！");
            }
        }
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        List<WorkNodeDTO> workNodeDTOList = new ArrayList<>();
        Map<String, WorkNodeDTO> trList = new HashMap<>();
        if (houseFlow.getWorkerType() == 3) { //管家查询管家应验收节点
            Example example = new Example(TechnologyRecord.class);
            example.createCriteria().andEqualTo(TechnologyRecord.STATE, 0).andEqualTo(TechnologyRecord.HOUSE_ID, house.getId());
            example.orderBy(TechnologyRecord.CREATE_DATE).desc();
            List<TechnologyRecord> productList = technologyRecordMapper.selectByExample(example);
            if (productList != null && productList.size() > 0) {
                for (TechnologyRecord pt : productList) {
                    Technology technology = iMasterTechnologyMapper.selectByPrimaryKey(pt.getTechnologyId());
                    if(technology.getType()==0){//普通节点直接跳过
                        continue;
                    }
                    if (trList.get(pt.getProductId()) == null) {
                        trList.put(pt.getProductId(), new WorkNodeDTO());
                    }
                    WorkNodeDTO workNodeDTOA = trList.get(pt.getProductId());
                    if (workNodeDTOA.getTrList() == null) {
                        workNodeDTOA.setTrList(new ArrayList<>());
                    }
                    List<TechnologyRecordDTO> technologyRecordDTOS = workNodeDTOA.getTrList();
                    if (CommonUtil.isEmpty(workNodeDTOA.getProductId())) {
                        StorefrontProduct storefrontProduct = masterStorefrontProductMapper.selectByPrimaryKey(pt.getProductId());
                        if(storefrontProduct!=null){
                            workNodeDTOA.setProductName(storefrontProduct.getProductName());//商品名
                            workNodeDTOA.setProductId(storefrontProduct.getId());//商品名
                        }
                    }

                    TechnologyRecordDTO trd = new TechnologyRecordDTO();
                    trd.setId(pt.getId());
                    trd.setName(pt.getName());
                    trd.setState(pt.getState());
                    trd.setWorkerTypeId(pt.getWorkerTypeId());
                    trd.setProductId(pt.getProductId());
                    trd.setUrl( webAddress + "gyDetail?title=工艺详情&technologyId=" + pt.getId());
                    trd.setImage(Utils.getImageAddress(address, pt.getImage()));
                    technologyRecordDTOS.add(trd);
                    if(CommonUtil.isEmpty(workNodeDTOA.getProductId())){
                        workNodeDTOA.setProductName("其他");//商品名
                    }
                    workNodeDTOA.setTrList(technologyRecordDTOS);
                    trList.put(pt.getProductId(), workNodeDTOA);
                }
            }
        }else{
            //工匠未提交的验收节点
            List<TechnologyRecordDTO>  productList=technologyRecordMapper.selectWorkerProductInfo(houseFlow.getHouseId(),houseFlow.getWorkerTypeId(),null);
            if (productList != null && productList.size() > 0) {
                {
                    for (TechnologyRecordDTO trd : productList) {
                        if (trList.get(trd.getProductId()) == null) {
                            trList.put(trd.getProductId(), new WorkNodeDTO());
                        }
                        WorkNodeDTO workNodeDTOA = trList.get(trd.getProductId());
                        if (workNodeDTOA.getTrList() == null) {
                            workNodeDTOA.setTrList(new ArrayList<>());
                        }
                        List<TechnologyRecordDTO> technologyRecordDTOS = workNodeDTOA.getTrList();
                        if (CommonUtil.isEmpty(workNodeDTOA.getProductId())) {
                            StorefrontProduct storefrontProduct = masterStorefrontProductMapper.selectByPrimaryKey(trd.getProductId());
                            workNodeDTOA.setProductName(storefrontProduct.getProductName());//商品名
                            workNodeDTOA.setProductId(storefrontProduct.getId());//商品名
                        }
                        trd.setImage(address + trd.getImage());
                        trd.setUrl( webAddress + "gyDetail?title=工艺详情&technologyId=" + trd.getId());
                        technologyRecordDTOS.add(trd);
                        workNodeDTOA.setTrList(technologyRecordDTOS);
                        trList.put(trd.getProductId(), workNodeDTOA);
                    }
                }
            }
        }
       for(String key:trList.keySet()){
           workNodeDTOList.add(trList.get(key));
       }

        //已验收节点
        List<TechnologyRecord> checkList = technologyRecordMapper.allChecked(houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
        Map map = new HashMap();
        map.put("checkNum",checkList.size());
        //总验收点数
        List<TechnologyRecordDTO>  productList=technologyRecordMapper.selectWorkerProductInfo(houseFlow.getHouseId(),houseFlow.getWorkerTypeId(),"1");
        map.put("allNum",productList.size());
        map.put("workNodeDTOList",workNodeDTOList);
        //已验收节点
        return ServerResponse.createBySuccess("查询成功", map);
    }

    /**
     * 获取上传图片列表
     * applyType 0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查
     */
    @Deprecated
    public ServerResponse uploadingImageList(String nodeArr) {
        try {
            List<Map<String, Object>> listMap = new ArrayList<>();
            Map<String, Object> map;
            if (StringUtil.isNotEmpty(nodeArr)) {
                String[] idArr = nodeArr.split(",");
                for (String id : idArr) {
                    Technology technology = forMasterAPI.byTechnologyId("", id);
                    map = new HashMap<>();
                    map.put("imageTypeId", id);
                    map.put("imageTypeName", technology.getName());
                    map.put("imageType", 3);  //节点照片
                    listMap.add(map);
                }
            } else {
                map = new HashMap<>();
                map.put("imageTypeId", "");
                map.put("imageTypeName", "现场照片");
                map.put("imageType", 2);
                listMap.add(0, map);
            }
            map = new HashMap<>();
            map.put("imageTypeId", "");
            map.put("imageTypeName", "材料照片");
            map.put("imageType", 0);
            listMap.add(listMap.size(), map);
            return ServerResponse.createBySuccess("查询成功", listMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询验收节点
     * 供管家选择验收
     *workNodeList( userToken,  houseFlowId, Integer applyType)} constraint instead
     * @see
     * @deprecated use the standard {@link com.dangjia.acg.service.matter.TechnologyRecordService
     */
    @Deprecated
    public ServerResponse technologyRecordList(String houseFlowId) {
        try {
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
            if (house.getPause() != null) {
                if (house.getPause() == 1) {
                    return ServerResponse.createByErrorMessage("该房子已暂停施工,请勿提交申请！");
                }
            }

            //所有已进场未完工工序的节点
            List<TechnologyRecordDTO> technologyRecordDTOS = new ArrayList<>();
            JSONArray jsonArray = budgetWorkerAPI.getAllTechnologyByHouseId(house.getCityId(), house.getId());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String technologyId = object.getString("technologyId");
                String technologyName = object.getString("technologyName");
                List<TechnologyRecord> technologyRecordList = technologyRecordMapper.checkByTechnologyId(house.getId(), technologyId, "3");
                if (technologyRecordList.size() == 0) {
                    TechnologyRecordDTO dto = new TechnologyRecordDTO();
                    dto.setId(technologyId);
                    dto.setName(technologyName);
                    dto.setState(0);//未验收
                    technologyRecordDTOS.add(dto);
                }
            }

            return ServerResponse.createBySuccess("查询成功", technologyRecordDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 已进场未完工
     */
    public List<HouseFlow> unfinishedFlow(String houseId) {
        return houseFlowMapper.unfinishedFlow(houseId);
    }

    /**
     * 所有购买材料
     */
    public List<Warehouse> warehouseList(String houseId) {
        return warehouseMapper.warehouseList(houseId, null, null);
    }

    public ServerResponse getByProductId(String productId, String houseId) {
        Warehouse warehouse = warehouseMapper.getByProductId(productId, houseId);
        return ServerResponse.createBySuccess("查询成功", warehouse);
    }
    /**
     * 查询当前工匠在当前房子上的所有已购买材料
     * @param houseId 房子ID
     * @return
     */
    public ServerResponse getAllProductListByhouseMemberId(PageDTO pageDTO, String houseId, String userToken, String searchKey){
        try{
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return ServerResponse.createByErrorMessage("未找到对应的用户信息");
            }
            Member member = (Member) object;
            String workerId=member.getId();
            PageInfo orderItemList=orderSplitItemService.getOrderItemListByhouseMemberId(pageDTO,houseId,workerId,searchKey);//查询当前工匠已购买的所有材料
            if(orderItemList.getTotal()==0){
               return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                        ,ServerCode.NO_DATA.getDesc());
            }
            return ServerResponse.createBySuccess("查询成功",orderItemList);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }
}
