package com.dangjia.acg.service.product;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.product.BasicsGoodsDTO;
import com.dangjia.acg.mapper.basics.IAttributeValueMapper;
import com.dangjia.acg.mapper.basics.ILabelMapper;
import com.dangjia.acg.mapper.basics.IUnitMapper;
import com.dangjia.acg.mapper.product.*;
import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.basics.Label;
import com.dangjia.acg.modle.product.*;
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
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/12
 * Time: 9:54
 */
@Service
public class DjBasicsGoodsService {
    private static Logger LOG = LoggerFactory.getLogger(DjBasicsGoodsService.class);
    @Autowired
    private DjBasicsGoodsMapper djBasicsGoodsMapper;
    @Autowired
    private DjBasicsProductMapper djBasicsProductMapper;
    @Autowired
    private DjBasicsProductMaterialMapper djBasicsProductMaterialMapper;
    @Autowired
    DjBasicsProductWorkerMapper djBasicsProductWorkerMapper;
    @Autowired
    private IBasicsGoodsMapper iBasicsGoodsMapper;
    @Autowired
    private IBasicsGoodsCategoryMapper iBasicsGoodsCategoryMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IUnitMapper iUnitMapper;
    @Autowired
    private IAttributeValueMapper iAttributeValueMapper;
    @Autowired
    private ILabelMapper iLabelMapper;

