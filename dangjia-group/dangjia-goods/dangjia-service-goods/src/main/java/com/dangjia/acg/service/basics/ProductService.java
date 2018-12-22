package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.basics.*;
import com.dangjia.acg.modle.basics.Label;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.basics.WorkerTechnology;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.dangjia.acg.modle.brand.Unit;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @类 名： ProductServiceImpl
 * @功能描述： 商品service实现类
 * @作者信息： zmj
 * @创建时间： 2018-9-10下午2:33:37
 */
@Service
public class ProductService {
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

    private static Logger LOG = LoggerFactory.getLogger(ProductService.class);
    //查询货品
    public ServerResponse<PageInfo> queryProduct(Integer pageNum, Integer pageSize, String categoryId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<Product> productList = iProductMapper.query(categoryId);
            PageInfo pageResult = new PageInfo(productList);
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (Product p : productList) {
                if (p.getImage() == null) {
                    continue;
                }
                String[] imgArr = p.getImage().split(",");
                String imgStr = "";
                String imgUrlStr = "";
                for (int i = 0; i < imgArr.length; i++) {
                    if (i == imgArr.length - 1) {
                        imgStr += address + imgArr[i];
                        imgUrlStr += imgArr[i];
                    } else {
                        imgStr += address + imgArr[i] + ",";
                        imgUrlStr += imgArr[i] + ",";
                    }
                }
                p.setImage(imgStr);
                Map<String, Object> map = CommonUtil.beanToMap(p);
                map.put("imageUrl", imgUrlStr);
                if (!StringUtils.isNotBlank( p.getLabelId())) {
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
     * 保存货品
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
                String productSn  = obj.getString("productSn");//货品编号
                String name  =  obj.getString("name");//货品品名称
                int snCount =0;
                int nameCount =0;
                for(int j = 0;j < jsonArr.size(); j++)
                {
                    JSONObject objJ = jsonArr.getJSONObject(j);
                    if(productSn.equals(objJ.getString("productSn")))
                    {
                        snCount ++;
                        if(snCount > 1)
                            return ServerResponse.createByErrorMessage("货号编号不能重复");
                    }
                    if(name.equals(objJ.getString("name")))
                    {
                        LOG.info("insertProduct name:" + name);
                        nameCount ++;
                        if(nameCount > 1)
                            return ServerResponse.createByErrorMessage("商品名称不能重复");
                    }
                }
            }

            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                Product product = new Product();
                String productId = obj.getString("id");
                product.setName(obj.getString("name"));//货品品名称
                product.setCategoryId(obj.getString("categoryId"));//分类id
                product.setGoodsId(obj.getString("goodsId"));//商品id
                String productSn  = obj.getString("productSn");
                if (!StringUtils.isNotBlank(productSn))
                    return ServerResponse.createByErrorMessage("货号编号不能为空");
                product.setProductSn(productSn);//货品编号
                String[] imgArr = obj.getString("image").split(",");
                String[] technologyIds = obj.getString("technologyIds").split(",");//工艺节点
                String imgStr = "";
                for (int j = 0; j < imgArr.length; j++) {
                    String img = imgArr[j];
                    if (j == imgArr.length - 1) {
                        imgStr += img;
                    } else {
                        imgStr += img + ",";
                    }
                }
                if (!StringUtils.isNotBlank(imgStr))
                    return ServerResponse.createByErrorMessage("货品图片不能为空");
                product.setImage(imgStr);//图片地址
                product.setUnitId(obj.getString("unitId"));//单位
                product.setLabelId(obj.getString("labelId"));//标签
                product.setUnitName(obj.getString("unitName"));//单位
                product.setWeight(obj.getString("weight"));//重量
                product.setConvertQuality(obj.getDouble("convertQuality"));//换算量
                product.setConvertUnit(obj.getString("convertUnit"));//换算单位
                product.setType(obj.getInteger("type"));//是否禁用0：禁用；1不禁用
                product.setMaket(obj.getInteger("maket"));//是否上架0：不上架；1：上架
                product.setCost(obj.getDouble("cost"));//平均成本价
                product.setPrice(obj.getDouble("price"));//销售价
                product.setProfit(obj.getDouble("profit"));//利润率
                product.setBrandId(obj.getString("brandId"));//品牌id
                String brandSeriesId = obj.getString("brandSeriesId");
                if (!StringUtils.isNotBlank(brandSeriesId))
                    return ServerResponse.createByErrorMessage("品牌系列id不能为空");
                product.setBrandSeriesId(brandSeriesId);//品牌系列id
                product.setValueNameArr(obj.getString("valueNameArr"));//选中的属性选项名称字符串
                product.setValueIdArr(obj.getString("valueIdArr"));//选中的属性选项id串
                product.setAttributeIdArr(obj.getString("attributeIdArr"));//选中的属性id字符串
                if (productId == null || "".equals(productId)) {//没有id则新增
                    product.setCreateDate(new Date());
                    product.setModifyDate(new Date());
                    iProductMapper.insert(product);
                } else {//修改
                    product.setId(productId);
                    product.setModifyDate(new Date());
                    iProductMapper.updateByPrimaryKeySelective(product);
                }
                iTechnologyMapper.deleteWokerTechnologyByWgId(product.getId());
                for (String id : technologyIds) {
                    if (StringUtils.isNotBlank(id)) {
                        WorkerTechnology wt = new WorkerTechnology();
                        wt.setWorkerGoodsId(product.getId());
                        wt.setTechnologyId(id);
                        iTechnologyMapper.insertWokerTechnology(wt);// 需要将工艺替换
                    }
                }
            }
            return ServerResponse.createBySuccessMessage("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    /**
     * 修改货品
     * <p>Title: updateProduct</p>
     * <p>Description: </p>
     * @param productArr
     * @return
     */
    public ServerResponse updateProduct(String productArr) {
        try {
            JSONArray jsonArr = JSONArray.parseArray(productArr);
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                Product product = new Product();
                product.setId(obj.getString("id"));
                product.setName(obj.getString("name"));//货品品名称
                product.setCategoryId(obj.getString("categoryId"));//分类id
                product.setGoodsId(obj.getString("goodsId"));//商品id
                String productSn  = obj.getString("productSn");
                if (!StringUtils.isNotBlank(productSn))
                    return ServerResponse.createByErrorMessage("货号编号不能为空");
                product.setProductSn(productSn);//货品编号
                String[] technologyIds = obj.getString("technologyIds").split(",");//工艺节点
                if (obj.getString("image") != null && !"".equals(obj.getString("image"))) {
                    String[] imgArr = obj.getString("image").split(",");
                    String imgStr = "";
                    for (int j = 0; j < imgArr.length; j++) {
                        String img = imgArr[j];
                        if (j == imgArr.length - 1) {
                            imgStr += img;
                        } else {
                            imgStr += img + ",";
                        }
                    }
                    if (!StringUtils.isNotBlank(imgStr))
                        return ServerResponse.createByErrorMessage("货品图片不能为空");
                    product.setImage(imgStr);//图片地址
                }
                product.setUnitName(obj.getString("unitName"));//单位
                product.setUnitId(obj.getString("unitId"));//单位
                product.setLabelId(obj.getString("labelId"));//标签
                product.setWeight(obj.getString("weight"));//重量
                product.setConvertQuality(obj.getDouble("convertQuality"));//换算量
                product.setConvertUnit(obj.getString("convertUnit"));//换算单位
                product.setType(obj.getInteger("type"));//是否禁用0：禁用；1不禁用
                product.setMaket(obj.getInteger("maket"));//是否上架0：不上架；1：上架
                product.setCost(obj.getDouble("cost"));//平均成本价
                product.setPrice(obj.getDouble("price"));//销售价
                product.setProfit(obj.getDouble("profit"));//利润率
                product.setBrandId(obj.getString("brand_id"));//品牌id
                String brandSeriesId = obj.getString("brandSeriesId");
                if (!StringUtils.isNotBlank(brandSeriesId))
                    return ServerResponse.createByErrorMessage("品牌系列id不能为空");
                product.setBrandSeriesId(brandSeriesId);//品牌系列id
                product.setValueNameArr(obj.getString("valueNameArr"));//选中的属性选项名称字符串
                product.setValueIdArr(obj.getString("valueIdArr"));//选中的属性选项id串
                product.setAttributeIdArr(obj.getString("attributeIdArr"));//选中的属性id字符串
                product.setModifyDate(new Date());
                int p = iProductMapper.updateByPrimaryKeySelective(product);
                iTechnologyMapper.deleteWokerTechnologyByWgId(product.getId());
                for (String id : technologyIds) {
                    if (StringUtils.isNotBlank(id)) {
                        WorkerTechnology wt = new WorkerTechnology();
                        wt.setWorkerGoodsId(product.getId());
                        wt.setTechnologyId(id);
                        iTechnologyMapper.insertWokerTechnology(wt);// 需要将工艺替换
                    }
                }
            }
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    /**
     * 根据货品id查询货品对象
     *
     * @param id
     * @return
     */
    public ServerResponse getProductById(String id) {
        try {
            return ServerResponse.createBySuccess("查询成功", iProductMapper.selectByPrimaryKey(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 根据货品id删除货品对象
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
     * 修改单个货品标签
     * <p>Title: updateProductLabel</p>
     * <p>Description: </p>
     * @param id
     * @param labelId
     * @return
     */
    public ServerResponse updateProductLabel(String id,String labelId) {
        Label oldLabel = iLabelMapper.getLabelByName(labelId);
        Product pt = iProductMapper.getById(id);
        if (oldLabel == null)
            return ServerResponse.createBySuccessMessage("标签不存在");
        if (pt == null)
            return ServerResponse.createBySuccessMessage("货品不存在");

        // 查询商品及下属货品  queryGoodsList
        List<Product> productList = iProductMapper.queryByGoodsId(pt.getGoodsId());
        for (Product product: productList)
        {
            //查找 对应货品对应的商品中，有没有已经设置过该标签
            if(iLabelMapper.selectByPrimaryKey(product.getLabelId()).getName().equals(oldLabel.getName()))
                return ServerResponse.createBySuccessMessage("同一商品中，不能添加重复标签");
        }
        try {
            pt.setLabelId(labelId);
            iProductMapper.updateByPrimaryKeySelective(pt);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }


    /**
     * 批量添加/修改货品标签
     * <p>Title: updateProductLabel</p>
     * <p>Description: </p>
     * @param productLabelList
     * @return
     */
    public ServerResponse updateProductLabelList(String productLabelList) {

        JSONArray productLabelLists = JSONArray.parseArray(productLabelList);
        for (int i = 0; i < productLabelLists.size(); i++) {
            JSONObject productLabel = productLabelLists.getJSONObject(i);
            Label oldLabel = iLabelMapper.getLabelByName(productLabel.getString("labelId"));
            Product pt = iProductMapper.getById(productLabel.getString("id"));
            if (oldLabel == null)
                return ServerResponse.createBySuccessMessage("标签不存在");
            if (pt == null)
                return ServerResponse.createBySuccessMessage("货品不存在");

            // 查询商品及下属货品  queryGoodsList
            List<Product> productList = iProductMapper.queryByGoodsId(pt.getGoodsId());
            for (Product product: productList)
            {
                //查找 对应货品对应的商品中，有没有已经设置过该标签
                if(iLabelMapper.selectByPrimaryKey(product.getLabelId()).getName().equals(oldLabel.getName()))
                    return ServerResponse.createBySuccessMessage("同一商品中，不能添加重复标签");
            }
        }

        try {
            //批量修改 多个货品的 标签
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

}
