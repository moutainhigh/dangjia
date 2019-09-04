package com.dangjia.acg.service.sale.royalty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.auth.config.RedisSessionDAO;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.sale.royalty.DjAreaMatchSetupDTO;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.sale.*;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import com.dangjia.acg.modle.sale.royalty.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 提成配置模块
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 2019/7/26
 * Time: 16:16
 */
@Service
public class RoyaltyService {
    @Autowired
    private RoyaltyMapper royaltyMapper;

    @Autowired
    private SurfaceMapper surfaceMapper;
    @Autowired
    private DjRoyaltyMatchMapper djRoyaltyMatchMapper;
    private static Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);

    @Autowired
    private DjAreaMatchMapper djAreaMatchMapper;
    @Autowired
    private DjAreaMatchSetupMapper djAreaMatchSetupMapper;

    @Autowired
    private ResidentialBuildingMapper residentialBuildingMapper;

    @Autowired
    private IModelingVillageMapper iModelingVillageMapper;

    /**
     * 查询提成列表
     *
     * @return
     */
    public ServerResponse queryRoyaltySurface(PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<BaseEntity> baseEntityList = royaltyMapper.queryRoyaltySurface();
        if (baseEntityList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询提成列表", new PageInfo(baseEntityList));
    }

    /**
     * 新增提成信息
     *
     * @param lists
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addRoyaltyData(String lists) {
        DjRoyaltySurface djRoyaltySurface = new DjRoyaltySurface();
        JSONArray list = JSON.parseArray(lists);
        //插入提成配置总表
        if (surfaceMapper.insert(djRoyaltySurface) > 0) {
            //循环插入提成配置详情表
            DjRoyaltyDetailsSurface djr = new DjRoyaltyDetailsSurface();
            djr.setVillageId(djRoyaltySurface.getId());
            for (int i = 0; i < list.size(); i++) {
                djr = new DjRoyaltyDetailsSurface();
                djr.setVillageId(djRoyaltySurface.getId());
                djr.setCreateDate(new Date());
                JSONObject JS = list.getJSONObject(i);
                djr.setStartSingle(JS.getInteger("startSingle"));
                djr.setOverSingle(JS.getInteger("overSingle"));
                djr.setRoyalty(JS.getInteger("royalty"));
                royaltyMapper.insert(djr);
            }
            return ServerResponse.createBySuccessMessage("提交成功");
        }
        return ServerResponse.createBySuccessMessage("提交失败");
    }

    /**
     * 查询提成详情
     *
     * @param id
     * @return
     */
    public ServerResponse queryRoyaltyData(String id) {
        Example example = new Example(DjRoyaltyDetailsSurface.class);
        example.createCriteria().andEqualTo(DjRoyaltyDetailsSurface.VILLAGE_ID, id)
                .andEqualTo(DjRoyaltyDetailsSurface.DATA_STATUS, 0);
        example.orderBy(DjRoyaltyDetailsSurface.START_SINGLE).asc();
        List<DjRoyaltyDetailsSurface> djRoyaltyDetailsSurfaces = royaltyMapper.selectByExample(example);
        if (djRoyaltyDetailsSurfaces.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询提成列表", djRoyaltyDetailsSurfaces);
    }


    /**
     * 房子竣工拿提成
     * @param houseId
     */
    public void endRoyalty(String houseId){
        Example example=new Example(DjRoyaltyMatch.class);
        example.createCriteria().andEqualTo(DjRoyaltyMatch.HOUSE_ID,houseId)
                .andNotEqualTo(DjRoyaltyMatch.ORDER_STATUS,1);
        logger.info("houseId========================================"+houseId);
        List<DjRoyaltyMatch> djRoyaltyMatches = djRoyaltyMatchMapper.selectByExample(example);
        logger.info("djRoyaltyMatches========================================"+djRoyaltyMatches);
        for (DjRoyaltyMatch djRoyaltyMatch : djRoyaltyMatches) {
            logger.info("djRoyaltyMatch.getHouseId()========================================"+djRoyaltyMatch.getHouseId());
            DjRoyaltyMatch djRoyaltyMatch1=new DjRoyaltyMatch();
            djRoyaltyMatch1.setDataStatus(0);
            djRoyaltyMatch1.setOrderStatus(1);
            djRoyaltyMatch1.setUserId(djRoyaltyMatch.getUserId());
            djRoyaltyMatch1.setHouseId(djRoyaltyMatch.getHouseId());
            if(djRoyaltyMatch.getBranchRoyalty() != null){
                djRoyaltyMatch1.setMonthRoyalty((int) (djRoyaltyMatch.getBranchRoyalty()*0.25));
                djRoyaltyMatch1.setMeterRoyalty((int) (djRoyaltyMatch.getBranchRoyalty()*0.25)+djRoyaltyMatch.getMeterRoyalty());
            }else{
                djRoyaltyMatch1.setMonthRoyalty((int) (djRoyaltyMatch.getArrRoyalty()*0.25));
                djRoyaltyMatch1.setMeterRoyalty((int) (djRoyaltyMatch.getArrRoyalty()*0.25)+djRoyaltyMatch.getMeterRoyalty());
            }
            djRoyaltyMatch1.setArrRoyalty(djRoyaltyMatch.getArrRoyalty());
            djRoyaltyMatchMapper.insert(djRoyaltyMatch1);
        }
    }



    /**
     * 新增楼栋提成信息
     *
     * @param lists
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addAreaMatch(String lists,
                                       String villageId,
                                       String villageName,
                                       String buildingName,
                                       String buildingId,
                                       String resourceId) {
        logger.info("编辑删除原来的信息========================================"+!CommonUtil.isEmpty(resourceId));
        if(!CommonUtil.isEmpty(resourceId)){
            logger.info("编辑删除原来的信息1========================================"+resourceId);
            Example example = new Example(DjAreaMatch.class);
            example.createCriteria().andEqualTo(DjAreaMatch.RESOURCE_ID, resourceId);
            djAreaMatchMapper.deleteByExample(example);

//            Example example1 = new Example(DjAreaMatchSetup.class);
//            example.createCriteria().andEqualTo(DjAreaMatchSetup.RESOURCE_ID, resourceId);
            Map<String,Object> map = new HashMap<>();
            map.put("resourceId",resourceId);
            djAreaMatchSetupMapper.deleteBuildingId(map);
        }

        String resource = ((int)(Math.random() * 50000000) + 50000000 + "" + System.currentTimeMillis());
        String[] arr = buildingId.split(",");
        for (String s : arr) {
            DjAreaMatch djAreaMatch = new DjAreaMatch();
            djAreaMatch.setResourceId(resource);
            djAreaMatch.setVillageId(villageId);
            djAreaMatch.setVillageName(villageName);
            djAreaMatch.setBuildingId(s);
            ResidentialBuilding str = residentialBuildingMapper.selectByPrimaryKey(s);
            if(str != null){
                djAreaMatch.setBuildingName(str.getBuilding());
            }
            djAreaMatch.setVbName(villageName + str.getBuilding());
            //插入提成配置总表
            djAreaMatchMapper.insert(djAreaMatch);
        }


        JSONArray list = JSON.parseArray(lists);
        DjAreaMatchSetup djr = new DjAreaMatchSetup();
        for (int i = 0; i < list.size(); i++) {
            djr = new DjAreaMatchSetup();
            djr.setResourceId(resource);
            djr.setCreateDate(new Date());
            djr.setModifyDate(new Date());
            JSONObject JS = list.getJSONObject(i);
            djr.setStartSingle(JS.getInteger("startSingle"));
            djr.setOverSingle(JS.getInteger("overSingle"));
            djr.setRoyalty(JS.getInteger("royalty"));
            djr.setVillageId(villageId);
            djr.setBuildingId(buildingId);
            djAreaMatchSetupMapper.insert(djr);
        }
        return ServerResponse.createBySuccessMessage("提交成功");
    }


    /**
     * 查询楼栋配置详情
     * @param resourceId
     * @return
     */
    public ServerResponse queryAreaMatchInFo(String resourceId){

        Example example = new Example(DjAreaMatchSetup.class);
        example.createCriteria().andEqualTo(DjAreaMatchSetup.RESOURCE_ID, resourceId)
                .andEqualTo(DjAreaMatchSetup.DATA_STATUS, 0);
        example.orderBy(DjAreaMatchSetup.START_SINGLE).asc();
        List<DjAreaMatchSetup> djRoyaltyDetailsSurfaces = djAreaMatchSetupMapper.selectByExample(example);

        DjAreaMatchSetupDTO djAreaMatchSetupDTO = new DjAreaMatchSetupDTO();

        if(!djRoyaltyDetailsSurfaces.isEmpty()){
            //封装楼栋list
            List<Map<Object,Object>> buildingList = new ArrayList<>();
            String s = djRoyaltyDetailsSurfaces.get(0).getBuildingId();
            String[] arr = s.split(",");
            for (String ss:arr) {
                Map<Object,Object> BuildingMap  = new HashMap<>();
                ResidentialBuilding str = residentialBuildingMapper.selectByPrimaryKey(ss);
                BuildingMap.put("buildingId",ss);
                BuildingMap.put("buildingName",str.getBuilding());
                buildingList.add(BuildingMap);
            }

            //封装小区list
            List<Map<Object,Object>> VillageList = new ArrayList<>();
            Map<Object,Object> villageMap  = new HashMap<>();
            String villageId = djRoyaltyDetailsSurfaces.get(0).getVillageId();
            ModelingVillage modelingVillage = iModelingVillageMapper.selectByPrimaryKey(villageId);
            villageMap.put("villageId",villageId);
            villageMap.put("villageName",modelingVillage.getName());
            VillageList.add(villageMap);

            djAreaMatchSetupDTO.setDjAreaMatchSetups(djRoyaltyDetailsSurfaces);
            djAreaMatchSetupDTO.setBuildingList(buildingList);
            djAreaMatchSetupDTO.setVillageList(VillageList);
        }


        if (djAreaMatchSetupDTO == null) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询提成列表", djAreaMatchSetupDTO);
    }


    /**
     * 查询楼栋配置列表
     * @param villageId
     * @return
     */
    public ServerResponse queryAreaMatch(String villageId){
        Example example=new Example(DjAreaMatch.class);
        Example.Criteria criteria = example.createCriteria().andEqualTo(DjAreaMatch.DATA_STATUS, 0);
        if(!CommonUtil.isEmpty(villageId)){
            criteria.andEqualTo(DjAreaMatch.VILLAGE_ID,villageId);
        }
        return ServerResponse.createBySuccess("查询成功",djAreaMatchMapper.selectByExample(example));
    }


    /**
     * 删除楼栋配置
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse delAreaMatch(String id){

        DjAreaMatch djAreaMatch=new DjAreaMatch();
        djAreaMatch.setId(id);
        djAreaMatch.setDataStatus(1);
        djAreaMatchMapper.updateByPrimaryKeySelective(djAreaMatch);

        //查询提成所有信息
        DjAreaMatch dd = djAreaMatchMapper.selectByPrimaryKey(id);
        //查询楼栋id
        Example example = new Example(DjAreaMatch.class);
        example.createCriteria().andEqualTo(DjAreaMatch.RESOURCE_ID,dd.getResourceId()).
                andEqualTo(DjAreaMatch.DATA_STATUS, 0);
        List<DjAreaMatch> list = djAreaMatchMapper.selectByExample(example);


        String str="";
        if(list.size() > 1){
            String s="";
            for (DjAreaMatch ds : list) {
                if(ds != null){
                    s += ds.getBuildingId()+",";
                }
            }
            str = s.substring(0, s.length()-1);
        }else if(list.size() ==1){
            str = list.get(0).getBuildingId();
        }

        Map<String,Object> map = new HashMap<>();
        map.put("resourceId",dd.getResourceId());
        map.put("buildingId",str);

        if("".equals(str)){
            djAreaMatchSetupMapper.deleteBuildingId(map);
        }else{
            djAreaMatchSetupMapper.upDateBuildingId(map);
        }

        return ServerResponse.createBySuccessMessage("删除成功");
    }


}
