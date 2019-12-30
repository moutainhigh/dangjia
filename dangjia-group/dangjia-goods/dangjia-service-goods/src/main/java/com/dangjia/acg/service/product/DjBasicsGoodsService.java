package com.dangjia.acg.service.product;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.product.BasicsGoodsDTO;
import com.dangjia.acg.dto.product.DjBasicsProductTemplateDTO;
import com.dangjia.acg.mapper.basics.IAttributeMapper;
import com.dangjia.acg.mapper.basics.IAttributeValueMapper;
import com.dangjia.acg.mapper.basics.ILabelMapper;
import com.dangjia.acg.mapper.basics.IUnitMapper;
import com.dangjia.acg.mapper.product.*;
import com.dangjia.acg.modle.attribute.Attribute;
import com.dangjia.acg.modle.attribute.AttributeValue;
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
import tk.mybatis.mapper.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;


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
    private IBasicsProductTemplateMapper iBasicsProductTemplateMapper;
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
    private IAttributeMapper iAttributeMapper ;
    @Autowired
    private ILabelMapper iLabelMapper;
    @Autowired
    private DjBasicsLabelValueMapper djBasicsLabelValueMapper;
    @Autowired
    private IProductAddedRelationMapper iProductAddedRelationMapper;
    /**
     * 货品打标签
     *
     * @param goodsId
     * @param labels
     * @return
     */
    public ServerResponse addLabels(String goodsId, String labels,String cityId) {
        BasicsGoods djBasicsGoods = new BasicsGoods();
        djBasicsGoods.setCityId(cityId);
        djBasicsGoods.setId(goodsId);
        djBasicsGoods.setLabelIds(labels);
        djBasicsGoodsMapper.updateByPrimaryKeySelective(djBasicsGoods);
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
            String cityId = basicsGoodsDTO.getCityId();
            String name = basicsGoodsDTO.getName();
            String unitId = basicsGoodsDTO.getUnitId();
            String categoryId = basicsGoodsDTO.getCategoryId();
            int type = basicsGoodsDTO.getType();
            if (!StringUtils.isNotBlank(name))
                return ServerResponse.createByErrorMessage("名字不能为空");

            List<BasicsGoods> goodsList = iBasicsGoodsMapper.queryByName(name,cityId);
            if (goodsList.size() > 0)
                return ServerResponse.createByErrorMessage("名字不能重复");

            if (!StringUtils.isNotBlank(unitId))
                return ServerResponse.createByErrorMessage("单位id不能为空");

            if (!StringUtils.isNotBlank(categoryId))
                return ServerResponse.createByErrorMessage("分类不能为空");

            if (type < -1)
                return ServerResponse.createByErrorMessage("性质不能为空");

            BasicsGoods goods = getBasicsGoods(new BasicsGoods(),basicsGoodsDTO);
            goods.setCityId(cityId);
            goods.setCreateDate(new Date());
            iBasicsGoodsMapper.insert(goods);
            return ServerResponse.createBySuccess("新增成功", goods.getId());
        } catch (Exception e) {
            LOG.error("新增货品失败：", e);
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateBasicsGoods(BasicsGoodsDTO basicsGoodsDTO) {
        String id = basicsGoodsDTO.getId();
        String name = basicsGoodsDTO.getName();
        BasicsGoods oldBasicsGoods = iBasicsGoodsMapper.selectByPrimaryKey(id);
        if (!oldBasicsGoods.getName().equals(name)) {
            List<BasicsGoods> goodsList = iBasicsGoodsMapper.queryByName(name,basicsGoodsDTO.getCityId());
            if (goodsList.size() > 0)
                return ServerResponse.createByErrorMessage("该货品已存在");
        }
        BasicsGoods goods = getBasicsGoods(oldBasicsGoods,basicsGoodsDTO);
        goods.setId(basicsGoodsDTO.getId());
        iBasicsGoodsMapper.updateByPrimaryKeySelective(goods);

        iBasicsProductTemplateMapper.updateProductCategoryByGoodsId(id, oldBasicsGoods.getCategoryId());
        return ServerResponse.createBySuccess("修改成功", id);
    }

    /**
     * 对象转换
     *
     * @return
     */
    private BasicsGoods getBasicsGoods(BasicsGoods goods,BasicsGoodsDTO basicsGoodsDTO) {
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
        goods.setIsReservationDeliver(basicsGoodsDTO.getIsReservationDeliver());
        goods.setHousekeeperAcceptance(basicsGoodsDTO.getHousekeeperAcceptance()==null?0:basicsGoodsDTO.getHousekeeperAcceptance());
        goods.setTechnologyIds(basicsGoodsDTO.getTechnologyIds());
        if (!StringUtils.isNoneBlank(basicsGoodsDTO.getAttributeIdArr())) {
            goods.setAttributeIdArr(null);
        } else {
            goods.setAttributeIdArr(basicsGoodsDTO.getAttributeIdArr());
        }
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
        //删除货品下的商品信息
        Example example = new Example(DjBasicsProductTemplate.class);
        example.createCriteria().andEqualTo("goodsId", id);
        iBasicsProductTemplateMapper.deleteByExample(example);
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
            Map goodsMap = BeanUtils.beanToMap(basicsGoods);
            List<BasicsGoodsCategory> goodsCategoryList = getAllCategoryChildById(basicsGoods.getCategoryId());
            goodsMap.put("goodsCategoryList", goodsCategoryList);
            return ServerResponse.createBySuccess("查询成功", goodsMap);
        } catch (Exception e) {
            LOG.error("getBasicsGoodsByGid查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");

        }
    }

    /**
     * 根据类别I查询当前类别父类下面的甩有子类信息
     *
     * @param categoryId
     * @return
     */
    private List<BasicsGoodsCategory> getAllCategoryChildById(String categoryId) {
        BasicsGoodsCategory basicsGoodsCategory = iBasicsGoodsCategoryMapper.selectByPrimaryKey(categoryId);
        List<BasicsGoodsCategory> goodsCategoryList = iBasicsGoodsCategoryMapper.getAllCategoryChildById(basicsGoodsCategory.getParentTop());
        return goodsCategoryList;
    }

    /**
     * 供店铺使用
     * 模糊查询goods及下属product
     *
     * @param pageDTO
     * @param categoryId
     * @param name
     * @param type       是否禁用  0：禁用；1不禁用 ;  -1全部默认
     * @return
     */
    public ServerResponse queryGoodsListStorefront(String storefontId,String ifDjselfManage ,PageDTO pageDTO, String categoryId, String name, Integer type) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            String bgtype=null;
            if (ifDjselfManage.equals("0"))
            {
                bgtype="0,1";//删选0：实物商品；1：服务商品
            }

            List<DjBasicsProductTemplateDTO> productList = iBasicsProductTemplateMapper.queryProductTemplateByGoodsId(categoryId,storefontId,bgtype,name);
            LOG.debug("*****************判断productList是否为空 start*************************");
            if (productList == null || productList.size() <= 0) {
                return ServerResponse.createByErrorMessage("查无数据！");
            }
            LOG.debug("*****************判断productList是否为空 end *************************");

            for (DjBasicsProductTemplateDTO p : productList) {
                //type表示： 是否禁用  0：禁用；1不禁用 ;  -1全部默认
                if (type != null && !type.equals(p.getType()) && -1 != type) //不等于 type 的不返回给前端
                {
                    continue;
                }
                //图片路径+前缀
                StringBuilder imgUrlStr = new StringBuilder();
                StringBuilder imgStr = new StringBuilder();
                if (!CommonUtil.isEmpty(p.getImage())) {
                    String[] imgArr = p.getImage().split(",");
                    StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
                }
                p.setImage(imgStr.toString());
                p.setImageUrl(imgUrlStr.toString());
                //初始化单元名称
                if (StringUtils.isNoneBlank(p.getConvertUnit())) {
                    p.setConvertUnitName(iUnitMapper.selectByPrimaryKey(p.getConvertUnit()).getName());
                }

                String valueIdArr=p.getValueIdArr();
                String strNewValueNameArr = "";
                if (StringUtil.isNotEmpty(valueIdArr)) {
                    String[] newValueNameArr = valueIdArr.split(",");
                    for (int i = 0; i < newValueNameArr.length; i++) {
                        String valueId = newValueNameArr[i];
                        if (StringUtils.isNotBlank(valueId)) {
                            AttributeValue attributeValue = iAttributeValueMapper.selectByPrimaryKey(valueId);
                            if (attributeValue != null && StringUtils.isNotBlank(attributeValue.getName())) {
                                Attribute attribute = iAttributeMapper.selectByPrimaryKey(attributeValue.getAttributeId());
                                if (attribute != null && attribute.getType() == 2 && StringUtils.isNotBlank(strNewValueNameArr)) {
                                    strNewValueNameArr = attributeValue.getName();
                                } else if (attribute != null && attribute.getType() == 2) {
                                    strNewValueNameArr = strNewValueNameArr + "," + attributeValue.getName();
                                }
                            }

                        }
                    }
                }
                p.setNewValueNameArr(strNewValueNameArr);
                //初始化标签名称
                if (!StringUtils.isNotBlank(p.getLabelId())) {
                    p.setLabelId("");
                    p.setLabelName("");
                } else {
                    DjBasicsLabelValue label = djBasicsLabelValueMapper.selectByPrimaryKey(p.getLabelId());
                    if (label != null && label.getName() != null)
                        p.setLabelName(label.getName());
                }

            }

            PageInfo pageResult = new PageInfo(productList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询异常");
        }
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
    public ServerResponse queryGoodsList(PageDTO pageDTO, String categoryId, String name, Integer type,String cityId) {
        try {
            LOG.info("tqueryGoodsListByCategoryLikeName type :" + type);
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<BasicsGoods> goodsList = djBasicsGoodsMapper.queryGoodsListByCategoryLikeName(categoryId, name,cityId);
            PageInfo pageResult = new PageInfo(goodsList);
            List<Map<String, Object>> gMapList = new ArrayList<>();
            for (BasicsGoods goods : goodsList) {
                Map<String, Object> gMap = BeanUtils.beanToMap(goods);
                List<Map<String, Object>> mapList = new ArrayList<>();
                gMap.put("goodsUnitName", iUnitMapper.selectByPrimaryKey(goods.getUnitId()).getName());

                List<DjBasicsProductTemplate> productList = iBasicsProductTemplateMapper.queryByGoodsId(goods.getId());
                for (DjBasicsProductTemplate p : productList) {
                    //type表示： 是否禁用  0：禁用；1不禁用 ;  -1全部默认
                    if (type != null && !type.equals(p.getType()) && -1 != type) //不等于 type 的不返回给前端
                        continue;
                   /* StringBuilder imgUrlStr = new StringBuilder();
                    StringBuilder imgStr = new StringBuilder();
                    if (!CommonUtil.isEmpty(p.getImage())) {
                        String[] imgArr = p.getImage().split(",");
                        StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
                    }*/
                      Map<String, Object> map = BeanUtils.beanToMap(p);
                    map.put("imageUrl", StringTool.getImage(p.getImage(),address));
                    map.put("detailImageUrl", StringTool.getImage(p.getDetailImage(),address));
                    if(StringUtils.isNoneBlank(p.getConvertUnit())){
                        map.put("convertUnitName", iUnitMapper.selectByPrimaryKey(p.getConvertUnit()).getName());
                    }

                    map.put("newValueNameArr", getValueNameArr(p.getValueIdArr()));

                    if (!StringUtils.isNotBlank(p.getLabelId())) {
                        map.put("labelId", "");
                        map.put("labelName", "");
                    } else {
                        map.put("labelId", p.getLabelId());
                        DjBasicsLabelValue label = djBasicsLabelValueMapper.selectByPrimaryKey(p.getLabelId());
                        if (label!=null&&label.getName() != null)
                            map.put("labelName", label.getName());
                    }
                    //只有增值类关联商品才会有此数据
                    if(StringUtils.isNotBlank(p.getIsRelateionProduct())&&"1".equals(p.getIsRelateionProduct())){

                        List<String> relationProductIds=iProductAddedRelationMapper.getProdTemplateIdsByAddId(p.getId());
                        map.put("relationProductIds",relationProductIds);//关联商品IDs，用逗号分隔
                    }
                    if(p.getTechnologyIds()!=null&&StringUtils.isNotBlank(p.getTechnologyIds())){
                        List<String> technologyIds= Arrays.asList(p.getTechnologyIds() .split(",")).stream().map(s -> (s.trim())).collect(Collectors.toList());
                        map.put("technologyIds", technologyIds);

                    }
                    map.put("id",p.getId());
                    map.put("productType",goods.getType());
                    mapList.add(map);
                }
                gMap.put("productList", mapList);
                if(goods.getTechnologyIds()!=null&&StringUtils.isNotBlank(goods.getTechnologyIds())){
                    List<String> technologyIds= Arrays.asList(goods.getTechnologyIds() .split(",")).stream().map(s -> (s.trim())).collect(Collectors.toList());
                    gMap.put("technologyIds", technologyIds);
                }

                gMapList.add(gMap);
            }
            pageResult.setList(gMapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    public String  getValueNameArr(String valueIdArr){
        StringBuffer strNewValueNameArr=new StringBuffer();
        if (StringUtils.isNotBlank(valueIdArr)) {
            String[] newValueNameArr = valueIdArr.split(",");
            for (int i = 0; i < newValueNameArr.length; i++) {
              String valueId = newValueNameArr[i];
              AttributeValue attributeValue = iAttributeValueMapper.selectByPrimaryKey(valueId);
              if(attributeValue!= null&&StringUtils.isNotBlank(attributeValue.getName())){
                  if (StringUtils.isBlank(strNewValueNameArr.toString())) {
                      strNewValueNameArr.append(attributeValue.getName());
                  } else {
                      strNewValueNameArr.append(",").append(attributeValue.getName());
                  }
              }
            }
        }
        return strNewValueNameArr.toString();
    }
    /**
     * 查询货品标签
     * @param goodsId
     * @return
     */
    public ServerResponse queryGoodsLabels(String goodsId) {
        String s = djBasicsGoodsMapper.queryGoodsLabels(goodsId);
        if(!CommonUtil.isEmpty(s)){
            List<String> strings = Arrays.asList(s.split(","));
            Example example=new Example(DjBasicsLabel.class);
            example.createCriteria().andIn(DjBasicsLabel.ID,strings);
            return ServerResponse.createBySuccess("查询成功",iLabelMapper.selectByExample(example));
        }else{
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }

    }

}
