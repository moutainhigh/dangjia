package com.dangjia.acg.service.product;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.product.BasicsGoodsDTO;
import com.dangjia.acg.mapper.basics.IGoodsSeriesMapper;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dto.product.DjBasicsLabelDTO;
import com.dangjia.acg.mapper.product.DjBasicsLabelMapper;
import com.dangjia.acg.mapper.product.DjBasicsLabelValueMapper;
import com.dangjia.acg.mapper.product.DjBasicsProductLabelValMapper;
import com.dangjia.acg.mapper.product.DjBasicsProductMapper;
import com.dangjia.acg.mapper.product.IBasicsGoodsMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsLabel;
import com.dangjia.acg.modle.product.DjBasicsLabelValue;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import com.dangjia.acg.service.basics.ProductService;
import org.apache.commons.lang3.StringUtils;
import com.dangjia.acg.modle.product.DjBasicsProductLabelVal;
import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import com.dangjia.acg.modle.store.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    private IGoodsSeriesMapper iGoodsSeriesMapper;

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
            e.printStackTrace();
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
    public ServerResponse insertProduct(String productArr) {
        try {/*
            JSONArray jsonArr = JSONArray.parseArray(productArr);
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);

                String unitId = obj.getString("unitId");//单位
                if (!StringUtils.isNotBlank(unitId))
                    return ServerResponse.createByErrorMessage("单位id不能为空");

                String unitName = obj.getString("unitName");//单位
                if (!StringUtils.isNotBlank(unitName))
                    return ServerResponse.createByErrorMessage("单位名字不能为空");

                String categoryId = obj.getString("categoryId");//分类id
                if (!StringUtils.isNotBlank(categoryId))
                    return ServerResponse.createByErrorMessage("商品分类不能为空");

                String goodsId = obj.getString("goodsId");//goodsid
                if (!StringUtils.isNotBlank(goodsId))
                    return ServerResponse.createByErrorMessage("商品id不能为空");

                String id = obj.getString("id");//id
                String productSn = obj.getString("productSn");//商品编号
                if (!StringUtils.isNotBlank(productSn))
                    return ServerResponse.createByErrorMessage("商品编号不能为空");

                String name = obj.getString("name");//商品名字
                if (!StringUtils.isNotBlank(name))
                    return ServerResponse.createByErrorMessage("商品名字不能为空");

                Double convertQuality = obj.getDouble("convertQuality");//换算量
                LOG.info("insertProduct convertQuality:" + convertQuality);
                if (convertQuality <= 0)
                    return ServerResponse.createByErrorMessage("换算量必须大于0");

                String brandId = obj.getString("brandId");
                String brandSeriesId = obj.getString("brandSeriesId");
                String valueNameArr = obj.getString("valueNameArr");
                String valueIdArr = obj.getString("valueIdArr");
                String attributeIdArr = obj.getString("attributeIdArr");

                if (!StringUtils.isNotBlank(id)) {//没有id则新增
                    int brandSeriesIdCount = 0;
                    //同品牌同系列无属性值


                }

                String ret = checkProduct(name, productSn, id, jsonArr);
                if (!ret.equals("ok")) {
                    return ServerResponse.createByErrorMessage(ret);
                }
            }


            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                Product product = new Product();
                String productId = obj.getString("id");
                product.setName(obj.getString("name"));//product品名称
                product.setCategoryId(obj.getString("categoryId"));//分类id
                product.setGoodsId(obj.getString("goodsId"));//goodsid
                String productSn = obj.getString("productSn");
                if (!StringUtils.isNotBlank(productSn))
                    return ServerResponse.createByErrorMessage("商品编号不能为空");
                product.setProductSn(productSn);//商品编号
                String[] imgArr = obj.getString("image").split(",");
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
                product.setImage(imgStr.toString());//图片地址
                product.setUnitId(obj.getString("unitId"));//单位
//                product.setLabelId(obj.getString("labelId"));//标签
                product.setUnitName(obj.getString("unitName"));//单位
                product.setWeight(obj.getDouble("weight"));//重量
                product.setConvertQuality(obj.getDouble("convertQuality"));//换算量
                product.setConvertUnit(obj.getString("convertUnit"));//换算单位
                product.setType(obj.getInteger("type"));//是否禁用0：禁用；1不禁用
                product.setMaket(obj.getInteger("maket"));//是否上架0：不上架；1：上架
                product.setCost(0.00);//平均成本价
                product.setPrice(obj.getDouble("price"));//销售价
                product.setProfit(obj.getDouble("profit"));//利润率

                if (!StringUtils.isNoneBlank(obj.getString("brandId"))) {
                    product.setBrandId(null);
                } else {
                    product.setBrandId(obj.getString("brandId"));
                }

                if (!StringUtils.isNoneBlank(obj.getString("brandSeriesId"))) {
                    product.setBrandSeriesId(null);
                } else {
                    product.setBrandSeriesId(obj.getString("brandSeriesId"));
                }

//                product.setBrandId(obj.getString("brandId"));//品牌id
//                String brandSeriesId = obj.getString("brandSeriesId");
//                if (!StringUtils.isNotBlank(brandSeriesId))
//                    return ServerResponse.createByErrorMessage("品牌系列id不能为空");
//                product.setBrandSeriesId(brandSeriesId);//品牌系列id


                if (!StringUtils.isNoneBlank(obj.getString("valueNameArr"))) {
                    product.setValueNameArr(null);
                } else {
                    product.setValueNameArr(obj.getString("valueNameArr"));
                }

                if (!StringUtils.isNoneBlank(obj.getString("valueIdArr"))) {
                    product.setValueIdArr(null);
                } else {
                    product.setValueIdArr(obj.getString("valueIdArr"));
                }

                if (!StringUtils.isNoneBlank(obj.getString("attributeIdArr"))) {
                    product.setAttributeIdArr(null);
                } else {
                    product.setAttributeIdArr(obj.getString("attributeIdArr"));
                }

                if (productId == null || "".equals(productId)) {//没有id则新增
                    product.setCreateDate(new Date());
                    product.setModifyDate(new Date());
                    djBasicsProductMapper.insert(product);
                } else {//修改
                    product.setId(productId);
                    product.setModifyDate(new Date());
                    djBasicsProductMapper.updateByPrimaryKey(product);
                }

                LOG.info("insertProduct productId:" + product.getId());
                String ret = technologyService.insertTechnologyList(obj.getString("technologyList"), "0", 0, product.getId());
                if (!ret.equals("1"))  //如果不成功 ，弹出是错误提示
                    return ServerResponse.createByErrorMessage(ret);

                String[] deleteTechnologyIdArr = obj.getString("deleteTechnologyIds").split(",");//选中的属性id字符串
                for (String aDeleteTechnologyIdArr : deleteTechnologyIdArr) {
                    if (iTechnologyMapper.selectByPrimaryKey(aDeleteTechnologyIdArr) != null) {
                        if (iTechnologyMapper.deleteByPrimaryKey(aDeleteTechnologyIdArr) < 0)
                            return ServerResponse.createByErrorMessage("删除id：" + aDeleteTechnologyIdArr + "失败");
                    }
                }
//                iTechnologyMapper.deleteWokerTechnologyByWgId(product.getId());
//                for (String id : technologyIds) {
//                    if (StringUtils.isNotBlank(id)) {
//                        WorkerTechnology wt = new WorkerTechnology();
//                        wt.setWorkerGoodsId(product.getId());
//                        wt.setTechnologyId(id);
//                        iTechnologyMapper.insertWokerTechnology(wt);// 需要将工艺替换
//                    }
//                }
            }*/
            return ServerResponse.createBySuccessMessage("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
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

        return "ok";
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
}
