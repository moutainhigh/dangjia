package com.dangjia.acg.service.house;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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


    /****
     * 注入配置
     */
    @Autowired
    private RedisClient redisClient;

    public ServerResponse getCityList(){
        List<City> cityList = cityMapper.selectAll();
        return ServerResponse.createBySuccess("查询列表成功",cityList);
    }

    public ServerResponse getVillageList(HttpServletRequest request, String cityId){
        List<Map<String,Object>> mapList = modelingVillageMapper.getVillageList(cityId);
        return ServerResponse.createBySuccess("查询列表成功", mapList);
    }

    public ServerResponse getLayoutList(HttpServletRequest request,String villageId){
        Example example = new Example(ModelingLayout.class);
        example.createCriteria().andEqualTo("villageId", villageId);
        List<ModelingLayout> modelingLayoutList = modelingLayoutMapper.selectByExample(example);
        return ServerResponse.createBySuccess("查询列表成功", modelingLayoutList);
    }

    public ServerResponse getHouseList(HttpServletRequest request,String modelingLayoutId){
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo("modelingLayoutId", modelingLayoutId);
        List<House> houseList = houseMapper.selectByExample(example);
        return ServerResponse.createBySuccess("查询列表成功", houseList);
    }

    /**
     * 根据城市查询小区
     * @param cityId
     * @return
     */
    public ServerResponse getAllVillageByCity(String cityId) {
        List<VillageListDTO> vrlist = new ArrayList<>();//小区数组
        List<VillageListDTO> rvrlist = new ArrayList<>();//热门小区数组
        List<VillageDTO>   vresultlist = new ArrayList<>();//热门小区数组
            try {
                List<ModelingVillage>  mvlist=redisClient.getListCache("vresult:"+cityId, ModelingVillage.class);
                Integer number=modelingVillageMapper.getAllVillageCount(cityId);//统计根据城市id查询小区按字母排序
                if(mvlist==null||mvlist.size()!=number){
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
                redisClient.putListCaches("vresult:"+cityId,mvlist);
                return ServerResponse.createBySuccess("根据城市查询小区成功", vresultlist);
            } catch (Exception e) {
                redisClient.deleteCache("vresult:"+cityId);
                e.printStackTrace();
                return ServerResponse.createByErrorMessage("查询出错");
            }
//        }
//        return ServerResponse.createBySuccess("根据城市查询小区成功", vresultlist);
    }
}
