package com.dangjia.acg.service.product;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.product.ShoppingCartDTO;
import com.dangjia.acg.dto.product.ShoppingCartListDTO;
import com.dangjia.acg.mapper.core.IMasterAttributeValueMapper;
import com.dangjia.acg.mapper.core.IMasterUnitMapper;
import com.dangjia.acg.mapper.delivery.IMasterDeliverOrderAddedProductMapper;
import com.dangjia.acg.mapper.delivery.IOrderItemMapper;
import com.dangjia.acg.mapper.delivery.IOrderMapper;
import com.dangjia.acg.mapper.delivery.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberCollectMapper;
import com.dangjia.acg.mapper.product.IMasterGoodsMapper;
import com.dangjia.acg.mapper.product.IMasterProductTemplateMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.mapper.product.IShoppingCartMapper;
import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberCollect;
import com.dangjia.acg.modle.order.DeliverOrderAddedProduct;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.product.ShoppingCart;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * author:Chenyufeng
 * title:新版购物车处理类
 * date:2019.9.15
 */
@Service
public class ShopCartService {


    @Autowired
    private IMasterGoodsMapper iMasterGoodsMapper;

    @Autowired
    private CraftsmanConstructionService constructionService;

    @Autowired
    private IShoppingCartMapper iShoppingCartmapper;

    @Autowired
    private IMasterProductTemplateMapper iMasterProductTemplateMapper;

    @Autowired
    private BasicsStorefrontAPI basicsStorefrontAPI;

    private Logger logger = LoggerFactory.getLogger(ShopCartService.class);

    @Autowired
    private IMasterStorefrontProductMapper iMasterStorefrontProductMapper;

    @Autowired
    private IMemberCollectMapper iMemberCollectMapper;

    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IOrderItemMapper iOrderItemMapper;
    @Autowired
    private IMasterDeliverOrderAddedProductMapper masterDeliverOrderAddedProductMapper;
    @Autowired
    private IOrderSplitItemMapper iOrderSplitItemMapper;
    @Autowired
    private IMasterAttributeValueMapper iMasterAttributeValueMapper;
    @Autowired
    private IMasterUnitMapper iMasterUnitMapper;
    @Autowired
    private IOrderMapper iOrderMapper;

