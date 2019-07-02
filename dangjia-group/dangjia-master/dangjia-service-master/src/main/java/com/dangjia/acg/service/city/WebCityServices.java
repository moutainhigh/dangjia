package com.dangjia.acg.service.city;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.modle.other.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/1
 * Time: 15:35
 */
@Service
public class WebCityServices {
    @Autowired
    private ICityMapper cityMapper;

    public ServerResponse getCityList(String cityId){
        Example example=new Example(City.class);
        if(!CommonUtil.isEmpty(cityId)){
            return ServerResponse.createBySuccess("查询成功",cityMapper.selectByPrimaryKey(cityId));
        }
        example.createCriteria().andEqualTo(City.DATA_STATUS,0);
        return ServerResponse.createBySuccess("查询成功",cityMapper.selectByExample(example));
    }

    public ServerResponse addCity(City city){
        cityMapper.insert(city);
        return ServerResponse.createBySuccessMessage("创建成功");
    }

    public ServerResponse delCity(String cityId){
        City city=new City();
        city.setId(cityId);
        city.setDataStatus(1);
        cityMapper.updateByPrimaryKeySelective(city);
        return ServerResponse.createBySuccessMessage("删除成功");
    }

    public ServerResponse updateCity(City city){
        city.setCreateDate(null);
        cityMapper.updateByPrimaryKeySelective(city);
        return ServerResponse.createBySuccessMessage("修改成功");
    }




}
