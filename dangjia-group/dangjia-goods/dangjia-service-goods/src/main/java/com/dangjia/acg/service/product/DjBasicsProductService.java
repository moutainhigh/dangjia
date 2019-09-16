package com.dangjia.acg.service.product;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.app.repair.MasterMendWorkerAPI;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.product.AppBasicsProductDTO;
import com.dangjia.acg.dto.product.BasicsGoodsDTO;
import com.dangjia.acg.dto.product.BasicsProductDTO;
import com.dangjia.acg.dto.product.DjBasicsLabelDTO;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.ITechnologyMapper;
import com.dangjia.acg.mapper.product.*;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.dangjia.acg.modle.product.*;
import com.dangjia.acg.service.basics.TechnologyService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * 保存货品信息
     * <p>Title: saveBasicsGoods</p>
     * <p>Description: </p>
     *
     * @return
     */
    public ServerResponse saveBasicsGoods(BasicsGoodsDTO basicsGoodsDTO) {
        try {
            String name = basicsGoodsDTO.getName();
            String unitId = basicsGoodsDTO.getUnitId();
            String categoryId = basicsGoodsDTO.getCategoryId();
            int type = basicsGoodsDTO.getType();
            if (!StringUtils.isNotBlank(name))
                return ServerResponse.createByErrorMessage("名字不能为空");

            List<BasicsGoods> goodsList = iBasicsGoodsMapper.queryByName(name);
            if (goodsList.size() > 0)
                return ServerResponse.createByErrorMessage("名字不能重复");

            if (!StringUtils.isNotBlank(unitId))
                return ServerResponse.createByErrorMessage("单位id不能为空");

            if (!StringUtils.isNotBlank(categoryId))
                return ServerResponse.createByErrorMessage("分类不能为空");

            if (type < -1)
                return ServerResponse.createByErrorMessage("性质不能为空");

            BasicsGoods goods = getBasicsGoods(basicsGoodsDTO);
            iBasicsGoodsMapper.insert(goods);
            return ServerResponse.createBySuccess("新增成功", goods.getId());
        } catch (Exception e) {
            LOG.error("新增货品失败：",e);
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    /**
     * 对象转换
     *
     * @return
     */
    private BasicsGoods getBasicsGoods(BasicsGoodsDTO basicsGoodsDTO) {
        BasicsGoods goods = new BasicsGoods();
        goods.setName(basicsGoodsDTO.getName());
        goods.setOtherName(basicsGoodsDTO.getOtherName());//别名
        goods.setCategoryId(basicsGoodsDTO.getCategoryId());//分类
        goods.setBuy(basicsGoodsDTO.getBuy());//购买性质
        goods.setSales(basicsGoodsDTO.getSales());//退货性质
        goods.setUnitId(basicsGoodsDTO.getUnitId());//单位
        goods.setType(basicsGoodsDTO.getType());//goods性质
        goods.setCreateDate(new Date());
        goods.setModifyDate(new Date());
        goods.setIsInflueDecorationProgress(basicsGoodsDTO.getIsInflueDecorationProgress());
        goods.setIrreversibleReasons(basicsGoodsDTO.getIrreversibleReasons());
        goods.setIstop(basicsGoodsDTO.getIstop());
        goods.setBrandId(basicsGoodsDTO.getBrandId());
        goods.setIsElevatorFee(basicsGoodsDTO.getIsElevatorFee());
        goods.setIndicativePrice(basicsGoodsDTO.getIndicativePrice());
        goods.setLabelIds(basicsGoodsDTO.getLabelIds());
        return goods;
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
    public ServerResponse insertProduct(String productArr) {

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
            return ServerResponse.createBySuccessMessage("新增成功");
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
     * 商品信息暂存
     *
     * @param basicsProductDTO
     * @param technologyList
     * @param deleteTechnologyIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveProductTemporaryStorage(BasicsProductDTO basicsProductDTO,String technologyList, String  deleteTechnologyIds){
        if (!StringUtils.isNotBlank(basicsProductDTO.getCategoryId()))
            return ServerResponse.createByErrorMessage("商品分类不能为空");

        if (!StringUtils.isNotBlank(basicsProductDTO.getGoodsId()))
            return ServerResponse.createByErrorMessage("货品id不能为空");
        String goodsId=basicsProductDTO.getGoodsId();//货品ID
        DjBasicsGoods basicsGoods = djBasicsGoodsMapper.selectByPrimaryKey(goodsId);//查询货品表信息，判断是人工还是材料商品新增
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
        LOG.info("001----------添加商品主表 start:" + basicsProductDTO.getName());
        String productId = insertBasicsProductData(basicsProductDTO,imgStr,2);
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
        return ServerResponse.createBySuccessMessage("保存成功");
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

}