    /**
     * 货品打标签
     * @param goodsId
     * @param labels
     * @return
     */
    public ServerResponse addLabels(String goodsId, String labels) {
        DjBasicsGoods djBasicsGoods=new DjBasicsGoods();
        djBasicsGoods.setId(goodsId);
        djBasicsGoods.setLabelIds(labels);
        djBasicsGoodsMapper.updateByPrimaryKeySelective(djBasicsGoods);
        Example example=new Example(DjBasicsProduct.class);
        example.createCriteria().andEqualTo(DjBasicsProduct.GOODS_ID,goodsId);
        DjBasicsProduct djBasicsProduct=new DjBasicsProduct();
        djBasicsProduct.setLabelId(labels);
        djBasicsProductMapper.updateByExampleSelective(djBasicsProduct,example);
        return ServerResponse.createBySuccessMessage("货品打标签成功");
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
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateBasicsGoods(BasicsGoodsDTO basicsGoodsDTO){
            String id = basicsGoodsDTO.getId();
            String name = basicsGoodsDTO.getName();
            BasicsGoods oldBasicsGoods = iBasicsGoodsMapper.selectByPrimaryKey(id);
            if (!oldBasicsGoods.getName().equals(name)) {
                List<BasicsGoods> goodsList = iBasicsGoodsMapper.queryByName(name);
                if (goodsList.size() > 0)
                    return ServerResponse.createByErrorMessage("该货品已存在");
            }
            BasicsGoods goods = getBasicsGoods(basicsGoodsDTO);
            goods.setId(basicsGoodsDTO.getId());
            iBasicsGoodsMapper.updateByPrimaryKeySelective(goods);

            djBasicsProductMapper.updateProductCategoryByGoodsId(id,oldBasicsGoods.getCategoryId());
            return ServerResponse.createBySuccessMessage("修改成功");
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
     * 根据id删除goods和下面的商品信息
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse deleteBasicsGoods(String id) {
//            if (true)
//                return ServerResponse.createByErrorMessage("不能执行删除操作");
            iBasicsGoodsMapper.deleteByPrimaryKey(id);

            //删除材料商品扩展表信息
            djBasicsProductMapper.deleteProductMaterial(id);
            //删除人工商品扩展表信息
            djBasicsProductMapper.deleteProductWorker(id);
            //删除货品下的商品信息
            Example example = new Example(DjBasicsProduct.class);
            example.createCriteria().andEqualTo("goodsId", id);
            djBasicsProductMapper.deleteByExample(example);
            return ServerResponse.createBySuccessMessage("删除成功");
    }

    /**
     * 根据goodsid查询对应goods
     * <p>Title: getGoodsByGid</p>
     * <p>Description: </p>
     *
     * @param goodsId
     * @return
     */
    public ServerResponse getBasicsGoodsByGid(String goodsId) {
        try {
            BasicsGoods basicsGoods = iBasicsGoodsMapper.queryById(goodsId);
            Map goodsMap= BeanUtils.beanToMap(basicsGoods);
            List<BasicsGoodsCategory> goodsCategoryList=getAllCategoryChildById(basicsGoods.getCategoryId());
            goodsMap.put("goodsCategoryList",goodsCategoryList);
            return ServerResponse.createBySuccess("查询成功", goodsMap);
        } catch (Exception e) {
            LOG.error("getBasicsGoodsByGid查询失败：",e);
            return ServerResponse.createByErrorMessage("查询失败");

        }
    }

    /**
     * 根据类别I查询当前类别父类下面的甩有子类信息
     * @param categoryId
     * @return
     */
    private List<BasicsGoodsCategory> getAllCategoryChildById(String categoryId){
        BasicsGoodsCategory basicsGoodsCategory=iBasicsGoodsCategoryMapper.selectByPrimaryKey(categoryId);
        List<BasicsGoodsCategory> goodsCategoryList=iBasicsGoodsCategoryMapper.getAllCategoryChildById(basicsGoodsCategory.getParentTop());
        return goodsCategoryList;
    }
    /**
     * 模糊查询goods及下属product
     *
     * @param pageDTO
     * @param categoryId
     * @param name
     * @param type       是否禁用  0：禁用；1不禁用 ;  -1全部默认
     * @return
     */
    public ServerResponse queryGoodsList(PageDTO pageDTO, String categoryId, String name, Integer type) {
        try {
            LOG.info("tqueryGoodsListByCategoryLikeName type :" + type);
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjBasicsGoods> goodsList = djBasicsGoodsMapper.queryGoodsListByCategoryLikeName(categoryId, name);
            PageInfo pageResult = new PageInfo(goodsList);
            List<Map<String, Object>> gMapList = new ArrayList<>();
            for (DjBasicsGoods goods : goodsList) {
                Map<String, Object> gMap = BeanUtils.beanToMap(goods);
                List<Map<String, Object>> mapList = new ArrayList<>();
                gMap.put("goodsUnitName", iUnitMapper.selectByPrimaryKey(goods.getUnitId()).getName());

                List<DjBasicsProduct> productList = djBasicsProductMapper.queryByGoodsId(goods.getId());
                for (DjBasicsProduct p : productList) {
                    //type表示： 是否禁用  0：禁用；1不禁用 ;  -1全部默认
                    if (type!=null&& !type.equals(p.getType()) && -1 != type) //不等于 type 的不返回给前端
                        continue;
                    StringBuilder imgUrlStr = new StringBuilder();
                    StringBuilder imgStr = new StringBuilder();
                    if (!CommonUtil.isEmpty(p.getImage())) {
                        String[] imgArr = p.getImage().split(",");
                        StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
                    }
                    p.setImage(imgStr.toString());
                    DjBasicsProductMaterial djBasicsProductMaterial =djBasicsProductMaterialMapper.queryProductMaterialByProductId(p.getId());
                    DjBasicsProductWorker djBasicsProductWorker = djBasicsProductWorkerMapper.queryProductWorkerByProductId(p.getId());
                    Map<String, Object> map = BeanUtils.beanToMap(p);
                    map.put("imageUrl", imgUrlStr.toString());
                    StringBuilder strNewValueNameArr = new StringBuilder();
                    if(djBasicsProductMaterial!=null&&StringUtils.isNotBlank(djBasicsProductMaterial.getId())){
                        map.putAll(BeanUtils.beanToMap(djBasicsProductMaterial));
                        map.put("convertUnitName", iUnitMapper.selectByPrimaryKey(djBasicsProductMaterial.getConvertUnit()).getName());
                        if (StringUtils.isNotBlank(djBasicsProductMaterial.getValueIdArr())) {
                            String[] newValueNameArr = djBasicsProductMaterial.getValueIdArr().split(",");
                            for (int i = 0; i < newValueNameArr.length; i++) {
                                String valueId = newValueNameArr[i];
                                if (StringUtils.isNotBlank(valueId)) {
                                    AttributeValue attributeValue = iAttributeValueMapper.selectByPrimaryKey(valueId);
                                    if (i == 0) {
                                        strNewValueNameArr = new StringBuilder(attributeValue.getName());
                                    } else {
                                        strNewValueNameArr.append(",").append(attributeValue.getName());
                                    }
                                }
                            }
                        }
                    }
                    map.put("newValueNameArr", strNewValueNameArr.toString());
                    if(djBasicsProductWorker!=null&&StringUtils.isNotBlank(djBasicsProductWorker.getId())){
                        map.putAll(BeanUtils.beanToMap(djBasicsProductWorker));
                    }


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
                gMap.put("productList", mapList);
                gMapList.add(gMap);
            }
            pageResult.setList(gMapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
