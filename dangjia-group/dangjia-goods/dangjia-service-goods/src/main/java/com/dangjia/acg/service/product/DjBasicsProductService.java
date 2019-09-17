package com.dangjia.acg.service.product;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.app.repair.MasterMendWorkerAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.product.ActuarialGoodsDTO;
import com.dangjia.acg.dto.product.AppBasicsProductDTO;
import com.dangjia.acg.dto.product.BasicsProductDTO;
import com.dangjia.acg.dto.product.DjBasicsLabelDTO;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.IAttributeValueMapper;
import com.dangjia.acg.mapper.basics.ITechnologyMapper;
import com.dangjia.acg.mapper.basics.IUnitMapper;
import com.dangjia.acg.mapper.product.*;
import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.product.*;
import com.dangjia.acg.service.basics.TechnologyService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.dangjia.acg.util.StringTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

/**
 * 产品逻辑处理层
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Service
public class DjBasicsProductService {
    private static Logger LOG = LoggerFactory.getLogger(DjBasicsProductService.class);
    @Autowired
    private DjBasicsProductMapper djBasicsProductMapper;
    @Autowired
    private DjBasicsLabelMapper djBasicsLabelMapper;
    @Autowired
    private DjBasicsLabelValueMapper djBasicsLabelValueMapper;
    @Autowired
    private DjBasicsProductLabelValMapper djBasicsProductLabelValMapper;

    @Autowired
    private IBasicsGoodsMapper iBasicsGoodsMapper;
    @Autowired
    private TechnologyService technologyService;

    @Autowired
    private ITechnologyMapper iTechnologyMapper;

    @Autowired
    private DjBasicsGoodsMapper djBasicsGoodsMapper;
    @Autowired
    private DjBasicsProductMaterialMapper djBasicsProductMaterialMapper;

    @Autowired
    private DjBasicsProductWorkerMapper djBasicsProductWorkerMapper;

    @Autowired
    private IBudgetWorkerMapper iBudgetWorkerMapper;
    @Autowired
    private MasterMendWorkerAPI masterMendWorkerAPI;
    @Autowired
    private IUnitMapper iUnitMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IAttributeValueMapper iAttributeValueMapper;
    /**
     * 查询商品信息
     *
     * @param name
     * @return
     */
    public ServerResponse queryProductData(String name) {
        Example example = new Example(DjBasicsProduct.class);
        if (!CommonUtil.isEmpty(name)) {
            example.createCriteria().andLike(DjBasicsProduct.NAME, "%" + name + "%");
            List<DjBasicsProduct> list = djBasicsProductMapper.selectByExample(example);
            if (list.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            return ServerResponse.createBySuccess("查询成功", list);
        }
        return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
    }

    /**
     * 查看商品详情
     * @param request
     * @param productSn
     * @return
     */
    public ServerResponse queryDataByProductId(HttpServletRequest request, String productSn) {
        try {
            Example example = new Example(DjBasicsProduct.class);
            example.createCriteria().andEqualTo(DjBasicsProduct.PRODUCT_SN,productSn);
            List<DjBasicsProduct> djBasicsProduct = djBasicsProductMapper.selectByExample(example); //根据商品编号查询对象
            //获取商品ID，然后关联商品表
            String goodsId = djBasicsProduct.get(0).getGoodsId();
            //判断是人工商品还是货品商品
            BasicsGoods  basicsGoods = iBasicsGoodsMapper.selectByPrimaryKey(goodsId);
            //组合实体对象DTO返回   类型0：材料；1：服务；2：人工；3：体验；4：增值
            int type = basicsGoods.getType();
            AppBasicsProductDTO appBasicsProductDTO=null;
            if (type == 0 || type == 1) {//非人工
                //dj_basics_product_material
                appBasicsProductDTO= djBasicsProductMapper.queryProductMaterial(productSn).get(0);
            } else if (type == 2) {//人工
                //dj_basics_product_worker
                appBasicsProductDTO= djBasicsProductMapper.queryProductWorker(productSn).get(0);
            }
            appBasicsProductDTO.setType(type);//初始化类型（人工和非人工）
            return ServerResponse.createBySuccess("查询成功", appBasicsProductDTO);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }



    /**
     * 保存product
     * 0.判断商品是否重复
     * 1.保存商品共有信息
     * 2.保存人工或材料商品的个性化信息
     * <p>Title: insertProduct</p>
     * <p>Description: </p>
     *
     * @param productArr
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse insertBatchProduct(String productArr) {

            JSONArray jsonArr = JSONArray.parseArray(productArr);
            //1.商品作校验，校验前端传过来的商品是否符合条件
            String resCheckStr = checkProductData(jsonArr);
            if(StringUtils.isNotBlank(resCheckStr)){
                return ServerResponse.createByErrorMessage(resCheckStr);
            }

            //2.添加商品信息
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                BasicsProductDTO basicsProductDTO = JSONObject.toJavaObject(obj, BasicsProductDTO.class);
                String goodsId=basicsProductDTO.getGoodsId();//货品ID
                DjBasicsGoods basicsGoods = djBasicsGoodsMapper.selectByPrimaryKey(goodsId);//查询货品表信息，判断是人工还是材料商品新增
                //2.1添加商品主表信息
                String[] imgArr = basicsProductDTO.getImage().split(",");
//                String[] technologyIds = obj.getString("technologyIds").split(",");//工艺节点
                StringBuilder imgStr = new StringBuilder();
                for (int j = 0; j < imgArr.length; j++) {
                    String img = imgArr[j];
                    if (j == imgArr.length - 1) {
                        imgStr.append(img);
                    } else {
                        imgStr.append(img).append(",");
                    }
                }
                if (!StringUtils.isNotBlank(imgStr.toString()))
                    return ServerResponse.createByErrorMessage("商品图片不能为空");
                LOG.info("001----------添加商品主表 start:" + basicsProductDTO.getName());
                String productId = insertBasicsProductData(basicsProductDTO,imgStr,0);
                LOG.info("001----------添加商品主表 end productId:" + productId);

                if(2 == basicsGoods.getType()){
                    //2.2添加人工商品扩展信息
                    LOG.info("002-----添加人工商品扩展信息 start :" + productId);
                   String restr = insertBasicsProductDataWorker(basicsProductDTO,productId);
                    if (StringUtils.isNotBlank(restr))
                        return ServerResponse.createByErrorMessage(restr);
                    LOG.info("002-----添加人工商品扩展信息 end :" + productId);

                }else if(0 == basicsGoods.getType()||1 == basicsGoods.getType()){
                    LOG.info("003------添加材料商品扩展信息 start:" + productId);
                    //2.3添加材料商品扩展信息(材料和包工包料）
                    insertBasicsProductDataMaterial(basicsProductDTO,productId);
                    //添加材料商品的工艺信息
                    LOG.info("003----1---添加材料商品工艺信息:" + productId);
                    String ret = technologyService.insertTechnologyList(obj.getString("technologyList"), "0", 0, productId);
                    if (!ret.equals("1"))  //如果不成功 ，弹出是错误提示
                        return ServerResponse.createByErrorMessage(ret);

                    LOG.info("003------添加材料商品扩展信息 end:" + productId);
                }
                //3.删除对应需要删除的工艺信息
                String deleteTechnologyIds=obj.getString("deleteTechnologyIds");
                String restr = deleteTechnologylist(deleteTechnologyIds);
                if (StringUtils.isNotBlank(restr)) {
                    return ServerResponse.createByErrorMessage(restr);
                }
            }
            return ServerResponse.createBySuccessMessage("保存更新商品成功");
    }

    /**
     * 删除工艺信息
     * @param deleteTechnologyIds
     * @return
     */
    private String deleteTechnologylist(String deleteTechnologyIds){
        if (!CommonUtil.isEmpty(deleteTechnologyIds)) {
            String[] deleteTechnologyIdArr = deleteTechnologyIds.split(",");
            for (String aDeleteTechnologyIdArr : deleteTechnologyIdArr) {
                if (iTechnologyMapper.selectByPrimaryKey(aDeleteTechnologyIdArr) != null) {
                    if (iTechnologyMapper.deleteByPrimaryKey(aDeleteTechnologyIdArr) < 0)
                        return "删除id：" + aDeleteTechnologyIdArr + "失败";
                }
            }
        }
        return "";
    }

    /**
     * 添加商品主表信息
     * @param basicsProductDTO
     * @param imgStr 图处地址
     * @returndataStatus  数据状态，0正常，1删除，2存草稿
     */
    private String insertBasicsProductData(BasicsProductDTO basicsProductDTO,StringBuilder imgStr,int dataStatus){
        DjBasicsProduct product = new DjBasicsProduct();
        String productId = basicsProductDTO.getId();
        product.setName(basicsProductDTO.getName());//product品名称
        product.setCategoryId(basicsProductDTO.getCategoryId());//分类id
        product.setGoodsId(basicsProductDTO.getGoodsId());//goodsid
        String productSn = basicsProductDTO.getProductSn();
        product.setProductSn(productSn);//商品编号
        product.setImage(imgStr.toString());//图片地址
        product.setUnitId(basicsProductDTO.getUnitId());//单位
//                product.setLabelId(obj.getString("labelId"));//标签
        product.setUnitName(basicsProductDTO.getUnitName());//单位
        product.setType(basicsProductDTO.getType()==null?1:basicsProductDTO.getType());//是否禁用0：禁用；1不禁用
        product.setMaket(basicsProductDTO.getMaket()==null?1:basicsProductDTO.getMaket());//是否上架0：不上架；1：上架
        product.setPrice(basicsProductDTO.getPrice());//销售价
        product.setDataStatus(dataStatus);
        if (productId == null || "".equals(productId)) {//没有id则新增
            product.setCreateDate(new Date());
            product.setModifyDate(new Date());
            djBasicsProductMapper.insert(product);
        } else {//修改
            product.setId(productId);
            product.setModifyDate(new Date());
            djBasicsProductMapper.updateByPrimaryKey(product);
        }
        return product.getId();
    }

    /**
     * 添加人工商品扩展信息
     * @param basicsProductDTO
     * @param productId
     * @return
     */
    private String insertBasicsProductDataWorker(BasicsProductDTO basicsProductDTO,String productId){
        DjBasicsProductWorker djBasicsProductWorker = new DjBasicsProductWorker();
        djBasicsProductWorker.setProductId(productId);
        djBasicsProductWorker.setWorkExplain(basicsProductDTO.getWorkExplain());
        djBasicsProductWorker.setWorkerDec(basicsProductDTO.getWorkerDec());
        djBasicsProductWorker.setWorkerStandard(basicsProductDTO.getWorkerStandard());
        djBasicsProductWorker.setWorkerTypeId(basicsProductDTO.getWorkerTypeId());
        djBasicsProductWorker.setLastPrice(basicsProductDTO.getLastPrice());
        djBasicsProductWorker.setLastTime(basicsProductDTO.getLastTime());
        djBasicsProductWorker.setTechnologyIds(basicsProductDTO.getTechnologyIds());
        djBasicsProductWorker.setConsiderations(basicsProductDTO.getConsiderations());
        djBasicsProductWorker.setCalculateContent(basicsProductDTO.getCalculateContent());
        djBasicsProductWorker.setBuildContent(basicsProductDTO.getBuildContent());
        djBasicsProductWorker.setIsAgencyPurchase(basicsProductDTO.getIsAgencyPurchase());
        djBasicsProductWorker.setShowGoods(basicsProductDTO.getShowGoods());
        //根据商品ID查询扩展表的ID
        DjBasicsProductWorker oldBasicsProductWorker = djBasicsProductWorkerMapper.queryProductWorkerByProductId(productId);
        if(oldBasicsProductWorker!=null&&StringUtils.isNotBlank(oldBasicsProductWorker.getId())){
            //更新
            djBasicsProductWorker.setId(oldBasicsProductWorker.getId());
            djBasicsProductWorker.setModifyDate(new Date());
            if (djBasicsProductWorkerMapper.updateByPrimaryKeySelective(djBasicsProductWorker) < 0) {
                return "更新工价商品失败";
            } else {
                //相关联表也更新
                iBudgetWorkerMapper.updateBudgetMaterialByProductId(productId);
                Example example = new Example(DjBasicsProduct.class);
                example.createCriteria().andEqualTo(DjBasicsProduct.ID, productId);
                List<DjBasicsProduct> list = djBasicsProductMapper.selectByExample(example);
                masterMendWorkerAPI.updateMendWorker(JSON.toJSONString(list));
            }
        }else{
            //添加
            djBasicsProductWorker.setCreateDate(new Date());
            djBasicsProductWorker.setModifyDate(new Date());
            if (djBasicsProductWorkerMapper.insert(djBasicsProductWorker) < 0)
                return "新增工价商品失败";
        }
        return "";
    }

    /**
     * 添加材料商品扩展信息
     * @param basicsProductDTO
     * @param productId
     * @return
     */
    private void insertBasicsProductDataMaterial(BasicsProductDTO basicsProductDTO,String productId){
        DjBasicsProductMaterial djBasicsProductMaterial = new DjBasicsProductMaterial();
        djBasicsProductMaterial.setProductId(productId);
        djBasicsProductMaterial.setWeight(basicsProductDTO.getWeight());
        djBasicsProductMaterial.setCost(basicsProductDTO.getCost());
        djBasicsProductMaterial.setProfit(basicsProductDTO.getProfit());
        djBasicsProductMaterial.setConvertQuality(basicsProductDTO.getConvertQuality());
        djBasicsProductMaterial.setConvertUnit(basicsProductDTO.getConvertUnit());
        djBasicsProductMaterial.setIsInflueWarrantyPeriod(basicsProductDTO.getIsInflueWarrantyPeriod());
        djBasicsProductMaterial.setWorkerTypeId(basicsProductDTO.getWorkerTypeId());
        djBasicsProductMaterial.setMaxWarrantyPeriodYear(basicsProductDTO.getMaxWarrantyPeriodYear());
        djBasicsProductMaterial.setMinWarrantyPeriodYear(basicsProductDTO.getMinWarrantyPeriodYear());
        djBasicsProductMaterial.setMarketingName(basicsProductDTO.getMarketingName());
        djBasicsProductMaterial.setCartagePrice(basicsProductDTO.getCartagePrice());
        djBasicsProductMaterial.setDetailImage(basicsProductDTO.getDetailImage());
        djBasicsProductMaterial.setGuaranteedPolicy(basicsProductDTO.getGuaranteedPolicy());
        djBasicsProductMaterial.setRefundPolicy(basicsProductDTO.getRefundPolicy());
        if (!StringUtils.isNoneBlank(basicsProductDTO.getValueNameArr())) {
            djBasicsProductMaterial.setValueNameArr(null);
        } else {
            djBasicsProductMaterial.setValueNameArr(basicsProductDTO.getValueNameArr());
        }
        if (!StringUtils.isNoneBlank(basicsProductDTO.getValueIdArr())) {
            djBasicsProductMaterial.setValueIdArr(null);
        } else {
            djBasicsProductMaterial.setValueIdArr(basicsProductDTO.getValueIdArr());
        }
        if (!StringUtils.isNoneBlank(basicsProductDTO.getAttributeIdArr())) {
            djBasicsProductMaterial.setAttributeIdArr(null);
        } else {
            djBasicsProductMaterial.setAttributeIdArr(basicsProductDTO.getAttributeIdArr());
        }
        //根据商品ID查询扩展表的ID
        DjBasicsProductMaterial oldBasicsProductMaterial = djBasicsProductMaterialMapper.queryProductMaterialByProductId(productId);
        if(oldBasicsProductMaterial!=null&&StringUtils.isNotBlank(oldBasicsProductMaterial.getId())){
            //更新
            djBasicsProductMaterial.setId(oldBasicsProductMaterial.getId());
            djBasicsProductMaterial.setModifyDate(new Date());
            djBasicsProductMaterialMapper.updateByPrimaryKey(djBasicsProductMaterial);

        }else{
            //添加
            djBasicsProductMaterial.setCreateDate(new Date());
            djBasicsProductMaterial.setModifyDate(new Date());
            djBasicsProductMaterialMapper.insert(djBasicsProductMaterial);
        }

    }
    /**
     * 校验需添加商品数据是否正确
     * 1.校验字段是否为空
     * 2.校验材料商品的属性字段
     * 3校验商品是否已存在
     * @param jsonArr
     * @return
     */
    private String checkProductData(JSONArray jsonArr){
        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject obj = jsonArr.getJSONObject(i);
            //JSON对象转换成Java对象
            BasicsProductDTO basicsProductDTO = JSONObject.toJavaObject(obj, BasicsProductDTO.class);
            //1.判断必填字段是否为空
            String checkStr = checkFielsNull(basicsProductDTO);
            if(StringUtils.isNotBlank(checkStr)){
                return checkStr;
            }
            String id = basicsProductDTO.getId();//商品ID
            String name = basicsProductDTO.getName();//商品名称
            String productSn = basicsProductDTO.getProductSn();//商品编码
            String categoryId=basicsProductDTO.getCategoryId();//商品类别Id
            DjBasicsGoods basicsGoods = djBasicsGoodsMapper.selectByPrimaryKey(categoryId);
            if("0".equals(basicsGoods.getType())||"1".equals(basicsGoods.getType())){
                //判断当前添加的属性值是否有相同的已存在的商品（材料商品才有）
                checkStr = checkProductAttr(basicsProductDTO,jsonArr);
                if(StringUtils.isNotBlank(checkStr)){
                    return checkStr;
                }
            }
            //校验商品是否存在
            String ret = checkProduct(name, productSn, id, jsonArr);
            if (!ret.equals("ok")) {
                return ret;
            }
        }
        return "";
    }
    /**
     * 判断校验字段是否可以为空
     * @param basicsProductDTO
     * @return  //
     */
    private String checkFielsNull(BasicsProductDTO basicsProductDTO){
        //判断添加商品时，对应的字段不能为空
        /*if (!StringUtils.isNotBlank(basicsProductDTO.getUnitId()))
           return "单位id不能为空";

        if (!StringUtils.isNotBlank(basicsProductDTO.getUnitName()))
            return "单位名字不能为空";
*/
        if (!StringUtils.isNotBlank(basicsProductDTO.getCategoryId()))
            return "商品分类不能为空";

        if (!StringUtils.isNotBlank(basicsProductDTO.getGoodsId()))
            return "货品id不能为空";

        if (!StringUtils.isNotBlank(basicsProductDTO.getProductSn()))//商品编号
            return "商品编号不能为空";

        if (!StringUtils.isNotBlank(basicsProductDTO.getName()))//商品名字
            return "商品名字不能为空";

      /*  Double convertQuality = basicsProductDTO;//换算量
        LOG.info("insertProduct convertQuality:" + convertQuality);
        if (convertQuality <= 0)
            return "换算量必须大于0";
*/
        return "";
    }

    /**
     * 判断属性值添加是否重复
     * @param basicsProductDTO
     * @param jsonArr
     * @return
     */
    public String checkProductAttr(BasicsProductDTO basicsProductDTO,JSONArray jsonArr){
        if (!StringUtils.isNotBlank(basicsProductDTO.getId())) {//没有id则新增,判断是当前添加的属性值是否重复，是否已存在
            int valueIdArrCount = 0;
            String valueIdArr = basicsProductDTO.getValueIdArr();
            String productSn = basicsProductDTO.getProductSn();
            String id=basicsProductDTO.getId();
            String name=basicsProductDTO.getName();
            //属性值判断
            if (StringUtils.isNoneBlank(valueIdArr)
                    && StringUtils.isNoneBlank(basicsProductDTO.getAttributeIdArr())) {

                List<DjBasicsProduct> pValueList = djBasicsProductMapper.getPListByValueIdArr(valueIdArr);
                if (pValueList.size() > 0) {
                    String ret = checkProduct(name, productSn, id, jsonArr);
                    if (!ret.equals("ok")) {
                        return "属性值已存在,请检查编号:" + productSn;
                    }
                }

                //统计添加时是否存在同属性的
                for (int j = 0; j < jsonArr.size(); j++) {
                    JSONObject objJ = jsonArr.getJSONObject(j);
                    if (valueIdArr.equals(objJ.getString("valueIdArr"))) {
                        valueIdArrCount++;
                        if (valueIdArrCount > 1) {
                            String ret = checkProduct(name, productSn, id, jsonArr);
                            if (!ret.equals("ok")) {
                                return "属性值不能重复,请检查编号:" + productSn;
//                                        return ServerResponse.createByErrorMessage("无品牌无系列属性值不能重复,商品编号“" + objJ.getString("productSn") + "”");
                            }
                        }
                    }
                }
            }
        }
        return "";
    }


    /**
     * 检商商品是否添加重复
     * @param name
     * @param productSn
     * @param id
     * @param jsonArr
     * @return
     */
    public String checkProduct(String name, String productSn, String id, JSONArray jsonArr) {
        List<DjBasicsProduct> nameList = djBasicsProductMapper.queryByName(name);
        List<DjBasicsProduct> productSnList = djBasicsProductMapper.queryByProductSn(productSn);
        if (!StringUtils.isNotBlank(id)) {//没有id则新增
            if (nameList.size() > 0)
                return "名字“" + nameList.get(0).getName() + "”已存在";
            if (productSnList.size() > 0)
                return "编号“:" + productSnList.get(0).getProductSn() + "”已存在";
            int snCount = 0;
            int nameCount = 0;
            for (int j = 0; j < jsonArr.size(); j++) {
                JSONObject objJ = jsonArr.getJSONObject(j);
                if (productSn.equals(objJ.getString("productSn"))) {
                    snCount++;
                    if (snCount > 1)
                        return "编号“" + productSn + "”不能重复";
                }
                if (name.equals(objJ.getString("name"))) {
                    nameCount++;
                    if (nameCount > 1)
                        return "名字“" + name + "”不能重复";
                }
            }
        } else {//修改
            DjBasicsProduct oldProduct = djBasicsProductMapper.selectByPrimaryKey(id);
            if (!oldProduct.getName().equals(name)) {
                if (nameList.size() > 0)
                    return "名字“" + name + "”已存在";
            }
            if (!oldProduct.getProductSn().equals(productSn)) {
                if (productSnList.size() > 0)
                    return "编号“" + productSn + "”已存在";
            }
        }

        return "ok";
    }

    /**
     * 校验商品是否符合添加条件
     * @param basicsProductDTO
     * @param type 0材料，1包工包料，2人工
     * @return
     */
    public String checkSingleProductCommon(BasicsProductDTO basicsProductDTO,int type,JSONArray jsonArr){
        //1.判断必填字段是否为空
        String checkStr = checkFielsNull(basicsProductDTO);
        if(StringUtils.isNotBlank(checkStr)){
            return checkStr;
        }
        String id = basicsProductDTO.getId();//商品ID
        String name = basicsProductDTO.getName();//商品名称
        String productSn = basicsProductDTO.getProductSn();//商品编码
        String categoryId=basicsProductDTO.getCategoryId();//商品类别Id
        DjBasicsGoods basicsGoods = djBasicsGoodsMapper.selectByPrimaryKey(categoryId);
        if(type == 0 || type == 1){
            //判断当前添加的属性值是否有相同的已存在的商品（材料商品才有）
            checkStr = checkProductAttr(basicsProductDTO,jsonArr);
            if(StringUtils.isNotBlank(checkStr)){
                return checkStr;
            }
        }
        //校验商品是否存在
        String ret = checkProduct(name, productSn, id, jsonArr);
        if (!ret.equals("ok")) {
            return ret;
        }
        return "";
    }
    /**
     * 商品信息暂存
     *
     * @param basicsProductDTO
     * @param technologyList
     * @param deleteTechnologyIds
     * @param dataStatus 数据状态，0正常，1暂存
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveProductTemporaryStorage(BasicsProductDTO basicsProductDTO,String technologyList, String  deleteTechnologyIds,int dataStatus){
        if (!StringUtils.isNotBlank(basicsProductDTO.getCategoryId()))
            return ServerResponse.createByErrorMessage("商品分类不能为空");

        if (!StringUtils.isNotBlank(basicsProductDTO.getGoodsId()))
            return ServerResponse.createByErrorMessage("货品id不能为空");
        String goodsId=basicsProductDTO.getGoodsId();//货品ID
        DjBasicsGoods basicsGoods = djBasicsGoodsMapper.selectByPrimaryKey(goodsId);//查询货品表信息，判断是人工还是材料商品新增
        if(dataStatus == 0){
            //添加正式商品前的校验，商品名称和编码不能为空，且不能重复
           String restr = checkSingleProductCommon(basicsProductDTO,basicsGoods.getType(),new JSONArray());
           if(StringUtils.isNotBlank(restr)){
               return ServerResponse.createByErrorMessage(restr);
           }
       }
        //2.1添加商品主表信息
        StringBuilder imgStr = new StringBuilder();
        if(StringUtils.isNotBlank(basicsProductDTO.getImage())){
            String[] imgArr = basicsProductDTO.getImage().split(",");
//                String[] technologyIds = obj.getString("technologyIds").split(",");//工艺节点
            for (int j = 0; j < imgArr.length; j++) {
                String img = imgArr[j];
                if (j == imgArr.length - 1) {
                    imgStr.append(img);
                } else {
                    imgStr.append(img).append(",");
                }
            }
        }
        LOG.info("001----------添加商品主表 start:" +basicsProductDTO.getId()+"-----"+ basicsProductDTO.getName());
        String productId = insertBasicsProductData(basicsProductDTO,imgStr,dataStatus);
        LOG.info("001----------添加商品主表 end productId:" + productId);

        if(2 == basicsGoods.getType()){
            //2.2添加人工商品扩展信息
            LOG.info("002-----添加人工商品扩展信息 start :" + productId);
            String restr = insertBasicsProductDataWorker(basicsProductDTO,productId);
            if (StringUtils.isNotBlank(restr))
                return ServerResponse.createByErrorMessage(restr);
            LOG.info("002-----添加人工商品扩展信息 end :" + productId);

        }else if(0 == basicsGoods.getType()||1 == basicsGoods.getType()){
            LOG.info("003------添加材料商品扩展信息 start:" + productId);
            //2.3添加材料商品扩展信息(材料和包工包料）
            insertBasicsProductDataMaterial(basicsProductDTO,productId);
            //添加材料商品的工艺信息
            LOG.info("003----1---添加材料商品工艺信息:" + productId);
            String ret = technologyService.insertTechnologyList(technologyList, "0", 0, productId);
            if (!ret.equals("1"))  //如果不成功 ，弹出是错误提示
                return ServerResponse.createByErrorMessage(ret);

            LOG.info("003------添加材料商品扩展信息 end:" + productId);
        }
        //3.删除对应需要删除的工艺信息
        String restr = deleteTechnologylist(deleteTechnologyIds);
        if (StringUtils.isNotBlank(restr)) {
            return ServerResponse.createByErrorMessage(restr);
        }
        return ServerResponse.createBySuccess("保存成功",productId);
    }
    /**
     * 查询商品标签
     *
     * @param productId
     * @return
     */
    public ServerResponse queryProductLabels(String productId) {
        DjBasicsProduct djBasicsProduct = djBasicsProductMapper.selectByPrimaryKey(productId);
        List<DjBasicsLabelDTO> djBasicsLabelDTOList = new ArrayList<>();
        Arrays.asList(djBasicsProduct.getLabelId().split(",")).forEach(str -> {
            DjBasicsLabel djBasicsLabel = djBasicsLabelMapper.selectByPrimaryKey(str);
            DjBasicsLabelDTO djBasicsLabelDTO = new DjBasicsLabelDTO();
            djBasicsLabelDTO.setId(djBasicsLabel.getId());
            djBasicsLabelDTO.setName(djBasicsLabel.getName());
            Example example = new Example(DjBasicsLabelValue.class);
            example.createCriteria().andEqualTo(DjBasicsLabelValue.LABEL_ID, str)
                    .andEqualTo(DjBasicsLabelValue.DATA_STATUS, 0);
            djBasicsLabelDTO.setLabelValueList(djBasicsLabelValueMapper.selectByExample(example));
            djBasicsLabelDTOList.add(djBasicsLabelDTO);
        });
        if (djBasicsLabelDTOList.size() <= 0)
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        return ServerResponse.createBySuccess("查询成功", djBasicsLabelDTOList);
    }


    /**
     * 商品打标签
     *
     * @return
     */
    public ServerResponse addLabelsValue(String jsonStr) {
        try {
            JSONObject villageObj = JSONObject.parseObject(jsonStr);
            String productId = villageObj.getString("productId");//商品id
            //遍历标签值对象 数组  ， 一个商品 对应 多个标签
            String productLabelValList = villageObj.getString("productLabelValList");
            JSONArray productLabelValArr = JSONArray.parseArray(productLabelValList);
            for (int i = 0; i < productLabelValArr.size(); i++) {//遍历户型
                JSONObject obj = productLabelValArr.getJSONObject(i);
                String productLabelValId = obj.getString("id");//商品标签值id
                String labelId = obj.getString("labelId");//标签id
                String labelValId = obj.getString("labelValId");//标签值id
                DjBasicsProductLabelVal djBasicsProductLabelVal;
                if (CommonUtil.isEmpty(productLabelValId)) {//没有id则新增
                    djBasicsProductLabelVal = new DjBasicsProductLabelVal();
                    djBasicsProductLabelVal.setProductId(productId);
                    djBasicsProductLabelVal.setDataStatus(0);
                    djBasicsProductLabelVal.setLabelId(labelId);
                    djBasicsProductLabelVal.setLabelValId(labelValId);
                    djBasicsProductLabelValMapper.insert(djBasicsProductLabelVal);
                } else {
                    djBasicsProductLabelVal = djBasicsProductLabelValMapper.selectByPrimaryKey(productLabelValId);
                    if (djBasicsProductLabelVal.getLabelId().equals(labelId) && djBasicsProductLabelVal.getLabelValId().equals(labelValId)) {
                        return ServerResponse.createByErrorMessage("商品标签值已存在");
                    }
                    djBasicsProductLabelVal.setLabelId(labelId);
                    djBasicsProductLabelVal.setLabelValId(labelValId);
                    djBasicsProductLabelValMapper.updateByPrimaryKeySelective(djBasicsProductLabelVal);
                }
            }
            //要删除商品标签值id数组，逗号分隔
            String[] deleteproductLabelValIds = villageObj.getString("deleteproductLabelValIds").split(",");
            for (String deleteproductLabelValId : deleteproductLabelValIds) {
                if (djBasicsProductLabelValMapper.selectByPrimaryKey(deleteproductLabelValId) != null) {
                    if (djBasicsProductLabelValMapper.deleteByPrimaryKey(deleteproductLabelValId) < 0)
                        return ServerResponse.createByErrorMessage("删除id：" + deleteproductLabelValId + "失败");
                }
            }
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return ServerResponse.createBySuccessMessage("操作成功");


    }

    /**
     * 查询单个商品
     * @param request
     * @param id
     * @return
     */
    public DjBasicsProduct queryProductDataByID(HttpServletRequest request, String id)
    {
        return djBasicsProductMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据productid删除product对象
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse deleteBasicsProductById(String id) {
            DjBasicsProduct djBasicsProduct = new DjBasicsProduct();
            djBasicsProduct.setId(id);
            djBasicsProductMapper.deleteByPrimaryKey(djBasicsProduct);
            //删除材料商品扩展表
            Example example = new Example(DjBasicsProductMaterial.class);
            example.createCriteria().andEqualTo("productId", id);
            djBasicsProductMaterialMapper.deleteByExample(example);
            //删除人工商品扩展表
            example = new Example(DjBasicsProductWorker.class);
            example.createCriteria().andEqualTo("productId", id);
            djBasicsProductWorkerMapper.deleteByExample(example);
            return ServerResponse.createBySuccessMessage("删除成功");
    }



    /**
     * 模糊查询goods及下属product
     *
     * @param pageDTO
     * @param categoryId
     * @param name 分类名称
     * @param type 是否禁用  0：禁用；1不禁用 ;  -1全部默认
     * @return
     */
    public ServerResponse queryGoodsListByCategoryLikeName(PageDTO pageDTO, String categoryId, String name, Integer type, String categoryName) {
        try {
            LOG.info("tqueryGoodsListByCategoryLikeName type :" + type);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjBasicsGoods> djBasicsGoods = djBasicsGoodsMapper.queryGoodsListByCategoryLikeName(categoryId, name);
            PageInfo pageResult = new PageInfo(djBasicsGoods);
            List<ActuarialGoodsDTO> actuarialGoodsDTOS=new ArrayList<>();
            List<Map<String, Object>> gMapList = new ArrayList<>();
            ActuarialGoodsDTO actuarialGoodsDTO=new ActuarialGoodsDTO();
            actuarialGoodsDTO.setCategoryName(categoryName);
            djBasicsGoods.forEach(goods ->{
                Map<String, Object> gMap = BeanUtils.beanToMap(goods);
                List<Map<String, Object>> mapList = new ArrayList<>();
                if (2 != goods.getBuy()) {
                    List<DjBasicsProduct> djBasicsProducts = djBasicsProductMapper.queryByGoodsId(goods.getId());
                    for (DjBasicsProduct p : djBasicsProducts) {
                        //type表示： 是否禁用  0：禁用；1不禁用 ;  -1全部默认
                        if (type!=null&& !type.equals(p.getType()) && -1 != type) //不等于 type 的不返回给前端
                            continue;
                        Map<String, Object> map = BeanUtils.beanToMap(p);
                        mapList.add(map);
                    }
                    gMap.put("productList", mapList);
                    gMapList.add(gMap);
                }
            });
            actuarialGoodsDTO.setGMapList(gMapList);
            actuarialGoodsDTOS.add(actuarialGoodsDTO);
            pageResult.setList(actuarialGoodsDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }





    /**
     * 查询所有的商品
     * @param pageDTO
     * @param categoryId
     * @return
     */
    public ServerResponse<PageInfo> queryProduct(PageDTO pageDTO, String categoryId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<DjBasicsProduct> productList = djBasicsProductMapper.queryProductByCategoryId(categoryId);
            PageInfo pageResult = new PageInfo(productList);
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (DjBasicsProduct p : productList) {
                if (p.getImage() == null) {
                    continue;
                }
                String[] imgArr = p.getImage().split(",");
                StringBuilder imgStr = new StringBuilder();
                StringBuilder imgUrlStr = new StringBuilder();
                StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
                p.setImage(imgStr.toString());
                Map<String, Object> map = BeanUtils.beanToMap(p);
                map.put("imageUrl", imgUrlStr.toString());
                //查询商品对应的标签及标签值列表
                /*if (!StringUtils.isNotBlank(p.getLabelId())) {
                    map.put("labelId", "");
                    map.put("labelName", "");
                } else {
                    map.put("labelId", p.getLabelId());
                    Label label = djBasicsLabel.selectByPrimaryKey(p.getLabelId());
                    if (label.getName() != null)
                        map.put("labelName", label.getName());
                }*/
                mapList.add(map);
            }
            pageResult.setList(mapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询所有单位列表
     * @return
     */
    public ServerResponse queryUnit() {
        try {
            List<Unit> unitList = iUnitMapper.getUnit();
            return ServerResponse.createBySuccess("查询成功", unitList);
        } catch (Exception e) {
            LOG.error("查询失败：",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 根据productid查询product对象
     *
     * @param id
     * @return
     */
    public ServerResponse getProductById(String id) {
        try {
            DjBasicsProduct djBasicsProduct =djBasicsProductMapper.selectByPrimaryKey(id);
            Map<String,Object> map = getProductDetailByProductId(djBasicsProduct);
            return ServerResponse.createBySuccess("查询成功", map);
        } catch (Exception e) {
            LOG.error("查询失败：",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询货品下暂存的商品信息
     *
     * @param goodsId
     * @return
     */
    public ServerResponse getTemporaryStorageProductByGoodsId(String goodsId) {
        try {
            DjBasicsProduct djBasicsProduct =djBasicsProductMapper.queryTemporaryStorage(goodsId,"2");
            Map<String,Object> map = new HashMap<String,Object>();
            if(djBasicsProduct!=null&&StringUtils.isNotBlank(djBasicsProduct.getId())){
               map = getProductDetailByProductId(djBasicsProduct);
            }
            return ServerResponse.createBySuccess("查询成功", map);
        } catch (Exception e) {
            LOG.error("查询失败：",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 根据商品ID查询对应的商品详情信息
     * 1.材料或人工扩展信息
     * 2.单位信息
     * 3.工艺信息
     * 4.属性信息
     * @param djBasicsProduct
     * @return
     */
    private Map<String, Object> getProductDetailByProductId(DjBasicsProduct djBasicsProduct){
        String id=djBasicsProduct.getId();
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        String[] imgArr = djBasicsProduct.getImage().split(",");
        StringBuilder imgStr = new StringBuilder();
        StringBuilder imgUrlStr = new StringBuilder();
        StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
        djBasicsProduct.setImage(imgStr.toString());
        Map<String, Object> map = BeanUtils.beanToMap(djBasicsProduct);
        map.put("imageUrl", imgUrlStr.toString());
        //单位列表
        List<Unit> linkUnitList = getlinkUnitListByGoodsUnitId(djBasicsProduct.getGoodsId());
        //商品工艺信息
        List<Map<String, Object>> tTechnologymMapList = getTechnologymMapList(id,address);
        //根据商品查询材料商品扩展信息
        DjBasicsProductMaterial djBasicsProductMaterial=djBasicsProductMaterialMapper.queryProductMaterialByProductId(id);
        //根据商品查询人工商品扩展信息
        DjBasicsProductWorker djBasicsProductWorker=djBasicsProductWorkerMapper.queryProductWorkerByProductId(id);
        //材料商品信息
        if(djBasicsProductMaterial!=null&&StringUtils.isNotBlank(djBasicsProductMaterial.getId())){//添加材料商品返加
            if(StringUtils.isNotBlank(djBasicsProductMaterial.getDetailImage())){
                imgArr = djBasicsProductMaterial.getDetailImage().split(",");
                imgStr = new StringBuilder();
                imgUrlStr = new StringBuilder();
                StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
                djBasicsProductMaterial.setDetailImage(imgStr.toString());
            }
            Map<String, Object> djBasicsProductMaterialMap = BeanUtils.beanToMap(djBasicsProductMaterial);
            map.putAll(djBasicsProductMaterialMap);
        }
        //人工商品信息
        if(djBasicsProductWorker!=null&&StringUtils.isNotBlank(djBasicsProductWorker.getId())){//添加人工商品返回
            Map<String, Object> djBasicsProductWorkerMap = BeanUtils.beanToMap(djBasicsProductWorker);
            map.putAll(djBasicsProductWorkerMap);
        }
        //商品属性值信息
        String strNewValueNameArr = "";
        if (djBasicsProductMaterial!=null&&StringUtils.isNotBlank(djBasicsProductMaterial.getValueIdArr())) {
            strNewValueNameArr = getNewValueNameArr(djBasicsProductMaterial.getValueIdArr());
        }
        map.put("newValueNameArr", strNewValueNameArr);
        map.put("tTechnologymMapList", tTechnologymMapList);
        map.put("unitList",linkUnitList);
        map.put("imageUrl",imgUrlStr.toString());
        return map;
    }

    /**
     * 获取对应的属性值信息
     * @param valueIdArr
     * @return
     */
    private String getNewValueNameArr(String valueIdArr){
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
     * 查询关联的换算单位
     * @param goodsId
     * @return
     */
    private List<Unit> getlinkUnitListByGoodsUnitId(String goodsId){
        List<Unit> linkUnitList = new ArrayList<>();
        BasicsGoods oldGoods = iBasicsGoodsMapper.selectByPrimaryKey(goodsId);
        if(oldGoods!=null&&StringUtils.isNotBlank(oldGoods.getUnitId())){
            Unit unit = iUnitMapper.selectByPrimaryKey(oldGoods.getUnitId());
//            linkUnitList.add(unit);
            if (unit!=null&&unit.getLinkUnitIdArr() != null) {
                String[] linkUnitIdArr = unit.getLinkUnitIdArr().split(",");
                for (String linkUnitId : linkUnitIdArr) {
                    Unit linkUnit = iUnitMapper.selectByPrimaryKey(linkUnitId);
                    linkUnitList.add(linkUnit);
                }
            }
        }


        return linkUnitList;
    }

    /**
     * 查询对应工艺信息
     * @param productId
     * @return
     */
    private List<Map<String, Object>> getTechnologymMapList(String productId,String address){
        List<Technology> pTechnologyList = iTechnologyMapper.queryTechnologyByWgId(productId);
        List<Map<String, Object>> tTechnologymMapList = new ArrayList<>();
        for (Technology t : pTechnologyList) {
            if (t.getImage() == null) {
                continue;
            }
            String[] imgArr = t.getImage().split(",");
            StringBuilder imgStr = new StringBuilder();
            StringBuilder imgUrlStr = new StringBuilder();
            StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
            t.setImage(imgUrlStr.toString());
            Map<String, Object> techMap = BeanUtils.beanToMap(t);
            techMap.put("imageUrl", imgStr.toString());
            techMap.put("sampleImageUrl", address + t.getSampleImage());
            tTechnologymMapList.add(techMap);
        }
        return tTechnologymMapList;
    }


    //根据类别Id查到所有所属货品goods
    public ServerResponse getAllGoodsByCategoryId(String categoryId) {
        try {
            List<BasicsGoods> mapList = iBasicsGoodsMapper.queryByCategoryId(categoryId);
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 根据货品ID查询商品
     * @param goodsId
     * @return
     */
    public ServerResponse getAllProductByGoodsId(String goodsId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<DjBasicsProduct> pList = djBasicsProductMapper.queryByGoodsId(goodsId);
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (DjBasicsProduct p : pList) {
                if (p.getImage() == null) {
                    continue;
                }
                Map<String, Object> map = getProductDetailByProductId(p);
                mapList.add(map);
            }
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


}