    /**
     * 获取购物车列表
     *
     * @param userToken
     * @return
     */
    public ServerResponse queryCartList(PageDTO pageDTO, String userToken, String cityId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Storefront> strings = iShoppingCartmapper.queryStorefrontIds(member.getId(), cityId);
            List<ShoppingCartDTO> shoppingCartDTOS = new ArrayList<>();
            strings.forEach(storefront -> {
                ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
                shoppingCartDTO.setStorefrontName(storefront.getStorefrontName());
                shoppingCartDTO.setStorefrontId(storefront.getId());
                shoppingCartDTO.setStorefrontIcon(imageAddress + storefront.getSystemLogo());
                List<ShoppingCartListDTO> shoppingCartListDTOS = iShoppingCartmapper.queryCartList(member.getId(), cityId, storefront.getId(), null);
                shoppingCartListDTOS.forEach(shoppingCartListDTO -> {

                    shoppingCartListDTO.setImageUrl(StringTool.getImage(shoppingCartListDTO.getImage(), imageAddress));//图多张
                    shoppingCartListDTO.setImageSingle(StringTool.getImageSingle(shoppingCartListDTO.getImage(), imageAddress));//图一张
                    //当前时间小于调价的时间时则展示调价预告信息
                    if (shoppingCartListDTO.getAdjustedPrice() == null
                            || shoppingCartListDTO.getModityPriceTime() == null
                            || shoppingCartListDTO.getModityPriceTime().getTime() < (new Date()).getTime()) {
                        shoppingCartListDTO.setAdjustedPrice(null);
                        shoppingCartListDTO.setModityPriceTime(null);
                    }

                    //获取购物车增值商品信息
                    Example example1 = new Example(DeliverOrderAddedProduct.class);
                    example1.createCriteria().andEqualTo(DeliverOrderAddedProduct.ANY_ORDER_ID, shoppingCartListDTO.getId())
                            .andEqualTo(DeliverOrderAddedProduct.SOURCE, 4);
                    List<DeliverOrderAddedProduct> deliverOrderAddedProducts = masterDeliverOrderAddedProductMapper.selectByExample(example1);
                    shoppingCartListDTO.setAddedProducts(deliverOrderAddedProducts);
                    if (!CommonUtil.isEmpty(shoppingCartListDTO.getValueIdArr())) {
                        String strNewValueNameArr = "  ";
                        String[] newValueNameArr = shoppingCartListDTO.getValueIdArr().split(",");
                        for (int i = 0; i < newValueNameArr.length; i++) {
                            String valueId = newValueNameArr[i];
                            if (StringUtils.isNotBlank(valueId)) {
                                AttributeValue attributeValue = iMasterAttributeValueMapper.selectByPrimaryKey(valueId);
                                if (attributeValue != null && StringUtils.isNotBlank(attributeValue.getName())) {
                                    if (StringUtils.isBlank(strNewValueNameArr)) {
                                        strNewValueNameArr = attributeValue.getName();
                                    } else {
                                        strNewValueNameArr = strNewValueNameArr + " ," + attributeValue.getName();
                                    }
                                }
                            }
                        }
                        shoppingCartListDTO.setValueNameArr(strNewValueNameArr);
                    }
                });
                shoppingCartDTO.setShoppingCartListDTOS(shoppingCartListDTOS);
                shoppingCartDTOS.add(shoppingCartDTO);
            });
            if (shoppingCartDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            PageInfo pageResult = new PageInfo(shoppingCartDTOS);
            return ServerResponse.createBySuccess("获取购物车列表成功!", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统报错，获取购物车列表失败!");
        }
    }

    /**
     * 获取购物车数量
     *
     * @param userToken
     * @return
     */
    public ServerResponse getCartNum(String userToken) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return ServerResponse.createBySuccess("获取购物车数量成功!", 0);
            }
            Member member = (Member) object;
            Example example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, member.getId());
            Integer gnum = iShoppingCartmapper.selectCountByExample(example);
            return ServerResponse.createBySuccess("获取购物车数量成功!", gnum);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createBySuccess("获取购物车数量异常!", 0);
        }
    }

    /**
     * 清空购物车
     *
     * @param userToken
     * @return
     */
    public ServerResponse delCar(String userToken) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;


            //清空增值商品
            Example example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, member.getId());
            List<ShoppingCart> shoppingCarts = iShoppingCartmapper.selectByExample(example);
            if (shoppingCarts.size() > 0) {
                for (ShoppingCart shoppingCart : shoppingCarts) {
                    Example example1 = new Example(DeliverOrderAddedProduct.class);
                    example1.createCriteria().andEqualTo(DeliverOrderAddedProduct.ANY_ORDER_ID, shoppingCart.getId()).andEqualTo(DeliverOrderAddedProduct.SOURCE, 4);
                    masterDeliverOrderAddedProductMapper.deleteByExample(example1);
                }
            }
            //清空购物车商品
            int i = iShoppingCartmapper.deleteByExample(example);
            if (i >= 0) {
                return ServerResponse.createBySuccessMessage("清空购物车成功!");
            } else {
                return ServerResponse.createBySuccessMessage("清空购物车失败!");
            }
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("系统报错，清空购物车失败!");
        }
    }

    /**
     * 修改购物车商品数量
     *
     * @param shopCartId
     * @param shopCount
     * @return
     */
    public ServerResponse updateCart(String shopCartId, Double shopCount) {
        try {
            ShoppingCart newShoppingCart = new ShoppingCart();
            ShoppingCart oldShoppingCart = iShoppingCartmapper.selectByPrimaryKey(shopCartId);
            if (shopCount == 0) {
                return ServerResponse.createBySuccessMessage("修改成功");
            } else if (oldShoppingCart.getShopCount() == 1 && shopCount < 0) {
                return ServerResponse.createByErrorMessage("受不了了,宝贝不能在减少了哦");
            } else if (shopCount == 1 || shopCount == -1) {
                newShoppingCart.setId(shopCartId);
                newShoppingCart.setShopCount(oldShoppingCart.getShopCount() + shopCount);
                iShoppingCartmapper.updateByPrimaryKeySelective(newShoppingCart);
            } else {
                newShoppingCart.setId(shopCartId);
                newShoppingCart.setShopCount(shopCount);
                iShoppingCartmapper.updateByPrimaryKeySelective(newShoppingCart);
            }
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("系统报错，修改购车数量失败!");
        }
    }

    /**
     * 审核加入购物车是否达到条件
     *
     * @param productId
     * @return 0=直接通过,无提示；
     * 1=有房无精算(业主无房时[收藏+去装修])
     * 2=有房有精算(业主无房无精算时[收藏+去装修])
     * 3=有房有精算(业主有房有精算时[购买精算+加入购物车])
     * 4=有房有精算(业主有房无精算时[购买该商品+去精算])
     * 5=人工商品
     */
    public ServerResponse checkCart(String userToken, String productId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            StorefrontProduct product = iMasterStorefrontProductMapper.selectByPrimaryKey(productId);//目标product 对象
            BasicsGoods goods = iMasterGoodsMapper.selectByPrimaryKey(product.getGoodsId());
            if (goods.getType() == 2) {
                return ServerResponse.createBySuccess("为确保您买到正确的人工类商品\n" +
                        "请联系服务您的工匠协助购买", 5);
            }
            Integer purchaseRestrictions = iShoppingCartmapper.queryPurchaseRestrictions(productId);
            Example example = new Example(House.class);
            if (purchaseRestrictions == 1) {
                example.createCriteria().andEqualTo(House.MEMBER_ID, member.getId())
                        .andEqualTo(House.DATA_STATUS, 0)
                        .andNotEqualTo(House.VISIT_STATE, 2)
                        .andNotEqualTo(House.VISIT_STATE, 3)
                        .andNotEqualTo(House.VISIT_STATE, 4);
                Integer houses = iHouseMapper.selectCountByExample(example);
                if (houses <= 0)
                    return ServerResponse.createBySuccess("该类商品需要在开始装修后才能购买\n" +
                            "您可以先收藏起来\n" +
                            "或者前往装修", 1);
            } else if (purchaseRestrictions == 2) {
                example.createCriteria().andEqualTo(House.MEMBER_ID, member.getId())
                        .andEqualTo(House.DATA_STATUS, 0);
                Integer houses = iHouseMapper.selectCountByExample(example);
                if (houses <= 0) {
                    return ServerResponse.createBySuccess("该类商品需要在开始装修后才能购买\n" +
                            "您可以先收藏起来\n" +
                            "或者前往装修", 2);
                }
                example.createCriteria().andEqualTo(House.MEMBER_ID, member.getId())
                        .andEqualTo(House.DATA_STATUS, 0)
                        .andNotEqualTo(House.BUDGET_OK, 1)
                        .andNotEqualTo(House.BUDGET_OK, 5);
                houses = iHouseMapper.selectCountByExample(example);
                if (houses <= 0) {
                    return ServerResponse.createBySuccess("该类商品建议根据精算指导购买\n" +
                            "你可以先购买精算~", 3);
                }
                example.createCriteria().andEqualTo(House.MEMBER_ID, member.getId())
                        .andEqualTo(House.DATA_STATUS, 0)
                        .andEqualTo(House.BUDGET_OK, 0);
                houses = iHouseMapper.selectCountByExample(example);
                if (houses <= 0) {
                    return ServerResponse.createBySuccess("该类商品建议根据精算指导购买\n" +
                            "你可以先购买精算~", 4);
                }
            }
            return ServerResponse.createBySuccess("OK!", 0);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("加入购物车失败!");
        }
    }

    /**
     * 加入购物车
     *
     * @param userToken
     * @param cityId
     * @param productId
     * @param shopCount
     * @return
     */
    public ServerResponse addCart(String userToken, String cityId, String productId, Double shopCount, String addedProductIds) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            StorefrontProduct storefrontProduct = iMasterStorefrontProductMapper.selectByPrimaryKey(productId);
            if (null == storefrontProduct)
                return ServerResponse.createByErrorMessage("店铺商品不存在");
            DjBasicsProductTemplate djBasicsProductTemplate = iMasterProductTemplateMapper.selectByPrimaryKey(storefrontProduct.getProdTemplateId());
            if (null == djBasicsProductTemplate)
                return ServerResponse.createByErrorMessage("商品模板不存在");
            BasicsGoods goods = iMasterGoodsMapper.selectByPrimaryKey(djBasicsProductTemplate.getGoodsId());
            if (null == goods)
                return ServerResponse.createByErrorMessage("货品不存在");
            Unit unit = iMasterUnitMapper.selectByPrimaryKey(djBasicsProductTemplate.getUnitId());
            if (null == unit)
                return ServerResponse.createByErrorMessage("单位不存在");
            //有房有精算  根据用户的member_id去区分
            //无房无精算  根据用户的member_id去区分
            ServerResponse ccart = checkCart(userToken, productId);
            if (ccart.getResultObj() == null ||
                    (((Integer) ccart.getResultObj()) != 0 && ((Integer) ccart.getResultObj()) != 3 && ((Integer) ccart.getResultObj()) != 4)) {
                return ServerResponse.createByErrorMessage(ccart.getResultMsg());
            }
            String shoppingCartid;
            //判断去重,如果有的话就购买数量加1
            Example example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, member.getId())
                    .andEqualTo(ShoppingCart.PRODUCT_ID, productId)
                    .andEqualTo(ShoppingCart.STOREFRONT_ID, storefrontProduct.getStorefrontId());
            List<ShoppingCart> list = iShoppingCartmapper.selectByExample(example);
            if (list.size() == 0) {
                ShoppingCart shoppingCart = new ShoppingCart();
                shoppingCart.setMemberId(member.getId());
                shoppingCart.setProductId(productId);
                shoppingCart.setPrice(new BigDecimal(storefrontProduct.getSellPrice()));
                shoppingCart.setStorefrontId(storefrontProduct.getStorefrontId());
                shoppingCart.setProductName(storefrontProduct.getProductName());
                shoppingCart.setImage(storefrontProduct.getImage());
                shoppingCart.setShopCount(shopCount);
                shoppingCart.setValueIdArr(djBasicsProductTemplate.getValueIdArr());
                shoppingCart.setProductSn(djBasicsProductTemplate.getProductSn());
                shoppingCart.setUnitName(djBasicsProductTemplate.getUnitName());
                shoppingCart.setCategoryId(djBasicsProductTemplate.getCategoryId());
                shoppingCart.setProductType(goods.getType());
                shoppingCart.setIsReservationDeliver(goods.getIsReservationDeliver());
                shoppingCart.setCityId(cityId);
                shoppingCart.setUnitType(unit.getType());
                iShoppingCartmapper.insert(shoppingCart);
                shoppingCartid = shoppingCart.getId();
            } else {
                ShoppingCart myCart = list.get(0);
                myCart.setShopCount(myCart.getShopCount() + shopCount);
                iShoppingCartmapper.updateByPrimaryKeySelective(myCart);
                shoppingCartid = myCart.getId();
            }
            //更新或添加增值商品
            setAddedProduct(shoppingCartid, addedProductIds);
            return ServerResponse.createBySuccessMessage("加入购物车成功!");
        } catch (Exception e) {
            logger.info("添加失败", e);
            return ServerResponse.createByErrorMessage("系统报错，加入购物车失败!");
        }
    }


    /**
     * 删除已选购物车
     */
    public ServerResponse delCheckCart(String shopCartIds) {
        try {
            List<String> stringList = Arrays.asList(shopCartIds.split(","));
            Example example = new Example(ShoppingCart.class);
            example.createCriteria().andIn(ShoppingCart.ID, stringList);
            List<ShoppingCart> shoppingCarts = iShoppingCartmapper.selectByExample(example);
            if (shoppingCarts.size() < 0)
                return ServerResponse.createBySuccessMessage("商品不存在!");
            if (stringList.size() > 0) {
                for (String shoppingCartId : stringList) {
                    Example example1 = new Example(DeliverOrderAddedProduct.class);
                    example1.createCriteria().andEqualTo(DeliverOrderAddedProduct.ANY_ORDER_ID, shoppingCartId).andEqualTo(DeliverOrderAddedProduct.SOURCE, 4);
                    masterDeliverOrderAddedProductMapper.deleteByExample(example1);
                }
            }
            int i = iShoppingCartmapper.deleteByExample(example);
            if (i >= 0) {
                return ServerResponse.createBySuccessMessage("删除已选商品成功!");
            } else {
                return ServerResponse.createBySuccessMessage("删除已选商品失败!");
            }
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("系统报错，删除已选商品失败!");
        }
    }


    /**
     * 更换购物车商品
     *
     * @param shoppingCartId
     * @param productId
     * @return
     */
    public ServerResponse replaceShoppingCart(String shoppingCartId, String productId, Double shopCount, String addedProductIds) {
        try {
            ShoppingCart shoppingCart = iShoppingCartmapper.selectByPrimaryKey(shoppingCartId);
            StorefrontProduct storefrontProduct = iMasterStorefrontProductMapper.selectByPrimaryKey(productId);
            //判断去重,如果有的话就购买数量加1
            Example example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, shoppingCart.getMemberId())
                    .andEqualTo(ShoppingCart.PRODUCT_ID, productId)
                    .andEqualTo(ShoppingCart.STOREFRONT_ID, storefrontProduct.getStorefrontId());
            List<ShoppingCart> list = iShoppingCartmapper.selectByExample(example);
            if (list.size() > 0)
                return ServerResponse.createByErrorMessage("购物车已存在该商品");
            DjBasicsProductTemplate djBasicsProductTemplate = iMasterProductTemplateMapper.selectByPrimaryKey(storefrontProduct.getProdTemplateId());
            BasicsGoods goods = iMasterGoodsMapper.selectByPrimaryKey(djBasicsProductTemplate.getGoodsId());
            Unit unit = iMasterUnitMapper.selectByPrimaryKey(djBasicsProductTemplate.getUnitId());
            shoppingCart.setId(shoppingCartId);
            shoppingCart.setProductId(productId);
            shoppingCart.setProductSn(djBasicsProductTemplate.getProductSn());
            shoppingCart.setProductName(storefrontProduct.getProductName());
            shoppingCart.setImage(storefrontProduct.getImage());
            shoppingCart.setPrice(new BigDecimal(storefrontProduct.getSellPrice()));
            shoppingCart.setProductType(goods.getType());
            shoppingCart.setUnitName(djBasicsProductTemplate.getUnitName());
            shoppingCart.setStorefrontId(storefrontProduct.getStorefrontId());
            shoppingCart.setShopCount(shopCount);
            shoppingCart.setValueIdArr(djBasicsProductTemplate.getValueIdArr());
            shoppingCart.setUnitType(unit.getType());
            iShoppingCartmapper.updateByPrimaryKeySelective(shoppingCart);

            //更新或添加增值商品
            setAddedProduct(shoppingCartId, addedProductIds);
            ShoppingCartListDTO shoppingCartListDTO = iShoppingCartmapper.querySingleCartList(shoppingCartId);
            if (!CommonUtil.isEmpty(shoppingCartListDTO.getValueIdArr())) {
                String strNewValueNameArr = "";
                String[] newValueNameArr = shoppingCartListDTO.getValueIdArr().split(",");
                for (int i = 0; i < newValueNameArr.length; i++) {
                    String valueId = newValueNameArr[i];
                    if (StringUtils.isNotBlank(valueId)) {
                        AttributeValue attributeValue = iMasterAttributeValueMapper.selectByPrimaryKey(valueId);
                        if (attributeValue != null && StringUtils.isNotBlank(attributeValue.getName())) {
                            if (StringUtils.isBlank(strNewValueNameArr)) {
                                strNewValueNameArr = attributeValue.getName();
                                System.out.println();
                            } else {
                                strNewValueNameArr = strNewValueNameArr + "," + attributeValue.getName();
                            }
                        }
                    }
                }
                shoppingCartListDTO.setValueNameArr(strNewValueNameArr);
            }
            return ServerResponse.createBySuccess("更换成功!", shoppingCartListDTO);
        } catch (Exception e) {
            logger.info("系统报错,更换失败!", e);
            return ServerResponse.createByErrorMessage("系统报错,更换失败!");
        }
    }


    /**
     * 购物车移入收藏
     *
     * @param userToken
     * @param jsonStr
     * @return
     */
    public ServerResponse insertToCollect(String userToken, String jsonStr) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            JSONArray jsonArr = JSONArray.parseArray(jsonStr);
            for (Object o : jsonArr) {
                JSONObject obj = (JSONObject) o;
                String id = obj.getString("id");
                String productId = obj.getString("productId");
                StorefrontProduct storefrontProduct = iMasterStorefrontProductMapper.selectByPrimaryKey(productId);
                Example example = new Example(MemberCollect.class);
                example.createCriteria().andEqualTo(MemberCollect.CONDITION_TYPE, 1)
                        .andEqualTo(MemberCollect.DATA_STATUS, 0)
                        .andEqualTo(MemberCollect.COLLECT_ID, productId)
                        .andEqualTo(MemberCollect.MEMBER_ID, member.getId())
                        .andEqualTo(MemberCollect.STOREFRONT_ID, storefrontProduct.getStorefrontId());
                List<MemberCollect> memberCollects = iMemberCollectMapper.selectByExample(example);
                if (memberCollects.size() > 0)
                    return ServerResponse.createBySuccessMessage("移入成功");
                MemberCollect memberCollect = new MemberCollect();
                memberCollect.setCollectId(productId);
                memberCollect.setConditionType("1");
                memberCollect.setMemberId(member.getId());
                memberCollect.setStorefrontId(storefrontProduct.getStorefrontId());
                iShoppingCartmapper.deleteByPrimaryKey(id);
                iMemberCollectMapper.insert(memberCollect);
            }
            return ServerResponse.createBySuccessMessage("移入成功");
        } catch (Exception e) {
            logger.info("添加失败", e);
            return ServerResponse.createByErrorMessage("系统报错，移入失败!");
        }
    }


    /**
     * 再次购买
     *
     * @param userToken
     * @param cityId
     * @param jsonStr
     * @return
     */
    public ServerResponse addCartBuyAgain(String userToken, String cityId, String jsonStr, String orderId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            if(StringUtils.isNotBlank(orderId)) {
                Example example = new Example(OrderItem.class);
                example.createCriteria().andEqualTo(OrderItem.ORDER_ID, orderId)
                        .andEqualTo(OrderItem.DATA_STATUS, 0);
                List<OrderItem> orderItems = iOrderItemMapper.selectByExample(example);
                orderItems.forEach(orderItem -> {
                    this.addCartBuyAgain(userToken, orderItem.getShopCount(), orderItem.getId(), cityId, member.getId());
                });
            }else{
                if(StringUtils.isNotBlank(jsonStr)) {
                    JSONArray jsonArr = JSONArray.parseArray(jsonStr);
                    for (Object o : jsonArr) {
                        JSONObject obj = (JSONObject) o;
                        Double shopCount = obj.getDouble("shopCount");
                        String orderItemId = obj.getString("orderItemId");
                        this.addCartBuyAgain(userToken, shopCount, orderItemId, cityId, member.getId());
                    }
                }
            }
            Example example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, member.getId())
                    .andEqualTo(ShoppingCart.DATA_STATUS, 0);
            return ServerResponse.createBySuccess("加入购物车成功", iShoppingCartmapper.selectCountByExample(example));
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("添加失败", e);
            return ServerResponse.createByErrorMessage("系统报错，加入失败!");
        }
    }


    private ServerResponse addCartBuyAgain(String userToken, Double shopCount, String orderItemId, String cityId, String memberId) {
        OrderItem orderItem = iOrderItemMapper.selectByPrimaryKey(orderItemId);
        StringBuffer productId = new StringBuffer();
        if (orderItem != null) {
            productId.append(orderItem.getProductId());
        } else {
            OrderSplitItem orderSplitItem = iOrderSplitItemMapper.selectByPrimaryKey(orderItemId);
            productId.append(orderSplitItem.getProductId());
        }
        StorefrontProduct storefrontProduct = iMasterStorefrontProductMapper.selectByPrimaryKey(productId.toString());
        if (null == storefrontProduct)
            return ServerResponse.createByErrorMessage("店铺商品不存在");
        DjBasicsProductTemplate djBasicsProductTemplate = iMasterProductTemplateMapper.selectByPrimaryKey(storefrontProduct.getProdTemplateId());
        if (null == djBasicsProductTemplate)
            return ServerResponse.createByErrorMessage("商品模板不存在");
        BasicsGoods goods = iMasterGoodsMapper.selectByPrimaryKey(djBasicsProductTemplate.getGoodsId());
        if (null == goods)
            return ServerResponse.createByErrorMessage("货品不存在");
        Unit unit = iMasterUnitMapper.selectByPrimaryKey(djBasicsProductTemplate.getUnitId());
        if (null == unit)
            return ServerResponse.createByErrorMessage("单位不存在");
        //有房有精算  根据用户的member_id去区分
        //无房无精算  根据用户的member_id去区分
        //purchaseRestrictions:0自由购房；1有房无精算；2有房有精算
        ServerResponse ccart = checkCart(userToken, productId.toString());
        if (ccart.getResultObj() == null ||
                (((Integer) ccart.getResultObj()) != 0 && ((Integer) ccart.getResultObj()) != 3 && ((Integer) ccart.getResultObj()) != 4)) {
            return ServerResponse.createByErrorMessage(ccart.getResultMsg());
        }
        String shoppingCartid;
        //判断去重,如果有的话就购买数量加1
        Example example = new Example(ShoppingCart.class);
        example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, memberId)
                .andEqualTo(ShoppingCart.PRODUCT_ID, productId.toString())
                .andEqualTo(ShoppingCart.STOREFRONT_ID, storefrontProduct.getStorefrontId());
        List<ShoppingCart> list = iShoppingCartmapper.selectByExample(example);
        if (list.size() == 0) {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setMemberId(memberId);
            shoppingCart.setProductId(productId.toString());
            shoppingCart.setPrice(new BigDecimal(storefrontProduct.getSellPrice()));
            shoppingCart.setStorefrontId(storefrontProduct.getStorefrontId());
            shoppingCart.setProductName(storefrontProduct.getProductName());
            shoppingCart.setImage(storefrontProduct.getImage());
            shoppingCart.setShopCount(shopCount);
            shoppingCart.setValueIdArr(djBasicsProductTemplate.getValueIdArr());
            shoppingCart.setProductSn(djBasicsProductTemplate.getProductSn());
            shoppingCart.setUnitName(djBasicsProductTemplate.getUnitName());
            shoppingCart.setCategoryId(djBasicsProductTemplate.getCategoryId());
            shoppingCart.setProductType(goods.getType());
            shoppingCart.setIsReservationDeliver(goods.getIsReservationDeliver());
            shoppingCart.setCityId(cityId);
            shoppingCart.setUnitType(unit.getType());
            iShoppingCartmapper.insert(shoppingCart);
            shoppingCartid = shoppingCart.getId();
        } else {
            ShoppingCart myCart = list.get(0);
            myCart.setShopCount(myCart.getShopCount() + shopCount);
            iShoppingCartmapper.updateByPrimaryKeySelective(myCart);
            shoppingCartid = myCart.getId();
        }

        //添加增殖类商品
        example = new Example(DeliverOrderAddedProduct.class);
        example.createCriteria().andEqualTo(DeliverOrderAddedProduct.ANY_ORDER_ID, orderItemId)
                .andEqualTo(DeliverOrderAddedProduct.DATA_STATUS, 0);
        List<DeliverOrderAddedProduct> deliverOrderAddedProducts = masterDeliverOrderAddedProductMapper.selectByExample(example);
        List<String> addedProductIds = deliverOrderAddedProducts
                .stream()
                .map(DeliverOrderAddedProduct::getAddedProductId)
                .collect(Collectors.toList());
        if (addedProductIds.size() > 0) {
            setAddedProduct(shoppingCartid, addedProductIds.toString());
        }
        return null;
    }

    /**
     * 更新/设置购物车增值商品
     *
     * @param shoppingCartId
     * @param addedProductIds
     */
    private void setAddedProduct(String shoppingCartId, String addedProductIds) {
        if (!CommonUtil.isEmpty(shoppingCartId)) {
            Example example = new Example(DeliverOrderAddedProduct.class);
            example.createCriteria().andEqualTo(DeliverOrderAddedProduct.ANY_ORDER_ID, shoppingCartId).andEqualTo(DeliverOrderAddedProduct.SOURCE, 4);
            masterDeliverOrderAddedProductMapper.deleteByExample(example);
            if (!CommonUtil.isEmpty(addedProductIds)) {
                String[] addedProductIdList = addedProductIds.split(",");
                for (String addedProductId : addedProductIdList) {
                    StorefrontProduct product = iMasterStorefrontProductMapper.selectByPrimaryKey(addedProductId);
                    DeliverOrderAddedProduct deliverOrderAddedProduct1 = new DeliverOrderAddedProduct();
                    deliverOrderAddedProduct1.setAnyOrderId(shoppingCartId);
                    deliverOrderAddedProduct1.setAddedProductId(addedProductId);
                    deliverOrderAddedProduct1.setPrice(product.getSellPrice());
                    deliverOrderAddedProduct1.setProductName(product.getProductName());
                    deliverOrderAddedProduct1.setSource("4");
                    masterDeliverOrderAddedProductMapper.insert(deliverOrderAddedProduct1);
                }
            }
        }
    }
}
