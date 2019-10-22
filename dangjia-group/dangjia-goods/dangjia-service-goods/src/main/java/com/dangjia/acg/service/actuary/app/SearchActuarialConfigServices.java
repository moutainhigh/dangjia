package com.dangjia.acg.service.actuary.app;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.excel.ImportExcel;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.*;
import com.dangjia.acg.dto.actuary.app.*;
import com.dangjia.acg.mapper.actuary.*;
import com.dangjia.acg.mapper.basics.IAttributeValueMapper;
import com.dangjia.acg.mapper.basics.IBrandMapper;
import com.dangjia.acg.mapper.basics.IUnitMapper;
import com.dangjia.acg.mapper.product.IBasicsGoodsMapper;
import com.dangjia.acg.mapper.product.IBasicsProductTemplateMapper;
import com.dangjia.acg.modle.actuary.*;
import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.util.StringTool;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.rmi.log.LogInputStream;
import tk.mybatis.mapper.entity.Example;

import java.net.URL;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * Date: 2019/9/20
 * Time: 16:47
 */
@Service
public class SearchActuarialConfigServices {
    private static Logger logger = LoggerFactory.getLogger(SearchActuarialConfigServices.class);
    @Autowired
    private DjActuarialTemplateConfigMapper djActuarialTemplateConfigMapper;
    @Autowired
    private DjActuarialProductConfigMapper djActuarialProductConfigMapper;

    @Autowired
    private DjSimulationTemplateConfigMapper djSimulationTemplateConfigMapper;

    @Autowired
    private DjSimulationTemplateConfigDetailMapper djSimulationTemplateConfigDetailMapper;

    @Autowired
    private IBasicsGoodsMapper iBasicsGoodsMapper;

    @Autowired
    private  IBasicsProductTemplateMapper iBasicsProductTemplateMapper;

    @Autowired
    private DjActuarialSimulationRelationMapper djActuarialSimulationRelationMapper;

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private IUnitMapper iUnitMapper;
    @Autowired
    private IAttributeValueMapper iAttributeValueMapper;

    @Autowired
    private IBrandMapper iBrandMapper;

