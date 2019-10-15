package com.dangjia.acg.service.actuary;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.dto.actuary.ActuarialProductDTO;
import com.dangjia.acg.dto.actuary.ActuarialTemplateConfigDTO;
import com.dangjia.acg.mapper.actuary.DjActuarialProductConfigMapper;
import com.dangjia.acg.mapper.actuary.DjActuarialTemplateConfigMapper;
import com.dangjia.acg.mapper.product.IBasicsGoodsMapper;
import com.dangjia.acg.modle.actuary.DjActuarialProductConfig;
import com.dangjia.acg.modle.actuary.DjActuarialTemplateConfig;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * Date: 2019/9/20
 * Time: 16:47
 */
@Service
public class DjBasicsActuarialConfigurationServices {
    private static Logger logger = LoggerFactory.getLogger(DjBasicsActuarialConfigurationServices.class);
    @Autowired
    private DjActuarialTemplateConfigMapper djActuarialTemplateConfigMapper;
    @Autowired
    private DjActuarialProductConfigMapper djActuarialProductConfigMapper;

    @Autowired
    private IBasicsGoodsMapper iBasicsGoodsMapper;

    /**
     * 查询设计精算阶段配置
     *
     * @return
     */
    public ServerResponse queryActuarialTemplateConfig() {
        try {
            List<ActuarialTemplateConfigDTO> djBasicsActuarialConfigurationDTOS = djActuarialTemplateConfigMapper.queryActuarialTemplateConfig(null);
            return ServerResponse.createBySuccess("查询成功", djBasicsActuarialConfigurationDTOS);
        } catch (Exception e) {
            logger.error("queryActuarialTemplateConfig查询失败:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 根据阶段模板查询对应配置的商品货品信息
     *
     * @param actuarialTemplateId(阶段模板ID)
     * @return
     */
    public ServerResponse queryActuarialProductByConfigId(String actuarialTemplateId) {
        try {
            ActuarialTemplateConfigDTO actuarialTemplateConfigDTO=new ActuarialTemplateConfigDTO();
            List<ActuarialTemplateConfigDTO> djBasicsActuarialConfigurationDTOS = djActuarialTemplateConfigMapper.queryActuarialTemplateConfig(null);
            if(djBasicsActuarialConfigurationDTOS!=null&&djBasicsActuarialConfigurationDTOS.size()>0){
                actuarialTemplateConfigDTO=djBasicsActuarialConfigurationDTOS.get(0);

            }
            return ServerResponse.createBySuccess("查询成功", actuarialTemplateConfigDTO);
        } catch (Exception e) {
            logger.error("queryActuarialTemplateConfigById:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 批量编辑设计精算阶段的商品货品
     * @param actuarialProductStr
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse editActuarialProduct(String actuarialProductStr,String userId){
        logger.info("批量编辑设计精算阶段的货品商品---------start----",userId);
        JSONArray jsonArr = JSONArray.parseArray(actuarialProductStr);
        //1.商品作校验，校验前端传过来的商品是否符合条件
        String resCheckStr = checkProductData(jsonArr);
        if(StringUtils.isNotBlank(resCheckStr)){
            return ServerResponse.createByErrorMessage(resCheckStr);
        }
        logger.info("批量编辑设计精算阶段的货品商品---------checkend----",userId);
        //添加商品到对应的模板商品库中去
        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject obj = jsonArr.getJSONObject(i);
            ActuarialProductDTO actuarialProductDTO = JSONObject.toJavaObject(obj, ActuarialProductDTO.class);
            String productId = actuarialProductDTO.getId();
            DjActuarialProductConfig actuarialProductConfig=new DjActuarialProductConfig();
            actuarialProductConfig.setActuarialTemplateId(actuarialProductDTO.getActuarialTemplateId());
            actuarialProductConfig.setProductId(actuarialProductDTO.getProductId());
            actuarialProductConfig.setGoodsId(actuarialProductDTO.getGoodsId());
            actuarialProductConfig.setWorkerTypeId(actuarialProductDTO.getWorkerTypeId());
            actuarialProductConfig.setUpdateBy(userId);
            actuarialProductConfig.setModifyDate(new Date());
            //判断是添加还是更新
            if(StringUtils.isNotBlank(productId)){
                actuarialProductConfig.setCreateBy(userId);
                actuarialProductConfig.setCreateDate(new Date());
                actuarialProductConfig.setId(productId);
                djActuarialProductConfigMapper.updateByPrimaryKeySelective(actuarialProductConfig);
            }else{
                djActuarialProductConfigMapper.insert(actuarialProductConfig);
            }

        }
        logger.info("批量编辑设计精算阶段的货品商品---------end----",userId);
        return ServerResponse.createBySuccessMessage("保存成功");
    }
    /**
     * 校验需添加商品数据是否正确
     * 校验当前货品，商品，在当前数据库中是否已添加过，是否有重复添加
     * @param jsonArr
     * @return
     */
    private String checkProductData(JSONArray jsonArr){
        List productList=new ArrayList();
        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject obj = jsonArr.getJSONObject(i);
            //JSON对象转换成Java对象
            DjActuarialProductConfig actuarialProductConfig = JSONObject.toJavaObject(obj, DjActuarialProductConfig.class);
            if (productList.contains(actuarialProductConfig.getProductId())) {
                return "当前阶段，存在重复添加的商品，请核实！";
            }
        }
        return "";
    }

    /**
     * 删除对应的设计精算商品
     * @param id
     * @return
     */
    public ServerResponse deleteActuarialProduct(String id){
        try {
             djActuarialProductConfigMapper.deleteByPrimaryKey(id);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            logger.error("deleteActuarialProduct:",e);
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }

    /**
     * 查询设计精算的货品列表
     * @param configType
     * @return
     */
    public ServerResponse getActuarialGoodsList(String configType){
        try {
            String categoryName="";
            if(configType!=null&&"1".equals(configType)){
                categoryName="设计师";
            }else if(configType!=null&&"2".equals(configType)){
                categoryName="精算师";
            }
            List<BasicsGoods> mapList = iBasicsGoodsMapper.queryByCategoryName(categoryName);
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            logger.error("getActuarialGoodsList查询失败:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
