package com.dangjia.acg.service.actuary;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.*;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.*;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.attribute.Attribute;
import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.dangjia.acg.modle.house.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/11/15 0015
 * Time: 19:27
 */
@Service
public class ActuaryOperationService {

    @Autowired
    private IBudgetWorkerMapper budgetWorkerMapper;
    @Autowired
    private IBudgetMaterialMapper budgetMaterialMapper;
    @Autowired
    private GetForBudgetAPI getForBudgetAPI;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private WorkerTypeAPI workerTypeAPI;
    @Autowired
    private IGoodsMapper goodsMapper;
    @Autowired
    private IProductMapper productMapper;
    @Autowired
    private IWorkerGoodsMapper workerGoodsMapper;
    @Autowired
    private IUnitMapper unitMapper;
    @Autowired
    private ITechnologyMapper technologyMapper;
    @Autowired
    private IAttributeMapper attributeMapper;
    @Autowired
    private IBrandSeriesMapper brandSeriesMapper;
    @Autowired
    private IAttributeValueMapper valueMapper;
    @Autowired
    private IAttributeValueMapper attributeValueMapper;

    @Autowired
    private HouseAPI houseAPI;
    /**
     * 选择取消精算
     * buy": 0必买；1可选选中；2自购; 3可选没选中(业主已取消)
     *
     * 这里往精算表插入最新价格
     */
    public ServerResponse choiceGoods(String budgetIdList) {
        try {
            JSONArray arr = JSONArray.parseArray(budgetIdList);
            for(int i=0; i<arr.size(); i++){
                JSONObject obj = arr.getJSONObject(i);
                int buy = Integer.parseInt(obj.getString("buy"));
                String budgetMaterialId = obj.getString("budgetMaterialId");

                BudgetMaterial budgetMaterial = budgetMaterialMapper.selectByPrimaryKey(budgetMaterialId);
                if(buy == 1){
                    budgetMaterial.setDeleteState(2);//取消
                }else if (buy == 3){
                    budgetMaterial.setDeleteState(0);//选回来
                }else {
                    return ServerResponse.createByErrorMessage("操作失败,参数错误");
                }
                budgetMaterialMapper.updateByPrimaryKeySelective(budgetMaterial);
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 更换货品
     */
    public ServerResponse changeProduct(String productId, String budgetMaterialId){
        try{
            BudgetMaterial budgetMaterial = budgetMaterialMapper.selectByPrimaryKey(budgetMaterialId);
            Product product = productMapper.selectByPrimaryKey(productId);
            budgetMaterial.setProductId(productId);
            budgetMaterial.setProductSn(product.getProductSn());
            budgetMaterial.setProductName(product.getName());
            budgetMaterial.setPrice(product.getPrice());
            budgetMaterial.setCost(product.getCost());
            budgetMaterial.setTotalPrice(product.getPrice() * budgetMaterial.getShopCount());
            budgetMaterialMapper.updateByPrimaryKeySelective(budgetMaterial);
            return ServerResponse.createBySuccessMessage("操作成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 选择货品刷新页面
     * @param goodsId
     * @param brandSeriesId
     * @param attributeIdArr 属性值id集合
     */
    public ServerResponse selectProduct(String goodsId, String brandSeriesId, String attributeIdArr, String budgetMaterialId){
        try {
            String[] valueIdArr = attributeIdArr.split(",");
            Product product = productMapper.selectProduct(goodsId,brandSeriesId,valueIdArr);
            if (product == null){
                return ServerResponse.createBySuccess("暂无该货号","");
            }
            GoodsDTO goodsDTO= goodsDetail(product, budgetMaterialId);
            if (goodsDTO != null){
                return ServerResponse.createBySuccess("查询成功", goodsDTO);
            }else {
                return ServerResponse.createByErrorMessage("查询失败,数据异常");
            }
        } catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败,数据异常");
        }
    }

    /**
     * 商品详情
     * gId:  budgetWorkerId   budgetMaterialId
     */
    public ServerResponse getCommo(String gId, int type){
        try {
            if (type == 1){//人工
                BudgetWorker budgetWorker = budgetWorkerMapper.selectByPrimaryKey(gId);
                WorkerGoods workerGoods = workerGoodsMapper.selectByPrimaryKey(budgetWorker.getWorkerGoodsId());//人工商品
                WGoodsDTO wGoodsDTO = new WGoodsDTO();
                wGoodsDTO.setImage(getImage(workerGoods.getImage()));
                wGoodsDTO.setPrice("￥"+workerGoods.getPrice()+"/"+unitMapper.selectByPrimaryKey(workerGoods.getUnitId()).getName());
                wGoodsDTO.setName(workerGoods.getName());
                wGoodsDTO.setWorkerDec(workerGoods.getWorkerDec());
                List<Technology> technologyList = technologyMapper.queryTechnologyByWgId(workerGoods.getId());
                for (Technology technology:technologyList) {
                    technology.setImage(getImage(technology.getImage()));//图一张
                }
                wGoodsDTO.setTechnologyList(technologyList);
                return ServerResponse.createBySuccess("查询成功",wGoodsDTO);
            }else if(type == 2 || type == 3){//材料商品  服务商品
                BudgetMaterial budgetMaterial = budgetMaterialMapper.selectByPrimaryKey(gId);
                Product product = productMapper.selectByPrimaryKey(budgetMaterial.getProductId());//当前 货品
                GoodsDTO goodsDTO= goodsDetail(product, gId);
                if (goodsDTO!= null){
                    return ServerResponse.createBySuccess("查询成功", goodsDTO);
                }else {
                    return ServerResponse.createByErrorMessage("查询失败,数据异常");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败,数据异常");
        }
        return ServerResponse.createByErrorMessage("查询失败,type错误");
    }

    /**
     * 商品详情
     * gId:  WorkerGoodsId   ProductId
     */
    public ServerResponse getGoodsDetail(String gId, String cityId, int type){
        try {
            if (type == 1){//人工
                WorkerGoods workerGoods = workerGoodsMapper.selectByPrimaryKey(gId);//人工商品
                WGoodsDTO wGoodsDTO = new WGoodsDTO();
                if(!CommonUtil.isEmpty(workerGoods.getImage())) {
                    wGoodsDTO.setImage(getImage(workerGoods.getImage()));
                }
                wGoodsDTO.setPrice("￥"+workerGoods.getPrice()+"/"+unitMapper.selectByPrimaryKey(workerGoods.getUnitId()).getName());
                wGoodsDTO.setName(workerGoods.getName());
                wGoodsDTO.setWorkerDec(workerGoods.getWorkerDec());
                List<Technology> technologyList = technologyMapper.queryTechnologyByWgId(workerGoods.getId());
                for (Technology technology:technologyList) {
                    technology.setImage(getImage(technology.getImage()));//图一张
                }
                wGoodsDTO.setTechnologyList(technologyList);
                return ServerResponse.createBySuccess("查询成功",wGoodsDTO);
            }else if(type == 2 || type == 3){//材料商品  服务商品
                Product product = productMapper.selectByPrimaryKey(gId);//当前 货品
                GoodsDTO goodsDTO= goodsDetail(product, gId);
                if (goodsDTO!= null){
                    return ServerResponse.createBySuccess("查询成功", goodsDTO);
                }else {
                    return ServerResponse.createByErrorMessage("查询失败,数据异常");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败,数据异常");
        }
        return ServerResponse.createByErrorMessage("查询失败,type错误");
    }
    //商品详情
    public GoodsDTO goodsDetail(Product product, String budgetMaterialId){
        try{
            GoodsDTO goodsDTO = new GoodsDTO();//长图  品牌系列图+属性图(多个)
            List<String> imageList = new ArrayList<String>();//长图片 多图组合
            List<AttributeDTO> attributeDTOList = new ArrayList<AttributeDTO>();//属性
            List<BrandSeriesDTO> brandSeriesDTOList = new ArrayList<BrandSeriesDTO>();//品牌

            Goods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());//当前 商品
            //该商品关联所有品牌系列
            List<BrandSeries> brandSeriesList = brandSeriesMapper.queryBrandByGid(goods.getId());
            //根据商品分类id关联所有价格属性
            List<Attribute> goodsAttributeList = attributeMapper.queryPriceAttribute(goods.getCategoryId());

            goodsDTO.setBudgetMaterialId(budgetMaterialId);
            goodsDTO.setProductId(product.getId());
            goodsDTO.setGoodsId(goods.getId());
            goodsDTO.setImage(getImage(product.getImage()));//图一张
            goodsDTO.setPrice("￥"+product.getPrice()+"/"+product.getUnitName());
            goodsDTO.setName(product.getName());
            goodsDTO.setUnitName(product.getUnitName());//单位
            goodsDTO.setProductType(goods.getType());//材料类型
            for (Attribute attribute : goodsAttributeList){//循环属性
                List<AttributeValueDTO> valueDTOList = new ArrayList<AttributeValueDTO>();//属性选项集合
                AttributeDTO attributeDTO = new AttributeDTO();
                attributeDTO.setName(attribute.getName());
                //属性id查属性值
                List<AttributeValue> valueList =  valueMapper.queryByAttributeId(attribute.getId());
                for (AttributeValue attributeValue : valueList){//循环属性值
                    AttributeValueDTO valueDTO = new AttributeValueDTO();
                    valueDTO.setAttributeValueId(attributeValue.getId());
                    valueDTO.setName(attributeValue.getName());
                    if(this.isValue(attributeValue.getId(), product.getValueIdArr())){//当前货品属性
                        valueDTO.setState(1);//选中
                        imageList.add(getImage(attributeValue.getImage()));//属性图
                    }else {
                        valueDTO.setState(0);//未选中
                    }
                    valueDTOList.add(valueDTO);//添加属性选项进属性集合
                }
                attributeDTO.setValueDTOList(valueDTOList);
                attributeDTOList.add(attributeDTO);
            }
            goodsDTO.setAttributeDTOList(attributeDTOList);
            for (BrandSeries brandSeries : brandSeriesList){//循环品牌系列
                BrandSeriesDTO brandSeriesDTO = new BrandSeriesDTO();
                brandSeriesDTO.setBrandSeriesId(brandSeries.getId());
                brandSeriesDTO.setName(brandSeries.getName());
                if(brandSeries.getId().equals(product.getBrandSeriesId())){
                    brandSeriesDTO.setState(1);//选中
                    imageList.add(getImage(brandSeries.getImage()));//加入品牌系列图
                }else {
                    brandSeriesDTO.setState(0);//未选中
                }
                brandSeriesDTOList.add(brandSeriesDTO);
            }
            goodsDTO.setBrandDTOList(brandSeriesDTOList);
            goodsDTO.setImageList(imageList);

            return goodsDTO;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //判断该货品是不是该属性
    private boolean isValue(String valueId,String valueIdArr){
        String[] valueIdList = valueIdArr.split(",");
        for (int i=0; i<valueIdList.length; i++){
            if (valueId.equals(valueIdList[i])){
                return true;
            }
        }
        return false;
    }
    //取第一张图
    private String getImage(String images){
        try{
            if(StringUtil.isNotEmpty(images)){
                String[] imageArr = images.split(",");
                return configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + imageArr[0];
            }
        }catch (Exception e){
            e.printStackTrace();
            return "";//图片上传错误
        }
        return "";//暂无图片
    }

    /**
     * 查看工序 type 人工1 材料2 服务3
     * 支付时精算goods详情 查最新价格 共用此方法
     */
    public ServerResponse confirmActuaryDetail(String userToken,String houseId,String workerTypeId,int type,String cityId){
        ServerResponse serverResponse =workerTypeAPI.getNameByWorkerTypeId(workerTypeId);
        String  workerTypeName = "";
        if(serverResponse.isSuccess()) {
            workerTypeName = serverResponse.getResultObj().toString();
        }else {
            return ServerResponse.createByErrorMessage("查询工序精算失败");
        }
        Map<Integer,String> mapgx = new HashMap<>() ;
        mapgx.put(DjConstants.GXType.RENGGONG, "人工");
        mapgx.put(DjConstants.GXType.CAILIAO, "材料");
        mapgx.put(DjConstants.GXType.FUWU, "服务");
        FlowDTO flowDTO = new FlowDTO();
        flowDTO.setName(workerTypeName);
        flowDTO.setType(type);
        List<FlowActuaryDTO> flowActuaryDTOList = new ArrayList<FlowActuaryDTO>();
        String typsValue=mapgx.get(type);
        if(CommonUtil.isEmpty(typsValue)){
            return ServerResponse.createByErrorMessage("type参数错误");
        }
        if (type == DjConstants.GXType.RENGGONG){
            List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.getBudgetWorkerList(houseId,workerTypeId);
            for (BudgetWorker bw : budgetWorkerList){
                WorkerGoods workerGoods = workerGoodsMapper.selectByPrimaryKey(bw.getWorkerGoodsId());
                FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
                flowActuaryDTO.setName(bw.getName());
                flowActuaryDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + workerGoods.getImage());
                flowActuaryDTO.setTypeName(typsValue);
                flowActuaryDTO.setShopCount(bw.getShopCount());
                String url=configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.YZPageAddress.COMMO,userToken,cityId,flowActuaryDTO.getTypeName()+"商品详情")+"&gId="+bw.getId()+"&type="+type;
                flowActuaryDTO.setUrl(url);
                flowActuaryDTO.setPrice("￥"+workerGoods.getPrice()+"/"+unitMapper.selectByPrimaryKey(workerGoods.getUnitId()).getName());
                flowActuaryDTO.setTotalPrice("￥"+ workerGoods.getPrice() * bw.getShopCount());
                flowActuaryDTOList.add(flowActuaryDTO);
            }
            Double workerPrice = budgetWorkerMapper.getBudgetWorkerPrice(houseId,workerTypeId);//精算工钱
            flowDTO.setSumTotal("￥" + workerPrice);//合计
        }else{
            List<BudgetMaterial> budgetMaterialList =null;
            if (type == DjConstants.GXType.CAILIAO){
                budgetMaterialList = budgetMaterialMapper.getBudgetCaiList(houseId,workerTypeId);
                Double caiPrice = budgetMaterialMapper.getBudgetCaiPrice(houseId,workerTypeId);
                flowDTO.setSumTotal("￥" + caiPrice);//合计
            }
            if (type == DjConstants.GXType.FUWU){
                budgetMaterialList = budgetMaterialMapper.getBudgetSerList(houseId,workerTypeId);
                Double serPrice = budgetMaterialMapper.getBudgetSerPrice(houseId,workerTypeId);
                flowDTO.setSumTotal("￥" + serPrice);//合计
            }
            for (BudgetMaterial bm : budgetMaterialList){
                Goods goods = goodsMapper.selectByPrimaryKey(bm.getGoodsId());
                Product product = productMapper.selectByPrimaryKey(bm.getProductId());
                FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
                flowActuaryDTO.setBudgetMaterialId(bm.getId());
                flowActuaryDTO.setName(bm.getGoodsName());
                flowActuaryDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + product.getImage());
                flowActuaryDTO.setTypeName(typsValue);
                flowActuaryDTO.setShopCount(bm.getShopCount());
                String url=configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.YZPageAddress.COMMO,userToken,cityId,flowActuaryDTO.getTypeName()+"商品详情")+"&gId="+bm.getId()+"&type="+type;
                flowActuaryDTO.setUrl(url);
                if(bm.getDeleteState() == 2){
                    flowActuaryDTO.setBuy(3);//可选没选中(业主已取消)
                }else {
                    flowActuaryDTO.setBuy(goods.getBuy());
                }
                flowActuaryDTO.setAttribute(getAttributes(product));//拼接属性品牌
                flowActuaryDTO.setPrice("￥"+product.getPrice()+"/"+product.getUnitName());
                flowActuaryDTO.setTotalPrice("￥"+ product.getPrice() * bm.getShopCount());
                flowActuaryDTOList.add(flowActuaryDTO);
            }
        }
        flowDTO.setFlowActuaryDTOList(flowActuaryDTOList);
        return ServerResponse.createBySuccess("查询成功",flowDTO);
    }
    //拼接属性品牌
    private String getAttributes(Product product){
        String attributes = "";
        try {
            String[] valueIdArr = product.getValueIdArr().split(",");
            for (int i=0; i < valueIdArr.length; i++){
                AttributeValue attributeValue = attributeValueMapper.selectByPrimaryKey(valueIdArr[i]);
                attributes = attributes + " " + attributeValue.getName();
            }
            BrandSeries brandSeries = brandSeriesMapper.selectByPrimaryKey(product.getBrandSeriesId());
            attributes = attributes + " " + brandSeries.getName();
        }catch (Exception e){
            e.printStackTrace();
            return "查询属性失败";
        }
        return attributes;
    }

    /**
     * 精算详情 productType  0：材料；1：服务
     */
    public ServerResponse confirmActuary(String userToken,String houseId, String cityId){
        //从master获取工序详情
        List<Map<String,String>> mapList  = getForBudgetAPI.getFlowList(houseId);
        ActuaryDetailsDTO actuaryDetailsDTO = new ActuaryDetailsDTO();//最外层
        List<FlowDetailsDTO> flowDetailsDTOList = new ArrayList<FlowDetailsDTO>();
        for (Map<String,String> map : mapList){
            String name = map.get("name");
            String workerTypeId = map.get("workerTypeId");
            FlowDetailsDTO flowDetailsDTO = new FlowDetailsDTO();
            flowDetailsDTO.setName(name);
            List<DetailsDTO> detailsDTOList = new ArrayList<DetailsDTO>();//人工材料服务
            List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.getBudgetWorkerList(houseId,workerTypeId);//人工明细
            List<BudgetMaterial> materialCaiList = budgetMaterialMapper.getBudgetCaiList(houseId,workerTypeId);//材料明细
            List<BudgetMaterial> materialSerList = budgetMaterialMapper.getBudgetSerList(houseId,workerTypeId);//服务明细
            List<Map> mapworker =new ArrayList<>();
            Map<Integer,String> mapgx = new HashMap<>() ;
            mapgx.put(DjConstants.GXType.RENGGONG, "人工");
            mapgx.put(DjConstants.GXType.CAILIAO, "材料");
            mapgx.put(DjConstants.GXType.FUWU, "服务");
            for (Map.Entry<Integer, String> entry :mapgx.entrySet()) {
                Map m= new HashMap();
                m.put("key",String.valueOf(entry.getKey()));
                m.put("name",entry.getValue());
                Integer size=0;
                if(DjConstants.GXType.RENGGONG==entry.getKey()){size=budgetWorkerList.size(); }
                if(DjConstants.GXType.CAILIAO==entry.getKey()){size=materialCaiList.size(); }
                if(DjConstants.GXType.FUWU==entry.getKey()){size=materialSerList.size(); }
                m.put("size",size);
                mapworker.add(m);
            }
            for (Map mp:mapworker) {
                Integer size=(Integer)mp.get("size");
                String names=(String)mp.get("name");
                String key=(String)mp.get("key");
                if(size > 0){
                    DetailsDTO detailsDTO = new DetailsDTO();
                    detailsDTO.setImage("");
                    detailsDTO.setNameA(names);
                    detailsDTO.setNameB(name + "阶段"+names);
                    detailsDTO.setNameC(names+"明细");
                    detailsDTO.setType(key);
                    String url=configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.YZPageAddress.CONFIRMACTUARYDETAIL,userToken,cityId,names+"明细")+"&houseId="+houseId+"&workerTypeId="+workerTypeId+"&type="+key;
                    detailsDTO.setUrl(url);
                    detailsDTOList.add(detailsDTO);
                }
            }
            flowDetailsDTO.setDetailsDTOList(detailsDTOList);
            flowDetailsDTOList.add(flowDetailsDTO);
        }
        House house = houseAPI.getHouseById(houseId);
        actuaryDetailsDTO.setHouseId(houseId);
        actuaryDetailsDTO.setFlowDetailsDTOList(flowDetailsDTOList);
        actuaryDetailsDTO.setBudgetOk(house.getBudgetOk());

        return ServerResponse.createBySuccess("查询成功", actuaryDetailsDTO);
    }
}
