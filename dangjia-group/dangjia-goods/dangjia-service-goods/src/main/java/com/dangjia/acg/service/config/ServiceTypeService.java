package com.dangjia.acg.service.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.ElasticSearchAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.ElasticSearchDTO;
import com.dangjia.acg.mapper.config.IServiceTypeMapper;
import com.dangjia.acg.modle.actuary.ActuarialTemplate;
import com.dangjia.acg.modle.config.ServiceType;
import com.dangjia.acg.service.actuary.ActuarialTemplateService;
import com.dangjia.acg.service.actuary.DjBasicsActuarialConfigurationServices;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @类 名： ServiceTypeService.java
 * @功能描述： 服务类型配配置
 */
@Service
public class ServiceTypeService {
    private static Logger logger = LoggerFactory.getLogger(ServiceTypeService.class);
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IServiceTypeMapper iServiceTypeMapper;

   // @Autowired
   // private ElasticSearchAPI elasticSearchAPI;
    @Autowired
    private DjBasicsActuarialConfigurationServices actuarialConfigurationServices;
    /**
     * 根据ID查询服务详情
     * @param id
     * @return
     */
    public ServiceType getServiceTypeById( String id) {
        JSONObject json= null;//elasticSearchAPI.getSearchJsonId(ServiceType.class.getSimpleName(),id);
        ServiceType serviceType;
        if(json!=null){
            serviceType= json.toJavaObject(ServiceType.class);
        }else {
            serviceType=iServiceTypeMapper.selectByPrimaryKey(id);
        }
        return serviceType;

    }

    /**
     * 根据ID查询服务详情
     * @param id
     * @return
     */
    public ServerResponse selectServiceTypeById( String id) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            JSONObject json= null;//elasticSearchAPI.getSearchJsonId(ServiceType.class.getSimpleName(),id);
            if(json==null){
                ServiceType  serviceType=iServiceTypeMapper.selectByPrimaryKey(id);
                json=JSONObject.parseObject(JSON.toJSONString(serviceType));
            }
            json.put("coverImageUrl",address+json.get(ServiceType.COVER_IMAGE));
            json.put("imageUrl",address+json.get(ServiceType.IMAGE));
            return ServerResponse.createBySuccess("查询成功", json);
        } catch (Exception e) {
            logger.error("selectServiceTypeById查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }

    /**
     * 查询服务类型
     * @param pageDTO
     * @return
     */
    public ServerResponse<PageInfo> selectServiceTypeList( PageDTO pageDTO,String cityId) {
       // ElasticSearchDTO elasticSearchDTO=new ElasticSearchDTO();
        //表名字
       // elasticSearchDTO.setTableTypeName(ServiceType.class.getSimpleName());
        //排序字段
        Map<String, Integer> sortMap = new HashMap<>();
        sortMap.put(ServiceType.CREATE_DATE, 1);
      //  elasticSearchDTO.setSortMap(sortMap);
        //分页数据
      //  elasticSearchDTO.setPageDTO(pageDTO);
        //筛选数据
        Map<String, String> paramMap = new HashMap<>();
        if (!CommonUtil.isEmpty(cityId)) {
            paramMap.put(ServiceType.CITY_ID, cityId);
        }
        if(!paramMap.isEmpty()){
        //    elasticSearchDTO.setParamMap(paramMap);
        }
        PageInfo<JSONObject> redata =elasticSearchAPI.searchESJsonPage(elasticSearchDTO);
        if(redata!=null && redata.getList()!=null && redata.getList().size()>0){
            return ServerResponse.createBySuccess("查询成功", redata);
        }
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<ServiceType> serviceTypeList = iServiceTypeMapper.getServiceTypeList(cityId);
            List list = new ArrayList<>();
            for (ServiceType serviceType : serviceTypeList) {
                JSONObject json=JSONObject.parseObject(JSON.toJSONString(serviceType));
                json.put("coverImageUrl",address+serviceType.getCoverImage());
                json.put("imageUrl",address+serviceType.getImage());
                json.put("houseType",serviceType.getId());
                list.add(json);
                // elasticSearchAPI.saveESJson(json.toJSONString(),ServiceType.class.getSimpleName());
                 }
            PageInfo pageResult = new PageInfo(serviceTypeList);
            pageResult.setList(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 修改服务类型
     * @param id
     * @param name
     * @param image
     * @return
     */
    public ServerResponse updateServiceType( String id, String name,String coverImage, String image,String cityId) {
        try{
            ServiceType serviceType=new ServiceType();
            serviceType.setId(id);
            serviceType.setName(name);
            serviceType.setCoverImage(coverImage);
            serviceType.setImage(image);
            serviceType.setCityId(cityId);
            iServiceTypeMapper.updateByPrimaryKeySelective(serviceType);

            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            JSONObject json=JSONObject.parseObject(JSON.toJSONString(serviceType));
            json.put("coverImageUrl",address+serviceType.getCoverImage());
            json.put("imageUrl",address+serviceType.getImage());
            json.put("houseType",serviceType.getId());
            //elasticSearchAPI.updateResponse(json.toJSONString(),ServiceType.class.getSimpleName(),id);
            return ServerResponse.createBySuccess("修改成功", serviceType.getId());
        } catch (Exception e) {
            logger.error("修改失败",e);
            return ServerResponse.createByErrorMessage("修改失败");
        }

    }

    /**
     * 添加服务类型
     * @param name
     * @param image
     * @return
     */
    public ServerResponse insertServiceType(String name,String coverImage, String image,String cityId,String userId) {
        try{
            ServiceType serviceType=new ServiceType();
            serviceType.setName(name);
            serviceType.setCoverImage(coverImage);
            serviceType.setImage(image);
            serviceType.setCityId(cityId);
            iServiceTypeMapper.insert(serviceType);
            //添加对应的设计，精算阶段
            actuarialConfigurationServices.insertActuarialConfig(serviceType.getId(),"设计阶段","1",userId,cityId,"设计图是装修必备的资料，请确认您已有可指导装修施工的设计图；如果不确定的话，我们推荐您恢复勾选，提交订单后由工作人员协助判断。");
            actuarialConfigurationServices.insertActuarialConfig(serviceType.getId(),"精算阶段","2",userId,cityId,"精算是将您的设计图内容转化为商品或服务消费的过程，是当家装修平台的特色服务；您也可以单独体验设计服务，之后再选择是否继续体验精算以及施工服务。");

            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            JSONObject json=JSONObject.parseObject(JSON.toJSONString(serviceType));
            json.put("coverImageUrl",address+serviceType.getCoverImage());
            json.put("imageUrl",address+serviceType.getImage());
            json.put("houseType",serviceType.getId());
           // elasticSearchAPI.saveESJson(json.toJSONString(),ServiceType.class.getSimpleName());
            return ServerResponse.createBySuccess("新增成功", serviceType.getId());
        } catch (Exception e) {
            logger.error("新增失败",e);
            return ServerResponse.createByErrorMessage("新增失败");
        }

    }


    /**
     * 删除服务类型
     * @param id
     * @return
     */
    public ServerResponse deleteServiceType( String id) {
        try{
            ServiceType serviceType=new ServiceType();
            serviceType.setDataStatus(1);
            serviceType.setId(id);
            iServiceTypeMapper.updateByPrimaryKeySelective(serviceType);
           // elasticSearchAPI.deleteResponse(ServiceType.class.getSimpleName(),id);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            logger.error("删除成功",e);
            return ServerResponse.createByErrorMessage("删除失败");
        }

    }
}
