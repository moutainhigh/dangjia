package com.dangjia.acg.service.actuary.app;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.app.*;
import com.dangjia.acg.mapper.actuary.*;
import com.dangjia.acg.mapper.basics.IBrandMapper;
import com.dangjia.acg.mapper.basics.IUnitMapper;
import com.dangjia.acg.modle.actuary.DjActuarialSimulationRelation;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.service.product.app.GoodsProductTemplateService;
import com.dangjia.acg.util.StringTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


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
    private DjActuarialSimulationRelationMapper djActuarialSimulationRelationMapper;

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private IUnitMapper iUnitMapper;
    @Autowired
    private GoodsProductTemplateService goodsProductTemplateService;

    @Autowired
    private IBrandMapper iBrandMapper;


    //对排列结果进行存贮的list
    static List rangeList = new ArrayList();
    /**
     * 查询设计精算阶段配置(我要装修首页展示）
     *
     * @return
     */
    public ServerResponse searchActuarialList(String cityId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<ActuarialTemplateConfigAppDTO> actuarialTemplateConfigAppDTOS = djActuarialTemplateConfigMapper.searchAppActuarialList(cityId);
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
    public  void getProductList(List<ActuarialProductAppDTO> productList,String address){
        if(productList!=null&&productList.size()>0){
            for(ActuarialProductAppDTO ap:productList){
                String image=ap.getImage();
                if (image == null) {
                    continue;
                }
                //添加图片详情地址字段
                ap.setImageUrl(StringTool.getImage(ap.getImage(),address));//图多张
                ap.setImageSingle(StringTool.getImageSingle(ap.getImage(),address));//图一张
                //查询单位
                String unitId=ap.getUnit();
                //查询单位
                if(ap.getConvertQuality()!=null&&ap.getConvertQuality()>0){
                    unitId=ap.getConvertUnit();
                }

                if(unitId!=null&& StringUtils.isNotBlank(unitId)){
                    Unit unit= iUnitMapper.selectByPrimaryKey(unitId);
                    ap.setUnitName(unit!=null?unit.getName():"");
                    ap.setUnitType(unit.getType());
                }

                if(StringUtils.isNotBlank(ap.getBrandId())){
                    Brand brand=iBrandMapper.selectByPrimaryKey(ap.getBrandId());
                    ap.setBrandName(brand!=null?brand.getName():"");
                }
                //查询规格名称
                if (StringUtils.isNotBlank(ap.getValueIdArr())) {
                    ap.setValueNameArr(goodsProductTemplateService.getNewValueNameArr(ap.getValueIdArr()).replaceAll(",", " "));
                }
            }
        }
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
    public ServerResponse searchSimulationTitleList(String cityId){
        try{

            List<SimulationTemplateAppConfigDTO>  simulationTemplateConfigDTOList=djSimulationTemplateConfigMapper.searchSimulationTitleList(cityId);
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
    public ServerResponse searchSimulationTitleDetailList(String titleId,String cityId){
        try{
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<SimulationTemplateConfigDetailAppDTO> titleDetailList = djSimulationTemplateConfigDetailMapper.searchSimulationTitleDetailList(titleId,address,cityId);
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
    public ServerResponse searchSimulateCostInfoList(String groupCode,String cityId){
        try{
            logger.info("组合编码groupCode{}",groupCode);
            if(groupCode!=null&&StringUtils.isBlank(groupCode)) {
                return ServerResponse.createByErrorMessage("组合编码为空。");
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            //查询组合对应的精算模板ID
            DjActuarialSimulationRelation djActuarialSimulationRelation = djActuarialSimulationRelationMapper.querySimulateAssemblyRelateionInfo(getCodeList(groupCode),cityId);
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

