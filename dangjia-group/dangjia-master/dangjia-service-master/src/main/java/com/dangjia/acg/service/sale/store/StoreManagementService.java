package com.dangjia.acg.service.sale.store;

import com.dangjia.acg.auth.config.RedisSessionDAO;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.sale.residential.ResidentialRangeDTO;
import com.dangjia.acg.dto.sale.store.StoreUserDTO;
import com.dangjia.acg.mapper.clue.ClueMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.sale.ResidentialBuildingMapper;
import com.dangjia.acg.mapper.sale.ResidentialRangeMapper;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.mapper.store.IStoreUserMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import com.dangjia.acg.modle.sale.residential.ResidentialRange;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.sale.SaleService;
import com.dangjia.acg.util.Utils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:58
 */
@Service
public class StoreManagementService {

    @Autowired
    private IStoreUserMapper iStoreUserMapper;
    @Autowired
    private IModelingVillageMapper modelingVillageMapper;//小区
    @Autowired
    private ResidentialBuildingMapper residentialBuildingMapper;
    @Autowired
    private SaleService saleService;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IStoreMapper iStoreMapper;
    @Autowired
    private ResidentialRangeMapper residentialRangeMapper;
    @Autowired
    private ICustomerMapper iCustomerMapper;
    @Autowired
    private ClueMapper clueMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private UserMapper userMapper;
    private static Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);
    /**
     * 门店管理页
     *
     * @param userToken
     * @param pageDTO
     * @return
     */
    public ServerResponse storeManagementPage(String userToken, PageDTO pageDTO) {
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        object = saleService.getStore(accessToken.getUserId());
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Store store = (Store) object;
        List<StoreUserDTO> storeUserDTOS = iStoreUserMapper.getStoreUsers(store.getId(), null, 4);
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        for (StoreUserDTO storeUserDTO : storeUserDTOS) {
            String imageUrl = storeUserDTO.getUserHead();
            storeUserDTO.setUserHead(CommonUtil.isEmpty(imageUrl) ? null : (imageAddress + imageUrl));
        }
        List<ResidentialRangeDTO> residentialRangeDTOList = new ArrayList<>();
        PageInfo pageResult=new PageInfo();
        if (!CommonUtil.isEmpty(store.getVillages())) {
            Example example = new Example(ModelingVillage.class);
            example.createCriteria().andIn(ModelingVillage.ID, Arrays.asList(store.getVillages().split(",")));
            example.orderBy(ModelingVillage.CREATE_DATE).desc();
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<ModelingVillage> modelingVillages = modelingVillageMapper.selectByExample(example);
            pageResult = new PageInfo(modelingVillages);
            for (ModelingVillage modelingVillage : modelingVillages) {
                ResidentialRangeDTO residentialRangeDTO = new ResidentialRangeDTO();
                residentialRangeDTO.setVillageId(modelingVillage.getId());
                residentialRangeDTO.setVillagename(modelingVillage.getName());
                example = new Example(ResidentialBuilding.class);
                example.createCriteria().andEqualTo(ResidentialBuilding.STORE_ID, store.getId())
                        .andEqualTo(ResidentialBuilding.VILLAGE_ID, modelingVillage.getId());
                residentialRangeDTO.setList(residentialBuildingMapper.selectByExample(example));
                residentialRangeDTOList.add(residentialRangeDTO);
            }
            pageResult.setList(residentialRangeDTOList);
        }
        Map<String, Object> resultMap = new HashedMap();
        resultMap.put("storeUsers", storeUserDTOS);
        resultMap.put("residentialRangeDTOList", pageResult);
        resultMap.put("managerId", store.getUserId());
        resultMap.put("storeId", store.getId());
        return ServerResponse.createBySuccess("查询成功", resultMap);
    }


    /**
     * 添加楼栋
     *
     * @param villageId
     * @param modifyDate
     * @param building
     * @param storeId
     * @return
     */
    public ServerResponse addBuilding(String villageId, Date modifyDate, String building, String storeId) {
        Example example = new Example(ResidentialBuilding.class);
        example.createCriteria().andEqualTo(ResidentialBuilding.VILLAGE_ID, villageId)
                .andEqualTo(ResidentialBuilding.STORE_ID, storeId)
                .andEqualTo(ResidentialBuilding.BUILDING, building);
        if (residentialBuildingMapper.selectByExample(example).size() > 0) {
            return ServerResponse.createByErrorMessage("该楼栋已存在");
        }
        ResidentialBuilding residentialBuilding = new ResidentialBuilding();
        residentialBuilding.setVillageId(villageId);
        residentialBuilding.setModifyDate(CommonUtil.isEmpty(modifyDate) ? null : modifyDate);
        residentialBuilding.setBuilding(building);
        residentialBuilding.setDataStatus(0);
        residentialBuilding.setStoreId(storeId);
        if (residentialBuildingMapper.insert(residentialBuilding) > 0) {
            return ServerResponse.createBySuccessMessage("添加成功");
        }
        return ServerResponse.createByErrorMessage("添加失败");
    }


    /**
     * 删除楼栋
     *
     * @param buildingId
     * @return
     */
    public ServerResponse delBuilding(String buildingId) {
        if (residentialBuildingMapper.deleteByPrimaryKey(buildingId) > 0) {
            Example example = new Example(ResidentialRange.class);
            example.createCriteria().andLike(ResidentialRange.BUILDING_ID, "%" + buildingId + "%");
            List<ResidentialRange> list = residentialRangeMapper.selectByExample(example);
            for (ResidentialRange residentialRange : list) {
                if (residentialRange.getBuildingId().contains(",")) {
                    residentialRange.setBuildingId(residentialRange.getBuildingId().replace("," + buildingId, null));
                    residentialRangeMapper.updateByPrimaryKeySelective(residentialRange);
                    return ServerResponse.createBySuccessMessage("删除成功");
                } else {
                    residentialRangeMapper.deleteByPrimaryKey(residentialRange.getId());
                    return ServerResponse.createBySuccessMessage("删除成功");
                }
            }
            return ServerResponse.createBySuccessMessage("删除成功");
        }
        return ServerResponse.createByErrorMessage("删除失败");
    }


    /**
     * 修改楼栋
     *
     * @param buildingId
     * @param residentialBuilding
     * @return
     */
    public ServerResponse updatBuilding(String buildingId, ResidentialBuilding residentialBuilding) {
        ResidentialBuilding residentialBuilding1 = residentialBuildingMapper.selectByPrimaryKey(buildingId);
        if (!residentialBuilding1.getBuilding().equals(residentialBuilding.getBuilding())) {
            Example example = new Example(ResidentialBuilding.class);
            example.createCriteria().andEqualTo(ResidentialBuilding.BUILDING, residentialBuilding.getBuilding())
                    .andEqualTo(ResidentialBuilding.DATA_STATUS, 0);
            if (residentialBuildingMapper.selectByExample(example).size() > 0) {
                return ServerResponse.createByErrorMessage("该楼栋已存在");
            }
        }
        residentialBuilding.setId(buildingId);
        if (residentialBuildingMapper.updateByPrimaryKeySelective(residentialBuilding) > 0) {
            return ServerResponse.createBySuccessMessage("修改成功");
        }
        return ServerResponse.createByErrorMessage("修改失败");
    }


    public ServerResponse BuildingList(String storeId, PageDTO pageDTO, String userId) {
        Store store = iStoreMapper.selectByPrimaryKey(storeId);
        if (null != store) {
            Example example = new Example(ResidentialRange.class);
            example.createCriteria().andNotEqualTo(ResidentialRange.USER_ID, userId);//过滤已分配楼栋
            List<ResidentialRange> residentialRanges = residentialRangeMapper.selectByExample(example);
            List<String> slist = new ArrayList<>();
            for (ResidentialRange residentialRange : residentialRanges) {
                if (!CommonUtil.isEmpty(residentialRange.getBuildingId())) {
                    slist.addAll(Arrays.asList(residentialRange.getBuildingId().split(",")));
                }
            }
            List<ModelingVillage> modelingVillages=new ArrayList<>();
            if (!CommonUtil.isEmpty(store.getVillages())) {
                example = new Example(ModelingVillage.class);
                example.createCriteria().andIn(ModelingVillage.ID, Arrays.asList(store.getVillages().split(",")));
                modelingVillages = modelingVillageMapper.selectByExample(example);
            }
            List residentialRangeDTOList = new ArrayList();
            example = new Example(ResidentialRange.class);
            example.createCriteria().andEqualTo(ResidentialRange.USER_ID, userId);
            List<ResidentialRange> residentialRanges1 = residentialRangeMapper.selectByExample(example);
            List<String> slist1 = new ArrayList<>();
            for (ResidentialRange residentialRange : residentialRanges1) {
                if (!CommonUtil.isEmpty(residentialRange.getBuildingId())) {
                    slist1.addAll(Arrays.asList(residentialRange.getBuildingId().split(",")));
                }
            }
            PageInfo pageResult=null;
            for (ModelingVillage modelingVillage : modelingVillages) {
                example = new Example(ResidentialBuilding.class);
                Example.Criteria criteria = example.createCriteria();
//                criteria.andEqualTo(ResidentialBuilding.STORE_ID, store.getId());
                criteria.andEqualTo(ResidentialBuilding.VILLAGE_ID, modelingVillage.getId());
                if (slist.size() > 0) {
                    criteria.andNotIn(ResidentialBuilding.ID, slist);
                }
                PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
                List<ResidentialBuilding> residentialBuildings = residentialBuildingMapper.selectByExample(example);
                pageResult = new PageInfo(residentialBuildings);
                if (residentialBuildings.size() > 0) {
                    ResidentialRangeDTO residentialRangeDTO = new ResidentialRangeDTO();
                    residentialRangeDTO.setVillageId(modelingVillage.getId());
                    residentialRangeDTO.setVillagename(modelingVillage.getName());
                    for (ResidentialBuilding residentialBuilding : residentialBuildings) {
                        for (String s : slist1) {
                            if (s.equals(residentialBuilding.getId())) {
                                residentialBuilding.setChecked("1");
                            }
                        }
                    }
                    residentialRangeDTO.setList(residentialBuildings);
                    residentialRangeDTOList.add(residentialRangeDTO);
                }
            }
            pageResult.setList(residentialRangeDTOList);
            if (residentialRangeDTOList.size() > 0) {
                return ServerResponse.createBySuccess("查询成功", pageResult);
            }
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createByErrorMessage("门店不存在");
    }


    /**
     * 分配销售
     *
     * @param clueId
     * @return
     */
    public ServerResponse upDateCusService(String clueId, String cusSerice, String mcId, Integer phaseStatus) {
        String url = configUtil.getValue(SysConfig.PUBLIC_SALE_APP_ADDRESS, String.class);
        if (phaseStatus == 0) {
            Clue clue = clueMapper.selectByPrimaryKey(clueId);
            if (clue == null) {
                return ServerResponse.createByErrorMessage("找不到此线索");
            }
            clue.setCusService(cusSerice);
            clue.setStage(1);
            clue.setModifyDate(new Date());
            clueMapper.updateByPrimaryKeySelective(clue);
            MainUser user = userMapper.selectByPrimaryKey(cusSerice);
            if (user != null && !CommonUtil.isEmpty(user.getMemberId()))
                configMessageService.addConfigMessage(AppType.SALE, user.getMemberId(), "分配提醒",
                        "您收到一个店长分配的客户，请及时跟进。", 0, url
                                + Utils.getCustomerDetails("", clueId, phaseStatus, "0"));
            return ServerResponse.createBySuccessMessage("分配成功");
        } else {
            Clue clue = clueMapper.selectByPrimaryKey(clueId);
            if (clue != null) {
                clue.setCusService(cusSerice);
                clue.setStage(1);
                clue.setModifyDate(new Date());
                clueMapper.updateByPrimaryKeySelective(clue);
            }
            Customer customer = iCustomerMapper.selectByPrimaryKey(mcId);
            if (customer == null) {
                return ServerResponse.createByErrorMessage("找不到此客户");
            }
            customer.setUserId(cusSerice);
            customer.setStage(1);
            customer.setModifyDate(new Date());
            iCustomerMapper.updateByPrimaryKeySelective(customer);
            MainUser user = userMapper.selectByPrimaryKey(cusSerice);
            if (user != null && !CommonUtil.isEmpty(user.getMemberId()))
                configMessageService.addConfigMessage(AppType.SALE, user.getMemberId(), "分配提醒",
                        "您收到一个店长分配的客户，请及时跟进。", 0, url
                                + Utils.getCustomerDetails(customer.getMemberId(), "", phaseStatus, "1"));
            return ServerResponse.createBySuccessMessage("分配成功");
        }
    }

    /**
     * 转出客户
     *
     * @param clueId 线索id
     * @param mcId   客户基础id
     * @param cityId 城市id
     * @return
     */
    public ServerResponse upDateCustomer(String clueId, String mcId, String cityId, Integer phaseStatus) {
        try {
            Clue clue = new Clue();
            Customer customer = new Customer();
            clue.setCusService(null);
            clue.setStoreId(null);
            if (!CommonUtil.isEmpty(clueId)) {
                clue.setId(clueId);
            }
            if (!CommonUtil.isEmpty(cityId)) {
                clue.setCityId(cityId);
                customer.setCityId(cityId);
            }
            clue.setModifyDate(new Date());
            clue.setStage(1);
            //转出
            clue.setTurnStatus(1);
            //转出修改线索表
            clueMapper.updateByPrimaryKeySelective(clue);
            if (phaseStatus == 1) {
                if (!CommonUtil.isEmpty(mcId)) {
                    customer.setId(mcId);
                }
                customer.setModifyDate(new Date());
                customer.setUserId(null);
                customer.setStoreId(null);
                customer.setStage(1);
                //转出
                customer.setTurnStatus(1);
                //转出修改客户基础表
                iCustomerMapper.updateByPrimaryKey(customer);
            }
            return ServerResponse.createBySuccessMessage("转出成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("转出成功");
        }

    }

    /**
     * 小区所有楼栋
     * @param villageId
     * @return
     */
    public ServerResponse getBuildingByVillageId(String villageId){
        return ServerResponse.createBySuccess("查询成功",residentialBuildingMapper.getBuildingByVillageId(villageId));
    }

}
