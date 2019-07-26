package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.basics.ProductDTO;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.basics.*;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Label;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @类 名： ProductServiceImpl
 * @功能描述： goodsservice实现类
 * @作者信息： zmj
 * @创建时间： 2018-9-10下午2:33:37
 */
@Service
public class ProductService {

    @Autowired
    private IGoodsMapper iGoodsMapper;
    @Autowired
    private IProductMapper iProductMapper;
    @Autowired
    private IUnitMapper iUnitMapper;
    @Autowired
    private ILabelMapper iLabelMapper;
    @Autowired
    private IBrandMapper iBrandMapper;
    @Autowired
    private IBrandSeriesMapper iBrandSeriesMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private ITechnologyMapper iTechnologyMapper;
    @Autowired
    private IGoodsMapper goodsMapper;
    @Autowired
    private TechnologyService technologyService;
    @Autowired
    private IGroupLinkMapper iGroupLinkMapper;
    @Autowired
    private IBudgetMaterialMapper iBudgetMaterialMapper;
    @Autowired
    private ProductService productService;

    private static Logger LOG = LoggerFactory.getLogger(ProductService.class);

    //查询product
    public ServerResponse<PageInfo> queryProduct(PageDTO pageDTO, String categoryId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<Product> productList = iProductMapper.query(categoryId);
            PageInfo pageResult = new PageInfo(productList);
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (Product p : productList) {
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
                if (!StringUtils.isNotBlank(p.getLabelId())) {
                    map.put("labelId", "");
                    map.put("labelName", "");
                } else {
                    map.put("labelId", p.getLabelId());
                    Label label = iLabelMapper.selectByPrimaryKey(p.getLabelId());
                    if (label.getName() != null)
                        map.put("labelName", label.getName());
                }
                mapList.add(map);
            }
            pageResult.setList(mapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //查询单位
    public ServerResponse queryUnit() {
        try {
            List<Unit> unitList = iUnitMapper.getUnit();
            return ServerResponse.createBySuccess("查询成功", unitList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //查询品牌
    public ServerResponse queryBrand() {
        try {
            List<Brand> brandList = iBrandMapper.getBrands();
            return ServerResponse.createBySuccess("查询成功", brandList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 根据品牌id查询品牌系列
     *
     * @throws
     * @Title: queryBrandSeries
     * @Description: TODO
     * @param: @return
     * @return: JsonResult
     */
    public ServerResponse queryBrandSeries(String brandId) {
        try {
            List<BrandSeries> brandList = iBrandSeriesMapper.queryBrandSeries(brandId);
            return ServerResponse.createBySuccess("查询成功", brandList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 保存product
     * <p>Title: insertProduct</p>
     * <p>Description: </p>
     *
     * @param productArr
     * @return
     */
    public ServerResponse insertProduct(String productArr) {
        try {
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
                    if (!StringUtils.isNoneBlank(valueIdArr)
                            && !StringUtils.isNoneBlank(attributeIdArr)
                            && StringUtils.isNoneBlank(brandId)
                            && StringUtils.isNoneBlank(brandSeriesId)) {

                        List<Product> pValueList = iProductMapper.getPListByBrandSeriesIdAndNullValueId(brandId, brandSeriesId);
                        if (pValueList.size() > 0) {
                            String ret = checkProduct(name, productSn, id, jsonArr);
                            if (!ret.equals("ok")) {
                                return ServerResponse.createByErrorMessage("同品牌同系列无属性值的商品已存在,请检查编号:" + productSn);
                            }
                        }

                        //统计 没有品牌和系列时，同属性的
                        for (int j = 0; j < jsonArr.size(); j++) {
                            JSONObject objJ = jsonArr.getJSONObject(j);
                            if (brandId.equals(objJ.getString("brandId"))
                                    && brandSeriesId.equals(objJ.getString("brandSeriesId"))) {
                                brandSeriesIdCount++;
                                if (brandSeriesIdCount > 1) {
                                    String ret = checkProduct(name, productSn, id, jsonArr);
                                    if (!ret.equals("ok")) {
                                        return ServerResponse.createByErrorMessage("同品牌同系列无属性值不能重复,请检查编号:" + productSn);
                                    }
                                }
                            }
                        }
                    }

                    int valueIdArrCount = 0;
                    //无品牌无系列同属性值
                    if (StringUtils.isNoneBlank(valueIdArr)
                            && StringUtils.isNoneBlank(attributeIdArr)
                            && !StringUtils.isNoneBlank(brandId)
                            && !StringUtils.isNoneBlank(brandSeriesId)) {

                        List<Product> pValueList = iProductMapper.getPListByValueIdArrByNullBrandId(valueIdArr);
                        if (pValueList.size() > 0) {
                            String ret = checkProduct(name, productSn, id, jsonArr);
                            if (!ret.equals("ok")) {
                                return ServerResponse.createByErrorMessage("无品牌无系列属性值已存在,请检查编号:" + productSn);
                            }
                        }

                        //统计 没有品牌和系列时，同属性的
                        for (int j = 0; j < jsonArr.size(); j++) {
                            JSONObject objJ = jsonArr.getJSONObject(j);
                            if (valueIdArr.equals(objJ.getString("valueIdArr"))) {
                                valueIdArrCount++;
                                if (valueIdArrCount > 1) {
                                    String ret = checkProduct(name, productSn, id, jsonArr);
                                    if (!ret.equals("ok")) {
                                        return ServerResponse.createByErrorMessage("无品牌无系列属性值不能重复,请检查编号:" + productSn);
//                                        return ServerResponse.createByErrorMessage("无品牌无系列属性值不能重复,商品编号“" + objJ.getString("productSn") + "”");
                                    }
                                }
                            }
                        }
                    }

                    int valueCount = 0;
                    //同品牌同系列同属性值
                    if (StringUtils.isNoneBlank(attributeIdArr)
                            && StringUtils.isNoneBlank(valueIdArr)
                            && StringUtils.isNoneBlank(brandId)
                            && StringUtils.isNoneBlank(brandSeriesId)) {
                        List<Product> pValueList = iProductMapper.getPListByBrandSeriesId(brandId, brandSeriesId, valueIdArr);
                        if (pValueList.size() > 0) {
                            String ret = checkProduct(name, productSn, id, jsonArr);
                            if (!ret.equals("ok")) {
                                return ServerResponse.createByErrorMessage("同品牌同系列同属性值的商品已存在,请检查编号:" + productSn);
//                                return ServerResponse.createByErrorMessage("同品牌同系列同属性值的商品已存在");
                            }
                        }

                        //统计 同一个品牌，同系列，同属性的
                        for (int j = 0; j < jsonArr.size(); j++) {
                            JSONObject objJ = jsonArr.getJSONObject(j);
                            if (brandId.equals(objJ.getString("brandId"))
                                    && brandSeriesId.equals(objJ.getString("brandSeriesId"))
                                    && attributeIdArr.equals(objJ.getString("attributeIdArr"))
                                    && valueIdArr.equals(objJ.getString("valueIdArr"))) {
                                valueCount++;
                                if (valueCount > 1) {
                                    String ret = checkProduct(name, productSn, id, jsonArr);
                                    if (!ret.equals("ok")) {
                                        return ServerResponse.createByErrorMessage("同品牌同系列的属性值不能重复,请检查编号:" + productSn);
//                                        return ServerResponse.createByErrorMessage("同品牌同系列的属性值不能重复");
                                    }
                                }
                            }
                        }
                    }

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
                    iProductMapper.insert(product);
                } else {//修改
                    product.setId(productId);
                    product.setModifyDate(new Date());
                    iProductMapper.updateByPrimaryKey(product);
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
            }
            return ServerResponse.createBySuccessMessage("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    public String checkProduct(String name, String productSn, String id, JSONArray jsonArr) {
        List<Product> nameList = iProductMapper.queryByName(name);
        List<Product> productSnList = iProductMapper.queryByProductSn(productSn);
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
            Product oldProduct = iProductMapper.selectByPrimaryKey(id);
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
     * 根据productid查询product对象
     *
     * @param id
     * @return
     */
    public ServerResponse getProductById(String id) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            Product product =iProductMapper.selectByPrimaryKey(id);
            Goods oldGoods = iGoodsMapper.selectByPrimaryKey(product.getGoodsId());
            String[] imgArr = product.getImage().split(",");
            StringBuilder imgStr = new StringBuilder();
            StringBuilder imgUrlStr = new StringBuilder();
            StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
            product.setImage(imgStr.toString());
            Map<String, Object> map = BeanUtils.beanToMap(product);
            List<Unit> linkUnitList = new ArrayList<>();
            Unit unit = iUnitMapper.selectByPrimaryKey(oldGoods.getUnitId());
//            linkUnitList.add(unit);
            if (unit.getLinkUnitIdArr() != null) {
                String[] linkUnitIdArr = unit.getLinkUnitIdArr().split(",");
                for (String linkUnitId : linkUnitIdArr) {
                    Unit linkUnit = iUnitMapper.selectByPrimaryKey(linkUnitId);
                    linkUnitList.add(linkUnit);
                }
            }
            map.put("unitList",linkUnitList);
            map.put("imageUrl",imgUrlStr.toString());
            return ServerResponse.createBySuccess("查询成功", map);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 根据productid删除product对象
     *
     * @param id
     * @return
     */
    public ServerResponse deleteProductById(String id) {
        try {
            Product product = new Product();
            product.setId(id);
            iProductMapper.deleteByPrimaryKey(product);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }

    /**
     * 根据product更新名称
     *
     * @param product
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateProduct(Product product) {
        try {
            if(!CommonUtil.isEmpty(product.getId())){
                Product product1 = iProductMapper.selectByPrimaryKey(product.getId());
                if(product1==null){
                    return ServerResponse.createBySuccessMessage("更新失败！ 该商品不存在！");
                }
                iProductMapper.updateByPrimaryKeySelective(product);
                return ServerResponse.createBySuccessMessage("更新成功");
            }
        } catch (Exception e) {
            throw new BaseException(ServerCode.WRONG_PARAM, "更新异常");
        }
        return ServerResponse.createBySuccessMessage("更新成功");
    }
    /**
     * 根据productid更新名称
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateProductById(String id, String name) {
        try {
            Product product1 = iProductMapper.selectByPrimaryKey(id);
            Product product = new Product();
            product.setId(id);
            product.setName(name);
            iProductMapper.updateByPrimaryKeySelective(product);
            return ServerResponse.createBySuccessMessage("更新成功");
        } catch (Exception e) {
            throw new BaseException(ServerCode.WRONG_PARAM, "更新成功");
        }
    }

    /**
     * 修改单个product标签
     * <p>Title: updateProductLabel</p>
     * <p>Description: </p>
     *
     * @param id
     * @param labelId
     * @return
     */
    public ServerResponse updateProductLabel(String id, String labelId) {
        Label oldLabel = iLabelMapper.selectByPrimaryKey(labelId);
        Product pt = iProductMapper.getById(id);
        if (oldLabel == null)
            return ServerResponse.createBySuccessMessage("标签不存在");
        if (pt == null)
            return ServerResponse.createBySuccessMessage("product不存在");

        // 查询goods及下属product  queryGoodsList
        List<Product> productList = iProductMapper.queryByGoodsId(pt.getGoodsId());
        for (Product product : productList) {
            //查找 对应product对应的goods中，有没有已经设置过该标签
            if (iLabelMapper.selectByPrimaryKey(product.getLabelId()).getName().equals(oldLabel.getName()))
                return ServerResponse.createBySuccessMessage("同一货品中，不能添加重复标签");
        }
        try {
            pt.setLabelId(labelId);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }


    /**
     * 批量添加/修改product标签
     * <p>Title: updateProductLabel</p>
     * <p>Description: </p>
     *
     * @param productLabelList
     * @return
     */
    public ServerResponse updateProductLabelList(String productLabelList) {

        JSONArray productLabelLists = JSONArray.parseArray(productLabelList);
        for (int i = 0; i < productLabelLists.size(); i++) {
            JSONObject productLabel = productLabelLists.getJSONObject(i);
            Label oldLabel = iLabelMapper.selectByPrimaryKey(productLabel.getString("labelId"));
            Product pt = iProductMapper.getById(productLabel.getString("id"));
            if (oldLabel == null)
                return ServerResponse.createBySuccessMessage("标签不存在");
            if (pt == null)
                return ServerResponse.createBySuccessMessage("商品不存在");

            // 查询goods及下属product  queryGoodsList
            List<Product> productList = iProductMapper.queryByGoodsId(pt.getGoodsId());
            for (Product product : productList) {
                //查找 对应product对应的goods中，有没有已经设置过该标签
                if (iLabelMapper.selectByPrimaryKey(product.getLabelId()).getName().equals(oldLabel.getName()))
                    return ServerResponse.createBySuccessMessage("同一货品中，不能添加重复标签");
            }
        }

        try {
            //批量修改 多个product的 标签
            for (int i = 0; i < productLabelLists.size(); i++) {
                JSONObject productLabel = productLabelLists.getJSONObject(i);
                Product pt = iProductMapper.getById(productLabel.getString("id"));
                pt.setLabelId(productLabel.getString("labelId"));
                iProductMapper.updateByPrimaryKeySelective(pt);
            }
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    public ProductDTO getProductDTO(String productSn, String shopCount) {
        Example example = new Example(Product.class);
        example.createCriteria()
                .andEqualTo(Product.DATA_STATUS, '0')
                .andEqualTo(Product.PRODUCT_SN, productSn)
                .andEqualTo(Product.TYPE, "1")
                .andEqualTo(Product.MAKET, "1")
//                .andEqualTo(Product.WORKER_TYPE_ID,workerTypeId)
        ;
        List<Product> products = iProductMapper.selectByExample(example);
        ProductDTO productsDTO = new ProductDTO();
        if (products != null && products.size() > 0) {
            Product product = products.get(0);
            productsDTO.setGoodsId(product.getGoodsId());
            productsDTO.setProductId(product.getId());
            productsDTO.setProductName(product.getName());
            productsDTO.setUnitName(product.getUnitName());
            productsDTO.setLabelId(product.getLabelId());
            productsDTO.setShopCount(shopCount);
            Goods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());
            if (goods != null) {
                productsDTO.setGoodsName(goods.getName());
                productsDTO.setProductType(String.valueOf(goods.getType()));
                productsDTO.setBuy(String.valueOf(goods.getBuy()));
            }
        } else {
            productsDTO.setProductSn(productSn);
            productsDTO.setMsg("找不到该商品（" + productSn + "）,请检查是否创建或者停用！");
        }
        return productsDTO;
    }

    public PageInfo queryProductData( Integer pageNum,Integer pageSize,  String name, String categoryId, String productType, String[] productId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = iProductMapper.queryProductData(name, categoryId, productType, productId);
        PageInfo pageResult = new PageInfo(productList);
        return pageResult;
    }

}