    //对排列结果进行存贮的list
    static List rangeList = new ArrayList();
    /**
     * 查询设计精算阶段配置(我要装修首页展示）
     *
     * @return
     */
    public ServerResponse searchActuarialList() {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<ActuarialTemplateConfigAppDTO> actuarialTemplateConfigAppDTOS = djActuarialTemplateConfigMapper.searchAppActuarialList();
            if(actuarialTemplateConfigAppDTOS!=null&&actuarialTemplateConfigAppDTOS.size()>0){
                for(ActuarialTemplateConfigAppDTO atc:actuarialTemplateConfigAppDTOS){
                    getProductList(atc.getProductList(),address);
                }
            }

            return ServerResponse.createBySuccess("查询成功", actuarialTemplateConfigAppDTOS);
        } catch (Exception e) {
            logger.error("searchActuarialList查询失败:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    private  void getProductList(List<ActuarialProductAppDTO> productList,String address){
        if(productList!=null&&productList.size()>0){
            for(ActuarialProductAppDTO ap:productList){
                String image=ap.getImage();
                if (image == null) {
                    continue;
                }
                //添加图片详情地址字段
                String[] imgArr = image.split(",");
                StringBuilder imgStr = new StringBuilder();
                StringBuilder imgUrlStr = new StringBuilder();
                StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
                ap.setImageUrl(imgStr.toString());//图片详情地址设置
                //查询单位
                if(ap.getUnit()!=null&&StringUtils.isNotBlank(ap.getUnit())){
                    Unit unit= iUnitMapper.selectByPrimaryKey(ap.getUnit());
                    ap.setUnitName(unit!=null?unit.getName():"");
                }
                //查询规格名称
                if (StringUtils.isNotBlank(ap.getValueIdArr())) {
                    ap.setValueNameArr(getNewValueNameArr(ap.getValueIdArr()));
                }
                if(StringUtils.isNotBlank(ap.getBrandId())){
                    Brand brand=iBrandMapper.selectByPrimaryKey(ap.getBrandId());
                    ap.setBrandName(brand!=null?brand.getName():"");
                }
            }
        }
    }
    /**
     * 获取对应的属性值信息
     * @param valueIdArr
     * @return
     */
    public String getNewValueNameArr(String valueIdArr){
        String strNewValueNameArr = "";
        String[] newValueNameArr = valueIdArr.split(",");
        for (int i = 0; i < newValueNameArr.length; i++) {
            String valueId = newValueNameArr[i];
            if (StringUtils.isNotBlank(valueId)) {
                AttributeValue attributeValue = iAttributeValueMapper.selectByPrimaryKey(valueId);
                if(attributeValue!=null&&StringUtils.isNotBlank(attributeValue.getName())){
                    if (i == 0) {
                        strNewValueNameArr = attributeValue.getName();
                    } else {
                        strNewValueNameArr = strNewValueNameArr + "," + attributeValue.getName();
                    }
                }

            }
        }
        return strNewValueNameArr;
    }

    /**
     * 根据货品ID查询切换的商品
     * @param goodsId
     * @return
     */
    public ServerResponse searchChangeProductList(String goodsId){
        try{
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<ActuarialProductAppDTO> productAppDTOList=djActuarialProductConfigMapper.searchChangeProductList(goodsId);
            getProductList(productAppDTOList,address);
            return ServerResponse.createBySuccess("查询成功", productAppDTOList);
        } catch (Exception e) {
            logger.error("searchChangeProductList查询失败:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }


    }

    /**
     * 我要装修--模拟花费标题查询
     * @return
     */
    public ServerResponse searchSimulationTitleList(){
        try{

            List<SimulationTemplateAppConfigDTO>  simulationTemplateConfigDTOList=djSimulationTemplateConfigMapper.searchSimulationTitleList();
            return ServerResponse.createBySuccess("查询成功", simulationTemplateConfigDTOList);
        } catch (Exception e) {
            logger.error("searchSimulationTitleList查询失败:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 根据标题ID查询标题详情列表
     * @param titleId
     * @return
     */
    public ServerResponse searchSimulationTitleDetailList(String titleId){
        try{
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<SimulationTemplateConfigDetailAppDTO> titleDetailList = djSimulationTemplateConfigDetailMapper.searchSimulationTitleDetailList(titleId,address);
            return ServerResponse.createBySuccess("查询成功", titleDetailList);
        } catch (Exception e) {
            logger.error("searchSimulationTitleDetailList查询失败:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 根据组合编码查询对应花费详情
     * @param groupCode 组合编码 如：'A-1-2,B-1-1,C-1-3'
     * @return
     */
    public ServerResponse searchSimulateCostInfoList(String groupCode){
        try{
            logger.info("组合编码groupCode{}",groupCode);
            if(groupCode!=null&&StringUtils.isBlank(groupCode)) {
                return ServerResponse.createByErrorMessage("组合编码为空。");
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            //查询组合对应的精算模板ID
            DjActuarialSimulationRelation djActuarialSimulationRelation = djActuarialSimulationRelationMapper.querySimulateAssemblyRelateionInfo(getCodeList(groupCode));
            if(djActuarialSimulationRelation!=null&&StringUtils.isNotBlank(djActuarialSimulationRelation.getActuarialTemplateId())){
                //根据精算模板查询对应的商品信息
                //1.1查询分类汇总信息
                List<SimulationCostCategoryDTO>  simulationCostCategoryDTOList = djActuarialProductConfigMapper.querySimulationCostByCategoryId(djActuarialSimulationRelation.getActuarialTemplateId());
                //1.2查询商品列表信息
                if(simulationCostCategoryDTOList!=null&&simulationCostCategoryDTOList.size()>0){
                    for(SimulationCostCategoryDTO scc:simulationCostCategoryDTOList){
                        List<ActuarialProductAppDTO> actuarialProductAppDTOList= djActuarialProductConfigMapper.querySimulationCostInfoList( djActuarialSimulationRelation.getActuarialTemplateId(), scc.getCategoryId());
                        getProductList(actuarialProductAppDTOList,address);
                        scc.setProductList(actuarialProductAppDTOList);
                    }
                }
                return ServerResponse.createBySuccess("查询成功",simulationCostCategoryDTOList);
            }
            return ServerResponse.createByErrorMessage("查询失败,未找到对应的数据");

        }catch (Exception e){
            logger.error("searchSimulateCostInfoList查询失败:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 根据组合编码获取组合编码列表
     * @param groupCode
     * @return
     */
    private List<String> getCodeList(String groupCode){
        List codeList=new ArrayList();
        String[] codeStr=groupCode.split(",");
        for (int i=0;i<codeStr.length;i++){
            codeList.add(codeStr[i]);
        }
        return  codeList;
    }



}

