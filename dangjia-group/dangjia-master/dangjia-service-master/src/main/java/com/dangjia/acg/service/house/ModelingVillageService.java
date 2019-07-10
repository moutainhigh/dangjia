package com.dangjia.acg.service.house;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.house.VillageClassifyDTO;
import com.dangjia.acg.dto.house.VillageDTO;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IModelingLayoutMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.ModelingLayout;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.other.City;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 15:19
 */
@Service
public class ModelingVillageService {

    @Autowired
    private ICityMapper cityMapper;//城市
    @Autowired
    private IModelingVillageMapper modelingVillageMapper;//小区
    @Autowired
    private IModelingLayoutMapper modelingLayoutMapper;//户型
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private ConfigUtil configUtil;


    private static Logger LOG = LoggerFactory.getLogger(ModelingVillageService.class);
    /****
     * 注入配置
     */
    @Autowired
    private RedisClient redisClient;

    public ServerResponse getCityList() {
        Example example = new Example(City.class);
        example.createCriteria()
                .andEqualTo(City.STATE, "0");
        List<City> cityList = cityMapper.selectByExample(example);
        if (cityList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询列表成功", cityList);
    }

    public ServerResponse getVillageList(HttpServletRequest request, String cityId) {
        List<Map<String, Object>> mapList = modelingVillageMapper.getVillageList(cityId);
        if (mapList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询列表成功", mapList);
    }

    public ServerResponse getVillageAllListByCityId(HttpServletRequest request, PageDTO pageDTO, String cityId, String likeVillageName) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<ModelingVillage> allVillageList = modelingVillageMapper.getAllVillage(cityId, likeVillageName);
        if (allVillageList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(allVillageList);
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (ModelingVillage modelingVillage : allVillageList) {
            Map<String, Object> modelingVillageMap = BeanUtils.beanToMap(modelingVillage);
            List<ModelingLayout> modelingLayoutList = modelingLayoutMapper.queryModelingLayoutByVillageId(modelingVillage.getId());
            List<Map<String, Object>> modelingLayoutMapList = new ArrayList<>();
            for (ModelingLayout modelingLayout : modelingLayoutList) {
                Map<String, Object> modelingLayoutMap = BeanUtils.beanToMap(modelingLayout);
                modelingLayoutMap.put("imageUrl", address + modelingLayout.getImage());
                modelingLayoutMapList.add(modelingLayoutMap);
                modelingVillageMap.put("modelingLayoutList", modelingLayoutMapList);
            }
            mapList.add(modelingVillageMap);
        }
        pageResult.setList(mapList);
        return ServerResponse.createBySuccess("查询小区成功", pageResult);
    }

    public ServerResponse setVillage(HttpServletRequest request, String jsonStr) {
        try {
            LOG.info("setVillage :" + jsonStr);
            JSONObject villageObj = JSONObject.parseObject(jsonStr);
            String villageId = villageObj.getString("id");//小区id
            String villageName = villageObj.getString("name");//小区name
            if (!StringUtils.isNotBlank(villageName))
                return ServerResponse.createByErrorMessage("小区名称不能为空");
            ModelingVillage modelingVillage; //新增的 小区id
            if (!StringUtils.isNotBlank(villageId)) {//没有id则新增
                modelingVillage = new ModelingVillage();
                modelingVillage.setName(villageName);//小区名称
                modelingVillage.setCityId(villageObj.getString("cityId"));//城市
                modelingVillage.setAreaName(villageObj.getString("areaName"));//区域名称
                modelingVillage.setLayoutSum(0);//户型总数
                modelingVillage.setAddress(villageObj.getString("address"));//小区详细地址
                modelingVillage.setInitials(villageObj.getString("initials"));//存放ABCD
                modelingVillage.setLocationx(villageObj.getString("locationx"));//百度定位目标x
                modelingVillage.setLocationy(villageObj.getString("locationy"));//百度定位目标y
                modelingVillageMapper.insert(modelingVillage);
            } else {//修改
                modelingVillage = modelingVillageMapper.selectByPrimaryKey(villageId);
                if (!modelingVillage.getName().equals(villageName))
                    modelingVillage.setName(villageName);//小区名称
                modelingVillage.setCityId(villageObj.getString("cityId"));//城市
                modelingVillage.setAreaName(villageObj.getString("areaName"));//区域名称
                modelingVillage.setAddress(villageObj.getString("address"));//小区详细地址
                modelingVillage.setInitials(villageObj.getString("initials"));//存放ABCD
                modelingVillage.setLocationx(villageObj.getString("locationx"));//百度定位目标x
                modelingVillage.setLocationy(villageObj.getString("locationy"));//百度定位目标y
                modelingVillage.setModifyDate(new Date());
                modelingVillageMapper.updateByPrimaryKeySelective(modelingVillage);
            }
//            遍历户型对象 数组  ， 一个小区 对应 多个户型
            String modelingLayoutList = villageObj.getString("modelingLayoutList");
            JSONArray modelingLayoutArr = JSONArray.parseArray(modelingLayoutList);
            for (int i = 0; i < modelingLayoutArr.size(); i++) {//遍历户型
                JSONObject obj = modelingLayoutArr.getJSONObject(i);
                String layoutId = obj.getString("id");//户型id
                String name = obj.getString("name");//户型名称
                if (CommonUtil.isEmpty(name))
                    return ServerResponse.createByErrorMessage("户型名称不能为空");
                ModelingLayout modelingLayout;
                if (CommonUtil.isEmpty(layoutId)) {//没有id则新增
                    if (CommonUtil.isEmpty(modelingVillage.getId()))//没有id则新增
                        return ServerResponse.createByErrorMessage("小区id不能为null");
                    modelingLayout = new ModelingLayout();
                    modelingLayout.setVillageId(modelingVillage.getId());//设置 关联小区id
                    modelingLayout.setName(name);//户型name
                    modelingLayout.setImage(obj.getString("image"));//户型图片
                    modelingLayout.setBuildSquare(obj.getString("buildSquare"));//建筑面积
                    modelingLayoutMapper.insert(modelingLayout);
                    modelingVillage.setLayoutSum(modelingVillage.getLayoutSum() + 1);//累计小区户型总数
                    modelingVillage.setModifyDate(new Date());
                    modelingVillageMapper.updateByPrimaryKeySelective(modelingVillage);
                } else {
                    modelingLayout = modelingLayoutMapper.selectByPrimaryKey(layoutId);
                    if (!modelingLayout.getName().equals(name)) {
                        if (modelingLayoutMapper.queryModelingLayoutByName(layoutId, name).size() > 0)
                            return ServerResponse.createByErrorMessage("户型名称已存在");
                    }
                    modelingLayout.setName(name);//户型名称
                    modelingLayout.setImage(obj.getString("image"));//户型图片
                    modelingLayout.setBuildSquare(obj.getString("buildSquare"));//建筑面积
                    modelingLayout.setModifyDate(new Date());
                    modelingLayoutMapper.updateByPrimaryKeySelective(modelingLayout);
                }
            }
            String[] deleteLayoutIds = villageObj.getString("deleteLayoutIds").split(",");//要删除的户型id数组，逗号分隔
            for (String deleteLayoutId : deleteLayoutIds) {
                if (modelingLayoutMapper.selectByPrimaryKey(deleteLayoutId) != null) {
                    if (modelingLayoutMapper.deleteByPrimaryKey(deleteLayoutId) < 0)
                        return ServerResponse.createByErrorMessage("删除id：" + deleteLayoutId + "失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    public ServerResponse getLayoutList(HttpServletRequest request, String villageId) {
        Example example = new Example(ModelingLayout.class);
        example.createCriteria().andEqualTo("villageId", villageId);
        List<ModelingLayout> modelingLayoutList = modelingLayoutMapper.selectByExample(example);
        if (modelingLayoutList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询列表成功", modelingLayoutList);
    }

    public ServerResponse getHouseList(HttpServletRequest request, String modelingLayoutId) {
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.MODELING_LAYOUT_ID, modelingLayoutId)
                .andEqualTo(House.DATA_STATUS, 0);
        List<House> houseList = houseMapper.selectByExample(example);
        if (houseList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询列表成功", houseList);
    }

    /**
     * 根据城市查询小区
     *
     * @param cityId
     * @return
     */
    public ServerResponse getAllVillageByCity(String cityId) {
        try {
            List<VillageDTO> hotList = new ArrayList<>();//热门小区集合
            List<VillageClassifyDTO> villageClassifyDTOList = new ArrayList<>();//返回集
            Example example = new Example(ModelingVillage.class);
            example.createCriteria().andEqualTo("cityId", cityId).andGreaterThan("layoutSum", 2);
            example.orderBy("layoutSum").desc();
            PageHelper.startPage(0, 10);
            List<ModelingVillage> mvHotlist = modelingVillageMapper.selectByExample(example);
            for (ModelingVillage modelingVillage : mvHotlist) {
                VillageDTO villageDTO = new VillageDTO();//小区对象
                villageDTO.setVillageId(modelingVillage.getId());
                villageDTO.setInitials(modelingVillage.getInitials().toUpperCase());
                villageDTO.setName(modelingVillage.getName());
                hotList.add(villageDTO);
            }
            VillageClassifyDTO villageClassifyDTO = new VillageClassifyDTO(); //按热门分类对象
            villageClassifyDTO.setInitials("热");
            villageClassifyDTO.setVillageDTOList(hotList);//热门集合
            villageClassifyDTOList.add(villageClassifyDTO);
            List<VillageClassifyDTO> mvlist = redisClient.getListCache("vresult:" + cityId, VillageClassifyDTO.class);
            Integer number = modelingVillageMapper.getAllVillageCount(cityId);//统计根据城市id查询小区按字母排序
            if (mvlist == null || mvlist.size() != number) {
                mvlist = modelingVillageMapper.getAllVillageDTO(cityId, "");
            }
            villageClassifyDTOList.addAll(mvlist);
            if (mvlist.size() > 0) {
                redisClient.putListCaches("vresult:" + cityId, mvlist);
            }
            return ServerResponse.createBySuccess("根据城市查询小区成功", villageClassifyDTOList);
        } catch (Exception e) {
            redisClient.deleteCache("vresult:" + cityId);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询出错");
        }
    }

    public ServerResponse getModelingVillage(HttpServletRequest request) {
        String userID = request.getParameter(Constants.USERID);
        String cityKey = redisClient.getCache(Constants.CITY_KEY + userID, String.class);
        if (CommonUtil.isEmpty(cityKey)) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        Example example = new Example(ModelingVillage.class);
        example.createCriteria().andCondition("   FIND_IN_SET(city_id,'" + cityKey + "') ").andIsNotNull(ModelingVillage.LOCATIONX);
        List<ModelingVillage> modelingVillages = modelingVillageMapper.selectByExample(example);
        if (modelingVillages.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询列表成功", modelingVillages);
    }
}
