package com.dangjia.acg.service.product;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.product.DjBasicsActuarialConfigurationDTO;
import com.dangjia.acg.dto.product.SingleConfigurationDTO;
import com.dangjia.acg.mapper.product.DjBasicsActuarialConfigurationMapper;
import com.dangjia.acg.mapper.product.DjBasicsActuarialPhaseConfigurationMapper;
import com.dangjia.acg.modle.product.DjBasicsActuarialConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/20
 * Time: 16:47
 */
@Service
public class DjBasicsActuarialConfigurationServices {
    @Autowired
    private DjBasicsActuarialConfigurationMapper djBasicsActuarialConfigurationMapper;
    @Autowired
    private DjBasicsActuarialPhaseConfigurationMapper djBasicsActuarialPhaseConfigurationMapper;


    /**
     * 添加配置
     * @param jsonStr
     * @return
     */
    public ServerResponse addConfiguration(String jsonStr) {
        try {
            JSONObject villageObj = JSONObject.parseObject(jsonStr);
            String phaseId=villageObj.getString("phaseId");
            //遍历标商品配置对象 数组  ， 一个阶段 对应 多个配置对象
            String djBasicsActuarialConfigurationList = villageObj.getString("djBasicsActuarialConfigurationList");
            JSONArray productLabelValArr = JSONArray.parseArray(djBasicsActuarialConfigurationList);
            for (int i = 0; i < productLabelValArr.size(); i++) {//遍历户型
                JSONObject obj = productLabelValArr.getJSONObject(i);
                String djBasicsActuarialConfigurationId = obj.getString("id");//精算商品配置id
                String productId = obj.getString("productId");//货品id
                String goodsId = obj.getString("goodsId");//商品id'
                String anActuarialTable = obj.getString("anActuarialTable");//精算Excel
                DjBasicsActuarialConfiguration djBasicsActuarialConfiguration;
                if (CommonUtil.isEmpty(djBasicsActuarialConfigurationId)) {//没有id则新增
                    djBasicsActuarialConfiguration = new DjBasicsActuarialConfiguration();
                    djBasicsActuarialConfiguration.setPhaseId(phaseId);
                    djBasicsActuarialConfiguration.setProductId(productId);
                    djBasicsActuarialConfiguration.setDataStatus(0);
                    djBasicsActuarialConfiguration.setGoodsId(goodsId);
                    djBasicsActuarialConfiguration.setAnActuarialTable(anActuarialTable);
                    djBasicsActuarialConfigurationMapper.insert(djBasicsActuarialConfiguration);
                } else {
                    djBasicsActuarialConfiguration = djBasicsActuarialConfigurationMapper.selectByPrimaryKey(djBasicsActuarialConfigurationId);
                    if (djBasicsActuarialConfiguration.getProductId().equals(productId) && djBasicsActuarialConfiguration.getGoodsId().equals(goodsId)) {
                        return ServerResponse.createByErrorMessage("货品已存在");
                    }
                    djBasicsActuarialConfiguration.setProductId(productId);
                    djBasicsActuarialConfiguration.setGoodsId(goodsId);
                    djBasicsActuarialConfiguration.setAnActuarialTable(anActuarialTable);
                    djBasicsActuarialConfigurationMapper.updateByPrimaryKeySelective(djBasicsActuarialConfiguration);
                }
            }
            //要删除商品标签值id数组，逗号分隔
            String[] deletedjBasicsActuarialConfigurationIds = villageObj.getString("deletedjBasicsActuarialConfigurationIds").split(",");
            for (String deletedjBasicsActuarialConfigurationId : deletedjBasicsActuarialConfigurationIds) {
                if (djBasicsActuarialConfigurationMapper.selectByPrimaryKey(deletedjBasicsActuarialConfigurationId) != null) {
                    if (djBasicsActuarialConfigurationMapper.deleteByPrimaryKey(deletedjBasicsActuarialConfigurationId) < 0)
                        return ServerResponse.createByErrorMessage("删除id：" + deletedjBasicsActuarialConfigurationId + "失败");
                }
            }
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }


    /**
     * 查询配置
     * @return
     */
    public ServerResponse queryConfiguration() {
        try {
            List<DjBasicsActuarialConfigurationDTO> djBasicsActuarialConfigurationDTOS = djBasicsActuarialConfigurationMapper.queryConfiguration();
            if(djBasicsActuarialConfigurationDTOS.size()<=0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功",djBasicsActuarialConfigurationDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 查询单个配置
     * @param phaseId
     * @return
     */
    public ServerResponse querySingleConfiguration(String phaseId) {
        try {
            SingleConfigurationDTO singleConfigurationDTO=new SingleConfigurationDTO();
            singleConfigurationDTO.setPhaseId(phaseId);
            List<DjBasicsActuarialConfiguration> djBasicsActuarialConfigurations = djBasicsActuarialConfigurationMapper.querySingleConfiguration(phaseId);
            singleConfigurationDTO.setDjBasicsActuarialConfiguration(djBasicsActuarialConfigurations);
            return ServerResponse.createBySuccess("查询成功",singleConfigurationDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
