package com.dangjia.acg.service.actuary;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.modle.actuary.DjActuarialTemplateConfig;
import com.dangjia.acg.service.actuary.excel.ActuarialConfigExcelRead;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.ActuarialProductDTO;
import com.dangjia.acg.dto.actuary.ActuarialTemplateConfigDTO;
import com.dangjia.acg.dto.actuary.SimulationTemplateConfigDTO;
import com.dangjia.acg.dto.actuary.SimulationTemplateConfigDetailDTO;
import com.dangjia.acg.mapper.actuary.DjActuarialProductConfigMapper;
import com.dangjia.acg.mapper.actuary.DjActuarialTemplateConfigMapper;
import com.dangjia.acg.mapper.actuary.DjSimulationTemplateConfigDetailMapper;
import com.dangjia.acg.mapper.actuary.DjSimulationTemplateConfigMapper;
import com.dangjia.acg.mapper.product.IBasicsGoodsMapper;
import com.dangjia.acg.mapper.product.IBasicsProductTemplateMapper;
import com.dangjia.acg.modle.actuary.DjActuarialProductConfig;
import com.dangjia.acg.modle.actuary.DjSimulationTemplateConfig;
import com.dangjia.acg.modle.actuary.DjSimulationTemplateConfigDetail;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.IOException;
import java.util.*;


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
    private DjSimulationTemplateConfigMapper djSimulationTemplateConfigMapper;

    @Autowired
    private DjSimulationTemplateConfigDetailMapper djSimulationTemplateConfigDetailMapper;

    @Autowired
    private IBasicsGoodsMapper iBasicsGoodsMapper;

    @Autowired
    private  IBasicsProductTemplateMapper iBasicsProductTemplateMapper;

    @Autowired
    private ConfigUtil configUtil;

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
            if(StringUtils.isNotBlank(actuarialTemplateId)){
                List<ActuarialTemplateConfigDTO> djBasicsActuarialConfigurationDTOS = djActuarialTemplateConfigMapper.queryActuarialTemplateConfig(null);
                if(djBasicsActuarialConfigurationDTOS!=null&&djBasicsActuarialConfigurationDTOS.size()>0){
                    actuarialTemplateConfigDTO=djBasicsActuarialConfigurationDTOS.get(0);

                }
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
        logger.info("批量编辑设计精算阶段的货品商品---------start----{}",userId);
        JSONArray jsonArr = JSONArray.parseArray(actuarialProductStr);
        //1.商品作校验，校验前端传过来的商品是否符合条件
        String resCheckStr = checkProductData(jsonArr);
        if(StringUtils.isNotBlank(resCheckStr)){
            return ServerResponse.createByErrorMessage(resCheckStr);
        }
        logger.info("批量编辑设计精算阶段的货品商品---------checkend----{}",userId);
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
     * @return
     */
    public ServerResponse getActuarialGoodsList(){
        try {
            logger.info("查询所有类型为人工的货品");
            //查询所有的人工货品
            List<BasicsGoods> mapList = iBasicsGoodsMapper.queryByCategoryName(2);
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            logger.error("getActuarialGoodsList查询失败:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询当前货品下所有已上架的商品
     * @param goodsId
     * @return
     */
    public ServerResponse getActuarialProductListByGoodsId(String goodsId){
        try {
            logger.info("查询当前货品下所有已上架的商品");
            Example example=new Example(DjBasicsProductTemplate.class);
            example.createCriteria().andEqualTo(DjBasicsProductTemplate.DATA_STATUS,0).
                    andEqualTo(DjBasicsProductTemplate.MAKET,1).
                    andEqualTo(DjBasicsProductTemplate.TYPE,1).
                    andEqualTo(DjBasicsProductTemplate.GOODS_ID,goodsId);
            List<DjBasicsProductTemplate> mapList = iBasicsProductTemplateMapper.selectByExample(example);
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            logger.error("getActuarialProductListByGoodsId查询失败:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     *
     * @param userId 用户ID
     * @param configId 标题 ID
     * @param configName 标题 名称
     * @param configType 标题 类型
     * @param configDetailArr  标题下对应的类型列表（id 详情ID，simulationTemplateId 标题ID，name名称,image图片，labelName多个逗号分隔）
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse editSimulateionTemplateConfig(String userId,String configId,String configName,String configType,String configDetailArr){
        //编辑（添加修改标题数据）
        DjSimulationTemplateConfig djSimulationTemplateConfig = editSimulateTemplateInfo(userId,configId,configName,configType);
       //编辑（添加修改标题 下的选项值详情信息）
        JSONArray jsonArr = JSONArray.parseArray(configDetailArr);
        editSimulateTemplateDetailList(jsonArr,djSimulationTemplateConfig);
        return  ServerResponse.createByErrorMessage("保存成功");
    }

    /**
     * 编辑标题 信息
     * @param userId
     * @param configId
     * @param configName
     * @param configType
     * @return
     */
    private DjSimulationTemplateConfig editSimulateTemplateInfo(String userId,String configId,String configName,String configType){
        //判断标题是新增还是修改
        DjSimulationTemplateConfig djSimulationTemplateConfig=new DjSimulationTemplateConfig();
        djSimulationTemplateConfig.setConfigName(configName);
        djSimulationTemplateConfig.setModifyDate(new Date());
        djSimulationTemplateConfig.setUpdateBy(userId);
        if(configId!=null&&StringUtils.isNotBlank(configId)){
            djSimulationTemplateConfig.setId(configId);
            djSimulationTemplateConfigMapper.updateByPrimaryKeySelective(djSimulationTemplateConfig);
        }else{
            String configTypeIndex = djSimulationTemplateConfigMapper.selectCurrentIndexByConfigType(configType);
            logger.info("configTypeIndex==={}",configTypeIndex);
            //查询当前模板对应的序号值
            djSimulationTemplateConfig.setConfigType(configType);
            djSimulationTemplateConfig.setConfigTypeIndex(Integer.parseInt(configTypeIndex));
            djSimulationTemplateConfig.setCreateBy(userId);
            djSimulationTemplateConfig.setCreateDate(new Date());
            djSimulationTemplateConfigMapper.insert(djSimulationTemplateConfig);
        }
        return djSimulationTemplateConfigMapper.selectByPrimaryKey(djSimulationTemplateConfig.getId());
    }

    /**
     * 修改对应的标题 列表信息
     * @param jsonArr  标题 详情列表信息（id 详情ID，simulationTemplateId 标题ID，name名称,image图片，labelName多个逗号分隔）
     * @param djSimulationTemplateConfig  标题对应的基本信息
     */
    private void editSimulateTemplateDetailList(JSONArray jsonArr,DjSimulationTemplateConfig djSimulationTemplateConfig){
        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject obj = jsonArr.getJSONObject(i);
            String id=obj.getString("id");
            DjSimulationTemplateConfigDetail djSimulationTemplateConfigDetail=new DjSimulationTemplateConfigDetail();
            djSimulationTemplateConfigDetail.setImage(obj.getString("image"));
            djSimulationTemplateConfigDetail.setName("name");
            djSimulationTemplateConfigDetail.setConfigStatus(0);
            djSimulationTemplateConfigDetail.setLabelName(obj.getString("labelName"));
            djSimulationTemplateConfigDetail.setSimulationTemplateId(djSimulationTemplateConfig.getId());
            //判断是添加还是修改
            if(id!=null&&StringUtils.isNotBlank(id)){
                djSimulationTemplateConfigDetail.setId(id);
                djSimulationTemplateConfigDetailMapper.updateByPrimaryKeySelective(djSimulationTemplateConfigDetail);
            }else{
                String templteDetailIndex = djSimulationTemplateConfigDetailMapper.selectCurrentIndexByTemplateId(djSimulationTemplateConfig.getId());
                djSimulationTemplateConfigDetail.setTemplateDetailIndex(Integer.parseInt(templteDetailIndex));
                String code=djSimulationTemplateConfig.getConfigType()+"-"+djSimulationTemplateConfig.getConfigTypeIndex()+"-"+templteDetailIndex;
                djSimulationTemplateConfigDetail.setCode(code);
                djSimulationTemplateConfigDetailMapper.insert(djSimulationTemplateConfigDetail);
            }

        }
    }
    /**
     * 查询标题 列表信息
     * @return
     */
    public ServerResponse querySimulateionTemplateConfig(){
        try{
            List<SimulationTemplateConfigDTO> simulationTemplateConfigDTOList=djSimulationTemplateConfigMapper.querySimulateionTemplateConfig(null);
            return ServerResponse.createBySuccess("查询成功", simulationTemplateConfigDTOList);
        } catch (Exception e) {
            logger.error("querySimulateionTemplateConfig查询失败:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }

    /**
     * 查询标题 详情信息
     * @param simulationTemplateId 标题 ID
     * @return
     */
    public ServerResponse querySimulateionTemplateConfigById(String simulationTemplateId){
        try{
            SimulationTemplateConfigDTO simulationTemplateConfigDTO=new SimulationTemplateConfigDTO();
            if(StringUtils.isNotBlank(simulationTemplateId)){
                List<SimulationTemplateConfigDTO> simulationTemplateConfigDTOList=djSimulationTemplateConfigMapper.querySimulateionTemplateConfig(simulationTemplateId);
                if(simulationTemplateConfigDTOList!=null&&simulationTemplateConfigDTOList.size()>0){
                    simulationTemplateConfigDTO=simulationTemplateConfigDTOList.get(0);

                }
            }
            return ServerResponse.createBySuccess("查询成功", simulationTemplateConfigDTO);
        } catch (Exception e) {
            logger.error("querySimulateionTemplateConfigById查询失败:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 根据详情ID查询详情信息
     * @param simulationDetailId
     * @return
     */
    public ServerResponse querySimulateionDetailInfoById(String simulationDetailId){
        try{
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            SimulationTemplateConfigDetailDTO simulationTemplateConfigDetailDTO=djSimulationTemplateConfigDetailMapper.getSimulateionConfigDetail(simulationDetailId);
            Map map= BeanUtils.beanToMap(simulationTemplateConfigDetailDTO);
            map.put("imageUrl",address+simulationTemplateConfigDetailDTO.getImage());
            return ServerResponse.createBySuccess("查询成功", map);
        }catch (Exception e){
            logger.error("querySimulateionDetailInfoById查询失败:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    public ServerResponse deleteSimulateDetailInfoById(String simulationTemplateId){
        try{
            Example example=new Example(DjSimulationTemplateConfigDetail.class);
            example.createCriteria().andEqualTo(DjSimulationTemplateConfigDetail.SIMULATION_TEMPLATE_ID,simulationTemplateId).
                    andEqualTo(DjSimulationTemplateConfigDetail.CONFIG_STATUS,0);
            djSimulationTemplateConfigDetailMapper.deleteByExample(example);
            return ServerResponse.createBySuccessMessage("删除成功");
        }catch (Exception e){
            logger.error("deleteSimulateDetailInfoById删除失败:",e);
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }

    /**
     * 模拟excel 精算导入
     * @param name 上传文件名
     * @param fileName 上传后生成的文件名
     * @param address 上传后的路径
     * @return
     */
    public ServerResponse importSimulateExcelBudgets (String name,String fileName,String address,String userId){
      //  String addressPri = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        String addressPri = "e://dangjia/";
        try {
            ActuarialConfigExcelRead actuarialConfigExcelRead = new ActuarialConfigExcelRead();
            String postfix = actuarialConfigExcelRead.checkExcelType(fileName);
            List<Map<String,Object>> actuarialExcelList=new ArrayList<>();
            if ("xls".equals(postfix)) {
                 actuarialExcelList = actuarialConfigExcelRead.readXls(new File(addressPri+address));
            }else{
                actuarialExcelList = actuarialConfigExcelRead.readXlsx(new File(addressPri+address));
            }
            if(actuarialExcelList!=null&&actuarialExcelList.size()>0){
                //1.保存对应的excel文件
                DjActuarialTemplateConfig djActuarialTemplateConfig=new DjActuarialTemplateConfig();
                djActuarialTemplateConfig.setConfigName(name);
                djActuarialTemplateConfig.setConfigType("3");
                djActuarialTemplateConfig.setExcelFileName(fileName);
                djActuarialTemplateConfig.setExcelAddress(address);
                djActuarialTemplateConfig.setCreateBy(userId==null?"SYSTEM":userId);
                djActuarialTemplateConfig.setUpdateBy(userId==null?"SYSTEM":userId);
                djActuarialTemplateConfig.setCreateDate(new Date());
                djActuarialTemplateConfig.setModifyDate(new Date());
                djActuarialTemplateConfigMapper.insert(djActuarialTemplateConfig);
                //2.保存excel文件中的内容
                for(Map<String,Object> map:actuarialExcelList){
                    String productSn = (String)map.get("productSn");
                    String workTypeId = (String)map.get("workTypeId");
                    String productCount = (String)map.get("productCount");
                    DjActuarialProductConfig djActuarialProductConfig=new DjActuarialProductConfig();
                    djActuarialProductConfig.setActuarialTemplateId(djActuarialTemplateConfig.getId());// excel上传地址ID
                    djActuarialProductConfig.setProductSn(productSn);
                    djActuarialProductConfig.setWorkerTypeId(workTypeId);
                    djActuarialProductConfig.setPurchaseQuantity(productCount);
                    //根据商品编码，查询对应的商品ID及货品ID
                    List<DjBasicsProductTemplate> list=iBasicsProductTemplateMapper.queryByProductSn(productSn);
                    if(list!=null&&list.size()>0){
                        DjBasicsProductTemplate dj=list.get(0);
                        djActuarialProductConfig.setGoodsId(dj.getGoodsId());
                        djActuarialProductConfig.setProductId(dj.getId());
                    }
                    djActuarialProductConfigMapper.insert(djActuarialProductConfig);

                }
                 return ServerResponse.createBySuccessMessage("保存excel成功");
            }
            logger.info("excel读取结果值：{}",actuarialExcelList);
            return ServerResponse.createByErrorMessage("未读取到有效的excel数据，请检查数据是否正确");
        } catch (Exception e) {
            logger.error("读取excel失败",e);
            return ServerResponse.createByErrorMessage("保存excel失败");
        }
    }

    /**
     * 查询excel列表
     * @return
     */
    public ServerResponse querySimulateExcelList(){
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        Example example=new Example(DjActuarialTemplateConfig.class);
        example.createCriteria().andEqualTo(DjActuarialTemplateConfig.CONFIG_TYPE,"3")
        .andEqualTo(DjActuarialTemplateConfig.DATA_STATUS,0);//查类型为excel的数据列表
        List<DjActuarialTemplateConfig> templateList = djActuarialTemplateConfigMapper.selectByExample(example);
        List excelList=new ArrayList();
        if(templateList!=null&&templateList.size()>0){
            for(DjActuarialTemplateConfig dj :templateList){
                Map djMap=new HashMap();
                djMap.put("id",dj.getId());
                djMap.put("name",dj.getConfigName());
                djMap.put("fileName",dj.getExcelFileName());
                djMap.put("excelAddress",dj.getExcelAddress());
                djMap.put("excelAddressUrl",dj.getExcelAddress());
                excelList.add(djMap);
            }
        }
        return  ServerResponse.createBySuccess("查询成功",excelList);
    }

    //删除上传的excel文件(
    public ServerResponse deleteSimulateExcelById(String id){
        DjActuarialTemplateConfig djActuarialTemplateConfig = new DjActuarialTemplateConfig();
        djActuarialTemplateConfig.setId(id);
        djActuarialTemplateConfig.setDataStatus(1);//0正式数据，1删除
        djActuarialTemplateConfigMapper.updateByPrimaryKeySelective(djActuarialTemplateConfig);
        return ServerResponse.createBySuccess("删除成功",id);
    }

}
