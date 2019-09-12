package com.dangjia.acg.service.product;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.basics.IGoodsSeriesMapper;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dto.product.DjBasicsLabelDTO;
import com.dangjia.acg.mapper.product.DjBasicsLabelMapper;
import com.dangjia.acg.mapper.product.DjBasicsLabelValueMapper;
import com.dangjia.acg.mapper.product.DjBasicsProductLabelValMapper;
import com.dangjia.acg.mapper.product.DjBasicsProductMapper;
import com.dangjia.acg.mapper.product.IBasicsGoodsMapper;
import com.dangjia.acg.modle.brand.GoodsSeries;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsLabel;
import com.dangjia.acg.modle.product.DjBasicsLabelValue;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import org.apache.commons.lang3.StringUtils;
import com.dangjia.acg.modle.product.DjBasicsProductLabelVal;
import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import com.dangjia.acg.modle.store.Store;
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
     * @param name
     * @return
     */
    public ServerResponse queryProductData(String name){
        Example example = new Example(DjBasicsProduct.class);
        if(!CommonUtil.isEmpty(name)){
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
     * 保存goods
     * <p>Title: saveGoods</p>
     * <p>Description: </p>
     *
     * @param name
     * @param categoryId
     * @param buy
     * @param sales
     * @param unitId
     * @param type
     * @param arrString
     * @return
     */
    public ServerResponse saveBasicsGoods(String name, String categoryId, Integer buy,
                                    Integer sales, String unitId, Integer type, String arrString,String otherName) {
        try {
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

            BasicsGoods goods = new BasicsGoods();
            goods.setName(name);
            goods.setOtherName(otherName);//别名
            goods.setCategoryId(categoryId);//分类
            goods.setBuy(buy);//购买性质
            goods.setSales(sales);//退货性质
            goods.setUnitId(unitId);//单位
            goods.setType(type);//goods性质
            goods.setCreateDate(new Date());
            goods.setModifyDate(new Date());
            iBasicsGoodsMapper.insert(goods);
            if (buy != 2) //非自购
            {
                if (!StringUtils.isNoneBlank(arrString)) {
                    GoodsSeries gs = new GoodsSeries();
                    gs.setGoodsId(goods.getId());
                    gs.setBrandId(null);
                    gs.setSeriesId(null);
                    iGoodsSeriesMapper.insert(gs);
                } else {
                    JSONArray arr = JSONArray.parseArray(arrString);
                    for (int i = 0; i < arr.size(); i++) {//新增goods关联品牌系列
                        JSONObject obj = arr.getJSONObject(i);
                        GoodsSeries gs = new GoodsSeries();
                        gs.setGoodsId(goods.getId());
                        if (!StringUtils.isNoneBlank(obj.getString("brandId"))) {
                            gs.setBrandId(null);
                        } else {
                            gs.setBrandId(obj.getString("brandId"));
                        }

                        if (!StringUtils.isNoneBlank(obj.getString("seriesId"))) {
                            gs.setSeriesId(null);
                        } else {
                            gs.setSeriesId(obj.getString("seriesId"));
                        }
                        iGoodsSeriesMapper.insert(gs);
                    }
                }

            }

            return ServerResponse.createBySuccess("新增成功", goods.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }


    /**
     * 查询商品标签
     * @param productId
     * @return
     */
    public ServerResponse queryProductLabels(String productId) {
        DjBasicsProduct djBasicsProduct = djBasicsProductMapper.selectByPrimaryKey(productId);
        List<DjBasicsLabelDTO> djBasicsLabelDTOList=new ArrayList<>();
        Arrays.asList(djBasicsProduct.getLabelId().split(",")).forEach(str ->{
            DjBasicsLabel djBasicsLabel = djBasicsLabelMapper.selectByPrimaryKey(str);
            DjBasicsLabelDTO djBasicsLabelDTO=new DjBasicsLabelDTO();
            djBasicsLabelDTO.setId(djBasicsLabel.getId());
            djBasicsLabelDTO.setName(djBasicsLabel.getName());
            Example example=new Example(DjBasicsLabelValue.class);
            example.createCriteria().andEqualTo(DjBasicsLabelValue.LABEL_ID,str)
                    .andEqualTo(DjBasicsLabelValue.DATA_STATUS,0);
            djBasicsLabelDTO.setLabelValueList(djBasicsLabelValueMapper.selectByExample(example));
            djBasicsLabelDTOList.add(djBasicsLabelDTO);
        });
        if(djBasicsLabelDTOList.size()<=0)
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
        return ServerResponse.createBySuccess("查询成功",djBasicsLabelDTOList);
    }


    /**
     * 商品打标签
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
