package com.dangjia.acg.service.actuary;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.actuary.IActuarialTemplateMapper;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.actuary.IWaterfallFlowConfigMapper;
import com.dangjia.acg.mapper.product.ICategoryLabelMapper;
import com.dangjia.acg.modle.actuary.ActuarialTemplate;
import com.dangjia.acg.modle.actuary.WaterfallFlowConfig;
import com.dangjia.acg.modle.product.CategoryLabel;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.service.product.CategoryLabelService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @类 名： ProductServiceImpl
 * @功能描述： 商品service实现类
 * @作者信息： fzh
 */
@Service
public class WaterfallFlowConfigService {

    @Autowired
    private IWaterfallFlowConfigMapper waterfallFlowConfigMapper;
    @Autowired
    private ICategoryLabelMapper iCategoryLabelMapper;
    private static Logger logger = LoggerFactory.getLogger(WaterfallFlowConfigService.class);

   //查询瀑布流
    public ServerResponse queryWaterfallFlowConfig(String cityId){
        try{
            Example example=new Example(WaterfallFlowConfig.class);
            example.createCriteria().andEqualTo(WaterfallFlowConfig.CITY_ID,cityId)
            .andEqualTo(WaterfallFlowConfig.TYPE,1);//查询 所有的标签数据
            List<WaterfallFlowConfig> configList=waterfallFlowConfigMapper.selectByExample(example);
            return ServerResponse.createBySuccess("查询成功",configList);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //新增或修改瀑布流
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse editWaterfallFlowConfig(String waterfallConfigId,String userId,String cityId,
                                                  String name,Integer sort,String sourceInfoList){

        //判断名称是否存在
        Example example=new Example(WaterfallFlowConfig.class);
        example.createCriteria().andEqualTo(WaterfallFlowConfig.NAME,name)
        .andEqualTo(WaterfallFlowConfig.CITY_ID,cityId);
        if(StringUtils.isNotBlank(waterfallConfigId)){
            example.createCriteria().andNotEqualTo(WaterfallFlowConfig.ID,waterfallConfigId);
        }
        List<WaterfallFlowConfig> list=waterfallFlowConfigMapper.selectByExample(example);
        //先添加标签，再添加标签值
        WaterfallFlowConfig waterfallFlowConfig=new WaterfallFlowConfig();
        if(StringUtils.isNotBlank(waterfallConfigId)){
            waterfallFlowConfig=waterfallFlowConfigMapper.selectByPrimaryKey(waterfallConfigId);
            if(list!=null&&list.size()>0&&!name.equals(waterfallFlowConfig.getName())){
                return ServerResponse.createByErrorMessage("标签名称重复，请修改");
            }
            waterfallFlowConfig.setName(name);
            waterfallFlowConfig.setSort(sort);
            waterfallFlowConfig.setModifyDate(new Date());
            waterfallFlowConfigMapper.updateByPrimaryKeySelective(waterfallFlowConfig);
            //删除旧的数据源配置
            example=new Example(WaterfallFlowConfig.class);
            example.createCriteria().andEqualTo(WaterfallFlowConfig.PARENT_ID,waterfallConfigId);
            waterfallFlowConfigMapper.deleteByExample(example);
        }else{
            if(list!=null&&list.size()>0){
                return ServerResponse.createByErrorMessage("标签名称重复，请修改");
            }
            waterfallFlowConfig.setName(name);
            waterfallFlowConfig.setSort(sort);
            waterfallFlowConfig.setParentId("1");
            waterfallFlowConfig.setType(1);//标签
            waterfallFlowConfig.setCityId(cityId);
            waterfallFlowConfig.setModifyDate(new Date());
            waterfallFlowConfigMapper.insert(waterfallFlowConfig);
        }
        String parentId=waterfallFlowConfig.getId();
        //添加新的数据源配置
        JSONArray sourceArray=JSONArray.parseArray(sourceInfoList);
        if(sourceArray!=null){
            String str1="";//商品数据源
            String str2="";//攻略数据源
            WaterfallFlowConfig waterfallConfig;
            for (int i = 0; i < sourceArray.size(); i++) {
                JSONObject obj = (JSONObject) sourceArray.get(i);
                String anyId = obj.getString("anyId");//(类型为2，3，4，5中的任决ID）
                String anyName = obj.getString("anyName");
                Integer anyType = obj.getInteger("anyType");//类型：2商品，3装修说，4装修攻略，5精选案例
                Integer ratio = obj.getInteger("ratio");
                waterfallConfig =new WaterfallFlowConfig();//占比
                waterfallConfig.setCityId(cityId);
                waterfallConfig.setParentId(parentId);
                waterfallConfig.setName(anyName);
                waterfallConfig.setAnyId(anyId);
                waterfallConfig.setType(anyType);
                waterfallConfig.setRatio(ratio);
                waterfallConfig.setSort(i+1);
                if(anyType==2){
                    str1="商品";
                    waterfallConfig.setRemark("商品");
                }else{
                    str2="攻略";
                    waterfallConfig.setRemark("攻略");
                }
                waterfallFlowConfigMapper.insert(waterfallConfig);//添加数据源信息

            }
            if(StringUtils.isNotBlank(str1)&&StringUtils.isNotBlank(str2)){
                waterfallFlowConfig.setRemark(str1+","+str2);
            }else{
                waterfallFlowConfig.setRemark(str1+str2);
            }
            waterfallFlowConfigMapper.updateByPrimaryKeySelective(waterfallFlowConfig);//修改数据源字段

        }

        return ServerResponse.createBySuccessMessage("保存成功");
    }

    //查询瀑布流详情
    public ServerResponse queryWaterfallFlowConfigInfo(String waterfallConfigId){
        try{
            Map<String,Object> resultMap=new HashMap<>();
            //1.查询分类标签列表
            List<Map<String, Object>> categoryLabelList = new ArrayList<>();
            List<CategoryLabel> labelList = iCategoryLabelMapper.getCategoryLabel(null);
            if(labelList!=null){
                for (CategoryLabel categoryLabel : labelList) {
                    Map<String, Object> map = getSourceMap(categoryLabel.getId(),categoryLabel.getName(),2);//类别标签
                    categoryLabelList.add(map);
                }
            }
            resultMap.put("categoryLabelList",categoryLabelList);//类别标签列表
            //2.设计内容（攻略分类列表）
            List<Map<String, Object>> soureContentList = new ArrayList<>();
            List<Map<String,Object>> contentSourceList=new ArrayList();
            Map<String,Object> map = getSourceMap("C1001","装修说",3);
            soureContentList.add(map);
            map = getSourceMap("C1002","装修攻略",4);
            soureContentList.add(map);
            map = getSourceMap("C1003","精选案例",5);
            soureContentList.add(map);
            resultMap.put("soureContentList",soureContentList);//内容分类列表
            //3.查询已配置的标签及数据源信息
            if(StringUtils.isNotBlank(waterfallConfigId)){
                WaterfallFlowConfig waterfallFlowConfig=waterfallFlowConfigMapper.selectByPrimaryKey(waterfallConfigId);
                Example example=new Example(WaterfallFlowConfig.class);
                example.createCriteria().andEqualTo(WaterfallFlowConfig.PARENT_ID,waterfallConfigId);
                example.orderBy(WaterfallFlowConfig.SORT);
                List<WaterfallFlowConfig> configList=waterfallFlowConfigMapper.selectByExample(example);
                resultMap.put("waterfallConfigId",waterfallFlowConfig.getId());//瀑布流标签ID
                resultMap.put("name",waterfallFlowConfig.getName());//标签名称
                resultMap.put("sort",waterfallFlowConfig.getSort());//标签排序
                resultMap.put("configList",configList);//已配置数据源列表
            }

            return ServerResponse.createBySuccess("查询成功",resultMap);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    public Map<String,Object> getSourceMap(String id,String name,Integer type){
        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put("anyId",id);
        paramMap.put("anyName",name);
        paramMap.put("anyType",type);
        return paramMap;
    }


    //删除瀑布流
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse deleteWaterfallFlowConfig(String waterfallConfigId){
        //删除旧的数据源配置
        Example example=new Example(WaterfallFlowConfig.class);
        example.createCriteria().andEqualTo(WaterfallFlowConfig.PARENT_ID,waterfallConfigId);
        waterfallFlowConfigMapper.deleteByExample(example);
        //删除标签配置
        waterfallFlowConfigMapper.deleteByPrimaryKey(waterfallConfigId);

        return ServerResponse.createBySuccess("删除成功");
    }

}

