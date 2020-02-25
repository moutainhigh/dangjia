package com.dangjia.acg.service.house;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.product.StorefrontProductDTO;
import com.dangjia.acg.mapper.house.IHouseDistributionConfigMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.modle.house.HouseDistributionConfig;
import com.dangjia.acg.modle.other.City;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * author: qiyuxiang
 * Date: 2019/1/16 0001
 * Time: 17:56
 */
@Service
public class HouseDistributionConfigService {
    
    @Autowired
    private IHouseDistributionConfigMapper houseDistributionConfigMapper;

    @Autowired
    private ICityMapper cityMapper;
    @Autowired
    private IMasterStorefrontProductMapper iMasterStorefrontProductMapper;
    @Autowired
    private ConfigUtil configUtil;
    /**
     * 获取所有配置
     * @param houseDistributionConfig
     * @return
     */
    public ServerResponse getHouseDistributionConfigs(HttpServletRequest request, HouseDistributionConfig houseDistributionConfig) {
        List<HouseDistributionConfig> list = houseDistributionConfigMapper.getHouseDistributionConfigs(houseDistributionConfig.getVillages());
        return ServerResponse.createBySuccess("ok",list);
    }
    /**
     * 删除
     * @param id
     * @return
     */
    public ServerResponse delHouseDistributionConfig(HttpServletRequest request, String id) {
        if(this.houseDistributionConfigMapper.deleteByPrimaryKey(String.valueOf(id))>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("删除失败，请您稍后再试");
        }
    }

    /**
     * 修改
     * @param houseDistributionConfig
     * @return
     */
    public ServerResponse editHouseDistributionConfig(HttpServletRequest request, HouseDistributionConfig houseDistributionConfig) {
        if(this.houseDistributionConfigMapper.updateByPrimaryKeySelective(houseDistributionConfig)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }
    /**
     * 新增
     * @param houseDistributionConfig
     * @return
     */
    public ServerResponse addHouseDistributionConfig(HttpServletRequest request,HouseDistributionConfig houseDistributionConfig) {
        houseDistributionConfig.setId((int)(Math.random() * 50000000) + 50000000 + "" + System.currentTimeMillis());
        City city = cityMapper.selectByPrimaryKey(houseDistributionConfig.getCityId());
        houseDistributionConfig.setCityName(city.getName());
        if(this.houseDistributionConfigMapper.insertSelective(houseDistributionConfig)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }


    /**
     * 专业验房列表
     * @param cityId
     * @return
     */
    public ServerResponse queryProfessionalHomeInspector(String cityId, PageDTO pageDTO) {
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            PageHelper.startPage(pageDTO.getPageNum(),pageDTO.getPageSize());
            List<StorefrontProductDTO> storefrontProductDTOS =
                    iMasterStorefrontProductMapper.queryProfessionalHomeInspector(cityId);
            if(storefrontProductDTOS.size()<=0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            }
            storefrontProductDTOS.forEach(storefrontProductDTO -> {
                storefrontProductDTO.setImage(imageAddress+storefrontProductDTO.getImage());
            });
            PageInfo pageInfo=new PageInfo(storefrontProductDTOS);
            return ServerResponse.createBySuccess("查询成功",pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
