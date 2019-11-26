package com.dangjia.acg.service.actuary;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.excel.ImportExcel;
import com.dangjia.acg.dto.actuary.*;
import com.dangjia.acg.mapper.actuary.*;
import com.dangjia.acg.modle.actuary.*;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.product.IBasicsGoodsMapper;
import com.dangjia.acg.mapper.product.IBasicsProductTemplateMapper;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.net.*;
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
    private DjActuarialSimulationRelationMapper djActuarialSimulationRelationMapper;

    @Autowired
    private ConfigUtil configUtil;

    //对排列结果进行存贮的list
    static List rangeList = new ArrayList();
    /**
     * 查询设计精算阶段配置
     *
     * @return
     */
    public ServerResponse queryActuarialTemplateConfig(String cityId) {
        try {
            List<ActuarialTemplateConfigDTO> djBasicsActuarialConfigurationDTOS = djActuarialTemplateConfigMapper.queryActuarialTemplateConfig(null,cityId);
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
    public ServerResponse queryActuarialProductByConfigId(String actuarialTemplateId,String cityId) {
        try {
            ActuarialTemplateConfigDTO actuarialTemplateConfigDTO=new ActuarialTemplateConfigDTO();
            if(StringUtils.isNotBlank(actuarialTemplateId)){
                List<ActuarialTemplateConfigDTO> djBasicsActuarialConfigurationDTOS = djActuarialTemplateConfigMapper.queryActuarialTemplateConfig(actuarialTemplateId,cityId);
                if(djBasicsActuarialConfigurationDTOS!=null&&djBasicsActuarialConfigurationDTOS.size()>0){
                    actuarialTemplateConfigDTO=djBasicsActuarialConfigurationDTOS.get(0);
                    if(actuarialTemplateConfigDTO.getProductList()!=null&&actuarialTemplateConfigDTO.getProductList().size()>0){
                        List productList=new ArrayList();
                        for(int i=0;i<actuarialTemplateConfigDTO.getProductList().size();i++){
                            Map productMap=(Map)actuarialTemplateConfigDTO.getProductList().get(i);
                            productMap.put("prodList", iBasicsProductTemplateMapper.getProductStoreListByGoodsId((String)productMap.get("goodsId")));//商品列表
                            productMap.put("goodsList",iBasicsGoodsMapper.getActuarialGoodsListByCategoryId((String)productMap.get("categoryId"),cityId));//商品列表
                            productList.add(productMap);
                        }
                        actuarialTemplateConfigDTO.setProductList(productList);
                    }

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
    public ServerResponse editActuarialProduct(String actuarialProductStr,String actuarialTemplateId,String workTypeId,String userId,String cityId){
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
            actuarialProductConfig.setActuarialTemplateId(actuarialTemplateId);
            actuarialProductConfig.setProductId(actuarialProductDTO.getProductId());
            actuarialProductConfig.setGoodsId(actuarialProductDTO.getGoodsId());
            actuarialProductConfig.setWorkerTypeId(workTypeId);
            actuarialProductConfig.setUpdateBy(userId);
            actuarialProductConfig.setModifyDate(new Date());
            actuarialProductConfig.setCityId(cityId);
            actuarialProductConfig.setIsCalculatedArea(actuarialProductDTO.getIsCalculatedArea());
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
    public ServerResponse getActuarialGoodsListByCategoryId(String categoryId,String cityId){
        try {
            logger.info("查询所有类型为人工的货品");
            //查询所有的人工货品
            List<BasicsGoods> mapList = iBasicsGoodsMapper.getActuarialGoodsListByCategoryId(categoryId,cityId);
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            logger.error("getActuarialGoodsList查询失败:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询当前货品下所有已上架的商品(店铺已售买的商品）
     * @param goodsId
     * @return
     */
    public ServerResponse getActuarialProductListByGoodsId(String goodsId){
        try {
            logger.info("查询当前货品下所有已上架的商品(所有店铺有售卖的商品)");
            List<Map<String,Object>> productList= iBasicsProductTemplateMapper.getProductStoreListByGoodsId(goodsId);
            return ServerResponse.createBySuccess("查询成功", productList);
        } catch (Exception e) {
            logger.error("getActuarialProductListByGoodsId查询失败:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    private List<DjBasicsProductTemplate> getProdListByGoodsID(String goodsId){
        Example example=new Example(DjBasicsProductTemplate.class);
        example.createCriteria().andEqualTo(DjBasicsProductTemplate.DATA_STATUS,0).
                andEqualTo(DjBasicsProductTemplate.MAKET,1).
                andEqualTo(DjBasicsProductTemplate.TYPE,1).
                andEqualTo(DjBasicsProductTemplate.GOODS_ID,goodsId);
        List<DjBasicsProductTemplate> mapList = iBasicsProductTemplateMapper.selectByExample(example);
        return mapList;
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
    public ServerResponse editSimulateionTemplateConfig(String userId,String configId,String configName,String configType,String configDetailArr,String cityId){
        //编辑（添加修改标题数据）
        DjSimulationTemplateConfig djSimulationTemplateConfig = editSimulateTemplateInfo(userId,configId,configName,configType,cityId);
       //编辑（添加修改标题 下的选项值详情信息）
        JSONArray jsonArr = JSONArray.parseArray(configDetailArr);
        editSimulateTemplateDetailList(jsonArr,djSimulationTemplateConfig,cityId);
        return  ServerResponse.createBySuccessMessage("保存成功");
    }

    /**
     * 编辑标题 信息
     * @param userId 用户ID
     * @param configId 标题 ID
     * @param configName 标题 名称
     * @param configType 模板类型
     * @return
     */
    private DjSimulationTemplateConfig editSimulateTemplateInfo(String userId,String configId,String configName,String configType,String cityId){
        //判断标题是新增还是修改
        DjSimulationTemplateConfig djSimulationTemplateConfig=new DjSimulationTemplateConfig();
        djSimulationTemplateConfig.setConfigName(configName);
        djSimulationTemplateConfig.setModifyDate(new Date());
        djSimulationTemplateConfig.setUpdateBy(userId);
        djSimulationTemplateConfig.setCityId(cityId);
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
    private void editSimulateTemplateDetailList(JSONArray jsonArr,DjSimulationTemplateConfig djSimulationTemplateConfig,String cityId){
        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject obj = jsonArr.getJSONObject(i);
            String id=obj.getString("id");
            DjSimulationTemplateConfigDetail djSimulationTemplateConfigDetail=new DjSimulationTemplateConfigDetail();
            djSimulationTemplateConfigDetail.setImage(obj.getString("image"));
            djSimulationTemplateConfigDetail.setName(obj.getString("name"));
            djSimulationTemplateConfigDetail.setConfigStatus(0);
            djSimulationTemplateConfigDetail.setLabelName(obj.getString("labelName"));
            djSimulationTemplateConfigDetail.setSimulationTemplateId(djSimulationTemplateConfig.getId());
            djSimulationTemplateConfigDetail.setCityId(cityId);
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
    public ServerResponse querySimulateionTemplateConfig(String cityId){
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        try{
            List<SimulationTemplateConfigDTO> simulationTemplateConfigDTOList=djSimulationTemplateConfigMapper.querySimulateionTemplateConfig(null,address,cityId);
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
    public ServerResponse querySimulateionTemplateConfigById(String simulationTemplateId,String cityId){
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        try{
            SimulationTemplateConfigDTO simulationTemplateConfigDTO=new SimulationTemplateConfigDTO();
            if(StringUtils.isNotBlank(simulationTemplateId)){
                List<SimulationTemplateConfigDTO> simulationTemplateConfigDTOList=djSimulationTemplateConfigMapper.querySimulateionTemplateConfig(simulationTemplateId,address,cityId);
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
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse importSimulateExcelBudgets (String name,String fileName,String address,String userId,String cityId){

        //String addressPri = "e://dangjia/";

        List<ActuarialProductDTO> actuarialExcelList=new ArrayList<>();
        actuarialExcelList.addAll(getDataInfoList(address, fileName, 0, 0,"3"));//大管家
        actuarialExcelList.addAll(getDataInfoList(address, fileName, 0, 1,"4"));//拆除
        actuarialExcelList.addAll(getDataInfoList(address, fileName, 0, 2,"6"));//水电
        actuarialExcelList.addAll(getDataInfoList(address, fileName, 0, 3,"8"));//泥工
        actuarialExcelList.addAll(getDataInfoList(address, fileName, 0, 4,"9"));//木工
        actuarialExcelList.addAll(getDataInfoList(address, fileName, 0, 5,"10"));//油漆

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
            djActuarialTemplateConfig.setCityId(cityId);
            djActuarialTemplateConfigMapper.insert(djActuarialTemplateConfig);
            //2.保存excel文件中的内容
            for(ActuarialProductDTO actuarialProductDTO:actuarialExcelList){
                DjActuarialProductConfig djActuarialProductConfig = new DjActuarialProductConfig();
                djActuarialProductConfig.setActuarialTemplateId(djActuarialTemplateConfig.getId());// excel上传地址ID
                djActuarialProductConfig.setProductSn(actuarialProductDTO.getProductSn());
                djActuarialProductConfig.setPurchaseQuantity(actuarialProductDTO.getPurchaseQuantity());
                djActuarialProductConfig.setWorkerTypeId(actuarialProductDTO.getWorkerTypeId());
                djActuarialProductConfig.setCityId(cityId);
                //根据商品编码，查询对应的商品ID及货品ID
                List<DjBasicsProductTemplate> list=iBasicsProductTemplateMapper.queryByProductSn(djActuarialProductConfig.getProductSn());
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

    }

    /**
     * 读取excel中的数据
     * @param addressUrl
     * @param fileName
     * @param headerNum 首列
     * @param sheetIndex 表格
     * @return
     */
    private List<ActuarialProductDTO> getDataInfoList(String addressUrl,String fileName,int headerNum,int sheetIndex,String workTypeId){
        List<ActuarialProductDTO> actuarialExcelList=new ArrayList<>();
        try{
            String addressPri = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            URL url = new URL(addressPri+addressUrl);
            ImportExcel productInfo = new ImportExcel(fileName, url.openStream(),headerNum, sheetIndex);//商品信息
            List<ActuarialProductDTO> caiLiaoList = productInfo.getDataList(ActuarialProductDTO.class, 0);
            for (int i = 0; i < caiLiaoList.size(); i++) {
                ActuarialProductDTO actuarialProductDTO = caiLiaoList.get(i);
                if (CommonUtil.isEmpty(actuarialProductDTO)) {
                    break;
                }
                if (CommonUtil.isEmpty(actuarialProductDTO.getProductSn())) {
                    continue;
                }
                actuarialProductDTO.setWorkerTypeId(workTypeId);
                actuarialExcelList.add(actuarialProductDTO);
            }

        }catch (Exception e){
            logger.error("读取EXCEL异常：",e);
        }
        return actuarialExcelList;

    }


    /**
     * 查询excel列表
     * @return
     */
    public ServerResponse querySimulateExcelList(String cityId){
        try{
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            Example example=new Example(DjActuarialTemplateConfig.class);
            example.createCriteria().andEqualTo(DjActuarialTemplateConfig.CONFIG_TYPE,"3")
                    .andEqualTo(DjActuarialTemplateConfig.DATA_STATUS,0)
                    .andEqualTo(DjActuarialTemplateConfig.CITY_ID,cityId);//查类型为excel的数据列表
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
        }catch (Exception e) {
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }

    //删除上传的excel文件(
    public ServerResponse deleteSimulateExcelById(String id){
        DjActuarialTemplateConfig djActuarialTemplateConfig = new DjActuarialTemplateConfig();
        djActuarialTemplateConfig.setId(id);
        djActuarialTemplateConfig.setDataStatus(1);//0正式数据，1删除
        djActuarialTemplateConfigMapper.updateByPrimaryKeySelective(djActuarialTemplateConfig);
        return ServerResponse.createBySuccess("删除成功",id);
    }

    public ServerResponse querySimulateAssemblyList(String cityId){
        try{
            //查询code中返回的详情列表
            List  templateList=djSimulationTemplateConfigMapper.queryTemplateListByType(cityId);
            List listAll = new ArrayList();
            List list = new ArrayList();
            for(int i=0;i<templateList.size();i++){
                Map  templateMap=(Map)templateList.get(i);
                list.add(templateMap.get("templateDetailList"));
            }
            //初始化每次的list值
            rangeList = new ArrayList();
            logger.info("入参：list:{}"+list);
            range(list, listAll);
            logger.info("返回结果：{}"+rangeList);
            return ServerResponse.createBySuccess("查询成功",getAssemblyList());
        }catch (Exception e) {
            logger.error("querySimulateAssemblyList查询失败",e);
            return ServerResponse.createByErrorMessage("querySimulateAssemblyList查询失败");
        }

    }

    /**
     * 封装要返回给前端的参数
     * @return
     */
    List getAssemblyList(){
        List assemblyList=new ArrayList();
        if(rangeList!=null&&rangeList.size()>0){
            Map assemblyMap = new HashMap();
            for(int i=0;i<rangeList.size();i++){
                List detailList=(List)rangeList.get(i);
                String assemblyCode="";
                String assemblyName="";
                for(Object obj:detailList){
                    String[] strs=obj.toString().split(":");
                    if(StringUtils.isBlank(assemblyCode)){
                        assemblyCode=strs[0];
                        assemblyName=strs[1];
                    }else{
                        assemblyCode=assemblyCode+","+strs[0];
                        assemblyName=assemblyName+","+strs[1];
                    }
                }
                assemblyMap=new HashMap();
                assemblyMap.put("assemblyCode",assemblyCode);
                assemblyMap.put("assemblyName",assemblyName);
                assemblyList.add(assemblyMap);
            }
        }
        return assemblyList;
    }

    //递归循环要排列的组合列表
    void range(List list_range, List list_trans) {
        //获得当前要排列的list的长度
        int size = list_range.size();
        //循环到最后一次
        if (size == 1) {
            //将排列结果汇总
            for (Object object : (List) list_range.get(0)) {
                List l_list = new ArrayList();
                l_list.addAll(list_trans);
                l_list.add(object);
                //将此结果汇总
                rangeList.add(l_list);
            }
        } else {
            //没有循环到底
            //给存排列结果的list添加排列
            List loopList = (List) list_range.get(0);
            List list_loop = new ArrayList();
            list_loop.addAll(list_range);
            list_loop.remove(0);
            for (Object object : loopList) {
                //将其添加到存排列结果的list中
                List list_d = new ArrayList();
                list_d.addAll(list_trans);
                list_d.add(object);
                //递归
                range(list_loop, list_d);
            }
        }

    }

    /**
     * 保存组合精算信息
     * @param assemblyInfoAttr
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveSimulateAssemblyInfo(String assemblyInfoAttr,String userId,String cityId){
        if(assemblyInfoAttr!=null&&StringUtils.isNotBlank(assemblyInfoAttr)){
            logger.info("开始进入saveSimulateAssemblyInfo：========start==========");
            //1.获取需要保存的组合列表
            List<DjActuarialSimulationRelation> actuarialSimulationRelationList=getActuarialSimulationRelationList(assemblyInfoAttr,userId,cityId);
            if(actuarialSimulationRelationList!=null&&actuarialSimulationRelationList.size()>0){
                logger.info("开始数据保存：==================");
                //2.先删除历史正式数据的精算组合
                Example example=new Example(DjSimulationTemplateConfigDetail.class);
                example.createCriteria().andEqualTo(DjSimulationTemplateConfigDetail.DATA_STATUS,"0")
                                        .andEqualTo(DjSimulationTemplateConfigDetail.CITY_ID,cityId);
                djActuarialSimulationRelationMapper.deleteByExample(example);
                //3.删除正式数据关联的标题及精算表数据
                example=new Example(DjSimulationTemplateConfigDetail.class);
                example.createCriteria().andEqualTo(DjSimulationTemplateConfigDetail.CONFIG_STATUS,"1")
                                        .andEqualTo(DjSimulationTemplateConfigDetail.CITY_ID,cityId);
                djSimulationTemplateConfigDetailMapper.deleteByExample(example);//删除标题下的选项值信息
                djSimulationTemplateConfigMapper.deleteSimulationTemplate(cityId);//删除无用的标题数据
                djActuarialProductConfigMapper.deleteActuarialProductByTemplate(cityId);//删除无用的精算详情数据
                example=new Example(DjActuarialTemplateConfig.class);
                example.createCriteria().andEqualTo(DjActuarialTemplateConfig.DATA_STATUS,"1")
                                        .andEqualTo(DjActuarialTemplateConfig.CONFIG_TYPE,"3")
                                        .andEqualTo(DjSimulationTemplateConfig.CITY_ID,cityId);
                djActuarialTemplateConfigMapper.deleteByExample(example);//删除无用的精算信息
                //4.保存新的精算组合进去
                djActuarialSimulationRelationMapper.batchInsertAssemblyInfo(actuarialSimulationRelationList);
                //5.将临时数据的标题及详情改为正式数据，并重新拷贝一份临时数据出来，后续操作用
                djSimulationTemplateConfigDetailMapper.updateTemplateDetailConfig(cityId);//修改临时数据为正式数据
                djSimulationTemplateConfigDetailMapper.batchInsertTempateDetail(cityId);//批量添加一批新的临时数据
            }
        }

        return ServerResponse.createBySuccessMessage("保存成功");
    }

    /**
     * 获取需要保存的数据
     * @param assemblyInfoAttr
     * @param userId
     */
    private List<DjActuarialSimulationRelation> getActuarialSimulationRelationList(String assemblyInfoAttr, String userId,String cityId){
        List<DjActuarialSimulationRelation> actuarialSimulationRelationList=new ArrayList<>();
        JSONArray jsonArr = JSONArray.parseArray(assemblyInfoAttr);
        DjActuarialSimulationRelation djActuarialSimulationRelation;
        for(int i=0;i<jsonArr.size();i++){
            JSONObject obj = jsonArr.getJSONObject(i);
            String actuarialExcelId = obj.getString("actuarialExcelId");
            JSONArray assemblyInfoList = obj.getJSONArray("assemblyInfoList");
            if(assemblyInfoList!=null&&assemblyInfoList.size()>0){
                for(int j=0;j<assemblyInfoList.size();j++){
                    Map assemblyMap=(Map)assemblyInfoList.get(j);
                    djActuarialSimulationRelation=new DjActuarialSimulationRelation();
                    djActuarialSimulationRelation.setActuarialTemplateId(actuarialExcelId);
                    djActuarialSimulationRelation.setSimulationCodeGroup((String)assemblyMap.get("assemblyCode"));
                    djActuarialSimulationRelation.setSimulationNameGroup((String)assemblyMap.get("assemblyName"));
                    djActuarialSimulationRelation.setCreateBy(userId==null?"":userId);
                    djActuarialSimulationRelation.setUpdateBy(userId==null?"":userId);
                    djActuarialSimulationRelation.setCreateDate(new Date());
                    djActuarialSimulationRelation.setModifyDate(new Date());
                    djActuarialSimulationRelation.setCityId(cityId);
                    actuarialSimulationRelationList.add(djActuarialSimulationRelation);
                }
            }
        }
        return actuarialSimulationRelationList;
    }

    /**
     * 查询精算组合关系表
     * @return
     */
    public  ServerResponse querySimulateAssemblyRelateionList(String cityId){
        try{
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<ActuarialSimulateionReationDTO> relationList=djActuarialSimulationRelationMapper.querySimulateAssemblyRelateionList(address,cityId);
            return ServerResponse.createBySuccess("查询成功",relationList);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }
}
