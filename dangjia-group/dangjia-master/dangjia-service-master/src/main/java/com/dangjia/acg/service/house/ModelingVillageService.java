package com.dangjia.acg.service.house;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.house.VillageDTO;
import com.dangjia.acg.dto.house.VillageListDTO;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IModelingLayoutMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.ModelingLayout;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.other.City;
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

    private static Logger LOG = LoggerFactory.getLogger(ModelingVillageService.class);
    /****
     * 注入配置
     */
    @Autowired
    private RedisClient redisClient;

    public ServerResponse getCityList() {
        List<City> cityList = cityMapper.selectAll();
        return ServerResponse.createBySuccess("查询列表成功", cityList);
    }

    public ServerResponse getVillageList(HttpServletRequest request, String cityId) {
        List<Map<String, Object>> mapList = modelingVillageMapper.getVillageList(cityId);
        return ServerResponse.createBySuccess("查询列表成功", mapList);
    }

    public ServerResponse getVillageDetailsList(HttpServletRequest request, String cityId) {
        List<Map<String, Object>> mapList = modelingVillageMapper.getVillageList(cityId);
        return ServerResponse.createBySuccess("查询小区成功", mapList);
    }

    public ServerResponse setVillage(HttpServletRequest request, String jsonStr) {
        try {
            LOG.info("setVillage :" + jsonStr);
            JSONObject villageObj = JSONObject.parseObject(jsonStr);

            String villageId = villageObj.getString("id");//小区id
            String villageName = villageObj.getString("name");//小区name

            if (!StringUtils.isNotBlank(villageName))
                return ServerResponse.createByErrorMessage("小区名称不能为空");

            ModelingVillage newModelingVillage = null; //新增的 小区id
            if (!StringUtils.isNotBlank(villageObj.getString("villageId")))//没有id则新增
            {
                newModelingVillage = new ModelingVillage();
                newModelingVillage.setName(villageName);//小区名称
                newModelingVillage.setCityId(villageObj.getString("cityId"));//城市
                newModelingVillage.setAreaName(villageObj.getString("areaName"));//区域名称
                newModelingVillage.setAddress(villageObj.getString("address"));//小区详细地址
                newModelingVillage.setInitials(villageObj.getString("initials"));//存放ABCD
                newModelingVillage.setLocationx(villageObj.getString("locationx"));//百度定位目标x
                newModelingVillage.setLocationy(villageObj.getString("locationy"));//百度定位目标y

                newModelingVillage.setCreateDate(new Date());
                newModelingVillage.setModifyDate(new Date());
                modelingVillageMapper.insert(newModelingVillage);
//                return ServerResponse.createBySuccessMessage("新增小区成功");
            } else {//修改
                ModelingVillage srcModelingVillage = modelingVillageMapper.selectByPrimaryKey(villageId);

                if (!srcModelingVillage.getName().equals(villageName))
                    return ServerResponse.createByErrorMessage("小区名称已存在");
                srcModelingVillage.setName(villageName);//小区名称

                srcModelingVillage.setCityId(villageObj.getString("cityId"));//城市
                srcModelingVillage.setAreaName(villageObj.getString("areaName"));//区域名称
                srcModelingVillage.setAddress(villageObj.getString("address"));//小区详细地址
                srcModelingVillage.setInitials(villageObj.getString("initials"));//存放ABCD
                srcModelingVillage.setLocationx(villageObj.getString("locationx"));//百度定位目标x
                srcModelingVillage.setLocationy(villageObj.getString("locationy"));//百度定位目标y
                srcModelingVillage.setModifyDate(new Date());
                modelingVillageMapper.updateByPrimaryKeySelective(srcModelingVillage);
//                return ServerResponse.createBySuccessMessage("修改小区成功");
            }


            ModelingVillage srcModelingVillage = modelingVillageMapper.selectByPrimaryKey(villageId);

//            遍历户型对象 数组  ， 一个小区 对应 多个户型
            String modelingLayoutList = villageObj.getString("modelingLayoutList");
            JSONArray modelingLayoutArr = JSONArray.parseArray(modelingLayoutList);

            for (int i = 0; i < modelingLayoutArr.size(); i++) {//遍历户型
                JSONObject obj = modelingLayoutArr.getJSONObject(i);
                String layoutId = obj.getString("id");//户型id
                String name = obj.getString("name");//户型名称

                if (!StringUtils.isNotBlank(name))
                    return ServerResponse.createByErrorMessage("户型名称不能为空");

                if (!StringUtils.isNotBlank(layoutId))//没有id则新增
                {
                    if (!StringUtils.isNotBlank(newModelingVillage.getId()))//没有id则新增
                        return ServerResponse.createByErrorMessage("小区id不能为null");

                    ModelingLayout modelingLayout = new ModelingLayout();
                    modelingLayout.setVillageId(newModelingVillage.getId());//设置 关联小区id
                    modelingLayout.setName(name);//户型name
                    modelingLayout.setImage(obj.getString("image"));//户型图片
                    modelingLayout.setBuildSquare(obj.getString("buildSquare"));//建筑面积
                    modelingLayoutMapper.insert(modelingLayout);
                } else {
                    ModelingLayout oldModelingLayout = modelingLayoutMapper.selectByPrimaryKey(layoutId);
                    if (!oldModelingLayout.getName().equals(name))
                        return ServerResponse.createByErrorMessage("户型名称已存在");

//                    oldModelingLayout.setCityId(newModelingVillage.getCityId());//城市
                    oldModelingLayout.setName(name);//户型名称
                    oldModelingLayout.setImage(villageObj.getString("image"));//户型图片
                    oldModelingLayout.setBuildSquare(villageObj.getString("buildSquare"));//建筑面积
                    oldModelingLayout.setModifyDate(new Date());
                    modelingLayoutMapper.updateByPrimaryKeySelective(oldModelingLayout);
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
        return ServerResponse.createBySuccess("查询列表成功", modelingLayoutList);
    }

    public ServerResponse getHouseList(HttpServletRequest request, String modelingLayoutId) {
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo("modelingLayoutId", modelingLayoutId);
        List<House> houseList = houseMapper.selectByExample(example);
        return ServerResponse.createBySuccess("查询列表成功", houseList);
    }

    /**
     * 根据城市查询小区
     *
     * @param cityId
     * @return
     */
    public ServerResponse getAllVillageByCity(String cityId) {
        List<VillageListDTO> vrlist = new ArrayList<>();//小区数组
        List<VillageListDTO> rvrlist = new ArrayList<>();//热门小区数组
        List<VillageDTO> vresultlist = new ArrayList<>();//热门小区数组
        try {
            List<ModelingVillage> mvlist = redisClient.getListCache("vresult:" + cityId, ModelingVillage.class);
            Integer number = modelingVillageMapper.getAllVillageCount(cityId);//统计根据城市id查询小区按字母排序
            if (mvlist == null || mvlist.size() != number) {
                mvlist = modelingVillageMapper.getAllVillage(cityId);
            }
            if (mvlist != null) {
                VillageDTO vresult = new VillageDTO(); //字母对象
                for (int m = 0; m < mvlist.size(); m++) {
                    char c = mvlist.get(m).getInitials().charAt(0);
                    int i = c;
                    if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
                        VillageListDTO vr = new VillageListDTO();//小区对象
                        vr.setVillageId(mvlist.get(m).getId());
                        vr.setInitials(mvlist.get(m).getInitials().toUpperCase());
                        vr.setName(mvlist.get(m).getName());
                        if (m > 0) {//第一个直接存储，后面需要比较与前一个字母是否相同
                            if (mvlist.get(m).getInitials().toUpperCase().equals(mvlist.get(m - 1).getInitials().toUpperCase())) {
                                //如果与前一个字母相同则不用new新的字母对象，直接存储
                                vrlist.add(vr);//存放小区集合
                                vresult.setInitials(mvlist.get(m).getInitials().toUpperCase());
                                vresult.setVlist(vrlist);//小区集合放入对应字母对象
                            } else {
                                //如果与前一个字母不相同则new新的字母对象，再存储
                                vresultlist.add(vresult);//存放字母集合
                                vresult = new VillageDTO(); //字母对象
                                vrlist = new ArrayList<VillageListDTO>();//小区数组
                                vrlist.add(vr);//存放小区集合
                                vresult.setInitials(mvlist.get(m).getInitials().toUpperCase());
                                vresult.setVlist(vrlist);//小区集合放入对应字母对象
                            }
                        } else {
                            //如果与前一个字母相同则不用new新的字母对象，直接存储
                            vrlist.add(vr);//存放小区集合
                            vresult.setInitials(mvlist.get(m).getInitials().toUpperCase());
                            vresult.setVlist(vrlist);//小区集合放入对应字母对象
                        }
                    }
                }
            }
            redisClient.putListCaches("vresult:" + cityId, mvlist);
            return ServerResponse.createBySuccess("根据城市查询小区成功", vresultlist);
        } catch (Exception e) {
            redisClient.deleteCache("vresult:" + cityId);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询出错");
        }
//        }
//        return ServerResponse.createBySuccess("根据城市查询小区成功", vresultlist);
    }
}
